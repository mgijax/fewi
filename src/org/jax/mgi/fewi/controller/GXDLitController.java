package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.GxdLitAssayTypeAgePair;
import mgi.frontend.datamodel.GxdLitIndexRecord;
import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.finder.FooFinder;
import org.jax.mgi.fewi.finder.GxdLitFinder;
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
import org.jax.mgi.fewi.summary.GxdLitAssayTypeSummaryRow;
import org.jax.mgi.fewi.summary.GxdLitGeneSummaryRow;
import org.jax.mgi.fewi.summary.GxdLitReferenceSummaryRow;
import org.jax.mgi.fewi.util.StyleAlternator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * GXDLitController
 * @author mhall
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
    private FooFinder fooFinder;
    
    @Autowired
    private GxdLitFinder gxdLitFinder;

    @Autowired
    private ReferenceFinder referenceFinder;


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
/*    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView fooDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->fooDetailByKey started");

        // find the requested foo
        SearchResults searchResults
          = fooFinder.getFooByKey(dbKey);
        List<Marker> fooList = searchResults.getResultObjects();

        // there can be only one...
        if (fooList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Foo Found");
            return mav;
        }// success

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("foo_detail");

        //pull out the foo, and add to mav
        Marker foo = fooList.get(0);
        mav.addObject("foo", foo);

        // package referenes; gather via object traversal
        List<Reference> references = foo.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        return mav;
    }*/

    
    
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
        params.setPageSize(1000);
        
        logger.debug("Hitting the finder.");
        
        SearchResults results = gxdLitFinder.getGxdLitRecords(params);
        
        logger.debug("Building the summary rows");
        
        List<GxdLitIndexRecord> recordList = results.getResultObjects();

        logger.debug("Got the record list");
        
        // create/load the list of SummaryRow wrapper objects
        List<GxdLitGeneSummaryRow> summaryRows = new ArrayList<GxdLitGeneSummaryRow> ();
        
        String symbol = "";
        Boolean first = Boolean.TRUE;
        GxdLitGeneSummaryRow row = null;
        
        for (GxdLitIndexRecord record: recordList) {
        		if (first || !symbol.equals(record.getMarkerSymbol())) {
        			
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
        			
        			row = new GxdLitGeneSummaryRow(record, queryForm);
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
		
		HashSet <String> references = new HashSet<String>();
		int totalCount = 0;
		int totalReferences = 0;
		
		for (GxdLitGeneSummaryRow outRow: summaryRows) {
			for (GxdLitReferenceSummaryRow ref: outRow.getReferenceRecords()) {
				references.add(ref.getJnum());
				totalCount ++;
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
        mav.addObject("pairTable", parseAgeAssay(summaryRows));

        return mav;
    }


    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    private GxdLitAgeAssayTypePairTable parseAgeAssay (List<GxdLitGeneSummaryRow> rows) {
		
		Map <String, Boolean> hasAgeMap = new HashMap<String, Boolean> ();
		Map <String, Boolean> hasAssayTypeMap = new HashMap<String, Boolean> ();
		
		Map<String, Map<String, Integer>> countMap = new HashMap<String, Map<String, Integer>> ();
		
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
		assayTypes.add("Western");
		assayTypes.add("Western blot");
		assayTypes.add("RT-PCR");
		assayTypes.add("cDNA clones");
		assayTypes.add("RNase protection");
		assayTypes.add("Nuclease S1");
		assayTypes.add("Primer Extension");
	
		for (String type: assayTypes) {
			hasAssayTypeMap.put(type, Boolean.FALSE);
		}
		
		for (GxdLitGeneSummaryRow row: rows) {
			for (GxdLitReferenceSummaryRow refRows: row.getReferenceRecords()) {
				for (GxdLitAssayTypeAgePair pair: refRows.getValidPairs()) {
					String curAge = pair.getAge();
					String curType = pair.getAssayType().trim();
					hasAgeMap.put(curAge, Boolean.TRUE);
					hasAssayTypeMap.put(curType, Boolean.TRUE);
					if (! countMap.containsKey(curType)) {
						Map <String, Integer> newAgeMap = new HashMap<String, Integer> ();
						countMap.put(curType, newAgeMap);
					}
					
					if (! countMap.get(curType).containsKey(curAge)) {
						countMap.get(curType).put(curAge, 1);
					}
					else {
						countMap.get(curType).put(curAge, countMap.get(curType).get(curAge) + 1);
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
	    				if (countMap.get(key).containsKey(age)) {
	    					row.addCount("" + countMap.get(key).get(age));
	    				}
	    				else {
	    					row.addCount("");
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
        if ((nomen != null) && (!"".equals(nomen))) {
            filterList.add(new Filter (SearchConstants.GXD_LIT_MRK_NOMEN, nomen,
                Filter.OP_EQUAL));
        }

        List <String> ageList = query.getAge();
        Boolean ageAnyFound = Boolean.FALSE;
        
        if (ageList != null && ageList.size() != 0) {
        // Age Filter
        
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
