package org.jax.mgi.fewi.summary;

/**
 * Hold minimal data for a RNA-Seq heat map data cell
 */
public class GxdRnaSeqHeatMapCell {

	private String markerID;
	private String csmKey;
	private String avgQnTpm;

	public String getMarkerID() {
		return markerID;
	}
	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
	public String getCsmKey() {
		return csmKey;
	}
	public void setCsmKey(String csmKey) {
		this.csmKey = csmKey;
	}
	public String getAvgQnTpm() {
		return avgQnTpm;
	}
	public void setAvgQnTpm(String avgQnTpm) {
		this.avgQnTpm = avgQnTpm;
	}
}
