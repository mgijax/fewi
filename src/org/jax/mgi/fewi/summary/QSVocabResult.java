package org.jax.mgi.fewi.summary;

import java.util.List;

/**
 * Is: one result for the quick search's vocab/strain bucket
 */
public class QSVocabResult {

	private String primaryID;
	private List<String> accID;
	private String term;
	private String termType;
	private String definition;
	private List<String> synonym;
	private String vocabName;
	private String detailUri;
	private Long annotationCount;
	private String annotationText;
	private String annotationUri;
	private Integer sequenceNum;
	private Float score;
	private String stars;
	private String bestMatchType;
	private String bestMatchText;

	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	public String getStars() {
		return stars;
	}
	public void setStars(String stars) {
		this.stars = stars;
	}
	public String getBestMatchType() {
		return bestMatchType;
	}
	public void setBestMatchType(String bestMatchType) {
		this.bestMatchType = bestMatchType;
	}
	public String getBestMatchText() {
		return bestMatchText;
	}
	public void setBestMatchText(String bestMatchText) {
		this.bestMatchText = bestMatchText;
	}
	public String getPrimaryID() {
		return primaryID;
	}
	public void setPrimaryID(String primaryID) {
		this.primaryID = primaryID;
	}
	public List<String> getAccID() {
		return accID;
	}
	public void setAccID(List<String> accID) {
		this.accID = accID;
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
	public List<String> getSynonym() {
		return synonym;
	}
	public void setSynonym(List<String> synonym) {
		this.synonym = synonym;
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
	public Integer getSequenceNum() {
		return sequenceNum;
	}
	public void setSequenceNum(Integer sequenceNum) {
		this.sequenceNum = sequenceNum;
	}
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return term;
	}
}