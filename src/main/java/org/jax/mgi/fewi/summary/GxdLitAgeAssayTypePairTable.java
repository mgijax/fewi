package org.jax.mgi.fewi.summary;

import java.util.List;

import org.jax.mgi.fewi.config.ContextLoader;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitAgeAssayTypePairTable {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private List<String> ages;
	private List<GxdLitAssayTypeSummaryRow> assayTypes;
	
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

    public GxdLitAgeAssayTypePairTable (List<String> ages, List<GxdLitAssayTypeSummaryRow> assayTypes) {
    	this.ages = ages;
    	this.assayTypes = assayTypes;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public List<String> getAges() {
    	return ages;
    }
    public List<GxdLitAssayTypeSummaryRow> getAssayTypes() {
    	return assayTypes;
    }


}
