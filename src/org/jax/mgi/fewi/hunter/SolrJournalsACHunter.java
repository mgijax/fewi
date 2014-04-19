package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrJournalsACHunter extends SolrHunter<String>
{    
    /***
     * The constructor sets up this hunter so that it pulls back a list of
     * possible journals for a given input string.
     */
    public SolrJournalsACHunter() {        
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.REF_JOURNAL, new SolrPropertyMapper(IndexConstants.REF_JOURNAL));
        propertyMap.put(SearchConstants.AC_FOR_GXD, new SolrPropertyMapper(IndexConstants.AC_FOR_GXD));
        
	/* sort journal auto-complete results in a case-insensitive manner
	*/
	sortMap.put(SortConstants.REF_JOURNAL_AC,
	    new SolrSortMapper(IndexConstants.REF_JOURNAL_SORT_LOWER));

        /*
         * For this hunter we are not interested in keys, we instead want to pack 
         * a specific field from the returned solr documents into the response.
         */       
        otherString = IndexConstants.REF_JOURNAL_SORT;
        
    }
	
	@Value("${solr.journals_ac.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}
