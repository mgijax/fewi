package org.jax.mgi.fewi.hunter;

import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrSequenceSummaryHunter extends SolrHunter {

    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrSequenceSummaryHunter() {

        /*
         * Setup the property map.  This maps incoming filter to the
         * corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.SEQ_ID, new SolrPropertyMapper(IndexConstants.SEQ_ID));
        propertyMap.put(SearchConstants.MRK_KEY, new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
        propertyMap.put(SearchConstants.SEQ_KEY, new SolrPropertyMapper(IndexConstants.SEQ_KEY));
        propertyMap.put(SearchConstants.SEQ_PROVIDER, new SolrPropertyMapper(IndexConstants.SEQ_PROVIDER));

        /*
         * Setup the sort map;  This maps the requested sort to the
         * corresponding field names in the Solr implementation.
         */
        sortMap.put(SortConstants.SEQUENCE_TYPE, new SolrSortMapper(IndexConstants.SEQ_TYPE_SORT));
        sortMap.put(SortConstants.SEQUENCE_PROVIDER, new SolrSortMapper(IndexConstants.SEQ_PROVIDER_SORT));
        sortMap.put(SortConstants.SEQUENCE_LENGTH, new SolrSortMapper(IndexConstants.SEQ_LENGTH));

        /*
         * Unique identifier for mapping result sets
         */
        keyString = IndexConstants.SEQ_KEY;

    }

    // Inject the url for the solr instance.
    @Value("${solr.sequence.url}")
    public void setSolrUrl(String solrUrl) {
        super.solrUrl = solrUrl;
    }

}