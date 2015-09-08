package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.finder.MarkerAnnotationFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.MarkerAnnotationQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.GOSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*
 * This controller maps all /go/ uri's
 */
@Controller
@RequestMapping(value="/go")
public class GOController {

	private Logger logger = LoggerFactory.getLogger(GOController.class);

	@Autowired
	private MarkerAnnotationFinder markerAnnotationFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@RequestMapping(value="/reference/{referenceID}")
	public ModelAndView goSummaryByReferenceId(@PathVariable("referenceID") String referenceID) {
		logger.debug("->goSummaryByReferenceId started");

		// setup search parameters object to gather the requested object
		SearchParams searchParams = new SearchParams();
		Filter referenceIDFilter = new Filter(SearchConstants.REF_ID,
				referenceID);
		searchParams.setFilter(referenceIDFilter);

		// find the requested reference
		SearchResults<Reference> searchResults
		= referenceFinder.getReferenceByID(searchParams);
		List<Reference> referenceList = searchResults.getResultObjects();

		return goSummaryByReference(referenceList, referenceID);
	}

	@RequestMapping(value="/reference/key/{referenceKey}")
	public ModelAndView goSummaryByReferenceKey(
			@RequestParam("referenceKey") String referenceKey) {

		logger.debug("->goSummaryByReferenceKey started: " + referenceKey);

		// find the requested reference
		SearchResults<Reference> searchResults
		= referenceFinder.getReferenceByKey(referenceKey);
		List<Reference> referenceList = searchResults.getResultObjects();

		return goSummaryByReference(referenceList, referenceKey);
	}

	private ModelAndView goSummaryByReference(List<Reference> referenceList,
			String reference){

		ModelAndView mav = new ModelAndView("go_summary_reference");

		// there can be only one...
		if (referenceList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No reference found for " + reference);
			return mav;
		}
		if (referenceList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + reference);
			return mav;
		}
		// pull out the reference, and place into the mav
		Reference ref = referenceList.get(0);
		mav.addObject("reference", ref);

		// pre-generate query string
		mav.addObject("queryString", "referenceKey=" + ref.getReferenceKey() + "&vocab=GO");

