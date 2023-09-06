package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.hunter.SolrAlleleCollectionFacetHunter;
import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.AllelePhenoSummary;
import org.jax.mgi.fe.datamodel.AlleleRelatedMarker;
import org.jax.mgi.fewi.hunter.HibernateAlleleMutationInvolvesHunter;
import org.jax.mgi.fewi.hunter.SolrAlleleKeyHunter;
import org.jax.mgi.fewi.hunter.SolrAlleleMutationFacetHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AlleleFinder {

    private Logger logger = LoggerFactory.getLogger(AlleleFinder.class);

    @Autowired
    private SolrAlleleKeyHunter alleleHunter;
    
    @Autowired
    private SolrAlleleCollectionFacetHunter alleleCollectionFacetHunter;

    @Autowired
    private SolrAlleleMutationFacetHunter alleleMutationFacetHunter;

    @Autowired
    private HibernateObjectGatherer<Allele> alleleGatherer;

    @Autowired
    private HibernateObjectGatherer<AllelePhenoSummary> phenoGatherer;

    @Autowired
    private HibernateAlleleMutationInvolvesHunter mutationInvolvesHunter;

    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of an allele, for a given ID
    /////////////////////////////////////////////////////////////////////////

    public SearchResults<Allele> getAlleleByID(SearchParams searchParams) {

        logger.info("AlleleFinder.getAlleleByID()");

        // result object to be returned
        SearchResults<Allele> searchResults = new SearchResults<Allele>();

        // ask the hunter to identify which objects to return
        alleleHunter.hunt(searchParams, searchResults);

        // gather objects identified by the hunter, add them to the results
        List<Allele> alleleList = alleleGatherer.get( Allele.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(alleleList);

        return searchResults;
    }

    public List<AllelePhenoSummary> getPhenoSummaryByAlleleID(String alleleID)
    {
        return phenoGatherer.get(AllelePhenoSummary.class, Arrays.asList(alleleID), "primaryID");
    }
    public List<Allele> getAlleleByID(String alleleID)
    {
        return getAlleleByID(Arrays.asList(alleleID));
    }
    public List<Allele> getAlleleByID(List<String> alleleID)
    {
        return alleleGatherer.get( Allele.class, alleleID, "primaryID" );
    }


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of an allele, for a database key
    /////////////////////////////////////////////////////////////////////////

    public SearchResults<Allele> getAlleleByKey(String dbKey) {

        logger.debug("->getAlleleByKey()");

        // result object to be returned
        SearchResults<Allele> searchResults = new SearchResults<Allele>();

        // gather objects, add them to the results
        Allele allele = alleleGatherer.get( Allele.class, dbKey );
        searchResults.addResultObjects(allele);

        return searchResults;
    }

    /*
     * Facet functions
     */
    public SearchResults<String> getCollectionFacet(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		alleleCollectionFacetHunter.hunt(params, results);
		return results;
	}

    public SearchResults<String> getMutationFacet(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		alleleMutationFacetHunter.hunt(params, results);
		return results;
	}


    /** get 'mutation involves' markers for an allele
     */
    public SearchResults<AlleleRelatedMarker> getMutationInvolvesData(
	SearchParams params) {

	SearchResults<AlleleRelatedMarker> results =
	    new SearchResults<AlleleRelatedMarker>();
	mutationInvolvesHunter.hunt(params, results);
	return results;
    }
}
