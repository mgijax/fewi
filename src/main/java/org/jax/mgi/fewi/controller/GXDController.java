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
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.owasp.encoder.Encode;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.Genotype;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fe.datamodel.RelatedTermBackward;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fe.datamodel.VocabTermEmapInfo;
import org.jax.mgi.fe.datamodel.hdp.HdpGenoCluster;
import org.jax.mgi.fe.datamodel.sort.SmartAlphaComparator;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.CdnaFinder;
import org.jax.mgi.fewi.finder.GxdBatchFinder;
import org.jax.mgi.fewi.finder.GxdFinder;
import org.jax.mgi.fewi.finder.MPCorrelationMatrixCellFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.RecombinaseFinder;
import org.jax.mgi.fewi.finder.RecombinaseMatrixCellFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.finder.ExpressionHelperFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.forms.GxdHtQueryForm;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.forms.GxdQueryForm;
import org.jax.mgi.fewi.forms.QuickSearchQueryForm;
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
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.MatrixPaginator;
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
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPCorrelationMatrixCell;
import org.jax.mgi.fewi.searchUtil.entities.SolrRecombinaseMatrixCell;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.GxdAssaySummaryRow;
import org.jax.mgi.fewi.summary.GxdCountsSummary;
import org.jax.mgi.fewi.summary.GxdImageSummaryRow;
import org.jax.mgi.fewi.summary.GxdMarkerSummaryRow;
import org.jax.mgi.fewi.summary.GxdRnaSeqHeatMapMarker;
import org.jax.mgi.fewi.summary.GxdRnaSeqHeatMapSample;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.fe.IndexConstants;
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
	private static String MARKERTYPE_DISPLAY = "markerTypeDisplay";	// custom case
	private static String TPM_LEVEL_SORT = "tpmLevelSort";	// custom case
	
	// caches of objects for RNA-Seq Heat Map generation
	private static Map<String, GxdRnaSeqHeatMapMarker> hmMarkers = new HashMap<String, GxdRnaSeqHeatMapMarker>();
	private static Map<String, GxdRnaSeqHeatMapSample> hmSamples = new HashMap<String, GxdRnaSeqHeatMapSample>();
	
	// maps from EMAPA header term to its ID (for caching)
	private static Map<String, String> emapaHeaders = new HashMap<String, String>();
	
	// --------------------//
	// instance variables
	// --------------------//

	private final Logger logger = LoggerFactory.getLogger(GXDController.class);

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
	private ExpressionHelperFinder expressionHelper;

	@Autowired
	private QuickSearchController qsController;

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
	public ModelAndView getQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->getQueryForm started");

		ModelAndView mav = new ModelAndView("gxd/gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());

		return mav;
	}

	// "expanded" differential query form
	@RequestMapping("differential")
	public ModelAndView getDifferentialQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->getDifferentialQueryForm started");

		ModelAndView mav = new ModelAndView("gxd/gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showDifferentialQueryForm",true);

		return mav;
	}

	// "expanded" profile query form
	@RequestMapping("profile")
	public ModelAndView getProfileQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->getDifferentialQueryForm started");

		ModelAndView mav = new ModelAndView("gxd/gxd_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", new GxdQueryForm());
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showProfileQueryForm",true);

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));
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
			HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
			mav.addObject("queryString", FormatHelper.cleanJavaScript("batchSubmission=true&" + request.getQueryString() + idList));
		} else {
			mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));
		}

		return mav;
	}

	// "batch search" forwarded from quick search Features tab.
	@RequestMapping("batchForward")
	public ModelAndView forwardToBatchSearchForm(HttpSession session, @ModelAttribute QuickSearchQueryForm qsQF,
		HttpServletRequest request) {

		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->forwardToBatchSearchForm started");

		// Query string and filter values come in from Quick Search.  Use the QS controller to look up markers.
		
		StringBuffer markerIDs = new StringBuffer();
		for (QSFeatureResult qsResult : qsController.getFeatureResults(request, qsQF)) {
			markerIDs.append(qsResult.getPrimaryID());
			markerIDs.append(" ");
		}

		// Add the marker IDs to the GXD batch QF.

		GxdQueryForm gxdQF = new GxdQueryForm();
		gxdQF.setIds(markerIDs.toString());

		// If the set of IDs doesn't auto-populate into the field, we can updated it in JQuery.
		
		ModelAndView mav = new ModelAndView("gxd/gxd_query");
		mav.addObject("markerIDs", markerIDs.toString());

		// boilerplate
		
		mav.addObject("gxdQueryForm", new GxdQueryForm());
		mav.addObject("gxdBatchQueryForm", gxdQF);
		mav.addObject("gxdDifferentialQueryForm", new GxdQueryForm());
		mav.addObject("showBatchSearchForm",true);
		
		// Translate the anatomy filter value from terms to term IDs, to be added to the GXD batch QF as structureIDFilter.
		
		List<String> structureIDs = translateStructureFilterValues(qsQF.getExpressionFilterF());

		String queryString = "ids=" + gxdQF.getIds();
		for (String headerID : structureIDs) {
//			queryString = queryString + "&structureIDFilter=" + headerID;
		}
		
		mav.addObject("queryString", queryString);
		mav.addObject("structureIDs", structureIDs);
		return mav;
	}
	
	private List<String> translateStructureFilterValues(List<String> terms) {
		List<String> structureIDs = new ArrayList<String>();
		if ((terms != null) && (terms.size() > 0)) {
			for (String term : terms) {
				// If we've already seen this header, just use the cached lookup of the ID.
				if (emapaHeaders.containsKey(term)) {
					logger.info("emapaHeaders[" + term + "] = " + emapaHeaders.get(term));
					structureIDs.add(emapaHeaders.get(term));
				} else {
					// Otherwise, look up the record from Solr and add it to the cache.
					Filter exactTerm = new Filter(SearchConstants.STRUCTURE, term, Filter.Operator.OP_EQUAL);

					SearchParams params = new SearchParams();
					params.setPaginator(new Paginator());
					params.setFilter(exactTerm);
					List<SolrAnatomyTerm> termList = vocabFinder.getAnatomyTerms(params).getResultObjects();
					logger.info("Filter = " + exactTerm.toString());
					logger.info("Found " + termList.size() + " terms");
					
					if ((termList != null) && (termList.size() > 0)) {
						SolrAnatomyTerm header = termList.get(0);
						logger.info("Found term " + header.toString());
						if (header.getAccID() != null) {
							structureIDs.add(header.getAccID());
							logger.info("Found term with ID " + header.getAccID());
						}
					}
				}
			}
		}
		logger.info("Returning " + structureIDs.size() + " IDs");
		return structureIDs;
	}
	
	/*
	 * generic summary report
	 */
	@RequestMapping("/summary")
	public ModelAndView genericSummary(
			@ModelAttribute GxdQueryForm query,
			HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));
		return mav;

	}


	/*
	 * Summary by EMAPA/EMAPS ID
	 */
	@RequestMapping(value="/structure/{emapID}")
	public ModelAndView summeryByStructureId(
			HttpServletRequest request,
			@PathVariable("emapID") String emapID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

		logger.debug("summaryByStructureId routing to view ");
		return mav;
	}

	/*
	 * Summary by RNA-Seq experiment ID
	 */
	@RequestMapping(value="/experiment/{experimentID}")
	public ModelAndView summaryByExperimentId(
			HttpServletRequest request,
			@PathVariable("experimentID") String experimentID,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) throws BindException {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->summaryByExperimentId started");

		// setup view object
		ModelAndView mav = new ModelAndView("gxd/gxd_summary_by_experiment");

		// set up QF object to search for a single RNA-Seq experiment
		Paginator page = new Paginator(1);

		List<String> assayType = new ArrayList<String>();
		assayType.add("RNA-Seq");
		GxdQueryForm qf = new GxdQueryForm();
		qf.setAssayType(assayType);
		qf.setExperimentID(experimentID);

		SearchResults<SolrGxdAssay> searchResults = getGxdAssays(request, qf, page, result);
		List<SolrGxdAssay> assayList = searchResults.getResultObjects();

		// there can be only one...
		if (assayList.size() < 1) {
			// forward to error page
			return errorMav("No RNA-Seq experiment found for " + experimentID);
		}
		if (assayList.size() > 1) {
			// forward to error page
			return errorMav("Duplicate experiment found for " + experimentID);
		}
		SolrGxdAssay assay = assayList.get(0);
		mav.addObject("experiment", assay);
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

		logger.debug("summaryByExperimentId routing to view ");
		return mav;
	}

	/*
	 * Summary by Reference
	 */
	@RequestMapping(value="/reference/{refID}")
	public ModelAndView summeryByRefId(
			HttpServletRequest request,
			@PathVariable("refID") String refID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->cdnaSummaryByMarkerID started");
		
		ModelAndView mav = new ModelAndView("gxd/cdna_summary_by_marker");
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

		// get the marker object (if possible) and add it to the mav
		String error = addMarkerToMav(mav, mrkID);
		if (error != null) {
			return errorMav(error);
		}

		logger.debug("->cdnaSummaryByMarkerID routing to mav");
		return mav;
	}
	
	/* cDNA table of results (to be requested by Ajax)
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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->summeryByMrkId started");

		// setup view object
		ModelAndView mav = new ModelAndView("gxd/gxd_summary_by_marker");

		String error = addMarkerToMav(mav, mrkID);
		if (error != null) {
			return errorMav(error);
		}

		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

		// allow the wild-type filter to be applied as a GET parameter
		
		String wildtypeFilter = request.getParameter("wildtypeFilter");
		if (wildtypeFilter != null) {
			mav.addObject("wildtypeFilter", wildtypeFilter);
		}
		
		// handle requests for a specific Theiler Stage
		String theilerStage = request.getParameter("theilerStage");
		if (theilerStage != null) {
			mav.addObject("theilerStage", theilerStage);
		}else {
			mav.addObject("theilerStage", "");
		}

		// handle requests for a specific summary tab (excluding non-alphanumeric characters)
		String tab = request.getParameter("tab");
		if(tab != null) mav.addObject("tab", tab.replaceAll("[^a-zA-Z0-9_]", ""));

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
	 * Forward to MGI Batch Query (from Genes tab of GXD summary page)
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
	 * This method maps ajax requests from the expression summary page.  It
	 * parses the GxdQueryForm, generates SearchParams object, and issues
	 * the query to the GxdFinder.  The results are returned as JSON
	 */
	@RequestMapping("/markers/json")
	public @ResponseBody JsonSummaryResponse<GxdMarkerSummaryRow> gxdMarkerSummaryJson(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {

		logger.info("gxdMarkerSummaryJson() started");
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
		JsonSummaryResponse<GxdMarkerSummaryRow> jsonResponse = new JsonSummaryResponse<GxdMarkerSummaryRow>();
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

	@RequestMapping("/rnaSeqHeatMap")
	public ModelAndView gxdRnaSeqHeatMap(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query
			) throws CloneNotSupportedException, UnsupportedEncodingException {
		logger.info("gxdRnaSeqHeatMap() started");
		
		ModelAndView mav = new ModelAndView("gxd/gxd_rnaseq_heatmap");
		mav.addObject("queryString", FormatHelper.cleanJavaScript(getQueryString(request)));
		mav.addObject("ysf", getRnaSeqHeatMapYSF(query));
		
		return mav;
	}

	// Returns a String representing the "You Searched For" text describing the given 'query'.
	// Notes:
	//	1. This is only for the RNA-Seq heat map, not the regular QF summary.
	//	2. Since we do not support heat maps from the differential QF or the batch QF, their idiosyncrasies are not considered.
	private String getRnaSeqHeatMapYSF(GxdQueryForm originalQF) throws CloneNotSupportedException {
		// Create a clone of the given query object and overwrite base values with more specific filter values as appropriate.
		GxdQueryForm query = (GxdQueryForm) originalQF.clone();
		
		// Overwrite form's detected selection (if any) with filter value.
		if ((query.getDetectedFilter() != null) && (query.getDetectedFilter().size() > 0)) {
			if (query.getDetectedFilter().size() > 1) {
				query.setDetected("Yes</B> or <B>No");
			} else {
				query.setDetected(Encode.forHtml(query.getDetectedFilter().get(0)));
			}
		}
		
		// Did we have a filter for only mutant specimens?  (will need to override background if so)
		boolean mutantFilter = false;
		
		// Overwrite form's wild type selection (if any) with filter value.  If form's value is more specific,
		// keep that one.  (Do not overwrite a selected marker with "any".)
		if ((query.getWildtypeFilter() != null) && (query.getWildtypeFilter().size() > 0)) {
			if (query.getWildtypeFilter().size() == 2) {
				// Both mutant and wild type are selected.  This is only possible when the original field's
				// selection is "all specimens," so we don't really need to change it.  No op.
			} else if ("mutant".equals(query.getWildtypeFilter().get(0))) {
				mutantFilter = true;
			} else {
				query.setIsWildType("true");
			}
		}
		
		// Overwrite form's TS selection (if any) with filter value (convert to integers first).
		if ((query.getTheilerStageFilter() != null) && (query.getTheilerStageFilter().size() > 0)) {
			List<Integer> filteredTS = new ArrayList<Integer>();
			for (String ts : query.getTheilerStageFilter()) {
				try {
					filteredTS.add(Integer.parseInt(ts));
				} catch (Exception e) {}
			}
			query.setTheilerStage(filteredTS);
		}
	
		// list of lines to concatenate
		List<String> lines = new ArrayList<String>();
		lines.add(FormatHelper.bold("You Searched For:"));

		// temporary variable for composing each line
		StringBuffer sb = null;
		
		// other gene-related fields (various ID and symbol fields)
		List<String> idsToDo = new ArrayList<String>();
		List<String> symbolsToDo = new ArrayList<String>();
		
		if ((query.getMarkerIDs() != null) && (query.getMarkerIDs().size() > 0)) {
			idsToDo.addAll(query.getMarkerIDs());
		}
		if ((query.getMarkerMgiId() != null) && (!"".equals(query.getMarkerMgiId()))) {
			idsToDo.add(query.getMarkerMgiId());
		}
		
		if (idsToDo.size() > 0) {
			for (String markerID : idsToDo) {
				List<Marker> markersByID = markerFinder.getMarkerByPrimaryId(markerID);
				if ((markersByID != null) && (markersByID.size() > 0)) {
					for (Marker m : markersByID) {
						if (!symbolsToDo.contains(m.getSymbol())) {
							symbolsToDo.add(m.getSymbol());
						}
					}
				}
			}
		}
		
		if ((query.getMarkerSymbol() != null) && (!"".equals(query.getMarkerSymbol()))) {
			if (!symbolsToDo.contains(query.getMarkerSymbol())) {
				symbolsToDo.add(query.getMarkerSymbol());
			}
		}
		if ((query.getMatrixMarkerSymbol() != null) && (!"".equals(query.getMatrixMarkerSymbol()))) {
			if (!symbolsToDo.contains(query.getMatrixMarkerSymbol())) {
				symbolsToDo.add(query.getMatrixMarkerSymbol());
			}
		}
		List<String> markerSymbols = query.getMarkerSymbolFilter();
		if ((markerSymbols != null) && (markerSymbols.size() > 0)) {
			for (String symbol : markerSymbols) {
				if (!symbolsToDo.contains(symbol)) {
					symbolsToDo.add(symbol);
				}
			}
		}
		
		if (symbolsToDo.size() > 0) {
			Collections.sort(symbolsToDo);
			sb = new StringBuffer();
			sb.append("Gene(s): ");
			boolean isFirst = true;
			for (String symbol : symbolsToDo) {
				if (!isFirst) {
					sb.append(" or ");
				} else {
					isFirst = false;
				}
				sb.append(FormatHelper.bold(symbol));
			}
			lines.add(sb.toString());
		}
		
		// gene nomenclature field (if other fields didn't preclude it)
		if (symbolsToDo.size() == 0) {
			String nomenclature = query.getNomenclature();
			if ((nomenclature != null) && !"".equals(nomenclature)) {
				sb = new StringBuffer();
				sb.append("Gene nomenclature: ");
				sb.append(FormatHelper.bold(FormatHelper.cleanHtml(nomenclature)));
				sb.append(FormatHelper.smallGrey(" current symbol, name, synonyms"));
				lines.add(sb.toString());
			}
		}
		
		// allele
		String alleleID = query.getAlleleId();
		if ((alleleID != null) && (!"".equals(alleleID))) {
			List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
			if ((alleleList != null) && (alleleList.size() > 0)) {
				sb = new StringBuffer();
				sb.append("Allele: ");
				sb.append(FormatHelper.bold(FormatHelper.superscript(alleleList.get(0).getSymbol())));
				lines.add(sb.toString());
			}
		}
		
		// gene type filter
		if ((query.getMarkerTypeFilter() != null) && (query.getMarkerTypeFilter().size() > 0)) {
			sb = new StringBuffer();
			sb.append("Gene type: ");
			boolean isFirst = true;
			for (String mt : query.getMarkerTypeFilter()) {
				if (!isFirst) {
					sb.append(" or ");
				} else {
					isFirst = false;
				}
				sb.append(FormatHelper.bold(mt));
			}
			lines.add(sb.toString());
		}
		
		// chromosomal locations
		String locations = query.getLocations();
		if ((locations != null) && !"".equals(locations)) {
			String units = query.getLocationUnit();
			if (units == null) { units = "bp"; }
			
			sb = new StringBuffer();
			sb.append("Genome location(s): ");
			if (locations.indexOf(":") >= 0) {
				// location contains coordinate
				sb.append(FormatHelper.bold(FormatHelper.cleanHtml(locations + " " + units)));
			} else {
				// location is just a chromosome
				sb.append(FormatHelper.bold(FormatHelper.cleanHtml(locations)));
			}
			lines.add(sb.toString());
		}
		
		// vocab term -- annotation ID
		String vocabTerm = query.getVocabTerm();
		String annotationID = query.getAnnotationId();
		if ((vocabTerm != null) && (annotationID != null) && !"".equals(vocabTerm) && !"".equals(annotationID)) {
			String[] pieces = vocabTerm.split(" - ");
			sb = new StringBuffer();
			sb.append("Genes annotated to: ");
			if (pieces.length > 1) {
				sb.append(FormatHelper.bold(pieces[1] + ": " + pieces[0]));
			} else {
				sb.append(FormatHelper.bold(pieces[0]));
			}
			sb.append(FormatHelper.smallGrey(" includes subterms"));
			lines.add(sb.toString());
		}
		
		// annotation-based filters
		addAnnotationFilter(lines, "Molecular Function", query.getGoMfFilter());
		addAnnotationFilter(lines, "Biological Process", query.getGoBpFilter());
		addAnnotationFilter(lines, "Cellular Component", query.getGoCcFilter());
		addAnnotationFilter(lines, "Phenotype", query.getMpFilter());
		addAnnotationFilter(lines, "Disease", query.getDoFilter());
		
		// detected?
		String detectedText = query.getDetected();
		if ("Yes".equals(detectedText)) {
			detectedText = "Detected";
		} else if ("No".equals(detectedText)) {
			detectedText = "Not detected";
		} else {
			detectedText = "Assayed";
		}
		
		// structure (preference:  structure ID filter > structure ID > structure key > structure text field)
		String structure = query.getStructure();
		List<String> structureFilterID = query.getStructureIDFilter();
		String structureKey = query.getStructureKey();
		String structureID = query.getStructureID();
		String structureOut = null;					// the structure string we want to report to the user
		
		if ((structureFilterID != null) && (structureFilterID.size() > 0)) {
			// need to handle a list of terms for this one -- collect, sort, report 
			List<VocabTerm> vocabTerms = vocabFinder.getTermsByID(structureFilterID);

			if ((vocabTerms != null) && (vocabTerms.size() > 0)) {
				List<String> terms = new ArrayList<String>();
				for (VocabTerm term : vocabTerms) {
					terms.add(getTermText(term));
				}
				Collections.sort(terms);	

				boolean isFirst = true;
				StringBuffer tb = new StringBuffer();
				for (String t : terms) {
					if (!isFirst) {
						tb.append(" or ");
					} else {
						isFirst = false;
					}
					tb.append(FormatHelper.bold(FormatHelper.cleanHtml(t)));
				}
				structureOut = tb.toString(); 
			} else {
				structureOut = "(unknown structure)";
			}

		} else if ((structureID != null) && (!"".equals(structureID))) {
			List<VocabTerm> vocabTerms = vocabFinder.getTermByID(structureID);
			if ((vocabTerms != null) && (vocabTerms.size() > 0)) {
				structureOut = FormatHelper.bold(FormatHelper.cleanHtml(getTermText(vocabTerms.get(0))));
			} else {
				structureOut = "(unknown structure)";
			}
			
		} else if ((structureKey != null) && (!"".equals(structureKey))) {
			VocabTerm structureTerm = vocabFinder.getTermByKey(structureKey);
			if ((structureTerm != null) && (!"".equals(structureTerm))) {
				structureOut = FormatHelper.bold(FormatHelper.cleanHtml(getTermText(structureTerm)));
			} else {
				structureOut = "(unknown structure)";
			}
			
		} else if ((structure != null) && (!"".equals(structure))) {
			structureOut = FormatHelper.noScript(FormatHelper.noAlert(structure));
		}
		
		if (structureOut != null) {
			sb = new StringBuffer();
			sb.append(detectedText);
			sb.append(" in ");
			if ((structureOut != null) && !"".equals(structureOut)) {
				sb.append(FormatHelper.bold(structureOut));
				sb.append(FormatHelper.smallGrey(" includes substructures"));
			} else {
				sb.append(FormatHelper.bold("any structures"));
			}
			lines.add(sb.toString());
		}
		
		// experiment (preference: filter > single ID)
		String exptID = query.getExperimentID();
		List<String> exptIDs = query.getExperimentFilter();
		if ((exptIDs != null) && (exptIDs.size() > 0)) {
			sb = new StringBuffer();
			sb.append("Experiment: ");
			boolean isFirst = true;
			for (String experimentID : exptIDs) {
				if (!isFirst) {
					sb.append(" or ");
				} else {
					isFirst = false;
				}
				sb.append(FormatHelper.bold(experimentID));
			}
			lines.add(sb.toString());

		} else if ((exptID != null) && (!"".equals(exptID))) {
			sb = new StringBuffer();
			sb.append("Experiment: ");
			sb.append(FormatHelper.bold(exptID));
			lines.add(sb.toString());
		}
		
		// anatomical system
		if ((query.getSystemFilter() != null) && (query.getSystemFilter().size() > 0)) {
			sb = new StringBuffer();
			sb.append("within anatomical systems: ");
			boolean isFirst = true;
			for (String system : query.getSystemFilter()) {
				if (!isFirst) {
					sb.append(" or ");
				} else {
					isFirst = false;
				}
				sb.append(FormatHelper.bold(system));
			}
			lines.add(sb.toString());
		}
		
		// age
		if ((query.getAgesSelected() != null) && (!"".equals(query.getAgesSelected())) ) {
			sb = new StringBuffer();
			sb.append("at age(s): ");
			sb.append(FormatHelper.bold(query.getAgesSelected()));
			lines.add(sb.toString());
		}
		
		// Theiler stage (could show both age & TS because of filters)
		if ((query.getTheilerStage() != null) && (query.getTheilerStage().size() > 0)) {
			// skip display in only default TS of zero
			if ((query.getTheilerStage().size() > 1) || (query.getTheilerStage().get(0) != 0)) {
				sb = new StringBuffer();
				sb.append("at developmental stage(s): ");
				boolean isFirst = true;
				for (Integer ts : query.getTheilerStage()) {
					if (!isFirst) {
						sb.append(" or ");
					} else {
						isFirst = false;
					}
					sb.append(FormatHelper.bold("TS:" + ts.toString()));
				}
				lines.add(sb.toString());
			}
		}
		
		// genetic background & wild type
		if ((query.getMutatedIn() != null) && (!"".equals(query.getMutatedIn())) ) {
			sb = new StringBuffer();
			sb.append("Specimens: ");
			sb.append(FormatHelper.bold("Mutated in " + query.getMutatedIn()));
			sb.append(FormatHelper.smallGrey(" current symbol, name, synonyms"));
			lines.add(sb.toString());
		} else if ((query.getIsWildType() != null) && (!"".equals(query.getIsWildType())) ) {
			sb = new StringBuffer();
			sb.append("Specimens: ");
			sb.append(FormatHelper.bold("Wild type only"));
			lines.add(sb.toString());
		} else if (mutantFilter) {
			sb = new StringBuffer();
			sb.append("Specimens: ");
			sb.append(FormatHelper.bold("Mutant only"));
			lines.add(sb.toString());
		}
		
		// assay type
		sb = new StringBuffer();
		sb.append("Assayed by ");
		sb.append(FormatHelper.bold("(RNA-Seq)"));
		lines.add(sb.toString());
		
		// RNA-Seq (TPM) level
		if ((query.getTmpLevelFilter() != null) && (query.getTmpLevelFilter().size() > 0)) {
			sb = new StringBuffer();
			sb.append("TPM Level: ");

			boolean isFirst = true;
			for (String level : query.getTmpLevelFilter()) {
				if (!isFirst) {
					sb.append(" or ");
				} else {
					isFirst = false;
				}
				sb.append(FormatHelper.bold(level));
			}
			lines.add(sb.toString());
		}
		
		return StringUtils.join(lines, "<br/>");
	}
	
	private String getTermText (VocabTerm t) {
		if (t == null) return "";
		VocabTermEmapInfo emap = t.getEmapInfo();
		StringBuffer sb = new StringBuffer();
		sb.append(t.getTerm());

		if (emap != null) {
			if (emap.getStage() != null) {
				sb.append(" TS");
				sb.append(emap.getStage());
			} else if (emap.getStartStage() != null) {
				sb.append(" TS");
				sb.append(emap.getStartStage());
				if (emap.getEndStage() != null) {
					sb.append("-TS");
					sb.append(emap.getEndStage());
				}
			}
		} 
		return sb.toString();
	}

	// helper for getRnaSeqHeatMapYSF -- adds needed info to lines for the given filter (if it has values)
	private void addAnnotationFilter(List<String> lines, String filterName, List<String> terms) {
		if ((terms != null) && (terms.size() > 0)) {
			StringBuffer sb = new StringBuffer();
			sb.append("Genes associated with " + filterName + ": ");
			boolean isFirst = true;
			for (String term : terms) {
				if (!isFirst) {
					sb.append(" or ");
				} else {
					isFirst = false;
				}
				sb.append(FormatHelper.bold(term));
			}
			sb.append(FormatHelper.smallGrey(" includes subterms"));
			lines.add(sb.toString());
		}
	}
	
	// Get the count of documents for an RNA-Seq heat map.
	@RequestMapping("/rnaSeqHeatMap/totalCount")
	public @ResponseBody String gxdRnaSeqHeatMapTotalCount(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query
			) throws Exception {

		logger.info("gxdRnaSeqHeatMapTotalCount() started");
		populateMarkerIDs(session, query);

		List<String> assayType = new ArrayList<String>();
		assayType.add("RNA-Seq");
		
		SearchParams params = new SearchParams();
		query.setAssayType(assayType);
		params.setFilter(parseGxdQueryForm(query));
	
		Paginator page = new Paginator(0);
		page.setStartIndex(0);
		params.setPaginator(page);
		
		SearchResults<SolrGxdRnaSeqHeatMapResult> searchResults = gxdFinder.searchRnaSeqHeatMapResults(params);
		logger.info("gxdRnaSeqHeatMapTotalCount() returning: " + searchResults.getTotalCount());

		return Integer.toString(searchResults.getTotalCount());
	}

	// Get the count of documents for an RNA-Seq heat map.  'start' gives the record index to start with, while
	// 'end' gives us the one (after) which we should end; this is typical slicing behavior, gathering records
	// from start up to but not including end.
	// Transferring cell data as a comma-delimited string rather than a dictionary reduces data transit
	// by roughly 50%.
	@RequestMapping("/rnaSeqHeatMap/recordSlice")
	public @ResponseBody List<String> gxdRnaSeqHeatMapRecordSlice(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="start") String start,
			@RequestParam(value="end") String end
			) throws Exception {

		logger.info("gxdRnaSeqHeatMapRecordSlice() started");
		populateMarkerIDs(session, query);

		List<String> assayType = new ArrayList<String>();
		assayType.add("RNA-Seq");
		
		SearchParams params = new SearchParams();
		query.setAssayType(assayType);
		params.setFilter(parseGxdQueryForm(query));
	
		Integer startIndex = 0;
		Integer endIndex = 0;
		if (start.matches("[0-9]+")) startIndex = Integer.parseInt(start);
		if (end.matches("[0-9]+")) endIndex = Integer.parseInt(end);

		Paginator page = new Paginator(endIndex - startIndex);
		page.setStartIndex(startIndex);
		params.setPaginator(page);
		
		SearchResults<SolrGxdRnaSeqHeatMapResult> searchResults = gxdFinder.searchRnaSeqHeatMapResults(params);
		logger.info(" - got " + searchResults.getResultObjects().size() + " records");

		List<String> cells = new ArrayList<String>();
		for (SolrGxdRnaSeqHeatMapResult result : searchResults.getResultObjects()) {
			StringBuffer cell = new StringBuffer();
			cell.append(result.getMarkerMgiID());
			cell.append(",");
			cell.append(result.getConsolidatedSampleKey());
			cell.append(",");
			cell.append(result.getAvergageQNTPM());
			cells.add(cell.toString());
		}
		logger.info(" - returning " + cells.size() + " cells");
		return cells;
	}

	// Get additional data for a list of marker IDs.  Returns list of markers in proper order for display.
	// Maintains cache of marker data in controller to help avoid Solr queries.
	@RequestMapping("/rnaSeqHeatMap/markers")
	public @ResponseBody List<GxdRnaSeqHeatMapMarker> gxdRnaSeqHeatMapMarkers(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="hmMarkerIDs") String markerIDs
			) throws Exception {

		logger.info("gxdRnaSeqHeatMapMarkers() started");
		populateMarkerIDs(session, query);

		List<String> assayType = new ArrayList<String>();
		assayType.add("RNA-Seq");
		
		// retrieve markers
		List<GxdRnaSeqHeatMapMarker> markers = new ArrayList<GxdRnaSeqHeatMapMarker>();
		int fromCache = 0;
		
		// list of marker IDs that we don't already have cached and need to look up
		List<String> toFind = new ArrayList<String>();
		
		if ((markerIDs != null) && (!markerIDs.trim().equals(""))) {
			String[] markerIDList = markerIDs.trim().split("[ ]*,[ ]*");
			for (String id : markerIDList) {
				// if already cached, use that existing marker and query Solr if not yet cached
				if (hmMarkers.containsKey(id)) {
					markers.add(hmMarkers.get(id).clone());
					fromCache++;
				} else {
					toFind.add(id);
				}
			}

			/* Need to find markers in manageable batches so as not to overwhelm Solr's limit on
			 * boolean clauses in a search.  (configured at 8k clauses)
			 */
			List<List<String>> idBatches = FewiUtil.getBatches(toFind, 8000);
			Paginator page = new Paginator(10000);	
			SearchParams params = new SearchParams();
			params.setPaginator(page);
			
			for (List<String> idBatch : idBatches) {
				params.setFilter(new Filter(SearchConstants.CDNA_MARKER_ID, idBatch, Operator.OP_IN));
				
				SearchResults<Marker> searchResults = markerFinder.getMarkerByID(params);
				List<Marker> results = searchResults.getResultObjects();
				if ((results != null) && (results.size() > 0)) {
					for (Marker m : results) {
						GxdRnaSeqHeatMapMarker marker = new GxdRnaSeqHeatMapMarker();
						marker.setMarkerID(m.getPrimaryID());
						if (m.getEnsemblGeneModelID() != null) {
							marker.setEnsemblGMID(m.getEnsemblGeneModelID().getAccID()); 
						}
						marker.setSymbol(m.getSymbol());

						markers.add(marker);							// add to list of markers for this request
						hmMarkers.put(marker.getMarkerID(), marker);	// add to cache of markers for future use
					}
				}
			}

			// sort markers and assign indexes to marker objects
			Collections.sort(markers, new HeatMapMarkerComparator());
			for (int i=0; i < markers.size(); i++) {
				markers.get(i).setIndex(i);
			}
		}

		logger.info(" - got " + markers.size() + " markers (" + fromCache + " from cache)");
		return markers;
	}

	// Get additional data for a list of sample keys.  Returns list of samples in proper order for display.
	// Maintains cache of sample data in controller to help avoid Solr queries.
	@RequestMapping("/rnaSeqHeatMap/samples")
	public @ResponseBody List<GxdRnaSeqHeatMapSample> gxdRnaSeqHeatMapSamples(
			HttpSession session,
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@RequestParam(value="hmSampleKeys") String sampleKeys
			) throws Exception {

		logger.info("gxdRnaSeqHeatMapSamples() started");
		
		// retrieve samples
		List<GxdRnaSeqHeatMapSample> samples = new ArrayList<GxdRnaSeqHeatMapSample>();
		int fromCache = 0;
		
		if ((sampleKeys != null) && (!sampleKeys.trim().equals(""))) {
			String[] sampleKeyList = sampleKeys.trim().split("[ ]*,[ ]*");

			// sample keys to find (not already in cache)
			Set<String> keys = new HashSet<String>();

			for (String sampleKey : sampleKeyList) {
				// if already cached, use that existing sample and query Solr if not yet cached
				if (hmSamples.containsKey(sampleKey)) {
					samples.add(hmSamples.get(sampleKey).clone());
				} else {
					keys.add(sampleKey);
				}
			}
			fromCache = samples.size();

			if (keys.size() > 0) {
				SearchResults<SolrGxdRnaSeqConsolidatedSample> searchResults = gxdFinder.searchRnaSeqConsolidatedSamples(keys);
				List<SolrGxdRnaSeqConsolidatedSample> results = searchResults.getResultObjects();
				if (results != null) {
					for (SolrGxdRnaSeqConsolidatedSample result : results) {
						GxdRnaSeqHeatMapSample sample = new GxdRnaSeqHeatMapSample();
						sample.setAge(result.getAge());
						sample.setAlleles(result.getAlleles());
						sample.setBioreplicateSetID(result.getConsolidatedSampleKey());
						sample.setExpID(result.getAssayMgiID());
						sample.setSex(result.getSex());
						sample.setStage(result.getTheilerStage());
						sample.setStrain(result.getStrain());
						sample.setStructure(result.getStructure());

						samples.add(sample);										// add to list of samples for this request
						hmSamples.put(result.getConsolidatedSampleKey(), sample);	// add to cache of samples for future use
					}
				}
			}

			// sort samples and assign indexes to sample objects
			Collections.sort(samples, new HeatMapSampleComparator());
			for (int i=0; i < samples.size(); i++) {
				samples.get(i).setIndex(i);
			}
		}

		logger.info(" - got " + samples.size() + " samples (" + fromCache + " from cache)");
		return samples;
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

		// find the requested structure
		List<VocabTerm> structureTermList = vocabFinder.getTermByID(rowId);
		VocabTerm structureTerm = structureTermList.get(0);

		// force only the current row and column as filters
		query.getMatrixStructureId().add(rowId);
		query.setTheilerStageFilter(Arrays.asList(colId));

		// the object to return as a JSON object
		GxdStageMatrixPopup gxdStageMatrixPopup = new GxdStageMatrixPopup(structureTerm.getPrimaryID(), structureTerm.getTerm());

		// get the results for structure/stage
		Paginator page = new Paginator();
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));

		Integer posResults =  gxdFinder.getCountForMatrixPopup(params, "Yes", "result_key", rowId);
		Integer negResults = gxdFinder.getCountForMatrixPopup(params, "No", "result_key", rowId);
		Integer ambResults = gxdFinder.getCountForMatrixPopup(params, "Ambiguous", "result_key", rowId);

		gxdStageMatrixPopup.setCountPosResults(posResults);
		gxdStageMatrixPopup.setCountNegResults(negResults);
		gxdStageMatrixPopup.setCountAmbResults(ambResults);

		// Only look for marker counts in a category if we already
		// know it had > 0 results.  (for efficiency)

		if (posResults > 0) {
			gxdStageMatrixPopup.setCountPosGenes(gxdFinder.getCountForMatrixPopup(params, "Yes", "marker_key", rowId));
		}
		if (negResults > 0) {
			gxdStageMatrixPopup.setCountNegGenes(gxdFinder.getCountForMatrixPopup(params, "No", "marker_key", rowId));
		}
		if (ambResults > 0) {
			gxdStageMatrixPopup.setCountAmbGenes(gxdFinder.getCountForMatrixPopup(params, "Ambiguous", "marker_key", rowId));
		}

		// Only look for images if we already know at least one
		// category had > 0 results.  (for efficiency)

		if ((posResults > 0) || (negResults > 0) || (ambResults > 0)) {
			gxdStageMatrixPopup.setHasImage(gxdFinder.getImageFlagForMatrixPopup(params));
		}

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
		logger.info("colId= " + colId + ", rowId= " + rowId);

		// find the requested structure
		List<VocabTerm> structureTermList = vocabFinder.getTermByID(rowId);
		VocabTerm structureTerm = structureTermList.get(0);
		logger.info("got term= " + structureTerm.getTerm());

		// force only the current row and column as filters
		query.getMatrixStructureId().add(rowId);
		query.setMatrixMarkerSymbol(colId);

		// the object to return as a JSON object
		GxdGeneMatrixPopup gxdGeneMatrixPopup = new GxdGeneMatrixPopup(structureTerm.getPrimaryID(), structureTerm.getTerm());
		gxdGeneMatrixPopup.setSymbol(colId);

