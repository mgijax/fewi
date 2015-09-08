package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * SolrReferenceTypeFacetHunter
 * This hunter gathers the type (literature vs non-literature) facet information for the filter.
 * Most of its configuration items are set in the base hunter.
 */
@Repository
public class SolrReferenceTypeFacetHunter 
	extends SolrReferenceSummaryBaseHunter<String> {
    
    /***
     * The constructor sets up this hunter to gather the type
     * facet for references.
     */
    public SolrReferenceTypeFacetHunter() {        
        
        /**
         * This hunter only needs facet information, in order to 
         * populate the summaries filters.
         */
        
        facetString = IndexConstants.REF_GROUPING;
        
    }
    
    @Value("${solr.reference.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }

}
