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

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public T get(Integer key) {
		System.out.println("gatherer get key: " + key);
		if (sessionFactory != null && type != null){
			Session s = sessionFactory.getCurrentSession();
			return (T)s.get(type, key);
		} 
		if (sessionFactory == null){
			System.out.println("null sessionFactory");
		}		
		if (type == null){
			System.out.println("null type");
		}

		return null;
	}

	@Transactional(readOnly = true)
	public List<T> get(List<Integer> keys) {
		System.out.println("gatherer get keys");
		List<T> results = new ArrayList<T>();
		for (Integer key : keys) {
			results.add(this.get(key));
		}
		return results;
	}

	public void setType(Class<T> type) {
		System.out.println("set type");
		this.type = type;
	}

}
