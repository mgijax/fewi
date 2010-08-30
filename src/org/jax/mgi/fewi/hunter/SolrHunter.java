package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.PropertyMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.shr.fe.IndexConstants;
import org.apache.commons.lang.*;

/**
 * This is the Solr specific hunter.  It is responsible for mapping the higher
 *  level join type queries that are coming from the WI and translating them 
 *  into something that Solr can understand.  It is also responsible for 
 *  mapping the fe's idea of an in or not in clause to something that makes
 *  sense for solr.
 *  
 * @author mhall
 *
 */

public class SolrHunter implements Hunter {

    /**
     * These strings all have thier values set in the extending classes.
     */
   
    protected String solrUrl;
    protected String keyString;
    protected HashMap <String, PropertyMapper> propertyMap = 
        new HashMap<String, PropertyMapper>();
   
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
         * Setup our interface into solr.  Some of these values might be better
         * off in a configuration object somewhere.
         */
        
        CommonsHttpSolrServer server = null;
        
        try { server = new CommonsHttpSolrServer(solrUrl);}
        catch (Exception e) {e.printStackTrace();}

        server.setSoTimeout(30000);  // socket read timeout
        server.setConnectionTimeout(30000);
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxTotalConnections(100);
        server.setFollowRedirects(false);  // defaults to false
        server.setAllowCompression(true);
        server.setMaxRetries(1);
        
        SolrQuery query = new SolrQuery();
        
        String queryString = 
            translateFilter(searchParams.getFilter(), propertyMap);
        System.out.println(queryString);
        query.setQuery(queryString);
        
        /**
         * Tear apart the sort objects and add them to the query string.
         */
        
        for (Sort sort: searchParams.getSorts()) {
            if (sort.isDesc()) {
                query.addSortField(sort.getProperty(), SolrQuery.ORDER.desc);
            }
            else {
                query.addSortField(sort.getProperty(), SolrQuery.ORDER.asc);
            }
        }
        
        /**
         * Set the pagination parameters.
         */
        
        query.setRows(searchParams.getPageSize());
        
        query.setStart(searchParams.getStartIndex()-1);
        
        System.out.println("The Solr query:" + query + "\n");

        /**
         * Run the query.
         */
        
        QueryResponse rsp = null;
        try {
        rsp = server.query( query );
        SolrDocumentList sdl = rsp.getResults();

        /**
         * Package the results into the searchResults object.
         * We do this in a generic manner via the packKeys method.
         */
        
        searchResults.setResultKeys(packKeys(sdl));
        
        /**
         * Set the total number found.
         */
        
        searchResults.setTotalCount(new Integer((int) sdl.getNumFound()));

        /**
         * TODO: Set the metadata.  
         * This will be handles in a similar way as the packKeys method, making
         * this entire process generic.
         * 
         */
        
        }
        catch (Exception e) {e.printStackTrace();}   
        
        return ;
        
    }
    
    /**
     * packKeys
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

    List<String> packKeys(SolrDocumentList sdl) {
        List<String> keys = new ArrayList<String>();
        
        System.out.println("Keys: ");
        
        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();
            
            keys.add((String) doc.getFieldValue(keyString));
            System.out.println(doc.getFieldValue(keyString));
        }

        return keys;
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
    
    protected String translateFilter(Filter filter, HashMap<String, PropertyMapper> propertyMap) {
        
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
            
            if (filter.getProperty() == null || filter.getProperty().equals("")) {
                return "";
            }
            else {
                System.out.println(filter.getProperty());
            }
            
            
            // If its not an IN or NOT IN, get the query clause and return it 
            
            if (filter.getOperator() != Filter.OP_IN 
                    && filter.getOperator() != Filter.OP_NOT_IN) {
                return propertyMap.get(filter.getProperty()).getClause(filter.getValue(), filter.getOperator());
            }
            
            // If its an IN or NOT IN, break the query down further, joining the 
            // subclauses by OR. 
            
            else {
                
                int operator;
                
                if (filter.getOperator() == Filter.OP_NOT_IN) {
                    operator = Filter.OP_NOT_EQUAL;
                }
                else {
                    operator = Filter.OP_EQUAL;
                }
                String output = "(";
                int first = 1;
                for (String value: filter.getValues()) {
                    if (first == 1) {
                        output += propertyMap.get(filter.getProperty()).getClause(value, operator);
                        first = 0;
                    }
                    else {
                        output += " OR " + propertyMap.get(filter.getProperty()).getClause(value, operator);
                    }
                }
                return output + ")";
            }
        }
        
        // We do not have a simple filter, so recurse.
        // When we get the return value join it appropriately 
        // into the query string using AND's or OR's 
        // as specified by the filterClause.
        
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
            return "(" + StringUtils.join(resultsString, filterClauseMap.get(filter.getOperator())) + ")"; 
        }    
    }
}
