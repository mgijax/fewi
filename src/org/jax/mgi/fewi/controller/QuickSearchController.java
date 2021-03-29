package org.jax.mgi.fewi.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
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
import org.jax.mgi.fewi.summary.QSAlleleResult;
import org.jax.mgi.fewi.summary.QSAlleleResultWrapper;
import org.jax.mgi.fewi.summary.QSFeaturePart;
import org.jax.mgi.fewi.summary.QSVocabResult;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.summary.QSFeatureResultWrapper;
import org.jax.mgi.fewi.summary.QSResult;
import org.jax.mgi.fewi.summary.QSStrainResult;
import org.jax.mgi.fewi.summary.QSStrainResultWrapper;
import org.jax.mgi.fewi.summary.QSVocabResultWrapper;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.LimitedSizeCache;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.fe.IndexConstants;
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

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleSequenceAssociation;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceLocation;

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

	private static int SOLR_BATCH_SIZE = 150000;	// number of docs to retrieve from Solr per batch
	
	private static int FEATURE = 1;			// constants for types of objects we work with
	private static int VOCAB_TERM = 2;
	private static int STRAIN = 3;
	private static int ALLELE = 4;

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

	// Stemmed "words" that should not be matched alone in annotation-related fields (only in combination with
	// other "words").  (If matched alone, they return too many unhelpful matching documents.)
	private static Set<String> restrictedWords;
	static {
		restrictedWords = new HashSet<String>();
		restrictedWords.add("abnorm");
		restrictedWords.add("morpholog");
		restrictedWords.add("cell");
		restrictedWords.add("anomali");
		restrictedWords.add("gene");
		restrictedWords.add("system");
		restrictedWords.add("ani");
		restrictedWords.add("trap");
		restrictedWords.add("that");
		restrictedWords.add("activ");
	}
	
	// Types of vocabularies that have restricted fields.
	private static List<String> restrictedVocabs;
	static {
		restrictedVocabs = new ArrayList<String>();
		restrictedVocabs.add("Phenotype");
		restrictedVocabs.add("Function");
		restrictedVocabs.add("Process");
		restrictedVocabs.add("Component");
	}

	// Fields types for which we should exclude the restrictedWords as noted above (basically, annotations).
	private static List<String> restrictedTypes;
	static {
		restrictedTypes = new ArrayList<String>();
		for (String vocab : restrictedVocabs) {
			restrictedTypes.add(vocab);
			restrictedTypes.add(vocab + " Definition");
			restrictedTypes.add("Subterm " + vocab);
			restrictedTypes.add("Subterm " + vocab + " Definition");
		}
	}
	
	private static LimitedSizeCache<List<QSFeatureResult>> featureResultCache = new LimitedSizeCache<List<QSFeatureResult>>();
	private static LimitedSizeCache<List<QSVocabResult>> vocabResultCache = new LimitedSizeCache<List<QSVocabResult>>();
	private static LimitedSizeCache<List<QSStrainResult>> strainResultCache = new LimitedSizeCache<List<QSStrainResult>>();
	private static LimitedSizeCache<List<QSAlleleResult>> alleleResultCache = new LimitedSizeCache<List<QSAlleleResult>>();
	
    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(QuickSearchController.class);

    @Autowired
    private AccessionController accessionController;

    @Autowired
    private GXDController gxdController;

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
        logger.debug("->getQSMainPage started");

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
        logger.debug("->getQSSummary started");

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

        logger.debug("->getFeatureBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");
        
        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSFeatureResult> out = featureResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.debug(" - got " + out.size() + " feature results from cache");
        	
        } else {
        	// The index has been rebuilt to instead have each document be a point of data that can be matched, so
        	// we now need to retrieve all matching documents and then process them.  Guessing too high a number of
        	// expected results can be detrimental to efficiency, so we can do an initial query to get the count
        	// and then a second query to return them.
        
        	SearchParams exactSearch = getSearchParams(queryForm, BY_EXACT_MATCH, 0, FEATURE);
        	SearchParams inexactSearch = getSearchParams(queryForm, BY_INEXACT_MATCH, 0, FEATURE);
        	SearchParams stemmedSearch = getSearchParams(queryForm, BY_STEMMED_MATCH, 0, FEATURE);

        	SearchParams orSearch = new SearchParams();
        	orSearch.setPaginator(new Paginator(0));
        	List<Filter> either = new ArrayList<Filter>();
        	either.add(exactSearch.getFilter());
        	either.add(inexactSearch.getFilter());
        	either.add(stemmedSearch.getFilter());
        	orSearch.setFilter(Filter.or(either));
        
        	// Begin monitoring when we get our count.
        	String key = FewiUtil.startMonitoring("QS Feature Search", queryForm.toString());
        	
        	SearchResults<QSFeatureResult> eitherResults = qsFinder.getFeatureResults(orSearch);
        	Integer resultCount = eitherResults.getTotalCount();
        	logger.info("> Identified " + resultCount + " feature matches");
        
        	// Now that we know how many results we're looking for, we can retrieve them in batches.
        	
        	int start = 0;
        	out = new ArrayList<QSFeatureResult>();

        	Paginator batch = new Paginator(SOLR_BATCH_SIZE);
        	if (resultCount < SOLR_BATCH_SIZE) {
        		batch.setResults(resultCount);
        	}

        	while (start < resultCount) {
        		logger.info("> Seeking from " + start + " to " + (start + batch.getResults()));

        		batch.setStartIndex(start);
        		orSearch.setPaginator(batch);
        		
        		// Now do the query to retrieve all results.
        		List<QSFeatureResult> allMatches = null;
        	
        		try {
        			allMatches = qsFinder.getFeatureResults(orSearch).getResultObjects();
        		} catch (Exception e) {
        			// record the failure and return empty results
        			FewiUtil.failMonitoring(key, e.toString());
        			allMatches = new ArrayList<QSFeatureResult>();
        			logger.debug("Caught exception: " + e.toString());
        		}

        		logger.debug("Found " + allMatches.size() + " feature matches");
        
        		out = unifyFeatureMatches(queryForm.getTerms(), allMatches, out);
        		logger.debug("Currently tracking " + out.size() + " features");
        		
        		start = start + SOLR_BATCH_SIZE;
        	}

        	// stop monitoring when we reach the end
       		FewiUtil.endMonitoring(key);

        	featureResultCache.put(cacheKey, out);
        	logger.debug(" - added " + out.size() + " feature results to cache");
       	}
        
        List<QSFeatureResultWrapper> wrapped = new ArrayList<QSFeatureResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting features " + startIndex + " to " + Math.min(out.size(), endIndex));
        	wrapped = wrapFeatureResults(out.subList(startIndex, Math.min(out.size(), endIndex)));
        } else { 
        	logger.debug(" - not extracting,just returning empty list");
        }
        
        JsonSummaryResponse<QSFeatureResultWrapper> response = new JsonSummaryResponse<QSFeatureResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(out.size());
        logger.debug("Returning " + wrapped.size() + " feature matches");

        return response;
    }

	/* Take a list of QSFeatureResult objects (that are missing crucial information for display), look up the remaining
	 * data from a memory cache (retrieving additional data as needed), and wrap them up in objects for display.
	 */
	private List<QSFeatureResultWrapper> wrapFeatureResults(List<QSFeatureResult> results) {
		logger.debug("Wrapping " + results.size() + " feature results");
		Map<String,QSFeaturePart> cachedData = qsFinder.getMarkerParts(results);
        List<QSFeatureResultWrapper> wrapped = new ArrayList<QSFeatureResultWrapper>();

       	for (QSFeatureResult r : results) {
       		if (cachedData.containsKey(r.getPrimaryID())) {
       			QSFeaturePart part = cachedData.get(r.getPrimaryID());
       			if (part != null) {
       				r.setSymbol(part.getSymbol());
       				r.setName(part.getName());
       				r.setChromosome(part.getChromosome());
       				r.setLocation(part.getLocation());
       				r.setStrand(part.getStrand());
       			} else {
       				logger.debug("Cannot find " + r.getPrimaryID());
       			}
       		}
       		wrapped.add(new QSFeatureResultWrapper(r));
       	}
		return wrapped;
	}
	
	/* Take a list of QSAlleleResult objects (that are missing crucial information for display), look up the remaining
	 * data from a memory cache (retrieving additional data as needed), and wrap them up in objects for display.
	 */
	private List<QSAlleleResultWrapper> wrapAlleleResults(List<QSAlleleResult> results) {
		logger.debug("Wrapping " + results.size() + " allele results");
		Map<String,QSFeaturePart> cachedData = qsFinder.getAlleleParts(results);
        List<QSAlleleResultWrapper> wrapped = new ArrayList<QSAlleleResultWrapper>();

       	for (QSAlleleResult r : results) {
       		if (cachedData.containsKey(r.getPrimaryID())) {
       			QSFeaturePart part = cachedData.get(r.getPrimaryID());
       			if (part != null) {
       				r.setSymbol(part.getSymbol());
       				r.setName(part.getName());
       				r.setChromosome(part.getChromosome());
       				r.setLocation(part.getLocation());
       				r.setStrand(part.getStrand());
       			} else {
       				logger.debug("Cannot find " + r.getPrimaryID());
       			}
       		}
       		wrapped.add(new QSAlleleResultWrapper(r));
       	}
		return wrapped;
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

        // If we are searching in the restrictedTypes of data, we need to consider what to do with restrictedWords.
        // These are words (really word stems) that are so overwhelmingly common that we don't want to match them
        // solo to a field.  Rules:
        // 1. A restrictedWord can be matched solo to data types that are not in the restrictedTypes.
        // 2. Two or more consecutive restrictedWords can be matched to data types in the restrictedTypes.
        // 3. One or more restrictedWords can be matched to data types in the restrictedTypes -- as long as there
        //    is also a non-restrictedWord present in the same field.  (In reality, we can ignore these instances
        //    of restrictedWords because the document would match due to the presence of a non-restrictedWord.)
        // 4. Non-restrictedWords can be matched to any type of data.

        // Note: The full [potentially] multi-token query string is in the last position of qf.getTerms().
        
        String lastRestrictedWord = null; 
        
        for (String term : qf.getTerms()) {
        	term = stemmer.stemAll(stopwordRemover.remove(term));
        	if ((term != null) && (term.length() > 0)) {

        		// case 3 and case 4 (see comments above)
        		if (!restrictedWords.contains(term)) {
        			termFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM_STEMMED, term, Operator.OP_CONTAINS_WITH_COLON)); 
        			lastRestrictedWord = null;
        		}

        		// case 1 (see above)
        		else if (lastRestrictedWord == null) {
        			lastRestrictedWord = term;				// Remember this in case the next is restricted too.
        			List<Filter> f = new ArrayList<Filter>();
        			f.add(new Filter(SearchConstants.QS_SEARCH_TERM_STEMMED, term, Operator.OP_CONTAINS_WITH_COLON)); 
        			f.add(Filter.notIn(SearchConstants.QS_SEARCH_TERM_TYPE, restrictedTypes));
        			termFilters.add(Filter.and(f));
        		}

        		// case 2 (see above)
        		else {
        			lastRestrictedWord = lastRestrictedWord + " " + term;
        			termFilters.add(new Filter(SearchConstants.QS_SEARCH_TERM_STEMMED, lastRestrictedWord, Operator.OP_CONTAINS_WITH_COLON)); 
        			lastRestrictedWord = term;				// Remember this in case the next is restricted too.
        		}
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
	private List<QSFeatureResult> unifyFeatureMatches (List<String> searchTerms, List<QSFeatureResult> allMatches, List<QSFeatureResult> bestSoFar) {
		Map<String,QSResult> bestMatches = new HashMap<String,QSResult>();
		for (QSFeatureResult b : bestSoFar) {
			bestMatches.put(b.getPrimaryID(), (QSResult) b);
		}
		
		List<QSResult> a = new ArrayList<QSResult>(allMatches.size());
		for (QSFeatureResult r : allMatches) {
			a.add((QSResult) r);
		}
		List<QSResult> unified = unifyMatches(searchTerms, a, bestMatches);
		
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
		List<QSResult> unified = unifyMatches(searchTerms, a, null);
		
		List<QSVocabResult> out = new ArrayList<QSVocabResult>(unified.size());
		for (QSResult u : unified) {
			out.add((QSVocabResult) u);
		}
		return out;
	}
	
	// consolidate the lists of matching alleles, add star values, and setting best match values, then return
	private List<QSAlleleResult> unifyAlleleMatches (List<String> searchTerms, List<QSAlleleResult> allMatches, List<QSAlleleResult> bestSoFar) {
		Map<String,QSResult> bestMatches = new HashMap<String,QSResult>();
		for (QSAlleleResult b : bestSoFar) {
			bestMatches.put(b.getPrimaryID(), (QSResult) b);
		}
		
		List<QSResult> a = new ArrayList<QSResult>(allMatches.size());
		for (QSAlleleResult r : allMatches) {
			a.add((QSResult) r);
		}
		List<QSResult> unified = unifyMatches(searchTerms, a, bestMatches);
		
		List<QSAlleleResult> out = new ArrayList<QSAlleleResult>(unified.size());
		for (QSResult u : unified) {
			out.add((QSAlleleResult) u);
		}
		return out;
	}
	
	// consolidate the lists of matching strains, add star values, and setting best match values, then return
	private List<QSStrainResult> unifyStrainMatches (List<String> searchTerms, List<QSStrainResult> allMatches) {
		List<QSResult> a = new ArrayList<QSResult>(allMatches.size());
		for (QSStrainResult r : allMatches) {
			a.add((QSResult) r);
		}
		List<QSResult> unified = unifyMatches(searchTerms, a, null);
		
		List<QSStrainResult> out = new ArrayList<QSStrainResult>(unified.size());
		for (QSResult u : unified) {
			out.add((QSStrainResult) u);
		}
		return out;
	}
	
	// run the stemmer on each string in the list, and remove any stopwords
	private List<String> stemAndRemoveStopwords(List<String> searchTerms) {
        EasyStemmer stemmer = new EasyStemmer();
        StopwordRemover stopwordRemover = new StopwordRemover();
		List<String> out = new ArrayList<String>(searchTerms.size());

		for (String term : searchTerms) {
			String trim = stemmer.stemAll(stopwordRemover.remove(term));
			if ((trim != null) && (trim.length() > 0)) {
				out.add(trim);
			}
		}
		return out;
	}
	
	// determine if s can be converted to an integer (true) or not (false)
	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	// consolidate the lists of matching (abstract) QSResult objects, add star values, and setting best match values,
	// then return.  bestMatches can be passed in for processing batches via multiple invocations; if null, it will be
	// instantiated herein.
	private List<QSResult> unifyMatches (List<String> searchTerms, List<QSResult> allMatches, Map<String,QSResult> bestMatches) {
		// original search term will be the last item in the list of search terms
		String originalSearchTerm = searchTerms.get(searchTerms.size() - 1).toLowerCase();
		
		// search terms with stemming and stopword removal
		List<String> stemmedSearchTerms = stemAndRemoveStopwords(searchTerms);
		
		// Note: For search terms that are numbers, we only want those numbers to match whole words.
		// (For example, "4" should not match "14" or "41" or "141".)  To do this, we'll need to do regex
		// comparisons based on word boundaries (\b).  So, need to have two lists for comparison -- one using
		// String indexOf and the other with regex matches.
		
		List<String> nonStemmedTerms = new ArrayList<String>();			// non-numeric, non-stemmed terms to match
		List<String> stemmedTerms = new ArrayList<String>();			// non-numeric, stemmed terms to match
		List<Pattern> numericPatterns = new ArrayList<Pattern>();		// numeric terms to match
		List<String> toCheckOrder = searchTerms;						// terms to check for ordering
		
		// Take the original list of search terms and split the list into a non-numeric part and a numeric part,
		// the latter of which will be examined using word boundaries.
		for (String term : searchTerms) {
			if (isInteger(term)) {
				numericPatterns.add(Pattern.compile("\\b" + term + "\\b"));
			} else {
				nonStemmedTerms.add(term);
			}
		}
		
		// Go through the list of stemmed search terms and just keep the non-numerics, as we already have the
		// numeric list of Patterns (which wouldn't change due to stemming).
		for (String term : stemmedSearchTerms) {
			if (!isInteger(term)) {
				stemmedTerms.add(term);
			}
		}
		
		// last search term is the full string, so this is the count of individual words
		int wordCount = searchTerms.size() - 1;
		
		// There may be multiple entries in allMatches for the same feature (marker or allele), so we need to
		// prioritize them and only choose the best.  To do this, we iterate through documents and:
		// 1. determine star tier (exact / all words / any word)
		// 2. if best tier so far for this primary ID, keep this one
		// 3. if same as best tier so far but this has a better weight (based on data type), keep this one
		// 3a. Apply a minor boost to weight for cases where one of the words in the search string matches the
		//		start of the indexed string.  (This moves those matches up when choosing the Best Match and when
		//		displaying results in the QS.
		// 3b. Apply a minor boost to weight for cases where the whole search string is not an exact match to
		//		the indexed string but where the indexed string contains the words from the search string in the
		//		proper order.  (This also moves those matches up when choosing the Best Match and when displaying
		//		results in the QS.)
		
		if (bestMatches == null) {
			bestMatches = new HashMap<String,QSResult>();
		}
		for (QSResult match : allMatches) {
			String primaryID = match.getPrimaryID();
			String lowerDisplayTerm = match.getSearchTermDisplay().toLowerCase();
			String lowerTerm = null;
			List<String> termsToCheck = nonStemmedTerms;		// assume we compare with non-stemmed set of terms
			
			// For alleles, the marker symbol field is limited to 3-stars.
			boolean limitedType = "Marker Symbol".equals(match.getSearchTermType());
			
			// If the exact field is non-null, then we know we have an exact match to the search term,
			// aside from case sensitivity.
			if (match.getSearchTermExact() != null) {
				lowerTerm = match.getSearchTermExact().toLowerCase();
				
			} else if (match.getSearchTermStemmed() != null) {
				lowerTerm = match.getSearchTermStemmed().toLowerCase();
				termsToCheck = stemmedTerms;
				toCheckOrder = stemmedSearchTerms;

			} else if (match.getSearchTermInexact() != null) {
				lowerTerm = match.getSearchTermInexact().toLowerCase();
			}
			
			// boost to be applied to the weight (based on whether a match is to the start of the string)
			int prefixBoost = 0;	
			
			// boost to be applied to the weight (based on whether the indexed string contains all terms in the right order)
			int inOrderBoost = 0;	
			
			if (lowerTerm != null) {
				// search terms can be exact (4-star), contain all terms (3-star), or contain some terms (2-star)
				if (!limitedType && (lowerTerm.equals(originalSearchTerm) || lowerDisplayTerm.equals(originalSearchTerm))) {
					match.setStars("****");
				} else {
					int matchCount = 0;

					// check non-numeric search terms
					for (String word : termsToCheck) {
						if (lowerTerm.indexOf(word) >= 0) {
							matchCount++;
							
							if (lowerTerm.indexOf(word) == 0) {
								prefixBoost = 5;
							}
						}
					}

					// then check numeric search terms
					for (Pattern number : numericPatterns) {
						Matcher matcher = number.matcher(lowerTerm);
						if (matcher.find()) {
							matchCount++;
							
							if (matcher.start() == 0) {
								prefixBoost = 5;
							}
						}
					}

					if (matchCount >= wordCount) {
						match.setStars("***");
					} else {
						match.setStars("**");
					}
					
					// Only need to worry about the inOrderBoost if the user entered more than one word in the
					// search phrase.
					if (toCheckOrder.size() > 1) {
						toCheckOrder = toCheckOrder.subList(0, toCheckOrder.size() - 1);
						int lastTermIndex = -10;		// index of the last term matched
						inOrderBoost = 8;				// assume terms matched in order, larger boost than prefix
					
						for (String term : toCheckOrder) {
							int termIndex = lowerTerm.indexOf(term);
							if (termIndex < lastTermIndex) {
								// Found a word that was in the wrong order, so take away the boost.
								inOrderBoost = 0;
								break;
							}
							lastTermIndex = termIndex;
						}
					}
				}
			}
			
			// We'll double check that we identified at least one star, just in case something slipped through.  If
			// one has no stars, we just skip it.
			if (match.getStarCount() > 0) {
				boolean keepThisOne = false;		// Is this our best match so far for this feature?
				
				match.addBoost(prefixBoost);
				match.addBoost(inOrderBoost);

				// If we've already seen this feature, then we only want to keep this as the best match if:
				// 1. it has a higher star count than the previous best match, or
				// 2. it has the same star count as the previous best match and a larger weight (plus boosts).
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
// useful for debugging; just comment out for now...
//					match.setSearchTermDisplay(match.getSearchTermDisplay() + " " + match.getSearchTermWeight());
					bestMatches.put(primaryID, match);
				}
			}
		}
		
		logger.debug("bestMatches.size() = " + bestMatches.size());

		// Now use the Grouper to divide up the best matches we found into their star buckets.
		Grouper<QSResult> grouper = new Grouper<QSResult>();

		for (String primaryID : bestMatches.keySet()) {
			QSResult bestMatch = bestMatches.get(primaryID);
			grouper.add(bestMatch.getStars(), primaryID, bestMatch, bestMatch.getSearchTermWeight(), bestMatch.getSequenceNum());
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
		List<String> alleleFacets = getAlleleFacets(qf, facetField);
		List<String> resultList = unifyFacets(featureFacets, vocabFacets, strainFacets, alleleFacets);
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
	 * from the other groups and then sorting.
	 */
	private List<String> unifyFacets(List<String> group1, List<String> group2, List<String> group3, List<String> group4) {
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
		toUnify.add(group4);
		
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
	
	/* Execute the search for facets for the allele bucket using the given filterName, returning them as an
	 * unordered list of strings.
	 */
	private List<String> getAlleleFacets(QuickSearchQueryForm queryForm, String filterName) throws Exception {
        // match either ID, term, or synonyms
        
        SearchParams anySearch = getSearchParams(queryForm, BY_ANY, facetLimit, ALLELE);

        List<String> resultList = null;		// list of strings, each a value for a facet
        
        if (validFacetFields.contains(filterName)) {
        	resultList = qsFinder.getAlleleFacets(anySearch, filterName);
        } else {
        	throw new Exception("getAlleleFacets: Invalid facet name: " + filterName);
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

        logger.debug("->getVocabBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");

        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSVocabResult> out = vocabResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.debug(" - got " + out.size() + " vocab results from cache");
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
        	logger.debug("Identified " + resultCount + " term matches");

        	// Now do the query to retrieve all results.
        	String key = FewiUtil.startMonitoring("QS Vocab Term Search", queryForm.toString());
        	orSearch.setPaginator(new Paginator(resultCount));
        	List<QSVocabResult> allMatches = qsFinder.getVocabResults(orSearch).getResultObjects();
        	logger.debug("Loaded " + allMatches.size() + " term matches");
        	FewiUtil.endMonitoring(key);
        
        	out = (List<QSVocabResult>) unifyVocabMatches(queryForm.getTerms(), allMatches);
        	logger.debug("Consolidated down to " + out.size() + " terms");
        	
        	vocabResultCache.put(cacheKey, out);
        	logger.debug(" - added " + out.size() + " vocab results to cache");
        }
        
        List<QSVocabResultWrapper> wrapped = new ArrayList<QSVocabResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting results " + startIndex + " to " + Math.min(out.size(), endIndex));
        	for (QSVocabResult r : out.subList(startIndex, Math.min(out.size(), endIndex))) {

        		// For EMAPA and EMAPS terms, we need to retrieve and remember annotation counts.
        		if ("<<gxdCount>>".equals(r.getAnnotationText())) {
        			Integer resultCount = gxdController.getResultCountForID(r.getPrimaryID());
        			logger.debug("count for " + r.getPrimaryID() + ": " + resultCount);
        			if (resultCount > 0) {
        				String s = "";
        				if (resultCount > 1) {
        					s = "s";
        				}
        				r.setAnnotationCount(new Long(resultCount));
        				r.setAnnotationText(resultCount + " gene expression result" + s);
        			} else {
        				r.setAnnotationCount(0L);
        				r.setAnnotationText(null);
        			}
        		}

        		wrapped.add(new QSVocabResultWrapper(r));
        	}
        } else { 
        	logger.debug(" - not extracting,just returning empty term list");
        }
        
        JsonSummaryResponse<QSVocabResultWrapper> response = new JsonSummaryResponse<QSVocabResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(out.size());
        logger.debug("Returning " + wrapped.size() + " term matches");

        return response;
    }

    //-------------------------//
    // QS Results - bucket 3 JSON (strains)
    //-------------------------//
	@RequestMapping("/strainBucket")
	public @ResponseBody JsonSummaryResponse<QSStrainResultWrapper> getStrainBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm, @ModelAttribute Paginator page) {

        logger.debug("->getStrainBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");

        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSStrainResult> out = strainResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.debug(" - got " + out.size() + " strain results from cache");
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
        	logger.debug("Identified " + resultCount + " strain matches");

        	// Now do the query to retrieve all results.
        	String key = FewiUtil.startMonitoring("QS Strain Search", queryForm.toString());
        	orSearch.setPaginator(new Paginator(resultCount));
        	List<QSStrainResult> allMatches = qsFinder.getStrainResults(orSearch).getResultObjects();
        	logger.debug("Loaded " + allMatches.size() + " strain matches");
        	FewiUtil.endMonitoring(key);
        
        	out = (List<QSStrainResult>) unifyStrainMatches(queryForm.getTerms(), allMatches);
        	logger.debug("Consolidated down to " + out.size() + " strains");
        	
        	strainResultCache.put(cacheKey, out);
        	logger.debug(" - added " + out.size() + " strain results to cache");
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
        logger.debug("Returning " + wrapped.size() + " term matches");

        return response;
    }

    //-------------------------//
    // QS Results - bucket 4 JSON (alleles)
    //-------------------------//
	@RequestMapping("/alleleBucket")
	public @ResponseBody JsonSummaryResponse<QSAlleleResultWrapper> getAlleleBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm, @ModelAttribute Paginator page) {

        logger.debug("->getAlleleBucket started (seeking results " + page.getStartIndex() + " to " + (page.getStartIndex() + page.getResults()) + ")");

        int startIndex = page.getStartIndex();
        int endIndex = startIndex + page.getResults();
        
        String cacheKey = withoutPagination(request.getQueryString());
        List<QSAlleleResult> out = alleleResultCache.get(cacheKey);
        
        if (out != null) {
        	logger.debug(" - got " + out.size() + " allele results from cache");
        } else {
        	// The index has been rebuilt to instead have each document be a point of data that can be matched, so
        	// we now need to retrieve all matching documents and then process them.  Guessing too high a number of
        	// expected results can be detrimental to efficiency, so we can do an initial query to get the count
        	// and then a second query to return them.
        
        	SearchParams exactSearch = getSearchParams(queryForm, BY_EXACT_MATCH, 0, ALLELE);
        	SearchParams inexactSearch = getSearchParams(queryForm, BY_INEXACT_MATCH, 0, ALLELE);
        	SearchParams stemmedSearch = getSearchParams(queryForm, BY_STEMMED_MATCH, 0, ALLELE);

        	SearchParams orSearch = new SearchParams();
        	orSearch.setPaginator(new Paginator(0));
        	List<Filter> any = new ArrayList<Filter>();
        	any.add(exactSearch.getFilter());
        	any.add(inexactSearch.getFilter());
        	any.add(stemmedSearch.getFilter());
        	orSearch.setFilter(Filter.or(any));
        
        	SearchResults<QSAlleleResult> anyResults = qsFinder.getAlleleResults(orSearch);
        	Integer resultCount = anyResults.getTotalCount();
        	logger.debug("> Identified " + resultCount + " allele matches");

        	// Now that we know how many results we're looking for, we can retrieve them in batches.
        	String key = FewiUtil.startMonitoring("QS Allele Search", queryForm.toString());
        	int start = 0;
        	out = new ArrayList<QSAlleleResult>();

        	Paginator batch = new Paginator(SOLR_BATCH_SIZE);
        	if (resultCount < SOLR_BATCH_SIZE) {
        		batch.setResults(resultCount);
        	}
        	
        	while (start < resultCount) {
        		logger.info("> Seeking from " + start + " to " + (start + batch.getResults()));

        		batch.setStartIndex(start);
        		orSearch.setPaginator(batch);

        		// Now do the query to retrieve all results.
        		List<QSAlleleResult> allMatches = null;

        		try {
        			allMatches = qsFinder.getAlleleResults(orSearch).getResultObjects();
        		} catch (Exception e) {
        			// record the failure and return empty results
        			FewiUtil.failMonitoring(key, e.toString());
        			allMatches = new ArrayList<QSAlleleResult>();
        			logger.debug("Caught exception: " + e.toString());
        		}

        		logger.debug("Found " + allMatches.size() + " allele matches");

        		out = unifyAlleleMatches(queryForm.getTerms(), allMatches, out);
        		logger.debug("Currently tracking " + out.size() + " alleles");

        		start = start + SOLR_BATCH_SIZE;
        	}

        	// stop monitoring when we reach the end
       		FewiUtil.endMonitoring(key);
        	
        	alleleResultCache.put(cacheKey, out);
        	logger.debug(" - added " + out.size() + " allele results to cache");
        }
        
        List<QSAlleleResultWrapper> wrapped = new ArrayList<QSAlleleResultWrapper>();
        if (out.size() >= startIndex) {
        	logger.debug(" - extracting results " + startIndex + " to " + Math.min(out.size(), endIndex));
        	wrapped = wrapAlleleResults(out.subList(startIndex, Math.min(out.size(), endIndex)));
        } else { 
        	logger.debug(" - not extracting,just returning empty term list");
        }
        
        JsonSummaryResponse<QSAlleleResultWrapper> response = new JsonSummaryResponse<QSAlleleResultWrapper>();
        response.setSummaryRows(wrapped);
        response.setTotalCount(out.size());
        logger.debug("Returning " + wrapped.size() + " term matches");

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

        logger.debug("->getIDBucket started");
        
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
	
	// private inner class for scoring / sorting QS results.  We use a 3-level sort:  star rating,
	// weight of matched item (from indexer), and sequence num (smart-alpha, from indexer)
	private class Grouper<T> {
		private class SortItem<T> {
			public T item;
			public Integer weight = 0;
			public long sequenceNum;
			
			public SortItem(T item, Integer weight, long sequenceNum) {
				this.item = item;
				this.weight = weight;
				this.sequenceNum = sequenceNum;
			}
			
			public Comparator<SortItem<T>> getComparator() {
				return new SIComparator();
			}
			
			private class SIComparator implements Comparator<SortItem<T>> {
				public int compare (SortItem<T> a, SortItem<T> b) {
					if (a.weight < b.weight) { return 1; }
					if (a.weight > b.weight) { return -1; }
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
		
		public void add(String stars, String id, T item, Integer weight, long sequenceNum) {
			int starCount = stars.length();

			if (this.ids.contains(id)) { return; }
			
			if (starCount == 4) { this.fourStar.add(new SortItem<T>(item, weight, sequenceNum)); }
			else if (starCount == 3) { this.threeStar.add(new SortItem<T>(item, weight, sequenceNum)); }
			else if (starCount == 2) { this.twoStar.add(new SortItem<T>(item, weight, sequenceNum)); }
			else { this.oneStar.add(new SortItem<T>(item, weight, sequenceNum)); }

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
