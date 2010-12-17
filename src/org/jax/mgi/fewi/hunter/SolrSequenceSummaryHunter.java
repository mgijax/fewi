package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSequenceSummaryHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrSequenceSummaryHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.SEQ_ID, new SolrPropertyMapper(IndexConstants.SEQ_ID));
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
        propertyMap.put(SearchConstants.SEQ_KEY, new SolrPropertyMapper(IndexConstants.SEQ_KEY));
        
        // Sort map
        
        ArrayList <String> sortList = new ArrayList <String> ();
        sortList.add(IndexConstants.SEQ_TYPE_SORT);
        sortList.add(IndexConstants.SEQ_PROVIDER_SORT);
        sortList.add(IndexConstants.SEQ_LENGTH);
        
        sortMap.put(SortConstants.SEQUENCE_SORT, new SolrSortMapper(sortList));
        // Set the url for the solr instance.
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standar list 
         * of object keys returned.
         */
        
        keyString = IndexConstants.SEQ_KEY;
        
    }
   
    @Value("${solr.sequence.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}