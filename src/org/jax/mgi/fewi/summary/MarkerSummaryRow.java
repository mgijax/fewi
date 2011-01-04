package org.jax.mgi.fewi.summary;

import java.util.*;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;

import org.jax.mgi.fewi.util.DBConstants;
import org.jax.mgi.fewi.config.ContextLoader;

import javax.persistence.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a marker2;  represents on row in summary
 */
public class MarkerSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(MarkerSummaryRow.class);

	// encapsulated row object
	private Marker marker2;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a marker2 to wrap
    private MarkerSummaryRow () {}

    public MarkerSummaryRow (Marker marker2) {
    	this.marker2 = marker2;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getField1() {
    	return "<a href='" + fewiUrl + "marker2/marker2Id'>MGI Marker2 Detail </a>";
    }
    public String getField2() {
    	return "<a href='" + fewiUrl
    	  + "marker2/reference/J:28634'>Marker2 Summary for Reference J:28634</a>";
    }
    public String getField3() {
    	return "Marker 2 interesting data";
    }

    public String getSymbol() {
        return "<a href='" + fewiUrl + "marker2/" + marker2.getPrimaryID() + "'>" + marker2.getSymbol() +" </a>";
    }
    
    public String getName() {
        return marker2.getName();
    }
    
    public String getChr() {        
        MarkerLocation bestLoc = marker2.getPreferredCoordinates();
        return bestLoc.getChromosome();
    }    
    
    public String getLocation() {        
        MarkerLocation bestLoc = marker2.getPreferredCoordinates();
        return bestLoc.getStartCoordinate() + "-" + bestLoc.getEndCoordinate();
    }

    public String getCM() {        
        MarkerLocation bestLoc = marker2.getPreferredCentimorgans();
        return "" + bestLoc.getCmOffset();
    }
    
    public String getStrand() {
        return "";
    }
    
    public String getType() {
        return marker2.getMarkerType();
    }
    
    public String getPrimaryID() {
        return marker2.getPrimaryID();
    }
}
