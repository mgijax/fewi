package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Image;

import org.jax.mgi.fewi.hunter.SolrAlleleKeyHunter;
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
	private HibernateObjectGatherer<Allele> alleleGatherer;


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


}
