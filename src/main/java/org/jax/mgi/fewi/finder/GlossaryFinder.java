package org.jax.mgi.fewi.finder;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fe.datamodel.GlossaryTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding glossary terms
 */

@Repository
public class GlossaryFinder 
{
    
    @Autowired
	private SessionFactory sessionFactory;


    /* returns all glossary terms for the index page */
    @SuppressWarnings("unchecked")
	public List<GlossaryTerm> getGlossaryIndex()
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(GlossaryTerm.class);
    	query.addOrder(Order.asc("displayName"));
    	return query.list();
    }
    
	public GlossaryTerm getGlossaryTerm(String glossaryKey)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(GlossaryTerm.class).add(Restrictions.eq("glossaryKey", glossaryKey));
    	return (GlossaryTerm) query.uniqueResult();
    }
}
