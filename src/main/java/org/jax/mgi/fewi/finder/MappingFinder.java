package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.MappingExperiment;
import org.jax.mgi.fewi.hunter.SolrMappingHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.jsonmodel.MappingExperimentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

//import mgi.frontend.datamodel.MappingExperiment;

/*
 * This finder is responsible for finding genetic mapping experiments for markers and references.
 */
@Repository
public class MappingFinder {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(MappingFinder.class);

	@Autowired
	private SolrMappingHunter mappingHunter;

    @Autowired
    private HibernateObjectGatherer<MappingExperiment> experimentGatherer;

	//--- public methods ---//

	/* return all MappingExperimentSummary (from Solr) objects matching the given search parameters
	 */
	public SearchResults<MappingExperimentSummary> getExperiments(SearchParams searchParams) {
		logger.debug("->getExperiments");

		// result object to be returned
		SearchResults<MappingExperimentSummary> searchResults = new SearchResults<MappingExperimentSummary>();

		// ask the hunter to identify which objects to return
		mappingHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " mapping experiments");

		return searchResults;
	}
	
	/* return all MappingExperiment objects (from database via Hibernate) matching the given ID
	 */
    public List<MappingExperiment> getExperimentByID(String experimentID)
    {
        return getExperimentByID(Arrays.asList(experimentID));
    }

	/* return all MappingExperiment objects (from database via Hibernate) matching at least one of the given list of IDs
	 */
    public List<MappingExperiment> getExperimentByID(List<String> experimentID)
    {
        return experimentGatherer.get( MappingExperiment.class, experimentID, "primaryID" );
    }

    /* return all MappingExperiment objects matching the given database (experiment) key (should be 0-1)
     */
    public List<MappingExperiment> getExperimentByKey(String dbKey) {

        logger.debug("->getExperimentByKey()");

        // gather objects, add them to the results
        MappingExperiment experiment = experimentGatherer.get( MappingExperiment.class, dbKey );

        List<MappingExperiment> results = new ArrayList<MappingExperiment>();
        if (experiment != null) {
        	results.add(experiment);
        }
        return results;
    }
}
