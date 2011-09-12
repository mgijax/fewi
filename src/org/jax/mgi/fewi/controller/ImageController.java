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
import org.jax.mgi.fewi.util.PaginationControls;
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
      = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private ImageFinder imageFinder;

    @Autowired
    private AlleleFinder alleleFinder;

    @Autowired
    private MarkerFinder markerFinder;


    //--------------------------------------------------------------------//
    //------------------------------- DETAIL PAGES
    //--------------------------------------------------------------------//

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


    //----------------------------------------------------------//
    // image detail meta-handler 'allele' and 'marker' not in URL
    //----------------------------------------------------------//
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


    //--------------------------------------------------------------------//
    //------------------------------- SUMMARIES
    //--------------------------------------------------------------------//

    //-------------------------------//
    // Pheno Image Summary by Allele
    //-------------------------------//
    @RequestMapping(value="/phenoSummary/allele/{alleleID}")
    public ModelAndView phenoImageSummeryByAllele(
                @PathVariable("alleleID") String alleleID,
                @ModelAttribute Paginator page)
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
          mav.addObject("pageTitle", "Phenotype Images associated with this Transgene - MGI");
          mav.addObject("pageHeading", "Phenotype Images associated with this Transgene");
        } else {
          mav.addObject("pageTitle", "Phenotype Images associated with this Allele - MGI");
          mav.addObject("pageHeading", "Phenotype Images associated with this Allele");
        }

        // derive the synonym list, if the allele has any
        mav.addObject("synonyms", allele.getSynonyms());

        // setup search parameters to get the image objects
        SearchParams imageSearchParams = new SearchParams();
        Integer alleleKey = new Integer(allele.getAlleleKey());
        Filter alleleKeyFilter
          = new Filter(SearchConstants.ALL_KEY, alleleKey.toString());
        imageSearchParams.setFilter(alleleKeyFilter);

        SearchResults<ImageSummaryRow> imageSearchResults
          = imageFinder.getPhenoImagesByAlleleKey(imageSearchParams);
        List<ImageSummaryRow> imageSummaryRows
          = imageSearchResults.getResultObjects();

        mav.addObject("imageSummaryRows", imageSummaryRows);

        return mav;
    }


    //-------------------------------//
    // Pheno Image Summary by Marker
    //-------------------------------//
    @RequestMapping(value="/phenoSummary/marker/{markerID}")
    public ModelAndView phenoImageSummeryByMarker(
               @PathVariable("markerID") String markerID,
               @ModelAttribute Paginator page)
    {
        logger.debug("->phenoImageSummeryByMarker started");

        ModelAndView mav = new ModelAndView("image_phenoSummary_by_marker");

        // data holders
        Marker marker;
        List<ImageSummaryRow> imageSummaryRows;

        /**********************
        * Gather Marker Object
        **********************/

        // setup search parameters to get marker object
        SearchParams markerSearchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        markerSearchParams.setFilter(markerIdFilter);

        // find the requested marker for header
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
        marker = markerList.get(0);

        /**********************
        * Gather Image Objects
        **********************/

        // setup search parameters to get the image objects
        SearchParams imageSearchParams = new SearchParams();
        page.setResultsDefault(10);
        imageSearchParams.setPaginator(page);
        Integer markerKey = new Integer(marker.getMarkerKey());
        Filter markerKeyFilter
          = new Filter(SearchConstants.MRK_KEY, markerKey.toString());
        imageSearchParams.setFilter(markerKeyFilter);

        // gather the images, and generate the summary rows
        SearchResults<ImageSummaryRow> imageSearchResults
          = imageFinder.getPhenoImagesByMarkerKey(imageSearchParams);
        imageSummaryRows = imageSearchResults.getResultObjects();

        /**********************
        * Fill View Object
        **********************/

        // data objects
        mav.addObject("marker", marker);
        mav.addObject("imageSummaryRows", imageSummaryRows);

        // pagination
        PaginationControls paginationControls = new PaginationControls(page);
        paginationControls.setResultsTotal(imageSearchResults.getTotalCount());
        mav.addObject("paginationControls", paginationControls);

        return mav;
    }


    //-------------------------------//
    // GXD Image Summary by Marker
    //-------------------------------//
    @RequestMapping(value="/gxdSummary/marker/{markerID}")
    public ModelAndView gxdImageSummeryByMarker(
               @PathVariable("markerID") String markerID,
               @ModelAttribute Paginator page)
    {
        logger.debug("->gxdImageSummeryByMarker started");

        // view object
        ModelAndView mav = new ModelAndView("image_gxdSummary_by_marker");

        // data holders
        Marker marker;
        List<ImageSummaryRow> imageSummaryRows;

        /**********************
        * Gather Marker Object
        **********************/

        // setup search parameters to get marker object
        SearchParams markerSearchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        markerSearchParams.setFilter(markerIdFilter);

        // find the requested marker for header
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
        marker = markerList.get(0);

        /**********************
        * Gather Image Objects
        **********************/

        // setup search parameters to get the image objects
        SearchParams imageSearchParams = new SearchParams();
        page.setResultsDefault(10);
        imageSearchParams.setPaginator(page);
        Integer markerKey = new Integer(marker.getMarkerKey());
        Filter markerKeyFilter
          = new Filter(SearchConstants.MRK_KEY, markerKey.toString());
        imageSearchParams.setFilter(markerKeyFilter);

        // gather the images, and generate the summary rows
        SearchResults<ImageSummaryRow> imageSearchResults
          = imageFinder.getGxdImagesByMarkerKey(imageSearchParams);
        imageSummaryRows = imageSearchResults.getResultObjects();


        /**********************
        * Fill View Object
        **********************/

        // data objects
        mav.addObject("marker", marker);
        mav.addObject("imageSummaryRows", imageSummaryRows);

        // pagination
        PaginationControls paginationControls = new PaginationControls(page);
        paginationControls.setResultsTotal(imageSearchResults.getTotalCount());
        mav.addObject("paginationControls", paginationControls);

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
