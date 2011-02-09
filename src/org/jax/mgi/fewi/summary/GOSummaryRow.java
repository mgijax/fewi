package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.config.ContextLoader;
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

	// config values
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


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getCategory() {
    	return annot.getDagName();
    }
    public String getTerm() {
        return "<a href='"+ fewiUrl +"go/" + annot.getTermID() + "'> " + annot.getTerm() + "</a>";
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
                    inferredString = "<a href='" + fewiUrl + "accession/" + aifi.getAccID() 
                    + "'>" + aifi.getAccID() + "</a>";
                    first = Boolean.FALSE;
                }
                else {
                    inferredString = inferredString + "|<a href='" + fewiUrl + "accession/" + aifi.getAccID() 
                    + "'>" + aifi.getAccID() + "</a>";
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
        
        if (references.size() >= 1 && references.size() < 5)
        {
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
        else {
            return "<a href='" + fewiUrl + "references/marker/" + marker.getPrimaryID() 
            	+ "?term="+annot.getTerm()+"&evidence="+annot.getEvidenceCode()+"'>" 
            	+ references.size() + "</a>";            
        }
        
    }


}
