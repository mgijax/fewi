package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;

public class HmdcAnnotationGroup {
	
	//--------------------------//
	//--- instance variables ---//
	//--------------------------//
	
	// divider between human gene and disease in combination key
	private static String separator = "::::";
	
	// special flags that can be attached to individual cells (row ID, column ID)
	private static String backgroundSensitive = "bg";
	private static String normal = "normal";
	
	// set of flags associated with each cell (optionally; only cells with flags need appear)
	private Map<Integer,Map<Integer,Set<String>>> flags = new HashMap<Integer,Map<Integer,Set<String>>>();
	
	// true if this group has disease data, false if it has phenotype data
	private boolean hasDiseaseData = false;	
	
	// set of row IDs for mouse data (those not in this set are for human data)
	private Set<Integer> mouseRows = new HashSet<Integer>();
	
	// { row ID : text string for row header } and reversed
	private Map<Integer,String> rows = new HashMap<Integer,String>();
	private Map<String,Integer> rowsRev = new HashMap<String,Integer>();

	// { column ID : text string for column header } and reversed
	private Map<Integer,String> columns = new HashMap<Integer,String>();
	private Map<String,Integer> columnsRev = new HashMap<String,Integer>();

	// { row ID : { column ID : annotation count } }
	private Map<Integer,Map<Integer,Integer>> counts = new HashMap<Integer,Map<Integer,Integer>>();

	// { row ID : genoCluster key }
	private Map<Integer, Integer> genoClusterKeys = new HashMap<Integer, Integer>();
	
	// { genoCluster key : seq num }
	private Map<Integer, Integer> genoClusterSeqNums = new HashMap<Integer, Integer>();
	
	// { row ID : homology cluster key }
	private Map<Integer, String> homologyClusterKeys = new HashMap<Integer, String>();
	
	// { disease name : disease ID }
	private Map<String, String> diseaseIDs = new HashMap<String, String>();
	
	// { header text : sequence num }
	private Map<String, Integer> headerSequenceNumbers = new HashMap<String, Integer>();
	
	// set of genocluster keys for conditional genotypes
	private Set<Integer> conditionalGenoclusters = new HashSet<Integer>();
	
	//--------------------//
	//--- constructors ---//
	//--------------------//
	
	/* hide the default constructor
	 */
	private HmdcAnnotationGroup() {}
	
	/* constructor must specify type of data contained in this annotation group (diseases = true, phenotypes = false)
	 */
	public HmdcAnnotationGroup (boolean hasDiseases) {
		this.hasDiseaseData = hasDiseases;
	}

	//-----------------------//
	//--- general methods ---//
	//-----------------------//
	
	/* consolidates multiple human gene/disease rows down into a single row per gene.  This is needed
	 * because in the disease popup, we want to show each gene only once.
	 */
	public void consolidateHumanRows() {
		if (!this.hasHumanRows() || !this.hasDiseases()) { return; }
		
		// maps from each human symbol to its canonical row
		Map<String,Integer> bySymbol = new HashMap<String,Integer>();
		
		/* go through human rows.  For each gene, if we've already seen that one, then copy its
		 * data to that first instance and remove this later one
		 */
		for (Integer rowID : this.getHumanRowIDs()) {
			String symbol = this.getHumanSymbol(rowID);
			if (bySymbol.containsKey(symbol)) {
				Integer canonicalRowID = bySymbol.get(symbol);
				// add to existing counts
				Map<Integer,Integer> rowCounts = this.counts.get(rowID);
				Map<Integer,Integer> canonicalRowCounts = this.counts.get(canonicalRowID);

				for (Integer colID : rowCounts.keySet()) {
					Integer ct = rowCounts.get(colID);
					if (canonicalRowCounts.containsKey(colID)) {
						canonicalRowCounts.put(colID, ct + canonicalRowCounts.get(colID));
					} else {
						canonicalRowCounts.put(colID, ct);
					}
				}
				// remove from rows, rowsRev
				rowsRev.remove(rows.get(rowID));
				rows.remove(rowID);
				counts.remove(rowID);
			} else {
				bySymbol.put(symbol, rowID);
			}
		}
	}
	
