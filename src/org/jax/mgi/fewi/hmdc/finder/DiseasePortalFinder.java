package org.jax.mgi.fewi.hmdc.finder;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalDiseaseHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGeneHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGridAnnotationHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGridHighlightHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGridHunter;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import mgi.frontend.datamodel.hdp.HdpGenoCluster;

@Repository
public class DiseasePortalFinder {

	@Autowired
	private SolrDiseasePortalDiseaseHunter hdpDiseaseHunter;
	
	@Autowired
	private SolrDiseasePortalGeneHunter hdpGeneHunter;
	
	@Autowired
	private SolrDiseasePortalGridHunter hdpGridHunter;
	
	@Autowired
	private SolrDiseasePortalGridAnnotationHunter hdpGridAnnotationHunter;

	@Autowired
	private SolrDiseasePortalGridHighlightHunter hdpGridHighlightHunter;
	
	@Autowired
	private HibernateObjectGatherer<HdpGenoCluster> genoCGatherer;
	
	public SearchResults<SolrHdpDisease> getDiseases(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpDiseaseHunter.hunt(params, results, DiseasePortalFields.TERM_ID);
		SearchResults<SolrHdpDisease> srVT = new SearchResults<SolrHdpDisease>();
		srVT.cloneFrom(results,SolrHdpDisease.class);
		return srVT;
	}

	public SearchResults<SolrHdpMarker> getMarkers(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGeneHunter.hunt(params, results, DiseasePortalFields.MARKER_KEY);
		SearchResults<SolrHdpMarker> srM = new SearchResults<SolrHdpMarker>();
		srM.cloneFrom(results,SolrHdpMarker.class);
		return srM;
	}
	
	public SearchResults<SolrHdpGridEntry> getGridResults(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHunter.hunt(params, results);
		SearchResults<SolrHdpGridEntry> srM = new SearchResults<SolrHdpGridEntry>();
		srM.cloneFrom(results,SolrHdpGridEntry.class);
		return srM;
	}
	
	public SearchResults<SolrHdpGridAnnotationEntry> getGridAnnotationResults(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> annotationResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridAnnotationHunter.hunt(params, annotationResults);
		SearchResults<SolrHdpGridAnnotationEntry> srM = new SearchResults<SolrHdpGridAnnotationEntry>();
		srM.cloneFrom(annotationResults,SolrHdpGridAnnotationEntry.class);
		return srM;
	}

	public List<String> getGridHighlights(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> highlightResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHighlightHunter.hunt(params, highlightResults, DiseasePortalFields.TERM_HEADER);
		return highlightResults.getResultKeys();
	}

	// gets genocluster data for link from grid popup
	public List<HdpGenoCluster> getGenoClusterByKey(String genoClusterKey) {
		return genoCGatherer.get(HdpGenoCluster.class,Arrays.asList(genoClusterKey));
	}
}
