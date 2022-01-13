package org.jax.mgi.fewi.hunter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrQSStrainResultHunter extends SolrQSStrainResultBaseHunter {
	
    public SolrQSStrainResultHunter() {        
        super();
    }
	
    @Value("${solr.qsStrainBucket.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}