package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ProbeFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.ProbeQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.shr.jsonmodel.MolecularProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.springframework.web.bind.annotation.PathVariable;

/*
 * This controller maps all /probe/ uri's
 */
@Controller
@RequestMapping(value="/probe")
public class ProbeController {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(ProbeController.class);

	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired
	private ProbeFinder probeFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	//--- public methods ---//

	// no query form for probes; summary is only accessible from marker detail and reference summary/detail
	
	/* summary page for a marker
	 */
	@RequestMapping(value="/marker/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView markerSummary(@PathVariable("markerID") String markerID, @ModelAttribute ProbeQueryForm queryForm) {
		logger.debug("->markerSummary started (" + markerID + ")");
		if ((queryForm != null) && (queryForm.getSegmentType() != null)) {
			logger.debug("  - " + queryForm.toString());
		}
		
		queryForm.setMarkerID(markerID);
		return summaryPage(queryForm);
	}

	/* summary page for a reference
	 */
	@RequestMapping(value="/reference/{referenceID:.+}", method = RequestMethod.GET)
	public ModelAndView referenceSummary(@PathVariable("referenceID") String referenceID, @ModelAttribute ProbeQueryForm queryForm) {
		logger.debug("->referenceSummary started (" + referenceID + ")");
		if ((queryForm != null) && (queryForm.getSegmentType() != null)) {
			logger.debug("  - " + queryForm.toString());
		}
		
		queryForm.setReferenceID(referenceID);
		return summaryPage(queryForm);
	}

	/* generic method for summary pages by both marker and reference
	 */
	private ModelAndView summaryPage(ProbeQueryForm queryForm) {
		ModelAndView mav = new ModelAndView("probe/probe_summary");
		mav.addObject("queryString", queryForm.toQueryString());
		
		if (queryForm.getSegmentType() != null) {
			mav.addObject("segmentType", queryForm.getSegmentType());
		}

		// if we have a marker ID, then look up the marker and add it to the mav
		if (queryForm.getMarkerID() != null) {
			SearchResults<Marker> markerResults = markerFinder.getMarkerByID(queryForm.getMarkerID());
			if (markerResults.getTotalCount() != 1) {
				return errorMav("Marker ID " + queryForm.getMarkerID() + " does not return a single marker.");
			}
			Marker marker = markerResults.getResultObjects().get(0);
			mav.addObject("marker", marker);
			mav.addObject("title", "Probe Summary for " + marker.getSymbol());
			mav.addObject("description", "Molecular probes associated with mouse " + marker.getMarkerType()
				+ " " + marker.getSymbol());

		} else if (queryForm.getReferenceID() != null) {
			// or if we have a reference ID, look up the reference and add it to the mav
			SearchResults<Reference> referenceResults = referenceFinder.getReferenceByID(queryForm.getReferenceID());
			if (referenceResults.getTotalCount() != 1) {
				return errorMav("Reference ID " + queryForm.getReferenceID() + " does not return a single reference.");
			}
			Reference reference = referenceResults.getResultObjects().get(0);
			mav.addObject("reference", reference);
			mav.addObject("title", "Probe Summary for " + reference.getJnumID());
			mav.addObject("description", "Molecular probes associated with reference " + reference.getJnumID()
				+ ", " + reference.getMiniCitation());

		} else {
			return errorMav("Probe summary must be by either marker ID or reference ID.");
		}
		return mav;
	}

	/* return a mav for an error screen with the given message filled in
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

	/* generate the sorts (differs based on summary by marker or by reference)
	 */
	private List<Sort> genSorts(ProbeQueryForm queryForm) {
		logger.debug("->genSorts started");

		// marker summary sorts by type; reference summary sorts by name

		String sort = SortConstants.PRB_BY_TYPE;	// default to type sort
		boolean desc = false;						// always sort ascending

		if (queryForm.getReferenceID() != null) {
			sort = SortConstants.PRB_BY_NAME;
		}
		
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(sort, desc));
		return sorts;
	}
	
	/* generate the filters (translate the query parameters into Solr filters)
	 */
	private Filter genFilters(ProbeQueryForm query){
		logger.debug("->genFilters started");
		logger.debug("  - QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();

		// marker ID
		String markerID = query.getMarkerID();
		if ((markerID != null) && (markerID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.PRB_MARKER_ID, markerID, Filter.Operator.OP_EQUAL));
		}
		
		// reference ID
		String referenceID = query.getReferenceID();
		if ((referenceID != null) && (referenceID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.PRB_REFERENCE_ID, referenceID, Filter.Operator.OP_EQUAL));
		}
		
		// segment type
		String segmentType = query.getSegmentType();
		if ((segmentType != null) && (segmentType.length() > 0)) {
			filterList.add(new Filter(SearchConstants.PRB_SEGMENT_TYPE, segmentType, Filter.Operator.OP_EQUAL));
		}
		
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		}

		return containerFilter;
	}
	
	@RequestMapping("/table")
	public ModelAndView probeTable (@ModelAttribute ProbeQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->probeTable started");

		// perform query, and pull out the requested objects
		SearchResults<MolecularProbe> searchResults = getSummaryResults(query, page);
		List<MolecularProbe> probeList = searchResults.getResultObjects();

		ModelAndView mav = new ModelAndView("probe/probe_summary_table");
		mav.addObject("probes", probeList);
		mav.addObject("count", probeList.size());
		mav.addObject("totalCount", searchResults.getTotalCount());

		return mav;
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	// This is a convenience method to handle packing the SearchParams object
	// and return the SearchResults from the finder.
	private SearchResults<MolecularProbe> getSummaryResults(@ModelAttribute ProbeQueryForm query, @ModelAttribute Paginator page) {

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(query));
		params.setFilter(genFilters(query));
		
		// perform query, return SearchResults 
		return probeFinder.getProbes(params);
	}
}
