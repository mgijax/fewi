package org.jax.mgi.fewi.searchUtil;

public class Paginator {

	private Integer startIndex = 0;
	private Integer results;
	private Integer resultsDefault = 25;


    // startIndex
	public Integer getStartIndex() {
        if (startIndex < 0){return 0;}
		return startIndex;
	}
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

    // results
	public Integer getResults() {
        if (results==null){return resultsDefault;}
		return results;
	}
	public void setResults(Integer results) {
		this.results = results;
	}
	public void setResultsDefault(Integer resultsDefault) {
		this.resultsDefault = resultsDefault;
	}


    // toString for debugging purposes
	@Override
	public String toString() {
		return "Paginator [results=" + results + ", startIndex=" + startIndex
				+ "]";
	}
}
