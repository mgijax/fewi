package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

// hunter for retrieving experimental variable facet for GXD HT experiments
@Repository
public class SolrGxdHtExperimentVariableFacetHunter extends SolrGxdHtExperimentBaseHunter {
    public SolrGxdHtExperimentVariableFacetHunter() {        
        facetString = GxdHtFields.EXPERIMENTAL_VARIABLES;
    }
	
	@Value("${solr.gxdHtExperiment.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
