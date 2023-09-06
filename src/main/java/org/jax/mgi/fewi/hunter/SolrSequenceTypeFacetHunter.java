package org.jax.mgi.fewi.hunter;

import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSequenceTypeFacetHunter extends SolrSequenceBaseHunter {

//	private ObjectMapper mapper = new ObjectMapper();

    public SolrSequenceTypeFacetHunter() {
    	super();
        facetString = IndexConstants.SEQ_TYPE;
    }

//    @Value("${solr.sequence.url}")
//    public void setSolrUrl(String solrUrl) {
//    	super.solrUrl = solrUrl;
//    }
}
