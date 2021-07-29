package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jax.mgi.fewi.hunter.SolrQSVocabResultHunter;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.hunter.SolrQSAlleleResultFacetHunter;
import org.jax.mgi.fewi.hunter.SolrQSAlleleResultHunter;
import org.jax.mgi.fewi.hunter.SolrQSFeatureResultFacetHunter;
import org.jax.mgi.fewi.hunter.SolrQSFeatureResultTinyHunter;
import org.jax.mgi.fewi.hunter.SolrQSLookupHunter;
import org.jax.mgi.fewi.hunter.SolrQSOtherResultFacetHunter;
import org.jax.mgi.fewi.hunter.SolrQSOtherResultHunter;
import org.jax.mgi.fewi.hunter.SolrQSStrainResultFacetHunter;
import org.jax.mgi.fewi.hunter.SolrQSStrainResultHunter;
import org.jax.mgi.fewi.hunter.SolrQSVocabResultFacetHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.jax.mgi.fewi.summary.QSAlleleResult;
import org.jax.mgi.fewi.summary.QSFeaturePart;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.summary.QSOtherResult;
import org.jax.mgi.fewi.summary.QSStrainResult;
import org.jax.mgi.fewi.summary.QSVocabResult;
import org.jax.mgi.fewi.util.LimitedSizeCache;
import org.jax.mgi.snpdatamodel.ConsensusCoordinateSNP;
import org.jax.mgi.snpdatamodel.ConsensusSNP;

/*
 * This finder is responsible for finding results for the quick search
 */
@Repository
public class QuickSearchFinder {

	//--- static variables ---//
	
	// caches of symbol/name/location data for markers and alleles
	private static int cacheSize = 10000;
	private static LimitedSizeCache<QSFeaturePart> markerParts = new LimitedSizeCache<QSFeaturePart>(cacheSize);
	private static LimitedSizeCache<QSFeaturePart> alleleParts = new LimitedSizeCache<QSFeaturePart>(cacheSize);
	
	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(QuickSearchFinder.class);

	@Autowired
	private SnpFinder snpFinder;

	@Autowired
	private SolrQSAlleleResultHunter qsAlleleHunter;

	@Autowired
	private SolrQSOtherResultHunter qsOtherHunter;

	@Autowired
	private SolrQSStrainResultHunter qsStrainHunter;

	@Autowired
	private SolrQSVocabResultHunter qsVocabHunter;

	@Autowired
	private SolrQSFeatureResultTinyHunter qsFeatureTinyHunter;

	@Autowired
	private SolrQSFeatureResultFacetHunter featureFacetHunter;
	
	@Autowired
	private SolrQSAlleleResultFacetHunter alleleFacetHunter;
	
	@Autowired
	private SolrQSVocabResultFacetHunter vocabFacetHunter;
	
	@Autowired
	private SolrQSOtherResultFacetHunter otherFacetHunter;
	
	@Autowired
	private SolrQSStrainResultFacetHunter strainFacetHunter;
	
	@Autowired
	private SolrQSLookupHunter lookupHunter;
	
	//--- public methods ---//

	/* return all QSAlleleResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSAlleleResult> getAlleleResults(SearchParams searchParams) {
		logger.debug("->getAlleleResults");

		// result object to be returned
		SearchResults<QSAlleleResult> searchResults = new SearchResults<QSAlleleResult>();

		// ask the hunter to identify which objects to return
		qsAlleleHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS allele results");

		return searchResults;
	}

	/* return all QSstrainResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSStrainResult> getStrainResults(SearchParams searchParams) {
		logger.debug("->getStrainResults");

		// result object to be returned
		SearchResults<QSStrainResult> searchResults = new SearchResults<QSStrainResult>();

		// ask the hunter to identify which objects to return
		qsStrainHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS strain results");

		return searchResults;
	}

	/* return all QSOtherResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSOtherResult> getOtherResults(SearchParams searchParams) {
		logger.debug("->getOtherResults");

		// result object to be returned
		SearchResults<QSOtherResult> searchResults = new SearchResults<QSOtherResult>();

		// ask the hunter to identify which objects to return
		qsOtherHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS Other ID results");

		return searchResults;
	}
	
	/* return all SNP results (from Solr) as QSOtherResult objects, given the search words/phrases
	 */
	public SearchResults<QSOtherResult> getOtherSnpResults(List<String> terms) {
		logger.debug("->getOtherSnpResults");

		List<QSOtherResult> results = new ArrayList<QSOtherResult>();

		for (String term : terms) {
			if ((term != null) && (term.startsWith("rs"))) {
				SearchResults<ConsensusSNP> snpResults = snpFinder.getSnpByID(term);

				for (ConsensusSNP snp : snpResults.getResultObjects()) {
					QSOtherResult result = new QSOtherResult();
					result.setStars("****");
					result.setDetailUri("/snp/" + snp.getAccid());
					result.setObjectType(snp.getVariationClass());
					result.setPrimaryID(snp.getAccid());
					result.setSearchTermDisplay(snp.getAccid());
					result.setSearchTermType("SNP");
					result.setSearchTermExact(snp.getAccid());
					result.setSearchTermWeight(100);
					result.setSequenceNum(1L);
					result.setUniqueKey(snp.getAccid());
					
					String name = snp.getAccid();
					List<ConsensusCoordinateSNP> coords = snp.getConsensusCoordinates();
					if ((coords != null) && (coords.size() > 0)) {
						if (coords.size() > 1) {
							name = name + ", multiple coordinates";
						} else {
							ConsensusCoordinateSNP coord = coords.get(0);
							name = name + ", Chr" + coord.getChromosome();
							name = name + ": " + coord.getStartCoordinate();
							name = name + " (" + ContextLoader.getConfigBean().getProperty("SNP_ASSEMBLY_VERSION") + ")";
						}
					}
					name = name + ", " + snp.getVariationClass() + ", " + snp.getAlleleSummary();

					result.setName(name);
					results.add(result);
				}
			}
		}

		// result object to be returned
		SearchResults<QSOtherResult> searchResults = new SearchResults<QSOtherResult>();
		searchResults.setResultObjects(results);
		searchResults.setTotalCount(results.size());

		// ask the hunter to identify which objects to return
		logger.debug("->snpFinder found " + searchResults.getResultObjects().size() + " QS Other ID SNP results");

		return searchResults;
	}

