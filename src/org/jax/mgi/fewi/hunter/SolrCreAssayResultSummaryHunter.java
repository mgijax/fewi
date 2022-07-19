package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mgi.frontend.datamodel.group.RecombinaseEntity;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.MetaData;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrCreSystemHighlight;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.shr.fe.indexconstants.CreFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrCreAssayResultSummaryHunter extends SolrHunter<RecombinaseEntity> {

    /***
     * The constructor sets up this hunter so that it is specific to cre
     * summary pages.  Each item in the constructor sets a value that it has
     * inherited from its superclass, and then relies on the superclass to
     * perform all of the needed work via the hunt() method.
     */
    public SolrCreAssayResultSummaryHunter() {


        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         *
         */
        propertyMap.put(SearchConstants.ALL_DRIVER, new SolrPropertyMapper(CreFields.DRIVER_SEARCH));
        propertyMap.put(SearchConstants.ALL_SYSTEM, new SolrPropertyMapper(CreFields.SYSTEM));


        // structure
        propertyMap.put(SearchConstants.CRE_STRUCTURE,
          		new SolrPropertyMapper(CreFields.STRUCTURE_SEARCH));
        propertyMap.put(SearchConstants.CRE_DETECTED,
          		new SolrPropertyMapper(CreFields.DETECTED));
        propertyMap.put(SearchConstants.CRE_EXCLUSIVE_STRUCTURES,
          		new SolrPropertyMapper(CreFields.ALL_EXCLUSIVE_STRUCTURES));

        // structure key
        propertyMap.put(SearchConstants.STRUCTURE_KEY,
          		new SolrPropertyMapper(CreFields.ANNOTATED_STRUCTURE_KEY));

        // structure ID
        propertyMap.put(SearchConstants.STRUCTURE_ID,
          		new SolrPropertyMapper(CreFields.STRUCTURE_ID));

        // inducer
        propertyMap.put(SearchConstants.CRE_INDUCER,
          		new SolrPropertyMapper(CreFields.INDUCER));

        // detected in system
        propertyMap.put(SearchConstants.CRE_SYSTEM_DETECTED,
          		new SolrPropertyMapper(CreFields.ALL_SYSTEM_DETECTED));

        // not detected in system
        propertyMap.put(SearchConstants.CRE_SYSTEM_NOT_DETECTED,
          		new SolrPropertyMapper(CreFields.ALL_SYSTEM_NOT_DETECTED));

        /*
         * Setup the sort mapping.
         */
        // Allele level sorts
        sortMap.put(SortConstants.CRE_DRIVER, new SolrSortMapper(CreFields.DRIVER_SORT));
        sortMap.put(SortConstants.CRE_SYMBOL, new SolrSortMapper(CreFields.ALL_SYMBOL_SORT));
        sortMap.put(SortConstants.CRE_TYPE, new SolrSortMapper(CreFields.ALL_TYPE_SORT));
        sortMap.put(SortConstants.CRE_INDUCIBLE, new SolrSortMapper(CreFields.INDUCER));
        sortMap.put(SortConstants.CRE_REF_COUNT, new SolrSortMapper(CreFields.ALL_REFERENCE_COUNT_SORT));

        // result level sorts
        sortMap.put(SortConstants.CRE_BY_STRUCTURE, new SolrSortMapper(CreFields.BY_STRUCTURE));
        sortMap.put(SortConstants.CRE_BY_AGE, new SolrSortMapper(CreFields.BY_AGE));
        sortMap.put(SortConstants.CRE_BY_LEVEL, new SolrSortMapper(CreFields.BY_LEVEL));
        sortMap.put(SortConstants.CRE_BY_PATTERN, new SolrSortMapper(CreFields.BY_PATTERN));
        sortMap.put(SortConstants.CRE_BY_JNUM_ID, new SolrSortMapper(CreFields.BY_JNUM_ID));
        sortMap.put(SortConstants.CRE_BY_ASSAY_TYPE, new SolrSortMapper(CreFields.BY_ASSAY_TYPE));
        sortMap.put(SortConstants.CRE_BY_REPORTER_GENE, new SolrSortMapper(CreFields.BY_REPORTER_GENE));
        sortMap.put(SortConstants.CRE_BY_DETECTION_METHOD, new SolrSortMapper(CreFields.BY_DETECTION_METHOD));
        sortMap.put(SortConstants.CRE_BY_ASSAY_NOTE, new SolrSortMapper(CreFields.BY_ASSAY_NOTE));
        sortMap.put(SortConstants.CRE_BY_ALLELIC_COMPOSITION, new SolrSortMapper(CreFields.BY_ALLELIC_COMPOSITION));
        sortMap.put(SortConstants.CRE_BY_SEX, new SolrSortMapper(CreFields.BY_SEX));
        sortMap.put(SortConstants.CRE_BY_SPECIMEN_NOTE, new SolrSortMapper(CreFields.BY_SPECIMEN_NOTE));
        sortMap.put(SortConstants.CRE_BY_RESULT_NOTE, new SolrSortMapper(CreFields.BY_RESULT_NOTE));
        sortMap.put(SortConstants.CRE_BY_DETECTED, new SolrSortMapper(CreFields.BY_DETECTED));
        sortMap.put(SortConstants.CRE_BY_NOT_DETECTED, new SolrSortMapper(CreFields.BY_NOT_DETECTED));

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         *
         */

        propertyMap.put(SearchConstants.ALL_ID, new SolrPropertyMapper(IndexConstants.ALL_ID));
        propertyMap.put(SearchConstants.CRE_SYSTEM_KEY, new SolrPropertyMapper(CreFields.ALL_SYSTEM_KEY));


        // group fields
        this.groupFields.put(SearchConstants.ALL_KEY,
				IndexConstants.ALL_KEY);

        this.groupFields.put(SearchConstants.CRE_SYSTEM_HL_GROUP,
				CreFields.SYSTEM_HL_GROUP);


        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we are looking for the
         * standard list of keys to be returned.
         */

        keyString = CreFields.ASSAY_RESULT_KEY;

    }


    @SuppressWarnings("unchecked")
	@Override
	protected void packInformationByGroup(QueryResponse rsp,
			SearchResults<RecombinaseEntity> sr, SearchParams sp) {

		GroupResponse gr = rsp.getGroupResponse();
		// get the group command. In our case, there should only be one.
		GroupCommand gc = gr.getValues().get(0);

		// total count of groups
		int groupCount = gc.getNGroups();
		sr.setTotalCount(groupCount);

		// A list of all the primary keys in the document
		List<String> keys = new ArrayList<String>();

		// A mapping of documentKey -> Row level Metadata objects.
		Map<String, MetaData> metaList = new HashMap<String, MetaData>();

		List<Group> groups = gc.getValues();
		logger.debug("packing information for group: " + gc.getName());

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (Group g : groups) {
			String key = g.getGroupValue();
			// int numFound = (int) g.getResult().getNumFound();
			// get the top document of the group
			SolrDocument sd = g.getResult().get(0);
			if (gc.getName().equals(IndexConstants.ALL_KEY)) {
				// Allele group
				//String alleleKey = (String) sd.getFieldValue(IndexConstants.ALL_KEY);

				//sr.addResultObjects();
			}
			else if (gc.getName().equals(CreFields.SYSTEM_HL_GROUP)) {
				// System Highlight group

				String alleleKey = (String) sd.getFieldValue(IndexConstants.ALL_KEY);
				List<String> systems = (List<String>) sd.getFieldValue(CreFields.SYSTEM);
				if (systems == null) systems = new ArrayList<String>();
				Boolean detected = (Boolean) sd.getFieldValue(CreFields.DETECTED);

				SolrCreSystemHighlight systemHighlight = new SolrCreSystemHighlight();
				systemHighlight.setAlleleKey(alleleKey);
				systemHighlight.setSystems(systems);
				systemHighlight.setDetected(detected);

				sr.addResultObjects(systemHighlight);
			}

			/**
			 * In order to support older pages we pack the score directly as
			 * well as in the metadata.
			 */

			if (this.keyString != null) {
				keys.add(key);
				// scoreKeys.add("" + doc.getFieldValue("score"));
			}
		}

		// Include the information that was asked for.

		if (keys != null) {
			sr.setResultKeys(keys);
		}

		if (sp.includeRowMeta()) {
			sr.setMetaMapping(metaList);
		}

		this.packFacetData(rsp, sr);

	}

	/* gather and facet-related data from 'rsp' and package it into 'sr'
	 */
	private void packFacetData (QueryResponse rsp, SearchResults<RecombinaseEntity> sr) {
		if (this.facetString == null) { return; }

		logger.debug("this.facetString = " + this.facetString);
		List<String> facet = new ArrayList<String>();

		for (Count c : rsp.getFacetField(this.facetString).getValues()) {
			//logger.debug("facet = " + c.getName());
			facet.add(c.getName());
		}

		if (facet.size() > 0) {
			sr.setResultFacets(facet);
		}
	}

	public void setFacet(String facet) {
		facetString = facet;
	}

	@Value("${solr.creAssayResult.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

}
