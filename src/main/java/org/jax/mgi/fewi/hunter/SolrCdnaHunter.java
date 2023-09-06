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
import org.jax.mgi.shr.jsonmodel.Clone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SolrCdnaHunter extends SolrHunter<Clone> {

	private ObjectMapper mapper = new ObjectMapper();

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * data for cDNA clones
     */
    public SolrCdnaHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.CDNA_KEY, new SolrPropertyMapper(IndexConstants.CDNA_KEY));
        propertyMap.put(SearchConstants.CDNA_MARKER_ID, new SolrPropertyMapper(IndexConstants.CDNA_MARKER_ID));
        propertyMap.put(SearchConstants.CDNA_SEQUENCE_NUM, new SolrPropertyMapper(IndexConstants.CDNA_SEQUENCE_NUM));
        propertyMap.put(SearchConstants.CDNA_CLONE, new SolrPropertyMapper(IndexConstants.CDNA_CLONE));

        /* set up the sorting map */
        sortMap.put(SortConstants.SEQUENCE_NUM, new SolrSortMapper(IndexConstants.CDNA_SEQUENCE_NUM));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the experiment key.
         */
        keyString = IndexConstants.CDNA_KEY;

    }

	@Value("${solr.cdna.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<Clone> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing cDNA clone data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.CDNA_KEY);

			try {
				Clone clone = (Clone) mapper.readValue((String)doc.getFieldValue(IndexConstants.CDNA_CLONE), Clone.class);
				sr.addResultObjects(clone);
			} catch (IOException e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
	}
}
