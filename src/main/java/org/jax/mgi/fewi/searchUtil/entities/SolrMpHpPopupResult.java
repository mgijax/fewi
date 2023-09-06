package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;

public class SolrMpHpPopupResult
{
	String uniqueKey;
	String searchTermID;
	String searchTerm;
	String searchTermDefinition;
	String matchTermID;
	String matchTerm;
	String matchMethod;
	String matchType;
	String matchTermSynonym;
	String matchTermDefinition;
	
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	// searchTermID
	public String getSearchTermID() {
		return searchTermID;
	}
	public void setSearchTermID(String searchTermID) {
		this.searchTermID = searchTermID;
	}

	// searchTerm
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	// searchTermDefinition
	public String getSearchTermDefinition() {
		return searchTermDefinition;
	}
	public void setSearchTermDefinition(String searchTermDefinition) {
		this.searchTermDefinition = searchTermDefinition;
	}

	// matchTermID
	public String getMatchTermID() {
		return matchTermID;
	}
	public void setMatchTermID(String matchTermID) {
		this.matchTermID = matchTermID;
	}

	// matchTerm
	public String getMatchTerm() {
		return matchTerm;
	}
	public void setMatchTerm(String matchTerm) {
		this.matchTerm = matchTerm;
	}

	// matchMethod
	public String getMatchMethod() {
		return matchMethod;
	}
	public void setMatchMethod(String matchMethod) {
		this.matchMethod = matchMethod;
	}

	// matchType
	public String getMatchType() {
		return matchType;
	}
	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	// matchTermSynonym
	public String getMatchTermSynonym() {
		return matchTermSynonym;
	}
	public void setMatchTermSynonym(String matchTermSynonym) {
		this.matchTermSynonym = matchTermSynonym;
	}

	// 
	public String getMatchTermDefinition() {
		return matchTermDefinition;
	}
	public void setMatchTermDefinition(String matchTermDefinition) {
		this.matchTermDefinition = matchTermDefinition;
	}

	public String toString() {
		return "SolrMpHpPopupResult [ uniqueKey: "
			+ uniqueKey + ", "
			+ searchTermID + ", "
			+ searchTerm + ", "
			+ matchTermID + ", "
			+ matchTerm + ", "
			+ matchMethod + ", "
			+ matchType + ", "
			+ matchTermSynonym + ", "
			+ matchTermDefinition + " ]";
	}
}
