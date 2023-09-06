package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.MarkerSummaryFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerSummaryHunter extends SolrHunter<SolrSummaryMarker> {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * marker summary information given any possible sequence id.
     */
    public SolrMarkerSummaryHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.MRK_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_SYMBOL));
        propertyMap.put(SearchConstants.MRK_KEY,new SolrPropertyMapper(IndexConstants.MRK_KEY));
        propertyMap.put(SearchConstants.MRK_ID, new SolrPropertyMapper(IndexConstants.MRK_ID));
        propertyMap.put(SearchConstants.MRK_TERM_ID,new SolrPropertyMapper(IndexConstants.MRK_TERM_ID));
        propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
        propertyMap.put(SearchConstants.FEATURE_TYPE_KEY,new SolrPropertyMapper(MarkerSummaryFields.FEATURE_TYPE_KEY));
        propertyMap.put(SearchConstants.FEATURE_TYPE,new SolrPropertyMapper(MarkerSummaryFields.FEATURE_TYPE));
        propertyMap.put(SearchConstants.START_COORD, new SolrPropertyMapper(IndexConstants.START_COORD));
        propertyMap.put(SearchConstants.END_COORD, new SolrPropertyMapper(IndexConstants.END_COORD));
        propertyMap.put(SearchConstants.CM_OFFSET, new SolrPropertyMapper(IndexConstants.CM_OFFSET));
//        propertyMap.put(SearchConstants.STRAND, new SolrPropertyMapper(IndexConstants.CHROMOSOME));

        propertyMap.put(SearchConstants.MRK_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_SYMBOL));
        propertyMap.put(SearchConstants.MRK_CURRENT_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_CURRENT_SYMBOL));
        propertyMap.put(SearchConstants.MRK_HUMAN_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_HUMAN_SYMBOL));
        propertyMap.put(SearchConstants.MRK_RAT_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_RAT_SYMBOL));
        propertyMap.put(SearchConstants.MRK_RHESUS_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_RHESUS_SYMBOL));
        propertyMap.put(SearchConstants.MRK_CATTLE_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_CATTLE_SYMBOL));
        propertyMap.put(SearchConstants.MRK_DOG_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_DOG_SYMBOL));
        propertyMap.put(SearchConstants.MRK_ZFIN_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_ZFIN_SYMBOL));
        propertyMap.put(SearchConstants.MRK_CHICKEN_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_CHICKEN_SYMBOL));
        propertyMap.put(SearchConstants.MRK_CHIMP_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_CHIMP_SYMBOL));
        propertyMap.put(SearchConstants.MRK_FROG_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_FROG_SYMBOL));
        
        /*
         * Nomen search
         */
        propertyMap.put(SearchConstants.MRK_NOMENCLATURE, 
        		new SolrPropertyMapper(new ArrayList<String>(MarkerSummaryFields.NOMEN_FIELDS.values()
        		),"OR"));

        /*
         * InterPro searches
         */
        propertyMap.put(SearchConstants.INTERPRO_TERM,
    		new SolrPropertyMapper(Arrays.asList(
    				IndexConstants.MRK_TERM_ID,
    				MarkerSummaryFields.INTERPRO_TERM
    					),"OR"));
        /*
         * GO searches
         */
        propertyMap.put(SearchConstants.GO_TERM,
    		new SolrPropertyMapper(Arrays.asList(
    				IndexConstants.MRK_TERM_ID,
    				MarkerSummaryFields.GO_TERM
    					),"OR"));
        
        propertyMap.put(SearchConstants.GO_FUNCTION_TERM,new SolrPropertyMapper(MarkerSummaryFields.GO_FUNCTION_TERM));
        propertyMap.put(SearchConstants.GO_PROCESS_TERM,new SolrPropertyMapper(MarkerSummaryFields.GO_PROCESS_TERM));
        propertyMap.put(SearchConstants.GO_COMPONENT_TERM,new SolrPropertyMapper(MarkerSummaryFields.GO_COMPONENT_TERM));

        
        /*
         * Phenotype searches
         */
        propertyMap.put(SearchConstants.PHENOTYPE,
    		new SolrPropertyMapper(Arrays.asList(
    				MarkerSummaryFields.PHENO_ID,
    				MarkerSummaryFields.PHENO_TEXT
    					),"OR"));
        
        
        /*
         * sortable fields
         */
        this.sortMap.put(SortConstants.MRK_BY_SYMBOL,new SolrSortMapper(MarkerSummaryFields.BY_SYMBOL));
        this.sortMap.put(SortConstants.MRK_BY_LOCATION,new SolrSortMapper(MarkerSummaryFields.BY_LOCATION));
        this.sortMap.put(SortConstants.MRK_BY_TYPE,new SolrSortMapper(MarkerSummaryFields.FEATURE_TYPE));
        
        
        /*
         * Which fields to highlight
         */
        highlightRequireFieldMatch=true; // we want to highlight fields that we are not querying
        highlightFragmentSize=100;
        highlightSnippets = 1;
        highlightPre = "$$"; // to be replaced later, because we need to superscript the data also
        highlightPost = "/$$";
        

