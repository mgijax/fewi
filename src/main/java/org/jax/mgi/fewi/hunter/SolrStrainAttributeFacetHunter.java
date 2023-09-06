package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrStrainAttributeFacetHunter extends SolrStrainBaseHunter {
	
    public SolrStrainAttributeFacetHunter() {        
        facetString = IndexConstants.STRAIN_ATTRIBUTE;
    }
	
    @Value("${solr.strain.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}