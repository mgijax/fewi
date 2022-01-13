package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.hunter.SolrMarkerIDHunter;
import org.jax.mgi.fewi.hunter.SolrMarkerKeyHunter;
import org.jax.mgi.fewi.hunter.SolrMarkerSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MarkerFinder {


	private Logger logger = LoggerFactory.getLogger(MarkerFinder.class);

	@Autowired
	private SolrMarkerKeyHunter mrkKeyHunter;

	@Autowired
	private SolrMarkerIDHunter mrkIDHunter;

	@Autowired
	private SolrMarkerSummaryHunter markerSummaryHunter;

	@Autowired
	private HibernateObjectGatherer<Marker> mrkGatherer;

	public SearchResults<Marker> getMarkerByID(SearchParams searchParams) {

		logger.debug("->getMarkerByID()");

		// result object to be returned
		SearchResults<Marker> searchResults = new SearchResults<Marker>();

		// ask the hunter to identify which objects to return
		mrkKeyHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found these resultKeys - " + searchResults.getResultKeys());

		// gather objects identified by the hunter, add them to the results
		List<Marker> mrkList = mrkGatherer.get( Marker.class, searchResults.getResultKeys() );
		searchResults.setResultObjects(mrkList);

		return searchResults;
	}

	// convenience wrapper
	public SearchResults<Marker> getMarkerByID(String id) {
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(new Filter(SearchConstants.MRK_ID,id,Filter.Operator.OP_EQUAL));
		return getMarkerByID(searchParams);
	}

	// convenience wrapper
	public SearchResults<Marker> getMarkerBySymbol(String id) {
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(new Filter(SearchConstants.MRK_SYMBOL,id.trim(),Filter.Operator.OP_EQUAL));
		return getMarkers(searchParams);
	}

	public List<Marker> getMarkerByPrimaryId(String id) {
		return mrkGatherer.get(Marker.class,Arrays.asList(id),"primaryID");
	}

    public List<Marker> getMarkerByPrimaryIDs(List<String> markerIDs)
    {
        return mrkGatherer.get(Marker.class, markerIDs, "primaryID" );
    }

	/*--------------------------------------------*/
	/* Retrieval of a marker, for a given db key
    /*--------------------------------------------*/

	public SearchResults<Marker> getMarkerByKey(String dbKey) {

		logger.debug("->getMarkerByKey()");

		SearchResults<Marker> searchResults = new SearchResults<Marker>();

		// gather objects, add them to the results
		Marker marker = mrkGatherer.get( Marker.class, dbKey );
		searchResults.addResultObjects(marker);

		return searchResults;
	}

	//      Should this be using the markerSummaryHunter? 
	//		This gets a bit confusing, but am unsure about removing - kstone
	public SearchResults<Marker> getMarkers(SearchParams searchParams) {

		logger.debug("->getMarkers");

		// result object to be returned
		SearchResults<SolrSummaryMarker> searchResults = new SearchResults<SolrSummaryMarker>();

		// ask the hunter to identify which objects to return
		markerSummaryHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found these resultKeys - " + searchResults.getResultKeys());

		// clone SearchResults to allow us to set Marker objects instead of SolrSummaryMarker
		SearchResults<Marker> srMarker = new SearchResults<Marker>();
		srMarker.cloneFrom(searchResults);

		// gather objects identified by the hunter, add them to the results
		List<Marker> markerList = mrkGatherer.get( Marker.class, srMarker.getResultKeys() );
		srMarker.setResultObjects(markerList);

		return srMarker;
	}

	/*
	 * Retrieval of just marker IDs
	 */
	public SearchResults<String> getMarkerIDs(SearchParams searchParams) {
		logger.debug("->getMarkerIDs()");

		SearchResults<String> searchResults = new SearchResults<String>();

		mrkIDHunter.hunt(searchParams,searchResults);

		return searchResults;
	}

	/*
	 * Retrieval for marker summary
	 */
	public SearchResults<SolrSummaryMarker> getSummaryMarkers(SearchParams searchParams) {
		logger.debug("->getSummaryMarkers()");

		SearchResults<SolrSummaryMarker> searchResults = new SearchResults<SolrSummaryMarker>();

		markerSummaryHunter.hunt(searchParams,searchResults);

		return searchResults;
	}
}
