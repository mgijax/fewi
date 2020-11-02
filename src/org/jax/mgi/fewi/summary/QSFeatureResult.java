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
	private List<String> proteinDomains;
	private List<String> synonym;
	private List<String> orthologNomenOrg;

	private String markerSymbol;
	private String markerName;
	private List<String> markerSynonym;

	private List<String> functionAnnotationsID;
	private List<String> functionAnnotationsTerm;
	private List<String> functionAnnotationsSynonym;
	private List<String> functionAnnotationsDefinition;

	private List<String> processAnnotationsID;
	private List<String> processAnnotationsTerm;
	private List<String> processAnnotationsSynonym;
	private List<String> processAnnotationsDefinition;

	private List<String> componentAnnotationsID;
	private List<String> componentAnnotationsTerm;
	private List<String> componentAnnotationsSynonym;
	private List<String> componentAnnotationsDefinition;

	private List<String> phenotypeAnnotations;
	private List<String> humanPhenotypeAnnotations;
	private List<String> gxdAnnotationsWithTS;
	private List<String> diseaseAnnotations;
	private String featureType;
	private String detailUri;
	private Long sequenceNum;
	private String chromosome;
	private String startCoord;
	private String endCoord;
	private String strand;
	private Float score;
	private String stars;
	private String bestMatchType;
	private String bestMatchText;

	public List<String> getFunctionAnnotationsID() {
		return functionAnnotationsID;
	}
	public void setFunctionAnnotationsID(List<String> functionAnnotationsID) {
		this.functionAnnotationsID = functionAnnotationsID;
	}
	public List<String> getFunctionAnnotationsTerm() {
		return functionAnnotationsTerm;
	}
	public void setFunctionAnnotationsTerm(List<String> functionAnnotationsTerm) {
		this.functionAnnotationsTerm = functionAnnotationsTerm;
	}
	public List<String> getFunctionAnnotationsSynonym() {
		return functionAnnotationsSynonym;
	}
	public void setFunctionAnnotationsSynonym(List<String> functionAnnotationsSynonym) {
		this.functionAnnotationsSynonym = functionAnnotationsSynonym;
	}
	public List<String> getFunctionAnnotationsDefinition() {
		return functionAnnotationsDefinition;
	}
	public void setFunctionAnnotationsDefinition(List<String> functionAnnotationsDefinition) {
		this.functionAnnotationsDefinition = functionAnnotationsDefinition;
	}
	public List<String> getProcessAnnotationsID() {
		return processAnnotationsID;
	}
	public void setProcessAnnotationsID(List<String> processAnnotationsID) {
		this.processAnnotationsID = processAnnotationsID;
	}
	public List<String> getProcessAnnotationsTerm() {
		return processAnnotationsTerm;
	}
	public void setProcessAnnotationsTerm(List<String> processAnnotationsTerm) {
		this.processAnnotationsTerm = processAnnotationsTerm;
	}
	public List<String> getProcessAnnotationsSynonym() {
		return processAnnotationsSynonym;
	}
	public void setProcessAnnotationsSynonym(List<String> processAnnotationsSynonym) {
		this.processAnnotationsSynonym = processAnnotationsSynonym;
	}
	public List<String> getProcessAnnotationsDefinition() {
		return processAnnotationsDefinition;
	}
	public void setProcessAnnotationsDefinition(List<String> processAnnotationsDefinition) {
		this.processAnnotationsDefinition = processAnnotationsDefinition;
	}
	public List<String> getComponentAnnotationsID() {
		return componentAnnotationsID;
	}
	public void setComponentAnnotationsID(List<String> componentAnnotationsID) {
		this.componentAnnotationsID = componentAnnotationsID;
	}
	public List<String> getComponentAnnotationsTerm() {
		return componentAnnotationsTerm;
	}
	public void setComponentAnnotationsTerm(List<String> componentAnnotationsTerm) {
		this.componentAnnotationsTerm = componentAnnotationsTerm;
	}
	public List<String> getComponentAnnotationsSynonym() {
		return componentAnnotationsSynonym;
	}
	public void setComponentAnnotationsSynonym(List<String> componentAnnotationsSynonym) {
		this.componentAnnotationsSynonym = componentAnnotationsSynonym;
	}
	public List<String> getComponentAnnotationsDefinition() {
		return componentAnnotationsDefinition;
	}
	public void setComponentAnnotationsDefinition(List<String> componentAnnotationsDefinition) {
		this.componentAnnotationsDefinition = componentAnnotationsDefinition;
	}
	public List<String> getPhenotypeAnnotations() {
		return phenotypeAnnotations;
	}
	public void setPhenotypeAnnotations(List<String> phenotypeAnnotations) {
		this.phenotypeAnnotations = phenotypeAnnotations;
	}
	public List<String> getHumanPhenotypeAnnotations() {
		return humanPhenotypeAnnotations;
	}
	public void setHumanPhenotypeAnnotations(List<String> humanPhenotypeAnnotations) {
		this.humanPhenotypeAnnotations = humanPhenotypeAnnotations;
	}
	public List<String> getGxdAnnotationsWithTS() {
		return gxdAnnotationsWithTS;
	}
	public void setGxdAnnotationsWithTS(List<String> gxdAnnotationsWithTS) {
		this.gxdAnnotationsWithTS = gxdAnnotationsWithTS;
	}
	public List<String> getDiseaseAnnotations() {
		return diseaseAnnotations;
	}
	public void setDiseaseAnnotations(List<String> diseaseAnnotations) {
		this.diseaseAnnotations = diseaseAnnotations;
	}
	public List<String> getProteinDomains() {
		return proteinDomains;
	}
	public void setProteinDomains(List<String> proteinDomains) {
		this.proteinDomains = proteinDomains;
	}
	public List<String> getOrthologNomenOrg() {
		return orthologNomenOrg;
	}
	public void setOrthologNomenOrg(List<String> orthologNomenOrg) {
		this.orthologNomenOrg = orthologNomenOrg;
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
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public String getMarkerName() {
		return markerName;
	}
	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}
	public List<String> getMarkerSynonym() {
		return markerSynonym;
	}
	public void setMarkerSynonym(List<String> markerSynonym) {
		this.markerSynonym = markerSynonym;
	}
	@Override
	public String toString() {
		return symbol;
	}
}
