package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrAuthorsACHunter;
import org.jax.mgi.fewi.hunter.SolrEmapaACHunter;
import org.jax.mgi.fewi.hunter.SolrGxdEmapaACHunter;
import org.jax.mgi.fewi.hunter.SolrJournalsACHunter;
import org.jax.mgi.fewi.hunter.SolrVocabACHunter;
import org.jax.mgi.fewi.hunter.SolrDriverACHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDriverACResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AutocompleteFinder
{

	@Autowired
	private SolrAuthorsACHunter authorACHunter;

	@Autowired
	private SolrJournalsACHunter journalACHunter;

	@Autowired
	private SolrEmapaACHunter emapaACHunter;

	@Autowired
	private SolrGxdEmapaACHunter gxdEmapaACHunter;

	@Autowired
	private SolrVocabACHunter vocabACHunter;

	@Autowired
	private SolrDriverACHunter driverACHunter;

	// author
	public SearchResults<String> getAuthorAutoComplete(SearchParams params)
	{
		SearchResults<String> results = new SearchResults<String>();
		authorACHunter.hunt(params, results);
		return results;
	}

	// journal
	public SearchResults<String> getJournalAutoComplete(SearchParams params)
	{
		SearchResults<String> results = new SearchResults<String>();
		journalACHunter.hunt(params, results);
		return results;
	}

	// driver
	public SearchResults<SolrDriverACResult> getDriverAutoComplete(SearchParams params)
	{
		SearchResults<SolrDriverACResult> results = new SearchResults<SolrDriverACResult>();
		driverACHunter.hunt(params, results);
		return results;
	}

	/* retrieves autocomplete list for EMAPA terms in the CRE query form
	 */
	public SearchResults<EmapaACResult> getEmapaAutoComplete(
	    SearchParams params) {

	    SearchResults<EmapaACResult> results =
		new SearchResults<EmapaACResult>();

	    emapaACHunter.hunt(params, results);
	    return results;
	}

	/* retrieves autocomplete list for EMAPA terms in the GXD query form
	 */
	public SearchResults<EmapaACResult> getGxdEmapaAutoComplete(
	    SearchParams params) {

	    SearchResults<EmapaACResult> results =
		new SearchResults<EmapaACResult>();

	    gxdEmapaACHunter.hunt(params, results);
	    return results;
	}

	/* retrieves the autocomplete list for vocab terms */
	public SearchResults<VocabACResult> getVocabAutoComplete(SearchParams params)
	{
		SearchResults<VocabACResult> results = new SearchResults<VocabACResult>();
		vocabACHunter.hunt(params, results);
		return results;
	}
}
