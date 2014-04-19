package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import mgi.frontend.datamodel.Disease;

import org.jax.mgi.fewi.hunter.SolrDiseaseHunter;
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

/*
 * This finder is responsible for finding disease(s)
 */

@Repository
public class DiseaseFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(DiseaseFinder.class);
    
    @Autowired
    private SolrDiseaseHunter diseaseHunter;

    @Autowired
    private HibernateObjectGatherer<Disease> diseaseGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a disease, for a given ID
    /*-----------------------------------------*/

    public SearchResults<Disease> getDiseaseByKey(String dbKey) {

        logger.debug("->getDiseaseByKey()");

        // result object to be returned
        SearchResults<Disease> searchResults = new SearchResults<Disease>();

        // gather objects, add them to the results
        Disease disease = diseaseGatherer.get( Disease.class, dbKey );
        searchResults.addResultObjects(disease);

        return searchResults;
    }

    public SearchResults<Disease> getDiseaseByID(SearchParams searchParams) {

        logger.debug("->getDiseaseByID()");
        // result object to be returned
        SearchResults<Disease> searchResults = new SearchResults<Disease>();

        // ask the hunter to identify which objects to return
        diseaseHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        
        List<Disease> diseaseList = diseaseGatherer.get( Disease.class,
		searchResults.getResultKeys() );
        searchResults.setResultObjects(diseaseList);
        
        return searchResults;
    }
    public List<Disease> getDiseaseByID(String diseaseID)
    {
        return getDiseaseByID(Arrays.asList(diseaseID));
    }
    public List<Disease> getDiseaseByID(List<String> diseaseID)
    {
        return diseaseGatherer.get( Disease.class, diseaseID, "primaryID" );
    }
    
//    // convenience wrapper
//    public SearchResults<Disease> getDiseaseByID(String id) {
//        SearchParams searchParams = new SearchParams();
//        searchParams.setFilter(new Filter(SearchConstants.DISEASE_ID,id,Filter.OP_EQUAL));
//        return getDiseaseByID(searchParams);
//    }
    
}
