package org.jax.mgi.fewi.entities.hmdc;

import java.io.Serializable;

import org.jax.mgi.fewi.entities.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.searchUtil.SearchResults;

public class JsonGridPopupMapper implements Serializable {

	private SearchResults<SolrHdpEntityInterface> results;
	private SearchResults<SolrHdpEntityInterface> annotationResults;
	
	public JsonGridPopupMapper(SearchResults<SolrHdpEntityInterface> results, SearchResults<SolrHdpEntityInterface> annotationResults) {
		this.results = results;
		this.annotationResults = annotationResults;
	}


	


}
