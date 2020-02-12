package org.jax.mgi.fewi.summary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqHeatMapResult;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;

public class GxdRnaSeqHeatMapData {
	private final Logger logger = LoggerFactory.getLogger(GxdRnaSeqHeatMapData.class);

	//-------------------
	// instance variables
	//-------------------
	
	// accessible publicly
	public Integer rows;					// count of markers
	public Integer columns;					// count of consolidated samples (bioreplicate sets)
	public List<List<List<Float>>> seriesArrays;		// avg QN TPM indexed by: series, marker, column
	public List<String> seriesDataTypes;	// list of data types (one per series -- For now, we only have one series.)
	public List<String> seriesNames;		// list of series names (For now we only have one.)
	public HMMetadata rowMetadataModel;		// contains marker data
	public HMMetadata columnMetadataModel;	// contains sample data
	
	// used internally, not accessible publicly
	private List<Marker> markers;		// list of marker data
	private List<Sample> samples;		// list of sample data
	
	// We really don't need this, except for efficiency.  (An addition should be faster than a function call.)
	private int markerCount = 0;			// count of markers identified so far
	private int sampleCount = 0;			// count of samples identified so far

	private Map<String,Integer> mgiIDs = new HashMap<String,Integer>();		// maps from MGI marker ID to index into this.markers
	private Map<String,Integer> sampleIDs = new HashMap<String,Integer>();	// maps from bioreplicate set ID to index into this.samples
	private boolean analyzed = false;	// has this data set been analyzed yet?
	
	// Temporary cache of TPM values while we are collecting results (before composing the heat map).
	// Key 1 is the marker's position in this.markers; key 2 is the sample's position in this.samples.
	private Map<Integer,Map<Integer,String>> tpmCache = new HashMap<Integer,Map<Integer,String>>();
	
	//-------------------
	// methods
	//-------------------
	
	// constructor; initializes the object so it will be ready for collecting batches of results
	public GxdRnaSeqHeatMapData() {
		this.markers = new ArrayList<Marker>();
		this.samples = new ArrayList<Sample>();
	}
	
	// add a batch of results from Solr to the heat map we're building
	public void addResults(List<SolrGxdRnaSeqHeatMapResult> results) throws Exception {
		if (this.analyzed) {
			throw new Exception("GxdRnaSeqHeatMapData object has already been analyzed; cannot add more results");
		}
		this.addData(results);
	}
	
	// Add a new batch of results to this object.
	private void addData(List<SolrGxdRnaSeqHeatMapResult> results) {
		// Walk through the list of results once, collecting lists of unique markers and samples.
		for (SolrGxdRnaSeqHeatMapResult result : results) {
			String mgiID = result.getMarkerMgiID();
			String sampleID = result.getConsolidatedSampleKey();
			
			Marker m = null;
			if (!mgiIDs.containsKey(mgiID)) {
				m = new Marker();
				m.ensemblGMID = result.getMarkerEnsemblGeneModelID();
				m.symbol = result.getMarkerSymbol();
				m.mgiID = mgiID;
				m.index = this.markerCount++;
				this.markers.add(m);
				this.mgiIDs.put(mgiID, m.index);
			} else {
				m = this.markers.get(this.mgiIDs.get(mgiID));
			}
			
			if (!sampleIDs.containsKey(sampleID)) {
				this.sampleIDs.put(sampleID, this.sampleCount++);
			}
			Integer sampleIndex = this.sampleIDs.get(sampleID);
			
			if (!this.tpmCache.containsKey(m.index)) {
				this.tpmCache.put(m.index, new HashMap<Integer,String>());
			}
			this.tpmCache.get(m.index).put(sampleIndex, result.getAvergageQNTPM());
		}
	}
	
	// called by analyzeData() to extract data from the given 'consolidatedSamples' according to ordering
	// contained in this.sampleIDs, populating this.samples.
	private void populateSamples(Map<String, SolrGxdRnaSeqConsolidatedSample> consolidatedSamples) {
		this.samples = new ArrayList<Sample>(this.sampleIDs.size());
		for (int i = 0; i < this.sampleIDs.size(); i++) {
			this.samples.add(null);
		}

		for (String csKey : this.sampleIDs.keySet()) {
			SolrGxdRnaSeqConsolidatedSample cs = consolidatedSamples.get(csKey);
			Sample s = new Sample();
			
			if (cs != null) {
				s.age = cs.getAge();
				s.alleles = cs.getAlleles();
				s.bioreplicateSetID = csKey;
				s.expID = cs.getAssayMgiID();
				s.sex = cs.getSex();
				s.stage = cs.getTheilerStage();
				s.strain = cs.getStrain();
				s.structure = cs.getStructure();
				s.index = this.sampleIDs.get(csKey);
			} else {
				// should not happen
				s.age = "";
				s.alleles = "";
				s.bioreplicateSetID = csKey;
				s.expID = "";
				s.sex = "";
				s.stage = 0;
				s.strain = "";
				s.structure = "Missing sample info";
				s.index = this.sampleIDs.get(csKey);
			}
			this.samples.set(s.index, s);
		}
	}

