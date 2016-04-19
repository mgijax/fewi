package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrDiseasePortalDiseaseHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGeneHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGridHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpMarker;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpGridRow;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpDisease;
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
	
	public SearchResults<SolrHdpGridRow> getGrid(SearchParams params) {
		
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();

		hdpGridHunter.hunt(params, results);

		SearchResults<SolrHdpGridRow> srM = new SearchResults<SolrHdpGridRow>();
		srM.cloneFrom(results,SolrHdpGridRow.class);
		
		return srM;
	}

}
