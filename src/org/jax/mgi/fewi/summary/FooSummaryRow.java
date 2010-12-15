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
public class FooSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(FooSummaryRow.class);

	// encapsulated row object
	private Marker foo;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private FooSummaryRow () {}

    public FooSummaryRow (Marker foo) {
    	this.foo = foo;
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
