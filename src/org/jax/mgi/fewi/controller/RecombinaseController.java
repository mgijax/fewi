package org.jax.mgi.fewi.controller;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.AlleleSystemAssayResult;
import mgi.frontend.datamodel.Image;

import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.RecombinaseFinder;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.RecomImage;
import org.jax.mgi.fewi.summary.RecomImageRow;
import org.jax.mgi.fewi.summary.RecomSpecificitySummaryRow;
import org.jax.mgi.fewi.summary.RecombinaseSummary;
import org.jax.mgi.fewi.util.FormatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    
    @Autowired
    private AlleleFinder alleleFinder;

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
    public ModelAndView creSpecificity( HttpServletRequest request,
            @ModelAttribute RecombinaseQueryForm query) {

        logger.debug("->creSpecificity() started");

        ModelAndView mav = new ModelAndView("recombinase_specificity");

        Image thisImage;
        Iterator<Image> imageIter;
        List<Image> validatedImages   = new ArrayList<Image>();
        
        Allele allele = null;
        List<AlleleSystem> alleleSystems = new ArrayList<AlleleSystem>();
        AlleleSystem alleleSystem = null;
        
        /*
         * Lookup AlleleSystem object
         */
        String alleleKey = query.getAlleleKey();
        if (alleleKey != null && !"".equals(alleleKey)) {
        	SearchResults<Allele> alleleResults = alleleFinder.getAlleleByKey(alleleKey);
        	if (alleleResults.getResultObjects().size() == 1) {
        		allele = alleleResults.getResultObjects().get(0);
        		logger.debug("found allele: " + allele.getSymbol());
        		alleleSystems = allele.getAlleleSystems();
        		logger.debug("Systems for allele: " + alleleSystems.size());
        		for (AlleleSystem sys: alleleSystems) {
        			logger.debug("system: " + sys.getSystemKey().toString());
        			if (query.getSystemKey().equals(sys.getSystemKey().toString())) {
        				alleleSystem = sys;
        				break;
        			}
        		}
        	}
        } else {
            // setup search parameters object to gather the requested object
            SearchParams searchParams = new SearchParams();
            searchParams.setFilter(this.genFilters(query));

            // find the requested allele/system object
            SearchResults<AlleleSystem> searchResults =
                recombinaseFinder.getAlleleSystem(searchParams);
            alleleSystems = searchResults.getResultObjects();
	
            if (alleleSystems.size() == 1) {            
		        alleleSystem = alleleSystems.get(0);
		        allele = alleleSystem.getAllele();
            }
        }

        
        // ensure we found allele system
        if (allele == null || alleleSystem == null) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Allele/System not available");
        } else {
	        /*
	         * Remove sub-objects from AlleleSystem, and fill ModelAndView
	         * with display data
	         */
        	String queryString = request.getQueryString();
        	if (request.getParameterMap().containsKey("alleleKey")){
        		logger.debug("hasKey");
        		queryString = queryString + "&id=" + allele.getPrimaryID();
        	} 
        	
        	mav.addObject("queryString", queryString);
	        mav.addObject("alleleSystem", alleleSystem);
	        mav.addObject("allele", allele);
	        mav.addObject("systemDisplayStr",
	          FormatHelper.initCap(alleleSystem.getSystem()));
	        mav.addObject("otherAlleles", alleleSystem.getOtherAlleles());
	        mav.addObject("otherAllelesSize", alleleSystem.getOtherAlleles().size());
	        mav.addObject("otherSystems", alleleSystem.getOtherSystems());
	        mav.addObject("otherSystemsSize", alleleSystem.getOtherSystems().size());
	
	        // allele synonyms; pre-gen comma-delimitted list
	        List<String> synonymList = new ArrayList<String> ();
	        Iterator<AlleleSynonym> synonymIter = allele.getSynonyms().iterator();
	        while (synonymIter.hasNext()) {
	            AlleleSynonym thisSynonym = synonymIter.next();
	            synonymList.add(thisSynonym.getSynonym());
	        }
	        mav.addObject("synonymsString",
	          FormatHelper.superscript(FormatHelper.commaDelimit(synonymList)));
	
	        // remove images with 'null' values
	        imageIter = alleleSystem.getImages().iterator();
	        while (imageIter.hasNext()) {
	          thisImage = imageIter.next();
	          if (thisImage.getHeight() != null && thisImage.getWidth() != null) {
				validatedImages.add(thisImage);
			  }
		    }
	
	        // iterate over the validated images; pre-gen image gallery rows
	        int imageIndex = 0;
	        List<RecomImage> recomImages = new ArrayList<RecomImage>();
	        List<RecomImageRow> recomImageRows = new ArrayList<RecomImageRow>();
	        imageIter = validatedImages.iterator();
	        while (imageIter.hasNext()) {
	
	          thisImage = imageIter.next();
	
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
	        mav.addObject("galleryImagesRows", recomImageRows);
        }
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
        logger.debug(query.toString());
        List<AlleleSystemAssayResult> assayResultList = new ArrayList<AlleleSystemAssayResult>();
        SearchResults<AlleleSystemAssayResult> searchResults;
        
        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<RecomSpecificitySummaryRow> jsonResponse
        	= new JsonSummaryResponse<RecomSpecificitySummaryRow>();

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genRecomSummarySorts(request));
        params.setFilter(this.genFilters(query));

        // perform query, and pull out the requested objects
        searchResults = recombinaseFinder.getAssaySummary(params);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        assayResultList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<RecomSpecificitySummaryRow> summaryRows
        	= new ArrayList<RecomSpecificitySummaryRow>();
        
        for (AlleleSystemAssayResult thisAssayResult: assayResultList) {
            if (thisAssayResult == null) {
                logger.debug("--> Null Object");
            } else {
                summaryRows.add(new RecomSpecificitySummaryRow(thisAssayResult));
            }
        }

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);        
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


    // method to parse query parameters into filters
    private Filter parseRecombinaseQueryForm(RecombinaseQueryForm query){

        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        // build driver query filter
        String driver = query.getDriver();
        if ((driver != null) && (!"".equals(driver))) {
            filterList.add(new Filter (SearchConstants.ALL_DRIVER, driver,
                Filter.OP_EQUAL));
        }

        // build system query filter
        String system = query.getSystem();
        if ((system != null) && (!"".equals(system))) {
            filterList.add(new Filter (SearchConstants.ALL_SYSTEM, system,
                Filter.OP_EQUAL));
        }

        // build container filter and return
        Filter containerFilter = new Filter();
        containerFilter.setFilterJoinClause(Filter.FC_AND);
        containerFilter.setNestedFilters(filterList);
        return containerFilter;
    }


    // generation of filters
    private Filter genFilters(RecombinaseQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);

        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String driver = query.getDriver();
        String id = query.getId();
        String system = query.getSystem();
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