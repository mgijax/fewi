package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.GxdLitAssayTypeAgePair;
import mgi.frontend.datamodel.GxdLitIndexRecord;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

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
import org.jax.mgi.fewi.util.Highlighter;
import org.jax.mgi.fewi.util.StyleAlternator;
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

    private Logger logger
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
    public ModelAndView getQueryForm() {

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
    public ModelAndView gxdLitDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->gxdLitDetailByKey started");

        // find the requested Lit Detail
        SearchResults<GxdLitIndexRecord> searchResults
          = gxdLitFinder.getGxdLitByKey(dbKey);

        return gxdLitDetail(searchResults.getResultObjects(), dbKey);
    }
    
    @RequestMapping(value="/key")
    public ModelAndView gxdLitDetailByKeyParam(@RequestParam("_Index_key") String dbKey) {
        logger.debug("->referenceSummaryByAlleleKey started: " + dbKey);
       
        // find the requested Lit Detail
        SearchResults<GxdLitIndexRecord> searchResults
          = gxdLitFinder.getGxdLitByKey(dbKey);

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

        GxdLitGeneSummaryRow record = new GxdLitGeneSummaryRow(indexRecordList.get(0), queryForm, null);
        List <GxdLitGeneSummaryRow> detailList = new ArrayList<GxdLitGeneSummaryRow> ();
        detailList.add(record);

        mav.addObject("record", record.getReferenceRecords().get(0));
        mav.addObject("pairTable", parseAgeAssay(detailList, Boolean.TRUE, queryForm));
        mav.addObject("reference", indexRecordList.get(0).getReference());
        mav.addObject("marker", indexRecordList.get(0).getMarker());

        return mav;  	
    }



    //---------------------------------------------//
    // GXD Lit Query Form Summary by marker
    //---------------------------------------------//
    @RequestMapping("/marker/{markerID}")
    public ModelAndView gxdLitSummaryByMarker(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm,
            @PathVariable("markerID") String markerID) {

        logger.debug("->gxdLitSummary by Marker started");
        logger.debug("getting marker for id: " + markerID);

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIdFilter);

        // find the requested marker
        SearchResults searchResults
          = markerFinder.getMarkerByID(searchParams);
        List<Marker> markerList = searchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Foo Found");
            return mav;
        }
        if (markerList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }


        SearchParams params = new SearchParams();

        Marker marker = markerList.get(0);

        queryForm.setMarker_key(marker.getMarkerKey());

        Filter markerKeyFilter = new Filter(SearchConstants.MRK_KEY, ""+marker.getMarkerKey());

        logger.debug(markerKeyFilter.toString());

        params.setFilter(markerKeyFilter);
        params.setSorts(this.genSorts(request));
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder 2.");

        SearchResults results = gxdLitFinder.getGxdLitRecords(params);

        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list");

        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, null);

        // Get the total count of references and records.

		HashSet <String> references = new HashSet<String>();
		int totalCount = 0;
		int totalReferences = 0;

		Boolean hasFullyCoded = Boolean.FALSE;

		for (GxdLitGeneSummaryRow outRow: summaryRows) {
			for (GxdLitReferenceSummaryRow ref: outRow.getReferenceRecords()) {
				references.add(ref.getJnum());
				if (!hasFullyCoded && ref.getIsFullyCoded()) {
				    hasFullyCoded = Boolean.TRUE;
				}
				totalCount ++;
			}
		}

		totalReferences = references.size();

        ModelAndView mav = new ModelAndView("gxdlit_summary_by_marker");
        mav.addObject("marker", marker);
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", totalReferences);
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("age", queryForm.getAge().get(0));
        mav.addObject("assayType", queryForm.getAssayType().get(0));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", hasFullyCoded);

        return mav;
    }

    //---------------------------------------------//
    // GXD Lit Query Form Summary by reference
    //---------------------------------------------//
    @RequestMapping("/reference/{refID}")
    public ModelAndView gxdLitSummaryByReference(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm,
            @PathVariable("refID") String refID) {

        logger.debug("->gxdLitSummary by Reference started");
        logger.debug("getting reference for id: " + refID);

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter referenceIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(referenceIdFilter);

        // find the requested reference
        SearchResults searchResults
          = referenceFinder.searchReferences(searchParams);
        List<Reference> referenceList = searchResults.getResultObjects();

        // there can be only one...
        if (referenceList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Foo Found");
            return mav;
        }
        if (referenceList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }


        SearchParams params = new SearchParams();

        Reference reference = referenceList.get(0);

        Filter referenceKeyFilter = new Filter(SearchConstants.REF_KEY, ""+reference.getReferenceKey());

        logger.debug(referenceKeyFilter.toString());

        params.setFilter(referenceKeyFilter);
        params.setSorts(this.genSorts(request));
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder 2.");

        SearchResults results = gxdLitFinder.getGxdLitRecords(params);

        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list");

        queryForm.setReference_key(reference.getReferenceKey());

        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, null);

        // Get the total count of references and records.

		HashSet <String> references = new HashSet<String>();
		int totalCount = 0;
		int totalReferences = 0;

		Boolean hasFullyCoded = Boolean.FALSE;

		for (GxdLitGeneSummaryRow outRow: summaryRows) {
			for (GxdLitReferenceSummaryRow ref: outRow.getReferenceRecords()) {
				references.add(ref.getJnum());
				if (!hasFullyCoded && ref.getIsFullyCoded()) {
				    hasFullyCoded = Boolean.TRUE;
				}
				totalCount ++;
			}
		}

		totalReferences = references.size();

        ModelAndView mav = new ModelAndView("gxdlit_summary_by_reference");
        mav.addObject("reference", reference);
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", totalReferences);
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("age", queryForm.getAge().get(0));
        mav.addObject("assayType", queryForm.getAssayType().get(0));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", hasFullyCoded);

        return mav;
    }


    //---------------------------------------------//
    // GXD Lit Query Form Summary by age and assay
    //---------------------------------------------//
    @RequestMapping("/summary/ageAssay")
    public ModelAndView gxdLitSummaryByAgeAndAssay(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm) {

        logger.debug("->gxdLitSummary By Age and Assay started");
        logger.debug("queryString: " + request.getQueryString());

        SearchParams params = new SearchParams();

        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(queryForm));
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder.");

        SearchResults results = gxdLitFinder.getGxdLitRecords(params);

        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list");

        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, null);

        // Get the total count of references and records.

		HashSet <String> references = new HashSet<String>();
		int totalCount = 0;
		int totalReferences = 0;

		Boolean hasFullyCoded = Boolean.FALSE;

		for (GxdLitGeneSummaryRow outRow: summaryRows) {
			for (GxdLitReferenceSummaryRow ref: outRow.getReferenceRecords()) {
				references.add(ref.getJnum());
				if (!hasFullyCoded && ref.getIsFullyCoded()) {
				    hasFullyCoded = Boolean.TRUE;
				}
				totalCount ++;
			}
		}

		totalReferences = references.size();

        ModelAndView mav = new ModelAndView("gxdlit_summary_by_age_assay");
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", totalReferences);
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("age", queryForm.getAge().get(0));
        mav.addObject("assayType", queryForm.getAssayType().get(0));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", hasFullyCoded);

        return mav;
    }

    //-------------------------//
    // GXD Lit Query Form Summary
    //-------------------------//
    @RequestMapping("/summary")
    public ModelAndView gxdLitSummary(HttpServletRequest request,
            @ModelAttribute GxdLitQueryForm queryForm) {

        logger.debug("->gxdLitSummary started");
        logger.debug("queryString: " + request.getQueryString());

        SearchParams params = new SearchParams();

        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(queryForm));
