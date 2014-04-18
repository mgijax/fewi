package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mgi.frontend.datamodel.Accession;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
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
	
	private Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
	
    public HibernateAccessionSummaryHunter() {
				
		List<String> uniprotItems = new ArrayList<String>();
		uniprotItems.add("SWISS-PROT");
		uniprotItems.add("TrEMBL");
		typeMap.put("UniProt", uniprotItems);
		
		List<String> nomenItems = new ArrayList<String>();
		nomenItems.add("old symbol");
		nomenItems.add("%synonym");
		nomenItems.add("% symbol");	// removed 'ortholog'
		nomenItems.add("current name");
		typeMap.put("nomen", nomenItems);
		
		List<String> genbankItems = new ArrayList<String>();
		genbankItems.add("GenBank");
		genbankItems.add("RefSeq");
		typeMap.put("GenBank", genbankItems);
	}


	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {

        logger.debug("-> hunt");         
    	
    	StringBuffer hql = new StringBuffer("FROM Accession WHERE");
    	logger.debug("parse id"); 
    	String accId = searchParams.getFilter().getValue();
    	
    	if (accId != null && !"".equals(accId.trim())) { 
    		hql.append(" lower(search_id) = '" + accId.trim() + "'");
    	} else {
    		logger.debug("return empty"); 
    		return;
    	}
 	
    	logger.debug("run query");
    	// Add in the default ordering   	
    	hql.append(" order by sequence_num desc");
    	
    	int pageSize = searchParams.getPageSize();
    	int startIndex = searchParams.getStartIndex();
   	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());       
        logger.debug("This is the query: " + hql.toString());
       
        logger.debug("-> filter parsed" );   

        List<T> bm = new ArrayList<T>();
        
        List<T> qr = (List<T>)query.list();
        logger.debug("This is the size of the results: " + qr.size());
        logger.debug("-> query complete" );

        int start = 0;
        for (T item: qr){
        	if (start >= startIndex && start < (startIndex + pageSize) ) {
        		bm.add(item);
        	}
        	start ++;        	
        }
        
        logger.debug("-> results parsed" );
               
        searchResults.setTotalCount(qr.size());
        searchResults.setResultObjects(bm);        
    }
}
