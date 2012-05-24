package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mgi.frontend.datamodel.ActualDatabase;

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
public class HibernateActualDatabaseHunter<T> {

    //--------------------------//
    //--- instance variables ---//
    //--------------------------//

    // logger for the class
    private Logger logger = LoggerFactory.getLogger(HibernateActualDatabaseHunter.class);
    
    @Autowired
    private SessionFactory sessionFactory;
	
    private Class type;
	
    //----------------------//
    //--- public methods ---//
    //----------------------//
 
    public HibernateActualDatabaseHunter() {
	return;
    }


    public void hunt(SearchParams searchParams,
	    SearchResults<T> searchResults) {

	type = ActualDatabase.class;

        logger.debug("HibernateActualDatabaseHunter.hunt()");         
        
    	StringBuffer hql = new StringBuffer("FROM ActualDatabase");
//        logger.debug(" +---> query: " + hql.toString());
    	
	if (sessionFactory == null) {
//		logger.debug ("sessionFactory is null");
		searchResults.setTotalCount(0);
		return;
	}

        Query query = sessionFactory.getCurrentSession().createQuery(
		hql.toString() );
//	logger.debug(" +---> query finished");

        List<T> qr = query.list();
//        logger.debug(" +---> number of results is " + qr.size());

        searchResults.setTotalCount(qr.size());
        searchResults.setResultObjects(qr);        
//	logger.debug(" +---> populated SearchResults object");
	return;
    }
}
