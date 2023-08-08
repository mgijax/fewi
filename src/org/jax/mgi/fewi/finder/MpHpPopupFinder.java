package org.jax.mgi.fewi.finder;

import org.jax.mgi.fewi.hunter.SolrMpHpPopupHunter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrMpHpPopupResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MpHpPopupFinder
{

	@Autowired
	private SolrMpHpPopupHunter mpHpPopupHunter;

	/* retrieves the MP-HP relationships for HMDC Popup */
	public SearchResults<SolrMpHpPopupResult> getMpHpPopupResult(SearchParams params)
	{
		SearchResults<SolrMpHpPopupResult> results = new SearchResults<SolrMpHpPopupResult>();
		mpHpPopupHunter.hunt(params, results);
		return results;
	}
}
