package org.jax.mgi.fewi.hmdc.solr;

import java.util.List;

public class SolrHdpDisease implements SolrHdpEntityInterface {

	private String primaryId;
	private String term;
	private String vocabName;
	
	private Integer diseaseModelCount;
	private Integer diseaseRefCount;
	private List<String> diseaseMouseMarkers;
	private List<String> diseaseHumanMarkers;
	private List<String> doIds;
	private List<String> omimIds;
	
	public String getPrimaryId() {
		return primaryId;
	}
	public String getTerm() {
		return term;
	}
	public String getVocabName() {
		return vocabName;
	}

	public Integer getDiseaseModelCount() {
		return diseaseModelCount;
	}
	public List<String> getDiseaseMouseMarkers() {
		return diseaseMouseMarkers;
	}
	public List<String> getDiseaseHumanMarkers() {
		return diseaseHumanMarkers;
	}
	
	public void setPrimaryId(String primaryId) {
		this.primaryId = primaryId;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public void setVocabName(String vocabName) {
		this.vocabName = vocabName;
	}
	
	public void setDiseaseModelCount(Integer diseaseModelCount) {
		this.diseaseModelCount = diseaseModelCount;
	}
	public void setDiseaseMouseMarkers(List<String> diseaseMouseMarkers) {
		this.diseaseMouseMarkers = diseaseMouseMarkers;
	}
	public void setDiseaseHumanMarkers(List<String> diseaseHumanMarkers) {
		this.diseaseHumanMarkers = diseaseHumanMarkers;
	}
	public Integer getDiseaseRefCount() {
		return diseaseRefCount;
	}
	public void setDiseaseRefCount(Integer diseaseRefCount) {
		this.diseaseRefCount = diseaseRefCount;
	}

	public List<String> getDoIds() {
		return doIds;
	}
	public void setDoIds(List<String> doIds) {
		this.doIds = doIds;
	}
	
	public List<String> getOmimIds() {
		return omimIds;
	}
	public void setOmimIds(List<String> omimIds) {
		this.omimIds = omimIds;
	}

	@Override
	public String toString() {
		return "SolrVocTerm [term=" + term + "]";
	}
}
