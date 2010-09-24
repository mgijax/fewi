package org.jax.mgi.fewi.searchUtil;

import java.util.*;

/**
 * Search Results
 */
public class SearchResults<T> {


    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	// Database keys identified as the results from a search
	protected List<String> resultKeys = new ArrayList<String>();

	// Database keys identified as the results from a search
	protected List<String> resultStrings = new ArrayList<String>();
	
	// Database keys identified as the results from a search
    protected List<String> resultFacets = new ArrayList<String>();

	// Result Objects of a given search
	protected List<T> resultObjects = new ArrayList<T>();

	// Total number of possible results
	protected int totalCount = -1;



    //////////////////////////////////////////////////////////////////////////
    //  BASIC ACCESSORS
    //////////////////////////////////////////////////////////////////////////

	/**
	 * Get the keys of the results returned
	 */
	public List<String> getResultKeys() {
		return resultKeys;
	}

	/**
	 * Set the result keys of the search.
	 */
	public void setResultKeys(List<String> resultKeys) {
		this.resultKeys = resultKeys;
	}

	/**
	 * Get the keys of the results returned
	 */
	public List<String> getResultStrings() {
		return resultStrings;
	}

	/**
	 * Set the result keys of the search.
	 */
	public void setResultStrings(List<String> resultStrings) {
		this.resultStrings= resultStrings;
	}

	/**
	 * Get the result objects of the search
	 */
	public List<T> getResultObjects() {
		return resultObjects;
	}

	/**
	 * Set the result objects of the search.
	 */
	public void setResultObjects(List<T> resultObjects) {
		this.resultObjects = resultObjects;
	}

	public List<String> getResultFacets() {
        return resultFacets;
    }

    public void setResultFacets(List<String> resultFacets) {
        this.resultFacets = resultFacets;
    }

    /**
	 * Get the total number of possible results (-1 means unspecified)
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * Get the total number of possible results (-1 means unspecified)
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
