package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mgi.frontend.datamodel.Allele;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.CreFields;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrCreSummaryHunter extends SolrHunter<Allele> {
    
    /***
     * The constructor sets up this hunter so that it is specific to cre
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrCreSummaryHunter() {        
        
        /*
         * Setup the sort mapping.
         */
        sortMap.put(SortConstants.CRE_DRIVER, new SolrSortMapper(IndexConstants.ALL_DRIVER_SORT));
        sortMap.put(SortConstants.CRE_SYMBOL, new SolrSortMapper(IndexConstants.ALL_SYMBOL_SORT));
        sortMap.put(SortConstants.CRE_TYPE, new SolrSortMapper(IndexConstants.ALL_TYPE_SORT));
        sortMap.put(SortConstants.CRE_INDUCIBLE, new SolrSortMapper(IndexConstants.ALL_INDUCIBLE));
        sortMap.put(SortConstants.CRE_REF_COUNT, new SolrSortMapper(IndexConstants.ALL_REFERENCE_COUNT_SORT));
        sortMap.put(SortConstants.CRE_DETECTED_COUNT, new SolrSortMapper(IndexConstants.CRE_DETECTED_COUNT));
        sortMap.put(SortConstants.CRE_NOT_DETECTED_COUNT, new SolrSortMapper(IndexConstants.CRE_NOT_DETECTED_COUNT));

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
        propertyMap.put(SearchConstants.ALL_DRIVER, new SolrPropertyMapper(IndexConstants.ALL_DRIVER));
        propertyMap.put(SearchConstants.ALL_SYSTEM, new SolrPropertyMapper(IndexConstants.CRE_ALL_SYSTEM));
        
        // structure resulated search fields. All copied from gxdResult and behave exactly the same
        // structure
        propertyMap.put(SearchConstants.STRUCTURE,
          		new SolrPropertyMapper(GxdResultFields.STRUCTURE_ANCESTORS));

        // structure key
        propertyMap.put(SearchConstants.STRUCTURE_KEY,
          		new SolrPropertyMapper(GxdResultFields.STRUCTURE_KEY));

        // annotated structure key (does not include children)
        propertyMap.put(GxdResultFields.ANNOTATED_STRUCTURE_KEY,
          		new SolrPropertyMapper(GxdResultFields.ANNOTATED_STRUCTURE_KEY));
        
        // structure ID
        propertyMap.put(SearchConstants.STRUCTURE_ID,
          		new SolrPropertyMapper(GxdResultFields.STRUCTURE_ID));

        
        
        /*
         * Which fields to highlight
         */
        highlightRequireFieldMatch=false; // we want to highlight fields that we are not querying
        highlightFragmentSize=100;
        highlightSnippets = 1;
        
        // marker highlights
        for(String fieldName : CreFields.SYSTEM_FIELDS.values())
        {
        	highlightFields.add(fieldName);
        }
        
        
        this.returnedFields.add(IndexConstants.ALL_KEY);
        
        
        /*
         * field to iterate over and place into the output
         */
        keyString = IndexConstants.ALL_KEY;
        
    }
    

    /**
     * packInformation
     * @param sdl
     * @return List of keys
     * This overrides the typical behavior of packInformation method.
     * For this autocomplete hunter, we don't need highlighting or metadata, but
     * only need a list of Marker objects.
     */
    @Override
    protected void packInformation(QueryResponse rsp, SearchResults<Allele> sr,
            SearchParams sp) {
        
        // A list of all the primary keys in the document
        SolrDocumentList sdl = rsp.getResults();

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */
        
        List<String> resultKeys = new ArrayList<String>();
        for (SolrDocument doc : sdl)
        {
            
            //logger.debug(doc.toString());
            // Set the result object
            String allKey = (String) doc.getFieldValue(IndexConstants.ALL_KEY);
            resultKeys.add(allKey);
            
            // Add result to SearchResults
            //sr.addResultObjects(marker);
           // keyToResultMap.put(allKey,allKey);
        }

        sr.setResultKeys(resultKeys);
        
        // A mapping of field -> set of highlighted words
        // for the result set.
        Map<String, Set<String>> setHighlights = new HashMap<String, Set<String>> ();

        if (sp.includeSetMeta()) {
            sr.setResultSetMeta(new ResultSetMetaData(setHighlights));
        }

        if (sp.includeRowMeta()) {
           // sr.setMetaMapping(metaList);
        }

        if (!this.highlightFields.isEmpty() && sp.includeMetaHighlight() && sp.includeSetMeta())
        {
            // A mapping of documentKey -> Mapping of FieldName -> list of highlighted fields
            Map<String, Map<String, List<String>>> highlights = rsp.getHighlighting();

            for(String allKey : highlights.keySet())
            {
            	// only save the matching field names so we can match them in the results
            	Map<String,List<String>> highlight = highlights.get(allKey);
            	
            	// add highlights to metadata object
            	setHighlights.put(allKey,highlight.keySet());
            }
        }
    }
    
	@Value("${solr.cre.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
   
}
