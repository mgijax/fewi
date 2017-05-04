package org.jax.mgi.fewi.hunter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.jsonmodel.SimpleSequence;
import org.springframework.beans.factory.annotation.Value;
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
