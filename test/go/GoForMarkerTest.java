package go;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationProperty;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.controller.GOController;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.test.base.GOBaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGOControllerQuery;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.springframework.beans.factory.annotation.Autowired;


public class GoForMarkerTest extends GOBaseConcordionTest {
	

	
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
	
	public List<Annotation> getAnnotationsByMarkerId(String markerId) throws Exception
	{
		String mrkKey = getMarkerKeyForId(markerId);
		
		MockGOControllerQuery mq = this.getMockQuery().goController(goController);
		mq.setMrkKey(mrkKey);
		
		SearchResults<Annotation> searchResults = mq.getAnnotations();
		
		return searchResults.getResultObjects();
	}
	
	/*
	 * Return annotation from annotations that matches the jNumber, evidenceCode, and termId
	 */
	protected Annotation filterAnnotationByReferenceCodeTerm(List<Annotation> annotations, 
			String jNumber, String evidenceCode, String termId) {
		
		for (Annotation annotation : annotations) {
			
			if ( termId.equalsIgnoreCase(annotation.getTermID()) ) {
				
				// Check evidenceCode if supplied
				if ( evidenceCode == null || evidenceCode.equalsIgnoreCase(annotation.getEvidenceCode()) ) {
					
					for(Reference reference : annotation.getReferences()) {
						if ( jNumber.equalsIgnoreCase(reference.getJnumID()) ) {
							
							return annotation;
							
						}
					}
				}
			
			}
		}
		
		return null;
		
	}
	
	/*
	 * Return annotations subset from annotations that matches the jNumber, and termId
	 */
	protected List<Annotation> filterAnnotationByReferenceTerm(List<Annotation> annotations, 
			String jNumber, String termId) {
		
		List<Annotation> annotationSublist = new ArrayList<Annotation>();
		
		for (Annotation annotation : annotations) {
			
			if ( termId.equalsIgnoreCase(annotation.getTermID()) ) {
				
				for(Reference reference : annotation.getReferences()) {
					if ( jNumber.equalsIgnoreCase(reference.getJnumID()) ) {
						
						annotationSublist.add(annotation);
						
					}
				}
			
			}
		}
		
		return annotationSublist;
	}
	
	
	/*
	 * Test functions
	 */
	
	public List<String> getPropertyTypes(String geneId, 
			String jNumber, String termId) throws Exception
	{

		
		NotesTagConverter ntc = new NotesTagConverter();
		List<String> types = new ArrayList<String>();
		
		List<Annotation> annotations = getAnnotationsByMarkerId(geneId);
		annotations = filterAnnotationByReferenceTerm(annotations, jNumber, termId);
		
		for(Annotation annotation : annotations) {

			List<AnnotationProperty> properties = annotation.getAnnotationExtensions();
			for(AnnotationProperty property : properties) {
				
				types.add(property.getProperty());
			}
		}
		return types;
	}
	
	
	public List<String> getPropertyValues(String geneId,
			String jNumber, String evidenceCode, String termId, String propType) throws Exception
	{

		NotesTagConverter ntc = new NotesTagConverter();
		List<String> values = new ArrayList<String>();
		
		List<Annotation> annotations = getAnnotationsByMarkerId(geneId);
		Annotation annotation = filterAnnotationByReferenceCodeTerm(annotations, jNumber, evidenceCode, termId);
		
		if (annotation != null) {

			List<AnnotationProperty> properties = annotation.getAnnotationExtensions();
			for(AnnotationProperty property : properties) {
				
				if ( propType.equalsIgnoreCase(property.getProperty()) ) {
					values.add(ntc.convertNotes(property.getValue(),'|', true));
				}
			}
		}
		
		return values;
	}
	
	
	public List<String> getIsoforms(String geneId,
			String jNumber, String evidenceCode, String termId) throws Exception
	{

		
		NotesTagConverter ntc = new NotesTagConverter();
		List<String> values = new ArrayList<String>();
		
		List<Annotation> annotations = getAnnotationsByMarkerId(geneId);
		Annotation annotation = filterAnnotationByReferenceCodeTerm(annotations, jNumber, evidenceCode, termId);
		
		if (annotation != null) {

			List<AnnotationProperty> properties = annotation.getIsoforms();
			for(AnnotationProperty property : properties) {
				
				String value = property.getValue();
				// convert note tags without links for easy testing
				value = ntc.convertNotes(value, '|', true);
				values.add(value);
			}
		}
		
		return values;
	}
}
	
