package org.jax.mgi.fewi.controller;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.AlleleSystemAssayResult;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Image;
import org.jax.mgi.fewi.finder.RecombinaseFinder;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.summary.RecomImage;
import org.jax.mgi.fewi.summary.RecomImageRow;
import org.jax.mgi.fewi.summary.RecombinaseSummary;
import org.jax.mgi.fewi.summary.RecomSpecificitySummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import java.util.Iterator;

/*--------------------------------------*/
/* standard imports for all controllers */
/*--------------------------------------*/

import java.util.*;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.util.FormatHelper;

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
 * This controller maps all /recombinase/ uri's
 */
@Controller
@RequestMapping(value="/recombinase")
public class RecombinaseController {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    // logger for the class
    private Logger logger = LoggerFactory.getLogger (
        RecombinaseController.class);

    // get the finder to use for various methods
    @Autowired
    private RecombinaseFinder recombinaseFinder;

    /*-------------------------*/
    /* public instance methods */
    /*-------------------------*/

    // add new RecombinaseQueryForm and Paginator objects to model for QF
    @RequestMapping(method=RequestMethod.GET)
    public String getQueryForm(Model model) {
        model.addAttribute(new RecombinaseQueryForm());
        model.addAttribute("sort", new Paginator());
        return "recombinase_query";
    }

    // qf submission.  drop completed ReferenceQueryForm object and query string
    // into model for summary to use
    //-------------------------------//
    // Query Form Submission
    //-------------------------------//
    @RequestMapping("/summary")
    public String recombinaseSummary(HttpServletRequest request, Model model,
            @ModelAttribute RecombinaseQueryForm queryForm) {

        logger.debug("queryString: " + request.getQueryString());

        // objects needed by display
        model.addAttribute("recombinaseQueryForm", queryForm);
        model.addAttribute("queryString", request.getQueryString());

        return "recombinase_summary";
    }

    //-------------------------------//
    // JSON Results for Summary
    //-------------------------------//
    // this is the logic to perform the query and return json results
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<RecombinaseSummary> recombinaseSummaryJson(
            HttpServletRequest request,
            @ModelAttribute RecombinaseQueryForm query,
            @ModelAttribute Paginator page) {

        logger.debug(query.toString());

        // set up search parameters

        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.parseSorts(request));
        params.setFilter(this.parseRecombinaseQueryForm(query));

        // issue the query and get back the matching Allele objects

        SearchResults<Allele> searchResults =
            recombinaseFinder.searchRecombinases(params);

        // convert the Alleles to their RecombinaseSummary wrappers, and put
        // them in the JsonSummaryResponse object

        List<RecombinaseSummary> summaries = new ArrayList<RecombinaseSummary> ();
        Iterator<Allele> it = searchResults.getResultObjects().iterator();
        while (it.hasNext()) {
            summaries.add(new RecombinaseSummary(it.next()));
        }

        JsonSummaryResponse<RecombinaseSummary> jsonResponse =
            new JsonSummaryResponse<RecombinaseSummary>();

        jsonResponse.setSummaryRows (summaries);
        jsonResponse.setTotalCount (searchResults.getTotalCount());

