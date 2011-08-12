package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.finder.AutocompleteFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.AutocompleteAuthorResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.shr.fe.IndexConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * This controller maps all /autocomplete/ uri's
 */
@Controller
@RequestMapping(value="/autocomplete")
public class AutoCompleteController {
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(AutoCompleteController.class);
	
	// get the finders used by various methods
	@Autowired
	private AutocompleteFinder autocompleteFinder;		

	
	/*
	 * This method maps requests for author auto complete results.  The results
	 * are returned as JSON.
	 */
	@RequestMapping("/author/gxd")
	public @ResponseBody JsonSummaryResponse<AutocompleteAuthorResult> authorAutoCompleteForGXD(
			@RequestParam("query") String query) {
		// split input on any non-alpha and non-apostrophe characters
		List<String> words = 
			Arrays.asList(query.trim().split("[^a-zA-Z0-9']+"));
		logger.debug("author query:" + words.toString());
		//build SearchParams for author auto complete query
		SearchParams params = buildACQuery(SearchConstants.REF_AUTHOR, words, true);
		params.setIncludeRowMeta(true);
		params.setIncludeGenerated(true);
		
		JsonSummaryResponse<AutocompleteAuthorResult> r = new JsonSummaryResponse<AutocompleteAuthorResult>();
		List<AutocompleteAuthorResult> retResults = new ArrayList<AutocompleteAuthorResult>();
		SearchResults<String> qResults = autocompleteFinder.getAuthorAutoComplete(params);
		
		Map<String, MetaData> meta = qResults.getMetaMapping();
		for (String s: qResults.getResultStrings()) {
			retResults.add(new AutocompleteAuthorResult(s, meta.get(s).isGenerated()));
			logger.debug(s + ": " + meta.get(s).isGenerated());
		}
		r.setSummaryRows(retResults);
		// return results
		return r;
	}
	
	/*
	 * This method maps requests for author auto complete results.  The results
	 * are returned as JSON.
	 */
	@RequestMapping("/author")
	public @ResponseBody JsonSummaryResponse<AutocompleteAuthorResult> authorAutoComplete(
			@RequestParam("query") String query) {
		// split input on any non-alpha and non-apostrophe characters
		List<String> words = 
			Arrays.asList(query.trim().split("[^a-zA-Z0-9']+"));
		logger.debug("author query:" + words.toString());
		//build SearchParams for author auto complete query
		SearchParams params = buildACQuery(SearchConstants.REF_AUTHOR, words, false);
		params.setIncludeRowMeta(true);
		params.setIncludeGenerated(true);
		
		JsonSummaryResponse<AutocompleteAuthorResult> r = new JsonSummaryResponse<AutocompleteAuthorResult>();
		List<AutocompleteAuthorResult> retResults = new ArrayList<AutocompleteAuthorResult>();
		SearchResults<String> qResults = autocompleteFinder.getAuthorAutoComplete(params);
		
		Map<String, MetaData> meta = qResults.getMetaMapping();
		for (String s: qResults.getResultStrings()) {
			retResults.add(new AutocompleteAuthorResult(s, meta.get(s).isGenerated()));
			logger.debug(s + ": " + meta.get(s).isGenerated());
		}
		r.setSummaryRows(retResults);
		// return results
		return r;
	}
	
	/*
	 * This method maps requests for journal auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/journal/gxd")
	public @ResponseBody SearchResults<String> journalAutoCompleteForGXD(
			@RequestParam("query") String query) {
		// split input on any non-alpha characters
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9]"));
		logger.debug("journal query:" + words.toString());
		//build SearchParams for journal auto complete query
		SearchParams params = buildACQuery(SearchConstants.REF_JOURNAL, words, true);
		//return results
		return autocompleteFinder.getJournalAutoComplete(params);
	}
	
	/*
	 * This method maps requests for journal auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/journal")
	public @ResponseBody SearchResults<String> journalAutoComplete(
			@RequestParam("query") String query) {
		// split input on any non-alpha characters
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9]"));
		logger.debug("journal query:" + words.toString());
		//build SearchParams for journal auto complete query
		SearchParams params = buildACQuery(SearchConstants.REF_JOURNAL, words, false);
		//return results
		return autocompleteFinder.getJournalAutoComplete(params);
	}

	/*
	 * This is a helper method that takes a List of Strings and generates a  
	 * SearchParams object containing the appropriate Filter objects AND'ed 
	 * together for the requested Auto Complete query.  
	 */
	private SearchParams buildACQuery(String param, List<String> queries, Boolean forGXD){
		Filter f;
		SearchParams params = new SearchParams();
		params.setPageSize(1000);
		List<Sort> sorts = new ArrayList<Sort>();
		Sort s = new Sort(IndexConstants.REF_AUTHOR_SORT, false);
		if (param.equals(SearchConstants.REF_JOURNAL)) {
			s = new Sort(SortConstants.REF_JOURNAL_AC, false);
		}
		sorts.add(s);
		params.setSorts(sorts);
		
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
		
		if (forGXD) {
			List<Filter> finalList = new ArrayList<Filter>();
			finalList.add(f);
			Filter gxdClause = new Filter(SearchConstants.AC_FOR_GXD, "1", Filter.OP_EQUAL);
			finalList.add(gxdClause);
			
			Filter outerFilter = new Filter();
			outerFilter.setNestedFilters(finalList);
			outerFilter.setFilterJoinClause(Filter.FC_AND);
			
			params.setFilter(outerFilter);
		}
		else {
			params.setFilter(f);
		}
		
		logger.debug(params.getFilter().toString());
		
		return params;
	}
	
}
