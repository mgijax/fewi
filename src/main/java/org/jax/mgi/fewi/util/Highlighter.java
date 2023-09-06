package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class Highlighter {
	
    private Pattern pattern = null;    // regex pattern built to do the searching
    private String highlightTag = "span";
    private String highlightClass = "highlight";
    private static Pattern anchorPattern = Pattern.compile("<a(.*)</a>");

    public Highlighter(Collection<String> toFind) {
	    // build reg expr pattern string like:
	    //  "\\b(word1|word2|word3|...)"
	    // (could make the \\b an option so we could highlight text that
	    //   doesn't start on a word boundary)
	    String patternStr;	    
    		
    	if (toFind != null && toFind.size()>0)
    	{
    		String params = StringUtils.join(toFind, "|");
    		if(!params.trim().equals(""))
    		{
    			patternStr = "\\b(" + params + ")";
    			pattern = Pattern.compile( patternStr, Pattern.CASE_INSENSITIVE);
    		}
    	}
    }

    public String highLight(String inputText) {
	    // Return a copy of inputText with all text highlighting for strings
	    //  that match.
	    // Highlighting is putting text between <b>...</b>
	    //   someone who knows CSS could probably do this w/ color or something
	    // Could imagine several highLight methods for diff flavors of highlighting	
	    if (pattern == null || inputText == null || inputText.trim().equals("")){
	    	return inputText;
	    } 
	    
	    // need to exclude anchor tags so that urls in links will behave correctly.
	    // E.g. you searched for "europhenome" in a reference abstract and there is a link to "http://europhenome.org"
	    // this code is up for grabs in terms of optimizing, as long as urls in anchor tags are taken into account. - kstone
	    Matcher am = Highlighter.anchorPattern.matcher(inputText);
	    ArrayList<String> anchors = new ArrayList<String>();
	    String tempInput=inputText;
	    int count=0;
	    while(am.find())
	    {
	    	String anchor = am.group();
	    	tempInput = tempInput.substring(0,am.start()) + 
	    		"$$$"+count+"$$$" +
	    		tempInput.substring(am.end());
	    	anchors.add(anchor);
	    	count+=1;
	    }
	    Matcher matcher = pattern.matcher(tempInput);
	    tempInput = matcher.replaceAll(String.format("<%s class=%s>$1</%s>", highlightTag, highlightClass, highlightTag));
	    count=0;
	    for(String anchor : anchors)
	    {
	    	tempInput = tempInput.replace("$$$"+count+"$$$",anchor);
	    	count+=1;
	    }
	    return tempInput;
    }
}

