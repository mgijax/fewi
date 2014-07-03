package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerIDHunter extends SolrHunter<String> {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrMarkerIDHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.MRK_ID, new SolrPropertyMapper(IndexConstants.MRK_ID));
        propertyMap.put(IndexConstants.MRK_TERM_ID_FOR_GXD, new SolrPropertyMapper(IndexConstants.MRK_TERM_ID_FOR_GXD));
        propertyMap.put(SearchConstants.PRIMARY_KEY, new SolrPropertyMapper(IndexConstants.MRK_PRIMARY_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the primary marker ID
         */
        keyString = IndexConstants.MRK_PRIMARY_ID;

    }

	@Value("${solr.marker.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}