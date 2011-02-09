package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerAnnotationSummaryHunter extends SolrHunter {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrMarkerAnnotationSummaryHunter() {

        /*
         * Set up the sort mapping
         */
        
        sortMap.put(SortConstants.VOC_TERM, new SolrSortMapper(IndexConstants.VOC_TERM));
        sortMap.put(SortConstants.VOC_BY_DAG_STRUCT, new SolrSortMapper(IndexConstants.VOC_BY_DAG_STRUCT));
        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.VOC_VOCAB, new SolrPropertyMapper(IndexConstants.VOC_VOCAB));
        propertyMap.put(SearchConstants.VOC_RESTRICTION, new SolrPropertyMapper(IndexConstants.VOC_QUALIFIER));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        
         keyString = IndexConstants.ANNOTATION_KEY;

    }

	@Value("${solr.markerAnnotation.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}