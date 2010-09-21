package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.PropertyMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class SolrJournalsACHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrJournalsACHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.REF_JOURNAL, new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
                
        // Set the url for the solr instance.
        
        solrUrl = "http://cardolan.informatics.jax.org:8983/solr/journalsAC/";
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  If there is something special about this
         * field we will want to override the packKeys() method from the superclass
         * and implement the special logic in there.
         */
        
        //keyString = IndexConstants.SEQ_KEY;
        otherString = IndexConstants.REF_JOURNAL_SORT;
        
    }
   
}