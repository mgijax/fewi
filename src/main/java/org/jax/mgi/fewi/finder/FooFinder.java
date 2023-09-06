package org.jax.mgi.fewi.finder;

import java.util.List;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fewi.hunter.FooKeyHunter;
import org.jax.mgi.fewi.hunter.FooSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * This finder is responsible for finding foo(s)
 */

@Repository
public class FooFinder {

	private Logger logger = LoggerFactory.getLogger(FooFinder.class);

	@Autowired
	private FooKeyHunter fooKeyHunter;

	@Autowired
	private FooSummaryHunter fooSummaryHunter;

	@Autowired
	private HibernateObjectGatherer<Marker> fooGatherer;


	public SearchResults<Marker> getFooByID(SearchParams searchParams) {

		logger.debug("->getFooByID()");

		// result object to be returned
		SearchResults<Marker> searchResults = new SearchResults<Marker>();

		// ask the hunter to identify which objects to return
		fooKeyHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found these resultKeys - " + searchResults.getResultKeys());

		// gather objects identified by the hunter, add them to the results
		List<Marker> fooList = fooGatherer.get( Marker.class, searchResults.getResultKeys() );
		searchResults.setResultObjects(fooList);

		return searchResults;
	}


	public SearchResults<Marker> getFooByKey(String dbKey) {

		logger.debug("->getFooByKey()");

		// result object to be returned
		SearchResults<Marker> searchResults = new SearchResults<Marker>();

		// gather objects, add them to the results
		Marker foo = fooGatherer.get( Marker.class, dbKey );
		searchResults.addResultObjects(foo);

		return searchResults;
	}

	public SearchResults<Marker> getFoos(SearchParams searchParams) {

		logger.debug("->getFoos");

		// result object to be returned
		SearchResults<Marker> searchResults = new SearchResults<Marker>();

		// ask the hunter to identify which objects to return
		fooSummaryHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found these resultKeys - " + searchResults.getResultKeys());

		// gather objects identified by the hunter, add them to the results
		List<Marker> fooList = fooGatherer.get( Marker.class, searchResults.getResultKeys() );
		searchResults.setResultObjects(fooList);

		return searchResults;
	}

}
