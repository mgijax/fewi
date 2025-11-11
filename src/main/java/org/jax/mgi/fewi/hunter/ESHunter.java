package org.jax.mgi.fewi.hunter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.JoinClause;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.entities.ESAggLongCount;
import org.jax.mgi.fewi.searchUtil.entities.ESAggStringCount;
import org.jax.mgi.fewi.sortMapper.ESSortMapper;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;
import org.jax.mgi.snpdatamodel.document.ESEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.GeoShapeRelation;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeAggregationSource;
import co.elastic.clients.elasticsearch._types.aggregations.CompositeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.TopHitsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.TopHitsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.elasticsearch.esql.EsqlFormat;
import co.elastic.clients.elasticsearch.esql.QueryRequest;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BinaryResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.util.NamedValue;
import lombok.Getter;
import lombok.Setter;

/**
 * This is the Elastic specific hunter. It is responsible for translating
 * higher-level generic webapp requests to Elastic's specific API
 */

@SuppressWarnings({ "unchecked", "hiding", "rawtypes" })
public class ESHunter<T extends ESEntity> {

	public Logger log = LoggerFactory.getLogger(getClass());

	// Which field is the document key?
	protected String keyString;
	// Which other field do we want to use as a key? (This could be collapsed)
	protected String otherString;

	// What is the name of the facet that we want to pull out
	@Getter
	@Setter
	protected String facetString = null;

	protected RangeAggSpecification rangeSpec = null;

	// protected JacksonJsonpMapper jom = new JacksonJsonpMapper();
	protected ElasticsearchClient esClient = null;

	@Value("${es.server.host}")
	protected String esHost;
	@Value("${es.server.port}")
	protected String esPort;

	protected String esIndex;

	@Value("${solr.soTimeout}")
	protected Integer solrSoTimeout;
	@Value("${solr.connectionTimeout}")
	protected Integer connectionTimeout;
	@Value("${solr.maxConnectionsPerHost}")
	protected Integer maxConnectionsPerHost;
	@Value("${solr.maxTotalConnections}")
	protected Integer maxTotalConnections;
	@Value("${solr.maxRetries}")
	protected Integer maxRetries;
	@Value("${solr.resultsDefault}")
	protected Integer resultsDefault;
	@Value("${solr.factetNumberDefault}")
	protected Integer factetNumberDefault;

	protected final ObjectMapper mapper; // ✅ declare here

	// Front end fields -> PropertyMappers
	protected HashMap<String, ESPropertyMapper> propertyMap = new HashMap<String, ESPropertyMapper>();

	// Elastic Fields -> Front end field mappings
	protected HashMap<String, String> fieldToParamMap = new HashMap<String, String>();

	// Front end sorts -> backend fields
	protected HashMap<String, ESSortMapper> sortMap = new HashMap<String, ESSortMapper>();

	// FEWI's filter comparison -> Elastic's comparison operator
	protected HashMap<JoinClause, String> filterClauseMap = new HashMap<JoinClause, String>();

	protected List<String> returnedFields = new ArrayList<String>();
	protected Map<String, List<String>> groupReturnedFields = new HashMap<String, List<String>>();

	// Fields to highlight
	protected List<String> highlightFields = new ArrayList<String>();

	// unique token used for highlighting
	protected final String highlightToken = "!FRAG!";
	// OR set pre/post tokens (not to be used with highlightToken)
	// Setting these makes the hunter ignore the highlightToken above
	protected String highlightPre = null;
	protected String highlightPost = null;

	protected boolean highlightRequireFieldMatch = true;
	protected int highlightFragmentSize = 30000;
	protected int highlightSnippets = 100;

	protected Map<String, String> groupFields = new HashMap<String, String>();

	private Class<T> clazz;

	public ESHunter(Class<T> clazz) {
		this.clazz = clazz;
		filterClauseMap.put(Filter.JoinClause.FC_AND, " AND ");
		filterClauseMap.put(Filter.JoinClause.FC_OR, " OR ");
		this.mapper = new ObjectMapper(); // ✅ initialize here
	}

	public ESHunter(Class<T> clazz, String host, String port, String index) {
		this(clazz);
		this.esHost = host;
		this.esPort = port;
		this.esIndex = index;
	}

