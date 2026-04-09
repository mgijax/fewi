package org.jax.mgi.fewi.hunter;

import java.util.List;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdSexFacetHunter extends SolrGxdSummaryBaseHunter {

	/***
	 * The constructor sets up this hunter so that it is specific to GXD summary
	 * pages. Each item in the constructor sets a value that it has inherited
	 * from its superclass, and then relies on the superclass to perform all of
	 * the needed work via the hunt() method.
	 */
	public SolrGxdSexFacetHunter() {

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to pack it into the
		 * keys collection in the response.
		 */

		facetString = IndexConstants.GXD_SEX_FACET;

	}

	/** Override packFacetData so we can remove "Not Specified" and "Not Applicable" if present.
	 */
	protected void packFacetData(QueryResponse rsp,
		SearchResults<SolrGxdEntity> sr) {
		//
		super.packFacetData(rsp, sr);
		List<String> facets = sr.getResultFacets();
		facets.remove("Not Specified");
		facets.remove("Not Applicable");
	}

	@Value("${solr.gxdResult.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}
