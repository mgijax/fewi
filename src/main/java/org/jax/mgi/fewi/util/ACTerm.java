package org.jax.mgi.fewi.util;

import java.util.Comparator;
import java.util.List;

import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;

/* Is: a searchable term or synonoym for searching by an ACHelper
 * Notes: Terms are denormalized to make coding easier.  Each one of these objects will represent either
 * 	a term or one synonym for a term.  So, a vocab term with two synonyms will have three of these objects
 *	(one for the term itself, plus one for each synonym).
 */
public class ACTerm {
	//--- constants ---//
	
	// flags to indicate what type of match was found when comparing this ACTerm to a search String
	public static int NO_MATCH = 0;				// Search string does not match this ACTerm.
	public static int EXACT_TERM_MATCH = 1;		// Search string is an exact match for this ACTerm.
	public static int EXACT_SYNONYM_MATCH = 2;	// Search string is an exact match for a synonym of this ACTerm.
	public static int BEGINS_TERM_MATCH = 3;	// Search string is a begins match for this ACTerm.
	public static int BEGINS_SYNONYM_MATCH = 4;	// Search string is a begins match for a synonym of this ACTerm.
	public static int OTHER_MATCH = 5;			// Search string is matches this ACTerm otherwise.
	
	//--- instance variables ---//
	
	private VocabTerm term;			// raw data from the database
	private EmapaACResult emapaTerm;	// raw data from Solr instead (EMAPA autocomplete)
	private boolean isTerm;			// true if the object is for the term itself, false if for a synonym
	private List<String> tokens;	// list of lowercase tokens that can be used to find this term
	private String lowerString;		// lowercase version of the searchable String (a term or synonym)
	private String sortableTerm;	// the term itself (for sorting)
	
	//--- public methods ---//

	public ACTerm(VocabTerm term, boolean isTerm, String searchableString) {
		this.term = term;
		this.isTerm = isTerm;
		this.tokens = FewiUtil.tokenize(searchableString);
		this.lowerString = searchableString.toLowerCase();
		this.sortableTerm = term.getTerm();
	}
	
	public ACTerm(EmapaACResult emapaTerm, boolean isTerm, String searchableString) {
		this.emapaTerm = emapaTerm;
		this.isTerm = isTerm;
		this.tokens = FewiUtil.tokenize(searchableString);
		this.lowerString = searchableString.toLowerCase();
		this.sortableTerm = emapaTerm.getStructure();
	}
	
	// Get the list of tokens for this term.
	public List<String> getTokens() {
		return this.tokens;
	}
	
	// return a String that represents this term for a pick list
	public String getDisplayValue() {
		if (isTerm) {
			return this.lowerString;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(this.sortableTerm.toLowerCase());
		sb.append(" (");
		sb.append(this.lowerString);
		sb.append(")");
		return sb.toString();
	}
	
	// Compares 'queryString' with this term and its synonyms, returning an integer flag to indicate the highest-priority
	// type of match that was found.  Flags are defined above.
	public int getMatchType(String queryString) {
		return this.getMatchType(queryString, FewiUtil.tokenize(queryString));
	}
	
	// Compares 'queryStringLower' (a lowercased version of the query string) with this term and its synonyms, returning
	// an integer flag to indicate the highest-priority type of match that was found.  Flags are defined above.  This
	// method allows a List of String tokens to be passed in to aid efficiency, as we can just tokenize the query string
	// once in the calling method and re-use those tokens across many ACTerm objects.
	public int getMatchType(String queryStringLower, List<String> queryTokens) {
		// Assume we're dealing with the term itself.
		int exactFlag = EXACT_TERM_MATCH;
		int beginsFlag = BEGINS_TERM_MATCH;
		
		// If we're dealing with a synonym for the term, update the flags we'll return.
		if (!this.isTerm) {
			exactFlag = EXACT_SYNONYM_MATCH;
			beginsFlag = BEGINS_SYNONYM_MATCH;
		}

		// Is the query string a prefix to this string?
		if (this.lowerString.startsWith(queryStringLower)) {

			// If so, is it also an exact match?  (highest priority)
			if (this.lowerString.equals(queryStringLower)) {
				return exactFlag;
			}

			// Not an exact match, but a begins match.  (second priority)
			return beginsFlag;
		}

		// Are all the query tokens prefixes to the tokens for this string? (third priority)
		for (String qt : queryTokens) {
			boolean found = false;				// did not match this token yet
			for (String token : this.tokens) {
				if (token.startsWith(qt)) {
					found = true;
					break;
				}
			}
			
			// If we found a query token that didn't match a token for this term, bail out.
			if (!found) {
				return NO_MATCH;
			}
		}
		return OTHER_MATCH;
	}
	
	// returns the source VocabTerm for this object (null if this was initialized with an EmapaACResult)
	public VocabTerm getVocabTerm() {
		return this.term;
	}
	
	// returns the source EmapaACResult for this object (null if this was initialized with a VocabTerm)
	public EmapaACResult getEmapaACResult() {
		return this.emapaTerm;
	}
	
	// returns true if this object represents the term itself, false if it represents a synonym for it
	public boolean byTerm() {
		return this.isTerm;
	}
	
	// get a Comparator for sorting these objects
	public Comparator<ACTerm> getComparator() {
		return new ACTermComparator();
	}

	//--- private inner classes ---//
	
	// Comparator for use in sorting ACTerms
	private class ACTermComparator implements Comparator<ACTerm> {
		public int compare(ACTerm a, ACTerm b) {
			SmartAlphaComparator cmp = new SmartAlphaComparator();

			// first compare the base terms themselves
			int i = cmp.compare(a.sortableTerm, b.sortableTerm);
			if (i == 0) {
				// The terms match, so if only one is a synonym then the other should appear first, or if both are
				// for synonyms, then compare the synonyms.
				if (!a.isTerm) {
					// a is a synonym

					if (!b.isTerm) {
						// b is a synonym, too
						return cmp.compare(a.lowerString, b.lowerString);
					} else {
						// b is not a synonym, so it comes first
						return 1;
					}
				} else if (!b.isTerm) {
					// a is a raw term, b is a synonym, so a comes first
					return -1;
				} else {
					// a and b are both raw terms and they match, so just pick a
					return -1;
				}
			}
			return i;
		}
	}
}
