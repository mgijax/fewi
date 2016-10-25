package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jax.mgi.fewi.hunter.SolrGxdHtExperimentHunter;
import org.jax.mgi.fewi.hunter.SolrGxdHtSampleHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtSample;
import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
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

		// first, we need to search the samples and get the set of matching experiment keys (plus
		// the count of matching samples for each)
		Map<String, Integer> experimentKeyMap = gxdHtSampleHunter.getExperimentMap(searchParams);
		logger.debug("  -> got map, size " + experimentKeyMap.size());
		
		List<String> experimentKeys = new ArrayList<String>();
		for (String key : experimentKeyMap.keySet()) {
			experimentKeys.add(key);
		}
		logger.debug("  -> compiled list, size " + experimentKeys.size());
		
		// Now compose a new set of SearchParams based on the matching experiment keys.
		SearchParams keySearchParams = new SearchParams();
		keySearchParams.setSorts(searchParams.getSorts());
		keySearchParams.setPaginator(searchParams.getPaginator());
		keySearchParams.setFilter(new Filter(GxdHtFields.EXPERIMENT_KEY, experimentKeys, Operator.OP_IN));
		
		logger.debug("  -> filter: " + keySearchParams.getFilter());
		
		// result object to be returned
		SearchResults<GxdHtExperiment> searchResults = new SearchResults<GxdHtExperiment>();

		// ask the hunter to gather the necessary experiment objects
		gxdHtExperimentHunter.hunt(keySearchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " experiments");
		
		// go through and update the count of matching samples for each
		for (GxdHtExperiment experiment : searchResults.getResultObjects()) {
			String experimentKey = experiment.getExperimentKey().toString();
			if (experimentKeyMap.containsKey(experimentKey)) {
				experiment.setMatchingSampleCount(experimentKeyMap.get(experimentKey));
			} else {
				experiment.setMatchingSampleCount(0);
			}
		}

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
	
	public Set<String> getMatchingSampleKeys(SearchParams searchParams) {
		logger.debug("->getMatchingSampleKeys");
		
		return gxdHtSampleHunter.getSampleKeys(searchParams);
	}
}
