package org.jax.mgi.fewi.hunter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrQSVocabResultHunter extends SolrQSVocabResultBaseHunter {
	
    public SolrQSVocabResultHunter() {        
        super();
    }
	
    @Value("${solr.qsVocabBucket.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}