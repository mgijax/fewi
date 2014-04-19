package org.jax.mgi.fewi.hunter;

import mgi.frontend.datamodel.AlleleSystem;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrCreAlleleSystemKeyHunter extends SolrHunter<AlleleSystem> {
    
    /***
     * This hunter maps allele id, system key pairings to alleleSystemKeys
     */
    public SolrCreAlleleSystemKeyHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.ALL_ID, new SolrPropertyMapper(IndexConstants.ALL_ID));
        propertyMap.put(SearchConstants.CRE_SYSTEM_KEY, new SolrPropertyMapper(IndexConstants.CRE_SYSTEM_KEY));
        
        // Set the url for the solr instance.
        
        solrUrl = ContextLoader.getConfigBean().getProperty("solr.creAlleleSystem.url");
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we are looking for the 
         * standard list of keys to be returned.
         */
        
        keyString = IndexConstants.CRE_ALL_SYSTEM_KEY;
        
    }
    
	@Value("${solr.creAlleleSystem.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
   
}
