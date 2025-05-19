package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

// hunter for retrieving method facet for GXD HT experiments
@Repository
public class SolrGxdHtExperimentMethodFacetHunter extends SolrGxdHtExperimentBaseHunter {
    public SolrGxdHtExperimentMethodFacetHunter() {        
        facetString = GxdHtFields.METHODS;
    }
	
	@Value("${solr.gxdHtExperiment.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
