package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.VocabTerm;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.BatchFinder;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.finder.GxdFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.handler.GxdMatrixHandler;
import org.jax.mgi.fewi.matrix.GxdGeneMatrixPopup;
import org.jax.mgi.fewi.matrix.GxdMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.jax.mgi.fewi.matrix.GxdStageGridJsonResponse;
import org.jax.mgi.fewi.matrix.GxdStageMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdStageMatrixPopup;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdGeneMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.GxdAssaySummaryRow;
import org.jax.mgi.fewi.summary.GxdCountsSummary;
import org.jax.mgi.fewi.summary.GxdImageSummaryRow;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
	// static variables
	// --------------------//

	// value for the isWildType field in gxdResult index
	private static String WILD_TYPE = "wild type";

	// values for defining how we sort facet results
	private static String ALPHA = "alphabetic";	// sort alphabetically
	private static String RAW = "raw";		// as returned by solr
	private static String DETECTED = "detected";	// custom case

	// --------------------//
	// instance variables
	// --------------------//

	private final Logger logger = LoggerFactory.getLogger(GXDController.class);

	//@Autowired
	//private SolrMarkerKeyHunter mrkKeyHunter;

	@Autowired
	private BatchFinder batchFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired AlleleFinder alleleFinder;

	@Autowired
	private GxdFinder gxdFinder;

	@Autowired
	private VocabularyFinder vocabFinder;

	@Autowired
	private GXDLitController gxdLitController;

	@Autowired
	private BatchController batchController;

	@Autowired
	private GxdMatrixHandler gxdMatrixHandler;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit;

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
		mav.addObject("gxdBatchQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());

		return mav;
	}

	// "expanded" query form
	@RequestMapping("differential")
	public ModelAndView getDifferentialQueryForm() {

		logger.debug("->getDifferentialQueryForm started");

		ModelAndView mav = new ModelAndView("gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showDifferentialQueryForm",true);

		return mav;
	}

	/*
	 * report
	 */
	@RequestMapping("/report*")
	public ModelAndView resultsSummaryExport(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query) {

		logger.debug("generating report");
		// build a batch finder object and pass it to the view for iteration
		populateMarkerIDs(session, query);
		Filter qf = parseGxdQueryForm(query);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(SortConstants.GXD_GENE));
		sp.setSorts(sorts);
		GxdBatchFinder batchFinder = new GxdBatchFinder(gxdFinder,sp);

		logger.debug("routing to view object");
		ModelAndView mav = new ModelAndView("gxdResultsSummaryReport");
		mav.addObject("resultFinder", batchFinder);
		mav.addObject("queryString", request.getQueryString());
		return mav;

	}

	/* look up the marker IDs corresponding to the batch submission fields
	 * in 'form' and add them to the special field for them in 'form'
	 */
	private void populateMarkerIDs(HttpSession session, GxdQueryForm form) {
		String idString = form.getIds();

		// if no string of IDs and no uploaded file, just return
		if ( ((idString == null) || (idString.length() == 0)) && (form.getHasFile() == false) ) {
			return;
		}
		form.setBatchSubmission(true);

		BatchQueryForm bqf = new BatchQueryForm();
		bqf.setIdFile(form.getIdFile());
		bqf.setIdColumn(form.getIdColumn());
		bqf.setFileType(form.getFileType());
		bqf.setIds(form.getIds());
		bqf.setIdType(form.getIdType());

		List<String> ids = batchController.getIDList(bqf);
		List<String> markerIDs = batchController.getMarkerIDs(bqf, ids);

		logger.debug("Found " + markerIDs.size() + " markers for " + ids.size() + " IDs");

		session.setAttribute("idSet", ids);
		if ((markerIDs != null) && (markerIDs.size() > 0)) {
			form.setMarkerIDs(markerIDs);
		}
	}

	// handling for batch searches
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView getSummaryPost(HttpSession session,
			@ModelAttribute GxdQueryForm query,
			MultipartHttpServletRequest request) {

		logger.debug("->getSummaryPost started");

		session.removeAttribute("idSet");
		logger.debug("  --> sessionId: " + session.getId());

		logger.debug("  --> about to populateMarkerIDs()");
		populateMarkerIDs(session, query);
		logger.debug("  --> back from populateMarkerIDs()");
		query.setBatchSubmission(true);
		logger.debug("  --> Filters: " + parseGxdQueryForm(query));

		return getBatchSearchForm(session, query, request);
	}

	// "batch search" query form
	@RequestMapping("batchSearch")
	public ModelAndView getBatchSearchForm(HttpSession session,
			@ModelAttribute GxdQueryForm query,
			HttpServletRequest request) {

		logger.debug("->getBatchSearchForm started");

		ModelAndView mav = new ModelAndView("gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", query);
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showBatchSearchForm",true);

		logger.debug("  --> before 'if'");
		if (query.getBatchSubmission()) {
			logger.debug("    --> in 'if'");
			String idList = query.getIds();
			if ((idList != null) && (idList.length() > 0)) {
				idList = "&ids=" + idList.replaceAll("[\n\t ]+", " ");
			} else {
				idList = "";
			}
			mav.addObject("queryString", "batchSubmission=true&" + request.getQueryString() + idList);
		} else {
			logger.debug("    --> in 'else'");
			mav.addObject("queryString", request.getQueryString());
		}

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

		// the marker ID is an optional field; if it exists, get the
		// corresponding marker and add it to the mav.  This supports
		// the link from the anatomy slimgrid on the marker detail pg.

		boolean hasMarkerID = false;
		boolean hasStructure = false;

		String markerId = request.getParameter("markerMgiId");
		if (markerId != null) {
			String error = addMarkerToMav(mav, markerId);
			if (error != null) {
				return errorMav(error);
			}
			hasMarkerID = true;
		}

		// structure and structureId get special handling, in case
		// we're coming from the anatomy slimgrid on the marker
		// detail page

		String structure = request.getParameter("structure");
		String structureId = request.getParameter("structureID");

		if (structure != null) {
			mav.addObject("structure", structure);
		}
		if (structureId != null) {
			mav.addObject("structureId", structureId);
			hasStructure = true;
		}

		// if we have a marker ID and a structure ID, then we can 
		// assume we have arrived from a slimgrid on the marker
		// detail page.

		if (hasMarkerID && hasStructure) {
			mav.addObject("fromSlimgrid", "yes");
		}

		return mav;
	}

	/* build and return a mav which will present the given error message
	 * to the user
	 */
	private ModelAndView errorMav (String error) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", error);
		return mav;
	}

	/*
	 * report by markers
	 */
	@RequestMapping("marker/report*")
	public ModelAndView resultsMarkerSummaryExport(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query) {

		logger.debug("generating GXD marker report");
		// build a batch finder object and pass it to the view for iteration
		populateMarkerIDs(session, query);
		Filter qf = parseGxdQueryForm(query);
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
		mav.addObject("queryString", request.getQueryString());
		return mav;

	}


	/*
	 * Summary by EMAPA/EMAPS ID
	 */
	@RequestMapping(value="/structure/{emapID}")
	public ModelAndView summeryByStructureId(
			HttpServletRequest request,
			@PathVariable("emapID") String emapID) {

		logger.debug("->summaryByStructureId started");

		// setup view object
		ModelAndView mav = new ModelAndView("gxd_summary_by_structure");

		// setup search parameters object to gather the requested term
		SearchParams searchParams = new SearchParams();
		Filter idFilter = new Filter(SearchConstants.STRUCTURE_ID, emapID);
		searchParams.setFilter(idFilter);

		// find the requested structure
		List<VocabTerm> structureList = vocabFinder.getTermByID(emapID);

		// there can be only one...
		if (structureList.size() < 1) {
			// forward to error page
			return errorMav("No anatomy term found for " + emapID);
		}
		if (structureList.size() > 1) {
			// forward to error page
			return errorMav("Multiple anatomy terms found for " + emapID);
		}
		VocabTerm structure = structureList.get(0);
		mav.addObject("structure", structure);
		mav.addObject("queryString", request.getQueryString());

		logger.debug("summaryByStructureId routing to view ");
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
			return errorMav("No reference found for " + refID);
		}
		if (referenceList.size() > 1) {
			// forward to error page
			return errorMav("Dupe reference found for " + refID);
		}
		Reference reference = referenceList.get(0);
		mav.addObject("reference", reference);
		mav.addObject("queryString", request.getQueryString());

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
			return errorMav("No allele found for " + allID);
		}
		if (alleleList.size() > 1) {
			// forward to error page
			return errorMav("Dupe reference found for " + allID);
		}
		Allele allele = alleleList.get(0);
		mav.addObject("allele", allele);
		mav.addObject("queryString", request.getQueryString());

		logger.debug("summeryByAllId routing to view ");
		return mav;
	}


	/* looks up the Marker object associated with the given marker ID, and
	 * adds it to the mav with the 'marker' name.  Returns null if 
	 * successful, or an error message if it fails for some reason.
	 */
	private String addMarkerToMav (ModelAndView mav, String mrkID) {
		// setup search parameters object to get the requested marker
		SearchParams markerSearchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID,
			mrkID);
		markerSearchParams.setFilter(markerIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults
			= markerFinder.getMarkerByID(markerSearchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) {
			return "No marker found for " + mrkID;
		} else if (markerList.size() > 1) {
			return "Dupe marker ID found for " + mrkID;
		}
		Marker marker = markerList.get(0);
		mav.addObject("marker", marker);
		return null;
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

		String error = addMarkerToMav(mav, mrkID);
		if (error != null) {
			return errorMav(error);
		}

		mav.addObject("queryString", request.getQueryString());

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
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query) {

		logger.debug("forwarding gxd markers to batch");

		populateMarkerIDs(session, query);

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));

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
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdMarkerSummaryJson() started");
		populateMarkerIDs(session, query);

		SearchResults<SolrGxdMarker> searchResults = getGxdMarkerResults(request, query, page, result);
		List<SolrGxdMarker> markerList = searchResults.getResultObjects();

		List<GxdMarkerSummaryRow> summaryRows = new ArrayList<GxdMarkerSummaryRow>();
		GxdMarkerSummaryRow row;
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
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdAssaySummaryJson() started");
		populateMarkerIDs(session, query);

		SearchResults<SolrGxdAssay> searchResults = getGxdAssays(request, query, page, result);
		List<SolrGxdAssay> assayList = searchResults.getResultObjects();

		List<GxdAssaySummaryRow> summaryRows = new ArrayList<GxdAssaySummaryRow>();
		GxdAssaySummaryRow row;

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
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdResultsSummaryJson() started");
		logger.debug("querystring: " + request.getQueryString());
		populateMarkerIDs(session, query);

		SearchResults<SolrAssayResult> searchResults = getGxdAssayResults(request, query, page, result);

		List<SolrAssayResult> resultList = searchResults.getResultObjects();

		List<GxdAssayResultSummaryRow> summaryRows = new ArrayList<GxdAssayResultSummaryRow>();
		GxdAssayResultSummaryRow row;
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
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.debug("gxdImageSummaryJson() started");
		populateMarkerIDs(session, query);

		SearchResults<SolrGxdImage> searchResults = getGxdImages(request, query, page, result);
		List<SolrGxdImage> imageList = searchResults.getResultObjects();
		//List<SolrGxdAssay> assayList = searchResults.getResultObjects();

		List<GxdImageSummaryRow> summaryRows = new ArrayList<GxdImageSummaryRow>();
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

	/* retrieve the expression image that serves as the teaser image on
	 * the marker detail page for the given marker, or null if there are
	 * no expression images for the marker
	 */
	public GxdImageSummaryRow getMarkerDetailTeaserImage(Marker marker) {
		logger.debug("gxdMarkerDetailTeaserImage() started");

		// parse the various query parameter to generate SearchParams object
		GxdQueryForm query = new GxdQueryForm();
		query.setMarkerMgiId(marker.getPrimaryID());

		Paginator page = new Paginator(1);

		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));

		// sort using byDefaultSort
		params.setSorts(Arrays.asList(new Sort(SortConstants.BY_DEFAULT)));

		SearchResults<SolrGxdImage> results = gxdFinder.searchImages(params);

		List<SolrGxdImage> imageList = results.getResultObjects();

		for (SolrGxdImage image : imageList) {
			if (image != null){
				GxdImageSummaryRow row = new GxdImageSummaryRow(image);
				row.setMaxWidth(90);
				row.setMaxHeight(65);
				row.hideCopyright();
				row.skipDetailLink();
				return row;
			}
		}
		return null;
	}

	@RequestMapping("/stageMatrixPopup/json")
	public @ResponseBody GxdStageMatrixPopup gxdStageMatrixPopupJson(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="rowId") String rowId,
			@RequestParam(value="colId") String colId
			) {
		logger.debug("gxdStageMatrixPopupJson() started");
		populateMarkerIDs(session, query);

		logger.debug("request=" + request.getQueryString());

		// find the requested structure
		List<VocabTerm> structureTermList = vocabFinder.getTermByID(rowId);
		VocabTerm structureTerm = structureTermList.get(0);

		// force only the current row and column as filters
		query.getMatrixStructureId().add(rowId);
		query.setTheilerStageFilter(Arrays.asList(colId));

		// the object to return as a JSON object
		GxdStageMatrixPopup gxdStageMatrixPopup = new GxdStageMatrixPopup(structureTerm.getPrimaryID(), structureTerm.getTerm());

		int imageCount = getGxdImageCount(session, request,query);

		// get the results for structure/stage
		Paginator page = new Paginator(10000000);
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));
		SearchResults<SolrGxdMatrixResult> searchResults = gxdFinder.searchMatrixResults(params);
		List<SolrGxdMatrixResult> assayResultList = searchResults.getResultObjects();
		gxdStageMatrixPopup.setAssayResultList(assayResultList);
		gxdStageMatrixPopup.setHasImage(imageCount > 0);

		return gxdStageMatrixPopup;
	}

	@RequestMapping("/geneMatrixPopup/json")
	public @ResponseBody GxdGeneMatrixPopup gxdGeneMatrixPopupJson(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="rowId") String rowId,
			@RequestParam(value="colId") String colId
			) {
		logger.debug("gxdGeneMatrixPopupJson() started");
		populateMarkerIDs(session, query);

		logger.debug("request=" + request.getQueryString());

		// find the requested structure
		List<VocabTerm> structureTermList = vocabFinder.getTermByID(rowId);
		VocabTerm structureTerm = structureTermList.get(0);

		// force only the current row and column as filters
		query.getMatrixStructureId().add(rowId);
		query.setMatrixMarkerSymbol(colId);

		// the object to return as a JSON object
		GxdGeneMatrixPopup gxdGeneMatrixPopup = new GxdGeneMatrixPopup(structureTerm.getPrimaryID(), structureTerm.getTerm());

		int imageCount = getGxdImageCount(session, request,query);

		// get the results for structure/gene
		Paginator page = new Paginator(10000000);
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));
		SearchResults<SolrGxdMatrixResult> searchResults = gxdFinder.searchMatrixResults(params);
		List<SolrGxdMatrixResult> assayResultList = searchResults.getResultObjects();
		gxdGeneMatrixPopup.setAssayResultList(assayResultList);
		gxdGeneMatrixPopup.setHasImage(imageCount > 0);

		return gxdGeneMatrixPopup;
	}


	@RequestMapping("/stagegrid/json")
	public @ResponseBody GxdStageGridJsonResponse gxdStageGridJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			@RequestParam(value="mapChildrenOf",required=false) String childrenOf,
			@RequestParam(value="pathToOpen",required=false) List<String> pathsToOpen,
			HttpSession session) throws CloneNotSupportedException
			{
		logger.debug("gxdStageGridJson() started");
		populateMarkerIDs(session, query);

		boolean isFirstPage = page.getStartIndex() == 0;
		boolean isChildrenOfQuery = childrenOf!=null && !childrenOf.equals("");

		// save original query in case we are expanding a row
		GxdQueryForm originalQuery = (GxdQueryForm) query.clone();
		String sessionQueryString = originalQuery.toString();

		// check if we have a totalCount set (if so, we return early to indicate end of data)
		String totalCountSessionId = "GxdStageMatrixTotalCount_"+sessionQueryString;
		Integer totalCount = (Integer) session.getAttribute(totalCountSessionId);
		if(totalCount!=null && totalCount<page.getStartIndex())
		{
			logger.debug("reached end of result set");
			return new GxdStageGridJsonResponse();
		}

		// if we have a mapChildrenOf query, we filter by structureIds of the child rows of this parentId
		if(isChildrenOfQuery)
		{
			gxdMatrixHandler.addMapChildrenOfFilterForMatrix(query,childrenOf);
			// paths variable is not supported in conjunction with childrenOf query
			// childrenOf query takes precedence
			pathsToOpen = null;
		}

		// get the matrix results for this page
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));
		SearchResults<SolrGxdStageMatrixResult> searchResults = gxdFinder.searchStageMatrixResults(params);
		logger.debug("got matrix results");
		List<SolrGxdStageMatrixResult> resultList = searchResults.getResultObjects();

		// cache total count of assay results
		session.setAttribute(totalCountSessionId,searchResults.getTotalCount());

		// get the parent rows to be displayed
		List<GxdMatrixRow> parentTerms = gxdMatrixHandler.getParentTermsToDisplay(query,childrenOf,pathsToOpen);

		//logger.debug("term tree = "+parentTerms.get(0).printTree());
		List<GxdMatrixRow> flatRows = gxdMatrixHandler.getFlatTermList(parentTerms);
		//logger.debug("flat rows = "+StringUtils.join(flatRows,", "));


		// this gets all edges, but we only need all of them for the first page
		List<SolrDagEdge> edges = getDAGDescendentRelationships(query,flatRows).getResultObjects();

		// prune empty data rows
		if(isFirstPage)
		{
			Set<String> rowsWithChildren = new HashSet<String>();
			for(SolrDagEdge edge : edges)
			{
				rowsWithChildren.add(edge.getParentId());
			}
			Set<String> idsWithData = getExactStructureIds(query);
			idsWithData.addAll(rowsWithChildren);
			parentTerms = gxdMatrixHandler.pruneEmptyRows(parentTerms,idsWithData);
			//logger.debug("pruned Rows = "+parentTerms.get(0).printTree());
		}
		//logger.debug("edges for mapper size="+edges.size());
		//logger.debug(StringUtils.join(edges,", "));

		// get matrix cells; add stages to mapper if this is a row-expansion
		GxdStageMatrixMapper mapper = new GxdStageMatrixMapper(edges);

		// we need to get the whole list of theiler stages for the originalQuery to map all the dummy theiler stage cells
		if (isChildrenOfQuery)
		{
			String stagesInMatrixSessionId = "gxdMatrixStages_"+sessionQueryString;
			@SuppressWarnings("unchecked")
			Set<String> stagesInMatrix = (Set<String>) session.getAttribute(stagesInMatrixSessionId);
			if(stagesInMatrix==null)
			{
				stagesInMatrix = getStagesInMatrix(originalQuery);
				session.setAttribute(stagesInMatrixSessionId,stagesInMatrix);
			}
			logger.debug("adding stages to mapper: stages=" + stagesInMatrix);
			mapper.setStagesInMatrix(stagesInMatrix);
		}
		List<GxdMatrixCell> gxdMatrixCells = mapper.mapCells(flatRows, resultList);

		// only generate row relationships on first page/batch
		if (isFirstPage)
		{
			gxdMatrixHandler.assignOpenCloseState(parentTerms,query,edges);
		}

		// add to the response object
		GxdStageGridJsonResponse jsonResponse = new GxdStageGridJsonResponse();
		jsonResponse.setParentTerms(parentTerms);
		jsonResponse.setGxdMatrixCells(gxdMatrixCells);

		logger.debug("sending json response");
		return jsonResponse;
			}

	@RequestMapping("/genegrid/json")
	public @ResponseBody GxdStageGridJsonResponse gxdGeneGridJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			@RequestParam(value="mapChildrenOf",required=false) String childrenOf,
			@RequestParam(value="pathToOpen",required=false) List<String> pathsToOpen,
			HttpSession session) throws CloneNotSupportedException
			{
		logger.debug("gxdGeneGridJson() started");
		populateMarkerIDs(session, query);

		boolean isFirstPage = page.getStartIndex() == 0;
		boolean isChildrenOfQuery = childrenOf!=null && !childrenOf.equals("");

		// save original query in case we are expanding a row
		GxdQueryForm originalQuery = (GxdQueryForm) query.clone();
		String sessionQueryString = originalQuery.toString();

		// check if we have a totalCount set (if so, we return early to indicate end of data)
		String totalCountSessionId = "GxdGeneMatrixTotalCount_"+sessionQueryString;
		Integer totalCount = (Integer) session.getAttribute(totalCountSessionId);
		if(totalCount!=null && totalCount<page.getStartIndex())
		{
			logger.debug("reached end of result set");
			return new GxdStageGridJsonResponse();
		}

		// if we have a mapChildrenOf query, we filter by structureIds of the child rows of this parentId
		if(isChildrenOfQuery)
		{
			gxdMatrixHandler.addMapChildrenOfFilterForMatrix(query,childrenOf);
			pathsToOpen = null;
		}

		// get the matrix results for this page
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));
		SearchResults<SolrGxdGeneMatrixResult> searchResults = gxdFinder.searchGeneMatrixResults(params);
		logger.debug("got matrix results");
		List<SolrGxdGeneMatrixResult> resultList = searchResults.getResultObjects();

		// cache total count of assay results
		session.setAttribute(totalCountSessionId,searchResults.getTotalCount());

		// get the parent rows to be displayed
		List<GxdMatrixRow> parentTerms = gxdMatrixHandler.getParentTermsToDisplay(query,childrenOf,pathsToOpen);

		List<GxdMatrixRow> flatRows = gxdMatrixHandler.getFlatTermList(parentTerms);


		// this gets all edges, but we only need all of them for the first page
		List<SolrDagEdge> edges = getDAGDescendentRelationships(query,flatRows).getResultObjects();

		// prune empty data rows
		if(isFirstPage)
		{
			Set<String> rowsWithChildren = new HashSet<String>();
			for(SolrDagEdge edge : edges)
			{
				rowsWithChildren.add(edge.getParentId());
			}
			Set<String> idsWithData = getExactStructureIds(query);
			idsWithData.addAll(rowsWithChildren);

			parentTerms = gxdMatrixHandler.pruneEmptyRows(parentTerms,idsWithData);
			//logger.debug("pruned Rows = "+parentTerms.get(0).printTree());
		}
		//logger.debug("edges for mapper size="+edges.size());
		//logger.debug(StringUtils.join(edges,", "));

		// get matrix cells; add stages to mapper if this is a row-expansion
		GxdMatrixMapper mapper = new GxdMatrixMapper(edges);

		List<GxdMatrixCell> gxdMatrixCells = mapper.mapCells(flatRows, resultList);

		// only generate row relationships on first page/batch
		if (isFirstPage)
		{
			gxdMatrixHandler.assignOpenCloseState(parentTerms,query,edges);
		}

		// add to the response object
		GxdStageGridJsonResponse jsonResponse = new GxdStageGridJsonResponse();
		jsonResponse.setParentTerms(parentTerms);
		jsonResponse.setGxdMatrixCells(gxdMatrixCells);

		logger.debug("sending json response");
		return jsonResponse;
			}


	// -----------------------------------------------------------------//
	// Methods for getting query counts
	// -----------------------------------------------------------------//

	@RequestMapping("/markers/totalCount")
	public @ResponseBody Integer getGxdMarkerCount(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		logger.debug("called /markers/totalCount");
		populateMarkerIDs(session, query);
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));
		params.setPageSize(0);
		return gxdFinder.getMarkerCount(params);
	}
	@RequestMapping("/assays/totalCount")
	public @ResponseBody Integer getGxdAssayCount(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		logger.debug("called /assays/totalCount");
		populateMarkerIDs(session, query);
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));
		params.setPageSize(0);

		return gxdFinder.getAssayCount(params);
	}
	@RequestMapping("/results/totalCount")
	public @ResponseBody Integer getGxdResultCount(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		logger.debug("called /results/totalCount");
		populateMarkerIDs(session, query);
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));
		params.setPageSize(0);

		return gxdFinder.getAssayResultCount(params);
	}
	@RequestMapping("/images/totalCount")
	public @ResponseBody Integer getGxdImageCount(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		logger.debug("called /images/totalCount");
		populateMarkerIDs(session, query);
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));
		params.setPageSize(0);

		return gxdFinder.getImageCount(params);
	}

	@RequestMapping("/totalCounts")
	public @ResponseBody GxdCountsSummary getGxdCounts(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query)
	{
		logger.debug("called /totalCounts");
		populateMarkerIDs(session, query);
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));
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

		// special handling for Theiler Stage 27:
		// 1. if used in concert with other stages, ignore it
		// 2. if used alone, cannot map to GXD Lit Index, so bail
		List<Integer> stages = query.getTheilerStage();
		if (stages.contains(27)) {
			stages.remove(stages.indexOf(27));
			logger.debug("Removed TS 27 from list of stages");
			if (stages.size() == 0) {
				logger.debug("No more stages; cannot map to lit index");
				return null;
			}
		}

		boolean setLitQuery = false;

		GxdLitQueryForm gxdLitForm = new GxdLitQueryForm();
		// Only valid fields are Gene MGI ID, Nomenclature, Vocab Query, TS, Age, and Assay Type
		String nomenclature = query.getNomenclature().trim();
		if(nomenclature != null && !nomenclature.equals(""))
		{
			// suppress count and link if comma-delimited list of
			// marker symbols in nomen field
			if (nomenclature.indexOf(",") >= 0) {
				return null;
			}
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

		// Map Theiler stages to their corresponding ages, so we can
		// link to the GXD Literature Index using ages (embryonic days)

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

		// special handling of embryonic day-based ages, so we expand
		// 'any age' to be an OR-ed list, and so Postnatal becomes A.
		// These are so we can link to the GXD Literature Index.

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
		return (query.getDifStructureID()!=null && !query.getDifStructureID().equals(""))
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
		String structure = query.getStructureID();
		String difStructure = query.getDifStructureID();
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
					stageFilters.add(new Filter(SearchConstants.POS_STRUCTURE,"TS"+stage,Filter.Operator.OP_HAS_WORD));
				}
				// OR the stages together
				queryFilters.add(Filter.or(stageFilters));
			}
			List<Filter> dStageFilters = new ArrayList<Filter>();
			for(Integer dStage : query.getResolvedDifTheilerStage())
			{
				dStageFilters.add(new Filter(SearchConstants.POS_STRUCTURE,"TS"+dStage,Filter.Operator.OP_HAS_WORD));
			}
			Filter dStageFilter = Filter.or(dStageFilters);
			dStageFilter.negate();
			queryFilters.add(dStageFilter);
		}
		// perform both structure and stage diff
		else if(hasStructures && hasStages)
		{
			logger.debug("Performing dif query for BOTH structure and stage");
			// stub for 3rd differential ribbon logic
			if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
			{
				List<Filter> stageFilters = new ArrayList<Filter>();
				for(Integer stage : stages)
				{
					stageFilters.add(makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,"TS"+stage+" "+structure));
				}
				// OR the stages together
				queryFilters.add(Filter.or(stageFilters));
			}
			List<Filter> dStageFilters = new ArrayList<Filter>();
			for(Integer dStage : query.getResolvedDifTheilerStage())
			{
				dStageFilters.add(makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,"TS"+dStage+" "+difStructure));
			}
			Filter dStageFilter = Filter.or(dStageFilters);
			dStageFilter.negate();
			queryFilters.add(dStageFilter);
		}



		Filter difFilter = new Filter();
		if(queryFilters.size() > 0)
		{
			difFilter.setNestedFilters(queryFilters,Filter.JoinClause.FC_AND);
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
		String structure = query.getStructureID();
		String difStructure = query.getDifStructureID();
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
			posFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,structure));
			posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
			Filter posFilter = Filter.and(posFilters);

			// create negative results filter
			List<Filter> negFilters = new ArrayList<Filter>();
			negFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,difStructure));
			negFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
			Filter negFilter = Filter.and(negFilters);

			// or them to bring back both datasets
			queryFilters.add(Filter.or(Arrays.asList(posFilter,negFilter)));
		}
		// perform stages diff
		else if(hasStages && !hasStructures)
		{
			// create the positive results filter
			List<Filter> posFilters = new ArrayList<Filter>();
			posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
			if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
			{
				List<Filter> stageFilters = new ArrayList<Filter>();
				for(Integer stage : stages)
				{
					Filter stageF = new Filter(SearchConstants.GXD_THEILER_STAGE,stage,Filter.Operator.OP_HAS_WORD);
					stageFilters.add(stageF);

				}
				// OR the stages together
				posFilters.add(Filter.or(stageFilters));
			}
			Filter posFilter = Filter.and(posFilters);

			// create the negative results filter
			List<Filter> negFilters = new ArrayList<Filter>();
			negFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
			List<Filter> difStageFilters = new ArrayList<Filter>();
			for(Integer difStage : query.getResolvedDifTheilerStage())
			{
				Filter difStageF = new Filter(SearchConstants.GXD_THEILER_STAGE,difStage,Filter.Operator.OP_HAS_WORD);
				difStageFilters.add(difStageF);

			}
			// OR the stages together
			negFilters.add(Filter.or(difStageFilters));
			Filter negFilter = Filter.and(negFilters);

			queryFilters.add(Filter.or(Arrays.asList(posFilter,negFilter)));
		}
		else if(hasStructures && hasStages)
		{
			// stub for 3rd differential ribbon logic
			// create the positive results filter
			List<Filter> posFilters = new ArrayList<Filter>();
			posFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,structure));
			posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
			if(stages.size() > 0 && !stages.contains(GxdQueryForm.ANY_STAGE))
			{
				List<Filter> stageFilters = new ArrayList<Filter>();
				for(Integer stage : stages)
				{
					Filter stageF = new Filter(SearchConstants.GXD_THEILER_STAGE,stage,Filter.Operator.OP_HAS_WORD);
					stageFilters.add(stageF);

				}
				// OR the stages together
				posFilters.add(Filter.or(stageFilters));
			}
			Filter posFilter = Filter.and(posFilters);

			// create negative results filter
			List<Filter> negFilters = new ArrayList<Filter>();
			negFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,difStructure));
			negFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
			List<Filter> difStageFilters = new ArrayList<Filter>();
			for(Integer difStage : query.getResolvedDifTheilerStage())
			{
				Filter difStageF = new Filter(SearchConstants.GXD_THEILER_STAGE,difStage,Filter.Operator.OP_HAS_WORD);
				difStageFilters.add(difStageF);

			}
			// OR the stages together
			negFilters.add(Filter.or(difStageFilters));
			Filter negFilter = Filter.and(negFilters);

			// or them to bring back both datasets
			queryFilters.add(Filter.or(Arrays.asList(posFilter,negFilter)));
		}

		// all results MUST be wild type (broad definition)
		queryFilters.add(new Filter(SearchConstants.GXD_IS_WILD_TYPE, WILD_TYPE));

		return Filter.and(queryFilters);
	}

	/*
	 * Factored out building the anatomy style filter, because 1) it is complicated
	 * 	and 2) it is used in many places
	 */
	public Filter makeStructureSearchFilter(String queryField, String structureId)
	{
		//		Collection<String> structureTokens = QueryParser.parseNomenclatureSearch(structure);
		//
		//		String phraseSearch = "";
		//		for(String structureToken : structureTokens)
		//		{
		//			logger.debug("token="+structureToken);
		//			phraseSearch += structureToken+" ";
		//		}
		if(!structureId.trim().equals(""))
		{
			// surround with double quotes to make a solr phrase. added a slop of 100 (longest name is 62 chars)
			String sToken = "\""+structureId+"\"~100";
			return new Filter(queryField,sToken,Filter.Operator.OP_HAS_WORD);
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

		// ---------------------------
		// restrict results by filters (added to facetList)
		// ---------------------------

		if (query.getSystemFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_SYSTEM,
					query.getSystemFilter(), Filter.Operator.OP_IN));
		}

		if (query.getAssayTypeFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_ASSAY_TYPE,
					query.getAssayTypeFilter(), Filter.Operator.OP_IN));
		}

		if (query.getDetectedFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_DETECTED,
					query.getDetectedFilter(), Filter.Operator.OP_IN));
		}

		if (query.getTheilerStageFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_THEILER_STAGE,
					query.getTheilerStageFilter(), Filter.Operator.OP_IN));
		}

		if (query.getWildtypeFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_WILDTYPE,
					query.getWildtypeFilter(), Filter.Operator.OP_IN));
		}

		if(query.getStructureIDFilter().size() > 0) {
			facetList.add(new Filter(SearchConstants.STRUCTURE_ID,
					query.getStructureIDFilter(), Filter.Operator.OP_IN));
		}

		if(query.getMarkerSymbolFilter().size() > 0) {
			facetList.add(new Filter(SearchConstants.MRK_SYMBOL,
					query.getMarkerSymbolFilter(), Filter.Operator.OP_IN));
		}

		// matrix only query fields
		// absolute filter on structure ID (used by popups to restrict query to only this structure, but still keep the other structure queries and filters
		if(query.getMatrixStructureId() !=null && query.getMatrixStructureId().size() > 0) {
			List<Filter> matrixStructureFilters = new ArrayList<Filter>();
			for(String matrixStructureId : query.getMatrixStructureId())
			{
				matrixStructureFilters.add(new Filter(SearchConstants.STRUCTURE_ID,matrixStructureId,Filter.Operator.OP_EQUAL));
			}
			if(matrixStructureFilters.size() > 0)
			{
				queryFilters.add(Filter.or(matrixStructureFilters));
			}
		}
		if(query.getMatrixMarkerSymbol() !=null && !query.getMatrixMarkerSymbol().equals("")) {
			queryFilters.add(new Filter(SearchConstants.MRK_SYMBOL, query.getMatrixMarkerSymbol()));
		}

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
				queryFilters.add( new Filter(SearchConstants.MRK_KEY,markerKeys,Filter.Operator.OP_IN));
			}
			else
			{
				// need a way to prevent the standard query from returning results when the differential fails to find markers
				queryFilters.add( new Filter(SearchConstants.MRK_KEY,"NO_MARKERS_FOUND",Filter.Operator.OP_EQUAL));
			}
			// add the 2nd part of the differential query (I.e. what results to display for the given markers)
			queryFilters.add(makeDifferentialPart2Filter(query));

			if (facetList.size() > 0) {
				queryFilters.addAll(facetList);
			}

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
		String markerSymbol = query.getMarkerSymbol();

		if(structureKey !=null && !structureKey.equals("")) {
			Filter structureKeyFilter = new Filter(SearchConstants.STRUCTURE_KEY, structureKey);
			queryFilters.add(structureKeyFilter);
		}

		// spatial location
		if(query.getLocations() !=null && !query.getLocations().equals("")) {
			List<String> tokens = QueryParser.tokeniseOnWhitespaceAndComma(query.getLocations());
			List<Filter> locationFilters = new ArrayList<Filter>();
			for(String token : tokens){
				String spatialQueryString = SolrLocationTranslator.getQueryValue(token);
				if(spatialQueryString !=null && !spatialQueryString.equals("")) {
					locationFilters.add(new Filter(SearchConstants.MOUSE_COORDINATE,spatialQueryString,Filter.Operator.OP_HAS_WORD));
				}
			}
			if(locationFilters.size() > 0)
			{
				queryFilters.add(Filter.or(locationFilters));
			}
			else
			{
				// this enables location query to fail if input is invalid
				queryFilters.add(new Filter(SearchConstants.MRK_KEY,"NO_MARKERS_FOUND",Filter.Operator.OP_EQUAL));
			}
		}

		// special case...  if we have a structure ID and a structure,
		// then we need to search by the ID and skip the structure.

		boolean usedStructureID = false;

		if(structureID !=null && !structureID.equals("")) {
			usedStructureID = true;

			Filter structureIdFilter = new Filter (
					SearchConstants.STRUCTURE_ID, structureID);
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
		if(markerSymbol !=null && !markerSymbol.equals("")) {
			Filter markerSymbolFilter = new Filter(SearchConstants.MRK_SYMBOL, markerSymbol);
			queryFilters.add(markerSymbolFilter);
		}

		String nomenclature = query.getNomenclature();
		String annotationId = query.getAnnotationId();
		if(nomenclature!=null && !nomenclature.equals("")) {
			if (nomenclature.indexOf(",") >= 0) {
				List<Filter> nomenFilters = new ArrayList<Filter>();
				for (String s: nomenclature.split(",")) {
					Filter nomenFilter = FilterUtil.generateNomenFilter(SearchConstants.MRK_NOMENCLATURE, s);
					if(nomenFilter != null) nomenFilters.add(nomenFilter);
				}

				if (nomenFilters.size() > 0) {
					queryFilters.add(Filter.or(nomenFilters));
				}
			} else {
				Filter nomenFilter = FilterUtil.generateNomenFilter(SearchConstants.MRK_NOMENCLATURE, nomenclature);
				if(nomenFilter != null) queryFilters.add(nomenFilter);
			}
		}
		// vocab annotations
		else if(annotationId !=null && !annotationId.equals("")) {
			logger.debug("querying by vocab annotation term id "+annotationId);
			queryFilters.add(new Filter(GxdResultFields.ANNOTATION,annotationId,Filter.Operator.OP_EQUAL));
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
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
			}
			else if (detected.equalsIgnoreCase("no"))
			{
				//dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Unknown/Ambiguous",Filter.OP_EQUAL));
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
			}
			else if (detected.equalsIgnoreCase("explicit-yes"))
			{
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
			}
			else if (detected.equalsIgnoreCase("explicit-no"))
			{
				dFilters.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
			}

			queryFilters.add(Filter.or(dFilters));
		}

		// anatomical structure section -- only use if we didn't
		// already include a structure ID in the search

		String structure = query.getStructure();

		if(!usedStructureID && structure!=null && !structure.equals(""))
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
				Filter stageF = new Filter(SearchConstants.GXD_THEILER_STAGE,stage,Filter.Operator.OP_HAS_WORD);
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
				//postnatal means TS 28 (and TS 27)
				if(ages.contains(GxdQueryForm.POSTNATAL))
				{
					// same as TS 28 (and TS 27)
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,28,Filter.Operator.OP_HAS_WORD));
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,27,Filter.Operator.OP_HAS_WORD));
				}
				//embryonic means TS 1-26
				// if they selected embryonic, none of the age selections matter
				if(ages.contains(GxdQueryForm.EMBRYONIC))
				{
					// Same as TS 1-26 or NOT (TS 28 or TS 27)
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,28,Filter.Operator.OP_NOT_EQUAL));
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,27,Filter.Operator.OP_NOT_EQUAL));
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
								Filter ageMinFilter = new Filter(SearchConstants.GXD_AGE_MIN,age,Filter.Operator.OP_LESS_OR_EQUAL);
								Filter ageMaxFilter = new Filter(SearchConstants.GXD_AGE_MAX,age,Filter.Operator.OP_GREATER_OR_EQUAL);
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
			queryFilters.add(new Filter(SearchConstants.GXD_IS_WILD_TYPE, WILD_TYPE));
		} else if (query.getMutatedIn() != null && !"".equals(query.getMutatedIn())) {
			Filter mutatedInFilter = FilterUtil.generateNomenFilter(SearchConstants.GXD_MUTATED_IN, query.getMutatedIn());
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
				Filter aFilter = new Filter(SearchConstants.GXD_ASSAY_TYPE,assayType,Filter.Operator.OP_EQUAL);
				aFilters.add(aFilter);
			}
			queryFilters.add(Filter.or(aFilters));
		}

		// do we have a list of marker IDs (via the Batch Search tab)

		List<String> markerIDs = query.getMarkerIDs();
		if ((markerIDs != null) && (markerIDs.size() > 0)) {
			Filter markerIDsFilter = new Filter(SearchConstants.MRK_ID, markerIDs, Filter.Operator.OP_IN);
			queryFilters.add(markerIDsFilter);

		} else if (query.getBatchSubmission()) {
			// no markers were found for the submitted IDs, so
			// add a filter that prevents any matches (otherwise
			// we get all results)
			queryFilters.add(new Filter(SearchConstants.PRIMARY_KEY, "[-10 TO -10]", Filter.Operator.OP_HAS_WORD));
		}

		// And all base filter sections
		Filter gxdFilter = new Filter();
		if(queryFilters.size() > 0)
		{
			queryFilters.addAll(facetList);
			gxdFilter.setNestedFilters(queryFilters,Filter.JoinClause.FC_AND);
		}
		else if (facetList.size() > 0) {
			gxdFilter.setNestedFilters(facetList, Filter.JoinClause.FC_AND);
		}
		else
		{
			// default return all results?
			gxdFilter = new Filter(SearchConstants.PRIMARY_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD);
		}

		return gxdFilter;
	}

	// TODO: Returns a json mgi id list based on the filters that come in from the query form
	@RequestMapping("/markers/idList")
	public @ResponseBody String getIdList (HttpSession session, HttpServletRequest request, @ModelAttribute GxdQueryForm query) {

		logger.debug("called /markers/idList");
		populateMarkerIDs(session, query);
		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));
		params.setPageSize(20000);

		SearchResults<SolrGxdMarker> searchResults = gxdFinder.searchMarkers(params);
		List<SolrGxdMarker> list = searchResults.getResultObjects();

		StringBuffer ids = new StringBuffer();
		if (list != null && list.size() > 0) {
			for(SolrGxdMarker m: list) {
				ids.append(m.getMgiid() + ",");
			}
		}
		return ids.toString();
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
		params.setFilter(parseGxdQueryForm(query));

		params.setSorts(parseMarkerSorts(request));
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
		params.setFilter(parseGxdQueryForm(query));

		params.setSorts(parseAssaySorts(request));
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
		params.setFilter(parseGxdQueryForm(query));
		params.setSorts(parseAssayResultsSorts(request));

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
		params.setFilter(parseGxdQueryForm(query));

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

	// ---------------------------
	// facets for GXD Summary page
	// ---------------------------

	private Map<String, List<String>> facetGeneric (GxdQueryForm query,
			BindingResult result, String facetType) {

		logger.debug(query.toString());
		String order = ALPHA;

		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));

		SearchResults<SolrString> facetResults = null;

		if (FacetConstants.GXD_SYSTEM.equals(facetType)) {
			facetResults = gxdFinder.getSystemFacet(params);

		} else if (FacetConstants.GXD_ASSAY_TYPE.equals(facetType)) {
			facetResults = gxdFinder.getAssayTypeFacet(params);

		} else if (FacetConstants.GXD_DETECTED.equals(facetType)) {
			facetResults = gxdFinder.getDetectedFacet(params);
			order = DETECTED;

		} else if (FacetConstants.GXD_THEILER_STAGE.equals(facetType)) {
			facetResults = gxdFinder.getTheilerStageFacet(params);
			order = RAW;

		} else if (FacetConstants.GXD_WILDTYPE.equals(facetType)) {
			facetResults = gxdFinder.getWildtypeFacet(params);
		}
		else {
			facetResults = new SearchResults<SolrString>();
		}

		return parseFacetResponse(facetResults, order);
	}

	/* gets a list of systems for the system facet list, returned as JSON
	 */
	@RequestMapping("/facet/system")
	public @ResponseBody Map<String, List<String>> facetSystem(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result, FacetConstants.GXD_SYSTEM);
	}

	/* gets a list of assay types for the system facet list, returned as
	 * JSON
	 */
	@RequestMapping("/facet/assayType")
	public @ResponseBody Map<String, List<String>> facetAssayType(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_ASSAY_TYPE);
	}

	/* gets a list of detection levels for the system facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/detected")
	public @ResponseBody Map<String, List<String>> facetDetected(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_DETECTED);
	}

	/* gets a list of theiler stages for the system facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/theilerStage")
	public @ResponseBody Map<String, List<String>> facetTheilerStage(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_THEILER_STAGE);
	}

	/* gets a list of wild type values for the system facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/wildtype")
	public @ResponseBody Map<String, List<String>> facetWildtype(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_WILDTYPE);
	}

	private Map<String, List<String>> parseFacetResponse (
			SearchResults<SolrString> facetResults, String order) {

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();


		if (facetResults.getResultFacets().size() >= facetLimit) {
			l.add("Too many results to display. Modify your search or try another filter first.");
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() == 0) {
			l.add("No values in results to filter.");
			m.put("error", l);
		} else if (ALPHA.equals(order)) {
			m.put("resultFacets", facetResults.getSortedResultFacets());
		} else if (RAW.equals(order)) {
			m.put("resultFacets", facetResults.getResultFacets());
		} else {
			// DETECTED.equals(order)

			List<String> values = facetResults.getResultFacets();
			Collections.sort (values, new DetectedComparator());
			facetResults.setResultFacets(values);
			m.put("resultFacets", facetResults.getResultFacets());
		}

		return m;
	}

	private class DetectedComparator implements Comparator<String> {

		private final List<String> orderedItems = Arrays.asList (
				new String[] { "Yes", "No", "Ambiguous", "Not Specified" });

		public int compare (String a, String b) {
			int aIndex = orderedItems.indexOf(a);
			int bIndex = orderedItems.indexOf(b);

			// normal case: both a and b were in the orderedItems list

			if ((aIndex >= 0) && (bIndex >= 0)) {
				if (aIndex < bIndex) { return -1; }
				else if (aIndex > bIndex) { return 1; }
				else { return 0; }
			}

			// secondary cases: only one of them was in the list

			if (aIndex >= 0) { return -1; }
			if (bIndex >= 0) { return 1; }

			// tertiary case: neither was in the list -- sort alpha

			return a.compareToIgnoreCase(b);
		}
	}

	/*
	 * Matrix related methods 
	 */
	public SearchResults<SolrDagEdge> getDAGDirectRelationships(GxdQueryForm query,List<GxdMatrixRow> parentTerms)
	{
		SearchParams params = new SearchParams();
		params.setPageSize(100000);
		params.setFilter(parseGxdQueryForm(query));
		List<String> parentIds = new ArrayList<String>();
		for(GxdMatrixRow term : parentTerms)
		{
			parentIds.add(term.getRid());
		}
		return gxdFinder.searchMatrixDAGDirectEdges(params, parentIds);
	}

	public SearchResults<SolrDagEdge> getDAGDescendentRelationships(GxdQueryForm query,List<GxdMatrixRow> parentTerms)
	{
		SearchParams params = new SearchParams();
		params.setPageSize(100000);
		params.setFilter(parseGxdQueryForm(query));
		List<String> parentIds = new ArrayList<String>();
		for(GxdMatrixRow term : parentTerms)
		{
			parentIds.add(term.getRid());
		}
		return gxdFinder.searchMatrixDAGDescendentEdges(params, parentIds);
	}

	private Set<String> getExactStructureIds(GxdQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setPageSize(100000);
		params.setFilter(parseGxdQueryForm(query));
		SearchResults<SolrString> sr = gxdFinder.searchStructureIds(params);
		Set<String> ids = new HashSet<String>();
		for(SolrString s : sr.getResultObjects())
		{
			ids.add(s.toString());
		}
		return ids;
	}

	private Set<String> getStagesInMatrix(GxdQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setPageSize(100000);
		params.setFilter(parseGxdQueryForm(query));
		SearchResults<SolrString> sr = gxdFinder.searchStagesInMatrix(params);
		Set<String> stages = new HashSet<String>();
		for(SolrString s : sr.getResultObjects())
		{
			stages.add(s.toString());
		}
		return stages;
	}
}
