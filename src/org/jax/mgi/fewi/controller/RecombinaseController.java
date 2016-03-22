package org.jax.mgi.fewi.controller;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.AlleleSystem;
import mgi.frontend.datamodel.AlleleSystemAssayResult;
import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.group.RecombinaseEntity;

import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.RecombinaseFinder;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.highlight.RecombinaseHighlightInfo;
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
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final Logger logger = LoggerFactory.getLogger (
        RecombinaseController.class);

    // get the finder to use for various methods
    @Autowired
    private RecombinaseFinder recombinaseFinder;

    @Autowired
    private AlleleFinder alleleFinder;

    /*-------------------------*/
    /* public instance methods */
    /*-------------------------*/


    //-------------------------------//
    // Query Form Submission
    //-------------------------------//

    @RequestMapping("/summary")
    public String recombinaseSummary(HttpServletRequest request, Model model,
            @ModelAttribute RecombinaseQueryForm queryForm) {

        logger.debug("recombinase /summary queryString: " + request.getQueryString());

        // objects needed by display
        model.addAttribute("recombinaseQueryForm", queryForm);
        model.addAttribute("queryString", request.getQueryString());

        return "recombinase/recombinase_summary";
    }

    //-------------------------------//
    // JSON Results for Summary
    //-------------------------------//

    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<RecombinaseSummary> recombinaseSummaryJson(
            HttpServletRequest request,
            @ModelAttribute RecombinaseQueryForm query,
            @ModelAttribute Paginator page)
    {
        logger.debug(query.toString());

        // set up search parameters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(parseSummarySorts(request));
        params.setFilter(parseRecombinaseQueryForm(query));
        params.setIncludeMetaHighlight(true);
        params.setIncludeSetMeta(true);

        // issue the query and get back the matching Allele objects
        SearchResults<Allele> searchResults = recombinaseFinder.searchRecombinases(params);

        RecombinaseHighlightInfo highlightInfo = new RecombinaseHighlightInfo();
        if (!empty(query.getStructure()) || !empty(query.getSystem()) || !empty(query.getDetected()))
        {
        	// get highlight information
        	highlightInfo = recombinaseFinder.searchRecombinaseHighlights(params);
        }

        // convert the Alleles to their RecombinaseSummary wrappers, and put
        // them in the JsonSummaryResponse object
        List<RecombinaseSummary> summaries = new ArrayList<RecombinaseSummary> ();
        for(Allele allele : searchResults.getResultObjects())
        {
        	String allKeyStr = ((Integer) allele.getAlleleKey()).toString();
        	Set<String> detectedHighlights = highlightInfo.getDetectedHighlights(allKeyStr);
        	Set<String> notDetectedHighlights = highlightInfo.getNotDetectedHighlights(allKeyStr);

        	//logger.debug("allKey="+allKeyStr+" +hls=["+StringUtils.join(detectedHighlights,",")+"]");
        	//logger.debug("allKey="+allKeyStr+" -hls=["+StringUtils.join(notDetectedHighlights,",")+"]");


            summaries.add(new RecombinaseSummary(allele, detectedHighlights, notDetectedHighlights));
        }

        JsonSummaryResponse<RecombinaseSummary> jsonResponse = new JsonSummaryResponse<RecombinaseSummary>();

        jsonResponse.setSummaryRows (summaries);
        jsonResponse.setTotalCount (searchResults.getTotalCount());

        logger.info("done generating summary response");
        return jsonResponse;
    }



    //-------------------------------//
    // Cre Specificity
    //-------------------------------//
    @RequestMapping("/specificity")
    public ModelAndView creSpecificity( HttpServletRequest request,
    		@RequestParam(value="id", required=false) String alleleId,
    		@RequestParam(value="systemKey", required=false) String alleleSystemKey,
    		@RequestParam(value="system", required=false) String system) {

        logger.debug("->creSpecificity() started");

        ModelAndView mav = new ModelAndView("recombinase/recombinase_specificity");

        // search for allele system
        SearchResults<AlleleSystem> results;
        if (alleleSystemKey != null && !"".equals(alleleSystemKey)) {
        	results = recombinaseFinder.getAlleleSystemByKey(alleleSystemKey);
        }
        else {
        	// use allele ID and system label if we don't have the database key
        	results = recombinaseFinder.getAlleleSystemBySystem(alleleId, system);
        }
        List<AlleleSystem> alleleSystems = results.getResultObjects();

	// Last minute hack for existing bug...  We're coming to this page with
	// a systemKey parameter.  This sometimes refers to allele_system_key
	// in the recombinase_allele_system table, and this works okay.
	// Sometimes, however, it uses the other_system_key (from the
	// recombinase_other_system table) and the system_key (from the
	// recombinase_other_allele table), both of which identify the system
	// in the 'term' table.  These links were not working and needed a
	// quick fix before our release.

	String alleleID = request.getParameter("id");

        AlleleSystem alleleSystem = null;
        Allele allele = null;
	boolean foundMatch = false;

	// If we found a match with our 'systemKey', is it for the expected
	// allele?  If so, we're all set.
	if (alleleSystems.size() == 1) {
        	alleleSystem = alleleSystems.get(0);
        	allele = alleleSystem.getAllele();

		if (allele.getPrimaryID().equals(alleleID)) {
			foundMatch = true;
		} 
	}
	
	// If we didn't find an alleleSystem or if we didn't find one with the
	// right allele, then assume that the 'systemKey' refers to an
	// 'otherSystemKey' and try that route.
	if (!foundMatch) {
		results = recombinaseFinder.getAlleleSystems(alleleID, alleleSystemKey);
		alleleSystems = results.getResultObjects();

		if (alleleSystems.size() == 1) {
        		alleleSystem = alleleSystems.get(0);
        		allele = alleleSystem.getAllele();
			foundMatch = true;
		}
	}

	if (!foundMatch) {
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
	        List<Image> validatedImages = new ArrayList<Image>();

	        for(Image image : alleleSystem.getImages()) {
	          if (image.getHeight() != null && image.getWidth() != null) {
				validatedImages.add(image);
			  }
		    }

	        // iterate over the validated images; pre-gen image gallery rows
	        int imageIndex = 0;
	        List<RecomImage> recomImages = new ArrayList<RecomImage>();
	        List<RecomImageRow> recomImageRows = new ArrayList<RecomImageRow>();
	        for(Image image : validatedImages) {
	          imageIndex++;
	          RecomImage recomImage = new RecomImage(image, imageIndex);
	          recomImages.add(recomImage);

	          // if we have enough images to fill a row, of if this is our last
	          // image, create the row and add to row list
	          if ( ((imageIndex % 8 ) == 0) || imageIndex == validatedImages.size()) {
	            RecomImageRow row = new RecomImageRow();
	            row.setRecomImages(recomImages);
	            recomImageRows.add(row);
	            recomImages = new ArrayList<RecomImage>();
	          }
	        }
	        mav.addObject("galleryImagesRows", recomImageRows);
        }
        return mav;
    }



    //-------------------------//
    // JSON Specificity results
    //-------------------------//
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
        params.setSorts(genRecomSummarySorts(request));
        params.setFilter(genFilters(query));

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

    //-------------------------------//
    // Recombinase/Allele
    //-------------------------------//

    @RequestMapping("/allele/{allID}")
    public ModelAndView recombAlleleSystemsTable( HttpServletRequest request,
    		HttpServletResponse response,
            @PathVariable("allID") String allID) {

        logger.debug("->recombAlleleSystemsTable() started");

        // need to add headers to allow AJAX access
        AjaxUtils.prepareAjaxHeaders(response);

        ModelAndView mav = new ModelAndView("recombinase_table");

    	// find the requested Allele
        logger.debug("->asking alleleFinder for allele");
    	List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
    	// there can be only one...
        if (alleleList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No allele found for " + allID);
            return mav;
        }
        if (alleleList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + allID);
            return mav;
        }
        Allele allele = alleleList.get(0);
        logger.debug("->1 allele found");
        mav.addObject("allele",allele);

        List<AlleleSystem> alleleSystems =
          allele.getAlleleSystems();
        logger.debug("->List<AlleleSystem> size - " + alleleSystems.size());

        mav.addObject("alleleSystems",alleleSystems);

        return mav;
    }


    /*---------------------------------------------------------------------*/
    /* Facets for filters                                                  */
    /*---------------------------------------------------------------------*/

	// driver
	@RequestMapping("/facet/driver")
	public @ResponseBody Map<String, List<String>> facetDriver (@ModelAttribute RecombinaseQueryForm qf, HttpServletResponse response) {

		logger.debug(qf.toString());

		AjaxUtils.prepareAjaxHeaders(response);

		// setup SearchParams & SearchResults
		SearchParams params = new SearchParams();
		params.setFilter(parseRecombinaseQueryForm(qf));

		SearchResults<RecombinaseEntity> facetResults = recombinaseFinder.getDriverFacet(params);

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		if (facetResults.getResultFacets().size() == 0) {
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			m.put("resultFacets", facetResults.getSortedResultFacets());
		}

		return m;
	}

	// inducer
	@RequestMapping("/facet/inducer")
	public @ResponseBody Map<String, List<String>> facetInducer (@ModelAttribute RecombinaseQueryForm qf, HttpServletResponse response) {

		logger.debug(qf.toString());

		AjaxUtils.prepareAjaxHeaders(response);

		// setup SearchParams & SearchResults
		SearchParams params = new SearchParams();
		params.setFilter(parseRecombinaseQueryForm(qf));

		SearchResults<RecombinaseEntity> facetResults = recombinaseFinder.getInducerFacet(params);

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		if (facetResults.getResultFacets().size() == 0) {
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			m.put("resultFacets", facetResults.getSortedResultFacets());
		}

		return m;
	}

	// system detected
	@RequestMapping("/facet/systemDetected")
	public @ResponseBody Map<String, List<String>> facetSystemDetected (@ModelAttribute RecombinaseQueryForm qf, HttpServletResponse response) {

		logger.debug(qf.toString());

		AjaxUtils.prepareAjaxHeaders(response);

		// setup SearchParams & SearchResults
		SearchParams params = new SearchParams();
		params.setFilter(parseRecombinaseQueryForm(qf));

		SearchResults<RecombinaseEntity> facetResults = recombinaseFinder.getSystemDetectedFacet(params);

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		if (facetResults.getResultFacets().size() == 0) {
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			m.put("resultFacets", facetResults.getSortedResultFacets());
		}

		return m;
	}

	// system not detected
	@RequestMapping("/facet/systemNotDetected")
	public @ResponseBody Map<String, List<String>> facetSystemNotDetected (@ModelAttribute RecombinaseQueryForm qf, HttpServletResponse response) {

		logger.debug(qf.toString());

		AjaxUtils.prepareAjaxHeaders(response);

		// setup SearchParams & SearchResults
		SearchParams params = new SearchParams();
		params.setFilter(parseRecombinaseQueryForm(qf));

		SearchResults<RecombinaseEntity> facetResults = recombinaseFinder.getSystemNotDetectedFacet(params);

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		if (facetResults.getResultFacets().size() == 0) {
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			m.put("resultFacets", facetResults.getSortedResultFacets());
		}

		return m;
	}


    /*---------------------------------------------------------------------*/
    /* private instance methods                                            */
    /*---------------------------------------------------------------------*/

    private List<Sort> parseSummarySorts(HttpServletRequest request) {

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

			// remove starting comma - may be submitted with driver filter
			driver = driver.startsWith(",") ? driver.substring(1) : driver;

			List<String> drivers = new ArrayList<String>();
			for (String driverToken : driver.split(",")) {
	        	logger.debug("---->" + driverToken);
				drivers.add(driverToken);
			}
			filterList.add(new Filter(SearchConstants.ALL_DRIVER,
					drivers, Filter.Operator.OP_IN));
        }

        // build system query filter
        String system = query.getSystem();
        if ((system != null) && (!"".equals(system))) {
            filterList.add(new Filter (SearchConstants.ALL_SYSTEM, system,
                Filter.Operator.OP_EQUAL));
        }

        // Structure queries
        String structure = query.getStructure();

        if ((structure != null) && (!"".equals(structure))) {
        	logger.debug("splitting structure query into tokens");
			Collection<String> structureTokens = QueryParser.parseNomenclatureSearch(structure);

			String phraseSearch = "";
			for(String structureToken : structureTokens)
			{
				logger.debug("token="+structureToken);
				phraseSearch += structureToken+" ";
			}
			if(!phraseSearch.trim().equals(""))
			{
				// surround with double quotes to make a solr phrase. added a slop of 100 (longest name is 62 chars)
				String sToken = "\""+phraseSearch+"\"~100";
				filterList.add(new Filter(SearchConstants.CRE_STRUCTURE ,sToken,Filter.Operator.OP_HAS_WORD));
			}
        }

        // detected / not detected
        String detected = query.getDetected();
        String notDetected = query.getNotDetected();
        // detected and notDetected must be different to create a valid filter
        // if both are null, or both are true, then we do not filter results
        if( "true".equalsIgnoreCase(detected) && !detected.equalsIgnoreCase(notDetected) ) {
        	// detected only
        	filterList.add(new Filter(SearchConstants.CRE_DETECTED, "true", Filter.Operator.OP_HAS_WORD));
        }
        else if( "true".equalsIgnoreCase(notDetected) && !notDetected.equalsIgnoreCase(detected) ) {
        	// absent only

        	// Combine "no presents query" with "must have one absent query"
        	Filter notPresentFilter = new Filter(SearchConstants.CRE_DETECTED, "true", Filter.Operator.OP_HAS_WORD);
        	notPresentFilter.negate();
        	Filter absentFilter = new Filter(SearchConstants.CRE_DETECTED, "false", Filter.Operator.OP_HAS_WORD);

        	filterList.add(Filter.and(Arrays.asList(notPresentFilter, absentFilter)));
        }

        // inducer
		if (query.getInducer().size() > 0){
			filterList.add(new Filter(SearchConstants.CRE_INDUCER,
					query.getInducer(), Filter.Operator.OP_IN));
		}

        // detected in system
		if (query.getSystemDetected().size() > 0){
			filterList.add(new Filter(SearchConstants.CRE_SYSTEM_DETECTED,
					query.getSystemDetected(), Filter.Operator.OP_IN));
		}

        // not detected in system
		if (query.getSystemNotDetected().size() > 0){
			filterList.add(new Filter(SearchConstants.CRE_SYSTEM_NOT_DETECTED,
					query.getSystemNotDetected(), Filter.Operator.OP_IN));
		}

       	// return everything for empty query
        if (filterList.size() == 0) {
        	filterList.add(new Filter(SearchConstants.ALL_DRIVER,"[* TO *]",Filter.Operator.OP_HAS_WORD));
        }

        // build container filter and return
        Filter containerFilter = new Filter();
        containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
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
                Filter.Operator.OP_EQUAL));
        }
        if ((system != null) && (!"".equals(system))) {
            filterList.add(new Filter (SearchConstants.ALL_SYSTEM, system,
                Filter.Operator.OP_EQUAL));
        }
        if ((id != null) && (!"".equals(id))) {
            filterList.add(new Filter (SearchConstants.ALL_ID, id,
                Filter.Operator.OP_EQUAL));
        }
        if ((systemKey != null) && (!"".equals(systemKey))) {
            filterList.add(new Filter (SearchConstants.CRE_SYSTEM_KEY, systemKey,
                Filter.Operator.OP_EQUAL));
        }

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }

        logger.debug("genFilters -> " + containerFilter);
        return containerFilter;
    }

    private boolean empty(String s) {
    	return s == null || "".equals(s);
    }
}
