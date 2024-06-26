package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback;
import org.jax.mgi.fewi.propertyMapper.ESPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.JoinClause;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.sortMapper.ESSortMapper;
import org.jax.mgi.snpdatamodel.document.BaseESDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;

import co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;

import lombok.Getter;
import lombok.Setter;

/**
 * This is the Elastic specific hunter.  It is responsible for translating
 * higher-level generic webapp requests to Elastic's specific API
 */

public class ESHunter<T extends BaseESDocument> {

	public Logger log = LoggerFactory.getLogger(getClass());
	
	// Which field is the document key?
	protected String keyString;
	// Which other field do we want to use as a key? (This could be collapsed)
	protected String otherString;

	// What is the name of the facet that we want to pull out
	@Getter @Setter
	protected String facetString = null;

        protected RangeAggSpecification rangeSpec = null;

	//protected JacksonJsonpMapper jom = new JacksonJsonpMapper();
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

	// Front end fields -> PropertyMappers
	protected HashMap <String, ESPropertyMapper> propertyMap = new HashMap<String, ESPropertyMapper>();

	// Elastic Fields -> Front end field mappings
	protected HashMap <String, String> fieldToParamMap = new HashMap<String, String>();

	// Front end sorts -> backend fields
	protected HashMap <String, ESSortMapper> sortMap = new HashMap<String, ESSortMapper>();

	// FEWI's filter comparison -> Elastic's comparison operator
	protected HashMap<JoinClause, String> filterClauseMap = new HashMap<JoinClause, String>();

	protected List<String> returnedFields = new ArrayList<String>();
	protected Map<String,List<String>> groupReturnedFields = new HashMap<String,List<String>>();


	// Fields to highlight
	protected List <String> highlightFields = new ArrayList<String> ();

	// unique token used for highlighting
	protected final String highlightToken = "!FRAG!";
	// OR set pre/post tokens (not to be used with highlightToken)
	// 	Setting these makes the hunter ignore the highlightToken above
	protected String highlightPre = null;
	protected String highlightPost = null;


	protected boolean highlightRequireFieldMatch = true;
	protected int highlightFragmentSize = 30000;
	protected int highlightSnippets = 100;

	protected Map<String,String> groupFields = new HashMap<String,String>();

	private Class<T> clazz;

	public ESHunter(Class<T> clazz) {
		this.clazz = clazz;
		filterClauseMap.put(Filter.JoinClause.FC_AND, " AND ");
		filterClauseMap.put(Filter.JoinClause.FC_OR, " OR ");
	}

        //-----
        public void setRangeAggSpecification(String name, String field, long[][] ranges) {
                rangeSpec = new RangeAggSpecification(name, field, ranges);
        }
        public void clearRangeAggSpecification() {
                rangeSpec = null;
        }
        //-----

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {
		hunt(searchParams, searchResults, null, null);
	}

	public void hunt(SearchParams searchParams,SearchResults<T> searchResults, String groupField) {
		hunt(searchParams, searchResults, groupField, null);
	}

	public void joinHunt(SearchParams searchParams,SearchResults<T> searchResults, String joinField) {
		hunt(searchParams, searchResults, null, null);
	}

	public void joinHunt(SearchParams searchParams, SearchResults<T> searchResults, String joinField, String extraJoinClause) {
		hunt(searchParams, searchResults, null, extraJoinClause);
	}

