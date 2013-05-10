package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

// MGI classes
import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.GxdAssayResult;
import mgi.frontend.datamodel.GxdMarker;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.apache.commons.collections.set.ListOrderedSet;
import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.finder.GxdFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.MarkerBatchIDFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.forms.MarkerAnnotationQueryForm;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.hunter.SolrMarkerKeyHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.summary.GxdAssaySummaryRow;
import org.jax.mgi.fewi.summary.GxdCountsSummary;
import org.jax.mgi.fewi.summary.GxdImageSummaryRow;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.ReferenceSummary;
import org.jax.mgi.fewi.util.Highlighter;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.fewi.util.StyleAlternator;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;

// external classes
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * GXDController This controller handles the Gene Expression Data
 * (GXD) search form and result summaries.
 */


/*
 * This controller maps all /gxd/ uri's
 */
@Controller
@RequestMapping(value = "/gxd")
public class GXDController {

	// --------------------//
	// instance variables
	// --------------------//

	private Logger logger = LoggerFactory.getLogger(GXDController.class);

	@Autowired
    private SolrMarkerKeyHunter mrkKeyHunter;
	
	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired AlleleFinder alleleFinder;

	@Autowired
	private GxdFinder gxdFinder;

	@Autowired
	private GXDLitController gxdLitController;


	// -----------------------------------------------------------------//
	// public methods mapped to URLs
	// -----------------------------------------------------------------//


	/*
	 * GXD Query Form
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getQueryForm() {

		logger.debug("->getQueryForm started");

		ModelAndView mav = new ModelAndView("gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		
		return mav;
	}

	// "expanded" query form
	@RequestMapping("differential")
	public ModelAndView getDifferentialQueryForm() {

		logger.debug("->getQueryForm started");

		ModelAndView mav = new ModelAndView("gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showDifferentialQueryForm",true);
		
		return mav;
	}

	/*
	 * report
	 */
    @RequestMapping("/report*")
    public ModelAndView resultsSummaryExport(
            HttpServletRequest request,
			@ModelAttribute GxdQueryForm query) {

    	logger.debug("generating report");
    	// build a batch finder object and pass it to the view for iteration
		Filter qf = this.parseGxdQueryForm(query);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		GxdBatchFinder batchFinder = new GxdBatchFinder(gxdFinder,sp);

		logger.debug("routing to view object");
		ModelAndView mav = new ModelAndView("gxdResultsSummaryReport");
		mav.addObject("resultFinder", batchFinder);
		return mav;

    }

    /*
     * generic summary report
     */
    @RequestMapping("/summary")
    public ModelAndView genericSummary(
		    @ModelAttribute GxdQueryForm query,
            HttpServletRequest request) {

    	logger.debug("generating generic GXD summary");
		logger.debug("query string: " + request.getQueryString());
		logger.debug("query form: " + query);

		ModelAndView mav = new ModelAndView("gxd_generic_summary");
		mav.addObject("queryString", request.getQueryString());
		return mav;

    }
    /*
     * report by markers
     */
    @RequestMapping("marker/report*")
    public ModelAndView resultsMarkerSummaryExport(
            HttpServletRequest request,
			@ModelAttribute GxdQueryForm query) {

    	logger.debug("generating GXD marker report");
    	// build a batch finder object and pass it to the view for iteration
		Filter qf = this.parseGxdQueryForm(query);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		// use gene symbol sort
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(SortConstants.GXD_GENE));
		sp.setSorts(sorts);

		GxdBatchFinder batchFinder = new GxdBatchFinder(gxdFinder,sp);

