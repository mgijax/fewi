package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Probe;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceID;
import mgi.frontend.datamodel.SequenceLocation;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.SeqSummaryRow;
import org.jax.mgi.fewi.util.StyleAlternator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /sequence/ uri's
 */
@Controller
@RequestMapping(value="/sequence")
public class SequenceController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(SequenceController.class);

    @Autowired
    private SequenceFinder sequenceFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private MarkerFinder markerFinder;

    //--------------------//
    // public methods
    //--------------------//

    /*
     * Sequence Detail by ID
     */
    @RequestMapping(value="/{seqID:.+}", method = RequestMethod.GET)
    public ModelAndView seqDetailByID(@PathVariable("seqID") String seqID) {

        logger.debug("->seqDetail started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter seqIdFilter = new Filter(SearchConstants.SEQ_ID, seqID);
        searchParams.setFilter(seqIdFilter);

        // find the requested sequence
        SearchResults searchResults
          = sequenceFinder.getSequenceByID(searchParams);
        List<Sequence> seqList = searchResults.getResultObjects();

        // handle error conditions
        if (seqList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return mav;
        }
        if (seqList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object;

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("sequence_detail");

        // package detail page style alternators
        mav.addObject("leftTdStyles",
          new StyleAlternator("detailCat1","detailCat2"));
        mav.addObject("rightTdStyles",
          new StyleAlternator("detailData1","detailData2"));

        //pull out the sequence, and add to mav
        Sequence sequence = seqList.get(0);
        mav.addObject("sequence", sequence);

        // package annotated markers
        Set<Marker> markers = sequence.getMarkers();
        if (!markers.isEmpty()) {
            mav.addObject("markers", markers);
        }

        // package probes
        Set<Probe> probes = sequence.getProbes();
        if (!probes.isEmpty()) {
            mav.addObject("probes", probes);
        }

        // package referenes
        List<Reference> references = sequence.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        // package chromosome value
        List<SequenceLocation> locList = sequence.getLocations();
        if (!locList.isEmpty()) {
            mav.addObject("chromosome", locList.get(0).getChromosome());
        }

        // package other IDs for this sequence
        Set<SequenceID> ids = sequence.getIds();
        if (!ids.isEmpty() & ids.size() > 1) {

            List<SequenceID> otherIDs = new ArrayList<SequenceID>();
            Iterator<SequenceID> it = ids.iterator();

            // first is the primary ID;  skip it - we only want secondary IDs
            it.next();

            // make list of secondary IDs
            while (it.hasNext()) {
              SequenceID secondaryID = it.next();
              otherIDs.add(secondaryID);
            }

            // package other IDs
            mav.addObject("otherIDs", otherIDs);
        }

        // package source notificaiton
        if (sequence.hasRawValues()) {
            mav.addObject("sourceNotice", "* Value from GenBank/EMBL/DDBJ "
              + "could not be resolved to an MGI controlled vocabulary.");
        }

        return mav;
    }


    /*
     * Sequence Summary by Reference
     */
    @RequestMapping(value="/reference/{refID}")
    public ModelAndView seqSummeryByRefId(@PathVariable("refID") String refID) {
        logger.debug("->seqSummeryByRef started");

        // setup search parameters object to gather the requested reference
        SearchParams searchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(refIdFilter);        
        
        // find the requested reference
        SearchResults<Reference> searchResults
          = referenceFinder.searchReferences(searchParams);

        return seqSummaryByRef(searchResults.getResultObjects(), refID);
    }

    @RequestMapping(value="/reference")
    public ModelAndView seqSummeryByRefKey(@RequestParam("_Refs_key") String dbKey,
    		HttpServletRequest request) {
        logger.debug("->referenceSummaryByMarkerKey started: " + dbKey);       
        
        // find the requested reference
        SearchResults<Reference> searchResults
        	= referenceFinder.getReferenceByKey(dbKey);

        return seqSummaryByRef(searchResults.getResultObjects(), dbKey);
    }
    
    private ModelAndView seqSummaryByRef(List<Reference> refList, String refKey){
        ModelAndView mav = new ModelAndView("sequence_summary_reference");

        // there can be only one...
        if (refList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refKey);
            return mav;
        }
        if (refList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe references found for " + refKey);
            return mav;
        }

        // pull out the reference, and place into the mav
        Reference reference = refList.get(0);

        mav.addObject("queryString", "refKey=" + reference.getReferenceKey());
        mav.addObject("reference", reference);

        return mav;
    }

    /*
     * Sequence Summary by Marker
     */
    @RequestMapping(value="/marker/{mrkID}")
    public ModelAndView seqSummeryByMarkerId(
		HttpServletRequest request,
        @PathVariable("mrkID") String mrkID)
    {

        logger.debug("->seqSummeryByMarker started");

        // setup search parameters object to gather the requested marker
        SearchParams searchParams = new SearchParams();
        Filter mrkIdFilter = new Filter(SearchConstants.MRK_ID, mrkID);
        searchParams.setFilter(mrkIdFilter);

        // find the requested marker
        SearchResults searchResults
          = markerFinder.getMarkerByID(searchParams);
        
        String provider = request.getParameter("provider");

        return seqSummeryByMarker(searchResults.getResultObjects(), mrkID, provider);
    }

    @RequestMapping(value="/marker")
    public ModelAndView seqSummeryByMarkerKey(@RequestParam("_Marker_key") String markerKey,
    		HttpServletRequest request) {
        logger.debug("->referenceSummaryByMarkerKey started: " + markerKey);

        // find the requested reference
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByKey(markerKey);
        
        String provider = request.getParameter("provider");

        return seqSummeryByMarker(searchResults.getResultObjects(), markerKey, provider);
    }
    
    private ModelAndView seqSummeryByMarker(List<Marker> markerList, String markerKey, String provider){
        ModelAndView mav = new ModelAndView("sequence_summary_marker");
        
        // there can be only one...
        if (markerList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No marker found for " + markerKey);
            return mav;
        }
        if (markerList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe marker ID found for " + markerKey);
            return mav;
        }

        // pull out the marker, and place into the mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

        // build JSON querystring
        String queryString = new String("mrkKey=" + marker.getMarkerKey());
        if ((provider != null) && (!"".equals(provider))) {
          queryString = queryString + "&provider=" + provider;
		}

        mav.addObject("queryString", queryString);

        return mav;	
    }

    /*
     * JSON summary results
     */
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<SeqSummaryRow> seqSummaryJson(
            HttpServletRequest request,
            @ModelAttribute Paginator page) {

        logger.debug("->JsonSummaryResponse started");

        // generate search parms object to pass to finders
        SearchParams params = new SearchParams();
        params.setSorts(this.genSorts(request));
        params.setPaginator(page);

        // parameter parsing
        List<Filter> filterList = new ArrayList<Filter>();
        String refKey = request.getParameter("refKey");
        String mrkKey = request.getParameter("mrkKey");
        String provider = request.getParameter("provider");

        if (refKey != null) {
            filterList.add(new Filter(SearchConstants.REF_KEY, refKey));
	    }
        if (mrkKey != null) {
            filterList.add(new Filter(SearchConstants.MRK_KEY, mrkKey));
	    }
        if (provider != null) {
            filterList.add(new Filter(SearchConstants.SEQ_PROVIDER, provider));
	    }

        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
		}
        params.setFilter(containerFilter);


        // perform query, and pull out the sequences requested
        SearchResults searchResults
          = sequenceFinder.getSequences(params);
        List<Sequence> seqList = searchResults.getResultObjects();

        // create/load the list of SeqSummaryRow wrapper objects
        List<SeqSummaryRow> summaryRows = new ArrayList<SeqSummaryRow> ();
        Iterator<Sequence> it = seqList.iterator();
        while (it.hasNext()) {
            Sequence sequence = it.next();
            if (sequence == null) {
                logger.debug("--> Null Sequence Object");
            }else {
                summaryRows.add(new SeqSummaryRow(sequence));
            }
        }

        // The JSON return object will be serialized to a JSON response for YUI table.
        JsonSummaryResponse<SeqSummaryRow> jsonResponse
          = new JsonSummaryResponse<SeqSummaryRow>();

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        return jsonResponse;
    }


    /*
     * Sequence Detail by key
     */
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView seqDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->seqDetailByKey started");

        // find the requested sequence
        SearchResults searchResults
          = sequenceFinder.getSequenceByKey(dbKey);
        List<Sequence> seqList = searchResults.getResultObjects();

        // handle error conditions
        if (seqList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return mav;
        }
        // success - we have a single object;

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("sequence_detail");

        // package detail page style alternators
        mav.addObject("leftTdStyles",
          new StyleAlternator("detailCat1","detailCat2"));
        mav.addObject("rightTdStyles",
          new StyleAlternator("detailData1","detailData2"));

        //pull out the sequence, and add to mav
        Sequence sequence = seqList.get(0);
        mav.addObject("sequence", sequence);

        // package annotated markers
        Set<Marker> markers = sequence.getMarkers();
        if (!markers.isEmpty()) {
            mav.addObject("markers", markers);
        }

        // package probes
        Set<Probe> probes = sequence.getProbes();
        if (!probes.isEmpty()) {
            mav.addObject("probes", probes);
        }

        // package referenes
        List<Reference> references = sequence.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        // package chromosome value
        List<SequenceLocation> locList = sequence.getLocations();
        if (!locList.isEmpty()) {
            mav.addObject("chromosome", locList.get(0).getChromosome());
        }

        // package other IDs for this sequence
        Set<SequenceID> ids = sequence.getIds();
        if (!ids.isEmpty() & ids.size() > 1) {

            List<SequenceID> otherIDs = new ArrayList<SequenceID>();
            Iterator<SequenceID> it = ids.iterator();

            // first is the primary ID;  skip it - we only want secondary IDs
            it.next();

            // make list of secondary IDs
            while (it.hasNext()) {
              SequenceID secondaryID = it.next();
              otherIDs.add(secondaryID);
            }

            // package other IDs
            mav.addObject("otherIDs", otherIDs);
        }

        // package source notificaiton
        if (sequence.hasRawValues()) {
            mav.addObject("sourceNotice", "* Value from GenBank/EMBL/DDBJ "
              + "could not be resolved to an MGI controlled vocabulary.");
        }

        return mav;
    }



    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    // generate the sorts
    private List<Sort> genSorts(HttpServletRequest request) {

        logger.debug("->genSorts started");

        Sort typeSort = new Sort(SortConstants.SEQUENCE_TYPE);
        Sort provSort = new Sort(SortConstants.SEQUENCE_PROVIDER);
        Sort lenSort  = new Sort(SortConstants.SEQUENCE_LENGTH, true);

        List<Sort> sorts = new ArrayList<Sort>();
        sorts.add(typeSort);
        sorts.add(provSort);
        sorts.add(lenSort);
        return sorts;
    }

}
