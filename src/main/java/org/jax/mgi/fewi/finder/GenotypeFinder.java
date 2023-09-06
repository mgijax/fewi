package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import org.jax.mgi.fe.datamodel.Genotype;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GenotypeFinder {

	private Logger logger = LoggerFactory.getLogger(GenotypeFinder.class);

	@Autowired
	private HibernateObjectGatherer<Genotype> genotypeGatherer;


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of an genotype, for a given ID
    /////////////////////////////////////////////////////////////////////////

	public List<Genotype> getGenotypeByID(String genotypeID)
	{
		return getGenotypeByID(Arrays.asList(genotypeID));
	}
	public List<Genotype> getGenotypeByID(List<String> genotypeID)
	{
		return genotypeGatherer.get( Genotype.class, genotypeID, "primaryID" );
	}


    /////////////////////////////////////////////////////////////////////////
    //  Retrieval of an  genotype, for a database key
    /////////////////////////////////////////////////////////////////////////

    public Genotype getGenotypeByKey(String dbKey) {

        logger.debug("->getGenotypeByKey()");
        return genotypeGatherer.get( Genotype.class, dbKey );
    }


}
