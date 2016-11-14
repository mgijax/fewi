package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jax.mgi.fewi.searchUtil.entities.GxdHtSample;
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
import org.springframework.web.bind.annotation.PathVariable;

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

	// retrieves the query form
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

	// summary page for results from a query form submission
	@RequestMapping("/summary")
	public ModelAndView gxdHtSummary(HttpServletRequest request, @ModelAttribute GxdHtQueryForm queryForm) {

		logger.debug("->gxdHtSummary started");
		logger.debug("queryString: " + request.getQueryString());

		String errorString = validateParameters(queryForm);
		if (errorString != null) {
			return errorMav(errorString);
		}

		// if both age and stage are submitted, prefer stage and remove consideration of age
		if ((queryForm.getTheilerStage() != null) && (!queryForm.getTheilerStage().contains(GxdHtQueryForm.ANY_STAGE))) {
			if ((queryForm.getAge() != null) && (!queryForm.getAge().contains(GxdHtQueryForm.ANY_AGE))) {
				queryForm.setAge(GxdHtQueryForm.ANY_AGE);
			}
		}
		
		ModelAndView mav = new ModelAndView("gxdht/gxdht_query");
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("queryForm", queryForm);
		mav.addObject("pageType", "Summary");

		return mav;
	}

	// popup for samples, given an ArrayExpress experiment ID
	@RequestMapping(value="/samples/{experimentID:.+}", method = RequestMethod.GET)
	public ModelAndView gxdHtSamples(@PathVariable("experimentID") String experimentID, @ModelAttribute GxdHtQueryForm queryForm) {
		logger.debug("->gxdHtSamples started (ID " + experimentID + ")");

		GxdHtQueryForm query = new GxdHtQueryForm();
		query.setArrayExpressID(experimentID);
		
		// retrieve the experiment -- needed both for display of experiment info and to get the
		// experiment key that we can use to retrieve the samples
		
		SearchParams params = new SearchParams();
		params.setFilter(genFilters(query));
		SearchResults<GxdHtExperiment> searchResults = gxdHtFinder.getExperiments(params, query);
		List<GxdHtExperiment> experimentList = searchResults.getResultObjects();

		String error = null;
		GxdHtExperiment experiment = null;
		List<GxdHtSample> samples = null;
		boolean anyNonMouse = false;
		
		if (experimentList == null) {
			error = "Cannot find experiment for ID " + experimentID;
		} else if (experimentList.size() == 0) {
			error = "Cannot find experiment for ID " + experimentID;
		} else if (experimentList.size() > 1) {
			error = "Multiple experiments found for ID " + experimentID;
		} else {
			// retrieve samples for the experiment 
			experiment = experimentList.get(0);
			
			GxdHtQueryForm sampleQF = new GxdHtQueryForm();
			sampleQF.setExperimentKey(experiment.getExperimentKey().toString());

			SearchParams sampleParams = new SearchParams();
			sampleParams.setFilter(genFilters(sampleQF));
			sampleParams.setPageSize(9999999);
			
			SearchResults<GxdHtSample> sampleResults = gxdHtFinder.getSamples(sampleParams);
			samples = sampleResults.getResultObjects();
			
			for (GxdHtSample sample : samples) {
				String organism = sample.getOrganism();
				if ((organism == null) || !organism.contains("mouse")) {
					anyNonMouse = true;
					break;
				}
			}
		}

		// if we found any samples, get the matching ones and float them to the top
		if ((samples != null) && (samples.size() > 0)) {
			// add the experiment key to the set of parameters from the experiment search, to ensure
			// we're only bringing back samples for the desired experiment
			queryForm.setExperimentKey(experiment.getExperimentKey().toString());
			
			// also, we only want to float relevant samples to the top (relevancy = 'Yes')
			queryForm.setRelevancy("Yes");

			SearchParams sampleParams = new SearchParams();
			sampleParams.setFilter(genFilters(queryForm));
			Set<String> matchingSampleKeys = gxdHtFinder.getMatchingSampleKeys(sampleParams);

			List<GxdHtSample> matches = new ArrayList<GxdHtSample>();
			List<GxdHtSample> nonMatches = new ArrayList<GxdHtSample>();
			
			for (GxdHtSample sample : samples) {
				if (matchingSampleKeys.contains(sample.getSampleKey().toString())) {
					matches.add(sample);
					sample.setMatchesSearch(true);
				} else {
					nonMatches.add(sample);
				}
			}
			matches.addAll(nonMatches);
			samples = matches;
		}

		ModelAndView mav = new ModelAndView("gxdht/gxdht_samples");
		mav.addObject("experimentID", experimentID);
		if (error != null) { mav.addObject("error", error); }
		if (experiment != null) { mav.addObject("experiment", experiment); }
		if (samples != null) { mav.addObject("samples", samples); }
		if (anyNonMouse) { mav.addObject("showOrganism", true); }

		return mav;
	}

	/* returns non-null error string if any query form parameters fail validation
	 */
	public String validateParameters(GxdHtQueryForm query) {
		String structure = query.getStructure();
		if ((structure != null) && (structure.length() > 0) && !structure.matches(".*[A-Za-z0-9].*")) {
			return "Structure field must contain at least one letter or number; you specified: " + structure;
		}

		String text = query.getText();
		if ((text != null) && (text.length() > 0) && !text.matches(".*[A-Za-z0-9].*")) {
			return "Text field must contain at least one letter or number; you specified: " + text;
		}
		
		List<String> textScopes = query.getTextScope();
		if (textScopes != null) {
			Map<String,String> validScopes = query.getTextScopeOptions();
			for (String scope : textScopes) {
				if (!validScopes.containsKey(scope)) {
					return "Invalid selection for Text scope: " + scope;
				}
			}
		}

		List<Integer> stageList = query.getTheilerStage();
		if (stageList != null) {
			Map<Integer,String> stages = query.getTheilerStages();
			for (Integer stage : stageList) {
				if (!stages.containsKey(stage)) {
					return "Invalid selection for Theiler Stage: " + stage;
				}
			}
		}
		
		List<String> ages = query.getAge();
		if (ages != null) {
			Map<String,String> validAges = query.getAges();
			for (String age : ages) {
				if (!validAges.containsKey(age)) {
					return "Invalid selection for Age: " + age;
				}
			}
		}
		
		String sex = query.getSex();
		if ((sex != null) && (sex.length() > 0) && !query.getSexOptions().containsKey(sex)) {
				return "Invalid selection for Sex: " + sex;
		}
		
		String mutant = query.getMutatedIn();
		if ((mutant != null) && (mutant.length() > 0) && !mutant.matches(".*[A-Za-z0-9].*")) {
			return "Mutant field must contain at least one letter or number; you specified: " + mutant;
		}
		
		String method = query.getMethod();
		if ((method != null) && !query.getMethodOptions().containsKey(method)) {
			return "Invalid selection for Method: " + method;
		}
		return null;
	}
	
	@RequestMapping("/table")
	public ModelAndView experimentsTable (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {

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
		
		if (query.searchDescription() || query.searchTitle()) {
			mav.addObject("textSearch", query.getText());
			if (query.searchDescription()) { mav.addObject("highlightDescription", true); }
			if (query.searchTitle()) { mav.addObject("highlightTitle", true); }
		}
		return mav;
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
		
		// perform query, return SearchResults 
		return gxdHtFinder.getExperiments(params, query);
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
	
	/* return a mav for an error screen with the given message filled in
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

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
		
		// search by ArrayExpress ID
		String arrayExpressID = query.getArrayExpressID();
		if ((arrayExpressID != null) && (arrayExpressID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_EXPERIMENT_ID, arrayExpressID, Filter.Operator.OP_EQUAL));
		}
		
		// search by experiment key
		String experimentKey = query.getExperimentKey();
		if ((experimentKey != null) && (experimentKey.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_EXPERIMENT_KEY, experimentKey, Filter.Operator.OP_EQUAL));
		}
		
		// search for relevancy
		String relevancy = query.getRelevancy();
		if ((relevancy != null) && (relevancy.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_RELEVANCY, relevancy, Filter.Operator.OP_EQUAL));
		}
		
		// search by structure
		String structure = query.getStructure();
		if ((structure != null) && (structure.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_STRUCTURE_SEARCH, structure.replaceAll(" ", "+"), Filter.Operator.OP_EQUAL));
		}
		
		// search by stage (if available) or fall back on age (if no stage)
		List<Integer> stages = query.getTheilerStage();
		List<String> ages = query.getAge();
		if ((stages != null) && (stages.size() > 0) && (!stages.contains(GxdHtQueryForm.ANY_STAGE))) {
			List<Filter> stageFilters = new ArrayList<Filter>();
			for(Integer stage : stages)
			{
				Filter stageF = new Filter(SearchConstants.GXD_THEILER_STAGE, stage, Filter.Operator.OP_HAS_WORD);
				stageFilters.add(stageF);

			}
			filterList.add(Filter.or(stageFilters));		// add the theiler stage search filter 
		}
		// search by age
		else if ((ages != null) && (ages.size() > 0) && (!ages.contains(GxdHtQueryForm.ANY_AGE))) {
			// also do nothing if both postnatal and embryonic are selected, because it is equivalent to ANY
			if (!(ages.contains(GxdHtQueryForm.EMBRYONIC) && ages.contains(GxdHtQueryForm.POSTNATAL)))
			{
				List<Filter> ageFilters = new ArrayList<Filter>();
				//postnatal means TS 28 (and TS 27)
				if (ages.contains(GxdHtQueryForm.POSTNATAL))
				{
					// same as TS 28 (and TS 27)
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,28,Filter.Operator.OP_HAS_WORD));
					ageFilters.add(new Filter(SearchConstants.GXD_THEILER_STAGE,27,Filter.Operator.OP_HAS_WORD));
				}
				//embryonic means TS 1-26
				// if they selected embryonic, none of the age selections matter
				if (ages.contains(GxdHtQueryForm.EMBRYONIC))
				{
					ageFilters.add(Filter.range(SearchConstants.GXD_THEILER_STAGE, "1", "26"));
				}
				else
				{
					// iterate the legit age queries
					for(String age : ages)
					{
						if(!age.equalsIgnoreCase(GxdHtQueryForm.EMBRYONIC)
								&& !age.equalsIgnoreCase(GxdHtQueryForm.POSTNATAL))
						{
							try{
								Filter ageMinFilter = new Filter(SearchConstants.GXD_AGE_MIN,age,Filter.Operator.OP_LESS_OR_EQUAL);
								Filter ageMaxFilter = new Filter(SearchConstants.GXD_AGE_MAX,age,Filter.Operator.OP_GREATER_OR_EQUAL);
								// AND the min and max query to make a range query;
								ageFilters.add(Filter.and(Arrays.asList(ageMinFilter,ageMaxFilter)));
							}
							catch (NumberFormatException ne)
							{
								logger.info("an invalid age was passed to the form");
								logger.info(ne.getMessage());
								// ignore this. It just means someone manually entered an invalid url
							}
						}
					}
				}
				filterList.add(Filter.or(ageFilters));
			}
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
