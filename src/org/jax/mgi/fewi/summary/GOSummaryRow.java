package org.jax.mgi.fewi.summary;

import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.util.link.IDLinker;


/**
 * wrapper around an Annotation;  represents on row in summary
 */
public class GOSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private Annotation annot;
	
	private final IDLinker  linker = IDLinker.getInstance();

	// config values
    String wiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

    public GOSummaryRow (Annotation annot, Marker marker) {
    	this.annot = annot;
    	return;
    }

    public GOSummaryRow (Annotation annot, Reference reference) {
    	this.annot = annot;
    	return;
    }

    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getMarker() {
	List<Marker> markers = this.annot.getMarkers();
	if ((markers == null) || (markers.size() == 0)) {
	    return "";
	}

	// assume only one marker, and that the marker is for mouse
	Marker m = markers.get(0);

	String t = "<a href='" + fewiUrl + "marker/" + m.getPrimaryID()
	    + "'>" + m.getSymbol() + "</a>, " + m.getName();
	return t;
    }

    public String getChromosome() {
	List<Marker> markers = this.annot.getMarkers();
	if ((markers == null) || (markers.size() == 0)) {
	    return "";
	}

	// assume only one marker, and that the marker is for mouse
	Marker m = markers.get(0);

	// prefer a genetic location if one is available; if not, fall back on
	// whatever is available
	MarkerLocation ml = m.getPreferredCentimorgans();
	if (ml == null) {
		ml = m.getPreferredLocation();
	}
	if (ml.getChromosome() == null) {
		return "";
	}
	return ml.getChromosome();
    }

    public String getCategory() {
    	return annot.getDagName();
    }
    public String getTerm() {
    	String termText = "<a href='"+ wiUrl +"searches/GO.cgi?id=" + annot.getTermID() + "'> " + annot.getTerm() + "</a>";
    	if (annot.getQualifier() != null) {
    		return "<b>" + annot.getQualifier() + "</b> " + termText;
    	}
        return termText;
    }
    public String getEvidence() {
    	return annot.getEvidenceCode();
    }
    public String getInferred() {
        List<AnnotationInferredFromID> inferred = annot.getInferredFromList();
        if (inferred.size() >= 1)
        {
            String inferredString = "";
            Boolean first = Boolean.TRUE;
            
            for (AnnotationInferredFromID aifi: inferred) {
                if (first) {
			first = Boolean.FALSE;
                	inferredString = linker.getLink(aifi.getLogicalDB(), aifi.getAccID());
                }
                else {
                	inferredString = inferredString + " | " + linker.getLink(aifi.getLogicalDB(), aifi.getAccID());
                }
            }
            
            return inferredString;
        }

        return "";
    }
    public String getReferences() {
        List<Reference> references = annot.getReferences();
        
        if (references == null || references.size() == 0) {
            return "";
        }
        
        String refString = "";
        Boolean first = Boolean.TRUE;
        for (Reference ref: references) {
            if (first) {
		refString = "<a href='" + fewiUrl + "reference/" + ref.getJnumID() 
                            + "'>" + ref.getJnumID() + "</a>";
                first = Boolean.FALSE;
            }
            else {
		refString = refString + ", <a href='" + fewiUrl + "reference/" + ref.getJnumID() 
                            + "'>" + ref.getJnumID() + "</a>";
            }
        }
        return refString;
    }


}
