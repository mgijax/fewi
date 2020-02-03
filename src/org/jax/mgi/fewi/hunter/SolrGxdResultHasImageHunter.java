package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the gxdResultHasImage solr index (like gxdResult, but only with
 * classical data that has associated images)
 * 
 */

@Repository
public class SolrGxdResultHasImageHunter extends SolrGxdSummaryBaseHunter {
	/***
	 * The constructor sets up this hunter so that it is specific to GXD
	 * summary pages. Each item in the constructor sets a value that it has
	 * inherited from its superclass, and then relies on the superclass to
	 * perform all of the needed work via the hunt() method.
	 */
	public SolrGxdResultHasImageHunter() {
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to actually get a
		 * specific field, and return it rather than a list of keys.
		 */
		keyString = GxdResultFields.RESULT_KEY;
	}

	@Value("${solr.gxdResultHasImage.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
