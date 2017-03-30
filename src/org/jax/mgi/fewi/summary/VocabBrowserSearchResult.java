package org.jax.mgi.fewi.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.shr.jsonmodel.BrowserSynonym;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;


/**
 * wrapper around a BrowserTerm, as returned by Solr; represents one row in generic vocab browser
 * search pane (jsp/vocabBrowser/search.jsp)
 */
public class VocabBrowserSearchResult {

	private static String[] stopWords = { "and", "from", "of", "or",
		"the", "their", "to" };
	private static HashMap<String, String> stopWordMap = null;

	//-------------------
	// instance variables
	//-------------------

	private BrowserTerm term;		// encapsulated object
	private List<String> tokens;	// list of user's search tokens

	// config values
    String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

	//-------------
	// constructors
	//-------------

    public VocabBrowserSearchResult (BrowserTerm term) {
    	this.term = term;
    	return;
    }

    //------------------------------------------------------------------------
    // public instance methods
    //------------------------------------------------------------------------

    // set the user's search tokens, so we can use them to see what matched
    public void setTokens(List<String> tokens) {
    	this.tokens = tokens;
    }
    
    // primary accession ID for the term
    public String getAccID() {
    	return this.term.getPrimaryID().getAccID();
    }
    
    // text of the term itself
    public String getTerm() {
    	return this.term.getTerm();
    }
    
    // synonyms for the term
    public List<String> getSynonyms() {
    	List<String> synonyms = new ArrayList<String>();
    	if (this.term.getSynonyms() != null) {
    		for (BrowserSynonym synonym : this.term.getSynonyms()) {
    			synonyms.add(synonym.getSynonym());
    		}
    	}
    	return synonyms;
    }

    // maps from strings (term and/or synonyms) that match the user's search tokens to their 
    // highlighted versions
    public Map<String,String> getAllMatches() {
    	Map<String,String> matches = new HashMap<String,String>();

    	// term matches?
    	if (this.tokensMatched(this.getTerm())) {
    		matches.put(this.getTerm(), this.highlightString(this.getTerm()));
    	}
    	
    	// matching synonyms?
    	for (String synonym : this.getSynonyms()) {
    		if (this.tokensMatched(synonym)) {
    			matches.put(synonym, this.highlightString(synonym) + " <span class='synonymTag'>[synonym]</span>");
    		}
    	}
    	return matches;
    }
    
    // display string for autocomplete
    public String getAutocompleteDisplay() {
    	if (this.getMatchedTerm()) {
    		return this.getHighlightedTerm();
    	}
    	return this.getTerm() + " (" + this.getHighlightedSynonym() + ")";
    }
    
    // true if this term matched the term field
	public boolean getMatchedTerm() {
		return this.tokensMatched(this.term.getTerm());
	}

	// get the text of the term, with highlights applied
	public String getHighlightedTerm() {
		// if we don't match all tokens, return term without highlighting
		if (!this.getMatchedTerm()) { return this.term.getTerm(); }

		return this.highlightString(this.term.getTerm());
	}

	// get the first synonym which matches all search tokens, with them highlighted
	public String getHighlightedSynonym() {
		StringBuffer sb = new StringBuffer();

		if (this.term.getSynonyms() != null) {
			for (BrowserSynonym synonym : this.term.getSynonyms()) {
				if (this.tokensMatched(synonym.getSynonym())) {
					return this.highlightString(synonym.getSynonym());
				}
				sb.append(synonym);
				sb.append(" ");
		    }
		}

		if (this.tokensMatched(sb.toString())) {
		    return "matches across multiple synonyms";
		}

		return "matches across term and synonym(s)";
	}

    //------------------------------------------------------------------------
    // private instance methods
    //------------------------------------------------------------------------

	// does 's' have matches for all the search tokens (true) or not (false)?
	private boolean tokensMatched (String s) {
	    if (this.tokens == null) { return false; }

	    if (stopWordMap == null) { initMap(); }

	    String sLower = s.toLowerCase();
	    String tokenLower = null;
	    boolean foundToken = false;
	    
	    for (String token : this.tokens) {
	    	foundToken = false;
	    	tokenLower = token.toLowerCase();

	    	// If we find a piece that starts with the token, then we can keep going and check the next token.
	    	// Once we find a token that doesn't begin any piece, then we know that not all tokens matched and
	    	// we can bail out.

	    	for (String piece : this.splitTerm(sLower)) {
	    		// simple match
	    		if (piece.startsWith(tokenLower)) {
	    			foundToken = true;
	    			break;

	    		// match if we ignore apostrophes?
	    		} else if ((piece.indexOf("'") >= 0) && piece.replaceAll("'", "").startsWith(tokenLower)) {
	    			foundToken = true;
	    			break;
	    		}
	    	}

	    	if (!foundToken) {
	    		// if the missing token is a stopword, that's okay
	    		if (!stopWordMap.containsKey(tokenLower)) {
	    			return false;
	    		}
	    	}
	    }
	    return true;
	}