	/* returns true if the specified row has mouse data, false if it has human data
	 */
	public boolean isMouse(int rowID) {
		return mouseRows.contains(rowID);
	}
	
	/* returns true if this group has disease data, false if it has phenotype data
	 */
	public boolean hasDiseases() {
		return hasDiseaseData;
	}

	/* get the ID number corresponding to the given column text
	 */
	public int getColumnID (String columnText) {
		if (!columnsRev.containsKey(columnText)) {
			int columnID = columnsRev.size() + 1;
			columnsRev.put(columnText, columnID);
			columns.put(columnID, columnText);
		}
		return columnsRev.get(columnText);
	}
	
	/* get mapping from column text to column ID number
	 */
	public Map<String,Integer> getColumnIDMap() {
		return columnsRev;
	}
	
	/* increment the count of annotations for cell with the given row and column IDs
	 */
	public void addAnnotation (int rowID, int columnID) {
		if (!counts.containsKey(rowID)) {
			counts.put(rowID, new HashMap<Integer,Integer>());
		}
		if (!counts.get(rowID).containsKey(columnID)) {
			counts.get(rowID).put(columnID, 0);
		}
		counts.get(rowID).put(columnID, 1 + counts.get(rowID).get(columnID));
	}

	/* get a sorted list of the column headers (sorted in DAG order, so similar structures are grouped)
	 */
	public List<String> getColumns() {
		List<Integer> seqNums = new ArrayList<Integer>();
		Map<Integer,String> seqNumToHeader = new HashMap<Integer,String>();
		
		for (String header : headerSequenceNumbers.keySet()) {
			Integer seqNum = headerSequenceNumbers.get(header);
			seqNumToHeader.put(seqNum, header);
			seqNums.add(seqNum);
		}
		
		Collections.sort(seqNums);

		List<String> headers = new ArrayList<String>();
		for (Integer seqNum : seqNums) {
			headers.add(seqNumToHeader.get(seqNum));
		}
		return headers;
	}
	
	/* get a list of column IDs, sorted by the header text itself
	 */
	public List<Integer> getColumnIDs() {
		List<Integer> headerIDs = new ArrayList<Integer>();
		for (String header : getColumns()) {
			headerIDs.add(getColumnID(header));
		}
		return headerIDs;
	}
	
	/* return the count of annotations for the cell identified by the given row and column IDs
	 */
	public int getCount (int rowID, int columnID) {
		if (counts.containsKey(rowID) && counts.get(rowID).containsKey(columnID)) {
			return counts.get(rowID).get(columnID);
		}
		return 0;
	}
	
	/* get a map of counts for all row, column combinations in this group
	 */
	public Map<Integer,Map<Integer,Integer>> getCountMap() {
		Map<Integer,Map<Integer,Integer>> full = new HashMap<Integer,Map<Integer,Integer>>();
		for (Integer rowID : this.rows.keySet()) {
			full.put(rowID, new HashMap<Integer,Integer>());
			for (Integer columnID : this.columns.keySet()) {
				full.get(rowID).put(columnID, getCount(rowID, columnID));
			}
		}
		return full;
	}
	
	/* get a map of flags cached for all row, column combinations in this group (only including the
	 * specified flag)
	 */
	private Map<Integer,Map<Integer,String>> getFlagMap(String flag) {
		Map<Integer,Map<Integer,String>> full = new HashMap<Integer,Map<Integer,String>>();
		for (Integer rowID : this.rows.keySet()) {
			full.put(rowID, new HashMap<Integer,String>());
			for (Integer columnID : this.columns.keySet()) {
				String flagFound = null;
				if (flags.containsKey(rowID)) {
					if (flags.get(rowID).containsKey(columnID)) {
						if (flags.get(rowID).get(columnID).contains(flag)) {
							flagFound = flag;
						}
					}
				}
				full.get(rowID).put(columnID, flagFound);
			}
		}
		return full;
	}

	// is this group empty?  (ie- no rows and/or no columns)
	public boolean isEmpty() {
		return (rows.size() == 0) || (columns.size() == 0);
	}
	
