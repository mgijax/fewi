package org.jax.mgi.fewi.summary;

import java.util.Date;

// Wrapper over a monitored result object.
public class MpHpPopupRow {
	String searchId = null;
	String searchTerm = null;
	String matchType = null;
	String matchMethod = null;
	String matchTermID = null;
	String matchTermName = null;
	String matchTermSynonym = null;		
	String matchTermDefinition = null;		
	
	public MpHpPopupRow(String searchId, String searchTerm, String matchType, String matchMethod, String matchTermID, String matchTermName, String matchTermSynonym, String matchTermDefinition) {
		this.searchId = searchId;
		this.searchTerm = searchTerm;
		this.matchType = matchType;
		this.matchMethod = matchMethod;
		this.matchTermID = matchTermID;
		this.matchTermName = matchTermName;
		this.matchTermSynonym = matchTermSynonym;
		this.matchTermDefinition = matchTermDefinition;
	}

	public String getSearchId() {
		return searchId;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public String getMatchMethod() {
		return matchMethod;
	}

	public String getMatchType() {
		return matchType;
	}

	public String getMatchTermID() {
		return matchTermID;
	}

	public String getMatchTermName() {
		return matchTermName;
	}
	
	public String getMatchTermSynonym() {
		return matchTermSynonym;
	}
	
	public String getMatchTermDefinition() {
		return matchTermDefinition;
	}
}
