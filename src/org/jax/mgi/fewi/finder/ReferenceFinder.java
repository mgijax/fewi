package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.List;

import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.hunter.SolrReferenceSummaryHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReferenceFinder {
	
	private Logger logger = LoggerFactory.getLogger(ReferenceFinder.class);

	@Autowired
	private SolrReferenceSummaryHunter referenceHunter;

	@Autowired
	private HibernateObjectGatherer<Reference> referenceGatherer;

	public List<Reference> getReferencesByID(List<Integer> markerIDs) {
		logger.info("get referenceIDs");
		referenceGatherer.setType(Reference.class);
		return referenceGatherer.get(markerIDs);
	}

	public Reference getReferenceByID(int id) {
		logger.info("get id");
		referenceGatherer.setType(Reference.class);
		return referenceGatherer.get(id);
	}

	public SearchResults<Reference> searchReferences(SearchParams params) {
		logger.info("searchReferences");
		SearchResults<Reference> results = new SearchResults<Reference>();
		logger.info("hunt");
		referenceHunter.hunt(params, results);
		logger.info("solr done");
		List<String> keys = results.getResultKeys();
		List<Integer> iKeys = new ArrayList<Integer>();
		logger.info("convert keys");
		for (String k : keys) {
			System.out.println("convert: " + k);
			iKeys.add(new Integer(k));
		}
		logger.info("gather");
		referenceGatherer.setType(Reference.class);
		results.setResultObjects(referenceGatherer.get(iKeys));
		return results;
	}

}
