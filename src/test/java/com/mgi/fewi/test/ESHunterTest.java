package com.mgi.fewi.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.hunter.ESGxdConsolidatedSampleHunter;
import org.jax.mgi.fewi.hunter.ESGxdDagEdgeHunter;
import org.jax.mgi.fewi.hunter.ESGxdImagePaneHunter;
import org.jax.mgi.fewi.hunter.ESGxdProfileMarkerHunter;
import org.jax.mgi.fewi.hunter.ESGxdResultHasImageHunter;
import org.jax.mgi.fewi.hunter.ESGxdResultHunter;
import org.jax.mgi.fewi.searchUtil.ESLookup;
import org.jax.mgi.fewi.searchUtil.ESLookupIndex;
import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.entities.ESAggLongCount;
import org.jax.mgi.fewi.searchUtil.entities.ESAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.ESDagEdge;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdStageMatrixResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqConsolidatedSample;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdRnaSeqHeatMapResult;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.DagEdgeFields;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch._types.GeoShapeRelation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ESHunterTest {
	private static final Logger log = LoggerFactory.getLogger(ESHunterTest.class);

	private static final String ES_HOST = "bhmgigxdsolr01ld.jax.org";
//	private static final String ES_HOST = "34.74.34.59";
	private static final String ES_PORT = "9200";
//	private static final String ES_HOST = "localhost";
//	private static final String ES_HOST = "my-elasticsearch-project-e27cf3.es.us-east1.gcp.elastic.cloud";
//	private static final String ES_PORT = "443";	

	private ESGxdResultHunter esGxdResultHunter;
	private ESGxdResultHasImageHunter esGxdResultHasImageHunter;
	private ESGxdImagePaneHunter esGxdImagePaneHunter;
	private ESGxdProfileMarkerHunter esGxdProfileMarkerHunter;
	private ESGxdConsolidatedSampleHunter esGxdConsolidatedSampleHunter;

	@Before
	public void setUp() {
		log.info("ESHunterTest: " + ES_HOST + " " + ES_PORT);
		this.esGxdProfileMarkerHunter = new ESGxdProfileMarkerHunter(ESEntity.class, ES_HOST, ES_PORT,
				"gxd_profile_marker");
		this.esGxdResultHunter = new ESGxdResultHunter(ESAssayResult.class, ES_HOST, ES_PORT, "gxd_result");
		this.esGxdResultHunter.esGxdProfileMarkerHunter = this.esGxdProfileMarkerHunter;
		
		this.esGxdResultHasImageHunter = new ESGxdResultHasImageHunter(ESEntity.class, ES_HOST, ES_PORT,
				"gxd_result_has_image");
		this.esGxdResultHasImageHunter.esGxdProfileMarkerHunter = this.esGxdProfileMarkerHunter;

		this.esGxdImagePaneHunter = new ESGxdImagePaneHunter(ESGxdImage.class, ES_HOST, ES_PORT, "gxd_image_pane");
		this.esGxdConsolidatedSampleHunter = new ESGxdConsolidatedSampleHunter(SolrGxdRnaSeqConsolidatedSample.class,
				ES_HOST, ES_PORT, "gxd_consolidated_sample");
	}
	
	@Test
	public void testSearchDagEdge() {
		log.info("Test: testSearchDagEdge");
		
		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(10);
		List<Filter> filters = new ArrayList<Filter>();

		List<Filter> joinFilters = new ArrayList<Filter>();		
		joinFilters.add(Filter.in(GxdResultFields.PROF_POS_C_ANC_A, List.of("18242470")));   // brain detected
		joinFilters.add(Filter.notIn(GxdResultFields.PROF_POS_C_ANC_A, List.of("18239679")));  // liver not detected
		Filter joinQuery = Filter.and(joinFilters);
		filters.add(Filter.join("gxd_profile_marker", "markerMgiid", "markerMgiid", joinQuery));	

		filters.add(Filter.join("gxd_dag_edge", "emapsId", "emapsId", new Filter()));	
		
		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16894"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_EXACT, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));	
		
		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));

		searchParams.setFilter(Filter.and(filters));		
		
		SearchResults<ESDagEdgeJoin> assayResult = new SearchResults<ESDagEdgeJoin>();
		long start = System.currentTimeMillis();
		esGxdResultHunter.huntDocs(searchParams, assayResult, ESDagEdgeJoin.RETURN_FIELDS, true, ESDagEdgeJoin.class);
		ESSearchOption.logRunTime(log, start, "searchMatrixDAGDescendentEdges_result");

		List<ESDagEdgeJoin> resultObjects = assayResult.getResultObjects();
		log.info("resultObjects size: " + resultObjects.size());
		show(assayResult.getResultObjects(), 5);
		log.info("\n");	
	}	
	
	@Test
	public void testLookupJoinThreeIndex() {
		log.info("Test: testLookupJoinThreeIndex");

		ESLookup lookUpJoin = new ESLookup("gxd_result");

		List<ESLookupIndex> lookupIndexes = new ArrayList<ESLookupIndex>();
		lookupIndexes.add(new ESLookupIndex("gxd_profile_marker", GxdResultFields.MARKER_MGIID, false));
		lookupIndexes.add(new ESLookupIndex("gxd_dag_edge", GxdResultFields.EMAPS_ID, false));
		lookUpJoin.setLookupIndexes(lookupIndexes);

		List<String> extraStatements = new ArrayList<String>();
		
		List<String> keeps = new ArrayList<String>();
		keeps.add(GxdResultFields.MARKER_SYMBOL);
		keeps.addAll(ESDagEdgeJoin.RETURN_FIELDS);
		keeps.add(GxdResultFields.PROF_POS_R_EXACT);
		keeps.add(GxdResultFields.EMAPS_ID);
		keeps.add(GxdResultFields.ASSAY_MGIID);
		String keep = "KEEP ";
		keep = "GROUP BY ";
		int cnt = 0;
		for (String k: keeps) {
			if ( cnt > 0) {
				keep += ",";
		    }
			keep += k;
			cnt++;
		}
		extraStatements.add(keep);
//		extraStatements.add("WHERE " + DagEdgeFields.PARENT_ID + " IS NOT NULL");
//		extraStatements.add("WHERE " + GxdResultFields.EMAPS_ID + " IS NOT NULL");
		lookUpJoin.setExtraStatements(extraStatements);

		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(100);
		
		List<Filter> filters = new ArrayList<Filter>();

//		List<Filter> joinFilters = new ArrayList<Filter>();
//		joinFilters.add(Filter.in("posCAncA", List.of("18242470")));  // brain detected
//		joinFilters.add(Filter.notIn("posCAncA", List.of("18239679")));  // liver not detected
//		Filter joinQuery = Filter.and(joinFilters);
//		filters.add(Filter.join("gxdProfileMarker", "markerMgiid", "markerMgiid", joinQuery));	
		
//		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
//		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16894"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));	
		
		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));		
		
