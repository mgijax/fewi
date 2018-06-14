package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fewi.config.ContextLoader;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerGoAnnotation;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.BatchMarkerMpAnnotation;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerTissueCount;
import mgi.frontend.datamodel.StrainMarker;

/* wrapper class over a BatchMarkerId object, hiding the complexity of the
 * associated Marker / StrainMarker objects.
 */
public class BatchMarkerIdWrapper {
	private static String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	private static String strainLink = "<a href='(fewiUrl)strain/(id)' target='_blank'>(name)</a>".replace("(fewiUrl)", fewiUrl);

	private BatchMarkerId bmi;		// matching object, has either a Marker or a StrainMarker, but not both
	private Marker m;				// Marker to which the term in bmi matched (if any)
	private StrainMarker sm;		// StrainMarker to which the term in bmi matched (if any)
	
	public BatchMarkerIdWrapper (BatchMarkerId bmi) {
		this.bmi = bmi;
		this.m = bmi.getMarker();
		this.sm = bmi.getStrainMarker();
	}

	/***--- basic attributes of the match itself ---***/
	
	// term (or ID) that matched
	public String getTerm() {
		return bmi.getTerm();
	}
	
	// type of term that matched
	public String getTermType() {
		return bmi.getTermType();
	}
	
	// get unique identifier for this term/marker or term/strainMarker pair
	public String getUniqueKey() {
		return bmi.getUniqueKey();
	}
	
	// true if this match is to a canonical marker
	public boolean isMarkerMatch() {
		return m != null;
	}
	
	// true if this match is to a strain marker
	public boolean isStrainMarkerMatch() {
		return sm != null;
	}
	
	// true if this match is to a strain marker associated with a canonical marker
	public boolean hasCanonicalMarker() {
		return (m == null) && (sm != null) && (sm.getCanonicalMarkerID() != null);
	}
	
	/***--- attributes of the marker (or strain marker) associated with the matching term ---***/
	
	// both markers and strain markers have associated strains
	public String getStrain() {
		if (isMarkerMatch()) {
			// canonical markers always link to C57BL/6J
			return strainLink.replace("(id)", "MGI:3028467").replaceAll("(name)", m.getStrain());
		} else if (isStrainMarkerMatch()) {
			// strain markers link to their particular strain
			return strainLink.replace("(id)", sm.getStrainID()).replaceAll("(name)", sm.getStrainName());
		}
		return null;
	}
	
	// 1. if marker match, get the marker's symbol
	// 2. if strainmarker is associated with a canonical marker, get the canonical marker's symbol
	// 3. otherwise get the strain marker's first gene model ID
	public String getSymbol() {
		if (isMarkerMatch()) {
			return m.getSymbol();
		} else if (hasCanonicalMarker()) {
			return sm.getCanonicalMarkerSymbol();
		} else if (isStrainMarkerMatch()) {
			return sm.getFirstGeneModelID();
		}
		return "";
	}
	
	// 1. if marker match, get the marker's primary ID
	// 2. if strainmarker is associated with a canonical marker, get the canonical marker's primary ID
	// 3. otherwise get the strainmarker's first gene model ID
	public String getMarkerID() {
		if (isMarkerMatch()) {
			return m.getPrimaryID();
		} else if (isStrainMarkerMatch()) {
			if (hasCanonicalMarker()) {
				return sm.getCanonicalMarkerID();
			} else {
				return sm.getFirstGeneModelID();
			}
		}
		return "";
	}
	
	// 1. if marker match, get the marker's name
	// 2. if strainmarker is associated with a canonical marker, get the canonical marker's name
	// 3. otherwise a strain marker has no name
	public String getName() {
		if (isMarkerMatch()) {
			return m.getName();
		} else if (hasCanonicalMarker()) {
			return sm.getCanonicalMarkerName();
		}
		return "";
	}
	
	// 1. if marker match, get the marker's feature type
	// 2. if strainmarker match, get the strainmarker's feature type
	// 3. otherwise return an empty string
	public String getFeatureType() {
		if (isMarkerMatch()) {
			return m.getMarkerSubtype();
		} else if (isStrainMarkerMatch()) {
			return sm.getFeatureType();
		}
		return "";
	}
	
	// 1. if marker match, get chromosome from genomic location (preferred) or genetic location
	// 2. otherwise get from strainmarker
	// 3. failing both, return an empty string
	public String getChromosome() {
		if (isMarkerMatch()) {
			MarkerLocation loc = m.getPreferredCoordinates();
	    	if (loc != null){
				if (loc.getChromosome() != null) {
				    return loc.getChromosome();
				}
	    	}
			
		    loc = m.getPreferredCentimorgans();
		    if (loc != null){
	    		if (loc.getChromosome() != null){
	    			return loc.getChromosome();
	    		}
		    }
		} else if (isStrainMarkerMatch() && (sm.getChromosome() != null)) {
			return sm.getChromosome();
		}
		return "";
	}
	
