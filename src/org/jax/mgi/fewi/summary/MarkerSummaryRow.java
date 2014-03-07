package org.jax.mgi.fewi.summary;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.util.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * wrapper around a marker;  represents on row in summary
 */
public class MarkerSummaryRow 
{
	//-------------------
	// instance variables
	//-------------------
    private Logger logger = LoggerFactory.getLogger(MarkerSummaryRow.class);

	// encapsulated row object
	private SolrSummaryMarker marker;

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

	//-------------
	// constructors
	//-------------
    public MarkerSummaryRow (SolrSummaryMarker marker) {
    	this.marker = marker;
    }

    //------------------------------------------------------------------------
    // public instance methods;  JSON serializer will call all public methods
    //------------------------------------------------------------------------
    public String getLocation() {
    	return marker.getLocationDisplay();
    }
    
    public String getCoordinates() {
    	return marker.getCoordinateDisplay();
    }
    
    public String getSymbol() {
    	String detailUrl = fewiUrl+"marker/key/"+marker.getMarkerKey();
        return "<a href=\""+detailUrl+"\">"+FormatHelper.superscript(marker.getSymbol())+"</a>, "
    			+FormatHelper.superscript(marker.getName());
    }
    
    public String getFeatureType(){
    	return marker.getFeatureType();
    }
    
    public String getHighlights(){
    	return StringUtils.join(marker.getHighlights(),"<br>");
    }
    
}
