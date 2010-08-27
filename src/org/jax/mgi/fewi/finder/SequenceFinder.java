package org.jax.mgi.fewi.finder;

import java.util.*;

// fewi & data model objects
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import mgi.frontend.datamodel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceFinder {

	private Logger logger = LoggerFactory.getLogger(SequenceFinder.class);



	public List<Sequence> getSequenceByID(SearchParams searchParams) {

		logger.info("SequenceFinder.getSequenceByID");

//		Hunter seqHunter = new
		List<Sequence> seqList = new ArrayList<Sequence>();

		return seqList;
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
