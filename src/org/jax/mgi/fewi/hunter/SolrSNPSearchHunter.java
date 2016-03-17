package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSNPSearchHunter extends SolrSNPBaseHunter {

	public SolrSNPSearchHunter() {
		//groupFields.put(SearchConstants.SNPID, IndexConstants.SNP_CONSENSUSSNPID);
		facetString = IndexConstants.SNP_STRAINS;
	}

	@Value("${solr.searchsnp.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}