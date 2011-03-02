package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.GxdLitGeneRecord;
import mgi.frontend.datamodel.GxdLitReferenceRecord;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.GxdLitIndexRecord;

import org.jax.mgi.fewi.hunter.SolrGxdLitSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*-------*/
/* class */
/*-------*/

/**
 * This finder is responsible for putting together GxdLitRecords, and its 
 * unique in that its constructing these records as it goes.
 * 
 * It pulls in marker and references from the database as it iterates 
 * through the data to do this.
 * 
 */

@Repository
public class GxdLitFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(GxdLitFinder.class);
    
    @Autowired
    private SolrGxdLitSummaryHunter gxdLitSummaryHunter;
    
    @Autowired
    private HibernateObjectGatherer<GxdLitIndexRecord> gxdLitIndexGatherer;    
    

    /*---------------------------------*/
    /* Retrieval of multiple records
    /*---------------------------------*/

    public SearchResults<GxdLitIndexRecord> getGxdLitRecords(SearchParams searchParams) {

        logger.debug("->getGXDLitRecords");

        // result object to be returned
        SearchResults<GxdLitIndexRecord> searchResults = 
        		new SearchResults<GxdLitIndexRecord>();
        
        // ask the hunter to identify which objects to return
        gxdLitSummaryHunter.hunt(searchParams, searchResults);

        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());
        
        gxdLitIndexGatherer.setType(GxdLitIndexRecord.class);
        
        List<GxdLitIndexRecord> recordList =
    		new ArrayList<GxdLitIndexRecord> ();
        
        logger.debug("Right before the request");
        
        recordList = gxdLitIndexGatherer.get(searchResults.getResultKeys());
        
        logger.debug("Got past the hibernate request");
              
        searchResults.setResultObjects(recordList);
        
        return searchResults;
    }



}
