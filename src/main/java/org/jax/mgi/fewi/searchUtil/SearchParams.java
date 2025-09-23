package org.jax.mgi.fewi.searchUtil;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hunter.ESQuery;

/**
 * A SearchParams object represents Search parameters
 */
public class SearchParams {

    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	// filter for this search (may have nested filters, requiring recursion)
	protected Filter filter = new Filter();

	// ordered list of sorts
	protected List<Sort> sorts = new ArrayList<Sort>();

	// pagination bean
	private Paginator paginator = new Paginator();
	
	// boolean flags to enable meta packaging
	private boolean includeRowMeta = false;
	private boolean includeSetMeta = false;
	private boolean includeMetaScore = false;
	private boolean includeMetaHighlight = false;
	private boolean includeHighlightMarkup = true;
	private boolean includeGenerated = false;
	private boolean fetchKeysOnly = false;
	private boolean suppressLogs = false;
	private boolean returnFilterQuery = false;
	
	private ESQuery esQuery;
	private boolean esGroupCountOnly = false;
	
	public String toString() {
	    String s = "SearchParams [Filter: ";
	    s = s + filter.toString();
	    s = s + ", Sorts: " + sorts.toString();
	    s = s + ", Paginator: " + paginator;

	    if (includeRowMeta) { s = s + ", includeRowMeta"; }
	    if (includeSetMeta) { s = s + ", includeSetMeta"; }
	    if (includeMetaScore) { s = s + ", includeMetaScore"; }
	    if (includeMetaHighlight) { s = s + ", includeMetaHighlight"; }
	    if (includeHighlightMarkup) { s = s + ", includeHighlightMarkup"; }
	    if (includeGenerated) { s = s + ", includeGenerated"; }
	    if (fetchKeysOnly) { s = s + ", fetchKeysOnlyl"; }
	    if (suppressLogs) { s = s + ", suppressLogs"; }
	    if (returnFilterQuery) { s = s + ", returnFilterQuery"; }
	    s = s + "]";

	    return s;
	}

    //////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    //////////////////////////////////////////////////////////////////////////


	/**
	 * Get the filter object
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Set the filter object
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * Get the ordered sort list
	 */
	public List<Sort> getSorts() {
		return sorts;
	}

	/**
	 * Add a sort to the ordered sort list
	 */
	public void addSort(Sort sort) {
		sorts.add(sort);
	}

	/**
	 * Set the ordered sort list
	 */
	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
	}
	
	/**
	 * Set Paginator
	 */
	
	public Paginator getPaginator() {
		return this.paginator;
	}
	
	public void setPaginator(Paginator paginator){
		this.paginator = paginator;
	}

    public int getStartIndex() {
        return this.paginator.getStartIndex();
    }

    public void setStartIndex(int startIndex) {
        this.paginator.setStartIndex(startIndex);
    }

    public int getPageSize() {
        return this.paginator.getResults();
    }

    public void setPageSize(int pageSize) {
        this.paginator.setResults(pageSize);
    }

	public boolean includeRowMeta() {
		return includeRowMeta;
	}

	public void setIncludeRowMeta(boolean includeRowMeta) {
		this.includeRowMeta = includeRowMeta;
	}

	public boolean includeSetMeta() {
		return includeSetMeta;
	}

	public void setIncludeSetMeta(boolean includeSetMeta) {
		this.includeSetMeta = includeSetMeta;
	}

	public boolean includeMetaScore() {
		return includeMetaScore;
	}

	public void setIncludeMetaScore(boolean includeMetaScore) {
		this.includeMetaScore = includeMetaScore;
	}

	public boolean includeMetaHighlight() {
		return includeMetaHighlight;
	}

	public void setIncludeMetaHighlight(boolean includeMetaHighlight) {
		this.includeMetaHighlight = includeMetaHighlight;
	}

	public boolean includeGenerated () {
		return includeGenerated;
	}
	
	public void setIncludeGenerated(boolean includeGenerated) {
		this.includeGenerated = includeGenerated;
	}

	public boolean getFetchKeysOnly() {
		return fetchKeysOnly;
	}

	public void setFetchKeysOnly(boolean fetchKeysOnly) {
		this.fetchKeysOnly = fetchKeysOnly;
	}

	public boolean getSuppressLogs() {
		return suppressLogs;
	}

	public void setSuppressLogs(boolean suppressLogs) {
		this.suppressLogs = suppressLogs;
	}

	public boolean includeHighlightMarkup() {
		return includeHighlightMarkup;
	}
	public void setIncludeHighlightMarkup(boolean includeHighlightMarkup) {
		this.includeHighlightMarkup = includeHighlightMarkup;
	}

	public boolean getReturnFilterQuery() {
		return returnFilterQuery;
	}
	public void setReturnFilterQuery(boolean returnFilterQuery) {
		this.returnFilterQuery = returnFilterQuery;
	}

	/* make and return a copy of SearchParams 'a', such that any changes
	 * to the returned object will not affect 'a'.
	 */
	public static SearchParams copy (SearchParams a) {
		SearchParams b = new SearchParams();
		b.includeRowMeta = a.includeRowMeta;
		b.includeSetMeta = a.includeSetMeta;
		b.includeMetaScore = a.includeMetaScore;
		b.includeMetaHighlight = a.includeMetaHighlight;
		b.includeHighlightMarkup = a.includeHighlightMarkup;
		b.includeGenerated = a.includeGenerated;
		b.fetchKeysOnly = a.fetchKeysOnly;
		b.suppressLogs = a.suppressLogs;
		b.returnFilterQuery = a.returnFilterQuery;

		b.filter = Filter.copy(a.filter);
		b.paginator = Paginator.copy(a.paginator);
		for (Sort s : a.sorts) {
			b.sorts.add(Sort.copy(s));
		}
		return b;
	}

	public ESQuery getEsQuery() {
		return esQuery;
	}

	public void setEsQuery(ESQuery esQuery) {
		this.esQuery = esQuery;
	}

	public boolean isEsGroupCountOnly() {
		return esGroupCountOnly;
	}

	public void setEsGroupCountOnly(boolean esGroupCountOnly) {
		this.esGroupCountOnly = esGroupCountOnly;
	}


}
