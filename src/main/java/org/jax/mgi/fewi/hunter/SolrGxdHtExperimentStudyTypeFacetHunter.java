package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

// hunter for retrieving study type facet for GXD HT experiments
@Repository
public class SolrGxdHtExperimentStudyTypeFacetHunter extends SolrGxdHtExperimentBaseHunter {
    public SolrGxdHtExperimentStudyTypeFacetHunter() {        
        facetString = GxdHtFields.STUDY_TYPE;
    }
	
	@Value("${solr.gxdHtExperiment.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
