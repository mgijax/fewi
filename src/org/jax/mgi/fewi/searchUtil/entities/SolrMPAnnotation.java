package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

public class SolrMPAnnotation
{
	String annotationKey;
	String searchTermID;
	String genotypeKey;
	String allelePairs;
	String backgroundStrain;
	String term;
	String termID;
	List<String> references;
	
	public String getAnnotationKey() {
		return annotationKey;
	}
	public void setAnnotationKey(String annotationKey) {
		this.annotationKey = annotationKey;
	}

	public String getSearchTermID() {
		return searchTermID;
	}
	public void setSearchTermID(String searchTermID) {
		this.searchTermID = searchTermID;
	}

	public String getGenotypeKey() {
		return genotypeKey;
	}
	public void setGenotypeKey(String genotypeKey) {
		this.genotypeKey = genotypeKey;
	}

	public String getAllelePairs() {
		return allelePairs;
	}
	public void setAllelePairs(String allelePairs) {
		this.allelePairs = allelePairs;
	}

	public String getBackgroundStrain() {
		return backgroundStrain;
	}
	public void setBackgroundStrain(String backgroundStrain) {
		this.backgroundStrain = backgroundStrain;
	}

	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}

	public String getTermID() {
		return termID;
	}
	public void setTermID(String termID) {
		this.termID = termID;
	}

	public List<String> getReferences() {
		return references;
	}
	public void setReferences(List<String> references) {
		this.references = references;
	}

	public String toString() {
		return "SolrMPAnnotation [ annotationKey: "
			+ annotationKey + ", "
			+ termID + ", "
			+ term + " ]";
	}
}
