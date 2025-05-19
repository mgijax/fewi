package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jax.mgi.fewi.forms.GxdHtQueryForm;
import org.jax.mgi.fewi.hunter.SolrGxdHtExperimentHunter;
import org.jax.mgi.fewi.hunter.SolrGxdHtExperimentStudyTypeFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdHtExperimentVariableFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdHtExperimentMethodFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdHtSampleHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
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

	@Autowired
	private SolrGxdHtExperimentStudyTypeFacetHunter studyTypeFacetHunter;

	@Autowired
	private SolrGxdHtExperimentVariableFacetHunter variableFacetHunter;

	@Autowired
	private SolrGxdHtExperimentMethodFacetHunter methodFacetHunter;

	//--- public methods ---//
	public SearchResults<GxdHtExperiment> getExperiments(SearchParams searchParams, GxdHtQueryForm query) {
		logger.debug("->getExperiments");

		Map<String, Integer> experimentKeyMap = this.getMatchingExperimentKeyMap(searchParams, query);
		List<String> experimentKeys = this.getMatchingExperimentKeys(experimentKeyMap);
		
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

	// convert an experimentKeyMap to just a list of keys
	private List<String> getMatchingExperimentKeys(Map<String, Integer> experimentKeyMap) {
		List<String> experimentKeys = new ArrayList<String>();
		for (String key : experimentKeyMap.keySet()) {
			experimentKeys.add(key);
		}
		logger.debug("  -> compiled list, size " + experimentKeys.size());
		return experimentKeys;
	}
	
	// get a mapping from experiment key to a count of matching samples
	private Map<String, Integer> getMatchingExperimentKeyMap(SearchParams searchParams, GxdHtQueryForm query) {
		// first, we need to search the samples and get the set of matching experiment keys (plus
		// the count of matching samples for each).  If any sample-specific fields were submitted,
		// we only want to consider relevant samples.
		
		if ((query.getAge() != null) || (query.getMutatedIn() != null) || (query.getSex() != null) ||
			(query.getStructure() != null) || (query.getTheilerStage() != null) || (query.getStrain() != null) ||
			(query.getVariableFilter() != null) || (query.getStudyTypeFilter() != null) ||
			(query.getStructureID() != null)) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(searchParams.getFilter());
				filters.add(new Filter(SearchConstants.GXDHT_RELEVANCY, "Yes", Filter.Operator.OP_EQUAL));
				searchParams.setFilter(Filter.and(filters));
		}
		
		Map<String, Integer> experimentKeyMap = gxdHtSampleHunter.getExperimentMap(searchParams);
		logger.debug("  -> got map, size " + experimentKeyMap.size());
		return experimentKeyMap;
	}
		
	public SearchResults<GxdHtSample> getSamples(SearchParams searchParams) {
		logger.debug("->getSamples");

		// result object to be returned
		SearchResults<GxdHtSample> searchResults = new SearchResults<GxdHtSample>();

		// enforce the default sort
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(Sort.asc(SortConstants.BY_DEFAULT));
		searchParams.setSorts(sorts);

		// ask the hunter to identify which objects to return
		gxdHtSampleHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " samples");

		return searchResults;
	}
	
	public Set<String> getMatchingSampleKeys(SearchParams searchParams) {
		logger.debug("->getMatchingSampleKeys");
		
		return gxdHtSampleHunter.getSampleKeys(searchParams);
	}

    /* ---------------
     * Facet functions
     * ---------------
     */
    public SearchResults<GxdHtExperiment> getStudyTypeFacet(SearchParams params, GxdHtQueryForm query) {
		Map<String, Integer> experimentKeyMap = this.getMatchingExperimentKeyMap(params, query);
		List<String> experimentKeys = this.getMatchingExperimentKeys(experimentKeyMap);

		SearchParams keySearchParams = new SearchParams();
		keySearchParams.setPaginator(new Paginator(1));
		keySearchParams.setFilter(new Filter(GxdHtFields.EXPERIMENT_KEY, experimentKeys, Operator.OP_IN));

		SearchResults<GxdHtExperiment> results = new SearchResults<GxdHtExperiment>();
		studyTypeFacetHunter.hunt(keySearchParams, results);
		return results;
	}

    public SearchResults<GxdHtExperiment> getVariableFacet(SearchParams params, GxdHtQueryForm query) {
		Map<String, Integer> experimentKeyMap = this.getMatchingExperimentKeyMap(params, query);
		List<String> experimentKeys = this.getMatchingExperimentKeys(experimentKeyMap);

		SearchParams keySearchParams = new SearchParams();
		keySearchParams.setPaginator(new Paginator(1));
		keySearchParams.setFilter(new Filter(GxdHtFields.EXPERIMENT_KEY, experimentKeys, Operator.OP_IN));

		SearchResults<GxdHtExperiment> results = new SearchResults<GxdHtExperiment>();
		variableFacetHunter.hunt(keySearchParams, results);
		return results;
	}

    public SearchResults<GxdHtExperiment> getMethodFacet(SearchParams params, GxdHtQueryForm query) {
		Map<String, Integer> experimentKeyMap = this.getMatchingExperimentKeyMap(params, query);
		List<String> experimentKeys = this.getMatchingExperimentKeys(experimentKeyMap);

		SearchParams keySearchParams = new SearchParams();
		keySearchParams.setPaginator(new Paginator(1));
		keySearchParams.setFilter(new Filter(GxdHtFields.EXPERIMENT_KEY, experimentKeys, Operator.OP_IN));

		SearchResults<GxdHtExperiment> results = new SearchResults<GxdHtExperiment>();
		methodFacetHunter.hunt(keySearchParams, results);
		return results;
	}
}
