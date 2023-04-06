package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleCellLine;
import mgi.frontend.datamodel.AlleleID;
import mgi.frontend.datamodel.AlleleRelatedMarker;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerQtlExperiment;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceLocation;
import mgi.frontend.datamodel.phenotype.DiseaseTableDisease;
import mgi.frontend.datamodel.phenotype.PhenoTableGenotype;
import mgi.frontend.datamodel.phenotype.PhenoTableProvider;
import mgi.frontend.datamodel.phenotype.PhenoTableSystem;
import mgi.frontend.datamodel.sort.SmartAlphaComparator;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.antlr.BooleanSearch.BooleanSearch;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.detail.AlleleDetail;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.GenotypeFinder;
import org.jax.mgi.fewi.finder.ImageFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.AlleleQueryForm;
import org.jax.mgi.fewi.forms.FormWidgetValues;
import org.jax.mgi.fewi.forms.MutationInvolvesQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm;
import org.jax.mgi.fewi.summary.AlleleSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MutationInvolvesSummaryRow;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /allele/ uri's
 */
@Controller
@RequestMapping(value="/allele")
public class AlleleController {

	//--------------------//
	// class variables
	//--------------------//

	// keep this around, so we know what version of the assembly we have (even
	// for alleles with a representative sequence without proper coordinates)
	//
	private static String assemblyVersion = null;

	private static Integer DOWNLOAD_ROW_CAP = new Integer(250000);

	//--------------------//
	// instance variables
	//--------------------//

	private final Logger logger = LoggerFactory.getLogger(AlleleController.class);

	@Autowired
	private IDLinker idLinker;

	@Autowired
	private DbInfoFinder dbInfoFinder;

	@Autowired
	private AlleleFinder alleleFinder;

	@Autowired
	private ImageFinder imageFinder;

	@Autowired
	private GenotypeFinder genotypeFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private VocabularyController vocabularyController;
	
