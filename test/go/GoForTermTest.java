package go;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.AnnotationProperty;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.controller.GOController;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.test.base.GOBaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGOControllerQuery;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.springframework.beans.factory.annotation.Autowired;


public class GoForTermTest extends GOBaseConcordionTest {

	@Autowired
	GOController goController;
	
	@Autowired
	ReferenceFinder referenceFinder;
	
	/*
	 * Helpers
	 */
	
	
	
	public List<Annotation> getAnnotationsByTermId(String termId) throws Exception
	{
		
		MockGOControllerQuery mq = this.getMockQuery().goController(goController);
		mq.setTermId(termId);
		
		SearchResults<Annotation> searchResults = mq.getAnnotations();
		
		return searchResults.getResultObjects();
	}
	
	/*
	 * Return annotation from annotations that matches the markerId, jNumber, evidenceCode, and termId
	 */
	protected Annotation filterAnnotationByMarkerReferenceCodeTerm(List<Annotation> annotations, 
			String markerId, String jNumber, String evidenceCode, String termId) {
		
		for (Annotation annotation : annotations) {
			
			if ( termId.equalsIgnoreCase(annotation.getTermID()) ) {
				
				// Check evidenceCode if supplied
				if ( evidenceCode == null || evidenceCode.equalsIgnoreCase(annotation.getEvidenceCode()) ) {
					
					for(Marker marker : annotation.getMarkers()) {
						if ( markerId.equalsIgnoreCase(marker.getPrimaryID()) ) {
							
							for(Reference reference: annotation.getReferences()) {

								if ( jNumber.equalsIgnoreCase(reference.getJnumID()) ) {
									return annotation;
								}
							}
							
							
						}
					}
				}
			
			}
		}
		
		return null;
		
	}
	
	/*
	 * Return annotations subset from annotations that matches the markerId, jNumber, and termId
	 */
	protected List<Annotation> filterAnnotationByMarkerReferenceTerm(List<Annotation> annotations, 
			String markerId, String jNumber, String termId) {
		
		List<Annotation> annotationSublist = new ArrayList<Annotation>();
		
		for (Annotation annotation : annotations) {
			
			if ( termId.equalsIgnoreCase(annotation.getTermID()) ) {
				
				for(Marker marker : annotation.getMarkers()) {
					if ( markerId.equalsIgnoreCase(marker.getPrimaryID()) ) {
						
						for(Reference reference : annotation.getReferences()) {

							if ( jNumber.equalsIgnoreCase(reference.getJnumID()) ){

								annotationSublist.add(annotation);
							}
						}
						
					}
				}
			
			}
		}
		
		return annotationSublist;
	}
	
	
	/*
	 * Test functions
	 */

	public List<String> getPropertyTypes(String parentTermId, String geneId, 
			String jNumber, String termId) throws Exception
	{

		
		NotesTagConverter ntc = new NotesTagConverter();
		List<String> types = new ArrayList<String>();
		
		List<Annotation> annotations = getAnnotationsByTermId(parentTermId);
		annotations = filterAnnotationByMarkerReferenceTerm(annotations, geneId, jNumber, termId);
		
		for(Annotation annotation : annotations) {

			List<AnnotationProperty> properties = annotation.getAnnotationExtensions();
			for(AnnotationProperty property : properties) {
				
				types.add(property.getProperty());
			}
		}
		return types;
	}
	
	
	public List<String> getPropertyValues(String parentTermId, String geneId,
			String jNumber, String evidenceCode, String termId, String propType) throws Exception
	{

		
		NotesTagConverter ntc = new NotesTagConverter();
		List<String> values = new ArrayList<String>();
		
		List<Annotation> annotations = getAnnotationsByTermId(parentTermId);
		Annotation annotation = filterAnnotationByMarkerReferenceCodeTerm(annotations, geneId, jNumber, evidenceCode, termId);
		
		if (annotation != null) {

			List<AnnotationProperty> properties = annotation.getAnnotationExtensions();
			for(AnnotationProperty property : properties) {
				
				if ( propType.equalsIgnoreCase(property.getProperty()) ) {
					values.add(ntc.convertNotes(property.getValue(),'|',true));
				}
			}
		}
		
		return values;
	}
	
	
	public List<String> getIsoforms(String parentTermId, String geneId,
			String jNumber, String evidenceCode, String termId) throws Exception
	{

		
		NotesTagConverter ntc = new NotesTagConverter();
		List<String> values = new ArrayList<String>();
		
		List<Annotation> annotations = getAnnotationsByTermId(parentTermId);
		Annotation annotation = filterAnnotationByMarkerReferenceCodeTerm(annotations, geneId, jNumber, evidenceCode, termId);
		
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
	
