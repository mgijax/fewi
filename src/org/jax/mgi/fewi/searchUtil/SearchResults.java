package org.jax.mgi.fewi.searchUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

/**
 * Search Results
 */
public class SearchResults<T> {


    //////////////////////////////////////////////////////////////////////////
    //  INTERNAL FIELDS
    //////////////////////////////////////////////////////////////////////////

	// Database keys identified as the results from a search
	protected List<String> resultKeys = new ArrayList<String>();

	// Scores for the matched documents
/*    protected List<String> resultScores = new ArrayList<String>();*/

	// Database keys identified as the results from a search
	protected List<String> resultStrings = new ArrayList<String>();

	// Database keys identified as the results from a search
    protected List<String> resultFacets = new ArrayList<String>();

	// Result Objects of a given search
	protected List<T> resultObjects = new ArrayList<T>();
	
	// MetaData mapping
	protected Map<String, MetaData> metaMapping = new HashMap <String, MetaData>();

	// Metadata for the result set as a whole
	protected ResultSetMetaData resultSetMeta = new ResultSetMetaData();
	
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
	 * Get a mapping of object key -> MetaData
	 * @return
	 */
	
	public Map<String, MetaData> getMetaMapping() {
        return metaMapping;
    }

	/**
	 * set the MetaData mapping list
	 * @param metaMapping
	 */
	
    public void setMetaMapping(Map<String, MetaData> metaMapping) {
        this.metaMapping = metaMapping;
    }

    /**
	 * Get the list of scores for the documents returned
	 */
/*	public List<String> getResultScores() {
        return resultScores;
    }*/

	/**
	 * Set the list of scores for the documents to be returned.
	 */
/*    public void setResultScores(List<String> resultScores) {
        this.resultScores = resultScores;
    }*/

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


	/**
	 * Add a result object
	 */
	public void addResultObjects(T resultObject) {
		this.resultObjects.add(resultObject);
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

    public ResultSetMetaData getResultSetMeta() {
        return resultSetMeta;
    }

    public void setResultSetMeta(ResultSetMetaData resultSetMeta) {
        this.resultSetMeta = resultSetMeta;
    }

    /**
	 * Get facets
	 */
	public List<String> getResultFacets() {
	Collections.sort (resultFacets, new FacetSorter());
        return resultFacets;
    }

    /**
	 * Set facets
	 */
    public void setResultFacets(List<String> resultFacets) {
        this.resultFacets = resultFacets;
    }
}

class FacetSorter implements Comparator {
    public FacetSorter() {}

    public int compare (Object a, Object b) {
	String a1 = a.toString().toLowerCase();
	String b1 = b.toString().toLowerCase();

	return a1.compareTo(b1);
    }

    public boolean equals (Object c) {
	if (this == c) { return true; }
	return false;
    }
}
