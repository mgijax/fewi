package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fe.datamodel.VocabTermID;
import org.jax.mgi.fewi.hunter.SolrAnatomyTermHunter;
import org.jax.mgi.fewi.hunter.SolrBrowserTermHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
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
    private HibernateObjectGatherer<VocabTermID> termIdGatherer;

    @Autowired
    private SolrAnatomyTermHunter anatomyTermHunter;

    @Autowired
    private SolrBrowserTermHunter browserTermHunter;

    /* 
     * returns all vocab terms for the vocabulary beginning with the subsetLetter
     * e.g All terms beginning with A or B, etc.  If subsetLetter is null, gets
     * all terms in the specified vocabulary.
     */
    @SuppressWarnings("unchecked")
	public List<VocabTerm> getVocabSubset(String vocabName, String subsetLetter)
    {
    	Session s = sessionFactory.getCurrentSession();
    	Criteria query = s.createCriteria(VocabTerm.class);
    	query.add(Restrictions.eq("vocabName",vocabName))
    		.add(Restrictions.eq("isObsolete",0));
    	if (subsetLetter == null) {
    		logger.debug("Retrieving all terms in: " + vocabName);

    	} else if(subsetLetter.equals("0-9"))
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

    	// if we match the primary ID for at least one term, go with it.  Otherwise,
    	// look for terms by secondary IDs.

    	List<VocabTerm> terms = termGatherer.get (VocabTerm.class, id, "primaryId");
    	if ((terms != null) && (terms.size() > 0)) {
    		return terms;
    	}
    	
    	List<VocabTermID> termIDs = termIdGatherer.get(VocabTermID.class, id, "accID");
    	if ((termIDs != null) && (termIDs.size() > 0)) {
    		terms = new ArrayList<VocabTerm>();
    		for (VocabTermID termID : termIDs) {
    			terms.add(getTermByKey(termID.getTermKey().toString()));
    		}
    		return terms;
    	}
    	return null;
    }
    
	public VocabTerm getTermByKey(String key) {
		logger.debug("->getTermByKey(" + key + ")");
		return termGatherer.get (VocabTerm.class, key);
	}

	public List<VocabTerm> getTermsByID(List<String> ids) {
		return termGatherer.get(VocabTerm.class, ids, "primaryId");
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
    
    /*--- shared vocab browser methods -----------------------------------------------------*/
    
    /* quick lookup based solely on ID
     */
    public List<BrowserTerm> getBrowserTerm (String id) {
    	return getBrowserTerm(id, null, null);
    }

    /* quick lookup based on ID and vocab
     */
    public List<BrowserTerm> getBrowserTerm (String id, String vocabulary) {
    	return getBrowserTerm(id, vocabulary, null);
    }

    /* quick lookup based on ID, vocab, and DAG
     */
    public List<BrowserTerm> getBrowserTerm (String id, String vocabulary, String dag) {
    	List<Filter> filters = new ArrayList<Filter>();
    	filters.add(new Filter(SearchConstants.VB_ACC_ID, id.trim()));
    	if (vocabulary != null) {
    		filters.add(new Filter(SearchConstants.VB_VOCAB_NAME, vocabulary));
    	}
    	if (dag != null) {
    		filters.add(new Filter(SearchConstants.VB_DAG_NAME, dag));
    	}
    	
    	SearchParams sp = new SearchParams();
    	sp.setPageSize(2);						// just need to know if there's more than one

    	if (filters.size() > 1) { sp.setFilter(Filter.and(filters)); }
    	else { sp.setFilter(filters.get(0)); }
    	
    	SearchResults<BrowserTerm> results = getBrowserTerms(sp);
    	return results.getResultObjects();
    }
    
    /* more flexible searching, using standard SearchParams object
     */
    public SearchResults<BrowserTerm> getBrowserTerms (SearchParams searchParams) {
    	logger.debug("->VocabularyFinder.getBrowserTerms()");

    	// result object to be returned
    	SearchResults<BrowserTerm> searchResults = new SearchResults<BrowserTerm>();

    	// ask the hunter to identify which objects to return
    	browserTermHunter.hunt(searchParams, searchResults);
    	logger.debug("->hunter found " + searchResults.getResultObjects().size() + " BrowserTerms");

    	return searchResults;
    }
}
