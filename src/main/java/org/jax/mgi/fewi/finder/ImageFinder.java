package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jax.mgi.fe.datamodel.Image;
import org.jax.mgi.fewi.hunter.SolrAlleleImagesByAlleleHunter;
import org.jax.mgi.fewi.hunter.SolrAlleleImagesByMrkHunter;
import org.jax.mgi.fewi.hunter.SolrImageKeyHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.ImageSummaryRow;
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
    List<Image> imageList
      = imageGatherer.get( Image.class, searchResults.getResultKeys() );
    searchResults.setResultObjects(imageList);

    return searchResults;
  }







  /*-----------------------------------------*/
  /* Retrieval of a image, for a image db key
  /*-----------------------------------------*/

  public SearchResults<Image> getImageByKey(String dbKey) {

    logger.debug("->getImageByKey()");

    // result object to be returned
    SearchResults<Image> searchResults = new SearchResults<Image>();

    // gather objects identified by the hunter, add them to the results
    Image image = imageGatherer.get( Image.class, dbKey);

    searchResults.addResultObjects(image);

    return searchResults;
  }

  public Image getImageObjectByKey(int dbKey) {

    logger.debug("->getImageObjectByKey()");
    return imageGatherer.get( Image.class, "" + dbKey);
  }













  /*-----------------------------------------------*/
  /* Retrieval of pheno images, for a given allele
  /*-----------------------------------------------*/

  public SearchResults<ImageSummaryRow> getPhenoImagesByAlleleKey(SearchParams searchParams) {

    logger.debug("->getPhenoImagesByAlleleKey()");

    // result object to be returned
    SearchResults<ImageSummaryRow> searchResults = new SearchResults<ImageSummaryRow>();

    // ask the hunter to identify which objects to return
    alleleImagesHunter.hunt(searchParams, searchResults);
    logger.debug("->hunter found these resultKeys - "
      + searchResults.getResultKeys());

    // gather objects identified by the hunter
    List<Image> imageList = imageGatherer.get( Image.class, searchResults.getResultKeys() );

    // list of summary objects to be returned
    List<ImageSummaryRow> imageSummaryRowList
      = genSummaryRows (imageList);

    searchResults.setResultObjects(imageSummaryRowList);

    return searchResults;
  }



  /*-----------------------------------------------*/
  /* Retrieval of pheno images, for a given marker
  /*-----------------------------------------------*/

  public SearchResults<ImageSummaryRow> getPhenoImagesByMarkerKey(SearchParams searchParams) {

    logger.debug("->getPhenoImagesByMarkerKey()");

    // result object to be returned
    SearchResults<ImageSummaryRow> searchResults
      = new SearchResults<ImageSummaryRow>();

    // ask the hunter to identify which objects to return
    alleleImagesByMrkHunter.hunt(searchParams, searchResults);
    logger.debug("->hunter found these resultKeys - "
      + searchResults.getResultKeys());

    // gather objects identified by the hunter
    List<Image> imageList
      = imageGatherer.get( Image.class, searchResults.getResultKeys() );

    // list of summary objects to be returned
    List<ImageSummaryRow> imageSummaryRowList
      = genSummaryRows (imageList);

    searchResults.setResultObjects(imageSummaryRowList);

    return searchResults;
  }

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    // generate summary rows for a list of images
    private List<ImageSummaryRow> genSummaryRows (List<Image> imageList) {

        // list of summary objects to be returned
        List<ImageSummaryRow> imageSummaryRowList
          = new ArrayList<ImageSummaryRow>();

        // generate summary rows
        Image thisImage;
        Image thisThumbImage;
        Iterator<Image> imageIter = imageList.iterator();
        while (imageIter.hasNext())
        {
          thisImage = imageIter.next();
          if (thisImage.getHeight() != null && thisImage.getWidth() != null) {

logger.debug("----->BEFORE THUMB IMAGE GENERATION");
            // gather thumbnail for this image
            thisThumbImage = imageGatherer.get( Image.class, thisImage.getThumbnailImageKey().toString() );
logger.debug("----->AFTER THUMB IMAGE GENERATION");

            // new row;  add to list
            ImageSummaryRow imageSummaryRow = new ImageSummaryRow(thisImage, thisThumbImage);
            imageSummaryRowList.add(imageSummaryRow);

          }
        }
	logger.debug ("Returning list of " + imageSummaryRowList.size() +
		" images");

        return imageSummaryRowList;
    }

}
