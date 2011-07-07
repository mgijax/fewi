package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
		nomenItems.add("%ortholog symbol");
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
        
    	List<String> idSet = new ArrayList<String>();
    	List<String> idSetLower = new ArrayList<String>();
    	List<String> termTypes = new ArrayList<String>();   	
    	
    	StringBuffer hql = new StringBuffer("FROM BatchMarkerId WHERE ");
    	List<String> clauses = new ArrayList<String>();
    	String clause = null;
    	List<Filter> fList = searchParams.getFilter().getNestedFilters();
    	String prop;
    	if (fList != null) { 		    		
    		for (Filter f : fList) {
    			prop = f.getProperty();
    			if (prop != null && !"".equals(prop)){
    				if (SearchConstants.BATCH_TERM.equals(prop)){
    					idSet = f.getValues();
    					for (String id: idSet) {
							idSetLower.add(id.toLowerCase());
						}
    					clause = "lower(term) in (:ids) ";
    				} else if (SearchConstants.BATCH_TYPE.equals(prop)) {
    					if (typeMap.containsKey(f.getValue())){
    						termTypes.addAll(typeMap.get(f.getValue()));
    					} else {
    						termTypes.add(f.getValue());
    					}
    					List<String> tClauses = new ArrayList<String>();
    					for (String type : termTypes) {
    						tClauses.add("term_type like '" + type + "'");
						}
    					clause = "(" + StringUtils.join(tClauses, " OR ") + ")";
    				}
    			}
    			if (clause != null && !"".equals(clause)) {
    				clauses.add(clause);
    			}	
			}
    	}
    	
    	hql.append(StringUtils.join(clauses, " and "));
    	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        
		if(idSet != null && idSet.size() > 0) { 
			query.setParameterList("ids", idSetLower);
		}
       
        logger.debug("-> filter parsed" );   

        List<T> bm = new ArrayList<T>();
        BatchMarkerId tmp;
        
        Map<String, List<BatchMarkerId>> qResults = 
        	new LinkedHashMap<String, List<BatchMarkerId>>();
        List <BatchMarkerId> bResults;
        
        List<T> qr = query.list();
        Set<Integer> markerKey = new HashSet<Integer>();
        logger.debug("-> query complete" );
        String bTerm;
        for (T item: qr){
        	markerKey.add(((BatchMarkerId)item).getMarker().getMarkerKey());
        	bTerm = ((BatchMarkerId)item).getTerm().toLowerCase();
        	logger.debug("bTerm: " + bTerm);
        	if (qResults.containsKey(bTerm)) {
        		bResults = qResults.get(bTerm);
        	} else {
        		bResults = new ArrayList<BatchMarkerId>();
        		qResults.put(bTerm, bResults);
        	}
        	bResults.add((BatchMarkerId)item);
        }
        logger.debug("-> results parsed" );
        
        for (String id: idSet) {
        	if (qResults.containsKey(id.toLowerCase())){
        		bResults = qResults.get(id.toLowerCase());
        		for (BatchMarkerId b : bResults) {
        			b.setTerm(id);
					bm.add((T)b);
				}
        	} else {
    			tmp = new BatchMarkerId();
    			tmp.setTerm(id);
    			tmp.setTermType("");
    			bm.add((T)tmp);
        	}
        }
        logger.debug("-> results sorted" );
        
        searchResults.setTotalCount(bm.size());
        logger.debug("markers: " + markerKey.size());
        searchResults.getResultSetMeta().addCount("marker", markerKey.size());
        
        int endIndex = searchParams.getStartIndex() + searchParams.getPageSize();
        if (endIndex > bm.size()){
        	endIndex = bm.size();
        }
        searchResults.setResultObjects(bm.subList(searchParams.getStartIndex(), 
        		endIndex));        
    }
}