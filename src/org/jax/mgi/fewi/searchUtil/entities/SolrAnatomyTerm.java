package org.jax.mgi.fewi.searchUtil.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SolrAnatomyTerm
{
	// stopwords are duplicated from stopwords.txt in the anatomyAC
	// Solr index definition
	private static String[] stopWords = { "and", "from", "of", "or",
		"the", "their", "to" };
	private static HashMap<String, String> stopWordMap = null;

	String structureKey;
	String accID;
	String structure;
	String startStage;
	String endStage;
	List<String> synonyms;
	List<String> tokens;
	
	public String getStructureKey() {
		return structureKey;
	}
	public void setStructureKey(String structureKey) {
		this.structureKey = structureKey;
	}

	public String getAccID() {
		return accID;
	}
	public void setAccID(String accID) {
		this.accID = accID;
	}

	public String getStructure() {
		return structure;
	}
	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getStartStage() {
		return startStage;
	}
	public void setStartStage(String startStage) {
		this.startStage = startStage;
	}

	public String getEndStage() {
		return endStage;
	}
	public void setEndStage(String endStage) {
		this.endStage = endStage;
	}

	public List<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}

	public String toString() {
		return "SolrAnatomyTerm [ structureKey: "
			+ structureKey + ", "
			+ accID + ", "
			+ structure + " ]";
	}

	// --- convenience methods --- //

	// convenience method for getting a printable string for the stage
	// range of this term
	public String getStageRange() {
		StringBuffer sb = new StringBuffer();

		sb.append("TS");
		sb.append(this.startStage);

		if (!this.startStage.equals(this.endStage)) {
			sb.append("-");
			sb.append(this.endStage);
		}
		return sb.toString();
	}

	// set the list of search tokens, for use in highlighting
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	// do all the search tokens match in the structure field?
	public boolean getMatchedStructure() {
		return this.tokensMatched(this.structure);
	}

	// get the structure, with highlights applied
	public String getHighlightedStructure() {
		// if we don't match all tokens, return without highlighting
		if (!this.getMatchedStructure()) { return this.structure; }

		return this.highlightString(this.structure);
	}

	// get the first synonym which matches all search tokens, with them
	// highlighted
	public String getHighlightedSynonym() {
		StringBuffer sb = new StringBuffer();

		if (this.synonyms != null) {
		    for (String synonym : this.synonyms) {
			if (this.tokensMatched(synonym)) {
				return this.highlightString(synonym);
			}
			sb.append(synonym);
			sb.append(" ");
		    }
		}

		if (this.tokensMatched(sb.toString())) {
		    return "matches across multiple synonyms";
		}
		
		for (String t : this.tokens) {
			if (t.matches("[Mm][pP]:[0-9]+")) {
				return "";
			}
		}

		return "no matching synonym";
	}

	// --- private methoods --- //

	// does 's' have matches for all the search tokens?
	private boolean tokensMatched (String s) {
	    if (this.tokens == null) { return false; }

	    if (stopWordMap == null) { initMap(); }

	    String sLower = s.toLowerCase();
	    String tokenLower = null;
	    boolean foundToken = false;
	    
	    for (String token : this.tokens) {
		foundToken = false;
		tokenLower = token.toLowerCase();

		// If we find a piece that starts with the token, then we can
		// keep going and check the next token.  Once we find a token
		// that doesn't begin any piece, then we know that not all 
		// tokens matched and we can bail out.

		for (String piece : this.splitTerm(sLower)) {
		    // simple match
		    if (piece.startsWith(tokenLower)) {
			foundToken = true;
			break;

		    // match if we ignore apostrophes?
		    } else if ((piece.indexOf("'") >= 0) &&
			piece.replaceAll("'", "").startsWith(tokenLower)) {
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

	// Highlight all occurrences of search tokens in 's'.  Remember that
	// we only do prefix searching, so do not highlight tokens that appear
	// in the middle of words.  Does not care if all tokens are present.
	// Need to handle apostrophes that are embedded in 's', but not in
	// tokens.
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

		    // if we strip out any apostrophes, does this piece start
		    // with the token?
		    } else if ((pLower.indexOf("'") >= 0) && 
		        pLower.replaceAll("'", "").startsWith(tokenLower)) { 

			termTemp.append("<b>");

			int pIndex = 0;
			int tIndex = 0;

			String pChar = piece.substring(pIndex, pIndex + 1);
			String tChar = tokenLower.substring(tIndex, tIndex + 1);

			boolean done = false;

			// go character by character until we run out of
			// characters in the token

			while (!done) {
			    if (pChar.toLowerCase().equals(tChar)) {
				termTemp.append(pChar);

				tIndex++;
				if (tIndex < tokenLength) {
				    tChar = tokenLower.substring(tIndex,
					tIndex + 1);
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

			// then bring over any remaining characters from the
			// piece (after we're done matching the token)

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

	// split the given 'term' into a list of Strings, which when
	// concatenated would produce the original 'term'.  We split 'term' on
	// hyphens and whitespace, and include them as separate Strings in the
	// list.  Converts other whitespace (tabs, newlines) into spaces.
	// Example: "one-cell stage embryo" ==>
	//	[ "one", "-", "cell", " ", "stage", " ", "embryo" ]
	private List<String> splitTerm (String term) {
	    boolean hasHyphen = (term.indexOf("-") >= 0);

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
		if (!hasHyphen) {
		    out.add(p);

		} else if (p.indexOf("-") >= 0) {
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
		} else {
		    out.add(p);
		}
	    }
	    return out; 
	}

	private static void initMap() {
	    stopWordMap = new HashMap<String, String>();

	    for (String s : stopWords) {
		stopWordMap.put(s.toLowerCase(), s);
	    }
	    return;
	}
}
