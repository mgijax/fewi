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
public class SolrReferenceSummaryHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrReferenceSummaryHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        ArrayList <String> refList = new ArrayList <String> ();
        refList.add(IndexConstants.JNUM_ID);
        refList.add(IndexConstants.PUBMED_ID);
        
        propertyMap.put(SearchConstants.REF_ID, new SolrPropertyMapper(refList, "OR"));
        propertyMap.put(SearchConstants.REF_AUTHOR_ANY, new SolrPropertyMapper(IndexConstants.REF_AUTHOR_FORMATTED));
        propertyMap.put(SearchConstants.REF_AUTHOR_FIRST, new SolrPropertyMapper(IndexConstants.REF_FIRST_AUTHOR));
        propertyMap.put(SearchConstants.REF_AUTHOR_LAST, new SolrPropertyMapper(IndexConstants.REF_LAST_AUTHOR));
        propertyMap.put(SearchConstants.REF_JOURNAL, new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
        propertyMap.put(SearchConstants.REF_TEXT_ABSTRACT, new SolrPropertyMapper(IndexConstants.REF_ABSTRACT));
        propertyMap.put(SearchConstants.REF_TEXT_TITLE, new SolrPropertyMapper(IndexConstants.REF_TITLE));
        propertyMap.put(SearchConstants.REF_YEAR, new SolrPropertyMapper(IndexConstants.REF_YEAR));
        
                
        // Set the url for the solr instance.
        
        solrUrl = "http://cardolan.informatics.jax.org:8983/solr/reference/";
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  If there is something special about this
         * field we will want to override the packKeys() method from the superclass
         * and implement the special logic in there.
         */
        
        keyString = IndexConstants.REF_KEY;
        
    }
   
}