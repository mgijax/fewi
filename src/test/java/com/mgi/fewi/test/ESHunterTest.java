package com.mgi.fewi.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.jax.mgi.fewi.searchUtil.entities.ESGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.ESGxdMarker;
import org.jax.mgi.shr.fe.IndexConstants;
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

/*
 * 
Elasticsearch endpoints
	/_search → main workhorse (JSON DSL query)
	/_query → new ES|QL endpoint (pipeline syntax)
	/_count → just counts
	/_msearch → batch searches
	/_async_search → long-running searches
	/_knn_search → vector-based semantic search
	/_sql → SQL-style search
	/_eql/search → sequence-based event queries

Previously we only use the "query_string" to do all the query 
	POST /gxd_result/_search?typed_keys=true
	{
	  "query": {
	    "query_string": {
	      "query": "((nomenclature:\"Apob\") AND (assayType:\"Western blot\"))"
	    }
	  }
	}

Added aggregation and shape query 
	POST /gxd_result/_search?typed_keys=true
	{
	  "query": {
	    "bool": {
	      "must": [
	        {
	          "query_string": {
	            "query": "(nomenclature:\"Apob \"~100 AND assayType:\"Western blot\")"
	          }
	        },
	        {
	          "shape": {
	            "mc": {
	              "relation": "intersects",
	              "shape": {
	                "type": "geometrycollection",
	                "geometries": [
	                  {
	                    "type": "envelope",
	                    "coordinates": [
	                      [0, 10000000000],
	                      [3310000000, 3302999999]
	                    ]
	                  }
	                ]
	              }
	            }
	          }
	        }
	      ]
	    }
	  },
	  "aggregations": {
	    "assayKey": {
	      "terms": {
	        "field": "assayKey",
	        "order": [
	          { "_key": "asc" }
	        ],
	        "size": 8
	      },
	      "aggregations": {
	        "bucket_pagination": {
	          "bucket_sort": {
	            "from": 3,
	            "size": 5,
	            "sort": [
	              { "_key": { "order": "asc" } }
	            ]
	          }
	        },
	        "first_doc": {
	          "top_hits": {
	            "size": 1,
	            "_source": {
	              "includes": [
	                "markerSymbol",
	                "assayKey",
	                "assayMgiid",
	                "assayType",
	                "jNum",
	                "shortCitation"
	              ]
	            }
	          }
	        }
	      }
	    }
	  }
	}

/_query for joining of multiple indexes
	POST /_query
	{
	  "query": """
	    FROM gxd_result_has_image METADATA _id
	    | LOOKUP JOIN gxd_image_pane ON resultKey
	    | LOOKUP JOIN gxd_consolidated_sample ON strain
	    | WHERE markerSymbol == "Wnt1"
	    | KEEP markerSymbol, imageID, metaData
	    | WHERE imageID is not NULL
	  """
	}

 * 
 * 
 * 
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ESHunterTest {
	private static final Logger log = LoggerFactory.getLogger(ESHunterTest.class);

	private static final String ES_HOST = "bhmgigxdsolr01ld.jax.org";
//	private static final String ES_HOST = "localhost";
	private static final String ES_PORT = "9200";

	private ESGxdResultHunter gxdHasResultHunter;
	private ESGxdResultHasImageHunter gxdHasResultHasImageHunter;
	private ESGxdImagePaneHunter gxdImagePaneHunter;
	private ESGxdProfileMarkerHunter gxdProfileMarkerHunter;

	@Before
	public void setUp() {
		log.info("ESHunterTest: " + ES_HOST + " " + ES_PORT);
		this.gxdHasResultHunter = new ESGxdResultHunter(ESAssayResult.class, ES_HOST, ES_PORT, "gxd_result");
		this.gxdHasResultHasImageHunter = new ESGxdResultHasImageHunter(ESEntity.class, ES_HOST, ES_PORT,
				"gxd_result_has_image");
		this.gxdImagePaneHunter = new ESGxdImagePaneHunter(ESGxdImage.class, ES_HOST, ES_PORT, "gxd_image_pane");
		this.gxdProfileMarkerHunter = new ESGxdProfileMarkerHunter(ESEntity.class, ES_HOST, ES_PORT,
				"gxd_profile_marker");
	}

	/*
	 * Test search with a single parameter
	 */
	@Test
	public void testSimpleSearch() {
		log.info("Test: testSearchByParameter");
		SearchParams searchParams = new SearchParams();
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));
		SearchResults<ESAssayResult> searchResults = new SearchResults<ESAssayResult>();
		
		this.gxdHasResultHunter.huntDocs(searchParams, searchResults, ESAssayResult.RETURN_FIELDS);

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
		this.gxdHasResultHunter.hunt(searchParams, searchResults, searchOption);
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
//		searchParams.setFilter(Filter.equal(GxdResultFields.ASSAY_TYPE, "Western blot"));
		searchParams.addSort(new Sort(GxdResultFields.M_BY_MRK_SYMBOL, true));
		searchParams.setPageSize(10);
		searchParams.setStartIndex(2);

		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGetTotalCount(true);
		searchOption.setGroupField(GxdResultFields.MARKER_KEY);
		searchOption.setGetGroupFirstDoc(true);
		searchOption.setReturnFields(ESGxdMarker.RETURN_FIELDS);

		this.gxdHasResultHunter.hunt(searchParams, searchResults, searchOption);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		log.info("total number of groups: " + resultObjects.size());
		show(resultObjects, 5);
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
		this.gxdHasResultHunter.hunt(searchParams, searchResults, searchOption);

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

		this.gxdHasResultHunter.hunt(searchParams, searchResults, searchOption);

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
		;
		searchOption.setGroupField(GxdResultFields.MARKER_KEY);
		searchOption.setGetGroupFirstDoc(true);

		this.gxdHasResultHunter.hunt(searchParams, searchResults, searchOption);

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
		this.gxdHasResultHunter.hunt(searchParams, searchResults, searchOption);
		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertEquals(resultObjects.size(), 5);
		show(resultObjects, 5);

		searchParams.setStartIndex(3);
		SearchResults<ESEntity> searchResults2 = new SearchResults<ESEntity>();
		this.gxdHasResultHunter.hunt(searchParams, searchResults2, searchOption);
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
		this.gxdImagePaneHunter.hunt(searchParams, searchResults, searchOption);

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
		searchParams.setFilter(Filter.equal(GxdResultFields.NOMENCLATURE, "Apob"));
		long total = this.gxdHasResultHunter.huntCount(searchParams);
		Assert.assertTrue(total > 0);
		log.info("Total # of assays: " + total);
		log.info("\n");
	}

	@Test
	public void testImageCount() {
		log.info("Test: testImageCount");
		SearchParams searchParams = new SearchParams();
		long totalEstimate = this.gxdHasResultHasImageHunter.huntEstimatedUniqueCount(searchParams,
				ImagePaneFields.IMAGE_PANE_KEY, Optional.empty());
		long total = this.gxdHasResultHasImageHunter.huntExactUniqueCount(searchParams, ImagePaneFields.IMAGE_PANE_KEY);
		Assert.assertTrue(total > 0);
		log.info("images estimation total: " + totalEstimate + ", total: " + total);
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
		this.gxdProfileMarkerHunter.hunt(searchParams, searchResults, searchOption);
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
		gxdHasResultHunter.hunt(searchParams, results, searchOption);
		log.info("gxd finder marker count =" + results.getTotalCount());

		show(resultObjects, 5);
		log.info("\n");
	}

	@Test
	public void testRetrieveImages() {
		log.info("Test: testRetrieveImages");
		SearchParams searchParams = new SearchParams();
		searchParams.setPageSize(1000);
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		this.gxdHasResultHasImageHunter.huntGroupInfo(searchParams, searchResults, ImagePaneFields.IMAGE_PANE_KEY);

		List<ESEntity> resultObjects = searchResults.getResultObjects();
		Assert.assertNotNull(resultObjects);
		Assert.assertTrue(resultObjects.size() > 0);
		show(resultObjects, 5);

		List<String> imagePaneKeys = new ArrayList<String>(resultObjects.size());
		for (Object obj : resultObjects) {
			ESAggLongCount row = (ESAggLongCount) obj;
			imagePaneKeys.add(row.getKey() + "");
		}

		SearchParams searchParams2 = new SearchParams();
		searchParams2.setFilter(Filter.in(ImagePaneFields.IMAGE_PANE_KEY, imagePaneKeys));
		SearchResults<ESGxdImage> searchResults2 = new SearchResults<ESGxdImage>();
		ESSearchOption searchOption2 = new ESSearchOption();

		this.gxdImagePaneHunter.hunt(searchParams2, searchResults2, searchOption2);
		List resultObjects2 = searchResults2.getResultObjects();
		Assert.assertNotNull(resultObjects2);
		Assert.assertTrue(resultObjects2.size() > 0);
		show(resultObjects2, 5);
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

		this.gxdHasResultHasImageHunter.hunt(searchParams, searchResults, searchOption);

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

		this.gxdHasResultHasImageHunter.hunt(searchParams, searchResults, searchOption);

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

		this.gxdHasResultHunter.createESConnection();
		SearchResponse<JsonData> response = this.gxdHasResultHunter.getEsClient().search(searchRequest, JsonData.class);
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
