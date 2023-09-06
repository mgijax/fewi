package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.Genotype;
import org.jax.mgi.fe.datamodel.Image;
import org.jax.mgi.fe.datamodel.ImageAllele;
import org.jax.mgi.fe.datamodel.ImageID;
import org.jax.mgi.fe.datamodel.ImagePane;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.ImageFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.ImageSummaryRow;
import org.jax.mgi.fewi.util.PaginationControls;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.fe.IndexConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
      = LoggerFactory.getLogger(getClass().getName());

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

    // Access Via Image ID
    @RequestMapping(value="/pheno/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView phenoImageDetailByID(HttpServletRequest request, @PathVariable("imageID") String imageID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->phenoImageDetailByID started");

        // find the requested image
        List<Image> imageList = getImageForID(imageID);

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
    
    // Access Via Image ID
    @RequestMapping(value="/molecular/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView molecularImageDetailByID(HttpServletRequest request, @PathVariable("imageID") String imageID) 
    {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->molecularImageDetailByID started");
        
        // find the requested image
        List<Image> imageList = getImageForID(imageID);

        // ensure we found an image
        if (imageList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }

        //pull out the image
        Image image = imageList.get(0);
        
        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("image_detail_generic");
        mav.addObject("image", image);
        mav.addObject("imageType","Molecular");
        mav.addObject("imageAlleleList", image.getImageAlleles());
        mav.addObject("reference", image.getReference());

        return mav;
    }


    // Access Via Image DB Key
    @RequestMapping(value="/pheno/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView phenoImageDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->phenoImageDetailByKey started");

        // find the requested image
        SearchResults<Image> searchResults
          = imageFinder.getImageByKey(dbKey);

        List<Image> imageList = searchResults.getResultObjects();

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

    @RequestMapping(value="/pheno")
    public ModelAndView phenoImageDetailByKeyParam(HttpServletRequest request, @RequestParam("key") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->phenoImageDetailByKeyParam started: " + dbKey);

        // find the requested image
        SearchResults<Image> searchResults
          = imageFinder.getImageByKey(dbKey);

        List<Image> imageList = searchResults.getResultObjects();

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
    public ModelAndView expressionImageDetailByID(HttpServletRequest request, @PathVariable("imageID") String imageID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->expressionImageDetailByID started");

        // find the requested image
        List<Image> imageList = getImageForID(imageID);

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


    // Access Via Image DB Key
    @RequestMapping(value="/expression/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView expressionImageDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->expressionImageDetailByKey started");

        // find the requested image
        SearchResults<Image> searchResults
          = imageFinder.getImageByKey(dbKey);

        List<Image> imageList = searchResults.getResultObjects();

        // ensure we found an image
        if (imageList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }

        //pull out the image
        Image image = imageList.get(0);

        // generate and return mav for pheno detail
        return getGxdDetailMAV(image);
    }

    //----------------------------------------------------------//
    // image detail meta-handler 'allele' and 'marker' not in URL
    //----------------------------------------------------------//
    @RequestMapping(value="/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView detailByID(HttpServletRequest request, @PathVariable("imageID") String imageID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->detailByID started");

        // ModelAndView object to be returned
        ModelAndView mav = new ModelAndView();

        // find the requested image
        List<Image> imageList = getImageForID(imageID);

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
    			HttpServletRequest request,
                @PathVariable("alleleID") String alleleID,
                @ModelAttribute Paginator page)
    {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
        imageSearchParams.setPageSize(10000);
        imageSearchParams.setSorts(genDefaultSorts());
        Integer alleleKey = allele.getAlleleKey();
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


    //-----------------------------------//
    // Pheno Image Summary by Allele Key
    //-----------------------------------//
    @RequestMapping(value="/phenoSummary/allele")
    public ModelAndView phenoImageSummeryByAlleleKey(HttpServletRequest request, @RequestParam("key") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->phenoImageSummeryByAlleleKey started");

        ModelAndView mav = new ModelAndView("image_phenoSummary_by_allele");

        // find the requested allele for header
        SearchResults<Allele> alleleSearchResults
          = alleleFinder.getAlleleByKey(dbKey);

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
        imageSearchParams.setSorts(genDefaultSorts());
        Integer alleleKey = allele.getAlleleKey();
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
    			HttpServletRequest request,
               @PathVariable("markerID") String markerID,
               @ModelAttribute Paginator page)
    {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
        imageSearchParams.setSorts(genDefaultSorts());
        page.setResultsDefault(10);
        imageSearchParams.setPaginator(page);
        Integer markerKey = marker.getMarkerKey();
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


    //----------------------------------//
    // Pheno Image Summary by Marker Key
    //----------------------------------//
    @RequestMapping(value="/phenoSummary/marker")
    public ModelAndView phenoImageSummeryByMarkerKey(HttpServletRequest request, @RequestParam("key") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->phenoImageSummeryByMarker started");

        ModelAndView mav = new ModelAndView("image_phenoSummary_by_marker");
        Paginator page = new Paginator();

        // data holders
        Marker marker;
        List<ImageSummaryRow> imageSummaryRows;

        /**********************
        * Gather Marker Object
        **********************/

        // find the requested marker for header - using db key passed in
        SearchResults<Marker> markerSearchResults
          = markerFinder.getMarkerByKey(dbKey);

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
        imageSearchParams.setSorts(genDefaultSorts());
        page.setResultsDefault(10);
        imageSearchParams.setPaginator(page);
        Integer markerKey = marker.getMarkerKey();
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
    // NOTE: As of 2013/04/08 this redirects to the GXD controller
    //-------------------------------//
    @RequestMapping(value="/gxdSummary/marker/{markerID}")
    public String gxdImageSummeryByMarker(
               @PathVariable("markerID") String markerID,
               @ModelAttribute Paginator page)
    {
        logger.debug("->gxdImageSummeryByMarker started");
        logger.debug("Forwarding to /gxd/marker/{mgiid}?tab=imagestab");
        return "forward:/mgi/gxd/marker/"+markerID+"?tab=imagestab";
    }


    //---------------------------------//
    // GXD Image Summary by Marker Key
    // NOTE: As of 2013/04/08 this redirects to the GXD controller
    //---------------------------------//
    // this is solely to support forwarding from old URLs via urlmapper

    @RequestMapping(value="/gxdSummary/marker")
    public String gxdImageSummeryByMarkerKey(@RequestParam("key") String dbKey) {
        logger.debug("->gxdImageSummeryByMarker started");

        /**********************
        * Gather Marker Object
        **********************/

        // find the requested marker for header - using db key passed in
        SearchResults<Marker> markerSearchResults
          = markerFinder.getMarkerByKey(dbKey);

        List<Marker> markerList = markerSearchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            //ModelAndView mav = new ModelAndView("error");
            //mav.addObject("errorMsg", "No Marker Found");
           return "errorMsg";
        }
        if (markerList.size() > 1) { // dupe found
        	//ModelAndView mav = new ModelAndView("error");
            //mav.addObject("errorMsg", "Duplicate Key");
           // return mav;
           return "errorMsg";
        }

        // success - we have a single object
        Marker marker = markerList.get(0);
        logger.debug("forwarding to /gxd/marker/{mgiid}?tab=imagestab");
        return "forward:/mgi/gxd/marker/"+marker.getPrimaryID()+"?tab=imagestab";

    }

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    // generate the sorts
    private List<Sort> genDefaultSorts() {

        List<Sort> sorts = new ArrayList<Sort>();
        Sort sort = new Sort(SortConstants.BY_DEFAULT, false);
        sorts.add(sort);
        return sorts;
    }

    // retrieves an image list for a given ID
    public List<Image> getImageForID (String imageID) {

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter imageIdFilter = new Filter(SearchConstants.IMG_ID, imageID);
        searchParams.setFilter(imageIdFilter);

        // find the requested image
        SearchResults<Image> searchResults
          = imageFinder.getImageByID(searchParams);

        return searchResults.getResultObjects();

    }

    // generates a mav for a pheno image detail page
    private ModelAndView getPhenoDetailMAV (Image image) {

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("image_detail_generic");

        mav.addObject("image", image);
        mav.addObject("imageType","Phenotype");
        
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

//            IDLinker idLinker = ContextLoader.getIDLinker();
            IDLinker idLinker = IDLinker.getInstance();
            List<String> otherIdLinks = new ArrayList<String>();

            // for each 'otherID', generate the anchor to external resource
            for (ImageID thisOtherID : otherIDs){
              otherIdLinks.add(idLinker.getLinks(thisOtherID));
            }
            mav.addObject("otherIdLinks", otherIdLinks);
        }

        return mav;

    }
}
