package org.jax.mgi.fewi.finder;

/*-------------------------------*/
/* to be changed for each Finder */
/*-------------------------------*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.AlleleSystem;
import org.jax.mgi.fe.datamodel.AlleleSystemAssayResult;
import org.jax.mgi.fe.datamodel.group.RecombinaseEntity;
import org.jax.mgi.fewi.highlight.RecombinaseHighlightInfo;
import org.jax.mgi.fewi.hunter.HibernateAlleleSystemHunter;
import org.jax.mgi.fewi.hunter.SolrCreAssayResultSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrCreSystemHighlight;
import org.jax.mgi.shr.fe.indexconstants.CreFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*-------*/
/* class */
/*-------*/

@Repository
public class RecombinaseFinder {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/

	private final Logger logger = LoggerFactory.getLogger (
		RecombinaseFinder.class);

	@Autowired
	private HibernateAlleleSystemHunter alleleSystemHunter;

	@Autowired
	private SolrCreAssayResultSummaryHunter creAssayHunter;

	@Autowired
	private HibernateObjectGatherer<Allele> summaryGatherer;

	@Autowired
	private HibernateObjectGatherer<AlleleSystem> alleleSystemGatherer;

	@Autowired
	private HibernateObjectGatherer<AlleleSystemAssayResult> assayResultGatherer;


	/*-------------------------*/
	/* public instance methods */
	/*-------------------------*/


	// Recombinase Summary
	public SearchResults<Allele> searchRecombinases(SearchParams params) {

		logger.debug ("searchRecombinases");

		SearchResults<RecombinaseEntity> results = new SearchResults<RecombinaseEntity>();

		logger.debug ("hunt");
		creAssayHunter.hunt(params, results, SearchConstants.ALL_KEY);

		SearchResults<Allele> srA = new SearchResults<Allele>();
		srA.cloneFrom(results, Allele.class);

		logger.debug ("gather");
		List<Allele> alleles = summaryGatherer.get (Allele.class, srA.getResultKeys());

		srA.setResultObjects (alleles);
		return srA;
	}


	// recombinase specificity by allele ID + system's term key
	public SearchResults<AlleleSystem> getAlleleSystems(String alleleID, String termKey) {
		logger.debug ("->getAlleleSystems(" + alleleID + ", " + termKey + ")");
		SearchResults<AlleleSystem> searchResults = new SearchResults<AlleleSystem>();

		// collect our filters
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(new Filter(SearchConstants.ALL_ID, alleleID, Filter.Operator.OP_EQUAL));
		filters.add(new Filter(SearchConstants.CRE_SYSTEM_KEY, termKey, Filter.Operator.OP_EQUAL));
		SearchParams sp = new SearchParams();
		sp.setFilter(Filter.and(filters));

		alleleSystemHunter.hunt (sp, searchResults);

		return searchResults;
	}

	// Recombinase Specificity
	public SearchResults<AlleleSystem> getAlleleSystemByKey(String alleleSystemKey) {

		logger.debug ("->getAlleleSystemByKey("+alleleSystemKey+")");
		SearchResults<AlleleSystem> searchResults = new SearchResults<AlleleSystem>();
		AlleleSystem alleleSystem = alleleSystemGatherer.get (AlleleSystem.class, alleleSystemKey);

		if (alleleSystem != null) {
			searchResults.addResultObjects (alleleSystem);
		}
		return searchResults;
	}
	


	public SearchResults<AlleleSystem> getAlleleSystemBySystem(String alleleId, String system) {
		logger.debug ("->getAlleleSystemBySystem("+ alleleId + "," + system + ")");
		SearchResults<AlleleSystem> searchResults = new SearchResults<AlleleSystem>();
		
		SearchParams params = new SearchParams();
		params.setPageSize(1);
		params.setFilter(Filter.and(Arrays.asList(
				new Filter(SearchConstants.ALL_ID, alleleId),
				new Filter(SearchConstants.CRE_SYSTEM, system)
		)));
		
		this.alleleSystemHunter.hunt(params, searchResults);
		
		return searchResults;
	}