//        highlightFields.add(MarkerSummaryFields.GO_PROCESS_TERM);
//        highlightFields.add(MarkerSummaryFields.GO_FUNCTION_TERM);
//        highlightFields.add(MarkerSummaryFields.GO_COMPONENT_TERM);
//        highlightFields.add(MarkerSummaryFields.INTERPRO_TERM);

        // marker highlights
        for(String fieldName : MarkerSummaryFields.NOMEN_FIELDS.values())
        {
        	highlightFields.add(fieldName);
        }
        
        /*
         * Which fields to return
         */
        this.returnedFields.add(IndexConstants.MRK_SYMBOL);
        this.returnedFields.add(IndexConstants.MRK_NAME);
        this.returnedFields.add(IndexConstants.MRK_PRIMARY_ID);
        this.returnedFields.add(IndexConstants.MRK_KEY);
        this.returnedFields.add(MarkerSummaryFields.FEATURE_TYPE);
        this.returnedFields.add(IndexConstants.CHROMOSOME);
        this.returnedFields.add(IndexConstants.START_COORD);
        this.returnedFields.add(IndexConstants.END_COORD);
        this.returnedFields.add(IndexConstants.CM_OFFSET);
        this.returnedFields.add(IndexConstants.STRAND);
        this.returnedFields.add(MarkerSummaryFields.LOCATION_DISPLAY);
        this.returnedFields.add(MarkerSummaryFields.COORDINATE_DISPLAY);


        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the standard list of
         * object keys returned.
         */
        keyString = IndexConstants.MRK_KEY;

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
    protected void packInformation(QueryResponse rsp, SearchResults<SolrSummaryMarker> sr,
            SearchParams sp) {
        
        // A list of all the primary keys in the document
        SolrDocumentList sdl = rsp.getResults();

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */
        Map<String,SolrSummaryMarker> keyToResultMap = new HashMap<String,SolrSummaryMarker>();
        
        List<String> resultKeys = new ArrayList<String>();
        for (SolrDocument doc : sdl)
        {
            
            //logger.debug(doc.toString());
            // Set the result object
            String markerKey = (String) doc.getFieldValue(IndexConstants.MRK_KEY);
            resultKeys.add(markerKey);
            
            SolrSummaryMarker marker = new SolrSummaryMarker();

            marker.setSymbol((String) doc.getFieldValue(IndexConstants.MRK_SYMBOL));
            marker.setName((String) doc.getFieldValue(IndexConstants.MRK_NAME));
            marker.setMarkerKey(markerKey);
            marker.setMgiId((String) doc.getFieldValue(IndexConstants.MRK_PRIMARY_ID));
            marker.setFeatureType((String) doc.getFieldValue(MarkerSummaryFields.FEATURE_TYPE));
            marker.setChromosome((String) doc.getFieldValue(IndexConstants.CHROMOSOME));
            marker.setCoordStart((Integer) doc.getFieldValue(IndexConstants.START_COORD));
            marker.setCoordEnd((Integer) doc.getFieldValue(IndexConstants.END_COORD));
            marker.setCm((Double) doc.getFieldValue(IndexConstants.CM_OFFSET));
            marker.setStrand((String) doc.getFieldValue(IndexConstants.STRAND));
            marker.setCoordinateDisplay((String) doc.getFieldValue(MarkerSummaryFields.COORDINATE_DISPLAY));
            marker.setLocationDisplay((String) doc.getFieldValue(MarkerSummaryFields.LOCATION_DISPLAY));
            
            // Add result to SearchResults
            sr.addResultObjects(marker);
            keyToResultMap.put(markerKey,marker);
        }

        sr.setResultKeys(resultKeys);
        
        // A mapping of field -> set of highlighted words
        // for the result set.
        Map<String, List<String>> setHighlights = new HashMap<String, List<String>> ();

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

            for(String uniqueKey : highlights.keySet())
            {
            	Map<String,List<String>> highlight = highlights.get(uniqueKey);

            	if(!keyToResultMap.containsKey(uniqueKey)) continue;
            	SolrSummaryMarker marker = keyToResultMap.get(uniqueKey);

            	ArrayList<String> highlightList = new ArrayList<String>();
            	
            	// just add the field names that matched for now
//            	for(String field : highlight.keySet())
//            	{
//            		// just add the first one for each field
//            		String highlightMatch = "<b>"+field+"</b>:\""+highlight.get(field).get(0)+"\"";
//            		highlightList.add(highlightMatch);
//            	}
            	// only add the first matching highlight
            	Set<String> goHighlightList = new LinkedHashSet<String>();
            	outer:
            	for(String hf : this.highlightFields)
            	{
            		for(String field : highlight.keySet())
            		{
            			if(hf.equals(field))
            			{
            				String hl = highlight.get(field).get(0);
            				hl = FormatHelper.superscript(hl);
            				if (sp.includeHighlightMarkup()) {
                				hl = hl.replaceAll("/\\$\\$","</b>").replaceAll("\\$\\$","<b>");
            				} else {
                				hl = hl.replaceAll("/\\$\\$","").replaceAll("\\$\\$","");
            				}
            				String highlightMatch = field+":\""+hl+"\"";
                    		if(hf.equals(MarkerSummaryFields.GO_COMPONENT_TERM) ||
                    				hf.equals(MarkerSummaryFields.GO_PROCESS_TERM) ||
                    				hf.equals(MarkerSummaryFields.GO_FUNCTION_TERM) ||
                    				hf.equals(MarkerSummaryFields.INTERPRO_TERM))
                    		{
                    			highlightMatch = "<span style=\"background-color:yellow;\">"+highlightMatch+"</span>";
                    			goHighlightList.add(highlightMatch);
                    		}
                    		else
                    		{
                        		// only add the first match in priority order of the orginal list (for marker nomen)
                        		highlightList.add(highlightMatch);
                    			break outer;
                    		}
            			}
            		}
            	}
            	// put go terms last
            	highlightList.addAll(goHighlightList);
            	
            	// add to solr marker object for convenience
            	marker.setHighlights(highlightList);
            	// add highlights to metadata object
            	setHighlights.put(uniqueKey,highlightList);
            }
        }
    }
    
	@Value("${solr.marker.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}
}