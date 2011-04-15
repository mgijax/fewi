package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.propertyMapper.SolrReferenceTextSearchPropertyMapper;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrReferenceAuthorFacetHunter 
	extends SolrReferenceSummaryBaseHunter {
    
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