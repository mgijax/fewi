package org.jax.mgi.fewi.hunter;

import org.springframework.stereotype.Repository;

@Repository
public class SolrSequenceHunter extends SolrSequenceBaseHunter {

//	private ObjectMapper mapper = new ObjectMapper();

    public SolrSequenceHunter() {
    	super();
    }

//    @Value("${solr.sequence.url}")
//    public void setSolrUrl(String solrUrl) {
//    	super.solrUrl = solrUrl;
//    }
}
