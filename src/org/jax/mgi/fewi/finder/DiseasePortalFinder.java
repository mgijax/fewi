package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fewi.hunter.SolrDiseasePortalDiseaseHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGeneHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGridAnnotationHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalGridHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.JsonGridPopupMapper;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.JsonGridMapper;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpDisease;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpGridEntry;
import org.jax.mgi.fewi.searchUtil.entities.hmdc.SolrHdpMarker;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
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
	
	public JsonGridMapper getGridMapper(SearchParams params) {
		
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHunter.hunt(params, results);

		List<String> gridKeys = new ArrayList<String>();
		
		for(SolrHdpEntityInterface shei: results.getResultObjects()) {
			SolrHdpGridEntry shge = (SolrHdpGridEntry)shei;
			gridKeys.add(shge.getGridKey().toString());
		}
		
		Filter gridFilter = new Filter(DiseasePortalFields.GRID_KEY, gridKeys, Operator.OP_IN);
		params.setFilter(gridFilter);
		
		SearchResults<SolrHdpEntityInterface> annotationResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridAnnotationHunter.hunt(params, annotationResults);
		
		return new JsonGridMapper(results, annotationResults);
	}
	
	public JsonGridPopupMapper getGridPopupMapper(SearchParams params) {
		
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHunter.hunt(params, results);
		
		List<String> gridKeys = new ArrayList<String>();
		
		for(SolrHdpEntityInterface shei: results.getResultObjects()) {
			SolrHdpGridEntry shge = (SolrHdpGridEntry)shei;
			gridKeys.add(shge.getGridKey().toString());
		}
		
		Filter gridFilter = new Filter(DiseasePortalFields.GRID_KEY, gridKeys, Operator.OP_IN);
		params.setFilter(gridFilter);
		
		SearchResults<SolrHdpEntityInterface> annotationResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridAnnotationHunter.hunt(params, annotationResults);
		
		return new JsonGridPopupMapper(results, annotationResults);

	}

}
