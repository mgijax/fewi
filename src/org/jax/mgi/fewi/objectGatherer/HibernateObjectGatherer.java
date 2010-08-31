package org.jax.mgi.fewi.objectGatherer;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HibernateObjectGatherer<T> implements ObjectGathererInterface<T> {

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

		System.out.println("gatherer get key: " + key);
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

		System.out.println("gatherer get keys");
		List<T> results = new ArrayList<T>();

		for (String key : keys) {
			results.add(this.get(key));
		}
		return results;
	}

	/**
	 * Set the type of object for retrieval
	 */
	public void setType(Class<T> type) {
		System.out.println("set type");
		this.type = type;
	}

}
