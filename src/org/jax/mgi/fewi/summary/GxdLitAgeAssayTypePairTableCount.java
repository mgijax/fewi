package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.GxdLitAssayTypeAgePair;
import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitAgeAssayTypePairTableCount {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(GxdLitAgeAssayTypePairTableCount.class);

	// encapsulated row object
	private GxdLitAssayTypeAgePair firstPair;
	
	private GxdLitQueryForm queryForm;

	Integer count = 0;
	
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a foo to wrap
    private GxdLitAgeAssayTypePairTableCount () {}

    public GxdLitAgeAssayTypePairTableCount (GxdLitAssayTypeAgePair firstPair, GxdLitQueryForm queryForm) {
    	this.firstPair = firstPair;
    	this.queryForm = queryForm;
    	this.count = 1;
    	return;
    }

    public void addCount() {
    	this.count ++;
    }

    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getCountUrl () {
    	if (this.count.equals(1)) {
    		return "<a href=\""+ fewiUrl+"/gxdlit/key/"+this.firstPair.getIndexKey()+"\">" + count + "</a>";
    	}
    	else {
    		String queryUrl = fewiUrl + "/gxdlit/summary/ageAssay?";
    		queryUrl += "age=" + this.firstPair.getAge();
    		queryUrl += "&assayType=" + this.firstPair.getAssayType();
    		queryUrl += "&year=" + queryForm.getYear();
    		queryUrl += "&nomen=" + queryForm.getNomen();
    		queryUrl += "&journal=" + queryForm.getJournal();
    		queryUrl += "&text=" + queryForm.getText();
    		queryUrl += "&inAbstract=" + queryForm.isInAbstract();
    		queryUrl += "&inTitle=" + queryForm.isInTitle();
    		queryUrl += "&author=" + queryForm.getAuthor();
    		queryUrl += "&authorScope=" + queryForm.getAuthorScope();
    		return "<a href=\""+ queryUrl+"\">" + count + "</a>";
    	}
    }

}
