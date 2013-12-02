package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrJoinMapper;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdImage;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridData;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.jax.mgi.shr.fe.indexconstants.ImagePaneFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the diseasePortal solr index
 *
 * This is capable of grouping by various object types to return different summaries
 *
 * 		When grouping, stored fields for objects lower in the relationship will not be relevant (and should not be accessed).
 *		However, you are free, and expected, to query on any field, regardless of which object is grouped.
 *
 *		The relationship order currently looks like this cluster->marker->genotype->term
 *		Hence, if you group by genotype, you would have access to the marker and the cluster of that genotype.
 *			But you would not be able to get every term for that genotype (only the first one, depending on the sort order).
 *
 * @author kstone
 *
 */

@Repository
public class SolrDiseasePortalHunter extends SolrHunter
{
	private String diseasePortalAnnotationUrl;

    /***
     * The constructor sets up this hunter so that it is specific to diseasePortal
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrDiseasePortalHunter()
    {
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         *
         */

    	/*
    	 * query by gene
    	 */
    	propertyMap.put(SearchConstants.MRK_NOMENCLATURE,
    			new SolrPropertyMapper(Arrays.asList(
    				DiseasePortalFields.MARKER_NOMEN_SEARCH,
    				DiseasePortalFields.MARKER_ID_SEARCH
    					),"OR"));

    	propertyMap.put(DiseasePortalFields.ORGANISM,
           		new SolrPropertyMapper(DiseasePortalFields.ORGANISM));

    	/*
    	 * query by location
    	 */
    	 propertyMap.put(SearchConstants.MOUSE_COORDINATE,
           		new SolrPropertyMapper(DiseasePortalFields.MOUSE_COORDINATE));

    	 propertyMap.put(SearchConstants.HUMAN_COORDINATE,
            		new SolrPropertyMapper(DiseasePortalFields.HUMAN_COORDINATE));

    	 /*
    	  * query by genotype
    	  */
    	 propertyMap.put(SearchConstants.GENOTYPE_TYPE,
          		new SolrPropertyMapper(DiseasePortalFields.GENOTYPE_TYPE));

         /*
          * query by term
          * */
         propertyMap.put(SearchConstants.VOC_TERM_ID,
         		new SolrPropertyMapper(DiseasePortalFields.TERM_ID_SEARCH));

         propertyMap.put(SearchConstants.VOC_TERM,
        		 new SolrPropertyMapper(DiseasePortalFields.TERM_SEARCH));

         // This is a special field, set up for mapping MP terms to diseases
         // it is only used in the diseasePortal diseases summary (use instead of TERM_SEARCH)
         propertyMap.put(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE,
        		 new SolrPropertyMapper(DiseasePortalFields.TERM_SEARCH_FOR_DISEASE));

         // This is a special field, set up for mapping MP terms to diseases (via only super-simple genotypes)
         // it is only used in the diseasePortal grid summary (use instead of TERM_SEARCH)
         propertyMap.put(DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS,
        		 new SolrPropertyMapper(DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS));

         propertyMap.put(SearchConstants.VOC_TERM_TYPE,
          		new SolrPropertyMapper(DiseasePortalFields.TERM_TYPE));


         propertyMap.put(DiseasePortalFields.TERM_QUALIFIER,
            		new SolrPropertyMapper(DiseasePortalFields.TERM_QUALIFIER));
         
         propertyMap.put(DiseasePortalFields.TERM_HEADER,
         		new SolrPropertyMapper(DiseasePortalFields.TERM_HEADER));
         /*
          * query by keys
          */
         propertyMap.put(SearchConstants.MRK_KEY,
            		new SolrPropertyMapper(DiseasePortalFields.MARKER_KEY));
         propertyMap.put(SearchConstants.PRIMARY_KEY,
           		new SolrPropertyMapper(DiseasePortalFields.UNIQUE_KEY));

