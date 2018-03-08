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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.RelatedTermBackward;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.VocabTerm;
import mgi.frontend.datamodel.hdp.HdpGenoCluster;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.BatchFinder;
import org.jax.mgi.fewi.finder.CdnaFinder;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.finder.GxdFinder;
import org.jax.mgi.fewi.finder.MPCorrelationMatrixCellFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.RecombinaseFinder;
import org.jax.mgi.fewi.finder.RecombinaseMatrixCellFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.forms.GxdHtQueryForm;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.handler.GxdMatrixHandler;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.matrix.GxdGeneMatrixPopup;
import org.jax.mgi.fewi.matrix.GxdMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.jax.mgi.fewi.matrix.GxdPhenoMatrixCell;
import org.jax.mgi.fewi.matrix.GxdPhenoMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdRecombinaseMatrixCell;
import org.jax.mgi.fewi.matrix.GxdRecombinaseMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdStageGridJsonResponse;
import org.jax.mgi.fewi.matrix.GxdStageMatrixMapper;
import org.jax.mgi.fewi.matrix.GxdStageMatrixPopup;
import org.jax.mgi.fewi.matrix.PhenoMatrixPopup;
import org.jax.mgi.fewi.matrix.RecombinaseMatrixPopup;
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
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdPhenoMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRecombinaseMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPCorrelationMatrixCell;
import org.jax.mgi.fewi.searchUtil.entities.SolrRecombinaseMatrixCell;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.GxdAssaySummaryRow;
import org.jax.mgi.fewi.summary.GxdCountsSummary;
import org.jax.mgi.fewi.summary.GxdImageSummaryRow;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;
import org.jax.mgi.shr.jsonmodel.Clone;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
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

	@Autowired
	private BatchFinder batchFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired
	private RecombinaseFinder recombinaseFinder;

	@Autowired AlleleFinder alleleFinder;

	@Autowired
	private GxdFinder gxdFinder;

	@Autowired
	private VocabularyFinder vocabFinder;

	@Autowired
	private MPCorrelationMatrixCellFinder mpCorrelationMatrixCellFinder;

	@Autowired
	private RecombinaseMatrixCellFinder recombinaseMatrixCellFinder;

	@Autowired
	private GXDLitController gxdLitController;

	@Autowired
	private BatchController batchController;

	@Autowired
	private RecombinaseController recombinaseController;

	@Autowired
	private GXDHTController gxdhtController;

	@Autowired
	private GxdMatrixHandler gxdMatrixHandler;

	@Autowired
	private CdnaFinder cdnaFinder;

	@Autowired
	private DiseasePortalFinder diseasePortalFinder;

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

		ModelAndView mav = new ModelAndView("gxd/gxd_query");
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

		ModelAndView mav = new ModelAndView("gxd/gxd_query");
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

		ModelAndView mav = new ModelAndView("gxd/gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", query);
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showBatchSearchForm",true);

		if (query.getBatchSubmission()) {
			String idList = query.getIds();
			if ((idList != null) && (idList.length() > 0)) {
				idList = "&ids=" + idList.replaceAll("[\n\t ]+", " ");
			} else {
				idList = "";
			}
			mav.addObject("queryString", "batchSubmission=true&" + request.getQueryString() + idList);
		} else {
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

		ModelAndView mav = new ModelAndView("gxd/gxd_generic_summary");
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
		ModelAndView mav = new ModelAndView("gxd/gxd_summary_by_structure");

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
		ModelAndView mav = new ModelAndView("gxd/gxd_summary_by_reference");

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
		ModelAndView mav = new ModelAndView("gxd/gxd_summary_by_allele");


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

	/* cDNA summary by marker
	 */
	@RequestMapping(value="/cdna/marker/{mrkID}")
	public ModelAndView cdnaSummaryByMarkerID(
			HttpServletRequest request,
			@PathVariable("mrkID") String mrkID) {

		logger.debug("->cdnaSummaryByMarkerID started");
		
		ModelAndView mav = new ModelAndView("gxd/cdna_summary_by_marker");
		mav.addObject("queryString", request.getQueryString());

		// get the marker object (if possible) and add it to the mav
		String error = addMarkerToMav(mav, mrkID);
		if (error != null) {
			return errorMav(error);
		}

		logger.debug("->cdnaSummaryByMarkerID routing to mav");
		return mav;
	}
	
	/* cDNA table of results (to be requested by JSON)
	 */
	@RequestMapping(value="/cdna/table")
	public ModelAndView cdnaSummaryTable (HttpServletRequest request, @ModelAttribute Paginator page) {

		logger.debug("->cdnaSummaryTable started");
		
		// grab the marker ID
		String mrkID = request.getParameter("markerID");
		if (mrkID == null) {
			return errorMav("Missing marker ID parameter");
		}
		
		// perform query, and pull out the requested objects
		SearchResults<Clone> searchResults = getCdnaClones(request, mrkID, page);
		List<Clone> cloneList = searchResults.getResultObjects();

		ModelAndView mav = new ModelAndView("gxd/cdna_summary_table");
		mav.addObject("clones", cloneList);
		mav.addObject("count", cloneList.size());
		mav.addObject("totalCount", searchResults.getTotalCount());
		return mav;
	}
	
	/*
	 * This is a convenience method to handle packing the SearchParams object
	 * and return the SearchResults from the finder for cDNA clones.
	 */
	private SearchResults<Clone> getCdnaClones(HttpServletRequest request, String mrkID, 
			@ModelAttribute Paginator page) {

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(new Filter(SearchConstants.CDNA_MARKER_ID, mrkID, Filter.Operator.OP_EQUAL));

		// sort by sequence number in ascending order (descending = false)
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(SortConstants.SEQUENCE_NUM, false));
		params.setSorts(sorts); 
		
		// perform query, return SearchResults 
		return cdnaFinder.getClones(params);
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
		ModelAndView mav = new ModelAndView("gxd/gxd_summary_by_marker");

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

		// sort using byAssayType
		params.setSorts(Arrays.asList(new Sort(SortConstants.BY_IMAGE_ASSAY_TYPE)));

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

	/* handle request for JSON data from the expression x phenotype grid.  Cells can be either for expression
	 * data (for a marker, indicated by colId = 0) or phenotype data (for a genocluster, indicated by colId > 0).
	 */
	@RequestMapping("/phenogridPopup/json")
	public @ResponseBody PhenoMatrixPopup phenoMatrixPopupJson(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="rowId") String rowId,
			@RequestParam(value="colId") String colId,
			@RequestParam(value="genoclusterKey", required=false) String genoclusterKey
			) {
		logger.debug("phenoMatrixPopupJson() started");
		
		// find the requested marker, so we can get the symbol
		List<Marker> markerList = markerFinder.getMarkerByPrimaryId(query.getMarkerMgiId());
		Marker marker = markerList.get(0); 

		// If we are seeking wild-type expression data for the gene, use the existing gene grid's method
		// to do the basic retrieval.  If column 0 is desired, that's our indicator.
		if (colId.equals("0")) {
			// then get the relevant cell from the existing endpoint and convert it to a popup for this endpoint
			query.setWildtypeFilter(Arrays.asList("wild type"));
			PhenoMatrixPopup popup = new PhenoMatrixPopup(gxdGeneMatrixPopupJson(session, request, query, rowId, marker.getSymbol())); 
			popup.setSymbol(marker.getSymbol());
			popup.setMarkerId(marker.getPrimaryID());
			return popup;
		}
		
		// If we got here, we're instead looking for a popup for a phenotype cell.

		// find the requested structure
		List<VocabTerm> structureList = vocabFinder.getTermByID(rowId);
		VocabTerm structure = structureList.get(0);

		// the object to return as a JSON object
		PhenoMatrixPopup phenoMatrixPopup = new PhenoMatrixPopup(structure.getPrimaryID(), structure.getTerm());
		phenoMatrixPopup.setSymbol(marker.getSymbol());

		// build data for this for the marker/structure pair
		if ((query.getMarkerMgiId() != null) && !"".equals(query.getMarkerMgiId().trim())) {
			String fewiURL = ContextLoader.getConfigBean().getProperty("FEWI_URL");

			List<HdpGenoCluster> genoclusters = diseasePortalFinder.getGenoClusterByKey(genoclusterKey);
			if (genoclusters.size() > 0) {
				Genotype genotype = genoclusters.get(0).getGenotype();
				if (genotype != null) {
					phenoMatrixPopup.setAlleles(FormatHelper.formatUnlinkedAlleles(genotype.getCombination1()));
				}
				phenoMatrixPopup.setGenoclusterLink(fewiURL + "diseasePortal/genoCluster/view/" + genoclusterKey + "?structureID=" + rowId);
			} else {
				phenoMatrixPopup.setGenoclusterLink(fewiURL + "diseasePortal/genoCluster/view/" + genoclusterKey);
			}
		}
		
		return phenoMatrixPopup;
	}

	/* handle request for JSON data from the expression x recombinase grid.  Cells can be either for expression
	 * data (for a marker, indicated by colId = 0) or recombinase data (for an allele, indicated by colId > 0).
	 */
	@RequestMapping("/recombinasegridPopup/json")
	public @ResponseBody RecombinaseMatrixPopup recombinaseMatrixPopupJson(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="rowId") String rowId,
			@RequestParam(value="colId") String colId,
			@RequestParam(value="alleleId", required=false) String alleleId
			) {
		logger.debug("recombinaseMatrixPopupJson() started");
		logger.info("mgiMarkerId: " + query.getMarkerMgiId());
		logger.info("alleleId: " + alleleId);
		logger.info("colId: " + colId);
		logger.info("rowId: " + rowId);
		
		// find the requested marker, so we can get the symbol
		List<Marker> markerList = markerFinder.getMarkerByPrimaryId(query.getMarkerMgiId());
		Marker marker = markerList.get(0); 

		Allele allele = null;
		if ((alleleId != null) && (alleleId.trim().length() > 0)) {
			List<Allele> alleleList = alleleFinder.getAlleleByID(alleleId);
			allele = alleleList.get(0);
		}
		
		// If we are seeking wild-type expression data for the gene, use the existing gene grid's method
		// to do the basic retrieval.  If column 0 is desired, that's our indicator.
		if (colId.equals("0")) {
			// then get the relevant cell from the existing endpoint and convert it to a popup for this endpoint
			query.setWildtypeFilter(Arrays.asList("wild type"));
			RecombinaseMatrixPopup popup = new RecombinaseMatrixPopup(gxdGeneMatrixPopupJson(session, request, query, rowId, marker.getSymbol())); 
			popup.setSymbol(marker.getSymbol());
			popup.setMarkerId(marker.getPrimaryID());
			return popup;
		}
		
		// If we got here, we're instead looking for a popup for a recombinase cell.

		// find the requested structure
		List<VocabTerm> structureList = vocabFinder.getTermByID(rowId.trim());
		VocabTerm structure = structureList.get(0);

		// the object to return as a JSON object
		RecombinaseMatrixPopup matrixPopup = new RecombinaseMatrixPopup(structure.getPrimaryID(), structure.getTerm());
		matrixPopup.setSymbol(marker.getSymbol());

		// build data for this for the marker/structure/allele trio
		if ((query.getMarkerMgiId() != null) && !"".equals(query.getMarkerMgiId().trim())) {
			String fewiURL = ContextLoader.getConfigBean().getProperty("FEWI_URL");

			SolrRecombinaseMatrixCell solrCell = this.getRecombinaseCell(marker.getPrimaryID(), rowId, alleleId);
			matrixPopup.setCountNegResults(solrCell.getNotDetectedResults());
			matrixPopup.setCountAmbResults(solrCell.getAnyAmbiguous());
			matrixPopup.setCountPosResults(solrCell.getDetectedResults());

			if (allele != null) {
				matrixPopup.setAllele(allele.getSymbol());
				matrixPopup.setAlleleLink(fewiURL + "allele/" + allele.getPrimaryID() + "?recomRibbon=open");
			}
		}
		
		return matrixPopup;
	}

	@RequestMapping("/stagegrid/json")
	public @ResponseBody GxdStageGridJsonResponse<GxdMatrixCell> gxdStageGridJson(
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
			return new GxdStageGridJsonResponse<GxdMatrixCell>();
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
		GxdStageGridJsonResponse<GxdMatrixCell> jsonResponse = new GxdStageGridJsonResponse<GxdMatrixCell>();
		jsonResponse.setParentTerms(parentTerms);
		jsonResponse.setGxdMatrixCells(gxdMatrixCells);

		logger.debug("sending json response");
		return jsonResponse;
	}

	// serve up data for the recombinase grid
	@RequestMapping("/recombinasegrid/json")
	public @ResponseBody GxdStageGridJsonResponse<GxdRecombinaseMatrixCell> gxdRecombinaseGridJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			@RequestParam(value="mapChildrenOf",required=false) String childrenOf,
			@RequestParam(value="pathToOpen",required=false) List<String> pathsToOpen,
			@RequestParam(value="alleleID",required=false) String alleleID,
			HttpSession session) throws CloneNotSupportedException
			{
		logger.debug("gxdRecombinaseGridJson() started");
		
		boolean isFirstPage = page.getStartIndex() == 0;
		boolean isChildrenOfQuery = childrenOf!=null && !childrenOf.equals("");

		if (query.getMarkerMgiId() == null) {
			logger.debug("no marker ID specified");
			return new GxdStageGridJsonResponse<GxdRecombinaseMatrixCell>();
		}
		
		// determine if the marker is a driver for any recombinases; if not, bail out
		SearchResults<Marker> markers = markerFinder.getMarkerByID(query.getMarkerMgiId());
		if (markers.getTotalCount() == 0) {
			logger.debug("cannot find marker for ID " + query.getMarkerMgiId());
			return new GxdStageGridJsonResponse<GxdRecombinaseMatrixCell>();
		}
		
		Marker marker = markers.getResultObjects().get(0);
		if (!marker.isDriver()) {
			logger.debug("no recombinase alleles for ID " + query.getMarkerMgiId());
			return new GxdStageGridJsonResponse<GxdRecombinaseMatrixCell>();
		}
		
		// We only want wild-type expression data for this grid.
		query.setIsWildType("true");
		
		// save original query in case we are expanding a row
		GxdQueryForm originalQuery = (GxdQueryForm) query.clone();
		String sessionQueryString = originalQuery.toString();

		// check if we have a totalCount set (if so, we return early to indicate end of data)
		String totalCountSessionId = "GxdRecombinaseMatrixTotalCount_"+sessionQueryString;
		Integer totalCount = (Integer) session.getAttribute(totalCountSessionId);
		if(totalCount!=null && totalCount<page.getStartIndex())
		{
			logger.debug("reached end of result set");
			return new GxdStageGridJsonResponse<GxdRecombinaseMatrixCell>();
		}

		// pull in phenotype cells for the marker/childrenOf pair
		List<SolrRecombinaseMatrixCell> recombinaseCells = null;
		if ((query.getMarkerMgiId() != null) && !"".equals(query.getMarkerMgiId().trim())) {
			recombinaseCells = this.getRecombinaseCells(query.getMarkerMgiId(), childrenOf);
			logger.info("Got " + recombinaseCells.size() + " recombinase cells");
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
		SearchResults<SolrGxdRecombinaseMatrixResult> searchResults = gxdFinder.searchRecombinaseMatrixResults(params);
		logger.debug("got recombinase matrix results");
		List<SolrGxdRecombinaseMatrixResult> resultList = searchResults.getResultObjects();

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

			if (recombinaseCells != null) {
				for (SolrRecombinaseMatrixCell cell : recombinaseCells) {
					idsWithData.add(cell.getAnatomyID());
				}
			}
			parentTerms = gxdMatrixHandler.pruneEmptyRows(parentTerms,idsWithData);
		}

		// get matrix cells and set the marker ID, as appropriate
		GxdRecombinaseMatrixMapper mapper = new GxdRecombinaseMatrixMapper(edges);
		List<GxdRecombinaseMatrixCell> gxdMatrixCells = mapper.mapRecombinaseGridCells(flatRows, resultList);
		if ((query.getMarkerMgiId() != null) && (query.getMarkerMgiId().trim().length() > 0)) {
			for (GxdRecombinaseMatrixCell cell : gxdMatrixCells) {
				cell.setMgiId(query.getMarkerMgiId());
			}
		}

		// add recombinase cells to the expression ones just built
		if (recombinaseCells != null) {
			Set<String> idsWithChildren = new HashSet<String>();
			for (SolrRecombinaseMatrixCell cell : recombinaseCells) {
				GxdRecombinaseMatrixCell gpm = new GxdRecombinaseMatrixCell("Recombinase", cell.getAnatomyID(), "" + cell.getByColumn(), false);
				gpm.setAmbiguous(cell.getAnyAmbiguous());
				gpm.setAmbiguousOrNotDetectedChildren(cell.getAmbiguousOrNotDetectedChildren());
				gpm.setByRecombinase(cell.getByColumn());
				gpm.setCellType(cell.getCellType());
				gpm.setDetected(cell.getDetectedResults());
				gpm.setNotDetected(cell.getNotDetectedResults());
				gpm.setSymbol(cell.getSymbol());
				gpm.setChildren(cell.getChildren());
				gpm.setMgiId(cell.getColumnID());		// set allele ID
				if ( (alleleID != null) && (alleleID.equals(cell.getColumnID())) ) {
					gpm.setHighlightColumn(true);
				}
				gxdMatrixCells.add(gpm);
				
				if (cell.getChildren() > 0) {
					idsWithChildren.add(cell.getAnatomyID());
				}
			}
			
			if (idsWithChildren.size() > 0) {
				for (GxdMatrixRow row : parentTerms) {
					setExForRecombinases(row, idsWithChildren);
				}
			}
		}

		// only generate row relationships on first page/batch
		if (isFirstPage)
		{
			gxdMatrixHandler.assignOpenCloseState(parentTerms,query,edges);
		}

		// add to the response object
		GxdStageGridJsonResponse<GxdRecombinaseMatrixCell> jsonResponse = new GxdStageGridJsonResponse<GxdRecombinaseMatrixCell>();
		jsonResponse.setParentTerms(parentTerms);
		jsonResponse.setGxdMatrixCells(gxdMatrixCells);

		logger.debug("sending json response");
		return jsonResponse;
	}

	// set the ex flag appropriately for 'row' and its children, if corresponding ids are in 'idsWithChildren'
	private GxdMatrixRow setExForRecombinases (GxdMatrixRow row, Set<String> idsWithChildren) {
		if (idsWithChildren.contains(row.getRid())) {
			row.setEx(true);
		}
		for (GxdMatrixRow childRow : row.getChildren()) {
			setExForRecombinases(childRow, idsWithChildren);
		}
		return row;
	}
	
	// serve up data for the phenogrid, aka the correlation matrix
	@RequestMapping("/phenogrid/json")
	public @ResponseBody GxdStageGridJsonResponse<GxdPhenoMatrixCell> gxdPhenoGridJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			@RequestParam(value="mapChildrenOf",required=false) String childrenOf,
			@RequestParam(value="pathToOpen",required=false) List<String> pathsToOpen,
			@RequestParam(value="genoclusterKey",required=false) String genoclusterKey,
			HttpSession session) throws CloneNotSupportedException
			{
		logger.debug("gxdPhenoGridJson() started");
		
		// We only want wild-type expression data for this grid.
		query.setIsWildType("true");
		
		populateMarkerIDs(session, query);

		boolean isFirstPage = page.getStartIndex() == 0;
		boolean isChildrenOfQuery = childrenOf!=null && !childrenOf.equals("");

		// save original query in case we are expanding a row
		GxdQueryForm originalQuery = (GxdQueryForm) query.clone();
		String sessionQueryString = originalQuery.toString();

		// check if we have a totalCount set (if so, we return early to indicate end of data)
		String totalCountSessionId = "GxdPhenoMatrixTotalCount_"+sessionQueryString;
		Integer totalCount = (Integer) session.getAttribute(totalCountSessionId);
		if(totalCount!=null && totalCount<page.getStartIndex())
		{
			logger.debug("reached end of result set");
			return new GxdStageGridJsonResponse<GxdPhenoMatrixCell>();
		}

		// pull in phenotype cells for the marker/childrenOf pair
		List<SolrMPCorrelationMatrixCell> mpCells = null;
		if ((query.getMarkerMgiId() != null) && !"".equals(query.getMarkerMgiId().trim())) {
			mpCells = this.getMPCells(query.getMarkerMgiId(), childrenOf);
			logger.info("Got " + mpCells.size() + " MP cells");
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
		SearchResults<SolrGxdPhenoMatrixResult> searchResults = gxdFinder.searchPhenoMatrixResults(params);
		logger.debug("got pheno matrix results");
		List<SolrGxdPhenoMatrixResult> resultList = searchResults.getResultObjects();

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
			
			if (mpCells != null) {
				for (SolrMPCorrelationMatrixCell cell : mpCells) {
					idsWithData.add(cell.getAnatomyID());
				}
			}

			parentTerms = gxdMatrixHandler.pruneEmptyRows(parentTerms,idsWithData);
		}

		// get matrix cells and set the marker ID, if available
		GxdPhenoMatrixMapper mapper = new GxdPhenoMatrixMapper(edges);
		List<GxdPhenoMatrixCell> gxdMatrixCells = mapper.mapPhenoGridCells(flatRows, resultList);
		if ((query.getMarkerMgiId() != null) && (query.getMarkerMgiId().trim().length() > 0)) {
			for (GxdPhenoMatrixCell cell : gxdMatrixCells) {
				cell.setMarkerId(query.getMarkerMgiId());
			}
		}

		// add phenotype cells to the expression ones just built
		if (mpCells != null) {
			Set<String> idsWithChildren = new HashSet<String>();
			for (SolrMPCorrelationMatrixCell cell : mpCells) {
				GxdPhenoMatrixCell gpm = new GxdPhenoMatrixCell("Pheno", cell.getAnatomyID(), "" + cell.getByGenocluster(), false);
				gpm.setAllelePairs(cell.getAllelePairs());
				gpm.setGenoclusterKey("" + cell.getGenoclusterKey());
				gpm.setPhenoAnnotationCount(cell.getAnnotationCount());
				gpm.setByGenocluster(cell.getByGenocluster());
				gpm.setHasBackgroundSensitivity(cell.getHasBackgroundSensitivity());
				gpm.setIsNormal(cell.getIsNormal());
				gpm.setChildren(cell.getChildren());
				
				if ((genoclusterKey != null) && (genoclusterKey.trim().length() > 0)) {
					if (genoclusterKey.equals(gpm.getGenoclusterKey())) {
						gpm.setHighlightColumn(true);
					}
				}
				gxdMatrixCells.add(gpm);
				
				if (cell.getChildren() > 0) {
					idsWithChildren.add(cell.getAnatomyID());
				}
			}
			
			if (idsWithChildren.size() > 0) {
				for (GxdMatrixRow row : parentTerms) {
					setExForRecombinases(row, idsWithChildren);
				}
			}
		}
		
		// only generate row relationships on first page/batch
		if (isFirstPage)
		{
			gxdMatrixHandler.assignOpenCloseState(parentTerms,query,edges);
		}

		// add to the response object
		GxdStageGridJsonResponse<GxdPhenoMatrixCell> jsonResponse = new GxdStageGridJsonResponse<GxdPhenoMatrixCell>();
		jsonResponse.setParentTerms(parentTerms);
		jsonResponse.setGxdMatrixCells(gxdMatrixCells);

		logger.debug("sending json response");
		return jsonResponse;
	}

	/* Get a list of recombinase cells for a recombinase matrix for the given marker.  If non-null childrenOf
	 * will specify a single EMAPA ID whose child rows we want to retrieve.  Assumes markerID is not null.
	 */
	private List<SolrRecombinaseMatrixCell> getRecombinaseCells (String markerID, String childrenOf) {
		List<Filter> queryFilters = new ArrayList<Filter>();
		Filter markerIDFilter = new Filter(SearchConstants.CM_MARKER_ID, markerID);
		queryFilters.add(markerIDFilter);
		
		if ((childrenOf != null) && (childrenOf.trim().length() > 0)) {
			// subsequent searches will always request the children of a particular parent ID
			List<Filter> parentFilters = new ArrayList<Filter>();
			for (String parentID : childrenOf.split(",")) {
				parentFilters.add(new Filter(SearchConstants.CM_PARENT_ANATOMY_ID, parentID));
			}
			queryFilters.add(Filter.or(parentFilters));
		} else {
			// initial search should include cells for mouse, child terms of mouse, and child terms of organ system
			String mouseId = "EMAPA:25765";
			String organSystemId = "EMAPA:16103";
			List<Filter> termFilters = new ArrayList<Filter>();
			List<Filter> parentFilters = new ArrayList<Filter>();
			parentFilters.add(new Filter(SearchConstants.CM_PARENT_ANATOMY_ID, mouseId));
			parentFilters.add(new Filter(SearchConstants.CM_PARENT_ANATOMY_ID, organSystemId));
			termFilters.add(new Filter(SearchConstants.ANATOMY_ID, mouseId));
			termFilters.add(Filter.or(parentFilters));
			queryFilters.add(Filter.or(termFilters));
		}

		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(100000));
		params.setFilter(Filter.and(queryFilters));

		SearchResults<SolrRecombinaseMatrixCell> searchResults = recombinaseMatrixCellFinder.getCells(params);
		return searchResults.getResultObjects();
	}
	
	// Retrieve the data for a single recombinase cell in an expression x recombinase activity matrix.
	private SolrRecombinaseMatrixCell getRecombinaseCell(String markerID, String anatomyID, String alleleID) {
		List<Filter> queryFilters = new ArrayList<Filter>();
		if ((markerID != null) && (markerID.trim().length() > 0)) {
			queryFilters.add(new Filter(SearchConstants.CM_MARKER_ID, markerID));
		}
		if ((anatomyID != null) && (anatomyID.trim().length() > 0)) {
			queryFilters.add(new Filter(SearchConstants.ANATOMY_ID, anatomyID));
		}
		if ((alleleID != null) && (alleleID.trim().length() > 0)) {
			queryFilters.add(new Filter(SearchConstants.COLUMN_ID, alleleID));
		}
		
		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(100000));
		params.setFilter(Filter.and(queryFilters));

		SearchResults<SolrRecombinaseMatrixCell> searchResults = recombinaseMatrixCellFinder.getCells(params);
		if (searchResults.getTotalCount() > 1) {
			logger.error("getRecombinaseCell(" + markerID + "," + anatomyID + "," + alleleID + ") expected 1 cell, got " + searchResults.getTotalCount());
		} else if (searchResults.getTotalCount() == 0) {
			return null;
		}
		return searchResults.getResultObjects().get(0);
	}

	/* Get a list of MP cells for a correlation matrix for the given marker.  If non-null childrenOf
	 * will specify a single EMAPA ID whose child rows we want to retrieve.  Assumes markerID is not null.
	 */
	private List<SolrMPCorrelationMatrixCell> getMPCells (String markerID, String childrenOf) {
		List<Filter> queryFilters = new ArrayList<Filter>();
		Filter markerIDFilter = new Filter(SearchConstants.CM_MARKER_ID, markerID);
		queryFilters.add(markerIDFilter);
		
		if ((childrenOf != null) && (childrenOf.trim().length() > 0)) {
			// subsequent searches will always request the children of a particular parent ID
			List<Filter> parentFilters = new ArrayList<Filter>();
			for (String parentID : childrenOf.split(",")) {
				parentFilters.add(new Filter(SearchConstants.CM_PARENT_ANATOMY_ID, parentID));
			}
			queryFilters.add(Filter.or(parentFilters));
		} else {
			// initial search should include cells for mouse, child terms of mouse, and child terms of organ system
			String mouseId = "EMAPA:25765";
			String organSystemId = "EMAPA:16103";
			List<Filter> termFilters = new ArrayList<Filter>();
			List<Filter> parentFilters = new ArrayList<Filter>();
			parentFilters.add(new Filter(SearchConstants.CM_PARENT_ANATOMY_ID, mouseId));
			parentFilters.add(new Filter(SearchConstants.CM_PARENT_ANATOMY_ID, organSystemId));
			termFilters.add(new Filter(SearchConstants.ANATOMY_ID, mouseId));
			termFilters.add(Filter.or(parentFilters));
			queryFilters.add(Filter.or(termFilters));
		}

		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(100000));
		params.setFilter(Filter.and(queryFilters));

		SearchResults<SolrMPCorrelationMatrixCell> searchResults = mpCorrelationMatrixCellFinder.getCells(params);
		return searchResults.getResultObjects();
	}
	
	/* Retrieves the list of MP IDs we should highlight for the given structureID / genoclusterKey pair.
	 * This includes MP IDs mapped to either structureID or its descendants, as they contribute 
	 * annotations to the grid cell defined by structureID and genoclusterKey.
	 */
	@RequestMapping("/phenogrid/annotated_pheno_ids")
	public @ResponseBody String phenoGridAnnotatedPhenoIDs(
		@RequestParam(value="genoclusterKey") String genoclusterKey,
		@RequestParam(value="structureID") String structureID) {

		// get the anatomy term, so we can pick up the structure key
		List<VocabTerm> structureTerms = vocabFinder.getTermByID(structureID);
		if (structureTerms == null || structureTerms.size() == 0) {
			return "";
		}
		Integer structureKey = structureTerms.get(0).getTermKey();
		
		// build filters based on input parameters
		List<Filter> queryFilters = new ArrayList<Filter>();
		queryFilters.add(new Filter(SearchConstants.GENOCLUSTER_KEY, genoclusterKey));

		List<Filter> termFilters = new ArrayList<Filter>();
		termFilters.add(new Filter(SearchConstants.ANCESTOR_ANATOMY_KEY, structureKey.toString()));
		termFilters.add(new Filter(SearchConstants.ANATOMY_ID, structureID));
		queryFilters.add(Filter.or(termFilters));

		// execute the search to bring back all grid cells that would have contributed to that one
		// (searching by parentAnatomyID will bring back all cells of descendants of the structureID)
		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(100000));
		params.setFilter(Filter.and(queryFilters));
		SearchResults<SolrMPCorrelationMatrixCell> searchResults = mpCorrelationMatrixCellFinder.getCells(params);

		// build a set of all structure IDs that contributed annotations to the cell
		Set<String> emapaIDs = new HashSet<String>();
		emapaIDs.add(structureID);
		for (SolrMPCorrelationMatrixCell cell : searchResults.getResultObjects()) {
			emapaIDs.add(cell.getAnatomyID());
		}
		
		// now build a set of all MP IDs that are mapped to those structure IDs
		Set<String> mpIDs = new HashSet<String>();
		for (String emapaID : emapaIDs) {
			for (VocabTerm emapaTerm : vocabFinder.getTermByID(emapaID)) {
				for (RelatedTermBackward relationship : emapaTerm.getRelatedTermsBackward()) {
					if ("MP to EMAPA".equals(relationship.getRawRelationshipType())) {
						mpIDs.add(relationship.getRelatedTerm().getPrimaryId());
					}
				}
			}
		}

		// compose our output string
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (String mpID : mpIDs) {
			if (!first) { sb.append(","); }
			else { first = false; }
			sb.append(mpID);
		}
	
		return sb.toString();
	}

	@RequestMapping("/genegrid/json")
	public @ResponseBody GxdStageGridJsonResponse<GxdMatrixCell> gxdGeneGridJson(
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
			return new GxdStageGridJsonResponse<GxdMatrixCell>();
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
		GxdStageGridJsonResponse<GxdMatrixCell> jsonResponse = new GxdStageGridJsonResponse<GxdMatrixCell>();
		jsonResponse.setParentTerms(parentTerms);
		jsonResponse.setGxdMatrixCells(gxdMatrixCells);

		logger.debug("sending json response");
		return jsonResponse;
	}

	/*
	 * GXD Tissue by Gene/Phenotypes grid
	 */
	@RequestMapping(value="/phenogrid/{mrkID}")
	public ModelAndView gxdPhenoGrid(
			HttpServletRequest request,
			@PathVariable("mrkID") String mrkID,
			@RequestParam(value="genoclusterKey", required=false) String genoclusterKey) {

		logger.debug("->gxdPhenoGrid started");

		ModelAndView mav = new ModelAndView("gxd/gxd_phenogrid");
		
        // find the requested marker
        List<Marker> markerList = markerFinder.getMarkerByPrimaryId(mrkID);
        if (markerList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        } else if (markerList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        mav.addObject("marker", markerList.get(0));		
		
		// setup view object
		mav.addObject("mrkID", mrkID);
		mav.addObject("genoclusterKey", genoclusterKey);
		mav.addObject("queryString", request.getQueryString());

		logger.debug("gxdPhenoGrid routing to view ");
		return mav;
	}

	/*
	 * GXD Tissue by Gene/Recombinase grid
	 */
	@RequestMapping(value="/recombinasegrid/{mrkID}")
	public ModelAndView gxdGeneRecomGrid(
			HttpServletRequest request,
			@PathVariable("mrkID") String mrkID,
			@RequestParam(value="alleleID", required=false) String alleleID) {

		logger.debug("->gxdGeneRecomGrid started");

		ModelAndView mav = new ModelAndView("gxd/gxd_recombinasegrid");
		
        // find the requested marker
        List<Marker> markerList = markerFinder.getMarkerByPrimaryId(mrkID);
        if (markerList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        } else if (markerList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        mav.addObject("marker", markerList.get(0));		
		
		// setup view object
		mav.addObject("mrkID", mrkID);
		mav.addObject("alleleID", alleleID);
		mav.addObject("queryString", request.getQueryString());

		logger.debug("gxdGeneRecomGrid routing to view ");
		return mav;
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
		gxdLitTSMap.put(28,Arrays.asList("P")); //GXD Lit Option for postnatal
	}
	private static Map<String,List<String>> gxdLitAssayTypeMap = new HashMap<String,List<String>>();
	static
	{
		// Only assay type mappings that are not an exact 1:1 are defined
		gxdLitAssayTypeMap.put("Immunohistochemistry", Arrays.asList("Immunohistochemistry (section)","Immunohistochemistry (whole mount)"));
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
					mappedAges.add("P");
				}
				else if(age.equalsIgnoreCase(GxdQueryForm.EMBRYONIC))
				{
					// Need to select every age except A (and ANY)
					// loop through all the defaults and exclude the options we don't want
					for(String mappedAge : gxdLitForm.getAges().keySet())
					{
						if(!mappedAge.equals("ANY") && !mappedAge.equals("P"))
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
				|| (query.getDifTheilerStage().size() > 0)
				|| (query.getAnywhereElse() != "" && query.getAnywhereElse().trim().length() > 0);
	}

	/* This method creates the differential part 1 filters (used by resolveDifferentialMarkers() below)
	 * for cases where the "NOT anywhere else" checkbox has been checked.  It seemed simpler and more
	 * maintainable than trying to work it into the existing complex code.
	 */
	private void buildNowhereElseFilters(List<Filter> queryFilters, String structureID, List<Integer> stages) {
		/* If we got here, we have one of three cases:
		 * 1. user specified a single structure and checked "AND NOT anywhere else"
		 * 2. user specified 1+ Theiler stages and checked "AND NOT anywhere else"
		 * 3. user specified both a structure and 1+ Theiler stages and checked "AND NOT anywhere else"
		 */
		logger.info("entering buildNowhereElseFilters() with " + queryFilters.size() + " filters");
		boolean hasStructure = (structureID != null) && (structureID.trim().length() > 0);
		boolean hasStages = (stages != null) && (stages.size() > 0);
		
		List<Filter> diffFilters = new ArrayList<Filter>();
		
		// Look for the single specified structure (by key) in the exclusive structures field.
		if (hasStructure) {
			List<VocabTerm> structureList = vocabFinder.getTermByID(structureID);
			if (structureList.size() > 0) {
				diffFilters.add(new Filter(GxdResultFields.DIFF_EXCLUSIVE_STRUCTURES, 
					structureList.get(0).getTermKey(), Filter.Operator.OP_EQUAL));
			} else {
				logger.error("Cannot find key of EMAPA ID: " + structureID);
			}
		} 
		
		// Ensure that no other stages (other than those specified) appear in the exclusive stages field.
		if (hasStages && !stages.contains(GxdQueryForm.ANY_STAGE)) {
			for (int stage = 1; stage <= 28; stage++) {
				if (!stages.contains(stage)) {
					diffFilters.add(new Filter(GxdResultFields.DIFF_EXCLUSIVE_STAGES, stage, Filter.Operator.OP_NOT_HAS));
				}
			}
		}
		
		if (diffFilters.size() > 1) {
			queryFilters.add(Filter.and(diffFilters));
		} else if (diffFilters.size() == 1) {
			queryFilters.add(diffFilters.get(0));
		}
		logger.info("exiting with " + queryFilters.size() + " filters");
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
		boolean nowhereElse = (query.getAnywhereElse() != null) && (query.getAnywhereElse().trim().length() > 0);

		if (!nowhereElse) {
			if (query.getAnywhereElse() == null) {
				logger.info("anywhereElse is null");
			} else if (query.getAnywhereElse().trim().length() == 0) {
				logger.info("anywhereElse is empty string");
			}
		}
		// handle the case on the differential form where the "AND NOT anywhere else" checkbox is checked
		if (nowhereElse) {
			logger.info("building nowhereElse filters");
			buildNowhereElseFilters(queryFilters, structure, stages);
			logger.info("returned from building nowhereElse filters");
		}
		// perform structure diff
		else if(hasStructures && !hasStages)
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
	
	/* For differential searches where the 'AND NOT anywhere else' checkbox was checked...  Creates the
	 * differential part 2 filters that goes against the gxdResult index.  Split out into a separate method
	 * to make more maintainable in the future.
	 */
	private void buildNowhereElsePart2Filters(List<Filter> queryFilters, String structureID, List<Integer> stages) {
		// Need to return a set of positive results and a set of negative results.  Positive results should
		// match by structure, stage, or both, depending on whether either or both were specified.

		List<Filter> myFilters = new ArrayList<Filter>();
		
		// Create the positive results filter based on structure and/or stages specified.  This should return
		// all the "detected" results for each of the genes in the set selected by the part1 filters.

		List<Filter> posFilters = new ArrayList<Filter>();

		if ((structureID != null) && (structureID.trim().length() > 0)) {
			posFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID, structureID));
		}
			
		if ((stages != null) && (stages.size() > 0)) {
			if (!stages.contains(GxdQueryForm.ANY_STAGE)) {
				List<Filter> stageFilters = new ArrayList<Filter>();
				for(Integer stage : stages) {
					stageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE, stage, Filter.Operator.OP_HAS_WORD));
				}
				// OR the stages together
				posFilters.add(Filter.or(stageFilters));
			}
		}
			
		if (posFilters.size() > 0) {
			posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
			myFilters.add(Filter.and(posFilters));
		}

		// Create the negative results filter based on structure and/or stages specified.  This should return
		// the "not detected" results for each of the genes in the set selected by the part1 filters.
		
		List<Filter> negFilters = new ArrayList<Filter>();

		if ((structureID != null) && (structureID.trim().length() > 0)) {
			// Structure ID field also has ancestor IDs, so this should ensure that we avoid the specified
			// structure and its descendants.
			negFilters.add(new Filter(SearchConstants.STRUCTURE_ID, structureID, Filter.Operator.OP_EQUAL));
		}
			
		if ((stages != null) && (stages.size() > 0)) {
			if (!stages.contains(GxdQueryForm.ANY_STAGE)) {
				List<Filter> stageFilters = new ArrayList<Filter>();
				// User specified 1+ stages, so we need to make sure that the results are outside those stages.
				for(Integer stage : stages) {
					stageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE, stage, Filter.Operator.OP_HAS_WORD));
				}
				negFilters.add(Filter.or(stageFilters));
			}
		}
		
		if (negFilters.size() > 0) {
			// 1. We always want detected=No for these negative results.
			// 2. If both structure and stage are filled in, then we should OR those together, since if
			//		either is different, we would want to return the record.

			List<Filter> combinedNegFilters = new ArrayList<Filter>();
			combinedNegFilters.add(new Filter(SearchConstants.GXD_DETECTED, "No", Filter.Operator.OP_EQUAL));
			Filter poolFilter = Filter.and(negFilters);
			poolFilter.negate();
			combinedNegFilters.add(poolFilter);
			myFilters.add(Filter.and(combinedNegFilters));
		}
		
		// Find results matching either the positive criteria or the negative criteria.
		queryFilters.add(Filter.or(myFilters));
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
		boolean nowhereElse = (query.getAnywhereElse() != null) && (query.getAnywhereElse().trim().length() > 0);

		// if user checked 'AND NOT anywhere else' checkbox, handle that separately
		if (nowhereElse) {
			buildNowhereElsePart2Filters(queryFilters, structure, stages);
		}
		// perform structure diff
		else if(hasStructures && !hasStages)
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
			logger.info("In differential form processing");
			// Process DIFFERENTIAL QUERY FORM params
			// Do part 1 of the differential (I.e. find out what markers to bring back)
			List<String> markerKeys = resolveDifferentialMarkers(query);
			logger.info("Found marker keys: " + markerKeys.toString());
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
				String spatialQueryString = SolrLocationTranslator.getQueryValue(token, query.getLocationUnit());
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

	// Returns a json mgi id list based on the filters that come in from the query form
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
	
	/*
	 * Parses requested sort parameters for gxd marker image summary.
	 */
	private List<Sort> parseImageSorts(HttpServletRequest request) {

		logger.debug("->parseImageSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied
		String sortRequested = request.getParameter("sort");

		// empty
		if (sortRequested == null) {
			return sorts;
		}
		

		String dirRequested  = request.getParameter("dir");
		boolean desc = false;
		if("desc".equalsIgnoreCase(dirRequested)){
			desc = true;
		}

		// expected sort values
		if ("hybridization".equalsIgnoreCase(sortRequested)){
			
			// There is a custom sort on hybridization/"Specimen Type" for descending vs ascending
			//   Not Specified and blot assays always sort to the bottom either way
			if (desc) {
				sorts.add(new Sort(SortConstants.BY_IMAGE_HYBRIDIZATION_DESC, false));
			}
			else {
				sorts.add(new Sort(SortConstants.BY_IMAGE_HYBRIDIZATION_ASC, false));
			}
			
		} else if ("gene".equalsIgnoreCase(sortRequested)){
			
			sorts.add(new Sort(SortConstants.BY_IMAGE_MARKER, desc));
			
		} else {
			
			sorts.add(new Sort(SortConstants.BY_IMAGE_ASSAY_TYPE, desc));
		}


		logger.debug ("sort: " + StringUtils.join(sorts, ", "));
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

		// sort using byAssayType
		params.setSorts(this.parseImageSorts(request));
		
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
	
	// redirects to the matrix for the full set of gxd data
	@RequestMapping("/tissue_matrix")
	public ModelAndView getFullDataMatrix() {
		String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
		return new ModelAndView("redirect:" + fewiUrl + "gxd#gxd=nomenclature%3D%26vocabTerm%3D%26annotationId%3D%26locations%3D%26structure%3D%26structureID%3D%26theilerStage%3D0%26results%3D100%26startIndex%3D0%26sort%3D%26dir%3Dasc%26tab%3Dstagegridtab");
	}

	/* -----------------------------------------------------------------
	 * Methods in this section handle URLs for GXD High-Throughput data, 
	 * passing them on to separate controller.
	 * -----------------------------------------------------------------
	 */
	
	// HT query form
	@RequestMapping(value="/htexp_index", method=RequestMethod.GET)
	public ModelAndView htQueryForm(HttpServletResponse response) {
		return gxdhtController.getQueryForm(response);
	}
	
	// HT summary page (no results table -- that's injected by Javascript)
	@RequestMapping("/htexp_index/summary")
	public ModelAndView gxdHtSummary(HttpServletRequest request, @ModelAttribute GxdHtQueryForm queryForm) {
		return gxdhtController.gxdHtSummary(request, queryForm);
	}
	
	// HT sample popup (expects ArrayExpress ID)
	@RequestMapping(value="/htexp_index/samples/{experimentID:.+}", method = RequestMethod.GET)
	public ModelAndView htSamplePopup(@PathVariable("experimentID") String experimentID, @ModelAttribute GxdHtQueryForm queryForm) {
		return gxdhtController.gxdHtSamples(experimentID, queryForm);
	}
	
	// HT result table to inject into summary page (retrieve via Ajax)
	@RequestMapping("/htexp_index/table")
	public ModelAndView htExperimentsTable (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {
		return gxdhtController.experimentsTable(request, query, page);
	}
}
