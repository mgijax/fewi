package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrMPAnnotationHunter;
import org.jax.mgi.fewi.hunter.SolrMPCorrelationMatrixCellHunter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPAnnotation;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPCorrelationMatrixCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding MP cells for a correlation matrix
 */
@Repository
public class MPCorrelationMatrixCellFinder {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/

    private Logger logger = LoggerFactory.getLogger(MPCorrelationMatrixCellFinder.class);

    @Autowired
    private SolrMPCorrelationMatrixCellHunter mpCorrelationMatrixCellHunter;

	/*---------------------------------*/
	/* Retrieval of multiple annotations
	/*---------------------------------*/

    public SearchResults<SolrMPCorrelationMatrixCell> getCells(SearchParams searchParams) {

        logger.debug("->MPCorrelationMatrixCellFinder.getCells()");

        // result object to be returned
        SearchResults<SolrMPCorrelationMatrixCell> searchResults = new SearchResults<SolrMPCorrelationMatrixCell>();

        // ask the hunter to identify which objects to return
        mpCorrelationMatrixCellHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found " + searchResults.getResultObjects().size() + " cells");

        return searchResults;
    }
}
