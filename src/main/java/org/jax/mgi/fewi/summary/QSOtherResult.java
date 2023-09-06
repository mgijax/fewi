package org.jax.mgi.fewi.summary;

import java.util.List;

/**
 * Is: one result for the quick search's strain bucket
 */
public class QSOtherResult extends QSResult {

	private String uniqueKey;
	private String searchTermExact;
	private String searchTermInexact;
	private String searchTermStemmed;
	private String searchTermDisplay;
	private String searchTermType;
	private Integer searchTermWeight;
	private String stars;
	private int starCount = 0;

	private String primaryID;
	private String name;
	private String detailUri;
	private String objectType;
	private String objectSubtype;
	private Long sequenceNum;

	private List<String> goProcessFacets;
	private List<String> goFunctionFacets;
	private List<String> goComponentFacets;
	private List<String> diseaseFacets;
	private List<String> phenotypeFacets;
	private List<String> markerTypeFacets;

	public String getSearchTermInexact() {
		return searchTermInexact;
	}
	public void setSearchTermInexact(String searchTermInexact) {
		this.searchTermInexact = searchTermInexact;
	}
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	public String getSearchTermExact() {
		return searchTermExact;
	}
	public void setSearchTermExact(String searchTermExact) {
		this.searchTermExact = searchTermExact;
	}
	public String getSearchTermStemmed() {
		return searchTermStemmed;
	}
	public void setSearchTermStemmed(String searchTermStemmed) {
		this.searchTermStemmed = searchTermStemmed;
	}
	public String getSearchTermDisplay() {
		return searchTermDisplay;
	}
	public void setSearchTermDisplay(String searchTermDisplay) {
		this.searchTermDisplay = searchTermDisplay;
	}
	public String getSearchTermType() {
		return searchTermType;
	}
	public void setSearchTermType(String searchTermType) {
		this.searchTermType = searchTermType;
	}
	public Integer getSearchTermWeight() {
		return searchTermWeight;
	}
	public void setSearchTermWeight(Integer searchTermWeight) {
		this.searchTermWeight = searchTermWeight;
	}
	public List<String> getGoProcessFacets() {
		return goProcessFacets;
	}
	public void setGoProcessFacets(List<String> goProcessFacets) {
		this.goProcessFacets = goProcessFacets;
	}
	public List<String> getGoFunctionFacets() {
		return goFunctionFacets;
	}
	public void setGoFunctionFacets(List<String> goFunctionFacets) {
		this.goFunctionFacets = goFunctionFacets;
	}
	public List<String> getGoComponentFacets() {
		return goComponentFacets;
	}
	public void setGoComponentFacets(List<String> goComponentFacets) {
		this.goComponentFacets = goComponentFacets;
	}
	public List<String> getDiseaseFacets() {
		return diseaseFacets;
	}
	public void setDiseaseFacets(List<String> diseaseFacets) {
		this.diseaseFacets = diseaseFacets;
	}
	public List<String> getPhenotypeFacets() {
		return phenotypeFacets;
	}
	public void setPhenotypeFacets(List<String> phenotypeFacets) {
		this.phenotypeFacets = phenotypeFacets;
	}
	public List<String> getMarkerTypeFacets() {
		return markerTypeFacets;
	}
	public void setMarkerTypeFacets(List<String> markerTypeFacets) {
		this.markerTypeFacets = markerTypeFacets;
	}
	public String getStars() {
		return stars;
	}
	public int getStarCount() {
		return this.starCount;
	}
	public void setStars(String stars) {
		this.stars = stars;
		this.starCount = stars.length();
	}
	public String getPrimaryID() {
		return primaryID;
	}
	public void setPrimaryID(String primaryID) {
		this.primaryID = primaryID;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getObjectSubtype() {
		return objectSubtype;
	}
	public void setObjectSubtype(String objectSubtype) {
		this.objectSubtype = objectSubtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDetailUri() {
		return detailUri;
	}
	public void setDetailUri(String detailUri) {
		this.detailUri = detailUri;
	}
	public Long getSequenceNum() {
		return sequenceNum;
	}
	public void setSequenceNum(Long sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public void addBoost(int boost) {
		this.searchTermWeight += boost; 
	}
}
