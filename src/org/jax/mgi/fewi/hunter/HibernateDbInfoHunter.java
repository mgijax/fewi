package org.jax.mgi.fewi.hunter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mgi.frontend.datamodel.BatchMarkerId;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HibernateDbInfoHunter<T> {

    // logger for the class
    private Logger logger = LoggerFactory.getLogger(HibernateBatchSummaryHunter.class);
    
	@Autowired
	private SessionFactory sessionFactory;
	
	private Class type;
	
	private Map<String, List<String>> typeMap = new HashMap<String, List<String>>();
	
    public HibernateDbInfoHunter() {
	}

	public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {

        logger.debug("-> hunt");         
        type = BatchMarkerId.class;

    	StringBuffer hql = new StringBuffer("FROM DatabaseInfo ");    	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());      
        List<T> qr = query.list();

        searchResults.setTotalCount(qr.size());

        searchResults.setResultObjects(qr);        
    }
}