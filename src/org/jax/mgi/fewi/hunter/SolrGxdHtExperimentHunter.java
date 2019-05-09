package org.jax.mgi.fewi.hunter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

// hunter for retrieving GXD HT experiments
@Repository
public class SolrGxdHtExperimentHunter extends SolrGxdHtExperimentBaseHunter {
	@Value("${solr.gxdHtExperiment.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
