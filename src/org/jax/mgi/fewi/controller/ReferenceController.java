package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Sequence;

import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.ReferenceSummary;
import org.jax.mgi.fewi.util.Highlighter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	private MarkerFinder markerFinder;
	
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
			@ModelAttribute ReferenceQueryForm queryForm,
			BindingResult result) {

		parseReferenceQueryForm(queryForm, result);		
		model.addAttribute("referenceQueryForm", queryForm);
		
		if (result.hasErrors()) {
			return "reference_query";
		}
		
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
	@RequestMapping("/report*")
	public String referenceSummaryReport(
			HttpServletRequest request, Model model,
			@ModelAttribute ReferenceQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) {
				
		logger.debug("summaryReport");
		
		SearchResults<Reference> searchResults;
		try {
			searchResults = this.getSummaryResults(request, query, page, result);
	        model.addAttribute("results", searchResults.getResultObjects());

			return "referenceSummaryReport";			
		} catch (BindException be) {
			logger.debug("bind error");
			return "reference_query";
		}

	}
	
	/* 
	 * This method maps ajax requests from the reference summary page.  It 
	 * parses the ReferenceQueryForm, generates SearchParams object, and issues
	 * the query to the ReferenceFinder.  The results are returned as JSON
	 */
	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<ReferenceSummary> referenceSummaryJson(
			HttpServletRequest request, 
			@ModelAttribute ReferenceQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException {
					
		logger.debug("json");
		SearchResults<Reference> searchResults = this.getSummaryResults(request, query, page, result);
		
        List<Reference> refList = searchResults.getResultObjects();
        
        Map<String, Set<String>> highlighting = searchResults.getResultSetMeta().getSetHighlights();
		
        Set<String> textHl = new HashSet<String>(), authorHL = new HashSet<String>();
		
		logger.debug("wrap results");
        List<ReferenceSummary> summaryRows = new ArrayList<ReferenceSummary>();
        ReferenceSummary row;
        MetaData rowMeta;

        for (Reference ref : refList) {
			if (ref != null){
				row = new ReferenceSummary(ref);
				rowMeta = searchResults.getMetaMapping().get(String.valueOf(ref.getReferenceKey()));
				if (rowMeta != null){
					row.setScore(rowMeta.getScore());
				} else {
					row.setScore("0");
				}
				if (query.isInAbstract()){
					if (highlighting.containsKey(SearchConstants.REF_TEXT_TITLE_ABSTRACT)){
						textHl = highlighting.get(SearchConstants.REF_TEXT_TITLE_ABSTRACT);
					} else if (highlighting.containsKey(SearchConstants.REF_TEXT_ABSTRACT)){
						textHl= highlighting.get(SearchConstants.REF_TEXT_ABSTRACT);
					}
					row.setAbstractHL(new Highlighter(textHl));
				}
				if (query.isInTitle()){
					if (highlighting.containsKey(SearchConstants.REF_TEXT_TITLE_ABSTRACT)){
						textHl = highlighting.get(SearchConstants.REF_TEXT_TITLE_ABSTRACT);
					} else if (highlighting.containsKey(SearchConstants.REF_TEXT_TITLE)){
						textHl= highlighting.get(SearchConstants.REF_TEXT_TITLE);
					}
					row.setTitleHL(new Highlighter(textHl));
				}
				if (query.getAuthor() != null && !"".equals(query.getAuthor())){					
					for (String auth: highlighting.get(SearchConstants.REF_AUTHOR)) {
						authorHL.add(auth.replace(" ", "[\\s\\-']"));
					}
					row.setAuthorHL(new Highlighter(authorHL));
				}
//				if  (query.getAuthorFilter() != null && query.getAuthorFilter().size() > 0){
//					logger.debug("get authorFilter highlighting");
//					for(String auth: highlighting.get(FacetConstants.REF_AUTHORS)){
//						authorHL.add(auth.replace(" ", "[\\s\\-']"));
//					}
//					logger.debug("done");
//					row.setAuthorHL(new Highlighter(authorHL));
//				}
				summaryRows.add(row);
			} else {
				logger.debug("--> Null Object");
			}
		}
        
        JsonSummaryResponse<ReferenceSummary> jsonResponse
        		= new JsonSummaryResponse<ReferenceSummary>();
        jsonResponse.setSummaryRows(summaryRows);       
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        
        return jsonResponse;
	}
	
	private SearchResults<Reference> getSummaryResults(
			HttpServletRequest request, 
			@ModelAttribute ReferenceQueryForm query,
			@ModelAttribute Paginator page,
			BindingResult result) throws BindException{
		
		logger.debug("getSummaryResults query: " + query.toString());
		
		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		params.setPaginator(page);		
		params.setSorts(this.parseSorts(request));
		params.setFilter(this.parseReferenceQueryForm(query, result));
		
		// perform query and return results as json
		logger.debug("params parsed");		
		
		if (result.hasErrors()){
			logger.debug("bind error");
			throw new BindException(result);
		} else {
			return referenceFinder.searchSummaryReferences(params);
		}
	}
	
	/*
	 * This method maps requests for a reference detail page by id.
	 */
	@RequestMapping("/{refID}")
	public String referenceById(
			@PathVariable("refID") String refID,
			@ModelAttribute ReferenceQueryForm query,
			HttpServletRequest request, Model model) {
		
		model.addAttribute("queryString", "id=" + refID);		
		return "reference_detail";		
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
	 * This method maps requests for the reference summary for a sequence. 
	 * Note that this method does not process the actual query, but rather maps
	 * the request to the apropriate view and returns any Model objects needed
	 * by the view.  The view is responsible for issuing the ajax query that 
	 * will return the results to populate the data table.
	 */
	@RequestMapping("/marker/{markerID}")
	public String referenceSummaryForMarker(
			@PathVariable("markerID") String markerID,
			HttpServletRequest request, Model model) {
		
		logger.debug("reference_summary_marker");
		
        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIdFilter);

        // find the requested sequence
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByID(searchParams);
        
        List<Marker> markerList = searchResults.getResultObjects();

        if (markerList.size() < 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return "error";
        } else if (markerList.size() > 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return "error";
        }

        model.addAttribute("marker", markerList.get(0));
		model.addAttribute("queryString", "markerKey=" + markerList.get(0).getMarkerKey());
		
		return "reference_summary_marker";
	}
	
	/*
	 * This method parses the ReferenceQueryForm bean and constructs a Filter 
	 * object to represent the query.  
	 */
	private Filter parseReferenceQueryForm(ReferenceQueryForm query, BindingResult result){
		// start filter list for query filters
		List<Filter> queryList = new ArrayList<Filter>();
		// start filter list to store facet filters
		List<Filter> facetList = new ArrayList<Filter>();
		
		logger.debug("get params");
		
		// process normal query form parameter.  the resulting filter objects
		// are added to queryList.  
		
		//build author query filter
		String authorText = query.getAuthor().trim();
		if(authorText != null && !"".equals(authorText)){

			List<String> authors = this.parseList(authorText, ";");

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
		String journalText = query.getJournal().trim();
		if(journalText != null && !"".equals(journalText)){
			
			List<String> journals = this.parseList(journalText, ";");

			queryList.add(new Filter(SearchConstants.REF_JOURNAL, 
					journals, Filter.OP_IN));
		}
		
		// build year query filter
		String year = query.getYear().trim();
		if(year != null && !"".equals(year)){
			int rangeLoc = year.indexOf("-");
			if(rangeLoc > -1){
				List<String> years = this.parseList(year, "-");
				if (years.size() > 2){
					result.addError(
							new FieldError("referenceQueryForm", 
									"year", 
									"* Invalid range format"));
				} else if (years.size() == 2){
					logger.debug("year range: " + years.get(0) + "-" + years.get(1));
					try{
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
					} catch (NumberFormatException nfe){
						result.addError(
								new FieldError("referenceQueryForm", 
										"year", 
										"* Invalid number format"));
					}
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
			} else {
				try{
					// only used to validate number format
					Integer one = new Integer(year);
					queryList.add(new Filter(SearchConstants.REF_YEAR, 
							year, Filter.OP_EQUAL));
				} catch (NumberFormatException nfe){
					result.addError(
							new FieldError("referenceQueryForm", 
									"year", 
									"* Invalid number format"));
				}
			}
		}
		
		// build text query filter
		String textField = query.getText().trim();
		if(textField != null && !"".equals(textField)){
			Filter tf = new Filter();
			List<Filter> textFilters = new ArrayList<Filter>();

			if(query.isInAbstract()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_ABSTRACT, 
						textField, Filter.OP_CONTAINS));
			}
			if(query.isInTitle()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_TITLE, 
						textField, Filter.OP_CONTAINS));
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
		
		// build allele key query filter
		if (query.getMarkerKey() != null){
			logger.info("set alleleKey filter");
			queryList.add(new Filter(SearchConstants.MRK_KEY, 
					query.getMarkerKey().toString(), Filter.OP_EQUAL));
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
		if (query.getDataFilter().size() > 0){
			List<String> selections = new ArrayList<String>();
			for (String filter : query.getDataFilter()) {
				logger.debug(filter.replaceAll("\\*", ","));
				selections.add(filter.replaceAll("\\*", ","));
			}
			facetList.add(new Filter(FacetConstants.REF_CURATED_DATA, 
					selections, Filter.OP_IN));
		}
		
		logger.debug("build params");
		
		List<String> ids = new ArrayList<String>();
		String idtext = query.getId().trim();
		if (idtext != null && !"".equals(idtext)){
			ids = this.parseList(idtext, ";");
		}
		
		if (queryList.size() > 0){
			if (ids.size() > 0){
				result.addError(
						new FieldError("referenceQueryForm", 
								"id", 
								"* Invalid with other parameters"));
			} else {
				Filter f = new Filter();
				f.setFilterJoinClause(Filter.FC_AND);
				queryList.addAll(facetList);
				f.setNestedFilters(queryList);
				return f;
			}
		// none yet, so check the id query and build it
		} else if (ids.size() > 0){			
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
			result.addError(
				new ObjectError("referenceQueryForm", 
						"* Please enter some search parameters"));
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
		} else if ("year".equalsIgnoreCase(s)){
			s = SortConstants.REF_YEAR;
		} else {
			s = "score";
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
	 * This method maps requests for the author facet list.  The results are
	 * returned as JSON.  
	 */
	@RequestMapping("/facet/author")
	public @ResponseBody Map<String, List<String>> facetAuthor(
			@ModelAttribute ReferenceQueryForm query,
			BindingResult result) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();
		params.setFilter(this.parseReferenceQueryForm(query, result));
	
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
			@ModelAttribute ReferenceQueryForm query,
			BindingResult result) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query, result));
	
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
			@ModelAttribute ReferenceQueryForm query,
			BindingResult result) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query, result));
	
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
			@ModelAttribute ReferenceQueryForm query,
			BindingResult result) {
			
		logger.debug(query.toString());
		
		SearchParams params = new SearchParams();		
		params.setFilter(this.parseReferenceQueryForm(query, result));
	
		// perform query and return results as json
		logger.debug("params parsed");
		return this.parseFacetResponse(referenceFinder.getDataFacet(params));
	}
	
	private Map<String, List<String>> parseFacetResponse(
			SearchResults<String> facetResults) {
		
		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();
		
		if (facetResults.getResultFacets().size() >= facetLimit){
			l.add("Too many filter values to display.");
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() == 0) {
			l.add("Zero filter values to display.");
			m.put("error", l);
		} else {
			m.put("resultFacets", facetResults.getResultFacets());
		}
		return m;
	}
}
