package mgi.frontend.finder;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class HibernateGatherer<T> implements GenericGatherer<T> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Class<T> type;
	
	public HibernateGatherer(Class<T> type){
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public T get(Integer key) {
		System.out.println("gatherer get key");
		if (sessionFactory != null){
			return (T)sessionFactory.getCurrentSession().get(type, key);
		}
		System.out.println("null sessionFactory");
		return null;
	}

	@Transactional(readOnly = true)
	public List<T> get(List<Integer> keys) {
		System.out.println("gatherer get keys");
		List<T> results = new ArrayList<T>();
		for (Integer key : keys) {
			results.add((T)this.get(key));
		}
		return results;
	}
}
