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
    private String query;						// basic search fields
    private String queryType;
    private List<String> terms;

    private List<String> processFilterF;		// Genome Feature filters
    private List<String> componentFilterF;
    private List<String> functionFilterF;
    private List<String> phenotypeFilterF;
    private List<String> expressionFilterF;
    private List<String> diseaseFilterF;
    private List<String> featureTypeFilterF;

    private List<String> featureTypeFilterA;	// Allele filters
    private List<String> phenotypeFilterA;
    private List<String> diseaseFilterA;

    private List<String> processFilterV;		// Vocabulary Term filters
    private List<String> componentFilterV;
    private List<String> functionFilterV;
    private List<String> phenotypeFilterV;
    private List<String> expressionFilterV;
    private List<String> diseaseFilterV;

    private List<String> phenotypeFilterS;		// strain filters
    private List<String> diseaseFilterS;

    private List<String> featureTypeFilterO;	// other IDs filters

    //--------------------//
    // accessors
    //--------------------//
    public String getQuery() {
        // reverse escaping done for display of single-quotes in query box, remove leading/trailing spaces
        return query.replaceAll("&#39;", ",").trim();
    }
    public void setQuery(String query) {
        this.query = query.trim();				// ensure no leading/trailing spaces
    }

    public String getQueryType() {

    	boolean queryIsQuoted = false;
    	if (this.query.startsWith("\"") && this.query.endsWith("\"")) {
    		queryIsQuoted = true;
    	}

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
			if (((this.query.indexOf("*") >= 0) || (this.query.indexOf(",") >= 0)) && !queryIsQuoted ){
				return IndexConstants.QS_SEARCHTYPE_KEYWORDS;		// fall back on keywords for wildcard and comma
			}

    		return QUERY_TYPE_DEFAULT;
    	}
		if (((this.query.indexOf("*") >= 0) || (this.query.indexOf(",") >= 0)) && !queryIsQuoted ){
			return IndexConstants.QS_SEARCHTYPE_KEYWORDS;		// fall back on keywords for wildcard and comma
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
    		return "\"" + query.replaceAll("\"", "") + "\"";
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
    
    public List<String> getExpressionFilterF() {
		return expressionFilterF;
	}
	public void setExpressionFilterF(List<String> expressionFilter) {
		this.expressionFilterF = expressionFilter;
	}
	public List<String> getPhenotypeFilterF() {
		return phenotypeFilterF;
	}
	public void setPhenotypeFilterF(List<String> phenotypeFilter) {
		this.phenotypeFilterF = phenotypeFilter;
	}
	public List<String> getDiseaseFilterF() {
		return diseaseFilterF;
	}
	public void setDiseaseFilterF(List<String> diseaseFilter) {
		this.diseaseFilterF = diseaseFilter;
	}
	public List<String> getFeatureTypeFilterF() {
		return featureTypeFilterF;
	}
	public void setFeatureTypeFilterF(List<String> featureTypeFilter) {
		this.featureTypeFilterF = featureTypeFilter;
	}
	public List<String> getComponentFilterF() {
		return componentFilterF;
	}
	public void setComponentFilterF(List<String> componentFilter) {
		this.componentFilterF = componentFilter;
	}
	public List<String> getFunctionFilterF() {
		return functionFilterF;
	}
	public void setFunctionFilterF(List<String> functionFilter) {
		this.functionFilterF = functionFilter;
	}
	public List<String> getProcessFilterF() {
		return processFilterF;
	}
	public void setProcessFilterF(List<String> processFilter) {
		this.processFilterF = processFilter;
	}

	public List<String> getFeatureTypeFilterA() {
		return featureTypeFilterA;
	}
	public void setFeatureTypeFilterA(List<String> featureTypeFilterA) {
		this.featureTypeFilterA = featureTypeFilterA;
	}
	public List<String> getPhenotypeFilterA() {
		return phenotypeFilterA;
	}
	public void setPhenotypeFilterA(List<String> phenotypeFilterA) {
		this.phenotypeFilterA = phenotypeFilterA;
	}
	public List<String> getDiseaseFilterA() {
		return diseaseFilterA;
	}
	public void setDiseaseFilterA(List<String> diseaseFilterA) {
		this.diseaseFilterA = diseaseFilterA;
	}


	public List<String> getProcessFilterV() {
		return processFilterV;
	}
	public void setProcessFilterV(List<String> processFilterV) {
		this.processFilterV = processFilterV;
	}
	public List<String> getComponentFilterV() {
		return componentFilterV;
	}
	public void setComponentFilterV(List<String> componentFilterV) {
		this.componentFilterV = componentFilterV;
	}
	public List<String> getFunctionFilterV() {
		return functionFilterV;
	}
	public void setFunctionFilterV(List<String> functionFilterV) {
		this.functionFilterV = functionFilterV;
	}
	public List<String> getPhenotypeFilterV() {
		return phenotypeFilterV;
	}
	public void setPhenotypeFilterV(List<String> phenotypeFilterV) {
		this.phenotypeFilterV = phenotypeFilterV;
	}
	public List<String> getExpressionFilterV() {
		return expressionFilterV;
	}
	public void setExpressionFilterV(List<String> expressionFilterV) {
		this.expressionFilterV = expressionFilterV;
	}
	public List<String> getDiseaseFilterV() {
		return diseaseFilterV;
	}
	public void setDiseaseFilterV(List<String> diseaseFilterV) {
		this.diseaseFilterV = diseaseFilterV;
	}

	public List<String> getPhenotypeFilterS() {
		return phenotypeFilterS;
	}
	public void setPhenotypeFilterS(List<String> phenotypeFilterS) {
		this.phenotypeFilterS = phenotypeFilterS;
	}
	public List<String> getDiseaseFilterS() {
		return diseaseFilterS;
	}
	public void setDiseaseFilterS(List<String> diseaseFilterS) {
		this.diseaseFilterS = diseaseFilterS;
	}
	public List<String> getFeatureTypeFilterO() {
		return featureTypeFilterO;
	}
	public void setFeatureTypeFilterO(List<String> featureTypeFilterO) {
		this.featureTypeFilterO = featureTypeFilterO;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("QS");
		if (query != null) { sb.append(" query="); sb.append(query); }
		if (processFilterF != null) { sb.append(", processFilter="); sb.append(processFilterF); }
		if (componentFilterF != null) { sb.append(", componentFilter="); sb.append(componentFilterF); }
		if (functionFilterF != null) { sb.append(", functionFilter="); sb.append(functionFilterF); }
		return sb.toString();
	}
}
