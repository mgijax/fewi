package org.jax.mgi.fewi.hmdc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalConditionGroup;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalConditionQuery;
import org.jax.mgi.fewi.hmdc.models.GridResult;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpDisease;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridAnnotationEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpGridEntry;
import org.jax.mgi.fewi.hmdc.solr.SolrHdpMarker;
import org.jax.mgi.fewi.hmdc.visitors.GridVisitor;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.util.HmdcAnnotationGroup;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.jsonmodel.GridMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/diseasePortal")
public class DiseasePortalController {


	// get the finders used by various methods
	@Autowired
	private DiseasePortalFinder hdpFinder;

	@Autowired
	private MarkerFinder markerFinder;

	//@Autowired
	//private TermFinder termFinder;

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm() {
		return "hmdc/home";
	}

	@RequestMapping(value="/gridQuery", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GridResult gridQuery(@RequestBody String jsonInput, HttpSession session) throws Exception {

		SearchParams params = new SearchParams();
		params.setPageSize(1000000);
		
		session.setAttribute("jsonInput", jsonInput);
		
		Filter mainFilter = null;
		Filter highlightFilter = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			DiseasePortalConditionGroup group = (DiseasePortalConditionGroup)mapper.readValue(jsonInput, DiseasePortalConditionGroup.class);
			mainFilter = genQueryFilter(group);
			highlightFilter = genHighlightFilter(group);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		params.setFilter(mainFilter);
		params.setReturnFilterQuery(true);
		SearchResults<SolrHdpGridEntry> results = hdpFinder.getGridResults(params);
		
		List<String> gridKeys = new ArrayList<String>();
		
		for(SolrHdpGridEntry res: results.getResultObjects()) {
			gridKeys.add(res.getGridKey().toString());
		}
		
		Filter gridFilter = new Filter(DiseasePortalFields.GRID_KEY, gridKeys, Operator.OP_IN);
		params.setFilter(gridFilter);
		params.setPageSize(1000000);
		
		SearchResults<SolrHdpGridAnnotationEntry> annotationResults = hdpFinder.getGridAnnotationResults(params);
		
		params.setFilter(highlightFilter);
		List<String> highLights = hdpFinder.getGridHighlights(params);
		
		GridVisitor gv = new GridVisitor(results, annotationResults, highLights);

		return gv.getGridResult();
	}

	@RequestMapping(value="/geneQuery")
	public @ResponseBody List<SolrHdpMarker> geneQuery(@RequestBody String jsonInput) throws Exception {

		SearchParams params = new SearchParams();
		params.setPageSize(1000000);

		try {
			ObjectMapper mapper = new ObjectMapper();
			DiseasePortalConditionGroup group = (DiseasePortalConditionGroup)mapper.readValue(jsonInput, DiseasePortalConditionGroup.class);
			params.setFilter(genQueryFilter(group));
		} catch (IOException e) {
			e.printStackTrace();
		}

		SearchResults<SolrHdpMarker> searchResults = hdpFinder.getMarkers(params);

		return searchResults.getResultObjects();
	}

	@RequestMapping(value="/diseaseQuery")
	public @ResponseBody List<SolrHdpDisease> diseaseQuery(@RequestBody String jsonInput) throws Exception {

		SearchParams params = new SearchParams();
		params.setPageSize(1000000);

		try {
			ObjectMapper mapper = new ObjectMapper();
			DiseasePortalConditionGroup group = (DiseasePortalConditionGroup)mapper.readValue(jsonInput, DiseasePortalConditionGroup.class);
			params.setFilter(genQueryFilter(group));
		} catch (IOException e) {
			e.printStackTrace();
		}

		SearchResults<SolrHdpDisease> searchResults = hdpFinder.getDiseases(params);

		return searchResults.getResultObjects();

	}

	private Filter genHighlightFilter(DiseasePortalConditionGroup group) {
		
		List<Filter> filterList = new ArrayList<Filter>();

		for(DiseasePortalConditionQuery cq: group.getQueries()) {
			Filter f = cq.genHighlightFilter();
			if(f != null) {
				filterList.add(f);
			}
		}
		// Always return OR so that multiple terms across an AND search will be highlighted.
		return Filter.or(filterList);
	}

	private Filter genQueryFilter(DiseasePortalConditionGroup group) {

		List<Filter> filterList = new ArrayList<Filter>();

		if (group.getQueries() == null) {
			return null;
		}
		for(DiseasePortalConditionQuery cq: group.getQueries()) {
			filterList.add(cq.genFilter());
		}

		if(group.getOperator().equals("AND")) {
			return Filter.and(filterList);
		} else {
			return Filter.or(filterList);
		}
	}

	/* Serve up a phenotype popup, from clicking a phenotype cell on the HMDC grid.
	 * Expects two parameters in the request: gridClusterKey and header.  Any phenotype or
	 * disease terms or IDs can be passed in for highlighting using the (optional)
	 * term or termId parameters; multiples of each can be submitted.
	 */
	@RequestMapping(value="/phenotypePopup", method=RequestMethod.GET)
	public ModelAndView getPhenotypePopup(HttpServletRequest request, HttpSession session) {
		return getPopup(request, session, true);
	}

	/* Serve up a disease popup, from clicking a disease cell on the HMDC grid.
	 * Expects two parameters in the request: gridClusterKey and header.  Any phenotype or
	 * disease terms or IDs can be passed in for highlighting using the (optional)
	 * term or termId parameters; multiples of each can be submitted.
	 */
	@RequestMapping(value="/diseasePopup", method=RequestMethod.GET)
	public ModelAndView getDiseasePopup(HttpServletRequest request, HttpSession session) {
		return getPopup(request, session, false);
	}

	/* Serve up a phenotype or disease popup, from clicking a cell on the HMDC grid.
	 * Expects two parameters in the request: gridClusterKey and header.  Any phenotype or
	 * disease terms or IDs can be passed in for highlighting using the (optional)
	 * term or termId parameters; multiples of each can be submitted.  The 'isPhenotype'
	 * parameter should be true for phenotype popups and false for disease popups.
	 */
	private ModelAndView getPopup(HttpServletRequest request, HttpSession session, boolean isPhenotype) {
		// collect the required parameters and check that they are specified
		String gridClusterKey = request.getParameter("gridClusterKey");
		if (gridClusterKey == null) { return errorMav("Missing gridClusterKey parameter"); }

		String header = request.getParameter("header");
		if (header == null) { return errorMav("Missing header parameter"); }

		
		DiseasePortalConditionGroup group = null;
		if (session.getAttribute("jsonInput") != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				group = (DiseasePortalConditionGroup)mapper.readValue((String)session.getAttribute("jsonInput"), DiseasePortalConditionGroup.class);
				//mainFilter = genQueryFilter(group);
				//highlightFilter = genHighlightFilter(group);
			} catch (Exception e) {
				// not a fatal error; if the user's session has expired or if JBoss has been restarted,
				// just skip the additional conditions and at least give them a popup for the gridClusterKey
				// and the header term (don't restrict by location or by term search)
			}
		}
		if (group == null) {
			group = new DiseasePortalConditionGroup();
		}

		Filter mainFilter = genQueryFilter(group);
		
		List<Filter> filterList = new ArrayList<Filter>();
		if (mainFilter != null) {
			filterList.add(mainFilter);
		}
		filterList.add(new Filter(DiseasePortalFields.TERM_HEADER, header));
		filterList.add(new Filter(DiseasePortalFields.GRID_CLUSTER_KEY, gridClusterKey));
		
		// run the query to get the set of grid results
		SearchParams params = new SearchParams();
		params.setPageSize(10000);
		params.setFilter(Filter.and(filterList));
		params.setReturnFilterQuery(true);
		SearchResults<SolrHdpGridEntry> results = hdpFinder.getGridResults(params);
		
		// cache data from the grid to integrate later on with their annotations
		
		// list of gridKeys -- multiple per grid cluster key are possible (and likely)
		List<String> gridKeys = new ArrayList<String>();
		
		// includes only mouse gridKeys, so we can tell mouse from human
		Set<Integer> mouseGridKeys = new HashSet<Integer>();
		
		// grid key -> human marker symbol (for human gridKeys)
		Map<Integer,String> humanSymbols = new HashMap<Integer,String>();
		
		// grid key -> homology cluster key (for human gridKeys)
		Map<Integer,String> homologyClusterKeys = new HashMap<Integer,String>();
		
		// grid key -> allele pairs (for mouse gridKeys)
		Map<Integer,String> allelePairs = new HashMap<Integer,String>();
		
		List<SolrHdpGridEntry> gridResults = results.getResultObjects();
		for(SolrHdpGridEntry res: gridResults) {
			Integer gridKey = res.getGridKey();
			gridKeys.add(gridKey.toString());
			
			// mouse gridKeys have a non-null genocluster key; human data are not in genoclusters
			if (res.getGenoClusterKey() != null) {
				// is mouse data
				allelePairs.put(gridKey, res.getAllelePairs());
				mouseGridKeys.add(gridKey); 
			} else {
				// is human data
				humanSymbols.put(gridKey, res.getMarkerSymbol());
				homologyClusterKeys.put(gridKey, res.getHomologyClusterKey().toString());
			}
		}
		
		// pull out marker data needed for header
		List<String> humanMarkers = null;
		List<String> mouseMarkers = null;
		
		if (gridResults.size() > 0) {
			SolrHdpGridEntry first = gridResults.get(0);

			mouseMarkers = new ArrayList<String>();
			humanMarkers = new ArrayList<String>();
			
			for (GridMarker marker : first.getGridHumanSymbols()) { humanMarkers.add(marker.getSymbol()); }
			for (GridMarker marker : first.getGridMouseSymbols()) { mouseMarkers.add(marker.getSymbol()); }
		}
		
		/* For display on the popup, we want only the annotations that contribute to the cell
		 * identified by the grid cluster key (row) and the header (column).  At this point, we can
		 * use the individual grid keys rather than the grid cluster key, as the latter is made up
		 * of the former.
		 */
		List<Filter> annotationFilters = new ArrayList<Filter>(); 
		annotationFilters.add(new Filter(DiseasePortalFields.GRID_KEY, gridKeys, Operator.OP_IN));
		annotationFilters.add(new Filter(DiseasePortalFields.TERM_HEADER, header));
		params.setFilter(Filter.and(annotationFilters));
		
		SearchResults<SolrHdpGridAnnotationEntry> annotationResults = hdpFinder.getGridAnnotationResults(params);
		
		/* need to split the annotation results up into their categories (one per table displayed):
		 *	1. mouse genotype/phenotype (MP) annotations
		 *	2. human marker/phenotype (HPO) annotations
		 *	3. mouse genotype/OMIM annotations and human marker/OMIM annotations
		 */
		HmdcAnnotationGroup mpGroup = new HmdcAnnotationGroup(false);
		HmdcAnnotationGroup hpoGroup = new HmdcAnnotationGroup(false);
		HmdcAnnotationGroup omimGroup = new HmdcAnnotationGroup(true);

		for (SolrHdpGridAnnotationEntry result : annotationResults.getResultObjects()) {
			Integer gridKey = result.getGridKey();
			String termType = result.getTermType();
			
			if ("OMIM".equals(termType)) {
				if (mouseGridKeys.contains(gridKey)) {
					// is mouse genotype/disease annotation
					omimGroup.addMouseAnnotation(allelePairs.get(gridKey), result.getTerm());
				} else { 
					// is human marker/disease annotation
					omimGroup.addHumanAnnotation(humanSymbols.get(gridKey), homologyClusterKeys.get(gridKey),
						result.getTerm(), result.getSourceId(), result.getTerm(), result.getByDagTerm());
				}
			} else if ("Mammalian Phenotype".equals(termType)) {
				// is mouse genotype/MP annotation
				mpGroup.addMouseAnnotation(allelePairs.get(gridKey), result.getTerm());
			} else {
				// is human marker/HPO annotation (generated via OMIM-HPO mapping)
				hpoGroup.addHumanAnnotation(humanSymbols.get(gridKey), homologyClusterKeys.get(gridKey),
					result.getSourceTerm(), result.getSourceId(), result.getTerm(), result.getByDagTerm());
			}
		}

		// begin collecting the mav to return
		ModelAndView mav = new ModelAndView("hmdc/popup");
		
		// add the required parameters
		mav.addObject("gridClusterKey", gridClusterKey);
		mav.addObject("headerTerm", header);
		
		if (isPhenotype) {
			mav.addObject("isPhenotype", 1);
			mav.addObject("mpGroup", mpGroup);
			mav.addObject("hpoGroup", hpoGroup);
		} else {
			mav.addObject("isDisease", 1);
			mav.addObject("omimGroup", omimGroup);
		}
		
		// add any (optional) terms and term IDs that were specified to use for column highlighting
		String[] terms = request.getParameterValues("term");
		String[] termIds = request.getParameterValues("termId");
		
		if (terms != null && terms.length > 0) { mav.addObject("highlightTerms", terms); }
		if (termIds != null && termIds.length > 0) { mav.addObject("highlightTermIds", termIds); }

		if (mouseMarkers != null && mouseMarkers.size() > 0) { mav.addObject("mouseMarkers", mouseMarkers.toArray(new String[0])); }
		if (humanMarkers != null && humanMarkers.size() > 0) { mav.addObject("humanMarkers", humanMarkers.toArray(new String[0])); }

		mav.addObject("gridKeyCount", gridKeys.size());
		mav.addObject("annotationCount", annotationResults.getTotalCount());

		return mav;
	}

	/* build and return a mav for a page with the given error message
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}
}

	