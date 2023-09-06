package org.jax.mgi.fewi.hunter;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fe.datamodel.AlleleSystem;
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

    	List<Filter> fList = searchParams.getFilter().getNestedFilters();
		if (fList == null) {
		    logger.debug("no filters; return empty");
		    return;
		}
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AlleleSystem.class);

		for (Filter f : fList) {
		    String prop = f.getProperty();
		    if (SearchConstants.ALL_ID.equals(prop)) {
		    	 criteria.add(Restrictions.eq("alleleID", f.getValue()));
		    	 logger.debug("adding alleleId=" + f.getValue());
		    } else if (SearchConstants.CRE_SYSTEM_KEY.equals(prop)) {
		    	criteria.add(Restrictions.eq("systemKey", f.getValue()));
		    	logger.debug("adding systemKey=" + f.getValue());
		    } else if (SearchConstants.CRE_SYSTEM.equals(prop)) {
		    	criteria.add(Restrictions.eq("system", f.getValue()));
		    	logger.debug("adding system=" + f.getValue());
		    } else {
				if (prop != null) {
				    logger.debug("unknown filter (" + prop + "); return empty");
				} else {
				    logger.debug("unknown filter (null); return empty");
				}
				return;
		    }
		}

    	logger.debug("run query__: " + criteria.toString());
    	
    	criteria.setMaxResults(searchParams.getPageSize());
    	criteria.setFirstResult(searchParams.getStartIndex());
    	
    	
        @SuppressWarnings("unchecked")
        List<AlleleSystem> qr = criteria.list();
        
        logger.debug("This is the size of the results: " + qr.size());
        logger.debug("-> query complete" );

        
        logger.debug("-> results parsed" );
               
        searchResults.setTotalCount(qr.size());
        searchResults.setResultObjects(qr);        
    }
}
