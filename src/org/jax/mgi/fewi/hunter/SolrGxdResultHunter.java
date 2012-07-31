package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.ResultSetMetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAssayResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdAssay;
import org.jax.mgi.fewi.searchUtil.entities.SolrGxdMarker;
import org.jax.mgi.fewi.searchUtil.entities.StructureACResult;
import org.jax.mgi.fewi.searchUtil.entities.VocabACResult;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.GxdResultFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Hunter for the GXD Result solr index
 *
 * Initially we can query only by marker nomenclature
 *
 * @author kstone
 *
 */

@Repository
public class SolrGxdResultHunter extends SolrHunter
{
    /***
     * The constructor sets up this hunter so that it is specific to sequence
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrGxdResultHunter()
    {
        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         *
         */

         /*
          * marker nomenclature
          * */
         propertyMap.put(SearchConstants.MRK_NOMENCLATURE,
         		new SolrPropertyMapper(GxdResultFields.NOMENCLATURE));

         // jnum
         propertyMap.put(SearchConstants.REF_ID,
         		new SolrPropertyMapper(GxdResultFields.JNUM));

         // marker ID
         propertyMap.put(SearchConstants.MRK_ID,
         		new SolrPropertyMapper(GxdResultFields.MARKER_MGIID));

         // vocab annotation ids
         propertyMap.put(GxdResultFields.ANNOTATION,
          		new SolrPropertyMapper(GxdResultFields.ANNOTATION));

         // Assay type
         propertyMap.put(SearchConstants.GXD_ASSAY_TYPE,
        		 new SolrPropertyMapper(GxdResultFields.ASSAY_TYPE));

         // Theiler Stage
         propertyMap.put(SearchConstants.GXD_THEILER_STAGE,
          		new SolrPropertyMapper(GxdResultFields.THEILER_STAGE));

         // Age
         propertyMap.put(SearchConstants.GXD_AGE_MIN,
        		 new SolrPropertyMapper(GxdResultFields.AGE_MIN));
         propertyMap.put(SearchConstants.GXD_AGE_MAX,
        		 new SolrPropertyMapper(GxdResultFields.AGE_MAX));

         // is Expressed
         propertyMap.put(SearchConstants.GXD_DETECTED,
           		new SolrPropertyMapper(GxdResultFields.IS_EXPRESSED));

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




         propertyMap.put(SearchConstants.PRIMARY_KEY,
           		new SolrPropertyMapper(GxdResultFields.KEY));

         propertyMap.put(SearchConstants.GXD_IS_WILD_TYPE,
            		new SolrPropertyMapper(GxdResultFields.IS_WILD_TYPE));

         propertyMap.put(SearchConstants.GXD_MUTATED_IN,
         		new SolrPropertyMapper(GxdResultFields.MUTATED_IN));


        /*
         * Setup the sort mapping.
         */
        sortMap.put(SortConstants.GXD_GENE, new SolrSortMapper(GxdResultFields.R_BY_MRK_SYMBOL));
        sortMap.put(SortConstants.GXD_ASSAY_TYPE, new SolrSortMapper(GxdResultFields.R_BY_ASSAY_TYPE));
        sortMap.put(SortConstants.GXD_SYSTEM, new SolrSortMapper(GxdResultFields.R_BY_ANATOMICAL_SYSTEM));
        sortMap.put(SortConstants.GXD_AGE, new SolrSortMapper(GxdResultFields.R_BY_AGE));
        sortMap.put(SortConstants.GXD_STRUCTURE, new SolrSortMapper(GxdResultFields.R_BY_STRUCTURE));
        sortMap.put(SortConstants.GXD_DETECTION, new SolrSortMapper(GxdResultFields.R_BY_EXPRESSED));
        sortMap.put(SortConstants.GXD_GENOTYPE, new SolrSortMapper(GxdResultFields.R_BY_MUTANT_ALLELES));
        sortMap.put(SortConstants.GXD_REFERENCE, new SolrSortMapper(GxdResultFields.R_BY_REFERENCE));
        sortMap.put(SortConstants.GXD_LOCATION, new SolrSortMapper(GxdResultFields.M_BY_LOCATION));

