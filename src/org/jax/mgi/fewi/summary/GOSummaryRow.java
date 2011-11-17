package org.jax.mgi.fewi.summary;

import java.util.*;

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

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    String wiUrl   = ContextLoader.getConfigBean().getProperty("WI_URL");

	// encapsulated row objects
	private Annotation annot;
	private Marker marker;

	// object used to generate the AccID anchors
	private IDLinker  linker = ContextLoader.getIDLinker();


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a both an annotation and marker
    private GOSummaryRow () {}

    public GOSummaryRow (Annotation annot, Marker marker) {
    	this.annot = annot;
    	this.marker = marker;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    // CATAGORY
    public String getCategory() {
    	return annot.getDagName();
    }

    // CLASSIFICATION
    public String getTerm() {

    	String termText = "<a href='"+ wiUrl +"searches/GO.cgi?id=" + annot.getTermID()
    	    + "'> " + annot.getTerm() + "</a>";

    	if (annot.getQualifier() != null && annot.getQualifier().equals("NOT")) {
    		return "<b>NOT</b> " + termText;
    	}
        return termText;
    }

    // EVIDENCE
    public String getEvidence() {
    	return annot.getEvidenceCode();
    }

    // INFERRED FROM
    public String getInferred() {

        List<AnnotationInferredFromID> inferred = annot.getInferredFromList();

        if (inferred.size() >= 1)
        {
            String inferredString = "";
            Boolean first = Boolean.TRUE;

            for (AnnotationInferredFromID aifi: inferred) {
                if (first) {
                	inferredString = linker.getLink(aifi.getLogicalDB(), aifi.getAccID());
                	first = Boolean.FALSE;
                }
                else {
                	inferredString = inferredString + " | " + linker.getLink(aifi.getLogicalDB(), aifi.getAccID());
                }
            }

            return inferredString;
        }

        return "";
    }

    // REFERENCE
    public String getReferences() {

        List<Reference> references = annot.getReferences();
        if (references == null || references.size() == 0) {
            return "";
        }

        //deferred code to check the number of references.
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
