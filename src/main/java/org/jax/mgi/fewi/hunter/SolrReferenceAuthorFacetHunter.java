package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * SolrReferenceAuthorFacetHunter
 * @author mhall
 * This hunter gathers the autor facet information for the filter.
 * Most of its configuration items are set in the base hunter.
 */
@Repository
public class SolrReferenceAuthorFacetHunter 
	extends SolrReferenceSummaryBaseHunter<String> {
    
    /***
     * The constructor sets up this hunter to gather the author
     * facet for references.
     */
    public SolrReferenceAuthorFacetHunter() {        
        
        /**
         * This hunter only needs facet information, in order to 
         * populate the summaries filters.
         */
        
        facetString = IndexConstants.REF_AUTHOR_FACET;
        
    }
    
    @Value("${solr.reference.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }

}