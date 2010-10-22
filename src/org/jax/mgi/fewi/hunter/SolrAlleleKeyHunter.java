package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrAlleleKeyHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a allele key given any possible allele id.
     */
    public SolrAlleleKeyHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.ALL_ID, new SolrPropertyMapper(IndexConstants.ALL_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        
        keyString = IndexConstants.ALL_KEY;
        
    }
	
	@Value("${solr.allele.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}