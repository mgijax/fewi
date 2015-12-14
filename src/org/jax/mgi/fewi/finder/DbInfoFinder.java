package org.jax.mgi.fewi.finder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mgi.frontend.datamodel.DatabaseInfo;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding (s)
 */

@Repository
public class DbInfoFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/
    private final Logger logger = LoggerFactory.getLogger(DbInfoFinder.class);
    

    @Autowired
    private SessionFactory sessionFactory;

    /*
     * Get the MGD source database date
     */
    @SuppressWarnings("unchecked")
	public Date getSourceDatabaseDate() {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(DatabaseInfo.class);
    	query.add(Restrictions.eq("name", "built from mgd database date"));
    	
    	List<DatabaseInfo> infos = query.list();
    	if (infos.size() > 0){
    		DatabaseInfo info = infos.get(0);
    		DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
			try {
				return df.parse(info.getValue());
			} catch (ParseException e) {
				logger.error("Could not convert database date", e);
				e.printStackTrace();
			}
    	}
    	return null;
    }

}
