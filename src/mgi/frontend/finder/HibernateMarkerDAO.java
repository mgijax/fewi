package mgi.frontend.finder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mgi.frontend.datamodel.Marker;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class HibernateMarkerDAO implements MarkerDAO {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly = true)
	public Collection<Marker> getMarkers(Integer limit) throws DataAccessException {
		Collection<Marker> results = new ArrayList<Marker>();
		if (sessionFactory != null){
			Session session = sessionFactory.getCurrentSession();			
			results = session.createQuery("from Marker m " +
					"where m.symbol like '%a%' order by m.symbol").list();	
		}		
		return results;
	}

	@Transactional(readOnly = true)
	public Marker get(int id) throws DataAccessException {
		Marker marker = new Marker();
		if (sessionFactory != null){
			Session session = sessionFactory.getCurrentSession();
			marker = (Marker) session.get(Marker.class, new Integer(id));
		}
		return marker;
	}
	
	@Transactional(readOnly = true)
	public List<Marker> get(List<Integer> ids) throws DataAccessException {
		List<Marker> results = new ArrayList<Marker>();
		for (Integer id : ids) {
			results.add(this.get(id));
		}
		return results;
	}
}
