package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.jax.mgi.fewi.summary.QSFeatureResult;

@Repository
public class SolrQSFeatureResultBaseHunter extends SolrHunter<QSFeatureResult> {

	public SolrQSFeatureResultBaseHunter() {

		/*
		 * Set up the sort mapping.  These are fields we can sort by in Solr.
		 */
		sortMap.put(SortConstants.SCORE, new SolrSortMapper(IndexConstants.SCORE));

		/*
		 * Setup the property map.  This maps from the properties of the
		 * incoming filter list to the corresponding field names in the Solr
		 * implementation.
		 */
		propertyMap.put(SearchConstants.QS_PRIMARY_ID, new SolrPropertyMapper(IndexConstants.QS_PRIMARY_ID));
		propertyMap.put(SearchConstants.QS_IS_MARKER, new SolrPropertyMapper(IndexConstants.QS_IS_MARKER));
		propertyMap.put(SearchConstants.QS_FEATURE_TYPE, new SolrPropertyMapper(IndexConstants.QS_FEATURE_TYPE));
		propertyMap.put(SearchConstants.QS_ACC_ID, new SolrPropertyMapper(IndexConstants.QS_ACC_ID));
		propertyMap.put(SearchConstants.QS_SYMBOL, new SolrPropertyMapper(IndexConstants.QS_SYMBOL));
		propertyMap.put(SearchConstants.QS_NAME, new SolrPropertyMapper(IndexConstants.QS_NAME));
		propertyMap.put(SearchConstants.QS_SYNONYM, new SolrPropertyMapper(IndexConstants.QS_SYNONYM));
		propertyMap.put(SearchConstants.QS_DETAIL_URI, new SolrPropertyMapper(IndexConstants.QS_DETAIL_URI));
		propertyMap.put(SearchConstants.QS_SEQUENCE_NUM, new SolrPropertyMapper(IndexConstants.QS_SEQUENCE_NUM));
		propertyMap.put(SearchConstants.QS_CHROMOSOME, new SolrPropertyMapper(IndexConstants.QS_CHROMOSOME));
		propertyMap.put(SearchConstants.QS_START_COORD, new SolrPropertyMapper(IndexConstants.QS_START_COORD));
		propertyMap.put(SearchConstants.QS_END_COORD, new SolrPropertyMapper(IndexConstants.QS_END_COORD));
		propertyMap.put(SearchConstants.QS_PROTEIN_DOMAINS, new SolrPropertyMapper(IndexConstants.QS_PROTEIN_DOMAINS));
		propertyMap.put(SearchConstants.QS_STRAND, new SolrPropertyMapper(IndexConstants.QS_STRAND));
		propertyMap.put(SearchConstants.QS_SEARCH_TEXT, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TEXT));
		propertyMap.put(SearchConstants.QS_ORTHOLOG_NOMEN_ORG, new SolrPropertyMapper(IndexConstants.QS_ORTHOLOG_NOMEN_ORG));
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.QS_PRIMARY_ID;
	}

	@Value("${solr.qsFeatureBucket.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<QSFeatureResult> sr, SearchParams sp) {

		logger.debug ("Entering SolrQSFeatureResultBaseHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		Float zero = new Float(0.0);
		
		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID);

			try {
				QSFeatureResult result = new QSFeatureResult();
				result.setPrimaryID(key);
				result.setSymbol((String) doc.getFieldValue(IndexConstants.QS_SYMBOL));
				result.setName((String) doc.getFieldValue(IndexConstants.QS_NAME));
				result.setDetailUri((String) doc.getFieldValue(IndexConstants.QS_DETAIL_URI));
				result.setIsMarker((Integer) doc.getFieldValue(IndexConstants.QS_IS_MARKER));
				result.setFeatureType((String) doc.getFieldValue(IndexConstants.QS_FEATURE_TYPE));
				result.setChromosome((String) doc.getFieldValue(IndexConstants.QS_CHROMOSOME));
				if (doc.getFieldValue(IndexConstants.QS_START_COORD) != null) {
					result.setStartCoord(((Long) doc.getFieldValue(IndexConstants.QS_START_COORD)).toString());
					if (doc.getFieldValue(IndexConstants.QS_END_COORD) != null) {
						result.setEndCoord(((Long) doc.getFieldValue(IndexConstants.QS_END_COORD)).toString());
					}
				}
				result.setStrand((String) doc.getFieldValue(IndexConstants.QS_STRAND));
				
				try {
					result.setSynonym((List<String>) doc.getFieldValue(IndexConstants.QS_SYNONYM));
					result.setProteinDomains((List<String>) doc.getFieldValue(IndexConstants.QS_PROTEIN_DOMAINS));
					result.setAccID((List<String>) doc.getFieldValue(IndexConstants.QS_ACC_ID));
					result.setOrthologNomenOrg((List<String>) doc.getFieldValue(IndexConstants.QS_ORTHOLOG_NOMEN_ORG));
				} catch (Throwable t) {}
				
				Float score = (Float) doc.getFieldValue(IndexConstants.SCORE);
				if (score == null) {
					result.setScore(zero);
				} else {
					result.setScore(score);
				}

				Long seqNum = (Long) doc.getFieldValue(IndexConstants.QS_SEQUENCE_NUM);
				if (seqNum == null) {
					result.setSequenceNum(0L);
				} else {
					result.setSequenceNum(seqNum);
				}

				sr.addResultObjects(result);
			} catch (Exception e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
		this.packFacetData(rsp, sr);

		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " items");
	}

	/* gather and facet-related data from 'rsp' and package it into 'sr'
	 */
	private void packFacetData (QueryResponse rsp, SearchResults<QSFeatureResult> sr) {
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
