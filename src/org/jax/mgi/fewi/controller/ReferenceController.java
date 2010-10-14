package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
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

/*
 * This controller maps all /reference/ uri's
 */
@Controller
@RequestMapping(value="/reference")
public class ReferenceController {
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ReferenceController.class);
	
	// get the finder to use for various methods
	@Autowired
	private ReferenceFinder referenceFinder;

	// add a new ReferenceQueryForm and MySirtPaginator objects to model for QF 
	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model) {
		model.addAttribute(new ReferenceQueryForm());
		model.addAttribute("sort", new Paginator());
		return "reference_query";
	}
	
	// qf submission.  drop completed ReferenceQueryForm object and query string
	// into model for summary to use
	@RequestMapping("/summary")
	public String referenceSummary(HttpServletRequest request, Model model,
			@ModelAttribute ReferenceQueryForm queryForm) {

		model.addAttribute("referenceQueryForm", queryForm);
		model.addAttribute("queryString", request.getQueryString());
		logger.debug("queryString: " + request.getQueryString());

		return "reference_summary";
	}
	
	// this is the logis to perform the query and retur json results
	@RequestMapping("/json")
	public @ResponseBody SearchResults<Reference> referenceSummaryJson(
			HttpServletRequest request, 
			@ModelAttribute ReferenceQueryForm query,
			@ModelAttribute Paginator page) {
				
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();
		params.setPaginator(page);		
		params.setSorts(this.parseSorts(request));
		params.setFilter(this.parseReferenceQueryForm(query));

		// perform query and return results as json
		logger.debug("params parsed");
		return referenceFinder.searchReferences(params);
	}
	
	private Filter parseReferenceQueryForm(ReferenceQueryForm query){
		// start filter list for query filters
		List<Filter> queryList = new ArrayList<Filter>();
		// start filter list to store facet filters
		List<Filter> facetList = new ArrayList<Filter>();
		
		//build author query filter
		if(query.getAuthor() != null && !"".equals(query.getAuthor())){
			List<String> authors = Arrays.asList(query.getAuthor().trim().split(";"));	
			
			String scope = query.getAuthorScope();
			if ("first".equals(scope)){
				queryList.add(new Filter(SearchConstants.REF_AUTHOR_FIRST, authors, Filter.OP_IN));
			} else if ("last".equals(scope)){
				queryList.add(new Filter(SearchConstants.REF_AUTHOR_LAST, authors, Filter.OP_IN));
			} else {
				queryList.add(new Filter(SearchConstants.REF_AUTHOR_ANY, authors, Filter.OP_IN));
			}
		}

		// build journal query filter
		if(query.getJournal() != null && !"".equals(query.getJournal())){
			List<String> journals = Arrays.asList(query.getJournal().trim().split(";"));
			queryList.add(new Filter(SearchConstants.REF_JOURNAL, journals, Filter.OP_IN));
		}
		
		// build year query filter
		String year = query.getYear().trim();
		if(year != null && !"".equals(year)){
			int rangeLoc = year.indexOf("-");
			if(rangeLoc > -1){
				// TODO validate years are numbers
				List<String> years = Arrays.asList(query.getAuthor().trim().split("-"));
				if (years.size() == 2){
					// TODO handle years in any order
					queryList.add(new Filter(SearchConstants.REF_YEAR, years.get(0), Filter.OP_GREATER_OR_EQUAL));
					queryList.add(new Filter(SearchConstants.REF_YEAR, years.get(1), Filter.OP_LESS_OR_EQUAL));
				} else if (years.size() == 1) {
					if (rangeLoc == 0){
						queryList.add(new Filter(SearchConstants.REF_YEAR, years.get(0), Filter.OP_LESS_OR_EQUAL));
					} else {
						queryList.add(new Filter(SearchConstants.REF_YEAR, years.get(0), Filter.OP_GREATER_OR_EQUAL));
					}
				}
				// TODO error: too many years entered
			} else {
				queryList.add(new Filter(SearchConstants.REF_YEAR, year, Filter.OP_EQUAL));
			}
		}
		
		// build text query filter
		if(query.getText() != null && !"".equals(query.getText())){
			Filter tf = new Filter();
			List<Filter> textFilters = new ArrayList<Filter>();
			
			String text = query.getText();
			if(query.isInAbstract()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_ABSTRACT, text, Filter.OP_CONTAINS));
			}
			if(query.isInTitle()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_TITLE, text, Filter.OP_CONTAINS));
			}
			if (textFilters.size() == 1) {
				queryList.add(textFilters.get(0));
			} else {
				tf.setFilterJoinClause(Filter.FC_OR);
				tf.setNestedFilters(textFilters);
				queryList.add(tf);
			}
		}
		
		if(query.getAuthorFilter().size() > 0){
			facetList.add(new Filter(FacetConstants.REF_AUTHORS, 
					query.getAuthorFilter(), Filter.OP_IN));
		}
		
		if (query.getJournalFilter().size() > 0){
			facetList.add(new Filter(FacetConstants.REF_JOURNALS, 
					query.getJournalFilter(), Filter.OP_IN));
		}
		
		if (query.getYearFilter().size() > 0){
			List<String> years = new ArrayList<String>();
			for (Integer yearString : query.getYearFilter()) {
				years.add(String.valueOf(yearString));
			}
			facetList.add(new Filter(FacetConstants.REF_YEAR, 
					years, Filter.OP_IN));
		}
		
		if (query.getCuratedDataFilter().size() > 0){
			facetList.add(new Filter(FacetConstants.REF_CURATED_DATA, 
					query.getCuratedDataFilter(), Filter.OP_IN));
		}
		
		// we do have some filters, build 'em and add to searchParams
		if (queryList.size() > 0){
			Filter f = new Filter();
			f.setFilterJoinClause(Filter.FC_AND);
			queryList.addAll(facetList);
			f.setNestedFilters(queryList);
			return f;
		// none yet, so check the id query and build it
		} else if (query.getId() != null && !"".equals(query.getId())){
			List<String> ids = Arrays.asList(query.getId().split(";"));
			facetList.add(new Filter(SearchConstants.REF_ID, ids, Filter.OP_IN));
			//TODO filter 'PMID:' from ids
			Filter f = new Filter();
			f.setFilterJoinClause(Filter.FC_AND);
			f.setNestedFilters(facetList);
			return f;
		} else {
			//TODO no query params
		}
		
		return new Filter();
	}
	
	private List<Sort> parseSorts(HttpServletRequest request) {
		
		List<Sort> sorts = new ArrayList<Sort>();
		
		String s = request.getParameter("sort");
		String d = request.getParameter("dir");
		boolean desc = false;
		
		if ("authors".equalsIgnoreCase(s)){
			s = SortConstants.REF_AUTHORS;
		} else if ("journal".equalsIgnoreCase(s)){
			s = SortConstants.REF_JOURNAL;
		} else {
			s = SortConstants.REF_YEAR;
		}
		
		if("desc".equalsIgnoreCase(d)){
			desc = true;
		}
		Sort sort = new Sort(s, desc);
		
		sorts.add(sort);
		return sorts;
	}

	// mapping for author autocomplete requests
	@RequestMapping("/autocomplete/author")
	public @ResponseBody SearchResults<String> authorAutoComplete(@RequestParam("query") String query) {
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9']+"));
		logger.debug("author query:" + words.toString());
		SearchParams params = buildACQuery(SearchConstants.REF_AUTHOR, words);
		SearchResults<String> results = referenceFinder.getAuthorAutoComplete(params);
		return results;
	}
	
	// mapping for journal autocomplete requests
	@RequestMapping("/autocomplete/journal")
	public @ResponseBody SearchResults<String> journalAutoComplete(@RequestParam("query") String query) {
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9]"));
		logger.debug("journal query:" + words.toString());
		SearchParams params = buildACQuery(SearchConstants.REF_JOURNAL, words);
		SearchResults<String> results = referenceFinder.getJournalAutoComplete(params);
		return results;
	}

	// convenience method to build SearchParams object for autocomplete queries 
	private SearchParams buildACQuery(String param, List<String> queries){
		Filter f;
		SearchParams params = new SearchParams();
		params.setPageSize(1000);
		
		if (queries.size() > 1){
			f = new Filter();
			List<Filter> fList = new ArrayList<Filter>();
			Filter fItem;
			for (String q : queries) {
				fItem = new Filter(param, q, Filter.OP_WORD_BEGINS);
				fList.add(fItem);
			}
			f.setNestedFilters(fList);
			f.setFilterJoinClause(Filter.FC_AND);
		} else {
			f = new Filter(param, queries.get(0), Filter.OP_WORD_BEGINS);
		}
		params.setFilter(f);
		return params;
	}
	
	// mapping for author autocomplete requests
	@RequestMapping("/facet/author")
	public @ResponseBody SearchResults<String> facetAuthor(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.info(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return referenceFinder.getAuthorFacet(params);
	}
	
	@RequestMapping("/facet/journal")
	public @ResponseBody SearchResults<String> facetJournal(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.info(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return referenceFinder.getJournalFacet(params);
	}
	
	@RequestMapping("/facet/year")
	public @ResponseBody SearchResults<String> facetYear(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.info(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return referenceFinder.getYearFacet(params);
	}
	
	@RequestMapping("/facet/data")
	public @ResponseBody SearchResults<String> facetData(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.info(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return referenceFinder.getDataFacet(params);
	}
}
