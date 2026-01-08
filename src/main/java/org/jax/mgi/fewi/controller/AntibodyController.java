package org.jax.mgi.fewi.controller;

import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.Antibody;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.finder.AntibodyFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.AntibodyQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.shr.jsonmodel.AntibodyJ;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /antibody/ uri's
 */
@Controller
@RequestMapping(value="/antibody")
public class AntibodyController {


    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger
      = LoggerFactory.getLogger(AntibodyController.class);

    @Autowired
    private AntibodyFinder antibodyFinder;
 
    @Autowired
    private MarkerFinder markerFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private IDLinker idLinker;

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------------------------//
    // Antibody Detail by Antibody Key
    //--------------------------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView antibodyDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

	logger.debug ("-> antibodyDetailByKey started");

	// find the requested antibody by database key

	List<Antibody> antibodyList = antibodyFinder.getAntibodyByKey(dbKey);

	// should only be one.  error condition if not.

	if (antibodyList == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Antibody Found");
	    return mav;
	} else if (antibodyList.size() < 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Antibody Found");
	    return mav;
	} else if (antibodyList.size() > 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Unique Antibody Key Found");
	    return mav;
	}

        Antibody antibody = antibodyList.get(0);
	if (antibody == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Antibody Found");
	    return mav;
	}

	return prepareAntibody(antibody.getPrimaryID(), "antibody/antibody_detail");
    }

    //--------------------//
    // Antibody Detail By ID
    //--------------------//
    @RequestMapping(value="/{antibodyID:.+}", method = RequestMethod.GET)
    public ModelAndView antibodyDetailByID(HttpServletRequest request, @PathVariable("antibodyID") String antibodyID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.info("->antibodyDetailByID started, id=" + antibodyID);

	return prepareAntibody(antibodyID, "antibody/antibody_detail");
    }

    // --------------------------------------------------//
    // Shared code for populating the ModelAndView object
    // --------------------------------------------------//

    // code shared to send back a antibody detail page, regardless of
    // whether the initial link was by antibody ID or by database key
    private ModelAndView prepareAntibody (String antibodyID, String view) {

        List<Antibody> antibodyList = antibodyFinder.getAntibodyByID(antibodyID);
        // there can be only one...
        if (antibodyList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            logger.info("No Antibody Found");
            mav.addObject("errorMsg", "No Antibody Found");
            return mav;
        } else if (antibodyList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate Antibody ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView(view);
        
        //pull out the Antibody, and add to mav
        Antibody antibody = antibodyList.get(0);
        mav.addObject("antibody", antibody);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);

        return mav;
    }

	// no query form for antibodies; summary is only accessible from marker detail and reference summary/detail
	
	/* summary page for a marker
	 */
	@RequestMapping(value="/marker/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView markerSummary(HttpServletRequest request, @PathVariable("markerID") String markerID, @ModelAttribute AntibodyQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		queryForm.setMarkerID(markerID);
		return summaryPage(queryForm);
	}

	/* summary page for a reference
	 */
	@RequestMapping(value="/reference/{referenceID:.+}", method = RequestMethod.GET)
	public ModelAndView referenceSummary(HttpServletRequest request, @PathVariable("referenceID") String referenceID, @ModelAttribute AntibodyQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		queryForm.setReferenceID(referenceID);
		return summaryPage(queryForm);
	}

	/* generic method for summary pages by both marker and reference
	 */
	private ModelAndView summaryPage(AntibodyQueryForm queryForm) {
		ModelAndView mav = new ModelAndView("antibody/antibody_summary");
		mav.addObject("queryString", queryForm.toQueryString());
		
		// if we have a marker ID, then look up the marker and add it to the mav
		if (queryForm.getMarkerID() != null) {
			SearchResults<Marker> markerResults = markerFinder.getMarkerByID(queryForm.getMarkerID());
			if (markerResults.getTotalCount() != 1) {
				return errorMav("Marker ID " + queryForm.getMarkerID() + " does not return a single marker.");
			}
			Marker marker = markerResults.getResultObjects().get(0);
			mav.addObject("marker", marker);
			mav.addObject("title", "Antibody Summary for " + marker.getSymbol());
			mav.addObject("description", "Molecular antibodies associated with mouse " + marker.getMarkerType()
				+ " " + marker.getSymbol());

		} else if (queryForm.getReferenceID() != null) {
			// or if we have a reference ID, look up the reference and add it to the mav
			SearchResults<Reference> referenceResults = referenceFinder.getReferenceByID(queryForm.getReferenceID());
			if (referenceResults.getTotalCount() != 1) {
				return errorMav("Reference ID " + queryForm.getReferenceID() + " does not return a single reference.");
			}
			Reference reference = referenceResults.getResultObjects().get(0);
			mav.addObject("reference", reference);
			mav.addObject("title", "Antibody Summary for " + reference.getJnumID());
			mav.addObject("description", "Molecular antibodies associated with reference " + reference.getJnumID()
				+ ", " + reference.getMiniCitation());

		} else {
			return errorMav("Antibody summary must be by either marker ID or reference ID.");
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

        /* table of antibody data for summary page, to be requested via JSON
         */
        @RequestMapping("/table")
        public ModelAndView antibodyTable (@ModelAttribute AntibodyQueryForm query, @ModelAttribute Paginator page) {

                logger.debug("->antibodyTable started");

                // perform query, and pull out the requested objects
                SearchResults<AntibodyJ> searchResults = getSummaryResults(query, page);
                List<AntibodyJ> antibodyList = searchResults.getResultObjects();

                ModelAndView mav = new ModelAndView("antibody/antibody_summary_table");
                mav.addObject("antibodies", antibodyList);
                mav.addObject("count", antibodyList.size());
                mav.addObject("totalCount", searchResults.getTotalCount());

                return mav;
        }

	// This is a convenience method to handle packing the SearchParams object
	// and return the SearchResults from the finder.
	private SearchResults<AntibodyJ> getSummaryResults(@ModelAttribute AntibodyQueryForm query, @ModelAttribute Paginator page) {

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(query));
		params.setFilter(genFilters(query));
		
		// perform query, return SearchResults 
		return antibodyFinder.getAntibodies(params);
	}

	/* generate the sorts (differs based on summary by marker or by reference)
	 */
	private List<Sort> genSorts(AntibodyQueryForm queryForm) {
		logger.debug("->genSorts started");

		boolean desc = false;
		String sort = SortConstants.ANTIBODY_BY_NAME;

		String markerID = queryForm.getMarkerID();
		String referenceID = queryForm.getReferenceID();
		if( markerID != null && !markerID.equals("")) {
		    sort = SortConstants.ANTIBODY_BY_REF_COUNT;
		} else if (referenceID != null && !referenceID.equals("")) {
		    sort = SortConstants.ANTIBODY_BY_GENE;
		}
		
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(sort, desc));
		return sorts;
	}
	
	/* generate the filters (translate the query parameters into Solr filters)
	 */
	private Filter genFilters(AntibodyQueryForm query){
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
		
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		}

		return containerFilter;
	}
}
