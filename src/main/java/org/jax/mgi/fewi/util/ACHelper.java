package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;


/* Is: a helper class for making an autocomplete work against a list of vocabulary terms
 * Has: intelligence in how to match partial words from a search String against a list of vocab
 *	terms, returning a prioritized list of matches (ACMatches).
 * Note: All searching is case-insensitive and only considers alphanumeric characters.  Returns are lowercase.
 */
public class ACHelper {
	private final Logger logger = LoggerFactory.getLogger(ACHelper.class);

	//--- instance variables, all of which can be instantiated once and then the ACHelper shared across many threads ---//
	
	// list of objects we can use to find VocabTerms for a user's search string; each one ACTerm represents
	// either a term or a synonym for a term, and each is associated with the corresponding VocabTerm itself.
	private List<ACTerm> searchableTerms = null;
	
	// The keystone maps from a String to a List of Integers.  Ideally, the strings are 3-character prefixes for
	// tokens.  Each maps to a List of (integer) indexes into 'searchableTerms' where that term has a token
	// with that 3-character prefix.  For cases of 1- and 2-character words, those are also included here and map
	// to a List of (Integer) indexes into 'searchableTerms' where that term has a token that is that same
	// 1- or 2-character word.  For example, if the first term in 'searchableTerms' is "2-cell stage conceptus"
	// then four different strings in 'keystone' would have index 0 in their Lists: "2", "cel", "sta", and "con".
	private Map<String,List<Integer>> keystone = null;

	// Maps from 1- and 2-character Strings to a List of Strings, each of which is an entry in 'keystone' that
	// has that String as a prefix.  For example, if "uni" is in 'keystone' then both "u" and "un" will have
	// "uni" in their List of Strings here.
	private Map<String,List<String>> prefixes = null;
	
	// Maps each 1-, 2-, and 3-character String (union of 'keystone' and 'prefixes') to a count of the 'searchableTerms'
	// we would need to inspect when seeking matches.  This is used to quickly identify the smallest set of ACTerms that
	// we need to inspect for any given search term, thus allowing us to pick the fastest search path.  (Once we identify
	// the smallest token prefix, no others matter. We just search in that List.)
	private Map<String,Integer> termCount = null;
	
	//--- public methods ---//
	
	// Initialize the set of terms we want to consider, pulling just the terms themselves from a list of VocabTerm
	// objects.  By using VocabTerms to initialize, synonyms for the terms are also considered for matches.
	public void setVocabTerms(List<VocabTerm> vocabTerms) {
		this.searchableTerms = new ArrayList<ACTerm>();
		ACTermFactory factory = new ACTermFactory();
		
		// Add a searchable entry for each term and for each synonym.
		for (VocabTerm vt : vocabTerms) {
			this.searchableTerms.addAll(factory.getACTerms(vt));
		}

		// Analyze the list of searchable entries and build the indexes needed to efficiently search against them. 
		this.createIndexes();
	}

	// Initialize the set of terms we want to consider, pulling just the terms themselves from a list of EmapaACResult
	// objects.  By using EmapaACResults to initialize, synonyms for the terms are also considered for matches.
	public void setEmapaACResults(List<EmapaACResult> emapaACResults) {
		this.searchableTerms = new ArrayList<ACTerm>();
		ACTermFactory factory = new ACTermFactory();
		
		// Add a searchable entry for each term and for each synonym.
		for (EmapaACResult vt : emapaACResults) {
			this.searchableTerms.addAll(factory.getACTerms(vt));
		}

		// Analyze the list of searchable entries and build the indexes needed to efficiently search against them. 
		this.createIndexes();
	}

	// extract EmapaACResult objects from the given list of ACTerms
	public List<EmapaACResult> asEmapaACResults(List<ACTerm> terms) {
		List<EmapaACResult> results = new ArrayList<EmapaACResult>();
		for (ACTerm term : terms) {
			results.add(term.getEmapaACResult());
		}
		return results;
	}
	
	// convert the list of ACTerms to be their String matches
	public List<String> asStrings(List<ACTerm> terms) {
		List<String> results = new ArrayList<String>();
		for (ACTerm term : terms) {
			results.add(term.getDisplayValue());
		}
		return results;
	}
	
	// Match the given 'query' string against our set of terms, returning the top 200 matches.
	public List<ACTerm> search(String query) {
		return search(query, 200);
	}

