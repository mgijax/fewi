package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.finder.FooFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
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

/*
 * This controller maps all /foo/ uri's
 */
@Controller
@RequestMapping(value="/foo")
public class FooController {

	private Logger logger = LoggerFactory.getLogger(FooController.class);

	@Autowired
	private FooFinder fooFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	//    @Autowired
	//	private SnpFinder snpFinder;

	@Autowired
	private VocabularyFinder vocabFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 


	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletResponse response) {

		logger.debug("->getQueryForm started");
		response.addHeader("Access-Control-Allow-Origin", "*");

		ModelAndView mav = new ModelAndView("foo_query");
		mav.addObject("sort", new Paginator());
		mav.addObject(new FooQueryForm());
		return mav;
	}

	@RequestMapping("/summary")
	public ModelAndView fooSummary(HttpServletRequest request, @ModelAttribute FooQueryForm queryForm) {

		logger.debug("->fooSummary started");
		logger.debug("queryString: " + request.getQueryString());

		ModelAndView mav = new ModelAndView("foo_summary");
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("queryForm", queryForm);

		return mav;
	}

	@RequestMapping("/vocab")
	public ModelAndView vocSummary(HttpServletRequest request) {
		logger.debug("->vocSummary started");

		List<VocabTerm> terms = vocabFinder.getVocabSubset("DO","Z");
		for(VocabTerm term : terms) {
			logger.debug("found term "+term.getTerm());
		}
		ModelAndView mav = new ModelAndView("foo_query");
		mav.addObject("sort", new Paginator());
		mav.addObject(new FooQueryForm());
		return mav;
	}

	@RequestMapping(value="/{fooID:.+}", method = RequestMethod.GET)
	public ModelAndView fooDetailByID(@PathVariable("fooID") String fooID) {

		logger.debug("->fooDetailByID started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter fooIdFilter = new Filter(SearchConstants.FOO_ID, fooID);
		searchParams.setFilter(fooIdFilter);

		// find the requested foo
		SearchResults<Marker> searchResults = fooFinder.getFooByID(searchParams);
		List<Marker> fooList = searchResults.getResultObjects();

		// there can be only one...
		if (fooList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No Foo Found");
			return mav;
		}
		if (fooList.size() > 1) { // dupe found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Duplicate ID");
			return mav;
		}
		// success - we have a single object

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
	}

	@RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
	public ModelAndView fooDetailByKey(@PathVariable("dbKey") String dbKey) {

		logger.debug("->fooDetailByKey started");

		// find the requested foo
		SearchResults<Marker> searchResults = fooFinder.getFooByKey(dbKey);
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
	}

	@RequestMapping(value="/reference/{refID}")
	public ModelAndView fooSummeryByRef(@PathVariable("refID") String refID) {

		logger.debug("->fooSummeryByRef started");

		ModelAndView mav = new ModelAndView("foo_summary_reference");

		// setup search parameters object to gather the requested object
		SearchParams searchParams = new SearchParams();
		Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
		searchParams.setFilter(refIdFilter);

		// find the requested reference
		SearchResults<Reference> searchResults = referenceFinder.searchReferences(searchParams);
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

	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<FooSummaryRow> seqSummaryJson(HttpServletRequest request, @ModelAttribute FooQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->JsonSummaryResponse started");

		// perform query, and pull out the requested objects
		SearchResults<Marker> searchResults = getSummaryResults(request, query, page);
		List<Marker> fooList = searchResults.getResultObjects();

		// create/load the list of SummaryRow wrapper objects
		List<FooSummaryRow> summaryRows = new ArrayList<FooSummaryRow> ();
		Iterator<Marker> it = fooList.iterator();
		while (it.hasNext()) {
			Marker foo = it.next();
			if (foo == null) {
				logger.debug("--> Null Object");
			} else {
				summaryRows.add(new FooSummaryRow(foo));
			}
		}

		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<FooSummaryRow> jsonResponse = new JsonSummaryResponse<FooSummaryRow>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		return jsonResponse;
	}

	/*
	 * This method handles requests various reports; txt, xls.  It is intended 
	 * to perform the same query as the json method above, but only place the 
	 * result obljects list on the model.  It returns a string to indicate the
	 * view name to look up in the view class in the excel or text.properties
	 */
	@RequestMapping("/report*")
	public String referenceSummaryReport(HttpServletRequest request, Model model, @ModelAttribute FooQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("fooSummaryReport");		
		SearchResults<Marker> searchResults = getSummaryResults(request, query, page);
		model.addAttribute("results", searchResults.getResultObjects());
		return "fooSummaryReport";			
	}

	/*
	 * This method maps requests for the foo facet list.  The results are
	 * returned as JSON.  
	 */
	@RequestMapping("/facet/foo")
	public @ResponseBody Map<String, List<String>> facetAuthor(@ModelAttribute FooQueryForm query) {
		// perform query and return results as json
		logger.debug("get filter facets here");

		SearchResults<String> results = new SearchResults<String>();
		// hard-coded results for example purposes
		List<String> foos = new ArrayList<String>();
		foos.add("foo 1");
		foos.add("foo 2");
		foos.add("foo 3");
		results.setResultFacets(foos);

		return parseFacetResponse(results);
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	/*
	 * This is a convenience method to handle packing the SearchParams object
	 * and return the SearchResults from the finder.
	 */
	private SearchResults<Marker> getSummaryResults( HttpServletRequest request, @ModelAttribute FooQueryForm query, @ModelAttribute Paginator page){

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(request));
		params.setFilter(genFilters(query));

		// perform query, return SearchResults 
		return fooFinder.getFoos(params);
	}

	/*
	 * This is a convenience method to parse the facet response from the 
	 * SearchResults object, inspect it for error conditions, and return a 
	 * map that the ui is expecting.
	 */
	private Map<String, List<String>> parseFacetResponse(SearchResults<String> facetResults) {

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();

		if (facetResults.getResultFacets().size() >= facetLimit){
			logger.debug("too many facet results");
			l.add("Too many results to display. Modify your search or try another filter first.");
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() == 0) {
			logger.debug("no facet results");
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			m.put("resultFacets", facetResults.getResultFacets());
		}
		return m;
	}

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
			filterList.add(new Filter (SearchConstants.FOO_ID, param1, Filter.Operator.OP_EQUAL));
		}

		//
		if ((param2 != null) && (!"".equals(param2))) {
			filterList.add(new Filter (SearchConstants.FOO_ID, param2, Filter.Operator.OP_EQUAL));
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