         /*
          * query for grid cluster
          */
         propertyMap.put(SearchConstants.DP_GRID_CLUSTER_KEY,
            		new SolrPropertyMapper(DiseasePortalFields.GRID_CLUSTER_KEY));

         /*
          * query for geno cluster
          */
         propertyMap.put(SearchConstants.DP_GENO_CLUSTER_KEY,
            		new SolrPropertyMapper(DiseasePortalFields.GENO_CLUSTER_KEY));

         /*
          * query for mp id
          */
         propertyMap.put(SearchConstants.MP_HEADER,
            		new SolrPropertyMapper(DiseasePortalFields.TERM_HEADER));

        /*
         * Setup the sort mapping.
         */
         
         // grid sorts
         sortMap.put(DiseasePortalFields.GRID_BY_MOUSE_LOCATION, new SolrSortMapper(DiseasePortalFields.GRID_BY_MOUSE_LOCATION));
         sortMap.put(DiseasePortalFields.GRID_BY_HUMAN_LOCATION, new SolrSortMapper(DiseasePortalFields.GRID_BY_HUMAN_LOCATION));
         
         // marker sorts
        sortMap.put(SortConstants.DP_BY_MRK_SYMBOL, new SolrSortMapper(DiseasePortalFields.BY_MARKER_SYMBOL));
        sortMap.put(SortConstants.DP_BY_MRK_TYPE, new SolrSortMapper(DiseasePortalFields.BY_MARKER_TYPE));
        sortMap.put(SortConstants.DP_BY_ORGANISM, new SolrSortMapper(DiseasePortalFields.BY_MARKER_ORGANISM));
        sortMap.put(SortConstants.DP_BY_LOCATION, new SolrSortMapper(DiseasePortalFields.BY_MARKER_LOCATION));
        sortMap.put(SortConstants.DP_BY_HOMOLOGENE_ID, new SolrSortMapper(DiseasePortalFields.BY_HOMOLOGENE_ID));

        // term sorts
        sortMap.put(SortConstants.VOC_TERM, new SolrSortMapper(DiseasePortalFields.BY_TERM_NAME));
        sortMap.put(DiseasePortalFields.BY_TERM_DAG, new SolrSortMapper(DiseasePortalFields.BY_TERM_DAG));
        sortMap.put(SortConstants.VOC_TERM_ID, new SolrSortMapper(DiseasePortalFields.TERM_ID));
        sortMap.put(SortConstants.VOC_TERM_HEADER, new SolrSortMapper(DiseasePortalFields.BY_MP_HEADER));
        sortMap.put(DiseasePortalFields.BY_GENOCLUSTER, new SolrSortMapper(DiseasePortalFields.BY_GENOCLUSTER));


         /*
          * Groupings
          * list of fields that can be uniquely grouped on
          */
        this.groupFields.put(SearchConstants.DP_GRID_CLUSTER_KEY,DiseasePortalFields.GRID_CLUSTER_KEY);
        this.groupFields.put(SearchConstants.DP_GENO_CLUSTER_KEY,DiseasePortalFields.GENO_CLUSTER_KEY);
        this.groupFields.put(SearchConstants.MRK_KEY,DiseasePortalFields.MARKER_KEY);
        this.groupFields.put(DiseasePortalFields.TERM_HEADER,DiseasePortalFields.TERM_HEADER);
        this.groupFields.put(SearchConstants.VOC_TERM_ID,DiseasePortalFields.TERM_ID_GROUP);
        this.groupFields.put("bareMarkerKey",DiseasePortalFields.MARKER_KEY);


