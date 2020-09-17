package org.jax.mgi.fewi.hunter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrQSFeatureResultHunter extends SolrQSFeatureResultBaseHunter {
	
    public SolrQSFeatureResultHunter() {        
        super();
    }
	
    @Value("${solr.qsFeatureBucket.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}