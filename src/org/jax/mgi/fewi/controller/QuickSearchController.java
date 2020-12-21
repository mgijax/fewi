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
import org.jax.mgi.fewi.summary.QSResult;
import org.jax.mgi.fewi.summary.QSStrainResult;
import org.jax.mgi.fewi.summary.QSStrainResultWrapper;
import org.jax.mgi.fewi.summary.QSVocabResultWrapper;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.LimitedSizeCache;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;
import org.jax.mgi.shr.fe.util.EasyStemmer;
import org.jax.mgi.shr.fe.util.StopwordRemover;
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

	private static int FEATURE = 1;			// constants for types of objects we work with
	private static int VOCAB_TERM = 2;
	private static int STRAIN = 3;

	private static String BY_EXACT_MATCH = "exact_match";		// requires exact matching
	private static String BY_INEXACT_MATCH = "inexact_match";	// allows partial matches after stopword removal
	private static String BY_STEMMED_MATCH = "stemmed match";	// matches after stemming and stopword removal
	private static String BY_ANY = "any match";	
	
	private static Set<String> validFacetFields;
	static {
		validFacetFields = new HashSet<String>();
		validFacetFields.add(SearchConstants.QS_GO_COMPONENT_FACETS);
		validFacetFields.add(SearchConstants.QS_GO_PROCESS_FACETS);
		validFacetFields.add(SearchConstants.QS_GO_FUNCTION_FACETS);
	}

	private static LimitedSizeCache<List<QSFeatureResult>> featureResultCache = new LimitedSizeCache<List<QSFeatureResult>>();
	private static LimitedSizeCache<List<QSVocabResult>> vocabResultCache = new LimitedSizeCache<List<QSVocabResult>>();
	private static LimitedSizeCache<List<QSStrainResult>> strainResultCache = new LimitedSizeCache<List<QSStrainResult>>();
	
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
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSFeatureResult> out = featureResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.info(" - got " + out.size() + " feature results from cache");
        	
        } else {
        	// The index has been rebuilt to instead have each document be a point of data that can be matched, so
        	// we now need to retrieve all matching documents and then process them.  Guessing too high a number of
        	// expected results can be detrimental to efficiency, so we can do an initial query to get the count
        	// and then a second query to return them.
        
        	SearchParams exactSearch = getSearchParams(queryForm, BY_EXACT_MATCH, 0, FEATURE);
        	SearchParams inexactSearch = getSearchParams(queryForm, BY_STEMMED_MATCH, 0, FEATURE);

        	SearchParams orSearch = new SearchParams();
        	orSearch.setPaginator(new Paginator(0));
        	List<Filter> either = new ArrayList<Filter>();
        	either.add(exactSearch.getFilter());
        	either.add(inexactSearch.getFilter());
        	orSearch.setFilter(Filter.or(either));
        
        	SearchResults<QSFeatureResult> eitherResults = qsFinder.getFeatureResults(orSearch);
        	Integer resultCount = eitherResults.getTotalCount();
        	logger.info("Identified " + resultCount + " feature matches");
        
        	// Now do the query to retrieve all results.
        	orSearch.setPaginator(new Paginator(resultCount));
        	List<QSFeatureResult> allMatches = qsFinder.getFeatureResults(orSearch).getResultObjects();
        	logger.info("Loaded " + allMatches.size() + " feature matches");
        
        	out = unifyFeatureMatches(queryForm.getTerms(), allMatches);
        	logger.info("Consolidated down to " + out.size() + " features");
        	
        	featureResultCache.put(cacheKey, out);
        	logger.info(" - added " + out.size() + " feature results to cache");
       	}
        
        List<QSFeatureResultWrapper> wrapped = new ArrayList<QSFeatureResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting features " + startIndex + " to " + Math.min(out.size(), endIndex));
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

	// distill the various facet parameters down to a single Filter (should work across both all QS buckets)
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
	
	// Return a single filter that looks for features using the exact field, with multiple terms joined by an OR.
	private Filter createExactTermFilter(QuickSearchQueryForm qf) {
        List<Filter> exactFilters = new ArrayList<Filter>();

        for (String term : qf.getTerms()) {
        	exactFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM_EXACT, term, Operator.OP_EQUAL));
        }
        return Filter.or(exactFilters);
	}
	
	// Return a single filter that looks for features using the inexact field, with multiple terms joined by an OR.
	// Removes stopwords.
	private Filter createInexactTermFilter(QuickSearchQueryForm qf, int bucket) {
        List<Filter> filters = new ArrayList<Filter>();
        StopwordRemover stopwordRemover = new StopwordRemover();

        for (String term : qf.getTerms()) {
        	term = stopwordRemover.remove(term);
        	if ((term != null) && (term.length() > 0)) {
        		filters.add(new Filter(SearchConstants.QS_SEARCH_TERM_INEXACT, term, Operator.OP_STRING_CONTAINS));
        	}
        }

        // All search terms were removed by stemming, so don't return anything for this.
        if (filters.size() == 0) {
        	filters.add(new Filter(SearchConstants.QS_SEARCH_TERM_INEXACT, "abcdefghijklmn", Operator.OP_EQUAL));
        }
        
        if (bucket == VOCAB_TERM) {
        	// When looking for vocab terms by the inexact field, we do not want EMAPS terms
        	return notEmaps(Filter.or(filters));
        }
        return Filter.or(filters);
	}
	
	// Return a single filter that looks for documents by any non-ID type of field, with multiple
	// terms joined by an OR.
	private Filter createStemmedTermFilter(QuickSearchQueryForm qf, int bucket) {
        List<Filter> termFilters = new ArrayList<Filter>();
        EasyStemmer stemmer = new EasyStemmer();
        StopwordRemover stopwordRemover = new StopwordRemover();

        for (String term : qf.getTerms()) {
        	term = stemmer.stemAll(stopwordRemover.remove(term));
        	if ((term != null) && (term.length() > 0)) {
        		termFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM_STEMMED, term, Operator.OP_CONTAINS_WITH_COLON));
        	}
        }
        
        // All search terms were removed by stemming, so don't return anything for this.
        if (termFilters.size() == 0) {
        	termFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM_STEMMED, "abcdefghijklmn", Operator.OP_EQUAL));
        }
        
        if (bucket == VOCAB_TERM) {
        	// When looking for vocab terms by term, synonym, or definition, we do not want EMAPS terms
        	return notEmaps(Filter.or(termFilters));
        }
        return Filter.or(termFilters);
	}
	
	// consolidate the lists of matching features, add star values, and setting best match values, then return
	private List<QSFeatureResult> unifyFeatureMatches (List<String> searchTerms, List<QSFeatureResult> allMatches) {
		List<QSResult> a = new ArrayList<QSResult>(allMatches.size());
		for (QSFeatureResult r : allMatches) {
			a.add((QSResult) r);
		}
		List<QSResult> unified = unifyMatches(searchTerms, a);
		
		List<QSFeatureResult> out = new ArrayList<QSFeatureResult>(unified.size());
		for (QSResult u : unified) {
			out.add((QSFeatureResult) u);
		}
		return out;
	}

	// consolidate the lists of matching vocab terms, add star values, and setting best match values, then return
	private List<QSVocabResult> unifyVocabMatches (List<String> searchTerms, List<QSVocabResult> allMatches) {
		List<QSResult> a = new ArrayList<QSResult>(allMatches.size());
		for (QSVocabResult r : allMatches) {
			a.add((QSResult) r);
		}
		List<QSResult> unified = unifyMatches(searchTerms, a);
		
		List<QSVocabResult> out = new ArrayList<QSVocabResult>(unified.size());
		for (QSResult u : unified) {
			out.add((QSVocabResult) u);
		}
		return out;
	}
	
	// consolidate the lists of matching strains, add star values, and setting best match values, then return
	private List<QSStrainResult> unifyStrainMatches (List<String> searchTerms, List<QSStrainResult> allMatches) {
		List<QSResult> a = new ArrayList<QSResult>(allMatches.size());
		for (QSStrainResult r : allMatches) {
			a.add((QSResult) r);
		}
		List<QSResult> unified = unifyMatches(searchTerms, a);
		
		List<QSStrainResult> out = new ArrayList<QSStrainResult>(unified.size());
		for (QSResult u : unified) {
			out.add((QSStrainResult) u);
		}
		return out;
	}
	
	// consolidate the lists of matching (abstract) QSResult objects, add star values, and setting best match values,
	// then return
	private List<QSResult> unifyMatches (List<String> searchTerms, List<QSResult> allMatches) {
		
		Grouper<QSResult> grouper = new Grouper<QSResult>();
		
		// original search term will be the last item in the list of search terms
		String originalSearchTerm = searchTerms.get(searchTerms.size() - 1).toLowerCase();
		
		// last search term is the full string, so this is the count of individual words
		int wordCount = searchTerms.size() - 1;
		
		// There may be multiple entries in allMatches for the same feature (marker or allele), so we need to
		// prioritize them and only choose the best.  To do this, we iterate through documents and:
		// 1. determine star tier (exact / all words / any word)
		// 2. if best tier so far for this primary ID, keep this one
		// 3. if same as best tier so far but this has a better weight (based on data type), keep this one
		
		Map<String,QSResult> bestMatches = new HashMap<String,QSResult>();
		for (QSResult match : allMatches) {
			String primaryID = match.getPrimaryID();
			String lowerDisplayTerm = match.getSearchTermDisplay().toLowerCase();
			String lowerTerm = null;
			
			// If the exact field is non-null, then we know we have an exact match to the search term,
			// aside from case sensitivity.
			if (match.getSearchTermExact() != null) {
				match.setStars("****");
				
			} else if (match.getSearchTermStemmed() != null) {
				lowerTerm = match.getSearchTermStemmed().toLowerCase();
			} else if (match.getSearchTermInexact() != null) {
				lowerTerm = match.getSearchTermInexact().toLowerCase();
			}
				
			if (lowerTerm != null) {
				// search terms can be exact (4-star), contain all terms (3-star), or contain some terms (2-star)
				if (lowerTerm.equals(originalSearchTerm) || lowerDisplayTerm.equals(originalSearchTerm)) {
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
					QSResult bestMatch = bestMatches.get(primaryID);
					
					if (match.getStarCount() > bestMatch.getStarCount()) {
						keepThisOne = true;
					} else if (match.getStarCount() == bestMatch.getStarCount()) {
						Integer mw = match.getSearchTermWeight();
						Integer bw = bestMatch.getSearchTermWeight();
						if (mw != null) {
							if ((bw == null) || (mw > bw)) {
								keepThisOne = true; 
							}
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
		
		logger.info("bestMatches.size() = " + bestMatches.size());
		// Now use the Grouper to divide up the best matches we found into their star buckets.
		for (String primaryID : bestMatches.keySet()) {
			QSResult bestMatch = bestMatches.get(primaryID);
			grouper.add(bestMatch.getStars(), primaryID, bestMatch, bestMatch.getSequenceNum());
		}

		return grouper.toList();
	}
	
	/* Get the set of GO Process filter options for the current result set, including facets from all QS buckets
	 */
	@RequestMapping("/featureBucket/process")
	public @ResponseBody Map<String, List<String>> getProcessFacet (@ModelAttribute QuickSearchQueryForm qf, HttpServletResponse response) throws Exception {
		AjaxUtils.prepareAjaxHeaders(response);
		return getFacets(qf, SearchConstants.QS_GO_PROCESS_FACETS);
	}

	/* Get the set of GO Function filter options for the current result set, including facets from all QS buckets
	 */
	@RequestMapping("/featureBucket/function")
	public @ResponseBody Map<String, List<String>> getFunctionFacet (@ModelAttribute QuickSearchQueryForm qf, HttpServletResponse response) throws Exception {
		AjaxUtils.prepareAjaxHeaders(response);
		return getFacets(qf, SearchConstants.QS_GO_FUNCTION_FACETS);
	}

	/* Get the set of GO Component filter options for the current result set, including facets from all QS buckets
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
		List<String> strainFacets = getStrainFacets(qf, facetField);
		List<String> resultList = unifyFacets(featureFacets, vocabFacets, strainFacets);
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
	
	/* Unify 3 groups of facets into a single, ordered list for return.  Modifies group1 by adding entries
	 * from group2 and group3 and then sorting.
	 */
	private List<String> unifyFacets(List<String> group1, List<String> group2, List<String> group3) {
		Set<String> seenIt = new HashSet<String>();

		if (group1 != null) {
			for (String item : group1) {
				seenIt.add(item);
			}
		} else {
			group1 = new ArrayList<String>();
		}
		
		List<List<String>> toUnify = new ArrayList<List<String>>();
		toUnify.add(group2);
		toUnify.add(group3);
		
		for (List<String> group : toUnify) {
			if (group != null) {
				for (String item : group) {
					if (!seenIt.contains(item)) {
						group1.add(item);
						seenIt.add(item);
					}
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
        
        SearchParams anySearch = getSearchParams(queryForm, BY_ANY, facetLimit, FEATURE);

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
        
        SearchParams anySearch = getSearchParams(queryForm, BY_ANY, facetLimit, VOCAB_TERM);

        List<String> resultList = null;		// list of strings, each a value for a facet
        
        if (validFacetFields.contains(filterName)) {
        	resultList = qsFinder.getVocabFacets(anySearch, filterName);
        } else {
        	throw new Exception("getVocabFacets: Invalid facet name: " + filterName);
        }
        return resultList;
	}
	
	/* Execute the search for facets for the strain bucket using the given filterName, returning them as an
	 * unordered list of strings.
	 */
	private List<String> getStrainFacets(QuickSearchQueryForm queryForm, String filterName) throws Exception {
        // match either ID, term, or synonyms
        
        SearchParams anySearch = getSearchParams(queryForm, BY_ANY, facetLimit, STRAIN);

        List<String> resultList = null;		// list of strings, each a value for a facet
        
        if (validFacetFields.contains(filterName)) {
        	resultList = qsFinder.getStrainFacets(anySearch, filterName);
        } else {
        	throw new Exception("getStrainFacets: Invalid facet name: " + filterName);
        }
        return resultList;
	}
	
    //-------------------------//
    // QS Results - bucket 2 JSON (vocab terms and annotations)
    //-------------------------//
	@RequestMapping("/vocabBucket")
	public @ResponseBody JsonSummaryResponse<QSVocabResultWrapper> getVocabBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm, @ModelAttribute Paginator page) {

        logger.info("->getVocabBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");

        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSVocabResult> out = vocabResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.info(" - got " + out.size() + " vocab results from cache");
        } else {
        	// The index has been rebuilt to instead have each document be a point of data that can be matched, so
        	// we now need to retrieve all matching documents and then process them.  Guessing too high a number of
        	// expected results can be detrimental to efficiency, so we can do an initial query to get the count
        	// and then a second query to return them.
        
        	SearchParams idSearch = getSearchParams(queryForm, BY_EXACT_MATCH, 0, VOCAB_TERM);
        	SearchParams nomenSearch = getSearchParams(queryForm, BY_STEMMED_MATCH, 0, VOCAB_TERM);

        	SearchParams orSearch = new SearchParams();
        	orSearch.setPaginator(new Paginator(0));
        	List<Filter> idOrNomen = new ArrayList<Filter>();
        	idOrNomen.add(idSearch.getFilter());
        	idOrNomen.add(nomenSearch.getFilter());
        	orSearch.setFilter(Filter.or(idOrNomen));
        
        	SearchResults<QSVocabResult> idOrNomenResults = qsFinder.getVocabResults(orSearch);
        	Integer resultCount = idOrNomenResults.getTotalCount();
        	logger.info("Identified " + resultCount + " term matches");

        	// Now do the query to retrieve all results.
        	orSearch.setPaginator(new Paginator(resultCount));
        	List<QSVocabResult> allMatches = qsFinder.getVocabResults(orSearch).getResultObjects();
        	logger.info("Loaded " + allMatches.size() + " term matches");
        
        	out = (List<QSVocabResult>) unifyVocabMatches(queryForm.getTerms(), allMatches);
        	logger.info("Consolidated down to " + out.size() + " terms");
        	
        	vocabResultCache.put(cacheKey, out);
        	logger.info(" - added " + out.size() + " vocab results to cache");
        }
        
        List<QSVocabResultWrapper> wrapped = new ArrayList<QSVocabResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting results " + startIndex + " to " + Math.min(out.size(), endIndex));
        	for (QSVocabResult r : out.subList(startIndex, Math.min(out.size(), endIndex))) {
        		wrapped.add(new QSVocabResultWrapper(r));
        	}
        } else { 
        	logger.debug(" - not extracting,just returning empty term list");
        }
        
        JsonSummaryResponse<QSVocabResultWrapper> response = new JsonSummaryResponse<QSVocabResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(out.size());
        logger.info("Returning " + wrapped.size() + " term matches");

        return response;
    }

    //-------------------------//
    // QS Results - bucket 3 JSON (strains)
    //-------------------------//
	@RequestMapping("/strainBucket")
	public @ResponseBody JsonSummaryResponse<QSStrainResultWrapper> getStrainBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm, @ModelAttribute Paginator page) {

        logger.info("->getStrainBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");

        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSStrainResult> out = strainResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.info(" - got " + out.size() + " strain results from cache");
        } else {
        	// The index has been rebuilt to instead have each document be a point of data that can be matched, so
        	// we now need to retrieve all matching documents and then process them.  Guessing too high a number of
        	// expected results can be detrimental to efficiency, so we can do an initial query to get the count
        	// and then a second query to return them.
        
        	SearchParams exactSearch = getSearchParams(queryForm, BY_EXACT_MATCH, 0, STRAIN);
        	SearchParams inexactSearch = getSearchParams(queryForm, BY_INEXACT_MATCH, 0, STRAIN);
        	SearchParams stemmedSearch = getSearchParams(queryForm, BY_STEMMED_MATCH, 0, STRAIN);

        	SearchParams orSearch = new SearchParams();
        	orSearch.setPaginator(new Paginator(0));
        	List<Filter> any = new ArrayList<Filter>();
        	any.add(exactSearch.getFilter());
        	any.add(inexactSearch.getFilter());
        	any.add(stemmedSearch.getFilter());
        	orSearch.setFilter(Filter.or(any));
        
        	SearchResults<QSStrainResult> anyResults = qsFinder.getStrainResults(orSearch);
        	Integer resultCount = anyResults.getTotalCount();
        	logger.info("Identified " + resultCount + " term matches");

        	// Now do the query to retrieve all results.
        	orSearch.setPaginator(new Paginator(resultCount));
        	List<QSStrainResult> allMatches = qsFinder.getStrainResults(orSearch).getResultObjects();
        	logger.info("Loaded " + allMatches.size() + " term matches");
        
        	out = (List<QSStrainResult>) unifyStrainMatches(queryForm.getTerms(), allMatches);
        	logger.info("Consolidated down to " + out.size() + " terms");
        	
        	strainResultCache.put(cacheKey, out);
        	logger.info(" - added " + out.size() + " strain results to cache");
        }
        
        List<QSStrainResultWrapper> wrapped = new ArrayList<QSStrainResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting results " + startIndex + " to " + Math.min(out.size(), endIndex));
        	for (QSStrainResult r : out.subList(startIndex, Math.min(out.size(), endIndex))) {
        		wrapped.add(new QSStrainResultWrapper(r));
        	}
        } else { 
        	logger.debug(" - not extracting,just returning empty term list");
        }
        
        JsonSummaryResponse<QSStrainResultWrapper> response = new JsonSummaryResponse<QSStrainResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(out.size());
        logger.info("Returning " + wrapped.size() + " term matches");

        return response;
    }

	// Process the given parameters and return an appropriate SearchParams object (ready to use in a search).
	private SearchParams getSearchParams (QuickSearchQueryForm qf, String queryMode, Integer resultCount, int bucket) {
        Filter myFilter;
        
        if (BY_EXACT_MATCH.equals(queryMode)) {
        	myFilter = createExactTermFilter(qf);
        } else if (BY_INEXACT_MATCH.equals(queryMode)) {
        	myFilter = createInexactTermFilter(qf, bucket);
        } else if (BY_STEMMED_MATCH.equals(queryMode)) {
        	myFilter = createStemmedTermFilter(qf, bucket);
        } else { 	// BY_ANY
        	List<Filter> orFilters = new ArrayList<Filter>(2);
        	orFilters.add(createExactTermFilter(qf));
        	orFilters.add(createInexactTermFilter(qf, bucket));
        	orFilters.add(createStemmedTermFilter(qf, bucket));
        	myFilter = Filter.or(orFilters);
        }
        
        Filter facetFilters = getFilterFacets(qf);
        if (facetFilters != null) {
        	List<Filter> filtered = new ArrayList<Filter>();
        	filtered.add(myFilter);
        	filtered.add(facetFilters);
        	myFilter = Filter.and(filtered);
        }

        Paginator page = new Paginator(resultCount);			// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);		// sort by descending Solr score (best first)
        List<Sort> sorts = new ArrayList<Sort>(1);
        sorts.add(byScore);

        SearchParams sp = new SearchParams();
        sp.setPaginator(page);
        sp.setFilter(myFilter);
        return sp;
	}

	// special case -- add a qualifier to the given filter (returning the resulting Filter) such that we
	// do not return documents that have a primary ID beginning with "EMAPS".
	private Filter notEmaps(Filter f) {
		List<Filter> combo = new ArrayList<Filter>(2);
		combo.add(f);
		combo.add(new Filter(SearchConstants.QS_RAW_VOCAB_NAME, "EMAPS", Operator.OP_NOT_EQUAL));
		return Filter.and(combo);
	}
	
	// Take the given query string and eliminate any pagination parameters, leaving those parameters that
	// uniquely identify the search itself (the query text plus any filters).  This can be used to identify
	// the set of search results for caching.
	private String withoutPagination(String queryString) {
		return queryString.replaceAll("&startIndex=[0-9]+", "").replaceAll("&results=[0-9]+", "");
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
			public long sequenceNum;
			
			public SortItem(T item, long sequenceNum) {
				this.item = item;
				this.sequenceNum = sequenceNum;
			}
			
			public Comparator<SortItem<T>> getComparator() {
				return new SIComparator();
			}
			
			private class SIComparator implements Comparator<SortItem<T>> {
				public int compare (SortItem<T> a, SortItem<T> b) {
					if (a.sequenceNum < b.sequenceNum) { return -1; }
					if (a.sequenceNum > b.sequenceNum) { return 1; }
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
		
		public void add(String stars, String id, T item, long sequenceNum) {
			int starCount = stars.length();

			if (this.ids.contains(id)) { return; }
			
			if (starCount == 4) { this.fourStar.add(new SortItem<T>(item, sequenceNum)); }
			else if (starCount == 3) { this.threeStar.add(new SortItem<T>(item, sequenceNum)); }
			else if (starCount == 2) { this.twoStar.add(new SortItem<T>(item, sequenceNum)); }
			else { this.oneStar.add(new SortItem<T>(item, sequenceNum)); }

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
}
