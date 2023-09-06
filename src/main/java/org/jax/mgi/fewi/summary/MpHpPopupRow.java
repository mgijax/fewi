package org.jax.mgi.fewi.summary;

// Wrapper over a monitored result object.
public class MpHpPopupRow {
	String searchId;
	String searchTerm;
	String searchTermDefinition;
	String matchType;
	String matchMethod;
	String matchTermID;
	String matchTermName;
	String matchTermSynonym;		
	String matchTermDefinition;		
	
	public MpHpPopupRow(String searchId, String searchTerm, String searchTermDefinition, String matchType, String matchMethod, String matchTermID, String matchTermName, String matchTermSynonym, String matchTermDefinition) {
		this.searchId = searchId;
		this.searchTerm = searchTerm;
		this.searchTermDefinition = searchTermDefinition;
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

	public String getSearchTermDefinition() {
		return searchTermDefinition;
	}

	public String getMatchMethod() {
		if (matchMethod.equals("ManualMappingCuration")) {
			return "manual";
		}
		if (matchMethod.equals("LogicalReasoning")) {
			return "logical";
		}
		if (matchMethod.equals("LexicalMatching")) {
			return "lexical";
		}
		if (matchMethod.equals("mgiSynonymLexicalMatching")) {
			return "lexical";
		}
		if (matchMethod.equals("mgiTermLexicalMatching")) {
			return "lexical";
		}
		return matchMethod;
	}

	public String getMatchType() {
		if (matchType.equals("relatedMatch")) {
			return "related";
		}
		if (matchType.equals("broadMatch")) {
			return "broad";
		}
		if (matchType.equals("narrowMatch")) {
			return "narrow";
		}
		if (matchType.equals("closeMatch")) {
			return "close";
		}
		if (matchType.equals("exactMatch")) {
			return "exact";
		}
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
