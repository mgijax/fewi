package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// external
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fewi.finder.InteractionFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.forms.InteractionQueryForm;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
// internal
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
/*--------------------------------------*/
/* standard imports for all controllers */
/*--------------------------------------*/
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;
import org.jax.mgi.fewi.summary.InteractionSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /interaction/ uri's
 */
@Controller
@RequestMapping(value="/interaction")
public class InteractionController {


	//--------------------//
	// static variables
	//--------------------//

	// sort facet values alphabetically
	private static String ALPHA = "alpha";

	// keep facet values sorted as returned by Solr
	private static String RAW = "raw";	

	//--------------------//
	// instance variables
	//--------------------//

	private final Logger logger
	= LoggerFactory.getLogger(InteractionController.class);

	@Autowired
	private InteractionFinder interactionFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	//--------------------------------------------------------------------//
	// public methods
	//--------------------------------------------------------------------//

	//--------------------//
	// Interaction summary page by marker ID or IDs
	//--------------------//
	@RequestMapping(value="/explorer")
	public ModelAndView byMarkerID(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->byMarkerID started");

		// collect the marker IDs given by the user

		String markerIDs = request.getParameter("markerIDs");
		if (markerIDs == null) {
			return errorMav("No marker IDs found");
		}

		// convert the marker IDs  into their respective Marker objects,
		// assuming a 1-to-1 correspondence

		ArrayList<Marker> markers = new ArrayList<Marker>();
		for (String id : markerIDs.split(",")) {
			SearchResults<Marker> markerSR = markerFinder.getMarkerByID(id);
			List<Marker> markerList = markerSR.getResultObjects();

			if (markerList.size() < 1) {
				return errorMav("No marker found for ID " + id);
			} else if (markerList.size() > 1) {
				return errorMav("ID " + id + " refers to " + markerList.size() + " markers");
			}

			markers.add(markerList.get(0));
		}

		// re-sort our list of markers to be sorted by nomenclature

		Collections.sort (markers, markers.get(0).getComparator());

		// create our mav and add the marker to it

		ModelAndView mav = new ModelAndView("interaction_summary");
		mav.addObject("markers", markers);
		mav.addObject("queryString", request.getQueryString());

		return mav;
	}

	/** get the list of marker IDs that interact with (and include) the
	 * given marker IDs.  Must take into account the ordering, as well.
	 * Return them as a comma-delimited string.
	 */

	@RequestMapping("/idList")
	public @ResponseBody String getIdList (HttpServletRequest request, HttpServletResponse response) {
		logger.debug("starting getIdList()");

		// parse the various query parameters to generate SearchParams
		// object
		AjaxUtils.prepareAjaxHeaders(response);
		SearchParams params = new SearchParams();
		params.setFilter(requestToFilter(request));
		params.setSorts(genSorts(request));
		params.setPageSize(20000);

		logger.debug("Params: " + params);

		SearchResults<SolrInteraction> sr = interactionFinder.getInteraction(params);
		List<SolrInteraction> interactionList = sr.getResultObjects();
		logger.debug("Controller received " + interactionList.size()
				+ " SolrInteraction objects");

		// gather marker id strings in batches

		// We want to ensure that we are building our query string of IDs
		// in the order that we want the batch query to return them (which
		// is the order displayed in the interactions table).  So, we use
		// the map to track which ones we've already included, but we
		// don't generate the list from it or we lose our ordering.

		// maps from an ID to an empty string, signfying that we have
		// already included that ID in our request string
		HashMap<String, String> map = new HashMap<String, String>();

		// collects the IDs to be passed to be batch query
		StringBuffer ids = new StringBuffer();

		// add the input IDs first
		Map<String,String[]> parms = request.getParameterMap();
		List<String> inputIDs = Arrays.asList(parms.get("markerIDs"));

		for (String myId : inputIDs) {
			map.put(myId, "");
			ids.append(myId);
			ids.append(", ");
		}

		// now look up and add any other IDs

		Iterator<SolrInteraction> it = interactionList.iterator();
		while (it.hasNext()) {
			SolrInteraction reg = it.next();
			if (reg != null) {
				String orgID = reg.getOrganizerID();
				String partID = reg.getParticipantID();

				if (!map.containsKey(orgID)) {
					ids.append(orgID);
					ids.append(", ");
					map.put(orgID, "");
				}

				if (!map.containsKey(partID)) {
					ids.append(partID);
					ids.append(", ");
					map.put(partID, "");
				}
			}
		}

		logger.debug("Found " + map.size() + " IDs");
		return ids.toString();
	}