	// Match the given 'query' string against our set of terms, returning the top 'maxCount' matches.
	// Search rules:
	// 1. If the match was to a term's synonym, it is returned in parentheses with its term.
	// 2. Terms are returned in priority order, smart-alphabetized within a priority group.
	// 3. There are five priority groups:
	//		a. exact matches to the term
	//		b. exact matches to a synonym
	//		c. begins matches to the term
	//		d. begins matches to the synonym
	//		e. everything else
	public List<ACTerm> search(String query, int maxCount) {
		String queryLower = query.toLowerCase();
		List<ACTerm> matches = new ArrayList<ACTerm>();
		List<String> queryTokens = FewiUtil.tokenize(query);
		
		// For the sake of efficiency, we want to find the minimal set of ACTerm objects to examine.  To do this,
		// we want to find the oddest (most rare) of the tokens in queryTokens and look at only the ACTerm objects
		// for that one.  (The other tokens don't matter yet, as the matching terms will have to match the oddest
		// one, in addition to the others.)
		
		int minCount = -1;			// lowest count for a token examined so far
		String oddestPrefix = null;	// will be the prefix for the token with the 'minCount' found so far

		for (String token : queryTokens) {
			String prefix = this.getBestPrefix(token);
			if ((this.termCount != null) && (this.termCount.containsKey(prefix))) {
				int tokenCount = this.termCount.get(prefix);
				if ((tokenCount < minCount) || (minCount < 0)) {
					minCount = tokenCount;
					oddestPrefix = prefix;
				}
			} else {
				// User entered a token with an unknown prefix, so we can bail out now.  (Or the termCounts are
				// being initialized in another thread, but are not quite ready.)
				return matches;
			}
		}
		
		// Now we actually conduct the search against the ACTerm objects corresponding to 'oddestPrefix', sorting the
		// matches into five bins based on the type of match.  Since 'searchableTerms' is smart-alpha ordered, each
		// of the bins will also be ordered.  Then we can concatenate the bins to get properly ordered final results.
		
		List<ACTerm> exactTerm = new ArrayList<ACTerm>();
		List<ACTerm> exactSynonym = new ArrayList<ACTerm>();
		List<ACTerm> beginsTerm = new ArrayList<ACTerm>();
		List<ACTerm> beginsSynonym = new ArrayList<ACTerm>();
		List<ACTerm> otherMatches = new ArrayList<ACTerm>();
		
		if (oddestPrefix != null) {
			// list of indexes for ACTerms we need to check
			List<Integer> indexesToSearch = null;

			// For 1- and 2-character prefixes, get the precomputed prefixes to check in the keystone.
			if (oddestPrefix.length() < 3) {
				// tracks which indexes we've already added to 'indexesToSearch' to efficiently avoid duplicates
				Set<Integer> uniqueIndexes = new HashSet<Integer>();
				
				// composing a new list of indexes to search, union of all prefixes derived from 'oddestPrefix'
				indexesToSearch = new ArrayList<Integer>();
				
				for (String prefix : this.prefixes.get(oddestPrefix)) {
					for (Integer index : this.keystone.get(prefix)) {
						if (!uniqueIndexes.contains(index)) {
							uniqueIndexes.add(index);
							indexesToSearch.add(index);
						}
					}
				}
				// Because we consolidated indexes from multiple prefixes, they need to be put back in order.
				Collections.sort(indexesToSearch);

			} else {
				// For 3-character prefixes, we just need to examine the list of indexes for that particular prefix.
				indexesToSearch = this.keystone.get(oddestPrefix);
			}
			
			// Search through the identified ACTerm objects, compiling the five bins of matches.
			for (Integer index : indexesToSearch) {
				ACTerm term = this.searchableTerms.get(index);
				int matchCode = term.getMatchType(queryLower, queryTokens);

				if (matchCode == ACTerm.NO_MATCH) {
					// This term does not match, so move on to the next.
				} else if (matchCode == ACTerm.EXACT_TERM_MATCH) {
					exactTerm.add(term);
				} else if (matchCode == ACTerm.EXACT_SYNONYM_MATCH) {
					exactSynonym.add(term);
				} else if (matchCode == ACTerm.BEGINS_TERM_MATCH) {
					beginsTerm.add(term);
				} else if (matchCode == ACTerm.BEGINS_SYNONYM_MATCH) {
					beginsSynonym.add(term);
				} else if (matchCode == ACTerm.OTHER_MATCH) {
					otherMatches.add(term);
				}
			}
			
			// Now concatenate the bins of matches into a single one to return (quitting when we've crossed the max).
			matches.addAll(exactTerm);
			if (matches.size() < maxCount) matches.addAll(exactSynonym);
			if (matches.size() < maxCount) matches.addAll(beginsTerm);
			if (matches.size() < maxCount) matches.addAll(beginsSynonym);
			if (matches.size() < maxCount) matches.addAll(otherMatches);
		}
		if (matches.size() > maxCount) {
			return matches.subList(0, maxCount);
		}
		return matches;
	}
	