//		filters.add(Filter.equal(GxdResultFields.MARKER_SYMBOL, "Apob"));
//		String spatialQueryString = SolrLocationTranslator.getQueryValue("Chr12:3000000-10000000", "bp");
//		filters.add(new Filter(SearchConstants.MOUSE_COORDINATE, spatialQueryString, Filter.Operator.OP_SHAPE));
//		searchParams.setFilter(Filter.and(filters));

		SearchResults<ESDagEdge> searchResults = new SearchResults<ESDagEdge>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setEsQuery(lookUpJoin);
		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);

		List<ESDagEdge> resultObjects = searchResults.getResultObjects();
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("resultObjects size: " + resultObjects.size());
		show(resultObjects, 5);
		log.info("\n");
	}		

	@Test
	public void testSearchMatrixDAGDescendentEdges() {
		log.info("Test: testSearchMatrixDAGDescendentEdges");
		
		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(100000);
		List<Filter> filters = new ArrayList<Filter>();

		List<Filter> joinFilters = new ArrayList<Filter>();
		joinFilters.add(Filter.in("posCAncA", List.of("18242470")));  // brain detected
		joinFilters.add(Filter.notIn("posCAncA", List.of("18239679")));  // liver not detected
		Filter joinQuery = Filter.and(joinFilters);
		filters.add(Filter.join("gxdProfileMarker", "markerMgiid", "markerMgiid", joinQuery));	
		
		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16894"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));	
		
		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));

		searchParams.setFilter(Filter.and(filters));		
		
		SearchResults<ESAssayResult> assayResult = new SearchResults<ESAssayResult>();
		long start = System.currentTimeMillis();
		esGxdResultHunter.huntDocsScroll(searchParams, assayResult, List.of(GxdResultFields.EMAPS_ID));
		ESSearchOption.logRunTime(log, start, "searchMatrixDAGDescendentEdges_result");

		start = System.currentTimeMillis();
		esGxdResultHunter.huntDocs(searchParams, assayResult, List.of(GxdResultFields.EMAPS_ID));
		ESSearchOption.logRunTime(log, start, "searchMatrixDAGDescendentEdges_result");
		
		List<ESAssayResult> resultObjects = assayResult.getResultObjects();
		log.info("resultObjects size: " + resultObjects.size());
		show(assayResult.getResultObjects(), 5);
		log.info("\n");	
	}
	
	
	/*
	 * Test LOOKUP JOIN of gxd_dag_edge, gxd_result, gxd_marker_profile
	 */
	@Test
	public void testLookupJoinForGeneMatrix() {
		log.info("Test: testLookupJoinForGeneMatrix");
		
		SearchParams searchParams = new SearchParams();
		List<Filter> joinFilters = new ArrayList<Filter>();
		joinFilters.add(Filter.in("posCAncA", List.of("18239046", "18240789")));
		joinFilters.add(Filter.equal("posCExact", "115407125"));
		joinFilters.add(Filter.notIn("posCAncA", List.of("18239679")));
		Filter joinQuery = Filter.and(joinFilters);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.join("gxdProfileMarker", "markerMgiid", "markerMgiid", joinQuery));
		
		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16105"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));

		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));

		searchParams.setFilter(Filter.and(filters));

		SearchResults<ESGxdStageMatrixResult> results = new SearchResults<ESGxdStageMatrixResult>();
		esGxdResultHunter.huntGroupFirstDoc(searchParams, results, GxdResultFields.STAGE_MATRIX_GROUP,
				ESGxdStageMatrixResult.RETURN_FIELDS);


		List<ESGxdStageMatrixResult> resultObjects = results.getResultObjects();
		log.info("resultObjects size: " + resultObjects.size());
		show(results.getResultObjects(), 5);
		log.info("\n");
	}	
	
	@Test
	public void testConsolidateSamples() {
		log.info("Test: testConsolidateSamples");
		SearchParams searchParams = new SearchParams();

		SearchResults<SolrGxdRnaSeqConsolidatedSample> results = new SearchResults<SolrGxdRnaSeqConsolidatedSample>();
		esGxdConsolidatedSampleHunter.huntDocs(searchParams, results, SolrGxdRnaSeqConsolidatedSample.RETURN_FIELDS);

		show(results.getResultObjects(), 25);
		log.info("\n");
	}

	@Test
	public void testRNSSeqHeaMapResult() {
		log.info("Test: testRNSSeqHeaMapResult");
		SearchParams searchParams = new SearchParams();

		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setClazz(ESGxdRnaSeqHeatMapResult.class);
		searchOption.setReturnFields(ESGxdRnaSeqHeatMapResult.RETURN_FIELDS);

		SearchResults<ESGxdRnaSeqHeatMapResult> results = new SearchResults<ESGxdRnaSeqHeatMapResult>();
		esGxdResultHunter.hunt(searchParams, results, searchOption);

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
		
		show(results.getResultObjects(), 25);
		log.info("\n");
	}

	@Test
	public void testImageCount() {
		log.info("Test: testImageCount");
		SearchParams searchParams = new SearchParams();
//		long totalEstimate = this.esGxdResultHasImageHunter.huntEstimatedUniqueCount(searchParams,
//				ImagePaneFields.IMAGE_PANE_KEY, Optional.empty());
//		
//		
//		long total = this.esGxdResultHasImageHunter.huntExactUniqueCount(searchParams, ImagePaneFields.IMAGE_PANE_KEY);
//		Assert.assertTrue(total > 0);
//		log.info("images estimation total: " + totalEstimate + ", total: " + total);

		List<Filter> joinFilters = new ArrayList<Filter>();
		joinFilters.add(Filter.in("posCAncA", List.of("18239046", "18240789")));
		joinFilters.add(Filter.equal("posCExact", "115407125"));
		joinFilters.add(Filter.notIn("posCAncA", List.of("18239679")));
		Filter joinQuery = Filter.and(joinFilters);

		List<Filter> filters = new ArrayList<Filter>();

		filters.add(Filter.join("gxdProfileMarker", "markerMgiid", "markerMgiid", joinQuery));
		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16105"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));

		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));

		searchParams.setFilter(Filter.and(filters));
		long total = this.esGxdResultHasImageHunter.huntExactUniqueCount(searchParams, ImagePaneFields.IMAGE_PANE_KEY);
		log.info("images exact total: " + total);
		log.info("\n");
	}

	@Test
	public void testSearchMarker() {
		log.info("Test: testSearchMarker");
		SearchParams searchParams = new SearchParams();

		List<Filter> joinFilters = new ArrayList<Filter>();
		joinFilters.add(Filter.equal("posCAncA", "18239046"));
		joinFilters.add(Filter.notEqual("posCAncA", "18239679"));
		Filter joinQuery = Filter.and(joinFilters);

		List<Filter> filters = new ArrayList<Filter>();

		filters.add(Filter.join("gxdProfileMarker", "markerMgiid", "markerMgiid", joinQuery));
		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16105"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));

		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));

		searchParams.setFilter(Filter.and(filters));

		Sort sort = new Sort(GxdResultFields.M_BY_MRK_SYMBOL);
		searchParams.addSort(sort);

		SearchResults<ESGxdMarker> results = new SearchResults<ESGxdMarker>();
		esGxdResultHunter.huntGroupFirstDoc(searchParams, results, GxdResultFields.MARKER_KEY,
				ESGxdMarker.RETURN_FIELDS);

		show(results.getResultObjects(), 25);
		log.info("\n");
	}

	@Test
	public void testGetProfileMarker() {
		log.info("Test: testGetProfileMarker");
		SearchParams searchParams = new SearchParams();
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.equal("posRAncA", "18239165"));
		searchParams.setFilter(Filter.and(filters));

		SearchResults<ESAssayResult> searchResults = new SearchResults<ESAssayResult>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setClazz(ESAssayResult.class);
		searchOption.setReturnFields(List.of(GxdResultFields.MARKER_MGIID));
		this.esGxdProfileMarkerHunter.hunt(searchParams, searchResults, searchOption);
		List<ESAssayResult> resultObjects = searchResults.getResultObjects();

		List<String> mgiidFilters = new ArrayList<String>();
		for (ESAssayResult r : resultObjects) {
			mgiidFilters.add(r.getMarkerMgiid());
		}

		List<Filter> allfilters = new ArrayList<Filter>();
		allfilters.add(searchParams.getFilter());
		allfilters.add(Filter.in(GxdResultFields.MARKER_MGIID, mgiidFilters));
		searchParams.setFilter(Filter.and(allfilters));

		ESSearchOption s = new ESSearchOption();
		s.setGroupField(GxdResultFields.MARKER_KEY);
		s.setGetTotalCount(true);
		s.setCountNumOfBuckts(true);
		SearchResults<ESEntity> results = new SearchResults<ESEntity>();
		esGxdResultHunter.hunt(searchParams, results, s);
		log.info("gxd finder marker count =" + results.getTotalCount());

		show(resultObjects, 5);
		log.info("\n");
	}

	/*
	 * Test search with a single parameter
	 */
	@Test
	public void testSimpleSearch() {
		log.info("Test: testSearchByParameter");
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "apob"));
		SearchResults<ESAssayResult> searchResults = new SearchResults<ESAssayResult>();

		this.esGxdResultHunter.huntDocs(searchParams, searchResults, ESAssayResult.RETURN_FIELDS);

		List<ESAssayResult> resultObjects = searchResults.getResultObjects();
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("resultObjects size: " + resultObjects.size());
		show(resultObjects, 5);
		log.info("\n");
	}

	/*
	 * Test search with multiple parameters, with start index, and size
	 */
	@Test
	public void testSearchByMultiParameters() {
		log.info("Test: testSearchByMultiParameters");
		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(5);
		searchParams.setStartIndex(2);
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));
		filters.add(Filter.equal(GxdResultFields.ASSAY_TYPE, "Western blot"));
		String spatialQueryString = SolrLocationTranslator.getQueryValue("Chr12:3000000-10000000", "bp");
		filters.add(new Filter(SearchConstants.MOUSE_COORDINATE, spatialQueryString, Filter.Operator.OP_SHAPE));
		searchParams.setFilter(Filter.and(filters));

		SearchResults<ESAssayResult> searchResults = new SearchResults<ESAssayResult>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setReturnFields(ESAssayResult.RETURN_FIELDS);
		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);
		List<ESAssayResult> resultObjects = searchResults.getResultObjects();
		Assert.assertEquals(searchParams.getPageSize(), resultObjects.size());
		log.info("resultObjects size: " + resultObjects.size() + ", pageSize: " + searchParams.getPageSize());
		show(resultObjects, 5);
		log.info("\n");
	}

	@Test
	public void testAggregationBy() {
		log.info("Test: testAggregationBy");
		SearchParams searchParams = new SearchParams();
		// searchParams.setFilter(Filter.equal(GxdResultFields.ASSAY_TYPE, "Western
		// blot"));

		List<Filter> joinFilters = new ArrayList<Filter>();
		joinFilters.add(Filter.equal("posCAncA", "18239046"));
		joinFilters.add(Filter.notEqual("posCAncA", "18239679"));
		Filter joinQuery = Filter.and(joinFilters);

		List<Filter> filters = new ArrayList<Filter>();

		filters.add(Filter.join("gxdProfileMarker", "markerMgiid", "markerMgiid", joinQuery));
		filters.add(Filter.notEqual(GxdResultFields.ASSAY_TYPE, "RNA-Seq"));
		filters.add(Filter.equal(GxdResultFields.IS_WILD_TYPE, "wild type"));

		List<Filter> structIdFilters = new ArrayList<Filter>();
		structIdFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16105"));
		structIdFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "Yes"));

		List<Filter> structExtactFilters = new ArrayList<Filter>();
		structExtactFilters.add(Filter.equal(SearchConstants.STRUCTURE_ID, "EMAPA:16846"));
		structExtactFilters.add(Filter.equal(GxdResultFields.DETECTION_LEVEL, "No"));

		List<Filter> structFilters = new ArrayList<Filter>();
		structFilters.add(Filter.and(structIdFilters));
		structFilters.add(Filter.and(structExtactFilters));

		filters.add(Filter.or(structFilters));
		searchParams.setFilter(Filter.and(filters));

		searchParams.addSort(new Sort(GxdResultFields.M_BY_MRK_SYMBOL, false));
		searchParams.setPageSize(100);