		logger.debug("routing to view object");
		ModelAndView mav = new ModelAndView("gxdMarkersSummaryReport");
		mav.addObject("markerFinder", batchFinder);
		return mav;

    }


	/*
	 * Summary by Reference
	 */
    @RequestMapping(value="/reference/{refID}")
    public ModelAndView summeryByRefId(
		  HttpServletRequest request,
		  @PathVariable("refID") String refID) {

        logger.debug("->summeryByRefId started");

        // setup view object
        ModelAndView mav = new ModelAndView("gxd_summary_by_reference");

		// setup search parameters object to gather the requested marker
        SearchParams referenceSearchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        referenceSearchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults<Reference> referenceSearchResults
          = referenceFinder.searchReferences(referenceSearchParams);
        List<Reference> referenceList = referenceSearchResults.getResultObjects();

        // there can be only one...
        if (referenceList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refID);
            return mav;
        }
        if (referenceList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + refID);
            return mav;
        }
        Reference reference = referenceList.get(0);
        mav.addObject("reference", reference);

        logger.debug("summeryByRefId routing to view ");
		return mav;
    }


    /*
	 * Summary by Allele
	 */
    @RequestMapping(value="/allele/{allID}")
    public ModelAndView summeryByAllId(
		  HttpServletRequest request,
		  @PathVariable("allID") String allID) {

        logger.debug("->summeryByallId started");

        // setup view object
        ModelAndView mav = new ModelAndView("gxd_summary_by_allele");


		// setup search parameters object to gather the requested marker
        SearchParams alleleSearchParams = new SearchParams();
        Filter alleleIdFilter = new Filter(SearchConstants.ALL_ID, allID);
        alleleSearchParams.setFilter(alleleIdFilter);

        // find the requested marker
        SearchResults<Allele> alleleSearchResults
          = alleleFinder.getAlleleByID(alleleSearchParams);

        List<Allele> alleleList = alleleSearchResults.getResultObjects();
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
        mav.addObject("allele", allele);

        logger.debug("summeryByAllId routing to view ");
		return mav;
    }


	/*
	 * Summary by Marker
	 */
    @RequestMapping(value="/marker/{mrkID}")
    public ModelAndView summeryByMrkId(
		  HttpServletRequest request,
		  @PathVariable("mrkID") String mrkID) {

        logger.debug("->summeryByMrkId started");

        // setup view object
        ModelAndView mav = new ModelAndView("gxd_summary_by_marker");

		// setup search parameters object to gather the requested marker
        SearchParams markerSearchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, mrkID);
        markerSearchParams.setFilter(markerIdFilter);

        // find the requested marker
        SearchResults<Marker> markerSearchResults
          = markerFinder.getMarkerByID(markerSearchParams);
        List<Marker> markerList = markerSearchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No marker found for " + mrkID);
            return mav;
        }
        if (markerList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe marker ID found for " + mrkID);
            return mav;
        }
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

		// handle requests for a specific Theiler Stage
        String theilerStage = request.getParameter("theilerStage");
        if (theilerStage != null) {
			mav.addObject("theilerStage", theilerStage);
		}else {
			mav.addObject("theilerStage", "");
		}
        
        // handle requests for a specific summary tab
        String tab = request.getParameter("tab");
        if(tab != null) mav.addObject("tab", tab);

		// handle requests for a specific assay type
        String assayType = request.getParameter("assayType");
        if (assayType != null) {
			mav.addObject("assayType", assayType);
		}else {
			mav.addObject("assayType", "");
		}


        logger.debug("summeryByMrkId routing to view ");
		return mav;
    }


	/*
	 * Forward to Batch Query
	 */
    @RequestMapping(value="/batch")
    public String forwardToBatch(
		HttpServletRequest request,
		@ModelAttribute GxdQueryForm query) {

		logger.debug("forwarding gxd markers to batch");



		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setFilter(this.parseGxdQueryForm(query));

		GxdBatchFinder gxdBatchFinder = new GxdBatchFinder(gxdFinder,params);
		gxdBatchFinder.batchSize = 5000;

		// gather marker id strings in batches
		StringBuffer ids = new StringBuffer();
		while(gxdBatchFinder.hasNextMarkers())
		{
			SearchResults<SolrGxdMarker> searchResults = gxdBatchFinder.getNextMarkers();
			//add each marker ID to the ids list
	        for (SolrGxdMarker solrMarker : searchResults.getResultObjects()) {
				ids.append(solrMarker.getMgiid() + ", ");
			}
		}
		// setup batch query object
		BatchQueryForm batchQueryForm = new BatchQueryForm();
		batchQueryForm.setIds(ids.toString());

		request.setAttribute("queryForm", batchQueryForm);

		return "forward:/mgi/batch/forwardSummary";
    }


	// -----------------------------------------------------------------//
	// Methods for gathering data returned in JSON format
	// -----------------------------------------------------------------//


	/*
	 * This method maps ajax requests from the reference summary page.  It
	 * parses the ReferenceQueryForm, generates SearchParams object, and issues
	 * the query to the ReferenceFinder.  The results are returned as JSON
	 */
	@RequestMapping("/markers/json")
	public @ResponseBody JsonSummaryResponse<GxdMarkerSummaryRow> gxdMarkerSummaryJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdMarkerSummaryJson() started");
		SearchResults<SolrGxdMarker> searchResults = this.getGxdMarkerResults(request, query, page, result);
        List<SolrGxdMarker> markerList = searchResults.getResultObjects();

        List<GxdMarkerSummaryRow> summaryRows = new ArrayList<GxdMarkerSummaryRow>();
        GxdMarkerSummaryRow row;
        MetaData rowMeta;

        for (SolrGxdMarker marker : markerList) {
			if (marker != null){
				row = new GxdMarkerSummaryRow(marker);
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
        JsonSummaryResponse<GxdMarkerSummaryRow> jsonResponse
        		= new JsonSummaryResponse<GxdMarkerSummaryRow>();
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        
        logger.debug("gxdMarkersSummaryJson() found "+searchResults.getTotalCount()+" markers");

        return jsonResponse;
	}

	/*
	 * This method maps ajax requests from the reference summary page.  It
	 * parses the ReferenceQueryForm, generates SearchParams object, and issues
	 * the query to the ReferenceFinder.  The results are returned as JSON
	 */
	@RequestMapping("/assays/json")
	public @ResponseBody JsonSummaryResponse<GxdAssaySummaryRow> gxdAssaySummaryJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdAssaySummaryJson() started");
		SearchResults<SolrGxdAssay> searchResults = this.getGxdAssays(request, query, page, result);
        List<SolrGxdAssay> assayList = searchResults.getResultObjects();

        List<GxdAssaySummaryRow> summaryRows = new ArrayList<GxdAssaySummaryRow>();
        GxdAssaySummaryRow row;
        MetaData rowMeta;

        for (SolrGxdAssay assay : assayList) {
			if (assay != null){
				row = new GxdAssaySummaryRow(assay);
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
        JsonSummaryResponse<GxdAssaySummaryRow> jsonResponse
        		= new JsonSummaryResponse<GxdAssaySummaryRow>();
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        logger.debug("gxdAssaySummaryJson() found "+searchResults.getTotalCount()+" assays");

        return jsonResponse;
	}

	/*
	 * This method maps ajax requests from the reference summary page.  It
	 * parses the ReferenceQueryForm, generates SearchParams object, and issues
	 * the query to the ReferenceFinder.  The results are returned as JSON
	 */
	@RequestMapping("/results/json")
	public @ResponseBody JsonSummaryResponse<GxdAssayResultSummaryRow> gxdResultsSummaryJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdResultsSummaryJson() started");
		logger.debug("querystring: " + request.getQueryString());
		SearchResults<SolrAssayResult> searchResults = this.getGxdAssayResults(request, query, page, result);

        List<SolrAssayResult> resultList = searchResults.getResultObjects();

        List<GxdAssayResultSummaryRow> summaryRows = new ArrayList<GxdAssayResultSummaryRow>();
        GxdAssayResultSummaryRow row;
        MetaData rowMeta;

        for (SolrAssayResult gxdAssayResult : resultList) {
			if (gxdAssayResult != null){
				row = new GxdAssayResultSummaryRow(gxdAssayResult);
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
        JsonSummaryResponse<GxdAssayResultSummaryRow> jsonResponse
        		= new JsonSummaryResponse<GxdAssayResultSummaryRow>();
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());

        logger.debug("gxdResultsSummaryJson() found "+searchResults.getTotalCount()+" results");
        return jsonResponse;
	}
	
	@RequestMapping("/images/json")
	public @ResponseBody JsonSummaryResponse<GxdImageSummaryRow> gxdImageSummaryJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdImageSummaryJson() started");
		SearchResults<SolrGxdImage> searchResults = this.getGxdImages(request, query, page, result);
		List<SolrGxdImage> imageList = searchResults.getResultObjects();
        //List<SolrGxdAssay> assayList = searchResults.getResultObjects();

        List<GxdImageSummaryRow> summaryRows = new ArrayList<GxdImageSummaryRow>();
        MetaData rowMeta;

        for (SolrGxdImage image : imageList) {
			if (image != null){
		        GxdImageSummaryRow row = new GxdImageSummaryRow(image);
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
        JsonSummaryResponse<GxdImageSummaryRow> jsonResponse
        		= new JsonSummaryResponse<GxdImageSummaryRow>();
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        logger.debug("gxdImageSummaryJson() found "+searchResults.getTotalCount()+" images");

        return jsonResponse;
	}

	// -----------------------------------------------------------------//
	// Methods for getting query counts
	// -----------------------------------------------------------------//

	@RequestMapping("/markers/totalCount")
	public @ResponseBody Integer getGxdMarkerCount(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(this.parseGxdQueryForm(query));
		params.setPageSize(0);
		return gxdFinder.getMarkerCount(params);
	}
	@RequestMapping("/assays/totalCount")
	public @ResponseBody Integer getGxdAssayCount(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(this.parseGxdQueryForm(query));
		params.setPageSize(0);

		return gxdFinder.getAssayCount(params);
	}
	@RequestMapping("/results/totalCount")
	public @ResponseBody Integer getGxdResultCount(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(this.parseGxdQueryForm(query));
		params.setPageSize(0);

		return gxdFinder.getAssayResultCount(params);
	}
	@RequestMapping("/images/totalCount")
	public @ResponseBody Integer getGxdImageCount(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(this.parseGxdQueryForm(query));
		params.setPageSize(0);

		return gxdFinder.getImageCount(params);
	}

	@RequestMapping("/totalCounts")
	public @ResponseBody GxdCountsSummary getGxdCounts(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		// TODO: figure out how to speed up the performance
		SearchParams params = new SearchParams();
		params.setFilter(this.parseGxdQueryForm(query));
		// we want to get these counts as fast as possible
		//params.setSorts(new ArrayList<Sort>(0));
		params.setPageSize(0);

		GxdCountsSummary countsSummary = new GxdCountsSummary();
		countsSummary.setResultsCount(gxdFinder.getAssayResultCount(params));
		//countsSummary.setAssaysCount(gxdFinder.getAssayCount(params));
		countsSummary.setGenesCount(gxdFinder.getMarkerCount(params));

		return countsSummary;
	}

	/*
	 * Returns the count form the GXD Lit index
	 */
	@RequestMapping("/gxdLitCount")
	public @ResponseBody Integer getGxdLitCount(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		GxdLitQueryForm gxdLitForm = mapGxdLitQuery(query);
		// a value of 0 is still meaningful, so return -1 as an indicator that the query parameters submitted do not apply to the GXD Lit Form
		if (gxdLitForm == null) return -1;
		return gxdLitController.getGxdLitCount(gxdLitForm);
	}

	/*
	 * Forward query to the gxdLitSummary
	 */
	@RequestMapping("/gxdLitForward")
	public String getGxdLitForward(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		GxdLitQueryForm gxdLitForm = mapGxdLitQuery(query);
		if(gxdLitForm == null) gxdLitForm = new GxdLitQueryForm();
		request.setAttribute("gxdLitQueryForm", gxdLitForm);
		return "forward:/mgi/gxdlit/forward/summary";
	}




	// -----------------------------------------------------------------//
	// private methods
	// -----------------------------------------------------------------//

	/*
	 * TS Mapping
	 */
	private static Map<Integer,List<String>> gxdLitTSMap = new HashMap<Integer,List<String>>();
	static
	{
		gxdLitTSMap.put(1,Arrays.asList("0.5","1","1.5","2","2.5"));
		gxdLitTSMap.put(2,Arrays.asList("1","1.5","2","2.5"));
		gxdLitTSMap.put(3,Arrays.asList("1","1.5","2","2.5","3","3.5"));
		gxdLitTSMap.put(4,Arrays.asList("2","2.5","3","3.5","4"));
		gxdLitTSMap.put(5,Arrays.asList("3","3.5","4","4.5","5","5.5"));
		gxdLitTSMap.put(6,Arrays.asList("4","4.5","5","5.5"));
		gxdLitTSMap.put(7,Arrays.asList("4.5","5","5.5","6"));
		gxdLitTSMap.put(8,Arrays.asList("5","5.5","6","6.5"));
		gxdLitTSMap.put(9,Arrays.asList("6.5","7","7.5"));
		gxdLitTSMap.put(10,Arrays.asList("6.5","7","7.5","8"));
		gxdLitTSMap.put(11,Arrays.asList("7.5","8"));
		gxdLitTSMap.put(12,Arrays.asList("7.5","8","8.5","9"));
		gxdLitTSMap.put(13,Arrays.asList("8","8.5","9","9.5"));
		gxdLitTSMap.put(14,Arrays.asList("8.5","9","9.5","10"));
		gxdLitTSMap.put(15,Arrays.asList("9","9.5","10","10.5"));
		gxdLitTSMap.put(16,Arrays.asList("9.5","10","10.5","11"));
		gxdLitTSMap.put(17,Arrays.asList("10","10.5","11","11.5"));
		gxdLitTSMap.put(18,Arrays.asList("10.5","11","11.5"));
		gxdLitTSMap.put(19,Arrays.asList("11","11.5","12","12.5"));
		gxdLitTSMap.put(20,Arrays.asList("11.5","12","12.5","12.5","13"));
		gxdLitTSMap.put(21,Arrays.asList("12.5","13","13.5","14"));
		gxdLitTSMap.put(22,Arrays.asList("13.5","14","14.5","15"));
		gxdLitTSMap.put(23,Arrays.asList("15","15.5","16"));
		gxdLitTSMap.put(24,Arrays.asList("16","16.5","17"));
		gxdLitTSMap.put(25,Arrays.asList("17","17.5","18"));
		gxdLitTSMap.put(26,Arrays.asList("18","18.5","19","19.5","20"));
		gxdLitTSMap.put(28,Arrays.asList("A")); //GXD Lit Option for postnatal
	}
	private static Map<String,List<String>> gxdLitAssayTypeMap = new HashMap<String,List<String>>();
	static
	{
		// Only assay type mappings that are not an exact 1:1 are defined
		gxdLitAssayTypeMap.put("Immunohistochemistry", Arrays.asList("In situ protein (section)","In situ protein (whole mount)"));
		gxdLitAssayTypeMap.put("RNA in situ", Arrays.asList("In situ RNA (section)","In situ RNA (whole mount)"));
	}


	/*
	 * Constructs a GxdLitQueryForm by mapping parameters from the GXD query form
	 */
	private GxdLitQueryForm mapGxdLitQuery(GxdQueryForm query) {

		//logger.debug("GXDQueryForm = "+query.toString());
		//check form to see if any non-valid  fields are in it.
		// UPDATE THIS IF NEW FIELDS ARE ADDED TO FORM
		if (!query.getDetected().equals(GxdQueryForm.ANY_DETECTED)
				|| query.getIsWildType().equals("true")
				|| (query.getMutatedIn()!=null && !query.getMutatedIn().equals(""))
				|| (query.getStructure()!=null && !query.getStructure().equals(""))
				|| (query.getStructureKey()!=null && !query.getStructureKey().equals(""))
				|| (query.getStructureID()!=null && !query.getStructureID().equals(""))
				|| (query.getAnnotatedStructureKey()!=null && !query.getAnnotatedStructureKey().equals(""))
				|| (query.getJnum()!=null && !query.getJnum().equals(""))
				|| (query.getProbeKey()!=null && !query.getProbeKey().equals(""))
				|| (query.getAntibodyKey()!=null && !query.getAntibodyKey().equals(""))
				|| isDifferentialQuery(query))
		{
			logger.debug("Form fields have been entered that do not apply to gxd lit query.");
			// return null if we have no valid mapped query to make
			return null;
		}

		boolean setLitQuery = false;

		GxdLitQueryForm gxdLitForm = new GxdLitQueryForm();
		// Only valid fields are Gene MGI ID, Nomenclature, Vocab Query, TS, Age, and Assay Type
		String nomenclature = query.getNomenclature().trim();
		if(nomenclature != null && !nomenclature.equals(""))
		{
			setLitQuery=true;
			gxdLitForm.setNomen(nomenclature);
		}

		String markerMgiid = query.getMarkerMgiId().trim();
		if(markerMgiid!=null && !markerMgiid.equals(""))
		{
			setLitQuery=true;
			gxdLitForm.setMarkerId(markerMgiid);
		}

		// Map annotationID to multiple marker IDs
		String annotationID = query.getAnnotationId().trim();
		if(annotationID!=null && !annotationID.equals(""))
		{
			setLitQuery=true;
			gxdLitForm.setTermId(annotationID);
			// let's just assume that the vocab term string is set if we have an annotation term ID
			gxdLitForm.setVocabTerm(query.getVocabTerm());
		}

		List<Integer> stages = query.getTheilerStage();
		if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
		{
			setLitQuery=true;
			// use an ordered set, because this will be the order displayed on the summary output
			Set<String> mappedAges = new LinkedHashSet<String>();
			// Translate Theiler Stages to age ranges
			for(Integer stage:stages)
			{
				if(gxdLitTSMap.containsKey(stage))
				{
					// get the special mapping if needed
					for(String mappedAge : gxdLitTSMap.get(stage))
					{
						mappedAges.add(mappedAge);
					}
				}
				else
				{
					logger.info("error: TS"+stage+" has no mapping to Age for GXD Lit Query");
				}
			}
			gxdLitForm.setAge(new ArrayList<String>(mappedAges));
		}

		List<String> ages = query.getAge();
		if(ages.size() > 0 && !ages.contains(GxdQueryForm.ANY_AGE))
		{
			setLitQuery=true;
			Set<String> mappedAges = new LinkedHashSet<String>();
			for(String age:ages)
			{
				// GXD Lit represents Postnatal (Adult) as A, and Embryonic as E
				if(age.equalsIgnoreCase(GxdQueryForm.POSTNATAL))
				{
					mappedAges.add("A");
				}
				else if(age.equalsIgnoreCase(GxdQueryForm.EMBRYONIC))
				{
					// Need to select every age except A (and ANY)
					// loop through all the defaults and exclude the options we don't want
					for(String mappedAge : gxdLitForm.getAges().keySet())
					{
						if(!mappedAge.equals("ANY") && !mappedAge.equals("A"))
						{
							mappedAges.add(mappedAge);
						}
					}
				}
				else
				{
					mappedAges.add(age);
				}
			}
			logger.debug("ages = "+StringUtils.join(mappedAges,","));
			// set ages on gxd lit form
			gxdLitForm.setAge(new ArrayList<String>(mappedAges));
		}

		List<String> assayTypes = query.getAssayType();
		if(assayTypes.size() > 0 && assayTypes.size()<query.getAssayTypes().size())
		{
			setLitQuery=true;
			List<String> gxdLitAssayTypes = new ArrayList<String>();
			//Map GXD Assay types to GXD Lit Assay types
			for(String assayType:assayTypes)
			{
				if(gxdLitAssayTypeMap.containsKey(assayType))
				{
					// get the special mapping if needed
					for(String gxdLitAssayType : gxdLitAssayTypeMap.get(assayType))
					{
						gxdLitAssayTypes.add(gxdLitAssayType);
					}
				}
				else
				{
					gxdLitAssayTypes.add(assayType);
				}
			}
			gxdLitForm.setAssayType(gxdLitAssayTypes);
		}
		if(setLitQuery)
		{
			return gxdLitForm;
		}
		// return null if we have no valid mapped query to make
		return null;
	}

	/*
	 * Returns whether or not this query form has differential query params
	 */
	private boolean isDifferentialQuery(GxdQueryForm query)
	{
		return (query.getDifStructure()!=null && !query.getDifStructure().equals(""))
				|| 
				(query.getDifTheilerStage().size() > 0);
	}
	
	/*
	 * Creates the differental part 1 filters, 
	 * 	performs the query against gxdDifferentialMarker index
	 * 	and returns the unique marker keys returned
	 */
	private List<String> resolveDifferentialMarkers(GxdQueryForm query)
	{
		// start filter list for query filters
		List<Filter> queryFilters = new ArrayList<Filter>();

		// init form fields
		String structure = query.getStructure();
		String difStructure = query.getDifStructure();
		List<Integer> stages = query.getTheilerStage();
		List<Integer> difStages = query.getDifTheilerStage();
		
		// figure out what kind of diff query this is
		boolean hasStructures = (structure!=null && !structure.equals("")
				&& difStructure!=null && !difStructure.equals(""));
		boolean hasStages = (stages.size() > 0 && difStages.size()>0
				&& !(stages.contains(GxdQueryForm.ANY_STAGE) && difStages.contains(GxdQueryForm.ANY_STAGE_NOT_ABOVE)));
		
		// perform structure diff
		if(hasStructures && !hasStages)
		{
			Filter sFilter = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,structure);
			Filter dsFilter = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,difStructure);
			dsFilter.negate();
			queryFilters.add(sFilter);
			queryFilters.add(dsFilter);
		}
		// perform stages diff
		else if(hasStages && !hasStructures)
		{
			if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
			{
				List<Filter> stageFilters = new ArrayList<Filter>();
				for(Integer stage : stages)
				{
					stageFilters.add(new Filter(SearchConstants.POS_STRUCTURE,"TS"+stage,Filter.OP_HAS_WORD));
				}
				// OR the stages together
				queryFilters.add(Filter.or(stageFilters));
			}
			List<Filter> dStageFilters = new ArrayList<Filter>();
			for(Integer dStage : query.getResolvedDifTheilerStage())
			{
				dStageFilters.add(new Filter(SearchConstants.POS_STRUCTURE,"TS"+dStage,Filter.OP_HAS_WORD));
			}
			Filter dStageFilter = Filter.or(dStageFilters);
			dStageFilter.negate();
			queryFilters.add(dStageFilter);
		}
		// perform both structure and stage diff
		else if(hasStructures && hasStages)
		{
			// stub for 3rd differential ribbon logic
		}
		

		
		Filter difFilter = new Filter();
		if(queryFilters.size() > 0)
		{
			difFilter.setNestedFilters(queryFilters,Filter.FC_AND);
		}		
		else return null;
		
		
		SearchParams difSP = new SearchParams();
		difSP.setFilter(difFilter);
		
		return gxdFinder.searchDifferential(difSP);
	}
	/*
	 * Creates the differential part 2 filter that goes against the gxdResult index
	 */
	public Filter makeDifferentialPart2Filter(GxdQueryForm query)
	{
		ArrayList<Filter> queryFilters = new ArrayList<Filter>();
		// init form fields
		String structure = query.getStructure();
		String difStructure = query.getDifStructure();
		List<Integer> stages = query.getTheilerStage();
		List<Integer> difStages = query.getDifTheilerStage();
		
		// figure out what kind of diff query this is
		boolean hasStructures = (structure!=null && !structure.equals("")
				&& difStructure!=null && !difStructure.equals(""));
		boolean hasStages = (stages.size() > 0 && difStages.size()>0
				&& !(stages.contains(GxdQueryForm.ANY_STAGE) && difStages.contains(GxdQueryForm.ANY_STAGE_NOT_ABOVE)));
		
		// perform structure diff
		if(hasStructures && !hasStages)
		{
			// create the positive results filter
			List<Filter> posFilters = new ArrayList<Filter>();
			posFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE,structure));
			posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.OP_EQUAL));
			queryFilters.add(Filter.and(posFilters));
		}
		// perform stages diff
		else if(hasStages && !hasStructures)
		{
			// create the positive results filter
			List<Filter> posFilters = new ArrayList<Filter>();
			posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.OP_EQUAL));
			if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
			{
				List<Filter> stageFilters = new ArrayList<Filter>();
				for(Integer stage : stages)
				{
					Filter stageF = new Filter(SearchConstants.GXD_THEILER_STAGE,stage,Filter.OP_HAS_WORD);
					stageFilters.add(stageF);

				}
				// OR the stages together
				posFilters.add(Filter.or(stageFilters));
			}
			queryFilters.add(Filter.and(posFilters));
		}
		else if(hasStructures && hasStages)
		{
			// stub for 3rd differential ribbon logic
		}
		
		// all results MUST be wild type (broad definition)
		queryFilters.add(new Filter(SearchConstants.GXD_IS_WILD_TYPE, "true"));
		
		return Filter.and(queryFilters);
	}
	
	/*
	 * Factored out building the anatomy style filter, because 1) it is complicated
	 * 	and 2) it is used in many places
	 */
	public Filter makeStructureSearchFilter(String queryField, String structure)
	{
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
			return new Filter(queryField,sToken,Filter.OP_HAS_WORD);
		}
		return null;
	}
	
	/*
	 * This method parses the GxdQueryForm bean and constructs a Filter
	 * object to represent the query.
	 */
	private Filter parseGxdQueryForm(GxdQueryForm query){
		// start filter list for query filters
		List<Filter> queryFilters = new ArrayList<Filter>();
		// start filter list to store facet filters
		List<Filter> facetList = new ArrayList<Filter>();

		logger.debug("get params");

		// process normal query form parameter.  the resulting filter objects
		// are added to queryList.
		// the first this we need to check is if we have differential params
		if(isDifferentialQuery(query))
		{
			// Process DIFFERENTIAL QUERY FORM params
			// Do part 1 of the differential (I.e. find out what markers to bring back)
			List<String> markerKeys = resolveDifferentialMarkers(query);
			if(markerKeys !=null && markerKeys.size()>0)
			{
				queryFilters.add( new Filter(SearchConstants.MRK_KEY,markerKeys,Filter.OP_IN));
			}
			else
			{
				// need a way to prevent the standard query from returning results when the differential fails to find markers
				queryFilters.add( new Filter(SearchConstants.MRK_KEY,"NO_MARKERS_FOUND",Filter.OP_EQUAL));
			}
			// add the 2nd part of the differential query (I.e. what results to display for the given markers)
			queryFilters.add(makeDifferentialPart2Filter(query));
			
			return Filter.and(queryFilters);
			// NOTE: THIS WAS A DIFFERENTIAL QUERY, THE BELOW CODE ONLY APPLIES TO STANDARD QUERY FORM
		}
		
		// Process STANDARD QUERY FORM params
		
		// prep form parameter variables
		String markerMgiId = query.getMarkerMgiId();
		String jnum = query.getJnum();
		String structureKey = query.getStructureKey();
		String annotatedStructureKey = query.getAnnotatedStructureKey();
		String structureID = query.getStructureID();
		String alleleId = query.getAlleleId();
		String probeKey = query.getProbeKey();
		String antibodyKey = query.getAntibodyKey();

		if(structureKey !=null && !structureKey.equals("")) {
			Filter structureKeyFilter = new Filter(SearchConstants.STRUCTURE_KEY, structureKey);
			queryFilters.add(structureKeyFilter);
		}
		if(structureID !=null && !structureID.equals("")) {
			Filter structureIdFilter = new Filter(SearchConstants.STRUCTURE_ID, structureID);
			queryFilters.add(structureIdFilter);
		}
		if(annotatedStructureKey !=null && !annotatedStructureKey.equals("")) {
			Filter annotatedStructureKeyFilter = new Filter(GxdResultFields.ANNOTATED_STRUCTURE_KEY, annotatedStructureKey);
			queryFilters.add(annotatedStructureKeyFilter);
		}
		if(alleleId !=null && !alleleId.equals("")) {
			Filter alleleIdFilter = new Filter(SearchConstants.ALL_ID, alleleId);
			queryFilters.add(alleleIdFilter);
		}
		if(jnum !=null && !jnum.equals("")) {
			Filter jnumFilter = new Filter(SearchConstants.REF_ID, jnum);
			queryFilters.add(jnumFilter);
		}
		if(markerMgiId!=null && !markerMgiId.equals("")) {
			Filter markerIDFilter = new Filter(SearchConstants.MRK_ID, markerMgiId);
			queryFilters.add(markerIDFilter);
		}
		if(probeKey !=null && !probeKey.equals("")) {
			Filter probeKeyFilter = new Filter(SearchConstants.PROBE_KEY, probeKey);
			queryFilters.add(probeKeyFilter);
		}
		if(antibodyKey !=null && !antibodyKey.equals("")) {
			Filter antibodyKeyFilter = new Filter(SearchConstants.ANTIBODY_KEY, antibodyKey);
			queryFilters.add(antibodyKeyFilter);
		}

		String nomenclature = query.getNomenclature();
		String annotationId = query.getAnnotationId();
		if(nomenclature!=null && !nomenclature.equals("")) {
			Filter nomenFilter = generateNomenFilter(SearchConstants.MRK_NOMENCLATURE, nomenclature);
			if(nomenFilter != null) queryFilters.add(nomenFilter);
		}
		// vocab annotations
		else if(annotationId !=null && !annotationId.equals("")) {
			logger.debug("querying by vocab annotation term id "+annotationId);
			queryFilters.add(new Filter(GxdResultFields.ANNOTATION,annotationId,Filter.OP_EQUAL));
		}

		// is detected section
		String detected = query.getDetected();
		if(detected.equalsIgnoreCase("yes")
				|| detected.equalsIgnoreCase("no")
				|| detected.equalsIgnoreCase("explicit-no")
				|| detected.equalsIgnoreCase("explicit-yes"))
		{
			List<Filter> dFilters = new ArrayList<Filter>();
			
			// UPDATE: 2013-04-24 GXD changed their mind and does not want to return ambiguous results for detected radio buttons.
			// I am merely commenting out the code in case they change their mind again  - kstone
			if(detected.equalsIgnoreCase("yes"))
			{
				//dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Unknown/Ambiguous",Filter.OP_EQUAL));
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.OP_EQUAL));
			}
			else if (detected.equalsIgnoreCase("no"))
			{
				//dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Unknown/Ambiguous",Filter.OP_EQUAL));
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.OP_EQUAL));
			}
			else if (detected.equalsIgnoreCase("explicit-yes"))
			{
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.OP_EQUAL));
			}
			else if (detected.equalsIgnoreCase("explicit-no"))
			{
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.OP_EQUAL));
			}

			queryFilters.add(Filter.or(dFilters));
		}
		// anatomical structure section
		String structure = query.getStructure();

		if(structure!=null && !structure.equals(""))
		{
			logger.debug("splitting structure query into tokens");
			Filter sFilter = makeStructureSearchFilter(SearchConstants.STRUCTURE,structure);
			if(sFilter!=null) queryFilters.add(sFilter);
		}
		// theiler stage/age section
		List<Integer> stages = query.getTheilerStage();
		List<String> ages = query.getAge();
		// try theiler stage query first
		if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
		{
			logger.debug("adding theiler stage selections to query");
			List<Filter> stageFilters = new ArrayList<Filter>();
			for(Integer stage : stages)
			{
				Filter stageF = new Filter(SearchConstants.GXD_THEILER_STAGE,stage,Filter.OP_HAS_WORD);
				stageFilters.add(stageF);

			}

			// add the theiler stage search filter
			queryFilters.add(Filter.or(stageFilters));
		}
		// age query
		// this can only happen if theilerStage was not passed in
		else if(ages.size() > 0 && !ages.contains(GxdQueryForm.ANY_AGE))
		{
			// also do nothing if both postnatal and embryonic are selected, because it is equivalent to ANY
			if( !(ages.contains(GxdQueryForm.EMBRYONIC) && ages.contains(GxdQueryForm.POSTNATAL)))
			{
				List<Filter> ageFilters = new ArrayList<Filter>();
				//postnatal means TS 28
				if(ages.contains(GxdQueryForm.POSTNATAL))
				{
					// same as TS 28
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,28,Filter.OP_HAS_WORD));
				}
				//embryonic means TS 1-26
				// if they selected embryonic, none of the age selections matter
				if(ages.contains(GxdQueryForm.EMBRYONIC))
				{
					// Same as TS 1-26 or NOT TS 28
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,28,Filter.OP_NOT_EQUAL));
				}
				else
				{
					// iterate the legit age queries
					for(String age : ages)
					{
						if(!age.equalsIgnoreCase(GxdQueryForm.EMBRYONIC)
								&& !age.equalsIgnoreCase(GxdQueryForm.POSTNATAL))
						{
							try{
							//Float age_num = Float.parseFloat(age);
							Filter ageMinFilter = new Filter(SearchConstants.GXD_AGE_MIN,age,Filter.OP_LESS_OR_EQUAL);
							Filter ageMaxFilter = new Filter(SearchConstants.GXD_AGE_MAX,age,Filter.OP_GREATER_OR_EQUAL);
							// AND the min and max query to make a range query;
							ageFilters.add(Filter.and(Arrays.asList(ageMinFilter,ageMaxFilter)));
							}
							catch (NumberFormatException ne)
							{
								logger.info("an invalid age was passed to the form");
								logger.info(ne.getMessage());
								// ignore this. It just means someone manually entered an invalid url
							}
						}
					}
					// do some age things
				}
				queryFilters.add(Filter.or(ageFilters));
			}
		}
		if ((query.getIsWildType() != null && "true".equals(query.getIsWildType()))){
			queryFilters.add(new Filter(SearchConstants.GXD_IS_WILD_TYPE, "true"));
		} else if (query.getMutatedIn() != null && !"".equals(query.getMutatedIn())) {
			Filter mutatedInFilter = generateNomenFilter(SearchConstants.GXD_MUTATED_IN, query.getMutatedIn());
			if (mutatedInFilter != null) queryFilters.add(mutatedInFilter);
		}

		// Assay type(s) section
		List<String> assayTypes = query.getAssayType();
		// only build the filters if there are between 0 and 8 of the available assay types.
		if(assayTypes.size() > 0 && assayTypes.size()<query.getAssayTypes().size())
		{
			logger.debug("adding assay type selections to query");
			// build the assay type filters and OR them
			List<Filter> aFilters = new ArrayList<Filter>();
			for(String assayType : assayTypes)
			{
				logger.debug("querying by assay_type "+assayType);
				Filter aFilter = new Filter(SearchConstants.GXD_ASSAY_TYPE,assayType,Filter.OP_EQUAL);
				aFilters.add(aFilter);
			}
			queryFilters.add(Filter.or(aFilters));
		}

		// And all base filter sections
		Filter gxdFilter = new Filter();
		if(queryFilters.size() > 0)
		{
			gxdFilter.setNestedFilters(queryFilters,Filter.FC_AND);
		}
		else
		{
			// default return all results?
			gxdFilter = new Filter(SearchConstants.PRIMARY_KEY,"[* TO *]",Filter.OP_HAS_WORD);
		}

		return gxdFilter;
	}

	private Filter generateNomenFilter(String property, String query){
		logger.debug("splitting nomenclature query into tokens");
		Collection<String> nomens = QueryParser.parseNomenclatureSearch(query);
		Filter nomenFilter = new Filter();
		List<Filter> nomenFilters = new ArrayList<Filter>();
		// we want to group all non-wildcarded tokens into one solr phrase search
		List<String> nomenTokens = new ArrayList<String>();
		String phraseSearch = "";

		for(String nomen : nomens) {
			if(nomen.endsWith("*") || nomen.startsWith("*")) {
				nomenTokens.add(nomen);
			} else {
				phraseSearch += nomen+" ";
			}
		}

		if(!phraseSearch.trim().equals("")) {
			// surround with double quotes to make a solr phrase. added a slop of 100 (longest name is 62 chars)
			nomenTokens.add("\""+phraseSearch+"\"~100");
		}

		for(String nomenToken : nomenTokens) {
			logger.debug("token="+nomenToken);
			Filter nFilter = new Filter(property, nomenToken,Filter.OP_HAS_WORD);
			nomenFilters.add(nFilter);
		}

		if(nomenFilters.size() > 0) {
			nomenFilter.setNestedFilters(nomenFilters,Filter.FC_AND);
			// add the nomenclature search filter
			return nomenFilter;
		}
		// We don't want to return an empty filter object, because it screws up Solr.
		return null;
	}




	/*
	 * Parses requested sort parameters for gxd marker assay summary.
	 */
	private List<Sort> parseMarkerSorts(HttpServletRequest request) {

        logger.debug("->parseMarkerSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");

        // empty
        if (sortRequested == null) {
            return sorts;
        }

        // expected sort values
        if ("gene".equalsIgnoreCase(sortRequested)){
            sortRequested = GxdResultFields.M_BY_MRK_SYMBOL;
        } else if ("chr".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_LOCATION;
        } else if ("location".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_LOCATION;
        } else if ("cm".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_LOCATION;
        } else {
            sortRequested = GxdResultFields.M_BY_MRK_SYMBOL;
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

	/*
	 * Parses requested sort parameters for gxd assay summary.
	 */
	private List<Sort> parseAssaySorts(HttpServletRequest request) {

        logger.debug("->parseAssayResultsSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");

        // empty
        if (sortRequested == null) {
            return sorts;
        }

        // expected sort values
        if ("gene".equalsIgnoreCase(sortRequested)){
            sortRequested = GxdResultFields.A_BY_SYMBOL;
        } else if ("assayType".equalsIgnoreCase(sortRequested)){
            sortRequested = GxdResultFields.A_BY_ASSAY_TYPE;
        } else if ("reference".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_REFERENCE;
        } else {
            sortRequested = GxdResultFields.A_BY_SYMBOL;
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

	/*
	 * Parses requested sort parameters for gxd assay results summary.
	 */
	private List<Sort> parseAssayResultsSorts(HttpServletRequest request) {

        logger.debug("->parseAssayResultsSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");

        // empty
        if (sortRequested == null) {
            return sorts;
        }

        // expected sort values
        if ("gene".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_GENE;
        } else if ("assayType".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_ASSAY_TYPE;
        } else if ("anatomicalSystem".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_SYSTEM;
        } else if ("age".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_AGE;
        } else if ("structure".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_STRUCTURE;
        } else if ("detectionLevel".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_DETECTION;
        } else if ("genotype".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_GENOTYPE;
        } else if ("reference".equalsIgnoreCase(sortRequested)){
            sortRequested = SortConstants.GXD_REFERENCE;
        } else {
            sortRequested = SortConstants.GXD_GENE;
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


	// -----------------------------------------------------------------//
	// These methods are used by URL-mapped methods in this class, as
	// well as by our continuous integration and unit testing suites.
	// Therefore, these normally private methods are publicly exposed.
	// -----------------------------------------------------------------//

	public SearchResults<SolrGxdMarker> getGxdMarkerResults(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException{

		logger.debug("getGxdMarkerResults() started " );
		logger.debug("query =  " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		params.setPaginator(page);
		params.setFilter(this.parseGxdQueryForm(query));

		params.setSorts(this.parseMarkerSorts(request));
		//params.setSorts(Arrays.asList(IndexConstants.MRK_BY_SYMBOL));

		// perform query and return results as json
		logger.debug("params parsed");

		if (result.hasErrors()){
			logger.debug("bind error");
			throw new BindException(result);
		} else {
			return gxdFinder.searchMarkers(params);
		}
	}

	public SearchResults<SolrGxdAssay> getGxdAssays(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException{

		logger.debug("getGxdAssays() started " );
		logger.debug("query =  " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		params.setPaginator(page);
		params.setFilter(this.parseGxdQueryForm(query));

		params.setSorts(this.parseAssaySorts(request));
		//params.setSorts(Arrays.asList(IndexConstants.MRK_BY_SYMBOL));

		// perform query and return results as json
		logger.debug("params parsed");

		if (result.hasErrors()){
			logger.debug("bind error");
			throw new BindException(result);
		} else {
			return gxdFinder.searchAssays(params);
		}
	}

	public SearchResults<SolrAssayResult> getGxdAssayResults(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException{

		logger.debug("getGxdAssayResults() started ");
		logger.debug("query =  " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		params.setPaginator(page);
		params.setFilter(this.parseGxdQueryForm(query));
		params.setSorts(this.parseAssayResultsSorts(request));

		// perform query and return results as json
		logger.debug("params parsed");

		if (result.hasErrors()){
			logger.debug("bind error");
			throw new BindException(result);
		} else {
			return gxdFinder.searchAssayResults(params);
		}
	}

	public SearchResults<SolrGxdImage> getGxdImages(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException{

		logger.debug("getGxdImages() started " );
		logger.debug("query =  " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		params.setPaginator(page);
		params.setFilter(this.parseGxdQueryForm(query));

		// sort using byDefaultSort
		params.setSorts(Arrays.asList(new Sort(SortConstants.BY_DEFAULT)));

		// perform query and return results as json
		logger.debug("params parsed");

		if (result.hasErrors()){
			logger.debug("bind error");
			throw new BindException(result);
		} else {
			return gxdFinder.searchImages(params);
		}
	}
}
