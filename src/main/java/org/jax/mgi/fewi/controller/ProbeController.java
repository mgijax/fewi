package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.Probe;
import org.jax.mgi.fe.datamodel.Reference;
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
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.jsonmodel.MolecularProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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

    @Autowired
    private IDLinker idLinker;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	//--- public methods ---//

	// no query form for probes; summary is only accessible from marker detail and reference summary/detail
	
	/* summary page for a marker
	 */
	@RequestMapping(value="/marker/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView markerSummary(HttpServletRequest request, @PathVariable("markerID") String markerID, @ModelAttribute ProbeQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
	public ModelAndView referenceSummary(HttpServletRequest request, @PathVariable("referenceID") String referenceID, @ModelAttribute ProbeQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->referenceSummary started (" + referenceID + ")");
		if ((queryForm != null) && (queryForm.getSegmentType() != null)) {
			logger.debug("  - " + queryForm.toString());
		}
		
		queryForm.setReferenceID(referenceID);
		return summaryPage(queryForm);
	}

	/* table of probe data for summary page, to be requested via JSON
	 */
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

    @RequestMapping(value="/{probeID:.+}", method = RequestMethod.GET)
    public ModelAndView probeDetail(HttpServletRequest request, @PathVariable("probeID") String probeID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}


        logger.debug("->probeDetail started");

        List<Probe> probeList = probeFinder.getProbeByID(probeID);
        // there can be only one...
        if (probeList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Probe Found for ID " + probeID);
            return mav;
        } else if (probeList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "ID " + probeID + " is associated with multiple probes");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("probe/probe_detail");
        
        //pull out the Probe, and add to mav
        Probe probe = probeList.get(0);
        mav.addObject("probe", probe);
        addDetailSeo(probe, mav);
        flagDisplayFields(probe, mav);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);
        return mav;
    }

    /* Probe Detail by key
     */
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView probeDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->probeDetailByKey started");

        List<Probe> probeList = probeFinder.getProbeByKey(dbKey);
        // there can be only one...
        if (probeList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Probe Found for key " + dbKey);
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("probe/probe_detail");
        
        // pull out the Probe, and add to mav
        Probe probe = probeList.get(0);
        mav.addObject("probe", probe);
        addDetailSeo(probe, mav);
        flagDisplayFields(probe, mav);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);
        return mav;
    }

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

    // returns true if 's' has a value to show, false if it is null, Not Applicable, or Not Specified
    private boolean hasValueToShow(String s) {
    	return !((s == null) || "Not Applicable".equals(s) || "Not Specified".equals(s));
    }
    
    // adds flags to mav to indicate whether certain fields should be displayed (easier to handle here
    // in Java for cases where we want to suppress both Not Applicable and Not Specified)
    private void flagDisplayFields (Probe p, ModelAndView mav) {
    	if (hasValueToShow(p.getVector())) { mav.addObject("showVector", true); }
    	if (hasValueToShow(p.getStrain())) { mav.addObject("showStrain", true); }
    	if (hasValueToShow(p.getSex())) { mav.addObject("showSex", true); }
    	if (hasValueToShow(p.getAge())) { mav.addObject("showAge", true); }
    	if (hasValueToShow(p.getTissue())) { mav.addObject("showTissue", true); }
    	if (hasValueToShow(p.getCellLine())) { mav.addObject("showCellLine", true); }
    }
    
    // add SEO data (seoDescription, seoTitle, and seoKeywords) to the given detail page's mav
    private void addDetailSeo (Probe p, ModelAndView mav) {
    	List<String> synonyms = p.getSynonyms();
    	
    	// identify high-level segment type (probe or primer)
    	
    	String highLevelSegmentType = "Probe";
    	if ("primer".equals(p.getSegmentType()) ) {
    		highLevelSegmentType = "Primer";
    	}
    	mav.addObject("highLevelSegmentType", highLevelSegmentType);
    	
    	// compose browser / SEO title
    	
    	StringBuffer seoTitle = new StringBuffer();
    	seoTitle.append(p.getName());
    	seoTitle.append(" ");
    	seoTitle.append(highLevelSegmentType);
    	seoTitle.append(" Detail MGI Mouse ");
    	seoTitle.append(p.getPrimaryID());
    	mav.addObject("seoTitle", seoTitle.toString());
    	
    	// compose set of SEO keywords
    	
    	StringBuffer seoKeywords = new StringBuffer();
    	seoKeywords.append(p.getName());
    	if (!"Probe".equals(highLevelSegmentType)) {		// already adding lowercase 'probe' below
    		seoKeywords.append(", ");
    		seoKeywords.append(highLevelSegmentType); 
    	}
    	if ((synonyms != null) && (synonyms.size() > 0)) {
    		for (String synonym : synonyms) {
    			seoKeywords.append(", ");
    			seoKeywords.append(synonym);
    		}
    	}
    	seoKeywords.append(", probe, clone, mouse, mice, murine, Mus");
    	mav.addObject("seoKeywords", seoKeywords);
    	
    	// compose SEO description
    	
    	StringBuffer seoDescription = new StringBuffer();
    	seoDescription.append("View ");
    	seoDescription.append(highLevelSegmentType);
    	seoDescription.append(" ");
    	seoDescription.append(p.getName());
    	seoDescription.append(" : location, molecular source, gene associations, expression, sequences, polymorphisms, and references.");
    	mav.addObject("seoDescription", seoDescription);
    }
    
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
}
