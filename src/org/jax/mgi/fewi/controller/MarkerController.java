package org.jax.mgi.fewi.controller;

import java.text.Annotation;
import java.util.*;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

// fewi
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.FooQueryForm;
import org.jax.mgi.fewi.forms.MarkerQueryForm;
import org.jax.mgi.fewi.summary.MarkerSummaryRow;
import org.jax.mgi.fewi.util.ProviderLinker;

// data model objects
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.SequenceSource;
import mgi.frontend.datamodel.MarkerCountSetItem;
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
import org.jax.mgi.fewi.util.ProviderLinker;

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
 * This controller maps all /marker/ uri's
 */
@Controller
@RequestMapping(value="/marker")
public class MarkerController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(MarkerController.class);

    @Autowired
    private MarkerFinder MarkerFinder;

    @Autowired
    private ReferenceFinder referenceFinder;


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // Marker Query Form
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQueryForm() {

        logger.debug("->getQueryForm started");

        ModelAndView mav = new ModelAndView("marker_query");
        mav.addObject("sort", new Paginator());
        mav.addObject(new MarkerQueryForm());
        return mav;
    }


    //-------------------------//
    // Marker Query Form Summary
    //-------------------------//
    @RequestMapping("/summary")
    public ModelAndView markerSummary(HttpServletRequest request,
            @ModelAttribute FooQueryForm queryForm) {

        logger.debug("->markerSummary started");
        logger.debug("queryString: " + request.getQueryString());

        ModelAndView mav = new ModelAndView("marker_summary");
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);

        return mav;
    }


    //--------------------//
    // Marker Detail By ID
    //--------------------//
    @RequestMapping(value="/{markerID:.+}", method = RequestMethod.GET)
    public ModelAndView markerDetailByID(@PathVariable("markerID") String markerID) {

        logger.debug("->markerDetailByID started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIdFilter);

        // find the requested marker
        SearchResults searchResults
          = MarkerFinder.getMarkerByID(searchParams);
        List<Marker> markerList = searchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        }
        if (markerList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("marker_detail");

        //pull out the marker, and add to mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

        // We need to pull out the GO terms we want to use as teasers for
        // each ontology.  (This is easier in Java than JSTL, so we do
        // it here.)
        
        List<mgi.frontend.datamodel.Annotation> pAnnot = this.noDuplicates(
        		marker.getGoProcessAnnotations());
        List<mgi.frontend.datamodel.Annotation> cAnnot = this.noDuplicates(
        		marker.getGoComponentAnnotations());
        List<mgi.frontend.datamodel.Annotation> fAnnot = this.noDuplicates(
        		marker.getGoFunctionAnnotations());
        	
        if (!pAnnot.isEmpty()) {
        	if (pAnnot.size() > 2) {
        		mav.addObject ("processAnnot3", pAnnot.get(2));
        	}
        	if (pAnnot.size() > 1) {
        		mav.addObject ("processAnnot2", pAnnot.get(1));
        	}
       		mav.addObject ("processAnnot1", pAnnot.get(0));
        }

        if (!fAnnot.isEmpty()) {
        	if (fAnnot.size() > 2) {
        		mav.addObject ("functionAnnot3", fAnnot.get(2));
        	}
        	if (fAnnot.size() > 1) {
        		mav.addObject ("functionAnnot2", fAnnot.get(1));
        	}
       		mav.addObject ("functionAnnot1", fAnnot.get(0));
        }
        
        if (!cAnnot.isEmpty()) {
        	if (cAnnot.size() > 2) {
        		mav.addObject ("componentAnnot3", cAnnot.get(2));
        	}
        	if (cAnnot.size() > 1) {
        		mav.addObject ("componentAnnot2", cAnnot.get(1));
        	}
       		mav.addObject ("componentAnnot1", cAnnot.get(0));
        }

        // need to pull out and re-package the expression counts for assays
        // and results
        
        Iterator<MarkerCountSetItem> assayIterator = 
        	marker.getGxdAssayCountsByType().iterator();
        Iterator<MarkerCountSetItem> resultIterator = 
        	marker.getGxdResultCountsByType().iterator();
        MarkerCountSetItem item;
        
        ArrayList<String> gxdAssayTypes = new ArrayList();
        HashMap<String,String> gxdAssayCounts = new HashMap<String,String>();
        HashMap<String,String> gxdResultCounts = new HashMap<String,String>();
        
        while (assayIterator.hasNext()) {
        	item = assayIterator.next();
        	gxdAssayTypes.add(item.getCountType());
        	gxdAssayCounts.put(item.getCountType(), 
        		Integer.toString(item.getCount()) );
        }
        while (resultIterator.hasNext()) {
        	item = resultIterator.next();
        	gxdResultCounts.put(item.getCountType(), 
        		Integer.toString(item.getCount()) );
        }
        
        mav.addObject ("gxdAssayTypes", gxdAssayTypes);
        mav.addObject ("gxdAssayCounts", gxdAssayCounts);
        mav.addObject ("gxdResultCounts", gxdResultCounts);

        // pull out the strain/species and provider links for each 
        // representative sequence
        mgi.frontend.datamodel.Sequence repGenomic = marker.getRepresentativeGenomicSequence();
        if (repGenomic != null) {
        	List<SequenceSource> genomicSources = repGenomic.getSources();
        	if ((genomicSources != null) && (genomicSources.size() > 0)) {
        		mav.addObject ("genomicSource", genomicSources.get(0).getStrain());
        	}
        	mav.addObject ("genomicLink", ProviderLinker.getSeqProviderLinks(repGenomic));
        }
        mgi.frontend.datamodel.Sequence repTranscript = marker.getRepresentativeTranscriptSequence();
        if (repTranscript != null) {
        	List<SequenceSource> transcriptSources = repTranscript.getSources();
        	if ((transcriptSources != null) && (transcriptSources.size() > 0)) {
        		mav.addObject ("transcriptSource", transcriptSources.get(0).getStrain());
        	}
        	mav.addObject ("transcriptLink", ProviderLinker.getSeqProviderLinks(repTranscript));
        }
        mgi.frontend.datamodel.Sequence repPolypeptide = marker.getRepresentativePolypeptideSequence();
        if (repPolypeptide != null) {
        	List<SequenceSource> polypeptideSources = repPolypeptide.getSources();
        	if ((polypeptideSources != null) && (polypeptideSources.size() > 0)) {
        		mav.addObject ("polypeptideSource", polypeptideSources.get(0).getStrain());
        	}
        	mav.addObject ("polypeptideLink", ProviderLinker.getSeqProviderLinks(repPolypeptide));
        }
        
        // determine if we need a KOMP linkout (complex rules, so better in
        // Java than JSTL); for a link we must have:
        // 1. marker symbol of only 1 letter or an uppercase letter or number
        // 2. marker type of Pseudogene or Gene
        // 3. status not withdrawn
        
        String symbol = marker.getSymbol();
        String markerType = marker.getMarkerType();
        if ((symbol.length() == 1) || symbol.matches("^[A-Z0-9].*")) {
        	if (markerType.equals("Pseudogene") || markerType.equals("Gene")) {
        		if (!marker.getStatus().equals("withdrawn")) {
        			mav.addObject ("needKompLink", "yes");
        		}
        	}
        }
        return mav;
    }


    //--------------------//
    // Marker Detail By Key
    //--------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView markerDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->markerDetailByKey started");

        // find the requested marker
        SearchResults searchResults
          = MarkerFinder.getMarkerByKey(dbKey);
        List<Marker> markerList = searchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        }// success

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("marker_detail");

        //pull out the marker, and add to mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

        // package referenes; gather via object traversal
        List<Reference> references = marker.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        return mav;
    }


    //-------------------------------//
    // Marker Summary by Reference
    //-------------------------------//
    @RequestMapping(value="/reference/{refID}")
    public ModelAndView markerSummeryByRef(@PathVariable("refID") String refID) {

        logger.debug("->markerSummeryByRef started");

        ModelAndView mav = new ModelAndView("marker_summary_reference");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults searchResults
          = referenceFinder.searchReferences(searchParams);
        List<Reference> refList = searchResults.getResultObjects();

        // there can be only one...
        if (refList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refID);
            return mav;
        }
        if (refList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe references found for " + refID);
            return mav;
        }

        // pull out the reference, and place into the mav
        Reference reference = refList.get(0);
        mav.addObject("reference", reference);

        // pre-generate query string
        mav.addObject("queryString", "refKey=" + reference.getReferenceKey());

        return mav;
    }


    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<MarkerSummaryRow> seqSummaryJson(
            HttpServletRequest request,
			@ModelAttribute FooQueryForm query,
            @ModelAttribute Paginator page) {

        logger.debug("->JsonSummaryResponse started");

        // parameter parsing
        String refKey = request.getParameter("refKey");
        
        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(query));
        
        if (refKey != null) {
            params.setFilter(new Filter(SearchConstants.REF_KEY, refKey));
        }        

        // perform query, and pull out the requested objects
        SearchResults searchResults
          = MarkerFinder.getMarkers(params);
        List<Marker> markerList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<MarkerSummaryRow> summaryRows = new ArrayList<MarkerSummaryRow> ();
        Iterator<Marker> it = markerList.iterator();
        while (it.hasNext()) {
            Marker marker = it.next();
            if (marker == null) {
                logger.debug("--> Null Object");
            }else {
                summaryRows.add(new MarkerSummaryRow(marker));
            }
        }

        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<MarkerSummaryRow> jsonResponse
          = new JsonSummaryResponse<MarkerSummaryRow>();

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
            filterList.add(new Filter (SearchConstants.MRK_ID, param1,
                Filter.OP_EQUAL));
        }

        //
        if ((param2 != null) && (!"".equals(param2))) {
            filterList.add(new Filter (SearchConstants.MRK_ID, param2,
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

    /** return a List comparable to 'annotations' but with the duplicate
     * terms stripped out.
     */
    private List<mgi.frontend.datamodel.Annotation> noDuplicates (
    		List<mgi.frontend.datamodel.Annotation> annotations) {
    	
    	// the list we will compose to be returned
    	ArrayList<mgi.frontend.datamodel.Annotation> a = new 
    		ArrayList<mgi.frontend.datamodel.Annotation>();
    	
    	// iterates over the input list
    	Iterator<mgi.frontend.datamodel.Annotation> it = annotations.iterator();
    	
    	// tracks which terms we've seen already
    	HashMap<String, String> done = new HashMap();
    	
    	// which term we are looking at currently
    	mgi.frontend.datamodel.Annotation annot;
    	
    	while (it.hasNext()) {
    		annot = it.next();
    		if (!done.containsKey(annot.getTerm())) {
    			done.put (annot.getTerm(), "");
    			a.add (annot);
    		}
    	}
    	return a;
    }
}
