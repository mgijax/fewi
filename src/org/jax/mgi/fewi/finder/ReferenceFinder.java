package org.jax.mgi.fewi.finder;

import java.util.List;

import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.hunter.SolrAuthorsACHunter;
import org.jax.mgi.fewi.hunter.SolrJournalsACHunter;
import org.jax.mgi.fewi.hunter.SolrReferenceAuthorFacetHunter;
import org.jax.mgi.fewi.hunter.SolrReferenceHasDataFacetHunter;
import org.jax.mgi.fewi.hunter.SolrReferenceJournalFacetHunter;
import org.jax.mgi.fewi.hunter.SolrReferenceSummaryHunter;
import org.jax.mgi.fewi.hunter.SolrReferenceYearFacetHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.ReferenceSummary;
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
	private SolrReferenceAuthorFacetHunter authorFacetHunter;

	@Autowired
	private SolrReferenceJournalFacetHunter journalFacetHunter;

	@Autowired
	private SolrReferenceYearFacetHunter yearFacetHunter;

	@Autowired
	private SolrReferenceHasDataFacetHunter dataFacetHunter;

	@Autowired
	private HibernateObjectGatherer<Reference> referenceGatherer;


	public SearchResults<ReferenceSummary> searchSummaryReferences(SearchParams params) {
		logger.debug("searchReferences");
		SearchResults<ReferenceSummary> results = new SearchResults<ReferenceSummary>();
		logger.debug("hunt");
		referenceHunter.hunt(params, results);
		logger.debug("gather");
		referenceGatherer.setType(Reference.class);
		List<ReferenceSummary> summaryList = results.getResultObjects();
		ReferenceSummary refSumm;
		for(Reference ref: referenceGatherer.get(results.getResultKeys())){
			refSumm = new ReferenceSummary();
			summaryList.add(refSumm);
			refSumm.setReference(ref);
			refSumm.setScore(results.getResultScores().get(summaryList.indexOf(refSumm)));
		}
		return results;
	}


	public SearchResults<Reference> searchReferences(SearchParams params) {
			logger.debug("-->searchReferences");
			SearchResults<Reference> results = new SearchResults<Reference>();
			referenceHunter.hunt(params, results);
			logger.debug("-->searchReferences results.getResultKeys()" + results.getResultKeys());
			referenceGatherer.setType(Reference.class);

            List<Reference> refList = referenceGatherer.get( results.getResultKeys() );
            results.setResultObjects(refList);

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

	public SearchResults<String> getAuthorFacet(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		authorFacetHunter.hunt(params, results);
		return results;
	}

	public SearchResults<String> getJournalFacet(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		journalFacetHunter.hunt(params, results);
		return results;
	}

	public SearchResults<String> getYearFacet(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		yearFacetHunter.hunt(params, results);
		return results;
	}

	public SearchResults<String> getDataFacet(SearchParams params) {
		SearchResults<String> results = new SearchResults<String>();
		dataFacetHunter.hunt(params, results);
		return results;
	}

}
