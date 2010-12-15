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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SolrReferenceSummaryHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
	
    public SolrReferenceSummaryHunter() {        
        /**
         * Set up the sorting filter mapping. 
         */       
        sortMap.put(SortConstants.REF_AUTHORS, new SolrSortMapper(IndexConstants.REF_AUTHOR));
        sortMap.put(SortConstants.REF_JOURNAL, new SolrSortMapper(IndexConstants.REF_JOURNAL));
        sortMap.put(SortConstants.REF_YEAR, new SolrSortMapper(IndexConstants.REF_YEAR));
        
        /**
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */      
        ArrayList <String> refList = new ArrayList <String> ();
        refList.add(IndexConstants.JNUM_ID);
        refList.add(IndexConstants.PUBMED_ID);
        
        propertyMap.put(SearchConstants.REF_ID, new SolrPropertyMapper(refList, "OR"));
        propertyMap.put(SearchConstants.REF_AUTHOR_ANY, new SolrPropertyMapper(IndexConstants.REF_AUTHOR_FORMATTED));
        propertyMap.put(SearchConstants.REF_AUTHOR_FIRST, new SolrPropertyMapper(IndexConstants.REF_FIRST_AUTHOR));
        propertyMap.put(SearchConstants.REF_AUTHOR_LAST, new SolrPropertyMapper(IndexConstants.REF_LAST_AUTHOR));
        propertyMap.put(SearchConstants.REF_JOURNAL, new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
/*        propertyMap.put(SearchConstants.REF_TEXT_ABSTRACT, new SolrPropertyMapper(IndexConstants.REF_ABSTRACT));
        propertyMap.put(SearchConstants.REF_TEXT_TITLE, new SolrPropertyMapper(IndexConstants.REF_TITLE));*/
        propertyMap.put(SearchConstants.REF_YEAR, new SolrPropertyMapper(IndexConstants.REF_YEAR));
        propertyMap.put(SearchConstants.SEQ_KEY, new SolrPropertyMapper(IndexConstants.SEQ_KEY));
        propertyMap.put(SearchConstants.ALL_KEY, new SolrPropertyMapper(IndexConstants.ALL_KEY));

        // Placeholder for the new field mappings for title and abstract
        
        ArrayList <String> titleList = new ArrayList <String> ();
        titleList.add(IndexConstants.REF_TITLE_STEMMED);
        titleList.add(IndexConstants.REF_TITLE_UNSTEMMED);
        
        ArrayList <String> abstractList = new ArrayList <String> ();
        abstractList.add(IndexConstants.REF_ABSTRACT_STEMMED);
        abstractList.add(IndexConstants.REF_ABSTRACT_UNSTEMMED);
        
        propertyMap.put(SearchConstants.REF_TEXT_ABSTRACT, new SolrReferenceTextSearchPropertyMapper(abstractList, "OR"));
        propertyMap.put(SearchConstants.REF_TEXT_TITLE, new SolrReferenceTextSearchPropertyMapper(titleList, "OR"));
        
        // Put in the mashed up version of the title + abstract in a single field.
        
        ArrayList <String> titleAbstractList = new ArrayList <String> ();
        titleAbstractList.add(IndexConstants.REF_TITLE_ABSTRACT_STEMMED);
        titleAbstractList.add(IndexConstants.REF_TITLE_ABSTRACT_UNSTEMMED);
        
        propertyMap.put(SearchConstants.REF_TEXT_TITLE_ABSTRACT, new SolrReferenceTextSearchPropertyMapper(titleAbstractList, "OR"));
        
        
        
        
        
        propertyMap.put(FacetConstants.REF_AUTHORS, new SolrPropertyMapper(IndexConstants.REF_AUTHOR_FACET));
        propertyMap.put(FacetConstants.REF_JOURNALS, new SolrPropertyMapper(IndexConstants.REF_JOURNAL_FACET));
        propertyMap.put(FacetConstants.REF_YEAR, new SolrPropertyMapper(IndexConstants.REF_YEAR));
        propertyMap.put(FacetConstants.REF_CURATED_DATA, new SolrPropertyMapper(IndexConstants.REF_HAS_DATA));
        
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to pack it into the 
         * keys collection in the response.
         */       
        keyString = IndexConstants.REF_KEY;
    }
	
	@Value("${solr.reference.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
	
	private void checkFilter (Filter filter) {
	    if (filter.isBasicFilter()) {
	        return;
	    }
	    else {
	        List <Filter> flist = filter.getNestedFilters();
	        Boolean foundTitle = Boolean.FALSE;
	        Boolean foundAbstract = Boolean.FALSE;
	        String textToSearch = "";
	        
	        for (Filter f: flist) {
	            if (f.isBasicFilter()) {
	                if (f.getProperty().equals(SearchConstants.REF_TEXT_ABSTRACT)) {
	                    textToSearch = f.getValue();
	                    foundAbstract = Boolean.TRUE;
	                }
                    if (f.getProperty().equals(SearchConstants.REF_TEXT_TITLE)) {
                        textToSearch = f.getValue();
                        foundTitle = Boolean.TRUE;
                    }	                
	                
	            }
	            else {
	                checkFilter(f);
	            }
	        }
	        
	        if (foundTitle && foundAbstract) {
	            filter.setProperty(SearchConstants.REF_TEXT_TITLE_ABSTRACT);
	            filter.setValue(textToSearch);
	            filter.setOperator(Filter.OP_CONTAINS);
	            filter.setNestedFilters(new ArrayList<Filter> ());
	        }
	    }
	}
		
	protected SearchParams preProcessSearchParams(SearchParams searchParams) {
	    
	    Filter filter = searchParams.getFilter();
	    if (!filter.isBasicFilter()) {
	        checkFilter(filter);
	    }
	    return searchParams;
	}
	
}
