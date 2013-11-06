package org.jax.mgi.fewi.finder;

import java.util.*;

import mgi.frontend.datamodel.*;

import org.jax.mgi.fewi.hunter.SolrDiseasePortalHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGenoInResult;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

/*
 * Centered around retrieving Human Disease Portal data in various forms
 */
@Repository
public class DiseasePortalFinder
{
	private Logger logger = LoggerFactory.getLogger(DiseasePortalFinder.class);

	@Autowired
	private SolrDiseasePortalHunter hdpHunter;

	@Autowired
	private HibernateObjectGatherer<HdpGenoCluster> genoCGatherer;

    //////////////////////////////////////////////////////////////////////////
	// public data object/count gathering
    //////////////////////////////////////////////////////////////////////////

	// Group results by marker key to return the distinct matching marker keys
	public List<String> getMarkerKeys(SearchParams params)
	{
		SearchResults<SolrDiseasePortalMarker> results = this.huntMarkersGroup(params,true);

		return results.getResultKeys();
	}

	// Group results by marker to return the distinct matching markers
	public SearchResults<SolrDiseasePortalMarker> getMarkers(SearchParams params)
	{
		SearchResults<SolrDiseasePortalMarker> results = huntMarkersGroup(params);
		return results;
	}

	// Group results by grid clusters
	public SearchResults<SolrDpGridCluster> getGridClusters(SearchParams params)
	{
		SearchResults<SolrDpGridCluster> results = huntGridClustersGroup(params);
		return results;
	}

	// Group results by geno clusters
	public SearchResults<HdpGenoCluster> getGenoClusters(SearchParams params)
	{
		SearchResults<HdpGenoCluster> results = huntGenoClustersGroup(params);
		return results;
	}

	// Group results by term id to return the distinct matching diseases (but only diseases that would appear on Grid)
	public SearchResults<String> getGridDiseases(SearchParams params)
	{
		SearchResults<String> results = huntGridDiseasesGroup(params);
		return results;
	}

	// Group results by term id to return the distinct matching diseases
	public SearchResults<SolrVocTerm> getDiseases(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = huntDiseasesGroup(params);
		return results;
	}

	// Group results by term id to return the distinct matching phenotypes
	public SearchResults<SolrVocTerm> getPhenotypes(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = huntPhenotypesGroup(params);
		return results;
	}

	// annotation results for given request - via join
	public SearchResults<SolrDpGenoInResult> searchAnnotationsInGridResults(SearchParams params) 
	{
		SearchResults<SolrDpGenoInResult> results = new SearchResults<SolrDpGenoInResult>();
		
		// need to ensure that only docs come back that have a grid cluster
		Filter originalFilter = params.getFilter();
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
		);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		
		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationHeaders");
		
