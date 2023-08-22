package org.jax.mgi.fewi.hmdc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.VocabTerm;
import mgi.frontend.datamodel.hdp.HdpGenoCluster;

import org.codehaus.jackson.map.ObjectMapper;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.finder.MpHpPopupFinder;
import org.jax.mgi.fewi.forms.AccessionQueryForm;
import org.jax.mgi.fewi.summary.MpHpPopupRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
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
import org.jax.mgi.fewi.searchUtil.entities.SolrMpHpPopupResult;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.HmdcAnnotationGroup;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.jsonmodel.GridGenocluster;
import org.jax.mgi.shr.jsonmodel.GridMarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/diseasePortal")
public class DiseasePortalController {

	private final Logger logger = LoggerFactory.getLogger(DiseasePortalController.class);

	// get the finders used by various methods
	@Autowired
	private DiseasePortalFinder hdpFinder;

	@Autowired
	private VocabularyFinder vocabFinder;
	
	@Autowired
	private MarkerFinder markerFinder;
	
	@Autowired
	private MpHpPopupFinder mpHpPopupFinder;

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm() {
		return "hmdc/home";
	}

	@RequestMapping(value="/gridQuery", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody GridResult gridQuery(@RequestBody String jsonInput, HttpSession session) throws Exception {

		SearchParams params = new SearchParams();
		params.setPageSize(1000000);

		String queryToken = "";
		
		LinkedHashMap<String, String> queryHistoryMap = (LinkedHashMap<String, String>) session.getAttribute("queryHistoryMap");
		if(queryHistoryMap == null) {
			queryHistoryMap = new LinkedHashMap<String, String>() {
				protected boolean removeEldestEntry(Map.Entry eldest){
				    return size() > 10;
				}
			};
			session.setAttribute("queryHistoryMap", queryHistoryMap);
		}
		queryToken = UUID.randomUUID().toString();
		queryHistoryMap.put(queryToken, jsonInput);
		
		logger.info("Query History Map: " + queryHistoryMap.size());
		
		Filter mainFilter = null;
		Filter highlightFilter = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			DiseasePortalConditionGroup group = mapper.readValue(jsonInput, DiseasePortalConditionGroup.class);
			mainFilter = genQueryFilter(group);
			highlightFilter = genHighlightFilter(group);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		params.setFilter(mainFilter);
		params.setReturnFilterQuery(true);
		SearchResults<SolrHdpGridEntry> results = hdpFinder.getGridResults(params);
		
		List<String> gridKeys = new ArrayList<String>();
                Set<String> gridClusterKeySet = new HashSet<String>();
		
		for(SolrHdpGridEntry res: results.getResultObjects()) {
			gridKeys.add(res.getGridKey().toString());
                        gridClusterKeySet.add(res.getGridClusterKey().toString());
		}
		List<String> gridClusterKeys = new ArrayList<String>(gridClusterKeySet);
		
		Filter gridFilter = new Filter(DiseasePortalFields.GRID_CLUSTER_KEY, gridClusterKeys, Operator.OP_IN);
		params.setFilter(gridFilter);
		params.setPageSize(1000000);
		
		SearchResults<SolrHdpGridAnnotationEntry> annotationResults = hdpFinder.getGridAnnotationResults(params);
		
		List<String> highLights = null;

		// only highlight terms if we had a phenotype or disease condition in the search
		if (highlightFilter.hasNestedFilters()) {
			List<Filter> gridKeyAwareHighlights = new ArrayList<Filter>();
			gridKeyAwareHighlights.add(highlightFilter);
			gridKeyAwareHighlights.add(new Filter(DiseasePortalFields.GRID_CLUSTER_KEY, gridClusterKeys, Operator.OP_IN));
			params.setFilter(Filter.and(gridKeyAwareHighlights));
			highLights = hdpFinder.getGridHighlights(params);
		}
		
		GridVisitor gv = new GridVisitor(results, annotationResults, highLights);

		GridResult gridResult = gv.getGridResult();
		gridResult.setQueryToken(queryToken);
		return gridResult;
	}

	@RequestMapping(value="/geneQuery")
	public @ResponseBody List<SolrHdpMarker> geneQuery(@RequestBody String jsonInput) throws Exception {

		SearchParams params = new SearchParams();
		params.setPageSize(1000000);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			DiseasePortalConditionGroup group = mapper.readValue(jsonInput, DiseasePortalConditionGroup.class);
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
			DiseasePortalConditionGroup group = mapper.readValue(jsonInput, DiseasePortalConditionGroup.class);
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
			filterList.add(cq.genFilter(hdpFinder));
		}

		if("AND".equals(group.getOperator())) {
			return Filter.and(filterList);
		} else {
			return Filter.or(filterList);
		}
	}

