package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.HomologyCluster;
import org.jax.mgi.fewi.hunter.SolrHomologyHunter;
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
 * This finder is responsible for finding marker(s)
 */

@Repository
public class HomologyFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(MarkerFinder.class);
    
    @Autowired
    private SolrHomologyHunter homologyHunter;

    @Autowired
    private HibernateObjectGatherer<HomologyCluster> homologyGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a marker, for a given ID
    /*-----------------------------------------*/

    public SearchResults<HomologyCluster> getHomologyByID(SearchParams searchParams) {

        logger.debug("->getMarkerByID()");
        // result object to be returned
        SearchResults<HomologyCluster> searchResults = new SearchResults<HomologyCluster>();

        // ask the hunter to identify which objects to return
        homologyHunter.hunt(searchParams, searchResults);
        logger.debug("->hunter found these resultKeys - "
          + searchResults.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        
//        List<HomologyCluster> homologyList = new LinkedList<HomologyCluster>();
//        homologyList.add(gatherer.get( HomologyCluster.class, "1"));
//        searchResults.setResultObjects(homologyList);
        
        List<HomologyCluster> homologyList = homologyGatherer.get( HomologyCluster.class, searchResults.getResultKeys() );
        searchResults.setResultObjects(homologyList);
        
        return searchResults;
    }
    
    public List<HomologyCluster> getHomologyClusterByID(String id)
    {
        return getHomologyByID(Arrays.asList(id));
    }
    public List<HomologyCluster> getHomologyByID(List<String> id)
    {
        return homologyGatherer.get( HomologyCluster.class, id, "primaryID" );
    }

    public HomologyCluster getClusterByKey(String key) {
    	logger.debug("->getClusterByKey(" + key + ")");
    	return homologyGatherer.get (HomologyCluster.class, key);
    }
}
