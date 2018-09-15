package org.jax.mgi.fewi.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.jax.mgi.fewi.hunter.SolrSNPAlleleSearchHunter;
import org.jax.mgi.fewi.hunter.SolrSNPDataHunter;
import org.jax.mgi.fewi.hunter.SolrSNPSearchHunter;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.ConsensusSNPSummaryRow;
import org.jax.mgi.shr.fe.IndexConstants;
import org.jax.mgi.snpdatamodel.ConsensusCoordinateSNP;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/*
 * This finder is responsible for finding snp(s)
 */

@Repository
public class SnpFinder {

	@Autowired
	private SolrSNPDataHunter snpDataHunter;

	@Autowired
	private SolrSNPSearchHunter snpSearchHunter;
	
	@Autowired
	private SolrSNPAlleleSearchHunter snpAlleleSearchHunter;

//	public SearchResults<ConsensusSNP> getSnps(SearchParams searchParams) {
//		SearchResults<ConsensusSNP> searchResults = new SearchResults<ConsensusSNP>();
//		snpSearchHunter.hunt(searchParams, searchResults, SearchConstants.SNPID);
//
//		SearchParams dataSearchParams = new SearchParams();
//		Filter snpIdFilter = new Filter(SearchConstants.SNPID, searchResults.getResultKeys(), Filter.Operator.OP_IN);
//		dataSearchParams.setFilter(snpIdFilter);
//		
//		searchResults = new SearchResults<ConsensusSNP>();
//		snpDataHunter.hunt(dataSearchParams, searchResults);
//
//		return searchResults;
//	}
	
	public SearchResults<ConsensusSNP> getSnp(SearchParams searchParams) {

		SearchResults<ConsensusSNP> searchResults = new SearchResults<ConsensusSNP>();
		snpDataHunter.hunt(searchParams, searchResults);

		return searchResults;
	}

	// convenience wrapper
	public SearchResults<ConsensusSNP> getSnpByID(String snpID) {
		SearchParams searchParams = new SearchParams();
		Filter snpIdFilter = new Filter(SearchConstants.SNPID, snpID);
		searchParams.setFilter(snpIdFilter);
		return this.getSnp(searchParams);
	}

	public SearchResults<ConsensusSNPSummaryRow> getSummarySnps(SearchParams searchParams, List<String> matchedMarkerIds) {
		SearchResults<ConsensusSNP> searchResults1 = new SearchResults<ConsensusSNP>();

		Filter sameFilter = null;
		Filter diffFilter = null;
		
		if(searchParams.getFilter() != null) {
			sameFilter = searchParams.getFilter().getFirstFilterFor(SearchConstants.SAME_STRAINS);
			diffFilter = searchParams.getFilter().getFirstFilterFor(SearchConstants.DIFF_STRAINS);
		}
		
		if(sameFilter != null || diffFilter != null) {
			snpAlleleSearchHunter.setFacetString(IndexConstants.SNP_STRAINS);
			snpAlleleSearchHunter.hunt(searchParams, searchResults1);
		} else {
			snpSearchHunter.setFacetString(IndexConstants.SNP_STRAINS);
			snpSearchHunter.hunt(searchParams, searchResults1);
		}
		
		HashMap<String, String> ml = new HashMap<String, String>();
		for(String markerId: matchedMarkerIds) {
			ml.put(markerId, markerId);
		}

		SearchParams dataSearchParams = new SearchParams();
		Filter snpIdFilter = new Filter(SearchConstants.SNPID, searchResults1.getResultKeys(), Filter.Operator.OP_IN);
		dataSearchParams.setFilter(snpIdFilter);
		dataSearchParams.setPageSize(searchParams.getPageSize());
		
		SearchResults<ConsensusSNP> searchResults2 = new SearchResults<ConsensusSNP>();
		snpDataHunter.hunt(dataSearchParams, searchResults2);
		
		List<ConsensusSNPSummaryRow> summaryRows = new ArrayList<ConsensusSNPSummaryRow>();
		
		
		// <Start Sorting>
		// <Chromosome, <StartCoodinate, Snp>>
		TreeMap<Integer, TreeMap<Integer, ConsensusSNP>> sortedMap = new TreeMap<Integer, TreeMap<Integer, ConsensusSNP>>();

		// Sort by chromosome then by start coordinate
		for(ConsensusSNP snp: searchResults2.getResultObjects()) {
			if(snp.getConsensusCoordinates() != null && snp.getConsensusCoordinates().size() > 0) {
				
				ConsensusCoordinateSNP coord = snp.getConsensusCoordinates().get(0);
				TreeMap<Integer, ConsensusSNP> coordinateMap = null;
				
				int chromosomeLookup = 0;
				String chromosome = coord.getChromosome();
				switch (chromosome) {
					case "X":
						chromosomeLookup = 210;
						break;
					case "Y":
						chromosomeLookup = 230;
						break;
					case "XY":
						chromosomeLookup = 250;
						break;
					case "UN":
						chromosomeLookup = 270;
						break;
					case "MT":
						chromosomeLookup = 290;
						break;
					default:
						chromosomeLookup = Integer.parseInt(chromosome);
						break;
				}

				coordinateMap = sortedMap.get(chromosomeLookup);
				if(coordinateMap == null) {
					coordinateMap = new TreeMap<Integer, ConsensusSNP>();
					sortedMap.put(chromosomeLookup, coordinateMap);
				}
				coordinateMap.put(coord.getStartCoordinate(), snp);
			}
		}

		// Loop and Add
		for(Integer chromosome: sortedMap.keySet()) {
			for(Integer startCoordinate: sortedMap.get(chromosome).keySet()) {
				summaryRows.add(new ConsensusSNPSummaryRow(sortedMap.get(chromosome).get(startCoordinate), ml));
			}
		}
		// </End Sorting>
		
		
		SearchResults<ConsensusSNPSummaryRow> ret = new SearchResults<ConsensusSNPSummaryRow>();
		ret.setResultKeys(searchResults1.getResultKeys());
		ret.setTotalCount(searchResults1.getTotalCount());
		ret.setResultFacets(searchResults1.getResultFacets());
		ret.setResultObjects(summaryRows);
		return ret;
	}

