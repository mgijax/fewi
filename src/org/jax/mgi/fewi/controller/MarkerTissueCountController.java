package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.jax.mgi.fewi.finder.FooFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.MarkerTissueCountFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.FooQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.FooSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerTissueCountSummaryRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /foo/ uri's
 */
@Controller
@RequestMapping(value="/tissue")
public class MarkerTissueCountController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(MarkerTissueCountController.class);

    @Autowired
    private FooFinder fooFinder;

    @Autowired
    private MarkerTissueCountFinder tFinder;
    
    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private MarkerFinder markerFinder;
    
    //--------------------------------//
    // Tissue Count Summary by Marker
    //--------------------------------//
    
    @RequestMapping(value="/marker/{markerID}")
    public ModelAndView fooSummeryByRef(@PathVariable("markerID") String markerID) {

        logger.debug("->markerTissueCountSummary started");

        ModelAndView mav = new ModelAndView("marker_tissue_summary");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        Filter markerKeyFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerKeyFilter);

        // find the requested reference
        SearchResults searchResults
          = markerFinder.getMarkerByID(searchParams);
        List<Marker> markerList = searchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + markerID);
            return mav;
        }
        if (markerList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe references found for " + markerID);
            return mav;
        }

        // pull out the reference, and place into the mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);
                
        // pre-generate query string
        mav.addObject("queryString", "mrkKey=" + marker.getMarkerKey());

        return mav;
    }

    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<MarkerTissueCountSummaryRow> seqSummaryJson(
            HttpServletRequest request,
			@ModelAttribute FooQueryForm query,
            @ModelAttribute Paginator page) {

        logger.debug("->JsonSummaryResponse started");

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(query));

        // perform query, and pull out the requested objects
        SearchResults searchResults
          = tFinder.getFoos(params);
        List<MarkerTissueCount> fooList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<MarkerTissueCountSummaryRow> summaryRows = new ArrayList<MarkerTissueCountSummaryRow> ();
        Iterator<MarkerTissueCount> it = fooList.iterator();
        
        logger.debug("About to iterate through the tissue counts");
        
        while (it.hasNext()) {
            MarkerTissueCount mtc = it.next();
            logger.debug("Marker Key: " + mtc.getMarkerKey() + " Structure: " + mtc.getStructure());
            if (mtc == null) {
                logger.debug("--> Null Object");
            }else {
                summaryRows.add(new MarkerTissueCountSummaryRow(mtc));
            }
        }

        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<MarkerTissueCountSummaryRow> jsonResponse
          = new JsonSummaryResponse<MarkerTissueCountSummaryRow>();

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        return jsonResponse;
    }

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    // generate the sorts
    private List<Sort> genSorts(HttpServletRequest request) {

        logger.debug("->genSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");
        if (sortRequested == null) {
            sortRequested = SortConstants.FOO_SORT;
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

    // generate the filters
    private Filter genFilters(FooQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);


        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String param1 = query.getParam1();
        String param2 = query.getParam2();

        //
        if ((param1 != null) && (!"".equals(param1))) {
            filterList.add(new Filter (SearchConstants.FOO_ID, param1,
                Filter.OP_EQUAL));
        }

        //
        if ((param2 != null) && (!"".equals(param2))) {
            filterList.add(new Filter (SearchConstants.FOO_ID, param2,
                Filter.OP_EQUAL));
        }

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }

        return containerFilter;
    }

}