	//--- private methods ---//
	
	// Get the maximal prefix for which we have data for this token.  (If a token has 3 characters, we return those 3.
	// If not, then try fall back to 2 or 1.)
	private String getBestPrefix (String token) {
		int tokenLength = token.length();

		if (tokenLength >= 3) {
			return token.substring(0, 3);
		} else if (tokenLength == 2) {
			return token.substring(0, 2);
		} else if (tokenLength == 1) {
			return token.substring(0, 1);
		}
		return null;
	}
	
	// Analyze the List of 'searchableTerms' to populate the indexes in 'keystone', 'prefixes', and 'termCount'.
	private void createIndexes() {
		logger.info("Building indexes for " + this.searchableTerms.size() + " terms");

		// First, sort the searchable entries by smart-alpha, so we only need to do binning of matches once they're found.
		if (this.searchableTerms.size() > 0) {
			Collections.sort(this.searchableTerms, this.searchableTerms.get(0).getComparator());
			logger.info(" - sorted terms");
		}
		
		this.keystone = new HashMap<String, List<Integer>>();
		this.prefixes = new HashMap<String, List<String>>();
		
		// First, populate 'keystone' and 'prefixes' using 1-, 2-, and 3-letter prefixes to the tokens of each ACTerm.
		for (int i = 0; i < this.searchableTerms.size(); i++) {
			ACTerm acTerm = this.searchableTerms.get(i);
			for (String token : acTerm.getTokens()) {
				String prefix1 = null;				// 1-letter prefix of token
				String prefix2 = null;				// 2-letter prefix of token
				String prefix3 = null;				// 3-letter prefix of token
				int tokenLength = token.length();	// length of the token we're examining

				if (tokenLength >= 3) {
					// normal case: token is at least 3 letters
					prefix1 = token.substring(0, 1);
					prefix2 = token.substring(0, 2);
					prefix3 = token.substring(0, 3);

				} else if (tokenLength == 2) {
					// token has only 2 letters
					prefix1 = token.substring(0, 1);
					prefix2 = token.substring(0, 2);
					prefix3 = prefix2;
					
				} else if (tokenLength == 1) {
					// token has only 1 letter
					prefix1 = token.substring(0, 1);
					prefix2 = prefix1;
					prefix3 = prefix2;
				}
				
				if (tokenLength >= 1) {
					// Add the entry to the keystone mapping (3-character prefix to integer index)
					if (!this.keystone.containsKey(prefix3)) {
						this.keystone.put(prefix3, new ArrayList<Integer>());
					}
					this.keystone.get(prefix3).add(i);
					
					// Add mapping from 1-letter prefix to 3-letter prefix.
					if (!this.prefixes.containsKey(prefix1)) {
						this.prefixes.put(prefix1, new ArrayList<String>());
					}
					if (!this.prefixes.get(prefix1).contains(prefix3)) {
						this.prefixes.get(prefix1).add(prefix3);
					}

					// Add mapping from 2-letter prefix to 3-letter prefix.
					if (!this.prefixes.containsKey(prefix2)) {
						this.prefixes.put(prefix2, new ArrayList<String>());
					}
					if (!this.prefixes.get(prefix2).contains(prefix3)) {
						this.prefixes.get(prefix2).add(prefix3);
					}
				} // end -- if (tokenLenth >= 1)
			} // end -- for (String token : ...)
		} // end -- for (int i = 0; ...)

		logger.info(" - populated keystone and prefix maps");
		
		// Now that we have the 'keystone' and 'prefixes' we can compute the number of ACTerms that we'd need to search
		// for any token's prefix of 1-, 2-, or 3-characters.
		
		this.termCount = new HashMap<String, Integer>();
		
		// To find the number of terms to search, we walk through each key in 'prefixes', and then count ACTerms by
		// looking at 'keystone' for each of the 3-character prefixes referenced.
		
		for (String prefix : this.prefixes.keySet()) {
			int acTermCount = 0;
			for (String prefix3 : this.prefixes.get(prefix)) {
				acTermCount = acTermCount + this.keystone.get(prefix3).size();
			}
			this.termCount.put(prefix, acTermCount);
		}
		
		// And finally, also add the 3-character keys from 'keystone' to the counts.
		
		for (String prefix3 : this.keystone.keySet()) {
			this.termCount.put(prefix3, this.keystone.get(prefix3).size());
		}
		
		logger.info(" - populated term counts");
// commented out of production, but keeping for future use in debugging
		analyzeIndexes();
	} // end -- createIndexes() method
	