	// Recombinase Specificity Assay Summary
	public SearchResults<AlleleSystemAssayResult> getAssaySummary(SearchParams params) {

		logger.debug ("getAssaySummary");

		SearchResults<RecombinaseEntity> results
		  = new SearchResults<RecombinaseEntity>();

		logger.debug ("hunt");
		creAssayHunter.hunt (params, results);

		SearchResults<AlleleSystemAssayResult> srAR = new SearchResults<AlleleSystemAssayResult>();
		srAR.cloneFrom(results, AlleleSystemAssayResult.class);

		logger.debug ("gather");
		List<AlleleSystemAssayResult> alleleSystem
		  = assayResultGatherer.get (AlleleSystemAssayResult.class, srAR.getResultKeys());

		srAR.setResultObjects (alleleSystem);
		return srAR;
	}


	// Recombinase Allele Sumary - System Highlights
	public RecombinaseHighlightInfo searchRecombinaseSystemHighlights(SearchParams params) {

		// adjust pageSize for this query
		int originalPageSize = params.getPageSize();
		params.setPageSize(100000);

		RecombinaseHighlightInfo highlightInfo = new RecombinaseHighlightInfo();

		//-----------
		// Anatomical system highlights
		SearchResults<RecombinaseEntity> results = new SearchResults<RecombinaseEntity>();
		creAssayHunter.hunt(params, results, SearchConstants.CRE_SYSTEM_HL_GROUP);

		SearchResults<SolrCreSystemHighlight> srSL = new SearchResults<SolrCreSystemHighlight>();
		srSL.cloneFrom(results, SolrCreSystemHighlight.class);

		highlightInfo.addSystemHighlights(srSL.getResultObjects());

		// restore originalPageSize
		params.setPageSize(originalPageSize);

		return highlightInfo;
	}

	// Recombinase Allele Sumary - Cell type Highlights
	public RecombinaseHighlightInfo searchRecombinaseCellTypeHighlights(SearchParams params) {

		// adjust pageSize for this query
		int originalPageSize = params.getPageSize();
		params.setPageSize(100000);

		RecombinaseHighlightInfo highlightInfo = new RecombinaseHighlightInfo();

		//----------
		// Cell type highlights
		SearchResults<RecombinaseEntity> results2 = new SearchResults<RecombinaseEntity>();
		creAssayHunter.hunt(params, results2, SearchConstants.CRE_CELL_TYPE_HL_GROUP);

		SearchResults<SolrCreSystemHighlight> srSL2 = new SearchResults<SolrCreSystemHighlight>();
		srSL2.cloneFrom(results2, SolrCreSystemHighlight.class);

		highlightInfo.addCellTypeHighlights(srSL2.getResultObjects());

		// restore originalPageSize
		params.setPageSize(originalPageSize);

		return highlightInfo;
	}

	// filtering facets

	public SearchResults<RecombinaseEntity> getDriverFacet(SearchParams params) {

		SearchResults<RecombinaseEntity> results = new SearchResults<RecombinaseEntity>();

		logger.debug ("hunt");
		creAssayHunter.setFacet(CreFields.DRIVER_FACET);
		creAssayHunter.hunt(params, results, SearchConstants.ALL_KEY);

		return results;
	}

	public SearchResults<RecombinaseEntity> getInducerFacet(SearchParams params) {

		SearchResults<RecombinaseEntity> results = new SearchResults<RecombinaseEntity>();

		logger.debug ("hunt");
		creAssayHunter.setFacet(CreFields.INDUCER);
		creAssayHunter.hunt(params, results, SearchConstants.ALL_KEY);

		return results;
	}

	public SearchResults<RecombinaseEntity> getSystemDetectedFacet(SearchParams params) {

		SearchResults<RecombinaseEntity> results = new SearchResults<RecombinaseEntity>();

		logger.debug ("hunt");
		creAssayHunter.setFacet(CreFields.ALL_SYSTEM_DETECTED);
		creAssayHunter.hunt(params, results, SearchConstants.ALL_KEY);

		return results;
	}

	public SearchResults<RecombinaseEntity> getSystemNotDetectedFacet(SearchParams params) {

		SearchResults<RecombinaseEntity> results = new SearchResults<RecombinaseEntity>();

		logger.debug ("hunt");
		creAssayHunter.setFacet(CreFields.ALL_SYSTEM_NOT_DETECTED);
		creAssayHunter.hunt(params, results, SearchConstants.ALL_KEY);

		return results;
	}

}
