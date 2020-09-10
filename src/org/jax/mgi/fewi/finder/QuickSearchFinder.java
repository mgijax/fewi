package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.hunter.SolrQSVocabResultHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

	//--- public methods ---//

	/* return all QSVocabResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSVocabResult> getResults(SearchParams searchParams) {
		logger.debug("->getResults");

		// result object to be returned
		SearchResults<QSVocabResult> searchResults = new SearchResults<QSVocabResult>();

		// ask the hunter to identify which objects to return
		qsVocabHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS vocab results");

		return searchResults;
	}
}
