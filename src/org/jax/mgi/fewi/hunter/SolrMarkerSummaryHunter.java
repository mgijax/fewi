package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrMarkerSummaryHunter extends SolrHunter {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * a sequence key given any possible sequence id.
     */
    public SolrMarkerSummaryHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.MRK_SYMBOL, new SolrPropertyMapper(IndexConstants.MRK_SYMBOL));
        propertyMap.put("markerKey",new SolrPropertyMapper("markerKey"));
        propertyMap.put(SearchConstants.MRK_ID, new SolrPropertyMapper(IndexConstants.MRK_ID));
        propertyMap.put(SearchConstants.REF_KEY, new SolrPropertyMapper(IndexConstants.REF_KEY));
        propertyMap.put("featureTypeKey",new SolrPropertyMapper("featureTypeKey"));
        propertyMap.put("featureType",new SolrPropertyMapper("featureType"));
        propertyMap.put("markerNomen", new SolrPropertyMapper("markerNomen"));
        propertyMap.put("startCoord", new SolrPropertyMapper("startCoord"));
        propertyMap.put("endCoord", new SolrPropertyMapper("endCoord"));
        propertyMap.put("cmOffset", new SolrPropertyMapper("cm"));
        propertyMap.put("chromosome", new SolrPropertyMapper("chromosome"));
        

        /*
         * InterPro searches
         */
        propertyMap.put("interpro",
    		new SolrPropertyMapper(Arrays.asList(
    				"markerTermID",
    				"interProTerm"
    					),"OR"));
        /*
         * GO searches
         */
        propertyMap.put("go",
    		new SolrPropertyMapper(Arrays.asList(
    				"markerTermID",
    				"goTerm"
    					),"OR"));
        
        propertyMap.put("goProcess",new SolrPropertyMapper("goProcessTerm"));
        propertyMap.put("goFunction",new SolrPropertyMapper("goFunctionTerm"));
        propertyMap.put("goComponent",new SolrPropertyMapper("goComponentTerm"));

        
        /*
         * Phenotype searches
         */
        propertyMap.put("phenotype",
    		new SolrPropertyMapper(Arrays.asList(
    				"phenoId",
    				"phenoText"
    					),"OR"));
        
        
        /*
         * sortable fields
         */
        this.sortMap.put("bySymbol",new SolrSortMapper("bySymbol"));
        this.sortMap.put("byLocation",new SolrSortMapper("byLocation"));
        
        
        /*
         * Which fields to highlight
         */
        highlightRequireFieldMatch=false; // we want to highlight fields that we are not querying
        highlightFragmentSize=100;
        highlightSnippets = 1;
        highlightPre = "$$"; // to be replaced later, because we need to superscript the data also
        highlightPost = "/$$";
        // marker highlights
        highlightFields.add("currentSymbol");
        highlightFields.add("currentName");
        highlightFields.add("synonym");
        highlightFields.add("oldSymbol");
        highlightFields.add("oldName");
        highlightFields.add("relatedSynonym");
        highlightFields.add("alleleSymbol");
        highlightFields.add("alleleName");
        highlightFields.add("alleleSynonym");
        highlightFields.add("humanSymbol");
        highlightFields.add("humanName");
        highlightFields.add("humanSynonym");
        
        /*
         * Which fields to return
         */
        this.returnedFields.add("markerSymbol");
        this.returnedFields.add("markerName");
        this.returnedFields.add("markerKey");
        this.returnedFields.add("featureType");
        this.returnedFields.add("chromosome");
        this.returnedFields.add("startCoord");
        this.returnedFields.add("endCoord");
        this.returnedFields.add("locationDisplay");
        this.returnedFields.add("coordinateDisplay");


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
    protected void packInformation(QueryResponse rsp, SearchResults sr,
            SearchParams sp) {
        
        // A list of all the primary keys in the document
        SolrDocumentList sdl = rsp.getResults();

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */
        Map<String,SolrSummaryMarker> keyToResultMap = new HashMap<String,SolrSummaryMarker>();
        
        List<String> resultKeys = new ArrayList<String>();
        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();
            
            //logger.debug(doc.toString());
            // Set the result object
            String markerKey = (String) doc.getFieldValue("markerKey");
            resultKeys.add(markerKey);
            
            SolrSummaryMarker marker = new SolrSummaryMarker();

            marker.setSymbol((String) doc.getFieldValue("markerSymbol"));
            marker.setName((String) doc.getFieldValue("markerName"));
            marker.setMarkerKey(markerKey);
            marker.setMgiId("");
            marker.setFeatureType((String) doc.getFieldValue("featureType"));
            marker.setChromosome((String) doc.getFieldValue("chromosome"));
            marker.setCoordStart((Integer) doc.getFieldValue("startCoord"));
            marker.setCoordEnd((Integer) doc.getFieldValue("endCoord"));
            marker.setCoordinateDisplay((String) doc.getFieldValue("coordinateDisplay"));
            marker.setLocationDisplay((String) doc.getFieldValue("locationDisplay"));
            
            // Add result to SearchResults
            sr.addResultObjects(marker);
            keyToResultMap.put(markerKey,marker);
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

            for(String uniqueKey : highlights.keySet())
            {
            	Map<String,List<String>> highlight = highlights.get(uniqueKey);

            	if(!keyToResultMap.containsKey(uniqueKey)) continue;
            	SolrSummaryMarker marker = keyToResultMap.get(uniqueKey);

            	Set<String> highlightList = new LinkedHashSet<String>();
            	
            	// just add the field names that matched for now
//            	for(String field : highlight.keySet())
//            	{
//            		// just add the first one for each field
//            		String highlightMatch = "<b>"+field+"</b>:\""+highlight.get(field).get(0)+"\"";
//            		highlightList.add(highlightMatch);
//            	}
            	// only add the first matching highlight
            	outer:
            	for(String hf : this.highlightFields)
            	{
            		for(String field : highlight.keySet())
            		{
            			if(hf.equals(field))
            			{
            				String hl = highlight.get(field).get(0);
            				hl = FormatHelper.superscript(hl);
            				hl = hl.replaceAll("/\\$\\$","</b>").replaceAll("\\$\\$","<b>");
            				String highlightMatch = field+":\""+hl+"\"";
                    		highlightList.add(highlightMatch);
                    		break outer;
            			}
            		}
            	}
            	
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