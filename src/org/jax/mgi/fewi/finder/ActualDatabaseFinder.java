package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.ActualDatabase;

import org.jax.mgi.fewi.hunter.HibernateActualDatabaseHunter;
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
 * This finder is responsible for finding ActualDatabase objects
 */

@Repository
public class ActualDatabaseFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(
	ActualDatabaseFinder.class);

    @Autowired
    private HibernateActualDatabaseHunter actualDbHunter;


    /*-------------*/
    /* Constructor */
    /*-------------*/
    public ActualDatabaseFinder() {}

    /*----------------------------------*/
    /* Retrieval of all ActualDatabases */
    /*----------------------------------*/

    public List<ActualDatabase> getAll() {

        //logger.debug("HibernateActualDatabaseHunter.getAll()");

	SearchParams searchParams = new SearchParams();

        // result object to be returned
        SearchResults<ActualDatabase> searchResults =
		new SearchResults<ActualDatabase>();

	if (this.actualDbHunter == null) {
		this.actualDbHunter = new HibernateActualDatabaseHunter();
	}
        // ask the hunter to identify which objects to return
        actualDbHunter.hunt(searchParams, searchResults);

	//logger.debug ("Received searchResults");

        return searchResults.getResultObjects();
    }

}
