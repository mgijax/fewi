package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.jax.mgi.fewi.propertyMapper.SolrJoinMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.JoinClause;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * This is the Solr specific hunter. It is responsible for translating
 * higher-level generic webapp requests to Solr's specific API
 */
public class SolrHunter<T> implements Hunter<T> {

	public Logger logger = LoggerFactory.getLogger(getClass());

	/*----- INSTANCE VARIABLES -----*/

	/**
	 * Values set in the extending classes, determining each implementation's
	 * specific functionality
	 */

	// Which solr index?
	protected String solrUrl;
	// Which field is the document key?
	protected String keyString;
	// Which other field do we want to use as a key? (This could be collapsed)
	protected String otherString;
	// What is the name of the facet that we want to pull out
	protected String facetString = null;

	// For many (most?) queries, we don't care about document relevance score, just whether the doc matches or not.
	// If relevance score doesn't matter, we can improve performance by (sometimes a lot) by a slight change in execution.
	// See hunt() method below.
	protected boolean relevanceScoreMatters = true;

	/**
	 * solr server settings from configuration
	 */
	protected HttpSolrClient curServer = null;
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

	/**
	 * Mapping variables to map webapp request values to their 'columns' in Solr;
	 * useful in defining the 1->N mappings
	 */

	// Front end fields -> PropertyMappers
	protected HashMap<String, SolrPropertyMapper> propertyMap = new HashMap<String, SolrPropertyMapper>();

	// Solr Fields -> Front end field mappings
	protected HashMap<String, String> fieldToParamMap = new HashMap<String, String>();

	// Front end sorts -> backend fields
	protected HashMap<String, SolrSortMapper> sortMap = new HashMap<String, SolrSortMapper>();

	// FEWI's filter comparison -> Solr's comparison operator
	protected HashMap<JoinClause, String> filterClauseMap = new HashMap<JoinClause, String>();

	/*
	 * Fields to be returned in documents. 'score' is always returned, regardless
	 * All fields are returned by default
	 */
	protected List<String> returnedFields = new ArrayList<String>();
	protected Map<String, List<String>> groupReturnedFields = new HashMap<String, List<String>>();

	/**
	 * Highlighting
	 */

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

	/**
	 * Field Grouping
	 */
	// Fields that can be grouped on
	protected Map<String, String> groupFields = new HashMap<String, String>();

	/**
	 * Index joining
	 */
	// Indexes that can be joined on
	protected Map<String, SolrJoinMapper> joinIndices = new HashMap<String, SolrJoinMapper>();

	// shared connections when joining to other indices; string key maps to
	// connection
	protected Map<String, HttpSolrClient> joinedServers = new HashMap<String, HttpSolrClient>();

	/*----- CONSTRUCTOR -----*/

	public SolrHunter() {
		// Setup the mapping of the logical ands and or to the vendor
		// specific ones.

		filterClauseMap.put(Filter.JoinClause.FC_AND, " AND ");
		filterClauseMap.put(Filter.JoinClause.FC_OR, " OR ");

	}

	/*----- PUBLIC METHODS -----*/

