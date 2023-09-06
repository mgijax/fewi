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
import org.jax.mgi.shr.jsonmodel.SimpleSequence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SolrSequenceBaseHunter extends SolrHunter<SimpleSequence> {

	private ObjectMapper mapper = new ObjectMapper();

    public SolrSequenceBaseHunter() {

        /*
         * Set up the sort mapping.  These are fields we can sort by in Solr.
         */
        sortMap.put(SortConstants.BY_DEFAULT, new SolrSortMapper(IndexConstants.BY_DEFAULT));
        
        /*
         * Setup the property map.  This maps from the properties of the incoming filter list to the
         * corresponding field names in the Solr implementation.  
         */
        propertyMap.put(SearchConstants.SEQ_KEY, new SolrPropertyMapper(IndexConstants.SEQ_KEY));
        propertyMap.put(SearchConstants.SEQ_PROVIDER, new SolrPropertyMapper(IndexConstants.SEQ_PROVIDER));
        propertyMap.put(SearchConstants.SEQ_TYPE, new SolrPropertyMapper(IndexConstants.SEQ_TYPE));
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
        propertyMap.put(SearchConstants.SEQ_STRAIN, new SolrPropertyMapper(IndexConstants.SEQ_STRAIN));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
         keyString = IndexConstants.SEQ_KEY;
    }

    @Value("${solr.sequence.url}")
    public void setSolrUrl(String solrUrl) {
    	super.solrUrl = solrUrl;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void packInformation (QueryResponse rsp, SearchResults<SimpleSequence> sr, SearchParams sp) {
    	logger.debug ("Entering SolrSequenceHunter.packInformation()");

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing Sequence data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.SEQ_KEY);

			try {
				SimpleSequence seq = (SimpleSequence) mapper.readValue((String)doc.getFieldValue(IndexConstants.SEQ_SEQUENCE), SimpleSequence.class);
				sr.addResultObjects(seq);
			} catch (IOException e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
		this.packFacetData(rsp, sr);
		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " terms");
    }

	/* gather and facet-related data from 'rsp' and package it into 'sr'
	 */
	private void packFacetData (QueryResponse rsp, SearchResults<SimpleSequence> sr) {
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
