package org.jax.mgi.fewi.finder;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fe.datamodel.WKSilversMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding WKSilversMarker objects
 */

@Repository
public class WKSilversMarkerFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/
    private final Logger logger = LoggerFactory.getLogger(WKSilversMarkerFinder.class);
    
    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
	public List<WKSilversMarker> getWKSilversMarkers() {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(WKSilversMarker.class)
    		.add(Restrictions.ge("uniqueKey", 0))
    		.addOrder(Order.asc("sequenceNum"));
    	logger.info(query.toString());
    	logger.info("Found " + query.list().size() + " markers");
    	return query.list();
    }
}