	/* strip the markup out of the allele combination and leave just the allele symbols
	 */
	private String stripAlleleMarkup(String combination) {
		return combination.replaceAll("\\\\Allele.[^|]*.([^|]*).[^)]*.", "$1");
	}
	
    @RequestMapping(value="genoCluster/view/{genoClusterKey:.+}", method = RequestMethod.GET)
    public ModelAndView genoClusterView(@PathVariable("genoClusterKey") String genoClusterKey,
    	@RequestParam(value="structureID", required=false) String structureID)
    {
    	if (!FewiUtil.isPositiveInteger(genoClusterKey)) {
            return errorMav("No GenoCluster Found");
    	}

    	List<HdpGenoCluster> genoClusters = hdpFinder.getGenoClusterByKey(genoClusterKey);
    	// there can be only one...
        if (genoClusters.size() < 1) { // none found
            return errorMav("No GenoCluster Found");
        }
        HdpGenoCluster genoCluster = genoClusters.get(0);
        String plainPairs = stripAlleleMarkup(genoCluster.getGenotype().getCombination3()).replaceAll("\n", " ");
        String superscriptPairs = FormatHelper.superscript(plainPairs);
        
        List<String> strainList = new ArrayList<String>();
        for (Genotype g : genoCluster.getGenotypes()) {
        	strainList.add(g.getBackgroundStrain());
        }
        String strains = null; 
        if (strainList.size() > 1) {
        	strains = "(" + join(") or (", strainList) + ")";
        } else {
        	strains = strainList.get(0);
        }

        ModelAndView mav = new ModelAndView("hmdc/genocluster_detail");
        mav.addObject("genoCluster",genoCluster);
        mav.addObject("plainPairs", plainPairs);
        mav.addObject("superscriptPairs", superscriptPairs);
        mav.addObject("strains", strains);
        if ((structureID != null) && (structureID.trim().length() > 0)) {
        	List<VocabTerm> terms = vocabFinder.getTermByID(structureID);
        	if ((terms != null) && (terms.size() > 0)) {
        		mav.addObject("genoClusterKey",genoClusterKey);
        		mav.addObject("structureID", structureID);
        		mav.addObject("structureTerm", terms.get(0).getTerm());
        	}
        }
    	return mav;
    }

	/* join the given list of terms into a single string, separated by the given delimeter
	 */
	private String join (String delimiter, List<String> terms) {
		StringBuffer sb = new StringBuffer();
		
		if (terms != null) {
			boolean isFirst = true;
			for (String t : terms) {
				if (!isFirst) { sb.append(delimiter); }
				sb.append(t);
				isFirst = false;
			}
		}
		return sb.toString();
	}
	
