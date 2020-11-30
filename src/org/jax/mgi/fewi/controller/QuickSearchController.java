package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.QuickSearchFinder;
import org.jax.mgi.fewi.forms.AccessionQueryForm;
import org.jax.mgi.fewi.forms.QuickSearchQueryForm;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.AccessionSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.QSVocabResult;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.summary.QSFeatureResultWrapper;
import org.jax.mgi.fewi.summary.QSVocabResultWrapper;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
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

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /quicksearch/ uri's
 */
@Controller
@RequestMapping(value="/quicksearch")
public class QuickSearchController {


    //--------------------//
    // static variables
    //--------------------//

	private static String MARKER = "marker";			// flag for only matching markers
	private static String ALLELE = "allele";			// flag for only matching alleles
	private static String BY_ID = "match_by_id";		// match by ID
	private static String BY_TERM = "match_by_term";	// match by non-ID
	private static String BY_OTHER = "match_by_other";	// match by other (ortholog nomen or annotations)
	private static String BY_ANY = "match_by_any";		// match by any field
	private static String BY_SYNONYM = "match_by_synonym";	// match vocab by synonym
	private static String BY_DEFINITION = "match_by_def";	// match vocab by definition
	
	private static Set<String> validFacetFields;
	static {
		validFacetFields = new HashSet<String>();
		validFacetFields.add(SearchConstants.QS_GO_COMPONENT_FACETS);
		validFacetFields.add(SearchConstants.QS_GO_PROCESS_FACETS);
		validFacetFields.add(SearchConstants.QS_GO_FUNCTION_FACETS);
	}

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(QuickSearchController.class);

    @Autowired
    private AccessionController accessionController;

    @Autowired
    private QuickSearchFinder qsFinder;
    
	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 			// max values to display for a single facet

    
    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // QS Main Page -- redirect to MGI Home page
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQSMainPage() {
        logger.info("->getQSMainPage started");

        ModelAndView mav = new ModelAndView("redirect:" + ContextLoader.getConfigBean().getProperty("FEWI_URL"));
        return mav;
    }

    //-------------------------//
    // QS Results Page shell
    //-------------------------//
	@RequestMapping("/summary")
	public ModelAndView getQSSummary(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}
        logger.info("->getQSSummary started");

