package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Accession;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.util.FewiLinker;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jax.mgi.fewi.util.IDLinker;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class AccessionSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(AccessionSummaryRow.class);

	// encapsulated row object
	private Accession acc;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

    private IDLinker  linker = ContextLoader.getIDLinker();
    
    private FewiLinker  feLinker = FewiLinker.getInstance();
    
    private Boolean useKeyUrl = Boolean.FALSE;

	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private AccessionSummaryRow () {}

    public AccessionSummaryRow (Accession acc) {
    	this.acc = acc;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getAccId() {
    	return acc.getDisplayID();
    }
    public String getLogicalDb() {
    	return linker.getLinks(acc.getLogicalDB(), acc.getDisplayID());
    }
    public String getMgiLink() {
    	
    	String baseUrl = "";

    	// Figure out which object type to pass to the linker, so we can 
    	// reduce our overall if complexity.
    	String type = "";
    	
    	if (acc.getObjectType().equals("Vocabulary Term")) {
    		type = acc.getDisplayType();
    	}
    	else {
    		type = acc.getObjectType();
    	}

    	if (! useKeyUrl) {
    		baseUrl =  feLinker.getFewiIDLink(type, acc.getDisplayID());
    	}
    	else {
    		baseUrl =  feLinker.getFewiKeyLink(type, "" + acc.getObjectKey());
    	}
    	return "<a href=\"" + baseUrl + "\">" + "MGI " + type + " Detail" +  "</a>"; 
    }
    public String getDisplayType() {
    	return acc.getDisplayType();
    }
    public String getDescription() {
    	return acc.getDescription();
    }

    public void setUseKey() {
    	this.useKeyUrl = Boolean.TRUE; 
    }
    
    public String getObjectType() {
    	return acc.getObjectType();
    }

}
