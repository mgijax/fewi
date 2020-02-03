package org.jax.mgi.fewi.searchUtil;

public class Paginator {

	private Integer startIndex = 0;
	private Integer results;
	private Integer resultsDefault = 25;

	// shortcut to query all results
	public static Paginator ALL_PAGES = new Paginator();
	static {
		ALL_PAGES.setResults(1000000000);
	}
	
	public Paginator() {}
	public Paginator(Integer results)
	{
		this.results=results;
	}
	
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

	/* make and return a copy of Paginator 'a', such that changes to the
	 * returned object will not affect 'a'.
	 */
	public static Paginator copy (Paginator a) {
		Paginator b = new Paginator();
		b.startIndex = a.startIndex;
		b.results = a.results;
		b.resultsDefault = a.resultsDefault;
		return b;
	}
}
