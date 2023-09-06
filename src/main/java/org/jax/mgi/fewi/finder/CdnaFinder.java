package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrCdnaHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.jsonmodel.Clone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * This finder is responsible for finding cDNA clones
 */

@Repository
public class CdnaFinder {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(CdnaFinder.class);

	@Autowired
	private SolrCdnaHunter cdnaHunter;

	//--- public methods ---//
	public SearchResults<Clone> getClones(SearchParams searchParams) {
		logger.debug("->getClones");

		// result object to be returned
		SearchResults<Clone> searchResults = new SearchResults<Clone>();

		// ask the hunter to gather the necessary experiment objects
		cdnaHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " cDNA clones");
		
		return searchResults;
	}
}
