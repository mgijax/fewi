package org.jax.mgi.fewi.hunter;

import java.util.*;

// mgi classes
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.fewi.propertyMapper.PropertyMapper;
import org.jax.mgi.shr.fe.IndexConstants;

// external classes
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * This is the Solr specific hunter.  It is responsible for translating
 * higher-level generic webapp requests to Solr's specific API
 */
public class SolrHunter implements Hunter {

    public Logger logger = LoggerFactory.getLogger(this.getClass());


    /*----- INSTANCE VARIABLES -----*/

    /**
     * Values set in the extending classes, determining each
     * implementation's specific functionality
     */

    // Which solr index?
    protected String solrUrl;
    // Which field is the document key?
    protected String keyString;
    // Which other field do we want to use as a key? (This could be collapsed)
    protected String otherString;
    // What is the name of the facet that we want to pull out
    protected String facetString;

    /**
     * solr server settings from configuration
     */
    protected CommonsHttpSolrServer server = null;
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
     * Mapping variables to map webapp request values to their
     * 'columns' in Solr;  useful in defining the 1->N mappings
     */

    // Front end fields -> PropertyMappers
    protected HashMap <String, PropertyMapper> propertyMap =
        new HashMap<String, PropertyMapper>();

    // Solr Fields -> Front end field mappings
    protected HashMap <String, String> fieldToParamMap =
        new HashMap<String, String>();

    // Front end sorts -> backend fields
    protected HashMap <String, SolrSortMapper> sortMap =
        new HashMap<String, SolrSortMapper>();

    // FEWI's filter comparison -> Solr's comparison operator
    protected HashMap<Integer, String> filterClauseMap =
        new HashMap<Integer, String>();


    /**
     * Highlighting
     */

    // Fields to highlight
    protected List <String> highlightFields = new ArrayList<String> ();

    // unique token used for highlighting
    private final String highlightToken = "!FRAG!";



    /*----- CONSTRUCTOR -----*/

    public SolrHunter() {
        // Setup the mapping of the logical ands and or to the vendor
        // specific ones.

        filterClauseMap.put(Filter.FC_AND, " AND ");
        filterClauseMap.put(Filter.FC_OR, " OR ");

    }


    /*----- PUBLIC METHODS -----*/

    /**
     * hunt
     * @param SearchParams, SearchResults
     * @return none
     * Classes of the hunter interface must implement the hunt method.
     * This method is primarily responsible for breaking down a Filter
     * object into its component parts.  Once we are at that lowest level
     * we then invoke the SolrProperyMapper to get back the lowest level
     * query clauses.  These will then in turn be joined together by this
     * class, with the result being a query to place against solr.
     *
     * This method then runs the query, packages the results and exits.
     * This method is designed with the template methodology in mind, so
     * nearly all of the steps in the algorithm can be overwritten by
     * implementing classes.
     */

    @Override
    public void hunt(SearchParams searchParams, SearchResults searchResults) {


        // Invoke the hook, editing the search params as needed.
        searchParams = this.preProcessSearchParams(searchParams);

        // Setup our interface into solr.
        setupSolrConnection();

        // Create the query string by invoking the translate filter method.
        SolrQuery query = new SolrQuery();
        String queryString =
            translateFilter(searchParams.getFilter(), propertyMap);
        logger.info("Incoming Transformed String: " + queryString);
        query.setQuery(queryString);

        // pack the score
        query.setFields("score");

        // Add in the Sorts from the search parameters.
        addSorts(searchParams, query);

        // Perform highlighting, assuming its needed.  This method will take
        // care of determining that.
        addHighlightingFields(searchParams, query);

        // Set the pagination parameters.
        query.setRows(searchParams.getPageSize());
        if (searchParams.getStartIndex() != -1) {
            query.setStart(searchParams.getStartIndex());
        }else {
            query.setStart(resultsDefault);
        }

        // Add the facets, can be overwritten.
        addFacets(query);
        logger.info("This is the final Solr query:" + query + "\n");

        /**
         * Run the query & package results & result count
         */
        QueryResponse rsp = null;
        try {
            rsp = server.query( query );
            SolrDocumentList sdl = rsp.getResults();

            // Package the results into the searchResults object.
            packInformation(rsp, searchResults, searchParams);

            // Set the total number found.
            searchResults.setTotalCount(new Integer((int) sdl.getNumFound()));

            logger.debug("metaMapping: "
                + searchResults.getResultSetMeta().toString());
        }
        catch (Exception e) {e.printStackTrace();}

        return ;

    }