         /*
          * Groupings
          * list of fields that can be uniquely grouped on
          */
         this.groupFields.put(SearchConstants.MRK_KEY,GxdResultFields.MARKER_KEY);
         this.groupFields.put(SearchConstants.GXD_ASSAY_KEY,GxdResultFields.ASSAY_KEY);
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want to actually get a
         * specific field, and return it rather than a list of keys.
         */
        keyString = GxdResultFields.RESULT_KEY;
    }

    @Override
    protected void packInformation(QueryResponse rsp, SearchResults sr,
            SearchParams sp) {

        // A list of all the primary keys in the document

        SolrDocumentList sdl = rsp.getResults();
        logger.debug("packing gxd test hunt data");

        /**
         * Iterate through the response documents, extracting the information
         * that was configured at the implementing class level.
         */

        for (Iterator iter = sdl.iterator(); iter.hasNext();)
        {
            SolrDocument doc = (SolrDocument) iter.next();

            //logger.debug(doc.toString());
            // Set the result object
            String assayKey = (String) doc.getFieldValue(GxdResultFields.ASSAY_KEY);
            String age = (String) doc.getFieldValue(GxdResultFields.AGE);
            String anatomicalSystem = (String) doc.getFieldValue(GxdResultFields.ANATOMICAL_SYSTEM);
            String assayMgiid = (String) doc.getFieldValue(GxdResultFields.ASSAY_MGIID);
            String detectionLevel = (String) doc.getFieldValue(GxdResultFields.DETECTION_LEVEL);
            String jNum = (String) doc.getFieldValue(GxdResultFields.JNUM);
            String markerSymbol = (String) doc.getFieldValue(GxdResultFields.MARKER_SYMBOL);
            String markerMgiid = (String) doc.getFieldValue(GxdResultFields.MARKER_MGIID);
            String markerName = (String) doc.getFieldValue(GxdResultFields.MARKER_NAME);
            String printname = (String) doc.getFieldValue(GxdResultFields.STRUCTURE_PRINTNAME);
            Integer theilerStage = (Integer) doc.getFieldValue(GxdResultFields.THEILER_STAGE);
            String assayType = (String) doc.getFieldValue(GxdResultFields.ASSAY_TYPE);
            List<String> figures = (List<String>) doc.getFieldValue(GxdResultFields.FIGURE);
            String miniCitation = (String) doc.getFieldValue(GxdResultFields.SHORT_CITATION);
            String genotype = (String) doc.getFieldValue(GxdResultFields.GENOTYPE);
           // String pattern = (String) doc.getFieldValue("pattern");
            List<String> figuresPlain = (List<String>) doc.getFieldValue(GxdResultFields.FIGURE_PLAIN);
            String pubmedId = (String) doc.getFieldValue(GxdResultFields.PUBMED_ID);

            SolrAssayResult resultObject = new SolrAssayResult();
            resultObject.setAssayKey(assayKey);
            resultObject.setAssayType(assayType);
            resultObject.setAge(age);
            resultObject.setAnatomicalSystem(anatomicalSystem);
            resultObject.setAssayMgiid(assayMgiid);
            resultObject.setDetectionLevel(detectionLevel);
            resultObject.setFigures(figures);
            resultObject.setGenotype(genotype);
            resultObject.setJNum(jNum);
            resultObject.setMarkerSymbol(markerSymbol);
            resultObject.setPrintname(printname);
            resultObject.setShortCitation(miniCitation);
            resultObject.setTheilerStage(theilerStage);
            resultObject.setMarkerMgiid(markerMgiid);
            resultObject.setMarkerName(markerName);
            resultObject.setFiguresPlain(figuresPlain);
            resultObject.setPubmedId(pubmedId);

            // Add result to SearchResults
            sr.addResultObjects(resultObject);


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

        // A mapping of field -> set of highlighted words
        // for the result set.
        Map<String, Set<String>> setHighlights =
            new HashMap<String, Set<String>> ();

        // A mapping of documentKey -> Mapping of FieldName
        // -> list of highlighted words.
        Map<String, Map<String, List<String>>> highlights =
            rsp.getHighlighting();

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
        	if(gc.getName().equals(GxdResultFields.MARKER_KEY))
        	{
        		// we got ourselves a good old fashioned marker object
        		SolrGxdMarker m = new SolrGxdMarker();
        		m.setMgiid((String)sd.getFieldValue(GxdResultFields.MARKER_MGIID));
        		m.setSymbol((String)sd.getFieldValue(GxdResultFields.MARKER_SYMBOL));
        		m.setName((String)sd.getFieldValue(GxdResultFields.MARKER_NAME));
        		m.setType((String)sd.getFieldValue(GxdResultFields.MARKER_TYPE));
        		m.setChr((String)sd.getFieldValue(GxdResultFields.CHROMOSOME));
        		m.setStrand((String)sd.getFieldValue(GxdResultFields.STRAND));
        		m.setStartCoord((String)sd.getFieldValue(GxdResultFields.START_COORD));
        		m.setEndCoord((String)sd.getFieldValue(GxdResultFields.END_COORD));
        		m.setCytoband((String)sd.getFieldValue(GxdResultFields.CYTOBAND));
        		m.setCm((String)sd.getFieldValue(GxdResultFields.CENTIMORGAN));
        		sr.addResultObjects(m);
        	}
        	else if(gc.getName().equals(GxdResultFields.ASSAY_KEY))
        	{
        		// we got ourselves a good old fashioned assay object
        		SolrGxdAssay a = new SolrGxdAssay();
        		a.setMarkerSymbol((String)sd.getFieldValue(GxdResultFields.MARKER_SYMBOL));
        		a.setAssayKey((String)sd.getFieldValue(GxdResultFields.ASSAY_KEY));
        		a.setAssayMgiid((String)sd.getFieldValue(GxdResultFields.ASSAY_MGIID));
        		a.setAssayType((String)sd.getFieldValue(GxdResultFields.ASSAY_TYPE));
        		a.setJNum((String)sd.getFieldValue(GxdResultFields.JNUM));
        		a.setMiniCitation((String)sd.getFieldValue(GxdResultFields.SHORT_CITATION));
        		a.setHasImage((Boolean) sd.getFieldValue(GxdResultFields.ASSAY_HAS_IMAGE));

        		sr.addResultObjects(a);
        	}

            /**
             * In order to support older pages we pack the score directly as
             * well as in the metadata.
             */

            if (this.keyString != null) {
                keys.add(key);
                //scoreKeys.add("" + doc.getFieldValue("score"));
            }
        }

        // Include the information that was asked for.

        if (keys != null) {
            sr.setResultKeys(keys);
        }


        if (facet != null) {
            sr.setResultFacets(facet);
        }

        if (sp.includeSetMeta()) {
            sr.setResultSetMeta(new ResultSetMetaData(setHighlights));
        }

        if (sp.includeRowMeta()) {
            sr.setMetaMapping(metaList);
        }

    }

	@Value("${solr.gxd_result.url}")
	public void setSolrUrl(String solrUrl)
	{ super.solrUrl = solrUrl; }
}