package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.searchUtil.entities.group.SolrHdpEntity;
import org.springframework.beans.factory.annotation.Value;

public class SolrDiseasePortalDiseaseHunter extends SolrHunter<SolrHdpEntity> {


    public SolrDiseasePortalDiseaseHunter() {

    	// TODO property fields that need to be mapped


    }

	@Value("${solr.dp.disease.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
	
}
