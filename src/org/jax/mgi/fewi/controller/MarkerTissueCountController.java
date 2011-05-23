package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerTissueCount;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.MarkerTissueCountFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerTissueCountSummaryRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /tissue/ uri's
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
    private MarkerTissueCountFinder tFinder;
    
    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private MarkerFinder markerFinder;
    
    
    /* 
     * This method maps ajax requests from the reference summary page.  It 
     * parses the ReferenceQueryForm, generates SearchParams object, and issues
     * the query to the ReferenceFinder.  The results are returned as JSON
     */
    @RequestMapping("/marker/report*")
    public String tissueSummaryReport(
            HttpServletRequest request, Model model,
            @ModelAttribute Paginator page) {
                
        logger.debug("summaryReportText");
        

            SearchParams params = new SearchParams();
            
            String mrkKey = request.getParameter("mrkKey");
            
            if (mrkKey != null) {
                params.setFilter(new Filter(SearchConstants.MRK_KEY, mrkKey));
            }
            
            //params.setPaginator(page);
            params.setPageSize(5000);
            
            // perform query, and pull out the requested objects
            SearchResults searchResults
              = tFinder.getTissues(params);

            model.addAttribute("results", searchResults.getResultObjects());

            return "tissueSummaryReport";            


    }

    
    //--------------------------------//
    // Tissue Count Summary by Marker
    //--------------------------------//
    
    @RequestMapping(value="/marker/{markerID}")
    public ModelAndView tissueSummeryByMarker(@PathVariable("markerID") String markerID) {

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
    public @ResponseBody JsonSummaryResponse<MarkerTissueCountSummaryRow> tissueSummaryJson(
            HttpServletRequest request,
            @ModelAttribute Paginator page) {

        logger.debug("->Marker Tissue JsonSummaryResponse started");

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        
        String mrkKey = request.getParameter("mrkKey");
        
        if (mrkKey != null) {
            params.setFilter(new Filter(SearchConstants.MRK_KEY, mrkKey));
	    }
        
        params.setPaginator(page);
        
        // perform query, and pull out the requested objects
        SearchResults searchResults
          = tFinder.getTissues(params);
        List<MarkerTissueCount> markerList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<MarkerTissueCountSummaryRow> summaryRows = new ArrayList<MarkerTissueCountSummaryRow> ();
        Iterator<MarkerTissueCount> it = markerList.iterator();
        
        logger.debug("About to iterate through the tissue counts");
        
        while (it.hasNext()) {
            MarkerTissueCount mtc = it.next();
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

}
