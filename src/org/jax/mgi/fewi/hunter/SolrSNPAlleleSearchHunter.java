package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSNPAlleleSearchHunter extends SolrSNPBaseHunter {

	public SolrSNPAlleleSearchHunter() {
		//groupFields.put(SearchConstants.SNPID, IndexConstants.SNP_CONSENSUSSNPID);
		facetString = IndexConstants.SNP_STRAINS;
	}

	@Value("${solr.allelesnp.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}