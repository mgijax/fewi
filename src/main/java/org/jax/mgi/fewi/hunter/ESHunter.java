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
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.ExtendedBounds;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import lombok.Getter;
import lombok.Setter;

/**
 * This is the Solr specific hunter.  It is responsible for translating
 * higher-level generic webapp requests to Solr's specific API
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

        // Specs for histogram we cant to compute
        protected HistogramSpecification hist = null;

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

	// Solr Fields -> Front end field mappings
	protected HashMap <String, String> fieldToParamMap = new HashMap<String, String>();

	// Front end sorts -> backend fields
	protected HashMap <String, ESSortMapper> sortMap = new HashMap<String, ESSortMapper>();

	// FEWI's filter comparison -> Solr's comparison operator
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
        public void setHistogramSpecification(String name, String field, long interval, long min, long max) {
                hist = new HistogramSpecification(name, field, interval, min, max);
        }
        public void clearHistogramSpecification() {
                hist = null;
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

		// Invoke the hook, editing the search params as needed.

		createESConnection();

		// Create the query string by invoking the translate filter method.
		//SolrQuery query = new SolrQuery();

		String queryString = translateFilter(searchParams.getFilter(), propertyMap);

		//if(!searchParams.getSuppressLogs()) log.debug("TranslatedFilters: " + queryString);
		if(searchParams.getReturnFilterQuery()) searchResults.setFilterQuery(queryString);
		//if(!searchParams.getSuppressLogs()) log.info("ESQuery:" + queryString);

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
                        if (groupField != null) {
                                srb.aggregations(groupField, t -> t.terms(f -> f.field(groupField)));
                        }
                        if (facetString != null) {
                                srb.aggregations(facetString, t -> t.terms(f -> f.field(facetString).size(150)));
                        }
                        if (hist != null) {
                                ExtendedBounds.Builder ebb = (new ExtendedBounds.Builder()).min(hist.getMin()).max(hist.getMax());
                                srb.aggregations("heatmap", t -> t.histogram(f -> 
                                    f.field(hist.getField())
                                     .interval((double)hist.getInterval())
                                     .extendedBounds(ebb.build())
                                     ));
                        }
                        
                        SearchRequest searchRequest = srb.build();
                        log.info("Sending search request: " + searchRequest);
                        
                        resp = esClient.search(searchRequest, clazz);
                        log.info("Total hits: " + resp.hits().total().value());
			if (resp.aggregations().size() > 0) {
				log.info("Aggs: " + resp.aggregations());
                                if (facetString != null) {
                                        for(StringTermsBucket bucket: resp.aggregations().get(facetString).sterms().buckets().array()) {
                                                searchResults.getResultFacets().add(bucket.key().stringValue());
                                        }
                                        log.info("Facet results:" + searchResults.getResultFacets());
                                }
                                if (hist != null) {
                                        for(HistogramBucket bucket: resp.aggregations().get(hist.getName()).histogram().buckets().array()) {
                                                searchResults.getHistogram().put((long)bucket.key(), (long)bucket.docCount());
                                        }
                                        log.info("Histogram results:" + searchResults.getHistogram());
                                }
			}

			searchResults.setFilterQuery(queryString);
		
			for(Hit<T> hit: resp.hits().hits()) {
				searchResults.getResultKeys().add(hit.source().getConsensussnp_accid());
				searchResults.getResultObjects().add(hit.source());
			}

			searchResults.setTotalCount((int)resp.hits().total().value());

			//log.info(resp + "");

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

	protected void addSorts(SearchParams searchParams, SolrQuery query) {

		ORDER currentSort = null;

		for (Sort sort: searchParams.getSorts()) {

			// Determine the direction of the sort.

			if (sort.isDesc()) {
				currentSort = SolrQuery.ORDER.desc;
			}
			else {
				currentSort = SolrQuery.ORDER.asc;
			}

			/**
			 * Is this a configured sort?  If so check the sort map
			 * for 1->N Mappings.
			 */

			if (sortMap.containsKey(sort.getSort())) {
				for (String ssm: sortMap.get(sort.getSort()).getSortList()) {
					query.addSort(ssm,currentSort);
				}
			}

			/**
			 * Otherwise just add the sort in as is, Solr will ignore invalid
			 * sorts.
			 */

			else {
				query.addSort(sort.getSort(), currentSort);
			}
		}
	}

        private class HistogramSpecification {

                @Getter @Setter
                protected String name;

                @Getter @Setter
                protected String field;

                @Getter @Setter
                protected long interval;

                @Getter @Setter
                protected long min;

                @Getter @Setter
                protected long max;

                public HistogramSpecification(String n, String f, long i, long mn, long mx) {
                    name = n;
                    field = f;
                    interval = i;
                    min = mn;
                    max = mx;
                }
        }
}
