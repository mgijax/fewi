package org.jax.mgi.fewi.hunter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.ESSearchOption;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.JoinClause;
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
import co.elastic.clients.elasticsearch._types.GeoShapeRelation;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.LongTermsBucket;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
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

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {
		ESSearchOption searchOption = new ESSearchOption();
		searchOption.setGetTotalCount(true);
		hunt(searchParams, searchResults, searchOption);
	}

	public <T extends ESEntity> void hunt(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption) {

		createESConnection();

		String queryString = translateFilter(searchParams.getFilter(), propertyMap);

		// collect shape filter differently
		List<Filter> shapeFilters = collectShapeFilters(searchParams.getFilter());

		if (searchParams.getReturnFilterQuery())
			searchResults.setFilterQuery(queryString);

		try {
			// log.info("Running query & packaging searchResults: " + esIndex);
			int size = getPageSize(searchParams);
			if (searchOption.getEsQuery() != null) {
				huntESQuery(searchParams, searchResults, queryString, size, shapeFilters, searchOption);
			} else {
				huntESSearch(searchParams, searchResults, searchOption, queryString, size, shapeFilters);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("ESHunter.hunt finished");
	}

	public <T extends ESEntity> void huntESSearch(SearchParams searchParams, SearchResults<T> searchResults,
			ESSearchOption searchOption, String queryString, int size, List<Filter> shapeFilters) throws Exception {

		String groupField = searchOption.getGroupField();

		SearchRequest.Builder srb = new SearchRequest.Builder();
		srb.index(esIndex);

		setSearchQuery(srb, queryString, shapeFilters);

		SearchResponse<T> resp = null;
		TrackHits.Builder th = new TrackHits.Builder();
		th.enabled(true);
		srb.trackTotalHits(th.build());

		Highlight highlight = addHighlightingFields(searchParams);
		if (highlight != null) {
			srb.highlight(highlight);
		}
		addSorts(searchParams, srb);

		if (groupField != null) {
			// for aggregation, need to set the size of bucket, not the size of doc, and
			// from too
			if (searchOption.isGetTotalCount()) {
				srb.aggregations(getAggUniqueCountKey(groupField), a -> a.cardinality(c -> c.field(groupField)));
			} else {
				if (searchOption.isGetGroupFirstDoc()) {
					addGroupGetFirstDoc(searchParams, srb, groupField, searchParams.getStartIndex(), size);
				} else {
					srb.aggregations(groupField, t -> t.terms(f -> f.field(groupField).size(size)));

					srb.aggregations(groupField, t -> t
							.terms(f -> f.field(groupField).size(searchParams.getStartIndex() + size))
							.aggregations("bucket_pagination",
									a -> a.bucketSort(bs -> bs.from(searchParams.getStartIndex()).size(size)
											.sort(s -> s.field(fld -> fld.field("_key").order(SortOrder.Asc))))));
				}
			}
			srb.size(0);
		} else {
			srb.from(searchParams.getStartIndex());
			srb.size(size);
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

		resp = (SearchResponse<T>) esClient.search(searchRequest, clazz);
		log.info("Total hits: " + resp.hits().total().value());

		if (resp.aggregations().size() > 0) {
			if (searchOption.isGetTotalCount()) {
				Aggregate agg = resp.aggregations().get(getAggUniqueCountKey(groupField));
				int uniqueCount = (int) agg.cardinality().value();
				searchResults.setTotalCount(uniqueCount);
				return;
			}

			if (searchOption.isGetGroupInfo()) {
				processGroupFieldValues(resp, searchResults, groupField);
				return;
			}

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

			if (processESSearchAggregation(resp, searchResults, searchParams)) {
				log.debug("no need further processing for aggregation");
				return;
			}
		}

		for (Hit<T> hit : resp.hits().hits()) {
			if (hit.source() instanceof BaseESDocument) {
				searchResults.getResultKeys().add(((BaseESDocument) hit.source()).getConsensussnp_accid());
			}
			searchResults.getResultObjects().add(hit.source());
		}
		if (searchOption.isGetTotalCount()) {
			searchResults.setTotalCount((int) resp.hits().total().value());
		}
	}

	private <T extends ESEntity> void huntESQuery(SearchParams searchParams, SearchResults<T> searchResults,
			String queryString, int size, List<Filter> shapeFilters, ESSearchOption searchOption) throws Exception {

		List<String> whereCauses = new ArrayList<String>();
		if (queryString != null && !queryString.isEmpty()) {
			whereCauses.add(queryString.replaceAll(":", "==").replaceAll("~100", ""));
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

		String query = searchOption.getEsQuery().toQuery(whereCauses, size, searchOption.isGetTotalCount());
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

	private void setSearchQuery(SearchRequest.Builder srb, String queryString, List<Filter> shapeFilters) {
		List<Query> queryList = new ArrayList<>();
		// create "Query String Query"
		if (queryString != null && !queryString.isEmpty()) {
			QueryStringQuery.Builder qsb = new QueryStringQuery.Builder();
			qsb.query(queryString);
			Query queryStringQuery = new QueryStringQuery.Builder().query(queryString).build()._toQuery();
			queryList.add(queryStringQuery);
		}

		// "Shape Query"
		if (shapeFilters != null && !shapeFilters.isEmpty()) {
			Query shapeQuery = getShapeQuery(queryString, shapeFilters);
			queryList.add(shapeQuery);
		}

		if (!queryList.isEmpty()) {
			if (queryList.size() == 1) {
				srb.query(queryList.get(0));
			} else {
				// combine "Query String Query" and "Shape Query" together
				BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
				for (Query query : queryList) {
					boolBuilder.must(query);
				}
				Query combinedQuery = new Query.Builder().bool(boolBuilder.build()).build();
				srb.query(combinedQuery);
			}
		}
	}

	private <T extends ESEntity> void processGroupFieldValues(SearchResponse<T> resp, SearchResults searchResults,
			String groupField) {
		Aggregate agg = resp.aggregations().get(groupField);
		if (agg.isSterms()) {
			StringTermsAggregate stringAgg = agg.sterms();
			List<ESAggStringCount> resultObjects = new ArrayList<ESAggStringCount>();
			for (StringTermsBucket term : stringAgg.buckets().array()) {
				resultObjects.add(new ESAggStringCount(term.key().stringValue(), term.docCount()));
			}
			searchResults.setResultObjects(resultObjects);
		} else if (agg.isLterms()) {
			LongTermsAggregate longAgg = agg.lterms();
			List<ESAggLongCount> resultObjects = new ArrayList<ESAggLongCount>();
			for (LongTermsBucket term : longAgg.buckets().array()) {
				resultObjects.add(new ESAggLongCount(term.key(), term.docCount()));
			}
			searchResults.setResultObjects(resultObjects);
		}
	}

	protected List<Filter> collectShapeFilters(Filter filter) {
		if (filter == null)
			return null;

		if (filter.isBasicFilter() && filter.getOperator() == Filter.Operator.OP_SHAPE) {
			if (filter.getValue() != null && !filter.getValue().isEmpty()) {
				return new ArrayList<>(List.of(filter));
			}
		}

		List<Filter> shapeFilters = new ArrayList<Filter>();
		List<Filter> filters = filter.getNestedFilters();
		for (Filter f : filters) {
			List<Filter> temp = collectShapeFilters(f);
			if (temp != null) {
				shapeFilters.addAll(temp);
			}
		}
		return shapeFilters;
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

		Query shapeQuery = Query.of(q -> q.shape(g -> g.field("mc")
				.shape(s -> s.relation(GeoShapeRelation.Intersects).shape(JsonData.of(geometryCollection)))));
		return shapeQuery;
	}

	/**
	 * Processes the JSON response returned from a lookup request and converts it
	 * into a list of domain objects.
	 * <p>
	 * This method is responsible for parsing the provided {@link JsonNode}
	 * structure and mapping its contents into a list of {@code T} instances.
	 * Subclasses or implementations can override this method to customize the
	 * parsing and mapping logic.
	 *
	 * @param root the root {@link JsonNode} representing the lookup response
	 *             payload
	 * @return a list of parsed objects of type {@code T}
	 * @throws Exception if an error occurs while parsing or converting the response
	 */
	public <T extends ESEntity> List<T> processLookupResponse(JsonNode root, Class<T> clazz) throws Exception {
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
			if (filter.getOperator() == Filter.Operator.OP_SHAPE) {
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

	/**
	 * Retrieves the page size for the search request.
	 * <p>
	 * By default, this method delegates to {@link SearchParams#getPageSize()}, but
	 * subclasses can override to apply custom logic.
	 *
	 * @param searchParams the search parameters containing pagination information
	 * @return the number of results per page
	 */
	protected int getPageSize(SearchParams searchParams) {
		return searchParams.getPageSize();
	}

	/**
	 * Adds one or more aggregations to the Elasticsearch search request.
	 * <p>
	 * By default, this method applies a simple terms aggregation on the given
	 * {@code groupField}. Subclasses can override to define different or more
	 * complex aggregations.
	 *
	 * @param searchParams the search parameters for the request
	 * @param srb          the Elasticsearch search request builder
	 * @param groupField   the field to group by in the aggregation
	 * @param size         the maximum number of aggregation buckets to return
	 */
	protected void addGroupGetFirstDoc(SearchParams searchParams, SearchRequest.Builder srb, String groupField,
			int from, int size) {
		List<String> returnFields = new ArrayList<String>();
		if (GxdResultFields.MARKER_SYMBOL.equals(groupField)) {
			returnFields = List.of(GxdResultFields.MARKER_MGIID, GxdResultFields.MARKER_SYMBOL,
					GxdResultFields.MARKER_NAME, GxdResultFields.MARKER_TYPE, GxdResultFields.CHROMOSOME,
					GxdResultFields.CENTIMORGAN, GxdResultFields.CYTOBAND, GxdResultFields.START_COORD,
					GxdResultFields.END_COORD, GxdResultFields.STRAND);
		} else if (GxdResultFields.ASSAY_KEY.equals(groupField)) {
			returnFields = List.of(GxdResultFields.MARKER_SYMBOL, GxdResultFields.ASSAY_KEY,
					GxdResultFields.ASSAY_MGIID, GxdResultFields.ASSAY_TYPE, GxdResultFields.JNUM,
					GxdResultFields.SHORT_CITATION);
		} else if (GxdResultFields.STRUCTURE_EXACT.equals(groupField)) {
			returnFields = List.of(GxdResultFields.STRUCTURE_EXACT);
		} else if (GxdResultFields.THEILER_STAGE.equals(groupField)) {
			returnFields = List.of(GxdResultFields.THEILER_STAGE);
		}

		NamedValue<SortOrder> keySort = NamedValue.of("_key", SortOrder.Asc);
		List<String> fprop = returnFields;
		srb.aggregations(groupField, a -> a
				.terms(t -> t.field(groupField).size(from + size).order(Collections.singletonList(keySort)))
				.aggregations("first_doc",
						subAgg -> subAgg.topHits(th -> th.size(1).source(src -> src.filter(f -> f.includes(fprop)))))
				.aggregations("bucket_pagination", subAgg -> subAgg.bucketSort(bs -> bs.from(from).size(size)
						.sort(List.of(SortOptions.of(so -> so.field(f -> f.field("_key").order(SortOrder.Asc))))))));
	}

	/**
	 * Processes the aggregation results from an Elasticsearch search response.
	 * <p>
	 * By default, this implementation does nothing and returns {@code false}.
	 * Subclasses should override to extract aggregation results and populate
	 * {@link SearchResults}.
	 *
	 * @param resp          the Elasticsearch search response
	 * @param searchResults the container to hold processed search results
	 * @param searchParams  the search parameters associated with the request
	 * @return {@code true} if aggregations were processed and applied,
	 *         {@code false} otherwise
	 */
	protected <T extends ESEntity> boolean processESSearchAggregation(SearchResponse resp,
			SearchResults<T> searchResults, SearchParams searchParams) {
		return false;
	}

	/**
	 * NOTE: Solr uses "nomenclature" to filter out the marker/genes. In ES it does
	 * not work. Need to switch to user "markerSymbol". To keep the same UI, switch
	 * the field here.
	 * 
	 * Builds an Elasticsearch query string to search by marker symbol based on the
	 * provided filter.
	 *
	 * @param f the {@link Filter} containing marker-related criteria
	 * @return a query string formatted for Elasticsearch marker symbol search
	 */
	private String toMarkerSymbolQuery(Filter filter) {
		StringBuffer ret = new StringBuffer();
		if (filter.getValues() == null || filter.getValues().isEmpty()) {
			ret.toString();
		}
		ret.append("(");
		int cnt = 0;
		for (String val : filter.getValues()) {
			if (cnt > 0) {
				ret.append(" OR ");
			}
			String cleanVal = val.replace("~100", "").replaceAll("^\\s*\"|\"\\s*$", "").trim();
			ret.append(GxdResultFields.MARKER_SYMBOL + ":\"" + cleanVal + "\"");
			cnt++;
		}
		ret.append(")");

		return ret.toString();
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
}
