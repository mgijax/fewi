package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

// fewi
import org.jax.mgi.fewi.finder.MarkerAnnotationFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.forms.MarkerAnnotationQueryForm;
import org.jax.mgi.fewi.summary.OMIMSummaryRow;

// data model objects
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.Reference;


/*--------------------------------------*/
/* standard imports for all controllers */
/*--------------------------------------*/

// internal
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;

// external
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /omim/ uri's
 */
@Controller
@RequestMapping(value="/omim")
public class OMIMController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MarkerAnnotationFinder markerAnnotationFinder;

    @Autowired
    private MarkerFinder markerFinder;

    //-------------------------------//
    // OMIM Summary by Marker
    //-------------------------------//
    
    @RequestMapping(value="/marker/{markerID}")
    public ModelAndView annotationSummaryByMarker(@PathVariable("markerID") String markerID) {

        logger.debug("->omimSummaryByMarker started");

        ModelAndView mav = new ModelAndView("omim_summary_marker");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        Filter markerIDFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIDFilter);

        // find the requested reference
        SearchResults searchResults
          = markerFinder.getMarkerByID(searchParams);
        List<Marker> mrkList = searchResults.getResultObjects();

        // there can be only one...
        if (mrkList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No marker found for " + markerID);
            return mav;
        }
        if (mrkList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe marker found for " + markerID);
            return mav;
        }

        // pull out the reference, and place into the mav
        Marker marker = mrkList.get(0);
        mav.addObject("marker", marker);

        // pre-generate query string
        mav.addObject("queryString", "mrkKey=" + marker.getMarkerKey()+"&vocab=OMIM&restriction=not");
        
        return mav;
    }


    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<OMIMSummaryRow> seqSummaryJson(
            HttpServletRequest request,
			@ModelAttribute MarkerAnnotationQueryForm query,
            @ModelAttribute Paginator page) {

        logger.debug("->JsonSummaryResponse started");

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(query));

        // perform query, and pull out the requested objects
        SearchResults searchResults
          = markerAnnotationFinder.getMarkerAnnotations(params);
        List<Annotation> markerAnnotList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<OMIMSummaryRow> summaryRows = new ArrayList<OMIMSummaryRow> ();
        Iterator<Annotation> it = markerAnnotList.iterator();
        while (it.hasNext()) {
            Annotation markerAnnot = it.next();
            if (markerAnnot == null) {
                logger.debug("--> Null Object");
            }else {
                summaryRows.add(new OMIMSummaryRow(markerAnnot));
            }
        }

        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<OMIMSummaryRow> jsonResponse
          = new JsonSummaryResponse<OMIMSummaryRow>();

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
            sortRequested = SortConstants.VOC_TERM;
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
    private Filter genFilters(MarkerAnnotationQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);


        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String mrkKey = query.getMrkKey();
        String vocab = query.getVocab();
        String restriction = query.getRestriction();

        //
        if ((mrkKey != null) && (!"".equals(mrkKey))) {
            filterList.add(new Filter (SearchConstants.MRK_KEY, mrkKey,
                Filter.OP_EQUAL));
        }
        if ((vocab != null) && (!"".equals(vocab))) {
            filterList.add(new Filter (SearchConstants.VOC_VOCAB, vocab,
                Filter.OP_EQUAL));
        }
        if ((restriction != null) && (!"".equals(restriction))) {
            filterList.add(new Filter (SearchConstants.VOC_RESTRICTION, restriction,
                Filter.OP_NOT_EQUAL));
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
