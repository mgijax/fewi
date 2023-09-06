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
import org.jax.mgi.fewi.summary.QSFeaturePart;

@Repository
public class SolrQSLookupHunter extends SolrHunter<QSFeaturePart> {

	public SolrQSLookupHunter() {

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
		propertyMap.put(SearchConstants.QS_SYMBOL, new SolrPropertyMapper(IndexConstants.QS_SYMBOL));
		propertyMap.put(SearchConstants.QS_NAME, new SolrPropertyMapper(IndexConstants.QS_NAME));
		propertyMap.put(SearchConstants.QS_CHROMOSOME, new SolrPropertyMapper(IndexConstants.QS_CHROMOSOME));
		propertyMap.put(SearchConstants.QS_LOCATION, new SolrPropertyMapper(IndexConstants.QS_LOCATION));
		propertyMap.put(SearchConstants.QS_STRAND, new SolrPropertyMapper(IndexConstants.QS_STRAND));

		/*
		 *  The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.QS_PRIMARY_ID;
	}

	@Value("${solr.qsLookup.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<QSFeaturePart> sr, SearchParams sp) {

		logger.debug ("Entering SolrQSLookupHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID);

			try {
				QSFeaturePart result = new QSFeaturePart();
				result.setPrimaryID((String) doc.getFieldValue(IndexConstants.QS_PRIMARY_ID));
				result.setSymbol((String) doc.getFieldValue(IndexConstants.QS_SYMBOL));
				result.setName((String) doc.getFieldValue(IndexConstants.QS_NAME));
				result.setChromosome((String) doc.getFieldValue(IndexConstants.QS_CHROMOSOME));
				result.setLocation((String) doc.getFieldValue(IndexConstants.QS_LOCATION));
				result.setStrand((String) doc.getFieldValue(IndexConstants.QS_STRAND));
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
	private void packFacetData (QueryResponse rsp, SearchResults<QSFeaturePart> sr) {
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
