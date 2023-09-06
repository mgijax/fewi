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
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class SolrBrowserTermHunter extends SolrHunter<BrowserTerm> {

	private ObjectMapper mapper = new ObjectMapper();

    public SolrBrowserTermHunter() {

        /*
         * Set up the sort mapping.  These are fields we can sort by in Solr.
         */
        sortMap.put(SortConstants.VB_SEQUENCE_NUM, new SolrSortMapper(IndexConstants.VB_SEQUENCE_NUM));
        sortMap.put(SortConstants.VB_TERM, new SolrSortMapper(IndexConstants.VB_TERM));
        
        /*
         * Setup the property map.  This maps from the properties of the
		 * incoming filter list to the corresponding field names in the Solr
		 * implementation.  
         */
        propertyMap.put(SearchConstants.VB_ACC_ID, new SolrPropertyMapper(IndexConstants.VB_ACC_ID));
        propertyMap.put(SearchConstants.VB_PARENT_ID, new SolrPropertyMapper(IndexConstants.VB_PARENT_ID));
        propertyMap.put(SearchConstants.VB_SYNONYM, new SolrPropertyMapper(IndexConstants.VB_SYNONYM));
        propertyMap.put(SearchConstants.VB_TERM, new SolrPropertyMapper(IndexConstants.VB_TERM));
        propertyMap.put(SearchConstants.VB_VOCAB_NAME, new SolrPropertyMapper(IndexConstants.VB_VOCAB_NAME));
        propertyMap.put(SearchConstants.VB_DAG_NAME, new SolrPropertyMapper(IndexConstants.VB_DAG_NAME));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
         keyString = IndexConstants.VB_PRIMARY_ID;
    }

    @Value("${solr.vocabBrowser.url}")
    public void setSolrUrl(String solrUrl) {
    	super.solrUrl = solrUrl;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void packInformation (QueryResponse rsp, SearchResults<BrowserTerm> sr, SearchParams sp) {
    	logger.debug ("Entering SolrBrowserTermHunter.packInformation()");

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing BrowserTerm data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		List<String> resultKeys = new ArrayList<String>();

		for (SolrDocument doc : sdl) {
			String key = (String) doc.getFieldValue(IndexConstants.VB_PRIMARY_ID);

			try {
				BrowserTerm term = (BrowserTerm) mapper.readValue((String)doc.getFieldValue(IndexConstants.VB_BROWSER_TERM), BrowserTerm.class);
				sr.addResultObjects(term);
			} catch (IOException e) {
				e.printStackTrace();
			}

			resultKeys.add(key);
		}
		sr.setResultKeys(resultKeys);
		logger.debug ("Exiting packInformation() with " + sr.getResultObjects().size() + " terms");
    }
}
