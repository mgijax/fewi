package org.jax.mgi.fewi.searchUtil;

public class Paginator {
	
	private Integer startIndex = 1;
	private Integer results = 25;
	
	public Integer getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}
	public Integer getResults() {
		return results;
	}
	public void setResults(Integer results) {
		this.results = results;
	}
	@Override
	public String toString() {
		return "Paginator [results=" + results + ", startIndex=" + startIndex
				+ "]";
	}
}
