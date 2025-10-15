package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.ESAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMarker;

/**
 * A special finder that can query the GXD data in batches for generating reports
 * 
 * Note: It is not recommended to iterate over different types of objects (i.e. markers, assays, results) with the same GxdBatchFinder.
 * 			The currentOffset is a shared value and would have to be reset manually.
 * 
 * @author kstone
 *
 */
public class GxdBatchFinder 
{
	private GxdFinder finder;
	// no search params means "get me everything"
	public SearchParams searchParams = null;
	// default values
	public Integer batchSize = 10000;
	public Integer currentOffset = 0;
	private Integer totalCount = null;
	
	public GxdBatchFinder(GxdFinder finder)
	{
		this.finder=finder;
		this.searchParams = new SearchParams();
		searchParams.setFilter( new Filter(SearchConstants.PRIMARY_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD));
	}
	
	public GxdBatchFinder(GxdFinder finder,SearchParams searchParams)
	{
		this.finder=finder;
		this.searchParams=searchParams;
	}
	
	public GxdBatchFinder(GxdFinder finder,SearchParams searchParams,Integer batchSize)
	{
		this.finder=finder;
		this.searchParams=searchParams;
		this.batchSize=batchSize;
	}
	
	/*
	 *  Functions for iterating Assay Results
	 */
	public boolean hasNextResults()
	{
		if(totalCount != null && totalCount <= currentOffset) return false;
		return true;
	}
	
	public SearchResults<ESAssayResult> getNextResults()
	{
		if(currentOffset.equals(0)) totalCount = null;
		Paginator p = new Paginator();
		p.setResults(batchSize);
		p.setStartIndex(currentOffset);
		searchParams.setPaginator(p);
		SearchResults<ESAssayResult> results = finder.searchAssayResults(searchParams);
		currentOffset += batchSize;
		totalCount = results.getTotalCount();
		return results;
	}
	
	/*
	 * Functions for iterating Markers
	 */
	public boolean hasNextMarkers()
	{
		if(totalCount != null && totalCount <= currentOffset) return false;
		return true;
	}
	
	public SearchResults<ESGxdMarker> getNextMarkers()
	{
		if(currentOffset.equals(0)) totalCount = null;
		Paginator p = new Paginator();
		p.setResults(batchSize);
		p.setStartIndex(currentOffset);
		searchParams.setPaginator(p);
		SearchResults<ESGxdMarker> results = finder.searchMarkers(searchParams);
		currentOffset += batchSize;
		totalCount = results.getTotalCount();
		return results;
	}
	
}
