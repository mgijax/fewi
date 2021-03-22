package org.jax.mgi.fewi.summary;

import java.util.List;

/**
 * Is: one result for the quick search's vocab/strain bucket
 */
public class QSVocabResult extends QSResult {

	private String uniqueKey;
	private String searchTermExact;
	private String searchTermInexact;
	private String searchTermStemmed;
	private String searchTermDisplay;
	private String searchTermType;
	private Integer searchTermWeight;

	private String primaryID;
	private String term;
	private String termType;
	private String vocabName;
	private String rawVocabName;
	private String detailUri;
	private Long annotationCount;
	private String annotationText;
	private String annotationUri;
	private Long sequenceNum;

	private String stars;
	private int starCount = 0;

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
	public int getStarCount() {
		return starCount;
	}
	public void setStarCount(int starCount) {
		this.starCount = starCount;
	}
	public String getStars() {
		return stars;
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
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getTermType() {
		return termType;
	}
	public void setTermType(String termType) {
		this.termType = termType;
	}
	public String getVocabName() {
		return vocabName;
	}
	public void setVocabName(String vocabName) {
		this.vocabName = vocabName;
	}
	public String getDetailUri() {
		return detailUri;
	}
	public void setDetailUri(String detailUri) {
		this.detailUri = detailUri;
	}
	public Long getAnnotationCount() {
		return annotationCount;
	}
	public void setAnnotationCount(Long annotationCount) {
		this.annotationCount = annotationCount;
	}
	public String getAnnotationText() {
		return annotationText;
	}
	public void setAnnotationText(String annotationText) {
		this.annotationText = annotationText;
	}
	public String getAnnotationUri() {
		return annotationUri;
	}
	public void setAnnotationUri(String annotationUri) {
		this.annotationUri = annotationUri;
	}
	public Long getSequenceNum() {
		return sequenceNum;
	}
	public void setSequenceNum(Long sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	public String getRawVocabName() {
		return rawVocabName;
	}
	public void setRawVocabName(String rawVocabName) {
		this.rawVocabName = rawVocabName;
	}

	@Override
	public String toString() {
		return primaryID + ", " + term;
	}
	@Override
	public void addBoost(int boost) {
		this.searchTermWeight += boost; 
	}
}
