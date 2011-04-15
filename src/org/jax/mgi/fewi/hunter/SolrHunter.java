package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.PropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
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
 * This is the Solr specific hunter.  It is responsible for mapping the higher
 * level join type queries that are coming from the WI and translating them
 * into something that Solr can understand.  It is also responsible for
 * mapping the fe's idea of an in or not in clause to something that makes
 * sense for solr.
 *
 * @author mhall
 *
 */

public class SolrHunter implements Hunter {

    /**
     * These strings all have their values set in the extending classes.
     */

    protected String solrUrl;
    protected String keyString;
    protected String otherString;
    protected String facetString;    
    
    // Gather the solr server settings from configuration
    
    @Value("${solr.soTimeout}")
    private Integer solrSoTimeout;
    @Value("${solr.connectionTimeout}")
    private Integer connectionTimeout;
    @Value("${solr.maxConnectionsPerHost}")
    private Integer maxConnectionsPerHost;
    @Value("${solr.maxTotalConnections}")
    private Integer maxTotalConnections; 
    @Value("${solr.maxRetries}")
    private Integer maxRetries; 
    
    // Gather the default sizes for facets and results.  We will use these
    // unless they are overidden by the request
    
    @Value("${solr.resultsDefault}")
    private Integer resultsDefault; 
    @Value("${solr.factetNumberDefault}")
    private Integer factetNumberDefault; 

    protected HashMap <String, PropertyMapper> propertyMap =
        new HashMap<String, PropertyMapper>();
    
    protected HashMap <String, String> fieldToParamMap = 
        new HashMap<String, String>();
    
    protected HashMap <String, SolrSortMapper> sortMap =
        new HashMap<String, SolrSortMapper>();

    protected List <String> highlightFields = new ArrayList<String> ();
    
    public Logger logger = LoggerFactory.getLogger(this.getClass());
    
    // Setup the highlight token once.
    
    private final String highlightToken = "!FRAG!";

    /**
     * Here we map the higher level join clauses to their
     * respective Solr values.
     */

    private static HashMap<Integer, String> filterClauseMap =
        new HashMap<Integer, String>();
    
    
    public SolrHunter() {
        // Setup the mapping of the logical ands and or to the vendor
        // specific ones.

        filterClauseMap.put(Filter.FC_AND, " AND ");
        filterClauseMap.put(Filter.FC_OR, " OR ");

    }

    /**
     * Classes of the hunter interface must implement the hunt method.
     * This method is primarily responsible for breaking down a Filter
     * object into its component parts.  Once we are at that lowest level
     * we then invoke the SolrProperyMapper to get back the lowest level
     * query clauses.  These will then in turn be joined together by this
     * class, with the result being a query to place against solr.
     *
     * This method then runs the query, packages the results and exits.
     *
     */

