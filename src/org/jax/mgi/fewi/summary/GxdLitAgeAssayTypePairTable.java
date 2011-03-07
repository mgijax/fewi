package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitAgeAssayTypePairTable {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(GxdLitAgeAssayTypePairTable.class);

	// encapsulated row object
	private List<String> ages;
	private List<GxdLitAssayTypeSummaryRow> assayTypes;
	
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private GxdLitAgeAssayTypePairTable () {}

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
