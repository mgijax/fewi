package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalDiseaseHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGeneHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGridAnnotationHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGridHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

	
	public SearchResults<SolrHdpDisease> getDiseases(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpDiseaseHunter.hunt(params, results);
		SearchResults<SolrHdpDisease> srVT = new SearchResults<SolrHdpDisease>();
		srVT.cloneFrom(results,SolrHdpDisease.class);
		return srVT;
	}

	public SearchResults<SolrHdpMarker> getMarkers(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGeneHunter.hunt(params, results);
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

}
