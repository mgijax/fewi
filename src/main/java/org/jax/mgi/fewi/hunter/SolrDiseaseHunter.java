package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fe.datamodel.Disease;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrDiseaseHunter extends SolrHunter<Disease> {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrDiseaseHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.DISEASE_ID,
		new SolrPropertyMapper(IndexConstants.DISEASE_ID));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the disease key.
         */
        keyString = IndexConstants.DISEASE_KEY;

    }

	@Value("${solr.disease.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
