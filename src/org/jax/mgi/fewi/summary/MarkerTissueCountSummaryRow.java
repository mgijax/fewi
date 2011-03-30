package org.jax.mgi.fewi.summary;

import mgi.frontend.datamodel.MarkerTissueCount;

import org.jax.mgi.fewi.config.ContextLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a marker tissue count;  represents on row in summary
 */
public class MarkerTissueCountSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(MarkerTissueCountSummaryRow.class);

	// encapsulated row object
	private MarkerTissueCount mtc;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a marker tissue count to wrap
    private MarkerTissueCountSummaryRow () {}

    public MarkerTissueCountSummaryRow (MarkerTissueCount mtc) {
    	this.mtc = mtc;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getAll() {
    	if (mtc.getAllResultCount() > 0) {
    		return "<a href=\"" + fewiUrl + "gxd/assay/?q=markerKey=" + mtc.getMarkerKey()+ "&structureKey=" + mtc.getStructureKey()+ "\">" + mtc.getAllResultCount() + "</a>";
    	}
    	else {
    		return "0";
    	}
    }

    public String getNotDetected() {
    	if (mtc.getNotDetectedResultCount() > 0) {
    		return "<a href=\"" + fewiUrl + "gxd/assay/?q=markerKey=" + mtc.getMarkerKey()+ "&structureKey=" + mtc.getStructureKey()+ "&detected=0\">" + mtc.getNotDetectedResultCount() + "</a>";
    	}
    	else {
    		return "0";
    	}
    }

    public String getDetected() {
    	if (mtc.getDetectedResultCount() > 0) {
    		return "<a href=\"" + fewiUrl + "gxd/assay/?q=markerKey=" + mtc.getMarkerKey()+ "&structureKey=" + mtc.getStructureKey()+ "&detected=1\">" + mtc.getDetectedResultCount() + "</a>";
    	}
    	else {
    		return "0";
    	}
    }
    
    public String getStructure() {
    	return "<a href=\"" + fewiUrl + "ad/" +mtc.getStructureKey() + "\">" + "TS" + mtc.getStructure() + "</a>";
    }
}