	// 1. if marker match, get strand from genomic location
	// 2. otherwise get from strainmarker
	// 3. failing both, return an empty string
	public String getStrand() {
		if (isMarkerMatch()) {
			MarkerLocation loc = m.getPreferredCoordinates();
	    	if (loc != null){
				if (loc.getStrand() != null) {
				    return loc.getStrand();
				}
	    	}
		} else if (isStrainMarkerMatch() && (sm.getStrand() != null)) {
			return sm.getStrand();
		}
		return "";
	}
	
	// 1. if marker match, get start coordinate from genomic location
	// 2. otherwise get from strainmarker
	// 3. failing both, return null
	public Double getStart() {
		if (isMarkerMatch()) {
			MarkerLocation loc = m.getPreferredCoordinates();
	    	if (loc != null){
				if (loc.getStartCoordinate() != null) {
				    return loc.getStartCoordinate();
				}
	    	}
		} else if (isStrainMarkerMatch() && (sm.getStartCoordinate() != null)) {
			return sm.getStartCoordinate();
		}
		return null;
	}

	// 1. if marker match, get end coordinate from genomic location
	// 2. otherwise get from strainmarker
	// 3. failing both, return null
	public Double getEnd() {
		if (isMarkerMatch()) {
			MarkerLocation loc = m.getPreferredCoordinates();
	    	if (loc != null) {
				if (loc.getEndCoordinate() != null) {
				    return loc.getEndCoordinate();
				}
	    	}
		} else if (isStrainMarkerMatch() && (sm.getEndCoordinate() != null)) {
			return sm.getEndCoordinate();
		}
		return null;
	}
	
	// get a list of the marker IDs (not strain marker IDs) for the given logical database
	// for the associated marker (if there is one)
	public List<String> getIDs(String logicalDB) {
		return getIDs(logicalDB, null);
	}
	
	// get a list of the marker IDs (not strain marker IDs) for either of the given logical databases
	// for the associated marker (if there is one)
	public List<String> getIDs(String logicalDB1, String logicalDB2) {
    	List<String> idList = new ArrayList<String>();
    	if (m == null) { return idList; }
    	
		Set<String> ldbs = new HashSet<String>();
    	ldbs.add(logicalDB1);
    	if (logicalDB2 != null) {
    		ldbs.add(logicalDB2);
    	}
		
    	List<MarkerID> ids = m.getIds();
   		if (ids != null) {
    		for (MarkerID id : ids) {
    			if (ldbs.contains(id.getLogicalDB())) {
    				idList.add(id.getAccID());
				}
			}
    	}
    	return idList;
	}
	
	// Get a list of GO annotations for the associated marker (if any).  Return an empty list otherwise.
	public List<BatchMarkerGoAnnotation> getGOAnnotations() {
		if (m != null) {
			return m.getBatchMarkerGoAnnotations();
		}
		return new ArrayList<BatchMarkerGoAnnotation>();
	}

	// Get a list of MP annotations for the associated marker (if any).  Return an empty list otherwise.
	public List<BatchMarkerMpAnnotation> getMPAnnotations() {
		if (m != null) {
			return m.getBatchMarkerMpAnnotations();
		}
		return new ArrayList<BatchMarkerMpAnnotation>();
	}

	// Get a list of DO annotations for the associated marker (if any).  Return an empty list otherwise.
	public List<Annotation> getDOAnnotations() {
		if (m != null) {
			return m.getDOAnnotations();
		}
		return new ArrayList<Annotation>();
	}

	// Get a list of alleles for the associated marker (if any).  Return an empty list otherwise.
	public List<BatchMarkerAllele> getAlleles() {
		if (m != null) {
			return m.getBatchMarkerAlleles();
		}
		return new ArrayList<BatchMarkerAllele>();
	}

	// Get a list of records for expression counts by tissue for the associated marker (if any).
	// Return an empty list otherwise.
	public List<MarkerTissueCount> getExpressionCountsByTissue() {
		if (m != null) {
			return m.getMarkerTissueCounts();
		}
		return new ArrayList<MarkerTissueCount>();
	}

	// Get a list of records for RefSNPs for the associated marker (if any). Return an empty list otherwise.
	public List<String> getRefSNPs() {
		if (m != null) {
			return m.getBatchMarkerSnps();
		}
		return new ArrayList<String>();
	}
}