	public void joinGroupHunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField, String joinField) {
		hunt(searchParams, searchResults, groupField, null);
	}

	public void joinGroupHunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField, String joinField, String extraJoinClause) {
		hunt(searchParams, searchResults, groupField, extraJoinClause);
	}

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField, String extraJoinClause) {

		createESConnection();

		String queryString = translateFilter(searchParams.getFilter(), propertyMap);
		//if(!searchParams.getSuppressLogs()) log.debug("TranslatedFilters: " + queryString);

		if(searchParams.getReturnFilterQuery()) searchResults.setFilterQuery(queryString);

		SearchResponse<T> resp = null;
		try {
			//log.info("Running query & packaging searchResults: " + esIndex);

                        TrackHits.Builder th = new TrackHits.Builder();
                        th.enabled(true);

                        SearchRequest.Builder srb = new SearchRequest.Builder();
                        
                        QueryStringQuery.Builder qsb = new QueryStringQuery.Builder();
                        qsb.query(queryString);
                        srb.index(esIndex)
                                .query(qsb.build()._toQuery())
                                .from(searchParams.getStartIndex())
                                .trackTotalHits(th.build())
                                .size(searchParams.getPageSize());

                        addSorts(searchParams, srb);

                        if (groupField != null) {
                                srb.aggregations(groupField, t -> t.terms(f -> f.field(groupField)));
                        }
                        if (facetString != null) {
                                srb.aggregations(facetString, t -> t.terms(f -> f.field(facetString).size(150)));
                        }
                        if (rangeSpec != null) {
                                List<AggregationRange> aggRanges = new ArrayList<AggregationRange>();
                                for (long[] r : rangeSpec.getRanges()) {
                                    AggregationRange.Builder arb = new AggregationRange.Builder();
                                    aggRanges.add(arb.from(""+r[0]).to(""+r[1]).build());
                                }
                                srb.aggregations(rangeSpec.getName(), t -> t.range(f -> 
                                    f.field(rangeSpec.getField())
                                     .ranges(aggRanges)
                                     ));
                        }
                        
                        SearchRequest searchRequest = srb.build();
                        log.info("Sending search request: " + searchRequest);
                        
                        resp = esClient.search(searchRequest, clazz);
                        log.info("Total hits: " + resp.hits().total().value());

			if (resp.aggregations().size() > 0) {
				//log.info("Aggs: " + resp.aggregations());
                                if (facetString != null) {
                                        for(StringTermsBucket bucket: resp.aggregations().get(facetString).sterms().buckets().array()) {
                                                searchResults.getResultFacets().add(bucket.key().stringValue());
                                        }
                                }
                                if (rangeSpec != null) {
                                        for(RangeBucket bucket: resp.aggregations().get(rangeSpec.getName()).range().buckets().array()) {
                                                long[] b = { bucket.from().longValue(), bucket.to().longValue(), bucket.docCount() };
                                                searchResults.getHistogram().add(b);
                                        }
                                }
			}

			searchResults.setFilterQuery(queryString);
		
			for(Hit<T> hit: resp.hits().hits()) {
				searchResults.getResultKeys().add(hit.source().getConsensussnp_accid());
				searchResults.getResultObjects().add(hit.source());
			}

			searchResults.setTotalCount((int)resp.hits().total().value());

		} catch (Exception e) {
			e.printStackTrace();
		}

		log.debug("ESHunter.hunt finished");

	}

	protected String translateFilter(Filter filter, HashMap<String, ESPropertyMapper> propertyMap) {
		/**
		 * This is the end case for the recursion.  If we are at a node in the
		 * tree generate the chunk of query string for that node and return
		 * it back to the caller.
		 *
		 * An important concept here is the propertyMap, this mapping handles
		 * the query clause generation for a given single property.  This
		 * allows us to handle any special cases for properties using a single
		 * interface.
		 */
		if(filter==null) return "";

		String filterProperty = filter.getProperty();
		ESPropertyMapper pm;
		if(!propertyMap.containsKey(filterProperty)) {
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

			if (filter.getOperator() != Filter.Operator.OP_IN && filter.getOperator() != Filter.Operator.OP_NOT_IN && filter.getOperator() != Filter.Operator.OP_RANGE) {
				return pm.getClause(filter);
			} else if(filter.getOperator() == Filter.Operator.OP_RANGE) {
				return pm.getClause(filter);
			} else {
				String joinClause = "\" OR \"";
				String field = pm.getField();


				// The not operator works differently then then - operator one minuses the records 
				// The other takes the inverse of the records.
				String notOp = "";
				if(filter.getOperator() == Filter.Operator.OP_NOT_IN) {
					notOp = "!";
				}

				if(!"".equals(field)) {
					return notOp + field + ":(\"" + StringUtils.join(filter.getValues(), joinClause) + "\")";
				} else {
					// have to support the lazy programmer who feels the need to put multiple solr fields into one property
					String fieldJoinClause = " " + pm.getJoinClause() + " ";

					// build the multiple field list
					List<String> queryClauses = new ArrayList<String>();
					for(String listField : pm.getFieldList()) {
						queryClauses.add(notOp + listField + ":(\"" + StringUtils.join(filter.getValues(), joinClause) + "\")");
					}
					return "(" + StringUtils.join(queryClauses,fieldJoinClause) + ")";
				}
			}
		} else {
			List<Filter> filters = filter.getNestedFilters();

			List<String> resultsString = new ArrayList<String>();

			for (Filter f: filters) {

				String tempString = translateFilter(f, propertyMap);
				if (! tempString.equals("")) {
					resultsString.add(tempString);
				}
			}
			// handle negating a nested filter
			String negation = filter.isNegate() ? "-" : "";
			if(resultsString.size() > 1) {
				return negation + "(" + StringUtils.join(resultsString, filterClauseMap.get(filter.getFilterJoinClause())) + ")";
			} else if(filter.isNegate()) {
				return negation + "(" + StringUtils.join(resultsString, filterClauseMap.get(filter.getFilterJoinClause())) + ")";
			} else {
				return resultsString.get(0);
			}
		}
	}

	public String debugFilter(Filter f) {
		return translateFilter(f, propertyMap);
	}


	protected void createESConnection() {
		if (esClient == null) {
			RestClientBuilder client = RestClient.builder(new HttpHost(esHost, Integer.parseInt(esPort)));
			client.setRequestConfigCallback(new RequestConfigCallback() {
				public Builder customizeRequestConfig(Builder requestConfigBuilder) {
					int hour = (60 * 60 * 1000);
					int hours = 2 * hour;
					return requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(hours).setConnectionRequestTimeout(hours);
				}
			});
			ElasticsearchTransport transport = new RestClientTransport(client.build(), new JacksonJsonpMapper());
			esClient = new ElasticsearchClient(transport);

			log.info("Finished Connecting to ES client: " + client);
		}
	}

        // Not yet converted to Elastic!!
	protected void addHighlightingFields(SearchParams searchParams, SolrQuery query) {
		if (! highlightFields.isEmpty() && searchParams.includeMetaHighlight()) {
			for (String field: highlightFields) {
				query.addHighlightField(field);
			}
			query.setHighlight(Boolean.TRUE);
			query.setHighlightFragsize(this.highlightFragmentSize);
			query.setHighlightSnippets(this.highlightSnippets);
			query.setHighlightRequireFieldMatch(this.highlightRequireFieldMatch);
			if(this.highlightPre!=null)
			{
				query.setParam("hl.simple.pre", this.highlightPre);
				query.setParam("hl.simple.post", this.highlightPost);
			}
			else
			{
				query.setParam("hl.simple.pre", highlightToken);
				query.setParam("hl.simple.post", highlightToken);
			}
		}
	}

	protected void addSorts(SearchParams searchParams, SearchRequest.Builder srb) {

		SortOrder currentSort = null;
                FieldSort.Builder fsb = null;
                FieldSort fs = null;
                SortOptions so = null;
                SortOptions.Builder sob = null;

		for (Sort sort: searchParams.getSorts()) {

			// Determine the direction of the sort.

			if (sort.isDesc()) {
				currentSort = SortOrder.Desc;
			}
			else {
				currentSort = SortOrder.Asc;
			}

			/**
			 * Is this a configured sort?  If so check the sort map
			 * for 1->N Mappings.
			 */

			if (sortMap.containsKey(sort.getSort())) {
                                List<SortOptions> sol = new ArrayList<SortOptions>();
				for (String ssm: sortMap.get(sort.getSort()).getSortList()) {
                                        fsb = new FieldSort.Builder();
                                        fs = fsb.field(ssm).order(currentSort).build();
                                        sob = new SortOptions.Builder();
                                        so = sob.field(fs).build();
                                        sol.add(so);
				}
                                srb.sort(sol);
			}

			/**
			 * Otherwise just add the sort in as is, Solr will ignore invalid
			 * sorts.
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
                @Getter @Setter
                protected String name;

                @Getter @Setter
                protected String field;

                @Getter @Setter
                protected long[][] ranges;

                public RangeAggSpecification(String n, String f, long[][] r) {
                    name = n;
                    field = f;
                    ranges = r;
                }
        }
}