	//--------------------------------------------------------------------//
	// public methods
	//--------------------------------------------------------------------//

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AlleleQueryForm query) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		ModelAndView mav = new ModelAndView("allele_query");

		initQFCache();
		List<String> collectionValues = AlleleQueryForm.getCollectionValues();
		List<String> mutationValues = AlleleQueryForm.getMutationValues();

		// package data, and send to display layer
		mav.addObject("alleleQueryForm", query);
		mav.addObject("sort", new Paginator());
		mav.addObject("collectionValues", collectionValues);
		mav.addObject("mutationValues", mutationValues);
		return mav;
	}

	@RequestMapping("/phenoPopup")
	public ModelAndView phenoPopup (HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->systemPopup started");
		String formName = request.getParameter("formName");

		ModelAndView mav = new ModelAndView("allele_query_system_popup");
		if(notEmpty(formName)) {
			// Protect against unexpected values (only two allowed).
			if ("markerQF".equals(formName) || "alleleQueryForm".equals(formName)) {
				mav.addObject("formName",formName);
			}
		}
		mav.addObject("widgetValues1",FormWidgetValues.getPhenoSystemWidgetValues1());
		mav.addObject("widgetValues2",FormWidgetValues.getPhenoSystemWidgetValues2());

		return mav;
	}

	@RequestMapping(value="/summary", method=RequestMethod.GET)
	public ModelAndView alleleSummary(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AlleleQueryForm query) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		ModelAndView mav = new ModelAndView("allele_summary");

		// direct to marker summary if marker is in link
		if(notEmpty(query.getMarkerId())) {
			SearchResults<Marker> markerSR = markerFinder.getMarkerByID(query.getMarkerId());
			if(markerSR.getTotalCount()>0) {
				mav = new ModelAndView("allele_summary_by_marker");
				mav.addObject("marker",markerSR.getResultObjects().get(0));
			}
		}

		initQFCache();
		List<String> collectionValues = AlleleQueryForm.getCollectionValues();
		List<String> mutationValues = AlleleQueryForm.getMutationValues();
		mav.addObject("alleleQueryForm",query);
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("collectionValues", collectionValues);
		mav.addObject("mutationValues", mutationValues);
		
		// add a flag if the request was for alleles related to a
		// marker via a 'mutation involves' relationship
		if (query.getMutationInvolves() != null) {
			mav.addObject("mutationInvolves", "yes");
		}
		return mav;
	}


	//------------------------------------//
	// Allele Summary (By Refernce) Shell
	//------------------------------------//
	@RequestMapping(value="/reference/{refID}")
	public ModelAndView alleleSummeryByRefId(HttpServletRequest request, @PathVariable("refID") String refID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->alleleSummeryByRefId started");

		// setup view object
		ModelAndView mav = new ModelAndView("allele_summary_by_reference");

		// setup search parameters object to gather the requested marker
		SearchParams referenceSearchParams = new SearchParams();
		Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
		referenceSearchParams.setFilter(refIdFilter);

		// find the requested reference
		SearchResults<Reference> referenceSearchResults = referenceFinder.searchReferences(referenceSearchParams);
		List<Reference> referenceList = referenceSearchResults.getResultObjects();

		// there can be only one...
		if (referenceList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No reference found for " + refID);
			return mav;
		}
		if (referenceList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + refID);
			return mav;
		}
		Reference reference = referenceList.get(0);

		// prep query form for summary js request
		AlleleQueryForm query = new AlleleQueryForm();
		query.setJnumId(refID);

		// package data, and send to view layer
		mav.addObject("reference", reference);
		mav.addObject("alleleQueryForm",query);
		mav.addObject("queryString", "jnumId=" + refID);

		logger.debug("alleleSummeryByRefId routing to view ");
		return mav;
	}

	@RequestMapping(value="/{alleleID:.+}", method=RequestMethod.GET)
	public ModelAndView alleleDetailByID(HttpServletRequest request, HttpServletResponse response, @PathVariable("alleleID") String alleleID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->alleleDetailByID started");

		// find the requested Allele
		List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);

		// there can only be one Allele
		if ((alleleList == null) || (alleleList.size() < 1)) {
			return errorMav("No Allele Found");
		} else if (alleleList.size() > 1) {
			return errorMav("Duplicate ID");
		}

		// pass on to shared code to flesh out data for the allele detail page
		return prepareAllele(request, alleleList.get(0));
	}

	@RequestMapping(value="/key/{dbKey:.+}", method=RequestMethod.GET)
	public ModelAndView alleleDetailByKey(HttpServletRequest request, HttpServletResponse response, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->alleleDetailByKey started");

		if (!FewiUtil.isPositiveInteger(dbKey)) {
			return errorMav("No Allele Found");
		}
		
		// find the requested Allele
		SearchResults<Allele> searchResults = alleleFinder.getAlleleByKey(dbKey);
		List<Allele> alleleList = searchResults.getResultObjects();

		// there can only be one Allele
		if ((alleleList == null) || (alleleList.size() < 1)) {
			return errorMav("No Allele Found");
		} else if (alleleList.size() > 1) {
			// should not happen
			return errorMav("Duplicate Key");
		}

		// pass on to shared code to flesh out data for the allele detail page
		return prepareAllele(request, alleleList.get(0));
	}

	//------------------------------------------------//
	// 'Mutation Involves' relationships for an allele
	//------------------------------------------------//

	@RequestMapping(value="/mutationInvolves/{alleleID:.+}", method=RequestMethod.GET)
	public ModelAndView mutationInvolvesByID(HttpServletRequest request, HttpServletResponse response, @PathVariable("alleleID") String alleleID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->mutationInvolvesByID started");

		// find the requested Allele
		List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);

		// there can only be one Allele
		if (alleleList.size() < 1) {
			return errorMav("Unknown ID : No allele was found for " + alleleID);
		} else if (alleleList.size() > 1) {
			return errorMav("Duplicate ID : " + alleleID + " is shared by " + alleleList.size() + " alleles");
		}

		Allele allele = alleleList.get(0);

		ModelAndView mav = new ModelAndView("allele_mutation_involves");
		mav.addObject("allele", allele);

		mav.addObject("idLinker", idLinker);

		String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

		StringBuffer title = new StringBuffer();
		title.append("Genes/genome features involved in mutation: ");
		title.append("<a href='");
		title.append(fewiUrl);
		title.append("allele/");
		title.append(allele.getPrimaryID());
		title.append("' target='_blank' class='noUnderline'>");
		title.append(FormatHelper.superscript(allele.getSymbol()));
		title.append("</a>");

		String titleBar = allele.getSymbol() + " : mouse genes/genome features involved in this mutation";

		String seoDescription = "Mouse genes/genome features involved in mutation " + allele.getSymbol();

		StringBuffer seoKeywords = new StringBuffer();
		seoKeywords.append("MGI, ");
		seoKeywords.append(allele.getSymbol());
		seoKeywords.append(", ");
		seoKeywords.append(allele.getName());
		seoKeywords.append(", mouse, mice, murine, Mus musculus, ");
		seoKeywords.append(allele.getAlleleType());

		for (AlleleSynonym synonym : allele.getSynonyms()) {
			seoKeywords.append(", ");
			seoKeywords.append(synonym.getSynonym());
		}

		seoKeywords.append(", ");
		seoKeywords.append(allele.getPrimaryID());

		mav.addObject("pageTitle", titleBar);
		mav.addObject("pageTitleSuperscript", title.toString());
		mav.addObject("seoDescription", seoDescription);
		mav.addObject("seoKeywords", seoKeywords.toString());
		mav.addObject("alleleID", allele.getPrimaryID());
		return mav;
	}

	/* method to retrieve data for the table on the mutation involves page and
	 * feed it to the page in JSON format
	 */
	@RequestMapping("/mutationInvolvesJson")
	public @ResponseBody JsonSummaryResponse<MutationInvolvesSummaryRow>
	mutationInvolvesJson(HttpServletRequest request, @ModelAttribute MutationInvolvesQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->mutationInvolvesJson() started");
		logger.debug("queryForm: " + query.toString());

		// find the allele and its key

		String alleleID = query.getAlleleID();
		int alleleKey = -1;
		Allele allele = null;

		if (alleleID != null) {
			// find the requested Allele
			List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);

			// there can only be one Allele
			if (alleleList.size() < 1) {
				logger.debug("No Allele Found for ID: " + alleleID);
			} else if (alleleList.size() > 1) {
				logger.debug("ID matches multiple alleles: " + alleleID);
			}
			allele = alleleList.get(0);
			alleleKey = allele.getAlleleKey();
		}

		// build info needed to conduct the search

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setFilter(genMutationInvolvesFilter(query, alleleKey));
		logger.debug("Filter: " + params.getFilter().toString());

		// perform the query and pull out requested objects

		SearchResults<AlleleRelatedMarker> searchResults = alleleFinder.getMutationInvolvesData(params);
		List<AlleleRelatedMarker> markers = searchResults.getResultObjects();

		// convert to SummaryRow wrapper objects

		List<MutationInvolvesSummaryRow> summaryRows = new ArrayList<MutationInvolvesSummaryRow>();
		Iterator<AlleleRelatedMarker> it = markers.iterator();
		while (it.hasNext()) {
			AlleleRelatedMarker marker = it.next();
			if (marker != null) {
				summaryRows.add(new MutationInvolvesSummaryRow(marker, allele));
			}
		}

		JsonSummaryResponse<MutationInvolvesSummaryRow> jsonResponse = new JsonSummaryResponse<MutationInvolvesSummaryRow>();

		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		return jsonResponse;
	}

	private Filter genMutationInvolvesFilter(MutationInvolvesQueryForm query, int alleleKey) {

		if (alleleKey > 0) {
			return new Filter (SearchConstants.ALLELE_KEY, alleleKey, Filter.Operator.OP_EQUAL);
		}
		return null;
	}

	//------------------------------------//
	// Allele Summary Data Retrieval
	//------------------------------------//
	@RequestMapping("/summary/json")
	public @ResponseBody JsonSummaryResponse<AlleleSummaryRow> alleleSummaryJson(HttpServletRequest request, @ModelAttribute AlleleQueryForm query, @ModelAttribute Paginator page) throws RecognitionException {
		SearchResults<Allele> sr = getAlleles(request,query,page);
		logger.info("found "+sr.getTotalCount()+" alleles");

		sessionFactory.getCurrentSession().enableFilter("teaserMarkers");

		List<AlleleSummaryRow> sRows = new ArrayList<AlleleSummaryRow>();
		for(Allele allele : sr.getResultObjects()) {
			sRows.add(new AlleleSummaryRow(allele, idLinker));
		}

		logger.info("building summary rows");
		JsonSummaryResponse<AlleleSummaryRow> jsr = new JsonSummaryResponse<AlleleSummaryRow>();
		jsr.setTotalCount(sr.getTotalCount());
		jsr.setSummaryRows(sRows);
		return jsr;
	}

	public SearchResults<Allele> getAlleles(HttpServletRequest request, @ModelAttribute AlleleQueryForm query, @ModelAttribute Paginator page) throws RecognitionException {
		SearchParams sp = new SearchParams();
		sp.setPaginator(page);
		sp.setFilter(parseQueryForm(query));
		sp.setSorts(parseAlleleSorts(request));
		SearchResults<Allele> sr = alleleFinder.getAlleleByID(sp);
		return sr;
	}


	//--------------------------------//
	// Allele Summary Tab-Delim Report
	//--------------------------------//
	@RequestMapping("/report*")
	public ModelAndView alleleSummaryExport(HttpServletRequest request, @ModelAttribute AlleleQueryForm query, @ModelAttribute Paginator page) throws RecognitionException {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("generating report");

		SearchParams sp = new SearchParams();
		page.setResults(DOWNLOAD_ROW_CAP);
		sp.setPaginator(page);
		sp.setFilter(parseQueryForm(query));
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL));
		sp.setSorts(sorts);
		SearchResults<Allele> sr = alleleFinder.getAlleleByID(sp);

		List<Allele> alleles = sr.getResultObjects();

		ModelAndView mav = new ModelAndView("alleleSummaryReport");
		mav.addObject("alleles", alleles);
		return mav;

	}


	private Filter parseQueryForm(AlleleQueryForm query) throws RecognitionException {
		List<Filter> filters = new ArrayList<Filter>();
		// Allele Key
		String allKey = query.getAllKey();
		if(notEmpty(allKey)) {
			filters.add(new Filter(SearchConstants.ALL_KEY,allKey,Filter.Operator.OP_EQUAL));
		}
		// Allele IDs
		String allIds = query.getAllIds();
		if(notEmpty(allIds)) {
			List<String> allIdTokens = QueryParser.tokeniseOnWhitespaceAndComma(allIds);
			filters.add(new Filter(SearchConstants.ALL_ID,allIdTokens,Filter.Operator.OP_IN));
		}
		// Allele Type
		Filter alleleTypeFilter = makeListFilter(query.getAlleleType(),SearchConstants.ALL_TYPE, false);
		if(alleleTypeFilter!=null) filters.add(alleleTypeFilter);

		// Allele Sub Type
		Filter alleleSubTypeFilter = makeListFilter(query.getAlleleSubType(),SearchConstants.ALL_SUBTYPE, true);
		if(alleleSubTypeFilter!=null) filters.add(alleleSubTypeFilter);

		// Allele Collection
		Filter collectionFilter = makeListFilter(query.getCollection(),SearchConstants.ALL_COLLECTION, false);
		if(collectionFilter!=null) filters.add(collectionFilter);

		// Allele Mutation
		Filter mutationFilter = makeListFilter(query.getMutation(), SearchConstants.ALL_MUTATION, false);
		if(mutationFilter!=null) filters.add(mutationFilter);

		// Phenotypes
		String phenotype = query.getPhenotype();
		if(notEmpty(phenotype)) {
			BooleanSearch bs = new BooleanSearch();
			Filter f = bs.buildSolrFilter(SearchConstants.ALL_PHENOTYPE,phenotype);
			filters.add(f);
		}

		// Nomenclature
		String nomen = query.getNomen();
		if(notEmpty(nomen)) {
			Filter nomenFilter = FilterUtil.generateNomenFilter(SearchConstants.ALL_NOMEN,nomen);
			if(nomenFilter!=null) filters.add(nomenFilter);
		}

		// Reference Key
		String refKey = query.getRefKey();
		if(notEmpty(refKey)) {
			filters.add(new Filter(SearchConstants.REF_KEY,refKey,Filter.Operator.OP_EQUAL));
		}

		// Reference JNUM ID
		String jnumId = query.getJnumId();
		if(notEmpty(jnumId)) {
			filters.add(new Filter(SearchConstants.JNUM_ID,jnumId,Filter.Operator.OP_EQUAL));
		}

		// Has DO and remove mutation involves
		String hasDO = query.getHasDO();
		if(notEmpty(hasDO)) {
			filters.add(new Filter(SearchConstants.ALL_HAS_DO, hasDO, Filter.Operator.OP_EQUAL));
			if(hasDO.equals("1")) {
				String mrkId = query.getMarkerId();
				// -(mutationInvolvesMarkerIDs:"MGI:99999")
				Filter f = new Filter(SearchConstants.ALL_MI_MARKER_IDS, mrkId, Filter.Operator.OP_EQUAL);
				f.setNegate(true);
				filters.add(f);
			}
		}

		// Exclude Cell Lines
		String isCellLine = query.getIsCellLine();
		if(notEmpty(isCellLine)) {
			filters.add(new Filter(SearchConstants.ALL_IS_CELLLINE,isCellLine,Filter.Operator.OP_EQUAL));
		}

		//Marker ID has two meanings:
		// 1. if mutationInvolves is non-null, then look for the
		//    marker ID as one associated with alleles via the
		//    mutation involves field
		// 2. if not a mutationInvolves query, then look for alleles
		//    associated with the marker in the traditional sense

		String mrkId = query.getMarkerId();
		String mutationInvolves = query.getMutationInvolves();

		if(notEmpty(mrkId)) {
			if (notEmpty(mutationInvolves)) {
				filters.add(new Filter(SearchConstants.ALL_MI_MARKER_IDS, mrkId, Filter.Operator.OP_EQUAL));
			} else {
				filters.add(new Filter(SearchConstants.MRK_ID, mrkId, Filter.Operator.OP_EQUAL));
			}
		}

		// Chromosome
		List<String> chr = query.getChromosome();
		if(notEmpty(chr) && !chr.contains(AlleleQueryForm.COORDINATE_ANY)) {
			Filter chrFilter = makeListFilter(chr,SearchConstants.CHROMOSOME, false);
			if(chrFilter!=null) filters.add(chrFilter);
		}
		// Coordinate Search
		String coord = query.getCoordinate();
		String coordUnit = query.getCoordUnit();
		if(notEmpty(coord)) {
			Filter coordFilter = FilterUtil.genCoordFilter(coord,coordUnit);
			if(coordFilter==null) coordFilter = nullFilter();
			filters.add(coordFilter);
		}

		// CM Search
		String cm = query.getCm();
		if(notEmpty(cm)) {
			Filter cmFilter = FilterUtil.genCmFilter(cm);
			if(cmFilter==null) cmFilter = nullFilter();
			filters.add(cmFilter);
		}

		// Cytoband
		String cyto = query.getCyto();
		if(notEmpty(cyto)) {
			filters.add(new Filter(SearchConstants.CYTOGENETIC_OFFSET,cyto,Filter.Operator.OP_EQUAL));
		}

		Filter f;
		if(filters.size()>0) {
			// make sure not specified alleles are excluded
			filters.add(new Filter(SearchConstants.ALL_IS_WILD_TYPE,0,Filter.Operator.OP_EQUAL));
			f=Filter.and(filters);
		}
		else f = nullFilter(); // return nothing if no valid filters
		return f;
	}

	private Filter nullFilter() {
		return new Filter(SearchConstants.ALL_KEY,"-9999",Filter.Operator.OP_EQUAL);
	}

	/*
	 * Parses requested sort parameters for gxd marker assay summary.
	 */
	private List<Sort> parseAlleleSorts(HttpServletRequest request) {
		logger.debug("->parseAlleleSorts started");
		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied
		String sortRequested = request.getParameter("sort");

		String dirRequested  = request.getParameter("dir");
		boolean desc = true;
		if("asc".equalsIgnoreCase(dirRequested))
		{
			desc=false;
		}
		boolean asc=!desc;

		// expected sort values
		if ("nomen".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,desc));
		} else if ("category".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.ALL_BY_TYPE,desc));
			sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,desc));
		} else if ("chr".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.ALL_BY_CHROMOSOME,desc));
			sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,desc));
		} else if ("diseases".equalsIgnoreCase(sortRequested)) {
			sorts.add(new Sort(SortConstants.ALL_BY_DISEASE,desc));
			sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,false));
		} else {
			// default sort
			sorts.add(new Sort(SortConstants.ALL_BY_TRANSMISSION,asc));
			sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,asc));
		}

		logger.debug ("sort: " + sortRequested);

		return sorts;
	}

	//
        private class AlleleRelatedMarkerComparator extends SmartAlphaComparator<AlleleRelatedMarker> {
            public int compare(AlleleRelatedMarker m1, AlleleRelatedMarker m2) {
                return super.compare(m1.getRelatedMarkerSymbol(), m2.getRelatedMarkerSymbol());
            }    
        }    

	//
        private class MarkerSymbolComparator extends SmartAlphaComparator<Marker> {
            public int compare(Marker m1, Marker m2) {
                return super.compare(m1.getSymbol(), m2.getSymbol());
            }    
        }    

	//----------------------------//
	// Allele Detail (shared code)
	//----------------------------//
	/* look up a bunch of extra data and toss it in the MAV; a convenience
	 * method for use by the individual methods to render allele detail pages
	 * either by key or by ID
	 */
	private ModelAndView prepareAllele(HttpServletRequest request,Allele allele) {
		logger.debug("prepareAllele: started ....");
		if (allele == null) {
			return errorMav("Cannot find allele");
		}
		ModelAndView mav = new ModelAndView("allele_detail");
		mav.addObject ("allele", allele);

		dbDate(mav);

		mav.addObject("idLinker", idLinker);

		// add a Properties object with URLs for use at the JSP level
		Properties urls = idLinker.getUrlsAsProperties();
		mav.addObject("urls", urls);

		// pick up and save our 'expresses component' markers before
		// we enable the filter for the 'mutation involves' markers

		sessionFactory.getCurrentSession().enableFilter("expressesComponentMarkers");
		List<AlleleRelatedMarker> expressesComponent = allele.getExpressesComponentMarkers();
		if (expressesComponent.size() > 0) {
			Collections.sort(expressesComponent, new AlleleRelatedMarkerComparator());
			List<List<Marker>> ecOrthologs = new ArrayList<List<Marker>>();
			boolean showOrthologColumn = false;
			for (AlleleRelatedMarker arm : expressesComponent) {
				List<Marker> mouseOrths = new ArrayList<Marker>();
				if (! arm.getRelatedMarker().getOrganism().equals("mouse")) {
					mouseOrths = arm.getRelatedMarker().getAllianceDirectMouseOrthologs();
					Collections.sort(mouseOrths, new MarkerSymbolComparator());
					if (mouseOrths.size() > 0) {
						showOrthologColumn = true;
					}
				}
				ecOrthologs.add(mouseOrths);
			}
			mav.addObject("expressesComponent", expressesComponent);
			if (showOrthologColumn) {
				mav.addObject("showOrthologColumn", showOrthologColumn);
				mav.addObject("ecOrthologs", ecOrthologs);
			}
		}

		// allele_to_driver_gene
		// "drivenBy"
		//
		sessionFactory.getCurrentSession().enableFilter("drivenByMarkers");
		List<AlleleRelatedMarker> drivenBy = allele.getDrivenByMarkers();
		if (drivenBy.size() > 0) {
			Collections.sort(drivenBy, new AlleleRelatedMarkerComparator());
			List<List<Marker>> dbOrthologs = new ArrayList<List<Marker>>();
			boolean showOrthologColumn = false;
			for (AlleleRelatedMarker arm : drivenBy) {
				List<Marker> mouseOrths = new ArrayList<Marker>();
				if (! arm.getRelatedMarker().getOrganism().equals("mouse")) {
					mouseOrths = arm.getRelatedMarker().getAllianceDirectMouseOrthologs();
					Collections.sort(mouseOrths, new MarkerSymbolComparator());
					if (mouseOrths.size() > 0) {
						showOrthologColumn = true;
					}
				}
				dbOrthologs.add(mouseOrths);
			}
			mav.addObject("drivenBy", drivenBy);
			if (showOrthologColumn) {
				mav.addObject("showDbOrthologColumn", showOrthologColumn);
				mav.addObject("dbOrthologs", dbOrthologs);
			}
		}

		// When retrieving 'mutation involves' markers for the teaser,
		// ensure that we're only getting the ones we need, rather
		// than the whole set.
		sessionFactory.getCurrentSession().enableFilter("teaserMarkers");
		sessionFactory.getCurrentSession().enableFilter("mutationInvolvesMarkers");
		List<AlleleRelatedMarker> mutationInvolves = allele.getMutationInvolvesMarkers();
		if (mutationInvolves.size() > 0) {
			mav.addObject("mutationInvolves", mutationInvolves); 
		}

		String alleleType = allele.getAlleleType();

		// begin setting up keywords for SEO (search engine optimization)
		// meta tags.  Also set up a description snippet for display by search
		// engines when returning results.
		ArrayList<String> seoKeywords = new ArrayList<String>();
		seoKeywords.add(allele.getSymbol());
		seoKeywords.add("mouse");
		seoKeywords.add("mice");
		seoKeywords.add("murine");
		seoKeywords.add("Mus");
		seoKeywords.add("allele");

		StringBuffer seoDescription = new StringBuffer();
		seoDescription.append ("View ");
		seoDescription.append (allele.getSymbol());
		seoDescription.append (" allele: origin");

		if (allele.getMolecularDescription() != null && allele.getMolecularDescription().length() != 0) {
			seoDescription.append (", molecular description");
			mav.addObject("description", allele.getMolecularDescription());
		}

		if (alleleType.equals("Targeted")) {
			seoKeywords.add("null");
			seoKeywords.add("KO");
			seoKeywords.add("knock out");
			seoKeywords.add("knockout");
			seoKeywords.add("floxed");
			seoKeywords.add("loxP");
			seoKeywords.add("frt");
		} else if (alleleType.indexOf("Cre") >= 0 || alleleType.indexOf("Flp") >= 0) {
			seoKeywords.add("recombinase");
			seoKeywords.add("cre");
			seoKeywords.add("flp");
			seoKeywords.add("flpe");
		} else if (alleleType.indexOf("Transposase") >= 0) {
			seoKeywords.add("transposase");
		}

		if (!alleleType.equals("QTL") && !alleleType.equals("Not Applicable") && !alleleType.equals("Other")) {
			// all allele types except QTL and N/A get two standard seoKeywords
			seoKeywords.add("mutant");
			seoKeywords.add("mutation");
		}

		if (!alleleType.equals("Not Applicable") && !alleleType.equals("Not Specified")	&& !alleleType.equals("Other")) {
			// all allele types except N/A and N/S get their allele type as a
			// keyword
			seoKeywords.add(alleleType);
		}

		if (allele.getHasDiseaseModel()) {
			seoKeywords.add("disease model");
			seoDescription.append(" and human disease models");
		}

		if (allele.getPhenotypeImages().size() > 0) {
			seoDescription.append(", images");
		}

		seoDescription.append(", gene associations, and references.");

		List<AlleleSynonym> synonyms = allele.getSynonyms();

		for (AlleleSynonym synonym : synonyms) {
			seoKeywords.add (synonym.getSynonym());
		}

		// set up page title, subtitle, and labels dependent on allele type
		String title = FormatHelper.superscript(allele.getSymbol());
		String subtitle = null;
		String markerLabel = "Gene";
		String typeCategory = "Mutation";
		String symbolLabel = "Symbol";

		if (alleleType.equals("QTL")) {
			// is a QTL
			subtitle = "QTL Variant Detail";
			markerLabel = "QTL";
			typeCategory = "Variant";
			symbolLabel = "QTL variant";

		} else if (alleleType.equals("Transgenic")) {
			// is a transgene
			subtitle = "Transgenic Allele Detail";
			typeCategory = "Transgene";
		} else if (alleleType.equals("Not Specified") || alleleType.equals("Not Applicable") || alleleType.equals("Other")) {
			// do not show allele type in labels if Not Specified/Applicable
			subtitle = "Allele Detail";
		} else if (alleleType.indexOf('(') >= 0) {
			// trim out subtypes when putting allele type in labels
			subtitle = alleleType.replaceFirst("\\(.*\\)", "") + " Allele Detail";
		} else {
			// add allele type to labels
			subtitle = alleleType + " Allele Detail";
		}

		String keywords = StringUtils.join(seoKeywords,", ");

		mav.addObject("symbolLabel", symbolLabel);
		mav.addObject("alleleType", alleleType);
		mav.addObject("typeCategory", typeCategory);
		mav.addObject("seoKeywords", keywords.toString());
		mav.addObject("seoDescription", seoDescription.toString());

                if (allele.getIsWildType() == 0 && !"Cell Line".equals(allele.getTransmissionType()) && !"QTL".equals(allele.getAlleleType())) {
                    mav.addObject("linkToAlliance", "yes");
                } else {
                    mav.addObject("linkToAlliance", null);
                }

		// add the allele's marker(s) to the mav

		// If more than one associated marker, need code changes here.
		// For now, we just include the first one.

		String namesRaw = allele.getName();
		Marker marker = allele.getMarker();
		if (marker != null) {
			mav.addObject("marker", marker);

                        if ("Cytogenetic Marker".equals(marker.getMarkerType()) ||
                            "Complex/Cluster/Region".equals(marker.getMarkerType())) {
                            mav.addObject("linkToAlliance", null);
                        }

			// adjust the marker label for transgene markers, regardless of
			// the allele type.  Also, we do not want to link to the marker
			// detail page for transgene markers.
			if ("Transgene".equals(marker.getMarkerType())) {

				// only update the page subtitle if the allele type is not
				// Not Applicable or Not Specified
				if (!"Allele Detail".equals(subtitle)) {
					subtitle = "Transgene Detail";
				}

				markerLabel = "Transgene";
				mav.addObject("linkToMarker", null);
			} else {
				// if not a transgene, link to the marker detail page
				mav.addObject("linkToMarker", "yes");
			}

			if (!marker.getName().equals(namesRaw)) {
				namesRaw = marker.getName() + "; " + namesRaw;
			}

			// if this is a QTL marker, then add in the QTL notes (two types)
			if ("QTL".equals(allele.getAlleleType())) {
				List<MarkerQtlExperiment> qtlExpts = marker.getQtlMappingNotes();
				if (qtlExpts != null && qtlExpts.size() > 0) {
					mav.addObject("qtlExpts", qtlExpts);
				}

				List<MarkerQtlExperiment> qtlCandidateGenes = marker.getQtlCandidateGeneNotes();
				if (qtlCandidateGenes != null && qtlCandidateGenes.size() > 0) {
					mav.addObject("qtlCandidateGenes", qtlCandidateGenes);
				}

				String qtlNote = marker.getQtlNote();
				if (qtlNote != null && qtlNote.length() > 0) {
					mav.addObject("qtlNote", qtlNote);
				}
			}
		}

		mav.addObject("markerLabel", markerLabel);
		mav.addObject("title", title);
		mav.addObject("subtitle", subtitle);

		// start collecting values needed for JBrowse link, with priorities as follows:
		//    1. coordinates from an associated marker
		//    2. coordinates from representative sequence + a 5kb buffer
		long pointCoord = -1;
		long startCoord = -1;
		long endCoord = -1;
		String chromosome = null;

		// default text strings for JBrowse for the case where we do not have
		// coordinates from a marker, but instead from a representative
		// sequence.
		String jbrowseExtraLine = "";
		String jbrowseLabel = "View all gene trap sequence tags within a 10kb region";

		// genomic and genetic locations of the marker
		if (marker != null) {
			MarkerLocation coords = marker.getPreferredCoordinates();
			MarkerLocation cmOffset = marker.getPreferredCentimorgans();
			MarkerLocation cytoband = marker.getPreferredCytoband();

			StringBuffer genomicLocation = new StringBuffer();
			StringBuffer geneticLocation = new StringBuffer();

			if (coords == null) {
				genomicLocation.append ("unknown");
			} else {
				startCoord = coords.getStartCoordinate().longValue();
				endCoord = coords.getEndCoordinate().longValue();
				chromosome = coords.getChromosome();

				genomicLocation.append ("Chr");
				genomicLocation.append (chromosome);
				genomicLocation.append (":");
				genomicLocation.append (startCoord);
				genomicLocation.append ("-");
				genomicLocation.append (endCoord);
				genomicLocation.append (" bp");

				String strand = coords.getStrand();
				if (strand != null) {
					genomicLocation.append (", ");
					genomicLocation.append (strand);
					genomicLocation.append (" strand");
				}
			}
			mav.addObject("genomicLocation", genomicLocation.toString());

			if ((cmOffset != null) && (cmOffset.getCmOffset().floatValue() != -999.0)) {
				geneticLocation.append ("Chr");
				geneticLocation.append (cmOffset.getChromosome());

				float cM = cmOffset.getCmOffset().floatValue();

				if (cM >= 0.0) {
					geneticLocation.append (", ");

					// need to include a special label for QTLs
					if ("QTL".equals(marker.getMarkerType()))
					{
						geneticLocation.append ("cM position of peak correlated region/allele: ");
					}
					geneticLocation.append (cmOffset.getCmOffset().toString());
					geneticLocation.append (" cM");
				} else if (cM == -1.0 && cytoband == null) {
					geneticLocation.append (", Syntenic");
				}
			}

			if (cytoband != null) {
				if (cmOffset == null) {
					geneticLocation.append ("Chr");
					geneticLocation.append (cytoband.getChromosome());
				}
				geneticLocation.append (", cytoband ");
				geneticLocation.append (cytoband.getCytogeneticOffset());
			}
			mav.addObject("geneticLocation", geneticLocation.toString());
		}

		// allele symbol, name, and synonyms with HTML superscript tags
		String symbolSup = FormatHelper.superscript(allele.getSymbol());
		String nameSup = FormatHelper.superscript(namesRaw);
		if (marker != null) {
			String markerSymbolSup = FormatHelper.superscript(marker.getSymbol());
			mav.addObject ("markerSymbolSup", markerSymbolSup);
		}

		mav.addObject ("symbolSup", symbolSup);
		mav.addObject ("nameSup", nameSup);

		List<AlleleSynonym> synonymList = allele.getSynonyms();
		if (synonymList.size() > 0) {
			List<String> formattedSynonyms = new ArrayList<String>();
			for(AlleleSynonym synonym : synonymList) {
				formattedSynonyms.add(FormatHelper.superscript(synonym.getSynonym()));
			}
			mav.addObject ("synonyms", StringUtils.join(formattedSynonyms,", "));
		}

		// mutations and mutation label
		StringBuffer mutations = new StringBuffer();
		String mutationLabel = "Mutation";

		List<String> mutationList = allele.getMutations();
		if (mutationList == null || mutationList.size() == 0) {
			mutations.append ("Undefined");
		} else if (mutationList.size() == 1) {
			mutations.append (mutationList.get(0));
		} else {
			for (String m : mutationList) {
				if (mutations.length() > 0) mutations.append(", ");
				mutations.append(m);
			}
			mutationLabel = "Mutations";
		}

		String mutationString = mutations.toString();

		// We only want to show an Undefined mutation type if there is a
		// molecular note as well.
		if (!"Undefined".equals(mutationString) || allele.getMolecularDescription() != null) {
			mav.addObject("mutations", mutationString);
			mav.addObject("mutationLabel", mutationLabel);
		}

		// vector and vector type
		boolean hasMcl = false;
		List<AlleleCellLine> mutantCellLines = allele.getMutantCellLines();
		if (mutantCellLines.size() > 0) {
			hasMcl = true;
			AlleleCellLine mcl = mutantCellLines.get(0);
			String vector = mcl.getVector();
			String vectorType = mcl.getVectorType();

			if ((vector != null) && (!"Not Specified".equals(vector))) {
				mav.addObject("vector", vector);
			}

			if ((vectorType != null) && (!"Not Specified".equals(vectorType))) {
				mav.addObject("vectorType", vectorType);
			}
		}

		// mutant cell lines; ugly, but we need to do some slicing and dicing,
		// so actually compose HTML links here

		AlleleID kompID = allele.getKompID();

		if (hasMcl) {
			ArrayList<String> mcLines = new ArrayList<String>();
			String lastProvider = null;
			String idToLink = null;
			String provider = "";
			StringBuffer out = new StringBuffer();
			String name = null;

			for (AlleleCellLine mcl : mutantCellLines) {
				provider = mcl.getCreator();

				name = mcl.getPrimaryID();
				if ((name == null) || "null".equals(name)) {
					name = mcl.getName();
				}

				if (lastProvider == null) {
					idToLink = name;
					lastProvider = provider;
					mcLines.add(idToLink);
				} else if (lastProvider.equals(provider)) {
					mcLines.add(name);
				} else {
					if (out.length() > 0) {
						out.append (", ");
					}
//					out.append (formatMutantCellLines (mcLines, lastProvider, idToLink, kompID));
// suppress MCL links now, as many are no longer working:
					out.append (formatMutantCellLines (mcLines, lastProvider, null, null));
					idToLink = name;
					lastProvider = provider;
					mcLines = new ArrayList<String>();
					mcLines.add(idToLink);
				}
			}
			if (mcLines.size() > 0) {
				if (out.length() > 0) {
					out.append (", ");
				}
//				out.append (formatMutantCellLines (mcLines, lastProvider, idToLink, kompID));
// suppress MCL links now, as many are no longer working:
				out.append (formatMutantCellLines (mcLines, lastProvider, null, null));
			}

			// mixed alleles need a special message

			if (allele.getIsMixed() == 1) {
				out.append ("&nbsp;&nbsp;<B>More than one mutation may be present.</B>");
			}

			mav.addObject("mutantCellLines", out.toString());

			if (mutantCellLines.size() > 1) {
				mav.addObject("mutantCellLineLabel", "Mutant Cell Lines");
			} else {
				mav.addObject("mutantCellLineLabel", "Mutant Cell Line");
			}
		}

		// parent cell line, background strain

		AlleleCellLine pcl = allele.getParentCellLine();
		if (pcl != null) {
			if (pcl.getPrimaryID() != null) {
				mav.addObject("parentCellLine", pcl.getPrimaryID());
			} else {
				mav.addObject("parentCellLine", pcl.getName());
			}

			if ("Embryonic Stem Cell".equals(pcl.getCellLineType())) {
				mav.addObject("parentCellLineType", "ES Cell");
			} else {
				mav.addObject("parentCellLineType", pcl.getCellLineType());
			}
		}

		if (allele.getStrain() != null) {
			mav.addObject("strainLabel", allele.getStrainLabel());
			mav.addObject("backgroundStrain", FormatHelper.superscript(allele.getStrain()));
		}

		// add the allele's primary image and count of images (as a String)

		Image image = allele.getPrimaryImage();
		if (image != null) {
			mav.addObject("primaryImage", image);

			Integer thumbnailKey = image.getThumbnailImageKey();

			if (thumbnailKey != null) {
				Image thumbnail = imageFinder.getImageObjectByKey(thumbnailKey.intValue());
				mav.addObject("thumbnailImage", thumbnail);

				Integer width = thumbnail.getWidth();
				Integer height = thumbnail.getHeight();

				StringBuffer xy = new StringBuffer();
				if (width != null) {
					xy.append (" width='" + width.toString() + "'");
				}
				if (height != null) {
					xy.append (" heigh='" + height.toString() + "'");
				}

				mav.addObject("thumbnailDimensions", xy.toString());
			}
		}

		if(allele.getMolecularImages().size() > 0) {
			// just pick the first one
			Image molImage = allele.getMolecularImages().get(0);
			Integer thumbnailKey = molImage.getThumbnailImageKey();

			logger.info("looking up thumbnail key="+thumbnailKey);
			if (thumbnailKey != null) {
				Image thumbnail = imageFinder.getImageObjectByKey(thumbnailKey.intValue());
				logger.info("found thumbnail = "+thumbnail);
				logger.info("thumbnail id="+thumbnail.getPixeldbNumericID());
				mav.addObject("molecularThumbnail",thumbnail);
				mav.addObject("molecularImage",molImage);
			}
		}

		// add the allele's original reference

		Reference ref = allele.getOriginalReference();
		if (ref != null) {
			mav.addObject("originalReference", ref);
		}

		// add various notes for convenience

		String generalNote = allele.getGeneralNote();
		String derivationNote = allele.getDerivationNote();

		mav.addObject("generalNote", generalNote);
		mav.addObject("derivationNote", derivationNote);

		// already added qtlExpts, if available

		String knockoutNote = null;
		if ((allele.getHolder() != null) && (allele.getCompanyID() != null)) {
			String company = allele.getHolder();

			if (company.equals("Lexicon")) {
				company = "Lexicon Genetics, Inc.";
			} else if (company.equals("Deltagen")) {
				company = "Deltagen, Inc.";
			}

			knockoutNote = "See also, <a href='http://" + ContextLoader.getConfigBean().getProperty("PY_HOST") + "/knockout_mice/" + allele.getHolder().toLowerCase() + "/" + allele.getCompanyID() + ".html' class='MP'>data</a> as provided by " + company;

			mav.addObject("knockoutNote", knockoutNote);
		}

		// IMSR counts, labels, and links

		int imsrCellLineCount = 0;
		int imsrStrainCount = 0;
		int imsrForMarkerCount = 0;

		if (allele.getImsrCellLineCount() != null) {
			imsrCellLineCount = allele.getImsrCellLineCount().intValue();
		}
		if (allele.getImsrStrainCount() != null) {
			imsrStrainCount = allele.getImsrStrainCount().intValue();
		}
		if (allele.getImsrCountForMarker() != null) {
			imsrForMarkerCount = allele.getImsrCountForMarker().intValue();
		}

		String imsrCellLines = imsrCellLineCount + " " + FormatHelper.plural (imsrCellLineCount, "line") + " available";
		String imsrStrains = imsrStrainCount + " " + FormatHelper.plural (imsrStrainCount, "strain") + " available";
		String imsrForMarker = imsrForMarkerCount + " " + FormatHelper.plural (imsrForMarkerCount, "strain or line", "strains or lines") + " available";

		String imsrUrl = ContextLoader.getConfigBean().getProperty("IMSRURL");

		if (imsrCellLineCount > 0) {
			imsrCellLines = "<a href='" + imsrUrl + "summary?gaccid=" + allele.getPrimaryID() + "&states=archived&states=ES+Cell' class='MP'>" + imsrCellLines + "</a>";
		}
		if (imsrStrainCount > 0) {
			imsrStrains = "<a href='" + imsrUrl + "summary?gaccid=" + allele.getPrimaryID() + "&states=archived&states=embryo&states=live&states=ovaries&states=sperm'" + " class='MP'>" + imsrStrains + "</a>";
		}
		if (imsrForMarkerCount > 0) {
			imsrForMarker = "<a href='" + imsrUrl + "summary?gaccid=" + marker.getPrimaryID() + "' class='MP'>" + imsrForMarker + "</a>";
		}

		mav.addObject("imsrCellLines", imsrCellLines);
		mav.addObject("imsrStrains", imsrStrains);
		mav.addObject("imsrForMarker", imsrForMarker);

		// germline transmission / chimera generation

		String transmissionType = allele.getTransmissionType();
		String transmissionPhrase = allele.getTransmissionPhrase();
		String transmissionLabel = "Germline Transmission";

		if (transmissionType != null && !"Not Applicable".equals(transmissionType)) {
			if (transmissionType.equals("Chimeric")) {
				transmissionLabel = "Mouse Generated";
			}

			Reference transmissionRef = allele.getTransmissionReference();
			if (transmissionRef != null) {
				mav.addObject("transmissionReference", transmissionRef);
			}

			mav.addObject("transmissionLabel", transmissionLabel);
			mav.addObject("transmissionPhrase", transmissionPhrase);
		}

		// JBrowse text strings -- adjust if we got coords from a marker, then
		// add to the mav

		if (startCoord > 0) {
			jbrowseExtraLine = marker.getCountOfGeneTraps()+" gene trap insertions have trapped this gene<br/>";
			jbrowseLabel = "View all gene trap sequence tags in this region";
		}

		mav.addObject("jbrowseExtraLine", jbrowseExtraLine);
		mav.addObject("jbrowseLabel", jbrowseLabel);

		// sequence tags

		Sequence representativeSeq = allele.getRepresentativeSequence();
		List<Sequence> otherSeq = allele.getNonRepresentativeSequences();
		String asmVersion = null;

		if (representativeSeq != null) {
			mav.addObject("representativeSeq", representativeSeq);

			// need to pick up coords for JBrowse from representative sequence?
			// if so, add 5kb to each end.
			if (startCoord < 0) {
				List<SequenceLocation> seqLocs = representativeSeq.getLocations();

				if (seqLocs != null && seqLocs.size() > 0) {
					SequenceLocation seqLoc = seqLocs.get(0);
					startCoord = seqLoc.getStartCoordinate().longValue() - 5000;
					endCoord = seqLoc.getEndCoordinate().longValue() + 5000;
					chromosome = seqLoc.getChromosome();
					asmVersion = seqLoc.getVersion();
				}
			}

			// need to pick up point coordinate from representative sequence

			Float point = representativeSeq.getPointCoordinate();
			if (point != null) {
				pointCoord = point.longValue();
			}
		}
		if (otherSeq != null && otherSeq.size() > 0) {
			mav.addObject("otherSequences", otherSeq);

			// if we couldn't get an assembly version from the representative
			// sequence, then try to pull it from another sequence
			for (Sequence seq : otherSeq) {
				if (asmVersion == null) {
					List<SequenceLocation> seqLocs = seq.getLocations();

					if (seqLocs != null && seqLocs.size() > 0) {
						SequenceLocation seqLoc = seqLocs.get(0);
						asmVersion = seqLoc.getVersion();
					}
				}
			}
		}

		if (allele.getSequenceAssociations() != null) {
			mav.addObject("sequenceCount",allele.getSequenceAssociations().size());
		}

		// assemble and include JBrowse URLs (do here because of complexity,
		// rather than in the JSP)

		if (startCoord >= 0 && endCoord >= 0 && chromosome != null) {
			Properties externalUrls = ContextLoader.getExternalUrls();

			// link to jbrowse
			String jbrowseUrl = externalUrls.getProperty("JBrowseGeneTrap").replace("<chromosome>", chromosome).replace("<start>", Long.toString(startCoord)).replace("<end>", Long.toString(endCoord));

			// we only actually want the jbrowse link if we have a point coordinate.
			if (pointCoord >= 0) {
				mav.addObject("jbrowseLink", jbrowseUrl);
			}
		}

		// if we had sequence tags, then we need to have an assembly version.
		// order of preference for determining the assembly version:
		//   1. from sequence coordinates
		//   	a. already tried to get from representative sequence first
		//   	b. already tried to get from other sequences secondarily
		//   2. from marker
		//   3. from class variable
		//   4. fall back on "GRCm39" if none for 1-3

		if(asmVersion==null) {
			if (marker!=null) {
				MarkerLocation mrkLoc = marker.getPreferredCoordinates();
				if(mrkLoc!=null) {
					asmVersion = mrkLoc.getBuildIdentifier();
				}
			}
		}

		// if still null, try the class variable
		if(asmVersion==null) {
			asmVersion = assemblyVersion!=null ? assemblyVersion : "GRCm39";
		} else {
			// cache the asmVersion in class variable
			assemblyVersion=asmVersion;
		}

		mav.addObject("assemblyVersion", asmVersion);

		// do we need to show the recombinase ribbon as Open?
		if ("open".equals(request.getParameter("recomRibbon"))) {
			mav.addObject("recomTeaserStyle", "display:none");
			mav.addObject("recomWrapperStyle", "display:");
		} else {
			mav.addObject("recomTeaserStyle", "display:");
			mav.addObject("recomWrapperStyle", "display:none");
		}

		String userNote = allele.getRecombinaseUserNote();
		if (userNote != null && !userNote.equals("")) {
			try {
				NotesTagConverter ntc = new NotesTagConverter();
				mav.addObject("recombinaseUserNote",ntc.convertNotes(userNote, '|'));
			} catch (Exception e) {
				mav.addObject("recombinaseUserNote", userNote);
			}
		}

		List<SolrAnatomyTerm> results = (List<SolrAnatomyTerm>) vocabularyController.getAnatomySearchPane(allele.getPrimaryID()).getModel().get("results");
		boolean hasAnatomyTerms = results.size() > 0;

		// identify which sections will appear, based on what data is present
		// (needed for table of contents)

		// create an alleleDetail object for determining page logic
		AlleleDetail alleleDetail = new AlleleDetail(allele, hasAnatomyTerms);

		mav.addObject("alleleDetail",alleleDetail);
		mav.addObject("anatomyTermCount", results.size());

		if(allele.getPhenotypeImages().size() > 0 && allele.getPrimaryImage()!=null) {
			mav.addObject("imageCount",allele.getPhenotypeImages().size());
			mav.addObject("hasPrimaryPhenoImage",true);
		}

		return mav;
	}

	@RequestMapping(value="/phenotable/{allID}")
	public ModelAndView phenoTableByAllId(HttpServletRequest request,HttpServletResponse response, @PathVariable("allID") String allID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->phenoTableByAllId started");

		// need to add headers to allow AJAX access
		AjaxUtils.prepareAjaxHeaders(response);

		// setup view object
		ModelAndView mav = new ModelAndView("phenotype_table");

		// find the requested Allele
		logger.debug("->asking alleleFinder for allele");
		List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
		// there can be only one...
		if (alleleList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No allele found for " + allID);
			return mav;
		}
		if (alleleList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + allID);
			return mav;
		}
		Allele allele = alleleList.get(0);
		logger.debug("->1 allele found");

		List<PhenoTableSystem> phenoTableSystems = allele.getPhenoTableSystems();
		Hibernate.initialize(phenoTableSystems);
		logger.debug("->List<PhenoTableSystem> size - " + phenoTableSystems.size());

		// predetermine existance of a few columns.
		boolean hasSexCols=false;
		boolean hasSourceCols=false;
		List<PhenoTableProvider> providerList = null;

		for(PhenoTableGenotype g : allele.getPhenoTableGenotypeAssociations()) {
			if(g.getSexDisplay()!=null && !g.getSexDisplay().trim().equals("")) {
				hasSexCols=true;
			}

			providerList = g.getPhenoTableProviders();
			if ((providerList != null) && ((providerList.size() > 1) || (providerList.size() == 1 && !"MGI".equalsIgnoreCase(providerList.get(0).getInterpretationCenterName())))) {
				hasSourceCols=true;
			}
		}
		mav.addObject("allele",allele);
		mav.addObject("phenoTableSystems",phenoTableSystems);
		mav.addObject("phenoTableGenotypes",allele.getPhenoTableGenotypeAssociations());
		mav.addObject("hasSexCols",hasSexCols);
		mav.addObject("hasSourceCols",hasSourceCols);
		mav.addObject("phenoTableGenoSize",allele.getPhenoTableGenotypeAssociations().size());

		return mav;
	}


	/*
	 *
	 * Test genotype IDs [MGI:2166662]
	 */
	@RequestMapping(value="/genoview/{genoID}")
	public ModelAndView genoview(HttpServletRequest request, HttpServletResponse response, @PathVariable("genoID") String genoID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->genoview started");

		// need to add headers to allow AJAX access
		AjaxUtils.prepareAjaxHeaders(response);

		// setup view object
		ModelAndView mav = new ModelAndView("phenotype_table_geno_popup");

		// find the requested Allele
		logger.debug("->asking genotypeFinder for genotype");

		List<Genotype> genotypeList = genotypeFinder.getGenotypeByID(genoID);
		// there can be only one...
		if (genotypeList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No genotype found for " + genoID);
			return mav;
		}
		if (genotypeList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + genoID);
			return mav;
		}
		Genotype genotype = genotypeList.get(0);

		logger.debug("->1 genotype found");

		// eager load the entire collection
		// calling .size() is another trick eager load the entire collection
		Hibernate.initialize(genotype.getMPSystems());
		mav.addObject("genotype",genotype);
		mav.addObject("mpSystems", genotype.getMPSystems());

