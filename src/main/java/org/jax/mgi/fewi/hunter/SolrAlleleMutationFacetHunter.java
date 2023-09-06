package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrAlleleMutationFacetHunter extends SolrReferenceSummaryBaseHunter<String> {
	
    public SolrAlleleMutationFacetHunter() {        
        facetString = IndexConstants.ALL_MUTATION;
    }
	
    @Value("${solr.allele.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }
    
}