package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;



@Repository
public class SolrCachedBatchMarkerHunter {

    // logger for the class
    private Logger logger = LoggerFactory.getLogger(SolrCachedBatchMarkerHunter.class);

    public void hunt(SearchParams searchParams, SearchResults<?> searchResults) {

        logger.debug("-> Faking hunter responsibilities");

        List<String> keyList = new ArrayList<String>();
        keyList.add("33429"); // key of gene 'bcan'
        searchResults.setResultKeys(keyList);
        searchResults.setTotalCount(1);
    }
}