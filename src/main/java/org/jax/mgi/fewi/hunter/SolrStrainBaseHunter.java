package org.jax.mgi.fewi.hunter;

import java.io.IOException;
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
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SolrStrainBaseHunter extends SolrHunter<SimpleStrain> {

	private ObjectMapper mapper = new ObjectMapper();

	public SolrStrainBaseHunter() {

		/*
		 * Set up the sort mapping.  These are fields we can sort by in Solr.
		 */
		sortMap.put(SortConstants.BY_DEFAULT, new SolrSortMapper(IndexConstants.BY_DEFAULT));

		/*
		 * Setup the property map.  This maps from the properties of the
		 * incoming filter list to the corresponding field names in the Solr
		 * implementation.
		 */
		propertyMap.put(SearchConstants.ACC_ID, new SolrPropertyMapper(IndexConstants.ACC_ID));
		propertyMap.put(SearchConstants.STRAIN_KEY, new SolrPropertyMapper(IndexConstants.STRAIN_KEY));
		propertyMap.put(SearchConstants.STRAIN_TYPE, new SolrPropertyMapper(IndexConstants.STRAIN_TYPE));
		propertyMap.put(SearchConstants.STRAIN_NAME, new SolrPropertyMapper(IndexConstants.STRAIN_NAME));
		propertyMap.put(SearchConstants.STRAIN_NAME_LOWER, new SolrPropertyMapper(IndexConstants.STRAIN_NAME_LOWER));
		propertyMap.put(SearchConstants.STRAIN_TAGS, new SolrPropertyMapper(IndexConstants.STRAIN_TAGS));

		propertyMap.put(SearchConstants.VALIDATION, new SolrPropertyMapper(IndexConstants.VALIDATION));
		propertyMap.put(SearchConstants.DATA_SOURCE, new SolrPropertyMapper(IndexConstants.SCORE_SOURCE));
		propertyMap.put(SearchConstants.SCORE_NUMERIC, new SolrPropertyMapper(IndexConstants.SCORE_NUMERIC));

		/*
		 * The name of the field we want to iterate through the documents for
		 * and place into the output.  In this case we want the standard list of
		 * object keys returned.
		 */
		keyString = IndexConstants.STRAIN_KEY;
	}

	@Value("${solr.strain.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation (QueryResponse rsp, SearchResults<SimpleStrain> sr, SearchParams sp) {

		logger.debug ("Entering SolrStrainBaseHunter.packInformation()");

		SolrDocumentList sdl = rsp.getResults();

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.STRAIN_KEY);

			try {
				SimpleStrain strain = (SimpleStrain) mapper.readValue((String)doc.getFieldValue(IndexConstants.STRAIN), SimpleStrain.class);
				sr.addResultObjects(strain);
			} catch (IOException e) {
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
	private void packFacetData (QueryResponse rsp, SearchResults<SimpleStrain> sr) {
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
