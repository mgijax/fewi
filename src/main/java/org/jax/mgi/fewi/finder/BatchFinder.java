package org.jax.mgi.fewi.finder;

import java.util.List;

import org.jax.mgi.fe.datamodel.BatchMarkerId;
import org.jax.mgi.fewi.hunter.HibernateBatchSummaryHunter;
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
 * This finder is responsible for finding (s)
 */

@Repository
public class BatchFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(BatchFinder.class);

    @Autowired
    private HibernateBatchSummaryHunter batchMarkerHunter;


    /*---------------------------------*/
    /* Retrieval of multiple s
    /*---------------------------------*/

    public SearchResults<BatchMarkerId> getBatch(SearchParams searchParams) {

        logger.debug("->getBatch");

        // result object to be returned
        SearchResults<BatchMarkerId> searchResults = new SearchResults<BatchMarkerId>();

        // ask the hunter to identify which objects to return
        batchMarkerHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - " + searchResults.getResultObjects().size());

        return searchResults;
    }

    /*-------------------------------------------------*/
    /* Retrieve a distinct list of matching marker IDs */
    /*-------------------------------------------------*/
    public List<String> getMarkerIDs(SearchParams searchParams) {
	    logger.debug("in BatchFinder.getMarkerIDs()");
	    return batchMarkerHunter.getMarkerIDs(searchParams);
    }
}
