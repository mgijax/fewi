package org.jax.mgi.fewi.hmdc.finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalCoordHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalDiseaseHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGeneHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGridAnnotationHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGridHighlightHunter;
import org.jax.mgi.fewi.hmdc.hunter.SolrDiseasePortalGridHunter;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpCoord;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpEntityInterface;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.objectGatherer.HibernateObjectGatherer;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mgi.frontend.datamodel.hdp.HdpGenoCluster;

@Repository
public class DiseasePortalFinder {
	private Logger logger = LoggerFactory.getLogger(DiseasePortalFinder.class);

	@Autowired
	private SolrDiseasePortalDiseaseHunter hdpDiseaseHunter;
	
	@Autowired
	private SolrDiseasePortalGeneHunter hdpGeneHunter;
	
	@Autowired
	private SolrDiseasePortalGridHunter hdpGridHunter;
	
	@Autowired
	private SolrDiseasePortalGridAnnotationHunter hdpGridAnnotationHunter;

	@Autowired
	private SolrDiseasePortalGridHighlightHunter hdpGridHighlightHunter;
	
	@Autowired
	private SolrDiseasePortalCoordHunter hdpCoordHunter;
	
	@Autowired
	private HibernateObjectGatherer<HdpGenoCluster> genoCGatherer;
	
	public SearchResults<SolrHdpDisease> getDiseases(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpDiseaseHunter.hunt(params, results, DiseasePortalFields.TERM_ID);
		SearchResults<SolrHdpDisease> srVT = new SearchResults<SolrHdpDisease>();
		srVT.cloneFrom(results,SolrHdpDisease.class);
		return srVT;
	}

	public SearchResults<SolrHdpMarker> getMarkers(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGeneHunter.hunt(params, results, DiseasePortalFields.MARKER_KEY);
		SearchResults<SolrHdpMarker> srM = new SearchResults<SolrHdpMarker>();
		srM.cloneFrom(results,SolrHdpMarker.class);
		return srM;
	}
	
	public SearchResults<SolrHdpGridEntry> getGridResults(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHunter.hunt(params, results);
		SearchResults<SolrHdpGridEntry> srM = new SearchResults<SolrHdpGridEntry>();
		srM.cloneFrom(results,SolrHdpGridEntry.class);
		return srM;
	}
	
	public SearchResults<SolrHdpGridAnnotationEntry> getGridAnnotationResults(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> annotationResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridAnnotationHunter.hunt(params, annotationResults);
		SearchResults<SolrHdpGridAnnotationEntry> srM = new SearchResults<SolrHdpGridAnnotationEntry>();
		srM.cloneFrom(annotationResults,SolrHdpGridAnnotationEntry.class);
		return srM;
	}

	public List<String> getGridHighlights(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> highlightResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHighlightHunter.hunt(params, highlightResults, DiseasePortalFields.TERM_HEADER);
		return highlightResults.getResultKeys();
	}

	/* request highlights for the search results and get the list of document IDs for documents that
	 * had highlights returned. 
	 */
	public List<String> getHighlightedDocumentIDs(SearchParams params) {
		SearchResults<SolrHdpEntityInterface> highlightResults = new SearchResults<SolrHdpEntityInterface>();
		hdpGridHighlightHunter.hunt(params, highlightResults, DiseasePortalFields.TERM);
		return highlightResults.getResultKeys();
	}

	// gets genocluster data for link from grid popup
	public List<HdpGenoCluster> getGenoClusterByKey(String genoClusterKey) {
		return genoCGatherer.get(HdpGenoCluster.class,Arrays.asList(genoClusterKey));
	}

	// gets genocluster data for multiple genoclusters
	public List<HdpGenoCluster> getGenoClustersByKeys(List<Integer> genoClusterKeys) {
		if (genoClusterKeys == null) { return null; }
		
		List<String> gcKeys = new ArrayList<String>(genoClusterKeys.size());
		for (Integer gcKey : genoClusterKeys) {
			gcKeys.add(Integer.toString(gcKey));
		}
		return genoCGatherer.get(HdpGenoCluster.class, gcKeys);
	}
	
	// Get the appropriate filter to ensure that we search via coordinates for the specified organism.
	// Assumes organism is either mouse or human.
	private Filter getOrganismFilter(String organism) {
		List<Filter> both = new ArrayList<Filter>();
		List<Filter> left = new ArrayList<Filter>();
		List<Filter> right = new ArrayList<Filter>();
		
		left.add(new Filter(SearchConstants.ORGANISM, organism, Operator.OP_EQUAL));
		left.add(new Filter(SearchConstants.COORD_TYPE, "marker", Operator.OP_EQUAL));

		right.add(new Filter(SearchConstants.ORGANISM, organism, Operator.OP_NOT_EQUAL));
		right.add(new Filter(SearchConstants.COORD_TYPE, "ortholog", Operator.OP_EQUAL));
		
		both.add(Filter.and(left));
		both.add(Filter.and(right));
		return Filter.or(both);
	}
	