	/* (internal) join the strings in 'items' with the given 'delimiter', returning a new String
	 */
	private String join (String delimiter, Collection<String> items) {
		if (items == null) { return ""; }
		if (delimiter == null) { delimiter = ", "; }
		
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = items.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) { 
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	/* reasonable data to use for debugging output
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[HmdcAnnotationGroup: ");
		if (this.isEmpty()) {
			sb.append("empty");
		} else {
			sb.append("columns (" + join(", ", getColumns()) + "), ");
			sb.append("rows (" + join(", ", rowsRev.keySet()) + "), ");
			sb.append(mouseRows.size());
			sb.append(" mouse rows, ");
			sb.append(rows.size() - mouseRows.size());
			sb.append(" human rows, ");
			sb.append(columns.size());
			if (hasDiseaseData) { sb.append(" diseases"); } else { sb.append(" phenotypes"); }
		}
		sb.append("]");
		return sb.toString();
	}

	//---------------------------------------//
	//--- mouse genotype-specific methods ---//
	//---------------------------------------//
	
	/* get the ID number corresponding to the given mouse genotype
	 */
	public int getMouseRowID (String genotypeText) {
		if (!rowsRev.containsKey(genotypeText)) {
			int rowID = rowsRev.size() + 1;
			rowsRev.put(genotypeText, rowID);
			rows.put(rowID, genotypeText);
			mouseRows.add(rowID);
		}
		return rowsRev.get(genotypeText);
	}

	/* cache the sequence number for the given genocluster
	 */
	private void cacheGenoClusterSeqNum(int genoClusterKey, int seqNum) {
		genoClusterSeqNums.put(genoClusterKey, seqNum);
	}
	
	/* if this is a conditional genocluster, remember it
	 */
	private void cacheConditionalFlag(int genoClusterKey, boolean isConditional) {
		if (isConditional) { conditionalGenoclusters.add(genoClusterKey); }
	}
	
	/* map of row IDs to indicate if they are conditional genoclusters (1) or not (0)
	 */
	public Map<Integer,Integer> getConditionalRowIDs() {
		Map<Integer,Integer> out = new HashMap<Integer,Integer>();
		for (Integer rowID : genoClusterKeys.keySet()) {
			Integer genoClusterKey = genoClusterKeys.get(rowID);
			if (conditionalGenoclusters.contains(genoClusterKey)) {
				out.put(rowID, 1);
			} else {
				out.put(rowID, 0);
			}
		}
		return out;
	}
	
	/* increment the count of annotations for cell with the given row and column text values
	 */
	public void addMouseAnnotation (String genotypeText, String columnText, Integer seqNum, Integer genoClusterKey,
			Integer genoClusterSeqNum, boolean isNormal, boolean isBackgroundSensitive, boolean isConditional) {
		Integer rowID = getMouseRowID(genotypeText);
		Integer columnID = getColumnID(columnText);
		addAnnotation(rowID, columnID);
		cacheSequenceNum(columnText, seqNum);
		cacheGenoClusterKey(rowID, genoClusterKey);
		cacheGenoClusterSeqNum(genoClusterKey, genoClusterSeqNum);
		cacheConditionalFlag(genoClusterKey, isConditional);
		
		if (isNormal) { setNormalQualifier(rowID, columnID); }
		if (isBackgroundSensitive) { setBackgroundSensitive(rowID, columnID); }
	}

	/* returns true if there are any mouse rows, false if not
	 */
	public boolean hasMouseRows() {
		return !mouseRows.isEmpty();
	}

	/* convenience for JSTL access
	 */
	public boolean getHasMouseRows() {
		return hasMouseRows();
	}
	
	/* get a mapping from row ID to the mouse allele pairs (useful for JSTL access)
	 */
	public Map<Integer,String> getAllelePairMap() {
		return rows;
	}

	/* get the IDs for the rows containing mouse data, ordered by allele pairs
	 */
	public List<Integer> getMouseRowIDs() {
		// list of genocluster sequence numbers to sort
		List<Integer> toSort = new ArrayList<Integer>();
		
		// maps from genocluster sequence number to row ID
		Map<Integer,Integer> idLookup = new HashMap<Integer,Integer>();
		
		for (Integer rowID : rows.keySet()) {
			if (mouseRows.contains(rowID)) {
				Integer gcKey = getGenoClusterKey(rowID);
				Integer seqNum = genoClusterSeqNums.get(gcKey);
				toSort.add(seqNum);
				idLookup.put(seqNum, rowID);
			}
		}
		
		Collections.sort(toSort);

		// ordered list of row IDs to return
		List<Integer> toReturn = new ArrayList<Integer>(toSort.size());
		
		for (Integer sortValue : toSort) {
			toReturn.add(idLookup.get(sortValue));
		}
		return toReturn;
	}
	
	//-------------------------------------------//
	//--- human gene/disease-specific methods ---//
	//-------------------------------------------//
	
	/* get the IDs for the rows containing human data, ordered first by human symbol and
	 * then by disease name
	 */
	public List<Integer> getHumanRowIDs() {
		// list of "symbol disease" strings to sort
		List<String> toSort = new ArrayList<String>();
		
		// maps from "symbol disease" string to row ID
		Map<String,Integer> idLookup = new HashMap<String,Integer>();
		
		for (Integer rowID : rows.keySet()) {
			if (!mouseRows.contains(rowID)) {
				String sortValue = rows.get(rowID).replace(separator, " ");
				toSort.add(sortValue);
				idLookup.put(sortValue, rowID);
			}
		}
		
		// smart-alpha sort by gene then by disease
		Collections.sort(toSort, new SmartAlphaComparator());

		// ordered list of row IDs to return
		List<Integer> toReturn = new ArrayList<Integer>(toSort.size());
		
		for (String sortValue : toSort) {
			toReturn.add(idLookup.get(sortValue));
		}
		return toReturn;
	}
	
	/* get the ID number corresponding to the given human marker/disease pair
	 */
	public int getHumanRowID (String humanMarkerSymbol, String columnText) {
		String comboKey = humanMarkerSymbol + separator + columnText;
		if (!rowsRev.containsKey(comboKey)) {
			int rowID = rowsRev.size() + 1;
			rowsRev.put(comboKey, rowID);
			rows.put(rowID,  comboKey);
		}
		return rowsRev.get(comboKey);
	}

	/* cache the genoCluster key for the given row ID
	 */
	private void cacheGenoClusterKey (int rowID, int clusterKey) {
		genoClusterKeys.put(rowID, clusterKey);
	}

	/* retrieve the genoCluster key that was cached for the given row ID
	 */
	public Integer getGenoClusterKey (int rowID) {
		if (genoClusterKeys.containsKey(rowID)) {
			return genoClusterKeys.get(rowID);
		}
		return null;
	}

	/* get a mapping between the integer row ID and the integer genoCluster key (useful for JSTL access)
	 */
	public Map<Integer, Integer> getGenoClusterKeyMap() {
		return genoClusterKeys;
	}
	
	/* cache the homology cluster key for the given row ID
	 */
	private void cacheHomologyClusterKey (int rowID, String clusterKey) {
		homologyClusterKeys.put(rowID, clusterKey);
	}

	/* retrieve the homology cluster key that was cached for the given row ID
	 */
	public String getHomologyClusterKey (int rowID) {
		if (homologyClusterKeys.containsKey(rowID)) {
			return homologyClusterKeys.get(rowID);
		}
		return null;
	}

	/* get a mapping between the integer row ID and the String homology cluster key (useful for JSTL access)
	 */
	public Map<Integer, String> getHomologyClusterKeyMap() {
		return homologyClusterKeys;
	}
	
	/* cache the disease ID for the given disease name
	 */
	public void cacheDiseaseID (String disease, String diseaseID) {
		diseaseIDs.put(disease, diseaseID);
	}

	/* get the disease ID that was cached for the given disease name
	 */
	public String getDiseaseID (String disease) {
		if (diseaseIDs.containsKey(disease)) {
			return diseaseIDs.get(disease);
		}
		return null;
	}

	/* get a mapping between the disease name and the disease ID (useful for JSTL access)
	 */
	public Map<String, String> getDiseaseIDMap() {
		return diseaseIDs;
	}
	
	/* cache the sequence number to be used in ordering the given annotated term (in table header)
	 */
	public void cacheSequenceNum(String annotatedTerm, Integer headerSequenceNum) {
		headerSequenceNumbers.put(annotatedTerm, headerSequenceNum);
	}

	/* increment the count of annotations for cell with the given row and column text values.  If
	 * is an DO annotation, disease and annotatedTerm should match.  If is an HPO term, then the
	 * disease is the source of the HPO annotatedTerm.
	 */
	public void addHumanAnnotation (String humanMarkerSymbol, String homologyClusterKey, String disease,
			String annotatedTerm, Integer headerSequenceNum) {
		Integer rowID = getHumanRowID(humanMarkerSymbol, disease);
		addAnnotation(rowID, getColumnID(annotatedTerm));
		cacheHomologyClusterKey(rowID, homologyClusterKey);
		cacheSequenceNum(annotatedTerm, headerSequenceNum);
	}
	
	/* returns true if there are any human rows, false if not
	 */
	public boolean hasHumanRows() {
		return (rows.size() - mouseRows.size()) > 0;
	}
	
	/* convenience for JSTL access
	 */
	public boolean getHasHumanRows() {
		return hasHumanRows();
	}
	
	/* get the combo key (generated by getHumanRowID) for the given row ID, or null if bad ID
	 */
	private String getHumanRowHeader (int rowID) {
		// if this is a mouse row or an unknown row, then bail out (should not happen)
		if (mouseRows.contains(rowID) || !rows.containsKey(rowID)) {
			return null;
		}
		return rows.get(rowID);
	}
	
	/* get the marker symbol for the given human row ID, or null if bad ID
	 */
	public String getHumanSymbol (int rowID) {
		String comboKey = getHumanRowHeader(rowID);
		if (comboKey != null) {
			String[] parts = comboKey.split(separator);
			if (parts.length >= 2) {
				return parts[0];
			}
		}
		return null;
	}
	
	/* get a mapping from row ID to the human marker symbol (useful for JSTL access)
	 */
	public Map<Integer,String> getHumanSymbolMap() {
		Map<Integer,String> out = new HashMap<Integer,String>();
		for (Integer rowID : rows.keySet()) {
			out.put(rowID, getHumanSymbol(rowID));
		}
		return out;
	}
	
	/* get the disease name for the given human row ID, or null if bad ID
	 */
	public String getHumanDisease (int rowID) {
		String comboKey = getHumanRowHeader(rowID);
		if (comboKey != null) {
			String[] parts = comboKey.split(separator);
			if (parts.length >= 2) {
				return parts[1];
			}
		}
		return null;
	}

	/* get a mapping from the row ID to the human disease name (useful for JSTL access)
	 */
	public Map<Integer,String> getHumanDiseaseMap() {
		Map<Integer,String> out = new HashMap<Integer,String>();
		for (Integer rowID : rows.keySet()) {
			out.put(rowID, getHumanDisease(rowID));
		}
		return out;
	}
	
	//--- deal with the special flags ---//
	
	// attach the specific flag to the given (rowID, columnID) cell
	private void setFlag (int rowID, int columnID, String flag) {
		if (!flags.containsKey(rowID)) {
			flags.put(rowID, new HashMap<Integer,Set<String>>());
		}
		if (!flags.get(rowID).containsKey(columnID)) {
			flags.get(rowID).put(columnID, new HashSet<String>());
		}
		flags.get(rowID).get(columnID).add(flag);
	}
	
	// note that the cell at the given row and column has background-sensitive data
	public void setBackgroundSensitive (int rowID, int columnID) {
		setFlag(rowID, columnID, backgroundSensitive);
	}
	
	// note that the cell at the given row and column has data with a normal qualifier
	public void setNormalQualifier (int rowID, int columnID) {
		setFlag(rowID, columnID, normal);
	}
	
	// get a map of cells with normal flags, returns: { row ID : { column ID : flag }}
	public Map<Integer,Map<Integer,String>> getNormalMap() {
		return getFlagMap(normal);
	}

	// get a map of cells with background-sensitive flags, returns: { row ID : { column ID : flag }}
	public Map<Integer,Map<Integer,String>> getBackgroundSensitiveMap() {
		return getFlagMap(backgroundSensitive);
	}
}