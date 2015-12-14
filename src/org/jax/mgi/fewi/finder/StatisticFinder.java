package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.Statistic;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding MGI Statistics
 */

@Repository
public class StatisticFinder 
{
    
    @Autowired
    private SessionFactory sessionFactory;

    /*
     * Return all statistics
     */
    @SuppressWarnings("unchecked")
   	public List<Statistic> getAllStatistics()
       {
       	Session s = sessionFactory.getCurrentSession();
       	Criteria query = s.createCriteria(Statistic.class);
       	
       	query.addOrder(Order.asc("groupSequenceNum"));
       	query.addOrder(Order.asc("sequenceNum"));
       	return query.list();
       }
    
    
    /* 
     * returns all statistics for a group
     */
    @SuppressWarnings("unchecked")
	public List<Statistic> getStatisticsByGroup(String groupName)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(Statistic.class);
    	query.add(Restrictions.eq("groupName", groupName));
    	
    	query.addOrder(Order.asc("groupSequenceNum"));
    	query.addOrder(Order.asc("sequenceNum"));
    	return query.list();
    }
    
    
    /*
     * Get statistic by name
     * 
     * 	Chooses only one statistic record if it has multiple groups
     */
    @SuppressWarnings("unchecked")
	public Statistic getStatisticByName(String name)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(Statistic.class);
    	query.add(Restrictions.eq("name", name));
    	
    	Statistic statistic = null;
    	
    	List<Statistic> results = query.list();
    	if (results.size() > 0) {
    		statistic = results.get(0);
    	}
    	
    	return statistic;
    }
    
    /*
     * Get statistic by abbreviation
     * 
     * 	Chooses only one statistic record if it has multiple groups
     */
    @SuppressWarnings("unchecked")
	public Statistic getStatisticByAbbreviation(String abbreviation)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(Statistic.class);
    	query.add(Restrictions.eq("abbreviation", abbreviation));
    	
    	Statistic statistic = null;
    	
    	List<Statistic> results = query.list();
    	if (results.size() > 0) {
    		statistic = results.get(0);
    	}
    	
    	return statistic;
    }
}
