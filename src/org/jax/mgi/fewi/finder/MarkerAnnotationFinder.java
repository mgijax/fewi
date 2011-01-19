package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.MarkerAnnotation;

import org.jax.mgi.fewi.hunter.SolrMarkerAnnotationSummaryHunter;
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
public class MarkerAnnotationFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(MarkerAnnotationFinder.class);

    //@Autowired
    //private FooKeyHunter fooKeyHunter;

    @Autowired
    private SolrMarkerAnnotationSummaryHunter markerAnnotationSummaryHunter;

    @Autowired
    private HibernateObjectGatherer<MarkerAnnotation> markerAnnotationGatherer;


/*    -----------------------------------------
     Retrieval of a foo, for a given ID
    /*-----------------------------------------

    public SearchResults<Marker> getFooByID(SearchParams searchParams) {

        logger.debug("->getFooByID()");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        fooKeyHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        fooGatherer.setType(Marker.class);
        List<Marker> fooList
          = fooGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(fooList);

        return searchResults;
    }*/


	/*--------------------------------------------*/
	/* Retrieval of a foo, for a given db key
	/*--------------------------------------------*/

    public SearchResults<MarkerAnnotation> getMarkerAnnotationByKey(String dbKey) {

        logger.debug("->getMarkerAnnotationByKey()");

        // result object to be returned
        SearchResults<MarkerAnnotation> searchResults = new SearchResults<MarkerAnnotation>();

        // gather objects, add them to the results
        markerAnnotationGatherer.setType(MarkerAnnotation.class);
        MarkerAnnotation foo = markerAnnotationGatherer.get( dbKey );
        searchResults.addResultObjects(foo);

        return searchResults;
    }


    /*---------------------------------*/
    /* Retrieval of multiple foos
    /*---------------------------------*/

    public SearchResults<MarkerAnnotation> getMarkerAnnotations(SearchParams searchParams) {

        logger.debug("->getMarkerAnnotations");

        // result object to be returned
        SearchResults<MarkerAnnotation> searchResults = new SearchResults<MarkerAnnotation>();

        // ask the hunter to identify which objects to return
        markerAnnotationSummaryHunter.hunt(searchParams, searchResults);
        logger.info("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        markerAnnotationGatherer.setType(MarkerAnnotation.class);
        List<MarkerAnnotation> annotList
          = markerAnnotationGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(annotList);
        
        logger.info("Finder retrieved this many objects: " + annotList.size());

        return searchResults;
    }



}
