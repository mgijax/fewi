package org.jax.mgi.fewi.finder;

import java.util.*;

// fewi & data model objects
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

import mgi.frontend.datamodel.*;
import org.jax.mgi.fewi.hunter.SolrSequenceKeyHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceFinder {

	private Logger logger = LoggerFactory.getLogger(SequenceFinder.class);

	@Autowired
	private SolrSequenceKeyHunter sequenceHunter;

	@Autowired
	private HibernateObjectGatherer<Sequence> sequenceGatherer;


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of a sequence, for a given ID
    /////////////////////////////////////////////////////////////////////////

	public SearchResults<Sequence> getSequenceByID(SearchParams searchParams) {


		logger.info("SequenceFinder.getSequenceByID");

		SearchResults<Sequence> searchResults = new SearchResults<Sequence>();

		sequenceHunter.hunt(searchParams, searchResults);

//		referenceGatherer.setType(Reference.class);
//		results.setResultObjects(referenceGatherer.get(iKeys));

System.out.println("-->>object retrieval; sending key --> " + searchResults.getResultKeys());

		sequenceGatherer.setType(Sequence.class);
        List<Sequence> seqList = sequenceGatherer.get( searchResults.getResultKeys() );

System.out.println("-->>sequence list length --> " + seqList.size());




		return searchResults;
	}



//	@Autowired
//	private SolrMarkerHunter markerHunter;

//	@Autowired
//	private HibernateGatherer<Marker> markerGatherer;

//	public List<Marker> getMarkersByID(List<Integer> markerIDs) {
//		logger.info("get markerIDs");
//		markerGatherer.setType(Marker.class);
//		return markerGatherer.get(markerIDs);
//	}

//	public MarkerResults searchMarkers(MarkerQuery query) {
//		logger.info("searchMarkers");
//		return markerHunter.searchMarkers(query);
//	}

}
