package org.jax.mgi.fewi.summary;

import mgi.frontend.datamodel.Annotation;

import org.jax.mgi.fewi.config.ContextLoader;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class OMIMSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private Annotation annot;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
    String ncbiOmim = ContextLoader.getExternalUrls().getProperty("OMIM");


	//-------------
	// constructors
	//-------------

    public OMIMSummaryRow (Annotation markerAnnot) {
    	this.annot = markerAnnot;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getId () {
        return "<a href='" +ncbiOmim + annot.getTermID() +"' target='new'> " + annot.getTermID() + "</a>";
    }
    
    public String getTerm () {
        return "<a href='"+ fewiUrl +"omim/" + annot.getTermID() + "'> " + annot.getTerm() + "</a>";
    }
    
    public String getField1() {
    	return "<a href='" + fewiUrl + "foo/fooId'>MGI Foo Detail </a>";
    }
    public String getField2() {
    	return "<a href='" + fewiUrl
    	  + "foo/reference/J:28634'>Foo Summary for Reference J:28634</a>";
    }
    public String getField3() {
    	return "field3Data";
    }



}
