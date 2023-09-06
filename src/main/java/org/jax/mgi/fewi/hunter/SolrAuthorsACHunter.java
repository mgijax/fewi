package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrAuthorsACHunter extends SolrHunter<String>
{    
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrAuthorsACHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.REF_AUTHOR, new SolrPropertyMapper(IndexConstants.REF_AUTHOR));
        propertyMap.put(SearchConstants.AC_FOR_GXD, new SolrPropertyMapper(IndexConstants.AC_FOR_GXD));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a 
         * specific field, and return it rather than a list of keys.
         */  
        
        otherString = IndexConstants.REF_AUTHOR_SORT;
        
    }
    
	@Value("${solr.authors_ac.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
   
}