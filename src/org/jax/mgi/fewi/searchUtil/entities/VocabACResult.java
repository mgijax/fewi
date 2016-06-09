package org.jax.mgi.fewi.searchUtil.entities;

import java.util.List;


/**
 * Represents a result from the Vocab Term autocomplete hunter.
 * This is necessary so that we can get at all of the needed fields in the document
 * 
 * @author kstone
 *
 */
public class VocabACResult 
{
	private String term;
	private boolean isSynonym;
	private String originalTerm;
	//private String termKey;
	private List<String> derivedTerms;
	private String termId;
	private String rootVocab;
	private String displayVocab;
	private int markerCount;
	private boolean hasExpressionResults;

	public VocabACResult() {}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public boolean getIsSynonym() {
		return isSynonym;
	}
	public void setIsSynonym(boolean isSynonym) {
		this.isSynonym = isSynonym;
	}
	
	public String getOriginalTerm() {
		return originalTerm;
	}

	public void setOriginalTerm(String originalTerm) {
		this.originalTerm = originalTerm;
	}

	public List<String> getDerivedTerms() {
		return derivedTerms;
	}

	public void setDerivedTerms(List<String> derivedTerms) {
		this.derivedTerms = derivedTerms;
	}
	
	public String getRootVocab() {
		return rootVocab;
	}

	public void setRootVocab(String rootVocab) {
		this.rootVocab = rootVocab;
	}

	public String getDisplayVocab() {
		return displayVocab;
	}

	public void setDisplayVocab(String displayVocab) {
		this.displayVocab = displayVocab;
	}

	public int getMarkerCount() {
		return markerCount;
	}

	public void setMarkerCount(int markerCount) {
		this.markerCount = markerCount;
	}

	public boolean getHasExpressionResults() {
		return hasExpressionResults;
	}

	public void setHasExpressionResults(boolean hasExpressionResults) {
		this.hasExpressionResults = hasExpressionResults;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayVocab == null) ? 0 : displayVocab.hashCode());
		result = prime * result + (hasExpressionResults ? 1231 : 1237);
		result = prime * result + (isSynonym ? 1231 : 1237);
		result = prime * result + markerCount;
		result = prime * result
				+ ((originalTerm == null) ? 0 : originalTerm.hashCode());
		result = prime * result
				+ ((rootVocab == null) ? 0 : rootVocab.hashCode());
		result = prime * result + ((term == null) ? 0 : term.hashCode());
		result = prime * result + ((termId == null) ? 0 : termId.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "VocabACResult [term=" + term + ", isSynonym=" + isSynonym
				+ ", originalTerm=" + originalTerm + ", termId=" + termId
				+ ", rootVocab=" + rootVocab + ", displayVocab=" + displayVocab
				+ ", markerCount=" + markerCount + ", hasExpressionResults="
				+ hasExpressionResults + "]";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj!=null && obj instanceof VocabACResult)
		{
			return this.hashCode()==obj.hashCode();
		}
		return false;
	}

}
