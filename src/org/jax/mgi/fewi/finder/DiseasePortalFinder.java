package org.jax.mgi.fewi.finder;

import java.util.Arrays;
import java.util.List;

import mgi.frontend.datamodel.hdp.HdpGenoCluster;

import org.jax.mgi.fewi.hunter.SolrDiseasePortalHunter;
import org.jax.mgi.fewi.hunter.SolrDiseasePortalFeatureTypeFacetHunter;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridData;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.searchUtil.entities.group.SolrHdpEntity;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * Centered around retrieving Human Disease Portal data in various forms
 */
@Repository
public class DiseasePortalFinder
{
	private final Logger logger = LoggerFactory.getLogger(DiseasePortalFinder.class);

	@Autowired
	private SolrDiseasePortalHunter hdpHunter;

	@Autowired
	private SolrDiseasePortalFeatureTypeFacetHunter featureTypeFacetHunter;

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
	public SearchResults<SolrHdpGridCluster> getGridClusters(SearchParams params)
	{
		SearchResults<SolrHdpGridCluster> results = huntGridClustersGroup(params);
		return results;
	}

	// Group results by geno clusters
	public SearchResults<HdpGenoCluster> getGenoClusters(SearchParams params)
	{
		SearchResults<HdpGenoCluster> results = huntGenoClustersGroup(params);
		return results;
	}
	
	// Get Header terms for lighted columns
	public SearchResults<SolrString> getHighlightedColumns(SearchParams params) {
		SearchResults<SolrString> results = huntHighlightedColumns(params);
		return results;
	}
	
	// Get Header terms for lighted columns
	public SearchResults<SolrString> getHighlightedColumnHeaders(SearchParams params) {
		SearchResults<SolrString> results = huntHighlightedColumnHeaders(params);
		return results;
	}

	// Group results by term id to return the distinct matching diseases (but only diseases that would appear on Grid)
	public SearchResults<SolrString> getGridDiseases(SearchParams params)
	{
		SearchResults<SolrString> results = huntGridDiseasesGroup(params);
		return results;
	}

	// Group results by term id to return the distinct matching diseases
	public SearchResults<SolrVocTerm> getDiseases(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = huntDiseasesGroup(params);
		return results;
	}
	public List<SolrVocTerm> getDiseaseByID(String diseaseId)
	{
		SearchParams params = new SearchParams();
		params.setPageSize(1);
		params.setFilter(new Filter(SearchConstants.VOC_TERM_ID, diseaseId,Filter.Operator.OP_EQUAL));
		
		return getDiseases(params).getResultObjects();
	}

	// Group results by term id to return the distinct matching phenotypes
	public SearchResults<SolrVocTerm> getPhenotypes(SearchParams params)
	{
		SearchResults<SolrVocTerm> results = huntPhenotypesGroup(params);
		return results;
	}