	/* return all QSVocabResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSVocabResult> getVocabResults(SearchParams searchParams) {
		logger.debug("->getVocabResults");

		// result object to be returned
		SearchResults<QSVocabResult> searchResults = new SearchResults<QSVocabResult>();

		// ask the hunter to identify which objects to return
		qsVocabHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS vocab results");

		return searchResults;
	}

	/* return all QSFeatureResult (from Solr) objects matching the given search parameters
	 */
	public SearchResults<QSFeatureResult> getFeatureResults(SearchParams searchParams) {
		logger.debug("->getFeatureResults");

		// result object to be returned
		SearchResults<QSFeatureResult> searchResults = new SearchResults<QSFeatureResult>();

		// ask the hunter to identify which objects to return
		qsFeatureTinyHunter.hunt(searchParams, searchResults);
		logger.debug("->hunter found " + searchResults.getResultObjects().size() + " QS feature results");

		return searchResults;
	}
	
	/* get the specified facets for the matching feature results
	 */
	public List<String> getFeatureFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSFeatureResult> results = new SearchResults<QSFeatureResult>();
		featureFacetHunter.setFacetString(facetField);
		featureFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}

	/* get the specified facets for the matching vocab results
	 */
	public List<String> getVocabFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSVocabResult> results = new SearchResults<QSVocabResult>();
		vocabFacetHunter.setFacetString(facetField);
		vocabFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}

	/* get the specified facets for the matching strain results
	 */
	public List<String> getStrainFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSStrainResult> results = new SearchResults<QSStrainResult>();
		strainFacetHunter.setFacetString(facetField);
		strainFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}

	/* get the specified facets for the matching other ID results
	 */
	public List<String> getOtherFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSOtherResult> results = new SearchResults<QSOtherResult>();
		otherFacetHunter.setFacetString(facetField);
		otherFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}

	/* get the specified facets for the matching allele results
	 */
	public List<String> getAlleleFacets(SearchParams searchParams, String facetField) {
		SearchResults<QSAlleleResult> results = new SearchResults<QSAlleleResult>();
		alleleFacetHunter.setFacetString(facetField);
		alleleFacetHunter.hunt(searchParams, results);
		return results.getResultFacets();
	}
	
	/* Get a mapping from ID to the symbol/name/location parts corresponding to each of the given marker IDs.
	 */
	public Map<String,QSFeaturePart> getMarkerParts(List<QSFeatureResult> markers) {
		List<String> markerIDs = new ArrayList<String>(markers.size());
		for (QSFeatureResult marker : markers) {
			markerIDs.add(marker.getPrimaryID());
		}
		return this.getFeatureParts(markerIDs, markerParts);
	}
	
	/* Get a mapping from ID to the symbol/name/location parts corresponding to each of the given allele IDs.
	 */
	public Map<String,QSFeaturePart> getAlleleParts(List<QSAlleleResult> alleles) {
		List<String> alleleIDs = new ArrayList<String>(alleles.size());
		for (QSAlleleResult allele : alleles) {
			alleleIDs.add(allele.getPrimaryID());
		}
		return this.getFeatureParts(alleleIDs, alleleParts);
	}
	
	/* Get a mapping from ID to the symbol/name/location parts corresponding to each of the given marker IDs.
	 * Utilize the given cache to avoid retrieving common items from Solr.
	 */
	private Map<String,QSFeaturePart> getFeatureParts(List<String> ids, LimitedSizeCache<QSFeaturePart> cache) {
		logger.debug("->getFeatureParts (" + ids.size() + " IDs), cache size: " + cache.size());
		
		// mapping we're compiling to return
		Map<String,QSFeaturePart> out = new HashMap<String,QSFeaturePart>();

		// those not in cache that we need to get from Solr
		List<String> toFind = new ArrayList<String>();

		// get those that already exist in cache
		for (String id : ids) {
			if (cache.containsKey(id)) {
				out.put(id, cache.get(id));
			} else {
				toFind.add(id);
			}
		}
		logger.debug("Got " + out.size() + " from cache, " + toFind.size() + " yet to find");

		int sliceSize = 500;
		Paginator page = new Paginator(500);
		
		int start = 0;
		while (start < toFind.size()) {
			int end = Math.min(start + sliceSize, toFind.size());
			List<String> slice = toFind.subList(start, end);
			
			SearchParams params = new SearchParams();
			params.setPaginator(page);
			logger.debug("Looking up " + slice.size() + " IDs");
			params.setFilter(new Filter(SearchConstants.QS_PRIMARY_ID, slice, Filter.Operator.OP_IN));
			
			SearchResults<QSFeaturePart> results = new SearchResults<QSFeaturePart>();
			lookupHunter.hunt(params, results);
			
			logger.debug("Looked up " + results.getResultObjects().size() + " objects from Solr");
			for (QSFeaturePart fp : results.getResultObjects()) {
				out.put(fp.getPrimaryID(), fp);
				cache.put(fp.getPrimaryID(), fp);
			}
			
			start = end;
		}
		
		logger.debug("->hunter found " + out.size() + " feature parts");
		return out;
	}
}
