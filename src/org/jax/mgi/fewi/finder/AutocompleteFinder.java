package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrAuthorsACHunter;
import org.jax.mgi.fewi.hunter.SolrJournalsACHunter;
import org.jax.mgi.fewi.hunter.SolrStructureACHunter;
import org.jax.mgi.fewi.hunter.SolrEmapaACHunter;
import org.jax.mgi.fewi.hunter.SolrVocabACHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
import org.jax.mgi.fewi.searchUtil.entities.EmapaACResult;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AutocompleteFinder 
{
	private Logger logger = LoggerFactory.getLogger(AutocompleteFinder.class);

	@Autowired
	private SolrAuthorsACHunter authorACHunter;

	@Autowired
	private SolrJournalsACHunter journalACHunter;
	
	@Autowired
	private SolrStructureACHunter structureACHunter;
	
	@Autowired
	private SolrEmapaACHunter emapaACHunter;
	
	@Autowired
	private SolrVocabACHunter vocabACHunter;

	public SearchResults<String> getAuthorAutoComplete(SearchParams params) 
	{
		SearchResults<String> results = new SearchResults<String>();
		authorACHunter.hunt(params, results);
		return results;
	}

	public SearchResults<String> getJournalAutoComplete(SearchParams params) 
	{
		SearchResults<String> results = new SearchResults<String>();
		journalACHunter.hunt(params, results);
		return results;
	}
	
	/* retrieves the autocomplete list for structures in the Anatomical Dictionary */
	public SearchResults<StructureACResult> getStructureAutoComplete(SearchParams params) 
	{
		SearchResults<StructureACResult> results = new SearchResults<StructureACResult>();
		structureACHunter.hunt(params, results);
		return results;
	}

	/* retrieves autocomplete list for EMAPA terms in the GXD query form
	 */
	public SearchResults<EmapaACResult> getEmapaAutoComplete(
	    SearchParams params) {

	    SearchResults<EmapaACResult> results =
		new SearchResults<EmapaACResult>();

	    emapaACHunter.hunt(params, results);
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
