package mgi.frontend.finder;

import java.util.List;

import mgi.frontend.controller.MarkerQuery;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.results.MarkerResults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MarkerFinder {
	
	private Logger logger = LoggerFactory.getLogger(MarkerFinder.class);
	
	@Autowired
	private SolrMarkerHunter markerHunter;
	
	@Autowired
	private HibernateMarkerDAO markerGatherer;
	
	GenericGatherer<Marker> markerG = 
			new HibernateGatherer<Marker>(Marker.class);

	public List<Marker> getMarkersByID(List<Integer> markerIDs) {
		logger.info("get markerIDs");
		return markerGatherer.get(markerIDs);
	}

	public Marker getMarkerByID(int id) {
		logger.info("get id");
		return markerGatherer.get(id);
	}

	public MarkerResults searchMarkers(MarkerQuery query) {
		logger.info("searchMarkers");
		markerG.get(10603);
		return markerHunter.searchMarkers(query);
	}

}
