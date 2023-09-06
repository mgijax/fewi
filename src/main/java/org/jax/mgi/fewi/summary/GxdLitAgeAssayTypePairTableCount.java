package org.jax.mgi.fewi.summary;

import org.jax.mgi.fe.datamodel.GxdLitAssayTypeAgePair;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;


/**
 * wrapper around a foo;  represents on row in summary
 */
public class GxdLitAgeAssayTypePairTableCount {

	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private GxdLitAssayTypeAgePair firstPair;
	
	private GxdLitQueryForm queryForm;

	Integer count = 0;
	
	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

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
    		return "<a href=\""+ fewiUrl+"gxdlit/key/"+this.firstPair.getIndexKey()+"\">" + count + "</a>";
    	}
    	else {
    		String queryUrl = fewiUrl + "gxdlit/summary/ageAssay?";
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
    		if (queryForm.getReference_key() != null) {
    			queryUrl += "&reference_key=" + queryForm.getReference_key();
    		}
    		if (queryForm.getMarker_key() != null) {
    			queryUrl += "&marker_key=" + queryForm.getMarker_key();
    		}
    		if (queryForm.getMarkerId() != null) {
    			queryUrl += "&markerId=" + queryForm.getMarkerId();
    		}
    		
    		return "<a href=\""+ queryUrl.replaceAll("\"", "%22") +"\">" + count + "</a>";
    	}
    }

}