	// Get marker keys by coordinate search.  If errors occur, just return an empty list.
	public List<Integer> getMarkersByCoord(String organism, String coords) {
		List<Integer> markerKeys = new ArrayList<Integer>();
		List<Filter> filters = new ArrayList<Filter>();

		// No coords == no markers.
		if ((coords == null) || (coords.trim().length() == 0)) {
			return markerKeys;
		}
		
		// Search via mouse coordinates if:  mouse is specified, or no organism specified, or an unknown
		// organism specified.
		if ((organism == null) || "mouse".equals(organism) || ( (organism != "mouse") && (organism != "human") )) {
			filters.add(getOrganismFilter("mouse"));
		} else {
			filters.add(getOrganismFilter("human"));	// Otherwise search via human coordinates.
		}
		
		// Recognized formats, all case-insensitive:
		//	(a) Chr1:12345-678910 (the format given as an example on the QF)
		//	(b) Chr1:12345..678910 (be forgiving of .. substituted for a hyphen)
		//	(c) 1:12345-678910 (be forgiving of a missing Chr prefix)
		//	(d) 1:12345..678910 (forgive both missing Chr and .. substitution)
		//	(e) Chr1:12345 (only a point coordinate)
		//	(f) 1:12345 (forgive the missing Chr prefix)
		//	(g) Chr1 (whole chromosome)
		
		// standardize (a), (b), and (d) down to just (c).  Also (e) down to (f).
		coords = coords.toUpperCase().replaceAll("[.][.]", "-").replaceAll("[cC][hH][rR]", "").trim();	
		
        Pattern fullRange = Pattern.compile("^([0-9XYMT]+):([0-9]+)-([0-9]+)$");	// case (c)
        Matcher fullRangeMatcher = fullRange.matcher(coords);

        Pattern pointCoord = Pattern.compile("^([0-9XYMT]+):([0-9]+)$");			// case (f)
        Matcher pointCoordMatcher = pointCoord.matcher(coords);
        
        Pattern onlyChromosome = Pattern.compile("^([0-9XYMT]+)$");					// case (g)
        Matcher onlyChromosomeMatcher = onlyChromosome.matcher(coords);

		if (fullRangeMatcher.matches()) {
			filters.add(new Filter(SearchConstants.CHROMOSOME, fullRangeMatcher.group(1), Operator.OP_EQUAL));
        	filters.add(new Filter(SearchConstants.START_COORD, fullRangeMatcher.group(3), Operator.OP_LESS_OR_EQUAL));
        	filters.add(new Filter(SearchConstants.END_COORD, fullRangeMatcher.group(2), Operator.OP_GREATER_OR_EQUAL));
			
		} else if (pointCoordMatcher.matches()) {
			filters.add(new Filter(SearchConstants.CHROMOSOME, pointCoordMatcher.group(1), Operator.OP_EQUAL));
        	filters.add(new Filter(SearchConstants.START_COORD, fullRangeMatcher.group(2), Operator.OP_LESS_OR_EQUAL));
        	filters.add(new Filter(SearchConstants.END_COORD, fullRangeMatcher.group(2), Operator.OP_GREATER_OR_EQUAL));

		} else if (onlyChromosomeMatcher.matches()) {
			filters.add(new Filter(SearchConstants.CHROMOSOME, onlyChromosomeMatcher.group(1), Operator.OP_EQUAL));
		}
		
		if (filters.size() < 2) {		// If only 1, then only human/mouse is specified.  Bail out.
			return markerKeys;
		}

		// Compose and execute the search.
		
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(SearchConstants.MARKER_ID));
		
		SearchParams params = new SearchParams();
		params.setPaginator(new Paginator(10000));
		params.setFilter(Filter.and(filters));
		params.setSorts(sorts);

		SearchResults<SolrHdpEntityInterface> results = new SearchResults<SolrHdpEntityInterface>();
		hdpCoordHunter.hunt(params, results);

		SearchResults<SolrHdpCoord> srM = new SearchResults<SolrHdpCoord>();
		srM.cloneFrom(results, SolrHdpCoord.class);
		
		Set<Integer> keySet = new HashSet<Integer>();
		for (SolrHdpEntityInterface e : results.getResultObjects()) {
			SolrHdpCoord c = (SolrHdpCoord) e;
			Integer markerKey = c.getMarkerKey();
			if (!keySet.contains(markerKey)) {
				keySet.add(markerKey);
				markerKeys.add(markerKey);
			}
		}

		return markerKeys;
	}
}
