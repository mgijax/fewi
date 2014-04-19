package org.jax.mgi.fewi.hunter;

import mgi.frontend.datamodel.AlleleSystemAssayResult;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrCreAssayResultSummaryHunter extends SolrHunter<AlleleSystemAssayResult> {
    
    /***
     * The constructor sets up this hunter so that it is specific to cre
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrCreAssayResultSummaryHunter() {        
        
        /*
         * Setup the sort mapping.
         */
        
        sortMap.put(SortConstants.CRE_BY_STRUCTURE, new SolrSortMapper(IndexConstants.CRE_BY_STRUCTURE));
        sortMap.put(SortConstants.CRE_BY_AGE, new SolrSortMapper(IndexConstants.CRE_BY_AGE));
        sortMap.put(SortConstants.CRE_BY_LEVEL, new SolrSortMapper(IndexConstants.CRE_BY_LEVEL));
        sortMap.put(SortConstants.CRE_BY_PATTERN, new SolrSortMapper(IndexConstants.CRE_BY_PATTERN));
        sortMap.put(SortConstants.CRE_BY_JNUM_ID, new SolrSortMapper(IndexConstants.CRE_BY_JNUM_ID));
        sortMap.put(SortConstants.CRE_BY_ASSAY_TYPE, new SolrSortMapper(IndexConstants.CRE_BY_ASSAY_TYPE));
        sortMap.put(SortConstants.CRE_BY_REPORTER_GENE, new SolrSortMapper(IndexConstants.CRE_BY_REPORTER_GENE));
        sortMap.put(SortConstants.CRE_BY_DETECTION_METHOD, new SolrSortMapper(IndexConstants.CRE_BY_DETECTION_METHOD));
        sortMap.put(SortConstants.CRE_BY_ASSAY_NOTE, new SolrSortMapper(IndexConstants.CRE_BY_ASSAY_NOTE));
        sortMap.put(SortConstants.CRE_BY_ALLELIC_COMPOSITION, new SolrSortMapper(IndexConstants.CRE_BY_ALLELIC_COMPOSITION));
        sortMap.put(SortConstants.CRE_BY_SEX, new SolrSortMapper(IndexConstants.CRE_BY_SEX));
        sortMap.put(SortConstants.CRE_BY_SPECIMEN_NOTE, new SolrSortMapper(IndexConstants.CRE_BY_SPECIMEN_NOTE));
        sortMap.put(SortConstants.CRE_BY_RESULT_NOTE, new SolrSortMapper(IndexConstants.CRE_BY_RESULT_NOTE));        

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        
        propertyMap.put(SearchConstants.ALL_ID, new SolrPropertyMapper(IndexConstants.ALL_ID));
        propertyMap.put(SearchConstants.CRE_SYSTEM_KEY, new SolrPropertyMapper(IndexConstants.CRE_SYSTEM_KEY));
        
        // Set the url for the solr instance.
        
        solrUrl = ContextLoader.getConfigBean().getProperty("solr.creAssayResult.url");
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we are looking for the 
         * standard list of keys to be returned.
         */
        
        keyString = IndexConstants.CRE_ASSAY_RESULT_KEY;
        
    }
    
	@Value("${solr.creAssayResult.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
   
}
