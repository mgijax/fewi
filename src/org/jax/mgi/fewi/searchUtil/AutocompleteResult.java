package org.jax.mgi.fewi.searchUtil;

public class AutocompleteResult {
	private String term;
	private String synonym;
	
	public AutocompleteResult() {}

	public AutocompleteResult(String term) {
		this.term = term;
	}

	public AutocompleteResult(String term, String synonym) {
		this.term = term;
		this.synonym = synonym;
	}

	public String getSynonym() {
		return synonym;
	}

	public String getTerm() {
		return term;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
