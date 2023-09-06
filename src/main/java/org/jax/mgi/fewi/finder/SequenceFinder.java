package org.jax.mgi.fewi.finder;

import java.util.List;

import org.jax.mgi.fe.datamodel.Sequence;
import org.jax.mgi.fewi.hunter.HibernateSequenceHunter;
import org.jax.mgi.fewi.hunter.SolrSequenceHunter;
import org.jax.mgi.fewi.hunter.SolrSequenceStrainFacetHunter;
import org.jax.mgi.fewi.hunter.SolrSequenceTypeFacetHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.shr.jsonmodel.SimpleSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding sequences
 */
@Repository
public class SequenceFinder {

	/*--------------------*/
	/* instance variables */
	/*--------------------*/

    private final Logger logger = LoggerFactory.getLogger(SequenceFinder.class);
    
    @Autowired 
    private HibernateSequenceHunter hibernateSequenceHunter;

    @Autowired 
    private SolrSequenceHunter solrSequenceHunter;

    @Autowired 
    private SolrSequenceTypeFacetHunter typeFacetHunter;

    @Autowired 
    private SolrSequenceStrainFacetHunter strainFacetHunter;

    @Autowired
    private HibernateObjectGatherer<Sequence> sequenceGatherer;

	/*-----------------------------------------*/
	/* Retrieval of a sequence, for a given ID (submitted as a String)
	 * Returns null if seqID does not match a single Sequence object as
	 * its primary ID.
	/*-----------------------------------------*/
    public Sequence getSequenceByID(String seqID) {
        logger.debug("->SequenceFinder.getSequenceByID(" + seqID + ")");

	List<Sequence> seqList = this.getSequencesByID(seqID);

	if (seqList.size() != 1) {
	    logger.debug("->found " + seqList.size() + " sequences for ID " + seqID);
	    return null;
	}
	return seqList.get(0);
    }

	/*-----------------------------------------*/
	/* Retrieval of all sequences matching a given ID (submitted as a String)
	/*-----------------------------------------*/
    public List<Sequence> getSequencesByID(String seqID) {
        logger.debug("->SequenceFinder.getSequencesByID(" + seqID + ")");

        SearchParams params = new SearchParams();
        params.setFilter(new Filter(SearchConstants.SEQ_ID, seqID) );
        
        SearchResults<Sequence> results = getSequenceByID(params);
        return results.getResultObjects();
    }

	/*-----------------------------------------*/
	/* Retrieval of a sequence, for a given ID
	/*-----------------------------------------*/

    public SearchResults<Sequence> getSequenceByID(SearchParams searchParams) {

        logger.debug("->SequenceFinder.getSequenceByID()");
        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();

        // ask the hunter to identify which objects to return
        hibernateSequenceHunter.hunt(searchParams, searchResults, false);
        logger.debug("->hunter found these resultKeys - "+ searchResults.getResultKeys());

        return searchResults;
    }


	/*--------------------------------------------*/
	/* Retrieval of a sequence, for a given db key
	/*--------------------------------------------*/

    public SearchResults<Sequence> getSequenceByKey(String dbKey) {

        logger.debug("->SequenceFinder.getSequenceByKey()");

        // result object to be returned
        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();

        // gather objects, add them to the results
        Sequence seq = sequenceGatherer.get( Sequence.class, dbKey );
        searchResults.addResultObjects(seq);

        return searchResults;
    }


	/*---------------------------------*/
	/* Retrieval of multiple sequence
	/*---------------------------------*/

    public SearchResults<SimpleSequence> getSequences(SearchParams searchParams) {
        logger.debug("->SequenceFinder.getSequences()");

        // result object to be returned
        SearchResults<SimpleSequence> searchResults = new SearchResults<SimpleSequence>();

        // ask the hunter to identify which objects to return
        solrSequenceHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "+ searchResults.getResultKeys());

        return searchResults;
    }
    
    /* retrieval of strain facet
     */
	public SearchResults<SimpleSequence> getStrainFacet(SearchParams params) {
		SearchResults<SimpleSequence> results = new SearchResults<SimpleSequence>();
		strainFacetHunter.hunt (params, results);
		return results;
	}

    /* retrieval of type facet
     */
	public SearchResults<SimpleSequence> getTypeFacet(SearchParams params) {
		SearchResults<SimpleSequence> results = new SearchResults<SimpleSequence>();
		typeFacetHunter.hunt (params, results);
		return results;
	}
}
