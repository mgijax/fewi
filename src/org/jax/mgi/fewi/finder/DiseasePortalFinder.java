package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrDiseasePortalDiseaseHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGeneHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrHdpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DiseasePortalFinder {


	private final Logger logger = LoggerFactory.getLogger(DiseasePortalFinderOLD.class);

	@Autowired
	private SolrDiseasePortalDiseaseHunter hdpDiseaseHunter;
	
	@Autowired
	private SolrDiseasePortalGeneHunter hdpGeneHunter;

	
	public SearchResults<SolrVocTerm> getDiseases(SearchParams params) {
		
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();

		hdpDiseaseHunter.hunt(params, results);

		SearchResults<SolrVocTerm> srVT = new SearchResults<SolrVocTerm>();
		srVT.cloneFrom(results,SolrVocTerm.class);
		return srVT;
	}

	public SearchResults<SolrDiseasePortalMarker> getMarkers(SearchParams params) {
		
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();

		hdpGeneHunter.hunt(params, results);

		SearchResults<SolrDiseasePortalMarker> srM = new SearchResults<SolrDiseasePortalMarker>();
		srM.cloneFrom(results,SolrDiseasePortalMarker.class);

		return srM;
	}

}
