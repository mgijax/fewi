package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.VocabTerm;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding vocabulary terms
 */

@Repository
public class VocabularyFinder 
{
    private Logger logger = LoggerFactory.getLogger(VocabularyFinder.class);
    
    @Autowired
	private SessionFactory sessionFactory;

    @Autowired
    private HibernateObjectGatherer<VocabTerm> termGatherer;

    /* 
     * returns all vocab terms for the vocabulary beginning with the subsetLetter
     * e.g All terms beginning with A or B, etc
     */
    public List<VocabTerm> getVocabSubset(String vocabName, String subsetLetter)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(VocabTerm.class);
    	query.add(Restrictions.eq("vocabName",vocabName))
    		.add(Restrictions.eq("isObsolete",0));
    	if(subsetLetter.equals("0-9"))
    	{
    		// need to OR all the numbers
    		query.add(Restrictions.disjunction()
    				.add(Restrictions.ilike("term","0%"))
    				.add(Restrictions.ilike("term","1%"))
    				.add(Restrictions.ilike("term","2%"))
    				.add(Restrictions.ilike("term","3%"))
    				.add(Restrictions.ilike("term","4%"))
    			    .add(Restrictions.ilike("term","5%"))
    			    .add(Restrictions.ilike("term","6%"))
    			    .add(Restrictions.ilike("term","7%"))
    			    .add(Restrictions.ilike("term","8%"))
    			    .add(Restrictions.ilike("term","9%"))
    		);
    	}
    	else query.add(Restrictions.ilike("term",subsetLetter+"%"));
    	query.addOrder(Order.asc("term"));
    	return query.list();
    }

    public List<VocabTerm> getTermByID(String id) {
	logger.debug("->getTermByID(" + id + ")");

	return termGatherer.get (VocabTerm.class, id, "primaryId");
    }
}
