package org.jax.mgi.fewi.searchUtil.entities;

import java.util.ArrayList;

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
	private Double cm = null;
	private String strand = null;
	private String coordinateDisplay = "";
	private String locationDisplay = "";

	ArrayList<String> highlights = new ArrayList<String>();


	// marker key
	public String getMarkerKey() {
		return markerKey;
	}
	public void setMarkerKey(String markerKey) {
		this.markerKey = markerKey;
	}

	// symbol
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	// MGI ID
	public String getMgiId() {
		return mgiId;
	}
	public void setMgiId(String mgiId) {
		this.mgiId = mgiId;
	}

	// feature type
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	//name
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	// coordinate display
	public String getCoordinateDisplay() {
		return coordinateDisplay;
	}
	public void setCoordinateDisplay(String coordinateDisplay) {
		this.coordinateDisplay = coordinateDisplay;
	}

	// location display
	public String getLocationDisplay() {
		return locationDisplay;
	}
	public void setLocationDisplay(String locationDisplay) {
		this.locationDisplay = locationDisplay;
	}

	// chromosome
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	// start coord
	public Integer getCoordStart() {
		return coordStart;
	}
	public String getCoordStartStr() {
		String coordStartStr = "";
		if (this.coordStart != null) {
			coordStartStr = this.coordStart.toString();
		}
		return coordStartStr;
	}
	public void setCoordStart(Integer coordStart) {
		this.coordStart = coordStart;
	}

	// end coord
	public Integer getCoordEnd() {
		return coordEnd;
	}
	public String getCoordEndStr() {
		String coordEndStr = "";
		if (this.coordEnd != null) {
			coordEndStr = this.coordEnd.toString();
		}
		return coordEndStr;
	}
	public void setCoordEnd(Integer coordEnd) {
		this.coordEnd = coordEnd;
	}

	// cM position
	public Double getCm() {
		return cm;
	}
	public String getCmStr() {
		String cmStr = "";
		if (this.cm != null) {
			cmStr = this.cm.toString();
		}
		return cmStr;
	}
	public void setCm(Double cm) {
		this.cm = cm;
	}

	// strand
	public String getStrand() {
		String strandStr = "";
		if (this.strand != null) {
			strandStr = strand;
		}
		return strandStr;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}

	// highlights
	public ArrayList<String> getHighlights() {
		return highlights;
	}
	public void setHighlights(ArrayList<String> highlights) {
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
