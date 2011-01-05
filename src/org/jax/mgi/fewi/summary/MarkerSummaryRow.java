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
 * wrapper around a marker;  represents on row in summary
 */
public class MarkerSummaryRow {

	//-------------------
	// instance variables
	//-------------------

    private Logger logger = LoggerFactory.getLogger(MarkerSummaryRow.class);

	// encapsulated row object
	private Marker marker;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");


	//-------------
	// constructors
	//-------------

	// hide the default constructor - we NEED a marker to wrap
    private MarkerSummaryRow () {}

    public MarkerSummaryRow (Marker marker) {
    	this.marker = marker;
    	return;
    }


    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------

    public String getField1() {
    	return "<a href='" + fewiUrl + "marker/markerId'>MGI marker Detail </a>";
    }
    public String getField2() {
    	return "<a href='" + fewiUrl
    	  + "marker/reference/J:28634'>marker Summary for Reference J:28634</a>";
    }
    public String getField3() {
    	return "Marker 2 interesting data";
    }

    public String getSymbol() {
        return "<a href='" + fewiUrl + "marker/" + marker.getPrimaryID() + "'>" + marker.getSymbol() +" </a>";
    }
    
    public String getName() {
        return marker.getName();
    }
    
    public String getChr() {        
        MarkerLocation bestLoc = marker.getPreferredCoordinates();
        return bestLoc.getChromosome();
    }    
    
    public String getLocation() {        
        MarkerLocation bestLoc = marker.getPreferredCoordinates();
        return bestLoc.getStartCoordinate() + "-" + bestLoc.getEndCoordinate();
    }

    public String getCM() {        
        MarkerLocation bestLoc = marker.getPreferredCentimorgans();
        return "" + bestLoc.getCmOffset();
    }
    
    public String getStrand() {
        MarkerLocation bestLoc = marker.getPreferredCoordinates();
        return bestLoc.getStrand();
    }
    
    public String getType() {
        return marker.getMarkerType();
    }
    
    public String getPrimaryID() {
        return marker.getPrimaryID();
    }
    
}
