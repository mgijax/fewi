package org.jax.mgi.fewi.hunter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrStrainHunter extends SolrStrainBaseHunter {
	
    public SolrStrainHunter() {        
        super();
    }
	
    @Value("${solr.strain.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}