	// Highlight all occurrences of search tokens in 's'.  Remember that we only do prefix searching, so do
	// not highlight tokens that appear in the middle of words.  Does not care if all tokens are present.
	// Need to handle apostrophes that are embedded in 's', but not in tokens.
	private String highlightString (String s) {
	    if (this.tokens == null) { return s; }

	    String tokenLower = null;
	    String term = s;
	    String pLower = null;
	    StringBuffer termTemp = null;
	    int tokenLength = 0;
	    
	    for (String token : this.tokens) {
	    	tokenLower = token.toLowerCase();
	    	tokenLength = token.length();
	    	termTemp = new StringBuffer();
		
	    	for (String piece : this.splitTerm(term)) {
	    		pLower = piece.toLowerCase();

	    		// does this piece start with the token?
	    		if (pLower.startsWith(tokenLower)) {
	    			termTemp.append("<b>");
	    			termTemp.append(piece.substring(0, tokenLength));
	    			termTemp.append("</b>");
	    			termTemp.append(piece.substring(tokenLength));

	    		// if we strip out any apostrophes, does this piece start with the token?
	    		} else if ((pLower.indexOf("'") >= 0) && pLower.replaceAll("'", "").startsWith(tokenLower)) { 
	    			termTemp.append("<b>");

	    			int pIndex = 0;
	    			int tIndex = 0;

	    			String pChar = piece.substring(pIndex, pIndex + 1);
	    			String tChar = tokenLower.substring(tIndex, tIndex + 1);

	    			boolean done = false;

	    			// go character by character until we run out of characters in the token

	    			while (!done) {
	    				if (pChar.toLowerCase().equals(tChar)) {
	    					termTemp.append(pChar);
	    					tIndex++;
	    					if (tIndex < tokenLength) {
	    						tChar = tokenLower.substring(tIndex, tIndex + 1);
	    					} else {
	    						done = true;
	    					}

	    					pIndex++;
	    					if (!done) {
	    						pChar = piece.substring(pIndex, pIndex + 1);
	    					}

	    				} else if (pChar.equals("'")) {
	    					termTemp.append(pChar);
	    					pIndex++;
	    					pChar = piece.substring(pIndex, pIndex + 1); 
	    				}
	    			}

	    		termTemp.append("</b>");

	    		// then bring over any remaining characters from the piece (after we're done matching the token)

	    		if (pIndex < piece.length()) {
	    			termTemp.append(piece.substring(pIndex));
	    		}

	    		// this piece doesn't match the token at all
	    		} else {
	    			termTemp.append(piece);
	    		}
	    	}
	    	term = termTemp.toString();
	    }
	    return term.trim();
	}

	// split the given 'term' into a list of Strings, which when concatenated would produce the original
	// 'term'.  We split 'term' on hyphens and whitespace, and include them as separate Strings in the
	// list.  Converts other whitespace (tabs, newlines) into spaces.
	// Example: "one-cell stage embryo" ==> //	[ "one", "-", "cell", " ", "stage", " ", "embryo" ]
	private List<String> splitTerm (String term) {
		boolean hasHyphen = (term.indexOf("-") >= 0);
		boolean hasSlash = (term.indexOf("/") >= 0);

		// split on whitespace
		String[] pieces = term.split("\\s");

		ArrayList<String> out = new ArrayList<String>();

		String p = null;
		boolean first = false;

		for (int i = 0; i < pieces.length; i++) {
			// if not the first time through the loop, prepend a space
			if (out.size() != 0) {
				out.add(" ");
			}

			p = pieces[i];

			// if no hyphen in the whole string, don't bother looking
			if (!hasHyphen && !hasSlash) {
				out.add(p);

			} else if (hasHyphen && p.indexOf("-") >= 0) {
				first = true;
				for (String s : p.split("-")) {

					// if not first part of the hyphenated term, add
					// a hyphen

					if (!first) {
						out.add("-");
					}
					out.add(s);
					first = false;
				}

			} else if (hasSlash && p.indexOf("/") >= 0) {
				first = true;
				for (String s : p.split("/")) {

					// if not first part of the slash-delimited term, add a slash

					if (!first) {
						out.add("/");
					}
					out.add(s);
					first = false;
				}
			} else {
				out.add(p);
			}
		}
		return out; 
	}

    //------------------------------------------------------------------------
    // private class methods
    //------------------------------------------------------------------------

    // initialize the map of stopwords (that we can ignore when comparing tokens to terms and synonyms)
	private static void initMap() {
	    stopWordMap = new HashMap<String, String>();

	    for (String s : stopWords) {
	    	stopWordMap.put(s.toLowerCase(), s);
	    }
	    return;
	}
}
