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
public class SolrVocabACHunter extends SolrHunter<VocabACResult>
{
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrVocabACHunter() 
    {        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
    	
         /* 
          * synonym: the search field that will appear in autocomplete pick list 
          * we search both synonyms and terms
          * */  
         propertyMap.put(SearchConstants.VOC_TERM, new SolrPropertyMapper(IndexConstants.VOCABAC_TERM));
         
         propertyMap.put(SearchConstants.VOC_TERM_ID, new SolrPropertyMapper(IndexConstants.VOCABAC_TERM_ID));
   
         propertyMap.put(SearchConstants.VOC_VOCAB, new SolrPropertyMapper(IndexConstants.VOCABAC_VOCAB));
         
         propertyMap.put(SearchConstants.VOC_DERIVED_TERMS, new SolrPropertyMapper(IndexConstants.VOCABAC_DERIVED_TERMS));
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a 
         * specific field, and return it rather than a list of keys.
         */  
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
    protected void packInformation(QueryResponse rsp, SearchResults<VocabACResult> sr,
            SearchParams sp) {
        
        // A list of all the primary keys in the document

        SolrDocumentList sdl = rsp.getResults();

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */

        for (SolrDocument doc : sdl)
        {
            
            //logger.debug(doc.toString());
            // Set the result object
            String termId = (String) doc.getFieldValue(IndexConstants.VOCABAC_TERM_ID);
            String term = (String) doc.getFieldValue(IndexConstants.VOCABAC_TERM);
            boolean isSynonym = (Boolean) doc.getFieldValue(IndexConstants.VOCABAC_IS_SYNONYM);
            String originalTerm = (String) doc.getFieldValue(IndexConstants.VOCABAC_ORIGINAL_TERM);
            String rootVocab = (String) doc.getFieldValue(IndexConstants.VOCABAC_ROOT_VOCAB);
            // NOTE: not currently using the display vocab
            //String displayVocab = (String) doc.getFieldValue(IndexConstants.VOCABAC_VOCAB);
            List<String> derivedTerms = (List<String>)doc.getFieldValue(IndexConstants.VOCABAC_DERIVED_TERMS);
            int markerCount = (Integer) doc.getFieldValue(IndexConstants.VOCABAC_MARKER_COUNT);
            int gxdlitMarkerCount = (Integer) doc.getFieldValue(IndexConstants.VOCABAC_GXDLIT_MARKER_COUNT);
            VocabACResult resultObject = new VocabACResult();
            resultObject.setTermId(termId);
            resultObject.setTerm(term);
            resultObject.setDerivedTerms(derivedTerms);
            resultObject.setIsSynonym(isSynonym);
            resultObject.setOriginalTerm(originalTerm);
            resultObject.setRootVocab(rootVocab);
            //resultObject.setDisplayVocab(displayVocab);
            resultObject.setMarkerCount(markerCount);
            // check if there are any markers with gxd records
            resultObject.setHasExpressionResults(gxdlitMarkerCount>0);
            
            // Add result to SearchResults
            sr.addResultObjects(resultObject);
        }
    }
    
	@Value("${solr.vocab_term_ac.url}")
	public void setSolrUrl(String solrUrl) 
	{ super.solrUrl = solrUrl; }
}