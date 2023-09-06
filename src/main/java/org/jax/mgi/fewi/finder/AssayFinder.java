package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.ExpressionAssay;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AssayFinder {

	private Logger logger = LoggerFactory.getLogger(AssayFinder.class);

	@Autowired
	private HibernateObjectGatherer<ExpressionAssay> assayGatherer;


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of an genotype, for a given ID
    /////////////////////////////////////////////////////////////////////////

	public List<ExpressionAssay> getAssayByID(String assayID)
	{
        logger.debug("->getAssayByID " + assayID);
		return getAssayByID(Arrays.asList(assayID));
	}
	public List<ExpressionAssay> getAssayByID(List<String> assayID)
	{
		return assayGatherer.get( ExpressionAssay.class, assayID, "primaryID" );
	}


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of an  assay, for a database key
    /////////////////////////////////////////////////////////////////////////

    public ExpressionAssay getAssayByKey(String dbKey) {

        logger.debug("->getAssayByKey()");
        return assayGatherer.get( ExpressionAssay.class, dbKey );
    }


}
