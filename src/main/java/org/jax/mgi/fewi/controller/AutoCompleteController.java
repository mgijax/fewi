package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.finder.AutocompleteFinder;
import org.jax.mgi.fewi.finder.StrainFinder;
import org.jax.mgi.fewi.searchUtil.AutocompleteResult;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
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
import org.jax.mgi.fewi.summary.VocabBrowserACResult;
import org.jax.mgi.fewi.summary.VocabBrowserSearchResult;
import org.jax.mgi.fewi.util.ACHelper;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.CreFields;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
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

	@Autowired
	private StrainFinder strainFinder;

	@Autowired
	private StrainController strainController;

	// get a vocabulary controller
	@Autowired
	private VocabularyController vocabController;

	// provides in-memory searching of EMAPA structures, so we're not dependent on Solr for performance
	private static ACHelper emapaHelper = null;
	
	/*
	 * This method maps requests for strain auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/strainName")
	public @ResponseBody SearchResults<AutocompleteResult> strainAutoComplete(
			HttpServletResponse response,
			@RequestParam("query") String query,
			@RequestParam(value="tag", required=false) String tag) {

		int maxCount = 500;
		
		Filter tagFilter = null;
		if (tag != null) {
			tagFilter = new Filter(SearchConstants.STRAIN_TAGS, tag.trim(), Filter.Operator.OP_EQUAL);
		}
		
		// get exact matches
		SearchParams exact = new SearchParams();
		exact.setPageSize(maxCount);
		List<Filter> exactFilters = new ArrayList<Filter>();
		exactFilters.add(new Filter(SearchConstants.STRAIN_NAME_LOWER, query.toLowerCase(), Filter.Operator.OP_EQUAL));
		if (tagFilter != null) exactFilters.add(tagFilter);
		exact.setFilter(Filter.and(exactFilters));
		exact.setSorts(strainController.genSorts(null));
		SearchResults<SimpleStrain> srExact = strainFinder.getStrains(exact);
		List<SimpleStrain> exactMatches = srExact.getResultObjects();

		// exclude exact matches from the 'begins' set
		SearchParams begins = new SearchParams();
		begins.setPageSize(maxCount - exactMatches.size());
		List<Filter> beginsFilters = new ArrayList<Filter>();
		beginsFilters.add(new Filter(SearchConstants.STRAIN_NAME_LOWER, query.toLowerCase(), Filter.Operator.OP_GREEDY_BEGINS) );
		beginsFilters.add(Filter.notEqual(SearchConstants.STRAIN_NAME_LOWER, query.toLowerCase()));
		if (tagFilter != null) beginsFilters.add(tagFilter);
		begins.setFilter(Filter.and(beginsFilters));
		begins.setSorts(strainController.genSorts(null));
		SearchResults<SimpleStrain> srBegins = strainFinder.getStrains(begins);
		List<SimpleStrain> beginsMatches = srBegins.getResultObjects();

		// exclude exact matches and begins matches from the 'contains' set
		SearchParams contains = new SearchParams();
		contains.setPageSize(maxCount - exactMatches.size() - beginsMatches.size());
		List<Filter> containsFilters = new ArrayList<Filter>();
		containsFilters.add(new Filter(SearchConstants.STRAIN_NAME_LOWER, query.toLowerCase(), Filter.Operator.OP_STRING_CONTAINS) );
		containsFilters.add(new Filter(SearchConstants.STRAIN_NAME_LOWER, query.toLowerCase(), Filter.Operator.OP_GREEDY_BEGINS) );
		containsFilters.get(1).negate();
		if (tagFilter != null) containsFilters.add(tagFilter);
		contains.setFilter(Filter.and(containsFilters));
		contains.setSorts(strainController.genSorts(null));
		SearchResults<SimpleStrain> srContains = strainFinder.getStrains(contains);
		List<SimpleStrain> containsMatches = srContains.getResultObjects();

		// combine the lists
		
		exactMatches.addAll(beginsMatches);
		exactMatches.addAll(containsMatches);
				
		String lowerQuery = query.toLowerCase();
		List<AutocompleteResult> results = new ArrayList<AutocompleteResult>();
		for (SimpleStrain strain : exactMatches) {
			AutocompleteResult result = new AutocompleteResult(strain.getName());
			
			if (strain.getName().toLowerCase().indexOf(lowerQuery) < 0) {
				if (strain.getSynonyms() != null) {
					for (String synonym : strain.getSynonyms()) {
						if (synonym.toLowerCase().indexOf(lowerQuery) >= 0) {
							result.setSynonym(synonym);
							break;
						}
					}
				}
			}
			results.add(result);
		}
		logger.debug("Prepared " + results.size() + " strain AC results");
//		AjaxUtils.prepareAjaxHeaders(response);
//		logger.debug("Prepared Ajax Headers");
		SearchResults<AutocompleteResult> out = new SearchResults<AutocompleteResult>();
		out.cloneFrom(srContains);
		out.setResultObjects(results);
		out.setTotalCount(exactMatches.size());
		logger.debug("Exiting method");
		return out;
	}

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
	 * This method handles requests for the EMAPA structure autocomplete
	 * fields on the CRE query form.  Results are returned as JSON.
	 * move mgihome cre homepage to cre-specific controller url method
	 */
	@RequestMapping("/emapa")
	public @ResponseBody SearchResults<EmapaACResult> emapaAutoCompleteRequest(
			HttpServletResponse response,
			@RequestParam("query") String query) {
		logger.debug("autoCompleteController.emapaAutoCompleteRequest");
		AjaxUtils.prepareAjaxHeaders(response);
		return performEmapaAutoComplete(query);
	}

	/*
	 * This method handles requests for the EMAPA structure autocomplete
	 * fields on the GXD query form.  Results are returned as JSON.
	 */
	@RequestMapping("/gxdEmapa")
	public @ResponseBody SearchResults<EmapaACResult> gxdEmapaAutoCompleteRequest(
			HttpServletResponse response,
			@RequestParam("query") String query,
			@RequestParam(value="field", required=false) String field) {
		logger.debug("autoCompleteController.gxdEmapaAutoCompleteRequest");
		AjaxUtils.prepareAjaxHeaders(response);
		return performGxdEmapaAutoComplete(query,field);
	}

	/*
	 * Duplicate of the above url (/emapa) for cre compatibility
	 * move mgihome cre homepage to cre-specific controller url method
	 */
	@RequestMapping("/structure")
	public @ResponseBody SearchResults<EmapaACResult> structureAutoCompleteRequest(
			HttpServletResponse response,
			@RequestParam("query") String query) {
		return emapaAutoCompleteRequest(response,query);
	}

	/* method for use by automated testing, to mimic the above method
	 * move to cre-specific method signature
	 */
	public @ResponseBody SearchResults<EmapaACResult> emapaAutoComplete(String query) {
		return performEmapaAutoComplete(query);
	}

	/* float any results that begins with the given query string up to the
	 * top of the results
	 *
	 *  We need to find a way to do this in Solr. Not only is this convoluted and inconsistent,
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

	/* wrapper for GXD EMAPA autocomplete search
	 */
	private SearchResults<EmapaACResult> performGxdEmapaAutoComplete(String query, String field)
	{
		if (emapaHelper == null) {
			logger.info("Initializing: emapaHelper is null");

			// First call, so need to populate the ACHelper's data using all records from the Solr index.
			SearchParams params = new SearchParams();
			params.setPageSize(15000);
			params.setFilter(new Filter(SearchConstants.STRUCTURE, "*", Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED));
			
			SearchResults<EmapaACResult> results = autocompleteFinder.getGxdEmapaAutoComplete(params);
			logger.info("Got " + results.getTotalCount() + " docs from Solr");

			emapaHelper = new ACHelper();
			emapaHelper.setEmapaACResults(results.getResultObjects());
			logger.info("Processed docs in emapaHelper");
		}
		List<EmapaACResult> resultList = removeDuplicates(emapaHelper.asEmapaACResults(emapaHelper.search(query, 200, field)));
		SearchResults<EmapaACResult> searchResults = new SearchResults<EmapaACResult>();
		searchResults.setResultObjects(resultList);
		searchResults.setTotalCount(resultList.size());

		return searchResults;
	}

        /* Since I cannot seem to untangle what's going on with ACHelper, I'm just going to take a sledgehammer to
         * the duplicates issue and remove them after the fact. 
         * (see WTS2-942 and WTS2-1101).
         */
        private List<EmapaACResult> removeDuplicates (List<EmapaACResult> resultList) {
                Set<String> seen = new HashSet<String>();
                List<EmapaACResult> newList = new ArrayList<EmapaACResult>();
                for (int i = 0; i < resultList.size(); i++) {
                    EmapaACResult r = resultList.get(i);
                    if (!seen.contains(r.getAccID())) {
                        seen.add(r.getAccID());
                        newList.add(r);
                    }
                }
                return newList;
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

	/*
	 * This method maps requests for vocab term auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/vocabTerm")
	public @ResponseBody JsonSummaryResponse<VocabACSummaryRow> vocabAutoComplete(@RequestParam("query") String query) {

		SearchResults<VocabACResult> results = getVocabAutoCompleteResults(query);
		JsonSummaryResponse<VocabACSummaryRow> jsonResponse = makeJsonResponse(results,query,ACType.GXD);
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

		Filter noHuman = new Filter(SearchConstants.VOC_VOCAB, "Human Phenotype Ontology");
		noHuman.setNegate(true);

		fList.add(noHuman);

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
	 * This method is used in the HMDC for pheno/disease auto complete
	 */

	@RequestMapping("/hmdcTermAC")
	public @ResponseBody List<String> hmdcAutoComplete(@RequestParam("query") String query, @RequestParam("pageSize") String pageSize) {

		List<String> words = QueryParser.parseAutoCompleteHMDCSearch(query);
		logger.debug("vocab term query:" + words.toString());
		int returnAmount = 25;
		try {
			returnAmount = Integer.parseInt(pageSize);
		} catch (Exception e) {
			returnAmount = 25;
		}

		SearchParams params = new SearchParams();
		params.setIncludeMetaHighlight(true);

		if(query.length() < 6) {
			params.setPageSize((int)(Math.pow(10, query.length())));
		} else {
			params.setPageSize(1000000);
		}

		List<Filter> filterList = new ArrayList<Filter>();
		for (String q : words) {
			filterList.add(new Filter(SearchConstants.VOC_DERIVED_TERMS,q,Filter.Operator.OP_GREEDY_BEGINS));
		}
		List<Filter> vocabList = new ArrayList<Filter>();
		vocabList.add(new Filter(SearchConstants.VOC_VOCAB, "Disease Ontology"));
		vocabList.add(new Filter(SearchConstants.VOC_VOCAB, "Human Phenotype Ontology"));
		vocabList.add(new Filter(SearchConstants.VOC_VOCAB, "Mammalian Phenotype"));

		filterList.add(Filter.or(vocabList));

		params.setFilter(Filter.and(filterList));

		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("score", true));
		params.setSorts(sorts);

		SearchResults<VocabACResult> results = autocompleteFinder.getVocabAutoComplete(params);

		// Removes Dups
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		for(VocabACResult vacr: results.getResultObjects()) {
			for(String term: vacr.getDerivedTerms()) {
				map.put(term.toLowerCase(), term);
			}
		}
		if(map.size() > returnAmount) {
			return new ArrayList<String>(map.values()).subList(0, returnAmount);
		} else {
			return new ArrayList<String>(map.values());
		}
		
//		// Does not remove dups
//		ArrayList<String> list = new ArrayList<String>();
//		int count = 0;
//		for(VocabACResult vacr: results.getResultObjects()) {
//			for(String term: vacr.getDerivedTerms()) {
//				if(count++ < returnAmount) list.add(term);
//				else break;
//			}
//		}
//		return list;
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
	
	/*-------------------- Adult Mouse Anatomy (MA) methods --------------------*/

	/* autocomplete for the Adult Mouse Anatomy browser's search pane
	 */
	@RequestMapping("/ma_ontology")
	public @ResponseBody SearchResults<VocabBrowserACResult> getMouseAnatomyAutocomplete(
			HttpServletResponse response, @RequestParam("query") String query) {
		logger.debug("autoCompleteController.getMouseAnatomyAutocomplete(" + query + ")");
		AjaxUtils.prepareAjaxHeaders(response);
		return getSharedVocabBrowserAutocomplete(query, VocabularyController.MA_VOCAB);
	}
	
	/*-------------------- Mammalian Phenotype (MP) methods --------------------*/

	/* autocomplete for the Mammalian Phenotype browser's search pane
	 */
	@RequestMapping("/mp_ontology")
	public @ResponseBody SearchResults<VocabBrowserACResult> getMPAutocomplete(
			HttpServletResponse response, @RequestParam("query") String query) {
		logger.debug("autoCompleteController.getMPAutocomplete(" + query + ")");
		AjaxUtils.prepareAjaxHeaders(response);
		return getSharedVocabBrowserAutocomplete(query, VocabularyController.MP_VOCAB);
	}
	
	/*-------------------- Gene Ontology (GO) methods --------------------*/

	/* autocomplete for the Gene Ontology browser's search pane
	 */
	@RequestMapping("/gene_ontology")
	public @ResponseBody SearchResults<VocabBrowserACResult> getGOAutocomplete(
			HttpServletResponse response, @RequestParam("query") String query) {
		logger.debug("autoCompleteController.getGOAutocomplete(" + query + ")");
		AjaxUtils.prepareAjaxHeaders(response);
		return getSharedVocabBrowserAutocomplete(query, VocabularyController.GO_VOCAB);
	}
	
	/*-------------------- Human Phenotype Ontology (HPO) methods --------------------*/

	/* autocomplete for the Human Phenotype Ontology browser's search pane
	 */
	@RequestMapping("/hp_ontology")
	public @ResponseBody SearchResults<VocabBrowserACResult> getHPAutocomplete(
			HttpServletResponse response, @RequestParam("query") String query) {
		logger.debug("autoCompleteController.getHPAutocomplete(" + query + ")");
		AjaxUtils.prepareAjaxHeaders(response);
		return getSharedVocabBrowserAutocomplete(query, VocabularyController.HPO_VOCAB);
	}
	
	/*-------------------- Disease Ontology (HPO) methods --------------------*/

	/* autocomplete for the Disease Ontology browser's search pane
	 */
	@RequestMapping("/disease_ontology")
	public @ResponseBody SearchResults<VocabBrowserACResult> getDOAutocomplete(
			HttpServletResponse response, @RequestParam("query") String query) {
		logger.debug("autoCompleteController.getDOAutocomplete(" + query + ")");
		AjaxUtils.prepareAjaxHeaders(response);
		return getSharedVocabBrowserAutocomplete(query, VocabularyController.DO_VOCAB);
	}
	
	/*-------------------- shared vocab browser methods --------------------*/

	private SearchResults<VocabBrowserACResult> getSharedVocabBrowserAutocomplete (String query, String vocabName) {

		List<VocabBrowserSearchResult> results = vocabController.getSharedBrowserSearchResults(query, vocabName, 2000);
		List<VocabBrowserACResult> acResults = new ArrayList<VocabBrowserACResult>();
		for (VocabBrowserSearchResult result : results) {
			Map<String,String> matches = result.getAllMatches();
			for (String term : matches.keySet()) {
				acResults.add(new VocabBrowserACResult(term, matches.get(term), result.getAccID()));
			}
		}

		ACResultComparator<VocabBrowserACResult> comparator = new ACResultComparator<VocabBrowserACResult>(query);
		Collections.sort(acResults, comparator);

                int imax = Math.min(250, acResults.size());
                List<VocabBrowserACResult> acResults2 = new ArrayList<VocabBrowserACResult>(acResults.subList(0,imax));

		SearchResults<VocabBrowserACResult> sr = new SearchResults<VocabBrowserACResult>();
		sr.setResultObjects(acResults2);
		sr.setTotalCount(acResults2.size());
		return sr;
	}
	
	/*-------------------- helper class for shared vocab browser --------------------*/

	private class ACResultComparator<T> implements Comparator<T> {
		private String searchString;
		
		private ACResultComparator() {}

		public ACResultComparator(String searchString) {
			this.searchString = searchString.toLowerCase().replaceAll("-", " ").replaceAll("/", " ")
				.replaceAll("'", "").replaceAll(",", " ").replaceAll("[^A-Za-z0-9\\s]", "")
				.replaceAll("\\s\\s+", " ");
		}

		@Override
		public int compare(T arg0, T arg1) {
			return this.compare((VocabBrowserACResult) arg0, (VocabBrowserACResult) arg1);
		}

		public int compare(VocabBrowserACResult a, VocabBrowserACResult b) {
			String aTerm = a.getTerm().toLowerCase().replaceAll("-", " ").replaceAll("/", " ").replaceAll("'", "");
			String bTerm = b.getTerm().toLowerCase().replaceAll("-", " ").replaceAll("/", " ").replaceAll("'", "");

			// deal with exact matches first
			
			if (aTerm.equals(searchString)) {
				if (bTerm.equals(searchString)) {
					// a and b both exact matches, order by display string to put synonyms second
					return a.getAutocompleteDisplay().compareTo(b.getAutocompleteDisplay());
				} else {
					// a is exact match, b is not, so a first
					return -1;	
				}
			} else if (bTerm.equals(searchString)) {
				// a is not an exact match, b is, so b first
				return 1;
			}
			
			// then deal with begins matches next
			
			if (aTerm.startsWith(searchString)) {
				if (bTerm.startsWith(searchString)) {
					// a and b both begin with the search string; order by display string to put synonyms second
					return a.getAutocompleteDisplay().compareTo(b.getAutocompleteDisplay());
				} else {
					// a is begins match, b is not, so a first
					return -1;	
				}
			} else if (bTerm.startsWith(searchString)) {
				// a is not a begins match, b is, so b first
				return 1;
			}

			// fall back on string ordering of display string (if text matches, put synonym last)
			return a.getAutocompleteDisplay().compareTo(b.getAutocompleteDisplay());
		}
	}
}
