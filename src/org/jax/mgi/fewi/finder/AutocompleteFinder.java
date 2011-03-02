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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AutocompleteFinder {

	private Logger logger = LoggerFactory.getLogger(AutocompleteFinder.class);

	@Autowired
	private SolrAuthorsACHunter authorACHunter;

	@Autowired
	private SolrJournalsACHunter journalACHunter;

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
