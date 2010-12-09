package org.jax.mgi.fewi.finder;

import java.util.*;

/*-------------------------------*/
/* to be changed for each Finder */
/*-------------------------------*/

import mgi.frontend.datamodel.Marker;
import org.jax.mgi.fewi.hunter.SolrMarkerKeyHunter;
//import org.jax.mgi.fewi.hunter.MarkerSummaryHunter;

/*----------------------------------------*/
/* standard classes, used for all Finders */
/*----------------------------------------*/

// fewi
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;

// external libs
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding marker(s)
 */

@Repository
public class MarkerFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(MarkerFinder.class);

    @Autowired
    private SolrMarkerKeyHunter mrkKeyHunter;

//    @Autowired
//    private SolrMarkerSummaryHunter mrkSummaryHunter;

    @Autowired
    private HibernateObjectGatherer<Marker> mrkGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a marker, for a given ID
    /*-----------------------------------------*/

    public SearchResults<Marker> getMarkerByID(SearchParams searchParams) {

        logger.debug("->getMarkerByID()");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        mrkKeyHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        mrkGatherer.setType(Marker.class);
        List<Marker> mrkList = mrkGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(mrkList);

        return searchResults;
    }


    /*---------------------------------*/
    /* Retrieval of multiple markers
    /*---------------------------------*/
/* Not needed yet;  commented out
    public SearchResults<Marker> getMarkers(SearchParams searchParams) {

        logger.debug("->getMarkers");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        mrkSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        mrkGatherer.setType(Marker.class);
        List<Marker> mrkList = mrkGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(mrkList);

        return searchResults;
    }
*/


}