        return jsonResponse;
    }



    //-------------------------------//
    // Cre Specificity
    //-------------------------------//
    @RequestMapping("/specificity")
    public ModelAndView creSpecificity(
            HttpServletRequest request,
            @ModelAttribute RecombinaseQueryForm query) {

        logger.debug("->creSpecificity() started");

        ModelAndView mav = new ModelAndView("recombinase_specificity");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        searchParams.setFilter(this.genFilters(query));

        // find the requested allele/system object
        SearchResults<AlleleSystem> searchResults =
            recombinaseFinder.getAlleleSystem(searchParams);
        List<AlleleSystem> alleleSystems = searchResults.getResultObjects();

        // ensure we found something...
        if (alleleSystems.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Allele/System not available");
            return mav;
        }

        // gather required data objects, and place into the mav
        mav.addObject("queryString", request.getQueryString());
        AlleleSystem alleleSystem = alleleSystems.get(0);
        Allele allele = alleleSystem.getAllele();
        mav.addObject("alleleSystem", alleleSystem);
        mav.addObject("allele", allele);
        mav.addObject("systemDisplayStr",
          FormatHelper.initCap(alleleSystem.getSystem()));
        mav.addObject("otherAlleles", alleleSystem.getOtherAlleles());
        mav.addObject("otherAllelesSize", alleleSystem.getOtherAlleles().size());
        mav.addObject("otherSystems", alleleSystem.getOtherSystems());
        mav.addObject("otherSystemsSize", alleleSystem.getOtherSystems().size());

        // pre-gen comma-delimitted synonym list
        List<String> synonymList = new ArrayList<String> ();
        Iterator<AlleleSynonym> synonymIter = allele.getSynonyms().iterator();
        while (synonymIter.hasNext()) {
            AlleleSynonym thisSynonym = synonymIter.next();
            synonymList.add(thisSynonym.getSynonym());
        }
        mav.addObject("synonymsString",
          FormatHelper.superscript(FormatHelper.commaDelimit(synonymList)));

        // pre-form image gallery rows
        int imageIndex = 0;
        Image thisImage;
        List<RecomImage> recomImages = new ArrayList<RecomImage>();
        List<RecomImageRow> recomImageRows = new ArrayList<RecomImageRow>();

        Iterator<Image> imageIter = alleleSystem.getImages().iterator();
        while (imageIter.hasNext()) {

          thisImage = imageIter.next();
          if (thisImage.getHeight() != null && thisImage.getWidth() != null) {

            imageIndex++;
            RecomImage thisRecomImage
              = new RecomImage(thisImage, imageIndex);
            recomImages.add(thisRecomImage);

            // if we have enough images to fill a row, of if this is our last
            // image, create the row and add to row list
            if ( ((imageIndex % 8 ) == 0) || !imageIter.hasNext() ) {
                RecomImageRow thisRow = new RecomImageRow();
                thisRow.setRecomImages(recomImages);
                recomImageRows.add(thisRow);
                recomImages = new ArrayList<RecomImage>();
            }
	      }
        }
        mav.addObject("galleryImagesRows", recomImageRows);

        return mav;
    }



    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/jsonSpecificity")
    public @ResponseBody JsonSummaryResponse<RecomSpecificitySummaryRow> specificitySummaryJson(
            HttpServletRequest request,
            @ModelAttribute RecombinaseQueryForm query,
            @ModelAttribute Paginator page) {

        logger.debug("->specificitySummaryJson started");

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genRecomSummarySorts(request));
        params.setFilter(this.genFilters(query));

        // perform query, and pull out the requested objects
        SearchResults<AlleleSystemAssayResult> searchResults =
            recombinaseFinder.getAssaySummary(params);
        List<AlleleSystemAssayResult> assayResultList
          = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<RecomSpecificitySummaryRow> summaryRows
          = new ArrayList<RecomSpecificitySummaryRow> ();
        Iterator<AlleleSystemAssayResult> it = assayResultList.iterator();
        while (it.hasNext()) {
            AlleleSystemAssayResult thisAssayResult = it.next();
            if (thisAssayResult == null) {
                logger.debug("--> Null Object");
            }else {
                summaryRows.add(new RecomSpecificitySummaryRow(thisAssayResult));
            }
        }

        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<RecomSpecificitySummaryRow> jsonResponse
          = new JsonSummaryResponse<RecomSpecificitySummaryRow>();

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        return jsonResponse;
    }



    /*---------------------------------------------------------------------*/
    /* private instance methods                                            */
    /*---------------------------------------------------------------------*/

    /** TODO : needs to be adjusted for alleles
     *
     */
    private List<Sort> parseSorts(HttpServletRequest request) {

        List<Sort> sorts = new ArrayList<Sort>();

        String s = request.getParameter("sort");
        String d = request.getParameter("dir");
        boolean desc = false;
/*
        if ("symbol".equalsIgnoreCase(s)){
            s = SortConstants.CRE_SYMBOL;
        } else if ("alleletype".equalsIgnoreCase(s)){
            s = SortConstants.CRE_TYPE;
        } else if ("induciblenote".equalsIgnoreCase(s)){
            s = SortConstants.CRE_INDUCIBLE;
        } else if ("countofreferences".equalsIgnoreCase(s)){
            s = SortConstants.CRE_REF_COUNT;
        } else if ("inalimentarysystem".equalsIgnoreCase(s)){
            s = SortConstants.CRE_IN_ALIMENTARY_SYSTEM;
        } else {
            s = SortConstants.CRE_DRIVER;
        }
*/
        if (s == null) { s = SortConstants.CRE_DRIVER; }

        if("desc".equalsIgnoreCase(d)){
            desc = true;
        }
        Sort sort = new Sort(s, desc);
        logger.debug ("sort: " + sort.toString());
        sorts.add(sort);
        return sorts;
    }

    // generate the sorts for assay summary on recombinase detail
    private List<Sort> genRecomSummarySorts(HttpServletRequest request) {

        logger.debug("->genRecomSummarySorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");

        // empty
        if (sortRequested == null) {
            return sorts;
        }

        // expected sort values
        if ("structure".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.CRE_BY_STRUCTURE;
        } else if ("assayedAge".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.CRE_BY_AGE;
        } else if ("level".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.CRE_BY_LEVEL;
        } else if ("source".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.CRE_BY_JNUM_ID;
        } else if ("pattern".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.CRE_BY_PATTERN;
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


    private Filter parseRecombinaseQueryForm(RecombinaseQueryForm query){
        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String driver = query.getDriver();
        String system = query.getSystem();

        // build driver query filter
        if ((driver != null) && (!"".equals(driver))) {
            filterList.add(new Filter (SearchConstants.ALL_DRIVER, driver,
                Filter.OP_EQUAL));
        }

        // build system query filter
        if ((system != null) && (!"".equals(system))) {
            filterList.add(new Filter (SearchConstants.ALL_SYSTEM, system,
                Filter.OP_EQUAL));
        }

        // we do have some filters, build 'em and add to searchParams
        if (filterList.size() > 0){
            Filter f = new Filter();
            f.setFilterJoinClause(Filter.FC_AND);
            f.setNestedFilters(filterList);
            return f;
        // none yet, so check the id query and build it
        // } else if (query.getId() != null && !"".equals(query.getId())){
            //TODO -- do we need an ID query here?
            //List<String> ids = Arrays.asList(query.getId().split(";"));
            //return new Filter(SearchConstants.REF_ID, ids, Filter.OP_IN);
        } else {
            //TODO no query params
        }
        return new Filter();
    }




    // generation of filters
    private Filter genFilters(RecombinaseQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);

        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String driver = query.getDriver();
        String system = query.getSystem();
        String id = query.getId();
        String systemKey = query.getSystemKey();

        // build the possible filters
        if ((driver != null) && (!"".equals(driver))) {
            filterList.add(new Filter (SearchConstants.ALL_DRIVER, driver,
                Filter.OP_EQUAL));
        }
        if ((system != null) && (!"".equals(system))) {
            filterList.add(new Filter (SearchConstants.ALL_SYSTEM, system,
                Filter.OP_EQUAL));
        }
        if ((id != null) && (!"".equals(id))) {
            filterList.add(new Filter (SearchConstants.ALL_ID, id,
                Filter.OP_EQUAL));
        }
        if ((systemKey != null) && (!"".equals(systemKey))) {
            filterList.add(new Filter (SearchConstants.CRE_SYSTEM_KEY, systemKey,
                Filter.OP_EQUAL));
        }

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){

            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }

        logger.debug("genFilters -> " + containerFilter);
        return containerFilter;
    }


}