	public SearchResults<ConsensusSNPSummaryRow> getMatchingSnpCount(SearchParams searchParams, List<String> matchedMarkerIds) {
		SearchResults<ConsensusSNP> searchResults1 = new SearchResults<ConsensusSNP>();

		Filter sameFilter = null;
		Filter diffFilter = null;
		
		if(searchParams.getFilter() != null) {
			sameFilter = searchParams.getFilter().getFirstFilterFor(SearchConstants.SAME_STRAINS);
			diffFilter = searchParams.getFilter().getFirstFilterFor(SearchConstants.DIFF_STRAINS);
		}
		
		if(sameFilter != null || diffFilter != null) {
			snpAlleleSearchHunter.setFacetString(IndexConstants.SNP_STRAINS);
			snpAlleleSearchHunter.hunt(searchParams, searchResults1);
		} else {
			snpSearchHunter.setFacetString(IndexConstants.SNP_STRAINS);
			snpSearchHunter.hunt(searchParams, searchResults1);
		}
		
		HashMap<String, String> ml = new HashMap<String, String>();
		for(String markerId: matchedMarkerIds) {
			ml.put(markerId, markerId);
		}

/*		SearchParams dataSearchParams = new SearchParams();
		Filter snpIdFilter = new Filter(SearchConstants.SNPID, searchResults1.getResultKeys(), Filter.Operator.OP_IN);
		dataSearchParams.setFilter(snpIdFilter);
		dataSearchParams.setPageSize(searchParams.getPageSize());
		
		SearchResults<ConsensusSNP> searchResults2 = new SearchResults<ConsensusSNP>();
		snpDataHunter.hunt(dataSearchParams, searchResults2);
		
		List<ConsensusSNPSummaryRow> summaryRows = new ArrayList<ConsensusSNPSummaryRow>();
*/		
		SearchResults<ConsensusSNPSummaryRow> ret = new SearchResults<ConsensusSNPSummaryRow>();
//		ret.setResultKeys(searchResults1.getResultKeys());
		ret.setTotalCount(searchResults1.getTotalCount());
//		ret.setResultFacets(searchResults1.getResultFacets());
//		ret.setResultObjects(summaryRows);
		return ret;
	}
	
	/* get the function classes (as facets) for the consensus SNPs
	 * matching the current query
	 */
	public List<String> getFunctionClassFacets(SearchParams searchParams) {
		SearchResults<ConsensusSNP> results = new SearchResults<ConsensusSNP>();
		
		Filter sameFilter = null;
		Filter diffFilter = null;
		
		if(searchParams.getFilter() != null) {
			sameFilter = searchParams.getFilter().getFirstFilterFor(SearchConstants.SAME_STRAINS);
			diffFilter = searchParams.getFilter().getFirstFilterFor(SearchConstants.DIFF_STRAINS);
		}
		
		if(sameFilter != null || diffFilter != null) {
			snpAlleleSearchHunter.setFacetString(IndexConstants.SNP_FUNCTIONCLASS);
			snpAlleleSearchHunter.hunt(searchParams, results);
		} else {
			snpSearchHunter.setFacetString(IndexConstants.SNP_FUNCTIONCLASS);
			snpSearchHunter.hunt(searchParams, results);
		}

		return results.getResultFacets();
	}

	public String debugFilter(Filter f) {
		return snpSearchHunter.debugFilter(f);
	}

}
