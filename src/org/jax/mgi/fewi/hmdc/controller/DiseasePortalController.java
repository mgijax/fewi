package org.jax.mgi.fewi.hmdc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.TermFinder;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalConditionGroup;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalConditionQuery;
import org.jax.mgi.fewi.hmdc.forms.DiseasePortalQueryBuilder;
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

	@Autowired
	private TermFinder termFinder;

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm() {
		return "hmdc/home";
	}

	@RequestMapping(value="/gridQuery", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GridResult gridQuery(@RequestBody String jsonInput) throws Exception {

		SearchParams params = new SearchParams();
		params.setPageSize(1000000);

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
	public ModelAndView getPhenotypePopup(HttpServletRequest request) {
		return getPopup(request, true);
	}

	/* Serve up a disease popup, from clicking a disease cell on the HMDC grid.
	 * Expects two parameters in the request: gridClusterKey and header.  Any phenotype or
	 * disease terms or IDs can be passed in for highlighting using the (optional)
	 * term or termId parameters; multiples of each can be submitted.
	 */
	@RequestMapping(value="/diseasePopup", method=RequestMethod.GET)
	public ModelAndView getDiseasePopup(HttpServletRequest request) {
		return getPopup(request, false);
	}

	/* Serve up a phenotype or disease popup, from clicking a cell on the HMDC grid.
	 * Expects two parameters in the request: gridClusterKey and header.  Any phenotype or
	 * disease terms or IDs can be passed in for highlighting using the (optional)
	 * term or termId parameters; multiples of each can be submitted.  The 'isPhenotype'
	 * parameter should be true for phenotype popups and false for disease popups.
	 */
	private ModelAndView getPopup(HttpServletRequest request, boolean isPhenotype) {
		// collect the required parameters and check that they are specified
		String gridClusterKey = request.getParameter("gridClusterKey");
		if (gridClusterKey == null) { return errorMav("Missing gridClusterKey parameter"); }

		String header = request.getParameter("header");
		if (header == null) { return errorMav("Missing header parameter"); }

		
		DiseasePortalQueryBuilder builder = new DiseasePortalQueryBuilder("AND");
		builder.addCondition(DiseasePortalFields.GRID_CLUSTER_KEY, gridClusterKey);
		builder.addCondition(DiseasePortalFields.TERM_HEADER, header);
	
		Filter mainFilter = genQueryFilter(builder.getQueryGroup());
		
		// run the query to get the set of grid results
		SearchParams params = new SearchParams();
		params.setPageSize(10000);
		params.setFilter(mainFilter);
		params.setReturnFilterQuery(true);
		SearchResults<SolrHdpGridEntry> results = hdpFinder.getGridResults(params);
		
		// collect the grid results and get their annotations
		List<String> gridKeys = new ArrayList<String>();
		List<SolrHdpGridEntry> gridResults = results.getResultObjects();
		for(SolrHdpGridEntry res: gridResults) {
			gridKeys.add(res.getGridKey().toString());
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

		// begin collecting the mav to return
		ModelAndView mav = new ModelAndView("hmdc/popup");
		
		// add the required parameters
		mav.addObject("gridClusterKey", gridClusterKey);
		mav.addObject("headerTerm", header);
		
		if (isPhenotype) {
			mav.addObject("isPhenotype", 1);
		} else {
			mav.addObject("isDisease", 1);
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

	