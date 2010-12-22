package org.jax.mgi.fewi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Highlighter {
	
    private Pattern pattern;    // regex pattern built to do the searching
    private String highlightTag = "span";
    private String highlightClass = "highlight";

    public Highlighter(String[] toFind) {
	    // build reg expr pattern string like:
	    //  "\\b(word1|word2|word3|...)"
	    // (could make the \\b an option so we could highlight text that
	    //   doesn't start on a word boundary)
	    String patternStr;	    
    		
    	if (toFind != null && !"".equals(toFind)){
    		patternStr = "\\b(" + StringUtils.join(toFind, "|") + ")";
    	    pattern = Pattern.compile( patternStr, Pattern.CASE_INSENSITIVE);
    	}
    }

    public String highLight(String inputText) {
	    // Return a copy of inputText with all text highlighting for strings
	    //  that match.
	    // Highlighting is putting text between <b>...</b>
	    //   someone who knows CSS could probably do this w/ color or something
	    // Could imagine several highLight methods for diff flavors of highlighting	
	    Matcher matcher = pattern.matcher(inputText);
	    if (pattern == null){
	    	return inputText;
	    } 
	    return( matcher.replaceAll(String.format("<%s class=%s>$1</%s>", highlightTag, highlightClass, highlightTag)));
    }
}

