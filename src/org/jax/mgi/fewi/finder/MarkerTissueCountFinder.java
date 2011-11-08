package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.MarkerTissueCount;

import org.jax.mgi.fewi.hunter.FooKeyHunter;
import org.jax.mgi.fewi.hunter.FooSummaryHunter;
import org.jax.mgi.fewi.hunter.SolrMarkerTissueHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding foo(s)
 */

@Repository
public class MarkerTissueCountFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(MarkerTissueCountFinder.class);

    @Autowired
    private FooKeyHunter fooKeyHunter;

    @Autowired
    private FooSummaryHunter fooSummaryHunter;
    
    @Autowired
    private SolrMarkerTissueHunter tissueHunter;

    @Autowired
    private HibernateObjectGatherer<MarkerTissueCount> tissueGatherer;

    /*---------------------------------*/
    /* Retrieval of multiple tissues
    /*---------------------------------*/

    public SearchResults<MarkerTissueCount> getTissues(SearchParams searchParams) {

        logger.debug("->getTissues");

        // result object to be returned
        SearchResults<MarkerTissueCount> searchResults = new SearchResults<MarkerTissueCount>();

        logger.debug(searchParams.getFilter().toString());
        
        // ask the hunter to identify which objects to return
        tissueHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        List<MarkerTissueCount> tissueList
          = tissueGatherer.get( MarkerTissueCount.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(tissueList);

        return searchResults;
    }



}
