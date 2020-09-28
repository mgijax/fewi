package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.AccessionSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.QSVocabResult;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(QuickSearchController.class);

    @Autowired
    private AccessionController accessionController;

    @Autowired
    private QuickSearchFinder qsFinder;
    
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
	public @ResponseBody JsonSummaryResponse<QSFeatureResult> getFeatureBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getFeatureBucket started");
        
        Paginator page = new Paginator(1500);				// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);	// sort by descending Solr score (best first)

        List<String> terms = new ArrayList<String>();
        for (String term : queryForm.getQuery().replace(',', ' ').split(" ")) {
        	terms.add(term);
        }
        terms.add(queryForm.getQuery());
        
        // Search in order of priority:  ID matches, then symbol/name/synonym matches, then other matches
        
        List<Filter> idFilters = new ArrayList<Filter>();
        List<Filter> nomenFilters = new ArrayList<Filter>();
        List<Filter> otherFilters = new ArrayList<Filter>();

        for (String term : terms) {
        	idFilters.add(new Filter(SearchConstants.QS_ACC_ID, term, Operator.OP_EQUAL));
        	
        	List<Filter> nomenSet = new ArrayList<Filter>();
        	nomenSet.add(new Filter(SearchConstants.QS_SYMBOL, term, Operator.OP_CONTAINS));
        	nomenSet.add(new Filter(SearchConstants.QS_NAME, term, Operator.OP_CONTAINS));
        	nomenSet.add(new Filter(SearchConstants.QS_SYNONYM, term, Operator.OP_CONTAINS));
        	nomenFilters.add(Filter.or(nomenSet));
        	
        	otherFilters.add(new Filter(SearchConstants.QS_SEARCH_TEXT, term, Operator.OP_CONTAINS));
        }

        SearchParams idSearch = new SearchParams();
        idSearch.setPaginator(page);
        idSearch.setFilter(Filter.or(idFilters));

        SearchParams nomenSearch = new SearchParams();
        nomenSearch.setPaginator(page);
        nomenSearch.setFilter(Filter.or(nomenFilters));
        
        SearchParams otherSearch = new SearchParams();
        otherSearch.setPaginator(page);
        otherSearch.setFilter(Filter.or(otherFilters));
        
        List<QSFeatureResult> idMatches = qsFinder.getFeatureResults(idSearch).getResultObjects();
        logger.info("Got " + idMatches.size() + " ID matches");
        List<QSFeatureResult> nomenMatches = qsFinder.getFeatureResults(nomenSearch).getResultObjects();
        logger.info("Got " + nomenMatches.size() + " nomen matches");
        List<QSFeatureResult> otherMatches = qsFinder.getFeatureResults(otherSearch).getResultObjects();
        logger.info("Got " + otherMatches.size() + " other matches");
        
        List<Filter> anyFilters = new ArrayList<Filter>();
        anyFilters.addAll(idFilters);
        anyFilters.addAll(nomenFilters);
        anyFilters.addAll(otherFilters);
        
        SearchParams anySearch = new SearchParams();
        anySearch.setPaginator(new Paginator(0));
        anySearch.setFilter(Filter.or(anyFilters));

        int totalCount = qsFinder.getFeatureResults(anySearch).getTotalCount();
        logger.info("Identified " + totalCount + " matches in all");

        List<QSFeatureResult> out = unifyFeatureMatches(terms, idMatches, nomenMatches, otherMatches);
        
        JsonSummaryResponse<QSFeatureResult> response = new JsonSummaryResponse<QSFeatureResult>();
        response.setSummaryRows(out);
        response.setTotalCount(totalCount);
        logger.info("Returning " + out.size() + " matches");

        return response;
    }

	// consolidate the lists of matching features, add star values, and setting best match values, then return
	private List<QSFeatureResult> unifyFeatureMatches (List<String> searchTerms, List<QSFeatureResult> idMatches,
		List<QSFeatureResult> nomenMatches, List<QSFeatureResult> otherMatches) {
		
		Grouper<QSFeatureResult> grouper = new Grouper<QSFeatureResult>();
		
		// ID matches must be exact matches (aside from case sensitivity)
		
		for (QSFeatureResult match : idMatches) {
			boolean found = false;
			for (String id : match.getAccID()) {
				for (String term : searchTerms) {
					if (term.equalsIgnoreCase(id)) {
						match.setBestMatchType("ID");
						match.setBestMatchText(id);
						match.setStars("****");
						grouper.add("****", match.getPrimaryID(), match);
						found = true;
						break;
					}
				}
				if (found) { break; }
			}
			if (!found) {
				// should not happen, but let's make sure not to lose the result just in case
				match.setStars("*");
				grouper.add("*", match.getPrimaryID(), match);
			}
		}

		// At this point, we could have the same features matched in both nomenMatches and in otherMatches.  If we just
		// process them separately, it would be possible for the best nomenMatch for the feature to be a 2-star while
		// the best otherMatch could be a 4-star.  In that case, whichever is found first would be retained and the
		// other discarded.  So we need to reconcile the two lists into a single one with only one match per feature.
		
		// Make two sets of primary IDs.  Those in nomenSet need to have symbol, name, synonym checked.  Those in
		// otherSet need annotations and ortholog data checked.  Those in both need both checked.  This optimization 
		// reduces the number of strings we need to analyze for each data point.
		
		Set<String> byNomen = new HashSet<String>();	// IDs of features yet to process for nomen
		Set<String> byOther = new HashSet<String>();	// IDs of features yet to process for other matches
		
		for (QSFeatureResult match : nomenMatches) {
			byNomen.add(match.getPrimaryID());
		}
		for (QSFeatureResult match : otherMatches) {
			byOther.add(match.getPrimaryID());
		}
		
		List<List<QSFeatureResult>> bothLists = new ArrayList<List<QSFeatureResult>>();
		bothLists.add(nomenMatches);
		bothLists.add(otherMatches);

		// Exact are 4-star, begins are 3-star, contains are 2-star.
		
		BestMatchFinder bmf = new BestMatchFinder(searchTerms);
		for (List<QSFeatureResult> resultList : bothLists) {
			for (QSFeatureResult match : resultList) {
				String primaryID = match.getPrimaryID();
				boolean nomenToDo = byNomen.contains(primaryID);
				boolean otherToDo = byOther.contains(primaryID);
				
				// maps from value string to a string describing what type of data it is
				Map<String,String> options = new HashMap<String,String>();

				if (nomenToDo) {
					// Nomen matches can be to symbol, name, or synonym.
					options.put(match.getSymbol(), "symbol");
					options.put(match.getName(), "name");
					if (match.getSynonym() != null) {
						for (String synonym : match.getSynonym()) {
							options.put(synonym, "synonym");
						}
					}
					byNomen.remove(primaryID);
				}
				if (otherToDo) {
					// Other matches need to handle anything in searchText bucket (annotations, ortholog nomen, etc.)
					if (match.getOrthologNomenOrg() != null) {
						for (String orthologNomenOrg : match.getOrthologNomenOrg()) {
							String[] pieces = orthologNomenOrg.split(":");
							if ((pieces != null) && (pieces.length > 1)) {
								options.put(pieces[1], pieces[0]);
							}
						}
					}
					
					if (match.getProteinDomains() != null) {
						for (String domain : match.getProteinDomains()) {
							options.put(domain, "Protein Domain");
						}
					}
					byOther.remove(primaryID);
				}

				// If we encounter the same matching feature a second time (say, once in nomen and once in other)
				// then options will be empty and we can skip scoring and adding it again.
				if (options.size() > 0) {
					BestMatch bestMatch = bmf.getBestMatch(options);
					match.setStars(bestMatch.stars);
					match.setBestMatchText(bestMatch.matchText);
					match.setBestMatchType(bestMatch.matchType);

					grouper.add(bestMatch.stars, primaryID, match);
				}
			}
		}

		return grouper.toList();
	}
	
    //-------------------------//
    // QS Results - bucket 2 JSON (vocab terms and annotations)
    //-------------------------//
	@RequestMapping("/vocabBucket")
	public @ResponseBody JsonSummaryResponse<QSVocabResult> getVocabBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getVocabBucket started");
        
        Paginator page = new Paginator(1500);				// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);	// sort by descending Solr score (best first)

        List<String> terms = new ArrayList<String>();
        for (String term : queryForm.getQuery().replace(',', ' ').split(" ")) {
        	terms.add(term);
        }
        terms.add(queryForm.getQuery());		// add full term for exact matches
        
        // Search in order of priority:  ID matches, then term matches, then synonym matches
        
        List<Filter> idFilters = new ArrayList<Filter>();
        List<Filter> termFilters = new ArrayList<Filter>();
        List<Filter> synonymFilters = new ArrayList<Filter>();
        List<Filter> anyFilters = new ArrayList<Filter>();

        for (String term : terms) {
        	idFilters.add(new Filter(SearchConstants.QS_ACC_ID, term, Operator.OP_EQUAL));
        	termFilters.add(new Filter(SearchConstants.QS_TERM, term, Operator.OP_CONTAINS));
        	synonymFilters.add(new Filter(SearchConstants.QS_SYNONYM, term, Operator.OP_CONTAINS));
        }

        SearchParams idSearch = new SearchParams();
        idSearch.setPaginator(page);
        idSearch.setFilter(Filter.or(idFilters));

        SearchParams termSearch = new SearchParams();
        termSearch.setPaginator(page);
        termSearch.setFilter(Filter.or(termFilters));
        
        SearchParams synonymSearch = new SearchParams();
        synonymSearch.setPaginator(page);
        synonymSearch.setFilter(Filter.or(synonymFilters));
        
        List<QSVocabResult> idMatches = qsFinder.getVocabResults(idSearch).getResultObjects();
        logger.info("Got " + idMatches.size() + " ID matches");
        List<QSVocabResult> termMatches = qsFinder.getVocabResults(termSearch).getResultObjects();
        logger.info("Got " + termMatches.size() + " term matches");
        List<QSVocabResult> synonymMatches = qsFinder.getVocabResults(synonymSearch).getResultObjects();
        logger.info("Got " + synonymMatches.size() + " synonym matches");
        
        anyFilters.addAll(idFilters);
        anyFilters.addAll(termFilters);
        anyFilters.addAll(synonymFilters);
        
        SearchParams anySearch = new SearchParams();
        anySearch.setPaginator(new Paginator(0));
        anySearch.setFilter(Filter.or(anyFilters));

        int totalCount = qsFinder.getVocabResults(anySearch).getTotalCount();
        logger.info("Identified " + totalCount + " matches in all");

        List<QSVocabResult> out = unifyVocabMatches(terms, idMatches, termMatches, synonymMatches);
        
        JsonSummaryResponse<QSVocabResult> response = new JsonSummaryResponse<QSVocabResult>();
        response.setSummaryRows(out);
        response.setTotalCount(totalCount);
        logger.info("Returning " + out.size() + " matches");

        return response;
    }

	// consolidate the lists of matching vocab termss, add star values, and setting best match values, then return
	private List<QSVocabResult> unifyVocabMatches (List<String> searchTerms, List<QSVocabResult> idMatches,
		List<QSVocabResult> termMatches, List<QSVocabResult> synonymMatches) {
		
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
						grouper.add("****", match.getPrimaryID(), match);
						found = true;
						break;
					}
				}
				if (found) { break; }
			}
			if (!found) {
				// should not happen, but let's make sure not to lose the result just in case
				match.setStars("*");
				grouper.add("*", match.getPrimaryID(), match);
			}
		}

		// Term matches can be to symbol, name, or synonym.  Exact are 4-star, begins are 3-star, contains are 2-star.
		
		List<QSVocabResult> consolidatedMatches = new ArrayList<QSVocabResult>();
		consolidatedMatches.addAll(termMatches);
		consolidatedMatches.addAll(synonymMatches);

		BestMatchFinder bmf = new BestMatchFinder(searchTerms);
		for (QSVocabResult match : consolidatedMatches) {
			Map<String,String> options = new HashMap<String,String>();
			options.put(match.getTerm(), "term");
			if (match.getSynonym() != null) {
				for (String synonym : match.getSynonym()) {
					options.put(synonym, "synonym");
				}
			}

			BestMatch bestMatch = bmf.getBestMatch(options);
			match.setStars(bestMatch.stars);
			match.setBestMatchText(bestMatch.matchText);
			match.setBestMatchType(bestMatch.matchType);

			grouper.add(bestMatch.stars, match.getPrimaryID(), match);
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
		List<T> fourStar;
		List<T> threeStar;
		List<T> twoStar;
		List<T> oneStar;
		Set<String> ids;
		
		public Grouper() {
			this.fourStar = new ArrayList<T>();
			this.threeStar = new ArrayList<T>();
			this.twoStar = new ArrayList<T>();
			this.oneStar = new ArrayList<T>();
			this.ids = new HashSet<String>();
		}
		
		public void addFourStar(String id, T item) {
			this.fourStar.add(item);
		}
		
		public void addThreeStar(String id, T item) {
			this.threeStar.add(item);
		}
		
		public void addTwoStar(String id, T item) {
			this.twoStar.add(item);
		}
		
		public void addOneStar(String id, T item) {
			this.oneStar.add(item);
		}
		
		public void add(String stars, String id, T item) {
			int starCount = stars.length();

			if (this.ids.contains(id)) { return; }
			
			if (starCount == 4) { this.addFourStar(id, item); }
			else if (starCount == 3) { this.addThreeStar(id, item); }
			else if (starCount == 2) { this.addTwoStar(id, item); }
			else { this.addOneStar(id, item); }

			this.ids.add(id);
		}
		
		public List<T> toList() {
			List<T> all = new ArrayList<T>();
			all.addAll(fourStar);
			all.addAll(threeStar);
			all.addAll(twoStar);
			all.addAll(oneStar);
			return all;
		}
	}
	
	private class BestMatchFinder {
		private List<String> lowerTerms;
		
		public BestMatchFinder(List<String> searchTerms) {
			this.lowerTerms = new ArrayList<String>();
			for (String term : searchTerms) {
				this.lowerTerms.add(term.toLowerCase().replace("*", ""));
			}
		}
		
		// Find the best match (for the search terms included at instantiation) among the various options,
		// which map from a term to each one's term type.
		public BestMatch getBestMatch(Map<String,String> options) {
			// 1. iterate over terms in one pass
			// 2. test against each of the search terms
			// 3. bypass match types that are lower than what we've already found
			
			BestMatch match = new BestMatch();
			match.starCount = 0;
			match.stars = "";

			for (String key : options.keySet()) {
				String keyLower = key.toLowerCase();

				for (String term : lowerTerms) {
					// bail out once we find a 4-star match
					if (keyLower.equals(term)) {
						match.starCount = 4;
						match.stars = "****";
						match.matchText = key;
						match.matchType = options.get(key);
						return match;
					}
						
					// don't bother checking if we already have a 3-star match
					if (match.starCount < 3) {
						if (keyLower.startsWith(term)) {
							match.starCount = 3;
							match.stars = "***";
							match.matchText = key;
							match.matchType = options.get(key);
						}
					}
						
					// don't bother checking if we already have at least a 2-star match
					if (match.starCount < 2) {
						if (keyLower.contains(term)) {
							match.starCount = 2;
							match.stars = "**";
							match.matchText = key;
							match.matchType = options.get(key);
						}
					}
				}
			}
			return match;
		}
	}
	
	private class BestMatch {
		public int starCount;
		public String stars;
		public String matchType;
		public String matchText;
	}
}
