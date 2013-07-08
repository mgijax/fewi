package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mgi.frontend.datamodel.BatchMarkerId;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HibernateBatchSummaryHunter<T> {

    // logger for the class
    private Logger logger = LoggerFactory.getLogger(HibernateBatchSummaryHunter.class);
    
	@Autowired
	private SessionFactory sessionFactory;
	
	private Class type;
	
	private Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
	
    public HibernateBatchSummaryHunter() {
				
		List<String> uniprotItems = new ArrayList<String>();
		uniprotItems.add("SWISS-PROT");
		uniprotItems.add("TrEMBL");
		typeMap.put("UniProt", uniprotItems);
		
		List<String> vegaItems = new ArrayList<String>();
		vegaItems.add("VEGA Gene Model");
		vegaItems.add("VEGA Protein");
		vegaItems.add("VEGA Transcript");
		typeMap.put("VEGA", vegaItems);
		
		List<String> ensemblItems = new ArrayList<String>();
		ensemblItems.add("Ensembl Gene Model");
		ensemblItems.add("Ensembl Protein");
		ensemblItems.add("Ensembl Transcript");
		typeMap.put("Ensembl", ensemblItems);
		
		List<String> nomenItems = new ArrayList<String>();
		nomenItems.add("old symbol");
		nomenItems.add("%synonym");
		nomenItems.add("% symbol");	// removed 'ortholog'
		nomenItems.add("current name");
		nomenItems.add("current symbol");
		typeMap.put("nomen", nomenItems);
		
		List<String> genbankItems = new ArrayList<String>();
		genbankItems.add("GenBank");
		genbankItems.add("RefSeq");
		typeMap.put("GenBank", genbankItems);
	}

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {

        logger.debug("-> hunt");         
        type = BatchMarkerId.class;
        
        // the queried id set
    	List<String> idSet = new ArrayList<String>();
    	// actual ids sent to query are lowercased
    	List<String> idSetLower = new ArrayList<String>();  	
    	
    	// begin generating the HQSL clauses
    	StringBuffer hql = new StringBuffer("FROM BatchMarkerId WHERE ");
    	List<String> clauses = new ArrayList<String>();
    	 		
		for (Filter f : searchParams.getFilter().getNestedFilters()) {
			String prop = f.getProperty();
			if (SearchConstants.BATCH_TERM.equals(prop)){
				idSet = f.getValues();
				for (String id: idSet) {
					idSetLower.add(id.toLowerCase());
				}
				clauses.add("lower(term) IN (:ids) ");
			} 
			else if (SearchConstants.BATCH_TYPE.equals(prop)) {
		    	List<String> termTypes; 
				if (typeMap.containsKey(f.getValue())){
					termTypes = typeMap.get(f.getValue());
				} 
				else {
					termTypes = Arrays.asList(f.getValue());
				}
				List<String> tClauses = new ArrayList<String>();
				for (String type : termTypes) {
					tClauses.add("term_type LIKE '" + type + "'");
				}
				clauses.add("(" + StringUtils.join(tClauses, " OR ") + ")");
			}
		}
    	hql.append(StringUtils.join(clauses, " AND "));

        logger.debug("-> filter parsed" );   
        
    	// perform HSQL query for BathMarkerIds
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        
        List<T> qr = new ArrayList<T>();
        
		int start = 0, end = 0, batchSize = 2000;
		while (start < idSetLower.size()){
			end = start + batchSize;
			if (end > idSetLower.size()) end = idSetLower.size();
			logger.debug(String.format("batch %d-%d", start, end));
			query.setParameterList("ids", idSetLower.subList(start, end));				
			qr.addAll(query.list());
			start = end+1;
		}
		idSetLower = null;

        logger.debug("-> query complete" );

        // organize the results grouped by query term
        Map<String, List<BatchMarkerId>> qResults = new HashMap<String, List<BatchMarkerId>>();
        Set<Integer> markerKey = new HashSet<Integer>();
        
        for (T item: qr){
        	BatchMarkerId bmi = (BatchMarkerId) item;
        	
        	// tracking a unique set of marker keys
        	markerKey.add(bmi.getMarker().getMarkerKey());
        	
        	// get the batch marker term and lowercase it 
        	// to maintain a map of BatchMarkerId records for that lowercase term
        	String bTerm = bmi.getTerm().toLowerCase();
        	List <BatchMarkerId> bResults;
        	if (qResults.containsKey(bTerm)) {
        		bResults = qResults.get(bTerm);
        	} else {
        		bResults = new ArrayList<BatchMarkerId>();
        		qResults.put(bTerm, bResults);
        	}
        	bResults.add(bmi);
        }
        qr = null;
        logger.debug("-> results parsed" );
        
        // build the final result set by stepping through the original list of IDs (in order)
        // and mapping them to the retrieved BatchMarkerId records
        List<T> returnList = new ArrayList<T>();
        for (String id: idSet) {
        	if (qResults.containsKey(id.toLowerCase())) {
        		for (BatchMarkerId b : qResults.get(id.toLowerCase())) {
        			b.setTerm(id);
					returnList.add((T)b);
				}
        	} else {
        		BatchMarkerId blankId = new BatchMarkerId();
    			blankId.setTerm(id);
    			blankId.setTermType("");
    			returnList.add((T)blankId);
        	}
        }
        
        logger.debug("-> results sorted" );
        
        searchResults.setTotalCount(returnList.size());
        logger.debug("markers: " + markerKey.size());
        searchResults.getResultSetMeta().addCount("marker", markerKey.size());
        
        int endIndex = searchParams.getStartIndex() + searchParams.getPageSize();
        if (endIndex > returnList.size()){
        	endIndex = returnList.size();
        }
        searchResults.setResultObjects(returnList.subList(searchParams.getStartIndex(), 
        		endIndex));
    }
}
