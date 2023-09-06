package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.jax.mgi.fe.datamodel.Statistic;
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
	// memory cache of all statistics, so we only hit database once in life of webapp;
	// maps from statistic group name to list of statistic objects
	private Map<String,List<Statistic>> statisticCache = null;

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
    
    /* fill the statisticCache with results from the database; is a no-op if already populated.
     */
    private void populateCache() {
    	if (statisticCache != null) { return; }
    	
    	statisticCache = new HashMap<String,List<Statistic>>();
    	for (Statistic stat : this.getAllStatistics()) {
    		String groupLower = stat.getGroupName().toLowerCase();
    		if (!statisticCache.containsKey(groupLower)) {
    			statisticCache.put(groupLower, new ArrayList<Statistic>());
    		}
    		statisticCache.get(groupLower).add(stat);
    	}
    }
    
    /* returns all statistics for a group or empty list if unknown group.
     */
    @SuppressWarnings("unchecked")
	public List<Statistic> getStatisticsByGroup(String groupName)
    {
    	populateCache();
    	String groupLower = groupName.toLowerCase();
    	if (statisticCache.containsKey(groupLower)) {
    		return statisticCache.get(groupLower);
    	}
    	return new ArrayList<Statistic>();
    }
    
    
    /* Get statistic by name or null if no statistic with that name exists.
     * Chooses only one statistic record if it has multiple groups.
     * Not optimized code, but method may be unused, so not a priority.
     */
    @SuppressWarnings("unchecked")
	public Statistic getStatisticByName(String name)
    {
    	populateCache();
    	for (String group : statisticCache.keySet()) {
    		for (Statistic stat : statisticCache.get(group)) {
    			if (name.equals(stat.getName())) {
    				return stat;
    			}
    		}
    	}
    	return null;
    }
}
