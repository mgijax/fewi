package org.jax.mgi.fewi.finder;

import mgi.frontend.datamodel.Accession;

import org.jax.mgi.fewi.hunter.HibernateAccessionSummaryHunter;
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
    private HibernateAccessionSummaryHunter<Accession> accessionSummaryHunter;

    /*---------------------------------------*/
    /* Retrieval of multiple accession objects
    /*---------------------------------------*/
    public SearchResults<Accession> getAccessions(SearchParams searchParams) {
        logger.debug("->getAccessions");

        // result object to be returned
        SearchResults<Accession> searchResults = new SearchResults<Accession>();

        // ask the hunter to identify which objects to return
        accessionSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - " 
        		+ searchResults.getResultKeys());
       
        return searchResults;
    }
}