	/* build and return the page title for the popup, based on the parameters passed in.
	 * (could be done in the JSP, but it was getting complex, so we may as well do it in Java)
	 */
	private String buildPopupTitle(List<String> humanMarkers, List<String> mouseMarkers, String header, boolean isPhenotype, HmdcAnnotationGroup mpGroup,
			HmdcAnnotationGroup hpoGroup, HmdcAnnotationGroup diseaseGroup, boolean fromMarkerDetail) {

		StringBuffer sb = new StringBuffer();
		boolean human = ((hpoGroup != null) && (!hpoGroup.isEmpty())) ||
			((diseaseGroup != null) && diseaseGroup.hasHumanRows());
		boolean mouse = ((mpGroup != null) && (!mpGroup.isEmpty())) ||
			((diseaseGroup != null) && diseaseGroup.hasMouseRows());
		
		// MP title formats:
		//   1. Human and Mouse <header> abnormalities for <human genes>/<mouse genes>
		//   2. Human <header> abnormalities for <human genes>/<mouse genes>
		//   3. <header> abnormalities for <human genes>/<mouse genes>
		//	 *. if header is "normal phenotype", skip the word "abnormalities"
		// Disease title formats:
		//	 1. Human Genes and Mouse Models for <header> and <human genes>/<mouse genes>
		//	 2. Human Genes for <header> and <human genes>/<mouse genes>
		//	 3. Mouse Models for <header> and <human genes>/<mouse genes>
		// marker detail title format (from MP slimgrid on marker detail):
		//   1. Phenotype annotations related to <header>
		
		if (fromMarkerDetail) {
			sb.append("Phenotype annotations related to ");
			sb.append(header);
			return sb.toString();
		}

		// begin with organisms of included markers
		if (isPhenotype) {
			if (human) {
				if (mouse) { sb.append("Human and Mouse "); }
				else { sb.append("Human "); }
			} else if (mouse) { sb.append("Mouse "); }
		} else {
			if (human) {
				if (mouse) { sb.append("Human Genes and Mouse Models for "); }
				else { sb.append("Human Genes for "); }
			} else if (mouse) { sb.append("Mouse Models for "); }
		}
		
		// MP or Disease header term
		if (header != null) { sb.append(header); }
		
		// most MP terms have "abnormalities" in the title, but not Disease
		if (isPhenotype) {
			if (!header.equalsIgnoreCase("normal phenotype")) { sb.append(" abnormalities for "); }
			else { sb.append(" for "); }
		}
		else { sb.append(" and "); }		// Disease
		
		// if both human and mouse markers, separate the organisms with a slash
		if (humanMarkers != null && humanMarkers.size() > 0) {
			sb.append(join(", ", humanMarkers));
			if (mouseMarkers != null && mouseMarkers.size() > 0) {
				sb.append("/");
			}
		}
		if (mouseMarkers != null && mouseMarkers.size() > 0) {
			sb.append(join(", ", mouseMarkers));
		}
		
		return sb.toString();
	}

	/* get the grid cluster key corresponding to a given marker; assumes markerID is a valid MGI 
	 * marker (primary) ID
	 */
	private Integer getGridClusterKey(String markerID) {
		SearchParams params = new SearchParams();
		params.setPageSize(100);
		params.setFilter(new Filter(DiseasePortalFields.MARKER_MGI_ID, markerID));

		SearchResults<SolrHdpMarker> searchResults = hdpFinder.getMarkers(params);
		List<SolrHdpMarker> results = searchResults.getResultObjects();

		if (results != null && results.size() > 0) {
			return results.get(0).getGridClusterKey();
		}
		return null;
	}

	@RequestMapping(value="/searchPopup")
	public ModelAndView searchPopup () {

		logger.debug("->searchPopup started");

		ModelAndView mav = new ModelAndView("hmdc/popup_mphp_search");

		return mav;
	}

	@RequestMapping("/searchPopupJson")
	public @ResponseBody JsonSummaryResponse<MpHpPopupRow>
	searchPopupJson(HttpServletRequest request, @ModelAttribute AccessionQueryForm query) {

		logger.debug("->searchPopupJson() started");
		logger.debug("queryForm: " + query.toString().trim());

		List<String> items = Arrays.asList(query.getId().trim().split("[\\s,]+"));
        List <Filter> filterList = new ArrayList<Filter>();
        for (String item: items) {
            filterList.add(new Filter ("searchTermID" , item, Filter.Operator.OP_EQUAL));
        }

		// create query and gather results via finder
        SearchParams params = new SearchParams();
        params.setFilter(Filter.or(filterList));
		SearchResults<SolrMpHpPopupResult> results = mpHpPopupFinder.getMpHpPopupResult(params);
        List<SolrMpHpPopupResult> resultList = results.getResultObjects();
		logger.debug("Number of results: " + resultList.size());

		List<MpHpPopupRow> summaryRows = new ArrayList<MpHpPopupRow>();
		for(SolrMpHpPopupResult result: resultList) {

			summaryRows.add(new MpHpPopupRow(result.getSearchTermID(), 
				result.getSearchTerm(), 
				result.getSearchTermDefinition(), 
				result.getMatchType(), 
				result.getMatchMethod(), 
				result.getMatchTermID(), 
				result.getMatchTerm(), 
				result.getMatchTermSynonym(), 
				result.getMatchTermDefinition()));
		}

		JsonSummaryResponse<MpHpPopupRow> jsonResponse = new JsonSummaryResponse<MpHpPopupRow>();
		jsonResponse.setSummaryRows(summaryRows);
		return jsonResponse;
	}




