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
import org.jax.mgi.fewi.summary.QSVocabResult;

@Repository
public class SolrQSVocabResultBaseHunter extends SolrHunter<QSVocabResult> {

	public SolrQSVocabResultBaseHunter() {

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
		propertyMap.put(SearchConstants.QS_ACC_ID, new SolrPropertyMapper(IndexConstants.QS_ACC_ID));
		propertyMap.put(SearchConstants.QS_TERM, new SolrPropertyMapper(IndexConstants.QS_TERM));
		propertyMap.put(SearchConstants.QS_TERM_TYPE, new SolrPropertyMapper(IndexConstants.QS_TERM_TYPE));
		propertyMap.put(SearchConstants.QS_SYNONYM, new SolrPropertyMapper(IndexConstants.QS_SYNONYM));
		propertyMap.put(SearchConstants.QS_VOCAB_NAME, new SolrPropertyMapper(IndexConstants.QS_VOCAB_NAME));
		propertyMap.put(SearchConstants.QS_DETAIL_URI, new SolrPropertyMapper(IndexConstants.QS_DETAIL_URI));
		propertyMap.put(SearchConstants.QS_ANNOTATION_COUNT, new SolrPropertyMapper(IndexConstants.QS_ANNOTATION_COUNT));
		propertyMap.put(SearchConstants.QS_ANNOTATION_TEXT, new SolrPropertyMapper(IndexConstants.QS_ANNOTATION_TEXT));
		propertyMap.put(SearchConstants.QS_ANNOTATION_URI, new SolrPropertyMapper(IndexConstants.QS_ANNOTATION_URI));
		propertyMap.put(SearchConstants.QS_SEQUENCE_NUM, new SolrPropertyMapper(IndexConstants.QS_SEQUENCE_NUM));
		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.QS_PRIMARY_ID;
	}

	@Value("${solr.qsVocabBucket.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<QSVocabResult> sr, SearchParams sp) {

		logger.debug ("Entering SolrQSVocabResultBaseHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		Float zero = new Float(0.0);
		
		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID);

			try {
				QSVocabResult result = new QSVocabResult();
				result.setPrimaryID(key);
				result.setTerm((String) doc.getFieldValue(IndexConstants.QS_TERM));
				result.setTermType((String) doc.getFieldValue(IndexConstants.QS_TERM_TYPE));
				result.setVocabName((String) doc.getFieldValue(IndexConstants.QS_VOCAB_NAME));
				result.setDetailUri((String) doc.getFieldValue(IndexConstants.QS_DETAIL_URI));
				result.setAnnotationText((String) doc.getFieldValue(IndexConstants.QS_ANNOTATION_TEXT));
				result.setAnnotationUri((String) doc.getFieldValue(IndexConstants.QS_ANNOTATION_URI));
				
				try {
					result.setSynonym((List<String>) doc.getFieldValue(IndexConstants.QS_SYNONYM));
					result.setAccID((List<String>) doc.getFieldValue(IndexConstants.QS_ACC_ID));
				} catch (Throwable t) {}
				
				Float score = (Float) doc.getFieldValue(IndexConstants.SCORE);
				if (score == null) {
					result.setScore(zero);
				} else {
					result.setScore(score);
				}

				Long annotCount = (Long) doc.getFieldValue(IndexConstants.QS_ANNOTATION_COUNT);
				if (annotCount == null) {
					result.setAnnotationCount(0L);
				} else {
					result.setAnnotationCount(annotCount);
				}
				
				Integer seqNum = (Integer) doc.getFieldValue(IndexConstants.QS_SEQUENCE_NUM);
				if (seqNum == null) {
					result.setSequenceNum(0);
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
	private void packFacetData (QueryResponse rsp, SearchResults<QSVocabResult> sr) {
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
