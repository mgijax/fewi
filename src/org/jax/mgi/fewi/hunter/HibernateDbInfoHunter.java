package org.jax.mgi.fewi.hunter;

import java.util.List;

import mgi.frontend.datamodel.DatabaseInfo;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HibernateDbInfoHunter
{
    // logger for the class
    private Logger logger = LoggerFactory.getLogger(HibernateBatchSummaryHunter.class);
    
	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public void hunt(SearchParams searchParams, SearchResults<DatabaseInfo> searchResults) 
	{
        logger.debug("-> hunt");     

    	StringBuffer hql = new StringBuffer("FROM DatabaseInfo ");    	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());      
        List<DatabaseInfo> qr = query.list();

        searchResults.setTotalCount(qr.size());

        searchResults.setResultObjects(qr);        
    }
}