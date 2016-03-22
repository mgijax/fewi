package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.AlleleSystem;

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
public class HibernateAlleleSystemHunter
{
    // instance variables
 
    // logger for the class
    private final Logger logger = LoggerFactory.getLogger(HibernateAlleleSystemHunter.class);
    
    @Autowired
    private SessionFactory sessionFactory;
	
    // public instance methods

    public void hunt(SearchParams searchParams, SearchResults<AlleleSystem> searchResults)
    {
        logger.debug("-> HibernateAlleleSystemHunter.hunt()");         
    	
        String prop;
    	StringBuffer hql = new StringBuffer("FROM AlleleSystem WHERE ");
    	List<String> clauses = new ArrayList<String>();

    	List<Filter> fList = searchParams.getFilter().getNestedFilters();
		if (fList == null) {
		    logger.debug("no filters; return empty");
		    return;
		}

		for (Filter f : fList) {
		    prop = f.getProperty();
		    if (SearchConstants.ALL_ID.equals(prop)) {
			clauses.add ("allele_id = '" + f.getValue() + "'");
		    } else if (SearchConstants.CRE_SYSTEM_KEY.equals(prop)) {
			clauses.add ("system_key = " + f.getValue());
		    } else if (SearchConstants.CRE_SYSTEM.equals(prop)) {
		    	clauses.add("system = " + f.getValue());
		    } else {
			if (prop != null) {
			    logger.debug("unknown filter (" + prop + "); return empty");
			} else {
			    logger.debug("unknown filter (null); return empty");
			}
			return;
		    }
		}

		hql.append(StringUtils.join(clauses, " and "));
 	
    	logger.debug("run query: " + hql.toString());
    	
    	int pageSize = searchParams.getPageSize();
    	int startIndex = searchParams.getStartIndex();
   	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());       
        logger.debug("-> filter parsed" );   
        
        @SuppressWarnings("unchecked")
	List<AlleleSystem> qr = query.list();
        logger.debug("This is the size of the results: " + qr.size());
        logger.debug("-> query complete" );

        List<AlleleSystem> bm = new ArrayList<AlleleSystem>();
        int start = 0;
        for (AlleleSystem item: qr) {
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
