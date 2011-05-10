package org.jax.mgi.fewi.summary;

import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationInferredFromID;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.config.ContextLoader;
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
	
	private IDLinker  linker = ContextLoader.getIDLinker();

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
    	String termText = "<a href='"+ fewiUrl +"go/" + annot.getTermID() + "'> " + annot.getTerm() + "</a>";
    	if (annot.getQualifier() != null && annot.getQualifier().equals("NOT")) {
    		return "<b>NOT</b> " + termText;
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
                	inferredString = linker.getLink(aifi.getLogicalDB(), aifi.getAccID());
                }
                else {
                	inferredString = inferredString + "|" + linker.getLink(aifi.getLogicalDB(), aifi.getAccID());
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
        
        //deferred code to check the number of references.
        //if (references.size() >= 1 && references.size() < 5)
        //{
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
        //}
        // Old requirement to list a number for cases where we have more than 5 references.  This 
        // is currently being deferred.
/*        else {
            return "<a href='" + fewiUrl + "references/marker/" + marker.getPrimaryID() 
            	+ "?term="+annot.getTerm()+"&evidence="+annot.getEvidenceCode()+"'>" 
            	+ references.size() + "</a>";            
        }*/
        
    }


}
