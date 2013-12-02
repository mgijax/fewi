package org.jax.mgi.fewi.searchUtil.entities;

import java.util.*;

import mgi.frontend.datamodel.hdp.HdpGridAnnotation;

public class SolrHdpGridData implements HdpGridAnnotation
{
	Integer gridClusterKey;
	Integer genoClusterKey;
	Integer markerKey;
	String term;
	String termType;
	String termId;
	String vocabName;
	String qualifier;
	Integer annotCount;
	Integer humanAnnotCount;

	// grid cluster key
	public Integer getGridClusterKey() {return gridClusterKey;}
	public void setGridClusterKey(Integer gridClusterKey) {
		this.gridClusterKey = gridClusterKey;
	}

	// geno cluster key
	public Integer getGenoClusterKey() {return genoClusterKey;}
	public void setGenoClusterKey(Integer genoClusterKey) {
		this.genoClusterKey = genoClusterKey;
	}

	// marker key
	public Integer getMarkerKey() {return markerKey;}
	public void setMarkerKey(Integer markerKey) {
		this.markerKey = markerKey;
	}

	// term
	public String getTerm() {return term;}
	public void setTerm(String term) {
		this.term = term;
	}

	// term type
	public String getTermType() {return termType;}
	public void setTermType(String termType) {
		this.termType = termType;
	}

	// term ID
	public String getTermId() {return termId;}
	public void setTermId(String termId) {
		this.termId = termId;
	}

	// vocab name
	public String getVocabName() {return vocabName;}
	public void setVocabName(String vocabName) {
		this.vocabName = vocabName;
	}
	
	//qualifier
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	
	// annotCount
	public Integer getAnnotCount() {
		return annotCount==null ? 0 : annotCount;
	}
	public void setAnnotCount(Integer annotCount) {
		this.annotCount = annotCount;
	}
	
	// humanAnnotCount
	public Integer getHumanAnnotCount() {
		return humanAnnotCount==null ? 0 : humanAnnotCount;
	}
	public void setHumanAnnotCount(Integer humanAnnotCount) {
		this.humanAnnotCount = humanAnnotCount;
	}
	
	@Override
	public String getTermIdentifier() 
	{
		if("header".equals(termType)) return term;
		return termId;
	}
}
