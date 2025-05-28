package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtSample;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

// hunter for retrieving method facet for GXD HT samples
@Repository
public class SolrGxdHtSampleCellTypeFacetHunter extends SolrHunter<GxdHtSample> {
    public SolrGxdHtSampleCellTypeFacetHunter() {        
        facetString = GxdHtFields.CT_FACET_TERMS;
    }
	
	@Value("${solr.gxdHtSample.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
