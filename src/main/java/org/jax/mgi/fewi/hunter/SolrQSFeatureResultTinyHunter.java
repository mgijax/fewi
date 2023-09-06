package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrQSFeatureResultTinyHunter extends SolrHunter<QSFeatureResult> {

	public SolrQSFeatureResultTinyHunter() {

		/*
		 * Set up the sort mapping.  These are fields we can sort by in Solr.
		 */
		sortMap.put(SortConstants.SCORE, new SolrSortMapper(IndexConstants.SCORE));

		/*
		 * Setup the property map.  This maps from the properties of the
		 * incoming filter list to the corresponding field names in the Solr
		 * implementation.
		 */
		propertyMap.put(SearchConstants.UNIQUE_KEY, new SolrPropertyMapper(IndexConstants.UNIQUE_KEY));
		propertyMap.put(SearchConstants.QS_SEARCH_TERM_EXACT, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TERM_EXACT));
		propertyMap.put(SearchConstants.QS_SEARCH_TERM_INEXACT, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TERM_INEXACT));
		propertyMap.put(SearchConstants.QS_SEARCH_TERM_STEMMED, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TERM_STEMMED));
		propertyMap.put(SearchConstants.QS_SEARCH_TERM_DISPLAY, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TERM_DISPLAY));
		propertyMap.put(SearchConstants.QS_SEARCH_TERM_TYPE, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TERM_TYPE));
		propertyMap.put(SearchConstants.QS_SEARCH_TERM_WEIGHT, new SolrPropertyMapper(IndexConstants.QS_SEARCH_TERM_WEIGHT));

		propertyMap.put(SearchConstants.QS_FEATURE_TYPE, new SolrPropertyMapper(IndexConstants.QS_FEATURE_TYPE));
		propertyMap.put(SearchConstants.QS_PRIMARY_ID, new SolrPropertyMapper(IndexConstants.QS_PRIMARY_ID));
		propertyMap.put(SearchConstants.QS_SEQUENCE_NUM, new SolrPropertyMapper(IndexConstants.QS_SEQUENCE_NUM));

		/*
		 *  The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.UNIQUE_KEY;
	}

	@Value("${solr.qsFeatureBucket.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<QSFeatureResult> sr, SearchParams sp) {

		logger.debug ("Entering SolrQSFeatureResultBaseHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.UNIQUE_KEY);

			try {
				QSFeatureResult result = new QSFeatureResult();
				result.setUniqueKey((String) doc.getFieldValue(IndexConstants.UNIQUE_KEY));
				result.setSearchTermExact((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_EXACT));
				result.setSearchTermInexact((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_INEXACT));
				result.setSearchTermStemmed((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_STEMMED));
				result.setSearchTermDisplay((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_DISPLAY));
				result.setSearchTermType((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_TYPE));
				result.setDetailUri("/marker/" + (String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID));
				result.setFeatureType((String) doc.getFieldValue(IndexConstants.QS_FEATURE_TYPE));
				result.setPrimaryID((String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID));

				Integer weight = (Integer) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_WEIGHT);
				if (weight == null) {
					result.setSearchTermWeight(0);
				} else {
					result.setSearchTermWeight(weight);
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

		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " items");
	}
}
