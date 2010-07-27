package mgi.frontend.finder;

import java.util.Collection;

import mgi.frontend.datamodel.Marker;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MarkerDAO {
	@Transactional(readOnly = true)
	Collection<Marker> getMarkers(Integer limit) throws DataAccessException;

}
