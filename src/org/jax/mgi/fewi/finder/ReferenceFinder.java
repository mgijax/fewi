package org.jax.mgi.fewi.finder;

import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.hunter.SolrAuthorsACHunter;
import org.jax.mgi.fewi.hunter.SolrJournalsACHunter;
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
	private SolrAuthorsACHunter authorACHunter;
	
	@Autowired
	private SolrJournalsACHunter journalACHunter;

	@Autowired
	private HibernateObjectGatherer<Reference> referenceGatherer;

	public SearchResults<Reference> searchReferences(SearchParams params) {
		logger.debug("searchReferences");
		SearchResults<Reference> results = new SearchResults<Reference>();
		logger.debug("hunt");
		referenceHunter.hunt(params, results);
		logger.debug("gather");
		referenceGatherer.setType(Reference.class);
		results.setResultObjects(referenceGatherer.get(results.getResultKeys()));
		return results;
	}
	
	public SearchResults<String> getAuthorAutoComplete(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		authorACHunter.hunt(params, results);
		return results;
	}
	
	public SearchResults<String> getJournalAutoComplete(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		journalACHunter.hunt(params, results);
		return results;
	}

}
