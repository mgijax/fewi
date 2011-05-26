package org.jax.mgi.fewi.finder;

import java.util.*;

/*-------------------------------*/
/* finder specific classes       */
/*-------------------------------*/

import mgi.frontend.datamodel.Image;
import org.jax.mgi.fewi.hunter.SolrImageKeyHunter;
import org.jax.mgi.fewi.hunter.SolrAlleleImagesByAlleleHunter;
import org.jax.mgi.fewi.hunter.SolrAlleleImagesByMrkHunter;
import org.jax.mgi.fewi.hunter.SolrGxdImagesByMrkHunter;

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
  private SolrAlleleImagesByAlleleHunter alleleImagesHunter;

  @Autowired
  private SolrAlleleImagesByMrkHunter alleleImagesByMrkHunter;

  @Autowired
  private SolrGxdImagesByMrkHunter gxdImagesByMrkHunter;

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


  /*-----------------------------------------------*/
  /* Retrieval of pheno images, for a given allele
  /*-----------------------------------------------*/

  public SearchResults<Image> getPhenoImagesByAlleleKey(SearchParams searchParams) {

    logger.debug("->getPhenoImagesByAlleleKey()");

    // result object to be returned
    SearchResults<Image> searchResults = new SearchResults<Image>();

    // ask the hunter to identify which objects to return
    alleleImagesHunter.hunt(searchParams, searchResults);
    logger.debug("->hunter found these resultKeys - "
      + searchResults.getResultKeys());

    // gather objects identified by the hunter, add them to the results
    imageGatherer.setType(Image.class);
    List<Image> imageList
      = imageGatherer.get( searchResults.getResultKeys() );
    searchResults.setResultObjects(imageList);

    return searchResults;
  }



  /*-----------------------------------------------*/
  /* Retrieval of pheno images, for a given marker
  /*-----------------------------------------------*/

  public SearchResults<Image> getPhenoImagesByMarkerKey(SearchParams searchParams) {

    logger.debug("->getPhenoImagesByMarkerKey()");

    // result object to be returned
    SearchResults<Image> searchResults = new SearchResults<Image>();

    // ask the hunter to identify which objects to return
    alleleImagesByMrkHunter.hunt(searchParams, searchResults);
    logger.debug("->hunter found these resultKeys - "
      + searchResults.getResultKeys());

    // gather objects identified by the hunter, add them to the results
    imageGatherer.setType(Image.class);
    List<Image> imageList
      = imageGatherer.get( searchResults.getResultKeys() );
    searchResults.setResultObjects(imageList);

    return searchResults;
  }

  /*-----------------------------------------------*/
  /* Retrieval of GXD images, for a given marker
  /*-----------------------------------------------*/

  public SearchResults<Image> getGxdImagesByMarkerKey(SearchParams searchParams) {

    logger.debug("->getGxdImagesByMarkerKey()");

    // result object to be returned
    SearchResults<Image> searchResults = new SearchResults<Image>();

    // ask the hunter to identify which objects to return
    gxdImagesByMrkHunter.hunt(searchParams, searchResults);
    logger.debug("->hunter found these resultKeys - "
      + searchResults.getResultKeys());

    // gather objects identified by the hunter, add them to the results
    imageGatherer.setType(Image.class);
    List<Image> imageList
      = imageGatherer.get( searchResults.getResultKeys() );
    searchResults.setResultObjects(imageList);

    return searchResults;
  }


}
