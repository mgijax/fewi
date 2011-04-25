package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrImageSummaryHunter extends SolrHunter {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a image key given an allele key
     */
    public SolrImageSummaryHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.ALL_KEY, new SolrPropertyMapper(IndexConstants.ALL_KEY));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        keyString = IndexConstants.IMAGE_KEY;

    }

	@Value("${solr.image.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}