		// need to also include the disease->human marker annotations somehow
		modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD),
					new Filter(DiseasePortalFields.ORGANISM,"human",Filter.OP_EQUAL),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.OP_EQUAL)
				)
		);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationByMarker");
		
		return results;
	}
	
	public SearchResults<SolrDpGenoInResult> searchAnnotationsInPopupResults(SearchParams params) 
	{
		SearchResults<SolrDpGenoInResult> results = new SearchResults<SolrDpGenoInResult>();
		
		// need to ensure that only docs come back that have a grid cluster
		Filter originalFilter = params.getFilter();
//		Filter modifiedFilter = Filter.and(
//				Arrays.asList(originalFilter,
//					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
//				)
//		);
//		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
//		params.setFilter(modifiedFilter);
//		
//		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationHeaders");
		
		// need to also include the disease->human marker annotations somehow
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD),
					new Filter(DiseasePortalFields.ORGANISM,"human",Filter.OP_EQUAL),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.OP_EQUAL)
				)
		);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationByMarkerTerm");
		
		return results;
	}

	// ---- counts ----
	public Integer getGridClusterCount(SearchParams params)
	{
		SearchResults<SolrDpGridCluster> results = huntGridClustersGroup(params);
		return results.getTotalCount();
	}
	public Integer getMarkerCount(SearchParams params)
	{
		SearchResults<SolrDiseasePortalMarker> results = huntMarkersGroup(params);
		return results.getTotalCount();
	}
	public Integer getDiseaseCount(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = huntDiseasesGroup(params);
		return results.getTotalCount();
	}


    //////////////////////////////////////////////////////////////////////////
	// specific grouping functions to encapsulate the logic involved
    //////////////////////////////////////////////////////////////////////////

	// grid clusters
	private SearchResults<SolrDpGridCluster> huntGridClustersGroup(SearchParams params)
	{
		SearchResults<SolrDpGridCluster> results = new SearchResults<SolrDpGridCluster>();

		// make sure that only documents with grid cluster keys are included in the
		// group (otherwise you get a weird off-by-one error in the count)
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
		);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.DP_GRID_CLUSTER_KEY);

		return results;
	}

	// geno clusters
	private SearchResults<HdpGenoCluster> huntGenoClustersGroup(SearchParams params)
	{
		SearchResults<HdpGenoCluster> results = new SearchResults<HdpGenoCluster>();

		// make sure that only documents with geno cluster keys are included in the
		// group (otherwise you get a weird off-by-one error in the count)
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.DP_GENO_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
		);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.DP_GENO_CLUSTER_KEY);

		//hdpHunter.joinHunt(params, results, "diseasePortalAnnotationHeaders");
		
        logger.debug("->keys from hunter - " + results.getResultKeys());

        // gather objects identified by the hunter, add them to the results
        List<HdpGenoCluster> genoClusters = genoCGatherer.get( HdpGenoCluster.class, results.getResultKeys() );
        results.setResultObjects(genoClusters);

		return results;
	}

	// markers
	private SearchResults<SolrDiseasePortalMarker> huntMarkersGroup(SearchParams params)
	{ return huntMarkersGroup(params,false); }
	private SearchResults<SolrDiseasePortalMarker> huntMarkersGroup(SearchParams params,boolean justKeys)
	{
		SearchResults<SolrDiseasePortalMarker> results
		  = new SearchResults<SolrDiseasePortalMarker>();

		// make sure that only documents with marker keys are included in the
		// group (otherwise you get weird off-by-one error in the count) also
		// make sure that "complex" genotypes (as defined in TR11423 special
		// rules) are excluded from genes list
		Filter originalFilter = params.getFilter();
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.MRK_KEY,"[* TO *]",Filter.OP_HAS_WORD),
					new Filter(SearchConstants.GENOTYPE_TYPE,"complex",Filter.OP_NOT_EQUAL),
					new Filter(DiseasePortalFields.TERM_QUALIFIER,"normal",Filter.OP_NOT_EQUAL),
					new Filter(DiseasePortalFields.TERM_QUALIFIER,"not",Filter.OP_NOT_EQUAL)
				)
			);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		if(justKeys) hdpHunter.hunt(params, results,"bareMarkerKey");
		else hdpHunter.hunt(params, results,SearchConstants.MRK_KEY);
		params.setFilter(originalFilter);
		return results;
	}

	// diseases
	private SearchResults<SolrVocTerm> huntDiseasesGroup(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = new SearchResults<SolrVocTerm>();
		// make sure that only documents with OMIM type are included in the group
		Filter originalFilter = params.getFilter();
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.OP_EQUAL),
					new Filter(DiseasePortalFields.TERM_QUALIFIER,"not",Filter.OP_NOT_EQUAL)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_DISEASE);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		params.setFilter(originalFilter);
		return results;
	}
	// get distinct list of diseases for the grid
	private SearchResults<String> huntGridDiseasesGroup(SearchParams params)
	{
		SearchResults<String> results = new SearchResults<String>();
		// make sure that only documents with OMIM type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,DiseasePortalFields.TERM_HEADER);
		return results;
	}

	// get distinct list of mp headers for the grid
	public SearchResults<String> huntGridMPHeadersGroup(SearchParams params)
	{
		SearchResults<String> results = new SearchResults<String>();
		// make sure that only documents with MP type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"Mammalian Phenotype",Filter.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,DiseasePortalFields.TERM_HEADER);
		return results;
	}

	// get distinct list of mp terms for the grid drill down
	public SearchResults<SolrVocTerm> huntGridMPTermsGroup(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = new SearchResults<SolrVocTerm>();
		// make sure that only documents with MP type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"Mammalian Phenotype",Filter.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		return results;
	}
	
	// get distinct list of disease terms for the grid drill down
	public SearchResults<SolrVocTerm> huntGridDiseaseTermsGroup(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = new SearchResults<SolrVocTerm>();
		// make sure that only documents with Disease type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		return results;
	}

	// get distinct list of human markers for the grid drill down
	public SearchResults<SolrDiseasePortalMarker> huntGridHumanMarkerGroup(SearchParams params)
	{
		SearchResults<SolrDiseasePortalMarker> results = new SearchResults<SolrDiseasePortalMarker>();
		// make sure that only documents with human organism type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(DiseasePortalFields.ORGANISM,"human",Filter.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.MRK_KEY);
		return results;
	}


	// phenotypes
	private SearchResults<SolrVocTerm> huntPhenotypesGroup(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = new SearchResults<SolrVocTerm>();
		// make sure that only documents with MP type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
						new Filter(SearchConstants.VOC_TERM_TYPE,"Mammalian Phenotype",Filter.OP_EQUAL)
					)
				);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		return results;
	}

	// -------- Hibernate accessors ----------
	public List<HdpGenoCluster> getGenoClusterByKey(String genoClusterKey) 
	{
		return genoCGatherer.get(HdpGenoCluster.class,Arrays.asList(genoClusterKey));
	}
}
