package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/* This class gets the function classes (as facets) for the consensus SNPs
 * that match the query.
 */
@Repository
public class SolrSNPFunctionClassFacetHunter extends SolrSNPBaseHunter {

	public SolrSNPFunctionClassFacetHunter() {
		facetString = IndexConstants.SNP_FUNCTIONCLASS;
	}

	@Value("${solr.searchsnp.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