    /*----- PROTECTED METHODS -----*/

    /**
     * preprocessSearchParams
     * @param SearchParams
     * @return SearchParams
     *
     * This is a hook, any class that needs to modify the searchParams before
     * doing its work will override this method.
     *
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
                Boolean first = Boolean.TRUE;
                for (String value: filter.getValues()) {
                    if (first) {
                        output += propertyMap.get(filter.getProperty())
                            .getClause(value, operator);

                        first = Boolean.FALSE;
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
     * setupSolrConnection
     * @param none
     * @return none
     * This default implementation is responsible for setting up the
     * connection to the configured Solr Index.  Implementing classes
     * can override this template method in order to do special actions
     * as needed.
     */

    protected void setupSolrConnection() {

        try { server = new CommonsHttpSolrServer(solrUrl);}
        catch (Exception e) {
            System.out.println("Cannot reach the Solr server.");
            e.printStackTrace();
            }

        logger.debug("SolrTimeout:" + solrSoTimeout);

        server.setSoTimeout(solrSoTimeout);  // socket read timeout
        server.setConnectionTimeout(connectionTimeout);
        server.setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);
        server.setMaxTotalConnections(maxTotalConnections);
        server.setFollowRedirects(false);  // defaults to false
        server.setAllowCompression(true);
        server.setMaxRetries(maxRetries);

        return;
    }

    /**
     * addHighlightingFields
     * @param SearchParams, SolrQuery
     * @return none
     * This method checks to see whether or not the highlightFields variable
     * has had any information placed into it.
     *
     *  If so, we go ahead and modify the Solr query to ask for these fields to be
     *  highlighted, assuming the search itself asked for this to occur.
     *
     *  This is done for performance reasons, as asking for highlighting actually
     *  add to the workload that Solr is performing, as well as adds the the amount
     *  of information that we need to pack and process.
     */

    protected void addHighlightingFields(SearchParams searchParams, SolrQuery query) {
        if (! highlightFields.isEmpty() && searchParams.includeMetaHighlight()) {
            for (String field: highlightFields) {
                query.addHighlightField(field);
            }
            query.setHighlight(Boolean.TRUE);
            query.setHighlightFragsize(30000);
            query.setHighlightSnippets(100);
            query.setParam("hl.simple.pre", highlightToken);
            query.setParam("hl.simple.post", highlightToken);

        }
    }

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
     * @param SearchParams, SolrQuery
     * @return none
     * Tear apart the sort objects and add them to the query string.
     * This currently maps to a sortMapper object, which can turn a
     * conceptual single column sort from the wi's perspective to its
     * multiple column sort in the indexes.
     *
     * We modify the query directly, so once this method completes we
     * are ready to continue processing.
     */

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
     * If something more complex is required, the implementer is expected
     * to override this method with their own version.
     */

    protected void packInformation(QueryResponse rsp, SearchResults sr,
            SearchParams sp) {

        // A list of all the primary keys in the document
        List<String> keys = new ArrayList<String>();

        // A list of the documents scores.
        List<String> scoreKeys = new ArrayList<String>();

        // A listing of "otherStrings" for the documents.
        // These are used when the key isn't what we are
        // trying to get out of the document.
        List<String> info = new ArrayList<String>();

        // A listing of all of the facets.  This is used
        // at the set level.
        List<String> facet = new ArrayList<String>();


        // A mapping of field -> set of highlighted words
        // for the result set.
        Map<String, Set<String>> setHighlights =
            new HashMap<String, Set<String>> ();

        // A mapping of documentKey -> Mapping of FieldName
        // -> list of highlighted words.
        Map<String, Map<String, List<String>>> highlights =
            rsp.getHighlighting();

        // A mapping of documentKey -> Row level Metadata objects.
        Map<String, MetaData> metaList = new HashMap<String, MetaData> ();

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

                for (String key: highlightKeys) {
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
                         * the highlight mapping.  The highlighted sections
                         * will be surrounded by our impossible token.  As
                         * such when the string is split the highlighted
                         * tokens will be every other set of words.
                         */

                        for (String frag: fragments) {

                            /**
                             * We are in a highlighted section, parse out the
                             * matching word(s).
                             */

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
}
