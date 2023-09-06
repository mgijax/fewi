package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdTheilerStageFacetHunter extends SolrGxdSummaryBaseHunter {

	/***
	 * The constructor sets up this hunter so that it is specific to GXD summary
	 * pages. Each item in the constructor sets a value that it has inherited
	 * from its superclass, and then relies on the superclass to perform all of
	 * the needed work via the hunt() method.
	 */
	public SolrGxdTheilerStageFacetHunter() {

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to pack it into the
		 * keys collection in the response.
		 */

		facetString = IndexConstants.GXD_THEILER_STAGE_FACET;

	}

	@Value("${solr.gxdResult.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}
