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
	 * default is by primary key
	 */
	@Transactional(readOnly = true)
	public List<T> get(Class<T> modelObj, List<String> keys) {

		return get(modelObj,keys,"id");
	}
	
	/**
	 * Get a list of objects
	 * by specified field name
	 */
	@Transactional(readOnly = true)
	public List<T> get(Class<T> modelObj, List<String> keys, String fieldName) {

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

		// format the keys to Integer objects if possible
		List<Object> keyObjs = formatKeys(keys);
		
		// get results chunking if needed.
		long start = System.nanoTime();
		int step = 10000;
		if (keyObjs.size() > 10000) {
			int begin = 0;
			int end = step;

			while (begin < keyObjs.size()) {
				if (end > keyObjs.size()) {
					end = keyObjs.size();
				}

				List inList = keyObjs.subList(begin, end);

				queryResults.addAll(s.createCriteria(modelObj).add(Restrictions.in(fieldName, inList)).list());
				begin += step;
				end += step;
			}

		}
		else {
			queryResults = s.createCriteria(modelObj).add(Restrictions.in(fieldName, keyObjs)).list();
		}

		// load results into Map keyed by id
		if(fieldName.equals("id"))
		{
		String id;
			for (T r : queryResults) {
				id = meta.getIdentifier(r, s.getEntityMode()).toString();
				resultsMap.put(id, r);
			}
		}
		else
		{
			for (T r : queryResults) {
				String key = meta.getPropertyValue(r, fieldName, s.getEntityMode()).toString();
				resultsMap.put(key, r);
			}
		}
		// kill queryResults
		queryResults = null;

		// reorder results
		for (String t : keys) {
			// There is a Hibernate bug where it occassionally returns null objects. Not sure how to fix it in the datamodel yet.
			// This is a catch to prevent nulls from getting to the controllers -kstone 9-18-2012
			if(resultsMap.get(t)!=null) orderedResults.add(resultsMap.get(t));
		}
		resultsMap = new LinkedHashMap<String, T>();
		
		//logger.debug("Gatherer time: " + (System.nanoTime() - start)/(60*60*1000F));
		logger.debug("Finished");

		return orderedResults;
	}
	
	/*
	 * Formats keys to Integer if they parse as ints, else return the original list
	 */
	public List<Object> formatKeys(List<String> keys)
	{
		List<Object> keyObjs = new ArrayList<Object>();
		try
		{
			// convert keys to Integers
			for (String key : keys) {
				keyObjs.add(new Integer(key));
			}
			return keyObjs;
		}
		catch (NumberFormatException ne)
		{
			for(String key : keys)
			{
				keyObjs.add(key);
			}
			return keyObjs;
		}
	}

}
