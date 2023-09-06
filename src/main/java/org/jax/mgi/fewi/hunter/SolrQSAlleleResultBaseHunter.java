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
import org.jax.mgi.fewi.summary.QSAlleleResult;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrQSAlleleResultBaseHunter extends SolrHunter<QSAlleleResult> {

	public SolrQSAlleleResultBaseHunter() {

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

		propertyMap.put(SearchConstants.QS_CHROMOSOME, new SolrPropertyMapper(IndexConstants.QS_CHROMOSOME));
		propertyMap.put(SearchConstants.QS_START_COORD, new SolrPropertyMapper(IndexConstants.QS_START_COORD));
		propertyMap.put(SearchConstants.QS_END_COORD, new SolrPropertyMapper(IndexConstants.QS_END_COORD));
		propertyMap.put(SearchConstants.QS_STRAND, new SolrPropertyMapper(IndexConstants.QS_STRAND));

		propertyMap.put(SearchConstants.QS_GO_PROCESS_FACETS, new SolrPropertyMapper(IndexConstants.QS_GO_PROCESS_FACETS));
		propertyMap.put(SearchConstants.QS_GO_FUNCTION_FACETS, new SolrPropertyMapper(IndexConstants.QS_GO_FUNCTION_FACETS));
		propertyMap.put(SearchConstants.QS_GO_COMPONENT_FACETS, new SolrPropertyMapper(IndexConstants.QS_GO_COMPONENT_FACETS));
		propertyMap.put(SearchConstants.QS_DISEASE_FACETS, new SolrPropertyMapper(IndexConstants.QS_DISEASE_FACETS));
		propertyMap.put(SearchConstants.QS_PHENOTYPE_FACETS, new SolrPropertyMapper(IndexConstants.QS_PHENOTYPE_FACETS));
		propertyMap.put(SearchConstants.QS_MARKER_TYPE_FACETS, new SolrPropertyMapper(IndexConstants.QS_MARKER_TYPE_FACETS));

		/*
		 *  The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.UNIQUE_KEY;
	}

	@Value("${solr.qsAlleleBucket.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<QSAlleleResult> sr, SearchParams sp) {

		logger.debug ("Entering SolrQSAlleleResultBaseHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.UNIQUE_KEY);

			try {
				QSAlleleResult result = new QSAlleleResult();
				result.setUniqueKey((String) doc.getFieldValue(IndexConstants.UNIQUE_KEY));
				result.setSearchTermExact((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_EXACT));
				result.setSearchTermInexact((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_INEXACT));
				result.setSearchTermStemmed((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_STEMMED));
				result.setSearchTermDisplay((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_DISPLAY));
				result.setSearchTermType((String) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_TYPE));

				result.setFeatureType((String) doc.getFieldValue(IndexConstants.QS_FEATURE_TYPE));
				result.setPrimaryID((String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID));
				
				result.setGoProcessFacets((List<String>) doc.getFieldValue(IndexConstants.QS_GO_PROCESS_FACETS));
				result.setGoFunctionFacets((List<String>) doc.getFieldValue(IndexConstants.QS_GO_FUNCTION_FACETS));
				result.setGoComponentFacets((List<String>) doc.getFieldValue(IndexConstants.QS_GO_COMPONENT_FACETS));
				result.setDiseaseFacets((List<String>) doc.getFieldValue(IndexConstants.QS_DISEASE_FACETS));
				result.setPhenotypeFacets((List<String>) doc.getFieldValue(IndexConstants.QS_PHENOTYPE_FACETS));
				result.setMarkerTypeFacets((List<String>) doc.getFieldValue(IndexConstants.QS_MARKER_TYPE_FACETS));

				Integer weight = (Integer) doc.getFieldValue(IndexConstants.QS_SEARCH_TERM_WEIGHT);
				if (weight == null) {
					result.setSearchTermWeight(0);
				} else {
					result.setSearchTermWeight(weight);
				}

				sr.addResultObjects(result);
			} catch (Exception e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
		this.packFacetData(rsp, sr);

		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " alleles");
	}

	/* gather and facet-related data from 'rsp' and package it into 'sr'
	 */
	private void packFacetData (QueryResponse rsp, SearchResults<QSAlleleResult> sr) {
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
