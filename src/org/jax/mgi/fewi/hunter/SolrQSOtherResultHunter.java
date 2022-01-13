package org.jax.mgi.fewi.hunter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrQSOtherResultHunter extends SolrQSOtherResultBaseHunter {
	
    public SolrQSOtherResultHunter() {        
        super();
    }
	
    @Value("${solr.qsOtherBucket.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}