package org.jax.mgi.fewi.hunter;

import java.io.IOException;
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
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.jsonmodel.AntibodyJ;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SolrAntibodyHunter extends SolrHunter<AntibodyJ> {

	private ObjectMapper mapper = new ObjectMapper();

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * data for antibodies
     */
    public SolrAntibodyHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.ANTIBODY_KEY, new SolrPropertyMapper(IndexConstants.ANTIBODY_KEY));
        propertyMap.put(SearchConstants.ANTIBODY_MARKER_ID, new SolrPropertyMapper(IndexConstants.ANTIBODY_MARKER_ID));
        propertyMap.put(SearchConstants.ANTIBODY_JSON, new SolrPropertyMapper(IndexConstants.ANTIBODY_JSON));
        propertyMap.put(SearchConstants.ANTIBODY_REFERENCE_ID, new SolrPropertyMapper(IndexConstants.ANTIBODY_REFERENCE_ID));

        /* set up the sorting map */
        sortMap.put(SortConstants.ANTIBODY_BY_NAME, new SolrSortMapper(IndexConstants.ANTIBODY_BY_NAME));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the antibody key.
         */
        keyString = IndexConstants.ANTIBODY_KEY;
    }

	@Value("${solr.antibody.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<AntibodyJ> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing antibody data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.ANTIBODY_KEY);

			try {
				AntibodyJ antibody = (AntibodyJ) mapper.readValue((String)doc.getFieldValue(IndexConstants.ANTIBODY_JSON), AntibodyJ.class);
				sr.addResultObjects(antibody);
			} catch (IOException e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
	}
}
