package org.jax.mgi.fewi.summary;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.util.FormatHelper;


/**
 * wrapper around a marker;  represents on row in summary
 */
public class MarkerSummaryRow
{
	//-------------------
	// instance variables
	//-------------------

	// encapsulated row object
	private final SolrSummaryMarker marker;

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
		String whyMatched = StringUtils.join(marker.getHighlights(),"<br>");
		whyMatched = whyMatched.replace("\"", "");
    	return whyMatched;
    }

}
