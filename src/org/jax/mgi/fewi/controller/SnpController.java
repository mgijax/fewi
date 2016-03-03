package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.QueryFormOption;
import mgi.frontend.datamodel.sort.SmartAlphaComparator;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.QueryFormOptionFinder;
import org.jax.mgi.fewi.finder.SnpBatchFinder;
import org.jax.mgi.fewi.finder.SnpFinder;
import org.jax.mgi.fewi.forms.SnpQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.summary.ConsensusSNPSummaryRow;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.snpdatamodel.AlleleSNP;
import org.jax.mgi.snpdatamodel.ConsensusAlleleSNP;
import org.jax.mgi.snpdatamodel.ConsensusCoordinateSNP;
import org.jax.mgi.snpdatamodel.ConsensusMarkerSNP;
import org.jax.mgi.snpdatamodel.ConsensusSNP;
import org.jax.mgi.snpdatamodel.PopulationSNP;
import org.jax.mgi.snpdatamodel.SubSNP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
 * This controller maps all /snp/ uri's
 */
@Controller
@RequestMapping(value="/snp")
public class SnpController {


	//--------------------//
	// static variables
	//--------------------//

	// map for 'within X kb' options for the SNP QF
	private static Map<String,String> withinRanges = null;

	// map for 'chromosomes' options for the SNP QF
	private static Map<String, String> chromosomes = null;

	// map for 'selectedStrains' options for the SNP QF
	private static Map<String, String> selectableStrains = null;

	// map for 'selectedStrains' options for the SNP QF
	private static Map<String, String> referenceStrains = null;

	// map for 'coordinateUnits' options for the SNP QF
	private static Map<String, String> coordinateUnits = null;

	// map for options in search by pulldown
	private static Map<String, String> searchByOptions = null;

	// map for options in search by pulldown
	private static Map<String, String> searchBySameDiffOptions = null;
	
	// list for which marker types to show
	private static ArrayList<String> markerfeatureTypes = null;

	// dbSNP build number
	private static String buildNumber = null;

	private static String assemblyVersion = null;

	//--------------------//
	// instance variables
	//--------------------//

	private Logger logger = LoggerFactory.getLogger(SnpController.class);

	@Autowired
	private SnpFinder snpFinder;

	@Autowired
	private QueryFormOptionFinder queryFormOptionFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	//--------------------------------------------------------------------//
	// public methods
	//--------------------------------------------------------------------//

