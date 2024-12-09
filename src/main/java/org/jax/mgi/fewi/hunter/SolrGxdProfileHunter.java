package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdProfileHunter extends SolrHunter<String> {

	/***
	 * The constructor sets up this hunter so that it is specific to finding
	 * marker keys given a profile structure/stage query.
	 */
	public SolrGxdProfileHunter() {

		/*
		 * For GXD solr queries, don't bother with relevance scores.
		 */
		relevanceScoreMatters = false;

		/*
		 * Setup the property map. This maps from the properties of the incoming
		 * filter list to the corresponding field names in the Solr
		 * implementation.
		 */

		propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(
				GxdResultFields.MARKER_KEY));

		propertyMap.put(SearchConstants.PROF_POS_C_EXACT, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_C_EXACT));
		propertyMap.put(SearchConstants.PROF_POS_C_ANC, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_C_ANC));
		propertyMap.put(SearchConstants.PROF_POS_R_EXACT, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_R_EXACT));
		propertyMap.put(SearchConstants.PROF_POS_R_ANC, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_R_ANC));

		propertyMap.put(SearchConstants.PROF_POS_C_EXACT_A, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_C_EXACT_A));
		propertyMap.put(SearchConstants.PROF_POS_C_ANC_A, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_C_ANC_A));
		propertyMap.put(SearchConstants.PROF_POS_R_EXACT_A, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_R_EXACT_A));
		propertyMap.put(SearchConstants.PROF_POS_R_ANC_A, new SolrPropertyMapper(
				GxdResultFields.PROF_POS_R_ANC_A));

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want the standard list of
		 * object keys returned.
		 */

		keyString = GxdResultFields.MARKER_KEY;

		returnedFields.add(GxdResultFields.MARKER_KEY);
		returnedFields.add(GxdResultFields.MARKER_MGIID);
	}

	@Value("${solr.gxdProfileMarker.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
