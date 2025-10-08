package org.jax.mgi.fewi.searchUtil.entities;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.snpdatamodel.document.ESEntity;

public class SolrGxdAssay extends ESEntity implements SolrGxdEntity {

	String markerSymbol;
	String assayKey;
	String assayMgiid;
	String assayType;
	String jNum;
	String miniCitation;
	boolean hasImage;

	public String toString() {
		return "SolrGxdAssay: " + assayKey + " " + markerSymbol + ", " + assayMgiid;
	}
	// List<String> figure;

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public String getAssayKey() {
		return assayKey;
	}

	public void setAssayKey(String assayKey) {
		this.assayKey = assayKey;
	}

	public String getAssayMgiid() {
		return assayMgiid;
	}

	public void setAssayMgiid(String assayMgiid) {
		this.assayMgiid = assayMgiid;
	}

	public String getAssayType() {
		return assayType;
	}

	public void setAssayType(String assayType) {
		this.assayType = assayType;
	}

	public String getJNum() {
		return jNum;
	}

	public void setJNum(String jNum) {
		this.jNum = jNum;
	}

	public String getMiniCitation() {
		return miniCitation;
	}

	public void setMiniCitation(String miniCitation) {
		this.miniCitation = miniCitation;
	}

	public boolean getHasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

}
