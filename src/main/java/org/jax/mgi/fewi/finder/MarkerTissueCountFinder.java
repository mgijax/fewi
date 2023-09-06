package org.jax.mgi.fewi.finder;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fe.datamodel.MarkerTissueCount;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding foo(s)
 */

@Repository
public class MarkerTissueCountFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/
    
    @Autowired
	private SessionFactory sessionFactory;

    /*---------------------------------*/
    /* Retrieval of multiple tissues
    /*---------------------------------*/

    public List<MarkerTissueCount> getTissues(String mrkKey)
    {
    	return getTissues(mrkKey,null);
    }
    
	@SuppressWarnings("unchecked")
	public List<MarkerTissueCount> getTissues(String mrkKey,Paginator page)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(MarkerTissueCount.class).add(Restrictions.eq("markerKey", Integer.parseInt(mrkKey)));
    	if(page!=null)
    	{
    		query.setFirstResult(page.getStartIndex());
    		query.setMaxResults(page.getResults());
    	}
    	query.addOrder(Order.asc("sequenceNum"));
    	return query.list();
    }
	
	public Integer getTissueTotalCount(String mrkKey)
	{
		Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(MarkerTissueCount.class).add(Restrictions.eq("markerKey", Integer.parseInt(mrkKey)));
    	return ((Number) query.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
}
