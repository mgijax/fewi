package org.jax.mgi.fewi.summary;

import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.Reference;

import org.springframework.beans.factory.annotation.Autowired;
import org.jax.mgi.fewi.config.ContextLoader;
import org.springframework.stereotype.Component;
import org.jax.mgi.fewi.util.IDLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around an Annotation;  represents on row in summary
 */
public class GOSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(GOSummaryRow.class);

	// encapsulated row object
	private Annotation annot;
	
	private Marker marker;
	
	private Reference reference;

	private IDLinker  linker = ContextLoader.getIDLinker();

	// config values
    String wiUrl = ContextLoader.getConfigBean().getProperty("WI_URL");
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private GOSummaryRow () {}

    public GOSummaryRow (Annotation annot, Marker marker) {
    	this.annot = annot;
    	this.marker = marker;
    	return;
    }

    public GOSummaryRow (Annotation annot, Reference reference) {
    	this.annot = annot;
    	this.reference = reference;
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
            
	    if (linker == null) {
		linker = ContextLoader.getIDLinker();
	    }

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
