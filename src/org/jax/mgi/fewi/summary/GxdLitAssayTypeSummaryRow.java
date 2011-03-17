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
public class GxdLitAssayTypeSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(GxdLitAssayTypeSummaryRow.class);

	// encapsulated row object
	private List<GxdLitAgeAssayTypePairTableCount> counts = new ArrayList<GxdLitAgeAssayTypePairTableCount> ();
	private String assayType;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private GxdLitAssayTypeSummaryRow () {}

    public GxdLitAssayTypeSummaryRow (String assayType) {
    	this.assayType = assayType;
    	return;
    }

    public void addCount(GxdLitAgeAssayTypePairTableCount count) {
    	counts.add(count);
    }

    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getAssayType() {
    	return this.assayType;
    }
    public List<GxdLitAgeAssayTypePairTableCount> getCounts() {
    	return counts;
    }



}