	/* Serve up a phenotype or disease popup, from clicking a cell on the HMDC grid.
	 * Expects two parameters in the request: gridClusterKey and header.  
	 * The 'isPhenotype' parameter should be true for phenotype popups and false for disease popups.
	 */
	@RequestMapping(value="/popup", method=RequestMethod.GET)
	public ModelAndView popup(HttpServletRequest request, HttpSession session, @RequestParam(value = "isPhenotype") String isPhenotype, @RequestParam(value = "queryToken", required=false) String queryToken) throws Exception {
	//public ModelAndView popup(HttpServletRequest request) {
		// from a marker detail page slimgrid, we'll need to get the marker ID and
		// convert it to its corresponding gridClusterKey
		//String queryToken = "";

		boolean isPheno = false;
		if ("true".equalsIgnoreCase(isPhenotype)) {
			isPheno = true;
		}
		
		String gridClusterKey = null;
		String markerID = request.getParameter("markerID");
		boolean fromMarkerDetail = false;
		System.out.println("isPhenotype: " + isPheno);
		if (markerID != null) {
			Integer gck = getGridClusterKey(markerID);
			if (gck != null) {
				gridClusterKey = gck.toString();
			}
			fromMarkerDetail = true;
		}

		// collect the required parameters and check that they are specified
		if (gridClusterKey == null) {
			gridClusterKey = request.getParameter("gridClusterKey");
		}
		if (gridClusterKey == null) { return errorMav("Missing gridClusterKey parameter"); }

		String header = request.getParameter("header");
		if (header == null) { return errorMav("Missing header parameter"); }

		DiseasePortalConditionGroup group = null;
		Filter highlightFilter = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String previousJsonQuery = null;
			if(queryToken != null && queryToken.length() > 0) {
				LinkedHashMap<String, String> queryHistoryMap = (LinkedHashMap<String, String>) session.getAttribute("queryHistoryMap");
				if(queryHistoryMap != null) {
					previousJsonQuery = queryHistoryMap.get(queryToken);
				}
			}
			if(previousJsonQuery != null) {
				group = mapper.readValue(previousJsonQuery, DiseasePortalConditionGroup.class);
				highlightFilter = genHighlightFilter(group);
			}
		} catch (Exception e) {
			// This is a fatal error; if the user's session has expired or if JBoss has been restarted,
			// pheno group wants to give an error message.
		}

