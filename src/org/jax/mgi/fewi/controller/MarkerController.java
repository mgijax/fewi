package org.jax.mgi.fewi.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.HomologyCluster;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerAlleleAssociation;
import mgi.frontend.datamodel.MarkerBiotypeConflict;
import mgi.frontend.datamodel.MarkerCountSetItem;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerIDOtherMarker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerProbeset;
import mgi.frontend.datamodel.MarkerQtlExperiment;
import mgi.frontend.datamodel.MarkerSynonym;
import mgi.frontend.datamodel.OrganismOrtholog;
import mgi.frontend.datamodel.QueryFormOption;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.RelatedMarker;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceSource;
import mgi.frontend.datamodel.sort.SmartAlphaComparator;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.antlr.BooleanSearch.BooleanSearch;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.detail.MarkerDetail;
import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.QueryFormOptionFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.WKSilversMarkerFinder;
import org.jax.mgi.fewi.forms.AlleleQueryForm;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.forms.MarkerQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrSummaryMarker;
import org.jax.mgi.fewi.summary.GxdImageSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerSummaryRow;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.GOGraphConverter;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.TssMarkerWrapper;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.file.TextFileReader;
import org.jax.mgi.fewi.util.link.FewiLinker;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.fewi.util.link.ProviderLinker;
import org.jax.mgi.shr.fe.util.TextFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/marker")
public class MarkerController {

	// constants for polymorphism data
	private static String PCR = "PCR";
	private static String RFLP = "RFLP";
	
	private final Logger logger = LoggerFactory.getLogger(MarkerController.class);

	@Autowired
	private GXDController gxdController;

	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private WKSilversMarkerFinder wkSilversMarkerFinder;

	@Autowired
	private DbInfoFinder dbInfoFinder;

	@Autowired
	private QueryFormOptionFinder queryFormOptionFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired
	private IDLinker idLinker;

	@Autowired
	private SessionFactory sessionFactory;

	// cached data for query form
	private Map<String,String> chromosomeOptions = null;
	private String featureTypeHtml = null;
	private String featureTypeJson = null;
	private String snpBuildNumber = null;
	private String genomeBuild = null;
	private String mpHeaders = null;

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->getQueryForm started");
		initQueryForm();

