package org.jax.mgi.fewi.hmdc.solr;

public class SolrHdpCoord implements SolrHdpEntityInterface {
	private String uniqueKey;
	private Integer markerKey;
	private String markerID;
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	public Integer getMarkerKey() {
		return markerKey;
	}
	public void setMarkerKey(Integer markerKey) {
		this.markerKey = markerKey;
	}
	public String getMarkerID() {
		return markerID;
	}
	public void setMarkerID(String markerID) {
		this.markerID = markerID;
	}
	
	@Override
	public String toString() {
		return "SolrHdpCoord [uniqueKey=" + uniqueKey + ", markerKey=" + markerKey + ", markerID=" + markerID + "]";
	}
}
