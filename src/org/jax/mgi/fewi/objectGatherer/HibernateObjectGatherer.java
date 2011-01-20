package org.jax.mgi.fewi.objectGatherer;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HibernateObjectGatherer<T> implements ObjectGathererInterface<T> {

	private Logger logger = LoggerFactory.getLogger(HibernateObjectGatherer.class);

	private Class<T> type;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Get a single object
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public T get(String keyStr) {

        Integer key = new Integer(keyStr);


        //logger.debug("gatherer get key: " + key);
		if (sessionFactory != null && type != null){
			Session s = sessionFactory.getCurrentSession();
			return (T)s.get(type, key);
		}

		// problem conditions
		if (sessionFactory == null){
			System.out.println("null sessionFactory");
		}
		if (type == null){
			System.out.println("null type");
		}
		return null;
	}

	/**
	 * Get a list of objects
	 */
	@Transactional(readOnly = true)
	public List<T> get(List<String> keys) {

		Session s = sessionFactory.getCurrentSession();
		
		List<T> results = new ArrayList<T>();		
		List<Integer> k = new ArrayList<Integer>();
		
		long start = System.nanoTime();
		for (String key : keys) {
			results.add(this.get(key));
		}
		//results = s.createCriteria(type).add(Restrictions.in("id", k)).list();
		logger.debug("Gatherer time: " + (System.nanoTime() - start)/(60*60*1000F));

		return results;
	}

	/**
	 * Set the type of object for retrieval
	 */
	public void setType(Class<T> type) {
		logger.debug("set type -> " + type);
		this.type = type;
	}

}
