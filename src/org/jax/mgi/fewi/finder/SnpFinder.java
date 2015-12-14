package org.jax.mgi.fewi.finder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding SNP data
 * 
 * NOTE: This is just for testing. It is not used for anything yet. PLEASE IGNORE
 * 
 * 	-kstone
 */

//@Repository
//@Transactional(value="transactionManagerSnp",readOnly=true)
public class SnpFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private final Logger logger = LoggerFactory.getLogger(SnpFinder.class);
    
//    @Autowired
//	private SessionFactory sessionFactorySnp;
//
//    /*---------------------------------*/
//    /* Retrieval of SNP data
//    /*---------------------------------*/
//	public List<SnpStrain> getSnpStrains()
//    {
//    	Session s = sessionFactorySnp.openSession();
//    	//Session s = sessionFactorySnp.getCurrentSession();
//    	//Session s = sessionFactorySnp.getCurrentSession();
//    	Criteria query = s.createCriteria(SnpStrain.class);
//    	query.add(Restrictions.eq("snpStrainKey", 14));
//    	query.addOrder(Order.asc("sequenceNum"));
//    	List<SnpStrain> strains = query.list();
//    	s.close();
//    	return strains;
//    }
}
