package org.jax.mgi.fewi.hunter;

import java.util.*;

//fewi
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

// Spring
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

// Solr
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

@Repository
public class SolrSequenceSummaryHunter {


    public void hunt(SearchParams searchParams, SearchResults searchResults) throws DataAccessException {

        return ;
    }

}