//		int imageCount = getGxdImageCount(session, request,query);

		// get the results for structure/stage
		Paginator page = new Paginator();
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));

		Integer posResults =  gxdFinder.getCountForMatrixPopup(params, "Yes", "result_key", rowId);
		logger.info("got + results= " + posResults);
		Integer negResults = gxdFinder.getCountForMatrixPopup(params, "No", "result_key", rowId);
		logger.info("got - results= " + negResults);
		Integer ambResults = gxdFinder.getCountForMatrixPopup(params, "Ambiguous", "result_key", rowId);
		logger.info("got amb results= " + ambResults);

		gxdGeneMatrixPopup.setCountPosResults(posResults);
		gxdGeneMatrixPopup.setCountNegResults(negResults);
		gxdGeneMatrixPopup.setCountAmbResults(ambResults);

		// Only look for images if we already know at least one
		// category had > 0 results.  (for efficiency)

		if ((posResults > 0) || (negResults > 0) || (ambResults > 0)) {
			gxdGeneMatrixPopup.setHasImage(gxdFinder.getImageFlagForMatrixPopup(params));
			logger.info("got images");
		}

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
					phenoMatrixPopup.setAlleles(FormatHelper.formatUnlinkedAlleles(genotype.getCombination3()));
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

		// pull in recombinase cells for the marker/childrenOf pair
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
				cell.setSymbol(cell.getSymbol() + " - gene expression");
				cell.setHighlightColumn(true);
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
                                gpm.setDriverSpecies(cell.getOrganism());
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
				cell.setSymbol(cell.getSymbol() + " - gene expression");
				cell.setHighlightColumn(true);
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

	
	private List<String> getGridColDisplayList( 
			 GxdQueryForm query,
			Paginator page)
	{
		logger.debug("getGridColDisplayList() started");

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(parseGxdQueryForm(query));
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(GxdResultFields.M_BY_MRK_SYMBOL, false));
		params.setSorts(sorts);

		SearchResults<SolrGxdMarker> searchResults = gxdFinder.searchMarkers(params);
		List<SolrGxdMarker> markerList = searchResults.getResultObjects();

		List<String> matrixDisplayList = new ArrayList<String>();
		for (SolrGxdMarker marker : markerList) {
			if (marker != null){
				matrixDisplayList.add(marker.getMgiid());
			}
		}
		return matrixDisplayList;
	}	

	@RequestMapping("/genegrid/json")
	public @ResponseBody GxdStageGridJsonResponse<GxdMatrixCell> gxdGeneGridJson(
			HttpServletRequest request,
			@ModelAttribute GxdQueryForm query,
			@ModelAttribute Paginator page,
			@ModelAttribute MatrixPaginator matrixPage,
			@RequestParam(value="mapChildrenOf",required=false) String childrenOf,
			@RequestParam(value="pathToOpen",required=false) List<String> pathsToOpen,
			HttpSession session) throws CloneNotSupportedException
			{
		logger.debug("gxdGeneGridJson() started");
		populateMarkerIDs(session, query);
		
		boolean isFirstPage = page.getStartIndex() == 0;
		boolean isChildrenOfQuery = childrenOf!=null && !childrenOf.equals("");
		//logger.debug("--isFirstPage:" + String.valueOf(isFirstPage));
		//logger.debug("--isChildrenOfQuery:" + String.valueOf(isChildrenOfQuery));
		
		// save original query in case we are expanding a row
		GxdQueryForm originalQuery = (GxdQueryForm) query.clone();
		String sessionQueryString = originalQuery.toString();

		// check if we have a totalCount set (if so, we return early to indicate end of data)
		String totalCountSessionId = "GxdGeneMatrixTotalCount_"+sessionQueryString;
		Integer totalCount = (Integer) session.getAttribute(totalCountSessionId);

		try {
		if(Integer.parseInt(query.getMatrixMarkerTotal()) < page.getStartIndex())
		{
			logger.debug("reached end of result set");
			return new GxdStageGridJsonResponse<GxdMatrixCell>();
		}
		} catch (NumberFormatException e) {
			// keep going if string-to-integer conversion failed
		}

		// if we have a mapChildrenOf query, we filter by structureIds of the child rows of this parentId
		if(isChildrenOfQuery)
		{
			gxdMatrixHandler.addMapChildrenOfFilterForMatrix(query,childrenOf);
			pathsToOpen = null;
		}
		
		// pagination
		Paginator geneMatrixColumnPaginator = new Paginator(); // separate paginator 
		geneMatrixColumnPaginator.setStartIndex(page.getStartIndex());
		geneMatrixColumnPaginator.setResults(page.getResults());
		query.setMatrixDisplayList(getGridColDisplayList(query, geneMatrixColumnPaginator));

		// get the matrix results for this page
		SearchParams params = new SearchParams();
		Paginator geneMatrixResultPaginator = new Paginator(); // separate paginator 
		geneMatrixResultPaginator.setStartIndex(matrixPage.getStartIndexMatrix());
		geneMatrixResultPaginator.setResults(matrixPage.getResultsMatrix());
		params.setPaginator(geneMatrixResultPaginator);
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

		// get matrix cells; add stages to mapper if this is a row-expansion
		GxdMatrixMapper mapper = new GxdMatrixMapper(edges);

		List<GxdMatrixCell> gxdMatrixCells = mapper.mapCells(flatRows, resultList);

		// only generate row relationships on first page/batch
//		if (isFirstPage)
//		{
			gxdMatrixHandler.assignOpenCloseState(parentTerms,query,edges);
//		}

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

		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		if ((genoclusterKey != null) && !FewiUtil.isPositiveInteger(genoclusterKey)) {
			return errorMav("Invalid genocluster key");
		}
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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

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

		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		mav.addObject("queryString", FormatHelper.cleanJavaScript(request.getQueryString()));

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
	
	// lookup of result count based solely on EMAPA/EMAPS ID
	public Integer getResultCountForID(String termID) {
		logger.debug("in getResultCountForID(" + termID + ")");
		SearchParams params = new SearchParams();
		GxdQueryForm form = new GxdQueryForm();
		form.setStructureID(termID);
		params.setFilter(parseGxdQueryForm(form));
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

	//
	// DIFFERENTIAL AND PROFILE HANDLING
	//


	/*
	 * Returns whether or not this query form has differential query params
	 */
	private boolean isDifferentialQuery(GxdQueryForm query)
	{
		return (query.getDifStructureID()!=null && !query.getDifStructureID().equals(""))
			|| (query.getDifTheilerStage().size() > 0)
			|| (query.getAnywhereElse() != "" && query.getAnywhereElse().trim().length() > 0);
	}

	/*
	 * Returns whether or not this query form has profile query params; web-side checking
	 * will check for an empty submission 
	 */
	private boolean isProfileQuery(GxdQueryForm query)
	{
		return (query.getProfileStructureID1()!=null && !query.getProfileStructureID1().equals(""))
			|| (query.getProfileStructureID2()!=null && !query.getProfileStructureID2().equals(""))
			|| (query.getProfileStructureID3()!=null && !query.getProfileStructureID3().equals(""))
			|| (query.getProfileStructureID4()!=null && !query.getProfileStructureID4().equals(""))
			|| (query.getProfileStructureID5()!=null && !query.getProfileStructureID5().equals(""))
			|| (query.getProfileStructureID6()!=null && !query.getProfileStructureID6().equals(""))
			|| (query.getProfileStructureID7()!=null && !query.getProfileStructureID7().equals(""))
			|| (query.getProfileStructureID8()!=null && !query.getProfileStructureID8().equals(""))
			|| (query.getProfileStructureID9()!=null && !query.getProfileStructureID9().equals(""))
			|| (query.getProfileStructureID10()!=null && !query.getProfileStructureID10().equals(""));
	}

	/* This method creates the differential part 1 filters (used by resolveDifferentialMarkers() below)
	 * for cases where the "NOT anywhere else" checkbox has been checked.  It seemed simpler and more
	 * maintainable than trying to work it into the existing complex code.
	 */
	private void buildDifferentialNowhereElseFilters(List<Filter> queryFilters, String structureID, List<Integer> stages) {
		/* If we got here, we have one of three cases:
		 * 1. user specified a single structure and checked "AND NOT anywhere else"
		 * 2. user specified 1+ Theiler stages and checked "AND NOT anywhere else"
		 * 3. user specified both a structure and 1+ Theiler stages and checked "AND NOT anywhere else"
		 */
		logger.info("entering buildDifferentialNowhereElseFilters() with " + queryFilters.size() + " filters");
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
				} else {
					diffFilters.add(new Filter(GxdResultFields.DIFF_EXCLUSIVE_STAGES, stage, Filter.Operator.OP_CONTAINS));
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

		// if a symbol is specified (e.g.- a column heading in differential search grid), use it to reduce the set
		String mmSymbol = query.getMatrixMarkerSymbol();
		if ((mmSymbol == null) || ("".equals(mmSymbol))) {
			List<String> symbols = query.getMarkerSymbolFilter();
			if ((symbols != null) && (symbols.size() == 1)) {
				mmSymbol = symbols.get(0);
			}
		}
		if ((mmSymbol != null) && (!"".equals(mmSymbol))) {
			SearchResults<Marker> mmMarkers = markerFinder.getMarkerBySymbol(mmSymbol);
			logger.info("mmMarkers.size = " + mmMarkers.getResultObjects().size());
			if (mmMarkers.getResultObjects().size() == 1) {
				queryFilters.add(new Filter(GxdResultFields.MARKER_MGIID, mmMarkers.getResultObjects().get(0).getPrimaryID()));
				logger.info("added markerMgiid criteria: " + mmMarkers.getResultObjects().get(0).getPrimaryID());
			}
		}

		// init form fields
		String structure = query.getStructureID();
		String difStructure = query.getDifStructureID();
		List<Integer> stages = query.getTheilerStage();
		List<Integer> difStages = query.getDifTheilerStage();

		// figure out what kind of diff query this is
		boolean hasStructures = (structure!=null && !structure.equals("")
				&& difStructure!=null && !difStructure.equals(""));
		boolean hasStages = (stages.size() > 0 && difStages.size()>0
				&& !(stages.contains(GxdQueryForm.ANY_STAGE) && (
						difStages.contains(GxdQueryForm.ANY_STAGE_NOT_ABOVE) || difStages.contains(GxdQueryForm.ANY_STAGE)) ));
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
			buildDifferentialNowhereElseFilters(queryFilters, structure, stages);
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
			// structure and its descendants.  This filter will be negate()-ed later in the code.
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
				&& !(stages.contains(GxdQueryForm.ANY_STAGE) && (
					difStages.contains(GxdQueryForm.ANY_STAGE_NOT_ABOVE) || difStages.contains(GxdQueryForm.ANY_STAGE)) ));
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
	 * Creates the profile part 1 filters,
	 * 	performs the query against gxdDifferentialMarker index
	 * 	and returns the unique marker keys 
	 */
	private List<String> resolveProfileMarkers(GxdQueryForm query)
	{
		logger.debug("--Starting resolveProfileMarkers()");

        List<String> profileStructureKeys = new ArrayList<String>();

		// start filter list for query filters
		List<Filter> queryFilters = new ArrayList<Filter>();

		String profileStructureID1 = query.getProfileStructureID1();
		String profileStructureID2 = query.getProfileStructureID2();
		String profileStructureID3 = query.getProfileStructureID3();
		String profileStructureID4 = query.getProfileStructureID4();
		String profileStructureID5 = query.getProfileStructureID5();
		String profileStructureID6 = query.getProfileStructureID6();
		String profileStructureID7 = query.getProfileStructureID7();
		String profileStructureID8 = query.getProfileStructureID8();
		String profileStructureID9 = query.getProfileStructureID9();
		String profileStructureID10 = query.getProfileStructureID10();
		String detected1 = query.getDetected_1();
		String detected2 = query.getDetected_2();
		String detected3 = query.getDetected_3();
		String detected4 = query.getDetected_4();
		String detected5 = query.getDetected_5();
		String detected6 = query.getDetected_6();
		String detected7 = query.getDetected_7();
		String detected8 = query.getDetected_8();
		String detected9 = query.getDetected_9();
		String detected10 = query.getDetected_10();
		boolean profileNowhereElse = (query.getProfileNowhereElseCheckbox() != null) 
						&& (query.getProfileNowhereElseCheckbox().trim().length() > 0);

		if (profileNowhereElse) {

			logger.info("-- resolveProfileMarkers(); building profileNowhereElse filters");

			if (profileStructureID1!=null && !profileStructureID1.equals("")) {
				logger.debug("-- resolveProfileMarkers() 1 = " + profileStructureID1);
				List<VocabTerm> structureList1 = vocabFinder.getTermByID(profileStructureID1);
				if (structureList1.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList1.get(0).getTermKey()));
				}
			}
			if (profileStructureID2!=null && !profileStructureID2.equals("")) {
				logger.debug("-- resolveProfileMarkers() 2 = " + profileStructureID2);
				List<VocabTerm> structureList2 = vocabFinder.getTermByID(profileStructureID2);
				if (structureList2.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList2.get(0).getTermKey()));
				}
			}
			if (profileStructureID3!=null && !profileStructureID3.equals("")) {
				logger.debug("-- resolveProfileMarkers() 3 = " + profileStructureID3);
				List<VocabTerm> structureList3 = vocabFinder.getTermByID(profileStructureID3);
				if (structureList3.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList3.get(0).getTermKey()));
				}
			}
			if (profileStructureID4!=null && !profileStructureID4.equals("")) {
				logger.debug("-- resolveProfileMarkers() 4 = " + profileStructureID4);
				List<VocabTerm> structureList4 = vocabFinder.getTermByID(profileStructureID4);
				if (structureList4.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList4.get(0).getTermKey()));
				}
			}
			if (profileStructureID5!=null && !profileStructureID5.equals("")) {
				logger.debug("-- resolveProfileMarkers() 5 = " + profileStructureID5);
				List<VocabTerm> structureList5 = vocabFinder.getTermByID(profileStructureID5);
				if (structureList5.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList5.get(0).getTermKey()));
				}
			}
			if (profileStructureID6!=null && !profileStructureID6.equals("")) {
				logger.debug("-- resolveProfileMarkers() 6 = " + profileStructureID6);
				List<VocabTerm> structureList6 = vocabFinder.getTermByID(profileStructureID6);
				if (structureList6.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList6.get(0).getTermKey()));
				}
			}
			if (profileStructureID7!=null && !profileStructureID7.equals("")) {
				logger.debug("-- resolveProfileMarkers() 7 = " + profileStructureID7);
				List<VocabTerm> structureList7 = vocabFinder.getTermByID(profileStructureID7);
				if (structureList7.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList7.get(0).getTermKey()));
				}
			}
			if (profileStructureID8!=null && !profileStructureID8.equals("")) {
				logger.debug("-- resolveProfileMarkers() 8 = " + profileStructureID8);
				List<VocabTerm> structureList8 = vocabFinder.getTermByID(profileStructureID8);
				if (structureList8.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList8.get(0).getTermKey()));
				}
			}
			if (profileStructureID9!=null && !profileStructureID9.equals("")) {
				logger.debug("-- resolveProfileMarkers() 9 = " + profileStructureID9);
				List<VocabTerm> structureList9 = vocabFinder.getTermByID(profileStructureID9);
				if (structureList9.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList9.get(0).getTermKey()));
				}
			}
			if (profileStructureID10!=null && !profileStructureID10.equals("")) {
				logger.debug("-- resolveProfileMarkers() 10 = " + profileStructureID10);
				List<VocabTerm> structureList10 = vocabFinder.getTermByID(profileStructureID10);
				if (structureList10.size() > 0) {
					profileStructureKeys.add(Integer.toString(structureList10.get(0).getTermKey()));
				}
			}
			
			// retrieve the list of marker keys; uses sql-based expression helper hunter
			List<String> markerKeys = expressionHelper.expressedIn(profileStructureKeys, "gene"); 
			return markerKeys;

		}
		else { // perform structure matching

			logger.info("-- resolveProfileMarkers(); building structure match filters");

			if (profileStructureID1!=null && !profileStructureID1.equals("")) {
				logger.debug("-- resolveProfileMarkers() 1 = " + profileStructureID1);
				Filter filter1 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID1);
				if (detected1.equals("false")) {filter1.negate();}
				queryFilters.add(filter1);
			}
			if (profileStructureID2!=null && !profileStructureID2.equals("")) {
				logger.debug("-- resolveProfileMarkers() 2 = " + profileStructureID2);
				Filter filter2 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID2);
				if (detected2.equals("false")) {filter2.negate();}
				queryFilters.add(filter2);
			}
			if (profileStructureID3!=null && !profileStructureID3.equals("")) {
				logger.debug("-- resolveProfileMarkers() 3 = " + profileStructureID3);
				Filter filter3 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID3);
				if (detected3.equals("false")) {filter3.negate();}
				queryFilters.add(filter3);
			}
			if (profileStructureID4!=null && !profileStructureID4.equals("")) {
				logger.debug("-- resolveProfileMarkers() 4 = " + profileStructureID4);
				Filter filter4 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID4);
				if (detected4.equals("false")) {filter4.negate();}
				queryFilters.add(filter4);
			}
			if (profileStructureID5!=null && !profileStructureID5.equals("")) {
				logger.debug("-- resolveProfileMarkers() 5 = " + profileStructureID5);
				Filter filter5 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID5);
				if (detected5.equals("false")) {filter5.negate();}
				queryFilters.add(filter5);
			}
			if (profileStructureID6!=null && !profileStructureID6.equals("")) {
				logger.debug("-- resolveProfileMarkers() 6 = " + profileStructureID6);
				Filter filter6 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID6);
				if (detected6.equals("false")) {filter6.negate();}
				queryFilters.add(filter6);
			}
			if (profileStructureID7!=null && !profileStructureID7.equals("")) {
				logger.debug("-- resolveProfileMarkers() 7 = " + profileStructureID7);
				Filter filter7 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID7);
				if (detected7.equals("false")) {filter7.negate();}
				queryFilters.add(filter7);
			}
			if (profileStructureID8!=null && !profileStructureID8.equals("")) {
				logger.debug("-- resolveProfileMarkers() 8 = " + profileStructureID8);
				Filter filter8 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID8);
				if (detected8.equals("false")) {filter8.negate();}
				queryFilters.add(filter8);
			}
			if (profileStructureID9!=null && !profileStructureID9.equals("")) {
				logger.debug("-- resolveProfileMarkers() 9 = " + profileStructureID9);
				Filter filter9 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID9);
				if (detected9.equals("false")) {filter9.negate();}
				queryFilters.add(filter9);
			}
			if (profileStructureID10!=null && !profileStructureID10.equals("")) {
				logger.debug("-- resolveProfileMarkers() 10 = " + profileStructureID10);
				Filter filter10 = makeStructureSearchFilter(SearchConstants.POS_STRUCTURE,profileStructureID10);
				if (detected10.equals("false")) {filter10.negate();}
				queryFilters.add(filter10);
			}
		}

		Filter profileFilter = new Filter();
		logger.debug("-- resolveProfileMarkers() queryFilters.size() = " + queryFilters.size());
		if(queryFilters.size() > 0)
		{
			profileFilter.setNestedFilters(queryFilters,Filter.JoinClause.FC_AND);
		}		
		else return null; // punt; no filters

		// build Search Params object;  add our profile filters
		SearchParams profileSP = new SearchParams();
		profileSP.setFilter(profileFilter);

		return gxdFinder.searchDifferential(profileSP);
	}

	/*
	 * Helper for the profile part 2 filter (below); factoring out pos/neg
	 * filter generation
	 */
	public Filter makeProfileResultPosNegFilters(String profileStructureID)
	{

		List<Filter> outerFilters = new ArrayList<Filter>();

		// constructing positive filter
		List<Filter> posFilters = new ArrayList<Filter>();
		posFilters.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID, profileStructureID));
		posFilters.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));
		outerFilters.add(Filter.and(posFilters));
			
		// negative filter
		List<Filter> negFilters = new ArrayList<Filter>();
		negFilters.add(new Filter(SearchConstants.STRUCTURE_ID, profileStructureID, Filter.Operator.OP_EQUAL));
		Filter negPoolFilter = Filter.and(negFilters);
		negPoolFilter.negate(); //negate the filter before adding to the filter list
		List<Filter> combinedNegFilters = new ArrayList<Filter>();
		combinedNegFilters.add(new Filter(SearchConstants.GXD_DETECTED, "No", Filter.Operator.OP_EQUAL));
		combinedNegFilters.add(negPoolFilter);

		outerFilters.add(Filter.and(combinedNegFilters));

		return Filter.or(outerFilters);
	}

	/*
	 * Creates the profile part 2 filter that goes against the gxdResult index
	 */
	public Filter makeProfileResultFilters(GxdQueryForm query)
	{
		logger.debug("makeProfileResultFilters");

		// start filter list for query filters
		List<Filter> structureFilters = new ArrayList<Filter>();

		String profileStructureID1 = query.getProfileStructureID1();
		String profileStructureID2 = query.getProfileStructureID2();
		String profileStructureID3 = query.getProfileStructureID3();
		String profileStructureID4 = query.getProfileStructureID4();
		String profileStructureID5 = query.getProfileStructureID5();
		String profileStructureID6 = query.getProfileStructureID6();
		String profileStructureID7 = query.getProfileStructureID7();
		String profileStructureID8 = query.getProfileStructureID8();
		String profileStructureID9 = query.getProfileStructureID9();
		String profileStructureID10 = query.getProfileStructureID10();
		String detected1 = query.getDetected_1();
		String detected2 = query.getDetected_2();
		String detected3 = query.getDetected_3();
		String detected4 = query.getDetected_4();
		String detected5 = query.getDetected_5();
		String detected6 = query.getDetected_6();
		String detected7 = query.getDetected_7();
		String detected8 = query.getDetected_8();
		String detected9 = query.getDetected_9();
		String detected10 = query.getDetected_10();
		boolean profileNowhereElse = (query.getProfileNowhereElseCheckbox() != null) 
						&& (query.getProfileNowhereElseCheckbox().trim().length() > 0);


		if (profileNowhereElse) {
		logger.debug("makeProfileResultFilters - profileNowhereElse");
			if (profileStructureID1!=null && !profileStructureID1.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID1));
			}
			if (profileStructureID2!=null && !profileStructureID2.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID2));
			}			
			if (profileStructureID3!=null && !profileStructureID3.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID3));
			}	
			if (profileStructureID4!=null && !profileStructureID4.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID4));
			}
			if (profileStructureID5!=null && !profileStructureID5.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID5));
			}	
			if (profileStructureID6!=null && !profileStructureID6.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID6));
			}	
			if (profileStructureID7!=null && !profileStructureID7.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID7));
			}	
			if (profileStructureID8!=null && !profileStructureID8.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID8));
			}		
			if (profileStructureID9!=null && !profileStructureID9.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID9));
			}		
			if (profileStructureID10!=null && !profileStructureID10.equals("")) {
				structureFilters.add(makeProfileResultPosNegFilters(profileStructureID10));
			}	

		}
		else {
			logger.debug("makeProfileResultFilters - not profileNowhereElse");

			if (profileStructureID1!=null && !profileStructureID1.equals("")) {
				List<Filter> structureFilter1 = new ArrayList<Filter>();
				if (detected1.equals("false")) {
					structureFilter1.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID1))	;
					structureFilter1.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter1.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID1));
					structureFilter1.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter1));
			}
			if (profileStructureID2!=null && !profileStructureID2.equals("")) {
				List<Filter> structureFilter2 = new ArrayList<Filter>();
				if (detected2.equals("false")) {
					structureFilter2.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID2))	;
					structureFilter2.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter2.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID2));
					structureFilter2.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter2));
			}
			if (profileStructureID3!=null && !profileStructureID3.equals("")) {
				List<Filter> structureFilter3 = new ArrayList<Filter>();
				if (detected3.equals("false")) {
					structureFilter3.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID3))	;
					structureFilter3.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter3.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID3));
					structureFilter3.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter3));
			}
			if (profileStructureID4!=null && !profileStructureID4.equals("")) {
				List<Filter> structureFilter4 = new ArrayList<Filter>();
				if (detected4.equals("false")) {
					structureFilter4.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID4))	;
					structureFilter4.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter4.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID4));
					structureFilter4.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter4));
			}
			if (profileStructureID5!=null && !profileStructureID5.equals("")) {
				List<Filter> structureFilter5 = new ArrayList<Filter>();
				if (detected5.equals("false")) {
					structureFilter5.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID5))	;
					structureFilter5.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter5.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID5));
					structureFilter5.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter5));
			}		
			if (profileStructureID6!=null && !profileStructureID6.equals("")) {
				List<Filter> structureFilter6 = new ArrayList<Filter>();
				if (detected6.equals("false")) {
					structureFilter6.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID6))	;
					structureFilter6.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter6.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID6));
					structureFilter6.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter6));
			}
			if (profileStructureID7!=null && !profileStructureID7.equals("")) {
				List<Filter> structureFilter7 = new ArrayList<Filter>();
				if (detected7.equals("false")) {
					structureFilter7.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID7))	;
					structureFilter7.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter7.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID7));
					structureFilter7.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter7));
			}
			if (profileStructureID8!=null && !profileStructureID8.equals("")) {
				List<Filter> structureFilter8 = new ArrayList<Filter>();
				if (detected8.equals("false")) {
					structureFilter8.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID8))	;
					structureFilter8.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter8.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID8));
					structureFilter8.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter8));
			}
			if (profileStructureID9!=null && !profileStructureID9.equals("")) {
				List<Filter> structureFilter9 = new ArrayList<Filter>();
				if (detected9.equals("false")) {
					structureFilter9.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID9))	;
					structureFilter9.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter9.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID9));
					structureFilter9.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter9));
			}
			if (profileStructureID10!=null && !profileStructureID10.equals("")) {
				List<Filter> structureFilter10 = new ArrayList<Filter>();
				if (detected10.equals("false")) {
					structureFilter10.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_EXACT,profileStructureID10))	;
					structureFilter10.add(new Filter(SearchConstants.GXD_DETECTED,"No",Filter.Operator.OP_EQUAL));
				} else {
					structureFilter10.add(makeStructureSearchFilter(SearchConstants.STRUCTURE_ID,profileStructureID10));
					structureFilter10.add(new Filter(SearchConstants.GXD_DETECTED,"Yes",Filter.Operator.OP_EQUAL));				
				}
				structureFilters.add(Filter.and(structureFilter10));
			}
		}

		return Filter.or(structureFilters);
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

		// exclude RNA-Seq data from differential searches
		if (isDifferentialQuery(query)) {
			queryFilters.add(new Filter(SearchConstants.GXD_ASSAY_TYPE, "RNA-Seq", Filter.Operator.OP_NOT_EQUAL));
		}

		// ---------------------------
		// heat map sample restriction (for looking up an individual sample's records)
		// ---------------------------
		
		if ((query.getSampleKey() != null) && (!query.getSampleKey().trim().equals(""))) {
			queryFilters.add(new Filter(GxdResultFields.CONSOLIDATED_SAMPLE_KEY, query.getSampleKey().trim(),
				Filter.Operator.OP_EQUAL));
		}
		
		// ---------------------------
		// restrict results by filters (added to facetList)
		// ---------------------------

		if (query.getExperimentFilter().size() > 0) {
			facetList.add(new Filter(GxdResultFields.ASSAY_MGIID,
				query.getExperimentFilter(), Filter.Operator.OP_IN));
		}

		if (query.getSystemFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_SYSTEM,
					query.getSystemFilter(), Filter.Operator.OP_IN));
		}

		if (query.getAssayTypeFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_ASSAY_TYPE,
					query.getAssayTypeFilter(), Filter.Operator.OP_IN));
		}

		if (query.getTmpLevelFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_TMP_LEVEL,
					query.getTmpLevelFilter(), Filter.Operator.OP_IN));
		}

		if (query.getMarkerTypeFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_MARKER_TYPE,
					query.getMarkerTypeFilter(), Filter.Operator.OP_IN));
		}

		if (query.getMpFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_MP,
					query.getMpFilter(), Filter.Operator.OP_IN));
		}

		if (query.getDoFilter().size() > 0) {
			facetList.add(new Filter(FacetConstants.GXD_DO,
					query.getDoFilter(), Filter.Operator.OP_IN));
		}

		if (query.getGoBpFilter().size() > 0) {
			facetList.add(new Filter(GxdResultFields.GO_HEADERS_BP,
					query.getGoBpFilter(), Filter.Operator.OP_IN));
		}
		
		if (query.getGoCcFilter().size() > 0) {
			facetList.add(new Filter(GxdResultFields.GO_HEADERS_CC,
					query.getGoCcFilter(), Filter.Operator.OP_IN));
		}
		
		if (query.getGoMfFilter().size() > 0) {
			facetList.add(new Filter(GxdResultFields.GO_HEADERS_MF,
					query.getGoMfFilter(), Filter.Operator.OP_IN));
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
		// absolute filter on structure ID (used by popups to restrict query to only this structure, 
		// but still keep the other structure queries and filters
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

		// Move processing of a batch of marker IDs up above the differential
		// handling, so we can do pagination on the tissue x gene matrix.

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

		// profile handling; this method will end further processing of query parameters
		if(isProfileQuery(query))
		{
			logger.info("In profile form processing");

			// default filters for profile search
			queryFilters.add(new Filter(SearchConstants.GXD_ASSAY_TYPE, "RNA-Seq", Filter.Operator.OP_NOT_EQUAL));
			queryFilters.add(new Filter(SearchConstants.GXD_IS_WILD_TYPE, WILD_TYPE));

			// Process PROFILE QUERY FORM params
			// Do part 1 of the profile search (I.e. find out what markers to bring back)
			List<String> markerKeys = resolveProfileMarkers(query);

			logger.info("resolveProfileMarkers found " + markerKeys.size() + " marker keys");
			Collections.sort(markerKeys);
			logger.info(Arrays.toString(markerKeys.toArray()));
			if(markerKeys !=null && markerKeys.size()>0)
			{
				queryFilters.add( new Filter(SearchConstants.MRK_KEY,markerKeys,Filter.Operator.OP_IN));
			}else{
				// need a way to prevent the standard query from returning results when the differential fails to find markers
				queryFilters.add( new Filter(SearchConstants.MRK_KEY,"NO_MARKERS_FOUND",Filter.Operator.OP_EQUAL));
			}

			// add the 2nd part of the profile query (I.e. what results to display for the given markers)
			queryFilters.add(makeProfileResultFilters(query));

			if (facetList.size() > 0) {
				queryFilters.addAll(facetList);
			}

			return Filter.and(queryFilters);
			// NOTE: THIS WAS A PROFILE QUERY, STANDARD QUERY FORM LOGIC IS NOT EXECUTED
		}

		
		// differential handling; this method will end further processing of query parameters
		if(isDifferentialQuery(query))
		{
			logger.info("In differential form processing");

			// Process DIFFERENTIAL QUERY FORM params
			// Do part 1 of the differential (I.e. find out what markers to bring back)
			List<String> markerKeys = resolveDifferentialMarkers(query);
			logger.info("resolveDifferentialMarkers found " + markerKeys.size() + " marker keys");
			Collections.sort(markerKeys);
			logger.info(Arrays.toString(markerKeys.toArray()));
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
			// NOTE: THIS WAS A DIFFERENTIAL QUERY, STANDARD QUERY FORM LOGIC IS NOT EXECUTED
		}

		/*
		* Standard QF Parameter handling
		*/

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
		String experimentID = query.getExperimentID();

		if ((experimentID != null) && (!"".equals(experimentID))) {
			queryFilters.add(new Filter(GxdResultFields.ASSAY_MGIID,
				experimentID));
		}

		if(structureKey !=null && !structureKey.equals("")) {
			Filter structureKeyFilter = new Filter(SearchConstants.STRUCTURE_KEY, structureKey);
			queryFilters.add(structureKeyFilter);
		}

		// spatial location
		if(query.getLocations() !=null && !query.getLocations().equals("")) {
			List<String> tokens = QueryParser.tokeniseOnWhitespaceAndComma(query.getLocations());
			List<Filter> locationFilters = new ArrayList<Filter>();
			for(String token : tokens){
				String spatialQueryString = SolrLocationTranslator.getIntersectsQueryValue(token, query.getLocationUnit());
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

		// pagination list
		List<String> matrixDisplayList = query.getMatrixDisplayList();
		if ((matrixDisplayList != null) && (matrixDisplayList.size() > 0)) {
			Filter matrixDisplayListFilter = new Filter(SearchConstants.MRK_ID, matrixDisplayList, Filter.Operator.OP_IN);
			queryFilters.add(matrixDisplayListFilter);
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
		params.setPageSize(200000);

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
		logger.debug("getGxdMarkerResults() query =  " + query.toString());

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
		logger.debug("getGxdAssays() query =  " + query.toString());

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
		logger.debug("getGxdAssayResults() query =  " + query.toString());

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
		logger.debug("getGxdImages() query =  " + query.toString());

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

	/* gets a list of systems for the facet list, returned as JSON
	 */
	@RequestMapping("/facet/system")
	public @ResponseBody Map<String, List<String>> facetSystem(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result, FacetConstants.GXD_SYSTEM);
	}

	/* gets a list of assay types for the facet list, returned as
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

	/* gets a list of marker types for the facet list, returned as
	 * JSON
	 */
	@RequestMapping("/facet/markerType")
	public @ResponseBody Map<String, List<String>> facetMarkerType(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_MARKER_TYPE);
	}

	/* gets a list of detection levels for the facet list, returned
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

	/* gets a list of theiler stages for the facet list, returned
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

	/* gets a list of wild type values for the facet list, returned
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

	/* gets a list of MP values for the facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/mp")
	public @ResponseBody Map<String, List<String>> facetMp(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_MP);
	}

	/* gets a list of DO values for the facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/do")
	public @ResponseBody Map<String, List<String>> facetDo(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result,
				FacetConstants.GXD_DO);
	}
	
	/* gets a list of GO Molecular Function values for the facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/goMf")
	public @ResponseBody Map<String, List<String>> facetGoMf(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result, FacetConstants.GXD_GO_MF);
	}

	/* gets a list of GO Biological Process values for the facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/goBp")
	public @ResponseBody Map<String, List<String>> facetGoBp(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result, FacetConstants.GXD_GO_BP);
	}

	/* gets a list of GO Cellular Component values for the facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/goCc")
	public @ResponseBody Map<String, List<String>> facetGoCc(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result, FacetConstants.GXD_GO_CC);
	}

	/* gets a list of TMP Level values for the facet list, returned
	 * as JSON
	 */
	@RequestMapping("/facet/tmpLevel")
	public @ResponseBody Map<String, List<String>> facetTmpLevel(
			HttpSession session,
			@ModelAttribute GxdQueryForm query,
			BindingResult result) {

		populateMarkerIDs(session, query);
		return facetGeneric(query, result, FacetConstants.GXD_TMP_LEVEL);
	}
	
        /*
         * Helper that returns request parameters as a string. Hides the difference between GET and POST requests.
         * (The native HttpServletRequest.getQueryString() returns "" for POST requests.)
         */
        private String getQueryString (HttpServletRequest request) throws UnsupportedEncodingException {
            if ("GET".equals(request.getMethod())) {
                return request.getQueryString();
            } else if ("POST".equals(request.getMethod())) {
                String qString = "";
                Enumeration<String> pnames = request.getParameterNames();
                while (pnames.hasMoreElements()) {
                   String pname = pnames.nextElement();
                   String value = request.getParameter(pname);
                   if (qString.length() > 0) {
                       qString += "&";
                   }
                   qString += pname + "=" + URLEncoder.encode(value, "UTF-8");
                }
                return qString;
            } else {
                return "";
            }
        }

	/* generic facet handling
	 */
	private Map<String, List<String>> facetGeneric (GxdQueryForm query,
			BindingResult result, String facetType) {

		logger.debug(query.toString());
		String order = ALPHA;
		String emptyListMsg = "No values in results to filter.";

		SearchParams params = new SearchParams();
		params.setFilter(parseGxdQueryForm(query));

		SearchResults<SolrString> facetResults = null;

		if (FacetConstants.GXD_SYSTEM.equals(facetType)) {
			facetResults = gxdFinder.getSystemFacet(params);
		} else if (FacetConstants.GXD_ASSAY_TYPE.equals(facetType)) {
			facetResults = gxdFinder.getAssayTypeFacet(params);
		} else if (FacetConstants.GXD_MARKER_TYPE.equals(facetType)) {
			facetResults = gxdFinder.getMarkerTypeFacet(params);
			order = MARKERTYPE_DISPLAY; 
		} else if (FacetConstants.GXD_DETECTED.equals(facetType)) {
			facetResults = gxdFinder.getDetectedFacet(params);
			order = DETECTED;
		} else if (FacetConstants.GXD_THEILER_STAGE.equals(facetType)) {
			facetResults = gxdFinder.getTheilerStageFacet(params);
			order = RAW;
		} else if (FacetConstants.GXD_WILDTYPE.equals(facetType)) {
			facetResults = gxdFinder.getWildtypeFacet(params);
		} else if (FacetConstants.GXD_MP.equals(facetType)) {
			emptyListMsg = "No genes found with ontology associations.";
			facetResults = gxdFinder.getMpFacet(params);
		} else if (FacetConstants.GXD_DO.equals(facetType)) {
			emptyListMsg = "No genes found with ontology associations.";
			facetResults = gxdFinder.getDoFacet(params);
		} else if (FacetConstants.GXD_GO_MF.equals(facetType)) {
			emptyListMsg = "No genes found with ontology associations.";
			facetResults = gxdFinder.getGoFacet(params, "MF");
		} else if (FacetConstants.GXD_GO_BP.equals(facetType)) {
			emptyListMsg = "No genes found with ontology associations.";
			facetResults = gxdFinder.getGoFacet(params, "BP");
		} else if (FacetConstants.GXD_GO_CC.equals(facetType)) {
			emptyListMsg = "No genes found with ontology associations.";
			facetResults = gxdFinder.getGoFacet(params, "CC");
		} else if (FacetConstants.GXD_TMP_LEVEL.equals(facetType)) {
			order = TPM_LEVEL_SORT; 
			emptyListMsg = "No RNA-Seq results to filter.";
			facetResults = gxdFinder.getTmpLevelFacet(params);
		} else {
			facetResults = new SearchResults<SolrString>();
		}

		return parseFacetResponse(facetResults, order, emptyListMsg);
	}
	
	/* facet response parsing
	 */
	private Map<String, List<String>> parseFacetResponse (
			SearchResults<SolrString> facetResults, String order, String emptyListMsg) {

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();


		if (facetResults.getResultFacets().size() >= facetLimit) {
			l.add("Too many results to display. Modify your search or try another filter first.");
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() == 0) {
			l.add(emptyListMsg);
			m.put("error", l);
		} else if (ALPHA.equals(order)) {
			m.put("resultFacets", facetResults.getSortedResultFacets());
		} else if (RAW.equals(order)) {
			m.put("resultFacets", facetResults.getResultFacets());
		} else if (MARKERTYPE_DISPLAY.equals(order)) {
			// MARKER_TYPE filter list has unique sort
			List<String> values = facetResults.getResultFacets();
			List<String> cleanedFacetList = cleanMarkerTypeFacetList(values);
			Collections.sort (cleanedFacetList, new MarkerTypeFilterComparator());
			facetResults.setResultFacets(cleanedFacetList);
			m.put("resultFacets", facetResults.getResultFacets());
		} else if (TPM_LEVEL_SORT.equals(order)) {
			List<String> values = facetResults.getResultFacets();
			List<String> cleanedFacetList = cleanMarkerTypeFacetList(values);
			Collections.sort (cleanedFacetList, new TpmLevelComparator());
			facetResults.setResultFacets(cleanedFacetList);
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

	/* gene type facet requires we suppress a few low-level terms,
	 * but only from filter dialog (not result set)
	 */
	private List<String> cleanMarkerTypeFacetList(List<String> facetList) {
		facetList.remove("lincRNA gene");
		facetList.remove("antisense lncRNA gene");
		facetList.remove("sense intronic lncRNA gene");
		facetList.remove("sense overlapping lncRNA gene");
		facetList.remove("bidirectional promoter lncRNA gene");
		return facetList;

	}	
	
	/* facet sorting (marker type filter)
	 */
	private class MarkerTypeFilterComparator implements Comparator<String> {

		private final List<String> orderedItems = Arrays.asList (
				new String[] { "protein coding gene", "non-coding RNA gene" });

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

	/* facet sorting (detected filter)
	 */
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

	/* facet sorting (tpm level filter)
	 */
	private class TpmLevelComparator implements Comparator<String> {

		private final List<String> orderedItems = Arrays.asList (
				new String[] { "High", "Medium", "Low", "Below Cutoff" });

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
	public ModelAndView htQueryForm(HttpServletRequest request, HttpServletResponse response) {
		return gxdhtController.getQueryForm(request, response);
	}
	
	// HT summary page (no results table -- that's injected by Javascript)
	@RequestMapping("/htexp_index/summary")
	public ModelAndView gxdHtSummary(HttpServletRequest request, @ModelAttribute GxdHtQueryForm queryForm) {
		return gxdhtController.gxdHtSummary(request, queryForm);
	}
	
	// HT sample popup (expects ArrayExpress ID)
	@RequestMapping(value="/htexp_index/samples/{experimentID:.+}", method = RequestMethod.GET)
	public ModelAndView htSamplePopup(HttpServletRequest request, @PathVariable("experimentID") String experimentID, @ModelAttribute GxdHtQueryForm queryForm) {
		return gxdhtController.gxdHtSamples(request, experimentID, queryForm);
	}
	
	// HT result table to inject into summary page (retrieve via Ajax)
	@RequestMapping("/htexp_index/table")
	public ModelAndView htExperimentsTable (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {
		return gxdhtController.experimentsTable(request, query, page);
	}
	
	// filter values for experimental variables for a GXD HT summary (retrieve via Ajax)
	@RequestMapping("/htexp_index/facet/variable")
	public @ResponseBody Map<String, List<String>> htFacetVariable (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page, HttpServletResponse response) {
		return gxdhtController.getVariableFacet(query, null, response);
	}
	
	// filter values for study types for a GXD HT summary (retrieve via Ajax)
	@RequestMapping("/htexp_index/facet/studyType")
	public @ResponseBody Map<String, List<String>> htFacetStudyType (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page, HttpServletResponse response) {
		return gxdhtController.getStudyTypeFacet(query, null, response);
	}

	// comparator for markers in an RNA-Seq heat map
    private class HeatMapMarkerComparator extends SmartAlphaComparator<GxdRnaSeqHeatMapMarker> {
    	public int compare(GxdRnaSeqHeatMapMarker t1, GxdRnaSeqHeatMapMarker t2) {
    		// smart-alpha sort of Marker objects by symbol (fall back on ID sorting, if terms match)
    		
    		int i = super.compare(t1.getSymbol(), t2.getSymbol());
    		if (i == 0) {
    			i = super.compare(t1.getMarkerID(), t2.getMarkerID());
    		}
    		return i;
    	}
    }

	// comparator for samples in an RNA-Seq heat map
    private class HeatMapSampleComparator extends SmartAlphaComparator<GxdRnaSeqHeatMapSample> {
    	public int compare(GxdRnaSeqHeatMapSample t1, GxdRnaSeqHeatMapSample t2) {
    		// smart-alpha sort of Sample objects by structure (falling back on experiment ID and sample key)
    		
    		int i = super.compare(t1.getStructure(), t2.getStructure());
    		if (i == 0) {
    			i = super.compare(t1.getExpID(), t2.getExpID());
    			if (i == 0) {
    				i = super.compare(t1.getBioreplicateSetID(), t2.getBioreplicateSetID());
    			}
    		}
    		return i;
    	}
    }
}