    @Override
    public void hunt(SearchParams searchParams, SearchResults searchResults) {


    	/**
    	 * Invoke the hook, editing the search params as needed.
    	 */
        searchParams = this.preProcessSearchParams(searchParams);

        /**
         * Setup our interface into solr.  These are auto injected by spring
         * at load time.  The solrUrl is provided by the implementing classes.
         */

        
        CommonsHttpSolrServer server = null;
        
        try { server = new CommonsHttpSolrServer(solrUrl);}
        catch (Exception e) {
        	System.out.println("Cannot reach the Solr server.");
            e.printStackTrace();
            }

        logger.info("SolrTimeout:" + solrSoTimeout);
        
        server.setSoTimeout(solrSoTimeout);  // socket read timeout
        server.setConnectionTimeout(connectionTimeout);
        server.setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
        server.setMaxTotalConnections(maxTotalConnections);
        server.setFollowRedirects(false);  // defaults to false
        server.setAllowCompression(true);
        server.setMaxRetries(maxRetries);

        SolrQuery query = new SolrQuery();

        String queryString =
            translateFilter(searchParams.getFilter(), propertyMap);
        logger.info(queryString);
        query.setQuery(queryString);

        query.setFields("score"); // Always pack the score
        
        /**
         * Tear apart the sort objects and add them to the query string.  
         * This currently maps to a sortMapper object, which can turn a 
         * conceptual single column sort from the wi's perspective to its
         *  multiple column sort in the indexes.
         */
        
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
                    query.addSortField(ssm, currentSort);
                }
            }
            
            /**
             * Otherwise just add the sort in as is, Solr will ignore invalid
             * sorts. 
             */
            
            else {
              	query.addSortField(sort.getSort(), currentSort);
            }
        }
        
        /**
         * Do we want to highlight?  If so setup the highlighter with
         * known tokens so we can use regex to extract them later on.
         */
        
        if (! highlightFields.isEmpty()) {
            for (String field: highlightFields) {
                query.addHighlightField(field);
            }
            query.setHighlight(Boolean.TRUE);
            query.setHighlightFragsize(30000);
            query.setHighlightRequireFieldMatch(Boolean.TRUE);
            query.setParam("hl.simple.pre", highlightToken);
            query.setParam("hl.simple.post", highlightToken);

        }
        
        /**
         * Set the pagination parameters.
         */

        query.setRows(searchParams.getPageSize());

        if (searchParams.getStartIndex() != -1) {
            query.setStart(searchParams.getStartIndex());
        }
        else {
            query.setStart(resultsDefault);
        }

        /**
         *  We only ever ask for a single facet, if its set in 
         *  the implementing class, set it in the Solr request.
         */
        
        
        if (facetString != null) {
            query.addFacetField(facetString);
            query.setFacetMinCount(1);
            query.setFacetSort("lex");
            query.setFacetLimit(factetNumberDefault);
        }

        logger.info("This is the Solr query:" + query + "\n");

        /**
         * Run the query.
         */

        QueryResponse rsp = null;
        
        try {
        rsp = server.query( query );
        SolrDocumentList sdl = rsp.getResults();
        
        /**
         * Package the results into the searchResults object.
         * We do this in a generic manner via the packInformation method.
         */

        packInformation(rsp, searchResults, searchParams);

        
        logger.debug("metaMapping: " 
        		+ searchResults.getResultSetMeta().toString());
        
        /**
         * Set the total number found.
         */

        searchResults.setTotalCount(new Integer((int) sdl.getNumFound()));

        /**
         * This will be handles in a similar way as the packKeys 
         * method, making this entire process generic.
         *
         */

        }
        catch (Exception e) {e.printStackTrace();}

        return ;

    }
    
    /**
     * This is a hook, any class that needs to modify the searchParams before
     * doing its work will override this method.
     * 
     * @param searchParams
     * @return
     */
    protected SearchParams preProcessSearchParams(SearchParams searchParams) {
        return searchParams;
    }

    
    /**
     * translateFilter
     * @param filter
     * @param propertyMap
     * @return a query string
     *
     * This method is responsible for recursively tearing apart
     * the filter object and generating the query string.
     *
     * It also handles any special cases that arise for
     * a given technology. In solr there is no real concept for IN or NOT IN,
     * so we translate those into a series of joined OR clauses.
     *
     */

    protected String translateFilter(Filter filter, HashMap<String, 
    		PropertyMapper> propertyMap) {

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

        if (filter.isBasicFilter()) {

            // Check to see if the property is null or an empty string,
            // if it is, return an empty string

            if (filter.getProperty() == null || 
            		filter.getProperty().equals("")) {
            	
                return "";
            }

            // If its not an IN or NOT IN, get the query clause and return it

            if (filter.getOperator() != Filter.OP_IN
                    && filter.getOperator() != Filter.OP_NOT_IN) {
                return propertyMap.get(filter.getProperty())
                	.getClause(filter.getValue(), filter.getOperator());
            }

            /** If its an IN or NOT IN, break the query down further, joining 
             * thesubclauses by OR or AND as appropriate
             */

            else {

                int operator;
                String joinClause = "";
                if (filter.getOperator() == Filter.OP_NOT_IN) {
                    operator = Filter.OP_NOT_EQUAL;
                    joinClause = " AND ";
                }
                else {
                    operator = Filter.OP_EQUAL;
                    joinClause = " OR ";
                }
                
                // Create the subclause, surround it in parens
                
                String output = "(";
                int first = 1;
                for (String value: filter.getValues()) {
                    if (first == 1) {
                        output += propertyMap.get(filter.getProperty())
                        	.getClause(value, operator);
                        
                        first = 0;
                    }
                    else {
                        output += joinClause 
                        	+ propertyMap.get(filter.getProperty())
                        		.getClause(value, operator);
                    }
                }
                return output + ")";
            }
        }

        /** 
         * We do not have a simple filter, so recurse.
         * When we get the return value join it appropriately
         * into the query string using AND's or OR's
         * as specified by the filterClause.
         * 
         */

        else {
            
            String queryString = "";
            queryString = "(";
            int first = 1;
            List<Filter> filters = filter.getNestedFilters();

            List<String> resultsString = new ArrayList<String>();

            for (Filter f: filters) {

                String tempString = translateFilter(f, propertyMap);
                if (! tempString.equals("")) {
                    resultsString.add(tempString);
                }
            }
            return "(" + StringUtils.join(resultsString, 
            		filterClauseMap.get(filter.getFilterJoinClause())) 
            		+ ")";
        }
    }

    /**
     * packInformation
     * @param sdl
     * @return List of keys
     * This generic method is available to all extending classes.  All
     * that they need to do to take advantage of it is to set the keyString
     * variable.  This will then be used to extract a given field from the
     * returned documents as the key we want to return to the wi.
     *
     * If something more complex is requires, the implementer is expected
     * to override this method with their own version.
     */
    
    void packInformation(QueryResponse rsp, SearchResults sr, 
    		SearchParams sp) {
    	
        List<String> keys = new ArrayList<String>();
        List<String> scoreKeys = new ArrayList<String>();
        List<String> info = new ArrayList<String>();
        List<String> facet = new ArrayList<String>();
                
        Map<String, Set<String>> setHighlights = 
        	new HashMap<String, Set<String>> ();
        
        Map<String, MetaData> metaList = new HashMap<String, MetaData> ();
        
        Map<String, Map<String, List<String>>> highlights = 
        	rsp.getHighlighting();
        
        SolrDocumentList sdl = rsp.getResults();
    
        logger.debug("Packing information.");
        
        /**
         * Check for facets, if found pack them.
         */
        
        if (this.facetString != null) {
            for (Count c: rsp.getFacetField(facetString).getValues()) {
                facet.add(c.getName());
                logger.debug(c.getName());
            }
        }
        
        /**
         * Iterate through the response documents, extracting the information 
         * that was configured at the implementing class level.
         */
        
        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();
            
            /**
             * Calculate the row level metadata.  Currently this only 
             * applies to score, and whether or not a row is generated for 
             * autocomplete purposes.
             */
            
            if (sp.includeRowMeta()) {
            	
                MetaData tempMeta = new MetaData();
                if (sp.includeMetaScore()) {
                    tempMeta.setScore("" + doc.getFieldValue("score"));
                }
                if (sp.includeGenerated()) {
                	if (doc.getFieldValue(IndexConstants.AC_IS_GENERATED)
                			.equals(new Integer("1"))) {
                		tempMeta.setGenerated();
                	}
                }
                
                /**
                 * Store the metadata into a mapping based on whatever unique
                 * key that we can find.
                 */
                
                if (this.keyString != null) {
                    metaList.put((String) doc.getFieldValue(keyString), 
                    		tempMeta);
                }
                if (this.otherString != null) {
                    metaList.put((String) doc.getFieldValue(otherString), 
                    		tempMeta);
                }


            }
            
            /**
             * In order to support older pages we pack the score directly as
             * well as in the metadata.
             */
            
            if (this.keyString != null) {            
                keys.add((String) doc.getFieldValue(keyString));
                scoreKeys.add("" + doc.getFieldValue("score"));
            }

            if (this.otherString != null) {
                info.add((String) doc.getFieldValue(otherString));
            }
            
            /**
             * If we have highlighted fields, and we've requested highlighting
             * AND we have asked for result set meta.  Include it.
             * We are also translating from the Solr specific highlighting
             * format to a simpler list of strings.
             * The issue however is that we need to store this list so that 
             * its both document (or singular) result centric, as well as 
             * field centric.  So we need to build a reasonably 
             * complicated map.
             */
            
            if (!this.highlightFields.isEmpty() && sp.includeMetaHighlight() 
            		&& sp.includeSetMeta()) {
            
	            Set<String> highlightKeys = 
	            	highlights.get(doc.getFieldValue(keyString)).keySet();
	            Map<String, List<String>> highlightsMap = 
	            	highlights.get(doc.getFieldValue(keyString));
	            
	            for (Iterator iter2 = highlightKeys.iterator();
	            	iter2.hasNext();) {
	            	
	                String key = (String) iter2.next();
	
	                List <String> solrHighlights = highlightsMap.get(key);
	                for (String highlightWord: solrHighlights) {
	                    
	                    Boolean inAHL = Boolean.FALSE;
	                    /**
	                     * Our solr highlights are surrounded by an 
	                     * impossible token, so split based on that.
	                     */
	                    String [] fragments = 
	                    	highlightWord.split(highlightToken);
	                    
	                    /**
	                     * Every other fragment will be a highlighted word
	                     * setup a loop that iterates through the results
	                     * grabbing it.  Once we have it place it into 
	                     * the highlight mapping.
	                     */
	                    
	                    for (String frag: fragments) {
	                        if (inAHL) {
	                            if (setHighlights.containsKey(
	                            		fieldToParamMap.get(key))) {
	                            	
	                                setHighlights.get(fieldToParamMap
	                                	.get(key)).add(frag);
	                            }
	                            else {
	                                setHighlights.put(fieldToParamMap
	                                	.get(key), 
	                                	new HashSet <String> ());
	                                
	                                setHighlights.get(fieldToParamMap
	                                	.get(key)).add(frag);
	                            }
	                            inAHL = Boolean.FALSE;
	                        }
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
}
