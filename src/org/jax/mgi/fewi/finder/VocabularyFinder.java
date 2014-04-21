package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.VocabTerm;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fewi.hunter.SolrAnatomyTermHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm;
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
    private final Logger logger = LoggerFactory.getLogger(VocabularyFinder.class);
    
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateObjectGatherer<VocabTerm> termGatherer;

    @Autowired
    private SolrAnatomyTermHunter anatomyTermHunter;

    /* 
     * returns all vocab terms for the vocabulary beginning with the subsetLetter
     * e.g All terms beginning with A or B, etc
     */
    @SuppressWarnings("unchecked")
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

    /** get the anatomy terms which match the given search parameters
     * (for searching on the new anatomy browser)
     */
    public SearchResults<SolrAnatomyTerm> getAnatomyTerms (
	SearchParams searchParams) {

	logger.debug("->VocabularyFinder.getAnatomyTerms()");

	// result object to be returned
	SearchResults<SolrAnatomyTerm> searchResults =
	    new SearchResults<SolrAnatomyTerm>();

	// ask the hunter to identify which objects to return
	anatomyTermHunter.hunt(searchParams, searchResults);
	logger.debug("->hunter found "
	    + searchResults.getResultObjects().size() + " anatomy terms");

	return searchResults;
    }
}