		if(group == null && fromMarkerDetail) {
			group = new DiseasePortalConditionGroup();
		} else if(group == null) {
			return errorMav("Your session has expired.  Please resubmit your search before visiting a popup.");
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
		
		// grid key -> genocluster key (for mouse gridKeys)
		Map<Integer,Integer> genoClusterKeys = new HashMap<Integer,Integer>();
		
		// grid key -> allele pairs (for mouse gridKeys)
		Map<Integer,String> allelePairs = new HashMap<Integer,String>();
		
		// genocluster key -> sequence num
		Map<Integer,Integer> genoClusterSeqNum = new HashMap<Integer,Integer>();
		
		// set of conditional genocluster keys
		Set<Integer> conditionalGenoclusters = new HashSet<Integer>();
		
		List<SolrHdpGridEntry> gridResults = results.getResultObjects();
		List<GridGenocluster> genoclusters = new ArrayList<GridGenocluster>(gridResults.size());

		for(SolrHdpGridEntry res: gridResults) {
			Integer gridKey = res.getGridKey();
			gridKeys.add(gridKey.toString());
			genoclusters.add(res.getGridGenocluster());
			
			// mouse gridKeys have a non-null genocluster key; human data are not in genoclusters
			if (res.getGenoClusterKey() != null) {
				// is mouse data
				genoClusterKeys.put(gridKey, res.getGenoClusterKey());
				allelePairs.put(gridKey, res.getAllelePairs());
				mouseGridKeys.add(gridKey); 
				genoClusterSeqNum.put(res.getGenoClusterKey(), res.getByGenoCluster());
				if (res.isConditional()) {
					conditionalGenoclusters.add(res.getGenoClusterKey());
				}
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
		
		/* pick up the highlights, if we had a pheno/disease search condition
		 */
		Map<String,Integer> highlightedTerms = new HashMap<String,Integer>();

		if ((highlightFilter != null) && highlightFilter.hasNestedFilters()) {
			annotationFilters.add(highlightFilter);
			params.setFilter(Filter.and(annotationFilters));
			params.setPageSize(1000000);
			List<String> highLightedDocumentIDList = hdpFinder.getHighlightedDocumentIDs(params);

			Set<String> highlightedDocumentIDs = new HashSet<String>();
			if (highLightedDocumentIDList != null) {
				highlightedDocumentIDs.addAll(highLightedDocumentIDList);
			}
		
			for (SolrHdpGridAnnotationEntry result : annotationResults.getResultObjects()) {
				if (highlightedDocumentIDs.contains(result.getUniqueKey())) {
					highlightedTerms.put(result.getTerm(),1);
				}
			}
		}

		/* need to track these to see which rows to show in the legend on the pheno popup
		 */
		boolean anyBackgroundSensitive = false;
		boolean anyNormalQualifiers = false;
		
		/* need to split the annotation results up into their categories (one per table displayed):
		 *	1. mouse genotype/phenotype (MP) annotations
		 *	2. human marker/phenotype (HPO) annotations
		 *	3. mouse genotype/DO annotations and human marker/DO annotations
		 */
		HmdcAnnotationGroup mpGroup = new HmdcAnnotationGroup(false);
		HmdcAnnotationGroup hpoGroup = new HmdcAnnotationGroup(false);
		HmdcAnnotationGroup diseaseGroup = new HmdcAnnotationGroup(true);

		for (SolrHdpGridAnnotationEntry result : annotationResults.getResultObjects()) {
			Integer gridKey = result.getGridKey();
			String termType = result.getTermType();
			
			if (!anyBackgroundSensitive) {
				String bSensitive = result.getBackgroundSensitive();
				if (bSensitive != null && bSensitive.length() > 0) {
					anyBackgroundSensitive = true;
				}
			}
			if (!anyNormalQualifiers) {
				String qualifier = result.getQualifier();
				if (qualifier != null && qualifier.equalsIgnoreCase("normal")) {
					anyNormalQualifiers = true;
				}
			}
			
			if ("Disease Ontology".equals(termType)) {
				if (mouseGridKeys.contains(gridKey)) {
					// is mouse genotype/disease annotation
					diseaseGroup.addMouseAnnotation(allelePairs.get(gridKey), result.getTerm(), result.getByDagTerm(),
						genoClusterKeys.get(gridKey), genoClusterSeqNum.get(genoClusterKeys.get(gridKey)), false, false,
						conditionalGenoclusters.contains(genoClusterKeys.get(gridKey)));
				} else { 
					// is human marker/disease annotation
					diseaseGroup.addHumanAnnotation(humanSymbols.get(gridKey), homologyClusterKeys.get(gridKey),
						result.getTerm(), result.getTerm(), result.getByDagTerm());
				}
				diseaseGroup.cacheDiseaseID(result.getTerm(), result.getTermId());
			} else if ("Mammalian Phenotype".equals(termType)) {
				// is mouse genotype/MP annotation
				String term = result.getTerm();
				Integer seqNum = result.getByDagTerm();
					
				// special case where we want to promote the MP header to the leftmost column
				if (term.equals(header) || term.equals(header + " phenotype")) { seqNum = -1; }
					
				mpGroup.addMouseAnnotation(allelePairs.get(gridKey), term, seqNum, genoClusterKeys.get(gridKey),
					genoClusterSeqNum.get(genoClusterKeys.get(gridKey)),
					result.isNormalAnnotation(), result.isBackgroundSensitive(),
					conditionalGenoclusters.contains(genoClusterKeys.get(gridKey)));
			} else {
				// is human marker/HPO annotation (generated via DO-HPO mapping)
				hpoGroup.addHumanAnnotation(humanSymbols.get(gridKey), homologyClusterKeys.get(gridKey),
					result.getSourceTerm(), result.getTerm(), result.getByDagTerm());
				if (result.getSourceId() != null) {
					hpoGroup.cacheDiseaseID(result.getSourceTerm(), result.getSourceId());
				}
			}
		}
		
		// begin collecting the mav to return
		ModelAndView mav = new ModelAndView("hmdc/popup");
		
		// add the required parameters
		mav.addObject("gridClusterKey", gridClusterKey);
		mav.addObject("headerTerm", header);

		// squish the multiple human gene/disease rows down into one row per gene
		diseaseGroup.consolidateHumanRows();
		
		// compose the popup title (could do in JSP, but it was getting complex...)
		mav.addObject("pageTitle", buildPopupTitle(humanMarkers, mouseMarkers, header, isPheno, mpGroup, hpoGroup, diseaseGroup, fromMarkerDetail));
		
		if (isPheno) {
			mav.addObject("isPhenotype", 1);
			if (!mpGroup.isEmpty()) mav.addObject("mpGroup", mpGroup);
			if (!hpoGroup.isEmpty()) mav.addObject("hpoGroup", hpoGroup);
			if (highlightedTerms.size() > 0) mav.addObject("highlights", highlightedTerms);
		} else {
			mav.addObject("isDisease", 1);
			if (!diseaseGroup.isEmpty()) mav.addObject("diseaseGroup", diseaseGroup);
		}
		
		if (mouseMarkers != null && mouseMarkers.size() > 0) { mav.addObject("mouseMarkers", mouseMarkers.toArray(new String[0])); }
		if (humanMarkers != null && humanMarkers.size() > 0) { mav.addObject("humanMarkers", humanMarkers.toArray(new String[0])); }

		mav.addObject("gridKeyCount", gridKeys.size());
		mav.addObject("annotationCount", annotationResults.getTotalCount());
		mav.addObject("genoclusters", genoclusters);

		if (anyBackgroundSensitive) { mav.addObject("bSensitiveFlag", 1); }
		if (anyNormalQualifiers) { mav.addObject("normalFlag", 1); }
		
		if (fromMarkerDetail) {
			mav.addObject("fromMarkerDetail", 1);
			
			try {
				SearchResults<Marker> markerResults = markerFinder.getMarkerByID(markerID);
				List<Marker> markers = markerResults.getResultObjects();
				if ((markers != null) && (markers.size() > 0)) {
					mav.addObject("marker", markers.get(0));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return errorMav("Cannot find gene identified by " + markerID);
			}
		}
		return mav;
	}

	/* build and return a mav for a page with the given error message
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

	@RequestMapping(value="/marker/report*")
	public ModelAndView resultsMarkerSummaryExport(@RequestParam("jsonEncodedInput") String jsonEncodedInput) throws Exception  {
		if(jsonEncodedInput == null) return errorMav("No Json String");
		ModelAndView mav = new ModelAndView("hdpMarkersSummaryReport");
		List<SolrHdpMarker> geneList = geneQuery(jsonEncodedInput);
		mav.addObject("geneList", geneList);
		return mav;
	}

	@RequestMapping(value="/disease/report*")
	public ModelAndView resultsDiseaseSummaryExport(@RequestParam("jsonEncodedInput") String jsonEncodedInput) throws Exception {
		if(jsonEncodedInput == null) return errorMav("No Json String");
		ModelAndView mav = new ModelAndView("hdpDiseaseSummaryReport");
		List<SolrHdpDisease> diseaseList = diseaseQuery(jsonEncodedInput);
		mav.addObject("diseaseList", diseaseList);
		return mav;
	}
}
