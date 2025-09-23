package org.jax.mgi.fewi.hunter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.JoinClause;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.sortMapper.ESSortMapper;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.elasticsearch.esql.EsqlFormat;
import co.elastic.clients.elasticsearch.esql.QueryRequest;
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

public class ESHunter<T extends BaseESDocument> {

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

	// -----
	public void setRangeAggSpecification(String name, String field, long[][] ranges) {
		rangeSpec = new RangeAggSpecification(name, field, ranges);
	}

	public void clearRangeAggSpecification() {
		rangeSpec = null;
	}
	// -----

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {
		hunt(searchParams, searchResults, null, null);
	}

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField) {
		hunt(searchParams, searchResults, groupField, null);
	}

	public void joinHunt(SearchParams searchParams, SearchResults<T> searchResults, String joinField) {
		hunt(searchParams, searchResults, null, null);
	}

	public void joinHunt(SearchParams searchParams, SearchResults<T> searchResults, String joinField,
			String extraJoinClause) {
		hunt(searchParams, searchResults, null, extraJoinClause);
	}

	public void joinGroupHunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField,
			String joinField) {
		hunt(searchParams, searchResults, groupField, null);
	}

	public void joinGroupHunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField,
			String joinField, String extraJoinClause) {
		hunt(searchParams, searchResults, groupField, extraJoinClause);
	}

    /**
     * Retrieves the page size for the search request.  
     * <p>
     * By default, this method delegates to {@link SearchParams#getPageSize()}, 
     * but subclasses can override to apply custom logic.
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
     * {@code groupField}. Subclasses can override to define different
     * or more complex aggregations.
     *
     * @param searchParams the search parameters for the request
     * @param srb the Elasticsearch search request builder
     * @param groupField the field to group by in the aggregation
     * @param size the maximum number of aggregation buckets to return
     */
	protected void addAggregations(SearchParams searchParams, SearchRequest.Builder srb, String groupField, int size) {
		srb.aggregations(groupField, t -> t.terms(f -> f.field(groupField)));
	}

    /**
     * Processes the aggregation results from an Elasticsearch search response.  
     * <p>
     * By default, this implementation does nothing and returns {@code false}.
     * Subclasses should override to extract aggregation results and populate
     * {@link SearchResults}.
     *
     * @param resp the Elasticsearch search response
     * @param searchResults the container to hold processed search results
     * @param searchParams the search parameters associated with the request
     * @return {@code true} if aggregations were processed and applied, {@code false} otherwise
     */
	protected boolean processESSearchAggregation(SearchResponse resp, SearchResults<T> searchResults,
			SearchParams searchParams) {
		return false;
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
     * @param root the root {@link JsonNode} representing the lookup response payload
     * @return a list of parsed objects of type {@code T}
     * @throws Exception if an error occurs while parsing or converting the response
     */
	public List<T> processLookupResponse(JsonNode root) throws Exception {
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

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField,
			String extraJoinClause) {

		createESConnection();

		String queryString = translateFilter(searchParams.getFilter(), propertyMap);
		// if(!searchParams.getSuppressLogs()) log.debug("TranslatedFilters: " +
		// queryString);

		searchResults.setFilterQuery(queryString);
		if (searchParams.getReturnFilterQuery())
			searchResults.setFilterQuery(queryString);

		try {
			// log.info("Running query & packaging searchResults: " + esIndex);
			int size = getPageSize(searchParams);
			if (searchParams.getEsQuery() != null) {
				huntESQuery(searchParams, searchResults, queryString, size);
			} else {
				huntESSearch(searchParams, searchResults, groupField, extraJoinClause, queryString, size);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("ESHunter.hunt finished");

	}

	private void huntESQuery(SearchParams searchParams, SearchResults<T> searchResults, String queryString, int size)
			throws Exception {

		String query = searchParams.getEsQuery().toQuery(queryString, size, searchParams.isEsGroupCountOnly());

		log.info("esquery: " + query);
		BinaryResponse binResp = esClient.esql().query(QueryRequest.of(q -> q.query(query).format(EsqlFormat.Json)));

		JsonNode root = mapper.readTree(binResp.content());
		if (searchParams.isEsGroupCountOnly()) {
			int totalCount = root.path("values").get(0).get(0).asInt();
			searchResults.setTotalCount(totalCount);
		} else {
			List<T> results = processLookupResponse(root);
			searchResults.setResultObjects(results);
			searchResults.setTotalCount(results.size());
		}
		log.info("esql found: " + searchResults.getTotalCount());
	}

	protected String getAggUniqueCountKey(String groupField) {
		return "unique_" + groupField + "_count";
	}

	public void huntESSearch(SearchParams searchParams, SearchResults<T> searchResults, String groupField,
			String extraJoinClause, String queryString, int size) throws Exception {
		SearchResponse<T> resp = null;
		TrackHits.Builder th = new TrackHits.Builder();
		th.enabled(true);

		SearchRequest.Builder srb = new SearchRequest.Builder();
		QueryStringQuery.Builder qsb = new QueryStringQuery.Builder();
		qsb.query(queryString);
		srb.index(esIndex).query(qsb.build()._toQuery()).from(searchParams.getStartIndex()).trackTotalHits(th.build())
				.size(size);

		Highlight highlight = addHighlightingFields(searchParams);
		if (highlight != null) {
			srb.highlight(highlight);
		}
		addSorts(searchParams, srb);

		if (groupField != null) {
			if (searchParams.isEsGroupCountOnly()) {
				srb.aggregations(getAggUniqueCountKey(groupField), a -> a.cardinality(c -> c.field(groupField)));
			} else {
				addAggregations(searchParams, srb, groupField, size);
			}
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

		resp = esClient.search(searchRequest, clazz);
		log.info("Total hits: " + resp.hits().total().value());

		if (resp.aggregations().size() > 0) {
			if (searchParams.isEsGroupCountOnly()) {
				Aggregate agg = resp.aggregations().get(getAggUniqueCountKey(groupField));
				int uniqueCount = (int) agg.cardinality().value();
				searchResults.setTotalCount(uniqueCount);
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
			searchResults.getResultKeys().add(hit.source().getConsensussnp_accid());
			searchResults.getResultObjects().add(hit.source());
		}
		searchResults.setTotalCount((int) resp.hits().total().value());
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
			// Check to see if the property is null or an empty string,
			// if it is, return an empty string
			if (filterProperty == null || filterProperty.equals("")) {
				return "";
			}

			if (filter.getOperator() != Filter.Operator.OP_IN && filter.getOperator() != Filter.Operator.OP_NOT_IN
					&& filter.getOperator() != Filter.Operator.OP_RANGE) {
				
				 // NOTE: Solr uses "nomenclature" to filter out the marker/genes. In ES it does not work. Need
				 // to switch to use "markerSymbol". To keep the same UI, but switch the field here. Later to change 
				 // UI and here at the same time
				if ("nomenclature".equals(filterProperty)) {
					return toMarkerSymbolQuery(filter);
				} else {
					return pm.getClause(filter);
				}
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
				return resultsString.get(0);
			}
		}
	}

	/**
	 * NOTE: Solr uses "nomenclature" to filter out the marker/genes. In ES it does not work. Need
	 * to switch to user "markerSymbol". To keep the same UI, switch the field here.
	 * 
	 * Builds an Elasticsearch query string to search by marker symbol 
	 * based on the provided filter.
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

	protected void createESConnection() {
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
}
