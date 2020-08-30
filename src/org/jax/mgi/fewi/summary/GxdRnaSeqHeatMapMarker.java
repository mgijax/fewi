package org.jax.mgi.fewi.summary;

/**
 * Hold minimal data for a RNA-Seq heat map data cell
 */
public class GxdRnaSeqHeatMapMarker implements Cloneable {

	public String symbol;
	public String markerID;
	public String ensemblGMID;
	public Integer index;

	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getMarkerID() {
		return markerID;
	}
	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
	public String getEnsemblGMID() {
		return ensemblGMID;
	}
	public void setEnsemblGMID(String ensemblGMID) {
		this.ensemblGMID = ensemblGMID;
	}
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}

	public GxdRnaSeqHeatMapMarker clone() throws CloneNotSupportedException {
		return (GxdRnaSeqHeatMapMarker) super.clone();
	}
}