//		searchParams.setStartIndex(2);

		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGetTotalCount(true);
		searchOption.setGroupField(GxdResultFields.MARKER_KEY);
		searchOption.setGetGroupFirstDoc(true);
		searchOption.setReturnFields(ESGxdMarker.RETURN_FIELDS);

		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("total number of groups: " + resultObjects.size());
		show(resultObjects, 100);
		log.info("\n");
	}

	@Test
	public void testSearchByMouseCoordinates() {
		log.info("Test: testSearchByMouseCoordinates");
		SearchParams searchParams = new SearchParams();

		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));

		String spatialQueryString = SolrLocationTranslator.getQueryValue("Chr12:3000000-10000000", "bp");
		filters.add(new Filter(SearchConstants.MOUSE_COORDINATE, spatialQueryString, Filter.Operator.OP_SHAPE));

		searchParams.setFilter(Filter.and(filters));

		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("resultObjects size: " + resultObjects.size());
		log.info("\n");
	}

	@Test
	public void testAggGetTotalNumberOfGroup() {
		log.info("Test: testAggGetTotalNumberOfGroup");
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));

		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(GxdResultFields.MARKER_KEY);
		searchOption.setGetTotalCount(true);

		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertEquals(1, searchResults.getTotalCount());
		log.info("total number of groups: " + searchResults.getTotalCount());
		log.info("\n");
	}

	@Test
	public void testAggGetFirstHitForEachGroup() {
		log.info("Test: testAggGetFirstHitForEachGroup");
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));

		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGetTotalCount(true);
		searchOption.setGroupField(GxdResultFields.MARKER_KEY);
		searchOption.setGetGroupFirstDoc(true);

		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("total number of groups: " + resultObjects.size());
		show(resultObjects, 5);
		log.info("\n");
	}

	@Test
	public void testAggSortAndPagination() {
		log.info("Test: testAggSortAndPagination");
		SearchParams searchParams = new SearchParams();
		searchParams.setStartIndex(0);
		searchParams.setPageSize(5);
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));

		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(GxdResultFields.ASSAY_KEY);
		searchOption.setGetGroupFirstDoc(true);
		this.esGxdResultHunter.hunt(searchParams, searchResults, searchOption);
		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertEquals(resultObjects.size(), 5);
		show(resultObjects, 5);

		searchParams.setStartIndex(3);
		SearchResults<ESEntity> searchResults2 = new SearchResults<ESEntity>();
		this.esGxdResultHunter.hunt(searchParams, searchResults2, searchOption);
		List<ESEntity> resultObjects2 = searchResults2.getResultObjects();
		log.info("start index: " + searchParams.getStartIndex());
		show(resultObjects2, 5);
		log.info(resultObjects.get(searchParams.getStartIndex()).toString() + " == " + searchParams.getStartIndex()
				+ ": " + resultObjects2.get(0).toString());
		Assert.assertEquals(resultObjects.get(searchParams.getStartIndex()).toString(),
				resultObjects2.get(0).toString());
		log.info("\n");
	}

	/*
	 * Test LOOKUP JOIN of three indexes
	 */
	@Test
	public void testLookupJoin() {
		log.info("Test: testLookupJoin");

		ESLookup lookUpJoin = new ESLookup("gxd_result_has_image");

		List<ESLookupIndex> lookupIndexes = new ArrayList<ESLookupIndex>();
		lookupIndexes.add(new ESLookupIndex("gxd_image_pane", GxdResultFields.RESULT_KEY, false));
		lookupIndexes.add(new ESLookupIndex("gxd_consolidated_sample", GxdResultFields.STRAIN, false));
		lookUpJoin.setLookupIndexes(lookupIndexes);

		List<String> extraStatements = new ArrayList<String>();
		String keep = "KEEP ";
		keep += GxdResultFields.MARKER_KEY + "," + GxdResultFields.MARKER_SYMBOL + ",";
		keep += ImagePaneFields.IMAGE_PANE_KEY + "," + IndexConstants.IMAGE_ID + ",";
		keep += ImagePaneFields.IMAGE_PIXELDBID + "," + ImagePaneFields.IMAGE_LABEL + ",";
		keep += ImagePaneFields.PANE_X + "," + ImagePaneFields.PANE_Y + ",";
		keep += ImagePaneFields.PANE_WIDTH + "," + ImagePaneFields.PANE_HEIGHT + ",";
		keep += ImagePaneFields.IMAGE_WIDTH + "," + ImagePaneFields.IMAGE_HEIGHT + ",";
		keep += GxdResultFields.ASSAY_MGIID + "," + ImagePaneFields.IMAGE_META + ",";
		keep += GxdResultFields.CONSOLIDATED_SAMPLE_KEY;
		extraStatements.add(keep);
		extraStatements.add("WHERE " + IndexConstants.IMAGE_ID + " is not NULL");
		lookUpJoin.setExtraStatements(extraStatements);

		SearchParams searchParams = new SearchParams();
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.equal(GxdResultFields.MARKER_SYMBOL, "Apob"));
		String spatialQueryString = SolrLocationTranslator.getQueryValue("Chr12:3000000-10000000", "bp");
		filters.add(new Filter(SearchConstants.MOUSE_COORDINATE, spatialQueryString, Filter.Operator.OP_SHAPE));
		searchParams.setFilter(Filter.and(filters));

		SearchResults<ESGxdImage> searchResults = new SearchResults<ESGxdImage>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setEsQuery(lookUpJoin);
		this.esGxdImagePaneHunter.hunt(searchParams, searchResults, searchOption);

		List<ESGxdImage> resultObjects = searchResults.getResultObjects();
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("resultObjects size: " + resultObjects.size());
		show(resultObjects, 5);
		log.info("\n");
	}

	@Test
	public void testAssayCount() {
		log.info("Test: testAssayCount");
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "Wnt1"));
		long total = this.esGxdResultHunter.huntCount(searchParams);
		Assert.assertTrue(total > 0);
		log.info("Total # of assays: " + total);
		log.info("\n");
	}

	@Test
	public void testRetrieveImages() {
		log.info("Test: testRetrieveImages");
		SearchParams params = new SearchParams();

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
			if (imagePaneKeys.size() > 50000) { // Hongping to do
				break;
			}
		}
		imagePaneParams.setFilter(Filter.in(ImagePaneFields.IMAGE_PANE_KEY, imagePaneKeys));
		SearchResults<ESGxdImage> imageResult = new SearchResults<ESGxdImage>();
		ESSearchOption searchOption2 = new ESSearchOption();
		esGxdImagePaneHunter.hunt(imagePaneParams, imageResult, searchOption2);

		imageResult
				.setTotalCount(esGxdResultHasImageHunter.huntExactUniqueCount(params, ImagePaneFields.IMAGE_PANE_KEY));
		List resultObjects2 = imageResult.getResultObjects();
		Assert.assertNotNull(resultObjects2);
		Assert.assertTrue(resultObjects2.size() > 0);
		log.info("\n");
	}

	@Test
	public void testGetGroupInfoStringKey() {
		log.info("Test: testGetGroupInfoStringKey");
		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(10);
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(GxdResultFields.MARKER_SYMBOL);
		searchOption.setGetGroupInfo(true);

		this.esGxdResultHasImageHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		show(resultObjects, 2);
		log.info("\n");
	}

	@Test
	public void testGetGroupInfoIntegerKey() {
		log.info("Test: testGetGroupInfoIntegerKey");
		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(10);
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(ImagePaneFields.IMAGE_PANE_KEY);
		searchOption.setGetGroupInfo(true);

		this.esGxdResultHasImageHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		show(resultObjects, 2);
		log.info("\n");
	}

	@Test
	public void testShapeSearch() throws IOException {
		log.info("Test: testShapeSearch");
		List<List<Long>> envelopeCoordinates = List.of(List.of(0l, 10000000000l), // top-left (minX, maxY)
				List.of(4830000000l, 4800999999l) // bottom-right (maxX, minY)
		);

		Map<String, Object> geoJsonEnvelope = Map.of("type", "envelope", "coordinates", envelopeCoordinates);
		Query geoShapeQuery = Query.of(q -> q.shape(g -> g.field("mc")
				.shape(s -> s.relation(GeoShapeRelation.Intersects).shape(JsonData.of(geoJsonEnvelope)))));
		SearchRequest searchRequest = SearchRequest.of(s -> s.index("gxd_result").size(5).query(geoShapeQuery));
		log.info("Sending search request: " + JsonData.of(searchRequest).toString());

		this.esGxdResultHunter.createESConnection();
		SearchResponse<JsonData> response = this.esGxdResultHunter.getEsClient().search(searchRequest, JsonData.class);
		log.info("Search response: " + response.toString());
		log.info("\n");
	}

	private <T extends ESEntity> void show(List<T> list, int size) {
		int cnt = 0;
		for (Object o : list) {
			log.info(o.toString());
			cnt++;
			if (cnt >= size) {
				return;
			}
		}
	}

}
