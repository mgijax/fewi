package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fewi.config.ContextLoader;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class FooSummaryRow {

	//-------------------
	// instance variables
	//-------------------

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

    public FooSummaryRow (Marker foo) {
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

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