	//----------------------//
	// JSON summary results
	//----------------------//
	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<InteractionSummaryRow> interactionSummaryJson(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Paginator page) {

		logger.debug("->interactionSummaryResponse started");
		AjaxUtils.prepareAjaxHeaders(response);
		// 'request' has a 'markerIDs' field that is a comma-separated list of
		// marker IDs.  Parse this into filters.

		int pageSizeInt = 25;
		String pageSize = request.getParameter("pageSize");

		if (pageSize == null) {
			pageSize = request.getParameter("results");
		}

		if (pageSize != null) {
			try {
				int c = Integer.parseInt(pageSize);
				if (c > 0) {
					pageSizeInt = c;
				}
			} catch (Throwable e) {
				logger.debug ("Unparseable pageSize: " + pageSize);
			}
		}

		// set up the search parameters with the ID filters and the number of
		// results to return

		SearchParams sp = new SearchParams();
		//	sp.setFilter(Filter.or(idFilters));
		sp.setFilter(requestToFilter(request));
		sp.setPaginator(page);
		sp.setSorts(genSorts(request));
		sp.setPageSize(pageSizeInt);

		// find the requested interaction objects

		SearchResults<SolrInteraction> sr = interactionFinder.getInteraction(sp);
		List<SolrInteraction> interactionList = sr.getResultObjects();
		logger.debug("Controller received " + interactionList.size() + " SolrInteraction objects");

		// build the List of SummaryRow wrapper objects for interactionList

		List<InteractionSummaryRow> summaryRows = new ArrayList<InteractionSummaryRow> ();

		Iterator<SolrInteraction> it = interactionList.iterator();
		logger.debug("About to enter loop");
		while (it.hasNext()) {
			SolrInteraction reg = it.next();
			if (reg != null) {
				summaryRows.add(new InteractionSummaryRow(reg));
			}
		}
		logger.debug("Exited loop");

		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<InteractionSummaryRow> jsonResponse = new JsonSummaryResponse<InteractionSummaryRow>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(sr.getTotalCount());

		logger.debug("jsonResponse.size() = " + sr.getTotalCount());
		return jsonResponse;
	}





	/*
	 * This method handles requests various reports; txt, xls.  It is intended 
	 * to perform the same query as the json method above, but only place the 
	 * result objects list on the model.  It returns a string to indicate the
	 * view name to look up in the view class in the excel or text.properties
	 */

	@RequestMapping("/report*")
	public ModelAndView relationshipSummaryReport(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("relationshipSummaryReport");

		String markerIDs = request.getParameter("markerIDs");
		ArrayList<Filter> idFilters = new ArrayList<Filter>();

		if (markerIDs != null) {
			for (String id : markerIDs.split(",")) {
				idFilters.add(new Filter(SearchConstants.MRK_ID, id.trim()) );
			}
		}

		SearchParams sp = new SearchParams();
		sp.setFilter(Filter.or(idFilters));
		SearchResults<SolrInteraction> sr = interactionFinder.getInteraction(sp);

		ModelAndView mav = new ModelAndView("relationshipSummaryReport");
		mav.addObject("markerIDs", markerIDs);	
		mav.addObject("results", sr.getResultObjects());
		return mav;			
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	/* build the given error 'message' into a ModelAndView which goes to the
	 * error page, and return that ModelAndView
	 */
	private ModelAndView errorMav(String message) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", message);
		return mav;
	}

