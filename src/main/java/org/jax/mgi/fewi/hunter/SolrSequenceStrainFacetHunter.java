package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSequenceStrainFacetHunter extends SolrSequenceBaseHunter {

//	private ObjectMapper mapper = new ObjectMapper();

    public SolrSequenceStrainFacetHunter() {
    	super();
        facetString = IndexConstants.SEQ_STRAIN;
    }

//    @Value("${solr.sequence.url}")
//    public void setSolrUrl(String solrUrl) {
//    	super.solrUrl = solrUrl;
//    }
}
