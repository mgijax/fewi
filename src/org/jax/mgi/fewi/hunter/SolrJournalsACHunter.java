package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.stereotype.Repository;

@Repository
public class SolrJournalsACHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it pulls back a list of
     * possible journals for a given input string.
     */
    public SolrJournalsACHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.REF_JOURNAL, new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
                
        // Set the url for the solr instance.
        
        solrUrl = "http://cardolan.informatics.jax.org:8983/solr/journalsAC/";
        
        /*
         * For this hunter we are not interested in keys, we instead want to pack 
         * a specific field from the returned solr documents into the response.
         */
        
        otherString = IndexConstants.REF_JOURNAL_SORT;
        
    }
   
}