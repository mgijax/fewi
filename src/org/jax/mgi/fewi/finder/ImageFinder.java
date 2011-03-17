package org.jax.mgi.fewi.finder;

import java.util.*;

/*-------------------------------*/
/* finder specific classes       */
/*-------------------------------*/

import mgi.frontend.datamodel.Image;
import org.jax.mgi.fewi.hunter.SolrImageKeyHunter;

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
 * This finder is responsible for finding image(s)
 */

@Repository
public class ImageFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(ImageFinder.class);

    @Autowired
    private SolrImageKeyHunter imageKeyHunter;

    @Autowired
    private HibernateObjectGatherer<Image> imageGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a image, for a given ID
    /*-----------------------------------------*/

    public SearchResults<Image> getImageByID(SearchParams searchParams) {

        logger.debug("->getImageByID()");

        // result object to be returned
        SearchResults<Image> searchResults = new SearchResults<Image>();

        // ask the hunter to identify which objects to return
        imageKeyHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        imageGatherer.setType(Image.class);
        List<Image> imageList
          = imageGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(imageList);

        return searchResults;
    }


	/*--------------------------------------------*/
	/* Retrieval of a foo, for a given db key
	/*--------------------------------------------*/

/*    public SearchResults<Marker> getFooByKey(String dbKey) {

        logger.debug("->getFooByKey()");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // gather objects, add them to the results
        fooGatherer.setType(Marker.class);
        Marker foo = fooGatherer.get( dbKey );
        searchResults.addResultObjects(foo);

        return searchResults;
    }
*/

    /*---------------------------------*/
    /* Retrieval of multiple foos
    /*---------------------------------*/
/*
    public SearchResults<Marker> getFoos(SearchParams searchParams) {

        logger.debug("->getFoos");

        // result object to be returned
        SearchResults<Marker> searchResults = new SearchResults<Marker>();

        // ask the hunter to identify which objects to return
        fooSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        fooGatherer.setType(Marker.class);
        List<Marker> fooList
          = fooGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(fooList);

        return searchResults;
    }
*/


}