	// return Set of consolidated sample keys found so far
	public Set<String> getSampleIDs() {
		return this.sampleIDs.keySet();
	}
	
	// Must be called once finished adding batches of results; this analyzes the data and
	// populates the data structures for the heat map.
	public void analyzeData(Map<String, SolrGxdRnaSeqConsolidatedSample> consolidatedSamples) throws Exception {
		if (this.analyzed) {
			throw new Exception("GxdRnaSeqHeatMapData object has already been analyzed; cannot analyzed twice");
		}
		
		if ((consolidatedSamples == null) || (consolidatedSamples.size() == 0)) {
			throw new Exception("Missing consolidated samples; cannot analyze data for heat map");
		}

		populateSamples(consolidatedSamples);
		
		// compute our heatmap dimensions
		this.rows = mgiIDs.size();			// one row per marker
		this.columns = sampleIDs.size();	// one column per consolidated sample
		
		// sort the lists of markers and samples
		Collections.sort(this.markers, new MarkerCmp());
		Collections.sort(this.samples, new SampleCmp());

		this.addBoilerplate();
		this.populateRowVectors();
		this.populateColumnVectors();
		this.populateMeasurements();
		this.analyzed = true;
	}
	
	// Define the data that are common across all runs.
	private void addBoilerplate() {
		// one fixed value for the type of data Morpheus should expect
		this.seriesDataTypes = new ArrayList<String>();
		this.seriesDataTypes.add("Float32");

		// one fixed name of the data set Morpheus will deal with
		this.seriesNames = new ArrayList<String>();
		this.seriesNames.add("Mouse RNA-Seq Heat Map of GXD search results");
	}
	
	// Populate the row vectors, based on the list of markers.
	private void populateRowVectors() {
		List<String> symbols = new ArrayList<String>();
		List<String> mgiIDs = new ArrayList<String>();
		List<String> ensemblIDs = new ArrayList<String>();
		
		for (Marker m : this.markers) {
			symbols.add(m.symbol);
			mgiIDs.add(m.mgiID);
			ensemblIDs.add(m.ensemblGMID);
		}
		
		List<HMVector> vectors = new ArrayList<HMVector>();
		vectors.add(new HMVector("Gene Symbol", symbols));
		vectors.add(new HMVector("MGI ID", mgiIDs));
		vectors.add(new HMVector("Ensembl ID", ensemblIDs));
		
		this.rowMetadataModel = new HMMetadata(vectors);
	}
	
	// Populate the column vectors, based on the list of samples.
	private void populateColumnVectors() {
		List<String> labels = new ArrayList<String>();
		List<String> structures = new ArrayList<String>();
		List<String> ages = new ArrayList<String>();
		List<String> stages = new ArrayList<String>();
		List<String> alleles = new ArrayList<String>();
		List<String> strains = new ArrayList<String>();
		List<String> sexes = new ArrayList<String>();
		List<String> expIDs = new ArrayList<String>();
		List<String> sampleKeys = new ArrayList<String>();

		for (Sample s : this.samples) {
			if (!"wild-type".equals(s.alleles)) {
				// add a black star to the end to flag those with mutant alleles
				labels.add(s.getLabel() + "\u2605");
			} else {
				labels.add(s.getLabel());
			}
			structures.add(s.structure);
			ages.add(s.age);
			stages.add(Integer.toString(s.stage));
			alleles.add(s.alleles);
			strains.add(s.strain);
			sexes.add(s.sex);
			expIDs.add(s.expID);
			sampleKeys.add(s.bioreplicateSetID);
		}

		List<HMVector> vectors = new ArrayList<HMVector>();
		vectors.add(new HMVector("label", labels));
		vectors.add(new HMVector("structure", structures));
		vectors.add(new HMVector("age", ages));
		vectors.add(new HMVector("stage", stages));
		vectors.add(new HMVector("alleles", alleles));
		vectors.add(new HMVector("strain", strains));
		vectors.add(new HMVector("sex", sexes));
		vectors.add(new HMVector("expID", expIDs));
		vectors.add(new HMVector("MGI_BioReplicateSet_ID", sampleKeys));
		
		this.columnMetadataModel = new HMMetadata(vectors);
	}
	
