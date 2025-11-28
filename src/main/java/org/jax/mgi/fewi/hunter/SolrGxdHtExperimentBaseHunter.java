package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.jax.mgi.fewi.propertyMapper.SolrPropertyMapper;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.GxdHtExperiment;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdHtExperimentBaseHunter extends SolrHunter<GxdHtExperiment> {

    /***
     * The constructor sets up this hunter so that it is specific to finding
     * data for high-throughput expression experiments.
     */
    public SolrGxdHtExperimentBaseHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         * (This index is intended as a lookup based on experiment key.)
         */
        propertyMap.put(SearchConstants.GXDHT_EXPERIMENT_KEY, new SolrPropertyMapper(GxdHtFields.EXPERIMENT_KEY));

        /* set up the sorting map */
        sortMap.put(SortConstants.BY_DEFAULT, new SolrSortMapper(GxdHtFields.BY_DEFAULT));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the experiment key.
         */
        keyString = GxdHtFields.EXPERIMENT_KEY;

    }

	@Value("${solr.gxdHtExperiment.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<GxdHtExperiment> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing gxd ht experiment data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (SolrDocument doc : sdl) {
			GxdHtExperiment experiment = new GxdHtExperiment();
			experiment.setArrayExpressID((String) doc.getFieldValue(GxdHtFields.ARRAYEXPRESS_ID));
			experiment.setByDefault((Integer) doc.getFieldValue(GxdHtFields.BY_DEFAULT));
			experiment.setDescription((String) doc.getFieldValue(GxdHtFields.DESCRIPTION));
			experiment.setExperimentKey(Integer.parseInt((String) doc.getFieldValue(GxdHtFields.EXPERIMENT_KEY)));
			experiment.setGeoID((String) doc.getFieldValue(GxdHtFields.GEO_ID));
			experiment.setMethod((String) doc.getFieldValue(GxdHtFields.METHOD));
			experiment.setNote((String) doc.getFieldValue(GxdHtFields.NOTE));
			experiment.setSampleCount((Integer) doc.getFieldValue(GxdHtFields.SAMPLE_COUNT));
			experiment.setStudyType((String) doc.getFieldValue(GxdHtFields.STUDY_TYPE));
			experiment.setTitle((String) doc.getFieldValue(GxdHtFields.TITLE));
			experiment.setIsInAtlas((Integer) doc.getFieldValue(GxdHtFields.IS_IN_ATLAS));
			experiment.setIsLoaded((Integer) doc.getFieldValue(GxdHtFields.IS_LOADED));
			
			if (doc.getFieldValues(GxdHtFields.PUBMED_IDS) != null) {
				ArrayList<String> pmIDs = new ArrayList<String>();
				for (Object o : doc.getFieldValues(GxdHtFields.PUBMED_IDS)) {
					pmIDs.add((String) o);
				}
				experiment.setPubmedIDs(pmIDs);
			}
			
			if (doc.getFieldValues(GxdHtFields.JNUM_IDS) != null) {
				ArrayList<String> jnumIDs = new ArrayList<String>();
				for (Object o : doc.getFieldValues(GxdHtFields.JNUM_IDS)) {
					jnumIDs.add((String) o);
				}
				experiment.setJnumIDs(jnumIDs);
			}
			
			List<String> variables = new ArrayList<String>();
			for (Object o : doc.getFieldValues(GxdHtFields.EXPERIMENTAL_VARIABLES)) {
				variables.add((String) o);
			}
			experiment.setExperimentalVariables(variables);

			if (doc.getFieldValues(GxdHtFields.METHODS) != null) {
				List<String> methods = new ArrayList<String>();
				for (Object o : doc.getFieldValues(GxdHtFields.METHODS)) {
					methods.add((String) o);
				}
				experiment.setMethods(methods);
			}

			sr.addResultObjects(experiment);
		}
		this.packFacetData(rsp, sr);
	}

	/*
	 * gather any facet-related data from 'rsp' and package it into 'sr'
	 */
	protected void packFacetData(QueryResponse rsp, SearchResults<GxdHtExperiment> sr) {
		if (this.facetString == null) {
			logger.debug("this.facetString = null");
			return;
		}

		logger.debug("this.facetString = " + this.facetString);
		List<String> facet = new ArrayList<String>();

		for (Count c : rsp.getFacetField(this.facetString).getValues()) {
			facet.add(c.getName());
		}

		logger.debug("facets -> "+StringUtils.join(facet,", "));
		if (facet.size() > 0) {
			sr.setResultFacets(facet);
		}
	}
}
