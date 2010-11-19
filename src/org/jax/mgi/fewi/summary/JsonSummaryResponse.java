package org.jax.mgi.fewi.summary;

import java.util.*;

/**
 * JSON Response Object;  the purpose of this object is to be serialized
 * into a JSON response for YUI datatable interaction
 */
public class JsonSummaryResponse<T> {


    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	// Result Objects of a given search
	protected List<T> summaryRows = new ArrayList<T>();

	// Total number of possible results
	protected int totalCount = -1;



    //////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    //////////////////////////////////////////////////////////////////////////


	/**
	 * Get the summary rows
	 */
	public List<T> getSummaryRows() {
		return summaryRows;
	}

	/**
	 * Set the summary rows
	 */
	public void setSummaryRows(List<T> summaryRows) {
		this.summaryRows = summaryRows;
	}


    /**
	 * Get the total number of possible results (-1 means unspecified)
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * Set the total number of possible results
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
