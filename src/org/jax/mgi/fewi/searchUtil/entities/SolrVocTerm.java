package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrHdpEntity;

/*
 * This entity represents a vocab term object
 * 
 * Some fields may not pertain to every term, but maybe for a specific vocabulary.
 */
public class SolrVocTerm implements SolrHdpEntity
{
	String primaryId;
	String term;
	String vocabName;
	
	/*
	 *  vocabName=OMIM specific fields
	 *  They will likely be null for any other type of term
	 */
	Integer diseaseModelCount;
	Integer diseaseRefCount;
	List<String> diseaseMouseMarkers;
	List<String> diseaseHumanMarkers;
	
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
	
	@Override
	public String toString() {
		return "SolrVocTerm [term=" + term + "]";
	}
}