        /*
         * Which fields to highlight
         */
        highlightRequireFieldMatch=false; // we want to highlight fields that we are not querying
        highlightFragmentSize=100;
        highlightSnippets = 1;
        highlightPre = "<b>";
        highlightPost = "</b>";
        // marker highlights
       // highlightFields.add(DiseasePortalFields.HOMOLOGENE_ID);
        highlightFields.add(DiseasePortalFields.MARKER_ID);
        highlightFields.add(DiseasePortalFields.MARKER_SYMBOL);
        highlightFields.add(DiseasePortalFields.MARKER_NAME);
        highlightFields.add(DiseasePortalFields.MARKER_SYNONYM);
        highlightFields.add(DiseasePortalFields.ORTHOLOG_ID);
        highlightFields.add(DiseasePortalFields.ORTHOLOG_NOMEN);
        // term highlights
        highlightFields.add(DiseasePortalFields.TERM);
        highlightFields.add(DiseasePortalFields.TERM_ID);
        highlightFields.add(DiseasePortalFields.TERM_SYNONYM);
        highlightFields.add(DiseasePortalFields.TERM_ANCESTOR);
        highlightFields.add(DiseasePortalFields.TERM_ALT_ID);
        highlightFields.add(DiseasePortalFields.MP_TERM_FOR_DISEASE);

        /*
         * Which fields to return
         * 		Some fields may be large, but we have to store them for highlighting purposes.
         * 		We can make the return document smaller by defining the returned fields
         */
        // cluster fields
        returnedFields.add(DiseasePortalFields.GRID_CLUSTER_KEY);
        returnedFields.add(DiseasePortalFields.GENO_CLUSTER_KEY);

        // marker fields
        returnedFields.add(DiseasePortalFields.UNIQUE_KEY);
        returnedFields.add(DiseasePortalFields.MARKER_KEY);
        returnedFields.add(DiseasePortalFields.ORGANISM);
        returnedFields.add(DiseasePortalFields.HOMOLOGENE_ID);
        returnedFields.add(DiseasePortalFields.MARKER_SYMBOL);
        returnedFields.add(DiseasePortalFields.MARKER_MGI_ID);
        //returnedFields.add(DiseasePortalFields.MARKER_FEATURE_TYPE);
        returnedFields.add(DiseasePortalFields.LOCATION_DISPLAY);
        returnedFields.add(DiseasePortalFields.COORDINATE_DISPLAY);
        returnedFields.add(DiseasePortalFields.BUILD_IDENTIFIER);
        returnedFields.add(DiseasePortalFields.MARKER_DISEASE);
        returnedFields.add(DiseasePortalFields.MARKER_SYSTEM);
        returnedFields.add(DiseasePortalFields.MARKER_ALL_REF_COUNT);
        returnedFields.add(DiseasePortalFields.MARKER_DISEASE_REF_COUNT);
        
        // term fields
        returnedFields.add(DiseasePortalFields.TERM_ID);
        returnedFields.add(DiseasePortalFields.TERM);
        returnedFields.add(DiseasePortalFields.TERM_TYPE);
        returnedFields.add(DiseasePortalFields.TERM_HEADER);
        returnedFields.add(DiseasePortalFields.DISEASE_MODEL_COUNTS);
        returnedFields.add(DiseasePortalFields.DISEASE_REF_COUNT);
        returnedFields.add(DiseasePortalFields.TERM_MOUSESYMBOL);
        returnedFields.add(DiseasePortalFields.TERM_HUMANSYMBOL);
        
