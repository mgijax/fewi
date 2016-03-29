package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrJoinMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridData;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrHdpEntity;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * main Hunter for the diseasePortal solr index
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
public class SolrDiseasePortalHunterOLD extends SolrDiseasePortalBaseHunter
{
    /***
     * The constructor sets up this hunter so that it is specific to diseasePortal
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrDiseasePortalHunterOLD()
    {
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a
         * specific field, and return it rather than a list of keys.
         */
        keyString = DiseasePortalFields.UNIQUE_KEY;
    }

    @Override
    protected void packInformation(QueryResponse rsp, SearchResults<SolrHdpEntity> sr,
      SearchParams sp) {

        // A list of all the primary keys in the document

        SolrDocumentList sdl = rsp.getResults();
        logger.debug("-packing disease portal hunt data");

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */

        for (SolrDocument doc : sdl)
        {

            //logger.debug(doc.toString());
            // Set the result object
            String term = (String) doc.getFieldValue(DiseasePortalFields.TERM);

            // Add just the term if no groups are specified for now
            sr.addResultObjects(new SolrString(term));
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void packInformationByGroup(QueryResponse rsp, SearchResults<SolrHdpEntity> sr,
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
        		gridCluster.setHomologySource((String)sd.getFieldValue(DiseasePortalFields.HOMOLOGY_SOURCE));
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
        		String markerKeyString = markerKey != null ? markerKey.toString() : "";

        		if(!sp.getFetchKeysOnly())
        		{
	                // create and fill data entity
	        		SolrDiseasePortalMarker dpMarker = new SolrDiseasePortalMarker();
	        		dpMarker.setMarkerKey(markerKey.toString());
	                dpMarker.setOrganism((String)sd.getFieldValue(DiseasePortalFields.ORGANISM));
	                dpMarker.setGridClusterKey((Integer)sd.getFieldValue(DiseasePortalFields.GRID_CLUSTER_KEY));
	                dpMarker.setHomologyClusterKey((Integer)sd.getFieldValue(DiseasePortalFields.HOMOLOGY_CLUSTER_KEY));
	                dpMarker.setHomologeneId((String)sd.getFieldValue(DiseasePortalFields.HOMOLOGENE_ID));
	                dpMarker.setHomologySource((String)sd.getFieldValue(DiseasePortalFields.HOMOLOGY_SOURCE));
	                dpMarker.setName((String)sd.getFieldValue(DiseasePortalFields.MARKER_NAME));
	                dpMarker.setSymbol((String)sd.getFieldValue(DiseasePortalFields.MARKER_SYMBOL));
	                dpMarker.setMgiId((String)sd.getFieldValue(DiseasePortalFields.MARKER_MGI_ID));
	                dpMarker.setType((String)sd.getFieldValue(DiseasePortalFields.MARKER_FEATURE_TYPE));
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
        		keyToGroupKeyMap.put(uniqueKey,markerKeyString);

        	}
        	else if(gc.getName().equals(DiseasePortalFields.TERM_GROUP))
        	{
        		String term = (String) sd.getFieldValue(DiseasePortalFields.TERM_GROUP);

        		// return just the term name for now
        		sr.addResultObjects(new SolrString(term));
        		keys.add(term);
        		keyToGroupKeyMap.put(uniqueKey,term);
        	}
        	else if(gc.getName().equals(DiseasePortalFields.TERM_HEADER))
        	{
        		String header = (String) sd.getFieldValue(DiseasePortalFields.TERM_HEADER);

        		// return just the term name for now
        		sr.addResultObjects(new SolrString(header));
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
    protected void packInformationForJoin(QueryResponse rsp, SearchResults<SolrHdpEntity> sr,
            SearchParams sp)
    {
        SolrDocumentList sdl = rsp.getResults();
        logger.debug("-->packing joined hpd annotation data");

        /**
         * Iterate response documents, extracting data from solr doc
         */
        for (SolrDocument doc : sdl)
        {

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
