package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.finder.GxdHtFinder;
import org.jax.mgi.fewi.forms.GxdHtQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*
 * This controller maps all /gxdht/ uri's
 * (name follows precedent for gxdlit)
 */
@Controller
@RequestMapping(value="/gxdht")
public class GXDHTController {

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(GXDHTController.class);

	@Autowired
	private GxdHtFinder gxdHtFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	//--- public methods ---//

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletResponse response) {

		logger.debug("->getQueryForm started");
		response.addHeader("Access-Control-Allow-Origin", "*");

		ModelAndView mav = new ModelAndView("gxdht/gxdht_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("pageType", "Search");
		
		GxdHtQueryForm qf = new GxdHtQueryForm();
		qf.setTextScopeDefault();

		mav.addObject("queryForm", qf);
		return mav;
	}

	@RequestMapping("/summary")
	public ModelAndView gxdHtSummary(HttpServletRequest request, @ModelAttribute GxdHtQueryForm queryForm) {

		logger.debug("->gxdHtSummary started");
		logger.debug("queryString: " + request.getQueryString());

		ModelAndView mav = new ModelAndView("gxdht/gxdht_query");
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("queryForm", queryForm);
		mav.addObject("pageType", "Summary");

		return mav;
	}

//	@RequestMapping("/json")
	@RequestMapping("/table")
	public ModelAndView experimentsTable (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {
//	public @ResponseBody JsonSummaryResponse<GxdHtExperiment> experimentsJson(HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->experimentsTable started");

		// perform query, and pull out the requested objects
		SearchResults<GxdHtExperiment> searchResults = getSummaryResults(request, query, page);
		List<GxdHtExperiment> experimentList = searchResults.getResultObjects();

		// create/load the list of SummaryRow wrapper objects
		List<GxdHtExperiment> summaryRows = new ArrayList<GxdHtExperiment> ();
		Iterator<GxdHtExperiment> it = experimentList.iterator();
		while (it.hasNext()) {
			GxdHtExperiment experiment = it.next();
			if (experiment == null) {
				logger.debug("--> Null Object");
			} else {
				summaryRows.add(experiment);
			}
		}
		
		ModelAndView mav = new ModelAndView("gxdht/gxdht_summary_table");
		mav.addObject("experiments", summaryRows);
		mav.addObject("count", summaryRows.size());
		mav.addObject("totalCount", searchResults.getTotalCount());
		return mav;

/*
		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<GxdHtExperiment> jsonResponse = new JsonSummaryResponse<GxdHtExperiment>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		return jsonResponse;
*/
	}
/*
 * note -- should also do lookup by structure ID
 * 
	@RequestMapping(value="/reference/{refID:.+}", method = RequestMethod.GET)
	public ModelAndView gxdHtSummaryByReference(@PathVariable("refID") String refID) {

		logger.debug("->gxdHtSummaryByReference started");

		ModelAndView mav = new ModelAndView("gxdHt_summary_reference");

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
*/
	
/*
*/

	/*
	 * This method handles requests various reports; txt, xls.  It is intended 
	 * to perform the same query as the json method above, but only place the 
	 * result objects list on the model.  It returns a string to indicate the
	 * view name to look up in the view class in the excel or text.properties
	 */
/*
	@RequestMapping("/report*")
	public String referenceSummaryReport(HttpServletRequest request, Model model, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("gxdHtSummaryReport");		
		SearchResults<Marker> searchResults = getSummaryResults(request, query, page);
		model.addAttribute("results", searchResults.getResultObjects());
		return "gxdHtSummaryReport";			
	}
*/

	/*
	 * This method maps requests for the gxdHt facet list.  The results are
	 * returned as JSON.  
	 */
/*
	@RequestMapping("/facet/gxdHt")
	public @ResponseBody Map<String, List<String>> facetAuthor(@ModelAttribute GxdHtQueryForm query) {
		// perform query and return results as json
		logger.debug("get filter facets here");

		SearchResults<String> results = new SearchResults<String>();
		// hard-coded results for example purposes
		List<String> gxdHts = new ArrayList<String>();
		gxdHts.add("gxdHt 1");
		gxdHts.add("gxdHt 2");
		gxdHts.add("gxdHt 3");
		results.setResultFacets(gxdHts);

		return parseFacetResponse(results);
	}
*/
	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	/*
	 * This is a convenience method to handle packing the SearchParams object
	 * and return the SearchResults from the finder.
	 */
	private SearchResults<GxdHtExperiment> getSummaryResults( HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page){

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(request));
		params.setFilter(genFilters(query));
		
		// need to alter this...  should search samples, get distinct experiment IDs and count of matching
		// samples for each, get experiments, set matching sample count for each experiment, return list
		// of experiments

		// perform query, return SearchResults 
		return gxdHtFinder.getExperiments(params);
	}

	/*
	 * This is a convenience method to parse the facet response from the 
	 * SearchResults object, inspect it for error conditions, and return a 
	 * map that the ui is expecting.
	 */
/*
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
*/
	
	// generate the sorts
	private List<Sort> genSorts(HttpServletRequest request) {
		logger.debug("->genSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied
		String sortRequested = request.getParameter("sort");
		if (sortRequested == null) {
			sortRequested = SortConstants.BY_DEFAULT;
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
	private Filter genFilters(GxdHtQueryForm query){
		logger.debug("->genFilters started");
		logger.debug("QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();

		// text search of title, description, or combined titleDescription field
		String textSearch = query.getText().trim();
		if ((textSearch != null) && (textSearch.length() > 0)) {
			List<String> textSearchScope = query.getTextScope();
			boolean searchTitle = textSearchScope.contains("Title");
			boolean searchDescription = textSearchScope.contains("Description");
			
			String textField = null;	// which text field to search
			
			if (searchTitle && searchDescription) {
				textField = SearchConstants.GXDHT_TITLE_DESCRIPTION;
			} else if (searchTitle) {
				textField = SearchConstants.GXDHT_TITLE;
			} else if (searchDescription) {
				textField = SearchConstants.GXDHT_DESCRIPTION;
			}
			
			if (textField != null) {
				List<Filter> textFilters = new ArrayList<Filter>();
				for (String token : textSearch.split("[^A-Za-z0-9\\*]")) {
					String wcToken = token.trim();
					if (!wcToken.endsWith("*")) {
						wcToken += "*";
					}
					textFilters.add(new Filter(textField, wcToken, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED));
				}
				filterList.add(Filter.and(textFilters));
			}
		}

		// search by mutated-in (gene symbol / ID / synonym)
		String mutatedIn = query.getMutatedIn();
		if ((mutatedIn != null) && (mutatedIn.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_MUTATED_GENE, mutatedIn, Filter.Operator.OP_EQUAL));
		}
		
		// search by method
		String method = query.getMethod();
		if ((method != null) && (method.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_METHOD, method, Filter.Operator.OP_EQUAL));
		}
		
		// search by sex
		String sex = query.getSex();
		if ((sex != null) && (sex.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_SEX, sex, Filter.Operator.OP_EQUAL));
		}
		
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		} else {
			containerFilter = Filter.range(SearchConstants.GXDHT_EXPERIMENT_KEY, "*", "*");
		}

		return containerFilter;
	}
}