	// Go through the results and populate the heat map cells in seriesArrays.
	private void populateMeasurements() {
		// What we need to produce is effectively a two-dimensional array for each series.  However,
		// we only have one series.  So, we produce a two-dimensional array as a List of Lists of
		// integers, then we toss it as the only item in another list.
		
		// Each row is a list of integers.  Each integer is the value for a single column.  We'll
		// initialize all the values to -1 (not studied), then we can overwrite them as we walk through the data.
		List<List<Float>> rows = new ArrayList<List<Float>>();
		Float notStudied = null;
		for (int i = 0; i < this.rows; i++) {
			List<Float> row = new ArrayList<Float>();
			for (int col = 0; col < this.columns; col++) {
				row.add(notStudied);
			}
			rows.add(row);
		}
		
		// Define the mappings we'll use to identify the x and y for each cell in the heat map.
		Map<String,Integer> markerRows = this.buildLookup(this.getMarkerIDList(this.markers));
		Map<String,Integer> sampleColumns = this.buildLookup(this.getSampleIDList(this.samples));

		// Loop over markers and samples, adding results as we find them.
		for (int mIndex = 0; mIndex < this.rows; mIndex++) {
			// need to translate from mIndex (with sorted markers) to tpmCache indices (before sorted markers)
			int origMrkIndex = this.markers.get(mIndex).index;

			for (int sIndex = 0; sIndex < this.columns; sIndex++) {
				// need to translate from sIndex (with sorted samples) to tpmCache indices (before sorted samples)
				int origSmpIndex = this.samples.get(sIndex).index;
				
				Float avgQnTpm = notStudied;
				if (this.tpmCache.get(origMrkIndex).containsKey(origSmpIndex)) {
					String qn = this.tpmCache.get(origMrkIndex).get(origSmpIndex);
					try {
						avgQnTpm = Float.parseFloat(qn);
						rows.get(mIndex).set(sIndex, avgQnTpm);
					} catch (Exception e) {
						logger.info("Odd Avg QN TPM: " + qn);
					}
				}
			}
		}

		// Finally, wrap the rows inside another list so it appears as the set of rows for this (first and only) series.
		this.seriesArrays = new ArrayList<List<List<Float>>>();
		this.seriesArrays.add(rows);
	}
	
	// extract the IDs from the given markers
	private List<String> getMarkerIDList(List<Marker> markers) {
		List<String> keys = new ArrayList<String>();
		for (Marker m : markers) {
			keys.add(m.mgiID);
		}
		return keys;
	}

	// extract the IDs from the given samples
	private List<String> getSampleIDList(List<Sample> samples) {
		List<String> keys = new ArrayList<String>();
		for (Sample s : samples) {
			keys.add(s.bioreplicateSetID);
		}
		return keys;
	}
	
	// Build and return a Map that maps from each element in 'keys' to its index in the list.
	private Map<String,Integer> buildLookup(List<String> keys) {
		int i = 0;
		Map<String,Integer> map = new HashMap<String,Integer>();
		
		for (String key : keys) {
			map.put(key, i);
			i++;
		}
		return map;
	}
	
	//----------------------
	// private inner classes
	//----------------------
	
	// a heat map vector, containing a name for the vector and a list of strings associated with it
	private class HMVector {
		public String name;
		public List<String> array;
		
		// convenience constructor
		public HMVector (String name, List<String> array) {
			this.name = name;
			this.array = array;
		}
	}
	
	private class HMMetadata {
		public List<HMVector> vectors;
		
		// convenience constructor
		public HMMetadata (List<HMVector> vectors) {
			this.vectors = vectors;
		}
	}
	
	// all data needed for the heat map for one marker (row)
	private class Marker {
		public String symbol;
		public String mgiID;
		public String ensemblGMID;
		private Integer index;
	}
	
	// all data needed for the heat map for one sample (column)
	private class Sample {
		public String structure;
		public String age;
		public Integer stage;
		public String alleles;
		public String strain;
		public String sex;
		public String expID;
		public String bioreplicateSetID;
		private Integer index;
		
		public String getLabel() {
			StringBuffer sb = new StringBuffer();
			sb.append(structure);
			sb.append("_");
			sb.append(expID);
			sb.append("_");
			sb.append(bioreplicateSetID);
			return sb.toString();
		}
	}
	
	private class MarkerCmp implements Comparator<Marker> {
		public int compare (Marker a, Marker b) {
			SmartAlphaComparator cmp = new SmartAlphaComparator();

			// first compare the base terms themselves
			int i = cmp.compare(a.symbol, b.symbol);
			if (i == 0) {
				i = cmp.compare(a.mgiID,  b.mgiID);
				if (i == 0) {
					i = cmp.compare(a.ensemblGMID, b.ensemblGMID);
				}
			}
			return i;
			
		}
	}

	private class SampleCmp implements Comparator<Sample> {
		public int compare (Sample a, Sample b) {
			SmartAlphaComparator cmp = new SmartAlphaComparator();

			// first compare the base terms themselves
			int i = cmp.compare(a.structure, b.structure);
			if (i == 0) {
				i = cmp.compare(a.expID,  b.expID);
				if (i == 0) {
					i = cmp.compare(a.bioreplicateSetID, b.bioreplicateSetID);
				}
			}
			return i;
		}
	}
}
