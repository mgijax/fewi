package org.jax.mgi.fewi.hunter;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrMpHpPopupResult;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the MpHpPopup, off the HMDC Query Form
 * 
 */

@Repository
public class SolrMpHpPopupHunter extends SolrHunter<SolrMpHpPopupResult> {

	/***
	 * The constructor sets up this hunter so that it is specific to sequence
	 * summary pages.  Each item in the constructor sets a value that it has 
	 * inherited from its superclass, and then relies on the superclass to 
	 * perform all of the needed work via the hunt() method.
	 */
	public SolrMpHpPopupHunter() {        

		propertyMap.put("searchTermID", new SolrPropertyMapper("searchTermID"));
//		propertyMap.put(SearchConstants.VOC_TERM_ID, new SolrPropertyMapper(IndexConstants.VOCABAC_TERM_ID));
//		keyString = IndexConstants.VOCABAC_KEY;
	}

	/**
	 * packInformation
	 * @param sdl
	 * @return List of keys
	 * This overrides the typical behavior of packInformation method.
	 * For this hunter, we need a list of SolrMpHpPopupResult objects.
	 */
	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<SolrMpHpPopupResult> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		for (SolrDocument doc : sdl) {
			
			SolrMpHpPopupResult resultObject = new SolrMpHpPopupResult();
			resultObject.setSearchTermID((String) doc.getFieldValue("searchTermID"));
			resultObject.setSearchTerm((String) doc.getFieldValue("searchTerm"));
			resultObject.setSearchTermDefinition((String) doc.getFieldValue("searchTermDefinition"));
			resultObject.setMatchTermID((String) doc.getFieldValue("matchTermID"));
			resultObject.setMatchTerm((String) doc.getFieldValue("matchTerm"));
			resultObject.setMatchMethod((String) doc.getFieldValue("matchMethod"));
			resultObject.setMatchType((String) doc.getFieldValue("matchType"));
			resultObject.setMatchTermDefinition((String) doc.getFieldValue("matchTermDefinition"));
			resultObject.setMatchTermSynonym((String) doc.getFieldValue("matchTermSynonym"));
			sr.addResultObjects(resultObject);
		}
	}

	@Value("${solr.mphp_popup.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}