package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.Term;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/*
 * This finder is responsible for finding Term objects given a term ID
 */

@Repository
public class TermFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    //private Logger logger = LoggerFactory.getLogger(TermFinder.class);
    
    @Autowired
    private HibernateObjectGatherer<Term> termGatherer;


    /*-----------------------------------------*/
    /* Retrieval of a marker, for a given ID or list of IDs
    /*-----------------------------------------*/

    public List<Term> getTermsByID(String id)
    {
        return getTermsByID(Arrays.asList(id));
    }

    public List<Term> getTermsByID(List<String> id)
    {
        return termGatherer.get( Term.class, id, "primaryID" );
    }
}
