package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.stereotype.Repository;

@Repository
public class SolrReferenceAuthorFacetHunter extends SolrHunter {
    
    /***
     * The constructor sets up this hunter so that it is specific to reference
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrReferenceAuthorFacetHunter() {        
        
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
        propertyMap.put(FacetConstants.REF_CURATED_DATA, new SolrPropertyMapper(IndexConstants.REF_HAS_DATA));
                
        // Set the url for the solr instance.
        
        solrUrl = "http://cardolan.informatics.jax.org:8983/solr/reference/";
        
        
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to pack it into the 
         * keys collection in the response.
         */
        
        //keyString = IndexConstants.REF_KEY;
        facetString = IndexConstants.REF_AUTHOR_FACET;
        
    }
   
}