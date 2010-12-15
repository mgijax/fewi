package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Sequence;

import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.ReferenceSummary;
import org.jax.mgi.shr.fe.IndexConstants;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*
 * This controller maps all /reference/ uri's
 */
@Controller
@RequestMapping(value="/reference")
public class ReferenceController {
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(ReferenceController.class);
	
	// get the finders used by various methods
	@Autowired
	private ReferenceFinder referenceFinder;
	
	@Autowired
	private SequenceFinder sequenceFinder;
	
	@Autowired
	private AlleleFinder alleleFinder;
	
    @Value("${solr.factetNumberDefault}")
    private Integer facetLimit; 
	
	// add a new ReferenceQueryForm and MySirtPaginator objects to model for QF 
	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model) {
		model.addAttribute(new ReferenceQueryForm());
		model.addAttribute("sort", new Paginator());
		return "reference_query";
	}
	
	/* 
	 * This method maps requests for the reference summary view. Note that this 
	 * method does not process the actual query, but rather maps the request
	 * to the apropriate view and returns any Model objects needed by the
	 * view. The view is responsible for issuing the ajax query that will
	 * return the results to populate the data table.
	 */
	@RequestMapping("/summary")
	public String referenceSummary(HttpServletRequest request, Model model,
			@ModelAttribute ReferenceQueryForm queryForm) {

		//model.addAttribute("referenceQueryForm", queryForm);
		model.addAttribute("queryString", request.getQueryString());
		

		String text = request.getParameter("text");
		String sort = "year";
		if (text != null && !"".equals(text)){
			sort = "score";
		}
		
		model.addAttribute("defaultSort", sort);
		model.addAttribute("referenceQueryForm", queryForm);
		logger.debug("queryString: " + request.getQueryString());

		return "reference_summary";
	}
	
	/* 
	 * This method maps ajax requests from the reference summary page.  It 
	 * parses the ReferenceQueryForm, generates SearchParams object, and issues
	 * the query to the ReferenceFinder.  The results are returned as JSON
	 */
	@RequestMapping("/json")
	public @ResponseBody SearchResults<ReferenceSummary> referenceSummaryJson(
			HttpServletRequest request, 
			@ModelAttribute ReferenceQueryForm query,
			@ModelAttribute Paginator page) {
				
		logger.debug("json query: " + query.toString());
		
		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setPaginator(page);		
		params.setSorts(this.parseSorts(request));
		params.setFilter(this.parseReferenceQueryForm(query));

		// perform query and return results as json
		logger.debug("params parsed");

		return referenceFinder.searchSummaryReferences(params);
	}
	
	/*
	 * This method maps requests for a reference detail page by id.
	 */
	@RequestMapping("/{refID}")
	public String referenceById(
			@PathVariable("refID") String refID,
			HttpServletRequest request, Model model) {
		
		model.addAttribute("queryString", "id=" + refID);		
		return "reference_summary";		
	}
	
	/* 
	 * This method maps requests for the reference summary for an allele.  
	 * Note that this method does not process the actual query, but rather maps
	 * the request to the apropriate view and returns any Model objects needed
	 * by the view.  The view is responsible for issuing the ajax query that 
	 * will return the results to populate the data table.
	 */
	@RequestMapping("/allele/{alleleID}")
	public String referenceSummaryForAllele(			
			@PathVariable("alleleID") String alleleID,
			HttpServletRequest request, Model model) {		
		
        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter seqIdFilter = new Filter(SearchConstants.ALL_ID, alleleID);
        searchParams.setFilter(seqIdFilter);

        // find the requested sequence
        SearchResults<Allele> searchResults
          = alleleFinder.getAlleleByID(searchParams);
        
        List<Allele> alleleList = searchResults.getResultObjects();

        if (alleleList.size() < 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Allele Found");
            return "error";
        } else if (alleleList.size() > 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return "error";
        }
        
        model.addAttribute("allele", alleleList.get(0));
		model.addAttribute("queryString", "alleleKey=" + alleleList.get(0).getAlleleKey());
		
		return "reference_summary_allele";
	}
	
	/*
	 * This method maps requests for the reference summary for a sequence. 
	 * Note that this method does not process the actual query, but rather maps
	 * the request to the apropriate view and returns any Model objects needed
	 * by the view.  The view is responsible for issuing the ajax query that 
	 * will return the results to populate the data table.
	 */
	@RequestMapping("/sequence/{seqID}")
	public String referenceSummaryForSequence(
			@PathVariable("seqID") String seqID,
			HttpServletRequest request, Model model) {
		
		logger.debug("reference_summary_sequence");
		
        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter seqIdFilter = new Filter(SearchConstants.SEQ_ID, seqID);
        searchParams.setFilter(seqIdFilter);

        // find the requested sequence
        SearchResults<Sequence> searchResults
          = sequenceFinder.getSequenceByID(searchParams);
        
        List<Sequence> seqList = searchResults.getResultObjects();

        if (seqList.size() < 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return "error";
        } else if (seqList.size() > 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return "error";
        }
        
        model.addAttribute("sequence", seqList.get(0));
		model.addAttribute("queryString", "seqKey=" + seqList.get(0).getSequenceKey());
		
		return "reference_summary_sequence";
	}

	/*
	 * This method parses the ReferenceQueryForm bean and constructs a Filter 
	 * object to represent the query.  
	 */
	private Filter parseReferenceQueryForm(ReferenceQueryForm query){
		// start filter list for query filters
		List<Filter> queryList = new ArrayList<Filter>();
		// start filter list to store facet filters
		List<Filter> facetList = new ArrayList<Filter>();
		
		logger.debug("get params");
		
		// process normal query form parameter.  the resulting filter objects
		// are added to queryList.  
		
		//build author query filter
		if(query.getAuthor() != null && !"".equals(query.getAuthor())){

			List<String> authors = this.parseList(query.getAuthor().trim(), ";");

			String scope = query.getAuthorScope();
			
			if ("first".equals(scope)){
				queryList.add(new Filter(SearchConstants.REF_AUTHOR_FIRST, 
						authors, Filter.OP_IN));
			} else if ("last".equals(scope)){
				queryList.add(new Filter(SearchConstants.REF_AUTHOR_LAST, 
						authors, Filter.OP_IN));
			} else {
				queryList.add(new Filter(SearchConstants.REF_AUTHOR_ANY, 
						authors, Filter.OP_IN));
			}
		}

		// build journal query filter
		if(query.getJournal() != null && !"".equals(query.getJournal())){
			
			List<String> journals = this.parseList(query.getJournal().trim(), ";");

			queryList.add(new Filter(SearchConstants.REF_JOURNAL, 
					journals, Filter.OP_IN));
		}
		
		// build year query filter
		String year = query.getYear().trim();
		if(year != null && !"".equals(year)){
			int rangeLoc = year.indexOf("-");
			if(rangeLoc > -1){
				// TODO validate years are numbers	
				
				List<String> years = this.parseList(year, "-");

				if (years.size() == 2){
					logger.debug("year range: " + years.get(0) + "-" + years.get(1));
					Integer one = new Integer(years.get(0));
					Integer two = new Integer(years.get(1));
					
					if (one > two){
						years.set(0, two.toString());
						years.set(1, one.toString());
					}
					queryList.add(new Filter(SearchConstants.REF_YEAR, 
							years.get(0), Filter.OP_GREATER_OR_EQUAL));
					queryList.add(new Filter(SearchConstants.REF_YEAR, 
							years.get(1), Filter.OP_LESS_OR_EQUAL));
				} else {
					if (rangeLoc == 0){
						logger.debug("year <= " + years.get(0));
						queryList.add(new Filter(SearchConstants.REF_YEAR, 
								years.get(0), Filter.OP_LESS_OR_EQUAL));
					} else {
						logger.debug("year >= " + years.get(0));
						queryList.add(new Filter(SearchConstants.REF_YEAR, 
								years.get(0), Filter.OP_GREATER_OR_EQUAL));
					}
				}
				// TODO error: too many years entered
			} else {
				queryList.add(new Filter(SearchConstants.REF_YEAR, 
						year, Filter.OP_EQUAL));
			}
		}
		
		// build text query filter
		if(query.getText() != null && !"".equals(query.getText())){
			Filter tf = new Filter();
			List<Filter> textFilters = new ArrayList<Filter>();
			
			String text = query.getText();
			if(query.isInAbstract()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_ABSTRACT, 
						text, Filter.OP_CONTAINS));
			}
			if(query.isInTitle()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_TITLE, 
						text, Filter.OP_CONTAINS));
			}
			if (textFilters.size() == 1) {
				queryList.add(textFilters.get(0));
			} else {
				tf.setFilterJoinClause(Filter.FC_OR);
				tf.setNestedFilters(textFilters);
				queryList.add(tf);
			}
		}
		
		// process the summary for a parent object (allele, sequence) params.
		// these are added to queryList just like any regular query form param.
		
		// build sequence key query filter
		if (query.getSeqKey() != null){
			logger.info("set seqKey filter");
			queryList.add(new Filter(SearchConstants.SEQ_KEY, 
					query.getSeqKey().toString(), Filter.OP_EQUAL));
		}
		
		// build allele key query filter
		if (query.getAlleleKey() != null){
			logger.info("set alleleKey filter");
			queryList.add(new Filter(SearchConstants.ALL_KEY, 
					query.getAlleleKey().toString(), Filter.OP_EQUAL));
		}
		
		// process facet filters.  these filters are added to facetList as they 
		// should not be considered when validating the actual query.
		logger.debug("get filters");
		
		// build author facet query filter
		if(query.getAuthorFilter().size() > 0){
			facetList.add(new Filter(FacetConstants.REF_AUTHORS, 
					query.getAuthorFilter(), Filter.OP_IN));
		}
		// build journal facet query filter
		if (query.getJournalFilter().size() > 0){
			facetList.add(new Filter(FacetConstants.REF_JOURNALS, 
					query.getJournalFilter(), Filter.OP_IN));
		}
		// build year facet query filter
		if (query.getYearFilter().size() > 0){
			List<String> years = new ArrayList<String>();
			for (Integer yearString : query.getYearFilter()) {
				years.add(String.valueOf(yearString));
			}
			facetList.add(new Filter(FacetConstants.REF_YEAR, 
					years, Filter.OP_IN));
		}
		// build curated data facet query filter
		if (query.getCuratedDataFilter().size() > 0){
			List<String> selections = new ArrayList<String>();
			for (String filter : query.getCuratedDataFilter()) {
				logger.debug(filter.replaceAll("\\*", ","));
				selections.add(filter.replaceAll("\\*", ","));
			}
			facetList.add(new Filter(FacetConstants.REF_CURATED_DATA, 
					selections, Filter.OP_IN));
		}
		
		logger.debug("build params");
		
		// TODO id invalid case where ID param and others entered
		// valid parameters entered, build and return Filter 
		if (queryList.size() > 0){
			Filter f = new Filter();
			f.setFilterJoinClause(Filter.FC_AND);
			queryList.addAll(facetList);
			f.setNestedFilters(queryList);
			return f;
		// none yet, so check the id query and build it
		} else if (query.getId() != null && !"".equals(query.getId())){
			
			List<String> ids = this.parseList(query.getId().trim(), ";");
			List<String> cleanIds = new ArrayList<String>();
			for (String id : ids) {
				if (id.toLowerCase().startsWith("pmid:")){
					cleanIds.add(id.substring(5));
				} else {
					cleanIds.add(id);
				}
			}

			facetList.add(new Filter(SearchConstants.REF_ID, 
					cleanIds, Filter.OP_IN));
			Filter f = new Filter();
			f.setFilterJoinClause(Filter.FC_AND);
			f.setNestedFilters(facetList);
			return f;
		} else {
			//TODO no query params
		}
		
		return new Filter();
	}
	
	/*
	 * This is a helper method to parse sort parameters from the query string
	 * and return a Sort object.  
	 */
	private List<Sort> parseSorts(HttpServletRequest request) {
		
		List<Sort> sorts = new ArrayList<Sort>();
		
		String s = request.getParameter("sort");
		String d = request.getParameter("dir");
		boolean desc = false;
		
		if ("authors".equalsIgnoreCase(s)){
			s = SortConstants.REF_AUTHORS;
		} else if ("journal".equalsIgnoreCase(s)){
			s = SortConstants.REF_JOURNAL;
		} else if ("score".equalsIgnoreCase(s)){
			// null op
		} else {
			s = SortConstants.REF_YEAR;
		}
		
		if("desc".equalsIgnoreCase(d)){
			desc = true;
		}
		
		logger.debug("sort: " + s + " " + d);
		Sort sort = new Sort(s, desc);
		
		sorts.add(sort);
		return sorts;
	}
	
	private List<String> parseList(String list, String delimiter){
		String parsed[] = list.split(delimiter);
		List<String> items = new ArrayList<String>();
		String item;
		for (int i = 0; i < parsed.length; i++) {
			item = parsed[i].trim();
			if (item != null && !"".equals(item) ){
				items.add(item);
			}
		}
		return items;
	}

	/*
	 * This method maps requests for author auto complete results.  The results
	 * are returned as JSON.
	 */
	@RequestMapping("/autocomplete/author")
	public @ResponseBody SearchResults<String> authorAutoComplete(
			@RequestParam("query") String query) {
		// split input on any non-alpha and non-apostrophe characters
		List<String> words = 
			Arrays.asList(query.trim().split("[^a-zA-Z0-9']+"));
		logger.debug("author query:" + words.toString());
		//build SearchParams for author auto complete query
		SearchParams params = buildACQuery(SearchConstants.REF_AUTHOR, words);
		// return results
		return referenceFinder.getAuthorAutoComplete(params);
	}
	
	/*
	 * This method maps requests for journal auto complete results. The results
	 * are returned as JSON.
	 */
	@RequestMapping("/autocomplete/journal")
	public @ResponseBody SearchResults<String> journalAutoComplete(
			@RequestParam("query") String query) {
		// split input on any non-alpha characters
		List<String> words = Arrays.asList(query.trim().split("[^a-zA-Z0-9]"));
		logger.debug("journal query:" + words.toString());
		//build SearchParams for journal auto complete query
		SearchParams params = buildACQuery(SearchConstants.REF_JOURNAL, words);
		//return results
		return referenceFinder.getJournalAutoComplete(params);
	}

	/*
	 * This is a helper method that takes a List of Strings and generates a  
	 * SearchParams object containing the appropriate Filter objects AND'ed 
	 * together for the requested Auto Complete query.  
	 */
	private SearchParams buildACQuery(String param, List<String> queries){
		Filter f;
		SearchParams params = new SearchParams();
		params.setPageSize(1000);
		List<Sort> sorts = new ArrayList<Sort>();
		Sort s = new Sort(IndexConstants.REF_AUTHOR_SORT, false);
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
		params.setFilter(f);
		return params;
	}
	
	/*
	 * This method maps requests for the author facet list.  The results are
	 * returned as JSON.  
	 */
	@RequestMapping("/facet/author")
	public @ResponseBody Map<String, List<String>> facetAuthor(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");

		return this.parseFacetResponse(referenceFinder.getAuthorFacet(params));
	}
	
	/*
	 * This method maps requests for the journal facet list.  The results are
	 * returned as JSON.  
	 */
	@RequestMapping("/facet/journal")
	public @ResponseBody Map<String, List<String>> facetJournal(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return this.parseFacetResponse(referenceFinder.getJournalFacet(params));
	}
	
	/*
	 * This method maps requests for the year facet list.  The results are
	 * returned as JSON.  
	 */
	@RequestMapping("/facet/year")
	public @ResponseBody Map<String, List<String>> facetYear(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return this.parseFacetResponse(referenceFinder.getYearFacet(params));
	}
	
	/*
	 * This method maps requests for the curated data  facet list.  The results 
	 * are returned as JSON.  
	 */
	@RequestMapping("/facet/data")
	public @ResponseBody Map<String, List<String>> facetData(
			@ModelAttribute ReferenceQueryForm query) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return this.parseFacetResponse(referenceFinder.getDataFacet(params));
	}
	
	private Map<String, List<String>> parseFacetResponse(
			SearchResults<String> facetResults) {
		
		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		
		logger.info("facets: " + facetResults.getResultFacets().size());
		for (String f : facetResults.getResultFacets()) {
			logger.info(f);
		}
		
			if (facetResults.getResultFacets().size() >= facetLimit){
				l.add("Too many filter values to display.");
				m.put("error", l);
			} else if (facetResults.getResultFacets().size() < 2){				
				l.add("Nothing to filter here.  Move along.");
				m.put("error", l);
			} else {
				m.put("resultFacets", facetResults.getResultFacets());
			}
			return m;
	}
}
