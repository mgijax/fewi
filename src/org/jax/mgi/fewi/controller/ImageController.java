package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* controller specific
/*------------------------------*/

// data model objects
import mgi.frontend.datamodel.*;

// fewi
import org.jax.mgi.fewi.finder.ImageFinder;
import org.jax.mgi.fewi.finder.AlleleFinder;
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

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//

    @RequestMapping(value="/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView detailByID(@PathVariable("imageID") String imageID) {

        // ModelAndView object to be returned
        ModelAndView mav = new ModelAndView();




        return mav;
    }


    //--------------------//
    // Pheno Image Detail
    //--------------------//
    @RequestMapping(value="/pheno/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView phenoImageDetailByID(@PathVariable("imageID") String imageID) {

        logger.debug("->phenoImageDetailByID started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter imageIdFilter = new Filter(SearchConstants.IMG_ID, imageID);
        searchParams.setFilter(imageIdFilter);

        // find the requested image
        SearchResults searchResults
          = imageFinder.getImageByID(searchParams);
        List<Image> imageList = searchResults.getResultObjects();

        // there can be only one...
        if (imageList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }
        if (imageList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("image_detail_pheno");

        //pull out the image, and add to mav
        Image image = imageList.get(0);
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


    //------------------------//
    // Expression Image Detail
    //------------------------//
    @RequestMapping(value="/expression/{imageID:.+}", method = RequestMethod.GET)
    public ModelAndView expressionImageDetailByID(@PathVariable("imageID") String imageID) {

        logger.debug("->expressionImageDetailByID started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter imageIdFilter = new Filter(SearchConstants.IMG_ID, imageID);
        searchParams.setFilter(imageIdFilter);

        // find the requested image
        SearchResults searchResults
          = imageFinder.getImageByID(searchParams);
        List<Image> imageList = searchResults.getResultObjects();

        // there can be only one...
        if (imageList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Image Found");
            return mav;
        }
        if (imageList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("image_detail_expression");

        //pull out the imamge, and add to mav
        Image image = imageList.get(0);
        mav.addObject("image", image);

        // package reference
        Reference reference = image.getReference();
        mav.addObject("reference", reference);

        // package associated alleles
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

        // derive the synonym list, if the allele has any
        mav.addObject("synonyms", allele.getSynonyms());

        // setup search parameters to get the image objects
        SearchParams imageSearchParams = new SearchParams();
        Integer alleleKey = new Integer(allele.getAlleleKey());
        Filter alleleKeyFilter = new Filter(SearchConstants.ALL_KEY, alleleKey.toString());
        imageSearchParams.setFilter(alleleKeyFilter);

        // find the requested images for this allele
        SearchResults<Image> imageSearchResults
          = imageFinder.getImagesByAlleleKey(imageSearchParams);

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

}
