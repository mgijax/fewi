package go;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.Marker;

import org.jax.mgi.fewi.controller.GOController;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.test.base.GOBaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGOControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;


public class GoFromSlimGridTest extends GOBaseConcordionTest {
	

	
	@Autowired
	GOController goController;
	
	@Autowired
	MarkerFinder markerFinder;
	
	/*
	 * Helpers
	 */
	
	/*
	 * Resolves a marker ID to marker_key
	 */
	protected String getMarkerKeyForId(String markerId) throws Exception
	{
		SearchResults<Marker> results = markerFinder.getMarkerByID(markerId);
		
		Integer markerKey = 0;
		if (results.getTotalCount() > 0) {
			markerKey = results.getResultObjects().get(0).getMarkerKey();
		}
		return markerKey.toString();
	}
	
	public List<Annotation> getAnnotationsByMarkerIdAndHeader(String markerId, String slimHeaderTerm) throws Exception
	{
		String mrkKey = getMarkerKeyForId(markerId);
		
		MockGOControllerQuery mq = this.getMockQuery().goController(goController);
		mq.setMrkKey(mrkKey);
		mq.setSlimHeaderTerm(slimHeaderTerm);
		
		SearchResults<Annotation> searchResults = mq.getAnnotations();
		
		return searchResults.getResultObjects();
	}
	
	
	public List<String> getAllAnnotatedTermIds(String markerId, String slimHeaderTerm) throws Exception
	{	
		List<String> terms = new ArrayList<String>();
		List<Annotation> annotations = getAnnotationsByMarkerIdAndHeader(markerId, slimHeaderTerm);
		
		for (Annotation annotation : annotations) {
			System.out.println(annotation.getTerm());
			System.out.println(annotation.getTermID());
			terms.add(annotation.getTermID());
		}
		
		return terms;
	}
}
	
