package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrGxdHtExperimentHunter;
import org.jax.mgi.fewi.hunter.SolrGxdHtSampleHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * This finder is responsible for finding high-throughput expression experiments
 */

@Repository
public class GxdHtFinder {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(GxdHtFinder.class);

	@Autowired
	private SolrGxdHtSampleHunter gxdHtSampleHunter;

	@Autowired
	private SolrGxdHtExperimentHunter gxdHtExperimentHunter;

	//--- public methods ---//
	public SearchResults<GxdHtExperiment> getExperiments(SearchParams searchParams) {
		logger.debug("->getExperiments");

		// result object to be returned
		SearchResults<GxdHtExperiment> searchResults = new SearchResults<GxdHtExperiment>();

		// ask the hunter to identify which objects to return
		gxdHtExperimentHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " experiments");

		return searchResults;
	}

	public SearchResults<GxdHtSample> getSamples(SearchParams searchParams) {
		logger.debug("->getSamples");

		// result object to be returned
		SearchResults<GxdHtSample> searchResults = new SearchResults<GxdHtSample>();

		// ask the hunter to identify which objects to return
		gxdHtSampleHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " samples");

		return searchResults;
	}
}