		return mav;
	}

	@RequestMapping(value="/marker/{markerID}")
	public ModelAndView goSummaryByMarkerId(HttpServletRequest request,
	    @PathVariable("markerID") String markerID) {
		logger.debug("->goSummaryByMarkerId started");

		// look for an (optional) GO slimgrid header term, which would
		// restrict our set of results

		String header = request.getParameter("header");

		// setup search parameters object to gather the requested object
		SearchParams searchParams = new SearchParams();
		Filter markerIDFilter = new Filter(SearchConstants.MRK_ID, markerID);
		searchParams.setFilter(markerIDFilter);

		// find the requested marker
		SearchResults<Marker> searchResults
			= markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		return goSummaryByMarker(markerList, markerID, header);
	}

	@RequestMapping(value="/marker")
	public ModelAndView goSummaryByMarkerKey(HttpServletRequest request,
	    @RequestParam("key") String markerKey) {
		logger.debug("->goSummaryByMarkerKey started: " + markerKey);

		// look for an (optional) GO slimgrid header term, which would
		// restrict our set of results

		String header = request.getParameter("header");

		// find the requested markers
		SearchResults<Marker> searchResults
			= markerFinder.getMarkerByKey(markerKey);
		List<Marker> markerList = searchResults.getResultObjects();

		return goSummaryByMarker(markerList, markerKey, header);
	}

	private ModelAndView goSummaryByMarker(List<Marker> markerList, String mrk, String header){

		ModelAndView mav = new ModelAndView("go_summary_marker");

		// there can be only one...
		if (markerList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No marker found for " + mrk);
			return mav;
		}
		if (markerList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe marker found for " + mrk);
			return mav;
		}
		// pull out the marker, and place into the mav
		Marker marker = markerList.get(0);
		mav.addObject("marker", marker);

		// handle the (optional) GO slimgrid header term
		String headerParam = "";
		if (header != null) {
			mav.addObject("headerTerm", header);
			headerParam = "&header=" + header;
		}

		// pre-generate query string
		mav.addObject("queryString", "mrkKey=" + marker.getMarkerKey() + "&vocab=GO" + headerParam);

		return mav;
	}

	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<GOSummaryRow> seqSummaryJson(HttpServletRequest request,
			@ModelAttribute MarkerAnnotationQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->JsonSummaryResponse started");

		// generate search parms object;  add pagination, sorts, and filters
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(request));
		params.setFilter(genFilters(query));

		Marker m = null;
		Reference r = null;

		if (query.getMrkKey() != null) {
			SearchResults<Marker> sr = markerFinder.getMarkerByKey(query.getMrkKey());
			m = sr.getResultObjects().get(0);
		}

		if (query.getReferenceKey() != null) {
			SearchResults<Reference> sr = referenceFinder.getReferenceByKey(query.getReferenceKey());
			r = sr.getResultObjects().get(0);
		}

		// perform query, and pull out the requested objects
		SearchResults<Annotation> searchResults = markerAnnotationFinder.getMarkerAnnotations(params);
		List<Annotation> annotList = searchResults.getResultObjects();

		// create/load the list of SummaryRow wrapper objects
		List<GOSummaryRow> summaryRows = new ArrayList<GOSummaryRow> ();
		Iterator<Annotation> it = annotList.iterator();
		while (it.hasNext()) {
			Annotation annot = it.next();
			if (annot == null) {
				logger.debug("--> Null Object");
			} else if (m != null) {
				summaryRows.add(new GOSummaryRow(annot, m));
			} else if (r != null) {
				summaryRows.add(new GOSummaryRow(annot, r));
			}
		}

		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<GOSummaryRow> jsonResponse = new JsonSummaryResponse<GOSummaryRow>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		return jsonResponse;
	}

	//----------------------//
	// JSON summary results
	//----------------------//
	@RequestMapping("/report*")
	public ModelAndView seqSummaryExport(HttpServletRequest request, @ModelAttribute MarkerAnnotationQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->JsonSummaryResponse started");

		// generate search parms object;  add pagination, sorts, and filters
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(request));
		params.setFilter(genFilters(query));

		Marker m = null;
		Reference r = null;
		String view = null;

		if (query.getMrkKey() != null) {
			SearchResults<Marker> sr = markerFinder.getMarkerByKey(query.getMrkKey());
			m = sr.getResultObjects().get(0);
			view = "goMarkerSummaryReport";
		}

		if (query.getReferenceKey() != null) {
			SearchResults<Reference> sr = referenceFinder.getReferenceByKey(query.getReferenceKey());
			r = sr.getResultObjects().get(0);
			view = "goReferenceSummaryReport";
		}

		// perform query, and pull out the requested objects
		SearchResults<Annotation> searchResults = markerAnnotationFinder.getMarkerAnnotations(params);

		ModelAndView mav = new ModelAndView(view);
		if (m != null) { mav.addObject("marker", m); }
		if (r != null) { mav.addObject("reference", r); }
		mav.addObject("results", searchResults.getResultObjects());
		return mav;
	}

	// generate the sorts
	private List<Sort> genSorts(HttpServletRequest request) {

		logger.debug("->genSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied
		String sortRequested = request.getParameter("sort");
		String dirRequested  = request.getParameter("dir");

		boolean desc = false;
		if("desc".equalsIgnoreCase(dirRequested)){
			desc = true;
		}
		
		logger.debug("Sort: " + sortRequested);

		if("evidence".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.MRK_BY_EVIDENCE_CODE, desc));
		} else if("term".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.VOC_TERM, desc));
		} else {
			// Original Default
			sorts.add(new Sort(SortConstants.VOC_DAG_NAME, desc));
			sorts.add(new Sort(SortConstants.VOC_TERM, false));
		}

		logger.debug("sort: " + sorts.toString());
		return sorts;
	}

	// generate the filters
	private Filter genFilters(MarkerAnnotationQueryForm query){

		logger.debug("->genFilters started");
		logger.debug("QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();

		String mrkKey = query.getMrkKey();
		String refsKey = query.getReferenceKey();
		String vocab = query.getVocab();
		String restriction = query.getRestriction();
		String header = query.getHeader();
		List<String> aspectFilter = query.getAspectFilter();
		List<String> evidenceFilter = query.getEvidenceFilter();
		List<String> referenceFilter = query.getReferenceFilter();
		
		if ((header != null) && (!"".equals(header))) {
			filterList.add(new Filter (SearchConstants.SLIM_TERM, header));
		}
		if ((refsKey != null) && (!"".equals(refsKey))) {
			filterList.add(new Filter (SearchConstants.REF_KEY, refsKey, Filter.Operator.OP_EQUAL));
		}
		if ((mrkKey != null) && (!"".equals(mrkKey))) {
			filterList.add(new Filter (SearchConstants.MRK_KEY, mrkKey, Filter.Operator.OP_EQUAL));
		}
		if ((vocab != null) && (!"".equals(vocab))) {
			filterList.add(new Filter (SearchConstants.VOC_VOCAB, vocab, Filter.Operator.OP_EQUAL));
		}
		if ((restriction != null) && (!"".equals(restriction))) {
			filterList.add(new Filter (SearchConstants.VOC_RESTRICTION, restriction, Filter.Operator.OP_NOT_EQUAL));
		}   
		if (aspectFilter.size() > 0) {
			filterList.add(new Filter(SearchConstants.VOC_DAG_NAME, aspectFilter, Filter.Operator.OP_IN));
		}
		if (evidenceFilter.size() > 0) {
			filterList.add(new Filter(SearchConstants.EVIDENCE_CATEGORY, evidenceFilter, Filter.Operator.OP_IN));
		}
		if (referenceFilter.size() > 0) {
			filterList.add(new Filter(SearchConstants.REF_KEY, referenceFilter, Filter.Operator.OP_IN));
		}
		
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		}

		//	logger.debug("Filters: " + containerFilter.toString());
		return containerFilter;
	}

	@RequestMapping(value="/facet/{type}")
	public @ResponseBody Map<String, List<String>> facets (@ModelAttribute MarkerAnnotationQueryForm qf, BindingResult result, @PathVariable("type") String facetType) {

		System.out.println("facetType: " + facetType);
		System.out.println("qf: " + qf);
		
		Map<String, List<String>> facets = new HashMap<String, List<String>>();
		//List<String> l = new ArrayList<String>();
		
		SearchParams params = new SearchParams();
		params.setFilter(genFilters(qf));

		if("evidence".equalsIgnoreCase(facetType)) {
			facetType = SearchConstants.EVIDENCE_CATEGORY;
		} else if("term".equalsIgnoreCase(facetType)) {
			facetType = SortConstants.VOC_TERM;
		} else if("reference".equalsIgnoreCase(facetType)) {
			facetType = SortConstants.BY_REFERENCE;
		} else if("aspect".equalsIgnoreCase(facetType)){
			facetType = SortConstants.VOC_DAG_NAME;
		}
		
		SearchResults<Annotation> facetResults = markerAnnotationFinder.getFacetResults(params, facetType);

		List<String> results = facetResults.getSortedResultFacets();

		// need to customize sorting for evidence facets:
		//	Experimental, Homology, Automated, Other
		if (SearchConstants.EVIDENCE_CATEGORY.equalsIgnoreCase(facetType)) {
			Collections.sort (results, new EvidenceFacetSorter());
		}

		facets.put("resultFacets", results);
		return facets;
	}
}

class EvidenceFacetSorter implements Comparator<String> {
	public EvidenceFacetSorter() {}

	public int rank(String a) {
		if (a.startsWith("Experimental")) { return 0; }
		if (a.startsWith("Homology")) { return 1; }
		if (a.startsWith("Automated")) { return 2; }
		if (a.startsWith("Other")) { return 3; }
		return 4;
	}

	public int compare (String a, String b) {
		int aRank = this.rank(a);
		int bRank = this.rank(b);

		if (aRank == bRank) { return a.compareTo(b); }
		return Double.compare(aRank, bRank);
	}

	public boolean equals (Object c) {
		if (this == c) { return true; }
		return false;
	}
}	