	// generate the sorts, based on 'sort' and 'dir' parameters
	private List<Sort> genSorts(HttpServletRequest request) {

		logger.debug("->genSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied

		String sortRequested = request.getParameter("sort");

		if ("linkedOrganizer".equals(sortRequested)) {
			sortRequested = SortConstants.BY_FEATURE_1;
		} else if ("relationshipTerm".equals(sortRequested)) {
			sortRequested = SortConstants.BY_INTERACTION;
		} else if ("linkedParticipant".equals(sortRequested)) {
			sortRequested = SortConstants.BY_FEATURE_2;
		} else if ("validation".equals(sortRequested)) {
			sortRequested = SortConstants.BY_VALIDATION;
		} else if ("dataSource".equals(sortRequested)) {
			sortRequested = SortConstants.BY_DATA_SOURCE;
		} else if ("score".equals(sortRequested)) {
			sortRequested = SortConstants.BY_SCORE;
		} else if ("reference".equals(sortRequested)) {
			sortRequested = SortConstants.BY_REFERENCE;
		} else {
			sortRequested = SortConstants.BY_SCORE;
		}

		String dirRequested  = request.getParameter("dir");
		boolean desc = false;
		if("desc".equalsIgnoreCase(dirRequested) || "yui-dt-desc".equalsIgnoreCase(dirRequested)){
			desc = true;
		}

		Sort sort = new Sort(sortRequested, desc);
		sorts.add(sort);

		return sorts;
	}



	/*
	 * Forward to Batch Query
	 */
	@RequestMapping(value="/batch")
	public String forwardToBatch(HttpServletRequest request) {

		logger.debug("forwarding interaction markers to batch");

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setFilter(requestToFilter(request));
		params.setSorts(genSorts(request));
		params.setPageSize(20000);

		logger.debug("Params: " + params);

		SearchResults<SolrInteraction> sr = interactionFinder.getInteraction(params);
		List<SolrInteraction> interactionList = sr.getResultObjects();
		logger.debug("Controller received " + interactionList.size() + " SolrInteraction objects");

		// gather marker id strings in batches

		// We want to ensure that we are building our query string of
		// IDs in the order that we want the batch query to return
		// them (which is the order displayed in the interactions 
		// table).  So, we use the map to track which ones we've
		// already included, but we don't generate the list from it
		// or we lose our ordering.

		// maps from an ID to an empty string, signfying that we havee
		// already included that ID in our request string
		HashMap<String, String> map = new HashMap<String, String>();

		// collects the IDs to be passed to be batch query
		StringBuffer ids = new StringBuffer();

		Iterator<SolrInteraction> it = interactionList.iterator();
		logger.debug("About to enter loop");
		while (it.hasNext()) {
			SolrInteraction reg = it.next();
			if (reg != null) {
				String orgID = reg.getOrganizerID();
				String partID = reg.getParticipantID();

				if (!map.containsKey(orgID)) {
					ids.append(orgID);
					ids.append(", ");
					map.put(orgID, "");
				}

				if (!map.containsKey(partID)) {
					ids.append(partID);
					ids.append(", ");
					map.put(partID, "");
				}
			}
		}

		//logger.debug("Batch Ids: " + ids);
		logger.debug("Batch Ids: " + map.size());

		// setup batch query object
		BatchQueryForm batchQueryForm = new BatchQueryForm();
		batchQueryForm.setIds(ids.toString());
		batchQueryForm.setIdType("MGI");

		List<String> bqAttrib = batchQueryForm.getAttributes();
		bqAttrib.add ("Location");
		batchQueryForm.setAttributes(bqAttrib);

		request.setAttribute("queryForm", batchQueryForm);

		return "forward:/mgi/batch/forwardSummary";
	}


	/* traverse the items in the given list, split any items containing commas
	 * into separate items, returning a new list containing the original items
	 * without commas and the newly split items.  Also strips whitespace from
	 * items in the list.
	 */
	private static List<String> splitCommas (List<String> items) {
		ArrayList<String> out = new ArrayList<String>();

		for (String s : items) {
			for (String t : s.split(",")) {
				out.add(t.trim());
			}
		}
		return out;
	}

	/* parse the InteractionQueryForm bean and translate it into a Filter
	 * object that represents the query.
	 */
	private Filter parseInteractionQueryForm(InteractionQueryForm qf) {
		List<Filter> filters = new ArrayList<Filter>();

		logger.debug("parseInteractionQueryForm()");

		if (qf.getMarkerIDs().size() > 0) {
			filters.add(new Filter(SearchConstants.MRK_ID, splitCommas(qf.getMarkerIDs()), Filter.Operator.OP_IN));
			logger.debug("marker IDs: " + qf.getMarkerIDs().toString());
		}

		if (qf.getRelationshipTermFilter().size() > 0) {
			filters.add(new Filter(SearchConstants.RELATIONSHIP_TERM, splitCommas(qf.getRelationshipTermFilter()), Filter.Operator.OP_IN));
		}

		if (qf.getValidationFilter().size() > 0) {
			filters.add(new Filter(SearchConstants.VALIDATION, splitCommas(qf.getValidationFilter()), Filter.Operator.OP_IN));
		}

		if (qf.getDataSourceFilter().size() > 0) {
			filters.add(new Filter(SearchConstants.DATA_SOURCE, splitCommas(qf.getDataSourceFilter()), Filter.Operator.OP_IN));
		}

		if (qf.getScoreFilter() != null) {
			filters.add(Filter.greaterOrEqual(SearchConstants.SCORE_FILTERABLE, qf.getScoreFilter()) );
		}

		if (qf.getRequireScore()) {
			filters.add(Filter.greaterOrEqual(SearchConstants.SCORE_NUMERIC, "0"));
		}

		logger.debug("filters: " + filters.toString());

		if (filters.size() == 1) {
			return filters.get(0);
		}

		Filter andFilter = new Filter();
		andFilter.setNestedFilters(filters, Filter.JoinClause.FC_AND);

		logger.debug("andFilter: " + andFilter.toString());

		return andFilter;
	}

	/* parse the response from Solr for a facet query and return it as a
	 * Map with two possible keys (error and resultFacets).
	 */
	private Map<String, List<String>> parseFacetResponse (SearchResults<SolrInteraction> results, String order) {

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();

		if (results.getResultFacets().size() >= facetLimit) {
			l.add("Too many results to display.  Modify your search or try another filter first.");
			m.put("error", l);
		} else if (results.getResultFacets().size() == 0) {
			l.add("No values in results to filter.");
			m.put("error", l);
		} else if (ALPHA.equals(order)) {
			m.put("resultFacets", results.getSortedResultFacets());
		} else if (RAW.equals(order)) {
			m.put("resultFacets", results.getResultFacets());
		} else {
			// fallback on alpha if unknown ordering
			m.put("resultFacets", results.getSortedResultFacets());
		}
		return m;
	}

	/* convert an HttpServletRequest object to a Filter object, likely
	 * containing other embedded Filter objects
	 */
	private Filter requestToFilter (HttpServletRequest request) {
		InteractionQueryForm form = new InteractionQueryForm();

		// process fields one-by-one and dump them in a query form, then
		// use the method to convert a QF to a Filter

		Map<String,String[]> parms = request.getParameterMap();

		Iterator<String> it = parms.keySet().iterator();

		while (it.hasNext()) {
			String field = it.next();
			List<String> values = Arrays.asList(parms.get(field));

			logger.debug("field: " + field);
			logger.debug("values: " + values);

			if ((values == null) || (values.size() == 0)) {
				// shouldn't happen, but skip value-less fields if it does

			} else if ("markerIDs".equals(field)) {
				ArrayList<String> ids = new ArrayList<String>();

				for (String idString : values) {
					for (String markerID : idString.split(",")) {
						ids.add(markerID);
					}
				}
				form.setMarkerIDs(ids);

			} else if ("validationFilter".equals(field)) {
				form.setValidationFilter(values);
			} else if ("relationshipTermFilter".equals(field)) {
				form.setRelationshipTermFilter(values);
			} else if ("dataSourceFilter".equals(field)) {
				form.setDataSourceFilter(values);
			} else if ("scoreFilter".equals(field)) {
				if (values.size() >= 1) {
					form.setScoreFilter(values.get(0));
				}
			} else if ("requireScore".equals(field)) {
				if (values.size() >= 1) {
					if ("true".equals(values.get(0).toLowerCase())) {
						form.setRequireScore(true);
					} else {
						form.setRequireScore(false);
					}
				}
			}
		}
		return parseInteractionQueryForm(form);
	}

	// -----------------------
	// handle facets (filters)
	// -----------------------

	public Map<String, List<String>> facetGeneric (InteractionQueryForm qf, BindingResult result, String facetType) {

		logger.debug(qf.toString());
		String order = ALPHA;

		if (FacetConstants.INT_SCORE.equals(facetType)) {
			qf.setRequireScore(true);
		}

		SearchParams params = new SearchParams();
		params.setFilter(parseInteractionQueryForm(qf));

		SearchResults<SolrInteraction> facetResults = null;

		if (FacetConstants.INT_SCORE.equals(facetType)) {
			facetResults = interactionFinder.getScoreFacet(params);
		} else if (FacetConstants.INT_VALIDATION.equals(facetType)) {
			facetResults = interactionFinder.getValidationFacet(params);
		} else if (FacetConstants.INT_INTERACTION.equals(facetType)) {
			facetResults = interactionFinder.getInteractionFacet(params);
		} else if (FacetConstants.INT_DATA_SOURCE.equals(facetType)) {
			facetResults = interactionFinder.getDataSourceFacet(params);
		} else {
			facetResults = new SearchResults<SolrInteraction>();
		}
		return parseFacetResponse(facetResults, order);
	}

	/* get a list of validation values for the validation facet list,
	 * returned as JSON
	 */
	@RequestMapping("/facet/validation")
	public @ResponseBody Map<String, List<String>> facetValidation (@ModelAttribute InteractionQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return facetGeneric (qf, result, FacetConstants.INT_VALIDATION);
	}

	/* get a list of interaction terms for the interaction facet list,
	 * returned as JSON
	 */
	@RequestMapping("/facet/interaction")
	public @ResponseBody Map<String, List<String>> facetInteraction (@ModelAttribute InteractionQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return facetGeneric (qf, result, FacetConstants.INT_INTERACTION);
	}

	/* get a list of data source values for the data source facet list,
	 * returned as JSON
	 */
	@RequestMapping("/facet/dataSource")
	public @ResponseBody Map<String, List<String>> facetDataSource (@ModelAttribute InteractionQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return facetGeneric (qf, result, FacetConstants.INT_DATA_SOURCE);
	}

	/* get the minimum and maximum numeric scores for the score filter,
	 * returned as JSON
	 */
	@RequestMapping("/facet/score")
	public @ResponseBody Map<String, List<String>> facetScore (@ModelAttribute InteractionQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return facetGeneric (qf, result, FacetConstants.INT_SCORE);
	}
}
