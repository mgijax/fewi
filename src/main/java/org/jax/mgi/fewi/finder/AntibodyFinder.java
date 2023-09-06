package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.jax.mgi.fe.datamodel.Antibody;
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
 * This finder is responsible for finding Antibody objects by key or ID
 */
@Repository
public class AntibodyFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(AntibodyFinder.class);
    
    @Autowired
    private HibernateObjectGatherer<Antibody> antibodyGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a antibody, for a given ID
    /*-----------------------------------------*/

    public List<Antibody> getAntibodyByKey(String dbKey) {

        logger.debug("->getAntibodyByKey()");

        // gather objects, add them to the results
        Antibody antibody = antibodyGatherer.get( Antibody.class, dbKey );

        List<Antibody> results = new ArrayList<Antibody>();
	results.add(antibody);

        return results;
    }

    public List<Antibody> getAntibodyByID(String antibodyID)
    {
        return getAntibodyByID(Arrays.asList(antibodyID));
    }

    public List<Antibody> getAntibodyByID(List<String> antibodyID)
    {
        return antibodyGatherer.get( Antibody.class, antibodyID, "primaryID" );
    }
}
