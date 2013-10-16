package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.MarkerTissueCount;
import mgi.frontend.datamodel.snp.SnpStrain;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

/*-------*/
/* class */
/*-------*/

/*
 * This finder is responsible for finding SNP data
 */

@Repository
@Transactional(value="transactionManagerSnp",readOnly=true)
public class SnpFinder {

    /*--------------------*/
    /* instance variables */
    /*--------------------*/

    private Logger logger = LoggerFactory.getLogger(SnpFinder.class);
    
    @Autowired
	private SessionFactory sessionFactorySnp;

    /*---------------------------------*/
    /* Retrieval of SNP data
    /*---------------------------------*/
    @Transactional(value="transactionManagerSnp",readOnly=true)
	public List<SnpStrain> getSnpStrains()
    {
    	Session s = sessionFactorySnp.openSession();
    	//Session s = sessionFactorySnp.getCurrentSession();
    	//Session s = sessionFactorySnp.getCurrentSession();
    	Criteria query = s.createCriteria(SnpStrain.class);
    	query.addOrder(Order.asc("sequenceNum"));
    	List<SnpStrain> strains = query.list();
    	s.close();
    	return strains;
    }
}
