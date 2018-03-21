package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

// Is: a single MP cell in a correlation matrix (linked from marker detail; has anatomy x (expression + phenotype data) )
public class SolrMPCorrelationMatrixCell
{
	private String uniqueKey;
	private String anatomyID;
	private String anatomyTerm;
	private String markerSymbol;
	private Integer genoclusterKey;
	private String allelePairs;
	private Integer annotationCount;
	private Integer isNormal;
	private Integer hasBackgroundSensitivity;
	private Integer byGenocluster;
	private Integer children;

	/***--- getters ---***/
	
	public String getAllelePairs() {
		return allelePairs;
	}
	public String getAnatomyID() {
		return anatomyID;
	}
	public String getAnatomyTerm() {
		return anatomyTerm;
	}
	public Integer getAnnotationCount() {
		return annotationCount;
	}
	public Integer getChildren() {
		return children;
	}
	public Integer getByGenocluster() {
		return byGenocluster;
	}
	public Integer getGenoclusterKey() {
		return genoclusterKey;
	}
	public Integer getHasBackgroundSensitivity() {
		return hasBackgroundSensitivity;
	}
	public Integer getIsNormal() {
		return isNormal;
	}
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public String getUniqueKey() {
		return uniqueKey;
	}

	/***--- setters ---***/
	
	public void setAllelePairs(String allelePairs) {
		this.allelePairs = allelePairs;
	}
	public void setAnatomyID(String anatomyID) {
		this.anatomyID = anatomyID;
	}
	public void setAnatomyTerm(String anatomyTerm) {
		this.anatomyTerm = anatomyTerm;
	}
	public void setAnnotationCount(Integer annotationCount) {
		this.annotationCount = annotationCount;
	}
	public void setChildren(Integer children) {
		this.children = children;
	}
	public void setByGenocluster(Integer byGenocluster) {
		this.byGenocluster = byGenocluster;
	}
	public void setGenoclusterKey(Integer genoclusterKey) {
		this.genoclusterKey = genoclusterKey;
	}
	public void setHasBackgroundSensitivity(Integer hasBackgroundSensitivity) {
		this.hasBackgroundSensitivity = hasBackgroundSensitivity;
	}
	public void setIsNormal(Integer isNormal) {
		this.isNormal = isNormal;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	@Override
	public String toString() {
		return "SolrMPCorrelationMatrixCell [uniqueKey=" + uniqueKey + ", anatomyID=" + anatomyID + ", anatomyTerm="
				+ anatomyTerm + ", markerSymbol=" + markerSymbol + ", genoclusterKey=" + genoclusterKey
				+ ", allelePairs=" + allelePairs + ", annotationCount=" + annotationCount + ", isNormal=" + isNormal
				+ ", hasBackgroundSensitivity=" + hasBackgroundSensitivity + ", byGenocluster=" + byGenocluster + "]";
	}
}
