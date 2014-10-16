package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdDifferentialHunter extends SolrHunter<String> {

	/***
	 * The constructor sets up this hunter so that it is specific to finding
	 * marker keys given a differential structure/stage query.
	 */
	public SolrGxdDifferentialHunter() {

		/*
		 * Setup the property map. This maps from the properties of the incoming
		 * filter list to the corresponding field names in the Solr
		 * implementation.
		 */

		propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(
				GxdResultFields.MARKER_KEY));

		// search positive structures and their children (I.e. children whose
		// "ancestor" is the search term)
		propertyMap.put(SearchConstants.POS_STRUCTURE, new SolrPropertyMapper(
				GxdResultFields.DIFF_POS_ANCESTORS));

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want the standard list of
		 * object keys returned.
		 */

		keyString = GxdResultFields.MARKER_KEY;

	}

	@Value("${solr.gxdDifferentialMarker.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}