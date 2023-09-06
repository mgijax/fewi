package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrRecombinaseMatrixCellHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrRecombinaseMatrixCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding cells for a recombinase matrix
 */
@Repository
public class RecombinaseMatrixCellFinder {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/

    private Logger logger = LoggerFactory.getLogger(RecombinaseMatrixCellFinder.class);

    @Autowired
    private SolrRecombinaseMatrixCellHunter recombinaseMatrixCellHunter;

	/*---------------------------------*/
	/* Retrieval of multiple annotations
	/*---------------------------------*/

    public SearchResults<SolrRecombinaseMatrixCell> getCells(SearchParams searchParams) {

        logger.debug("->RecombinaseMatrixCellFinder.getCells()");

        // result object to be returned
        SearchResults<SolrRecombinaseMatrixCell> searchResults = new SearchResults<SolrRecombinaseMatrixCell>();

        // ask the hunter to identify which objects to return
        recombinaseMatrixCellHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found " + searchResults.getResultObjects().size() + " cells");

        return searchResults;
    }
}
