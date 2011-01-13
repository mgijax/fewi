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
public class SolrReferenceAuthorFacetHunter extends SolrReferenceSummaryBaseHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to reference
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrReferenceAuthorFacetHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * This property mapping is only what is specific to the Author Facet Hunter
         */
        
        facetString = IndexConstants.REF_AUTHOR_FACET;
        
    }
    
    @Value("${solr.reference.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }

}