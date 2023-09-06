package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.jax.mgi.fe.datamodel.QueryFormOption;
import org.jax.mgi.fewi.hunter.HibernateQueryFormOptionHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding foo(s)
 */
@Repository
public class QueryFormOptionFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(QueryFormOptionFinder.class);

    @Autowired
    private HibernateQueryFormOptionHunter queryFormOptionHunter;

    /*----------------*/
    /* public methods */
    /*----------------*/

    /* Convenience method to easily retrieve a Map appropriate to generate a
     * select list (using the fewi:selectOptions tag) in a JSP page
     */
    public Map<String,String> getOptionMap (String formName, String fieldName) {
	    SearchResults<QueryFormOption> qfOptions =
		    getQueryFormOptions(formName, fieldName);

	    Map<String, String> optionMap = new LinkedHashMap<String, String>();

	    for (QueryFormOption o : qfOptions.getResultObjects()) {
		    optionMap.put(o.getSubmitValue(), o.getDisplayValue());
	    }
	    return optionMap;
    }

    /* Convenience method for easy retrieval of a list of options for a given
     * query form and field
    */
    public SearchResults<QueryFormOption> getQueryFormOptions(String formName,String fieldName) 
	{
		List<Filter> filterList = new ArrayList<Filter>();
		SearchResults<QueryFormOption> searchResults =  new SearchResults<QueryFormOption>();
	
		// if either parameter is null, just return no options
		if ((formName == null) || (fieldName == null)) {
		    return searchResults;
		}
	
		filterList.add(new Filter (SearchConstants.FORM_NAME, formName,Filter.Operator.OP_EQUAL) );
	
		filterList.add(new Filter (SearchConstants.FIELD_NAME, fieldName,Filter.Operator.OP_EQUAL) );
	
		Filter containerFilter = new Filter();
		containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
		containerFilter.setNestedFilters(filterList);
	
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter (containerFilter);
		searchParams.setPageSize (1000);
	
		return getQueryFormOptions(searchParams);
    }

    /* Retrieval of multiple QueryFormOption objects */
    public SearchResults<QueryFormOption> getQueryFormOptions(SearchParams searchParams) 
    {
        logger.debug("->getQueryFormOptions");

        // result object to be returned
        SearchResults<QueryFormOption> searchResults = new SearchResults<QueryFormOption>();

        // ask the hunter to identify which objects to return
        queryFormOptionHunter.hunt(searchParams, searchResults);
       
        return searchResults;
    }
}
