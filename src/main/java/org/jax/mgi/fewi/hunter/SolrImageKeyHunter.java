package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fe.datamodel.Image;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrImageKeyHunter extends SolrHunter<Image> {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * an image key given an image ID.
     */
    public SolrImageKeyHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.IMG_ID, new SolrPropertyMapper(IndexConstants.IMAGE_ID));

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