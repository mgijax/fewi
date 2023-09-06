package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.GxdLitAssayTypeAgePair;
import org.jax.mgi.fe.datamodel.GxdLitIndexRecord;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fewi.finder.GxdLitFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.GxdLitQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.GxdLitAgeAssayTypePairTable;
import org.jax.mgi.fewi.summary.GxdLitAgeAssayTypePairTableCount;
import org.jax.mgi.fewi.summary.GxdLitAssayTypeSummaryRow;
import org.jax.mgi.fewi.summary.GxdLitGeneSummaryRow;
import org.jax.mgi.fewi.summary.GxdLitReferenceSummaryRow;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.Highlighter;
import org.jax.mgi.fewi.util.StyleAlternator;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.fe.IndexConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


/**
 * GXDLitController
 * This controller handles two tables on the GXDLit summary page, one for genes/references/assay type + age pairs
 * And another for Assay Type, Age Pairings alone.
 */

/*
 * This controller maps all /gxd/ uri's
 */
@Controller
@RequestMapping(value="/gxdlit")
public class GXDLitController {


    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger
      = LoggerFactory.getLogger(GXDLitController.class);

    @Autowired
    private MarkerFinder markerFinder;

    @Autowired
    private GxdLitFinder gxdLitFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

    @Value("${gxdLit.limit}")
    private Integer gxdLimit;


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // GXD Lit Query Form
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->getQueryForm started");

