package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jax.mgi.fewi.hunter.SolrProbeHunter;
import org.jax.mgi.fewi.hunter.SolrStrainHunter;
import org.jax.mgi.fewi.hunter.SolrStrainTypeFacetHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.jsonmodel.MolecularProbe;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mgi.frontend.datamodel.Probe;
import mgi.frontend.datamodel.Strain;

/*
 * This finder is responsible for finding mouse strains
 */
@Repository
public class StrainFinder {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(StrainFinder.class);

	@Autowired
	private SolrStrainHunter strainHunter;

	@Autowired
	private SolrStrainTypeFacetHunter strainTypeFacetHunter;

    @Autowired
    private HibernateObjectGatherer<Strain> strainGatherer;

	//--- public methods ---//

	/* return all SimpleStrain (from Solr) objects matching the given search parameters
	 */
	public SearchResults<SimpleStrain> getStrains(SearchParams searchParams) {
		logger.debug("->getStrains");

		// result object to be returned
		SearchResults<SimpleStrain> searchResults = new SearchResults<SimpleStrain>();

		// ask the hunter to identify which objects to return
		strainHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " strains");

		return searchResults;
	}
	
	/* return all Strain objects (from database via Hibernate) matching the given ID
	 */
    public List<Strain> getStrainByID(String strainID)
    {
        return getStrainByID(Arrays.asList(strainID));
    }

	/* return all Strain objects (from database via Hibernate) matching at least one of the given list of IDs
	 */
    public List<Strain> getStrainByID(List<String> strainID)
    {
        return strainGatherer.get( Strain.class, strainID, "primaryID" );
    }

    /* return all Strain objects matching the given database (strain) key (should be 0-1)
     */
    public List<Strain> getStrainByKey(String dbKey) {

        logger.debug("->getStrainByKey()");

        // gather objects, add them to the results
        Strain strain = strainGatherer.get( Strain.class, dbKey );

        List<Strain> results = new ArrayList<Strain>();
        if (strain != null) {
        	results.add(strain);
        }
        return results;
    }

    /* ---------------
     * Facet functions
     * ---------------
     */
    public SearchResults<SimpleStrain> getStrainTypeFacet(SearchParams params) {
		SearchResults<SimpleStrain> results = new SearchResults<SimpleStrain>();
		strainTypeFacetHunter.hunt(params, results);
		return results;
	}
}
