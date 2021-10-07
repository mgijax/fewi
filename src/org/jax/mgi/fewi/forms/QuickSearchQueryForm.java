package org.jax.mgi.fewi.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.shr.fe.IndexConstants;

/*-------*/
/* class */
/*-------*/

public class QuickSearchQueryForm {

	public static List<String> QUERY_TYPE_OPTIONS = new ArrayList<String>();
	public static Map<String, String> QUERY_TYPE_OPTION_MAP = new LinkedHashMap<String,String>();
	static {
		QUERY_TYPE_OPTIONS.add(IndexConstants.QS_SEARCHTYPE_EXACT_PHRASE);
		QUERY_TYPE_OPTIONS.add(IndexConstants.QS_SEARCHTYPE_KEYWORDS);
		QUERY_TYPE_OPTIONS.add(IndexConstants.QS_SEARCHTYPE_MOUSE_COORD);
		QUERY_TYPE_OPTIONS.add(IndexConstants.QS_SEARCHTYPE_HUMAN_COORD);

		QUERY_TYPE_OPTION_MAP.put(IndexConstants.QS_SEARCHTYPE_EXACT_PHRASE, "Keywords, Symbols, or IDs (exact phrase)");
		QUERY_TYPE_OPTION_MAP.put(IndexConstants.QS_SEARCHTYPE_KEYWORDS, "Keywords, Symbols, or IDs");
		QUERY_TYPE_OPTION_MAP.put(IndexConstants.QS_SEARCHTYPE_MOUSE_COORD, "Mouse Location");
		QUERY_TYPE_OPTION_MAP.put(IndexConstants.QS_SEARCHTYPE_HUMAN_COORD, "Human Location");
	};
	public static String QUERY_TYPE_DEFAULT = IndexConstants.QS_SEARCHTYPE_EXACT_PHRASE;
			
    //--------------------//
    // instance variables
    //--------------------//
    private String query;
    private String queryType;
    private List<String> processFilter;
    private List<String> componentFilter;
    private List<String> functionFilter;
    private List<String> phenotypeFilter;
    private List<String> expressionFilter;
    private List<String> diseaseFilter;
    private List<String> featureTypeFilter;
    private List<String> terms;

    //--------------------//
    // accessors
    //--------------------//
    public String getQuery() {
        return query.replaceAll("&#39;", ",");			// reverse escaping done for display of single-quotes in query box
    }
    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryType() {
   		// If missing or unrecognized, default to standard text search mechanism.
    	if ((queryType == null) || !QUERY_TYPE_OPTION_MAP.containsKey(queryType)) {
    		// If this appears to be a coordinate, assume it's a mouse coordinate.
    		if (this.query != null) {
    			if (this.query.matches("[cC][hH][rR][0-9XYMT]+:[0-9]+-[0-9]+")) {		// full coordinate range
    				return IndexConstants.QS_SEARCHTYPE_MOUSE_COORD;
    			} else if (this.query.matches("[cC][hH][rR][0-9XYMT]+:[0-9]+")) {		// point coordinate
    				return IndexConstants.QS_SEARCHTYPE_MOUSE_COORD;
    			} else if (this.query.matches("[cC][hH][rR][0-9XYMT]+")) {			// just a chromosome
    				return IndexConstants.QS_SEARCHTYPE_MOUSE_COORD;
    			}
    		}
    		if (this.query.indexOf("*") >= 0) {
    			return IndexConstants.QS_SEARCHTYPE_KEYWORDS;					// fall back on keywords for wildcard search
    		}
    		return QUERY_TYPE_DEFAULT;
    	}
   		if (this.query.indexOf("*") >= 0) {
   			return IndexConstants.QS_SEARCHTYPE_KEYWORDS;					// fall back on keywords for wildcard search
   		}
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	/* For certain circumstances, we want to automatically wrap a query string in double-quotes, to ensure it doesn't
     * get split up.  The current list of circumstances is:
     * 	1. cases where the input string begins with a number, is followed by a comma, then has other numbers and letters.
     * 		(but not spaces)
     *  2. cases where the query type is set to look for an exact phrase
     */
    private Pattern case1 = Pattern.compile("^[0-9]+,[A-Za-z0-9]+$");
    private String autoQuote(String query) {
    	Matcher case1match = case1.matcher(query);
    	if (case1match.matches() || IndexConstants.QS_SEARCHTYPE_EXACT_PHRASE.equals(this.getQueryType())) {
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
    
    public List<String> getExpressionFilter() {
		return expressionFilter;
	}
	public void setExpressionFilter(List<String> expressionFilter) {
		this.expressionFilter = expressionFilter;
	}
	public List<String> getPhenotypeFilter() {
		return phenotypeFilter;
	}
	public void setPhenotypeFilter(List<String> phenotypeFilter) {
		this.phenotypeFilter = phenotypeFilter;
	}
	public List<String> getDiseaseFilter() {
		return diseaseFilter;
	}
	public void setDiseaseFilter(List<String> diseaseFilter) {
		this.diseaseFilter = diseaseFilter;
	}
	public List<String> getFeatureTypeFilter() {
		return featureTypeFilter;
	}
	public void setFeatureTypeFilter(List<String> featureTypeFilter) {
		this.featureTypeFilter = featureTypeFilter;
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
