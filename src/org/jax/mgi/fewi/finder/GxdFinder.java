package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.fewi.hunter.SolrGxdAssayTypeFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdMarkerTypeFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdMpFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdDoFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdGoFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdTmpLevelFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdDetectedFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdDifferentialHunter;
import org.jax.mgi.fewi.hunter.SolrGxdMatrixResultHunter;
import org.jax.mgi.fewi.hunter.SolrGxdResultHunter;
import org.jax.mgi.fewi.hunter.SolrGxdRnaSeqConsolidatedSampleHunter;
import org.jax.mgi.fewi.hunter.SolrGxdRnaSeqHeatMapResultHunter;
import org.jax.mgi.fewi.hunter.SolrGxdResultHasImageHunter;
import org.jax.mgi.fewi.hunter.SolrGxdSystemFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdTheilerStageFacetHunter;
import org.jax.mgi.fewi.hunter.SolrGxdWildtypeFacetHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdGeneMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdPhenoMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRecombinaseMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.shr.fe.indexconstants.DagEdgeFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * Centered around retrieving GXD data in various forms
 */
@Repository
public class GxdFinder {

	private final Logger logger = LoggerFactory.getLogger(GxdFinder.class);

	@Autowired
	private SolrGxdResultHunter gxdResultHunter;

	@Autowired
	private SolrGxdResultHasImageHunter gxdResultHasImageHunter;

	@Autowired
	private SolrGxdMatrixResultHunter gxdMatrixResultHunter;

	@Autowired
	private SolrGxdRnaSeqHeatMapResultHunter gxdRnaSeqHeatMapResultHunter;

	@Autowired
	private SolrGxdDifferentialHunter gxdDifferentialHunter;

	@Autowired
	private SolrGxdSystemFacetHunter gxdSystemFacetHunter;

	@Autowired
	private SolrGxdAssayTypeFacetHunter gxdAssayTypeFacetHunter;

	@Autowired
	private SolrGxdMarkerTypeFacetHunter gxdMarkerTypeFacetHunter;

	@Autowired
	private SolrGxdDetectedFacetHunter gxdDetectedFacetHunter;

	@Autowired
	private SolrGxdTheilerStageFacetHunter gxdTheilerStageFacetHunter;

	@Autowired
	private SolrGxdWildtypeFacetHunter gxdWildtypeFacetHunter;

	@Autowired
	private SolrGxdMpFacetHunter gxdMpFacetHunter;
		
	@Autowired
	private SolrGxdDoFacetHunter gxdDoFacetHunter;

	@Autowired
	private SolrGxdGoFacetHunter gxdGoFacetHunter;

	@Autowired
	private SolrGxdTmpLevelFacetHunter gxdTmpLevelFacetHunter;
	
