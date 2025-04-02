package org.jax.mgi.fewi.hunter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jax.mgi.fewi.searchUtil.entities.GxdHtSample;
import org.jax.mgi.fewi.sortMapper.SolrSortMapper;
import org.jax.mgi.shr.fe.indexconstants.GxdHtFields;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SolrGxdHtSampleHunter extends SolrHunter<GxdHtSample> {

	// mapping from experiment key to count of matching experiments
	private Map<String, Integer> experimentKeys = null;
	
	// set of sample keys that match the search criteria
	private Set<String> sampleKeys = null;
	
	private boolean gatherExperimentKeys = false;	// true to populate experimentKeys
	private boolean gatherSampleKeys = false;		// true to populate sampleKeys
	
    /***
     * The constructor sets up this hunter so that it is specific to finding
     * data for samples for high-throughput expression experiments
     */
    public SolrGxdHtSampleHunter() {

        /*
         * Setup the property map.  This maps from the properties of the incoming
         * filter list to the corresponding field names in the Solr implementation.
         */
        propertyMap.put(SearchConstants.GXDHT_STRUCTURE_SEARCH, new SolrPropertyMapper(GxdHtFields.STRUCTURE_SEARCH));
        propertyMap.put(SearchConstants.GXDHT_SEX, new SolrPropertyMapper(GxdHtFields.SEX));
        propertyMap.put(SearchConstants.GXDHT_MUTATED_GENE, new SolrPropertyMapper(GxdHtFields.MUTATED_GENE));
        propertyMap.put(SearchConstants.GXDHT_METHOD, new SolrPropertyMapper(GxdHtFields.METHOD));
        propertyMap.put(SearchConstants.GXDHT_TITLE, new SolrPropertyMapper(GxdHtFields.TITLE));
        propertyMap.put(SearchConstants.GXDHT_DESCRIPTION, new SolrPropertyMapper(GxdHtFields.DESCRIPTION));
        propertyMap.put(SearchConstants.GXDHT_TITLE_DESCRIPTION, new SolrPropertyMapper(GxdHtFields.TITLE_DESCRIPTION));
        propertyMap.put(SearchConstants.GXDHT_EXPERIMENT_ID, new SolrPropertyMapper(GxdHtFields.EXPERIMENT_ID));
        propertyMap.put(SearchConstants.GXDHT_EXPERIMENTAL_VARIABLE, new SolrPropertyMapper(GxdHtFields.EXPERIMENTAL_VARIABLES));
        propertyMap.put(SearchConstants.RNASEQ_TYPE, new SolrPropertyMapper(GxdHtFields.RNASEQ_TYPE));
        propertyMap.put(SearchConstants.REF_ID, new SolrPropertyMapper(GxdHtFields.REFERENCE_ID));
        propertyMap.put(SearchConstants.GXD_AGE_MIN, new SolrPropertyMapper(GxdHtFields.AGE_MIN));
        propertyMap.put(SearchConstants.GXD_AGE_MAX, new SolrPropertyMapper(GxdHtFields.AGE_MAX));
        propertyMap.put(SearchConstants.GXDHT_RELEVANCY, new SolrPropertyMapper(GxdHtFields.RELEVANCY));

        /* set up the sorting map */
        sortMap.put(SortConstants.BY_DEFAULT, new SolrSortMapper(GxdHtFields.BY_DEFAULT));
        
        /*
         * The name of the field we want to iterate through the documents for
         * and place into the output.  In this case we want the sample key.
         */
        keyString = GxdHtFields.SAMPLE_KEY;

    }

	@Value("${solr.gxdHtSample.url}")
	public void setSolrUrl(String solrUrl) {
		super.solrUrl = solrUrl;
	}

	@Override
	protected void packInformation(QueryResponse rsp, SearchResults<GxdHtSample> sr, SearchParams sp) {

		// A list of all the primary keys in the document

		SolrDocumentList sdl = rsp.getResults();
		logger.debug("packing gxd ht sample data");

		/**
		 * Iterate through the response documents, extracting the information
		 * that was configured at the implementing class level.
		 */

		for (SolrDocument doc : sdl) {
			GxdHtSample sample = new GxdHtSample();
			sample.setAge((String) doc.getFieldValue(GxdHtFields.AGE));
			sample.setByDefault((Integer) doc.getFieldValue(GxdHtFields.BY_DEFAULT));
			sample.setExperimentKey(Integer.parseInt((String) doc.getFieldValue(GxdHtFields.EXPERIMENT_KEY)));
			sample.setGeneticBackground((String) doc.getFieldValue(GxdHtFields.GENETIC_BACKGROUND));
			sample.setMutantAlleles((String) doc.getFieldValue(GxdHtFields.MUTANT_ALLELES));
			sample.setName((String) doc.getFieldValue(GxdHtFields.NAME));
			sample.setRnaseqType((String) doc.getFieldValue(GxdHtFields.RNASEQ_TYPE));
			sample.setNote((String) doc.getFieldValue(GxdHtFields.NOTE));
			sample.setOrganism((String) doc.getFieldValue(GxdHtFields.ORGANISM));
			sample.setSampleKey(Integer.parseInt((String) doc.getFieldValue(GxdHtFields.SAMPLE_KEY)));
			sample.setSex((String) doc.getFieldValue(GxdHtFields.SEX));
			sample.setStructureID((String) doc.getFieldValue(GxdHtFields.STRUCTURE_ID));
			sample.setStructureTerm((String) doc.getFieldValue(GxdHtFields.STRUCTURE_TERM));
			sample.setTheilerStage((Integer) doc.getFieldValue(GxdHtFields.THEILER_STAGE));
			sample.setCelltypeID((String) doc.getFieldValue(GxdHtFields.CELLTYPE_ID));
			sample.setCelltypeTerm((String) doc.getFieldValue(GxdHtFields.CELLTYPE_TERM));
			sample.setRelevancy((String) doc.getFieldValue(GxdHtFields.RELEVANCY));
			sr.addResultObjects(sample);
		}
		this.packFacetData(rsp, sr);
	}

	/* build and return a Set of sample keys that match the search parameters
	 */
	public Set<String> getSampleKeys(SearchParams params) {
		this.sampleKeys = new HashSet<String>();
		
		/* We need to use the set of search parameters to query Solr and collect the sample keys
		 * as facets (to avoid having to return whole documents).
		 */
		this.gatherSampleKeys = true;
		
		// preserve hunter's previous state
		String previousFacetString = this.facetString;
		Integer previousFacetLimit = this.factetNumberDefault;
		Integer resultLimit = this.resultsDefault;
		
		// update for this particular facet search
		this.facetString = SearchConstants.GXDHT_SAMPLE_KEY;
		this.resultsDefault = 0;
		this.factetNumberDefault = -1;		// no limit
		
		// do the search and process the results
		SearchResults<GxdHtSample> searchResults = new SearchResults<GxdHtSample>();
		this.hunt(params, searchResults);
		
		// restore hunter's previous state
		this.facetString = previousFacetString;
		this.factetNumberDefault = previousFacetLimit;
		this.resultsDefault = resultLimit;
		
		this.gatherSampleKeys = false;
		return this.sampleKeys;
	}
	
	/* build and return a map of experiment keys and their matching sample counts
	 */
	public Map<String, Integer> getExperimentMap(SearchParams params) {
		this.experimentKeys = new HashMap<String, Integer>();
		
		/* We need to use the set of search parameters to query Solr and collect the experiment keys
		 * as facets.  The count of matching samples for each is the count for each facet.  We do not
		 * need to return any sample records for this search, only the facets.
		 */
		this.gatherExperimentKeys = true;
		
		// preserve hunter's previous state
		String previousFacetString = this.facetString;
		Integer previousFacetLimit = this.factetNumberDefault;
		Integer resultLimit = this.resultsDefault;
		
		// update for this particular facet search
		this.facetString = SearchConstants.GXDHT_EXPERIMENT_KEY;
		this.resultsDefault = 0;
		this.factetNumberDefault = -1;		// no limit
		
		// do the search and process the results
		SearchResults<GxdHtSample> searchResults = new SearchResults<GxdHtSample>();
		this.hunt(params, searchResults);
		
		// restore hunter's previous state
		this.facetString = previousFacetString;
		this.factetNumberDefault = previousFacetLimit;
		this.resultsDefault = resultLimit;
		
		this.gatherExperimentKeys = false;
		return this.experimentKeys;
	}
	
		
	/*
	 * gather any facet-related data from 'rsp' and package it into 'sr'
	 */
	protected void packFacetData(QueryResponse rsp, SearchResults<GxdHtSample> sr) {
		if (this.facetString == null) {
			return;
		}

		logger.debug("this.facetString = " + this.facetString);
		List<String> facet = new ArrayList<String>();			// traditional value-only facet

		for (Count c : rsp.getFacetField(this.facetString).getValues()) {
			facet.add(c.getName());

			if (this.gatherExperimentKeys) {
				this.experimentKeys.put(c.getName(), (int) c.getCount());		// value + count facet
			} else if (this.gatherSampleKeys) {
				this.sampleKeys.add(c.getName());
			}
		}

		logger.debug("facets -> "+StringUtils.join(facet,", "));
		if (facet.size() > 0) {
			sr.setResultFacets(facet);
		}
	}
}
