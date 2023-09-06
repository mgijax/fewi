package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jax.mgi.fe.datamodel.Annotation;
import org.jax.mgi.fe.datamodel.BatchMarkerAllele;
import org.jax.mgi.fe.datamodel.BatchMarkerGoAnnotation;
import org.jax.mgi.fe.datamodel.BatchMarkerId;
import org.jax.mgi.fe.datamodel.BatchMarkerMpAnnotation;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerID;
import org.jax.mgi.fe.datamodel.MarkerLocation;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;

/* wrapper class over a BatchMarkerId object, hiding some of the complexity
 */
public class BatchMarkerIdWrapper {
	private BatchMarkerId bmi;		// matching object, has an associated Marker
	private Marker m;				// Marker to which the term in bmi matched (should not be null)
	
	public BatchMarkerIdWrapper (BatchMarkerId bmi) {
		this.bmi = bmi;
		this.m = bmi.getMarker();
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
	
	/***--- attributes of the marker associated with the matching term ---***/
	
	// if defined, get the marker's symbol
	public String getSymbol() {
		if ((m != null) && (m.getSymbol() != null)) {
			return m.getSymbol();
		}
		return "";
	}
	
	// if defined, get the marker's primary ID
	public String getMarkerID() {
		if ((m != null) && (m.getPrimaryID() != null)) {
			return m.getPrimaryID();
		}
		return "No associated gene";
	}
	
	// if defined, get the marker's name
	public String getName() {
		if ((m != null) && (m.getName() != null)) {
			return m.getName();
		}
		return "";
	}
	
	// if defined, get the marker's feature type
	public String getFeatureType() {
		if ((m != null) && (m.getMarkerSubtype() != null)) {
			return m.getMarkerSubtype();
		}
		return "";
	}
	
	// get chromosome from genomic location (preferred) or genetic location
	public String getChromosome() {
		if (m == null) { return ""; }
		
		MarkerLocation loc = m.getPreferredCoordinates();
	    if (loc != null) {
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
		return "";
	}
	
	// if defined, get strand from genomic location
	public String getStrand() {
		if (m == null) { return ""; }

		MarkerLocation loc = m.getPreferredCoordinates();
	    if (loc != null){
			if (loc.getStrand() != null) {
			    return loc.getStrand();
			}
	    }
		return "";
	}
	
	// if defined, get start coordinate from genomic location
	public Double getStart() {
		if (m != null) {
			MarkerLocation loc = m.getPreferredCoordinates();
	    	if (loc != null){
				if (loc.getStartCoordinate() != null) {
					return loc.getStartCoordinate();
				}
	    	}
		}
		return null;
	}

	// if defined, get end coordinate from genomic location
	public Double getEnd() {
		if (m != null) {
			MarkerLocation loc = m.getPreferredCoordinates();
			if (loc != null) {
				if (loc.getEndCoordinate() != null) {
					return loc.getEndCoordinate();
				}
			}
		}
		return null;
	}
	
	// get a list of the marker IDs for the given logical database
	public List<String> getIDs(String logicalDB) {
		return getIDs(logicalDB, null);
	}
	
	// get a list of the marker IDs for either of the given logical databases
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
		if (m == null) { return new ArrayList<BatchMarkerGoAnnotation>(); }
		return m.getBatchMarkerGoAnnotations();
	}

	// Get a list of MP annotations for the associated marker (if any).  Return an empty list otherwise.
	public List<BatchMarkerMpAnnotation> getMPAnnotations() {
		if (m == null) { return new ArrayList<BatchMarkerMpAnnotation>(); }
		return m.getBatchMarkerMpAnnotations();
	}

	// Get a list of DO annotations for the associated marker (if any).  Return an empty list otherwise.
	public List<Annotation> getDOAnnotations() {
		if (m == null) { return new ArrayList<Annotation>(); }
		return m.getDOAnnotations();
	}

	// Get a list of alleles for the associated marker (if any).  Return an empty list otherwise.
	public List<BatchMarkerAllele> getAlleles() {
		if (m == null) { return new ArrayList<BatchMarkerAllele>(); }
		return m.getBatchMarkerAlleles();
	}

	// Get a list of records for expression counts by tissue for the associated marker (if any).
	// Return an empty list otherwise.
	public List<MarkerTissueCount> getExpressionCountsByTissue() {
		if (m == null) { return new ArrayList<MarkerTissueCount>(); }
		return m.getMarkerTissueCounts();
	}

	// Get a list of records for RefSNPs for the associated marker (if any). Return an empty list otherwise.
	public List<String> getRefSNPs() {
		if (m == null) { return new ArrayList<String>(); }
		return m.getBatchMarkerSnps();
	}
}
