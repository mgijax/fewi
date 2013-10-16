package org.jax.mgi.fewi.finder;


import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;

/**
 * A special finder that can query the DiseasePortal data in batches for generating reports
 * 
 * Note: It is not recommended to iterate over different types of objects (i.e. markers, diseases) with the same DiseasePortalBatchFinder.
 * 			The currentOffset is a shared value and would have to be reset manually.
 * 
 * @author kstone
 *
 */
public class DiseasePortalBatchFinder 
{
	private DiseasePortalFinder finder;
	// no search params means "get me everything"
	public SearchParams searchParams = null;
	// default values
	public Integer batchSize = 10000;
	public Integer currentOffset = 0;
	private Integer totalCount = null;
	
	public DiseasePortalBatchFinder(DiseasePortalFinder finder,SearchParams searchParams)
	{
		this.finder=finder;
		this.searchParams=searchParams;
	}
	
	public DiseasePortalBatchFinder(DiseasePortalFinder finder,SearchParams searchParams,Integer batchSize)
	{
		this.finder=finder;
		this.searchParams=searchParams;
		this.batchSize=batchSize;
	}
	
	
	/*
	 * Functions for iterating Markers
	 */
	public boolean hasNextMarkers()
	{
		if(totalCount != null && totalCount <= currentOffset) return false;
		return true;
	}
	
	public SearchResults<SolrDiseasePortalMarker> getNextMarkers()
	{
		if(currentOffset.equals(0)) totalCount = null;
		Paginator p = new Paginator();
		p.setResults(batchSize);
		p.setStartIndex(currentOffset);
		searchParams.setPaginator(p);
		SearchResults<SolrDiseasePortalMarker> results = finder.getMarkers(searchParams);
		currentOffset += batchSize;
		totalCount = results.getTotalCount();
		return results;
	}
	
	/*
	 *  Functions for iterating Diseases
	 */
	public boolean hasNextDiseases()
	{
		if(totalCount != null && totalCount <= currentOffset) return false;
		return true;
	}
	public SearchResults<SolrVocTerm> getNextDiseases()
	{
		if(currentOffset.equals(0)) totalCount = null;
		Paginator p = new Paginator();
		p.setResults(batchSize);
		p.setStartIndex(currentOffset);
		searchParams.setPaginator(p);
		SearchResults<SolrVocTerm> results = finder.getDiseases(searchParams);
		currentOffset += batchSize;
		totalCount = results.getTotalCount();
		return results;
	}
	
}
