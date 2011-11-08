package org.jax.mgi.fewi.finder;

import java.util.*;

/*-------------------------------*/
/* to be changed for each Finder */
/*-------------------------------*/

import mgi.frontend.datamodel.Marker;
import org.jax.mgi.fewi.hunter.FooKeyHunter;
import org.jax.mgi.fewi.hunter.FooSummaryHunter;

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
 * This finder is responsible for finding foo(s)
 */

@Repository
public class GXDAssayFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(GXDAssayFinder.class);

    @Autowired
    private FooKeyHunter fooKeyHunter;

    @Autowired
    private FooSummaryHunter fooSummaryHunter;

    @Autowired
    private HibernateObjectGatherer<Marker> fooGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a foo, for a given ID
    /*-----------------------------------------*/

    public SearchResults<Marker> getFooByID(SearchParams searchParams) {

        logger.debug("->getFooByID()");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        fooKeyHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        List<Marker> fooList
          = fooGatherer.get( Marker.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(fooList);

        return searchResults;
    }


	/*--------------------------------------------*/
	/* Retrieval of a foo, for a given db key
	/*--------------------------------------------*/

    public SearchResults<Marker> getFooByKey(String dbKey) {

        logger.debug("->getFooByKey()");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // gather objects, add them to the results
        Marker foo = fooGatherer.get( Marker.class, dbKey );
        searchResults.addResultObjects(foo);

        return searchResults;
    }


    /*---------------------------------*/
    /* Retrieval of multiple foos
    /*---------------------------------*/

    public SearchResults<Marker> getFoos(SearchParams searchParams) {

        logger.debug("->getFoos");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        fooSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        List<Marker> fooList
          = fooGatherer.get( Marker.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(fooList);

        return searchResults;
    }



}
