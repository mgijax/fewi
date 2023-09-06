package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.propertyMapper.SolrReferenceTextSearchPropertyMapper;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;

public class SolrReferenceSummaryBaseHunter<T> extends SolrHunter<T> {
    
    /***
     * The constructor sets up this hunter so that it is specific to reference
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     * 
     * This class is the base for all of the other reference summary hunters.
     * The reason that this needed to be created was mostly for maintenence reasons.
     * 
     * The reference hunters have spawned at least 7 different hunters to date.  Each
     * of these hunters share 95%+ common configuration.  By abstracting this 
     * configuration information out to this base hunter, it allows us to have a 
     * single point of change for most reference changes.
     * 
     * Also, since this is a base class, any of the specific settings can be
     * overwritten in the extending classes.  Allowing them to be customized
     * at need.
     */
	
    public SolrReferenceSummaryBaseHunter() {        
        
    	/**
         * Set up the sorting filter mapping.
         * Technically this mapping is no longer strictly required, as 
         * long as the requested sort column happens to match the sorting 
         * column in the index. 
         */
    	
        sortMap.put(SortConstants.REF_AUTHORS, 
        		new SolrSortMapper(IndexConstants.REF_AUTHOR));
        sortMap.put(SortConstants.REF_JOURNAL, 
        		new SolrSortMapper(IndexConstants.REF_JOURNAL));
        sortMap.put(SortConstants.REF_YEAR, 
        		new SolrSortMapper(IndexConstants.REF_YEAR));
        
        /**
         * Setup the property map.  This maps from the properties of the 
         * incoming filter list to the corresponding field names in the Solr 
         * implementation.
         */
        
        /*
         * This is a 1 -> N Relationship, a single field on the QF maps
         * to multiple fields in the index.
         */
        
        
        // The 1->N Mapping should be joined with OR's
        propertyMap.put(SearchConstants.REF_ID, 
        		new SolrPropertyMapper(IndexConstants.REF_ID));
        
        propertyMap.put(IndexConstants.REF_DISEASE_RELEVANT_MARKER_ID, 
        		new SolrPropertyMapper(IndexConstants.REF_DISEASE_RELEVANT_MARKER_ID));
        propertyMap.put(IndexConstants.REF_DISEASE_ID, 
        		new SolrPropertyMapper(IndexConstants.REF_DISEASE_ID));
        
        propertyMap.put(SearchConstants.REF_AUTHOR_ANY, 
        		new SolrPropertyMapper(
        				IndexConstants.REF_AUTHOR_FORMATTED));
        propertyMap.put(SearchConstants.REF_AUTHOR_FIRST, 
        		new SolrPropertyMapper(
        				IndexConstants.REF_FIRST_AUTHOR));
        propertyMap.put(SearchConstants.REF_AUTHOR_LAST, 
        		new SolrPropertyMapper(
        				IndexConstants.REF_LAST_AUTHOR));
        propertyMap.put(SearchConstants.REF_JOURNAL, 
        		new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
        propertyMap.put(SearchConstants.REF_YEAR, 
        		new SolrPropertyMapper(IndexConstants.REF_YEAR));
        propertyMap.put(SearchConstants.SEQ_KEY, 
        		new SolrPropertyMapper(IndexConstants.SEQ_KEY));
        propertyMap.put(SearchConstants.ALL_KEY, 
        		new SolrPropertyMapper(IndexConstants.ALL_KEY));
        propertyMap.put(SearchConstants.MRK_KEY, 
        		new SolrPropertyMapper(IndexConstants.MRK_KEY));

        /*
         *  This is a 1->N Relationship from QF to index fields
         */
        
        ArrayList <String> abstractList = new ArrayList <String> ();
        abstractList.add(IndexConstants.REF_ABSTRACT_STEMMED);
        abstractList.add(IndexConstants.REF_ABSTRACT_UNSTEMMED);
        
        propertyMap.put(SearchConstants.REF_TEXT_ABSTRACT, 
        		new SolrReferenceTextSearchPropertyMapper(
        				abstractList, "OR"));

        /*
         * This is a 1->N Relationship from QF to index fields
         */
        
        ArrayList <String> titleList = new ArrayList <String> ();
        titleList.add(IndexConstants.REF_TITLE_STEMMED);
        titleList.add(IndexConstants.REF_TITLE_UNSTEMMED);
        
        // The 1->N Mapping should be joined with OR's
        propertyMap.put(SearchConstants.REF_TEXT_TITLE, 
        		new SolrReferenceTextSearchPropertyMapper(
        				titleList, "OR"));
        
        /*
         * This is a 1->N Relationship from QF to index fields
         */
                
        ArrayList <String> titleAbstractList = new ArrayList <String> ();
        titleAbstractList.add(IndexConstants.REF_TITLE_ABSTRACT_STEMMED);
        titleAbstractList.add(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED);
        
        // The 1->N Mapping should be joined with OR's
        propertyMap.put(SearchConstants.REF_TEXT_TITLE_ABSTRACT, 
        		new SolrReferenceTextSearchPropertyMapper(
        				titleAbstractList, "OR"));
                
        propertyMap.put(FacetConstants.REF_AUTHORS, 
        		new SolrPropertyMapper(
        				IndexConstants.REF_AUTHOR_FACET));
        propertyMap.put(FacetConstants.REF_JOURNALS, 
        		new SolrPropertyMapper(
        				IndexConstants.REF_JOURNAL_FACET));
        propertyMap.put(FacetConstants.REF_YEAR, 
        		new SolrPropertyMapper(IndexConstants.REF_YEAR));
        propertyMap.put(FacetConstants.REF_CURATED_DATA, 
        		new SolrPropertyMapper(IndexConstants.REF_HAS_DATA));
        
        // End property mapping section
        
        /**
         * What fields might we want highlighted results for?
         * Any field listed here will be requested for highlighting
         * all the way down to Solr.
         */
        
        highlightFields.add(IndexConstants.REF_ABSTRACT_STEMMED);
        highlightFields.add(IndexConstants.REF_ABSTRACT_UNSTEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_STEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_UNSTEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_ABSTRACT_STEMMED);
        highlightFields.add(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED);
        highlightFields.add(IndexConstants.REF_AUTHOR_FORMATTED);
        
        /* A reverse Mapping of Highlightable fields in the index to what 
         * parameter it came from.  This supports the N->1 Mapping 
         * back to the QF Parameters.
         */
        
        fieldToParamMap.put(IndexConstants.REF_ABSTRACT_STEMMED,
        		SearchConstants.REF_TEXT_ABSTRACT);
        fieldToParamMap.put(IndexConstants.REF_ABSTRACT_UNSTEMMED,
        		SearchConstants.REF_TEXT_ABSTRACT);
        fieldToParamMap.put(IndexConstants.REF_TITLE_STEMMED,
        		SearchConstants.REF_TEXT_TITLE);
        fieldToParamMap.put(IndexConstants.REF_TITLE_UNSTEMMED,
        		SearchConstants.REF_TEXT_TITLE);
        fieldToParamMap.put(IndexConstants.REF_TITLE_ABSTRACT_STEMMED,
        		SearchConstants.REF_TEXT_TITLE_ABSTRACT);
        fieldToParamMap.put(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED,
        		SearchConstants.REF_TEXT_TITLE_ABSTRACT);
        fieldToParamMap.put(IndexConstants.REF_AUTHOR_FORMATTED, 
        		SearchConstants.REF_AUTHOR);
        fieldToParamMap.put(IndexConstants.REF_FIRST_AUTHOR, 
        		SearchConstants.REF_AUTHOR);
        fieldToParamMap.put(IndexConstants.REF_LAST_AUTHOR, 
        		SearchConstants.REF_AUTHOR);
        fieldToParamMap.put(IndexConstants.REF_AUTHOR_FACET, 
        		SearchConstants.REF_AUTHOR);
        
        
        /*
         * The base hunter sets nothing at all to be returned.  
         * The specific return requirements are handled in the subclasses.
         */
        
    }
		
    /**
     * checkFilter
     * @param filter
     * This private method is unique to the reference grouping of hunters.
     * Its responsible to create a new query field if both title and abstract
     * searches are present.
     * This DEPENDS on the fact that the abstract and title fields are 
     * going to be found in a nested filter.  If that ever changes this 
     * code will break.
     */
    
	private void checkFilter (Filter filter) {
		
		// This is our base case, if we find a base filter return.
		
	    if (filter.isBasicFilter()) {
	        return;
	    }
	    
	    // Otherwise recurse, searching for the text searching clauses
	    
	    else {
	        List <Filter> flist = filter.getNestedFilters();
	        Boolean foundTitle = Boolean.FALSE;
	        Boolean foundAbstract = Boolean.FALSE;
	        String textToSearch = "";
	        
	        /**
	         * Iterate through the nested filters.  The two text filters 
	         * that we are interested in should be at the same level.
	         */
	        
	        for (Filter f: flist) {
	            if (f.isBasicFilter()) {
	                if (f.getProperty().equals(
	                		SearchConstants.REF_TEXT_ABSTRACT)) {
	                	
	                    textToSearch = f.getValue();
	                    foundAbstract = Boolean.TRUE;
	                }
                    if (f.getProperty().equals(
                    		SearchConstants.REF_TEXT_TITLE)) {
                        
                    	textToSearch = f.getValue();
                        foundTitle = Boolean.TRUE;
                    }	                
	                
	            }
	            // Found another nested filter set, recurse!
	            else {
	                checkFilter(f);
	            }
	        }
	        
	        // Found what we are looking for, make a new filter and 
	        // replace.
	        
	        if (foundTitle && foundAbstract) {
	            filter.setProperty(
	            		SearchConstants.REF_TEXT_TITLE_ABSTRACT);
	            filter.setValue(textToSearch);
	            filter.setOperator(Filter.Operator.OP_CONTAINS);
	            filter.setNestedFilters(new ArrayList<Filter> ());
	        }
	    }
	}
		
	/**
	 * References have implemented the preProcessSearchParams hook.
	 * For the case of references we need to recursively dig through the 
	 * search parameters to find the text searching clauses.
	 * 
	 * If it finds both an abstract and a title search it replaces those
	 * with a search to the aggregate field in the index.
	 * 
	 */
	
	protected SearchParams preProcessSearchParams(
			SearchParams searchParams) {
	    
	    Filter filter = searchParams.getFilter();
	    if (!filter.isBasicFilter()) {
	        checkFilter(filter);
	    }
	    return searchParams;
	}
	
}
