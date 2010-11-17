package org.jax.mgi.fewi.finder;

import java.util.*;

// fewi & data model objects
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;

// mgi classes
import mgi.frontend.datamodel.*;
import org.jax.mgi.fewi.hunter.SolrSequenceKeyHunter;
import org.jax.mgi.fewi.hunter.SolrSequenceSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;

// external libs
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SequenceFinder {

    private Logger logger = LoggerFactory.getLogger(SequenceFinder.class);

    @Autowired
    private SolrSequenceKeyHunter sequenceHunter;

    @Autowired
    private SolrSequenceSummaryHunter solrSequenceSummaryHunter;


    @Autowired
    private HibernateObjectGatherer<Sequence> sequenceGatherer;


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of a sequence, for a given ID
    /////////////////////////////////////////////////////////////////////////

    public SearchResults<Sequence> getSequenceByID(SearchParams searchParams) {

        logger.info("SequenceFinder.getSequenceByID()");

        // result object to be returned
        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();

        // ask the hunter to identify which objects to return
        sequenceHunter.hunt(searchParams, searchResults);
        logger.debug("-->hunt() resultKeys =" + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        sequenceGatherer.setType(Sequence.class);
        List<Sequence> seqList = sequenceGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(seqList);

        return searchResults;
    }


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of sequences
    /////////////////////////////////////////////////////////////////////////

    public SearchResults<Sequence> getSequences(SearchParams searchParams) {

        logger.debug("SequenceFinder.getSequences()");

        // gather objects identified by the hunter, add them to the results
        sequenceGatherer.setType(Sequence.class);

        // result object to be returned
        SearchResults<Sequence> searchResults = new SearchResults<Sequence>();

        // ask the hunter to identify which objects to return
        solrSequenceSummaryHunter.hunt(searchParams, searchResults);
        logger.debug("-->hunt() resultKeys =" + searchResults.getResultKeys());


        List<Sequence> seqList = sequenceGatherer.get( searchResults.getResultKeys() );
        searchResults.setResultObjects(seqList);

        return searchResults;
    }



}
