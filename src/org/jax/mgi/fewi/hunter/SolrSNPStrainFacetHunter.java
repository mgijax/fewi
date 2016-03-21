package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/* This class gets the strains (as facets) which have allele calls for the
 * consensus SNPs that match the query.
 */
@Repository
public class SolrSNPStrainFacetHunter extends SolrSNPBaseHunter {

	public SolrSNPStrainFacetHunter() {
		facetString = IndexConstants.SNP_STRAINS;
	}

	@Value("${solr.searchsnp.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
