package org.jax.mgi.fewi.hunter;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the GXD EMAPA term autocomplete. It queries all structures and
 * their synonyms, and returns the matching field, plus the base structure name
 * that it maps to.
 */

@Repository
public class SolrGxdEmapaACHunter extends SolrHunter<EmapaACResult> {
	/***
	 * The constructor sets up this hunter so that it is specific to our need.
	 * Each item in the constructor sets a value that it has inherited from its
	 * superclass, and then relies on the superclass to perform all of the
	 * needed work via the hunt() method.
	 */
	public SolrGxdEmapaACHunter() {
		/*
		 * Setup the property map. This maps from the properties of the incoming
		 * filter list to the corresponding field names in the Solr
		 * implementation.
		 */

		propertyMap.put(SearchConstants.STRUCTURE, new SolrPropertyMapper(
				IndexConstants.STRUCTUREAC_QUERYTEXT));
		
		propertyMap.put(SearchConstants.ACC_ID, new SolrPropertyMapper(
				IndexConstants.ACC_ID));

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output. In this case we want to actually get a
		 * specific field, and return it rather than a list of keys.
		 */
		keyString = IndexConstants.STRUCTUREAC_STRUCTURE;
	}

	/**
	 * packInformation
	 * 
	 * @return List of keys This overrides the typical behavior of
	 *         packInformation method. For this autocomplete hunter, we don't
	 *         need highlighting or metadata, but only need a list of
	 *         EmapaACResult objects.
	 */
	@Override
	protected void packInformation(QueryResponse rsp,
			SearchResults<EmapaACResult> sr, SearchParams sp) {

		// A list of all the primary keys in the document
		SolrDocumentList sdl = rsp.getResults();

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */
		for (SolrDocument doc : sdl) {
			// Set the result object
			String structure = (String) doc
					.getFieldValue(IndexConstants.STRUCTUREAC_STRUCTURE);
			String synonym = (String) doc
					.getFieldValue(IndexConstants.STRUCTUREAC_SYNONYM);
			String queryText = (String) doc
					.getFieldValue(IndexConstants.STRUCTUREAC_QUERYTEXT);
			boolean isStrictSynonym = (Boolean) doc
					.getFieldValue(IndexConstants.STRUCTUREAC_IS_STRICT_SYNONYM);
			boolean hasCre = (Boolean) doc
					.getFieldValue(IndexConstants.STRUCTUREAC_HAS_CRE);
			String startStage = (String) doc
					.getFieldValue(IndexConstants.GXD_START_STAGE);
			String endStage = (String) doc
					.getFieldValue(IndexConstants.GXD_END_STAGE);
			String accID = (String) doc.getFieldValue(IndexConstants.ACC_ID);

			EmapaACResult resultObject = new EmapaACResult(structure, synonym,
					isStrictSynonym, hasCre);
			resultObject.setStartStage(startStage);
			resultObject.setEndStage(endStage);
			resultObject.setAccID(accID);
			resultObject.setQueryText(queryText);

			// Add result to SearchResults
			sr.addResultObjects(resultObject);
		}
	}

	@Value("${solr.gxdEmapa_ac.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
