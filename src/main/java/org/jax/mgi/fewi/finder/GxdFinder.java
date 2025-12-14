package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.hunter.ESGxdAssayResultHunter;
import org.jax.mgi.fewi.hunter.ESGxdConsolidatedSampleHunter;
import org.jax.mgi.fewi.hunter.ESGxdDagEdgeHunter;
import org.jax.mgi.fewi.hunter.ESGxdImagePaneHunter;
import org.jax.mgi.fewi.hunter.ESGxdProfileMarkerHunter;
import org.jax.mgi.fewi.hunter.ESGxdResultHasImageHunter;
import org.jax.mgi.fewi.hunter.ESGxdResultHunter;
import org.jax.mgi.fewi.hunter.SolrGxdMatrixResultHunter;
import org.jax.mgi.fewi.hunter.SolrGxdResultHasImageHunter;
import org.jax.mgi.fewi.hunter.SolrGxdResultHunter;
import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.ESAggLongCount;
import org.jax.mgi.fewi.searchUtil.entities.ESAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdGeneMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdPhenoMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdRecombinaseMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrGxdEntity;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.DagEdgeFields;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * Centered around retrieving GXD data in various forms
 */
@Repository
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GxdFinder {

	private final Logger logger = LoggerFactory.getLogger(GxdFinder.class);

	@Autowired
	private ESGxdResultHunter esGxdResultHunter;

	@Autowired
	private ESGxdAssayResultHunter esGxdAssayResultHunter;

	@Autowired
	private ESGxdResultHasImageHunter esGxdResultHasImageHunter;

	@Autowired
	private ESGxdImagePaneHunter esGxdImagePaneHunter;
	
	@Autowired
	private ESGxdDagEdgeHunter esGxdDagEdgeHunter;	
	
	@Autowired
	public ESGxdProfileMarkerHunter esGxdProfileMarkerHunter;	

	@Autowired
	public ESGxdConsolidatedSampleHunter esGxdConsolidatedSampleHunter;	
	
	@Autowired
	private SolrGxdResultHunter gxdResultHunter;

	@Autowired
	private SolrGxdResultHasImageHunter gxdResultHasImageHunter;

	@Autowired
	private SolrGxdMatrixResultHunter gxdMatrixResultHunter;
/*
	@Autowired
	private SolrGxdRnaSeqHeatMapResultHunter gxdRnaSeqHeatMapResultHunter;

	@Autowired
	private SolrGxdProfileHunter gxdProfileHunter;

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
	private SolrGxdCoFacetHunter gxdCoFacetHunter;

	@Autowired
	private SolrGxdDoFacetHunter gxdDoFacetHunter;

	@Autowired
	private SolrGxdGoFacetHunter gxdGoFacetHunter;

	@Autowired
	private SolrGxdTmpLevelFacetHunter gxdTmpLevelFacetHunter;

	@Autowired
	private SolrGxdRnaSeqConsolidatedSampleHunter gxdRnaSeqConsolidatedSampleHunter;
*/
	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getMarkerCount(SearchParams params) {
		int total = esGxdResultHunter.huntExactUniqueCount(params, GxdResultFields.MARKER_KEY);
		logger.info("gxd finder marker count =" + total);
		return total;
	}

	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayCount(SearchParams params) {
		int total = esGxdResultHunter.huntExactUniqueCount(params, GxdResultFields.ASSAY_KEY);
		logger.info("gxd finder assay count =" + total);
		return total;
	}

	/*
	 * Only does the Solr query to return the total document (or group) count
	 */
	public Integer getAssayResultCount(SearchParams params) {
		int total = esGxdResultHunter.huntCount(params);
		logger.info("gxd finder assay result count =" + total);
		return total;
	}

	public Integer getImageCount(SearchParams params) {
		int total = esGxdResultHasImageHunter.huntExactUniqueCount(params, ImagePaneFields.IMAGE_PANE_KEY);
		logger.info("gxd finder assay count =" + total);
		return total;
	}

	public SearchResults<ESAssayResult> searchAssayResults(SearchParams params) {
		SearchResults<ESAssayResult> results = new SearchResults<ESAssayResult>();
		esGxdAssayResultHunter.huntDocs(params, results, ESAssayResult.RETURN_FIELDS);

		// get total count
		results.setTotalCount(esGxdAssayResultHunter.huntCount(params));
		SearchResults<ESAssayResult> srAR = new SearchResults<ESAssayResult>();
		srAR.cloneFrom(results, ESAssayResult.class);
		return srAR;
	}

	public SearchResults<ESGxdAssay> searchAssays(SearchParams params) {
		SearchResults<ESGxdAssay> results = new SearchResults<ESGxdAssay>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.ASSAY_KEY, ESGxdAssay.RETURN_FIELDS);

		results.setTotalCount(esGxdResultHunter.huntExactUniqueCount(params, GxdResultFields.ASSAY_KEY));
		SearchResults<ESGxdAssay> srGA = new SearchResults<ESGxdAssay>();
		srGA.cloneFrom(results, ESGxdAssay.class);
		for (ESGxdAssay result : srGA.getResultObjects()) {
			if ("RNA-Seq".equals(result.getAssayType())) {
				result.setMarkerSymbol("Whole Genome");
			}
		}
		return srGA;
	}

	public SearchResults<ESGxdMarker> searchMarkers(SearchParams params) {	
		SearchResults<ESGxdMarker> results = new SearchResults<ESGxdMarker>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.MARKER_KEY, ESGxdMarker.RETURN_FIELDS);

		results.setTotalCount(esGxdResultHunter.huntExactUniqueCount(params, GxdResultFields.MARKER_KEY));
		SearchResults<ESGxdMarker> srGM = new SearchResults<ESGxdMarker>();
		srGM.cloneFrom(results, ESGxdMarker.class);
		return srGM;
	}

	public SearchResults<SolrString> searchStructureIds(SearchParams params) {
		SearchResults<ESEntity> results = new SearchResults<ESEntity>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.STRUCTURE_EXACT,
				List.of(GxdResultFields.STRUCTURE_EXACT));

		SearchResults<SolrString> srGM = new SearchResults<SolrString>();
		srGM.cloneFrom(results, SolrString.class);
		return srGM;
	}

	public SearchResults<SolrString> searchStagesInMatrix(SearchParams params) {
		SearchResults<ESEntity> results = new SearchResults<ESEntity>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.THEILER_STAGE,
				List.of(GxdResultFields.THEILER_STAGE));

		SearchResults<SolrString> srGM = new SearchResults<SolrString>();
		srGM.cloneFrom(results, SolrString.class);
		return srGM;
	}

	public SearchResults<ESGxdImage> searchImages(SearchParams params) {
		SearchParams resultImageParams = new SearchParams();
		resultImageParams.setFilter(params.getFilter());		
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		esGxdResultHasImageHunter.huntGroupInfo(resultImageParams, searchResults, ImagePaneFields.IMAGE_PANE_KEY);

		SearchParams imagePaneParams = new SearchParams();
		imagePaneParams.setSorts(params.getSorts());
		imagePaneParams.setStartIndex(params.getStartIndex());
		imagePaneParams.setPageSize(params.getPageSize());
		List<ESEntity> resultObjects = searchResults.getResultObjects();
		List<String> imagePaneKeys = new ArrayList<String>(resultObjects.size());
		for (Object obj : resultObjects) {
			ESAggLongCount row = (ESAggLongCount) obj;
			imagePaneKeys.add(row.getKey() + "");
			if ( imagePaneKeys.size() > SearchConstants.SEARCH_MAX_POST_SIZE ) {  //Hongping es will hang if too big
				break;
			}
		}
		imagePaneParams.setFilter(Filter.in(ImagePaneFields.IMAGE_PANE_KEY, imagePaneKeys));
		SearchResults<ESGxdImage> imageResult = new SearchResults<ESGxdImage>();
		ESSearchOption searchOption2 = new ESSearchOption();
		esGxdImagePaneHunter.hunt(imagePaneParams, imageResult, searchOption2);
		
		imageResult.setTotalCount(esGxdResultHasImageHunter.huntExactUniqueCount(params, ImagePaneFields.IMAGE_PANE_KEY));
		return imageResult;
	}

	public SearchResults<ESGxdMarker> searchBatchMarkerIDs(SearchParams params) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(GxdResultFields.MARKER_KEY);
		SearchResults<ESEntity> results = new SearchResults<ESEntity>();
		esGxdResultHunter.hunt(params, results, searchOption);

		SearchResults<ESGxdMarker> srGM = new SearchResults<ESGxdMarker>();
		srGM.cloneFrom(results, ESGxdMarker.class);
		return srGM;
	}
	
	/*
	 * get RNA-Seq results for heat map count
	 */
	public SearchResults<SolrGxdRnaSeqHeatMapResult> searchRnaSeqHeatMapResultsCount(SearchParams params) {
		SearchResults<SolrGxdRnaSeqHeatMapResult> results = new SearchResults<SolrGxdRnaSeqHeatMapResult>();
		long start = System.currentTimeMillis();
		int total = esGxdResultHunter.huntCount(params);
		out(start, "searchRnaSeqHeatMapResultsCount");
		results.setTotalCount(total);
		return results;
	}

	/*
	 * get RNA-Seq results for heat map
	 */
	public SearchResults<SolrGxdRnaSeqHeatMapResult> searchRnaSeqHeatMapResults(SearchParams params) {
		SearchResults<ESGxdRnaSeqHeatMapResult> results = new SearchResults<ESGxdRnaSeqHeatMapResult>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setReturnFields(ESGxdRnaSeqHeatMapResult.RETURN_FIELDS);
		searchOption.setClazz(ESGxdRnaSeqHeatMapResult.class);
		long start = System.currentTimeMillis();
		esGxdResultHunter.hunt(params, results, searchOption);
		out(start, "searchRnaSeqHeatMapResults");

		String wildType = "wild-type";
		List<SolrGxdRnaSeqHeatMapResult> solrResults = new ArrayList<SolrGxdRnaSeqHeatMapResult>();
		if (results.getResultObjects() != null) {
			for (ESGxdRnaSeqHeatMapResult esResult : results.getResultObjects()) {
				SolrGxdRnaSeqHeatMapResult solrResult = new SolrGxdRnaSeqHeatMapResult();
				solrResult.setStructureID(esResult.getStructureExact());
				solrResult.setTheilerStage(esResult.getTheilerStage());
				solrResult.setStrain(esResult.getStrain());
				
				String genotype = esResult.getGenotype();
				 if (genotype == null) {
					 genotype = wildType;
				 } else {
					 genotype = FormatHelper.stripAlleleTags(genotype).replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				 }				
				solrResult.setAlleles(genotype);
				
				solrResult.setAssayMgiID(esResult.getAssayMgiid());
				solrResult.setAge(esResult.getAge());
				solrResult.setStructure(esResult.getPrintname());
				solrResult.setConsolidatedSampleKey(esResult.getConsolidatedSampleKey());
				solrResult.setSex(esResult.getSex());
				solrResult.setMarkerSymbol(esResult.getMarkerSymbol());
				solrResult.setMarkerMgiID(esResult.getMarkerMgiid());
				solrResult.setMarkerSymbol(esResult.getMarkerSymbol());
				solrResult.setMarkerEnsemblGeneModelID(esResult.getEnsemblGeneModelID());
				solrResult.setAvergageQNTPM(esResult.getAvgQnTpmLevel());
				solrResults.add(solrResult);				
			}
		}
		
		SearchResults<SolrGxdRnaSeqHeatMapResult> solrResult = new SearchResults<SolrGxdRnaSeqHeatMapResult>();
		solrResult.setResultObjects(solrResults);
		solrResult.setTotalCount(results.getTotalCount());
		
		SearchResults<SolrGxdRnaSeqHeatMapResult> srMR = new SearchResults<SolrGxdRnaSeqHeatMapResult>();
		srMR.cloneFrom(solrResult, SolrGxdRnaSeqHeatMapResult.class);
		return srMR;
	}

	/*
	 * get consolidated samples for heat map (returns all of them)
	 */
	public SearchResults<SolrGxdRnaSeqConsolidatedSample> searchRnaSeqConsolidatedSamples(Set<String> sampleIDs) {
		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(10000));

		List<Filter> clauses = new ArrayList<Filter>(sampleIDs.size());
		for (String sampleID : sampleIDs) {
			clauses.add(new Filter(GxdResultFields.CONSOLIDATED_SAMPLE_KEY, sampleID));
		}
		params.setFilter(Filter.or(clauses));
		
		SearchResults<ESGxdRnaSeqConsolidatedSample> results = new SearchResults<ESGxdRnaSeqConsolidatedSample>();
		esGxdConsolidatedSampleHunter.huntDocs(params, results, ESGxdRnaSeqConsolidatedSample.RETURN_FIELDS);
		String wildType = "wild-type";
		
		List<SolrGxdRnaSeqConsolidatedSample> solrResults = new ArrayList<SolrGxdRnaSeqConsolidatedSample>();
		if (results.getResultObjects() != null) {
			for (ESGxdRnaSeqConsolidatedSample esResult : results.getResultObjects()) {
				SolrGxdRnaSeqConsolidatedSample solrResult = new SolrGxdRnaSeqConsolidatedSample();
				
				solrResult.setStructureID(esResult.getStructureExact());
				solrResult.setTheilerStage(esResult.getTheilerStage());
				solrResult.setStrain(esResult.getStrain());
				
				String genotype = esResult.getGenotype();
				 if (genotype == null) {
					 genotype = wildType;
				 } else {
					 genotype = FormatHelper.stripAlleleTags(genotype).replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				 }				
				solrResult.setAlleles(genotype);
				
				solrResult.setAssayMgiID(esResult.getAssayMgiid());
				solrResult.setAge(esResult.getAge());
				solrResult.setStructure(esResult.getPrintname());
				solrResult.setConsolidatedSampleKey(esResult.getConsolidatedSampleKey());
				solrResult.setSex(esResult.getSex());
				solrResult.setBioreplicateCount( esResult.getBioreplicateCount() );
				solrResults.add(solrResult);				
			}
		}
		
		SearchResults<SolrGxdRnaSeqConsolidatedSample> solrResult = new SearchResults<SolrGxdRnaSeqConsolidatedSample>();
		solrResult.setResultObjects(solrResults);
		solrResult.setTotalCount(results.getTotalCount());
		
		SearchResults<SolrGxdRnaSeqConsolidatedSample> srMR = new SearchResults<SolrGxdRnaSeqConsolidatedSample>();
		srMR.cloneFrom(solrResult, SolrGxdRnaSeqConsolidatedSample.class);	
		return srMR;
	}

	/**
	 * Queries for data for the tissue matrices
	 */
	/*
	 * Do not group, just return all matrix results for the given query
	 */	
	public SearchResults<ESGxdMatrixResult> searchMatrixResults(SearchParams params) {
		SearchResults<ESAssayResult> results = new SearchResults<ESAssayResult>();
		esGxdAssayResultHunter.huntDocs(params, results, ESAssayResult.RETURN_FIELDS);

		SearchResults<ESGxdMatrixResult> srMR = new SearchResults<ESGxdMatrixResult>();
		srMR.cloneFrom(results, ESGxdMatrixResult.class);
		return srMR;
	}

	/*
	 * get a flag to indicate whether the single matrix cell selected in 'params'
	 * has an associated image or not
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
		esGxdAssayResultHunter.hunt(params, results);
		
		return (results.getTotalCount() > 0);
	}	

	/*
	 * get the count of (distinct result_key or marker_key, specified by groupBy)
	 * having the specified value for detectionLevel for the stage by structure
	 * matrix (should be a single cell selected in 'params')
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
		results.setTotalCount(esGxdResultHunter.huntExactUniqueCount(params, groupBy));

		return results.getTotalCount();
	}	

	/*
	 * Group by the tissue x stage relevant fields
	 */
	public SearchResults<ESGxdStageMatrixResult> searchStageMatrixResults(SearchParams params) {
		SearchResults<ESGxdStageMatrixResult> results = new SearchResults<ESGxdStageMatrixResult>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.STAGE_MATRIX_GROUP,
				ESGxdStageMatrixResult.RETURN_FIELDS);

		SearchResults<ESGxdStageMatrixResult> srMR = new SearchResults<ESGxdStageMatrixResult>();
		srMR.cloneFrom(results, ESGxdStageMatrixResult.class);
		return srMR;
	}

	/*
	 * Group by the tissue x gene relevant fields for the recombinase grid
	 */
	public SearchResults<ESGxdRecombinaseMatrixResult> searchRecombinaseMatrixResults(SearchParams params) {
		SearchResults<ESGxdRecombinaseMatrixResult> results = new SearchResults<ESGxdRecombinaseMatrixResult>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.GENE_MATRIX_GROUP,
				ESGxdGeneMatrixResult.RETURN_FIELDS);			

		SearchResults<ESGxdRecombinaseMatrixResult> srMR = new SearchResults<ESGxdRecombinaseMatrixResult>();
		srMR.cloneFrom(results, ESGxdRecombinaseMatrixResult.class);

		// Default behavior of cloneFrom is giving a list that's still underlying as
		// SolrGxdGeneMatrixResult objects.
		// Need to clean it up...

		List<ESGxdRecombinaseMatrixResult> phenoResults = new ArrayList<ESGxdRecombinaseMatrixResult>();
		for (SolrGxdEntity geneResult : results.getResultObjects()) {
			phenoResults.add(new ESGxdRecombinaseMatrixResult((ESGxdGeneMatrixResult) geneResult));
		}
		srMR.setResultObjects(phenoResults);
		return srMR;
	}

	/*
	 * Group by the tissue x gene relevant fields for the pheno grid
	 */
	public SearchResults<ESGxdPhenoMatrixResult> searchPhenoMatrixResults(SearchParams params) {
		SearchResults<ESGxdPhenoMatrixResult> results = new SearchResults<ESGxdPhenoMatrixResult>();
		esGxdResultHunter.huntGroupFirstDoc(params, results, GxdResultFields.STAGE_MATRIX_GROUP,
				ESGxdGeneMatrixResult.RETURN_FIELDS);			

		SearchResults<ESGxdPhenoMatrixResult> srMR = new SearchResults<ESGxdPhenoMatrixResult>();
		srMR.cloneFrom(results, ESGxdPhenoMatrixResult.class);

		// Default behavior of cloneFrom is giving a list that's still underlying as
		// SolrGxdGeneMatrixResult objects.
		// Gotta clean it up...

		List<ESGxdPhenoMatrixResult> phenoResults = new ArrayList<ESGxdPhenoMatrixResult>();
		for (SolrGxdEntity geneResult : results.getResultObjects()) {
			phenoResults.add(new ESGxdPhenoMatrixResult((ESGxdGeneMatrixResult) geneResult));
		}
		srMR.setResultObjects(phenoResults);
		return srMR;
	}

	/*
	 * Group by the tissue x gene relevant fields
	 */	
	public SearchResults<ESGxdGeneMatrixResult> searchGeneMatrixResults(SearchParams params) {
		SearchResults<ESGxdGeneMatrixResult> result = new SearchResults<ESGxdGeneMatrixResult>();
		esGxdResultHunter.huntGroupFirstDoc(params, result, GxdResultFields.GENE_MATRIX_GROUP,
				ESGxdGeneMatrixResult.RETURN_FIELDS);
		
		SearchResults<ESGxdGeneMatrixResult> srMR = new SearchResults<ESGxdGeneMatrixResult>();
		srMR.cloneFrom(result, ESGxdGeneMatrixResult.class);
		return srMR;
	}

	public SearchResults<ESDagEdge> searchMatrixDAGDirectEdges(SearchParams params, List<String> parentTermIds) {
		SearchResults<ESAssayResult> assayResult = new SearchResults<ESAssayResult>();
		esGxdResultHunter.huntDocs(params, assayResult, List.of(GxdResultFields.STRUCTURE_EXACT));
		Map<String, String> maps = new HashMap<String, String>();
		if ( assayResult.getResultObjects() != null ) {
			for (ESAssayResult r: assayResult.getResultObjects()) {
				maps.put(r.getStructureExact(), null);
			}
		}
		SearchParams dagParams = new SearchParams();
		dagParams.setStartIndex(params.getStartIndex());
		dagParams.setPageSize(params.getPageSize());		
		List<Filter> allfilters = new ArrayList<Filter>();
		allfilters.add(Filter.equal(DagEdgeFields.EDGE_TYPE, DagEdgeFields.DIRECT_EDGE_TYPE));		
		allfilters.add(Filter.in(DagEdgeFields.RELATED_DESCENDENT, new ArrayList<>(maps.keySet())));
		if (parentTermIds != null && parentTermIds.size() > 0) {
			allfilters.add(Filter.in(DagEdgeFields.PARENT_ID, parentTermIds));
		}
		dagParams.setFilter(Filter.and(allfilters));			

		SearchResults<ESDagEdge> results = new SearchResults<ESDagEdge>();
		esGxdDagEdgeHunter.hunt(params, results);
		
		SearchResults<ESDagEdge> srTC = new SearchResults<ESDagEdge>();
		srTC.cloneFrom(results, ESDagEdge.class);
		return srTC;
	}

	public SearchResults<ESDagEdge> searchMatrixDAGDescendentEdges(SearchParams params, List<String> parentTermIds) {
		long start;
		SearchResults<ESAssayResult> assayResult = new SearchResults<ESAssayResult>();
		start = System.currentTimeMillis();
		esGxdResultHunter.huntDocs(params, assayResult, List.of(GxdResultFields.EMAPS_ID));
		out(start, "RESULT");
		Map<String, String> maps = new HashMap<String, String>();
		if ( assayResult.getResultObjects() != null ) {
			for (ESAssayResult r: assayResult.getResultObjects()) {
				maps.put(r.getEmapsId(), null);
			}
		}
		SearchParams dagParams = new SearchParams();
		dagParams.setStartIndex(params.getStartIndex());
		dagParams.setPageSize(params.getPageSize());
		List<Filter> allfilters = new ArrayList<Filter>();
		allfilters.add(Filter.equal(DagEdgeFields.EDGE_TYPE, DagEdgeFields.DESCENDENT_EDGE_TYPE));		
		allfilters.add(Filter.in(GxdResultFields.EMAPS_ID, new ArrayList<>(maps.keySet())));
		if (parentTermIds != null && parentTermIds.size() > 0) {
			allfilters.add(Filter.in(DagEdgeFields.PARENT_ID, parentTermIds));
		}
		dagParams.setFilter(Filter.and(allfilters));		
		
		SearchResults<ESDagEdge> results = new SearchResults<ESDagEdge>();
		start = System.currentTimeMillis();
		esGxdDagEdgeHunter.hunt(dagParams, results);
		out(start, "DAG");
		SearchResults<ESDagEdge> srTC = new SearchResults<ESDagEdge>();
		srTC.cloneFrom(results, ESDagEdge.class);
		return srTC;
	}
	
	public void out(long start, String message) {
		long end = System.currentTimeMillis();
		logger.warn(message + ": " + (end - start) + " ms");
	}	

	public List<String> searchProfile(SearchParams params) {
		params.setPageSize(100000);
		// result object to be returned
		SearchResults<String> searchResults = new SearchResults<String>();
		// ask the hunter to identify which objects to return
		esGxdProfileMarkerHunter.hunt(params, searchResults);
		return searchResults.getResultKeys();
	}

	public SearchResults<SolrString> getSystemFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_SYSTEM_FACET);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getAssayTypeFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_ASSAY_TYPE_FACET);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getTmpLevelFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_TMPLEVEL_FACET);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getMarkerTypeFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_MARKER_TYPE_FACET);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getDetectedFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_DETECTED_FACET);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getWildtypeFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_WILDTYPE_FACET);
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getTheilerStageFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_THEILER_STAGE_FACET);		
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getMpFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_MP_FACET);		
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getDoFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_DO_FACET);		
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getCoFacet(SearchParams params) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_CO_FACET);		
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

	public SearchResults<SolrString> getGoFacet(SearchParams params, String dag) {
		SearchResults<SolrGxdEntity> results = new SearchResults<SolrGxdEntity>();
		esGxdResultHunter.huntFacets(params, results, IndexConstants.GXD_DO_FACET);				
		if ("BP".equals(dag)) {
			esGxdResultHunter.huntFacets(params, results, GxdResultFields.GO_HEADERS_BP);
		} else if ("CC".equals(dag)) {
			esGxdResultHunter.huntFacets(params, results, GxdResultFields.GO_HEADERS_CC);
		} else {
			esGxdResultHunter.huntFacets(params, results, GxdResultFields.GO_HEADERS_MF);
		}
		SearchResults<SolrString> srSS = new SearchResults<SolrString>();
		srSS.cloneFrom(results, SolrString.class);
		return srSS;
	}

}