        ModelAndView mav = new ModelAndView("gxdlit_query");
        mav.addObject("sort", new Paginator());
        mav.addObject(new GxdLitQueryForm());
        return mav;
    }

    //--------------------//
    // GXD Lit Detail By Key
    //--------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView gxdLitDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->gxdLitDetailByKey started");
        
        if (!FewiUtil.isPositiveInteger(dbKey)) {
        	return errorMav("Cannot find GXD Lit Entry");
        }

        // find the requested Lit Detail
        SearchResults<GxdLitIndexRecord> searchResults = gxdLitFinder.getGxdLitByKey(dbKey);
        if ((searchResults == null) || (searchResults.getTotalCount() == 0)) {
        	return errorMav("Cannot find GXD Lit Entry");
        }
        return gxdLitDetail(searchResults.getResultObjects(), dbKey);
    }
    
    @RequestMapping(value="/key")
    public ModelAndView gxdLitDetailByKeyParam(HttpServletRequest request, @RequestParam("_Index_key") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        if (!FewiUtil.isPositiveInteger(dbKey)) {
        	return errorMav("Cannot find GXD Lit Entry");
        }

        logger.debug("->referenceSummaryByAlleleKey started: " + dbKey);
       
        // find the requested Lit Detail
        SearchResults<GxdLitIndexRecord> searchResults = gxdLitFinder.getGxdLitByKey(dbKey);
        if ((searchResults == null) || (searchResults.getTotalCount() == 0)) {
        	return errorMav("Cannot find GXD Lit Entry");
        }
        return gxdLitDetail(searchResults.getResultObjects(), dbKey);
    }
    
    private ModelAndView gxdLitDetail(List<GxdLitIndexRecord> indexRecordList, String allele){
        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("gxdlit_detail");
    	
        // there can be only one...
        if (indexRecordList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No GxdLitIndexRecord Found");
            return mav;
        }// success

        GxdLitQueryForm queryForm = new GxdLitQueryForm();

        try {
        	GxdLitGeneSummaryRow record = new GxdLitGeneSummaryRow(indexRecordList.get(0), queryForm, null);
        	List <GxdLitGeneSummaryRow> detailList = new ArrayList<GxdLitGeneSummaryRow> ();
        	detailList.add(record);

        	mav.addObject("record", record.getReferenceRecords().get(0));
        	mav.addObject("pairTable", parseAgeAssay(detailList, Boolean.TRUE, queryForm));
        	mav.addObject("reference", indexRecordList.get(0).getReference());
        	mav.addObject("marker", indexRecordList.get(0).getMarker());
        } catch (Exception e) {
        	return errorMav("Cannot find GXD Lit Entry");
        }
        return mav;  	
    }



    //---------------------------------------------//
    // GXD Lit Query Form Summary by marker
    //---------------------------------------------//
    @RequestMapping("/marker/{markerID}")
    public ModelAndView gxdLitSummaryByMarkerId(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm,
            @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->gxdLitSummary by Marker started");
        logger.debug("getting marker for id: " + markerID);

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIdFilter);

        // find the requested marker
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByID(searchParams);

        return gxdLitSummaryByMarker(request, searchResults.getResultObjects(), queryForm);
    }
    
    @RequestMapping(value="/summary",  params={"_Marker_key"})
    public ModelAndView gxdLitSummaryByMarkerKey(@RequestParam("_Marker_key") String markerKey,
    		HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm){
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        // find the requested reference
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByKey(markerKey);

        return gxdLitSummaryByMarker(request, searchResults.getResultObjects(), queryForm);
    }
    
    private ModelAndView gxdLitSummaryByMarker(HttpServletRequest request,
    		List<Marker> markerList, 
    		GxdLitQueryForm queryForm){
        ModelAndView mav = new ModelAndView("gxdlit_summary_by_marker");
        
        // there can be only one...
        if (markerList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        }
        if (markerList.size() > 1) { // dupe found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        
        SearchParams params = new SearchParams();

        Marker marker = markerList.get(0);

        queryForm.setMarker_key(marker.getMarkerKey());

        Filter markerKeyFilter = new Filter(SearchConstants.MRK_KEY, ""+marker.getMarkerKey());

        logger.debug(markerKeyFilter.toString());

        params.setFilter(markerKeyFilter);
        params.setSorts(genSorts(request));
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder 2.");

        SearchResults<GxdLitIndexRecord> results = gxdLitFinder.getGxdLitRecords(params);
        Integer totalCount = results.getTotalCount();
        
        
        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list");

        // This is admittedly a bad solution to getting data by side effect. However, the design of this class was poor to begin with, and we have limited time to refactor.
        Map<String,Object> analysisData = new HashMap<String,Object>();
        analysisData.put("refCount", 0);
        analysisData.put("hasFullyCoded", false);
        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, null,analysisData);
                
        mav.addObject("marker", marker);
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3Gxd"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", analysisData.get("refCount"));
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("age", queryForm.getAge().get(0));
        mav.addObject("assayType", queryForm.getAssayType().get(0));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", analysisData.get("hasFullyCoded"));

        return mav;
    	
    }

    //---------------------------------------------//
    // GXD Lit Query Form Summary by reference
    //---------------------------------------------//
    @RequestMapping("/reference/{refID}")
    public ModelAndView gxdLitSummaryByReferenceId(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm,
            @PathVariable("refID") String refID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->gxdLitSummary by Reference started");
        logger.debug("getting reference for id: " + refID);

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter referenceIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(referenceIdFilter);

        // find the requested reference
        SearchResults<Reference> searchResults
          = referenceFinder.searchReferences(searchParams);

        return gxdLitSummaryByReference(request, searchResults.getResultObjects(), queryForm);
    }
    
    @RequestMapping(value="/summary",  params={"_Refs_key"})
    public ModelAndView gxdLitSummaryByReferenceKey(@RequestParam("_Refs_key") String referenceKey,
    		HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm){
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        // find the requested reference
        SearchResults<Reference> searchResults
        = referenceFinder.getReferenceByKey(referenceKey);

        return gxdLitSummaryByReference(request, searchResults.getResultObjects(), queryForm);
    }
    
    private ModelAndView gxdLitSummaryByReference(HttpServletRequest request,
    		List<Reference> referenceList, 
    		GxdLitQueryForm queryForm){
    	ModelAndView mav = new ModelAndView("gxdlit_summary_by_reference");
    	
        // there can be only one...
        if (referenceList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Foo Found");
            return mav;
        }
        if (referenceList.size() > 1) { // dupe found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }

        SearchParams params = new SearchParams();

        Reference reference = referenceList.get(0);

        Filter referenceKeyFilter = new Filter(SearchConstants.REF_KEY, ""+reference.getReferenceKey());

        logger.debug(referenceKeyFilter.toString());

        params.setFilter(referenceKeyFilter);
        params.setSorts(genSorts(request));
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder 2.");

        SearchResults<GxdLitIndexRecord> results = gxdLitFinder.getGxdLitRecords(params);
        Integer totalCount = results.getTotalCount();

        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list");

        queryForm.setReference_key(reference.getReferenceKey());

        // This is admittedly a bad solution to getting data by side effect. However, the design of this class was poor to begin with, and we have limited time to refactor.
        Map<String,Object> analysisData = new HashMap<String,Object>();
        analysisData.put("refCount", 0);
        analysisData.put("hasFullyCoded", false);
        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, null,analysisData);

        mav.addObject("reference", reference);
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3Gxd"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", analysisData.get("refCount"));
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("age", queryForm.getAge().get(0));
        mav.addObject("assayType", queryForm.getAssayType().get(0));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", analysisData.get("hasFullyCoded"));

        return mav;    	
    }


    //---------------------------------------------//
    // GXD Lit Query Form Summary by age and assay
    //---------------------------------------------//
    @RequestMapping("/summary/ageAssay")
    public ModelAndView gxdLitSummaryByAgeAndAssayType(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->gxdLitSummary By Age and Assay started");
        logger.debug("queryString: " + request.getQueryString());

        SearchParams params = new SearchParams();

        params.setSorts(genSorts(request));
        params.setFilter(genFilters(queryForm));
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder.");

        SearchResults<GxdLitIndexRecord> results = gxdLitFinder.getGxdLitRecords(params);

        return gxdLitSummaryByAgeAndAssay(request, results.getResultObjects(), queryForm);
    }

    
    private ModelAndView gxdLitSummaryByAgeAndAssay(HttpServletRequest request,
    		List<GxdLitIndexRecord> recordList, 
    		GxdLitQueryForm queryForm){
        ModelAndView mav = new ModelAndView("gxdlit_summary_by_age_assay");
         
        // create/load the list of SummaryRow wrapper objects for the gene section

		Integer totalCount = recordList.size();
        // This is admittedly a bad solution to getting data by side effect. However, the design of this class was poor to begin with, and we have limited time to refactor.
        Map<String,Object> analysisData = new HashMap<String,Object>();
        analysisData.put("refCount", 0);
        analysisData.put("hasFullyCoded", false);
        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, null,analysisData);
       
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3Gxd"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", analysisData.get("refCount"));
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("age", queryForm.getAge().get(0));
        mav.addObject("assayType", queryForm.getAssayType().get(0));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", analysisData.get("hasFullyCoded"));
        return mav;
    }


	//-------------------------//
    // GXD Lit Query Form Summary
    //-------------------------//
    @RequestMapping("/summary")
    public ModelAndView gxdLitSummary(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->gxdLitSummary started");
        logger.debug("queryString: " + request.getQueryString());

        SearchParams params = new SearchParams();

        params.setSorts(genSorts(request));
        params.setFilter(genFilters(queryForm));
//		params.setIncludeSetMeta(true);
//		params.setIncludeMetaHighlight(true);
//		params.setIncludeRowMeta(true);
//		params.setIncludeMetaScore(true);
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder.");

        SearchResults<GxdLitIndexRecord> results = gxdLitFinder.getGxdLitRecords(params);

		int totalCount = results.getTotalCount();
        //Map<String, Set<String>> highlighting = results.getResultSetMeta().getSetHighlights();

        Highlighter textHl = null;

//        logger.debug("Checking highlighting.");

//        if (highlighting.containsKey(SearchConstants.GXD_LIT_LONG_CITATION)){
//        	textHl = new Highlighter(highlighting.get(SearchConstants.GXD_LIT_LONG_CITATION));
//        }

        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list: " + recordList.size() + " records");

        // This is admittedly a bad solution to getting data by side effect. However, the design of this class was poor to begin with, and we have limited time to refactor.
        Map<String,Object> analysisData = new HashMap<String,Object>();
        analysisData.put("refCount", 0);
        analysisData.put("hasFullyCoded", false);
        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, textHl,analysisData);

        logger.debug("ref count after = "+analysisData.get("refCount"));

        ModelAndView mav = new ModelAndView("gxdlit_summary");
        //if a markerID is in the query resolve it to a marker object
        String markerID = queryForm.getMarkerId();
        if(markerID!=null && !markerID.equals(""))
        {
        	SearchResults<Marker> sr = markerFinder.getMarkerByID(markerID);
        	if (sr.getTotalCount()>0)
        	{
        		mav.addObject("marker",sr.getResultObjects().get(0));
        	}
        }
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3Gxd"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", analysisData.get("refCount"));
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", analysisData.get("hasFullyCoded"));

        return mav;
    }
    
    @RequestMapping("/forward/summary")
    public ModelAndView gxdLitSummary(HttpServletRequest request) {
    	return gxdLitSummary(request,(GxdLitQueryForm)request.getAttribute("gxdLitQueryForm"));
    }

    //-------------------------//
    // Total Count of GXD Lit Records
    //-------------------------//
    public Integer getGxdLitCount(GxdLitQueryForm queryForm) {

        logger.debug("->gxdLitCount started");

        SearchParams params = new SearchParams();

        params.setFilter(genFilters(queryForm));
        params.setPageSize(0);

        logger.debug("Hitting the finder.");

        SearchResults<GxdLitIndexRecord> results = gxdLitFinder.getGxdLitRecords(params);
        int totalCount = results.getTotalCount();
        
        logger.debug("found "+totalCount+" gxd lit records");

        return totalCount;
    }

	// convenience method -- construct a ModelAndView for the error page and
	// include the given 'msg' as the error String to be reported
	private ModelAndView errorMav (String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//
    
    /*
     * additional return values are set in the analysisData parameter vie side effect.
     */
    private List<GxdLitGeneSummaryRow> generateGeneSection(List<GxdLitIndexRecord> recordList, GxdLitQueryForm queryForm, Highlighter textHl,Map<String,Object> analysisData) {

    	logger.debug("Generating the Gene Section");

    	List<GxdLitGeneSummaryRow> summaryRows = new ArrayList<GxdLitGeneSummaryRow> ();

    	String symbol = "";
	    Boolean first = Boolean.TRUE;
	    GxdLitGeneSummaryRow row = null;

	    Set<String> uniqueReferences = new HashSet<String>();
	    
	    for (GxdLitIndexRecord record: recordList) {
	    	// count nmber of unique references
			uniqueReferences.add(record.getJnumId());
			// flag if any records have fully coded data
			if(!(Boolean)analysisData.get("hasFullyCoded") && record.getFullCodedResultCount()>0) analysisData.put("hasFullyCoded", true);
			record.getFullCodedResultCount();
    		if (first || !symbol.equals(record.getMarkerSymbol())) {

    			//logger.debug("Either this is the first, or its a new symbol.");

    			if (!first) {

    				// Has a result count
    				Boolean hasACount = Boolean.FALSE;
    				for (GxdLitReferenceSummaryRow refRow: row.getReferenceRecords()) {
    					if (Integer.parseInt(refRow.getCount()) > 0) {
    						hasACount = Boolean.TRUE;
    					}
    				}

    				if (hasACount) {
        				// Add in the last record
        				summaryRows.add(row);
    				}
    			}

    			row = new GxdLitGeneSummaryRow(record, queryForm, textHl);
    			symbol = record.getMarkerSymbol();
    			first = Boolean.FALSE;
    		}
    		else {
    			row.addRecord(record);
    		}

	    }
	    

	    // Add in the last record found before the loop kicked out. Assuming it has a count associate
	    // with it.

	    if (row != null) {
			Boolean hasACount = Boolean.FALSE;
			for (GxdLitReferenceSummaryRow refRow: row.getReferenceRecords()) {
				if (Integer.parseInt(refRow.getCount()) > 0) {
					hasACount = Boolean.TRUE;
				}
			}

			if (hasACount) {
				// Add in the last record
				summaryRows.add(row);
			}
	    }

	    // set the return value of the referenceCount
	    analysisData.put("refCount",uniqueReferences.size());
	    logger.debug (" --> built " + summaryRows.size() + " summaryRows");
	    return summaryRows;
	}

	private GxdLitAgeAssayTypePairTable parseAgeAssay (List<GxdLitGeneSummaryRow> rows, Boolean includeAllTypes, GxdLitQueryForm queryForm) {

		Map <String, Boolean> hasAgeMap = new HashMap<String, Boolean> ();
		Map <String, Boolean> hasAssayTypeMap = new HashMap<String, Boolean> ();

		Map<String, Map<String, GxdLitAgeAssayTypePairTableCount>> countMap = new HashMap<String, Map<String, GxdLitAgeAssayTypePairTableCount>> ();

		List <String> ages = new ArrayList<String>();
		ages.add("0.5");
		ages.add("1");
		ages.add("1.5");
		ages.add("2");
		ages.add("2.5");
		ages.add("3");
		ages.add("3.5");
		ages.add("4");
		ages.add("4.5");
		ages.add("5");
		ages.add("5.5");
		ages.add("6");
		ages.add("6.5");
		ages.add("7");
		ages.add("7.5");
		ages.add("8");
		ages.add("8.5");
		ages.add("9");
		ages.add("9.5");
		ages.add("10");
		ages.add("10.5");
		ages.add("11");
		ages.add("11.5");
		ages.add("12");
		ages.add("12.5");
		ages.add("13");
		ages.add("13.5");
		ages.add("14");
		ages.add("14.5");
		ages.add("15");
		ages.add("15.5");
		ages.add("16");
		ages.add("16.5");
		ages.add("17");
		ages.add("17.5");
		ages.add("18");
		ages.add("18.5");
		ages.add("19");
		ages.add("19.5");
		ages.add("20");
		ages.add("E");
		ages.add("P");

		for (String age: ages) {
			hasAgeMap.put(age, Boolean.FALSE);
		}

		List<String> assayTypes = new ArrayList<String> ();
		assayTypes.add("Immunohistochemistry (section)");
		assayTypes.add("In situ RNA (section)");
		assayTypes.add("Immunohistochemistry (whole mount)");
		assayTypes.add("In situ RNA (whole mount)");
		assayTypes.add("In situ reporter (knock in)");
		assayTypes.add("Northern blot");
		assayTypes.add("Western blot");
		assayTypes.add("RT-PCR");
		assayTypes.add("cDNA clones");
		assayTypes.add("RNase protection");
		assayTypes.add("Nuclease S1");
		assayTypes.add("Primer Extension");

		// Initialize the maps default inclusion values, if we have requested all assay types
		// we default to all true, if not we default to all false.

		for (String type: assayTypes) {
			if (includeAllTypes) {
				hasAssayTypeMap.put(type, Boolean.TRUE);
			}
			else {
				hasAssayTypeMap.put(type, Boolean.FALSE);
			}
		}
		String curAge, curType;
		for (GxdLitGeneSummaryRow row: rows) {
			for (GxdLitReferenceSummaryRow refRows: row.getReferenceRecords()) {
				for (GxdLitAssayTypeAgePair pair: refRows.getValidPairs()) {
					curAge = pair.getAge();
					curType = pair.getAssayType().trim();
					hasAgeMap.put(curAge, Boolean.TRUE);
					hasAssayTypeMap.put(curType, Boolean.TRUE);
					if (! countMap.containsKey(curType)) {
						Map<String, GxdLitAgeAssayTypePairTableCount> newAgeMap = new HashMap<String, GxdLitAgeAssayTypePairTableCount> ();
						countMap.put(curType, newAgeMap);
					}

					if (! countMap.get(curType).containsKey(curAge)) {
						countMap.get(curType).put(curAge, new GxdLitAgeAssayTypePairTableCount(pair, queryForm));
					}
					else {
						GxdLitAgeAssayTypePairTableCount temp = countMap.get(curType).get(curAge);
						temp.addCount();
						countMap.get(curType).put(curAge, temp);
					}
				}
			}
		}

	    // Setup the table itself

	    List <String> allAges = new ArrayList <String>();

	    for (String key: ages) {
	    	if (hasAgeMap.get(key)) {
	    		allAges.add(key);
	    	}
	    }

	    List<GxdLitAssayTypeSummaryRow> allTypes = new ArrayList <GxdLitAssayTypeSummaryRow> ();

	    GxdLitAssayTypeSummaryRow row;
	    for (String key: assayTypes) {
	    	if (hasAssayTypeMap.get(key)) {
	    		row = new GxdLitAssayTypeSummaryRow(key);
	    		for (String age: ages) {
	    			if (hasAgeMap.get(age)) {
	    				if (countMap.containsKey(key) && countMap.get(key).containsKey(age)) {
	    					row.addCount(countMap.get(key).get(age));
	    				}
	    				else {
	    					row.addCount(null);
	    				}
	    			}
	    		}
	    		allTypes.add(row);
	    	}
	    }

	    return new GxdLitAgeAssayTypePairTable(allAges, allTypes);
	}


	// generate the sorts
    private List<Sort> genSorts(HttpServletRequest request) {

        logger.debug("->genSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        Sort sort = new Sort(SortConstants.MRK_BY_SYMBOL, Boolean.FALSE);
        sorts.add(sort);

        Sort sort2 = new Sort(SortConstants.REF_BY_AUTHOR, Boolean.FALSE);
        sorts.add(sort2);

        logger.debug ("sort: " + sort.toString());
        return sorts;
    }

    // generate the filters
    private Filter genFilters(GxdLitQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);


        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String nomenclature = query.getNomen();
        //Nomen filter
        if(nomenclature!=null && !nomenclature.equals("")) {
			Filter nomenFilter = FilterUtil.generateNomenFilter(SearchConstants.GXD_LIT_MRK_NOMEN, nomenclature);
			if(nomenFilter != null) filterList.add(nomenFilter);
		}
        // vocab term => marker IDs
        String termId = query.getTermId();
        if(termId!=null && !termId.equals(""))
        {
        	filterList.add(new Filter(IndexConstants.MRK_TERM_ID,termId,Filter.Operator.OP_EQUAL));
        	// COMMENTING OUT THIS STRATEGY, BECAUSE IT IS SLOW AND TIES UP SOLR's RESOURCES
//        	// In order to do get the marker IDs associated with a termID search, we will have to 
//			// query the marker index and iterate the markers
//			SearchParams params = new SearchParams();
//			params.setFilter(new Filter(IndexConstants.MRK_TERM_ID_FOR_GXD,termId,Filter.OP_EQUAL));
//			MarkerBatchIDFinder markerBatchIDFinder = new MarkerBatchIDFinder(markerFinder,params);
//			markerBatchIDFinder.batchSize = 5000;
//			
//			// gather marker id strings in batches
//			List<String> markerIds = new ArrayList<String>();
//			while(markerBatchIDFinder.hasNextResults())
//			{
//				SearchResults<String> searchResults = markerBatchIDFinder.getNextResults();
//				//add each marker ID to the ids list
//		        for (String mgiid: searchResults.getResultKeys()) {
//		        	if(mgiid != null && !mgiid.equals("")) markerIds.add(mgiid);
//				}
//			}
//			logger.debug("termID = "+termId+" resolves to the following marker IDs = "+StringUtils.join(markerIds,","));
//			if(markerIds.size()>0)
//			{
//	        	List<Filter> markerIdFilters = new ArrayList<Filter>();
//	        	for(String markerId : markerIds)
//	        	{
//	        		if(markerId!=null && !markerId.equals(""))
//	        		{
//	        			markerIdFilters.add(new Filter(SearchConstants.MRK_ID,markerId,Filter.OP_EQUAL));
//	        		}
//	        	}
//	        	if(markerIdFilters.size()>0)
//	        	{
//	        		// We OR a list of marker IDs
//	        		Filter markerIdFilter = new Filter();
//	        		markerIdFilter.setFilterJoinClause(Filter.FC_OR);
//	        		markerIdFilter.setNestedFilters(markerIdFilters);
//	        		filterList.add(markerIdFilter);
//	        	}
//			}
        }
        // marker MGI Id
        String markerId = query.getMarkerId();
        if(markerId!=null && !markerId.equals(""))
        {
        	filterList.add(new Filter(SearchConstants.MRK_ID,markerId,Filter.Operator.OP_EQUAL));
        }
//       WE REMOVED THIS SECTION TO BRING IN LINE WITH 5.x GXD FORM QUERYING
//		 THIS CODE REMAINS AS A REFERENCE IN CASE WE MISSED SOMETHING
//       // Nomen Filter
//        if ((nomen != null) && (!"".equals(nomen.trim()))) {
//	    String field;
//
//	    // remember if the query string was quoted
//	    boolean wasQuoted = false;
//	    if (nomen.contains("\"")) { wasQuoted = true; }
//
//	    // then strip the quotes
//	    nomen = nomen.replaceAll("\"", "");
//
//	    // convert any substrings of non-alphanumeric characters with a
//	    // space
//	    nomen = nomen.replaceAll("[^A-Za-z0-9]+", " "); 
//
//	    // then remove any leading and trailing spaces
//	    nomen = nomen.trim();
//
//	    if (nomen.length() < 1) {
//		// if we have no search string left after cleanup, do not add
//		// a filter for nomenclature
//
//	    } else if (nomen.length() <= 2) {
//		// for 1-2 characters, do an exact match against only symbols
//		// and synonyms.  if it was quoted, just ignore the quotes.
//
//		field = SearchConstants.GXD_LIT_MRK_SYMBOL;
//        	filterList.add(new Filter (field, nomen.trim(),
//			Filter.OP_EQUAL));
//
//	    } else if (wasQuoted) {
//		// for 3+ characters with quotes, do a contains search for the
//		// quoted string
//
//		field = SearchConstants.GXD_LIT_MRK_NOMEN;
//        	filterList.add(new Filter (field, "\"" + nomen + "\"", 
//			Filter.OP_CONTAINS));
//
//	    } else {
//		// for 3+ characters with no quotes, do a begins search
//		// where we must match ALL tokens
//
//		field = SearchConstants.GXD_LIT_MRK_NOMEN_BEGINS;
//
//		// separate tokens on one or more consecutive spaces
//		String[] tokens = nomen.split(" +");
//
//		// collect a filter for each token
//
//		List<Filter> nomenFilters = new ArrayList<Filter>();
//
//		for (String token: tokens) {
//			nomenFilters.add (new Filter (field, token,
//				Filter.OP_EQUAL) );
//		} 
//
//		// If we only found one filter, just add it.
//		// Otherwise, AND them together under a grouping filter and
//		// add that one.
//
//		if (nomenFilters.size() == 1) {
//    			filterList.add(nomenFilters.get(0));
//		} else {
//    			Filter tf = new Filter();
//    			tf.setFilterJoinClause(Filter.FC_AND);
//			tf.setNestedFilters(nomenFilters);
//			filterList.add(tf);
//    		}
//	    }
//        }

        // Age AND Assay Type Filters

        List <String> ageList = query.getAge();
        ageList.remove(null); ageList.remove("");
        boolean doAgeSearch = ageList != null && ageList.size() > 0 && !(ageList.contains("ANY"));

        List<String> assayTypeList = query.getAssayType();
        assayTypeList.remove(null); assayTypeList.remove("");
        boolean doAssayTypeSearch = assayTypeList != null && assayTypeList.size() > 0 && !(assayTypeList.contains("ANY"));
        
        // If we are both both and age and an assay type search, use the combined field for greater precision
        if(doAgeSearch && doAssayTypeSearch)
        {
        	List<Filter> ageAssayTypeFilters = new ArrayList<Filter>();
        	for(String age : ageList)
        	{
        		for(String assayType : assayTypeList)
        		{
        			ageAssayTypeFilters.add(new Filter(IndexConstants.GXD_LIT_AGE_ASSAY_TYPE_PAIR,
        					age+"-"+assayType,
        					Filter.Operator.OP_EQUAL));
        		}
        	}
        	Filter tf = new Filter();
			tf.setFilterJoinClause(Filter.JoinClause.FC_OR);
			tf.setNestedFilters(ageAssayTypeFilters);
			filterList.add(tf);
        }
        else
        {
	        if (doAgeSearch) {
		        List <Filter> ageFilters = new ArrayList<Filter>();
	
		        for (String age: ageList) {
		        	ageFilters.add(new Filter (SearchConstants.GXD_LIT_AGE , age,
			                Filter.Operator.OP_EQUAL));
		        }
				Filter tf = new Filter();
				tf.setFilterJoinClause(Filter.JoinClause.FC_OR);
				tf.setNestedFilters(ageFilters);
				filterList.add(tf);
	        }
	        else if (doAssayTypeSearch) {
	    	        List <Filter> assayTypeFilters = new ArrayList<Filter>();
	
	    	        for (String assayType: assayTypeList) {
    		        	assayTypeFilters.add(new Filter (SearchConstants.GXD_LIT_ASSAY_TYPE , assayType,
    			                Filter.Operator.OP_EQUAL));
	    	        }
					Filter tf = new Filter();
					tf.setFilterJoinClause(Filter.JoinClause.FC_OR);
					tf.setNestedFilters(assayTypeFilters);
					filterList.add(tf);
	    	}
        }


		//build author query filter
		if(query.getAuthor() != null && !"".equals(query.getAuthor())){

			List<String> authors = parseList(query.getAuthor().trim(), ";");

			String scope = query.getAuthorScope();

			if ("first".equals(scope)){
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_FIRST,
						authors, Filter.Operator.OP_IN));
			} else if ("last".equals(scope)){
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_LAST,
						authors, Filter.Operator.OP_IN));
			} else {
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_ANY,
						authors, Filter.Operator.OP_IN));
			}
		}

		// build journal query filter
		if(query.getJournal() != null && !"".equals(query.getJournal())){

			List<String> journals = parseList(query.getJournal().trim(), ";");

			filterList.add(new Filter(SearchConstants.REF_JOURNAL,
					journals, Filter.Operator.OP_IN));
		}


		// build year query filter
		String year = query.getYear().trim();
		if(year != null && !"".equals(year)){
			int rangeLoc = year.indexOf("-");
			if(rangeLoc > -1){

				List<String> years = parseList(year, "-");

				if (years.size() == 2){
					logger.debug("year range: " + years.get(0) + "-" + years.get(1));
					Integer one = Integer.parseInt(years.get(0));
					Integer two = Integer.parseInt(years.get(1));

					if (one > two){
						years.set(0, two.toString());
						years.set(1, one.toString());
					}
					filterList.add(new Filter(SearchConstants.REF_YEAR,
							years.get(0), Filter.Operator.OP_GREATER_OR_EQUAL));
					filterList.add(new Filter(SearchConstants.REF_YEAR,
							years.get(1), Filter.Operator.OP_LESS_OR_EQUAL));
				} else {
					if (rangeLoc == 0){
						logger.debug("year <= " + years.get(0));
						filterList.add(new Filter(SearchConstants.REF_YEAR,
								years.get(0), Filter.Operator.OP_LESS_OR_EQUAL));
					} else {
						logger.debug("year >= " + years.get(0));
						filterList.add(new Filter(SearchConstants.REF_YEAR,
								years.get(0), Filter.Operator.OP_GREATER_OR_EQUAL));
					}
				}
			} else {
				filterList.add(new Filter(SearchConstants.REF_YEAR,
						year, Filter.Operator.OP_EQUAL));
			}
		}

		// build text query filter
		if(query.getText() != null && !"".equals(query.getText())){
			Filter tf = new Filter();
			List<Filter> textFilters = new ArrayList<Filter>();

			String text = query.getText();
			if(query.isInAbstract()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_ABSTRACT,
						text, Filter.Operator.OP_CONTAINS));
			}
			if(query.isInTitle()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_TITLE,
						text, Filter.Operator.OP_CONTAINS));
			}
			if (textFilters.size() == 1) {
				filterList.add(textFilters.get(0));
			} else {
				tf.setFilterJoinClause(Filter.JoinClause.FC_OR);
				tf.setNestedFilters(textFilters);
				filterList.add(tf);
			}
		}

		// Reference Key Filter
		if (query.getReference_key() != null) {
			Filter rkf = new Filter(SearchConstants.REF_KEY, "" + query.getReference_key(), Filter.Operator.OP_EQUAL);
			filterList.add(rkf);
		}

		// Marker Key Filter
		if (query.getMarker_key() != null) {
			Filter mkf = new Filter(SearchConstants.MRK_KEY, "" + query.getMarker_key(), Filter.Operator.OP_EQUAL);
			filterList.add(mkf);
		}

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }
        else
        {
        	// default to a query that will bring no results back
        	containerFilter = new Filter(IndexConstants.GXD_LIT_SINGLE_KEY,"-1",Filter.Operator.OP_EQUAL);
        }

        logger.debug("Got past the filter construction.");

        return containerFilter;
    }

	private List<String> parseList(String list, String delimiter){
		String parsed[] = list.split(delimiter);
		List<String> items = new ArrayList<String>();
		String item;
		for (int i = 0; i < parsed.length; i++) {
			item = parsed[i].trim();
			if (item != null && !"".equals(item) ){
				items.add(item);
			}
		}
		return items;
	}
}