	@Autowired
	private SolrGxdRnaSeqConsolidatedSampleHunter gxdRnaSeqConsolidatedSampleHunter;
	
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getMarkerCount(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.MRK_KEY);
		logger.debug("gxd finder marker count =" + results.getTotalCount());
		return results.getTotalCount();
	}

	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayCount(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.GXD_ASSAY_KEY);
		logger.debug("gxd finder assay count =" + results.getTotalCount());
		return results.getTotalCount();
	}

	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayResultCount(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results);
		logger.debug("gxd finder assay result count ="
				+ results.getTotalCount());
		return results.getTotalCount();
	}

	public Integer getImageCount(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHasImageHunter.joinHunt(params, results, "gxdImagePane");
		logger.debug("gxd finder image count =" + results.getTotalCount());
		return results.getTotalCount();
	}

	public SearchResults<SolrAssayResult> searchAssayResults(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results);

		SearchResults<SolrAssayResult> srAR = new SearchResults<SolrAssayResult>();
		srAR.cloneFrom(results, SolrAssayResult.class);
		return srAR;
	}

	public SearchResults<SolrGxdAssay> searchAssays(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.GXD_ASSAY_KEY);

		SearchResults<SolrGxdAssay> srGA = new SearchResults<SolrGxdAssay>();
		srGA.cloneFrom(results, SolrGxdAssay.class);

		for (SolrGxdAssay result : srGA.getResultObjects()) {
			if ("RNA-Seq".equals(result.getAssayType())) {
				result.setMarkerSymbol("Whole Genome");
			}
		}
		return srGA;
	}

	public SearchResults<SolrGxdMarker> searchMarkers(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.MRK_KEY);

		SearchResults<SolrGxdMarker> srGM = new SearchResults<SolrGxdMarker>();
		srGM.cloneFrom(results, SolrGxdMarker.class);
		return srGM;
	}

	public SearchResults<SolrString> searchStructureIds(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.STRUCTURE_EXACT);

		SearchResults<SolrString> srGM = new SearchResults<SolrString>();
		srGM.cloneFrom(results, SolrString.class);
		return srGM;
	}

	public SearchResults<SolrString> searchStagesInMatrix(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter
				.hunt(params, results, SearchConstants.GXD_THEILER_STAGE);

		SearchResults<SolrString> srGM = new SearchResults<SolrString>();
		srGM.cloneFrom(results, SolrString.class);
		return srGM;
	}

	public SearchResults<SolrGxdImage> searchImages(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHasImageHunter.joinHunt(params, results, "gxdImagePane");

		SearchResults<SolrGxdImage> srGI = new SearchResults<SolrGxdImage>();
		srGI.cloneFrom(results, SolrGxdImage.class);
		return srGI;
	}

	public SearchResults<SolrGxdMarker> searchBatchMarkerIDs(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdResultHunter.hunt(params, results, SearchConstants.MRK_ID);

		SearchResults<SolrGxdMarker> srGM = new SearchResults<SolrGxdMarker>();
		srGM.cloneFrom(results, SolrGxdMarker.class);
		return srGM;
	}

	/* get RNA-Seq results for heat map
	 */
	public SearchResults<SolrGxdRnaSeqHeatMapResult> searchRnaSeqHeatMapResults(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdRnaSeqHeatMapResultHunter.hunt(params, results);

		SearchResults<SolrGxdRnaSeqHeatMapResult> srMR = new SearchResults<SolrGxdRnaSeqHeatMapResult>();
		srMR.cloneFrom(results, SolrGxdRnaSeqHeatMapResult.class);
		return srMR;
	}

	/* get consolidated samples for heat map (returns all of them)
	 */
	public SearchResults<SolrGxdRnaSeqConsolidatedSample> searchRnaSeqConsolidatedSamples(Set<String> sampleIDs) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(10000));
		
		List<Filter> clauses = new ArrayList<Filter>(sampleIDs.size());
		for (String sampleID : sampleIDs) {
			clauses.add(new Filter(GxdResultFields.CONSOLIDATED_SAMPLE_KEY, sampleID));
		}
		params.setFilter(Filter.or(clauses));

		gxdRnaSeqConsolidatedSampleHunter.hunt(params, results);

		SearchResults<SolrGxdRnaSeqConsolidatedSample> srMR = new SearchResults<SolrGxdRnaSeqConsolidatedSample>();
		srMR.cloneFrom(results, SolrGxdRnaSeqConsolidatedSample.class);
		return srMR;
	}

	/**
	 * Queries for data for the tissue matrices
	 */
	/*
	 * Do not group, just return all matrix results for the given query
	 */
	public SearchResults<SolrGxdMatrixResult> searchMatrixResults(
			SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results);

		SearchResults<SolrGxdMatrixResult> srMR = new SearchResults<SolrGxdMatrixResult>();
		srMR.cloneFrom(results, SolrGxdMatrixResult.class);
		return srMR;
	}
	
	/* get a flag to indicate whether the single matrix cell selected in
	 * 'params' has an associated image or not
	 */
	public boolean getImageFlagForMatrixPopup(SearchParams p) {
		// We only need a single results returned, just to show a match.
		Paginator page = new Paginator(1);

		// Get a copy of the parameters we can tweak.
		SearchParams params = SearchParams.copy(p);
		params.setPaginator(page);

		List<Filter> combo = new ArrayList<Filter>();
		combo.add(params.getFilter());
		combo.add(new Filter(SearchConstants.HAS_IMAGE, "true", Filter.Operator.OP_EQUAL));
		params.setFilter(Filter.and(combo));

		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results);

		return (results.getTotalCount() > 0);
	}

	/* get the count of (distinct result_key or marker_key, specified by
	 * groupBy) having the specified value for detectionLevel for the stage
	 * by structure matrix (should be a single cell selected in 'params')
	 */
	public Integer getCountForMatrixPopup(SearchParams p, String detectionLevel, String groupBy, String emapaID) {
		// We don't actually need any results returned, just a count.
		Paginator page = new Paginator(0);

		// Get a copy of the parameters we can tweak.
		SearchParams params = SearchParams.copy(p);
		params.setPaginator(page);

		List<Filter> combo = new ArrayList<Filter>();
		combo.add(params.getFilter());

		// For "Yes" and "No" annotations, we search for them directly.
		// For "Ambiguous", we need to look for either "Ambiguous" or "Not Specified".
		if (!"Ambiguous".equals(detectionLevel)) {
			combo.add(new Filter(SearchConstants.GXD_DETECTED, detectionLevel, Filter.Operator.OP_EQUAL));
		} else {
			List<Filter> eitherOr = new ArrayList<Filter>();
			eitherOr.add(new Filter(SearchConstants.GXD_DETECTED, detectionLevel, Filter.Operator.OP_EQUAL));
			eitherOr.add(new Filter(SearchConstants.GXD_DETECTED, "Not Specified", Filter.Operator.OP_EQUAL));
			combo.add(Filter.or(eitherOr));
		}

		// Only "yes" annotations percolate upward; the other detection
		// levels are only for the exact term.
		if (!"Yes".equals(detectionLevel)) {
			combo.add(new Filter(SearchConstants.STRUCTURE_EXACT, emapaID, Filter.Operator.OP_EQUAL));
		}

		params.setFilter(Filter.and(combo));

		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results, groupBy);

		return results.getTotalCount();
	}

	/*
	 * Group by the tissue x stage relevant fields
	 */
	public SearchResults<SolrGxdStageMatrixResult> searchStageMatrixResults(
			SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results,
				SearchConstants.STAGE_MATRIX_GROUP);

		SearchResults<SolrGxdStageMatrixResult> srMR = new SearchResults<SolrGxdStageMatrixResult>();
		srMR.cloneFrom(results, SolrGxdStageMatrixResult.class);
		return srMR;
	}
	
	/*
	 * Group by the tissue x gene relevant fields for the recombinase grid
	 */
	public SearchResults<SolrGxdRecombinaseMatrixResult> searchRecombinaseMatrixResults(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results, SearchConstants.GENE_MATRIX_GROUP);

		SearchResults<SolrGxdRecombinaseMatrixResult> srMR = new SearchResults<SolrGxdRecombinaseMatrixResult>();
		srMR.cloneFrom(results, SolrGxdRecombinaseMatrixResult.class);
		
		// Default behavior of cloneFrom is giving a list that's still underlying as SolrGxdGeneMatrixResult objects.
		// Need to clean it up...
		
		List<SolrGxdRecombinaseMatrixResult> phenoResults = new ArrayList<SolrGxdRecombinaseMatrixResult>();
		for (SolrGxdEntity geneResult : results.getResultObjects()) {
			phenoResults.add(new SolrGxdRecombinaseMatrixResult((SolrGxdGeneMatrixResult) geneResult));
		}
		srMR.setResultObjects(phenoResults);
		return srMR;
	}

	/*
	 * Group by the tissue x gene relevant fields for the pheno grid
	 */
	public SearchResults<SolrGxdPhenoMatrixResult> searchPhenoMatrixResults(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results, SearchConstants.GENE_MATRIX_GROUP);

		SearchResults<SolrGxdPhenoMatrixResult> srMR = new SearchResults<SolrGxdPhenoMatrixResult>();
		srMR.cloneFrom(results, SolrGxdPhenoMatrixResult.class);
		
		// Default behavior of cloneFrom is giving a list that's still underlying as SolrGxdGeneMatrixResult objects.
		// Gotta clean it up...
		
		List<SolrGxdPhenoMatrixResult> phenoResults = new ArrayList<SolrGxdPhenoMatrixResult>();
		for (SolrGxdEntity geneResult : results.getResultObjects()) {
			phenoResults.add(new SolrGxdPhenoMatrixResult((SolrGxdGeneMatrixResult) geneResult));
		}
		srMR.setResultObjects(phenoResults);
		return srMR;
	}

	/*
	 * Group by the tissue x gene relevant fields
	 */
	public SearchResults<SolrGxdGeneMatrixResult> searchGeneMatrixResults(
			SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMatrixResultHunter.hunt(params, results,
				SearchConstants.GENE_MATRIX_GROUP);

		SearchResults<SolrGxdGeneMatrixResult> srMR = new SearchResults<SolrGxdGeneMatrixResult>();
		srMR.cloneFrom(results, SolrGxdGeneMatrixResult.class);
		return srMR;
	}

	public SearchResults<SolrDagEdge> searchMatrixDAGDirectEdges(
			SearchParams params, List<String> parentTermIds) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		String parentIdFilter = null;
		if (parentTermIds != null && parentTermIds.size() > 0) {
			parentIdFilter = DagEdgeFields.PARENT_ID + ": (\""
					+ StringUtils.join(parentTermIds, "\" OR \"") + "\")";
		}
		gxdMatrixResultHunter.joinHunt(params, results, "dagDirectEdge",
				parentIdFilter);

		SearchResults<SolrDagEdge> srTC = new SearchResults<SolrDagEdge>();
		srTC.cloneFrom(results, SolrDagEdge.class);
		return srTC;
	}

	public SearchResults<SolrDagEdge> searchMatrixDAGDescendentEdges(
			SearchParams params, List<String> parentTermIds) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		String parentIdFilter = null;
		if (parentTermIds != null && parentTermIds.size() > 0) {
			parentIdFilter = DagEdgeFields.PARENT_ID + ": (\""
					+ StringUtils.join(parentTermIds, "\" OR \"") + "\")";
		}
		gxdMatrixResultHunter.joinHunt(params, results, "dagDescendentEdge",
				parentIdFilter);

		SearchResults<SolrDagEdge> srTC = new SearchResults<SolrDagEdge>();
		srTC.cloneFrom(results, SolrDagEdge.class);
		return srTC;
	}

	/*
	 * Returns marker keys based on differential search
	 */
	public List<String> searchDifferential(SearchParams params) {
		params.setPageSize(100000);
		// result object to be returned
		SearchResults<String> searchResults = new SearchResults<String>();
		// ask the hunter to identify which objects to return
		gxdDifferentialHunter.hunt(params, searchResults);
		return searchResults.getResultKeys();
	}

	public SearchResults<SolrString> getSystemFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdSystemFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getAssayTypeFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdAssayTypeFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}
	
	public SearchResults<SolrString> getTmpLevelFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdTmpLevelFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}
	
	public SearchResults<SolrString> getMarkerTypeFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMarkerTypeFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getDetectedFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdDetectedFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getWildtypeFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdWildtypeFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getTheilerStageFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdTheilerStageFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getMpFacet(SearchParams params) {
		logger.debug("Starting: getMpFacet");
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdMpFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getDoFacet(SearchParams params) {
		logger.debug("Starting: getDoFacet");
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		gxdDoFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getGoFacet(SearchParams params, String dag) {
		logger.debug("Starting: getGoFacet");
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		if ("BP".equals(dag)) {
			gxdGoFacetHunter.setFacetField(GxdResultFields.GO_HEADERS_BP);
		} else if ("CC".equals(dag)) {
			gxdGoFacetHunter.setFacetField(GxdResultFields.GO_HEADERS_CC);
		} else {
			gxdGoFacetHunter.setFacetField(GxdResultFields.GO_HEADERS_MF);
		}
		gxdGoFacetHunter.hunt(params, results);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}
	
	
	
}