	// annotation results for given request - via join
	public SearchResults<SolrHdpGridData> searchAnnotationsInGridResults(SearchParams params) 
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		
		// need to ensure that only docs come back that have a grid cluster
		Filter originalFilter = params.getFilter();
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
		);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		
		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationHeaders");
		
		// need to also include the disease->human marker annotations somehow
		modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD),
					new Filter(DiseasePortalFields.ORGANISM,"human",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.Operator.OP_EQUAL)
				)
		);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationByMarker");
		
		SearchResults<SolrHdpGridData> srGD = new SearchResults<SolrHdpGridData>();
		srGD.cloneFrom(results,SolrHdpGridData.class);
		
		return srGD;
	}
	
	public SearchResults<SolrHdpGridData> searchAnnotationsInPopupResults(SearchParams params) 
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		
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
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD),
					new Filter(DiseasePortalFields.ORGANISM,"human",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.Operator.OP_EQUAL)
				)
		);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		hdpHunter.joinHunt(params, results, "diseasePortalAnnotationByMarkerTerm");
		
		SearchResults<SolrHdpGridData> srGD = new SearchResults<SolrHdpGridData>();
		srGD.cloneFrom(results,SolrHdpGridData.class);
		
		return srGD;
	}

	// ---- counts ----
	public Integer getGridClusterCount(SearchParams params)
	{
		SearchResults<SolrHdpGridCluster> results = huntGridClustersGroup(params);
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
	private SearchResults<SolrHdpGridCluster> huntGridClustersGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();

		// make sure that only documents with grid cluster keys are included in the
		// group (otherwise you get a weird off-by-one error in the count)
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
		);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.DP_GRID_CLUSTER_KEY);

		SearchResults<SolrHdpGridCluster> srGC = new SearchResults<SolrHdpGridCluster>();
		srGC.cloneFrom(results,SolrHdpGridCluster.class);
		return srGC;
	}

	// geno clusters
	private SearchResults<HdpGenoCluster> huntGenoClustersGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();

		// make sure that only documents with geno cluster keys are included in the
		// group (otherwise you get a weird off-by-one error in the count)
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.DP_GENO_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
		);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.DP_GENO_CLUSTER_KEY);

		//hdpHunter.joinHunt(params, results, "diseasePortalAnnotationHeaders");
		
        logger.debug("->keys from hunter - " + results.getResultKeys());
        SearchResults<HdpGenoCluster> srGC = new SearchResults<HdpGenoCluster>();
        srGC.cloneFrom(results);

        // gather objects identified by the hunter, add them to the results
        List<HdpGenoCluster> genoClusters = genoCGatherer.get( HdpGenoCluster.class, srGC.getResultKeys() );
        srGC.setResultObjects(genoClusters);

		return srGC;
	}

	// markers
	private SearchResults<SolrDiseasePortalMarker> huntMarkersGroup(SearchParams params)
	{ return huntMarkersGroup(params,false); }
	private SearchResults<SolrDiseasePortalMarker> huntMarkersGroup(SearchParams params,boolean justKeys)
	{
		SearchResults<SolrHdpEntity> results
		  = new SearchResults<SolrHdpEntity>();

		// make sure that only documents with marker keys are included in the
		// group (otherwise you get weird off-by-one error in the count) also
		// make sure that "complex" genotypes (as defined in TR11423 special
		// rules) are excluded from genes list
		Filter originalFilter = params.getFilter();
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.MRK_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD),
					new Filter(SearchConstants.GENOTYPE_TYPE,"complex",Filter.Operator.OP_NOT_EQUAL),
					new Filter(DiseasePortalFields.TERM_QUALIFIER,"normal",Filter.Operator.OP_NOT_EQUAL),
					new Filter(DiseasePortalFields.TERM_QUALIFIER,"not",Filter.Operator.OP_NOT_EQUAL)
				)
			);
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);
		if(justKeys) hdpHunter.hunt(params, results,"bareMarkerKey");
		else hdpHunter.hunt(params, results,SearchConstants.MRK_KEY);
		params.setFilter(originalFilter);
		
		SearchResults<SolrDiseasePortalMarker> srM = new SearchResults<SolrDiseasePortalMarker>();
		srM.cloneFrom(results,SolrDiseasePortalMarker.class);
		
		return srM;
	}

	// diseases
	private SearchResults<SolrVocTerm> huntDiseasesGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with OMIM type are included in the group
		Filter originalFilter = params.getFilter();
		Filter modifiedFilter = Filter.and(
				Arrays.asList(originalFilter,
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.Operator.OP_EQUAL),
					new Filter(DiseasePortalFields.TERM_QUALIFIER,"not",Filter.Operator.OP_NOT_EQUAL)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_DISEASE);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		params.setFilter(originalFilter);
		
		SearchResults<SolrVocTerm> srVT = new SearchResults<SolrVocTerm>();
		srVT.cloneFrom(results,SolrVocTerm.class);
		return srVT;
	}

	private SearchResults<SolrString> huntHighlightedColumns(SearchParams params) {
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();

		params.getFilter().replaceProperty(DiseasePortalFields.TERM, DiseasePortalFields.TERM_SEARCH);
		
		hdpHunter.hunt(params, results, DiseasePortalFields.TERM_GROUP);
		
		SearchResults<SolrString> srS = new SearchResults<SolrString>();
		srS.cloneFrom(results,SolrString.class);
		
//		for(String s: srS.getResultKeys()) {
//			logger.info("huntHighlightedColumns: Solr String: " + s);
//		}
		return srS;
	}
	
	private SearchResults<SolrString> huntHighlightedColumnHeaders(SearchParams params) {
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();

		params.getFilter().replaceProperty(DiseasePortalFields.TERM, DiseasePortalFields.TERM_SEARCH);

		hdpHunter.hunt(params, results, DiseasePortalFields.TERM_HEADER);

		SearchResults<SolrString> srS = new SearchResults<SolrString>();
		srS.cloneFrom(results,SolrString.class);
//		for(String s: srS.getResultKeys()) {
//			logger.info("huntHighlightedColumnHeaders: Solr String: " + s);
//		}
		return srS;
	}
	
	// get distinct list of diseases for the grid
	private SearchResults<SolrString> huntGridDiseasesGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with OMIM type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,DiseasePortalFields.TERM_HEADER);
		
		SearchResults<SolrString> srS = new SearchResults<SolrString>();
		srS.cloneFrom(results,SolrString.class);
		return srS;
	}

	// get distinct list of mp headers for the grid
	public SearchResults<SolrString> huntGridMPHeadersGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with MP type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"Mammalian Phenotype",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,DiseasePortalFields.TERM_HEADER);
		
		SearchResults<SolrString> srS = new SearchResults<SolrString>();
		srS.cloneFrom(results,SolrString.class);
		
		return srS;
	}

	// get distinct list of mp terms for the grid drill down
	public SearchResults<SolrVocTerm> huntGridMPTermsGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with MP type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"Mammalian Phenotype",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		
		SearchResults<SolrVocTerm> srVT = new SearchResults<SolrVocTerm>();
		srVT.cloneFrom(results,SolrVocTerm.class);
		return srVT;
	}
	
	// get distinct list of disease terms for the grid drill down
	public SearchResults<SolrVocTerm> huntGridDiseaseTermsGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with Disease type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(SearchConstants.VOC_TERM_TYPE,"OMIM",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		
		SearchResults<SolrVocTerm> srVT = new SearchResults<SolrVocTerm>();
		srVT.cloneFrom(results,SolrVocTerm.class);
		return srVT;
	}

	// get distinct list of human markers for the grid drill down
	public SearchResults<SolrDiseasePortalMarker> huntGridHumanMarkerGroup(SearchParams params)
	{
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with human organism type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
					new Filter(DiseasePortalFields.ORGANISM,"human",Filter.Operator.OP_EQUAL),
					new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD)
				)
			);
		// replace the term search property with a disease specific one that includes mp terms that map to diseases
		modifiedFilter.replaceProperty(SearchConstants.VOC_TERM,DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.MRK_KEY);
		
		SearchResults<SolrDiseasePortalMarker> srM = new SearchResults<SolrDiseasePortalMarker>();
		srM.cloneFrom(results,SolrDiseasePortalMarker.class);
		return srM;
	}


	// phenotypes
	private SearchResults<SolrVocTerm> huntPhenotypesGroup(SearchParams params)
	{		
		SearchResults<SolrHdpEntity> results = new SearchResults<SolrHdpEntity>();
		// make sure that only documents with MP type are included in the group
		Filter modifiedFilter = Filter.and(
				Arrays.asList(params.getFilter(),
						new Filter(SearchConstants.VOC_TERM_TYPE,"Mammalian Phenotype",Filter.Operator.OP_EQUAL)
					)
				);
		params.setFilter(modifiedFilter);

		hdpHunter.hunt(params, results,SearchConstants.VOC_TERM_ID);
		
		SearchResults<SolrVocTerm> srVT = new SearchResults<SolrVocTerm>();
		srVT.cloneFrom(results,SolrVocTerm.class);
		return srVT;
	}

	//----------------------------------------
	//--- facet hunters (for filters) --------
	//----------------------------------------

	/* get the feature types for markers matching the search params
	 */
	public SearchResults<SolrString> getFeatureTypeFacet(
		SearchParams params) {
	    SearchResults<SolrHdpEntity> results =
		new SearchResults<SolrHdpEntity>();
	    featureTypeFacetHunter.hunt(params, results);
	    SearchResults<SolrString> srss = new SearchResults<SolrString>();
	    srss.cloneFrom(results, SolrString.class);
	    return srss;
	}

	// -------- Hibernate accessors ----------
	public List<HdpGenoCluster> getGenoClusterByKey(String genoClusterKey) 
	{
		return genoCGatherer.get(HdpGenoCluster.class,Arrays.asList(genoClusterKey));
	}

}
