package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.util.FewiUtil;

/*-------*/
/* class */
/*-------*/

public class QuickSearchQueryForm {

    //--------------------//
    // instance variables
    //--------------------//
    private String query;
    private List<String> processFilter;
    private List<String> componentFilter;
    private List<String> functionFilter;
    private List<String> terms;

    //--------------------//
    // accessors
    //--------------------//
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }

    /* For certain circumstances, we want to automatically wrap a query string in double-quotes, to ensure it doesn't
     * get split up.  The current list of circumstances is:
     * 	1. cases where the input string begins with a number, is followed by a comma, then has other numbers and letters.
     * 		(but not spaces)
     */
    private Pattern case1 = Pattern.compile("^[0-9]+,[A-Za-z0-9]+$");
    private String autoQuote(String query) {
    	Matcher case1match = case1.matcher(query);
    	if (case1match.matches()) {
    		return "\"" + query + "\"";
    	}
    	
    	// no special handling needed, just return the original string
    	return query;
    }
    
    // Return a list of search "terms", all in lowercase and computed from this.query.  (cannot be set; this is read-only)
    // Note: once computed, the value of 'terms' is cached in this.terms.  Puts the full string as the
    // last term.
    public List<String> getTerms() {
    	if ((terms == null) && (query != null)) {
    		query = autoQuote(query);
    		
    		// If our split fails, it's because of an unbalanced number of double-quotes.  In that case,
    		// just strip them out and proceed.
    		try {
    			terms = FewiUtil.intelligentSplit(query.replace(',', ' ').toLowerCase());
    		} catch (Exception e) {
    			terms = Arrays.asList(query.replace(',', ' ').replace('"',' ').toLowerCase().split(" "));
    		}
    		terms.add(query.replace('"',  ' '));
    	}
    	return terms;
    }
    
    public List<String> getComponentFilter() {
		return componentFilter;
	}
	public void setComponentFilter(List<String> componentFilter) {
		this.componentFilter = componentFilter;
	}
	public List<String> getFunctionFilter() {
		return functionFilter;
	}
	public void setFunctionFilter(List<String> functionFilter) {
		this.functionFilter = functionFilter;
	}
	public List<String> getProcessFilter() {
		return processFilter;
	}
	public void setProcessFilter(List<String> processFilter) {
		this.processFilter = processFilter;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("QS");
		if (query != null) { sb.append(" query="); sb.append(query); }
		if (processFilter != null) { sb.append(", processFilter="); sb.append(processFilter); }
		if (componentFilter != null) { sb.append(", componentFilter="); sb.append(componentFilter); }
		if (functionFilter != null) { sb.append(", functionFilter="); sb.append(functionFilter); }
		return sb.toString();
	}
}
