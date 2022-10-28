package org.jax.mgi.fewi.hunter;

import java.lang.Object;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A class that allows you to do SQL queries in the fe db, because sometimes you just need to.
 */
@Repository
public class HibernateSqlHunter {

    // logger for the class
    private final Logger logger = LoggerFactory.getLogger (HibernateSqlHunter.class);

    @Autowired
    private SessionFactory sessionFactory;

    public List<List<String>> sql (String query) {
        logger.info("Doing SQL query: " + query);
        Session session = sessionFactory.getCurrentSession();
        SQLQuery squery = session.createSQLQuery(query);
        List<Object[]> rows = squery.list();
        List<List<String>> retVal = new ArrayList<List<String>>();
        for(Object[] row : rows){
            List<String> r = new ArrayList<String>();
            for (int i=0; i < row.length; i++) {
                r.add(row[i].toString());
            }
            retVal.add(r);
        }
        logger.info("Query returned " + retVal.size() + " rows.");
        return retVal;
    }
}