	/**
	 * hunt
	 * 
	 * @param SearchParams, SearchResults
	 * @return none Classes of the hunter interface must implement the hunt method.
	 *         This method is primarily responsible for breaking down a Filter
	 *         object into its component parts. Once we are at that lowest level we
	 *         then invoke the SolrProperyMapper to get back the lowest level query
	 *         clauses. These will then in turn be joined together by this class,
	 *         with the result being a query to place against solr.
	 *
	 *         This method then runs the query, packages the results and exits. This
	 *         method is designed with the template methodology in mind, so nearly
	 *         all of the steps in the algorithm can be overwritten by implementing
	 *         classes.
	 */

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {
		hunt(searchParams, searchResults, null, null, null);
	}

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField) {
		hunt(searchParams, searchResults, groupField, null, null);
	}

	public void joinHunt(SearchParams searchParams, SearchResults<T> searchResults, String joinField) {
		hunt(searchParams, searchResults, null, joinField, null);
	}

	public void joinHunt(SearchParams searchParams, SearchResults<T> searchResults, String joinField, String extraJoinClause) {
		hunt(searchParams, searchResults, null, joinField, extraJoinClause);
	}

	public void joinGroupHunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField, String joinField) {
		hunt(searchParams, searchResults, groupField, joinField, null);
	}

	public void joinGroupHunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField, String joinField, String extraJoinClause) {
		hunt(searchParams, searchResults, groupField, joinField, extraJoinClause);
	}

	/**
	 * hunt
	 * 
	 * @param SearchParams, SearchResults, groupField
	 * @return none
	 *
	 *         An alternative implementation of the hunt() method. Allows
	 *         specification of a "group by" field.
	 *
	 */

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults, String groupField, String joinField, String extraJoinClause) {

		// Invoke the hook, editing the search params as needed.
		searchParams = preProcessSearchParams(searchParams);

		HttpSolrClient qServer;
		// Setup our interface into solr.
		boolean doJoin = false;
		if (joinField != null && joinIndices.containsKey(joinField)) {
			// If we do not already have a joined connection for that field, then we must
			// create one.
			if (!joinedServers.containsKey(joinField)) {
				joinedServers.put(joinField, createSolrConnection(joinIndices.get(joinField).getToIndexUrl()));
			}
			qServer = joinedServers.get(joinField);
			doJoin = true;
		} else {
			if (curServer == null)
				curServer = createSolrConnection();
			qServer = curServer;
		}
		// Create the query string by invoking the translate filter method.
		SolrQuery query = new SolrQuery();

		String queryString = translateFilter(searchParams.getFilter(), propertyMap);

		if (!searchParams.getSuppressLogs())
			logger.debug("TranslatedFilters: " + queryString);
		if (searchParams.getReturnFilterQuery())
			searchResults.setFilterQuery(queryString);

		// If a join field is specified add the join clause to the beginning of the
		// query string
		if (doJoin) {
			queryString = joinIndices.get(joinField).getJoinClause(queryString, extraJoinClause);
		}

		if (relevanceScoreMatters) {
			query.setQuery(queryString);
		} else {
			query.setQuery("*:*");
			query.addFilterQuery(queryString);
		}

		// Add group field, if passed in.
		boolean doGrouping = false;
		if (groupField != null && groupFields.containsKey(groupField)) {
			query.set(GroupParams.GROUP, true);
			// ensure that the count of groups is included.
			query.set(GroupParams.GROUP_TOTAL_COUNT, true);
			query.set(GroupParams.GROUP_FIELD, groupFields.get(groupField));
			doGrouping = true;
		}

		// configure which document fields to return
		// also make sure the score comes back
		if (!doJoin && doGrouping && groupReturnedFields.containsKey(groupField)) {
			List<String> docFields = new ArrayList<String>(this.groupReturnedFields.get(groupField));
			docFields.add("score");
			// I know the typecasting looks kludgy, but it's simpler than declaring array
			// sizes and looping.
			query.setFields(docFields.toArray(new String[0]));
		} else if (!doJoin && this.returnedFields.size() > 0) {
			List<String> docFields = new ArrayList<String>(returnedFields);
			docFields.add("score");
			// I know the typecasting looks kludgy, but it's simpler than declaring array
			// sizes and looping.
			query.setFields(docFields.toArray(new String[0]));
		} else
			query.setFields("*", "score"); // if none specified do *

		// Add in the Sorts from the search parameters.
		addSorts(searchParams, query);

		// Perform highlighting, assuming its needed. This method will take
		// care of determining that.
		if (searchParams.includeMetaHighlight() || searchParams.includeHighlightMarkup()) {
			addHighlightingFields(searchParams, query);
		}

		// Set the pagination parameters.
		query.setRows(searchParams.getPageSize());
		if (searchParams.getStartIndex() != -1) {
			query.setStart(searchParams.getStartIndex());
		} else {
			query.setStart(resultsDefault);
		}

		// Add the facets, can be overwritten.
		addFacets(query);
		if (!searchParams.getSuppressLogs())
			logger.info("SolrQuery:" + query);

		/**
		 * Run the query & package results & result count
		 */
		QueryResponse rsp = null;
		try {
			logger.debug("Running query & packaging searchResults");

			rsp = qServer.query(query, METHOD.POST);
			SolrDocumentList sdl = rsp.getResults();
			if (doGrouping) {
				// Package the results into the searchResults object by traversing GroupResponse
				// object.
				packInformationByGroup(rsp, searchResults, searchParams);
			} else if (doJoin) {
				// Package the results normally, but wrap them in a function that can be
				// overrided for custom implementation
				packInformationForJoin(rsp, searchResults, searchParams);
				// Set the total number found.
				searchResults.setTotalCount((int) sdl.getNumFound());
			} else {
				// Package the results into the searchResults object.
				packInformation(rsp, searchResults, searchParams);

				// Set the total number found.
				searchResults.setTotalCount((int) sdl.getNumFound());
			}

			// logger.debug("metaMapping: "
			// + searchResults.getResultSetMeta().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.debug("SolrHunter.hunt finished");
		return;

	}

	/*----- PROTECTED METHODS -----*/

	/**
	 * preprocessSearchParams
	 * 
	 * @param SearchParams
	 * @return SearchParams
	 *
	 *         This is a hook, any class that needs to modify the searchParams
	 *         before doing its work will override this method.
	 *
	 */
	protected SearchParams preProcessSearchParams(SearchParams searchParams) {
		return searchParams;
	}

	/**
	 * translateFilter
	 * 
	 * @param filter
	 * @param propertyMap
	 * @return a query string
	 *
	 *         This method is responsible for recursively tearing apart the filter
	 *         object and generating the query string.
	 *
	 *         It also handles any special cases that arise for a given technology.
	 *         In solr there is no real concept for IN or NOT IN, so we translate
	 *         those into a series of joined OR clauses.
	 *
	 */

	protected String translateFilter(Filter filter, HashMap<String, SolrPropertyMapper> propertyMap) {
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
		SolrPropertyMapper pm;
		if (!propertyMap.containsKey(filterProperty)) {
			pm = new SolrPropertyMapper(filterProperty);
		} else
			pm = propertyMap.get(filter.getProperty());

		if (filter.isBasicFilter()) {
			// Check to see if the property is null or an empty string,
			// if it is, return an empty string
			if (filterProperty == null || filterProperty.equals("")) {
				return "";
			}

			// If its not an IN or NOT IN, get the query clause and return it
			if (filter.getOperator() != Filter.Operator.OP_IN && filter.getOperator() != Filter.Operator.OP_NOT_IN && filter.getOperator() != Filter.Operator.OP_RANGE) {
				return pm.getClause(filter);
			}

			// Handle special range case
			else if (filter.getOperator() == Filter.Operator.OP_RANGE) {
				return pm.getClause(filter);
			}

			/**
			 * If its an IN or NOT IN or RANGE, break the query down further, joining
			 * thesubclauses by OR or AND as appropriate
			 */
			else {
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
					// Create the subclause, surround it in parens
					return notOp + field + ":(\"" + StringUtils.join(filter.getValues(), joinClause) + "\")";
				} else {
					// have to support the lazy programmer who feels the need to put multiple solr
					// fields into one property
					String fieldJoinClause = " " + pm.getJoinClause() + " ";

					// build the multiple field list
					List<String> queryClauses = new ArrayList<String>();
					for (String listField : pm.getFieldList()) {
						queryClauses.add(notOp + listField + ":(\"" + StringUtils.join(filter.getValues(), joinClause) + "\")");
					}
					return "(" + StringUtils.join(queryClauses, fieldJoinClause) + ")";
				}
			}
		}

		/**
		 * We do not have a simple filter, so recurse. When we get the return value join
		 * it appropriately into the query string using AND's or OR's as specified by
		 * the filterClause.
		 *
		 */

		else {

			if (filter.getJoinQuery() != null) {
			        String s = String.format("{!join fromIndex=%s from=%s to=%s v='%s'}",
				  filter.getFromIndex(), filter.getFromField(), filter.getToField(),
				  translateFilter(filter.getJoinQuery(), propertyMap));
				if (filter.isNegate()) s = "-(" + s + ")";
				return s;
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
					return negation + "(" + StringUtils.join(resultsString,
					    filterClauseMap.get(filter.getFilterJoinClause())) + ")";
				} else if (filter.isNegate()) {
					return negation + "(" + StringUtils.join(resultsString,
					    filterClauseMap.get(filter.getFilterJoinClause())) + ")";
				} else {
					return resultsString.get(0);
				}
			}
		}
	}

	public String debugFilter(Filter f) {
		return translateFilter(f, propertyMap);
	}

	/**
	 * setupSolrConnection
	 * 
	 * @param none
	 * @return none This default implementation is responsible for setting up the
	 *         connection to the configured Solr Index. Implementing classes can
	 *         override this template method in order to do special actions as
	 *         needed.
	 */

	protected HttpSolrClient createSolrConnection() {
		return createSolrConnection(solrUrl);
	}

	protected HttpSolrClient createSolrConnection(String url) {
		ModifiableSolrParams solrParams = new ModifiableSolrParams();
		solrParams.set(HttpClientUtil.PROP_ALLOW_COMPRESSION, true);
		solrParams.set(HttpClientUtil.PROP_SO_TIMEOUT, solrSoTimeout);
		solrParams.set(HttpClientUtil.PROP_CONNECTION_TIMEOUT, connectionTimeout);
		solrParams.set(HttpClientUtil.PROP_MAX_CONNECTIONS, maxTotalConnections);
		solrParams.set(HttpClientUtil.PROP_MAX_CONNECTIONS_PER_HOST, maxConnectionsPerHost);
		solrParams.set(HttpClientUtil.PROP_FOLLOW_REDIRECTS, false);
		solrParams.set(HttpClientUtil.PROP_USE_RETRY, maxRetries);

		HttpSolrClient server = null;
		logger.info("solrUrl->" + url);
		try {
			server = new HttpSolrClient.Builder(url).withInvariantParams(solrParams).build();
		} catch (Exception e) {
			System.out.println("Cannot reach the Solr server.");
			e.printStackTrace();
		}

		return server;
	}

	/**
	 * addHighlightingFields
	 * 
	 * @param SearchParams, SolrQuery
	 * @return none This method checks to see whether or not the highlightFields
	 *         variable has had any information placed into it.
	 *
	 *         If so, we go ahead and modify the Solr query to ask for these fields
	 *         to be highlighted, assuming the search itself asked for this to
	 *         occur.
	 *
	 *         This is done for performance reasons, as asking for highlighting
	 *         actually add to the workload that Solr is performing, as well as adds
	 *         the the amount of information that we need to pack and process.
	 */

	protected void addHighlightingFields(SearchParams searchParams, SolrQuery query) {
		if (!highlightFields.isEmpty() && searchParams.includeMetaHighlight()) {
			for (String field : highlightFields) {
				query.addHighlightField(field);
			}
			query.setHighlight(Boolean.TRUE);
			query.setHighlightFragsize(this.highlightFragmentSize);
			query.setHighlightSnippets(this.highlightSnippets);
			query.setHighlightRequireFieldMatch(this.highlightRequireFieldMatch);
			if (this.highlightPre != null) {
				query.setParam("hl.simple.pre", this.highlightPre);
				query.setParam("hl.simple.post", this.highlightPost);
			} else {
				query.setParam("hl.simple.pre", highlightToken);
				query.setParam("hl.simple.post", highlightToken);
			}
		}
	}

	/*
	 * add a request for Solr facets to the given 'query'
	 */
	protected void addFacets(SolrQuery query) {
		if (facetString != null) {
			query.addFacetField(facetString);
			query.setFacetMinCount(1);
			query.setFacetSort("index");
			query.setFacetLimit(factetNumberDefault);
		}
	}

	/**
	 * addSorts
	 * 
	 * @param SearchParams, SolrQuery
	 * @return none Tear apart the sort objects and add them to the query string.
	 *         This currently maps to a sortMapper object, which can turn a
	 *         conceptual single column sort from the wi's perspective to its
	 *         multiple column sort in the indexes.
	 *
	 *         We modify the query directly, so once this method completes we are
	 *         ready to continue processing.
	 */

	protected void addSorts(SearchParams searchParams, SolrQuery query) {

		ORDER currentSort = null;

		for (Sort sort : searchParams.getSorts()) {

			// Determine the direction of the sort.

			if (sort.isDesc()) {
				currentSort = SolrQuery.ORDER.desc;
			} else {
				currentSort = SolrQuery.ORDER.asc;
			}

			/**
			 * Is this a configured sort? If so check the sort map for 1->N Mappings.
			 */

			if (sortMap.containsKey(sort.getSort())) {
				for (String ssm : sortMap.get(sort.getSort()).getSortList()) {
					query.addSort(ssm, currentSort);
				}
			}

			/**
			 * Otherwise just add the sort in as is, Solr will ignore invalid sorts.
			 */

			else {
				query.addSort(sort.getSort(), currentSort);
			}
		}
	}

	/**
	 * packInformation
	 * 
	 * @param sdl
	 * @return List of keys This generic method is available to all extending
	 *         classes. All that they need to do to take advantage of it is to set
	 *         the keyString variable. This will then be used to extract a given
	 *         field from the returned documents as the key we want to return to the
	 *         wi.
	 *
	 *         If something more complex is required, the implementer is expected to
	 *         override this method with their own version.
	 */

	protected void packInformation(QueryResponse rsp, SearchResults<T> sr, SearchParams sp) {

		// A list of all the primary keys in the document
		List<String> keys = new ArrayList<String>();

		// A list of the documents scores.
		List<String> scoreKeys = new ArrayList<String>();

		// A listing of "otherStrings" for the documents.
		// These are used when the key isn't what we are
		// trying to get out of the document.
		List<String> info = new ArrayList<String>();

		// A listing of all of the facets. This is used
		// at the set level.
		List<String> facet = new ArrayList<String>();

		// A mapping of field -> set of highlighted words
		// for the result set.
		Map<String, List<String>> setHighlights = new HashMap<String, List<String>>();

		// A mapping of documentKey -> Mapping of FieldName
		// -> list of highlighted words.
		Map<String, Map<String, List<String>>> highlights = rsp.getHighlighting();

		// A mapping of documentKey -> Row level Metadata objects.
		Map<String, MetaData> metaList = new HashMap<String, MetaData>();

		SolrDocumentList sdl = rsp.getResults();

		/**
		 * Check for facets, if found pack them.
		 */

		if (this.facetString != null) {
			for (Count c : rsp.getFacetField(facetString).getValues()) {
				facet.add(c.getName());
				// if(!sp.getSuppressLogs()) logger.debug(c.getName());
			}
		}

		/**
		 * Iterate through the response documents, extracting the information that was
		 * configured at the implementing class level.
		 */

		for (SolrDocument doc : sdl) {
			/**
			 * Calculate the row level metadata. Currently this only applies to score, and
			 * whether or not a row is generated for autocomplete purposes.
			 */

			if (sp.includeRowMeta()) {

				MetaData tempMeta = new MetaData();
				if (sp.includeMetaScore()) {
					tempMeta.setScore("" + doc.getFieldValue("score"));
				}
				if (sp.includeGenerated()) {
					if (doc.getFieldValue(IndexConstants.AC_IS_GENERATED).equals(1)) {
						tempMeta.setGenerated();
					}
				}

				/**
				 * Store the metadata into a mapping based on whatever unique key that we can
				 * find.
				 */

				if (this.keyString != null) {
					metaList.put("" + doc.getFieldValue(keyString), tempMeta);
				}
				if (this.otherString != null) {
					metaList.put((String) doc.getFieldValue(otherString), tempMeta);
				}

			}

			/**
			 * In order to support older pages we pack the score directly as well as in the
			 * metadata.
			 */

			if (this.keyString != null) {
				keys.add("" + doc.getFieldValue(keyString));
				scoreKeys.add("" + doc.getFieldValue("score"));
			}

			if (this.otherString != null) {
				info.add((String) doc.getFieldValue(otherString));
			}

			/**
			 * If we have highlighted fields, and we've requested highlighting AND we have
			 * asked for result set meta. Include it. We are also translating from the Solr
			 * specific highlighting format to a simpler list of strings. The issue however
			 * is that we need to store this list so that its both document (or singular)
			 * result centric, as well as field centric. So we need to build a reasonably
			 * complicated map.
			 */

			if (!this.highlightFields.isEmpty() && sp.includeMetaHighlight() && sp.includeSetMeta()) {

				Set<String> highlightKeys = highlights.get(doc.getFieldValue(keyString)).keySet();
				Map<String, List<String>> highlightsMap = highlights.get(doc.getFieldValue(keyString));

				for (String key : highlightKeys) {
					List<String> solrHighlights = highlightsMap.get(key);
					for (String highlightWord : solrHighlights) {
						Boolean inAHL = Boolean.FALSE;
						/**
						 * Our solr highlights are surrounded by an impossible token, so split based on
						 * that.
						 */
						String[] fragments = highlightWord.split(highlightToken);

						/**
						 * Every other fragment will be a highlighted word setup a loop that iterates
						 * through the results grabbing it. Once we have it place it into the highlight
						 * mapping. The highlighted sections will be surrounded by our impossible token.
						 * As such when the string is split the highlighted tokens will be every other
						 * set of words.
						 */

						for (String frag : fragments) {

							/**
							 * We are in a highlighted section, parse out the matching word(s).
							 */

							if (inAHL) {
								if (setHighlights.containsKey(fieldToParamMap.get(key))) {

									setHighlights.get(fieldToParamMap.get(key)).add(frag);
								} else {
									setHighlights.put(fieldToParamMap.get(key), new ArrayList<String>());

									setHighlights.get(fieldToParamMap.get(key)).add(frag);
								}
								inAHL = Boolean.FALSE;
							}

							/**
							 * This is a non highlighted section, move on.
							 */

							else {
								inAHL = Boolean.TRUE;
							}
						}
					}
				}
			}
		}

		// Include the information that was asked for.

		if (keys != null) {
			sr.setResultKeys(keys);
		}

		if (info != null) {
			sr.setResultStrings(info);
		}

		if (facet != null) {
			sr.setResultFacets(facet);
		}

		if (sp.includeSetMeta()) {
			sr.setResultSetMeta(new ResultSetMetaData(setHighlights));
		}

		if (sp.includeRowMeta()) {
			sr.setMetaMapping(metaList);
		}

	}

	/**
	 * Version of the above method that is only called for join queries. This
	 * provides a way to separate custom implementations for packaging join query
	 * results
	 *
	 * @param rsp
	 * @param sr
	 * @param sp
	 */
	protected void packInformationForJoin(QueryResponse rsp, SearchResults<T> sr, SearchParams sp) {
		packInformation(rsp, sr, sp);
	}

	/**
	 * packInformation
	 * 
	 * @param sdl
	 * @return List of keys This generic method is available to all extending
	 *         classes. All that they need to do to take advantage of it is to set
	 *         the keyString variable. This will then be used to extract a given
	 *         field from the returned documents as the key we want to return to the
	 *         wi.
	 *
	 *         If something more complex is required, the implementer is expected to
	 *         override this method with their own version.
	 *
	 *         This method traverses the GroupResponse object, assuming that a group
	 *         query was done.
	 */

	protected void packInformationByGroup(QueryResponse rsp, SearchResults<T> sr, SearchParams sp) {

		GroupResponse gr = rsp.getGroupResponse();
		// get the group command. In our case, there should only be one.
		GroupCommand gc = gr.getValues().get(0);

		// total count of groups
		int groupCount = gc.getNGroups();
		sr.setTotalCount(groupCount);

		// A list of all the primary keys in the document
		List<String> keys = new ArrayList<String>();

		// A listing of all of the facets. This is used
		// at the set level.
		List<String> facet = new ArrayList<String>();

		// A mapping of field -> set of highlighted words
		// for the result set.
		Map<String, List<String>> setHighlights = new HashMap<String, List<String>>();

		// A mapping of documentKey -> Mapping of FieldName
		// -> list of highlighted words.
		// Map<String, Map<String, List<String>>> highlights =rsp.getHighlighting();

		// A mapping of documentKey -> Row level Metadata objects.
		Map<String, MetaData> metaList = new HashMap<String, MetaData>();

		List<Group> groups = gc.getValues();

		/**
		 * Check for facets, if found pack them.
		 */

		if (this.facetString != null) {
			for (Count c : rsp.getFacetField(facetString).getValues()) {
				facet.add(c.getName());
				if (!sp.getSuppressLogs())
					logger.debug(c.getName());
			}
		}

		/**
		 * Iterate through the response documents, extracting the information that was
		 * configured at the implementing class level.
		 */

		for (Group g : groups) {
			String key = g.getGroupValue();
			// int numFound = (int) g.getResult().getNumFound();

			/**
			 * In order to support older pages we pack the score directly as well as in the
			 * metadata.
			 */

			if (this.keyString != null) {
				keys.add(key);
				// scoreKeys.add("" + doc.getFieldValue("score"));
			}
		}

		// Include the information that was asked for.

		if (keys != null) {
			sr.setResultKeys(keys);
		}

		if (facet != null) {
			sr.setResultFacets(facet);
		}

		if (sp.includeSetMeta()) {
			sr.setResultSetMeta(new ResultSetMetaData(setHighlights));
		}

		if (sp.includeRowMeta()) {
			sr.setMetaMapping(metaList);
		}

	}

	public String getFacetString() {
		return facetString;
	}

	public void setFacetString(String facetString) {
		this.facetString = facetString;
	}
}
