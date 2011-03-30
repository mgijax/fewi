package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.Accession;

import org.jax.mgi.fewi.hunter.FooKeyHunter;
import org.jax.mgi.fewi.hunter.HibernateAccessionSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding foo(s)
 */

@Repository
public class AccessionFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(AccessionFinder.class);

    @Autowired
    private FooKeyHunter fooKeyHunter;

    @Autowired
    private HibernateAccessionSummaryHunter fooSummaryHunter;

    @Autowired
    private HibernateObjectGatherer<Accession> accessionGatherer;

    /*---------------------------------*/
    /* Retrieval of multiple foos
    /*---------------------------------*/

    public SearchResults<Accession> getAccessions(SearchParams searchParams) {

        logger.debug("->getFoos");

        // result object to be returned
        SearchResults<Accession> searchResults = new SearchResults<Accession>();

        // ask the hunter to identify which objects to return
        fooSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
/*        accessionGatherer.setType(Accession.class);
        List<Accession> fooList
          = accessionGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(fooList);*/

        logger.debug("Got past the gatherer");
        
        return searchResults;
    }



}
