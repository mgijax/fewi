package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import mgi.frontend.datamodel.QueryFormOption;

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
public class HibernateQueryFormOptionHunter<T> {

    // instance variables
 
    // logger for the class
    private Logger logger = LoggerFactory.getLogger(HibernateQueryFormOptionHunter.class);
    
    @Autowired
    private SessionFactory sessionFactory;
	
    private Class type;
	
    // public instance methods

    public HibernateQueryFormOptionHunter() {
    }


    public void hunt(SearchParams searchParams, SearchResults<T> searchResults) {

        logger.debug("-> hunt");         
        type = QueryFormOption.class;   	
    	
	String prop;
    	StringBuffer hql = new StringBuffer("FROM QueryFormOption WHERE ");
	List<String> clauses = new ArrayList<String>();

	List<Filter> fList = searchParams.getFilter().getNestedFilters();
	if (fList == null) {
	    logger.debug("no filters; return empty");
	    return;
	}

	for (Filter f : fList) {
	    prop = f.getProperty();
	    if (SearchConstants.FORM_NAME.equals(prop)) {
		clauses.add ("form_name = '" + f.getValue() + "'");
	    } else if (SearchConstants.FIELD_NAME.equals(prop)) {
		clauses.add ("field_name = '" + f.getValue() + "'");
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
    	hql.append(" order by sequence_num");
 	
    	logger.debug("run query: " + hql.toString());
    	
    	int pageSize = searchParams.getPageSize();
    	int startIndex = searchParams.getStartIndex();
   	
        Query query = sessionFactory.getCurrentSession().createQuery(hql.toString());       
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
