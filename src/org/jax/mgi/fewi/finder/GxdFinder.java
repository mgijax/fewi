package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.hunter.SolrGxdDifferentialHunter;
import org.jax.mgi.fewi.hunter.SolrGxdResultHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * Centered around retrieving GXD data in various forms
 */
@Repository
public class GxdFinder
{
	private Logger logger = LoggerFactory.getLogger(AutocompleteFinder.class);

	@Autowired
	private SolrGxdResultHunter gxdResultHunter;
	
	@Autowired
	private SolrGxdDifferentialHunter gxdDifferentialHunter;

//    /*
//	 * Only returning keys to start
//	 */
//	public SearchResults<String> search(SearchParams params)
//	{
//		SearchResults<String> results = new SearchResults<String>();
//		gxdResultHunter.hunt(params, results);
//		return results;
//	}
//
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getMarkerCount(SearchParams params){
		SearchResults<SolrGxdMarker> results = new SearchResults<SolrGxdMarker>();
		gxdResultHunter.hunt(params, results, SearchConstants.MRK_KEY);
		logger.debug("gxd finder marker count ="+results.getTotalCount());
		return results.getTotalCount();
	}
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayCount(SearchParams params){
		SearchResults<SolrGxdAssay> results = new SearchResults<SolrGxdAssay>();
		gxdResultHunter.hunt(params, results, SearchConstants.GXD_ASSAY_KEY);
		logger.debug("gxd finder assay count ="+results.getTotalCount());
		return results.getTotalCount();
	}
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayResultCount(SearchParams params){
		SearchResults<SolrAssayResult> results = new SearchResults<SolrAssayResult>();
		gxdResultHunter.hunt(params, results);
		logger.debug("gxd finder assay result count ="+results.getTotalCount());
		return results.getTotalCount();
	}
	public Integer getImageCount(SearchParams params) {
		SearchResults<SolrGxdImage> results = new SearchResults<SolrGxdImage>();
		gxdResultHunter.joinHunt(params, results,"gxdImagePane");
		logger.debug("gxd finder image count ="+results.getTotalCount());
		return results.getTotalCount();
	}

	public SearchResults<SolrAssayResult> searchAssayResults(SearchParams params) {
		SearchResults<SolrAssayResult> results = new SearchResults<SolrAssayResult>();
		gxdResultHunter.hunt(params, results);
		return results;
	}

	public SearchResults<SolrGxdAssay> searchAssays(SearchParams params) {
		SearchResults<SolrGxdAssay> results = new SearchResults<SolrGxdAssay>();
		gxdResultHunter.hunt(params, results,SearchConstants.GXD_ASSAY_KEY);
		return results;
	}

	public SearchResults<SolrGxdMarker> searchMarkers(SearchParams params) {
		SearchResults<SolrGxdMarker> results = new SearchResults<SolrGxdMarker>();
		gxdResultHunter.hunt(params, results,SearchConstants.MRK_KEY);
		return results;
	}
	public SearchResults<SolrGxdImage> searchImages(SearchParams params) {
		SearchResults<SolrGxdImage> results = new SearchResults<SolrGxdImage>();
		gxdResultHunter.joinHunt(params, results,"gxdImagePane");
		return results;
	}
	
	public SearchResults<SolrGxdMarker> searchBatchMarkerIDs(SearchParams params) {
		SearchResults<SolrGxdMarker> results = new SearchResults<SolrGxdMarker>();
		gxdResultHunter.hunt(params, results,SearchConstants.MRK_ID);
		return results;
	}

	/*
	 *  Returns marker keys based on differential search
	 */
	public List<String> searchDifferential(SearchParams params)
	{
		params.setPageSize(100000);
		 // result object to be returned
        SearchResults<String> searchResults = new SearchResults<String>();
        // ask the hunter to identify which objects to return
        gxdDifferentialHunter.hunt(params, searchResults);
        return searchResults.getResultKeys();
	}
}
