package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* controller specific
/*------------------------------*/

// data model objects
import mgi.frontend.datamodel.*;

// index constants
import org.jax.mgi.shr.fe.IndexConstants;

// fewi
import org.jax.mgi.fewi.finder.ImageFinder;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.summary.ImageSummaryRow;
import org.jax.mgi.fewi.util.IDLinker;
import org.jax.mgi.fewi.config.ContextLoader;

/*--------------------------------------*/
/* standard imports for all controllers */
/*--------------------------------------*/

// internal
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;

// external
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /image/ uri's
 */
@Controller
@RequestMapping(value="/image")
public class ImageController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageFinder imageFinder;

    @Autowired
    private AlleleFinder alleleFinder;

    @Autowired
    private MarkerFinder markerFinder;

    //--------------------------------------------------------------------//
    // public detail page methods
    //--------------------------------------------------------------------//

    //-----------------------------------------//
    // handler for multiple image detail types
    //-----------------------------------------//
    @RequestMapping(value="/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView detailByID(@PathVariable("imageID") String imageID) {

        logger.debug("->detailByID started");

        // ModelAndView object to be returned
        ModelAndView mav = new ModelAndView();

        // find the requested image
        List<Image> imageList = this.getImageForID(imageID);

        // ensure we found an image
        if (imageList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }

        //pull out the image
        Image image = imageList.get(0);

        // derive the correct image detail mav
        if (image.getImageClass().equals(IndexConstants.IMAGE_CLASS_PHENO)) {
          // generate mav for pheno detail
          mav = getPhenoDetailMAV(image);
        }
        if (image.getImageClass().equals(IndexConstants.IMAGE_CLASS_GXD)) {
          // generate and return mav for expression detail
          mav = getGxdDetailMAV(image);
        }

        return mav;
    }


    //--------------------//
    // Pheno Image Detail
    //--------------------//
    @RequestMapping(value="/pheno/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView phenoImageDetailByID(@PathVariable("imageID") String imageID) {

        logger.debug("->phenoImageDetailByID started");

        // find the requested image
        List<Image> imageList = this.getImageForID(imageID);

        // ensure we found an image
        if (imageList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }

        //pull out the image
        Image image = imageList.get(0);

        // generate and return mav for pheno detail
        return getPhenoDetailMAV(image);
    }


    //------------------------//
    // Expression Image Detail
    //------------------------//
    @RequestMapping(value="/expression/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView expressionImageDetailByID(@PathVariable("imageID") String imageID) {

        logger.debug("->expressionImageDetailByID started");

        // find the requested image
        List<Image> imageList = this.getImageForID(imageID);

        // ensure we found an image
        if (imageList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }

        //pull out the imamge
        Image image = imageList.get(0);

        // generate and return mav for expression detail
        return getGxdDetailMAV(image);
    }


    //--------------------------------------------------------------------//
    // public summary page methods
    //--------------------------------------------------------------------//

    //-------------------------------//
    // Pheno Image Summary by Allele
    //-------------------------------//
    @RequestMapping(value="/phenoSummary/allele/{alleleID}")
    public ModelAndView phenoImageSummeryByAllele(
                           @PathVariable("alleleID") String alleleID)
    {
        logger.debug("->phenoImageSummeryByAllele started");

        ModelAndView mav = new ModelAndView("image_phenoSummary_by_allele");

        // setup search parameters to get allele object
        SearchParams alleleSearchParams = new SearchParams();
        Filter alleleIdFilter = new Filter(SearchConstants.ALL_ID, alleleID);
        alleleSearchParams.setFilter(alleleIdFilter);

        // find the requested allele for header
        SearchResults<Allele> alleleSearchResults
          = alleleFinder.getAlleleByID(alleleSearchParams);

        List<Allele> alleleList = alleleSearchResults.getResultObjects();

        // there can be only one...
        if (alleleList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Allele Found");
            return mav;
        }
        if (alleleList.size() > 1) { // dupe found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object

        // pull out the allele, and place into the mav
        Allele allele = alleleList.get(0);
        mav.addObject("allele", allele);

        // derive displayed title & heading; special handling for allele type
        if (allele.getAlleleType().startsWith("Transgenic")) {
          mav.addObject("pageTitle", "Phenotype Images Associated With This Transgene - MGI");
          mav.addObject("pageHeading", "Phenotype Images Associated With This Transgene");
        } else {
          mav.addObject("pageTitle", "Phenotype Images Associated With This Allele - MGI");
          mav.addObject("pageHeading", "Phenotype Images Associated With This Allele");
        }

        // derive the synonym list, if the allele has any
        mav.addObject("synonyms", allele.getSynonyms());

        // setup search parameters to get the image objects
        SearchParams imageSearchParams = new SearchParams();
        Integer alleleKey = new Integer(allele.getAlleleKey());
        Filter alleleKeyFilter
          = new Filter(SearchConstants.ALL_KEY, alleleKey.toString());
        imageSearchParams.setFilter(alleleKeyFilter);

        // find the requested images for this allele
        SearchResults<Image> imageSearchResults
          = imageFinder.getPhenoImagesByAlleleKey(imageSearchParams);

        // generate summary row objects
        Image thisImage;
        List<Image> imageList = imageSearchResults.getResultObjects();
        List<ImageSummaryRow> imageSummaryRows
          = new ArrayList<ImageSummaryRow>();
        Iterator<Image> imageIter = imageList.iterator();
        while (imageIter.hasNext())
        {
          thisImage = imageIter.next();
          if (thisImage.getHeight() != null && thisImage.getWidth() != null) {
            ImageSummaryRow imageSummaryRow = new ImageSummaryRow(thisImage);
            imageSummaryRows.add(imageSummaryRow);
          }
        }
        mav.addObject("imageSummaryRows", imageSummaryRows);

        return mav;
    }


    //-------------------------------//
    // Pheno Image Summary by Marker
    //-------------------------------//
    @RequestMapping(value="/phenoSummary/marker/{markerID}")
    public ModelAndView phenoImageSummeryByMarker(
                           @PathVariable("markerID") String markerID)
    {
        logger.debug("->phenoImageSummeryByMarker started");

        ModelAndView mav = new ModelAndView("image_phenoSummary_by_marker");

        // setup search parameters to get allele object
        SearchParams markerSearchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        markerSearchParams.setFilter(markerIdFilter);

        // find the requested allele for header
        SearchResults<Marker> markerSearchResults
          = markerFinder.getMarkerByID(markerSearchParams);

        List<Marker> markerList = markerSearchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        }
        if (markerList.size() > 1) { // dupe found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object

        // pull out the marker, and place into the mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

        // setup search parameters to get the image objects
        SearchParams imageSearchParams = new SearchParams();
        Integer markerKey = new Integer(marker.getMarkerKey());
        Filter markerKeyFilter
          = new Filter(SearchConstants.MRK_KEY, markerKey.toString());
        imageSearchParams.setFilter(markerKeyFilter);

        // find the requested images for this allele
        SearchResults<Image> imageSearchResults
          = imageFinder.getPhenoImagesByMarkerKey(imageSearchParams);

        // generate summary row objects
        Image thisImage;
        List<Image> imageList = imageSearchResults.getResultObjects();
        List<ImageSummaryRow> imageSummaryRows
          = new ArrayList<ImageSummaryRow>();
        Iterator<Image> imageIter = imageList.iterator();
        while (imageIter.hasNext())
        {
          thisImage = imageIter.next();
          if (thisImage.getHeight() != null && thisImage.getWidth() != null) {
            ImageSummaryRow imageSummaryRow = new ImageSummaryRow(thisImage);
            imageSummaryRows.add(imageSummaryRow);
          }
        }
        mav.addObject("imageSummaryRows", imageSummaryRows);

        // total counts of alleles
        mav.addObject("totalImages", imageSearchResults.getTotalCount());

        return mav;
    }


    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    // retrieves an image list for a given ID
    private List<Image> getImageForID (String imageID) {

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter imageIdFilter = new Filter(SearchConstants.IMG_ID, imageID);
        searchParams.setFilter(imageIdFilter);

        // find the requested image
        SearchResults searchResults
          = imageFinder.getImageByID(searchParams);

        return searchResults.getResultObjects();

    }

    // generates a mav for a pheno image detail page
    private ModelAndView getPhenoDetailMAV (Image image) {

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("image_detail_pheno");

        mav.addObject("image", image);

        // package associated alleles
        List<ImageAllele> imageAlleleList = image.getImageAlleles();
        if (!imageAlleleList.isEmpty()) {
            mav.addObject("imageAlleleList", imageAlleleList);
        }

        // package genotypes
        List<Genotype> genotypeList = image.getGenotypes();
        if (!genotypeList.isEmpty()) {
            mav.addObject("genotypeList", genotypeList);
        }

        // package reference
        Reference reference = image.getReference();
        mav.addObject("reference", reference);

        return mav;

    }

    // generates a mav for a GXD image detail page
    private ModelAndView getGxdDetailMAV (Image image) {

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("image_detail_expression");

        mav.addObject("image", image);

        // package reference
        Reference reference = image.getReference();
        mav.addObject("reference", reference);

        // package image panes
        List<ImagePane> imagePaneList = image.getImagePanes();
        if (!imagePaneList.isEmpty()) {
            mav.addObject("imagePaneList", imagePaneList);
        }

        // other IDs;  used in 'other DB links" section
        List<ImageID> otherIDs = image.getOtherIds();
        if (otherIDs.size() > 0) {

            ImageID thisOtherID;
            IDLinker idLinker = ContextLoader.getIDLinker();
            List<String> otherIdLinks = new ArrayList<String>();

            // for each 'otherID', generate the anchor to external resource
            Iterator otherIDsIter = otherIDs.iterator();
            while (otherIDsIter.hasNext() ){
              thisOtherID = (ImageID)otherIDsIter.next();
              otherIdLinks.add(idLinker.getLinks(thisOtherID));
            }
            mav.addObject("otherIdLinks", otherIdLinks);
        }

        return mav;

    }
}
