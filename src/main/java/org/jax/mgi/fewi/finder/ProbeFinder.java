package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.Probe;
import org.jax.mgi.fewi.hunter.SolrProbeHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.jsonmodel.MolecularProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * This finder is responsible for finding molecular probes
 */
@Repository
public class ProbeFinder {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(ProbeFinder.class);

	@Autowired
	private SolrProbeHunter probeHunter;

    @Autowired
    private HibernateObjectGatherer<Probe> probeGatherer;

	//--- public methods ---//

	/* return all MolecularProbe (from Solr) objects matching the given search parameters
	 */
	public SearchResults<MolecularProbe> getProbes(SearchParams searchParams) {
		logger.debug("->getProbes");

		// result object to be returned
		SearchResults<MolecularProbe> searchResults = new SearchResults<MolecularProbe>();

		// ask the hunter to identify which objects to return
		probeHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " probes");

		return searchResults;
	}
	
	/* return all Probe objects (from database via Hibernate) matching the given ID
	 */
    public List<Probe> getProbeByID(String probeID)
    {
        return getProbeByID(Arrays.asList(probeID));
    }

	/* return all Probe objects (from database via Hibernate) matching at least one of the given list of IDs
	 */
    public List<Probe> getProbeByID(List<String> probeID)
    {
        return probeGatherer.get( Probe.class, probeID, "primaryID" );
    }

    /* return all Probe objects matching the given database (probe) key (should be 0-1)
     */
    public List<Probe> getProbeByKey(String dbKey) {

        logger.debug("->getProbeByKey()");

        // gather objects, add them to the results
        Probe probe = probeGatherer.get( Probe.class, dbKey );

        List<Probe> results = new ArrayList<Probe>();
        if (probe != null) {
        	results.add(probe);
        }
        return results;
    }
}