	//----------------------------------------------------------//
	// make sure we have the data we need for the SNP query form
	//----------------------------------------------------------//
	public void initQueryForm(ModelAndView mav) {

		if(coordinateUnits == null) {
			coordinateUnits = new HashMap<String, String>();
			coordinateUnits.put("bp", "bp");
			coordinateUnits.put("Mbp", "Mbp");
		}

		if (chromosomes == null) {
			chromosomes = queryFormOptionFinder.getOptionMap("snp", "chromosome");
			logger.debug("Cached " + chromosomes.size() + " coordinate ranges");

			LinkedHashMap<String,String> tempChromosomes = new LinkedHashMap<String,String>();

			tempChromosomes.put("", "Select One");
			for (String key : chromosomes.keySet()) {
				tempChromosomes.put(key,chromosomes.get(key));
			}

			chromosomes = tempChromosomes;
		}

		if (withinRanges == null) {
			withinRanges = queryFormOptionFinder.getOptionMap("snp", "coordinate range");
			logger.debug("Cached " + withinRanges.size() + " coordinate ranges");
		}

		if (buildNumber == null) {
			SearchResults<QueryFormOption> options = queryFormOptionFinder.getQueryFormOptions("snp", "build number");
			List<QueryFormOption> optionList = options.getResultObjects();

			if (optionList.size() > 0) {
				buildNumber = optionList.get(0).getDisplayValue();
				logger.debug("Cached SNP build number: " + buildNumber);
			}
		}

		if(referenceStrains == null) {
			Map<String, String> tempList = queryFormOptionFinder.getOptionMap("snp", "strain");
			referenceStrains = new LinkedHashMap<String, String>();
			referenceStrains.put("", "No Reference Strain Selected");
			for(String key: tempList.keySet()) {
				referenceStrains.put(key, tempList.get(key));
			}
		}
		
		if (markerfeatureTypes == null) {
			SearchResults<QueryFormOption> options = queryFormOptionFinder.getQueryFormOptions("marker", "mcv");
			List<QueryFormOption> optionList = options.getResultObjects();
			
			markerfeatureTypes = new ArrayList<String>();
			
			// This is a really bad way to do this but not knowing where
			// else to put this list here it is.
			ArrayList<String> markerFeatureTypesToNotDisplay = new ArrayList<String>();
			markerFeatureTypesToNotDisplay.add("all feature types");
			markerFeatureTypesToNotDisplay.add("gene");
			markerFeatureTypesToNotDisplay.add("other feature type");
			markerFeatureTypesToNotDisplay.add("QTL");
			markerFeatureTypesToNotDisplay.add("transgene");
			markerFeatureTypesToNotDisplay.add("cytogenetic marker");
			markerFeatureTypesToNotDisplay.add("chromosomal deletion");
			markerFeatureTypesToNotDisplay.add("insertion");
			markerFeatureTypesToNotDisplay.add("chromosomal inversion");
			markerFeatureTypesToNotDisplay.add("Robertsonian fusion");
			markerFeatureTypesToNotDisplay.add("reciprocal chromosomal translocation");
			markerFeatureTypesToNotDisplay.add("heritable phenotypic marker");
			markerFeatureTypesToNotDisplay.add("chromosomal translocation");
			markerFeatureTypesToNotDisplay.add("chromosomal duplication");
			markerFeatureTypesToNotDisplay.add("chromosomal transposition");
			markerFeatureTypesToNotDisplay.add("unclassified cytogenetic marker");
			markerFeatureTypesToNotDisplay.add("BAC/YAC end");

			for(QueryFormOption o: optionList) {
				if(!markerFeatureTypesToNotDisplay.contains(o.getDisplayValue())) {
					markerfeatureTypes.add(o.getSubmitValue());
				}
			}
			
			logger.debug("Cached " + markerfeatureTypes.size() + " Feature Type options");
		}

		if (selectableStrains == null) {
			selectableStrains = queryFormOptionFinder.getOptionMap("snp", "strain");
			for(String key: selectableStrains.keySet()) {
				selectableStrains.put(key, FormatHelper.superscript(selectableStrains.get(key)));
			}
			logger.debug("Cached " + selectableStrains.size() + " selectable strains");
		}

		if (assemblyVersion == null) {
			SearchResults<QueryFormOption> options = queryFormOptionFinder.getQueryFormOptions("marker", "build_number");
			List<QueryFormOption> optionList = options.getResultObjects();

			if (optionList.size() > 0) {
				assemblyVersion = optionList.get(0).getDisplayValue();
				logger.debug("Cached Assembly number: " + assemblyVersion);
			}
		}

		mav.addObject("chromosomes", chromosomes);
		mav.addObject("withinRanges", withinRanges);
		mav.addObject("selectableStrains", selectableStrains);
		mav.addObject("referenceStrains", referenceStrains);
		mav.addObject("coordinateUnits", coordinateUnits);

		mav.addObject("assemblyVersion", assemblyVersion);
		mav.addObject("buildNumber", buildNumber);

		searchByOptions = new LinkedHashMap<String, String>();
		searchByOptions.put(SearchConstants.MRK_SYMBOL, "Current symbols");
		searchByOptions.put(SearchConstants.MRK_NOMENCLATURE, "Current symbols/names, synonyms & homologs");

		searchBySameDiffOptions = new LinkedHashMap<String, String>();
		searchBySameDiffOptions.put("", "Not compared to the Reference Strain");
		searchBySameDiffOptions.put("diff_reference", "Different from the Reference Strain");
		searchBySameDiffOptions.put("same_reference", "Same as the Reference Strain");

		mav.addObject("searchByOptions", searchByOptions);
		mav.addObject("searchBySameDiffOptions", searchBySameDiffOptions);

		mav.addObject("seoDescription", "Search for mouse SNPs represented in dbSNP by gene or genome region. Results include selected strains. Filter by dbSNP function class.");
		mav.addObject("seoKeywords", "SNP, SNPs, refSNP, dbSNP, build 142, variation, variant, function class, intron, locus region, mRNA-UTR, coding-nonSynonymous, coding-synonymous, noncoding-transcript-variant, splice-site , allele, mouse strains, polymorphism.");

	}

