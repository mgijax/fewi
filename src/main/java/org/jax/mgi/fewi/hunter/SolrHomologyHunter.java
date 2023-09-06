package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fe.datamodel.HomologyCluster;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrHomologyHunter extends SolrHunter<HomologyCluster>
{
    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrHomologyHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.HOMOLOGY_ID, new SolrPropertyMapper(IndexConstants.HOMOLOGY_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the primary marker ID
         */
        keyString = IndexConstants.HOMOLOGY_KEY;

    }

	@Value("${solr.homology.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}