        /*
         * define the returned fields for each type of group query
         */
        groupReturnedFields.put(SearchConstants.DP_GRID_CLUSTER_KEY,Arrays.asList(DiseasePortalFields.GRID_CLUSTER_KEY,
        		DiseasePortalFields.GRID_MOUSE_SYMBOLS,
        		DiseasePortalFields.GRID_HUMAN_SYMBOLS,
        		DiseasePortalFields.HOMOLOGENE_ID));
        groupReturnedFields.put(SearchConstants.DP_GENO_CLUSTER_KEY,Arrays.asList(DiseasePortalFields.GRID_CLUSTER_KEY,
        		DiseasePortalFields.GENO_CLUSTER_KEY));
        groupReturnedFields.put(SearchConstants.MRK_KEY,Arrays.asList(DiseasePortalFields.GRID_CLUSTER_KEY,
        		DiseasePortalFields.GENO_CLUSTER_KEY,
        		DiseasePortalFields.UNIQUE_KEY,
		        DiseasePortalFields.MARKER_KEY,
		        DiseasePortalFields.ORGANISM,
		        DiseasePortalFields.HOMOLOGENE_ID,
		        DiseasePortalFields.MARKER_SYMBOL,
		        DiseasePortalFields.MARKER_MGI_ID,
		        //DiseasePortalFields.MARKER_FEATURE_TYPE,
		        DiseasePortalFields.LOCATION_DISPLAY,
		        DiseasePortalFields.COORDINATE_DISPLAY,
		        DiseasePortalFields.BUILD_IDENTIFIER,
		        DiseasePortalFields.MARKER_DISEASE,
		        DiseasePortalFields.MARKER_SYSTEM,
		        DiseasePortalFields.MARKER_ALL_REF_COUNT,
		        DiseasePortalFields.MARKER_DISEASE_REF_COUNT,
		        DiseasePortalFields.MARKER_IMSR_COUNT));
        groupReturnedFields.put(DiseasePortalFields.TERM_HEADER,Arrays.asList(DiseasePortalFields.TERM_HEADER));
        groupReturnedFields.put(SearchConstants.VOC_TERM_ID,Arrays.asList(DiseasePortalFields.TERM_ID,
		        DiseasePortalFields.TERM,
		        DiseasePortalFields.TERM_TYPE,
		        DiseasePortalFields.TERM_HEADER,
		        DiseasePortalFields.DISEASE_MODEL_COUNTS,
		        DiseasePortalFields.DISEASE_REF_COUNT,
		        DiseasePortalFields.TERM_MOUSESYMBOL,
		        DiseasePortalFields.TERM_HUMANSYMBOL));
        groupReturnedFields.put("bareMarkerKey",Arrays.asList(DiseasePortalFields.MARKER_KEY));

        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a
         * specific field, and return it rather than a list of keys.
         */
        keyString = DiseasePortalFields.UNIQUE_KEY;
    }

    @Override
    protected void packInformation(QueryResponse rsp, SearchResults sr,
            SearchParams sp) {

        // A list of all the primary keys in the document

        SolrDocumentList sdl = rsp.getResults();
        logger.debug("-packing disease portal hunt data");

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */

        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();

            //logger.debug(doc.toString());
            // Set the result object
            String term = (String) doc.getFieldValue(DiseasePortalFields.TERM);

            // Add just the term if no groups are specified for now
            sr.addResultObjects(term);
        }
    }

    @Override
    protected void packInformationByGroup(QueryResponse rsp, SearchResults sr,
            SearchParams sp) {
    	GroupResponse gr = rsp.getGroupResponse();

    	// get the group command. In our case, there should only be one.
    	GroupCommand gc = gr.getValues().get(0);

    	// total count of groups
    	int groupCount = gc.getNGroups();
    	sr.setTotalCount(groupCount);

        // A list of all the primary keys in the document
        List<String> keys = new ArrayList<String>();

        // A listing of all of the facets.  This is used
        // at the set level.
        List<String> facet = new ArrayList<String>();

        Map<String,String> keyToGroupKeyMap = new HashMap<String,String>();

        // A mapping of documentKey -> Row level Metadata objects.
        Map<String, MetaData> metaList = new HashMap<String, MetaData> ();

        List<Group> groups = gc.getValues();
        logger.debug("packing information for group: "+gc.getName());

        /**
         * Check for facets, if found pack them.
         */

        if (this.facetString != null) {
            for (Count c: rsp.getFacetField(facetString).getValues()) {
                facet.add(c.getName());
                logger.debug(c.getName());
            }
        }

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */

        for (Group g : groups)
        {
        	String key = g.getGroupValue();
        	int numFound = (int) g.getResult().getNumFound();

        	// get the top document of the group
        	SolrDocument sd = g.getResult().get(0);
        	String uniqueKey = (String)sd.getFieldValue(DiseasePortalFields.UNIQUE_KEY);

        	if(gc.getName().equals(DiseasePortalFields.GRID_CLUSTER_KEY))
        	{
        		// this is an hdp_gridcluster_key
        		Integer gridClusterKey = (Integer)sd.getFieldValue(DiseasePortalFields.GRID_CLUSTER_KEY);

        		// Grid cluster is to be filled out by Hibernate (since it is too complex), so we just add the result keys here.
        		SolrHdpGridCluster gridCluster = new SolrHdpGridCluster();
        		
        		gridCluster.setGridClusterKey(gridClusterKey);
        		gridCluster.setHomologeneId((String)sd.getFieldValue(DiseasePortalFields.HOMOLOGENE_ID));
        		gridCluster.setMouseSymbols((List<String>)sd.getFieldValue(DiseasePortalFields.GRID_MOUSE_SYMBOLS));
        		gridCluster.setHumanSymbols((List<String>)sd.getFieldValue(DiseasePortalFields.GRID_HUMAN_SYMBOLS));
        		
        		keys.add(gridClusterKey.toString());
        		sr.addResultObjects(gridCluster);
        		keyToGroupKeyMap.put(uniqueKey,gridClusterKey.toString());
        		
        	}
        	if(gc.getName().equals(DiseasePortalFields.GENO_CLUSTER_KEY))
        	{
        		// this is an hdp_genocluster_key
        		Integer genoClusterKey = (Integer)sd.getFieldValue(DiseasePortalFields.GENO_CLUSTER_KEY);

        		// Geno cluster is to be filled out by Hibernate (since it is too complex), so we just add the result keys here.
        		keys.add(genoClusterKey.toString());
        		keyToGroupKeyMap.put(uniqueKey,genoClusterKey.toString());
        	}
        	else if(gc.getName().equals(DiseasePortalFields.MARKER_KEY))
        	{
        		// we got ourselves a good old fashioned marker object
        		Integer markerKey = (Integer)sd.getFieldValue(DiseasePortalFields.MARKER_KEY);
        		// return just the marker key for now
        		//sr.addResultObjects(markerKey);
        		if(markerKey != null) keys.add(markerKey.toString());

        		if(!sp.getFetchKeysOnly())
        		{
	                // create and fill data entity
	        		SolrDiseasePortalMarker dpMarker = new SolrDiseasePortalMarker();
	        		dpMarker.setMarkerKey(markerKey.toString());
	                dpMarker.setOrganism((String)sd.getFieldValue(DiseasePortalFields.ORGANISM));
	                dpMarker.setHomologeneId((String)sd.getFieldValue(DiseasePortalFields.HOMOLOGENE_ID));
	                dpMarker.setSymbol((String)sd.getFieldValue(DiseasePortalFields.MARKER_SYMBOL));
	                dpMarker.setMgiId((String)sd.getFieldValue(DiseasePortalFields.MARKER_MGI_ID));
	               // dpMarker.setType((String)sd.getFieldValue(DiseasePortalFields.MARKER_FEATURE_TYPE));
	                dpMarker.setLocation((String)sd.getFieldValue(DiseasePortalFields.LOCATION_DISPLAY));
	                dpMarker.setCoordinate((String)sd.getFieldValue(DiseasePortalFields.COORDINATE_DISPLAY));
	                dpMarker.setCoordinateBuild((String)sd.getFieldValue(DiseasePortalFields.BUILD_IDENTIFIER));
	                dpMarker.setDisease((List<String>)sd.getFieldValue(DiseasePortalFields.MARKER_DISEASE));
	                dpMarker.setSystem((List<String>)sd.getFieldValue(DiseasePortalFields.MARKER_SYSTEM));
	                dpMarker.setAllRefCount((Integer)sd.getFieldValue(DiseasePortalFields.MARKER_ALL_REF_COUNT));
	                dpMarker.setDiseaseRefCount((Integer)sd.getFieldValue(DiseasePortalFields.MARKER_DISEASE_REF_COUNT));
	                dpMarker.setImsrCount((Integer)sd.getFieldValue(DiseasePortalFields.MARKER_IMSR_COUNT));
	                
	        		sr.addResultObjects(dpMarker);
        		}
        		keyToGroupKeyMap.put(uniqueKey,markerKey.toString());

        	}
        	else if(gc.getName().equals(DiseasePortalFields.TERM_HEADER))
        	{
        		String header = (String) sd.getFieldValue(DiseasePortalFields.TERM_HEADER);

        		// return just the term name for now
        		sr.addResultObjects(header);
        		keys.add(header);
        		keyToGroupKeyMap.put(uniqueKey,header);
        	}
        	else if(gc.getName().equals(DiseasePortalFields.TERM_ID_GROUP))
        	{
                // create data entity for display
        		SolrVocTerm vt = new SolrVocTerm();

        		// this is a term object
        		String termId =(String)sd.getFieldValue(DiseasePortalFields.TERM_ID);

        		vt.setTerm((String)sd.getFieldValue(DiseasePortalFields.TERM));
        		vt.setPrimaryId(termId);
        		String vocab = (String)sd.getFieldValue(DiseasePortalFields.TERM_TYPE);
        		vt.setVocabName(vocab);
        		if("OMIM".equalsIgnoreCase(vocab))
        		{
        			vt.setDiseaseRefCount((Integer)sd.getFieldValue(DiseasePortalFields.DISEASE_REF_COUNT));
        			vt.setDiseaseModelCount(((Integer)sd.getFieldValue(DiseasePortalFields.DISEASE_MODEL_COUNTS)));
        			vt.setDiseaseMouseMarkers(((List<String>)sd.getFieldValue(DiseasePortalFields.TERM_MOUSESYMBOL)));
        			vt.setDiseaseHumanMarkers(((List<String>)sd.getFieldValue(DiseasePortalFields.TERM_HUMANSYMBOL)));
        		}

        		// return just the term name for now
        		sr.addResultObjects(vt);
        		keys.add(termId);
        		keyToGroupKeyMap.put(uniqueKey,termId);
        	}

        }

        // Include the information that was asked for.

        if (keys != null) {
            sr.setResultKeys(keys);
        }

        if (facet != null) {
            sr.setResultFacets(facet);
        }

        // A mapping of field -> set of highlighted words
        // for the result set.
        Map<String, Set<String>> setHighlights = new HashMap<String, Set<String>> ();

        if (sp.includeSetMeta()) {
            sr.setResultSetMeta(new ResultSetMetaData(setHighlights));
        }

        if (sp.includeRowMeta()) {
            sr.setMetaMapping(metaList);
        }

        if (!this.highlightFields.isEmpty() && sp.includeMetaHighlight() && sp.includeSetMeta())
        {
            // A mapping of documentKey -> Mapping of FieldName -> list of highlighted fields
            Map<String, Map<String, List<String>>> highlights = rsp.getHighlighting();

            for(String uniqueKey : highlights.keySet())
            {
            	Map<String,List<String>> highlight = highlights.get(uniqueKey);

            	if(!keyToGroupKeyMap.containsKey(uniqueKey)) continue;
            	String groupKey = keyToGroupKeyMap.get(uniqueKey);

            	setHighlights.put(groupKey,new LinkedHashSet<String>());
            	// just add the field names that matched for now
            	for(String field : highlight.keySet())
            	{
            		// just add the first one for each field
            		String highlightMatch = "<b>"+field+"</b>:\""+highlight.get(field).get(0)+"\"";
                	setHighlights.get(groupKey).add(highlightMatch);
            	}
            }
        }
    }

    /*
     * Custom implementation for the join response
     *
     */
    @Override
    protected void packInformationForJoin(QueryResponse rsp, SearchResults sr,
            SearchParams sp)
    {
        SolrDocumentList sdl = rsp.getResults();
        logger.debug("-->packing joined hpd annotation data");

        /**
         * Iterate response documents, extracting data from solr doc
         */
        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
          SolrDocument doc = (SolrDocument) iter.next();

          // fill data object, and add to SearchResults
          SolrHdpGridData genoInResult = new SolrHdpGridData();
          genoInResult.setGridClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GRID_CLUSTER_KEY));
          genoInResult.setGenoClusterKey((Integer) doc.getFieldValue(DiseasePortalFields.GENO_CLUSTER_KEY));
          genoInResult.setMarkerKey((Integer) doc.getFieldValue(DiseasePortalFields.MARKER_KEY));
          genoInResult.setTerm((String) doc.getFieldValue(DiseasePortalFields.TERM));
          genoInResult.setTermId((String) doc.getFieldValue(DiseasePortalFields.TERM_ID));
          genoInResult.setTermType((String) doc.getFieldValue(DiseasePortalFields.TERM_TYPE));
          genoInResult.setVocabName((String) doc.getFieldValue(DiseasePortalFields.VOCAB_NAME));
          genoInResult.setQualifier((String) doc.getFieldValue(DiseasePortalFields.TERM_QUALIFIER));
          genoInResult.setAnnotCount((Integer) doc.getFieldValue(DiseasePortalFields.ANNOT_COUNT));
          genoInResult.setHumanAnnotCount((Integer) doc.getFieldValue(DiseasePortalFields.HUMAN_ANNOT_COUNT));

          sr.addResultObjects(genoInResult);
        }
    }

	@Value("${solr.disease_portal.url}")
	public void setSolrUrl(String solrUrl)
	{ super.solrUrl = solrUrl; }

	@Value("${solr.disease_portal_annotation.url}")
	public void setDiseasePortalAnnotationUrl(String diseasePortalAnnotationUrl)
	{
		this.diseasePortalAnnotationUrl = diseasePortalAnnotationUrl;
		/*
         * Joined indices
         * List of indices that can be joined to this index.
         * This means you query *this* index (the "from"), but return docs from the joined index (the "to").
         */
        this.joinIndices.put("diseasePortalAnnotationHeaders", new SolrJoinMapper(diseasePortalAnnotationUrl,
            DiseasePortalFields.GENO_CLUSTER_KEY,
            "diseasePortal",
            DiseasePortalFields.GENO_CLUSTER_KEY,
            "termType:header")
        );
        // create one for just the terms in the annotation index
        this.joinIndices.put("diseasePortalAnnotationTerms", new SolrJoinMapper(diseasePortalAnnotationUrl,
            DiseasePortalFields.GENO_CLUSTER_KEY,
            "diseasePortal",
            DiseasePortalFields.GENO_CLUSTER_KEY,
            "termType:term")
        );
        // create a way to also join on markerKey
        this.joinIndices.put("diseasePortalAnnotationByMarker", new SolrJoinMapper(diseasePortalAnnotationUrl,
                DiseasePortalFields.HUMAN_DISEASE_JOIN_KEY,
                "diseasePortal",
                DiseasePortalFields.HUMAN_DISEASE_JOIN_KEY,
                "termType:header")
            );
        // create a way to also join on markerKey
        this.joinIndices.put("diseasePortalAnnotationByMarkerTerm", new SolrJoinMapper(diseasePortalAnnotationUrl,
        		DiseasePortalFields.HUMAN_DISEASE_JOIN_KEY,
                "diseasePortal",
                DiseasePortalFields.HUMAN_DISEASE_JOIN_KEY,
                "termType:term")
            );
	}
}
