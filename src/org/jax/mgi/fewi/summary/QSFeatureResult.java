package org.jax.mgi.fewi.summary;

import java.util.List;

/**
 * Is: one result for the quick search's feature (marker + allele) bucket
 */
public class QSFeatureResult {

	private String primaryID;
	private List<String> accID;
	private String symbol;
	private String name;
	private Integer isMarker;
	private List<String> synonym;
	private String featureType;
	private String detailUri;
	private Long sequenceNum;
	private String chromosome;
	private String startCoord;
	private String endCoord;
	private String strand;
	private Float score;

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
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIsMarker() {
		return isMarker;
	}
	public void setIsMarker(Integer isMarker) {
		this.isMarker = isMarker;
	}
	public List<String> getSynonym() {
		return synonym;
	}
	public void setSynonym(List<String> synonym) {
		this.synonym = synonym;
	}
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
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
	public String getChromosome() {
		return chromosome;
	}
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}
	public String getStartCoord() {
		return startCoord;
	}
	public void setStartCoord(String startCoord) {
		this.startCoord = startCoord;
	}
	public String getEndCoord() {
		return endCoord;
	}
	public void setEndCoord(String endCoord) {
		this.endCoord = endCoord;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "QSFeatureResult [primaryID=" + primaryID + ", symbol=" + symbol + ", featureType=" + featureType + "]";
	}
}
