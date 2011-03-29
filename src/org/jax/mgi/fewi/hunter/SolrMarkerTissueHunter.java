package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerTissueHunter extends SolrHunter {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a MarkerTissue key given a marker ID.
     */
    public SolrMarkerTissueHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        keyString = IndexConstants.UNIQUE_KEY;

    }

	@Value("${solr.markerTissue.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}