	//--------------------//
	// Snp Query Form
	//--------------------//
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletResponse response) {

		logger.debug("->getQueryForm started");
		response.addHeader("Access-Control-Allow-Origin", "*");

		ModelAndView mav = new ModelAndView("snp/query");
		mav.addObject("sort", new Paginator());

		initQueryForm(mav);

		SnpQueryForm qf = new SnpQueryForm();
		qf.setSelectedStrains(new ArrayList<String>(selectableStrains.keySet()));

		mav.addObject("snpQueryForm", qf);
		return mav;
	}

	//-------------------------//
	// Snp Query Form Summary
	//-------------------------//
	@RequestMapping("/summary")
	public ModelAndView snpSummary(HttpServletRequest request, @ModelAttribute SnpQueryForm queryForm, @ModelAttribute Paginator page, BindingResult result) {

		logger.debug("->snpSummary started");
		//logger.debug("queryString: " + request.getQueryString());

		if(queryForm.getWithinRange() == null || queryForm.getWithinRange().equals("")) {
			queryForm.setWithinRange("2000");
		}

		ModelAndView mav = new ModelAndView("snp/summary");
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("snpQueryForm", queryForm);

		initQueryForm(mav);

		if (queryForm.getMarkerID() != null) {
			Marker mrk = getMarker(queryForm.getMarkerID());
			if (mrk != null) {
				mav.addObject("marker", mrk);
			} else {
				ModelAndView err = new ModelAndView("error");
				err.addObject("errorMsg", "Specified ID does not uniquely identify a marker.");
				return err;
			}
		}
		return mav;
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	// parse the query form object and generate the filters (embedded within a
	// single Filter wrapper object)
	private Filter genFilters(SnpQueryForm query, List<String> matchedMarkerIds, BindingResult result) {
		logger.debug("->genFilters started");
		logger.debug("genFilters: QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();

		// add marker-related filters
		if ( (query.getNomen() != null && query.getNomen().length() > 0) || (query.getMarkerID() != null && query.getMarkerID().length() > 0) ) {
			Filter markerFilter = genMarkerFilter(query, matchedMarkerIds, result);
			if(matchedMarkerIds.size() > 0) {
				logger.debug("matchedMarkerIds: " + matchedMarkerIds);
			}
			if(markerFilter != null) {
				filterList.add(markerFilter);
			}
		}

		// Chromosome Search
		String selectedChromosome = query.getSelectedChromosome();
		if(selectedChromosome != null) {
			Filter chrFilter = new Filter(SearchConstants.CHROMOSOME, selectedChromosome, Operator.OP_IN);
			filterList.add(chrFilter);
		}

		if(filterList.size() > 0) {
			// Coordinate Search
			String coord = query.getCoordinate();
			String coordUnit = query.getCoordinateUnit();
			if(coord != null) {
				Filter coordFilter = FilterUtil.genCoordFilter(coord,coordUnit,true);
				if(coordFilter!=null) {
					filterList.add(coordFilter);
				} else {
					result.addError(new ObjectError("snpQueryForm", "* Please enter one or more search parameters"));
					return null;
				}
			}
		}

		/* Strians Filter - 
		 * no choice of strains from marker detail page, so skip this
		 * section.  (Otherwise, it won't return facets for the 
		 * function class filter.)
		 */
		if((filterList.size() > 0) && query.getMarkerID() == null) {
			List<String> selectedStrains = query.getSelectedStrains();
			if(selectedStrains == null) {
				selectedStrains = new ArrayList<String>();
			}

			String referenceStrain = query.getReferenceStrain();
			if(referenceStrain != null && !referenceStrain.equals("")) {
				Filter referenceStrainFilter = new Filter(SearchConstants.STRAINS, referenceStrain, Operator.OP_IN);
				filterList.add(referenceStrainFilter);
				selectedStrains.remove(referenceStrain);
			}

			Filter selectedStrainFilter = new Filter(SearchConstants.STRAINS, selectedStrains, Operator.OP_IN);
			filterList.add(selectedStrainFilter);
		}

		// function class filter
		List<String> functionClasses = query.getFunctionClassFilter();
		if ((functionClasses != null) && (functionClasses.size() > 0)) {
			filterList.add(new Filter(SearchConstants.FUNCTIONCLASS, functionClasses, Filter.Operator.OP_IN));
		}

		if(filterList.size() > 0) {
			// This needs to get removed when the decision to bring in "Mixed" and In-Del's happen
			// Everything is ready to go other then the display formating - oblod
			filterList.add(new Filter(SearchConstants.VARIATIONCLASS, "SNP"));
			return Filter.and(filterList);
		} else {
			result.addError(new ObjectError("snpQueryForm", "* Please enter one or more search parameters"));
			return null;
		}

	}

	// return Filter for matching markers by ID or coordinates (via the
	// query form's markerID and nomen fields)
	private Filter genMarkerFilter(SnpQueryForm query, List<String> matchedMarkerIds, BindingResult result) {

		ArrayList<Filter> markerFilters = new ArrayList<Filter>();

		// if a marker ID is specified directly, use it
		if (query.getMarkerID() != null) {
			matchedMarkerIds.add(query.getMarkerID());

			Marker mrk = getMarker(query.getMarkerID());

			Filter f = getFilterForMarkerCoordinateSearch(query, mrk);
			if (f != null) {
				markerFilters.add(f);
			}

		} else if (query.getNomen() != null) {
			// if we have markers specified by nomenclature, get their IDs;
			// allowed queries:
			//    1. symbol
			//    2. symbol with wildcard
			//    3. comma-delimited list including combinations of #1 and #2

			List<String> mismatches = new ArrayList<String>();

			String nomen = query.getNomen();

			for (String symbol : nomen.split(",")) {
				String stripped = symbol.trim();
				SearchParams sp = new SearchParams();
				sp.setPageSize(250000);

				// Taken from genFilters in the Marker Controller
				//Filter symbolFilter = new Filter(SearchConstants.MRK_SYMBOL,"\""+stripped.replace("\"","")+"\"^1000000000000",Filter.Operator.OP_HAS_WORD);
				Filter symbolFilter = new Filter(SearchConstants.MRK_SYMBOL, stripped, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED);

				if(query.getSearchGeneBy().equals(SearchConstants.MRK_NOMENCLATURE)) {
					List<Filter> filterList = new ArrayList<Filter>();
					filterList.add(symbolFilter);
					filterList.add(FilterUtil.generateNomenFilter(SearchConstants.MRK_NOMENCLATURE,stripped));
					sp.setFilter(Filter.or(filterList));
				} else {
					sp.setFilter(symbolFilter);
				}

				SearchResults<Marker> results = markerFinder.getMarkers(sp);

				logger.debug("Marker lookup (" + stripped + "), " + results.getResultObjects().size() + " results, filter: " + sp.getFilter());

				if (results.getResultObjects().size() > 0) {
					for (Marker marker : results.getResultObjects()) {
						Filter f = getFilterForMarkerCoordinateSearch(query, marker);
						if (f != null) {
							markerFilters.add(f);
						}
						matchedMarkerIds.add(marker.getPrimaryID());
					}
				} else {
					mismatches.add(symbol);
				}
			}



			// mismatching nomen is not a fatal error, so use extra parameters
			// to ensure the user's original nomen query string appears in the
			// box on the form
			if (mismatches.size() > 1) {
				result.addError(new FieldError("snpQueryForm", "nomen", query.getNomen(), true, null, null, "* Values match no markers: " + StringUtils.join(mismatches, ", ")));
			} else if (mismatches.size() > 0) {
				result.addError(new FieldError("snpQueryForm", "nomen", query.getNomen(), true, null, null, "* Value matches no markers: " + mismatches.get(0)));
			}
		}

		if(markerFilters.size() > 0) {
			return Filter.or(markerFilters);
		} else {
			return null;
		}
	}


	/* get a Filter object which contains the filters necessary for a
	 * single marker
	 */
	private Filter getFilterForMarkerCoordinateSearch(SnpQueryForm qf, Marker m) {
		MarkerLocation ml = m.getPreferredCoordinates();

		// do we have a coordinate-based location, and does it have the
		// three required parts?  if so, build the filter for it.

		if (ml != null) {

			if((ml.getStartCoordinate() != null) && (ml.getEndCoordinate() != null) && (ml.getChromosome() != null)) {
				// see in what range around the marker we need to
				// return SNPs

				Integer withinRange;
				if ((qf.getWithinRange() != null) && (Integer.parseInt(qf.getWithinRange()) >= 0) ) {
					withinRange = Integer.parseInt(qf.getWithinRange());
				} else {
					withinRange = 0;
				}

				// now, assuming we have coordinates, build the range
				// filter and the chromosome filter

				ArrayList<Filter> filters = new ArrayList<Filter>();

				filters.add(new Filter(SearchConstants.CHROMOSOME, ml.getChromosome(), Filter.Operator.OP_EQUAL));

				filters.add(Filter.range(
						SearchConstants.STARTCOORDINATE,
						String.valueOf((int)(ml.getStartCoordinate() * 1) - withinRange),
						String.valueOf((int)(ml.getEndCoordinate() * 1) + withinRange)) );

				// Trying to get
				// ((CHROMOSOME AND STARTCOORDINATE AND ENDCOORDINATE) OR MARKERID)

				Filter andFilter = Filter.and(filters);
				filters = new ArrayList<Filter>();
				filters.add(andFilter);

				filters.add(new Filter(SearchConstants.MARKERID, m.getPrimaryID(), Operator.OP_EQUAL));

				return Filter.or(filters);
			} else {
				// Even if it can't find coordinates there might still be a dbSNP association
				return new Filter(SearchConstants.MARKERID, m.getPrimaryID(), Operator.OP_EQUAL);
			}

		} else {
			return null;
		}

	}

	@RequestMapping(value="/{snpID:.+}", method = RequestMethod.GET)
	public ModelAndView snpDetailByID(@PathVariable("snpID") String snpID) {

		logger.debug("->snpDetailByID started");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter snpIdFilter = new Filter(SearchConstants.SNPID, snpID);
		searchParams.setFilter(snpIdFilter);

		SearchResults<ConsensusSNP> searchResults = snpFinder.getSnp(searchParams);
		List<ConsensusSNP> snpList = searchResults.getResultObjects();

		// there can be only one...
		if (snpList.size() < 1) { // none found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No SNP Found");
			return mav;
		}
		if (snpList.size() > 1) { // dupe found
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", "Duplicate ID");
			return mav;
		}
		// success - we have a single object

		// generate ModelAndView object to be passed to detail page
		ModelAndView mav = new ModelAndView("snp/detail");

		// pull out the snp, sort the markers, filter out those which
		// are related by distance and are beyond 2kb, and add to mav
		ConsensusSNP snp = snpList.get(0);

		for (ConsensusCoordinateSNP ccs : snp.getConsensusCoordinates()) {
			List<ConsensusMarkerSNP> markers = ccs.getMarkers();
			markers = filterByDistance(markers);
			customizeFunctionClasses(markers);

			Collections.sort(markers, new ConsensusMarkerSNPComparator());
			ccs.setMarkers(markers);
		}

		mav.addObject("snp", snp);
		Properties externalUrls = ContextLoader.getExternalUrls();
		mav.addObject("JBrowserLinkTemplate", externalUrls.getProperty("JBrowseHighlight"));

		// pull out the list of strains and add to mav, if any

		List<String> strains = new ArrayList<String>();
		for (ConsensusAlleleSNP alleleCall : snp.getConsensusAlleles()) {
			strains.add(alleleCall.getStrain());
		}
		Collections.sort(strains, new SmartAlphaComparator<String>());

		if (strains.size() > 0) {
			mav.addObject("strains", strains);
		}

		// separate the SubSNPs into two lists and add to mav, if any:
		// 1. list of SubSNPs with per-strains allele calls in at least one population
		// 2. list of SubSNPs with per-strain allele calls in at least one population

		List<SubSNP> withStrains = new ArrayList<SubSNP>();
		List<SubSNP> noStrains = new ArrayList<SubSNP>();

		for (SubSNP subSNP : snp.getSubSNPs()) {
			boolean addedToWith = false;
			boolean addedToWithout = false;

			for (PopulationSNP pop : subSNP.getPopulations()) {
				// check whether we need to put the subSNP
				// into either list

				List<AlleleSNP> ssAlleles = pop.getAlleles();
				if ((ssAlleles != null) && (ssAlleles.size() > 0)) {
					if (!addedToWith) {
						withStrains.add(subSNP);
						addedToWith = true;
					}
				} else if (!addedToWithout) {
					noStrains.add(subSNP);
					addedToWithout = true;
				}
			}
		}
		if (withStrains.size() > 0) {
			mav.addObject("subSnpsWithStrains", withStrains);
		}
		if (noStrains.size() > 0) {
			mav.addObject("subSnpsWithoutStrains", noStrains);
		}

		// determine if there are any marker associations and add a
		// flag to the mav if so

		boolean hasMarkers = false;
		for (ConsensusCoordinateSNP cc : snp.getConsensusCoordinates()){
			List<ConsensusMarkerSNP> markers = cc.getMarkers();
			if ((markers != null) && (markers.size() > 0)) {
				hasMarkers = true;
			}
		}
		if (hasMarkers) {
			mav.addObject("hasMarkers", "true");
		}

		String snpAssemblyVersion = "GRCm" + ContextLoader.getConfigBean().getProperty("SNP_ASSEMBLY_VERSION");
		mav.addObject("snpAssemblyVersion", snpAssemblyVersion);

		setupSeo(mav, snp);

		return mav;
	}

	private void setupSeo(ModelAndView mav, ConsensusSNP snp) {
		String locations = "";
		String comma = "";
		for(ConsensusCoordinateSNP c: snp.getConsensusCoordinates()) {
			locations += comma + "Chr:" + c.getChromosome() + ":" + c.getStartCoordinate();
			comma = ", ";
		}

		String seoDescription = "View concensus SNP " + snp.getAccid() + " at location " + locations + ", with variation, flanking sequence, strains, SNP assays, strains, function classes, and genes.";

		String ssIds = "";
		String subIds = "";
		String space = "";
		for(SubSNP s: snp.getSubSNPs()) {
			ssIds += space + s.getAccid();
			subIds += space + s.getSubmitterId();
			space = ", ";
		}

		String seoKeywords = snp.getAccid() + ", " + ssIds + ", " + subIds;
		mav.addObject("seoDescription", seoDescription);
		mav.addObject("seoKeywords", seoKeywords);

	}

	// easy method to look up a marker
	private Marker getMarker (String mrkID) {
		// setup search parameters object to gather the requested object
		SearchParams searchParams = new SearchParams();
		Filter mrkIdFilter = new Filter(SearchConstants.MRK_ID, mrkID);
		searchParams.setFilter(mrkIdFilter);

		// find the requested marker
		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> mrkList = searchResults.getResultObjects();

		// there can be only one...
		if (mrkList.size() != 1) {
			return null;
		}

		// pull out the marker, and return it
		return mrkList.get(0);
	}

	//-------------------------------//
	//  Summary by Marker
	//-------------------------------//
	// get the SNP summary page when coming from a link on the marker
	// detail page, rather than from the query form
	@RequestMapping(value="/marker/{mrkID}")
	public ModelAndView snpSummaryByMarker(@PathVariable("mrkID") String mrkID) {

		logger.debug("->snpSummaryByMarker started");

		Marker marker = getMarker(mrkID);

		// error if no marker...
		if (marker == null) {
			// forward to error page
			ModelAndView mav = new ModelAndView("error");
			mav.addObject("errorMsg", mrkID + " does not uniquely identify a marker");
			return mav;
		}

		// place the marker into the mav
		ModelAndView mav = new ModelAndView("snp/summary");
		initQueryForm(mav);
		mav.addObject("marker", marker);

		// place a SnpQueryForm into the mav
		SnpQueryForm form = new SnpQueryForm();
		form.setMarkerID(mrkID);
		mav.addObject("snpQueryForm", form);

		// pre-generate query string
		mav.addObject("queryString", "markerID=" + marker.getPrimaryID()+"&withinRange=2000");

		return mav;
	}

	//--------------------------------//
	// table of SNPs for summary page
	//--------------------------------//
	@RequestMapping("/table")
	public ModelAndView snpSummaryTable(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SnpQueryForm query, @ModelAttribute Paginator page, BindingResult result) {

		logger.debug("->snpSummaryTable started");

		// add headers to allow Ajax access
		AjaxUtils.prepareAjaxHeaders(response);

		// set up MAV
		ModelAndView mav = new ModelAndView("snp/summary_table");
		initQueryForm(mav);

		// list of IDs for markers which matched the query
		List<String> matchedMarkerIds = new ArrayList<String>();

		// Defaults all strains on when coming from marker detail page
		if(query.getMarkerID() != null && query.getMarkerID().length() > 0) {
			query.setSelectedStrains(new ArrayList<String>(selectableStrains.keySet()));
		}

		// build the set of filters for the search
		Filter searchFilter = genFilters(query, matchedMarkerIds, result);

		// if the user searched for SNPs in a genome region (not for
		// a marker or set of markers by nomenclature), then we want
		// to include a JBrowse link for the region.

		Properties externalUrls = ContextLoader.getExternalUrls();
		String jbrowse = externalUrls.getProperty("JBrowseHighlight");

		Filter chromFilter = searchFilter.getFirstFilterFor(SearchConstants.CHROMOSOME);
		Filter coordFilter = searchFilter.getFirstFilterFor(SearchConstants.STARTCOORDINATE);
		
		if (jbrowse != null && searchFilter != null && chromFilter != null && coordFilter != null) {
			String chrom = null;
			long startCoord = 0;
			long endCoord = 0;

			try {
				if ((query.getSelectedChromosome() != null) && (query.getCoordinate() != null) && (query.getCoordinateUnit() != null) && (chromFilter != null) && (coordFilter != null)) {
					chrom = chromFilter.getValues().get(0);
					startCoord = Long.parseLong(coordFilter.getValues().get(0));
					endCoord = Long.parseLong(coordFilter.getValues().get(1));
				}

				if (chrom != null) {
					// Add padding to either side of the coordinates
					// to produce a pleasing presentation (not all
					// highlighted).  We'll add 50% to each side.

					long pad = (endCoord - startCoord) / 2;

					// compensate for tiny ranges (eg- point coords)
					if (pad < 100) { pad = 2000; }

					String link = jbrowse.replace("<chromosome>", chrom).replaceFirst("<start>", String.valueOf(Math.max(0, startCoord - pad))).replaceFirst("<end>", String.valueOf(endCoord + pad)).replace("<start>", String.valueOf(startCoord)).replace("<end>", String.valueOf(endCoord));
					mav.addObject("jbrowseLink", link);
				}
			} catch (NumberFormatException e) {
				// pass - skip the link if conversion issue
			}
		}
		
		if(markerfeatureTypes != null && query.getSelectedChromosome() != null && query.getCoordinate() != null && query.getCoordinateUnit() != null && searchFilter != null && chromFilter != null && coordFilter != null) {
			String dlim = "";
			String queryString = "";
			for(String id: markerfeatureTypes) {
				queryString += dlim + "mcv=" + id;
				dlim = "&";
			}
			String markerSummaryLink = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "marker/summary?chromosome=" + query.getSelectedChromosome() + "&coordinate=" + query.getCoordinate() + "&coordUnit=" + query.getCoordinateUnit() + "&" + queryString;
			mav.addObject("markerSummaryLink", markerSummaryLink);
		}

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(request));
		
		Filter sameDiffFilter = genSameDiffFilter(query);
		
		if(searchFilter != null) {
			if(sameDiffFilter != null) {
				ArrayList<Filter> list = new ArrayList<Filter>();
				list.add(searchFilter);
				list.add(sameDiffFilter);
				params.setFilter(Filter.and(list));
			} else {
				params.setFilter(searchFilter);
			}
		}

		SearchResults<ConsensusSNPSummaryRow> searchResults = snpFinder.getSummarySnps(params, matchedMarkerIds);

		mav.addObject("snps", searchResults.getResultObjects());

		// Which strains to show logic
		// Reference Strain handled in query.getSelectedStrains
		List<String> displayedStrains = null;

		if(query.isHideStrains()) {
			List<String> resultStrains = searchResults.getResultFacets();
			List<String> selectedStrains = query.getSelectedStrains();

			if(selectedStrains == null) selectedStrains = new ArrayList<String>();
			if(query.getReferenceStrain() != null && !query.getReferenceStrain().equals("")) {
				selectedStrains.remove(query.getReferenceStrain());
				selectedStrains.add(0, query.getReferenceStrain());
			}

			displayedStrains = new ArrayList<String>();
			for(String strain: selectedStrains) {
				if(resultStrains.contains(strain)) {
					displayedStrains.add(strain);
				}
			}
		} else {
			displayedStrains = query.getSelectedStrains();
			if(query.getReferenceStrain() != null && !query.getReferenceStrain().equals("")) {
				displayedStrains.add(0, query.getReferenceStrain());
			}
		}

		Map<String,String> strainHeaders = new HashMap<String,String>();
		if ((displayedStrains != null) && (displayedStrains.size() > 0)){
			for (String s : displayedStrains) {
				strainHeaders.put(s, getStrainCode(s));
			}
		}

		mav.addObject("count", searchResults.getTotalCount());
		mav.addObject("referenceStrain", query.getReferenceStrain());
		mav.addObject("strains", displayedStrains);
		mav.addObject("strainHeaders", strainHeaders);

		return mav;
	}

	private Filter genSameDiffFilter(SnpQueryForm query) {
		if(query.getSearchBySameDiff() != null && query.getReferenceStrain() != null && query.getReferenceStrain().length() > 0) {

			List<Filter> filterList = new ArrayList<Filter>();

			filterList.add(new Filter(SearchConstants.SAME_STRAINS, query.getReferenceStrain(), Operator.OP_IN));

			if(query.getSearchBySameDiff().equals("same_reference")) {
				filterList.add(new Filter(SearchConstants.SAME_STRAINS, query.getSelectedStrains(), Operator.OP_IN));
				filterList.add(new Filter(SearchConstants.DIFF_STRAINS, query.getSelectedStrains(), Operator.OP_NOT_IN));
				return Filter.and(filterList);
			} else if(query.getSearchBySameDiff().equals("diff_reference")) {

				filterList.add(new Filter(SearchConstants.SAME_STRAINS, query.getSelectedStrains(), Operator.OP_NOT_IN));
				filterList.add(new Filter(SearchConstants.DIFF_STRAINS, query.getSelectedStrains(), Operator.OP_IN));
				return Filter.and(filterList);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/* takes a strain name with non-alphanumeric characters and converts
	 * it into a unique string that is acceptable as an HTML ID attribute
	 */
	private static String getStrainCode(String value) {
		StringBuffer sb = new StringBuffer();
		sb.append("s_");	// must start with a letter

		String upStrain = value.toUpperCase();

		for (int i = 0; i < upStrain.length(); i++) {
			char c = upStrain.charAt(i);
			if ((c >= 'A') && (c <= 'Z')) { sb.append(c); }
			else if ((c >= '0') && (c <= '9')) { sb.append(c); }
			else if (c == '/') { sb.append("slash"); }
			else if (c == '<') { sb.append("lt"); }
			else if (c == '>') { sb.append("gt"); }
			else if (c == '+') { sb.append("plus"); }
			else if (c == '-') { sb.append("dash"); }
			else if (c == '.') { sb.append("dot"); }
			else if (c == ' ') { sb.append("space"); }
			else { sb.append((int) c); }	// ASCII code
		}
		return sb.toString();
	}

	//-------------------------------------//
	// total count of SNPs for summary page
	//-------------------------------------//
	//	@RequestMapping("/count")
	//	public @ResponseBody Integer snpSummaryCount(HttpServletRequest request, @ModelAttribute SnpQueryForm query, BindingResult result) {
	//
	//		logger.debug("->snpSummaryCount started");
	//
	//		// list of IDs for markers which matched the query
	//		List<String> matchedMarkerIds = new ArrayList<String>();
	//		
	//		// build the set of filters for the search
	//		Filter searchFilters = genFilters(query, matchedMarkerIds, result);
	//
	//		// build the set of parameters for the query
	//		SearchParams params = new SearchParams();
	//
	//		// we don't want any actual results, just the count
	//		params.setPageSize(0);
	//
	//		if(searchFilters != null) {
	//			params.setFilter(searchFilters);
	//		}
	//
	//		SearchResults<ConsensusSNPSummaryRow> searchResults = snpFinder.getSummarySnps(params, matchedMarkerIds);
	//
	//		return searchResults.getTotalCount();
	//	}

	//	// This method handles requests various reports; txt, xls.  It is intended 
	//	// to perform the same query as the json method above, but only place the 
	//	// result obljects list on the model.  It returns a string to indicate the
	//	// view name to look up in the view class in the excel or text.properties
	//	@RequestMapping("/report*")
	//	public String snpSummaryReport(
	//			HttpServletRequest request, Model model,
	//			@ModelAttribute SnpQueryForm query,
	//			@ModelAttribute Paginator page) {
	//
	//		logger.debug("snpSummaryReport");		
	//		
	//		SearchParams params = new SearchParams();
	//		params.setPaginator(page);
	//		SearchResults<ConsensusSNP> searchResults = snpFinder.getSnps(params);
	//		
	//		model.addAttribute("results", searchResults.getResultObjects());
	//		return "snpSummaryReport";			
	//	}

	// This method maps requests for the function class facet list for
	// SNPs matching the query.
	@RequestMapping("/facet/functionClass")
	public @ResponseBody Map<String, List<String>> facetAuthor(@ModelAttribute SnpQueryForm query, BindingResult result, HttpServletResponse response) {
		// perform query and return results as json
		logger.debug("get function class facets here");
		AjaxUtils.prepareAjaxHeaders(response);

		// list of IDs for markers which matched the query
		List<String> matchedMarkerIds = new ArrayList<String>();

		// build the set of filters for the search
		Filter searchFilters = genFilters(query, matchedMarkerIds, result);

		// perform query, and pull out the requested objects
		SearchParams params = new SearchParams();
		if(searchFilters != null) {
			params.setFilter(searchFilters);
		}

		List<String> facets = snpFinder.getFunctionClassFacets(params);

		// must remove "Contig-Reference", since display of this is
		// suppressed on the summary page (see summary object)
		facets.remove("Contig-Reference");

		SearchResults<String> results = new SearchResults<String>();
		results.setResultFacets(facets);

		return parseFacetResponse(results);
	}

	// SNPs out to tab-delimited file (from summary page)
	@RequestMapping("/report*")
	public ModelAndView snpsToTabDelimitedFile (HttpServletRequest request,
			@ModelAttribute SnpQueryForm query, BindingResult result) {

		logger.debug("generating tab-delimited report");

		// Since there can be a very large number of SNPs returned, 
		// we will use a SnpBatchFinder to retrieve & process them in
		// batches to save memory on the server.

		List<String> matchedMarkerIds = new ArrayList<String>();
		Filter searchFilter = genFilters(query, matchedMarkerIds, result);
		Filter sameDiffFilter = genSameDiffFilter(query);
		
		SearchParams params = new SearchParams();
		
		if(searchFilter != null) {
			if(sameDiffFilter != null) {
				ArrayList<Filter> list = new ArrayList<Filter>();
				list.add(searchFilter);
				list.add(sameDiffFilter);
				params.setFilter(Filter.and(list));
			} else {
				params.setFilter(searchFilter);
			}
		}

		params.setSorts(genSorts(request));
		SnpBatchFinder batchFinder = new SnpBatchFinder(snpFinder, params);

		ModelAndView mav = new ModelAndView("snpSummaryReport");
		mav.addObject("resultFinder", batchFinder);
		mav.addObject("queryString", request.getQueryString());
		initQueryForm(mav);

		if (matchedMarkerIds.size() > 0) {
			mav.addObject("matchedMarkerIds", matchedMarkerIds);
			mav.addObject("withinRange", query.getWithinRange());
		}
		return mav; 
	}

	// This is a convenience method to parse the facet response from the 
	// SearchResults object, inspect it for error conditions, and return a 
	// map that the ui is expecting.
	private Map<String, List<String>> parseFacetResponse(SearchResults<String> facetResults) {

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();

		if (facetResults.getResultFacets().size() >= facetLimit){
			logger.debug("too many facet results");
			l.add("Too many results to display. Modify your search or try another filter first.");
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() == 0) {
			logger.debug("no facet results");
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			List<String> facets = facetResults.getResultFacets();
			Collections.sort(facets, new SmartAlphaComparator<String>());
			m.put("resultFacets", facets);
		}
		return m;
	}

	// generate the sorts
	private List<Sort> genSorts(HttpServletRequest request) {

		logger.debug("->genSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// Change this to new SORT column once added
		// SearchConstants.SNPSORTCOLUMN

		Sort sort = new Sort(SearchConstants.CHROMOSOME, false);
		sorts.add(sort);
		sort = new Sort(SearchConstants.STARTCOORDINATE, false);
		sorts.add(sort);

		logger.debug ("sort: " + sort.toString());
		return sorts;
	}

	// update function classes where tweaks are needed for display on SNP
	// detail page
	private void customizeFunctionClasses (List<ConsensusMarkerSNP> markers) {
		/* customizations needed:
		 * 1. if we have a distance-direction and a distance-from, then
		 * 	we need to note updated to "<distance> bp <direction>"
		 * 2. if we have a distance-direction and a Locus-Region, then
		 * 	we needto update to include the direction
		 * 3. strip out any trailing " of"
		 */
		for (ConsensusMarkerSNP m : markers) {
			String direction = m.getDistanceDirection();
			int from = m.getDistanceFrom();
			String fc = m.getFunctionClass();

			if ("Locus-Region".equals(fc) && (direction != null)) {
				m.setFunctionClass(fc + " (" + direction + ")");
			} else if ("within distance of".equals(fc) && (direction != null) && (from > 0)) {
				m.setFunctionClass(from + " bp " + direction);
			} else if (fc.endsWith(" of")) {
				m.setFunctionClass(fc.replace(" of", ""));
			}
		}
	}

	// trim out coordinates-based marker associations that are beyond 2k
	private List<ConsensusMarkerSNP> filterByDistance (List<ConsensusMarkerSNP> markers) {
		List<ConsensusMarkerSNP> filtered = new ArrayList<ConsensusMarkerSNP>();
		for (ConsensusMarkerSNP m : markers) {
			if (!"within distance of".equals(m.getFunctionClass()) || (m.getDistanceFrom() <= 2000) ) {
				filtered.add(m);
			}
		}
		return filtered;
	}

	// comparator for sorting related markers for each SNP coordinate
	private class ConsensusMarkerSNPComparator extends SmartAlphaComparator<ConsensusMarkerSNP> {
		public int compare(ConsensusMarkerSNP m1, ConsensusMarkerSNP m2) {
			// order smart-alpha by:
			// 1. symbol
			// 2. transcript ID
			// 3. function class

			int i = super.compare(m1.getSymbol(), m2.getSymbol());
			if (i != 0) { return i; }

			i = super.compare(m1.getTranscript(), m2.getTranscript());
			if (i != 0) { return i; }

			return super.compare(m1.getFunctionClass(), m2.getFunctionClass());
		}
	}

}
