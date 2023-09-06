package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.MarkerTissueCountFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerTissueCountSummaryRow;
import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final Logger logger
      = LoggerFactory.getLogger(MarkerTissueCountController.class);

    @Autowired
    private MarkerTissueCountFinder tFinder;

    @Autowired
    private MarkerFinder markerFinder;
    
    // a cache object
    private final Map<String,Integer> totalCountsCache = new HashMap<String,Integer>();
    
    
    /* 
     * This method maps ajax requests from the reference summary page.  It 
     * parses the ReferenceQueryForm, generates SearchParams object, and issues
     * the query to the ReferenceFinder.  The results are returned as JSON
     */
    @RequestMapping("/marker/report*")
    public String tissueSummaryReport(
            HttpServletRequest request, Model model) {
        logger.debug("summaryReportText");
        

            SearchParams params = new SearchParams();
            
            String mrkKey = request.getParameter("mrkKey");
            params.setFilter(new Filter(SearchConstants.MRK_KEY, mrkKey));
            
            Paginator page = new Paginator();
            page.setResults(5000);
            
            // perform query, and pull out the requested objects
            List<MarkerTissueCount> tissueCounts = tFinder.getTissues(mrkKey,page);

            model.addAttribute("results", tissueCounts);

            return "tissueSummaryReport";            


    }

    
    //--------------------------------//
    // Tissue Count Summary by Marker
    //--------------------------------//
    
    @RequestMapping(value="/marker/{markerID}")
    public ModelAndView tissueSummeryByMarkerId(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}
                
        logger.debug("->tissueSummeryByMarkerId started");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        Filter markerKeyFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerKeyFilter);

        // find the requested reference
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByID(searchParams);
        List<Marker> markerList = searchResults.getResultObjects();

        return tissueSummeryByMarker(markerList, markerID);
    }
    
    @RequestMapping(value="/marker")
    public ModelAndView tissueSummeryByMarkerKey(HttpServletRequest request, @RequestParam("key") String markerKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}
                
        logger.debug("->tissueSummeryByMarkerKey started: " + markerKey);

        // find the requested reference
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByKey(markerKey);
        List<Marker> markerList = searchResults.getResultObjects();

        return tissueSummeryByMarker(markerList, markerKey);
    }
    
    private ModelAndView tissueSummeryByMarker(List<Marker> markerList, String mrk){
    	
    	ModelAndView mav = new ModelAndView("marker_tissue_summary");

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
        
        
        params.setFilter(new Filter(SearchConstants.MRK_KEY, mrkKey));
        
        params.setPaginator(page);
        
        // perform query, and pull out the requested objects
        List<MarkerTissueCount> tissueCounts = tFinder.getTissues(mrkKey,page);

        // create/load the list of SummaryRow wrapper objects
        List<MarkerTissueCountSummaryRow> summaryRows = new ArrayList<MarkerTissueCountSummaryRow> ();
        logger.debug("About to iterate through the tissue counts");
        
        for(MarkerTissueCount mtc : tissueCounts)
        {
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
        jsonResponse.setTotalCount(getTotalTissueCount(mrkKey));
        return jsonResponse;
    }
    
    public Integer getTotalTissueCount(String mrkKey)
    {
    	if(totalCountsCache.containsKey(mrkKey))
    	{
    		return totalCountsCache.get(mrkKey);
    	}
    	
    	Integer tc = tFinder.getTissueTotalCount(mrkKey);
    	totalCountsCache.put(mrkKey,tc);
    	return tc;
    }

}
