package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.Accession;

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
public class HibernateAccessionSummaryHunter<T> {

    // logger for the class
    private Logger logger = LoggerFactory.getLogger(HibernateAccessionSummaryHunter.class);
    
	@Autowired
	private SessionFactory sessionFactory;
	
	private Class type;
	
	private Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
	
    public HibernateAccessionSummaryHunter() {
				
		List<String> uniprotItems = new ArrayList<String>();
		uniprotItems.add("SWISS-PROT");
		uniprotItems.add("TrEMBL");
		typeMap.put("UniProt", uniprotItems);
		
		List<String> nomenItems = new ArrayList<String>();
		nomenItems.add("old symbol");
		nomenItems.add("%synonym");
		nomenItems.add("%ortholog symbol");
		nomenItems.add("current name");
		typeMap.put("nomen", nomenItems);
		
		List<String> genbankItems = new ArrayList<String>();
		genbankItems.add("GenBank");
		genbankItems.add("RefSeq");
		typeMap.put("GenBank", genbankItems);
	}



	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {

        logger.debug("-> hunt");         
        type = Accession.class;
        
    	List<String> keySet = new ArrayList<String>();
    	
    	List<String> termTypes = new ArrayList<String>();   	
    	
    	StringBuffer hql = new StringBuffer("FROM Accession WHERE");
    	
    	List<String> clauses = new ArrayList<String>();
    	String clause = null;
    	List<Filter> fList = searchParams.getFilter().getNestedFilters();
    	String prop;
    	if (fList != null) { 		    		
    		for (Filter f : fList) {
    			prop = f.getProperty();
    			if (prop != null && !"".equals(prop)){
    				logger.debug("Property: " + prop);
    				if (SearchConstants.ACC_ID.equals(prop)) {
    					clauses.add(" search_id = '" + f.getValue() + "' ");
    				}
    			}
    			
			}
    	}
    	
    	hql.append(StringUtils.join(clauses, " and "));
    	
    	// Add in the default ordering
    	
    	hql.append(" order by sequence_num desc");
    	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());
        logger.debug("This is the query: " + hql.toString());
        
/*		if(idSet != null && idSet.size() > 0) { 
			query.setParameterList("ids", idSet);
		}*/
       
        logger.debug("-> filter parsed" );   

        List<T> bm = new ArrayList<T>();
        Accession tmp;
        
        Map<String, Accession> qResults = 
        	new LinkedHashMap<String, Accession>();
        Accession bResults;
        
        List<T> qr = query.list();
        logger.debug("This is the size of the results: " + qr.size());
        Set<Integer> accessionID = new HashSet<Integer>();
        logger.debug("-> query complete" );
        int bTerm;
        for (T item: qr){

           	bm.add(item);
        	        	
        }
        
        logger.debug("-> results parsed" );
               
        searchResults.setTotalCount(bm.size());
        //searchResults.getResultSetMeta().addCount("accession", markerKey.size());
        
        int endIndex = searchParams.getStartIndex() + searchParams.getPageSize();
        if (endIndex > bm.size()){
        	endIndex = bm.size();
        }
        searchResults.setResultObjects(bm);        
    }
}