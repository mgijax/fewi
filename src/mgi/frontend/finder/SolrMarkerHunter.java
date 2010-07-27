package mgi.frontend.finder;

import java.util.Iterator;

import mgi.frontend.controller.MarkerQuery;
import mgi.frontend.results.MarkerResults;
import mgi.frontend.util.QueryUtil;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerHunter {


    public MarkerResults searchMarkers(MarkerQuery markerQuery) throws DataAccessException {
        // Dummy code so that Mark can do his side.

        MarkerResults results = new MarkerResults();

        CommonsHttpSolrServer server = null;
        
        try { server = new CommonsHttpSolrServer( "http://cardolan.informatics.jax.org:8984/solr/" );}
        catch (Exception e) {e.printStackTrace();}

        server.setSoTimeout(1000);  // socket read timeout
        server.setConnectionTimeout(1000);
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxTotalConnections(100);
        server.setFollowRedirects(false);  // defaults to false
        // allowCompression defaults to false.
        // Server side must support gzip or deflate for this to have any effect.
        server.setAllowCompression(true);
        server.setMaxRetries(1);

        SolrQuery query = new SolrQuery();

        String queryString = "";
        String highlightFields = ""; 

        if (markerQuery.getQuery() != null && ! markerQuery.getQuery().equals("")) {
            if (! markerQuery.getVocabularies().isEmpty()) {
                for (String vocab: markerQuery.getVocabularies()) {
                    if (vocab.equals("Nomenclature")) {
                        queryString = QueryUtil.addORClause(queryString, "Symbol: " + markerQuery.getQuery() + "^5000");
                        queryString = QueryUtil.addORClause(queryString, "Symbol_exact: " + markerQuery.getQuery() + "^10000");
                        queryString = QueryUtil.addORClause(queryString, "Current_Name: " + markerQuery.getQuery() + "^1000");
                        queryString = QueryUtil.addORClause(queryString, "Old_Name: " + markerQuery.getQuery() + "^500");
                        queryString = QueryUtil.addORClause(queryString, "Synonyms: " + markerQuery.getQuery());
                        highlightFields = QueryUtil.addHighlight(highlightFields, "Symbol, Symbol_exact, Current_Name, Old_Name, Synonyms");
                    }
                    if (vocab.equals("GO")) {
                        queryString = QueryUtil.addORClause(queryString, "GO/Marker: " + markerQuery.getQuery());
                        highlightFields = QueryUtil.addHighlight(highlightFields, "GO/Marker");
                    }
                    if (vocab.equals("MP")) {
                        queryString = QueryUtil.addORClause(queryString, "Mammalian_Phenotype/Genotype: " + markerQuery.getQuery());
                        highlightFields = QueryUtil.addHighlight(highlightFields, "Mammalian_Phenotype/Genotype");
                    }
                    if (vocab.equals("GXD")) {
                        queryString = QueryUtil.addORClause(queryString, "AD: " + markerQuery.getQuery());
                        highlightFields = QueryUtil.addHighlight(highlightFields, "AD");
                    }
                    if (vocab.equals("OMIM")) {
                        queryString = QueryUtil.addORClause(queryString, "OMIM/Genotype: " + markerQuery.getQuery());
                        queryString = QueryUtil.addORClause(queryString, "OMIM/Human_Marker: " + markerQuery.getQuery());
                        highlightFields = QueryUtil.addHighlight(highlightFields, "OMIM/Genotype, OMIM/Human_Marker");
                    }
                    if (vocab.equals("IP")) {
                        queryString = QueryUtil.addORClause(queryString, "InterPro/Marker: " + markerQuery.getQuery());
                        highlightFields = QueryUtil.addHighlight(highlightFields, "Interpro/Marker");
                    }

                    if (queryString != null) {
                        queryString = "(" + queryString + ") ";
                    }
                }
            }
        } 

        if (! markerQuery.getChromosomes().isEmpty()) {
            String clause = "";
            for (String chr: markerQuery.getChromosomes()) {
                if (! chr.equals("Any")) {
                    clause = QueryUtil.addORClause(clause, "Chr: " + chr);
                }
            }
            if (!clause.equals("")) {
                queryString = QueryUtil.addAndClause(queryString, "(" + clause + ")");
            }
        }  
 
        query.setQuery(queryString);
        
        System.out.println(markerQuery.getSort());
        
        if (markerQuery.getSort() != null) { 
            String column = markerQuery.getSort();
            String order = markerQuery.getDir();
            System.out.println("sort: " + column);
            if(column != null && order != null){
            	System.out.println("sort");
                if (column.equals("symbol")) { 
                	System.out.println("symbol");
                    if (order.equals("asc")) {
                        query.addSortField("Symbol", SolrQuery.ORDER.asc);
                    }
                    else {
                        query.addSortField("Symbol", SolrQuery.ORDER.desc);
                    }
                }
                
                if (column.equals("name")) { 
                	System.out.println("name");
                    if (order.equals("asc")) {
                        query.addSortField("Current_Name", SolrQuery.ORDER.asc);
                    }
                    else {
                        query.addSortField("Current_Name", SolrQuery.ORDER.desc);
                    }
                }
                if (column.equals("loc")) {
                	System.out.println("loc");
                    if (order.equals("asc")) {
                        query.addSortField("Chr", SolrQuery.ORDER.asc);
                    }
                    else {
                        query.addSortField("Chr", SolrQuery.ORDER.desc);
                    }
                }            	
            }
        }
        
        System.out.println(query);
        
        query.setRows(markerQuery.getResults());
        query.setHighlight(true);
        query.setParam("hl.fl", highlightFields);

        int start = markerQuery.getStartIndex();

        System.out.println("Starting on document: " + start);

        query.setStart(start);

        System.out.println("Query String: " + queryString + "\n");
        results.setQueryString(queryString);

        QueryResponse rsp = null;
        try {
        rsp = server.query( query );
        SolrDocumentList sdl = rsp.getResults();

        System.out.println ("The map: " + rsp.getHighlighting());

        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();
            String mk = (String) doc.getFieldValue("Marker_Key");
            results.addMarkerIDs(new Integer(mk));
        }

        results.setMaxCount(new Integer((int) sdl.getNumFound()));

        }
        catch (Exception e) {e.printStackTrace();}

        return results;
    }

}