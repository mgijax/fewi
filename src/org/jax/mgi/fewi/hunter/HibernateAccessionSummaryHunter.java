package org.jax.mgi.fewi.hunter;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Logger logger = LoggerFactory.getLogger(HibernateAccessionSummaryHunter.class);
    
	@Autowired
	private SessionFactory sessionFactory;
	
	private final Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
	
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

    	String accId = searchParams.getFilter().getValue();
    	if (accId == null && "".equals(accId.trim())) { 
    		logger.debug("return empty"); 
    		return;
    	}

        Query query = sessionFactory.getCurrentSession().createQuery(
        	"FROM Accession WHERE lower(search_id) = :searchID "
        	+ "ORDER BY sequence_num DESC");
        query.setString("searchID", accId);

    	logger.debug("run query");
    	
    	int pageSize = searchParams.getPageSize();
    	int startIndex = searchParams.getStartIndex();
   	
        List<T> bm = new ArrayList<T>();
        
        @SuppressWarnings("unchecked")
		List<T> qr = query.list();
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
