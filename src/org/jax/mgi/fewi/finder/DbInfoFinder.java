package org.jax.mgi.fewi.finder;

import mgi.frontend.datamodel.DatabaseInfo;

import org.jax.mgi.fewi.hunter.HibernateDbInfoHunter;
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
 * This finder is responsible for finding (s)
 */

@Repository
public class DbInfoFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(BatchFinder.class);

    @Autowired
    private HibernateDbInfoHunter dbInfoHunter;


    /*---------------------------------*/
    /* Retrieval of multiple s
    /*---------------------------------*/

    public SearchResults<DatabaseInfo> getInfo(SearchParams searchParams) {

        logger.debug("->getDbInfo");

        // result object to be returned
        SearchResults<DatabaseInfo> searchResults = new SearchResults<DatabaseInfo>();

        // ask the hunter to identify which objects to return
        dbInfoHunter.hunt(searchParams, searchResults);

        return searchResults;
    }

}
