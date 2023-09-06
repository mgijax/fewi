package org.jax.mgi.fewi.hunter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrInteractionHunter extends SolrInteractionBaseHunter {

	public SolrInteractionHunter() {
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.REG_KEY;
	}

	@Value("${solr.interaction.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	/** overriding addSorts() to handle the special case of sorting by the
	 * combined data source + score duo.  In this case, we always want to
	 * sort by data source first (and always ascending), and we want to 
	 * secondarily sort score descending on the first click and ascending on
	 * the second click.  Otherwise, handling sorting as in the parent class.
	 */
//	@Override
	protected void addSortsDefunct(SearchParams searchParams, SolrQuery query) {
		ORDER currentSort = null;

		for (Sort sort: searchParams.getSorts()) {

			// pick the right Solr constant for ascending or descending

			if (sort.isDesc()) {
				currentSort = SolrQuery.ORDER.desc;
			} else {
				currentSort = SolrQuery.ORDER.asc;
			}

			// first, look for the special case for sorting by the validation,
			// then the data source and score duo.

			if (SortConstants.BY_SCORE.equals(sort.getSort())) {

				// We will sort validation descending first (and ascending on
				// the second click), data source always ascending, and 
				// then score descending first (and ascending on the second
				// click).
				int fieldNum = 0;

				// fix currentSort to be the opposite, since we want the first
				// sort of validation and score to be descending
				if (sort.isDesc()) {
					currentSort = SolrQuery.ORDER.asc;
				} else {
					currentSort = SolrQuery.ORDER.desc;
				}

				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					if (fieldNum == 1) {
						query.addSort(ssm, SolrQuery.ORDER.asc);
					} else {
						query.addSort(ssm, currentSort);
					}
					fieldNum++;
				}

			} else if (sortMap.containsKey(sort.getSort())) {

				// Otherwise, if this sort is configured in the sortMap, then
				// we can have it mapped to 1->N fields in the Solr index.

				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					query.addSort(ssm, currentSort);
				}

			} else {
				// otherwise, we just pass through an unmapped field to Solr
				// and let it deal with it

				query.addSort(sort.getSort(), currentSort);
			}
		}
	}

	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<SolrInteraction> sr, SearchParams sp) {

		logger.debug ("Entering SolrInteractionHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		for (SolrDocument doc : sdl) {
			SolrInteraction item = new SolrInteraction();

			item.setRegKey((String)doc.getFieldValue(IndexConstants.REG_KEY));
			item.setOrganizerID((String)doc.getFieldValue(IndexConstants.ORGANIZER_ID));
			item.setOrganizerSymbol((String)doc.getFieldValue(IndexConstants.ORGANIZER_SYMBOL));
			item.setParticipantID((String)doc.getFieldValue(IndexConstants.PARTICIPANT_ID));
			item.setParticipantSymbol((String)doc.getFieldValue(IndexConstants.PARTICIPANT_SYMBOL));
			item.setRelationshipTerm((String)doc.getFieldValue(IndexConstants.RELATIONSHIP_TERM));
			item.setEvidenceCode((String)doc.getFieldValue(IndexConstants.EVIDENCE_CODE));
			item.setScore((String)doc.getFieldValue(IndexConstants.SCORE_VALUE));
			item.setScoreSource((String)doc.getFieldValue(IndexConstants.SCORE_SOURCE));
			item.setValidation((String)doc.getFieldValue(IndexConstants.VALIDATION));
			item.setNotes((String)doc.getFieldValue(IndexConstants.NOTES));
			item.setJnumID((String)doc.getFieldValue(IndexConstants.JNUM_ID));
			item.setMatureTranscript((String)doc.getFieldValue(IndexConstants.MATURE_TRANSCRIPT));
			
			item.setAlgorithm((String) doc.getFieldValue(IndexConstants.ALGORITHM));
			item.setOrganizerProductID((String) doc.getFieldValue(IndexConstants.ORGANIZER_PRODUCT_ID));
			item.setParticipantProductID((String) doc.getFieldValue(IndexConstants.PARTICIPANT_PRODUCT_ID));
			item.setOtherReferences((String) doc.getFieldValue(IndexConstants.OTHER_REFERENCES));

			sr.addResultObjects(item);
		}
		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " items");
	}

}
