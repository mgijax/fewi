package org.jax.mgi.fewi.finder;

import java.util.*;

/*-------------------------------*/
/* to be changed for each Finder */
/*-------------------------------*/

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerSequenceAssociation;
import mgi.frontend.datamodel.MarkerTissueCount;
import mgi.frontend.datamodel.Sequence;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Example;
import org.hibernate.NullPrecedence;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fewi.hunter.HibernateSequenceHunter;
import org.jax.mgi.fewi.hunter.SolrSequenceKeyHunter;
import org.jax.mgi.fewi.hunter.SolrSequenceSummaryHunter;

/*----------------------------------------*/
/* standard classes, used for all Finders */
/*----------------------------------------*/

// fewi
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;

// external libs
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

    private Logger logger = LoggerFactory.getLogger(SequenceFinder.class);
    
    //@Autowired
    //private SolrSequenceKeyHunter sequenceHunter;
    
    @Autowired 
    private HibernateSequenceHunter hibernateSequenceHunter;

    //@Autowired
    //private SolrSequenceSummaryHunter solrSequenceSummaryHunter;

    @Autowired
    private HibernateObjectGatherer<Sequence> sequenceGatherer;


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

//        // result object to be returned
//        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();
//
//        // ask the hunter to identify which objects to return
//        sequenceHunter.hunt(searchParams, searchResults);
//        logger.debug("->hunter found these resultKeys - "
//          + searchResults.getResultKeys());
//
//        // gather objects identified by the hunter, add them to the results
//        List<Sequence> seqList = sequenceGatherer.get( Sequence.class, searchResults.getResultKeys() );
//        searchResults.setResultObjects(seqList);
//
//        return searchResults;
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

    public SearchResults<Sequence> getSequences(SearchParams searchParams) {

    	return getSequencesByHibernate(searchParams);
//        logger.debug("->SequenceFinder.getSequences()");
//
//        // result object to be returned
//        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();
//
//        // ask the hunter to identify which objects to return
//        solrSequenceSummaryHunter.hunt(searchParams, searchResults);
//        logger.debug("->hunter found these resultKeys - "
//          + searchResults.getResultKeys());
//
//        // gather objects identified by the hunter, add them to the results
//        List<Sequence> seqList = sequenceGatherer.get( Sequence.class, searchResults.getResultKeys() );
//        searchResults.setResultObjects(seqList);
//
//        return searchResults;
    }

    
    
    public SearchResults<Sequence> getSequencesByHibernate(SearchParams searchParams) {

        logger.debug("->SequenceFinder.getSequences()");

        // result object to be returned
        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();

        // ask the hunter to identify which objects to return
        hibernateSequenceHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "+ searchResults.getResultKeys());

        return searchResults;
    }
}
