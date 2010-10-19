package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrCreSummaryHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to cre
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrCreSummaryHunter() {        
        
        /*
         * Setup the sort mapping.
         */
        sortMap.put(SortConstants.CRE_DRIVER, new SolrSortMapper(IndexConstants.ALL_DRIVER));
        sortMap.put(SortConstants.CRE_SYMBOL, new SolrSortMapper(IndexConstants.ALL_SYMBOL_SORT));
        sortMap.put(SortConstants.CRE_TYPE, new SolrSortMapper(IndexConstants.ALL_TYPE_SORT));
        sortMap.put(SortConstants.CRE_INDUCIBLE, new SolrSortMapper(IndexConstants.ALL_INDUCIBLE));
        sortMap.put(SortConstants.CRE_REF_COUNT, new SolrSortMapper(IndexConstants.ALL_REFERENCE_COUNT_SORT));
        sortMap.put(SortConstants.CRE_IN_ALIMENTARY_SYSTEM, new SolrSortMapper(IndexConstants.CRE_IN_ALIMENTARY_SYSTEM));
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.ALL_DRIVER, new SolrPropertyMapper(IndexConstants.ALL_DRIVER));
        propertyMap.put(SearchConstants.ALL_SYSTEM, new SolrPropertyMapper(IndexConstants.CRE_ALL_SYSTEM));
        
        // Set the url for the solr instance.
        
        solrUrl = "http://cardolan.informatics.jax.org:8983/solr/cre/";
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we are looking for the 
         * standard list of keys to be returned.
         */
        
        keyString = IndexConstants.ALL_KEY;
        
    }
    
	@Value("${solr.cre.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
   
}