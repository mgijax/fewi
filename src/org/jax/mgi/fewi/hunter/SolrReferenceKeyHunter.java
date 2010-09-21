package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.stereotype.Repository;

@Repository
public class SolrReferenceKeyHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific finding reference keys
     * having been given a reference id.
     */
    public SolrReferenceKeyHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.REF_ID, new SolrPropertyMapper(IndexConstants.REF_ID));
                
        // Set the url for the solr instance.
        
        solrUrl = "http://cardolan.informatics.jax.org:8983/solr/reference/";
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  If there is something special about this
         * field we will want to override the packKeys() method from the superclass
         * and implement the special logic in there.
         */
        
        keyString = IndexConstants.REF_KEY;
        
    }
   
}