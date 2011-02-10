package org.jax.mgi.fewi.finder;

import mgi.frontend.datamodel.BatchMarkerId;

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


    /*-----------------------------------------*/
    /* Retrieval of a , for a given ID
    /*-----------------------------------------*/

    public SearchResults<BatchMarkerId> getByID(SearchParams searchParams) {

        logger.debug("->getByID()");

        // result object to be returned
        SearchResults<BatchMarkerId> searchResults = new SearchResults<BatchMarkerId>();

        // ask the hunter to identify which objects to return
        batchMarkerHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
//        markerGatherer.setType(Marker.class);
//        List<Marker> markerList
//          = markerGatherer.get( searchResults.getResultKeys() );
//        searchResults.setResultObjects(markerList);

        return searchResults;
    }


	/*--------------------------------------------*/
	/* Retrieval of a , for a given db key
	/*--------------------------------------------*/

    public SearchResults<BatchMarkerId> getByKey(String dbKey) {

        logger.debug("->getByKey()");

        // result object to be returned
        SearchResults<BatchMarkerId> searchResults = new SearchResults<BatchMarkerId>();
        
        return searchResults;
    }


    /*---------------------------------*/
    /* Retrieval of multiple s
    /*---------------------------------*/

    public SearchResults<BatchMarkerId> getBatch(SearchParams searchParams) {

        logger.debug("->getBatch");

        // result object to be returned
        SearchResults<BatchMarkerId> searchResults = new SearchResults<BatchMarkerId>();

        // ask the hunter to identify which objects to return
        batchMarkerHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - " + searchResults.getResultKeys());

        return searchResults;
    }



}
