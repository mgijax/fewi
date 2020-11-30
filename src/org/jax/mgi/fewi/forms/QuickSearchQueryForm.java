package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.List;

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
    		terms = new ArrayList<String>();
    		for (String term : query.replace(',', ' ').split(" ")) {
    			terms.add(term.toLowerCase());
    		}
    		terms.add(query);
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
		return "QuickSearchQueryForm [query=" + query + "]";
	}
}
