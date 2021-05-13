package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    // Return a list of search "terms", all in lowercase and computed from this.query.  (cannot be set; this is read-only)
    // Note: once computed, the value of 'terms' is cached in this.terms.  Puts the full string as the
    // last term.
    public List<String> getTerms() {
    	if ((terms == null) && (query != null)) {
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
