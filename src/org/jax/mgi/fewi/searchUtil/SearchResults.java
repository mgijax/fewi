package org.jax.mgi.fewi.searchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.searchUtil.entities.UniqueableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;

/**
 * Search Results
 */
public class SearchResults<T> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
        return resultFacets;
    }

	public List<String> getSortedResultFacets() {
		List<String> sortedFacets = new ArrayList<String>(resultFacets);
		Collections.sort (sortedFacets, new SmartAlphaComparator());
        return sortedFacets;
    }

    /**
	 * Set facets
	 */
    public void setResultFacets(List<String> resultFacets) {
        this.resultFacets = resultFacets;
    }

    /**
     * uniques the list of result objects, and preserves order
     * result object must implement UniqueableObject interface
     */
    public void uniqueifyResultObjects()
    {
    	// No reason to unique a list of less than 2 items.
    	// also, objects need to implement the correct interface
    	if(this.resultObjects.size() > 1)
    	{
    		if(this.resultObjects.get(0) instanceof UniqueableObject)
    		{
    			HashSet<Object> uniqueKeys = new HashSet<Object>();
    			List<T> uniqueResultObjects = new ArrayList<T>();
    			for(T result : this.resultObjects)
    			{
    				Object uniqueKey = ((UniqueableObject) result).getUniqueKey();
    				if(!uniqueKeys.contains(uniqueKey))
    				{
    					uniqueResultObjects.add(result);
    					uniqueKeys.add(uniqueKey);
    				}
    			}
    			this.setResultObjects(uniqueResultObjects);
    		}
    		else
    		{
    			logger.warn("Result Object ["+this.resultObjects.get(0).getClass()+"] does not implement "+
    					UniqueableObject.class+" interface. Cannot uniqueify original result set.");
    		}
    	}
    }

    /*
     * takes an existing SR object and copies over every non-T field (I.e. excludes resultObjects)
     * 	unless clazz != null, then it will try to cast every object in resultObjects to clazz
     */
    public void cloneFrom(SearchResults<?> existingSr)
    {
    	cloneFrom(existingSr,null);
    }
    @SuppressWarnings("unchecked")
	public void cloneFrom(SearchResults<?> existingSr,Class<T> clazz)
    {
    	this.setTotalCount(existingSr.getTotalCount());
    	this.setResultKeys(existingSr.getResultKeys());
    	this.setResultStrings(existingSr.getResultStrings());
    	this.setMetaMapping(existingSr.getMetaMapping());
    	this.setResultFacets(existingSr.getResultFacets());
    	this.setResultSetMeta(existingSr.getResultSetMeta());
    	if(clazz != null)
    	{
    		List<T> newResultObjects = new ArrayList<T>();
    		for(Object obj : existingSr.getResultObjects())
    		{
    			newResultObjects.add((T) obj);
    		}
    		this.setResultObjects(newResultObjects);
    	}
    }
}

class FacetSorter implements Comparator<String> {
    public FacetSorter() {}

    public int compare (String a, String b) {
	String a1 = a.toLowerCase();
	String b1 = b.toLowerCase();

	return a1.compareTo(b1);
    }

    public boolean equals (Object c) {
	if (this == c) { return true; }
	return false;
    }
}
