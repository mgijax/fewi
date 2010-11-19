package org.jax.mgi.fewi.controller;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

import mgi.frontend.datamodel.Allele;
import org.jax.mgi.fewi.finder.RecombinaseFinder;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.summary.RecombinaseSummary;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import java.util.Iterator;

/*--------------------------------------*/
/* standard imports for all controllers */
/*--------------------------------------*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /recombinase/ uri's
 */
@Controller
@RequestMapping(value="/recombinase")
public class RecombinaseController {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger (
		RecombinaseController.class);
	
	// get the finder to use for various methods
	@Autowired
	private RecombinaseFinder recombinaseFinder;

	/*-------------------------*/
	/* public instance methods */
	/*-------------------------*/

	// add new RecombinaseQueryForm and Paginator objects to model for QF 
	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model) {
		model.addAttribute(new RecombinaseQueryForm());
		model.addAttribute("sort", new Paginator());
		return "recombinase_query";
	}
	
	// qf submission.  drop completed ReferenceQueryForm object and query string
	// into model for summary to use
	@RequestMapping("/summary")
	public String recombinaseSummary(HttpServletRequest request, Model model,
			@ModelAttribute RecombinaseQueryForm queryForm) {

		model.addAttribute("recombinaseQueryForm", queryForm);
		model.addAttribute("queryString", request.getQueryString());
		logger.debug("queryString: " + request.getQueryString());

		return "recombinase_summary";
	}
	
	// this is the logic to perform the query and return json results
	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<RecombinaseSummary> recombinaseSummaryJson(
			HttpServletRequest request, 
			@ModelAttribute RecombinaseQueryForm query,
			@ModelAttribute Paginator page) {
				
		logger.debug(query.toString());

		// set up search parameters
		
		SearchParams params = new SearchParams();
		params.setPaginator(page);		
		params.setSorts(this.parseSorts(request));
		params.setFilter(this.parseRecombinaseQueryForm(query));

		// issue the query and get back the matching Allele objects
		
		SearchResults<Allele> searchResults = 
			recombinaseFinder.searchRecombinases(params);
		
		// convert the Alleles to their RecombinaseSummary wrappers, and put
		// them in the JsonSummaryResponse object

		List<RecombinaseSummary> summaries = new ArrayList<RecombinaseSummary> ();
		Iterator<Allele> it = searchResults.getResultObjects().iterator();
		while (it.hasNext()) {
			summaries.add(new RecombinaseSummary(it.next()));
		}

        JsonSummaryResponse<RecombinaseSummary> jsonResponse = 
        	new JsonSummaryResponse<RecombinaseSummary>();

        jsonResponse.setSummaryRows (summaries);
        jsonResponse.setTotalCount (searchResults.getTotalCount());
		
		return jsonResponse;
	}

	/*--------------------------*/
	/* private instance methods */
	/*--------------------------*/

	/** TODO : needs to be adjusted for alleles
	 * 
	 */
	private List<Sort> parseSorts(HttpServletRequest request) {
		
		List<Sort> sorts = new ArrayList<Sort>();
		
		String s = request.getParameter("sort");
		String d = request.getParameter("dir");
		boolean desc = false;
/*		
		if ("symbol".equalsIgnoreCase(s)){
			s = SortConstants.CRE_SYMBOL;
		} else if ("alleletype".equalsIgnoreCase(s)){
			s = SortConstants.CRE_TYPE;
		} else if ("induciblenote".equalsIgnoreCase(s)){
			s = SortConstants.CRE_INDUCIBLE;
		} else if ("countofreferences".equalsIgnoreCase(s)){
			s = SortConstants.CRE_REF_COUNT;
		} else if ("inalimentarysystem".equalsIgnoreCase(s)){
			s = SortConstants.CRE_IN_ALIMENTARY_SYSTEM;
		} else {
			s = SortConstants.CRE_DRIVER;
		}
*/
		if (s == null) { s = SortConstants.CRE_DRIVER; }
		
		if("desc".equalsIgnoreCase(d)){
			desc = true;
		}
		Sort sort = new Sort(s, desc);
		logger.debug ("sort: " + sort.toString());
		sorts.add(sort);
		return sorts;
	}

	private Filter parseRecombinaseQueryForm(RecombinaseQueryForm query){
		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();
		
		String driver = query.getDriver();
		String system = query.getSystem();
		
		// build driver query filter
		if ((driver != null) && (!"".equals(driver))) {
			filterList.add(new Filter (SearchConstants.ALL_DRIVER, driver, 
				Filter.OP_EQUAL));
		}
		
		// build system query filter
		if ((system != null) && (!"".equals(system))) {
			filterList.add(new Filter (SearchConstants.ALL_SYSTEM, system,
				Filter.OP_EQUAL));
		}
		
		// we do have some filters, build 'em and add to searchParams
		if (filterList.size() > 0){
			Filter f = new Filter();
			f.setFilterJoinClause(Filter.FC_AND);
			f.setNestedFilters(filterList);
			return f;
		// none yet, so check the id query and build it
		// } else if (query.getId() != null && !"".equals(query.getId())){
			//TODO -- do we need an ID query here?
			//List<String> ids = Arrays.asList(query.getId().split(";"));
			//return new Filter(SearchConstants.REF_ID, ids, Filter.OP_IN);
		} else {
			//TODO no query params
		}
		return new Filter();
	}
}
