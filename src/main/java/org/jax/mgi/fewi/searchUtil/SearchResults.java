package org.jax.mgi.fewi.searchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.searchUtil.entities.UniqueableObject;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

@Data
public class SearchResults<T> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// list of facet values 
	protected List<String> resultFacets = new ArrayList<String>();
        //
        protected List<long[]> histogram = new ArrayList<long[]>(); // each entry is [startCoord, endCoord, count]
        //
	// MetaData mapping
	protected Map<String, MetaData> metaMapping = new HashMap <String, MetaData>();
	// Database keys identified as the results from a search
	protected List<String> resultKeys = new ArrayList<String>();
	// Database keys identified as the results from a search
	protected List<String> resultStrings = new ArrayList<String>();
	// Result Objects of a given search
	protected List<T> resultObjects = new ArrayList<T>();
	// Metadata for the result set as a whole
	protected ResultSetMetaData resultSetMeta = new ResultSetMetaData();

	// Total number of possible results
	protected int totalCount = -1;
	// String to store the solr query that was run
	protected String filterQuery = "";

	public void addResultObjects(T resultObject) {
		this.resultObjects.add(resultObject);
	}
	public List<String> getSortedResultFacets() {
		List<String> sortedFacets = new ArrayList<String>(resultFacets);
		Collections.sort (sortedFacets, new SmartAlphaComparator());
		return sortedFacets;
	}

	/**
	 * uniques the list of result objects, and preserves order
	 * result object must implement UniqueableObject interface
	 */
	public void uniqueifyResultObjects() {
		// No reason to unique a list of less than 2 items.
		// also, objects need to implement the correct interface
		if(this.resultObjects.size() > 1) {
			if(this.resultObjects.get(0) instanceof UniqueableObject) {
				HashSet<Object> uniqueKeys = new HashSet<Object>();
				List<T> uniqueResultObjects = new ArrayList<T>();
				for(T result : resultObjects) {
					Object uniqueKey = ((UniqueableObject) result).getUniqueKey();
					if(!uniqueKeys.contains(uniqueKey)) {
						uniqueResultObjects.add(result);
						uniqueKeys.add(uniqueKey);
					}
				}
				setResultObjects(uniqueResultObjects);
			} else {
				logger.warn("Result Object [" + resultObjects.get(0).getClass() + "] does not implement " +
						UniqueableObject.class + " interface. Cannot uniqueify original result set.");
			}
		}
	}

	/*
	 * takes an existing SR object and copies over every non-T field (I.e. excludes resultObjects)
	 * 	unless clazz != null, then it will try to cast every object in resultObjects to clazz
	 */
	public void cloneFrom(SearchResults<?> existingSr) {
		cloneFrom(existingSr,null);
	}
	
	@SuppressWarnings("unchecked")
	public void cloneFrom(SearchResults<?> existingSr,Class<T> clazz) {
		this.setTotalCount(existingSr.getTotalCount());
		this.setResultKeys(existingSr.getResultKeys());
		this.setResultStrings(existingSr.getResultStrings());
		this.setMetaMapping(existingSr.getMetaMapping());
		this.setResultFacets(existingSr.getResultFacets());
		this.setResultSetMeta(existingSr.getResultSetMeta());
		this.setFilterQuery(existingSr.getFilterQuery());
		if(clazz != null) {
			List<T> newResultObjects = new ArrayList<T>();
			for(Object obj : existingSr.getResultObjects()) {
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