		ModelAndView mav = new ModelAndView("marker_query");
		mav.addObject("sort", new Paginator());
		mav.addObject("chromosomes", chromosomeOptions);
		mav.addObject("htmlMcv", featureTypeHtml);
		mav.addObject("jsonMcv", featureTypeJson);
		mav.addObject("genomeBuild", genomeBuild);
		mav.addObject("snpBuildNumber", snpBuildNumber);
		mav.addObject("queryForm",new MarkerQueryForm());
		return mav;
		
	}

	@RequestMapping("/phenoPopup")
	public ModelAndView phenoPopup (HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		if (mpHeaders == null) {
			SearchResults<QueryFormOption> mpResults = queryFormOptionFinder.getQueryFormOptions("allele", "phenotype");
			List<QueryFormOption> headers = mpResults.getResultObjects();
			logger.debug("phenoPopup() received " + headers.size() + " MP header options");

			StringBuffer phenoHeaders = new StringBuffer();

			for (QueryFormOption header : headers) {
				phenoHeaders.append("<option value='");
				phenoHeaders.append(header.getSubmitValue());
				phenoHeaders.append("'>");
				phenoHeaders.append(header.getDisplayValue());
				phenoHeaders.append("</option>");
			}
			mpHeaders = phenoHeaders.toString();
		}

		ModelAndView mav = new ModelAndView("phenotype_popup");
		mav.addObject("mpHeaders", mpHeaders);
		return mav;
	}

	@RequestMapping("/summary")
	public ModelAndView markerSummary(HttpServletRequest request, @ModelAttribute MarkerQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->markerSummary started");
		logger.debug("queryString: " + request.getQueryString());

		// flag any errors for start and end marker, if specified
		String startMarker = queryForm.getStartMarker();
		String endMarker = queryForm.getEndMarker();
		if(notEmpty(startMarker) || notEmpty(endMarker)) {
			if(!notEmpty(startMarker)) {
				return errorMav("start marker not specified");
			} else if(!notEmpty(endMarker)) {
				return errorMav("end marker not specified");
			}
			// query for the start and end markers to check their coordinates and chromosomes
			// check that we can find the start marker
			SearchParams sp = new SearchParams();
			sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,startMarker,Filter.Operator.OP_EQUAL));
			SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);
			if(sr.getTotalCount()<1) {
				return errorMav("start marker <b>"+startMarker+"</b> not found");
			} else if(sr.getTotalCount()>1) {
				return errorMav("start marker symbol <b>"+startMarker+"</b> matches multiple markers");
			}
			SolrSummaryMarker startMarkerObj = sr.getResultObjects().get(0);

			// check that we can find the end marker
			sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,endMarker,Filter.Operator.OP_EQUAL));
			sr = markerFinder.getSummaryMarkers(sp);
			if(sr.getTotalCount()<1) {
				return errorMav("end marker <b>"+endMarker+"</b> not found");
			} else if(sr.getTotalCount()>1) {
				return errorMav("end marker symbol <b>"+endMarker+"</b> matches multiple markers");
			}
			SolrSummaryMarker endMarkerObj = sr.getResultObjects().get(0);

			// check chromosome
			if(!startMarkerObj.getChromosome().equals(endMarkerObj.getChromosome())) {
				return errorMav("Marker <b>"+startMarker+"("+startMarkerObj.getChromosome()+")</b> not on same chromosome as " +
						"<b>"+endMarker+"("+endMarkerObj.getChromosome()+")</b>");
			}

			// check that coordinate exist for both
			if(!notEmpty(startMarkerObj.getCoordStart()) || !notEmpty(startMarkerObj.getCoordEnd())) {
				return errorMav("start marker <b>"+startMarker+"</b> has no coordinates");
			}
			if(!notEmpty(endMarkerObj.getCoordStart()) || !notEmpty(endMarkerObj.getCoordEnd())) {
				return errorMav("end marker <b>"+endMarker+"</b> has no coordinates");
			}
		}

		initQueryForm();

		ModelAndView mav = new ModelAndView("marker_summary");
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("queryForm", queryForm);

		mav.addObject("markerIds", getQueryIdsAsJson(request, queryForm));
		mav.addObject("chromosomes", chromosomeOptions);
		mav.addObject("htmlMcv", featureTypeHtml);
		mav.addObject("jsonMcv", featureTypeJson);
		mav.addObject("snpBuildNumber", snpBuildNumber);
		mav.addObject("genomeBuild", genomeBuild);

		return mav;
	}

	@RequestMapping("/batch")
	public String forwardToBatch(HttpServletRequest request,@ModelAttribute MarkerQueryForm query) {
		logger.debug("forwarding markers to batch");


		SearchParams sp = new SearchParams();
		sp.setPageSize(250000);
		sp.setFilter(genFilters(query));

		SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);

		List<SolrSummaryMarker> markers = sr.getResultObjects();

		StringBuffer ids = new StringBuffer();
		for(SolrSummaryMarker marker : markers) {
			//logger.info("marker="+marker);
			ids.append(marker.getMgiId() + ", ");
		}

		BatchQueryForm batchQueryForm = new BatchQueryForm();
		batchQueryForm.setIds(ids.toString());

		request.setAttribute("queryForm", batchQueryForm);

		return "forward:/mgi/batch/forwardSummary";
	}

	private String getQueryIdsAsJson(HttpServletRequest request, @ModelAttribute MarkerQueryForm query) {

		SearchParams sp = new SearchParams();
		sp.setPageSize(250000);
		sp.setFilter(genFilters(query));

		SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);

		List<SolrSummaryMarker> markers = sr.getResultObjects();

		StringBuffer ids = new StringBuffer();
		ids.append("[");
		for(SolrSummaryMarker marker : markers) {
			ids.append("\"" + marker.getMgiId() + "\",");
		}
		ids.append("]");
		return ids.toString();
	}


	/* return a mav for an error screen with the given message filled in
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

	@RequestMapping(value="/phenotypes/report.xlsx", params={"markerId"})
	public ModelAndView downloadPhenotypes(HttpServletRequest request,
		@RequestParam("markerId") String mrkID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}


		logger.debug("->downloadPhenotypes started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, mrkID);
		searchParams.setFilter(markerIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No Marker Found");
			return mav;
		}
		if (markerList.size() > 1) { // dupe found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Duplicate ID");
			return mav;
		}

		ModelAndView mav = new ModelAndView("markerPhenotypesReport");

		mav.addObject("marker", markerList.get(0));

		String multigenic = request.getParameter("multigenic");
		if (multigenic != null) {
			logger.debug("multigenic = " + multigenic);
		} else {
			logger.debug("multigenic = null");
		}
		if ("1".equals(multigenic)) {
			mav.addObject("multigenic", "1");
			logger.debug("added multigenic param = " + multigenic);
		}
		return mav;
	}

	@RequestMapping(value="/phenotypes/{mrkID:[MGImgi0-9:]+}")
	public ModelAndView tableOfPhenotypes(HttpServletRequest request,
		@PathVariable("mrkID") String mrkID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->tableOfPhenotypes started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, mrkID);
		searchParams.setFilter(markerIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No Marker Found");
			return mav;
		}
		if (markerList.size() > 1) { // dupe found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Duplicate ID");
			return mav;
		}

		ModelAndView mav = new ModelAndView("marker_phenotypes");

		mav.addObject("marker", markerList.get(0));

		String multigenic = request.getParameter("multigenic");
		if ("1".equals(multigenic)) {
			mav.addObject("multigenic", "1");
		}
		return mav;
	}

	@RequestMapping(value="/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView markerDetailByID(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->markerDetailByID started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
		searchParams.setFilter(markerIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No Marker Found");
			return mav;
		}
		if (markerList.size() > 1) { // dupe found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Duplicate ID");
			return mav;
		}

		return prepareMarker(markerList.get(0));
	}

	/* look up a bunch of extra data and toss it in the MAV; a convenience
	 * method for use by the individual methods to render markers by ID or
	 * by key
	 */
	private ModelAndView prepareMarker(Marker marker) {

		// if this marker is a transgene with only one allele, then we should
		// go to the allele detail page (special case)

		if ("Transgene".equals(marker.getMarkerType()) && (marker.getCountOfAlleles() == 1)) {
			// need to look up the allele ID for this transgene marker

			try {
				MarkerAlleleAssociation assoc = marker.getAlleleAssociations().get(0);

				String alleleID = assoc.getAllele().getPrimaryID();

				FewiLinker linker = FewiLinker.getInstance();
				ModelAndView mav = new ModelAndView("redirect:" + linker.getFewiIDLink(ObjectTypes.ALLELE, alleleID));
				return mav;
			} catch (Exception e) {
				logger.error("Could not find allele ID for transgene marker", e);
				ModelAndView mav = new ModelAndView("error");
				mav.addObject("errorMsg", "Could not find allele ID for transgene marker");
				return mav;
			}
		}

		// generate ModelAndView object to be passed to detail page
		ModelAndView mav = new ModelAndView("marker/marker_detail");

		// set specific hibernate filters to omit data that does not appear on this page. (for performance)
		sessionFactory.getCurrentSession().enableFilter("markerDetailRefs");
		sessionFactory.getCurrentSession().enableFilter("markerDetailMarkerInteractions");

		// search engine optimization data
		ArrayList<String> seoKeywords = new ArrayList<String>();
		ArrayList<String> seoDataTypes = new ArrayList<String>();
		StringBuffer seoDescription = new StringBuffer();

		setupSEO(marker, seoKeywords, seoDataTypes, seoDescription);

		setupPageVariables(mav, marker);

		setupRibbon1(mav, marker);
		setupLocationRibbon(mav, marker);
		setupRibbon5(mav, marker);
		setupDiseaseRibbon(mav, marker);
		setupHomologyRibbons(mav, marker);
		setupInteractinosRibbon(mav, marker);
		setupGoClassificationsRibbon(mav, marker, seoDataTypes);
		setupExpressionRibbon(mav, marker, seoDataTypes);
		setupOtherDatabaseLinks(mav, marker, seoDescription);
		setupSequencesRibbon(mav, marker);
		setupProteinAnnotationsRibbon(mav, marker);
		setupReferencesRibbon(mav, marker);

		finalizeSEO(mav, marker, seoKeywords, seoDataTypes, seoDescription);
		
		mav.addObject("markerDetail", new MarkerDetail(marker));

		return mav;
	}

        private class RelatedMarkerComparator extends SmartAlphaComparator<RelatedMarker> {
            public int compare(RelatedMarker m1, RelatedMarker m2) {
                return super.compare(m1.getRelatedMarkerSymbol(), m2.getRelatedMarkerSymbol());
            }    
        }    

	private void setupPageVariables(ModelAndView mav, Marker marker) {
		// Sets up variables from the queryform database
		initQueryForm();

		// add an IDLinker to the mav for use at the JSP level
		mav.addObject("idLinker", idLinker);

		// add snp build number to the page
		mav.addObject("snpBuildNumber", snpBuildNumber);

		// add the marker to mav
		mav.addObject("marker", marker);

		dbDate(mav);

		List<String> memberSymbols = new ArrayList<String>();
		for (RelatedMarker member : marker.getClusterMembers()) {
			memberSymbols.add(member.getRelatedMarkerSymbol());
		}
		mav.addObject("memberSymbols", StringUtils.join(memberSymbols, ", "));

		List<TssMarkerWrapper> tssMarkers = new ArrayList<TssMarkerWrapper>();
		for (RelatedMarker tss : marker.getTss()) {
			tssMarkers.add(new TssMarkerWrapper(marker, tss));
		}
		if (tssMarkers.size() > 0) {
			Collections.sort(tssMarkers, tssMarkers.get(0).getComparator());
			mav.addObject("tssMarkers", tssMarkers);
		}

                // 
                mav.addObject("buildNumber", ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION"));

                // Set up candidate genes data. Empty unless the current marker is a QTL with
                // candidate genes. Each "candidate" in the following is actually
                // the candidate gene + other info (like reference), and the same gene can occur
                // more than once, with difference references.
                List<RelatedMarker> candidates = marker.getCandidates();
                Collections.sort(candidates, new RelatedMarkerComparator());
                Set<String> uniqueCandidates = new HashSet<String>();
                List<String> candNotes = new ArrayList<String>();
                for (RelatedMarker cand : candidates) {
                    uniqueCandidates.add(cand.getRelatedMarkerID());
                    Marker m = cand.getRelatedMarker();
                    for (MarkerQtlExperiment qe : m.getQtlCandidateGeneNotes()) {
                        if (qe.getJnumID().equals(cand.getJnumID())) {
                            candNotes.add(qe.getNote());
                        }
                    }
                }
                mav.addObject("nCandidates", uniqueCandidates.size());
                mav.addObject("candidates", candidates);
                mav.addObject("candidateNotes", candNotes);

                // Set up candidateFor data. Empty unless the current marker is a
                // candidate gene for some QTL. Each "candidateFor" in the following is 
                // a QTL + other info (like reference), and the same QTL can occur
                // more than once, with difference references.
                List<RelatedMarker> candidateFor = marker.getCandidateFor();
                Collections.sort(candidateFor, new RelatedMarkerComparator());
                Set<String> uniqueCandidateFor = new HashSet<String>();
                List<String> candForNotes = new ArrayList<String>();
                for (RelatedMarker qtl : candidateFor) {
                    uniqueCandidateFor.add(qtl.getRelatedMarkerID());
                    Marker q = qtl.getRelatedMarker();
                    for (MarkerQtlExperiment qe : q.getQtlCandidateGeneNotes()) {
                        if (qe.getJnumID().equals(qtl.getJnumID())) {
                            candForNotes.add(qe.getNote());
                        }
                    }
                }
                mav.addObject("nCandidateFor", uniqueCandidateFor.size());
                mav.addObject("candidateFor", candidateFor);
                mav.addObject("candidateForNotes", candForNotes);

                // Set up interactingQTL data. Empty unless current marker is a
                // QTL that has interaction relationships with other QTL.
                List<RelatedMarker> interactingQTL = marker.getInteractingQTL();
                Collections.sort(interactingQTL, new RelatedMarkerComparator());
                mav.addObject("nInteractingQTL", interactingQTL.size());
                mav.addObject("interactingQTL", interactingQTL);

                // Set PARtner gene data. Empty unless current marker has a 
                // PARtner gene
                List<RelatedMarker> parGenes = marker.getParGenes();
                Collections.sort(parGenes, new RelatedMarkerComparator());
                mav.addObject("hasParGene", parGenes.size() > 0);
                if (parGenes.size() > 0) {
                    mav.addObject("parGene", parGenes.get(0));
                }
	}

	private void setupRibbon1(ModelAndView mav, Marker marker) {

		String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

		// Ribbon 1
		mav.addObject("hasClusters", marker.getClusters().size() > 0);
		mav.addObject("hasClusterMembers", marker.getClusterMembers().size() > 0);

		List<MarkerBiotypeConflict> conflicts = marker.getBiotypeConflicts();
		if ((conflicts != null) && (conflicts.size() > 0)) {
			StringBuffer conflictTable = new StringBuffer();
			conflictTable.append ("Biotypes are flagged as conflicting when annotations from multiple sources for the same genome feature in the same strain are different. Biotype annotations that differ among different strains for the equivalent genome feature are considered polymorphisms, not conflicts.");
			conflictTable.append ("<table class=bioMismatch>");
			conflictTable.append ("<tr class=header><td>Source</td><td>BioType</td><td>Gene ID</td></tr>");
			conflictTable.append ("<tr><td>MGI</td><td>" + marker.getMarkerSubtype() + "</td><td><a href=" + fewiUrl + "marker/" + marker.getPrimaryID() + ">" + marker.getPrimaryID() + "</a></td></tr>");
			for (MarkerBiotypeConflict conflict : conflicts) {
				conflictTable.append("<tr>");
				conflictTable.append("<td>" + conflict.getLogicalDB() + "</td>");
				conflictTable.append("<td>" + conflict.getBiotype() + "</td>");
				conflictTable.append("<td>" + idLinker.getLink(conflict.getLogicalDB(), conflict.getAccID()).replaceAll("'", "") + "</td>");
				conflictTable.append("</tr>");
			}
			conflictTable.append ("</table>");
			mav.addObject ("biotypeConflictTable", conflictTable.toString());
		}

		/* add data for strain-specific markers */
		String ssNote = marker.getStrainSpecificNote();
		if (ssNote != null) {
			List<Reference> ssRefs = marker.getStrainSpecificReferences();
			boolean isFirst = true;

			if((ssRefs != null) && (ssRefs.size() > 0)) {
				ssNote = ssNote + "(";
				for(Reference ref : ssRefs) {
					if(!isFirst) { ssNote = ssNote + ", "; }
					else { isFirst = false; }

					ssNote = ssNote + "<a href=" + fewiUrl + "reference/" + ref.getJnumID() + " target=_blank>" + ref.getJnumID() + "</a>";
				}
				ssNote = ssNote + ")";
			}
			mav.addObject ("strainSpecificNote", ssNote);
		}

		mav.addObject("memberCount", marker.getClusterMembers().size());

		mav.addObject("hasTss", marker.getTss().size() > 0);
		mav.addObject("isTssFor", marker.getTssFor().size() > 0);
		if (marker.getTssFor().size() > 0) {
			mav.addObject("tssFor", marker.getTssFor().get(0));
			NumberFormat formatter = new DecimalFormat("#,###");
			mav.addObject("distanceFrom", formatter.format(
				TssMarkerWrapper.computeDistance(
					marker.getTssFor().get(0).getRelatedMarker().getPreferredCoordinates(),
					marker.getPreferredCoordinates()) ) );
		}
		mav.addObject("tssCount", marker.getTss().size());
	}

	
	/*
	 * marker locations ribbon
	 */
	private void setupLocationRibbon(ModelAndView mav, Marker marker) {
		ArrayList<String> qtlIDs = new ArrayList<String>();

		for (MarkerID anId: marker.getIds()) {
			if (anId.getLogicalDB().equalsIgnoreCase("Download data from the QTL Archive")) {
				qtlIDs.add(idLinker.getLink(anId));
			}
		}
		mav.addObject ("qtlIDs", qtlIDs);

	}

	private void setupRibbon5(ModelAndView mav, Marker marker) {

		Properties externalUrls = ContextLoader.getExternalUrls();

		String startCoordinate = null;
		String endCoordinate = null;
		String chromosome = null;

		String ensemblLocation = null;
		MarkerLocation coords = marker.getPreferredCoordinates();
		if (coords != null) {
			startCoordinate = Long.toString(coords.getStartCoordinate().longValue());
			endCoordinate = Long.toString(coords.getEndCoordinate().longValue());
			chromosome = coords.getChromosome();
			ensemblLocation = chromosome + ":" + startCoordinate + "-" + endCoordinate;
		}

		String ensemblGenomeBrowserUrl = null;
		// Ensembl Genome Browser
		if (ensemblLocation != null) {
			ensemblGenomeBrowserUrl = externalUrls.getProperty("Ensembl_Genome_Browser");

			MarkerID ensemblGmID = marker.getEnsemblGeneModelID();

			// plug in Ensembl gene model ID, if available
			if (ensemblGmID != null) {
				ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace("<id>", ensemblGmID.getAccID());
			} else {
				ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace("g=<id>", "");
				ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace(";", "");
			}

			// plug in coordinates, if available
			ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace("<location>", ensemblLocation);
		}
		if (ensemblGenomeBrowserUrl != null) {
			mav.addObject ("ensemblGenomeBrowserUrl", ensemblGenomeBrowserUrl);
		}

		String ucscGenomeBrowserUrl = null;
		if (coords != null) {
			ucscGenomeBrowserUrl = externalUrls.getProperty("UCSC_Genome_Browser").replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);
		}
		if (ucscGenomeBrowserUrl != null) {
			mav.addObject ("ucscGenomeBrowserUrl", ucscGenomeBrowserUrl);
		}

		String gbrowseUrl = null;
		String jbrowseUrl = null;
		String gbrowseThumbnailUrl = null;
		if (coords != null) {

			gbrowseUrl = externalUrls.getProperty("GBrowse").replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);

			jbrowseUrl = externalUrls.getProperty("JBrowse").replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);

			// if this marker is a TSS, then we need a different URL that will open up the TSS track
			if ("TSS cluster".equals(marker.getMarkerSubtype())) {
				Long tssStart = Long.parseLong(startCoordinate) - 500;
				Long tssEnd = Long.parseLong(endCoordinate) + 500;
				jbrowseUrl = externalUrls.getProperty("JBrowseTSS_Highlight").replaceAll("<chromosome>", chromosome).replace("<startHighlight>", startCoordinate).replace("<endHighlight>", endCoordinate).replace("<start>", "" + tssStart).replace("<end>", "" + tssEnd);
			} else if ("Ensembl Reg".equals(coords.getProvider()) || "VISTA".equals(coords.getProvider()) ) {
                                jbrowseUrl = externalUrls.getProperty("JBrowseReg").replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);
                        }

			gbrowseThumbnailUrl = externalUrls.getProperty("GBrowse_Thumbnail").replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);

			// add tracks for special marker "types"
			if(marker.getIsSTS()) {
				jbrowseUrl = jbrowseUrl + ",STS";
				gbrowseUrl = gbrowseUrl.replace("label=","label=STS-");
				gbrowseThumbnailUrl = gbrowseThumbnailUrl.replace("t=","t=STS;t=");
			}
		}
		if (gbrowseUrl != null) {
			mav.addObject ("gbrowseUrl", gbrowseUrl);
		}
		if (jbrowseUrl != null) {
			mav.addObject ("jbrowseUrl", jbrowseUrl);
		}
		if (gbrowseThumbnailUrl != null) {
			mav.addObject ("gbrowseThumbnailUrl", gbrowseThumbnailUrl);
		}

		boolean isDnaSegment = "DNA Segment".equals(marker.getMarkerType());
		String ncbiMapViewerUrl = null;
		// NCBI Map Viewer (2 separate URLs -- see notes above)
		if ((coords != null) && !isDnaSegment) {
			ncbiMapViewerUrl = externalUrls.getProperty("NCBI_Map_Viewer_by_Coordinates");
			ncbiMapViewerUrl = ncbiMapViewerUrl.replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);
		} else if ((coords != null) && isDnaSegment) {
			ncbiMapViewerUrl = externalUrls.getProperty("NCBI_Map_Viewer_by_Coordinates_DNA_Segment");
			ncbiMapViewerUrl = ncbiMapViewerUrl.replace("<chromosome>", chromosome).replace("<start>", startCoordinate).replace("<end>", endCoordinate);
		}
		if (ncbiMapViewerUrl != null) {
			mav.addObject ("ncbiMapViewerUrl", ncbiMapViewerUrl);
		}
	}

	private void setupHomologyRibbons(ModelAndView mav, Marker marker) {
		// links to genome browsers (complex rules so put them here and
		// keep the JSP simple)

		// new simpler rules as of C4AM (coordinates for any marker) project:
		// 1. Any marker with coordinates gets links to all five genome
		//    browsers (Ensembl, NCBI, UCSC, GBrowse).  No more
		//    restrictions by marker type.
		// 2. If a marker has multiple IDs, use the first one returned to make
		//    the link.
		// 3. External links go to build 39 (GRCm39) data.
		// 4. For Ensembl, use coordinates -- or
		//    both if both are available.
		// 5. NCBI's map viewer needs to use two different URLs for cases
		//    where:
		//	a. there are coordinates and marker is not a dna segment, or
		//	b. there are coordinates and marker is a dna segment.
		// 6. If a marker has coordinates, both link to GBrowse and show a
		//    thumbnail image from GBrowse.
		// 7. The UCSC genome browser (when the marker has coordinates)


		// add human homologs to model if present
		List<OrganismOrtholog> mouseOOs = marker.getOrganismOrthologsFiltered();

		TreeMap<String, Marker> sortedMap = new TreeMap<String, Marker>();

		// Organism, Symbol Ordered, Marker
		LinkedHashMap<String, TreeMap<String, Marker>> hcopLinks = new LinkedHashMap<String, TreeMap<String, Marker>>();

		List<HomologyCluster> homologyClusteres = new ArrayList<HomologyCluster>();

		for(OrganismOrtholog mouseOO: mouseOOs) {
			HomologyCluster homologyCluster = mouseOO.getHomologyCluster();
			if (homologyCluster != null) {
				homologyClusteres.add(homologyCluster);

				OrganismOrtholog humanOO = homologyCluster.getOrganismOrtholog("human");
				
				if (humanOO != null) {
					for(Marker hh : humanOO.getMarkers()) {
						// preload these associations for better hibernate query planning
						hh.getLocations().size();
						hh.getAliases().size();
						hh.getSynonyms().size();
						hh.getBiotypeConflicts().size();
						hh.getIds().size();
						sortedMap.put(hh.getSymbol(), hh);
						if(!hcopLinks.containsKey("human")) {
							hcopLinks.put("human", new TreeMap<String, Marker>());
						}
						hcopLinks.get("human").put(hh.getSymbol(), hh);
					}
				}
			}
		}
		mav.addObject("homologyClasses", homologyClusteres);
		mav.addObject("hcopLinks", hcopLinks);

		List<Marker> humanHomologs = new ArrayList<Marker>();
		for(String key: sortedMap.keySet()) {
			humanHomologs.add(sortedMap.get(key));
		}
		mav.addObject("humanHomologs", humanHomologs);
	}

	private void setupDiseaseRibbon(ModelAndView mav, Marker marker) {
		
		HashMap<String, Annotation> allAnnotations = new HashMap<String, Annotation>();
		// DiseaseTerm -> DiseaseTermId
		TreeMap<String, String> sortedDiseaseMapByTerm = new TreeMap<String, String>(new SmartAlphaComparator<String>());
		// DiseaseId -> Annotation
		HashMap<String, Annotation> MouseDOAnnotations = new HashMap<String, Annotation>();
		for(Annotation a: marker.getDOAnnotations()) {
			if (!"NOT".equals(a.getQualifier())) {
				sortedDiseaseMapByTerm.put(a.getTerm(), a.getTermID());
				MouseDOAnnotations.put(a.getTermID(), a);
				if(!allAnnotations.containsKey(a.getTermID())) {
					allAnnotations.put(a.getTermID(), a);
				}
			}
		}

		// Symbol -> {DiseaseId -> Annoation}
		HashMap<String, HashMap<String, Annotation>> humanAnnotations = marker.getHumanHomologDOAnnotations();
		// DiseaseId -> Annoation
		HashMap<String, Annotation> HumanDOAnnotations = new HashMap<String, Annotation>();
		// Symbol -> Symbol (sorted)
		TreeMap<String, String> sortedAllHumanMarkers = new TreeMap<String, String>();
		for(String symbol: humanAnnotations.keySet()) {
			for(String annotId: humanAnnotations.get(symbol).keySet()) {
				String term = humanAnnotations.get(symbol).get(annotId).getTerm();
				sortedDiseaseMapByTerm.put(term, annotId);
				HumanDOAnnotations.put(annotId, humanAnnotations.get(symbol).get(annotId));
				if(!allAnnotations.containsKey(annotId)) {
					allAnnotations.put(annotId, humanAnnotations.get(symbol).get(annotId));
				}
			}
			sortedAllHumanMarkers.put(symbol, symbol);
		}

		// Sort Mouse first then both then Human
		ArrayList<String> mouse = new ArrayList<String>();
		ArrayList<String> both = new ArrayList<String>();
		ArrayList<String> human = new ArrayList<String>();
		
		int mouseDiseaseCount = 0;
		int bothDiseaseCount = 0;
		int humanDiseaseCount = 0;
		
		for(String diseaseTerm: sortedDiseaseMapByTerm.keySet()) {
			String diseaseId = sortedDiseaseMapByTerm.get(diseaseTerm);
			if(MouseDOAnnotations.containsKey(diseaseId) && !"NOT".equals(MouseDOAnnotations.get(diseaseId).getQualifier()) && HumanDOAnnotations.containsKey(diseaseId)) {
				both.add(diseaseTerm);
				bothDiseaseCount++;
			} else if(MouseDOAnnotations.containsKey(diseaseId)) {
				mouse.add(diseaseTerm);
				mouseDiseaseCount++;
			} else if(HumanDOAnnotations.containsKey(diseaseId)) {
				human.add(diseaseTerm);
				humanDiseaseCount++;
			}
		}
		
		// Condordance
		ArrayList<String> sortedCondordanceMouseBothHuman = new ArrayList<String>();
		sortedCondordanceMouseBothHuman.addAll(both);
		sortedCondordanceMouseBothHuman.addAll(mouse);
		sortedCondordanceMouseBothHuman.addAll(human);
		
		// List[ Column -> Data ]
		ArrayList<HashMap<String, String>> rowMap = new ArrayList<HashMap<String,String>>();
	
		int mouseCount = 0;
		int bothCount = 0;
		int humanCount = 0;
		
		for(String diseaseTerm: sortedCondordanceMouseBothHuman) {
			HashMap<String, String> row = new HashMap<String, String>();
			String diseaseId = sortedDiseaseMapByTerm.get(diseaseTerm);
			
			if(MouseDOAnnotations.containsKey(diseaseId) && HumanDOAnnotations.containsKey(diseaseId)) {
				row.put("type", "both");
				if(bothCount == 0) {
					row.put("headerRow", bothDiseaseCount + "");
					bothCount++;
				}
			} else if(MouseDOAnnotations.containsKey(diseaseId)) {
				row.put("type", "mouse");
				if(mouseCount == 0) {
					row.put("headerRow", mouseDiseaseCount + "");
					mouseCount++;
				}
			} else if(HumanDOAnnotations.containsKey(diseaseId)) {
				row.put("type", "human");
				if(humanCount == 0) {
					row.put("headerRow", humanDiseaseCount + "");
					humanCount++;
				}
			}
			
			row.put("diseaseId", diseaseId);
			row.put("diseaseTerm", diseaseTerm);
			
			rowMap.add(row);
		}


		// TODO get the linker working
		// Disease ID -> List<GenotypeId>
		mav.addObject("MouseModels", marker.getMouseModelsMap());
		mav.addObject("NotMouseModels", marker.getNotMouseModelsMap());
		mav.addObject("AllHumanSymbols", StringUtils.join(sortedAllHumanMarkers.keySet(), ","));
		mav.addObject("MouseDOAnnotations", MouseDOAnnotations);
		mav.addObject("HumanDOAnnotations", HumanDOAnnotations);
		mav.addObject("allAnnotations", allAnnotations);
		mav.addObject("DiseaseRows", rowMap);
		
		// Formaters for Super scripts
		mav.addObject("tf", new TextFormat());
		mav.addObject("ntc", new NotesTagConverter());
		
	}
	
	
	
	
	
	

	private void setupInteractinosRibbon(ModelAndView mav, Marker marker) {
		// add regulation data (compose these as individual rows here, rather
		// than in the JSP due to complexity of mapping RV terms currently)

		ArrayList<String> interactions = new ArrayList<String>();
		FewiLinker linker = FewiLinker.getInstance();

		MarkerCountSetItem countItem;
		Iterator<MarkerCountSetItem> interactionIterator = marker.getInteractionCountsByType().iterator();

		while (interactionIterator.hasNext()) {
			countItem = interactionIterator.next();

			String countType = countItem.getCountType();
			int count = countItem.getCount();

			StringBuffer sb = new StringBuffer();

			sb.append(marker.getSymbol());
			sb.append(" ");
			sb.append(countType);
			sb.append(" ");
			sb.append(count);
			sb.append(" marker");
			if (count > 1) {
				sb.append("s");
			}

			Iterator<Marker> teaserIt = marker.getInteractionTeaserMarkers().iterator();

			if(teaserIt != null) {
				sb.append(" (");

				Marker rm;
				while (teaserIt.hasNext()) {
					rm = teaserIt.next();

					sb.append("<a href='");
					sb.append(linker.getFewiIDLink(ObjectTypes.MARKER, rm.getPrimaryID() ));
					sb.append("'>");
					sb.append(rm.getSymbol());
					sb.append("</a>");

					if (teaserIt.hasNext()) {
						sb.append(", ");
					}

				}

				if (count > 3) {
					sb.append(", ...");
				}
				sb.append(")");
			}
			interactions.add(sb.toString());
		}

		/* Logical values for cluster members section */
		mav.addObject("interactions", interactions);

	}

	/* For the GO ribbon, we need to update the SEO data types and to add
	 * the reference genome project URL.
	 */
	private void setupGoClassificationsRibbon(ModelAndView mav, Marker marker, ArrayList<String> seoDataTypes) {

		if (!(marker.getGoProcessAnnotations().isEmpty()
			&& marker.getGoComponentAnnotations().isEmpty()
			&& marker.getGoFunctionAnnotations().isEmpty() ) ) {
				seoDataTypes.add("function");
		}

		String refGenomeUrl = ContextLoader.getConfigBean().getProperty("MGIHOME_URL");

		if (!refGenomeUrl.endsWith("/")) {
			refGenomeUrl = refGenomeUrl + "/";
		}
		refGenomeUrl = refGenomeUrl + "GO/reference_genome_project.shtml";
		mav.addObject ("referenceGenomeURL", refGenomeUrl);

	}

	private void setupExpressionRibbon(ModelAndView mav, Marker marker, ArrayList<String> seoDataTypes) {

		// need to pull out and re-package the expression counts for assays
		// and results

		Iterator<MarkerCountSetItem> assayIterator = marker.getGxdAssayCountsByType().iterator();
		Iterator<MarkerCountSetItem> resultIterator = marker.getGxdResultCountsByType().iterator();
		MarkerCountSetItem item;

		ArrayList<String> gxdAssayTypes = new ArrayList<String>();
		HashMap<String,String> gxdAssayCounts = new HashMap<String,String>();
		HashMap<String,String> gxdResultCounts = new HashMap<String,String>();

		while (assayIterator.hasNext()) {
			item = assayIterator.next();
			gxdAssayTypes.add(item.getCountType());
			gxdAssayCounts.put(item.getCountType(), Integer.toString(item.getCount()));
		}
		while (resultIterator.hasNext()) {
			item = resultIterator.next();
			gxdResultCounts.put(item.getCountType(), Integer.toString(item.getCount()));
		}

		mav.addObject ("gxdAssayTypes", gxdAssayTypes);
		mav.addObject ("gxdAssayCounts", gxdAssayCounts);
		mav.addObject ("gxdResultCounts", gxdResultCounts);

		// expression, sequences, polymorphisms, proteins, references keywords

		if ((gxdAssayTypes.size() > 0) || (marker.getCountOfGxdLiterature() > 0)) {
			seoDataTypes.add("expression");
		}

		// get the expression teaser image, if there is one
		if (marker.getCountOfGxdImages() > 0) {
		    GxdImageSummaryRow gxdImage = gxdController.getMarkerDetailTeaserImage(marker);
		    if (gxdImage != null) {
			mav.addObject("gxdImage", gxdImage.getImage());
		    }
		} 
	}

	private void setupOtherDatabaseLinks(ModelAndView mav, Marker marker, StringBuffer seoDescription) {

		String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

		// slice and dice the data for the "Other database links" section, to
		// ease the formatting requirements that would be cumbersome in JSTL.

		// Note: All IDs now go into 'otherIDs', but only those logical
		// databases which should appear in the 'other database links'
		// section should go into 'logicalDBs'.

		ArrayList<String> logicalDBs = new ArrayList<String>();
		HashMap<String,String> otherIDs = new HashMap<String,String>();

		Iterator<MarkerID> it = marker.getIds().iterator();
		MarkerID myID;
		String myLink;
		String logicaldb;
		String otherLinks;
		MarkerID ncbiEvidenceID = marker.getSingleID("NCBI Gene Model Evidence");
		boolean isGeneModelID = false;

		while (it.hasNext()) {
			myID = it.next();
			logicaldb = myID.getLogicalDB();
			myLink = idLinker.getLink(myID);

			// for gene model sequences, we need to add evidence links where possible
			if ("Ensembl Gene Model".equals(logicaldb)) {
				myLink = myLink + " (" + FormatHelper.setNewWindow(idLinker.getLink("Ensembl Gene Model Evidence", myID.getAccID(), "Evidence")) + ")";
				isGeneModelID = true;
			}

			// special note about gene model IDs which overlap other markers, if needed
			if (isGeneModelID) {
				List<MarkerIDOtherMarker> otherMarkers = myID.getOtherMarkers();
				MarkerIDOtherMarker otherMarker;

				if ((otherMarkers != null) && (otherMarkers.size() > 0)) {
					myLink = myLink + " (also overlaps ";
					Iterator<MarkerIDOtherMarker> markerIterator = otherMarkers.iterator();
					while (markerIterator.hasNext()) {
						otherMarker = markerIterator.next();

						myLink = myLink + "<a href='" + fewiUrl + "marker/" + otherMarker.getPrimaryID() + "'>" + otherMarker.getSymbol() + "</a>";

						if (markerIterator.hasNext()) {
							myLink = myLink + ", ";
						}
					}
					myLink = myLink + ") ";
				}
			}

			if (!otherIDs.containsKey(logicaldb)) {
				if (myID.getIsForOtherDbSection() == 1) {
					logicalDBs.add(logicaldb);
				}
				otherIDs.put(logicaldb, myLink);
			} else {
				otherLinks = otherIDs.get(logicaldb);
				otherIDs.put(logicaldb, otherLinks + ", " + myLink);
			}
		}

		// update the SEO description with information about the
		// marker's coordinates

		MarkerLocation location = marker.getPreferredCoordinates();
		if ((location != null) &&
			(location.getStartCoordinate() != null) &&
			(location.getEndCoordinate() != null) ) {

			seoDescription.append(" Chr");
			seoDescription.append(location.getChromosome());
			seoDescription.append(":");
			seoDescription.append(location.getStartCoordinate().longValue());
			seoDescription.append("-");
			seoDescription.append(location.getEndCoordinate().longValue());
		} else if (!"UN".equals(marker.getChromosome())) {
			seoDescription.append(" Chr");
			seoDescription.append(marker.getChromosome());
		}

		mav.addObject ("otherIDs", otherIDs);
		mav.addObject ("logicalDBs", logicalDBs);

	}

	private void setupSequencesRibbon(ModelAndView mav, Marker marker) {

		// pull out the strain/species and provider links for each
		// representative sequence
		Sequence repGenomic = marker.getRepresentativeGenomicSequence();
		if (repGenomic != null) {
			List<SequenceSource> genomicSources = repGenomic.getSources();
			if ((genomicSources != null) && (genomicSources.size() > 0)) {
				mav.addObject ("genomicSource", genomicSources.get(0).getStrain());
			}
			mav.addObject ("genomicLink", ProviderLinker.getSeqProviderLinks(repGenomic));
		}
		Sequence repTranscript = marker.getRepresentativeTranscriptSequence();
		if (repTranscript != null) {
			List<SequenceSource> transcriptSources = repTranscript.getSources();
			if ((transcriptSources != null) && (transcriptSources.size() > 0)) {
				mav.addObject ("transcriptSource", transcriptSources.get(0).getStrain());
			}
			mav.addObject ("transcriptLink", ProviderLinker.getSeqProviderLinks(repTranscript));
		}
		Sequence repPolypeptide = marker.getRepresentativePolypeptideSequence();
		if (repPolypeptide != null) {
			List<SequenceSource> polypeptideSources = repPolypeptide.getSources();
			if ((polypeptideSources != null) && (polypeptideSources.size() > 0)) {
				mav.addObject ("polypeptideSource", polypeptideSources.get(0).getStrain());
			}
			mav.addObject ("polypeptideLink", ProviderLinker.getSeqProviderLinks(repPolypeptide));
		}
	}

	private void setupProteinAnnotationsRibbon(ModelAndView mav, Marker marker) {
		// add a Properties object with URLs for use at the JSP level
		Properties urls = idLinker.getUrlsAsProperties();
		mav.addObject("urls", urls);
	}

	private void setupReferencesRibbon(ModelAndView mav, Marker marker) {
		// disease relevant reference count is now cached in the
		// database, rather than being retrieved from solr

		if (marker.getCountOfDiseaseRelevantReferences() > 0) {
			mav.addObject("diseaseRefCount", marker.getCountOfDiseaseRelevantReferences());
		}
	}

	private void setupSEO(Marker marker, ArrayList<String> seoKeywords, ArrayList<String> seoDataTypes, StringBuffer seoDescription) {

		seoDescription.append("View mouse ");
		seoDescription.append(marker.getSymbol());

		seoKeywords.add("MGI");
		seoKeywords.add(marker.getSymbol());
		seoKeywords.add(marker.getName());
		seoKeywords.add("mouse");
		seoKeywords.add("mice");
		seoKeywords.add("murine");
		seoKeywords.add("Mus musculus");
		seoKeywords.add(marker.getMarkerType());
		seoKeywords.add(marker.getMarkerSubtype());

		for (MarkerSynonym s : marker.getSynonyms()) {
			seoKeywords.add(s.getSynonym());
		}
		seoKeywords.add(marker.getPrimaryID());


		if (marker.getMPAnnotations().size() > 0) {
			seoDataTypes.add("phenotypes");
		}

		if (marker.getCountOfSequences() > 0) {
			seoDataTypes.add("sequences");
		}

		if (marker.getPolymorphismCountsByType().size() > 0) {
			seoDataTypes.add("polymorphisms");
		}

		if (marker.getProteinAnnotations().size() > 0) {
			seoDataTypes.add("proteins");
		}

		if (marker.getCountOfReferences() > 0) {
			seoDataTypes.add("references");
		}
	}
	private void finalizeSEO(ModelAndView mav, Marker marker, ArrayList<String> seoKeywords, ArrayList<String> seoDataTypes, StringBuffer seoDescription) {

		// SEO meta tags

		if (seoDataTypes.size() > 0) {
			seoDescription.append(" with: ");

			boolean isFirst = true;
			for (String dt : seoDataTypes) {
				if (!isFirst) {
					seoDescription.append(", ");
				}
				seoDescription.append(dt);
				isFirst = false;
			}
		}


		mav.addObject("seoDescription", seoDescription);

		if (seoKeywords.size() > 0) {
			StringBuffer seoKeywordString = new StringBuffer();
			boolean isFirst = true;
			for (String keyword : seoKeywords) {
				if (!isFirst) {
					seoKeywordString.append(", ");
				}
				seoKeywordString.append(keyword);
				isFirst = false;
			}
			mav.addObject("seoKeywords", seoKeywordString);
		}
	}



	@RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
	public ModelAndView markerDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->markerDetailByKey started");

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByKey(dbKey);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No Marker Found");
			return mav;
		}// success

		return prepareMarker(markerList.get(0));
	}

	@RequestMapping(value="/reference/{refID}")
	public ModelAndView markerSummaryByRefId(HttpServletRequest request, @PathVariable("refID") String refID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->markerSummaryByRefId started");

		// setup search parameters object to gather the requested object
		SearchParams searchParams = new SearchParams();
		Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
		searchParams.setFilter(refIdFilter);

		// find the requested reference
		SearchResults<Reference> searchResults = referenceFinder.searchReferences(searchParams);
		List<Reference> refList = searchResults.getResultObjects();

		// there can be only one...
		if (refList.size() < 1) {
			// forward to error page
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No reference found for " + refID);
			return mav;
		}
		if (refList.size() > 1) {
			// forward to error page
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe references found for " + refID);
			return mav;
		}

		return markerSummaryByRef(refList.get(0));
	}

	@RequestMapping(value="/reference/key/{refKey}")
	public ModelAndView markerSummeryByRefKey(HttpServletRequest request, @PathVariable("refKey") String refKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->markerSummeryByRefKey started");

		SearchResults<Reference> referenceResults = referenceFinder.getReferenceByKey(refKey);
		List<Reference> referenceList = referenceResults.getResultObjects();
		if (referenceList.size() < 1) {
			// forward to error page
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No reference found for " + refKey);
			return mav;
		}
		if (referenceList.size() > 1) {
			// forward to error page
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + refKey);
			return mav;
		}
		return markerSummaryByRef(referenceList.get(0));
	}

	private  ModelAndView markerSummaryByRef(Reference reference) {
		ModelAndView mav = new ModelAndView("marker_summary_reference");
		new MarkerQueryForm();

		// package data, and send to view layer
		mav.addObject("reference", reference);

		// pre-generate query string
		mav.addObject("queryString", "refKey=" + reference.getReferenceKey());
		logger.debug("markerSummaryByRef routing to view ");
		return mav;
	}

	@RequestMapping(value="/polymorphisms/pcr/{markerID}")
	public ModelAndView markerPolymorphismsPcr(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		return markerPolymorphisms(markerID, PCR);
	}

	@RequestMapping(value="/polymorphisms/rflp/{markerID}")
	public ModelAndView markerPolymorphismsRflp(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		return markerPolymorphisms(markerID, RFLP);
	}

	private ModelAndView markerPolymorphisms(String markerID, String polymorphismType) {

		logger.debug("->markerPolymorphisms started (" + markerID + ", " + polymorphismType + ")");
		
		if (!RFLP.equals(polymorphismType) && !PCR.equals(polymorphismType)) {
			return errorMav("Unknown polymorphismType: " + polymorphismType);
		}

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(markerID);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			return errorMav("No marker found for " + markerID);
		} else if (markerList.size() > 1) { // dupe found
			return errorMav("ID " + markerID + " refers to more than one marker");
		}

		Marker marker = markerList.get(0);

		// generate ModelAndView object to be passed to polymorphism page
		ModelAndView mav = new ModelAndView("marker_polymorphisms");
		mav.addObject("marker", marker);
		mav.addObject("polymorphismType", polymorphismType);
		mav.addObject("title", polymorphismType + " Polymorphisms for " + marker.getSymbol());
		mav.addObject("description", polymorphismType + " polymorphisms associated with mouse " + marker.getMarkerType()
				+ " " + marker.getSymbol() + ", " + marker.getPrimaryID());
		if (PCR.equals(polymorphismType)) {
			mav.addObject("polymorphisms", marker.getPcrPolymorphisms());
		} else {
			mav.addObject("polymorphisms", marker.getRflpPolymorphisms());
		}
		dbDate(mav);

		return mav;
	}

	@RequestMapping(value="/probeset/{markerID}")
	public ModelAndView markerProbesets(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->markerProbesets started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
		searchParams.setFilter(markerIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No Marker Found");
			return mav;
		}
		if (markerList.size() > 1) { // dupe found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Duplicate ID");
			return mav;
		}

		Marker marker = markerList.get(0);

		// generate ModelAndView object to be passed to detail page
		ModelAndView mav = new ModelAndView("marker_probeset_summary");

		// add the marker to mav
		mav.addObject("marker", marker);

		// need to collect the FTP reports and sort them

		HashMap<String,String> reports = new HashMap<String,String>();

		Iterator<MarkerProbeset> it = marker.getProbesets().iterator();
		MarkerProbeset probeset = null;

		while (it.hasNext()) {
			probeset = it.next();
			reports.put(probeset.getPlatform(), probeset.getReportName());
		}

		ArrayList<String> reportList = new ArrayList<String>(reports.keySet());
		Collections.sort(reportList);

		mav.addObject("reportsOrdered", reportList);
		mav.addObject("reports", reports);

		// add the date
		dbDate(mav);

		return mav;
	}
	
	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<MarkerSummaryRow> seqSummaryJson(HttpServletRequest request, @ModelAttribute MarkerQueryForm query, @ModelAttribute Paginator page) throws org.antlr.runtime.RecognitionException {
		logger.debug("->JsonSummaryResponse started");

		SearchResults<SolrSummaryMarker> searchResults = getSummaryMarkers(request,query,page);
		List<SolrSummaryMarker> markerList = searchResults.getResultObjects();

		// create/load the list of SummaryRow wrapper objects
		List<MarkerSummaryRow> summaryRows = new ArrayList<MarkerSummaryRow>();
		for(SolrSummaryMarker marker : markerList) {
			//logger.info("marker="+marker);
			summaryRows.add(new MarkerSummaryRow(marker));
		}
		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<MarkerSummaryRow> jsonResponse = new JsonSummaryResponse<MarkerSummaryRow>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		return jsonResponse;
	}


	/*
	 * This is exposed for our automated tests to use
	 */
	public SearchResults<SolrSummaryMarker> getSummaryMarkers(HttpServletRequest request, MarkerQueryForm query, Paginator page) throws org.antlr.runtime.RecognitionException {
		// parameter parsing
		String refKey = request.getParameter("refKey");

		// generate search parms object;  add pagination, sorts, and filters
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		// if we have nomen query, do text highlighting
		if(notEmpty(query.getNomen())) {
			params.setIncludeMetaHighlight(true);
			params.setIncludeSetMeta(true);
		}
		params.setSorts(genSorts(request,query));
		params.setFilter(genFilters(query));

		if (refKey != null) {
			params.setFilter(new Filter(SearchConstants.REF_KEY, refKey));
		}

		// perform query, and pull out the requested objects
		return markerFinder.getSummaryMarkers(params);
	}

	@RequestMapping("/report*")
	public ModelAndView markerSummaryExport(HttpServletRequest request, @ModelAttribute MarkerQueryForm query) throws org.antlr.runtime.RecognitionException {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("generating report");

		SearchParams sp = new SearchParams();
		sp.setPageSize(250000);
		sp.setFilter(genFilters(query));

		// if we have nomen query, do text highlighting
		if(notEmpty(query.getNomen())) {
			sp.setIncludeMetaHighlight(true);
			sp.setIncludeSetMeta(true);
			sp.setIncludeHighlightMarkup(false);
		}

		SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);

		List<SolrSummaryMarker> markers = sr.getResultObjects();

		ModelAndView mav = new ModelAndView("markerSummaryReport");
		mav.addObject("markers", markers);
		return mav;

	}

	/* GO graph for an individual marker (by MGI ID)
	 */
	@RequestMapping(value="/gograph/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView markerGOGraphByID(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("markerGOGraphByID started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
		searchParams.setFilter(markerIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// there can be only one...
		if (markerList.size() < 1) { // none found
			return errorMav("No Marker Found");
		}
		if (markerList.size() > 1) { // dupe found
			return errorMav("Duplicate ID");
		}

		// at this point, we have a single marker
		Marker marker = markerList.get(0);

		if (marker.getHasGOGraph() != 1) {
			return errorMav(marker.getSymbol() + " has no GO Graph");
		}

		String goGraphText = null;
		try {
			String goGraphPath = ContextLoader.getConfigBean().getProperty("GO_GRAPHS_PATH");

			if (!goGraphPath.endsWith("/")) {
				goGraphPath = goGraphPath + "/marker/" + markerID.replace(":", "_") + ".html";
			} else {
				goGraphPath = goGraphPath + "marker/" + markerID.replace(":", "_") + ".html";
			}

			logger.debug("Reading GO Graph from: " + goGraphPath);

			goGraphText = TextFileReader.readFile(goGraphPath);

			if (goGraphText == null) {
				logger.debug("GO Graph text is null");
			} else {
				logger.debug("GO Graph text length: " + goGraphText.length());
			}

			// convert special MGI markups to their full HTML equivalents

			NotesTagConverter ntc = new NotesTagConverter();
			goGraphText = ntc.convertNotes(goGraphText, '|');

			GOGraphConverter ggc = new GOGraphConverter();
			goGraphText = ggc.translateMarkups(goGraphText);

		} catch (IOException e) {
			return errorMav("Could not read marker GO graph from file");
		}

		// generate the MAV to be passed to the detail page
		ModelAndView mav = new ModelAndView("marker_go_graph");
		mav.addObject("marker", marker);

		// last-second cleanup at the method's end...
		mav.addObject("goGraphText", goGraphText);
		return mav;
	}

	/* add databaseDate to the given mav
	 */
	private void dbDate(ModelAndView mav) {
		mav.addObject("databaseDate", dbInfoFinder.getSourceDatabaseDate());
	}

	// generate the sorts
	private List<Sort> genSorts(HttpServletRequest request,MarkerQueryForm query) {
		logger.debug("->genSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied
		String sortRequested = request.getParameter("sort");
		String dirRequested  = request.getParameter("dir");

		// User can set initialSort and dir via link, but we want datatable sort to override
		if((sortRequested==null || sortRequested.equals("default"))
				&& notEmpty(query.getInitialSort())
				&& notEmpty(query.getInitialDir())) {
			sortRequested = query.getInitialSort();
			dirRequested = query.getInitialDir();
		}

		boolean desc = false;
		if("desc".equalsIgnoreCase(dirRequested)){
			desc = true;
		}

		logger.debug("user requested marker sort="+sortRequested+", dir="+dirRequested);
		if("symbol".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL, desc));
		} else if("coordinates".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.MRK_BY_LOCATION, desc));
		} else if("featureType".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.MRK_BY_TYPE, desc));
			sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL,false));
		} else if(notEmpty(query.getNomen()) || notEmpty(query.getGo()) || notEmpty(query.getInterpro())) {
			sorts.add(new Sort("score",true));
			sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL,false));
		} else {
			// default is by symbol
			sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL,false));
		}
		return sorts;
	}

	// generate the filters
	private Filter genFilters(MarkerQueryForm query) {
		logger.debug("->genFilters started");
		logger.debug("QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> queryFilters = new ArrayList<Filter>();

		String nomen = query.getNomen();
		if(notEmpty(nomen)) {
			Filter nomenFilter = Filter.or(Arrays.asList(
					FilterUtil.generateNomenFilter(SearchConstants.MRK_NOMENCLATURE,nomen),
					// try to boost an exact match
					new Filter(SearchConstants.MRK_SYMBOL,"\""+nomen.replace("\"","")+"\"^1000000000000",Filter.Operator.OP_HAS_WORD)
					));
			if(nomenFilter!=null) queryFilters.add(nomenFilter);
		}

		// Phenotypes
		String phenotype = query.getPhenotype();
		if(notEmpty(phenotype)) {
			BooleanSearch bs = new BooleanSearch();
			Filter f = bs.buildSolrFilter(SearchConstants.PHENOTYPE,phenotype);
			queryFilters.add(f);
		}

		//InterPro
		String interpro = query.getInterpro();
		if(notEmpty(interpro)) {

			BooleanSearch bs = new BooleanSearch();
			Filter f = bs.buildSolrFilter(SearchConstants.INTERPRO_TERM,interpro);
			queryFilters.add(f);

		}

		//GO
		String go = query.getGo();
		if(notEmpty(go)) {
			List<String> goVocabs = query.getGoVocab();
			List<Filter> goVocabFilters = new ArrayList<Filter>();
			if(!(notEmpty(goVocabs) && goVocabs.size()<3)) {
				// goVocabs = Arrays.asList(SearchConstants.GO_TERM);
				goVocabs = new ArrayList<String>(query.getGoVocabDisplay().keySet());
			}
			goVocabs.add(SearchConstants.MRK_TERM_ID);
			for(String goVocab : goVocabs) {

				BooleanSearch bs = new BooleanSearch();
				Filter f = bs.buildSolrFilter(goVocab,go);
				goVocabFilters.add(f);
			}
			// OR the query against each vocabulary (e.g. function,process,component)
			queryFilters.add(Filter.or(goVocabFilters));
		}

		// feature type
		Filter featureTypeFilter = makeListFilter(query.getFeatureType(),SearchConstants.FEATURE_TYPE);
		if(featureTypeFilter!=null) queryFilters.add(featureTypeFilter);

		Filter mcvFilter = makeListFilter(query.getMcv(),SearchConstants.FEATURE_TYPE_KEY);
		if(mcvFilter!=null) queryFilters.add(mcvFilter);

		// Chromosome & coordinate
		List<String> chr = query.getChromosome();
		String coord = query.getCoordinate();
		String coordUnit = query.getCoordUnit();
		if(notEmpty(chr) && !chr.contains(AlleleQueryForm.COORDINATE_ANY)) { // vaid chromosome value
			if(notEmpty(coord)) { // we have coordinates; search only genomic chromosome
				Filter coordChrFilter = makeListFilter(chr,SearchConstants.GENOMIC_CHROMOSOME);
				if(coordChrFilter!=null) queryFilters.add(coordChrFilter);
			} else { //search both genomic and genetic chromosomes (they can be different)
				List<Filter> coordFilters = new ArrayList<Filter>();
				coordFilters.add(makeListFilter(chr,SearchConstants.GENOMIC_CHROMOSOME));
				coordFilters.add(makeListFilter(chr,SearchConstants.GENETIC_CHROMOSOME));
				queryFilters.add(Filter.or(coordFilters));
			}
		}
		if(notEmpty(coord)) {
			Filter coordFilter = FilterUtil.genCoordFilter(coord,coordUnit);
			if(coordFilter==null) coordFilter = nullFilter();
			queryFilters.add(coordFilter);
		}

		// CM Search
		String cm = query.getCm();
		if(notEmpty(cm)) {
			Filter cmFilter = FilterUtil.genCmFilter(cm);
			if(cmFilter==null) cmFilter = nullFilter();
			queryFilters.add(cmFilter);
		}

		String startMarker = query.getStartMarker();
		String endMarker = query.getEndMarker();
		if(notEmpty(startMarker) && notEmpty(endMarker)) {
			// query for the start and end markers to check their coordinates and chromosomes
			// check that we can find the start marker
			SearchParams sp = new SearchParams();
			sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,startMarker,Filter.Operator.OP_EQUAL));
			SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);
			if(sr.getTotalCount()>0) {
				SolrSummaryMarker startMarkerObj = sr.getResultObjects().get(0);
				// check that we can find the end marker
				sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,endMarker,Filter.Operator.OP_EQUAL));
				sr = markerFinder.getSummaryMarkers(sp);
				if(sr.getTotalCount()>0) {
					SolrSummaryMarker endMarkerObj = sr.getResultObjects().get(0);

					if(notEmpty(startMarkerObj.getChromosome())
							&& notEmpty(startMarkerObj.getCoordStart())
							&& notEmpty(startMarkerObj.getCoordEnd())
							&& notEmpty(endMarkerObj.getChromosome())
							&& notEmpty(endMarkerObj.getCoordStart())
							&& notEmpty(endMarkerObj.getCoordEnd())) {
						Integer startCoord = Math.min(startMarkerObj.getCoordStart(),endMarkerObj.getCoordStart());
						Integer endCoord = Math.max(startMarkerObj.getCoordEnd(),endMarkerObj.getCoordEnd());

						String coordString = startCoord+"-"+endCoord;
						logger.info("build coord string from markers "+startMarker+" to "+endMarker+" = "+coordString);
						Filter chromosomeFilter = new Filter(SearchConstants.CHROMOSOME,startMarkerObj.getChromosome(),Filter.Operator.OP_EQUAL);
						Filter coordFilter = FilterUtil.genCoordFilter(coordString,"bp");
						if(coordFilter==null) coordFilter = nullFilter();
						queryFilters.add(Filter.and(Arrays.asList(chromosomeFilter,coordFilter)));
					}
				}
			}
		}
		String refKey = query.getRefKey();
		if(notEmpty(refKey)) {
			queryFilters.add(new Filter(SearchConstants.REF_KEY,refKey,Filter.Operator.OP_EQUAL));
		}
		String markerID = query.getMarkerID();
		if(notEmpty(markerID)) {
			queryFilters.add(new Filter(SearchConstants.MLD_MARKER_ID,markerID,Filter.Operator.OP_EQUAL));
		}

		if(queryFilters.size()<1) return nullFilter();
		return Filter.and(queryFilters);
	}

	// returns a filter that should always fail to retrieve results
	private Filter nullFilter() {
		return new Filter("markerKey","-99999",Filter.Operator.OP_EQUAL);
	}

	private Filter makeListFilter(List<String> values, String searchConstant) {
		Filter f = null;
		if(values!=null && values.size()>0) {
			List<Filter> vFilters = new ArrayList<Filter>();
			for(String value : values) {
				vFilters.add(new Filter(searchConstant,value,Filter.Operator.OP_EQUAL));
			}
			f = Filter.or(vFilters);
		}
		return f;
	}


	private void initQueryForm(List<QueryFormOption> options) {
		if(!MarkerQueryForm.initialized) {
			// do stuff
			Map<String,String> mcvToDisplay = new HashMap<String,String>();
			for(QueryFormOption option : options) {
				mcvToDisplay.put(option.getSubmitValue(),option.getDisplayValue());
			}
			MarkerQueryForm.setMarkerTypeKeyToDisplayMap(mcvToDisplay);
		}
	}
	/* For automated test access */
	public void initQueryForm() {
		/* if we don't have a cached version of the chromosome options (for
		 * the selction list), then we need to pull them out of the database,
		 * generate it, and cache it
		 */
		if (chromosomeOptions == null) {
			SearchResults<QueryFormOption> chrResults = queryFormOptionFinder.getQueryFormOptions("marker","chromosome");
			List<QueryFormOption> chromosomes = chrResults.getResultObjects();

			chromosomeOptions = new LinkedHashMap<String,String>();
			chromosomeOptions.put(MarkerQueryForm.CHROMOSOME_ANY,"Any");
			for (QueryFormOption chromosome : chromosomes) {
				chromosomeOptions.put(chromosome.getSubmitValue(),chromosome.getDisplayValue());
			}
		}

		/* if we don't have a cached version of the feature type options (for
		 * the feature type section, then we need to pull them out of the
		 * database, generate it, and cache it
		 */
		if (featureTypeHtml == null) {
			SearchResults<QueryFormOption> mtResults = queryFormOptionFinder.getQueryFormOptions("marker", "mcv");
			List<QueryFormOption> markerTypes = mtResults.getResultObjects();
			logger.debug("getQueryForm() received " + markerTypes.size() + " Feature Type options");

			featureTypeHtml = FormatHelper.buildHtmlTree(markerTypes);
			featureTypeJson = FormatHelper.buildJsonTree(markerTypes);

			initQueryForm(markerTypes);
		}

		// if we don't have a cached version of the build number, retrieve it
		// and cache it
		if (genomeBuild == null) {
			SearchResults<QueryFormOption> gbResults = queryFormOptionFinder.getQueryFormOptions("marker", "build_number");
			List<QueryFormOption> genomeBuilds = gbResults.getResultObjects();
			logger.debug("getQueryForm() received " + genomeBuilds.size() + " Genome Build options");

			if (genomeBuilds.size() > 0) {
				genomeBuild = genomeBuilds.get(0).getDisplayValue();
			}
		}
		
		if (snpBuildNumber == null) {
			SearchResults<QueryFormOption> options = queryFormOptionFinder.getQueryFormOptions("snp", "build number");
			List<QueryFormOption> optionList = options.getResultObjects();

			if (optionList.size() > 0) {
				snpBuildNumber = optionList.get(0).getDisplayValue();
				logger.debug("Cached SNP build number: " + snpBuildNumber);
			}
		}
		
		
	}
	private boolean notEmpty(String s) {
		return s!=null && !s.equals("");
	}
	private boolean notEmpty(Integer i) {
		return i!=null && i>0;
	}
	@SuppressWarnings("rawtypes")
	private boolean notEmpty(List l) {
		return l!=null && l.size()>0;
	}
	
	/* Serve up the Rosetta table for the W.K. Silvers book via Ajax
	 */
	@RequestMapping(value="/wksilversTable")
	public ModelAndView wksilversTable(HttpServletRequest request, HttpServletResponse response) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->wksilversTable() started");

		// need to add headers to allow AJAX access
		AjaxUtils.prepareAjaxHeaders(response);

		// setup view object
		ModelAndView mav = new ModelAndView("wksilvers_table");
		mav.addObject("markers", wkSilversMarkerFinder.getWKSilversMarkers());
		return mav;
	}
}
