package mgi.frontend.finder;

import java.util.List;

import mgi.frontend.controller.MarkerQuery;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.results.MarkerResults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ReferenceFinder {
	
	private Logger logger = LoggerFactory.getLogger(ReferenceFinder.class);
	
	@Autowired
	private SolrReferenceHunter referenceHunter;
	
	private GenericGatherer<Reference> referenceGatherer = 
		new HibernateGatherer<Reference>(Reference.class);

	public List<Reference> getReferencesByID(List<Integer> referenceIDs) {
		return referenceGatherer.get(referenceIDs);
	}

	public Reference getReferenceByID(int id) {
		return referenceGatherer.get(id);
	}

	public MarkerResults searchMarkers(MarkerQuery query) {
		//return referenceHunter.searchReferences(query);
		return null;
	}


}
