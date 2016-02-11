package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.antlr.BooleanSearch.BooleanSearch;
import org.jax.mgi.fewi.finder.AutocompleteFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.PrettyFilterPrinter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDriverACResult;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.summary.AutocompleteAuthorResult;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.VocabACSummaryRow;
import org.jax.mgi.fewi.summary.VocabACSummaryRow.ACType;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.CreFields;
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
	private final Logger logger = LoggerFactory.getLogger(AutoCompleteController.class);

	// get the finders used by various methods
	@Autowired
	private AutocompleteFinder autocompleteFinder;


	/*
	 * This method maps requests for driver auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/driver")
	public @ResponseBody SearchResults<SolrDriverACResult> driverAutoComplete(
			@RequestParam("query") String query) {

		// split input on any non-alpha characters
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9]"));
		logger.debug("driver query:" + words.toString());

		//build SearchParams for driver auto complete query
	    SearchParams params = new SearchParams();
		params.setPageSize(1000);
	    Filter f = new Filter();
	    List<Filter> fList = new ArrayList<Filter>();

	    for (String q : words) {
			Filter wordFilter = new Filter(SearchConstants.CRE_DRIVER, q.toLowerCase(),
			    Filter.Operator.OP_GREEDY_BEGINS);
			fList.add(wordFilter);
	    }
	    f.setNestedFilters(fList,Filter.JoinClause.FC_AND);
	    params.setFilter(f);

	    // sorts
	    List<Sort> sorts = new ArrayList<Sort>();
	    sorts.add(new Sort(CreFields.DRIVER, false));
	    params.setSorts(sorts);

		//return results
		return autocompleteFinder.getDriverAutoComplete(params);
	}

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
				fItem = new Filter(param, q, Filter.Operator.OP_WORD_BEGINS);
				fList.add(fItem);
			}
			f.setNestedFilters(fList,Filter.JoinClause.FC_AND);
		} else {
			f = new Filter(param, queries.get(0), Filter.Operator.OP_WORD_BEGINS);
		}

		if (forGXD) {
			List<Filter> finalList = new ArrayList<Filter>();
			finalList.add(f);
			Filter gxdClause = new Filter(SearchConstants.AC_FOR_GXD, "1", Filter.Operator.OP_EQUAL);
			finalList.add(gxdClause);

			Filter outerFilter = new Filter();
			outerFilter.setNestedFilters(finalList,Filter.JoinClause.FC_AND);
			params.setFilter(outerFilter);
		}
		else {
			params.setFilter(f);
		}

		logger.debug(params.getFilter().toString());

		return params;
	}

	/*
	 * This method handles requests for the EMAPA strucure autocomplete
	 * fields on the CRE query form.  Results are returned as JSON.
	 * TODO - move mgihome cre homepage to cre-specific controller url method
	 */
	@RequestMapping("/emapa")
	public @ResponseBody SearchResults<EmapaACResult> emapaAutoCompleteRequest(
		HttpServletResponse response,
		@RequestParam("query") String query)
	{
		logger.debug("autoCompleteController.emapaAutoCompleteRequest");
		AjaxUtils.prepareAjaxHeaders(response);
		return performEmapaAutoComplete(query);
	}

	/*
	 * This method handles requests for the EMAPA strucure autocomplete
	 * fields on the GXD query form.  Results are returned as JSON.
	 */
	@RequestMapping("/gxdEmapa")
	public @ResponseBody SearchResults<EmapaACResult> gxdEmapaAutoCompleteRequest(
		HttpServletResponse response,
		@RequestParam("query") String query)
	{
		logger.debug("autoCompleteController.gxdEmapaAutoCompleteRequest");
		AjaxUtils.prepareAjaxHeaders(response);
		return performGxdEmapaAutoComplete(query);
	}

	/*
	 * Duplicate of the above url for cre compatibility
	 * TODO - move mgihome cre homepage to cre-specific controller url method
	 */
	@RequestMapping("/structure")
	public @ResponseBody SearchResults<EmapaACResult> structureAutoCompleteRequest(
			HttpServletResponse response,
			@RequestParam("query") String query) {
		return emapaAutoCompleteRequest(response,query);
	}

	/* method for use by automated testing, to mimic the above method
	 * TODO - move to cre-specific method signature
	 */
	public @ResponseBody SearchResults<EmapaACResult> emapaAutoComplete(String query) {
		return performEmapaAutoComplete(query);
	}

	/* float any results that begins with the given query string up to the
	 * top of the results
	 *
	 * TODO: We need to find a way to do this in Solr. Not only is this convoluted and inconsistent,
	 * 	it likely won't do what is expected in every case. -kstone
	 */
	private SearchResults<EmapaACResult> floatBeginsMatches (String query,
	    SearchResults<EmapaACResult> searchResults) {
	    if (query == null) { return searchResults; }

	    ArrayList<EmapaACResult> begins = new ArrayList<EmapaACResult>();
	    ArrayList<EmapaACResult> other = new ArrayList<EmapaACResult>();
	    String queryLower = query.toLowerCase();

		for (EmapaACResult result : searchResults.getResultObjects()) {
			if (result.getStructure().toLowerCase().startsWith(queryLower)) {
			    begins.add(result);
			} else {
				other.add(result);
			}
		}

	    begins.addAll(other);
	    searchResults.setResultObjects(begins);
	    return searchResults;
	}

	/* wrapper for CRE EMAPA autocomplete search
	 */
	private SearchResults<EmapaACResult> performEmapaAutoComplete(String query)
	{
	    // split input on any non-alpha characters
	    Collection<String> words =
		QueryParser.parseAutoCompleteSearch(query);

	    logger.debug("structure query:" + words.toString());

	    // if no query string, return an empty result set

	    if(words.size() == 0) {
			SearchResults<EmapaACResult> sr = new SearchResults<EmapaACResult>();
			sr.setTotalCount(0);
			return sr;
	    }

	    // otherwise, do the search and request the top 200 matches
	    SearchParams params = new SearchParams();
	    params.setPageSize(200);

	    Filter f = new Filter();
	    List<Filter> fList = new ArrayList<Filter>();

	    // build an AND-ed list of tokens for BEGINS searching in fList

	    for (String q : words) {
			Filter wordFilter = new Filter(SearchConstants.STRUCTURE, q,
			    Filter.Operator.OP_GREEDY_BEGINS);
			fList.add(wordFilter);
	    }

	    f.setNestedFilters(fList,Filter.JoinClause.FC_AND);

	    params.setFilter(f);

	    // default sorts are "score","autocomplete text"
	    List<Sort> sorts = new ArrayList<Sort>();

	    sorts.add(new Sort("score", true));
	    sorts.add(new Sort(IndexConstants.STRUCTUREAC_BY_SYNONYM, false));
	    params.setSorts(sorts);

	    SearchResults<EmapaACResult> results = autocompleteFinder.getEmapaAutoComplete(params);

	    // need a unique list of terms.
	    results.uniqueifyResultObjects();

	    results = floatBeginsMatches(query, results);
	    return results;
	}

	/* Wrapper for GXD EMAPA autocomplete search.
	 * Shared by gxdEmapa & automated testing
	 */
	private SearchResults<EmapaACResult> performGxdEmapaAutoComplete(String query)
	{
	    // split input on any non-alpha characters
	    Collection<String> words =
		QueryParser.parseAutoCompleteSearch(query);

	    logger.debug("structure query:" + words.toString());

	    // if no query string, return an empty result set
	    if(words.size() == 0) {
			SearchResults<EmapaACResult> sr = new SearchResults<EmapaACResult>();
			sr.setTotalCount(0);
			return sr;
	    }

	    // otherwise, do the search and request the top 200 matches
	    SearchParams params = new SearchParams();
	    params.setPageSize(200);

	    Filter f = new Filter();
	    List<Filter> fList = new ArrayList<Filter>();

	    // build an AND-ed list of tokens for BEGINS searching in fList
	    for (String q : words) {
			Filter wordFilter = new Filter(SearchConstants.STRUCTURE, q,
			    Filter.Operator.OP_GREEDY_BEGINS);
			fList.add(wordFilter);
	    }
	    f.setNestedFilters(fList,Filter.JoinClause.FC_AND);
	    params.setFilter(f);

	    // default sorts are "score","autocomplete text"
	    List<Sort> sorts = new ArrayList<Sort>();
	    sorts.add(new Sort("score", true));
	    sorts.add(new Sort(IndexConstants.STRUCTUREAC_BY_SYNONYM, false));
	    params.setSorts(sorts);

	    SearchResults<EmapaACResult> results = autocompleteFinder.getGxdEmapaAutoComplete(params);
	    // need a unique list of terms.
	    results.uniqueifyResultObjects();

	    results = floatBeginsMatches(query, results);
	    return results;
	}

	/*
	 * This method maps requests for vocab term auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/vocabTerm")
	public @ResponseBody JsonSummaryResponse<VocabACSummaryRow> vocabAutoComplete(
			@RequestParam("query") String query) {

		SearchResults<VocabACResult> results= getVocabAutoCompleteResults(query);
		//List<VocabACSummaryRow> summaryRows = new ArrayList<VocabACSummaryRow>();
//
//        for (VocabACResult result : results.getResultObjects()) {
//			if (result != null){
//				VocabACSummaryRow row = new VocabACSummaryRow(result,query,ACType.GXD);
//				summaryRows.add(row);
//			} else {
//				logger.debug("--> Null Object");
//			}
//		}
		JsonSummaryResponse<VocabACSummaryRow> jsonResponse = makeJsonResponse(results,query,ACType.GXD);
		return jsonResponse;
	}

	//autocomplete for the disease portal phenotypes query
	@RequestMapping("/diseasePortal/phenotypes")
	public @ResponseBody JsonSummaryResponse<VocabACSummaryRow> vocabHDPPhenotypesAutoComplete(
			HttpServletResponse response,
			@RequestParam("query") String query) {

		// filter specific vocabs for this autocomplete
		Filter vocabFilter = new Filter(SearchConstants.VOC_VOCAB,Arrays.asList("OMIM","Mammalian Phenotype"),Filter.Operator.OP_IN);
		SearchResults<VocabACResult> results = getVocabAutoCompleteResults(query,Arrays.asList(vocabFilter));
		new ArrayList<VocabACSummaryRow>();

//        for (VocabACResult result : results.getResultObjects()) {
//			if (result != null){
//				VocabACSummaryRow row = new VocabACSummaryRow(result,query,ACType.DISEASE_PORTAL);
//				summaryRows.add(row);
//			} else {
//				logger.debug("--> Null Object");
//			}
//		}


		AjaxUtils.prepareAjaxHeaders(response);
		JsonSummaryResponse<VocabACSummaryRow> jsonResponse = makeJsonResponse(results,query,ACType.DISEASE_PORTAL);
		return jsonResponse;
	}

	public SearchResults<VocabACResult> getVocabAutoCompleteResults(String query)
	{
		return getVocabAutoCompleteResults(query,new ArrayList<Filter>());
	}
	public SearchResults<VocabACResult> getVocabAutoCompleteResults(String query,List<Filter> additionalFilters)
	{
		// split input on any non-alpha characters
		Collection<String> words = QueryParser.parseAutoCompleteSearch(query);
		logger.debug("vocab term query:" + words.toString());

		SearchParams params = new SearchParams();
		params.setPageSize(100);

		List<Filter> fList = new ArrayList<Filter>();
		for (String q : words) {
			Filter termFilter = new Filter(SearchConstants.VOC_TERM,q,Filter.Operator.OP_GREEDY_BEGINS);
			fList.add(termFilter);
		}
		if(additionalFilters!=null) fList.addAll(additionalFilters);

		Filter f = Filter.and(fList);

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
	public JsonSummaryResponse<VocabACSummaryRow> makeJsonResponse(SearchResults<VocabACResult> results,String query,ACType formatType)
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
				VocabACSummaryRow row = new VocabACSummaryRow(result,ps,formatType);
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

	/*
	 *  resolves a list of IDs into their matching terms
	 *  I.e. you pass in a comma or whitespace separated list of IDs (and terms).
	 *  	Any token, matching an ID, will be replaced with the term it matches.
	 */
	@RequestMapping("/vocabTerm/resolve")
	public @ResponseBody HashMap<String, String> resolveVocabTermId(@RequestParam("ids") String ids) {

		/*
		 *  Honestly, I am too lazy to figure out how to get StringUtils.replaceEach() to do case-insensitive matching (which I don't think it does).
		 *  	So, to satisfy the curators who feel the need to have lowercase MP IDs resolved in "you searched for" text,
		 *  		which can only come through users (not autocomplete),
		 *  		I am putting in this hack.
		 *  	-kstone
		 */
		BooleanSearch bs = new BooleanSearch();
		ids = ids.replaceAll("mp:","MP:");
		ids = bs.sanitizeInput(ids.replace(",", " "));

		HashMap<String, String> ret = new HashMap<String, String>();

		SearchResults<VocabACResult> results = resolveVocabTermIdQuery(ids);

		if(results==null) {
			ret.put("ids", ids);
			return ret;
		}

		// if there are any hits, map them here
		Set<String> duplicates = new HashSet<String>();
		// build parallel lists of search -> replacement strings
		String[] searchList = new String[results.getTotalCount()];
		String[] replacementList = new String[results.getTotalCount()];
		int index = 0;
		for(VocabACResult result : results.getResultObjects())
		{
			String termId = result.getTermId();
			if(duplicates.contains(termId)) continue;
			duplicates.add(termId);

			String replacement = result.getOriginalTerm() + " - " + result.getTermId();
			replacementList[index] = replacement;
			searchList[index] = result.getTermId();
			index++;
		}

		Filter f = bs.buildSolrFilter(SearchConstants.VOC_TERM, ids);

		if(f != null) {
			PrettyFilterPrinter pfp = new PrettyFilterPrinter();
			f.Accept(pfp);
			String out = pfp.generateOutput();
			logger.info("Modified Query String: " + out);
			ret.put("ids", StringUtils.replaceEach(out,searchList,replacementList));
		} else {
			f = generateTermFilter(ids);
			PrettyFilterPrinter pfp = new PrettyFilterPrinter();
			f.Accept(pfp);
			String out = pfp.generateOutput();
			logger.info("Modified Query due to Error: " + ids);
			ret.put("ids", StringUtils.replaceEach(out,searchList,replacementList));
			ret.put("error", bs.getHtmlErrorString());
		}
		return ret;
	}

	private Filter generateTermFilter(String ids){
		List<String> idTokens = QueryParser.tokeniseOnWhitespaceAndComma(ids);
		List<Filter> filters = new ArrayList<Filter>();
		if(idTokens.size()>0)
		{
			List<Filter> idFilters = new ArrayList<Filter>();
			for(String idToken : idTokens)
			{
				idFilters.add(new Filter(SearchConstants.VOC_TERM_ID,idToken,Filter.Operator.OP_EQUAL));
			}
			filters.add(Filter.or(idFilters));
		}
		if(filters.size() > 0) {
			return Filter.or(filters);
		}
		return null;
	}

	private SearchResults<VocabACResult> resolveVocabTermIdQuery(String ids) {
		// filter specific vocabs for this autocomplete

		List<String> idTokens = new ArrayList<String>(Arrays.asList(ids.split("[\\s,]+|\\(|\\)")));

		// Not a construct for the newb.
		while(idTokens.remove(""));

		List<Filter> filters = new ArrayList<Filter>();

		if(idTokens.size()>0)
		{
			List<Filter> idFilters = new ArrayList<Filter>();
			for(String idToken : idTokens)
			{
				idFilters.add(new Filter(SearchConstants.VOC_TERM_ID,idToken,Filter.Operator.OP_EQUAL));
			}
			filters.add(Filter.or(idFilters));
		}
		if(filters.size()<=0) return null; // do nothing if we have no filters

		SearchParams params = new SearchParams();
		params.setFilter(Filter.and(filters));
		params.setPageSize(10000); // set to a high number to ensure we get all the results

		SearchResults<VocabACResult> results = autocompleteFinder.getVocabAutoComplete(params);
		return results;
	}

	/*
	 * resolve emapaIDs to their names
	 * assumes a format of "EMAPA:1234,EMAPA:56789,..."
	 * returns "brain,lung,..."
	 */
	@RequestMapping("/emapaID/resolve")
	public @ResponseBody List<String> resolveEmapaIdList(
			@RequestParam("ids") String ids)
	{
		List<String> returnValues = new ArrayList<String>();
		List<String> idTokens = QueryParser.tokeniseOnWhitespaceAndComma(ids);
		SearchResults<EmapaACResult> results = resolveEmapaIds(idTokens);

		// make a lookup of IDs to names
		Map<String,String> idToNameMap = new HashMap<String,String>();
		for(EmapaACResult result : results.getResultObjects())
		{
			idToNameMap.put(result.getAccID(),result.getStructure());
		}
		for(String idToken : idTokens)
		{
			if(idToNameMap.containsKey(idToken))
			{
				returnValues.add(idToNameMap.get(idToken));
			}
			else
			{
				logger.debug("emapaID/resolve-> could not map "+idToken+" to an emapa term");
				returnValues.add(idToken);
			}
		}

		return returnValues;
	}

	private SearchResults<EmapaACResult> resolveEmapaIds(List<String> idTokens)
	{
		List<Filter> filters = new ArrayList<Filter>();

		if(idTokens.size()>0)
		{
			List<Filter> idFilters = new ArrayList<Filter>();
			for(String idToken : idTokens)
			{
				idFilters.add(new Filter(SearchConstants.ACC_ID,idToken,Filter.Operator.OP_EQUAL));
			}
			filters.add(Filter.or(idFilters));
		}
		if(filters.size()<=0) return null; // do nothing if we have no filters

		SearchParams params = new SearchParams();
		params.setFilter(Filter.and(filters));
		params.setPageSize(10000); // set to a high number to ensure we get all the results

		SearchResults<EmapaACResult> results = autocompleteFinder.getGxdEmapaAutoComplete(params);
		return results;
	}
}
