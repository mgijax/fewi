package org.jax.mgi.fewi.searchUtil.entities;

import java.util.*;


/**
 * Marker on marker summary
 */
public class SolrSummaryMarker
{
	private String markerKey;
	private String symbol = "";
	private String name = "";
	private String mgiId = "";
	private String featureType = "";
	private String chromosome = "";
	private Integer coordStart = null;
	private Integer coordEnd = null;
	private String coordinateDisplay = "";
	private String locationDisplay = "";
	
	Set<String> highlights=new LinkedHashSet<String>();
	
	
	public String getMarkerKey() {
		return markerKey;
	}
	public String getSymbol() {
		return symbol;
	}
	public String getMgiId() {
		return mgiId;
	}
	public String getFeatureType() {
		return featureType;
	}
	public void setMarkerKey(String markerKey) {
		this.markerKey = markerKey;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public void setMgiId(String mgiId) {
		this.mgiId = mgiId;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
	public String getCoordinateDisplay() {
		return coordinateDisplay;
	}
	public String getLocationDisplay() {
		return locationDisplay;
	}
	public void setCoordinateDisplay(String coordinateDisplay) {
		this.coordinateDisplay = coordinateDisplay;
	}
	public void setLocationDisplay(String locationDisplay) {
		this.locationDisplay = locationDisplay;
	}
	
	public String getChromosome() {
		return chromosome;
	}
	
	public Integer getCoordStart() {
		return coordStart;
	}
	public Integer getCoordEnd() {
		return coordEnd;
	}
	
	public void setCoordStart(Integer coordStart) {
		this.coordStart = coordStart;
	}
	public void setCoordEnd(Integer coordEnd) {
		this.coordEnd = coordEnd;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	
	
	public Set<String> getHighlights() {
		return highlights;
	}
	public void setHighlights(Set<String> highlights) {
		this.highlights = highlights;
	}
	@Override
	public String toString() {
		return "SolrSummaryMarker [markerKey=" + markerKey + ", symbol="
				+ symbol + ", name=" + name + ", mgiId=" + mgiId
				+ ", featureType=" + featureType + ", coordinateDisplay="
				+ coordinateDisplay + ", locationDisplay=" + locationDisplay
				+ "]";
	}
	
	
}
