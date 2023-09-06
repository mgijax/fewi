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
import org.jax.mgi.shr.jsonmodel.MolecularProbe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SolrProbeHunter extends SolrHunter<MolecularProbe> {

	private ObjectMapper mapper = new ObjectMapper();

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * data for molecular probes
     */
    public SolrProbeHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.PRB_KEY, new SolrPropertyMapper(IndexConstants.PRB_KEY));
        propertyMap.put(SearchConstants.PRB_MARKER_ID, new SolrPropertyMapper(IndexConstants.PRB_MARKER_ID));
        propertyMap.put(SearchConstants.PRB_PROBE, new SolrPropertyMapper(IndexConstants.PRB_PROBE));
        propertyMap.put(SearchConstants.PRB_SEGMENT_TYPE, new SolrPropertyMapper(IndexConstants.PRB_SEGMENT_TYPE));
        propertyMap.put(SearchConstants.PRB_REFERENCE_ID, new SolrPropertyMapper(IndexConstants.PRB_REFERENCE_ID));

        /* set up the sorting map */
        sortMap.put(SortConstants.PRB_BY_NAME, new SolrSortMapper(IndexConstants.PRB_BY_NAME));
        sortMap.put(SortConstants.PRB_BY_TYPE, new SolrSortMapper(IndexConstants.PRB_BY_TYPE));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the probe key.
         */
        keyString = IndexConstants.PRB_KEY;
    }

	@Value("${solr.probe.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<MolecularProbe> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing probe data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.PRB_KEY);

			try {
				MolecularProbe probe = (MolecularProbe) mapper.readValue((String)doc.getFieldValue(IndexConstants.PRB_PROBE), MolecularProbe.class);
				sr.addResultObjects(probe);
			} catch (IOException e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
	}
}
