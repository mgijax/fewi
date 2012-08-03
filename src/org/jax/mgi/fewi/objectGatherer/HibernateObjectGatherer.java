package org.jax.mgi.fewi.objectGatherer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HibernateObjectGatherer<T> implements ObjectGathererInterface<T> {

	private Logger logger = LoggerFactory.getLogger(HibernateObjectGatherer.class);

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Get a single object
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public T get(Class<T> modelObj, String keyStr) {

        logger.debug("gathering object for keys - " + keyStr);

        Integer key = new Integer(keyStr);
		if (sessionFactory != null && modelObj != null){
			Session s = sessionFactory.getCurrentSession();
			return (T)s.get(modelObj, key);
		}

		// problem conditions
		if (sessionFactory == null){
			System.out.println("null sessionFactory");
		}
		if (modelObj == null){
			System.out.println("null type");
		}
		return null;
	}


    /**
     * Get a list of objects;  doesn't use Criteria query, and instead
     * gathers the objects individually.  Useful to see which object
     * is throwing an exception during instantiation
     */
    @Transactional(readOnly = true)
    public List<T> getIndividually(Class<T> modelObj, List<String> keys) {

        logger.debug("gathering objects for keys - " + keys);

        List<T> results = new ArrayList<T>();

        for (String key : keys) {
            results.add(this.get(modelObj, key));
        }
        return results;
    }


	/**
	 * Get a list of objects
	 */
	@Transactional(readOnly = true)
	public List<T> get(Class<T> modelObj, List<String> keys) {

		logger.debug("Started : objects keys - " + keys);

		// get necessary Hibernate objects
		Session s = sessionFactory.getCurrentSession();
		ClassMetadata meta = sessionFactory.getClassMetadata(modelObj);

		// collections to process results
		List<T> queryResults = new ArrayList<T>();
		Map<String, T> resultsMap = new LinkedHashMap<String, T>();
		List<T> orderedResults = new ArrayList<T>();

		// if no keys to lookup, return empty list
		if (keys.size() == 0){
			return orderedResults;
		}

		List<Integer> keyInts = new ArrayList<Integer>();

		// convert keys to Integers
		for (String key : keys) {
			keyInts.add(new Integer(key));
		}

		// get results chunking if needed.
		long start = System.nanoTime();
		int step = 10000;
		if (keyInts.size() > 10000) {
			int begin = 0;
			int end = step;

			while (begin < keyInts.size()) {
				if (end > keyInts.size()) {
					end = keyInts.size();
				}

				List inList = keyInts.subList(begin, end);

				queryResults.addAll(s.createCriteria(modelObj).add(Restrictions.in("id", inList)).list());
				begin += step;
				end += step;
			}

		}
		else {
			queryResults = s.createCriteria(modelObj).add(Restrictions.in("id", keyInts)).list();
		}

		// load results into Map keyed by id
		String id;
		for (T r : queryResults) {
			id = meta.getIdentifier(r, s.getEntityMode()).toString();
			resultsMap.put(id, r);
		}
		// kill queryResults
		queryResults = null;

		// reorder results
		for (String t : keys) {
			orderedResults.add(resultsMap.get(t));
		}
		resultsMap = new LinkedHashMap<String, T>();
		
		//logger.debug("Gatherer time: " + (System.nanoTime() - start)/(60*60*1000F));
		logger.debug("Finished");

		return orderedResults;
	}

}