        ModelAndView mav = new ModelAndView("quicksearch");
        mav.addObject("query", queryForm.getQuery());
		mav.addObject("queryString", request.getQueryString());
        return mav;
    }

    //-------------------------//
    // QS Results - bucket 1 JSON (markers and alleles)
    //-------------------------//
	@RequestMapping("/featureBucket")
	public @ResponseBody JsonSummaryResponse<QSFeatureResultWrapper> getFeatureBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm, @ModelAttribute Paginator page) {

        logger.info("->getFeatureBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");
        
        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        // The index has been rebuilt to instead have each document be a point of data that can be matched, so
        // we now need to retrieve all matching documents and then process them.  Guessing too high a number of
        // expected results can be detrimental to efficiency, so we can do an initial query to get the count
        // and then a second query to return them.
        
        SearchParams idSearch = getFeatureSearchParams(queryForm, BY_ID, 0);
        SearchParams nomenSearch = getFeatureSearchParams(queryForm, BY_TERM, 0);

        SearchParams orSearch = new SearchParams();
        orSearch.setPaginator(new Paginator(0));
        List<Filter> idOrNomen = new ArrayList<Filter>();
        idOrNomen.add(idSearch.getFilter());
        idOrNomen.add(nomenSearch.getFilter());
        orSearch.setFilter(Filter.or(idOrNomen));
        
        SearchResults<QSFeatureResult> idOrNomenResults = qsFinder.getFeatureResults(orSearch);
        Integer resultCount = idOrNomenResults.getTotalCount();
        logger.info("Identified " + resultCount + " matches");
        
        // Now do the query to retrieve all results.
        orSearch.setPaginator(new Paginator(resultCount));
        List<QSFeatureResult> allMatches = qsFinder.getFeatureResults(orSearch).getResultObjects();
        logger.info("Loaded " + allMatches.size() + " matches");
        
        List<QSFeatureResult> out = unifyFeatureMatches(queryForm.getTerms(), allMatches);
        logger.info("Consolidated down to " + out.size() + " features");
        
        List<QSFeatureResultWrapper> wrapped = new ArrayList<QSFeatureResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting results " + startIndex + " to " + Math.min(out.size(), endIndex));
        	for (QSFeatureResult r : out.subList(startIndex, Math.min(out.size(), endIndex))) {
        		wrapped.add(new QSFeatureResultWrapper(r));
        	}
        } else { 
        	logger.debug(" - not extracting,just returning empty list");
        }
        
        JsonSummaryResponse<QSFeatureResultWrapper> response = new JsonSummaryResponse<QSFeatureResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(out.size());
        logger.info("Returning " + wrapped.size() + " feature matches");

        return response;
    }

	// Restrict the given search params to only look at the certain object type (MARKER or ALLELE).
	// Note: This alters the 'params' object so it will have the revised filter.
	private SearchParams restrictTo (SearchParams params, String objectType) {
		String markerFlag = null;
		
		if (ALLELE.equals(objectType)) {
			markerFlag = "0";
		} else if (MARKER.equals(objectType)) {
			markerFlag = "1";
		} else {
			return params;
		}
		
		List<Filter> filters = new ArrayList<Filter>(2);
		filters.add(params.getFilter());
		filters.add(new Filter(SearchConstants.QS_IS_MARKER, markerFlag, Filter.Operator.OP_EQUAL));
		params.setFilter(Filter.and(filters));
		return params;
	}
	
	// Process the given parameters and return an appropriate SearchParams object (ready to use in a search).
	private SearchParams getFeatureSearchParams (QuickSearchQueryForm qf, String queryMode, Integer resultCount) {
        Filter featureFilter;
        
        if (BY_ID.equals(queryMode)) {
        	featureFilter = createIDFilter(qf, false);
        } else if (BY_TERM.equals(queryMode)) {
        	featureFilter = createFeatureTermFilter(qf);
        } else if (BY_OTHER.equals(queryMode)) { // BY_OTHER
        	featureFilter = createFeatureOtherFilter(qf);
        } else {	// BY_ANY
        	Filter idFilter = createIDFilter(qf, false);
        	Filter nomenFilter = createFeatureTermFilter(qf);
        	Filter otherFilter = createFeatureOtherFilter(qf);
        	featureFilter = this.orFilters(idFilter, nomenFilter, otherFilter, null);
        }
        
        Filter facetFilters = getFilterFacets(qf);
        if (facetFilters != null) {
        	List<Filter> filtered = new ArrayList<Filter>();
        	filtered.add(featureFilter);
        	filtered.add(facetFilters);
        	featureFilter = Filter.and(filtered);
        }

        Paginator page = new Paginator(resultCount);			// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);		// sort by descending Solr score (best first)
        List<Sort> sorts = new ArrayList<Sort>(1);
        sorts.add(byScore);

        SearchParams featureSearch = new SearchParams();
        featureSearch.setPaginator(page);
        featureSearch.setFilter(featureFilter);
        return featureSearch;
	}
	
	// distill the various facet parameters down to a single Filter (should work across both feature and
	// vocab term buckets)
	private Filter getFilterFacets (QuickSearchQueryForm qf) {
		List<Filter> filters = new ArrayList<Filter>();
		
		Filter processFilter = getFilterForOneField(SearchConstants.QS_GO_PROCESS_FACETS, qf.getProcessFilter());
		if (processFilter != null) { filters.add(processFilter); }
		
		Filter functionFilter = getFilterForOneField(SearchConstants.QS_GO_FUNCTION_FACETS, qf.getFunctionFilter());
		if (functionFilter != null) { filters.add(functionFilter); }
		
		Filter componentFilter = getFilterForOneField(SearchConstants.QS_GO_COMPONENT_FACETS, qf.getComponentFilter());
		if (componentFilter != null) { filters.add(componentFilter); }
		
		if (filters.size() > 0) {
			return Filter.and(filters);
		}
		return null;
	}
	
	// Compose a single Filter for the given field name, OR-ing choices from the selected list.
	private Filter getFilterForOneField (String facetField, List<String> selected) {
		if ((selected == null) || (selected.size() == 0) || (facetField == null)) { return null; }

		List<Filter> filters = new ArrayList<Filter>();
		for (String choice : selected) {
			filters.add(new Filter(facetField, choice, Filter.Operator.OP_EQUAL));
		}
		return Filter.or(filters);
	}
	
	// Return a single filter that looks for features by ID, with multiple terms joined by an OR.
	private Filter createIDFilter(QuickSearchQueryForm qf, boolean isVocabBucket) {
        List<Filter> idFilters = new ArrayList<Filter>();

        for (String term : qf.getTerms()) {
        	if (isVocabBucket) {
        		idFilters.add(new Filter(SearchConstants.QS_ACC_ID, term, Operator.OP_EQUAL));
        	} else {
        		idFilters.add(new Filter(SearchConstants.QS_SEARCH_ID, term, Operator.OP_EQUAL));
        	}
        }
        return Filter.or(idFilters);
	}
	
	// Return a single filter that looks for features by any non-ID type of field, with multiple
	// terms joined by an OR.
	private Filter createFeatureTermFilter(QuickSearchQueryForm qf) {
        List<Filter> termFilters = new ArrayList<Filter>();

        for (String term : qf.getTerms()) {
        	termFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM, term, Operator.OP_CONTAINS));
        }
        return Filter.or(termFilters);
	}
	
	// Return a single filter that looks for features by other data (ortholog nomen or annotations), with multiple
	// terms joined by an OR.
	private Filter createFeatureOtherFilter(QuickSearchQueryForm qf) {
        List<Filter> otherFilters = new ArrayList<Filter>();

        for (String term : qf.getTerms()) {
        	otherFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM, term, Operator.OP_CONTAINS));
        }
        return Filter.or(otherFilters);
	}
	
	// Join 3 filters by an OR.
	private Filter orFilters(Filter f1, Filter f2, Filter f3, Filter f4) {
		List<Filter> filters = new ArrayList<Filter>();
		if (f1 != null) filters.add(f1);
		if (f2 != null) filters.add(f2);
		if (f3 != null) filters.add(f3);
		if (f4 != null) filters.add(f4);
		if (filters.size() == 0) {
			// should not happen
			return null;
		}
		return Filter.or(filters);
	}
	
	// consolidate the lists of matching features, add star values, and setting best match values, then return
	private List<QSFeatureResult> unifyFeatureMatches (List<String> searchTerms, List<QSFeatureResult> allMatches) {
		
		Grouper<QSFeatureResult> grouper = new Grouper<QSFeatureResult>();
		
		// original serach term will be the last item in the list of search terms
		String originalSearchTerm = searchTerms.get(searchTerms.size() - 1).toLowerCase();
		
		// last search term is the full string, so this is the count of individual words
		int wordCount = searchTerms.size() - 1;
		
		// There may be multiple entries in allMatches for the same feature (marker or allele), so we need to
		// prioritize them and only choose the best.  To do this, we iterate through documents and:
		// 1. determine star tier (exact / all words / any word)
		// 2. if best tier so far for this primary ID, keep this one
		// 3. if same as best tier so far but this has a better weight (based on data type), keep this one
		
		Map<String,QSFeatureResult> bestMatches = new HashMap<String,QSFeatureResult>();
		for (QSFeatureResult match : allMatches) {
			String primaryID = match.getPrimaryID();
			
			if (match.getSearchID() != null) {
				// IDs can only be exact matches
				match.setStars("****");
				
			} else if (match.getSearchTerm() != null) {
				String lowerTerm = match.getSearchTerm().toLowerCase();

				// search terms can be exact (4-star), contain all terms (3-star), or contain some terms (2-star)
				if (lowerTerm.equals(originalSearchTerm)) {
					match.setStars("****");
				} else {
					int matchCount = 0;
					for (String word : searchTerms) {
						if (lowerTerm.indexOf(word) >= 0) {
							matchCount++;
						}
					}
					
					if (matchCount == wordCount) {
						match.setStars("***");
					} else {
						match.setStars("**");
					}
				}
			}
			
			// We'll double check that we identified at least one star, just in case something slipped through.  If
			// one has no stars, we just skip it.
			if (match.getStarCount() > 0) {
				boolean keepThisOne = false;		// Is this our best match so far for this feature?
				
				// If we've already seen this feature, then we only want to keep this as the best match if:
				// 1. it has a higher star count than the previous best match, or
				// 2. it has the same star count as the previous best match and a larger weight.
				if (bestMatches.containsKey(primaryID)) {
					QSFeatureResult bestMatch = bestMatches.get(primaryID);
					
					if (match.getStarCount() > bestMatch.getStarCount()) {
						keepThisOne = true;
					} else if (match.getStarCount() == bestMatch.getStarCount()) {
						if (match.getSearchTermWeight() > bestMatch.getSearchTermWeight()) {
							keepThisOne = true; 
						}
					}
				} else {
					// We haven't seen this feature before, so keep this as the initial "best match".
					keepThisOne = true;
				}
				
				if (keepThisOne) {
					bestMatches.put(primaryID, match);
				}
			}
		}
		
		// Now use the Grouper to divide up the best matches we found into their star buckets.
		for (String primaryID : bestMatches.keySet()) {
			QSFeatureResult bestMatch = bestMatches.get(primaryID);
			grouper.add(bestMatch.getStars(), primaryID, bestMatch, bestMatch.getSequenceNum());
		}

		return grouper.toList();
	}
	
	/* Get the set of GO Process filter options for the current result set, including facets from both the
	 * feature bucket and the vocab bucket.
	 */
	@RequestMapping("/featureBucket/process")
	public @ResponseBody Map<String, List<String>> getProcessFacet (@ModelAttribute QuickSearchQueryForm qf, HttpServletResponse response) throws Exception {
		AjaxUtils.prepareAjaxHeaders(response);
		return getFacets(qf, SearchConstants.QS_GO_PROCESS_FACETS);
	}

	/* Get the set of GO Function filter options for the current result set, including facets from both the
	 * feature bucket and the vocab bucket.
	 */
	@RequestMapping("/featureBucket/function")
	public @ResponseBody Map<String, List<String>> getFunctionFacet (@ModelAttribute QuickSearchQueryForm qf, HttpServletResponse response) throws Exception {
		AjaxUtils.prepareAjaxHeaders(response);
		return getFacets(qf, SearchConstants.QS_GO_FUNCTION_FACETS);
	}

	/* Get the set of GO Component filter options for the current result set, including facets from both the
	 * feature bucket and the vocab bucket.
	 */
	@RequestMapping("/featureBucket/component")
	public @ResponseBody Map<String, List<String>> getComponentFacet (@ModelAttribute QuickSearchQueryForm qf, HttpServletResponse response) throws Exception {
		AjaxUtils.prepareAjaxHeaders(response);
		return getFacets(qf, SearchConstants.QS_GO_COMPONENT_FACETS);
	}

	// Retrieve the facets for the specified field, in a form suitable for conversion to JSON.
	private Map<String, List<String>> getFacets (QuickSearchQueryForm qf, String facetField) throws Exception {
		List<String> featureFacets = getFeatureFacets(qf, facetField);
		List<String> vocabFacets = getVocabFacets(qf, facetField);
		List<String> resultList = unifyFacets(featureFacets, vocabFacets);
		String error = null;
		
        if (resultList.size() == 0) {
        	error = "No values for filtering";
        } else if (resultList.size() > facetLimit) {
        	error = "Too many values; please use another filter to reduce the data set first.";
        }

        Map<String, List<String>> out = new HashMap<String, List<String>>();
        if (error == null) {
			out.put("resultFacets", resultList);
        } else {
        	List<String> messages = new ArrayList<String>(1);
        	messages.add(error);
			out.put("error", messages);
        }
		return out;
	}
	
	/* Unify group1 and group2 facets into a single, ordered list for return.  Modifies group1 by adding entries
	 * from group2 and then sorting.
	 */
	private List<String> unifyFacets(List<String> group1, List<String> group2) {
		Set<String> seenIt = new HashSet<String>();

		if (group1 != null) {
			for (String item : group1) {
				seenIt.add(item);
			}
		} else {
			group1 = new ArrayList<String>();
		}
		
		if (group2 != null) {
			for (String item : group2) {
				if (!seenIt.contains(item)) {
					group1.add(item);
					seenIt.add(item);
				}
			}
		}
		Collections.sort(group1);
		return group1;
	}

	/* Execute the search for facets for the feature bucket using the given filterName, returning them as an
	 * unordered list of strings.
	 */
	private List<String> getFeatureFacets(QuickSearchQueryForm queryForm, String filterName) throws Exception {
        // match either ID, nomenclature, or other (annotations and ortholog nomen)
        
        SearchParams anySearch = getFeatureSearchParams(queryForm, BY_ANY, facetLimit);

        List<String> resultList = null;		// list of strings, each a value for a facet
        
        if (validFacetFields.contains(filterName)) {
        	resultList = qsFinder.getFeatureFacets(anySearch, filterName);
        } else {
        	throw new Exception("getFeatureFacets: Invalid facet name: " + filterName);
        }
        return resultList;
	}
	
	/* Execute the search for facets for the vocab bucket using the given filterName, returning them as an
	 * unordered list of strings.
	 */
	private List<String> getVocabFacets(QuickSearchQueryForm queryForm, String filterName) throws Exception {
        // match either ID, term, or synonyms
        
        SearchParams anySearch = getVocabSearchParams(queryForm, BY_ANY, facetLimit);

        List<String> resultList = null;		// list of strings, each a value for a facet
        
        if (validFacetFields.contains(filterName)) {
        	resultList = qsFinder.getVocabFacets(anySearch, filterName);
        } else {
        	throw new Exception("getVocabFacets: Invalid facet name: " + filterName);
        }
        return resultList;
	}
	
    //-------------------------//
    // QS Results - bucket 2 JSON (vocab terms and annotations)
    //-------------------------//
	@RequestMapping("/vocabBucket")
	public @ResponseBody JsonSummaryResponse<QSVocabResultWrapper> getVocabBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getVocabBucket started");
        Integer resultCount = 300;
        
        // Search in order of priority:  ID matches, then term matches, then synonym matches
        SearchParams idSearch = getVocabSearchParams(queryForm, BY_ID, resultCount);
        SearchParams termSearch = getVocabSearchParams(queryForm, BY_TERM, resultCount);
        SearchParams synonymSearch = getVocabSearchParams(queryForm, BY_SYNONYM, resultCount);
        SearchParams definitionSearch = getVocabSearchParams(queryForm, BY_DEFINITION, resultCount);
        
        List<QSVocabResult> idMatches = qsFinder.getVocabResults(idSearch).getResultObjects();
        logger.info("Got " + idMatches.size() + " ID matches");
        List<QSVocabResult> termMatches = qsFinder.getVocabResults(termSearch).getResultObjects();
        logger.info("Got " + termMatches.size() + " term matches");
        List<QSVocabResult> synonymMatches = qsFinder.getVocabResults(synonymSearch).getResultObjects();
        logger.info("Got " + synonymMatches.size() + " synonym matches");
        List<QSVocabResult> definitionMatches = qsFinder.getVocabResults(definitionSearch).getResultObjects();
        logger.info("Got " + definitionMatches.size() + " definition matches");
        
        SearchParams anySearch = getVocabSearchParams(queryForm, BY_ANY, 0);

        int totalCount = qsFinder.getVocabResults(anySearch).getTotalCount();
        logger.info("Identified " + totalCount + " matches in all");

        List<QSVocabResult> out = unifyVocabMatches(queryForm.getTerms(), idMatches, termMatches, synonymMatches, definitionMatches);
        
        List<QSVocabResultWrapper> wrapped = new ArrayList<QSVocabResultWrapper>();
        for (QSVocabResult r : out) {
        	wrapped.add(new QSVocabResultWrapper(r));
        }
        
        JsonSummaryResponse<QSVocabResultWrapper> response = new JsonSummaryResponse<QSVocabResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(totalCount);
        logger.info("Returning " + wrapped.size() + " vocab matches");

        return response;
    }

	// Process the given parameters and return an appropriate SearchParams object (ready to use in a search).
	private SearchParams getVocabSearchParams (QuickSearchQueryForm qf, String queryMode, Integer resultCount) {
        Filter vocabFilter;
        
        if (BY_ID.equals(queryMode)) {
        	vocabFilter = createIDFilter(qf, true);
        } else if (BY_TERM.equals(queryMode)) {
        	vocabFilter = notEmaps(createContainsFilter(qf, SearchConstants.QS_TERM));
        } else if (BY_SYNONYM.equals(queryMode)) {
        	vocabFilter = notEmaps(createContainsFilter(qf, SearchConstants.QS_SYNONYM));
        } else if (BY_DEFINITION.equals(queryMode)) {
        	vocabFilter = notEmaps(createContainsFilter(qf, SearchConstants.QS_DEFINITION));
        } else {	// BY_ANY
        	Filter idFilter = createIDFilter(qf, true);
        	Filter termFilter = notEmaps(createContainsFilter(qf, SearchConstants.QS_TERM));
        	Filter synonymFilter = notEmaps(createContainsFilter(qf, SearchConstants.QS_SYNONYM));
        	Filter definitionFilter = notEmaps(createContainsFilter(qf, SearchConstants.QS_DEFINITION));
        	vocabFilter = this.orFilters(idFilter, termFilter, synonymFilter, definitionFilter);
        }
        
        Filter facetFilters = getFilterFacets(qf);
        if (facetFilters != null) {
        	List<Filter> filtered = new ArrayList<Filter>();
        	filtered.add(vocabFilter);
        	filtered.add(facetFilters);
        	vocabFilter = Filter.and(filtered);
        }

        Paginator page = new Paginator(resultCount);			// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);		// sort by descending Solr score (best first)
        List<Sort> sorts = new ArrayList<Sort>(1);
        sorts.add(byScore);

        SearchParams vocabSearch = new SearchParams();
        vocabSearch.setPaginator(page);
        vocabSearch.setFilter(vocabFilter);
        return vocabSearch;
	}
	
	// special case -- add a qualifier to the given filter (returning the resulting Filter) such that we
	// do not return documents that have a primary ID beginning with "EMAPS".
	private Filter notEmaps(Filter f) {
		List<Filter> combo = new ArrayList<Filter>(2);
		combo.add(f);
		combo.add(new Filter(SearchConstants.QS_RAW_VOCAB_NAME, "EMAPS", Operator.OP_NOT_EQUAL));
		return Filter.and(combo);
	}
	
	// Return a single filter that looks for vocab terms by the given field, with multiple terms joined by an OR.
	private Filter createContainsFilter(QuickSearchQueryForm qf, String fieldname) {
        List<Filter> termFilters = new ArrayList<Filter>();

        for (String term : qf.getTerms()) {
        	List<Filter> termSet = new ArrayList<Filter>();
        	termSet.add(new Filter(fieldname, term, Operator.OP_CONTAINS));
        	termFilters.add(Filter.or(termSet));
        }
        return Filter.or(termFilters);
	}
	
	// consolidate the lists of matching vocab termss, add star values, and setting best match values, then return
	private List<QSVocabResult> unifyVocabMatches (List<String> searchTerms, List<QSVocabResult> idMatches,
		List<QSVocabResult> termMatches, List<QSVocabResult> synonymMatches, List<QSVocabResult> definitionMatches) {
		
		Grouper<QSVocabResult> grouper = new Grouper<QSVocabResult>();
		
		// ID matches must be exact matches (aside from case sensitivity)
		
		for (QSVocabResult match : idMatches) {
			boolean found = false;
			for (String id : match.getAccID()) {
				for (String term : searchTerms) {
					if (term.equalsIgnoreCase(id)) {
						match.setBestMatchType("ID");
						match.setBestMatchText(id);
						match.setStars("****");
						grouper.add("****", match.getPrimaryID(), match, 500);
						found = true;
						break;
					}
				}
				if (found) { break; }
			}
			if (!found) {
				// should not happen, but let's make sure not to lose the result just in case
				match.setStars("*");
				grouper.add("*", match.getPrimaryID(), match, 0);
			}
		}

		// Term matches can be to symbol, name, or synonym.  Exact are 4-star, begins are 3-star, contains are 2-star.
		
		List<QSVocabResult> consolidatedMatches = new ArrayList<QSVocabResult>();
		consolidatedMatches.addAll(termMatches);
		consolidatedMatches.addAll(synonymMatches);
		consolidatedMatches.addAll(definitionMatches);

		BestMatchFinder bmf = new BestMatchFinder(searchTerms);
		for (QSVocabResult match : consolidatedMatches) {
			BestMatchOptions options = new BestMatchOptions();
			options.put(match.getTerm(), "Term");
			if (match.getSynonym() != null) {
				for (String synonym : match.getSynonym()) {
					options.put(synonym, "Synonym");
				}
			}
			if (match.getDefinition() != null) {
				options.put(match.getDefinition(), "Definition");
			}

			BestMatch bestMatch = bmf.getBestMatch(options);
			match.setStars(bestMatch.stars);
			match.setBestMatchText(bestMatch.matchText);
			match.setBestMatchType(bestMatch.matchType);

			grouper.add(bestMatch.stars, match.getPrimaryID(), match, bestMatch.boost);
		}

		return grouper.toList();
	}

    //-------------------------//
    // QS Results - bucket 3 JSON (accession ID matches)
    //-------------------------//
	@RequestMapping("/idBucket")
	public @ResponseBody JsonSummaryResponse<AccessionSummaryRow> getIDBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getIDBucket started");
        
        JsonSummaryResponse<AccessionSummaryRow> out = new JsonSummaryResponse<AccessionSummaryRow>();
        
        // handle multiple terms joined by commas or spaces
        String[] terms = queryForm.getQuery().replace(',', ' ').split(" ");

       	AccessionQueryForm accQF = new AccessionQueryForm();
        Paginator page = new Paginator(1000);
       	accQF.setFlag("QS");

        for (String term : terms) {
        	accQF.setId(term);
        	if (out.getTotalCount() == 0) {
        		out = accessionController.accessionSummaryJson(request, accQF, page);
        	} else {
        		// could merge sets to avoid duplication, but just append for simplicity for now
        		List<AccessionSummaryRow> oldResults = out.getSummaryRows();
        		out = accessionController.accessionSummaryJson(request, accQF, page);

        		oldResults.addAll(out.getSummaryRows());

        		out.setSummaryRows(oldResults);
        		out.setTotalCount(oldResults.size());
        	}
        }
        return out;
    }
	
	// private inner class for scoring / sorting QS results
	private class Grouper<T> {
		private class SortItem<T> {
			public T item;
			public long boost;
			
			public SortItem(T item, long boost) {
				this.item = item;
				this.boost = boost;
			}
			
			public Comparator<SortItem<T>> getComparator() {
				return new SIComparator();
			}
			
			private class SIComparator implements Comparator<SortItem<T>> {
				public int compare (SortItem<T> a, SortItem<T> b) {
					if (a.boost < b.boost) { return -1; }
					if (a.boost > b.boost) { return 1; }
					return a.toString().compareTo(b.toString());
				}
			}
		}
		List<SortItem<T>> fourStar;
		List<SortItem<T>> threeStar;
		List<SortItem<T>> twoStar;
		List<SortItem<T>> oneStar;
		Set<String> ids;
		
		public Grouper() {
			this.fourStar = new ArrayList<SortItem<T>>();
			this.threeStar = new ArrayList<SortItem<T>>();
			this.twoStar = new ArrayList<SortItem<T>>();
			this.oneStar = new ArrayList<SortItem<T>>();
			this.ids = new HashSet<String>();
		}
		
		public void add(String stars, String id, T item, long boost) {
			int starCount = stars.length();

			if (this.ids.contains(id)) { return; }
			
			if (starCount == 4) { this.fourStar.add(new SortItem<T>(item, boost)); }
			else if (starCount == 3) { this.threeStar.add(new SortItem<T>(item, boost)); }
			else if (starCount == 2) { this.twoStar.add(new SortItem<T>(item, boost)); }
			else { this.oneStar.add(new SortItem<T>(item, boost)); }

			this.ids.add(id);
		}
		
		private List<T> sortAndExtract(List<SortItem<T>> myList) {
			List<T> extracted = new ArrayList<T>(myList.size());
			if ((myList != null) && (myList.size() > 0)) {
				Collections.sort(myList, myList.get(0).getComparator());
				for (SortItem<T> element : myList) {
					extracted.add(element.item);
				}
			}
			return extracted;
		}
		
		public List<T> toList() {
			List<T> all = new ArrayList<T>();
			all.addAll(sortAndExtract(fourStar));
			all.addAll(sortAndExtract(threeStar));
			all.addAll(sortAndExtract(twoStar));
			all.addAll(sortAndExtract(oneStar));
			return all;
		}
	}
	
	private class BestMatchFinder {
		private String searchString;		// concatenation of lowerTerms, used for full-string matching
		private List<String> lowerTerms;	// words from user's search string
		private int searchTermCount = 0;	// number of words in user's search string
		
		public BestMatchFinder(List<String> searchTerms) {
			this.lowerTerms = new ArrayList<String>();
			for (String term : searchTerms) {
				this.lowerTerms.add(term.toLowerCase().replace("*", ""));
			}
			this.searchTermCount = this.lowerTerms.size();
			// Full search string should come in as the last of the search terms, per the QuickSearchQueryForm's API.
			this.searchString = this.lowerTerms.get(searchTermCount - 1);
		}
		
		// initial filtering; return a new map of options with all those that cannot match winnowed out
		private Map<String,String> winnow(BestMatchOptions options) {
			Map<String,String> potential = new HashMap<String,String>();

			for (String term : options.keySet()) {
				String termLower = term.toLowerCase();

				for (String searchWord : lowerTerms) {
					// Once we find a single search word that matches this term, keep the term and 
					// don't look for more.  If we don't find any, then it cannot become a "best match",
					// so leave it out.
					if (termLower.contains(searchWord)) {
						potential.put(term, options.get(term));
						break;
					}
				}
			}
			return potential;
		}
		
		// Find the best match (for the search terms included at instantiation) among the various options,
		// which map from a term to each one's term type.
		public BestMatch getBestMatch(BestMatchOptions options) {
			// Winnow things down so that the only options we consider are possibilities.
			Map<String,String> filteredOptions = this.winnow(options);

			// best match found so far
			BestMatch bestMatch = new BestMatch();
			bestMatch.starCount = 0;
			bestMatch.stars = "";
			bestMatch.boost = 0;
			bestMatch.matchText = "N/A";
			bestMatch.matchType = "Best Match";

			// iterate through each option for possible "best match" strings
			for (String key : filteredOptions.keySet()) {
				String keyLower = key.toLowerCase().trim();

				// data compiled about this possible "best match" string (on this loop iteration)
				BestMatch thisMatch = new BestMatch();
				thisMatch.starCount = 0;
				thisMatch.stars = "";
				thisMatch.boost = 0;
				thisMatch.matchText = key;
				thisMatch.matchType = options.get(key);

				/* New scoring considerations:
				 * 1. 4-star matches are to full string.
				 * 2. 3-star matches contain all words from the search string.
				 * 3. 2-star matches contain at least one of the words from the search string.
				 * 4. Multi-word matches must be boosted up in their star-tier, so we need to consider
				 * 	  all search words.  To accomplish this, here's the heuristic we're using:
				 * 		a. Keep a "boost" for each potential best match.
				 * 		b. A 4-star matching search word adds 500 to boost.  (keep at the top)
				 * 		c. Search words appearing in the string in the user's order are worth more (7)
				 * 			than search words that appear but are not in the user's order (5).  So two
				 * 			words in order are preferred over two out of order, but three out of order
				 * 			are better than two in order.
				 * 		d. Check to see if the count of matching terms matches the count of search terms (3-star)
				 * 			or not (only 2-star).
				 * 		e. Keep the highest star value among b-d to represent the whole potential best match.
				 * 		f. Return the best match that has the highest boost.
				 * 5. Of note, 85-90% of user searches are using a single word, meaning this logic is not
				 * 	  needed most of the time.
				 */
				if (keyLower.equals(this.searchString)) {
					// exact match to search string -- 4-star match, big boost
					thisMatch.starCount = 4;
					thisMatch.stars = "****";
					thisMatch.boost += 500;
				} else {
					int lastMatchPosition = -1;			// where was the last matching search term?
					int matchingWordCount = 0;			// number of words from the search string that appear in keyLower
				
					for (String term : lowerTerms) {
						int thisTermPosition = keyLower.indexOf(term);
						if (thisTermPosition >= 0) {
							if (thisTermPosition >= lastMatchPosition) {
								thisMatch.boost += 7;
							} else {
								thisMatch.boost += 5;
							}
							lastMatchPosition = thisTermPosition;
							matchingWordCount += 1;
						}
					}
					
					if (matchingWordCount == this.searchTermCount) {
						thisMatch.starCount = 3;
						thisMatch.stars = "***";
					} else {
						thisMatch.starCount = 2;
						thisMatch.stars = "**";
					}
				}
				
				if (thisMatch.starCount > bestMatch.starCount) {
					bestMatch = thisMatch;
				} else if ((thisMatch.starCount == bestMatch.starCount) && (thisMatch.boost > bestMatch.boost)) {
					bestMatch = thisMatch;
				}
			}
			return bestMatch;
		}
	}
	
	private class BestMatch {
		public int starCount;
		public String stars;
		public String matchType;
		public String matchText;
		public long boost;
	}
	
	// special extension of a HashMap that:
	// 1. keeps mixed case keys, but...
	// 2. does not add a new key/value pair if we already have that key (case insensitive)
	// So, if we already have "Kit" as a key, then we do not allow "KIT" or "kit" or "kiT" to be added, etc.
	private class BestMatchOptions {
		HashMap<String,String> options = new HashMap<String,String>();
		HashSet<String> lowerKeys = new HashSet<String>();
		
		public String put(String key, String value) {
			String lowerKey = key.toLowerCase();
			if (!lowerKeys.contains(lowerKey)) {
				lowerKeys.add(lowerKey);
				options.put(key, value);
				return value;
			}
			return null;
		}
		
		public int size() {
			return this.options.size();
		}
		
		public Set<String> keySet() {
			return options.keySet();
		}
		
		public String get(String key) {
			if (options.containsKey(key)) {
				return options.get(key);
			}
			return null;
		}
	}
}
