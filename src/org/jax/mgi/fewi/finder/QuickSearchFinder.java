package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.hunter.SolrQSVocabResultHunter;
import org.jax.mgi.fewi.hunter.SolrQSFeatureResultFacetHunter;
import org.jax.mgi.fewi.hunter.SolrQSFeatureResultHunter;
import org.jax.mgi.fewi.hunter.SolrQSVocabResultFacetHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.summary.QSVocabResult;

/*
 * This finder is responsible for finding results for the quick search
 */
@Repository
public class QuickSearchFinder {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(QuickSearchFinder.class);

	@Autowired
	private SolrQSVocabResultHunter qsVocabHunter;

	@Autowired
	private SolrQSFeatureResultHunter qsFeatureHunter;

	@Autowired
	private SolrQSFeatureResultFacetHunter featureFacetHunter;
	
	@Autowired
	private SolrQSVocabResultFacetHunter vocabFacetHunter;
	
	//--- public methods ---//

	/* return all QSVocabResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSVocabResult> getVocabResults(SearchParams searchParams) {
		logger.debug("->getResults");

		// result object to be returned
		SearchResults<QSVocabResult> searchResults = new SearchResults<QSVocabResult>();

		// ask the hunter to identify which objects to return
		qsVocabHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS vocab results");

		return searchResults;
	}

	/* return all QSFeatureResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSFeatureResult> getFeatureResults(SearchParams searchParams) {
		logger.debug("->getFeatureResults");

		// result object to be returned
		SearchResults<QSFeatureResult> searchResults = new SearchResults<QSFeatureResult>();

		// ask the hunter to identify which objects to return
		qsFeatureHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS feature results");

		return searchResults;
	}
	
	/* get the specified facets for the matching feature results
	 */
	public List<String> getFeatureFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSFeatureResult> results = new SearchResults<QSFeatureResult>();
		featureFacetHunter.setFacetString(facetField);
		featureFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}

	/* get the specified facets for the matching vocab results
	 */
	public List<String> getVocabFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSVocabResult> results = new SearchResults<QSVocabResult>();
		vocabFacetHunter.setFacetString(facetField);
		vocabFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}

}
