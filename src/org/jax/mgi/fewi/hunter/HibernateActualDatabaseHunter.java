package org.jax.mgi.fewi.hunter;

import java.util.List;

import mgi.frontend.datamodel.ActualDatabase;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HibernateActualDatabaseHunter
{
    //--------------------------//
    //--- instance variables ---//
    //--------------------------//
    
    @Autowired
    private SessionFactory sessionFactory;
	
    //----------------------//
    //--- public methods ---//
    //----------------------//

    @SuppressWarnings("unchecked")
	public void hunt(SearchParams searchParams,SearchResults<ActualDatabase> searchResults) 
    {
        //logger.debug("HibernateActualDatabaseHunter.hunt()");         
        
    	StringBuffer hql = new StringBuffer("FROM ActualDatabase");
		if (sessionFactory == null) {
			searchResults.setTotalCount(0);
			return;
		}

        Query query = sessionFactory.getCurrentSession().createQuery(
		hql.toString() );

        List<ActualDatabase> qr = query.list();
        searchResults.setTotalCount(qr.size());
        searchResults.setResultObjects(qr);        
    }
}
