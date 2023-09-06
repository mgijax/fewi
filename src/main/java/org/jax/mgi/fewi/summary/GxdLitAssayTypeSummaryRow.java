package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.config.ContextLoader;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitAssayTypeSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private List<GxdLitAgeAssayTypePairTableCount> counts = new ArrayList<GxdLitAgeAssayTypePairTableCount> ();
	private String assayType;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

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
