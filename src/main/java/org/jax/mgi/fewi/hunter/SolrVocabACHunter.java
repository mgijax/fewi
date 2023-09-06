package org.jax.mgi.fewi.hunter;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.shr.fe.IndexConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the structure autocomplete.
 * These are structures in the GXD Anatomical Dictionary.
 * It queries all structures and their synonyms, and returns 
 * the matching field, plus the base structure name that it maps to.
 * 
 * @author kstone
 *
 */

@Repository
public class SolrVocabACHunter extends SolrHunter<VocabACResult> {
	/***
	 * The constructor sets up this hunter so that it is specific to sequence
	 * summary pages.  Each item in the constructor sets a value that it has 
	 * inherited from its superclass, and then relies on the superclass to 
	 * perform all of the needed work via the hunt() method.
	 */
	public SolrVocabACHunter() {        

		propertyMap.put(SearchConstants.VOC_TERM, new SolrPropertyMapper(IndexConstants.VOCABAC_TERM));
		propertyMap.put(SearchConstants.VOC_TERM_ID, new SolrPropertyMapper(IndexConstants.VOCABAC_TERM_ID));
		propertyMap.put(SearchConstants.VOC_VOCAB, new SolrPropertyMapper(IndexConstants.VOCABAC_VOCAB));
		propertyMap.put(SearchConstants.VOC_DERIVED_TERMS, new SolrPropertyMapper(IndexConstants.VOCABAC_DERIVED_TERMS));

		highlightPre = "";
		highlightPost = "";
		highlightSnippets = 1;
		highlightFields.add(SearchConstants.VOC_DERIVED_TERMS);

		keyString = IndexConstants.VOCABAC_KEY;
	}

	/**
	 * packInformation
	 * @param sdl
	 * @return List of keys
	 * This overrides the typical behavior of packInformation method.
	 * For this autocomplete hunter, we don't need highlighting or metadata, but
	 * only need a list of VocabACResult objects.
	 */
	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<VocabACResult> sr, SearchParams sp) {

		SolrDocumentList sdl = rsp.getResults();

		for (SolrDocument doc : sdl) {
			
			VocabACResult resultObject = new VocabACResult();
			
			resultObject.setTermId((String) doc.getFieldValue(IndexConstants.VOCABAC_TERM_ID));
			resultObject.setTerm((String) doc.getFieldValue(IndexConstants.VOCABAC_TERM));
			
			// Highlighting is used on the HMDC for autocomplete.
			if(rsp.getHighlighting() != null) {
				resultObject.setDerivedTerms((List<String>)rsp.getHighlighting().get(doc.getFieldValue(IndexConstants.UNIQUE_KEY)).get(IndexConstants.VOCABAC_DERIVED_TERMS));
				resultObject.getDerivedTerms().add((String)doc.getFieldValue(IndexConstants.VOCABAC_TERM));
			} else {
				List<String> list = (List<String>)doc.getFieldValue(IndexConstants.VOCABAC_DERIVED_TERMS);
				resultObject.setDerivedTerms(list);
			}
			
			// Either an entry in the GXD Lit Index or in full-coded classical data or in RNA-Seq data is good enough
			// to flag this term for having expression data.
			boolean hasExpressionResults = (Integer) doc.getFieldValue(IndexConstants.VOCABAC_GXDLIT_MARKER_COUNT) > 0;
			if (!hasExpressionResults) {
				hasExpressionResults = (Integer) doc.getFieldValue(IndexConstants.VOCABAC_EXPRESSION_MARKER_COUNT) > 0;
			}
			
			resultObject.setIsSynonym((Boolean) doc.getFieldValue(IndexConstants.VOCABAC_IS_SYNONYM));
			resultObject.setOriginalTerm((String) doc.getFieldValue(IndexConstants.VOCABAC_ORIGINAL_TERM));
			resultObject.setRootVocab((String) doc.getFieldValue(IndexConstants.VOCABAC_ROOT_VOCAB));
			resultObject.setMarkerCount((Integer) doc.getFieldValue(IndexConstants.VOCABAC_MARKER_COUNT));
			resultObject.setHasExpressionResults(hasExpressionResults);
			sr.addResultObjects(resultObject);
		}
	}

	@Value("${solr.vocab_term_ac.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}