	// snp search use this one
	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGetTotalCount(true);
		searchOption.setTractTopHit(true);
		hunt(searchParams, searchResults, searchOption);
	}

	// get total count of docs for the index with search filter
	public int huntCount(SearchParams searchParams) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setUseCountEndpoint(true);
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		hunt(searchParams, searchResults, searchOption);
		return searchResults.getTotalCount();
	}

	// get estimated unique value count for a field (groupField), fast
	// precisionThreshold range 1-40000, the higher, the more accurate but takes
	// longer
	public int huntEstimatedUniqueCount(SearchParams searchParams, String groupField,
			Optional<Integer> precisionThreshold) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(groupField);
		searchOption.setGetTotalCount(true);
		searchOption.setPrecisionThreshold(precisionThreshold.orElse(SearchConstants.SEARCH_PRECISION_THRESHOLD));
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		hunt(searchParams, searchResults, searchOption);
		return searchResults.getTotalCount();
	}

	// get exact unique value count for a field (groupField), slow
	public int huntExactUniqueCount(SearchParams searchParams, String groupField) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(groupField);
		searchOption.setGetTotalCount(true);
		searchOption.setCountNumOfBuckts(true);
		SearchResults<ESEntity> searchResults = new SearchResults<ESEntity>();
		hunt(searchParams, searchResults, searchOption);
		return searchResults.getTotalCount();
	}

	// retrieve docs for the index with search filter, specifying return fields
	public void huntDocs(SearchParams searchParams, SearchResults<T> searchResults, List<String> returnFields) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setReturnFields(returnFields);
		hunt(searchParams, searchResults, searchOption);
	}

	/*
	 * parse out key and doc_count group information
	 * 
	 * [ {"key": 21389, "doc_count": 144 }, { "key": 21391, "doc_count": 144 } ]
	 *
	 * The key could be String or Integer
	 *
	 */
	public void huntGroupInfo(SearchParams searchParams, SearchResults<T> searchResults, String groupField) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(groupField);
		searchOption.setGetGroupInfo(true);
		hunt(searchParams, searchResults, searchOption);
	}

	// get grouped keys
	public void huntFacets(SearchParams searchParams, SearchResults<T> searchResults, String groupField) {
		huntGroupInfo(searchParams, searchResults, groupField);

		List<T> resultObjects = searchResults.getResultObjects();
		if (resultObjects == null) {
			return;
		}

		List<String> facets = new ArrayList<String>(resultObjects.size());
		for (Object obj : resultObjects) {
			if (obj instanceof ESAggLongCount) {
				facets.add(((ESAggLongCount) obj).getKey() + "");
			} else if (obj instanceof ESAggStringCount) {
				facets.add(((ESAggStringCount) obj).getKey());
			}
		}
		searchResults.setResultFacets(facets);
	}

	// retrieve grouped bucket with first doc
	public void huntGroupFirstDoc(SearchParams searchParams, SearchResults<T> searchResults, String groupField,
			List<String> returnFields) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGroupField(groupField);
		searchOption.setGetGroupFirstDoc(true);
		searchOption.setReturnFields(returnFields);
		hunt(searchParams, searchResults, searchOption);
	}

	// retrieve all buckets
	public void huntAllBuckets(SearchParams searchParams, SearchResults<T> searchResults, String groupField,
			List<String> returnFields) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGetAllBuckets(true);
		searchOption.setReturnFields(returnFields);
		hunt(searchParams, searchResults, searchOption);
	}

	public <T extends ESEntity> void hunt(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption) {

		createESConnection();

		// Invoke the hook, editing the search params as needed by subclass.
		if (preProcessSearchParams(searchParams, searchOption)) {
			log.info("Stopped after preProcessSearchParams");
			return;
		}

		if (searchParams.getReturnFilterQuery())
			searchResults.setFilterQuery(translateFilter(searchParams.getFilter(), propertyMap));

		try {
			if (searchOption.isUseCountEndpoint()) {
				// ES endpint "/count"
				huntDoCount(searchParams, searchResults, searchOption);
			} else if (searchOption.getEsQuery() == null) {
				// ES endpint "/search"
				huntDoSearch(searchParams, searchResults, searchOption);
			} else {
				// ES endpoint "/query" for ESQL
				huntDoQuery(searchParams, searchResults, searchOption);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("ESHunter.hunt finished");
	}

	public <T extends ESEntity> void huntDoCount(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption) throws Exception {

		CountRequest.Builder srb = new CountRequest.Builder();
		srb.index(esIndex);

		Query query = getAllQuery(searchParams, searchOption);
		if (query != null) {
			srb.query(query);
		}

		CountRequest countRequest = srb.build();
		log.info("Sending count request: " + countRequest);

		CountResponse countResponse = esClient.count(countRequest);
		log.info("Total matching documents: " + countResponse.count());
		searchResults.setTotalCount((int) countResponse.count());
	}

	public <T extends ESEntity> void huntDoSearch(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption) throws Exception {

		String groupField = searchOption.getGroupField();

		SearchRequest.Builder srb = new SearchRequest.Builder();
		srb.index(esIndex);

		Query query = getAllQuery(searchParams, searchOption);
		if (query != null) {
			srb.query(query);
		}

		if (searchOption.isGetAllBuckets()) {
			getAllBuckets(searchParams, searchResults, searchOption);
			return;
		}

		SearchResponse<T> resp = null;
		if (searchOption.isTractTopHit()) {
			TrackHits.Builder th = new TrackHits.Builder();
			th.enabled(true);
			srb.trackTotalHits(th.build());
		}

		Highlight highlight = addHighlightingFields(searchParams);
		if (highlight != null) {
			srb.highlight(highlight);
		}

		if (groupField == null) {
			if (searchOption.getReturnFields() != null && !searchOption.getReturnFields().isEmpty()) {
				srb.source(src -> src.filter(f -> f.includes(searchOption.getReturnFields())));
			}
			addSorts(searchParams, srb);
			srb.from(searchParams.getStartIndex());
			srb.size(searchParams.getPageSize());
		} else {
			addSearchAggregation(searchParams, searchOption, searchResults, srb);
			srb.size(0); // no need for agg
		}
		if (facetString != null) {
			srb.aggregations(facetString, t -> t.terms(f -> f.field(facetString).size(150)));
		}
		if (rangeSpec != null) {
			List<AggregationRange> aggRanges = new ArrayList<AggregationRange>();
			for (long[] r : rangeSpec.getRanges()) {
				AggregationRange.Builder arb = new AggregationRange.Builder();
				aggRanges.add(arb.from((double) r[0]).to((double) r[1]).build());
			}
			srb.aggregations(rangeSpec.getName(), t -> t.range(f -> f.field(rangeSpec.getField()).ranges(aggRanges)));
		}

		SearchRequest searchRequest = srb.build();
		log.info("Sending search request: " + searchRequest);

		Class callClazz;
		if (searchOption.getClazz() == null) {
			callClazz = this.clazz;
		} else {
			callClazz = searchOption.getClazz();
		}
		resp = (SearchResponse<T>) esClient.search(searchRequest, callClazz);
		log.info("Total hits: " + resp.hits().total().value());

		if (resp.aggregations().size() > 0) {
			// if total unique count, parse out
			Aggregate agg = resp.aggregations().get(getAggUniqueCountKey(groupField));
			if (agg != null) {
				int uniqueCount = (int) agg.cardinality().value();
				searchResults.setTotalCount(uniqueCount);
				log.info("Estimated unique count for " + groupField + ": " + uniqueCount);
			}

			if (searchOption.isCountNumOfBuckts()) {
				Aggregate termAgg = resp.aggregations().get(getAggUniqueBucketsKey(groupField));
				if (termAgg != null) {
					if (termAgg.isSterms()) {
						StringTermsAggregate stringAgg = termAgg.sterms();
						searchResults.setTotalCount(stringAgg.buckets().array().size());
						log.info("Exact unique String count for " + groupField + ": " + searchResults.getTotalCount());
					} else if (termAgg.isLterms()) {
						LongTermsAggregate longAgg = termAgg.lterms();
						searchResults.setTotalCount(longAgg.buckets().array().size());
						log.info("Exact unique Integer count for " + groupField + ": " + searchResults.getTotalCount());
					}
				}
			} else if (searchOption.isGetGroupInfo()) {
				// parse out group info
				Aggregate termAgg = resp.aggregations().get(groupField);
				if (termAgg != null) {
					parseGroupInfo(termAgg, searchResults);
				}
			} else if (searchOption.isGetGroupFirstDoc()) {
				parseFirstDoc(resp, searchResults, searchParams);
			}
			// left over, might be used in SNP query
			if (facetString != null) {
				for (StringTermsBucket bucket : resp.aggregations().get(facetString).sterms().buckets().array()) {
					searchResults.getResultFacets().add(bucket.key().stringValue());
				}
			}
			if (rangeSpec != null) {
				for (RangeBucket bucket : resp.aggregations().get(rangeSpec.getName()).range().buckets().array()) {
					long[] b = { bucket.from().longValue(), bucket.to().longValue(), bucket.docCount() };
					searchResults.getHistogram().add(b);
				}
			}
		} else {
			if (searchOption.isGetTotalCount()) {
				searchResults.setTotalCount((int) resp.hits().total().value());
			}
		}
		for (Hit<T> hit : resp.hits().hits()) {
			if (hit.source() instanceof BaseESDocument) {
				searchResults.getResultKeys().add(((BaseESDocument) hit.source()).getConsensussnp_accid());
			}
			searchResults.getResultObjects().add(hit.source());
		}
	}

	private <T extends ESEntity> void huntDoQuery(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption) throws Exception {

		List<String> whereCauses = new ArrayList<String>();
		String queryString = translateFilter(searchParams.getFilter(), propertyMap);
		if (queryString != null && !queryString.isEmpty()) {
			whereCauses.add(queryString.replaceAll(":", "==").replaceAll("~100", ""));
		}

		List<Filter> shapeFilters = null;
		if (searchParams.getFilter() != null) {
			shapeFilters = searchParams.getFilter().collectFilters(Filter.Operator.OP_SHAPE);
		}

		if (shapeFilters != null && !shapeFilters.isEmpty()) {
			List<String> polygons = new ArrayList<String>();
			for (Filter filter : shapeFilters) {
				String polygon = toPolygon(filter);
				if (polygon != null) {
					polygons.add(polygon);
				}
			}
			if (polygons != null && !polygons.isEmpty()) {
				String joinedPolygons = polygons.stream()
						.map(p -> "ST_INTERSECTS(mc, \"" + p + "\" :: cartesian_shape)")
						.collect(Collectors.joining(" OR "));
				whereCauses.add(joinedPolygons);
			}
		}

		String query = searchOption.getEsQuery().toQuery(whereCauses, searchParams.getPageSize(),
				searchOption.isGetTotalCount());
		log.info("esquery: " + query);
		BinaryResponse binResp = esClient.esql().query(QueryRequest.of(q -> q.query(query).format(EsqlFormat.Json)));

		JsonNode root = mapper.readTree(binResp.content());
		if (searchOption.isGetTotalCount()) {
			int totalCount = root.path("values").get(0).get(0).asInt();
			searchResults.setTotalCount(totalCount);
		} else {
			List results = processLookupResponse(root, clazz);
			searchResults.setResultObjects(results);
		}
		log.info("esql found: " + searchResults.getTotalCount());
	}

	public <T extends ESEntity> void getAllBuckets(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption) throws Exception {

		int pageSize = 500;
		List<String> returnFields;
		if (searchOption.getReturnFields() == null) {
			returnFields = List.of();
		} else {
			returnFields = searchOption.getReturnFields();
		}

		Map<String, FieldValue> afterKey = null;

		System.out.println("Starting composite aggregation pagination...");
		while (true) {
			SearchRequest.Builder srb = new SearchRequest.Builder();
			srb.index(esIndex);
			srb.size(0);
			Query query = getAllQuery(searchParams, searchOption);
			if (query != null) {
				srb.query(query);
			}

			TopHitsAggregation.Builder topHitsBuilder = new TopHitsAggregation.Builder();
			topHitsBuilder.size(1);
			topHitsBuilder.source(src -> src.filter(f -> f.includes(returnFields)));

			TopHitsAggregation topHits = topHitsBuilder.build();
			Aggregation topHitsAgg = new Aggregation.Builder().topHits(topHits).build();

			// Create the composite sources
			CompositeAggregationSource byMrkSymbolSource = new CompositeAggregationSource.Builder()
					.terms(ts -> ts.field("byMrkSymbol")).build();
			CompositeAggregationSource markerKeySource = new CompositeAggregationSource.Builder()
					.terms(ts -> ts.field("markerKey")).build();

			// Create the composite aggregation
			CompositeAggregation.Builder compositeBuilder = new CompositeAggregation.Builder();
			compositeBuilder.size(pageSize);
			compositeBuilder.sources(
					List.of(Map.of("byMrkSymbol_sort", byMrkSymbolSource), Map.of("markerKey", markerKeySource)));
			if (afterKey != null) {
				compositeBuilder.after(afterKey);
			}
			CompositeAggregation composite = compositeBuilder.build();

			// Define the main Aggregation object and add both sub-aggregations to it
			Aggregation.Builder markerKeysAggBuilder = new Aggregation.Builder();
			markerKeysAggBuilder.composite(composite);
			markerKeysAggBuilder.aggregations("first_doc", topHitsAgg);
			Aggregation markerKeysAgg = markerKeysAggBuilder.build();

			srb.aggregations("marker_keys", markerKeysAgg);
			SearchRequest searchRequest = srb.build();
			log.info("Sending search request: " + searchRequest);
			SearchResponse<Void> response = esClient.search(searchRequest, Void.class);

			Aggregate agg = response.aggregations().get("marker_keys");
			CompositeAggregate compositeAggResult = agg.composite();
			List<CompositeBucket> buckets = compositeAggResult.buckets().array();

			if (buckets.isEmpty()) {
				log.info("No more buckets found. Exiting pagination.");
				break;
			}
			log.info("# of buckets " + buckets.size());

			// Process the buckets for the current page
			for (CompositeBucket bucket : buckets) {
				Map<String, FieldValue> keyMap = bucket.key();
				Object markerKey = keyMap.get("markerKey") != null ? keyMap.get("markerKey")._get() : null;
				Object byMrkSymbol = keyMap.get("byMrkSymbol_sort") != null ? keyMap.get("byMrkSymbol_sort")._get()
						: null;

				Aggregate firstDocAgg = bucket.aggregations().get("first_doc");
				if (firstDocAgg != null && firstDocAgg.isTopHits()) {
					TopHitsAggregate topHitsAggs = firstDocAgg.topHits();
					List<Hit<JsonData>> hits = topHitsAggs.hits().hits();

					if (!hits.isEmpty()) {
						Hit<JsonData> firstHit = hits.get(0);

						// ✅ Extract _source as a JsonData -> Map
						Map<String, Object> sourceMap = firstHit.source().to(Map.class);

						// ✅ Access individual fields safely
						String docMarkerKey = sourceMap.get("markerKey") != null ? sourceMap.get("markerKey").toString()
								: null;
						String markerSymbol = sourceMap.get("markerSymbol") != null
								? sourceMap.get("markerSymbol").toString()
								: null;
						String markerName = sourceMap.get("markerName") != null ? sourceMap.get("markerName").toString()
								: null;
						String byMrkSymbolStr = sourceMap.get("byMrkSymbol") != null
								? sourceMap.get("byMrkSymbol").toString()
								: null;

						log.info("Parsed first doc -> markerKey={}, markerSymbol={}, markerName={}, byMrkSymbol={}",
								docMarkerKey, markerSymbol, markerName, byMrkSymbolStr);
					}
				}
			}

			// Get the after_key for the next page
			afterKey = compositeAggResult.afterKey();

			if (afterKey == null) {
				System.out.println("Reached the end of pagination.");
				break;
			}
		}
	}

	private Query getAllQuery(SearchParams searchParams, ESSearchOption searchOption) {
		List<Query> queryList = new ArrayList<>();
		// create "Query String Query"
		String queryString = translateFilter(searchParams.getFilter(), propertyMap);
		if (queryString != null && !queryString.isEmpty()) {
			QueryStringQuery.Builder qsb = new QueryStringQuery.Builder();
			qsb.query(queryString);
			Query queryStringQuery = new QueryStringQuery.Builder().query(queryString).build()._toQuery();
			queryList.add(queryStringQuery);
		}

		List<Filter> shapeFilters = null;
		List<Filter> inFilters = null;
		if (searchParams.getFilter() != null) {
			shapeFilters = searchParams.getFilter().collectFilters(Filter.Operator.OP_SHAPE);
			inFilters = searchParams.getFilter().collectFilters(Filter.Operator.OP_TERM_IN);
		}

		// "Shape Query"
		if (shapeFilters != null && !shapeFilters.isEmpty()) {
			Query shapeQuery = getShapeQuery(queryString, shapeFilters);
			queryList.add(shapeQuery);
		}

		// "In Filters" -> convert each filter into a terms query
		if (inFilters != null && !inFilters.isEmpty()) {
			for (Filter filter : inFilters) {
				if (filter.getValues() != null && !filter.getValues().isEmpty()) {
					String field = getMappedField(filter.getProperty());
					List<FieldValue> values = filter.getValues().stream().map(FieldValue::of).toList();
					TermsQuery.Builder termsBuilder = new TermsQuery.Builder().field(field).terms(t -> t.value(values));
					queryList.add(new Query.Builder().terms(termsBuilder.build()).build());
				}
			}
		}

		Query combinedQuery = null;
		if (!queryList.isEmpty()) {
			if (queryList.size() == 1) {
				combinedQuery = queryList.get(0);
			} else {
				// combine "Query String Query" and "Shape Query" together
				BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
				for (Query query : queryList) {
					boolBuilder.must(query);
				}
				combinedQuery = new Query.Builder().bool(boolBuilder.build()).build();
			}
		}
		return combinedQuery;
	}

	private <T extends ESEntity> void parseGroupInfo(Aggregate termAgg, SearchResults searchResults) {
		if (termAgg.isSterms()) {
			StringTermsAggregate stringAgg = termAgg.sterms();
			List<ESAggStringCount> resultObjects = new ArrayList<ESAggStringCount>();
			for (StringTermsBucket term : stringAgg.buckets().array()) {
				resultObjects.add(new ESAggStringCount(term.key().stringValue(), term.docCount()));
			}
			searchResults.setResultObjects(resultObjects);
		} else if (termAgg.isLterms()) {
			LongTermsAggregate longAgg = termAgg.lterms();
			List<ESAggLongCount> resultObjects = new ArrayList<ESAggLongCount>();
			for (LongTermsBucket term : longAgg.buckets().array()) {
				resultObjects.add(new ESAggLongCount(term.key(), term.docCount()));
			}
			searchResults.setResultObjects(resultObjects);
		}
	}

	private String toPolygon(Filter filter) {
		String value = filter.getValue();
		if (value == null) {
			return null;
		}
		value = value.replaceAll("[\\[\\]\\s]", "");
		String[] points = value.split("TO");
		long[] point1 = parsePoint(points[0]);
		long[] point2 = parsePoint(points[1]);

		long xmin = Math.min(point1[0], point2[0]);
		long xmax = Math.max(point1[0], point2[0]);
		long ymin = Math.min(point1[1], point2[1]);
		long ymax = Math.max(point1[1], point2[1]);

		// Build POLYGON WKT string
		String polygon = String.format("POLYGON((%d %d, %d %d, %d %d, %d %d, %d %d))", xmin, ymin, // lower-left
				xmin, ymax, // upper-left
				xmax, ymax, // upper-right
				xmax, ymin, // lower-right
				xmin, ymin // close polygon
		);

		return polygon;
	}

	protected String getAggUniqueCountKey(String groupField) {
		return "unique_" + groupField + "_count";
	}

	protected String getAggUniqueBucketsKey(String groupField) {
		return "unique_" + groupField + "_buckets";
	}

	protected String getAggSortKeyName(String groupField) {
		return groupField + "_sort_value";
	}

	protected String getAggCompositeBucketsKey(String groupField) {
		return groupField + "_buckets";
	}

	private long[] parsePoint(String pointStr) {
		try {
			String[] parts = pointStr.split(",");
			return new long[] { (long) Double.parseDouble(parts[0]), (long) Double.parseDouble(parts[1]) };
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private Query getShapeQuery(String queryString, List<Filter> shapeFilters) {

		List<Map<String, Object>> geometries = new ArrayList<Map<String, Object>>();
		for (Filter filter : shapeFilters) {
			String value = filter.getValue();
			if (value == null) {
				continue;
			}
			value = value.replaceAll("[\\[\\]\\s]", "");
			String[] points = value.split("TO");
			long[] point1 = parsePoint(points[0]);
			long[] point2 = parsePoint(points[1]);

			List<List<Long>> envelopeCoordinates = List.of(List.of(point1[0], point2[1]), // top-left (minX, maxY)
					List.of(point2[0], point1[1]) // bottom-right (maxX, minY)
			);

			Map<String, Object> geoJsonEnvelope = Map.of("type", "envelope", "coordinates", envelopeCoordinates);
			geometries.add(geoJsonEnvelope);
		}

		Map<String, Object> geometryCollection = Map.of("type", "geometrycollection", "geometries", geometries);

		if (geometries.isEmpty()) {
			return null;
		}

		Query shapeQuery = Query.of(q -> q.shape(g -> g.field(GxdResultFields.MOUSE_COORDINATE)
				.shape(s -> s.relation(GeoShapeRelation.Intersects).shape(JsonData.of(geometryCollection)))));
		return shapeQuery;
	}

	protected <T extends ESEntity> List<T> processLookupResponse(JsonNode root, Class<T> clazz) throws Exception {
		List<String> columns = new ArrayList<>();
		root.get("columns").forEach(col -> columns.add(col.get("name").asText()));

		List<T> results = new ArrayList<>();
		for (JsonNode row : root.get("values")) {

			T obj = clazz.getDeclaredConstructor().newInstance();

			for (int i = 0; i < columns.size(); i++) {
				String colName = columns.get(i);
				JsonNode valueNode = row.get(i);

				// Try to find a matching field in the class
				Field field;
				try {
					field = clazz.getDeclaredField(colName);
				} catch (NoSuchFieldException e) {
					continue; // skip unknown fields
				}

				field.setAccessible(true);

				if (valueNode.isNull()) {
					field.set(obj, null);
				} else if (field.getType().equals(Integer.class)) {
					field.set(obj, valueNode.asInt());
				} else if (field.getType().equals(String.class)) {
					field.set(obj, valueNode.asText());
				} else if (field.getType().equals(List.class)) {
					List<String> listVal = new ArrayList<>();
					if (valueNode.isArray()) {
						for (JsonNode n : valueNode) {
							listVal.add(n.asText());
						}
					} else {
						listVal.add(valueNode.asText());
					}
					field.set(obj, listVal);
				} else {
					Object val = mapper.treeToValue(valueNode, field.getType());
					field.set(obj, val);
				}
			}
			results.add(obj);
		}
		return results;
	}

	protected String getMappedField(String uiProperty) {

		if (propertyMap.containsKey(uiProperty)) {
			ESPropertyMapper pm = propertyMap.get(uiProperty);
			return pm.getField();
		} else {
			return uiProperty;
		}
	}

	protected String translateFilter(Filter filter, HashMap<String, ESPropertyMapper> propertyMap) {
		/**
		 * This is the end case for the recursion. If we are at a node in the tree
		 * generate the chunk of query string for that node and return it back to the
		 * caller.
		 *
		 * An important concept here is the propertyMap, this mapping handles the query
		 * clause generation for a given single property. This allows us to handle any
		 * special cases for properties using a single interface.
		 */
		if (filter == null)
			return "";

		String filterProperty = filter.getProperty();
		ESPropertyMapper pm;
		if (!propertyMap.containsKey(filterProperty)) {
			pm = new ESPropertyMapper(filterProperty);
		} else {
			pm = propertyMap.get(filter.getProperty());
		}

		if (filter.isBasicFilter()) {
			// do nothing for shape parameter
			if (filter.getOperator() == Filter.Operator.OP_SHAPE
					|| filter.getOperator() == Filter.Operator.OP_TERM_IN) {
				return "";
			}
			// Check to see if the property is null or an empty string,
			// if it is, return an empty string
			if (filterProperty == null || filterProperty.equals("")) {
				return "";
			}

			if (filter.getOperator() != Filter.Operator.OP_IN && filter.getOperator() != Filter.Operator.OP_NOT_IN
					&& filter.getOperator() != Filter.Operator.OP_RANGE) {
				return pm.getClause(filter);
			} else if (filter.getOperator() == Filter.Operator.OP_RANGE) {
				return pm.getClause(filter);
			} else {
				String joinClause = "\" OR \"";
				String field = pm.getField();

				// The not operator works differently then then - operator one minuses the
				// records
				// The other takes the inverse of the records.
				String notOp = "";
				if (filter.getOperator() == Filter.Operator.OP_NOT_IN) {
					notOp = "!";
				}

				if (!"".equals(field)) {
					return notOp + field + ":(\"" + StringUtils.join(filter.getValues(), joinClause) + "\")";
				} else {
					// have to support the lazy programmer who feels the need to put multiple solr
					// fields into one property
					String fieldJoinClause = " " + pm.getJoinClause() + " ";

					// build the multiple field list
					List<String> queryClauses = new ArrayList<String>();
					for (String listField : pm.getFieldList()) {
						queryClauses.add(
								notOp + listField + ":(\"" + StringUtils.join(filter.getValues(), joinClause) + "\")");
					}
					return "(" + StringUtils.join(queryClauses, fieldJoinClause) + ")";
				}
			}
		} else {
			List<Filter> filters = filter.getNestedFilters();

			List<String> resultsString = new ArrayList<String>();

			for (Filter f : filters) {

				String tempString = translateFilter(f, propertyMap);
				if (!tempString.equals("")) {
					resultsString.add(tempString);
				}
			}
			// handle negating a nested filter
			String negation = filter.isNegate() ? "-" : "";
			if (resultsString.size() > 1) {
				return negation + "("
						+ StringUtils.join(resultsString, filterClauseMap.get(filter.getFilterJoinClause())) + ")";
			} else if (filter.isNegate()) {
				return negation + "("
						+ StringUtils.join(resultsString, filterClauseMap.get(filter.getFilterJoinClause())) + ")";
			} else {
				if (resultsString.size() > 0) {
					return resultsString.get(0);
				} else {
					return "";
				}
			}
		}
	}

	private void addSearchAggregation(SearchParams searchParams, ESSearchOption searchOption,
			SearchResults searchResults, SearchRequest.Builder srb) throws Exception {

		String groupField = searchOption.getGroupField();
		int from = searchParams.getStartIndex();
		int size = searchParams.getPageSize();

		// Add total unique count if needed
		if (searchOption.isGetTotalCount()) {
			if (searchOption.isCountNumOfBuckts()) {
				// slower, but exact SearchConstants.SEARCH_MAX_RESULT_WINDOW
				srb.aggregations(getAggUniqueBucketsKey(groupField),
						a -> a.terms(t -> t.field(groupField).size(SearchConstants.SEARCH_MAX_RESULT_WINDOW)));
			} else {
				// fast, but not exact for large data
				srb.aggregations(getAggUniqueCountKey(groupField), a -> a.cardinality(
						c -> c.field(groupField).precisionThreshold(searchOption.getPrecisionThreshold())));
			}
		}

		// no need for group info
		if (!(searchOption.isGetGroupFirstDoc() || searchOption.isGetGroupInfo())) {
			return;
		}
		srb.aggregations(groupField, a -> {
			// Base terms aggregation
			a.terms(t -> t.field(groupField).size(SearchConstants.SEARCH_MAX_GROUP_BASE_MAX_SIZE));

			// add first_doc aggregation
			if (searchOption.isGetGroupFirstDoc()) {
				List<String> returnFields;
				if (searchOption.getReturnFields() == null) {
					returnFields = List.of();
				} else {
					returnFields = searchOption.getReturnFields();
				}
				a.aggregations("first_doc", subAgg -> subAgg
						.topHits(th -> th.size(1).source(src -> src.filter(f -> f.includes(returnFields)))));

				Map<String, SortOptions> sortMap = getSortFields(searchParams);
				if (!sortMap.isEmpty()) {
					for (String key : sortMap.keySet()) {
						a.aggregations(getAggSortKeyName(key), sub -> sub.min(m -> m.field(key)));
					}
				}

				// Always add bucket pagination
				List<SortOptions> sorts = new ArrayList<>();
				if (!sortMap.isEmpty()) {
					sorts = new ArrayList<>(sortMap.values());
				}
				List<SortOptions> sortsFinal = sorts;
				a.aggregations("bucket_pagination",
						subAgg -> subAgg.bucketSort(bs -> bs.from(from).size(size).sort(sortsFinal)));
			}
			return a;
		});
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends ESEntity> void parseFirstDoc(SearchResponse resp, SearchResults<T> searchResults,
			SearchParams searchParams) {
	}

	protected int toInt(Object obj) {
		if (obj == null) {
			return 0;
		}

		try {
			return Integer.parseInt((String) obj);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	// Helper: safely convert object to string
	protected String getString(Object obj) {
		return obj != null ? obj.toString() : null;
	}

	public String debugFilter(Filter f) {
		return translateFilter(f, propertyMap);
	}

	public void createESConnection() {
		if (esClient == null) {
			RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, Integer.parseInt(esPort), "http"));
			builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
				@Override
				public org.apache.http.client.config.RequestConfig.Builder customizeRequestConfig(
						org.apache.http.client.config.RequestConfig.Builder requestConfigBuilder) {
					int hour = 60 * 60 * 1000;
					int hours = 2 * hour;
					return requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(hours)
							.setConnectionRequestTimeout(hours);
				}
			});

			ElasticsearchTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper());
			esClient = new ElasticsearchClient(transport);
			log.info("Finished Connecting to ES client: " + esHost + ":" + esPort);
		}
	}

	protected Highlight addHighlightingFields(SearchParams searchParams) {
		if (!highlightFields.isEmpty() && searchParams.includeMetaHighlight()) {

			List<NamedValue<HighlightField>> hlFields = new ArrayList<>();
			for (String field : highlightFields) {
				hlFields.add(NamedValue.of(field, HighlightField.of(hf -> hf.fragmentSize(highlightFragmentSize)
						.numberOfFragments(highlightSnippets).requireFieldMatch(highlightRequireFieldMatch))));
			}

			return Highlight.of(h -> {
				h.fields(hlFields);
				if (highlightPre != null && highlightPost != null) {
					h.preTags(highlightPre).postTags(highlightPost);
				} else {
					h.preTags(highlightToken).postTags(highlightToken);
				}
				return h;
			});
		}
		return null; // no highlighting
	}

	protected void addSorts(SearchParams searchParams, SearchRequest.Builder srb) {

		SortOrder currentSort = null;
		FieldSort.Builder fsb = null;
		FieldSort fs = null;
		SortOptions so = null;
		SortOptions.Builder sob = null;

		for (Sort sort : searchParams.getSorts()) {
			// Determine the direction of the sort.

			if (sort.isDesc()) {
				currentSort = SortOrder.Desc;
			} else {
				currentSort = SortOrder.Asc;
			}

			/**
			 * Is this a configured sort? If so check the sort map for 1->N Mappings.
			 */

			if (sortMap.containsKey(sort.getSort())) {
				List<SortOptions> sol = new ArrayList<SortOptions>();
				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					fsb = new FieldSort.Builder();
					fs = fsb.field(ssm).order(currentSort).build();
					sob = new SortOptions.Builder();
					so = sob.field(fs).build();
					sol.add(so);
				}
				srb.sort(sol);
			}

			/**
			 * Otherwise just add the sort in as is, Solr will ignore invalid sorts.
			 */

			else {
				fsb = new FieldSort.Builder();
				fs = fsb.field(sort.getSort()).order(currentSort).build();
				sob = new SortOptions.Builder();
				so = sob.field(fs).build();
				srb.sort(so);
			}
		}
	}

	private Map<String, SortOptions> getSortFields(SearchParams searchParams) {
		Map<String, SortOptions> map = new HashMap<>();

		SortOrder order;
		for (Sort sort : searchParams.getSorts()) {
			if (sort.isDesc()) {
				order = SortOrder.Desc;
			} else {
				order = SortOrder.Asc;
			}
			if (sortMap.containsKey(sort.getSort())) {
				// Loop over sort list
				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					FieldSort fs = new FieldSort.Builder().field(getAggSortKeyName(ssm)).order(order).build();
					SortOptions so = new SortOptions.Builder().field(fs).build();
					map.put(ssm, so);
					// break;
					// Hongping todo only works for the first one, add multiple sort if needed
				}
			} else {
				FieldSort fs = new FieldSort.Builder().field(getAggSortKeyName(sort.getSort())).order(order).build();
				SortOptions so = new SortOptions.Builder().field(fs).build();
				map.put(sort.getSort(), so);
			}
		}
		return map;
	}

	/**
	 * preprocessSearchParams
	 * 
	 * @param SearchParams
	 * @return boolean if false, stop the further process
	 *
	 *         This is a hook, any class that needs to modify the searchParams
	 *         before doing its work will override this method.
	 *
	 */
	protected boolean preProcessSearchParams(SearchParams searchParams, ESSearchOption searchOption) {
		return false;
	}

	private int getTotalNumberOfBuckets(SearchParams searchParams, SearchResults searchResults,
			ESSearchOption searchOption) throws Exception {

		String groupField = searchOption.getGroupField();
		Map<String, CompositeAggregationSource> sources = Map.of(groupField,
				CompositeAggregationSource.of(s -> s.terms(t -> t.field(groupField))));

		int pageSize = 10000; // Adjust as needed
		int totalCount = 0;
		Map<String, FieldValue> afterKey = null;
		while (true) {
			SearchRequest.Builder srb = new SearchRequest.Builder();
			srb.index(esIndex);

			Query query = getAllQuery(searchParams, searchOption);
			if (query != null) {
				srb.query(query);
			}

			CompositeAggregation.Builder compositeBuilder = new CompositeAggregation.Builder().size(pageSize)
					.sources(sources);

			if (afterKey != null) {
				compositeBuilder.after(afterKey);
			}
			srb.aggregations(getAggUniqueBucketsKey(groupField), a -> a.composite(compositeBuilder.build()));

			SearchRequest searchRequest = srb.build();
			log.info("Sending search request: " + searchRequest);
			SearchResponse<Void> response = this.esClient.search(searchRequest, Void.class);

			var compositeAgg = response.aggregations().get(getAggUniqueBucketsKey(groupField)).composite();
			List<CompositeBucket> buckets = compositeAgg.buckets().array();
			totalCount += buckets.size();
			log.info("Fetched " + buckets.size() + " buckets, total so far: " + totalCount);
			afterKey = compositeAgg.afterKey();
			// If no more pages, break
			if (afterKey == null || buckets.isEmpty()) {
				break;
			}
		}

		log.info("Total bucket count = " + totalCount);
		return totalCount;
	}

	private class RangeAggSpecification {
		@Getter
		@Setter
		protected String name;

		@Getter
		@Setter
		protected String field;

		@Getter
		@Setter
		protected long[][] ranges;

		public RangeAggSpecification(String n, String f, long[][] r) {
			name = n;
			field = f;
			ranges = r;
		}

	}

	public ElasticsearchClient getEsClient() {
		return esClient;
	}

	public void setRangeAggSpecification(String name, String field, long[][] ranges) {
		rangeSpec = new RangeAggSpecification(name, field, ranges);
	}

	public void clearRangeAggSpecification() {
		rangeSpec = null;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}
}
