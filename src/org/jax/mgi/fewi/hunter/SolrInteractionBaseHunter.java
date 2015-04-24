package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrInteraction;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrInteractionBaseHunter extends SolrHunter<SolrInteraction> {

	public SolrInteractionBaseHunter() {

		/*
		 * Set up the sort mapping.  These are fields we can sort by in Solr.
		 */

		ArrayList<String> validationSort = new ArrayList<String>();
		validationSort.add(IndexConstants.VALIDATION_SORTABLE);
		validationSort.add(IndexConstants.SCORE_SORTABLE);

		sortMap.put(SortConstants.BY_FEATURE_1, new SolrSortMapper(IndexConstants.BY_ORGANIZER_SYMBOL));
		sortMap.put(SortConstants.BY_FEATURE_2, new SolrSortMapper(IndexConstants.BY_PARTICIPANT_SYMBOL));
		sortMap.put(SortConstants.BY_INTERACTION, new SolrSortMapper(IndexConstants.RELATIONSHIP_TERM));
		sortMap.put(SortConstants.BY_VALIDATION, new SolrSortMapper(validationSort));
		sortMap.put(SortConstants.BY_DATA_SOURCE, new SolrSortMapper(IndexConstants.SCORE_SOURCE_SORTABLE));
		sortMap.put(SortConstants.BY_SCORE, new SolrSortMapper(IndexConstants.SCORE_SORTABLE));
		sortMap.put(SortConstants.BY_REFERENCE, new SolrSortMapper(IndexConstants.BY_JNUM_ID));

		/*
		 * Setup the property map.  This maps from the properties of the
		 * incoming filter list to the corresponding field names in the Solr
		 * implementation.  As we only allow searching by one field, that
		 * is the only one we define here currently.
		 */
		propertyMap.put(SearchConstants.MRK_ID, new SolrPropertyMapper(IndexConstants.MRK_ID));
		propertyMap.put(SearchConstants.RELATIONSHIP_TERM, new SolrPropertyMapper(IndexConstants.RELATIONSHIP_TERM));
		propertyMap.put(SearchConstants.VALIDATION, new SolrPropertyMapper(IndexConstants.VALIDATION));
		propertyMap.put(SearchConstants.DATA_SOURCE, new SolrPropertyMapper(IndexConstants.SCORE_SOURCE));
		propertyMap.put(SearchConstants.SCORE_NUMERIC, new SolrPropertyMapper(IndexConstants.SCORE_NUMERIC));

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
	 * combined validation + score duo.  In this case, we always want to
	 * sort by data source first (and always ascending), and we want to 
	 * secondarily sort score descending on the first click and ascending on
	 * the second click.  Otherwise, handling sorting as in the parent class.
	 */
	@Override
	protected void addSorts(SearchParams searchParams, SolrQuery query) {
		ORDER currentSort = null;

		logger.debug("********** in addSorts()");
		for (Sort sort: searchParams.getSorts()) {

			// pick the right Solr constant for ascending or
			// descending (reverse the traditional ordering for
			// the score column)

			if (SortConstants.BY_SCORE.equals(sort.getSort())) {
			    if (sort.isDesc()) {
				currentSort = SolrQuery.ORDER.asc;
			    } else {
				currentSort = SolrQuery.ORDER.desc;
			    }
			} else if (sort.isDesc()) {
				currentSort = SolrQuery.ORDER.desc;
			} else {
				currentSort = SolrQuery.ORDER.asc;
			}

			// first, look for the special case for sorting by the
			// validation then the score.

			if (SortConstants.BY_VALIDATION.equals(sort.getSort())) {
				logger.debug("In branch 1");

				// We will sort validation ascending first and
				// score descending first

				int fieldNum = 0;

				// fix currentSort to be the opposite, since we want the first
				// sort of validation and score to be descending
				
				ORDER validationSort = SolrQuery.ORDER.asc;
				ORDER scoreSort = SolrQuery.ORDER.desc;

				if (sort.isDesc()) {
					validationSort = SolrQuery.ORDER.desc;
					scoreSort = SolrQuery.ORDER.asc;
				}

				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					if (fieldNum == 0) {
						query.addSort(ssm, validationSort);
					} else {
						query.addSort(ssm, scoreSort);
					}
					fieldNum++;
				}

			} else if (sortMap.containsKey(sort.getSort())) {
				logger.debug("In branch 2");

				// Otherwise, if this sort is configured in the sortMap, then
				// we can have it mapped to 1->N fields in the Solr index.

				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					query.addSort(ssm, currentSort);
				}

			} else {
				logger.debug("In branch 3 : " + sort.getSort());
				// otherwise, we just pass through an unmapped field to Solr
				// and let it deal with it

				query.addSort(sort.getSort(), currentSort);
			}
		}
	}

	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<SolrInteraction> sr, SearchParams sp) {

		logger.debug ("Entering SolrInteractionBaseHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		for (SolrDocument doc : sdl)
		{
			SolrInteraction item = new SolrInteraction();

			item.setRegKey ((String) doc.getFieldValue(IndexConstants.REG_KEY));
			item.setOrganizerID((String) doc.getFieldValue(IndexConstants.ORGANIZER_ID));
			item.setOrganizerSymbol ((String) doc.getFieldValue(IndexConstants.ORGANIZER_SYMBOL));
			item.setParticipantID((String) doc.getFieldValue(IndexConstants.PARTICIPANT_ID));
			item.setParticipantSymbol ((String) doc.getFieldValue(IndexConstants.PARTICIPANT_SYMBOL));
			item.setRelationshipTerm ((String) doc.getFieldValue(IndexConstants.RELATIONSHIP_TERM));
			item.setEvidenceCode ((String) doc.getFieldValue(IndexConstants.EVIDENCE_CODE));
			
			item.setScore ((String) doc.getFieldValue(IndexConstants.SCORE_VALUE));
			item.setScoreSource ((String) doc.getFieldValue(IndexConstants.SCORE_SOURCE));
			item.setValidation ((String) doc.getFieldValue(IndexConstants.VALIDATION));
			item.setNotes ((String) doc.getFieldValue(IndexConstants.NOTES));
			item.setJnumID ((String) doc.getFieldValue(IndexConstants.JNUM_ID));
			item.setMatureTranscript ((String) doc.getFieldValue(IndexConstants.MATURE_TRANSCRIPT));
			
			item.setAlgorithm((String) doc.getFieldValue(IndexConstants.ALGORITHM));
			item.setOrganizerProductID((String) doc.getFieldValue(IndexConstants.ORGANIZER_PRODUCT_ID));
			item.setParticipantProductID((String) doc.getFieldValue(IndexConstants.PARTICIPANT_PRODUCT_ID));
			item.setOtherReferences((String) doc.getFieldValue(IndexConstants.OTHER_REFERENCES));
			
			sr.addResultObjects(item);
		}

		this.packFacetData(rsp, sr);

		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " items");
	}

	/* gather and facet-related data from 'rsp' and package it into 'sr'
	 */
	private void packFacetData (QueryResponse rsp, SearchResults<SolrInteraction> sr) {
		if (this.facetString == null) { return; }

		logger.debug("this.facetString = " + this.facetString);
		List<String> facet = new ArrayList<String>();

		for (Count c : rsp.getFacetField(this.facetString).getValues()) {
			facet.add(c.getName());
			logger.debug("  --> " + c.getName());
		}

		if (facet.size() > 0) {
			sr.setResultFacets(facet);
		}
	}

}
