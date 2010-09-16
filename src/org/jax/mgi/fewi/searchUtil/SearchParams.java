package org.jax.mgi.fewi.searchUtil;

import java.util.*;

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

	// pagination control;  first result to be returned
	protected int startIndex = 1;

	// pagination control;  size of resultset to be returned
	// negative number signifies no limit
	protected int pageSize = 25;



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
		return;
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
		return;
	}

	/**
	 * Set the ordered sort list
	 */
	public void setSorts(List<Sort> sorts) {
		this.sorts = sorts;
		return;
	}

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
