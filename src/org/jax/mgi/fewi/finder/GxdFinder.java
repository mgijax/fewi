package org.jax.mgi.fewi.finder;

import java.util.List;

import org.jax.mgi.fewi.hunter.SolrGxdAssayTypeFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdDetectedFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdDifferentialHunter;
import org.jax.mgi.fewi.hunter.SolrGxdResultHunter;
import org.jax.mgi.fewi.hunter.SolrGxdSystemFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdTheilerStageFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdWildtypeFacetHunter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdEntity;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
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
	private final Logger logger = LoggerFactory.getLogger(AutocompleteFinder.class);

	@Autowired
	private SolrGxdResultHunter gxdResultHunter;
	
	@Autowired
	private SolrGxdDifferentialHunter gxdDifferentialHunter;

	@Autowired
	private SolrGxdSystemFacetHunter gxdSystemFacetHunter;
	
	@Autowired
	private SolrGxdAssayTypeFacetHunter gxdAssayTypeFacetHunter;
	
	@Autowired
	private SolrGxdDetectedFacetHunter gxdDetectedFacetHunter;
	
	@Autowired
	private SolrGxdTheilerStageFacetHunter gxdTheilerStageFacetHunter;
	
	@Autowired
	private SolrGxdWildtypeFacetHunter gxdWildtypeFacetHunter;
	
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
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.MRK_KEY);
		logger.debug("gxd finder marker count ="+results.getTotalCount());
		return results.getTotalCount();
	}
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayCount(SearchParams params){
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.GXD_ASSAY_KEY);
		logger.debug("gxd finder assay count ="+results.getTotalCount());
		return results.getTotalCount();
	}
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayResultCount(SearchParams params){
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results);
		logger.debug("gxd finder assay result count ="+results.getTotalCount());
		return results.getTotalCount();
	}
	public Integer getImageCount(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.joinHunt(params, results,"gxdImagePane");
		logger.debug("gxd finder image count ="+results.getTotalCount());
		return results.getTotalCount();
	}

	public SearchResults<SolrAssayResult> searchAssayResults(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results);
		
		SearchResults<SolrAssayResult> srAR = new SearchResults<SolrAssayResult>();
		srAR.cloneFrom(results,SolrAssayResult.class);
		return srAR;
	}

	public SearchResults<SolrGxdAssay> searchAssays(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results,SearchConstants.GXD_ASSAY_KEY);
		
		SearchResults<SolrGxdAssay> srGA = new SearchResults<SolrGxdAssay>();
		srGA.cloneFrom(results,SolrGxdAssay.class);
		return srGA;
	}

	public SearchResults<SolrGxdMarker> searchMarkers(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results,SearchConstants.MRK_KEY);
		
		SearchResults<SolrGxdMarker> srGM = new SearchResults<SolrGxdMarker>();
		srGM.cloneFrom(results,SolrGxdMarker.class);
		return srGM;
	}
	public SearchResults<SolrGxdImage> searchImages(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.joinHunt(params, results,"gxdImagePane");
		
		SearchResults<SolrGxdImage> srGI = new SearchResults<SolrGxdImage>();
		srGI.cloneFrom(results,SolrGxdImage.class);
		return srGI;
	}
	
	public SearchResults<SolrGxdMarker> searchBatchMarkerIDs(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results,SearchConstants.MRK_ID);
		
		SearchResults<SolrGxdMarker> srGM = new SearchResults<SolrGxdMarker>();
		srGM.cloneFrom(results,SolrGxdMarker.class);
		return srGM;	}

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

	public SearchResults<SolrString> getSystemFacet(SearchParams params) {
	    SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
	    gxdSystemFacetHunter.hunt(params, results);
	    SearchResults<SolrString> srSS = new SearchResults<SolrString>();
	    srSS.cloneFrom(results,SolrString.class);
	    return srSS;
	}

	public SearchResults<SolrString> getAssayTypeFacet(SearchParams params) {
	    SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
	    gxdAssayTypeFacetHunter.hunt(params, results);
	    SearchResults<SolrString> srSS = new SearchResults<SolrString>();
	    srSS.cloneFrom(results,SolrString.class);
	    return srSS;
	}

	public SearchResults<SolrString> getDetectedFacet(SearchParams params) {
	    SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
	    gxdDetectedFacetHunter.hunt(params, results);
	    SearchResults<SolrString> srSS = new SearchResults<SolrString>();
	    srSS.cloneFrom(results,SolrString.class);
	    return srSS;
	}

	public SearchResults<SolrString> getWildtypeFacet(SearchParams params) {
	    SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
	    gxdWildtypeFacetHunter.hunt(params, results);
	    SearchResults<SolrString> srSS = new SearchResults<SolrString>();
	    srSS.cloneFrom(results,SolrString.class);
	    return srSS;
	}

	public SearchResults<SolrString> getTheilerStageFacet(SearchParams params) {
	    SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
	    gxdTheilerStageFacetHunter.hunt(params, results);
	    SearchResults<SolrString> srSS = new SearchResults<SolrString>();
	    srSS.cloneFrom(results,SolrString.class);
	    return srSS;
	}
}