//		params.setIncludeSetMeta(true);
//		params.setIncludeMetaHighlight(true);
//		params.setIncludeRowMeta(true);
//		params.setIncludeMetaScore(true);
        params.setPageSize(gxdLimit);

        logger.debug("Hitting the finder.");

        SearchResults results = gxdLitFinder.getGxdLitRecords(params);

        Map<String, Set<String>> highlighting = results.getResultSetMeta().getSetHighlights();

        Highlighter textHl = null;

//        logger.debug("Checking highlighting.");

//        if (highlighting.containsKey(SearchConstants.GXD_LIT_LONG_CITATION)){
//        	textHl = new Highlighter(highlighting.get(SearchConstants.GXD_LIT_LONG_CITATION));
//        }

        logger.debug("Building the summary rows");

        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list: " + recordList.size() + " records");

        // create/load the list of SummaryRow wrapper objects for the gene section
        List<GxdLitGeneSummaryRow> summaryRows = generateGeneSection(recordList, queryForm, textHl);

        // Get the total count of references and records.

		HashSet <String> references = new HashSet<String>();
		int totalCount = 0;
		int totalReferences = 0;

        Boolean hasFullyCoded = Boolean.FALSE;

		for (GxdLitGeneSummaryRow outRow: summaryRows) {
			for (GxdLitReferenceSummaryRow ref: outRow.getReferenceRecords()) {
				references.add(ref.getJnum());
				totalCount ++;
				if (!hasFullyCoded && ref.getIsFullyCoded()) {
				    hasFullyCoded = Boolean.TRUE;
				}
			}
		}

		totalReferences = references.size();

        ModelAndView mav = new ModelAndView("gxdlit_summary");
        mav.addObject("stripe", new StyleAlternator("stripe1","stripe2"));
        mav.addObject("geneResult", new StyleAlternator("","stripe3"));
        mav.addObject("summaryRows", summaryRows);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);
        mav.addObject("refCount", totalReferences);
        mav.addObject("totalCount", totalCount);
        mav.addObject("pairTable", parseAgeAssay(summaryRows, Boolean.FALSE, queryForm));
        mav.addObject("limit", gxdLimit);
        mav.addObject("hasFullyCoded", hasFullyCoded);

        return mav;
    }


    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    private List<GxdLitGeneSummaryRow> generateGeneSection(List<GxdLitIndexRecord> recordList, GxdLitQueryForm queryForm, Highlighter textHl) {

    	logger.debug("Generating the Gene Section");

    	List<GxdLitGeneSummaryRow> summaryRows = new ArrayList<GxdLitGeneSummaryRow> ();

    	String symbol = "";
	    Boolean first = Boolean.TRUE;
	    GxdLitGeneSummaryRow row = null;

	    for (GxdLitIndexRecord record: recordList) {

	    		if (first || !symbol.equals(record.getMarkerSymbol())) {

	    			//logger.debug("Either this is the first, or its a new symbol.");

	    			if (!first) {

	    				// Has a result count
	    				Boolean hasACount = Boolean.FALSE;
	    				for (GxdLitReferenceSummaryRow refRow: row.getReferenceRecords()) {
	    					if (new Integer(refRow.getCount()) > 0) {
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
				if (new Integer(refRow.getCount()) > 0) {
					hasACount = Boolean.TRUE;
				}
			}

			if (hasACount) {
				// Add in the last record
				summaryRows.add(row);
			}
	    }

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
		ages.add("A");

		for (String age: ages) {
			hasAgeMap.put(age, Boolean.FALSE);
		}

		List<String> assayTypes = new ArrayList<String> ();
		assayTypes.add("In situ protein (section)");
		assayTypes.add("In situ RNA (section)");
		assayTypes.add("In situ protein (whole mount)");
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

		for (GxdLitGeneSummaryRow row: rows) {
			for (GxdLitReferenceSummaryRow refRows: row.getReferenceRecords()) {
				for (GxdLitAssayTypeAgePair pair: refRows.getValidPairs()) {
					String curAge = pair.getAge();
					String curType = pair.getAssayType().trim();
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


	    for (String key: assayTypes) {
	    	if (hasAssayTypeMap.get(key)) {
	    		GxdLitAssayTypeSummaryRow row = new GxdLitAssayTypeSummaryRow(key);
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

        String nomen = query.getNomen();

        // Nomen Filter
        if ((nomen != null) && (!"".equals(nomen.trim()))) {
	    String field;

	    // remember if the query string was quoted
	    boolean wasQuoted = false;
	    if (nomen.contains("\"")) { wasQuoted = true; }

	    // then strip the quotes
	    nomen = nomen.replaceAll("\"", "");

	    // convert any substrings of non-alphanumeric characters with a
	    // space
	    nomen = nomen.replaceAll("[^A-Za-z0-9]+", " "); 

	    // then remove any leading and trailing spaces
	    nomen = nomen.trim();

	    if (nomen.length() < 1) {
		// if we have no search string left after cleanup, do not add
		// a filter for nomenclature

	    } else if (nomen.length() <= 2) {
		// for 1-2 characters, do an exact match against only symbols
		// and synonyms.  if it was quoted, just ignore the quotes.

		field = SearchConstants.GXD_LIT_MRK_SYMBOL;
        	filterList.add(new Filter (field, nomen.trim(),
			Filter.OP_EQUAL));

	    } else if (wasQuoted) {
		// for 3+ characters with quotes, do a contains search for the
		// quoted string

		field = SearchConstants.GXD_LIT_MRK_NOMEN;
        	filterList.add(new Filter (field, "\"" + nomen + "\"", 
			Filter.OP_CONTAINS));

	    } else {
		// for 3+ characters with no quotes, do a begins search
		// where we must match ALL tokens

		field = SearchConstants.GXD_LIT_MRK_NOMEN_BEGINS;

		// separate tokens on one or more consecutive spaces
		String[] tokens = nomen.split(" +");

		// collect a filter for each token

		List<Filter> nomenFilters = new ArrayList<Filter>();

		for (String token: tokens) {
			nomenFilters.add (new Filter (field, token,
				Filter.OP_EQUAL) );
		} 

		// If we only found one filter, just add it.
		// Otherwise, AND them together under a grouping filter and
		// add that one.

		if (nomenFilters.size() == 1) {
    			filterList.add(nomenFilters.get(0));
		} else {
    			Filter tf = new Filter();
    			tf.setFilterJoinClause(Filter.FC_AND);
			tf.setNestedFilters(nomenFilters);
			filterList.add(tf);
    		}
	    }
        }

        // Age Filter

        List <String> ageList = query.getAge();
        Boolean ageAnyFound = Boolean.FALSE;

        if (ageList != null && ageList.size() != 0) {
	        List <Filter> ageFilters = new ArrayList<Filter>();

	        for (String age: ageList) {
		        if ((age != null) && (!"".equals(age))) {

		        	if (age.equals("ANY")) {
		        		ageAnyFound = Boolean.TRUE;
		        	}
		        	ageFilters.add(new Filter (SearchConstants.GXD_LIT_AGE , age,
			                Filter.OP_EQUAL));
		        }
	        }

	        if (!ageAnyFound) {
				if (ageFilters.size() == 1) {
					filterList.add(ageFilters.get(0));
				} else {
					Filter tf = new Filter();
					tf.setFilterJoinClause(Filter.FC_OR);
					tf.setNestedFilters(ageFilters);
					filterList.add(tf);
				}
	        }
        }

        // Assay Type Filter

        List<String> assayTypeList = query.getAssayType();
        Boolean assayTypeAnyFound = Boolean.FALSE;

        if (assayTypeList != null && assayTypeList.size() != 0) {

    	        List <Filter> assayTypeFilters = new ArrayList<Filter>();

    	        for (String assayType: assayTypeList) {
    		        if ((assayType != null) && (!"".equals(assayType))) {

    		        	if (assayType.equals("ANY")) {
    		        		assayTypeAnyFound = Boolean.TRUE;
    		        	}
    		        	assayTypeFilters.add(new Filter (SearchConstants.GXD_LIT_ASSAY_TYPE , assayType,
    			                Filter.OP_EQUAL));
    		        }
    	        }

    	        if (!assayTypeAnyFound) {
    				if (assayTypeFilters.size() == 1) {
    					filterList.add(assayTypeFilters.get(0));
    				} else {
    					Filter tf = new Filter();
    					tf.setFilterJoinClause(Filter.FC_OR);
    					tf.setNestedFilters(assayTypeFilters);
    					filterList.add(tf);
    				}
    	        }
            }


		//build author query filter
		if(query.getAuthor() != null && !"".equals(query.getAuthor())){

			List<String> authors = this.parseList(query.getAuthor().trim(), ";");

			String scope = query.getAuthorScope();

			if ("first".equals(scope)){
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_FIRST,
						authors, Filter.OP_IN));
			} else if ("last".equals(scope)){
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_LAST,
						authors, Filter.OP_IN));
			} else {
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_ANY,
						authors, Filter.OP_IN));
			}
		}

		// build journal query filter
		if(query.getJournal() != null && !"".equals(query.getJournal())){

			List<String> journals = this.parseList(query.getJournal().trim(), ";");

			filterList.add(new Filter(SearchConstants.REF_JOURNAL,
					journals, Filter.OP_IN));
		}


		// build year query filter
		String year = query.getYear().trim();
		if(year != null && !"".equals(year)){
			int rangeLoc = year.indexOf("-");
			if(rangeLoc > -1){
				// TODO validate years are numbers

				List<String> years = this.parseList(year, "-");

				if (years.size() == 2){
					logger.debug("year range: " + years.get(0) + "-" + years.get(1));
					Integer one = new Integer(years.get(0));
					Integer two = new Integer(years.get(1));

					if (one > two){
						years.set(0, two.toString());
						years.set(1, one.toString());
					}
					filterList.add(new Filter(SearchConstants.REF_YEAR,
							years.get(0), Filter.OP_GREATER_OR_EQUAL));
					filterList.add(new Filter(SearchConstants.REF_YEAR,
							years.get(1), Filter.OP_LESS_OR_EQUAL));
				} else {
					if (rangeLoc == 0){
						logger.debug("year <= " + years.get(0));
						filterList.add(new Filter(SearchConstants.REF_YEAR,
								years.get(0), Filter.OP_LESS_OR_EQUAL));
					} else {
						logger.debug("year >= " + years.get(0));
						filterList.add(new Filter(SearchConstants.REF_YEAR,
								years.get(0), Filter.OP_GREATER_OR_EQUAL));
					}
				}
				// TODO error: too many years entered
			} else {
				filterList.add(new Filter(SearchConstants.REF_YEAR,
						year, Filter.OP_EQUAL));
			}
		}

		// build text query filter
		if(query.getText() != null && !"".equals(query.getText())){
			Filter tf = new Filter();
			List<Filter> textFilters = new ArrayList<Filter>();

			String text = query.getText();
			if(query.isInAbstract()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_ABSTRACT,
						text, Filter.OP_CONTAINS));
			}
			if(query.isInTitle()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_TITLE,
						text, Filter.OP_CONTAINS));
			}
			if (textFilters.size() == 1) {
				filterList.add(textFilters.get(0));
			} else {
				tf.setFilterJoinClause(Filter.FC_OR);
				tf.setNestedFilters(textFilters);
				filterList.add(tf);
			}
		}

		// Reference Key Filter
		if (query.getReference_key() != null && !query.getReference_key().equals("")) {
			Filter rkf = new Filter(SearchConstants.REF_KEY, "" + query.getReference_key(), Filter.OP_EQUAL);
			filterList.add(rkf);
		}

		// Marker Key Filter
		if (query.getMarker_key() != null && !query.getMarker_key().equals("")) {
			Filter mkf = new Filter(SearchConstants.MRK_KEY, "" + query.getMarker_key(), Filter.OP_EQUAL);
			filterList.add(mkf);
		}

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
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
