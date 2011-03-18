package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* controller specific
/*------------------------------*/
import org.jax.mgi.fewi.forms.FooQueryForm;
// fewi
import org.jax.mgi.fewi.finder.ImageFinder;

// data model objects
import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.ImageAllele;
import mgi.frontend.datamodel.ImagePane;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Genotype;


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


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


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

        // find the requested foo
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

        // find the requested foo
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


        return mav;
    }


    //--------------------//
    // Foo Detail By Key
    //--------------------//
/*    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView fooDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->fooDetailByKey started");

        // find the requested foo
        SearchResults searchResults
          = fooFinder.getFooByKey(dbKey);
        List<Marker> fooList = searchResults.getResultObjects();

        // there can be only one...
        if (fooList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Foo Found");
            return mav;
        }// success

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("foo_detail");

        //pull out the foo, and add to mav
        Marker foo = fooList.get(0);
        mav.addObject("foo", foo);

        // package referenes; gather via object traversal
        List<Reference> references = foo.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        return mav;
    }
*/

    //-------------------------------//
    // Foo Summary by Reference
    //-------------------------------//
/*    @RequestMapping(value="/reference/{refID}")
    public ModelAndView fooSummeryByRef(@PathVariable("refID") String refID) {

        logger.debug("->fooSummeryByRef started");

        ModelAndView mav = new ModelAndView("foo_summary_reference");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults searchResults
          = referenceFinder.searchReferences(searchParams);
        List<Reference> refList = searchResults.getResultObjects();

        // there can be only one...
        if (refList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refID);
            return mav;
        }
        if (refList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe references found for " + refID);
            return mav;
        }

        // pull out the reference, and place into the mav
        Reference reference = refList.get(0);
        mav.addObject("reference", reference);

        // pre-generate query string
        mav.addObject("queryString", "refKey=" + reference.getReferenceKey());

        return mav;
    }

*/

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//
/*
    // generate the sorts
    private List<Sort> genSorts(HttpServletRequest request) {

        logger.debug("->genSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");
        if (sortRequested == null) {
            sortRequested = SortConstants.FOO_SORT;
        }

        String dirRequested  = request.getParameter("dir");
        boolean desc = false;
        if("desc".equalsIgnoreCase(dirRequested)){
            desc = true;
        }

        Sort sort = new Sort(sortRequested, desc);
        sorts.add(sort);

        logger.debug ("sort: " + sort.toString());
        return sorts;
    }

    // generate the filters
    private Filter genFilters(FooQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);


        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String param1 = query.getParam1();
        String param2 = query.getParam2();

        //
        if ((param1 != null) && (!"".equals(param1))) {
            filterList.add(new Filter (SearchConstants.FOO_ID, param1,
                Filter.OP_EQUAL));
        }

        //
        if ((param2 != null) && (!"".equals(param2))) {
            filterList.add(new Filter (SearchConstants.FOO_ID, param2,
                Filter.OP_EQUAL));
        }

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }

        return containerFilter;
    }
*/

}
