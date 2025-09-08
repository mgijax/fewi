package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

	@Autowired
	private IDLinker idLinker;

	//--- public methods ---//

	// retrieves the query form
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletRequest request, HttpServletResponse response) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}


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

	public Integer getGxdHtExperimentCount(
			HttpServletRequest request,
			GxdHtQueryForm query)
	{
		logger.debug("called /summary/totalCount");
		SearchParams params = new SearchParams();
		params.setFilter(genFilters(query));
		params.setPageSize(0);

		return gxdHtFinder.getExperimentCount(params, query);
	}

	// lookup of experiment count for CellType Ontoloty term
	public synchronized Integer getExperimentCountForCoID(String termID) {

		logger.debug("---in getExperimentCountForCoID(" + termID + ")");

		SearchParams params = new SearchParams();
		GxdHtQueryForm query = new GxdHtQueryForm();

		query.setCellTypeID(termID);
		params.setFilter(genFilters(query));
		params.setPageSize(0);

		return gxdHtFinder.getExperimentCount(params, query);
	}

	// determine if we searched by any sample-specific fields (not experiment-level fields); if
	// so, then we will need to float the matching samples to the top and do highlighting
	public boolean searchedBySampleFields(GxdHtQueryForm queryForm) {
		if ((queryForm.getAge() != null) && (queryForm.getAge().size() > 0)) {
			for (String age : queryForm.getAge()) {
				if (!age.equals(queryForm.ANY_AGE)) {
					return true;
				}
			}
		}
		if ((queryForm.getMutatedIn() != null) && (queryForm.getMutatedIn().length() > 0)) {
			return true;
		} 
		if ((queryForm.getMutantAlleleId() != null) && (queryForm.getMutantAlleleId().length() > 0)) {
			return true;
		} 
		if ((queryForm.getSex() != null) && (queryForm.getSex().length() > 0)) {
			return true;
		} 
		if ((queryForm.getStrain() != null) && (queryForm.getStrain().length() > 0)) {
			return true;
		} 
		if ((queryForm.getStructure() != null) && (queryForm.getStructure().length() > 0)) {
			return true;
		} 
		if ((queryForm.getStructureID() != null) && (queryForm.getStructureID().length() > 0)) {
			return true;
		} 
		if ((queryForm.getCellTypeID() != null) && (queryForm.getCellTypeID().length() > 0)) {
			return true;
		} 
		if ((queryForm.getCellType() != null) && (queryForm.getCellType().length() > 0)) {
			return true;
		} 
		if ((queryForm.getTheilerStage() != null) && (queryForm.getTheilerStage().size() > 0)) {
			for (Integer ts : queryForm.getTheilerStage()) {
				if (ts != queryForm.ANY_STAGE) {
					return true;
				}
			}
		} 
		if (queryForm.getMethod() == null || queryForm.getMethod().indexOf("RNA-seq") == -1) {
			for (String m : queryForm.getMethod()) {
				if (m.contains("bulk") || m.contains("spatial") || m.contains("single cell")) {
					return true;
				}
			}
		}
		return false;
	}

	// popup for samples, given an ArrayExpress experiment ID
	@RequestMapping(value="/samples/{experimentID:.+}", method = RequestMethod.GET)
	public ModelAndView gxdHtSamples(HttpServletRequest request, @PathVariable("experimentID") String experimentID, @ModelAttribute GxdHtQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
                boolean anyCellTypes = false;
		
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
				}
                                if (sample.getCelltypeTerm() != null) {
                                        anyCellTypes = true;
                                }
			}
		}

		boolean highlightSamples = searchedBySampleFields(queryForm);
		
		// if we found any samples, get the matching ones and float them to the top
		if (highlightSamples && (samples != null) && (samples.size() > 0)) {
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
                if (anyCellTypes) { mav.addObject("showCellTypes", true); }
		mav.addObject("highlightSamples", highlightSamples);

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
		
		List<String> methods = query.getMethod();
		for (String m : methods) {
			if (!query.getMethodOptions().containsKey(m)) {
				return "Invalid selection for Method: " + m;
			}
		}
		return null;
	}
	
	@RequestMapping("/table")
	public ModelAndView experimentsTable (HttpServletRequest request, @ModelAttribute GxdHtQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->experimentsTable started");

		boolean highlightSamples = searchedBySampleFields(query);

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
		mav.addObject("highlightSamples", highlightSamples);
		mav.addObject("idLinker", idLinker);
		
		if (query.searchDescription() || query.searchTitle()) {
			mav.addObject("textSearch", query.getText());
			if (query.searchDescription()) { mav.addObject("highlightDescription", true); }
			if (query.searchTitle()) { mav.addObject("highlightTitle", true); }
		}
		return mav;
	}

	/* get the set of experimental variable filter options for the current result set
	 */
	@RequestMapping("/facet/variable")
	public @ResponseBody Map<String, List<String>> getVariableFacet (@ModelAttribute GxdHtQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return this.getFacets(qf, "variable");
	}

	/* get the set of study type filter options for the current result set
	 */
	@RequestMapping("/facet/studyType")
	public @ResponseBody Map<String, List<String>> getStudyTypeFacet (@ModelAttribute GxdHtQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return this.getFacets(qf, "studyType");
	}

	/* get the set of method filter options for the current result set
	 */
	@RequestMapping("/facet/method")
	public @ResponseBody Map<String, List<String>> getMethodFacet (@ModelAttribute GxdHtQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return this.getFacets(qf, "method");
	}

	/* get the set of cell type filter options for the current result set
	 */
	@RequestMapping("/facet/celltype")
	public @ResponseBody Map<String, List<String>> getCellTypeFacet (@ModelAttribute GxdHtQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		return this.getFacets(qf, "cellType");
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	/* handles gathering the various sets of filter choices
	 */
	private Map<String, List<String>> getFacets (@ModelAttribute GxdHtQueryForm qf, String filterName) {
		Map<String, List<String>> out = new HashMap<String, List<String>>();
		List<String> messages = new ArrayList<String>();
		
		SearchParams params = new SearchParams();
		params.setPageSize(0);
		params.setFilter(genFilters(qf));
		List<String> facetChoices = null;
		
		if ("variable".equals(filterName)) {
			facetChoices = gxdHtFinder.getVariableFacet(params, qf).getResultFacets();
		} else if ("studyType".equals(filterName)) {
			facetChoices = gxdHtFinder.getStudyTypeFacet(params, qf).getResultFacets();
		} else if ("method".equals(filterName)) {
			facetChoices = gxdHtFinder.getMethodFacet(params, qf).getResultFacets();
		} else if ("cellType".equals(filterName)) {
			facetChoices = gxdHtFinder.getCellTypeFacet(params, qf).getResultFacets();
		}

		if (facetChoices == null) {
			messages.add("Coding error: Invalid filterName (" + filterName + ") in getFacets");
			out.put("error", messages);
			
		} else if (facetChoices.size() > facetLimit) {
			messages.add("Too many results to display.  Modify your search or try another filter first.");
			out.put("error", messages);

		} else if (facetChoices.size() == 0) {
			messages.add("No values in results to filter.");
			out.put("error", messages);
			
		} else {
			// remove choices that we don't want to give the user
			facetChoices.remove("Not Applicable");
			facetChoices.remove("Not Specified");
			facetChoices.remove("Not Curated");

			if ("variable".equals(filterName)) {
				Collections.sort(facetChoices, new GxdHtExperiment.SortExperimentalVariables());
			} else {
				Collections.sort(facetChoices);
			}
			out.put("resultFacets", facetChoices);
		}
		return out;
	}

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

		// search by mutant reference ID
		String referenceID = query.getReferenceID();
		if ((referenceID != null) && (referenceID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_REFERENCE_ID, referenceID, Filter.Operator.OP_EQUAL));
		}

		// search by mutant allele ID
		String mutantAlleleId = query.getMutantAlleleId();
		if ((mutantAlleleId != null) && (mutantAlleleId.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_MUTANT_ALLELE_IDS, mutantAlleleId, Filter.Operator.OP_EQUAL));
		}

		
		// search by string
		String strain = query.getStrain();
		if ((strain != null) && (strain.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_STRAIN, strain, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED));
		}
		
		// -----------------------------------------------------------------------
		// search by method
		List<String> methods = query.getMethod(); // from query form
		List<Filter> mFilters = new ArrayList<Filter>();
		if (methods != null && (methods.size() > 0)) {
			for (String m : methods) {
				if ((m != null) && (m.length() > 0)) {
					Filter mF = new Filter(SearchConstants.GXDHT_METHOD, m, Filter.Operator.OP_EQUAL);
					mFilters.add(mF);
				}
			}
		}
		if (mFilters.size() > 0) {
			filterList.add(Filter.or(mFilters));
		}
		// -----------------------------------------------------------------------
		// Apply method filter
		methods = query.getMethodFilter(); // from method filter
		mFilters = new ArrayList<Filter>();
		if (methods != null && (methods.size() > 0)) {
			for (String m : methods) {
				if ((m != null) && (m.length() > 0)) {
					Filter mF = new Filter(SearchConstants.GXDHT_METHODS, m, Filter.Operator.OP_EQUAL);
					mFilters.add(mF);
				}
			}
		}
		if (mFilters.size() > 0) {
			filterList.add(Filter.or(mFilters));
		}

		// -----------------------------------------------------------------------

		// search by sex
		String sex = query.getSex();
		if ((sex != null) && (sex.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_SEX, sex, Filter.Operator.OP_EQUAL));
		}
		
		// search by ArrayExpress ID
		String arrayExpressID = query.getArrayExpressID();
		if ((arrayExpressID != null) && (arrayExpressID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_EXPERIMENT_ID, arrayExpressID, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED));
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
		
		// search by cell type
		String cellType = query.getCellType();
		if ((cellType != null) && (cellType.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_CT_SEARCH_TERMS, cellType, Filter.Operator.OP_EQUAL));
		}
		
		// search by cell type ID
		String cellTypeID = query.getCellTypeID();
		if ((cellTypeID != null) && (cellTypeID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.GXDHT_CT_SEARCH_IDS, cellTypeID, Filter.Operator.OP_EQUAL));
		}
		
		// if cell type filter specified, add to query
		List<String> cellTypeFilter = query.getCellTypeFilter();
		if ((cellTypeFilter != null) && (cellTypeFilter.size() > 0)) {
		    List<Filter> ctFilters = new ArrayList<Filter>();
		    for (String ct : cellTypeFilter) {
			    if ((ct != null) && (ct.length() > 0)) {
				    Filter ctF = new Filter(SearchConstants.GXDHT_CT_FACET_TERMS, ct, Filter.Operator.OP_EQUAL);
				    ctFilters.add(ctF);
			    }
		    }
		    if (ctFilters.size() > 0) {
			    filterList.add(Filter.or(ctFilters));
		    }
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
		
		// filter by experimental variables
		List<String> variables = query.getVariableFilter();
		if ((variables != null) && (variables.size() > 0)) {
			List<Filter> variableFilters = new ArrayList<Filter>();
			for(String ev : variables)
			{
				Filter evFilter = new Filter(SearchConstants.GXDHT_EXPERIMENTAL_VARIABLE, ev, Filter.Operator.OP_EQUAL);
				variableFilters.add(evFilter);
			}
			filterList.add(Filter.or(variableFilters));		// any one of the experimental variables is a match 
		}
		
		// filter by study type
		List<String> studyTypes = query.getStudyTypeFilter();
		if ((studyTypes != null) && (studyTypes.size() > 0)) {
			List<Filter> studyTypeFilters = new ArrayList<Filter>();
			for(String st : studyTypes)
			{
				Filter stFilter = new Filter(SearchConstants.GXDHT_STUDY_TYPE, st, Filter.Operator.OP_EQUAL);
				studyTypeFilters.add(stFilter);
			}
			filterList.add(Filter.or(studyTypeFilters));		// any one of the study types is a match 
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
