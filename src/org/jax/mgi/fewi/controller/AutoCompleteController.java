package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import mgi.frontend.datamodel.GxdAssayResult;

import org.jax.mgi.fewi.finder.AutocompleteFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.summary.AutocompleteAuthorResult;
import org.jax.mgi.fewi.summary.GxdAssayResultSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.VocabACSummaryRow;
import org.jax.mgi.fewi.util.QueryParser;
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
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9']+"));
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
			f.setNestedFilters(fList,Filter.FC_AND);
		} else {
			f = new Filter(param, queries.get(0), Filter.OP_WORD_BEGINS);
		}
		
		if (forGXD) {
			List<Filter> finalList = new ArrayList<Filter>();
			finalList.add(f);
			Filter gxdClause = new Filter(SearchConstants.AC_FOR_GXD, "1", Filter.OP_EQUAL);
			finalList.add(gxdClause);
			
			Filter outerFilter = new Filter();
			outerFilter.setNestedFilters(finalList,Filter.FC_AND);
			
			params.setFilter(outerFilter);
		}
		else {
			params.setFilter(f);
		}
		
		logger.debug(params.getFilter().toString());
		
		return params;
	}

	
	/*
	 * This method maps requests for structure auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/structure")
	public @ResponseBody SearchResults<StructureACResult> structureAutoComplete(
			@RequestParam("query") String query) {
		// split input on any non-alpha characters
		Collection<String> words = QueryParser.parseAutoCompleteSearch(query);
		logger.debug("structure query:" + words.toString());
		
		if(words.size() == 0)
		{
			// return an empty result set;
			SearchResults<StructureACResult> sr = new SearchResults<StructureACResult>();
			sr.setTotalCount(0);
			return sr;
		}

		SearchParams params = new SearchParams();
		params.setPageSize(1000);
		
		Filter f = new Filter();
		List<Filter> fList = new ArrayList<Filter>();
		for (String q : words) {
			Filter wordFilter = new Filter(SearchConstants.STRUCTURE,q,Filter.OP_GREEDY_BEGINS);
			fList.add(wordFilter);
		}
		f.setNestedFilters(fList,Filter.FC_AND);
		
		params.setFilter(f);
		
		// default sorts are "score","autocomplete text"
		List<Sort> sorts = new ArrayList<Sort>();
		
		sorts.add(new Sort("score",true));
		sorts.add(new Sort(IndexConstants.STRUCTUREAC_BY_SYNONYM,false));
		params.setSorts(sorts);
		
		SearchResults<StructureACResult> results = autocompleteFinder.getStructureAutoComplete(params);
		// need a unique list of terms. 
		results.uniqueifyResultObjects();
		return results;
	}
	
	/*
	 * This method maps requests for vocab term auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/vocabTerm")
	public @ResponseBody JsonSummaryResponse<VocabACSummaryRow> vocabAutoComplete(
			@RequestParam("query") String query) {
		
		SearchResults<VocabACResult> results= this.getVocabAutoCompleteResults(query);
		List<VocabACSummaryRow> summaryRows = new ArrayList<VocabACSummaryRow>();
		
        for (VocabACResult result : results.getResultObjects()) {
			if (result != null){
				VocabACSummaryRow row = new VocabACSummaryRow(result,query);
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
		JsonSummaryResponse<VocabACSummaryRow> jsonResponse = makeJsonResponse(results,query);
		return jsonResponse;
	}

	
	public @ResponseBody SearchResults<VocabACResult> getVocabAutoCompleteResults(
			@RequestParam("query") String query) {
		
		// split input on any non-alpha characters
		Collection<String> words = QueryParser.parseAutoCompleteSearch(query);
		logger.debug("vocab term query:" + words.toString());

		SearchParams params = new SearchParams();
		params.setPageSize(500);
		
		Filter f = new Filter();
		List<Filter> fList = new ArrayList<Filter>();
		for (String q : words) {
			Filter termFilter = new Filter(SearchConstants.VOC_TERM,q,Filter.OP_GREEDY_BEGINS);
			fList.add(termFilter);
		}
		f.setNestedFilters(fList,Filter.FC_AND);
		
		params.setFilter(f);
		
		// default sorts are "score","termLength","term"
		List<Sort> sorts = new ArrayList<Sort>();
		
		sorts.add(new Sort("score",true));
		sorts.add(new Sort(IndexConstants.VOCABAC_TERM_LENGTH,false));
		sorts.add(new Sort(IndexConstants.VOCABAC_BY_TERM,false));
		sorts.add(new Sort(IndexConstants.VOCABAC_BY_ORIGINAL_TERM,false));
		params.setSorts(sorts);
		
		return autocompleteFinder.getVocabAutoComplete(params);
		
	}
	
	/*
	 * precompiles the regex pattern matchers and then builds the html formatted responses
	 */
	public JsonSummaryResponse<VocabACSummaryRow> makeJsonResponse(SearchResults<VocabACResult> results,String query)
	{
		List<VocabACSummaryRow> summaryRows = new ArrayList<VocabACSummaryRow>();
		
		// compile the regex patterns
		List<String> queryTokens = QueryParser.parseAutoCompleteSearch(query);
		List<Pattern> ps = new ArrayList<Pattern>();
		for(String token : queryTokens)
		{
			if(!token.equals("")) ps.add(Pattern.compile(token,Pattern.CASE_INSENSITIVE));
		}
		
		// build the html formatted summary objects
        for (VocabACResult result : results.getResultObjects()) {
			if (result != null){
				VocabACSummaryRow row = new VocabACSummaryRow(result,ps);
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
		JsonSummaryResponse<VocabACSummaryRow> jsonResponse = new JsonSummaryResponse<VocabACSummaryRow>();
		jsonResponse.setSummaryRows(summaryRows);       
		jsonResponse.setTotalCount(results.getTotalCount());
		
		return jsonResponse;
	}
}