/*
		for (GenotypeDisease gd : genotype.getDiseases()) {
			logger.info(" found disease: "+gd.getTerm());
			for(GenotypeDiseaseReference gr : gd.getReferences()) {
				logger.info(" found disease reference: "+gr.getJnumID());
			}
		}
		if(genotype.hasPrimaryImage()) {
			logger.info(" has Image: "+genotype.getPrimaryImage().getMgiID());
		}
*/
		mav.addObject("hasDiseaseModels", genotype.getDiseases().size()>0);
		mav.addObject("hasImage",genotype.hasPrimaryImage());
		mav.addObject("counter", request.getParameter("counter") );

		
		// Setup SEO

		NotesTagConverter ntc = new NotesTagConverter();
		String alleleSymbolConv = ntc.convertNotes(genotype.getCombination3(), '|', true, true).trim();
		
		String description = "View ";
		String keywords = "";
		if(alleleSymbolConv != null) {
			description += alleleSymbolConv;
			keywords += alleleSymbolConv + ", ";
		}
		if(genotype.getBackgroundStrain() != null && !genotype.getBackgroundStrain().equals("Not Specified")) {
			description += " " + genotype.getBackgroundStrain();
			keywords += genotype.getBackgroundStrain() + ", ";
		}
		if(genotype.getCellLines() != null) {
			keywords += genotype.getCellLines() + ", ";
		}
		description += ": phenotypes, images, diseases, and references.";
		keywords += "mouse, mice, murine, Mus";
		
		//description="View ${alleleSymbolConv} ${genotype.backgroundStrain}: phenotypes, images, diseases, and references.";
		//keywords="${alleleSymbolConv}, ${genotype.backgroundStrain}, ${genotype.cellLines}, mouse, mice, murine, Mus";
		
		mav.addObject("seodescription", description);
		mav.addObject("seokeywords", keywords);
		
		return mav;
	}

	@RequestMapping(value="/allgenoviews/{alleleID}")
	public ModelAndView allGenoviews(HttpServletRequest request, HttpServletResponse response, @PathVariable("alleleID") String alleleID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->all genoviews started");

		// need to add headers to allow AJAX access
		AjaxUtils.prepareAjaxHeaders(response);

		// setup view object
		ModelAndView mav = new ModelAndView("phenotype_table_all_geno_popups");

		// find the requested Allele
		logger.debug("->asking alleleFinder for allele");

		List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
		// there can be only one...
		if (alleleList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No allele found for " + alleleID);
			return mav;
		}
		if (alleleList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + alleleID);
			return mav;
		}
		Allele allele = alleleList.get(0);

		logger.debug("->1 allele found");

		// eager load the entire collection
		// calling .size() is another trick eager load the entire collection
		Hibernate.initialize(allele.getPhenoTableGenotypeAssociations());
		mav.addObject("allele", allele);
		mav.addObject("genotypeAssociations",allele.getPhenoTableGenotypeAssociations());
		return mav;
	}

	@RequestMapping(value="/alldiseasegenoviews/{alleleID}")
	public ModelAndView allDiseaseGenoviews(HttpServletRequest request, HttpServletResponse response, @PathVariable("alleleID") String alleleID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->all disease genoviews started");

		// need to add headers to allow AJAX access
		AjaxUtils.prepareAjaxHeaders(response);

		// setup view object
		ModelAndView mav = new ModelAndView("phenotype_table_all_geno_popups");

		// find the requested Allele
		logger.debug("->asking alleleFinder for allele");

		List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
		// there can be only one...
		if (alleleList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No allele found for " + alleleID);
			return mav;
		}
		if (alleleList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + alleleID);
			return mav;
		}
		Allele allele = alleleList.get(0);

		logger.debug("->1 allele found");

		// eager load the entire collection
		// calling .size() is another trick eager load the entire collection
		Hibernate.initialize(allele.getDiseaseTableGenotypeAssociations());
		mav.addObject("genotypeAssociations",allele.getDiseaseTableGenotypeAssociations());

		return mav;
	}

	//---------------------//
	// Allele Disease-Table
	//---------------------//
	@RequestMapping(value="/diseasetable/{allID}")
	public ModelAndView diseaseTableByAllId(HttpServletRequest request,HttpServletResponse response, @PathVariable("allID") String allID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->diseaseTableByAllId started");

		// need to add headers to allow AJAX access
		AjaxUtils.prepareAjaxHeaders(response);

		// setup view object
		ModelAndView mav = new ModelAndView("disease_table");

		mav.addObject("idLinker", idLinker);
		// find the requested Allele
		logger.debug("->asking alleleFinder for allele");
		List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
		// there can be only one...
		if (alleleList.size() < 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No allele found for " + allID);
			return mav;
		}
		if (alleleList.size() > 1) {
			// forward to error page
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Dupe reference found for " + allID);
			return mav;
		}
		Allele allele = alleleList.get(0);
		logger.debug("->1 allele found");

		List<DiseaseTableDisease> diseaseTableDiseases = allele.getDiseaseTableDiseases();
		Hibernate.initialize(diseaseTableDiseases);
		logger.debug("->List<DiseaseTableDisease> size - " + diseaseTableDiseases.size());
		mav.addObject("allele",allele);
		mav.addObject("diseases",diseaseTableDiseases);
		mav.addObject("genotypes",allele.getDiseaseTableGenotypeAssociations());
		mav.addObject("diseaseTableGenoSize",allele.getDiseaseTableGenotypeAssociations().size());

		return mav;
	}

	// convenience method -- construct a ModelAndView for the error page and
	// include the given 'msg' as the error String to be reported
	private ModelAndView errorMav (String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

	// format mutant cell lines for output on allele detail page, with link
	// to provider
	private String formatMutantCellLines (List<String> mcLines, String provider, String idToLink, AlleleID kompID) {
		StringBuffer s = new StringBuffer();

		// comma-delimited list of mutant cell line IDs
		if(mcLines.size()>0) {
			s.append("<b>").append(StringUtils.join(mcLines,"</b>, <b>")).append("</b>");
		}

		return s.toString();
	}

	private void dbDate(ModelAndView mav) {
		mav.addObject("databaseDate", dbInfoFinder.getSourceDatabaseDate());
	}

	private boolean notEmpty(String s) {
		return s!=null && !s.equals("");
	}
	
	@SuppressWarnings("rawtypes")
	private boolean notEmpty(List l) {
		return l!=null && l.size()>0;
	}
	
	// checks any cachable fields of the allele query form, and initializes them if needed
	private void initQFCache() {
		if(AlleleQueryForm.getCollectionValues()==null) {
			// get collection facets
			SearchParams sp = new SearchParams();
			sp.setPageSize(0);
			sp.setFilter(new Filter(SearchConstants.ALL_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD));

			SearchResults<String> sr = alleleFinder.getCollectionFacet(sp);
			List<String> collectionValues = sr.getResultFacets();
			AlleleQueryForm.setCollectionValues(collectionValues);

			SearchResults<String> sr2 = alleleFinder.getMutationFacet(sp);
			List<String> mutationValues = sr2.getResultFacets();
			AlleleQueryForm.setMutationValues(mutationValues);
		}
	}

	/* take a list of possible values for a field named by 'searchConstant'
	 * and turn them into an appropriate Filter for Solr.  if 'joinWithAnd'
	 * is true, then we require all those values when searching (a boolean
	 * AND).  if false then we require at least one of those values (a
	 * boolean OR).
	 */
	private Filter makeListFilter(List<String> values, String searchConstant, boolean joinWithAnd) {
		Filter f = null;
		if(values!=null && values.size()>0) {
			List<Filter> vFilters = new ArrayList<Filter>();
			for(String value : values) {
				vFilters.add(new Filter(searchConstant,value,Filter.Operator.OP_EQUAL));
			}
			if (joinWithAnd) {
				f = Filter.and(vFilters);
			} else {
				f = Filter.or(vFilters);
			}
		}
		return f;
	}
}