	// Report to the log file some statistics about the indexes.
	private void analyzeIndexes() {
		List<Integer> ints = new ArrayList<Integer>();		// for computations
		
		logger.info("Analyzing keystone index:");
		logger.info(" - " + this.keystone.size() + " keys");
		for (String key : this.keystone.keySet()) {
			ints.add(this.keystone.get(key).size());
		}
		logger.info(" - max of " + this.max(ints) + " terms referred to");
		logger.info(" - min of " + this.min(ints) + " terms referred to");
		logger.info(" - average of " + this.average(ints) + " terms referred to");
		logger.info(" - std dev of " + this.stddev(ints) + " terms referred to");

		ints = new ArrayList<Integer>();
		logger.info("Analyzing prefixes index:");
		logger.info(" - " + this.prefixes.size() + " keys");
		for (String key : this.prefixes.keySet()) {
			ints.add(this.prefixes.get(key).size());
		}
		logger.info(" - max of " + this.max(ints) + " keystone entries referred to");
		logger.info(" - min of " + this.min(ints) + " keystone entries referred to");
		logger.info(" - average of " + this.average(ints) + " keystone entries referred to");
		logger.info(" - std dev of " + this.stddev(ints) + " keystone entries referred to");

		ints = new ArrayList<Integer>();
		List<Integer> ints1 = new ArrayList<Integer>();
		List<Integer> ints2 = new ArrayList<Integer>();
		List<Integer> ints3 = new ArrayList<Integer>();
		logger.info("Analyzing term counts index:");
		logger.info(" - " + this.termCount.size() + " keys");
		for (String key : this.termCount.keySet()) {
			ints.add(this.termCount.get(key));
			if (key.length() == 1) {
				ints1.add(this.termCount.get(key));
			} else if (key.length() == 2) {
				ints2.add(this.termCount.get(key));
			} else if (key.length() == 3) {
				ints3.add(this.termCount.get(key));
			}
		}
		logger.info(" - max of " + this.max(ints) + " terms to search");
		logger.info(" - min of " + this.min(ints) + " terms to search");
		logger.info(" - average of " + this.average(ints) + " terms to search");
		logger.info(" - std dev of " + this.stddev(ints) + " terms to search");

		logger.info("Analyzing 1-char subset of term counts index:");
		logger.info(" - " + ints1.size() + " keys");
		logger.info(" - max of " + this.max(ints1) + " terms to search");
		logger.info(" - min of " + this.min(ints1) + " terms to search");
		logger.info(" - average of " + this.average(ints1) + " terms to search");
		logger.info(" - std dev of " + this.stddev(ints1) + " terms to search");

		logger.info("Analyzing 2-char subset of term counts index:");
		logger.info(" - " + ints2.size() + " keys");
		logger.info(" - max of " + this.max(ints2) + " terms to search");
		logger.info(" - min of " + this.min(ints2) + " terms to search");
		logger.info(" - average of " + this.average(ints2) + " terms to search");
		logger.info(" - std dev of " + this.stddev(ints2) + " terms to search");

		logger.info("Analyzing 3-char subset of term counts index:");
		logger.info(" - " + ints3.size() + " keys");
		logger.info(" - max of " + this.max(ints3) + " terms to search");
		logger.info(" - min of " + this.min(ints3) + " terms to search");
		logger.info(" - average of " + this.average(ints3) + " terms to search");
		logger.info(" - std dev of " + this.stddev(ints3) + " terms to search");

		logger.info("Finished analysis of indexes");
	}
	
	private String min(List<Integer> ints) {
		int mn = Integer.MAX_VALUE;
		for (Integer i : ints) {
			if (i < mn) mn = i;
		}
		return Integer.toString(mn);
	}

	private String max(List<Integer> ints) {
		int mx = Integer.MIN_VALUE;
		for (Integer i : ints) {
			if (i > mx) mx = i;
		}
		return Integer.toString(mx);
	}

	private String average(List<Integer> ints) {
		int sum = 0;
		for (Integer i : ints) {
			sum = sum + i;
		}
		return Float.toString(((float) sum) / ints.size());
	}

	private String stddev(List<Integer> ints) {
		// find average first; go with double precision for accuracy
		int sum = 0;
		for (Integer i : ints) {
			sum = sum + i;
		}
		double mean = ((double) sum) / ints.size();
		
		// find sum of squares of differences from mean
		double dsum = 0;	
		for (Integer i : ints) {
			dsum = dsum + Math.pow(i - mean, 2);
		}
		
		// stddev is square root of average distance from mean
		double avgDistance = dsum / ints.size();
		return Double.toString(Math.sqrt(avgDistance));
	}
}
