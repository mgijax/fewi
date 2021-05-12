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
import org.jax.mgi.fewi.forms.StrainQueryForm;
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
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
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

	// list for which marker types to show
	private static ArrayList<String> markerfeatureTypes = null;

	// dbSNP build number
	private static String snpBuildNumber = null;

	private static String[] homologSymbolFields = new String[] {
        SearchConstants.MRK_SYMBOL, 
        SearchConstants.MRK_HUMAN_SYMBOL, 
        SearchConstants.MRK_RAT_SYMBOL, 
        SearchConstants.MRK_RHESUS_SYMBOL, 
        SearchConstants.MRK_CATTLE_SYMBOL, 
        SearchConstants.MRK_DOG_SYMBOL, 
        SearchConstants.MRK_ZFIN_SYMBOL, 
        SearchConstants.MRK_CHICKEN_SYMBOL, 
        SearchConstants.MRK_CHIMP_SYMBOL, 
        SearchConstants.MRK_FROG_SYMBOL
	};

	// list of DO/CC Founder strain names (retrieved and cached)
	private static List<String> doccFounders = null;
	
	// list of Sanger Mouse Genomes Project (MGP) strain names (retrieved and cached)
	private static List<String> mgpStrains = null;

	// list of 'allele agreement' filter choices
	public static String referenceAllelesMatch = "All reference strains agree on allele call";
	public static String comparisonAllelesDiffer = "All reference strains agree and all comparison strains differ from reference";
	public static String comparisonAllelesAgree = "All reference strains agree and all comparison strains agree with reference";

	//--------------------//
	// instance variables
	//--------------------//

	private Logger logger = LoggerFactory.getLogger(SnpController.class);

	@Autowired
	private StrainController strainController;

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

	public String getSnpBuildNumber() {
		if (snpBuildNumber == null) {
			SearchResults<QueryFormOption> options = queryFormOptionFinder.getQueryFormOptions("snp", "build number");
			List<QueryFormOption> optionList = options.getResultObjects();

			if (optionList.size() > 0) {
				snpBuildNumber = optionList.get(0).getDisplayValue();
				logger.debug("Cached SNP build number: " + snpBuildNumber);
			}
		}
		return snpBuildNumber;
	}
	
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

		String dbSnpBuildNumber = getSnpBuildNumber();

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

		if ((doccFounders == null) || (mgpStrains == null)) {
			StrainQueryForm doccQuery = new StrainQueryForm();
			doccQuery.setGroup("DOCCFounders");
			doccFounders = makeStrainList(doccQuery);
			logger.debug("Cached " + doccFounders.size() + " DO/CC Founders");

			StrainQueryForm mgpQuery = new StrainQueryForm();
			mgpQuery.setIsSequenced(1);
			mgpStrains = makeStrainList(mgpQuery);
			logger.debug("Cached " + mgpStrains.size() + " MGP Strains");
		}
		mav.addObject("doccFounders", doccFounders);
		mav.addObject("mgpStrains", mgpStrains);
		
		mav.addObject("chromosomes", chromosomes);
		mav.addObject("withinRanges", withinRanges);
		mav.addObject("selectableStrains", selectableStrains);
		mav.addObject("referenceStrains", referenceStrains);
		mav.addObject("coordinateUnits", coordinateUnits);

		mav.addObject("assemblyVersion", ContextLoader.getConfigBean().getProperty("SNP_ASSEMBLY_VERSION"));
		mav.addObject("dbSnpBuildNumber", this.getSnpBuildNumber());
		mav.addObject("buildNumber", ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION"));

		searchByOptions = new LinkedHashMap<String, String>();
		searchByOptions.put(SearchConstants.MRK_HOMOLOG_SYMBOLS, "Current symbol (mouse or homologs)");
		searchByOptions.put(SearchConstants.MRK_SYMBOL, "Current symbol (mouse)");
		searchByOptions.put(SearchConstants.MRK_NOMENCLATURE, "Current symbols/names, synonyms & homologs");

		mav.addObject("searchByOptions", searchByOptions);

		Map<String,String> yesNoOptions = new LinkedHashMap<String, String>();
		yesNoOptions.put("yes", "Yes");
		yesNoOptions.put("no", "No");
		mav.addObject("yesNoOptions", yesNoOptions);

		mav.addObject("seoDescription", "Search for mouse SNPs by gene or genome region. Results include selected strains. Filter by function class.");
		mav.addObject("seoKeywords", "SNP, SNPs, refSNP, build 142, variation, variant, function class, intron, locus region, mRNA-UTR, coding-nonSynonymous, coding-synonymous, noncoding-transcript-variant, splice-site , allele, mouse strains, polymorphism.");

	}

	//--------------------//
	// Snp Query Form
	//--------------------//
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletRequest request, HttpServletResponse response) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->getQueryForm started");
		response.addHeader("Access-Control-Allow-Origin", "*");

		ModelAndView mav = new ModelAndView("snp/query");
		mav.addObject("sort", new Paginator());

		initQueryForm(mav);

		SnpQueryForm qf = new SnpQueryForm();
		qf.setSelectedStrains(new ArrayList<String>(selectableStrains.keySet()));
		qf.setDefaults();

		mav.addObject("snpQueryForm", qf);
		return mav;
	}

	//-------------------------//
	// Snp Query Form Summary
	//-------------------------//
	@RequestMapping("/summary")
	public ModelAndView snpSummary(HttpServletRequest request, @ModelAttribute SnpQueryForm queryForm, @ModelAttribute Paginator page, BindingResult result) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->snpSummary started");
		//logger.debug("queryString: " + request.getQueryString());

		if(queryForm.getWithinRange() == null || queryForm.getWithinRange().trim().equals("")) {
			queryForm.setWithinRange("2000");
		}
		queryForm.setDefaults();

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
		
		if ((queryForm.getAlleleAgreementFilter() != null) && (queryForm.getAlleleAgreementFilter().size() > 1)) {
			ModelAndView err = new ModelAndView("error");
			err.addObject("errorMsg", "Please choose only one value for the Allele Agreement filter.<br/>"
				+ "Note: The filter \"" + referenceAllelesMatch + "\" is automatically applied when you choose "
				+ "either of the \"Comparison Strains Differ or Agree\" filters.");
			return err;
		}
		return mav;
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	// identifier can be either a marker ID, a marker symbol, or a RefSNP ID.  Look up relevant data to
	// find the coordinates for the specified item.  Return as a CoordinateRange.  Returns null if
	// we cannot find a location for the specified identifier.
	private CoordinateRange getCoordinates(String identifier) {
		// check by marker ID first
		SearchResults<Marker> markerIDresults = markerFinder.getMarkerByID(identifier);
		if (markerIDresults.getTotalCount() > 0) {
			if (markerIDresults.getTotalCount() == 1) {
				MarkerLocation ml = markerIDresults.getResultObjects().get(0).getPreferredCoordinates();
				if (ml != null) {
					return new CoordinateRange(ml);
				} else {
					return new CoordinateRange("Marker " + identifier + " has no coordinates");
				}
			} else {
				return new CoordinateRange(identifier + " matches multiple markers");
			}
		}
		
		// failing that, check by marker symbol
		
		SearchResults<Marker> markerSymbolResults = markerFinder.getMarkerBySymbol(identifier);
		if (markerSymbolResults.getTotalCount() > 0) {
			if (markerSymbolResults.getTotalCount() == 1) {
				MarkerLocation ml = markerSymbolResults.getResultObjects().get(0).getPreferredCoordinates();
				if (ml != null) {
					return new CoordinateRange(ml);
				} else {
					return new CoordinateRange("Marker " + identifier + " has no coordinates");
				}
			} else {
				return new CoordinateRange(identifier + " matches multiple markers");
			}
		}
		
		// finally, check by SNP ID
		SearchResults<ConsensusSNP> snpResults = snpFinder.getSnpByID(identifier);
		if (snpResults.getTotalCount() > 0) {
			if (snpResults.getTotalCount() == 1) {
				List<ConsensusCoordinateSNP> coords = snpResults.getResultObjects().get(0).getConsensusCoordinates();
				if (coords.size() == 1) {
					return new CoordinateRange(coords.get(0));
				} else if (coords.size() > 1) {
					return new CoordinateRange("SNP " + identifier + " has multiple coordinates");
				} else {
					return new CoordinateRange("SNP" + identifier + " has no coordinates");
				}
			} else {
				return new CoordinateRange(identifier + " matches multiple SNPs");
			}
		}
		
		return new CoordinateRange("Cannot find " + identifier);
	}
	
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
		if(selectedChromosome != null && selectedChromosome.length() > 0) {
			Filter chrFilter = new Filter(SearchConstants.CHROMOSOME, selectedChromosome, Operator.OP_IN);
			filterList.add(chrFilter);

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

		// restrict to a particular slice due to zooming in via a heatmap cell
		Long sliceStart = query.getSliceStartCoord();
		Long sliceEnd = query.getSliceEndCoord();
		if ((sliceStart != null) && (sliceEnd != null)) {
			Filter sliceFilter = FilterUtil.genCoordFilter(sliceStart + "-" + sliceEnd, "bp", true);
			if(sliceFilter!=null) {
				filterList.add(sliceFilter);
			} else {
				result.addError(new ObjectError("snpQueryForm", "* Faulty selection of heatmap slice (internal error)"));
				return null;
			}
		}

		// Marker Range search
		String startMarker = query.getStartMarker();
		String endMarker = query.getEndMarker();
		if ((startMarker != null) && (endMarker != null) && (startMarker.trim().length() > 0) && (endMarker.trim().length() > 0)) {
			// Both start marker and end marker were specified.  Ensure that we don't also have a coordinate search.
			if ((query.getCoordinate() != null) && (query.getCoordinate().trim().length() > 0)) {
				result.addError(new ObjectError("snpQueryForm", "Please search by either Genome Coordinates or Marker Range, but not both."));
				return null;
			}
			CoordinateRange startCR = getCoordinates(startMarker);
			if (startCR.error != null) {
				result.addError(new ObjectError("snpQueryForm", "Error in the start marker: " + startCR.error));
				return null;
			}
			CoordinateRange endCR = getCoordinates(endMarker);
			if (endCR.error != null) {
				result.addError(new ObjectError("snpQueryForm", "Error in the end marker: " + endCR.error));
				return null;
			}

			// Confirm that the markers exist on the same chromosome.
			if (!startCR.chromosome.equals(endCR.chromosome)) {
				result.addError(new ObjectError("snpQueryForm", "Markers " + startMarker + " and " + endMarker + " are on different chromosomes."));
				return null;
			}
			
			// Confirm that a user-selected chromosome matches.
			if ((selectedChromosome != null) && (!selectedChromosome.trim().equals("")) && !selectedChromosome.equals(startCR.chromosome)) {
				result.addError(new ObjectError("snpQueryForm", "Selected chromosome does not match the chromosome of the specified markers."));
				return null;
			}
			
			String coordString = startCR.getSpan(endCR).getCoords();
			Filter coordFilter = FilterUtil.genCoordFilter(coordString, "bp", true);
			if (coordFilter != null) {
				filterList.add(coordFilter);
			}

			Filter chrFilter = new Filter(SearchConstants.CHROMOSOME, startCR.chromosome, Operator.OP_IN);
			filterList.add(chrFilter);
			
		} else if ((startMarker != null) && (endMarker == null)) {
			result.addError(new ObjectError("snpQueryForm", "To search by Marker Range, please enter both a start marker and an end marker"));
			return null;
			
		} else if ((startMarker == null) && (endMarker != null)) {
			result.addError(new ObjectError("snpQueryForm", "To search by Marker Range, please enter both a start marker and an end marker"));
			return null;
		}
		
		/* Strains Filter - 
		 * no choice of strains from marker detail page, so skip this
		 * section.  (Otherwise, it won't return facets for the 
		 * function class filter.)
		 */
		if((filterList.size() > 0) && query.getMarkerID() == null) {
			List<String> selectedStrains = query.getSelectedStrains();
			if(selectedStrains == null) {
				selectedStrains = new ArrayList<String>();
			}

			List<String> referenceStrains = query.getReferenceStrains();
			if(referenceStrains != null && (referenceStrains.size() > 0)) {
				List<Filter> refStrains = new ArrayList<Filter>();
				
				for (String referenceStrain : referenceStrains) {
					refStrains.add(new Filter(SearchConstants.STRAINS, referenceStrain, Operator.OP_IN));
					selectedStrains.remove(referenceStrain);
				}
				if ("yes".equalsIgnoreCase(query.getAllowNullsForReferenceStrains())) {
					// Just require at least one reference strain to have an allele call in each SNP.
					filterList.add(Filter.or(refStrains));
				} else {
					// Require all reference strains to have an allele call in each SNP. (default)
					filterList.add(Filter.and(refStrains));
				}
			}

			if ((selectedStrains != null) && (selectedStrains.size() > 0)) {
				List<Filter> cmpStrains = new ArrayList<Filter>();
				for (String comparisonStrain : selectedStrains) {
					// default behavior is just to look for comparison strains (at least one) in the list
					// of strain with an allele call for the SNP.
					cmpStrains.add(new Filter(SearchConstants.STRAINS, comparisonStrain, Operator.OP_IN));
				}
				if ("no".equalsIgnoreCase(query.getAllowNullsForComparisonStrains())) {
					// Require all comparison strains to have allele calls for each SNP.
					filterList.add(Filter.and(cmpStrains));
				} else {
					// Just require at least one comparison strain to have an allele call for each SNP.
					filterList.add(Filter.or(cmpStrains));
				}
			}
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

				Filter.Operator op = Filter.Operator.OP_EQUAL;
				if (symbol.contains("*")) {
					op = Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED;
				}

				// Taken from genFilters in the Marker Controller
				//Filter symbolFilter = new Filter(SearchConstants.MRK_SYMBOL,"\""+stripped.replace("\"","")+"\"^1000000000000",Filter.Operator.OP_HAS_WORD);
				Filter symbolFilter = new Filter(SearchConstants.MRK_SYMBOL, stripped, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED);

				if(query.getSearchGeneBy().equals(SearchConstants.MRK_NOMENCLATURE)) {
					List<Filter> filterList = new ArrayList<Filter>();
					filterList.add(symbolFilter);
					filterList.add(FilterUtil.generateNomenFilter(SearchConstants.MRK_NOMENCLATURE,stripped));
					sp.setFilter(Filter.or(filterList));
				} else if (query.getSearchGeneBy().equals(SearchConstants.MRK_SYMBOL)) {
					sp.setFilter(symbolFilter);
				} else {
					List<Filter> filterList = new ArrayList<Filter>();
					for (String symbolField : homologSymbolFields) {
						symbolFilter = new Filter(symbolField, stripped, op);
						filterList.add(symbolFilter);
					}
					sp.setFilter(Filter.or(filterList));
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
				if ((qf.getWithinRange() != null) && (Integer.parseInt(qf.getWithinRange().trim()) >= 0) ) {
					withinRange = Integer.parseInt(qf.getWithinRange().trim());
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
	public ModelAndView snpDetailByID(HttpServletRequest request, @PathVariable("snpID") String snpID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->snpDetailByID started");

		SearchResults<ConsensusSNP> searchResults = snpFinder.getSnpByID(snpID);
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

		mav.addObject("assemblyVersion", ContextLoader.getConfigBean().getProperty("SNP_ASSEMBLY_VERSION"));
		mav.addObject("dbSnpBuildNumber", this.getSnpBuildNumber());
		mav.addObject("buildNumber", ContextLoader.getConfigBean().getProperty("ASSEMBLY_VERSION"));

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
	public ModelAndView snpSummaryByMarker(HttpServletRequest request, @PathVariable("mrkID") String mrkID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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

	//----------------------------------//
	// heatmap of SNPs for summary page
	//----------------------------------//
	@RequestMapping("/heatmap")
	public ModelAndView snpSummaryHeatmap(HttpServletRequest request, HttpServletResponse response, @ModelAttribute SnpQueryForm query, BindingResult result) {
		logger.debug("->snpSummaryHeatmap started");

		// add headers to allow Ajax access
		AjaxUtils.prepareAjaxHeaders(response);

		// set up MAV
		ModelAndView mav = new ModelAndView("snp/summary_heatmap");
		
		// list of IDs for markers which matched the query
		List<String> matchedMarkerIds = new ArrayList<String>();

		// Defaults all strains on when coming from marker detail page
		if(query.getMarkerID() != null && query.getMarkerID().length() > 0) {
			query.setSelectedStrains(new ArrayList<String>(selectableStrains.keySet()));
		}

		// build the set of filters for the search
		Filter searchFilter = genFilters(query, matchedMarkerIds, result);
		if (result.hasErrors()) {
			mav.addObject("error", "Filter errors");
			return mav;
		}
		
		if ((query.getAlleleAgreementFilter() != null) && (query.getAlleleAgreementFilter().size() > 1)) {
			mav.addObject("error", "Allele Agreement filter error");
			return mav;
		}
		
		// We can only have a heatmap if we have a single genomic region.  That is, a contiguous region
		// on a single chromosome.  That can be:
		//	1. a single marker matched
		//	2. a chromosome & coordinate range
		//	3. a range specified by start & end markers
		
		String chromosome = null;
		Long startCoordinate = null;
		Long endCoordinate = null;
		String error = null;
		
		if (matchedMarkerIds.size() == 1) {
			CoordinateRange range = getCoordinates(matchedMarkerIds.get(0));
			if (range.error == null) {
				int pad = 0;
				if (query.getWithinRange() != null) {
					pad = Integer.parseInt(query.getWithinRange().trim());
				}
				chromosome = range.chromosome;
				startCoordinate = range.startCoordinate - pad;
				endCoordinate = range.endCoordinate + pad;
			} else {
				error = range.error;
			}

		} else if ((query.getSelectedChromosome() != null) && (query.getCoordinate() != null) && (query.getCoordinate().trim().length() > 0)) {
			String coords = query.orientCoordinates(query.getCoordinate());
			int pos = coords.indexOf("-");
			if (pos > 0) {
				try {
					chromosome = query.getSelectedChromosome();
					Double startDouble = Double.parseDouble(coords.substring(0, pos));
					Double endDouble = Double.parseDouble(coords.substring(pos + 1));
					if ("Mbp".equalsIgnoreCase(query.getCoordinateUnit())) {
						startDouble = startDouble * 1000000;
						endDouble = endDouble * 1000000;
					}
					
					startCoordinate = Math.round(startDouble);
					endCoordinate = Math.round(endDouble);
				} catch (Exception e) {
					error = "Data conversion errors";
				}
			} else {
				error = "Invalid coordinates";
			}
			
		} else if ((query.getStartMarker() != null) && (query.getEndMarker() != null)) {
			CoordinateRange range1 = getCoordinates(query.getStartMarker());
			CoordinateRange range2 = getCoordinates(query.getEndMarker());
			if (range1.error == null && range2.error == null) {
				if (range1.chromosome.equals(range2.chromosome)) {
					chromosome = range1.chromosome;
					startCoordinate = Math.min(range1.startCoordinate, range2.startCoordinate);
					endCoordinate = Math.max(range1.endCoordinate, range2.endCoordinate);
				} else {
					error = "Chromosomes differ: " + range1.chromosome + " vs " + range2.chromosome;
				}
			} else if (range1.error != null) {
				error = range1.error;
			} else if (range2.error != null) {
				error = range2.error;
			}
		} else {
			// failed to identify a single genomic region -- just let it fall through
		}
		
		// If we don't have a single region, then bail out.
		if ((error != null) || ((chromosome == null) && (startCoordinate == null) && (endCoordinate == null))) {
			if (error != null) {
				mav.addObject("error", "No single region (" + error + ")");
			} else {
				mav.addObject("error", "No single region");
			}
			return mav;
		}
		
		// adjust the range coordinates based on whether the user has zoomed in or not
		if ((query.getSliceStartCoord() != null) && (query.getSliceEndCoord() != null)) {
			startCoordinate = query.getSliceStartCoord();
			endCoordinate = query.getSliceEndCoord();
		}
		
		long rangeSize = endCoordinate - startCoordinate;
		long numberOfBins = Math.max(1, Math.min(rangeSize, 20));	// at least 1 bin, no more than 20
		
		mav.addObject("chromosome", chromosome);
		mav.addObject("startCoordinate", startCoordinate);
		mav.addObject("endCoordinate", endCoordinate);
		mav.addObject("numberOfBins", numberOfBins);

		Paginator page = new Paginator(0);
		
		// find counts for the slices of the query range
		
		SnpQueryForm sliceQF = query.clone();
		sliceQF.setSelectedChromosome(chromosome);
		sliceQF.setSliceStartCoord(startCoordinate);
		sliceQF.setSliceEndCoord(endCoordinate);
		sliceQF.setStartMarker(null);
		sliceQF.setEndMarker(null);
		
		// Our maximum count (for determining the shading of the heatmap cells) is based on either:
		//	1. the count passed in via the link from a heatmap click, or
		//	2. the highest count for the cells (if we're not coming from an existing heatmap)
		int maxCount = 0;
		if (query.getSliceMaxCount() != null) {
			maxCount = query.getSliceMaxCount();
		}

		long sliceStart = startCoordinate;
		double sliceSize = ((double) (endCoordinate - startCoordinate)) / (numberOfBins * (double) 1.0);

		List<Integer> sliceCounts = new ArrayList<Integer>();
		Map<Integer,String> sliceColors = new HashMap<Integer,String>();
		Map<Integer,String> sliceStartCoord = new HashMap<Integer,String>();
		Map<Integer,String> sliceEndCoord = new HashMap<Integer,String>();
		
		for (int i = 0; i < numberOfBins; i++) {
			long sliceEnd = Math.round(startCoordinate + ((i + 1) * sliceSize));
			
			sliceQF.setCoordinateUnit("bp");
			sliceQF.setCoordinate(sliceStart + "-" + sliceEnd);
			
			Filter sliceFilter = genFilters(sliceQF, matchedMarkerIds, result);
			if (result.hasErrors()) {
				for (ObjectError e : result.getAllErrors()) {
					logger.info("Error: " + e.getDefaultMessage());
				}
			}

			SearchParams params = new SearchParams();
			params.setPaginator(page);

			Filter sameDiffFilter = genSameDiffFilter(sliceQF);
			if(sliceFilter != null) {
				if(sameDiffFilter != null) {
					ArrayList<Filter> list = new ArrayList<Filter>();
					list.add(sliceFilter);
					list.add(sameDiffFilter);
					params.setFilter(Filter.and(list));
				} else {
					params.setFilter(sliceFilter);
				}
			}

			SearchResults<ConsensusSNPSummaryRow> searchResults = snpFinder.getMatchingSnpCount(params, matchedMarkerIds);
			int sliceCount = searchResults.getTotalCount();

			sliceCounts.add(sliceCount);
			maxCount = Math.max(maxCount, sliceCount);
			sliceStartCoord.put(i, "" + sliceStart);
			sliceEndCoord.put(i, "" + sliceEnd);

			sliceStart = sliceEnd + 1;
		}

		// Now that we have computed the maximum bin count, we can also assign cell colors.
		for (int i = 0; i < numberOfBins; i++) {
			int sliceCount = sliceCounts.get(i);
			sliceColors.put(sliceCount, FormatHelper.getSnpColorCode(sliceCount, maxCount, maxCount));
		}
		
		mav.addObject("sliceCounts", sliceCounts);
		mav.addObject("sliceColors", sliceColors);
		mav.addObject("sliceStartCoords", sliceStartCoord);
		mav.addObject("sliceEndCoords", sliceEndCoord);
		mav.addObject("sliceMaxCount", maxCount);
		mav.addObject("prettyStart", FormatHelper.getPrettyCoordinate(startCoordinate));
		mav.addObject("prettyEnd", FormatHelper.getPrettyCoordinate(endCoordinate));
		if (startCoordinate == 0) {
			mav.addObject("prettyRange", FormatHelper.getPrettyCoordinate(endCoordinate - startCoordinate));
			mav.addObject("binSize", FormatHelper.getPrettyCoordinate(Math.round((endCoordinate - startCoordinate) / numberOfBins) ));
		} else {
			mav.addObject("prettyRange", FormatHelper.getPrettyCoordinate(endCoordinate - startCoordinate + 1));
			mav.addObject("binSize", FormatHelper.getPrettyCoordinate(Math.round((endCoordinate - startCoordinate + 1) / numberOfBins) ));
		}
		
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
		if (result.hasErrors()) {
			List<String> errors = new ArrayList<String>();
			for (ObjectError er : result.getAllErrors()) {
				errors.add(er.getDefaultMessage());
			}
			mav.addObject("errors", errors);
		}

		if ((query.getAlleleAgreementFilter() != null) && (query.getAlleleAgreementFilter().size() > 1)) {
			List<String> errors = new ArrayList<String>();
			errors.add("Please choose only one value for the Allele Agreement filter.<br/>"
				+ "Note: The filter \"" + referenceAllelesMatch + "\" is automatically applied when you choose "
				+ "either of the \"Comparison Strains Differ or Agree\" filters.");
			mav.addObject("errors", errors);
			
			// ensure we get no results, so the message will get displayed
			searchFilter = null;
		}

		// if the user searched for SNPs in a genome region (not for
		// a marker or set of markers by nomenclature), then we want
		// to include a JBrowse link for the region.

		Properties externalUrls = ContextLoader.getExternalUrls();
		String jbrowse = externalUrls.getProperty("JBrowseHighlight");

		if (jbrowse != null && searchFilter != null) {
			String chrom = null;
			long startCoord = 0;
			long endCoord = 0;

			try {
				if ((query.getSelectedChromosome() != null) && (query.getCoordinate() != null) && (query.getCoordinateUnit() != null)) {
					Filter chromFilter = searchFilter.getFirstFilterFor(SearchConstants.CHROMOSOME);
					Filter coordFilter = searchFilter.getFirstFilterFor(SearchConstants.STARTCOORDINATE);
					if((chromFilter != null) && (coordFilter != null)) {
						chrom = chromFilter.getValues().get(0);
						startCoord = Long.parseLong(coordFilter.getValues().get(0));
						endCoord = Long.parseLong(coordFilter.getValues().get(1));
					}
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

		if(markerfeatureTypes != null && searchFilter != null) {
			String dlim = "";
			String queryString = "";
			for(String id: markerfeatureTypes) {
				queryString += dlim + "mcv=" + id;
				dlim = "&";
			}

			String qChromosome = query.getSelectedChromosome();
			String qCoordinate = query.getCoordinate();
			String qUnit = query.getCoordinateUnit();
			String startMarker = query.getStartMarker();
			String endMarker = query.getEndMarker();

			String locationParameters = null;
			
			if ((qChromosome != null) && (qChromosome.trim().length() > 0)
				&& (qCoordinate != null) && (qCoordinate.trim().length() > 0)
				&& (qUnit != null) && (qUnit.trim().length() > 0)) {
					locationParameters = "chromosome=" + qChromosome + "&coordinate=" + qCoordinate + "&coordUnit=" + qUnit;
			} else if ((startMarker != null) && (startMarker.trim().length() > 0)
				&& (endMarker != null) && (endMarker.trim().length() > 0)) {
					locationParameters = "startMarker=" + startMarker + "&endMarker=" + endMarker;
			}

			if (locationParameters != null) {
				String markerSummaryLink = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "marker/summary?chromosome=" + query.getSelectedChromosome() + "&" + locationParameters + "&" + queryString;
				mav.addObject("markerSummaryLink", markerSummaryLink); 
			}
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
			if(query.getReferenceStrains() != null && query.getReferenceStrains().size() > 0) {
				List<String> referenceStrainsReversed = query.getReferenceStrains().subList(0, query.getReferenceStrains().size());
				Collections.reverse(referenceStrainsReversed);
				for (String referenceStrain : referenceStrainsReversed) {
					selectedStrains.remove(referenceStrain);
					selectedStrains.add(0, referenceStrain);
				}
			}

			displayedStrains = new ArrayList<String>();
			for(String strain: selectedStrains) {
				if(resultStrains.contains(strain)) {
					displayedStrains.add(strain);
				}
			}
		} else {
			displayedStrains = query.getSelectedStrains();
			if(query.getReferenceStrains() != null && query.getReferenceStrains().size() > 0) {
				for (String referenceStrain : query.getReferenceStrains()) {
					displayedStrains.add(0, referenceStrain);
				}
			}
		}

		Map<String,String> strainHeaders = new HashMap<String,String>();
		if ((displayedStrains != null) && (displayedStrains.size() > 0)){
			for (String s : displayedStrains) {
				strainHeaders.put(s, getStrainCode(s));
			}
		}

		mav.addObject("count", searchResults.getTotalCount());
		mav.addObject("referenceStrains", query.getReferenceStrains());
		mav.addObject("strains", displayedStrains);
		mav.addObject("strainHeaders", strainHeaders);

		return mav;
	}

	private Filter genSameDiffFilter(SnpQueryForm query) {
		// if not a single allele agreement filter or no reference strain, just bail out
		if ((query.getAlleleAgreementFilter() == null) 
				|| (query.getAlleleAgreementFilter().size() != 1)
				|| (query.getReferenceStrains() == null)
				|| (query.getReferenceStrains().size() == 0)) {
			return null;
		}

		// At this point we have four options to consider:
		// 1. the default -- reference strains don't need to have the same allele.  #1 is handled by the 'if' above.
		// 2. referenceAllelesMatch -- "All Reference Strains Have Same Allele"
		// 3. comparisonAllelesAgree -- referenceAllelesMatch AND "All Comparison Strains Agree with Reference Allele"
		// 4. comparisonAllelesDiffer -- referenceAllelesMatch AND "All Comparison Strains Differ from Reference Allele"

		List<Filter> filterList = new ArrayList<Filter>();

		// needed for #2, #3, and #4
		filterList.add(new Filter(SearchConstants.SAME_STRAINS, query.getReferenceStrains(), Operator.OP_IN));
		for (String referenceStrain : query.getReferenceStrains()) {
			filterList.add(new Filter(SearchConstants.DIFF_STRAINS, referenceStrain, Operator.OP_NOT_IN));
		}

		// #3
		if (comparisonAllelesAgree.equalsIgnoreCase(query.getAlleleAgreementFilter().get(0))) {
			filterList.add(new Filter(SearchConstants.SAME_STRAINS, query.getSelectedStrains(), Operator.OP_IN));
			filterList.add(new Filter(SearchConstants.DIFF_STRAINS, query.getSelectedStrains(), Operator.OP_NOT_IN));

		// #4
		} else if (comparisonAllelesDiffer.equalsIgnoreCase(query.getAlleleAgreementFilter().get(0))) {
			filterList.add(new Filter(SearchConstants.SAME_STRAINS, query.getSelectedStrains(), Operator.OP_NOT_IN));
			filterList.add(new Filter(SearchConstants.DIFF_STRAINS, query.getSelectedStrains(), Operator.OP_IN));
		}
		return Filter.and(filterList);
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
		Filter searchFilter = genFilters(query, matchedMarkerIds, result);
		Filter sameDiffFilter = genSameDiffFilter(query);
		
		// perform query, and pull out the requested objects
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

		List<String> facets = snpFinder.getFunctionClassFacets(params);

		// must remove "Contig-Reference", since display of this is
		// suppressed on the summary page (see summary object)
		facets.remove("Contig-Reference");

		SearchResults<String> results = new SearchResults<String>();
		results.setResultFacets(facets);

		return parseFacetResponse(results, true, null);
	}

	// This method maps requests for the allele agreement facet list for
	// SNPs matching the query.
	@RequestMapping("/facet/alleleAgreement")
	public @ResponseBody Map<String, List<String>> facetAlleleAgreement(@ModelAttribute SnpQueryForm query, BindingResult result, HttpServletResponse response) {
		// perform query and return results as json
		logger.debug("finding allele agreement filter values");
		AjaxUtils.prepareAjaxHeaders(response);
		
		String errorMessage = null;
		List<String> facets = new ArrayList<String>();
		
		// If we already have one selected, just return it.
		if ((query.getAlleleAgreementFilter() != null) && (query.getAlleleAgreementFilter().size() > 0)) {
			for (String facet : query.getAlleleAgreementFilter()) {
				facets.add(facet);
			}

		} else {
			// Otherwise, we have up to three choices for the user...
			
			// Filter is only valid if there is at least one reference strain chosen.
			if ((query.getReferenceStrains() != null) && (query.getReferenceStrains().size() > 0)) {

				if (query.getReferenceStrains().size() > 1) {
					// only show this option if >1 reference strain has been selected
					facets.add(referenceAllelesMatch);
				}
			
				facets.add(comparisonAllelesDiffer);
				facets.add(comparisonAllelesAgree);
			} else {
				errorMessage = "This filter is only valid when at least one Reference Strain has been selected.";
			}
		}

		SearchResults<String> results = new SearchResults<String>();
		results.setResultFacets(facets);
		return parseFacetResponse(results, false, errorMessage);
	}

	// SNPs out to tab-delimited file (from summary page)
	@RequestMapping("/report*")
	public ModelAndView snpsToTabDelimitedFile (HttpServletRequest request,
			@ModelAttribute SnpQueryForm query, BindingResult result) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
			mav.addObject("withinRange", query.getWithinRange().trim());
		}
		return mav; 
	}

	// This is a convenience method to parse the facet response from the 
	// SearchResults object, inspect it for error conditions, and return a 
	// map that the ui is expecting.
	private Map<String, List<String>> parseFacetResponse(SearchResults<String> facetResults, boolean sortResults, String errorMessage) {

		Map<String, List<String>> m = new HashMap<String, List<String>>();
		List<String> l = new ArrayList<String>();

		if ((errorMessage != null) && (errorMessage.length() > 0)) {
			l.add(errorMessage);
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() >= facetLimit){
			logger.debug("too many facet results");
			l.add("Too many results to display. Modify your search or try another filter first.");
			m.put("error", l);
		} else if (facetResults.getResultFacets().size() == 0) {
			logger.debug("no facet results");
			l.add("No values in results to filter.");
			m.put("error", l);
		} else {
			List<String> facets = facetResults.getResultFacets();
			if (sortResults) {
				Collections.sort(facets, new SmartAlphaComparator<String>());
			}
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
		 * 	we need to update to include the direction
		 * 3. strip out any trailing " of"
		 */
		boolean outOfSync = ("true".equalsIgnoreCase(ContextLoader.getConfigBean().getProperty("snpsOutOfSync")));
		for (ConsensusMarkerSNP m : markers) {
			String direction = m.getDistanceDirection();
			int from = m.getDistanceFrom();
			String fc = m.getFunctionClass();

			if (!outOfSync && "Locus-Region".equals(fc) && (direction != null)) {
				m.setFunctionClass(fc + " (" + direction + ")");
			} else if (!outOfSync && "within distance of".equals(fc) && (direction != null) && (from > 0)) {
				m.setFunctionClass(from + " bp " + direction);
			} else if (fc.endsWith(" of")) {
				m.setFunctionClass(fc.replace(" of", ""));
			}
		}
	}

	// trim out coordinates-based marker associations that are beyond 2k
	private List<ConsensusMarkerSNP> filterByDistance (List<ConsensusMarkerSNP> markers) {
		boolean outOfSync = ("true".equalsIgnoreCase(ContextLoader.getConfigBean().getProperty("snpsOutOfSync")));
		List<ConsensusMarkerSNP> filtered = new ArrayList<ConsensusMarkerSNP>();
		for (ConsensusMarkerSNP m : markers) {
			if (!"within distance of".equals(m.getFunctionClass()) || (!outOfSync && (m.getDistanceFrom() <= 2000)) ) {
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

	// use the strainController to search for the strains specified by 'form' and return their names in a list
	private List<String> makeStrainList(StrainQueryForm form) {
		SearchResults<SimpleStrain> sr = strainController.getSummaryResults(form, new Paginator(1000));
		List<String> names = new ArrayList<String>();
		for (SimpleStrain strain : sr.getResultObjects()) {
			names.add(strain.getName());
		}
		return names;
	}
	
	// ------------- //
	// inner classes
	// ------------- //
	class CoordinateRange {
		public String chromosome = null;
		public Long startCoordinate = null;
		public Long endCoordinate = null;
		public String strand = null; 
		public String error = null;
		
		public CoordinateRange(String error) {
			this.error = error;
		}
		
		public CoordinateRange(MarkerLocation ml) {
			this.chromosome = ml.getChromosome();
			this.startCoordinate = Math.round(ml.getStartCoordinate());
			this.endCoordinate = Math.round(ml.getEndCoordinate());
			this.strand = ml.getStrand();
			this.orderCoordinates();
		}
		
		public CoordinateRange(ConsensusCoordinateSNP c) {
			this.chromosome = c.getChromosome();
			this.startCoordinate = (long) c.getStartCoordinate();
			this.endCoordinate = (long) c.getStartCoordinate();
			this.strand = c.getStrand();
			this.orderCoordinates();
		}
		
		public CoordinateRange() {}

		// Ensure that startCoordinate is lower than endCoordinate.
		private void orderCoordinates() {
			if ((this.startCoordinate != null) && (this.endCoordinate != null)) {
				if (this.startCoordinate > this.endCoordinate) {
					Long t = this.startCoordinate;
					this.startCoordinate = this.endCoordinate;
					this.endCoordinate = t;
				}
			}
		}
		
		// Get a new coordinate range that spans this one and 'cr'.  Assumes the chromosomes
		// and strands match.  Assumes all coordinates are non-null.
		public CoordinateRange getSpan(CoordinateRange cr) {
			CoordinateRange out = new CoordinateRange();
			out.chromosome = this.chromosome;
			out.strand = this.strand;
			out.error = this.error;
			out.startCoordinate = Math.min(this.startCoordinate, cr.startCoordinate);
			out.endCoordinate = Math.max(this.endCoordinate, cr.endCoordinate);
			return out;
		}
		
		// Get the coordinates, separated by a dash.  Assumes coordinates are non-null;
		public String getCoords() {
			return this.startCoordinate + "-" + this.endCoordinate;
		}
		
		public String toString() {
			if (this.error != null) {
				return "Error: " + this.error;
			}
			return "Chr" + this.chromosome + ":" + this.getCoords() + " (" + this.strand + ")";
			
		}
	}
}
