package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
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
public class SolrStructureACHunter extends SolrHunter 
{
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has 
     * inherited from its superclass, and then relies on the superclass to 
     * perform all of the needed work via the hunt() method.
     */
    public SolrStructureACHunter() 
    {        
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * 
         */
    	
    	 /* structure: the base structure this document maps to */
         //propertyMap.put(SearchConstants.STRUCTURE, new SolrPropertyMapper(IndexConstants.STRUCTUREAC_STRUCTURE));

         /* 
          * synonym: the search field that will appear in autocomplete pick list 
          * we search both synonyms and base structure names
          * */  
//    	 ArrayList <String> synonymList = new ArrayList <String> ();
//         synonymList.add(IndexConstants.STRUCTUREAC_SYNONYM);
//         synonymList.add(IndexConstants.STRUCTUREAC_STRUCTURE);
//         propertyMap.put(SearchConstants.STRUCTURE, 
//         		new SolrPropertyMapper(synonymList, "OR"));
         propertyMap.put(SearchConstants.STRUCTURE, new SolrPropertyMapper(IndexConstants.STRUCTUREAC_SYNONYM));
   
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a 
         * specific field, and return it rather than a list of keys.
         */  
        keyString = IndexConstants.STRUCTUREAC_STRUCTURE;
    }
    
    /**
     * packInformation
     * @param sdl
     * @return List of keys
     * This overrides the typical behavior of packInformation method.
     * For this autocomplete hunter, we don't need highlighting or metadata, but
     * only need a list of StructureACResult objects.
     */
    @Override
    protected void packInformation(QueryResponse rsp, SearchResults sr,
            SearchParams sp) {

        // A list of all the primary keys in the document

        SolrDocumentList sdl = rsp.getResults();

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */

        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();
            
            // Set the result object
            String structure = (String) doc.getFieldValue(IndexConstants.STRUCTUREAC_STRUCTURE);
            String synonym = (String) doc.getFieldValue(IndexConstants.STRUCTUREAC_SYNONYM);
            boolean isStrictSynonym = (Boolean) doc.getFieldValue(IndexConstants.STRUCTUREAC_IS_STRICT_SYNONYM);
            boolean hasCre = (Boolean) doc.getFieldValue(IndexConstants.STRUCTUREAC_HAS_CRE);
            StructureACResult resultObject = new StructureACResult(structure,synonym,isStrictSynonym,hasCre);
            
            // Add result to SearchResults
            sr.addResultObjects(resultObject);
        }
    }
    
	@Value("${solr.structure_ac.url}")
	public void setSolrUrl(String solrUrl) 
	{ super.solrUrl = solrUrl; }
}