package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.Marker;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.hunter.SolrMarkerIDHunter;
import org.jax.mgi.fewi.hunter.SolrMarkerKeyHunter;
import org.jax.mgi.fewi.hunter.SolrMarkerSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
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
    
    @Autowired
    private SolrMarkerIDHunter mrkIDHunter;
    
    @Autowired
    private SolrMarkerSummaryHunter markerSummaryHunter;

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
        List<Marker> mrkList = mrkGatherer.get( Marker.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(mrkList);

        return searchResults;
    }
    // convenience wrapper
    public SearchResults<Marker> getMarkerByID(String id) {
        SearchParams searchParams = new SearchParams();
        searchParams.setFilter(new Filter(SearchConstants.MRK_ID,id,Filter.OP_EQUAL));
        return getMarkerByID(searchParams);
    }

    /*--------------------------------------------*/
    /* Retrieval of a marker, for a given db key
    /*--------------------------------------------*/

    public SearchResults<Marker> getMarkerByKey(String dbKey) {

        logger.debug("->getMarkerByKey()");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // gather objects, add them to the results
        Marker marker = mrkGatherer.get( Marker.class, dbKey );
        searchResults.addResultObjects(marker);

        return searchResults;
    }


    /*---------------------------------*/
    /* Retrieval of multiple markers
    /*---------------------------------*/

    public SearchResults<Marker> getMarkers(SearchParams searchParams) {

        logger.debug("->getMarkers");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        markerSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        List<Marker> markerList
          = mrkGatherer.get( Marker.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(markerList);

        return searchResults;
    }
    
    /*
     * Retrieval of just marker IDs
     */
    public SearchResults<String> getMarkerIDs(SearchParams searchParams)
    {
    	logger.debug("->getMarkerIDs()");

        // result object to be returned
        SearchResults<String> searchResults = new SearchResults<String>();

        mrkIDHunter.hunt(searchParams,searchResults);

        return searchResults;
    }
    
}
