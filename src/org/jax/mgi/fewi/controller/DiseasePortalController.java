package org.jax.mgi.fewi.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.hdp.HdpGenoCluster;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.antlr.BooleanSearch.BooleanSearch;
import org.jax.mgi.fewi.finder.DiseasePortalBatchFinder;
import org.jax.mgi.fewi.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.forms.DiseasePortalQueryForm;
import org.jax.mgi.fewi.matrix.HdpGridMapper;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.JoinClause;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.PrettyFilterPrinter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.FacetConstants;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridCluster.SolrDpGridClusterMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrHdpGridData;
import org.jax.mgi.fewi.searchUtil.entities.SolrString;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.summary.HdpDiseaseSummaryRow;
import org.jax.mgi.fewi.summary.HdpGenoByHeaderPopupRow;
import org.jax.mgi.fewi.summary.HdpGridClusterSummaryRow;
import org.jax.mgi.fewi.summary.HdpMarkerByHeaderPopupRow;
import org.jax.mgi.fewi.summary.HdpMarkerSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.ImageUtils;
import org.jax.mgi.fewi.util.QueryParser;
import org.jax.mgi.fewi.util.file.FileProcessor;
import org.jax.mgi.fewi.util.file.FileProcessorOutput;
import org.jax.mgi.fewi.util.file.VcfProcessorOutput;
import org.jax.mgi.shr.fe.indexconstants.DiseasePortalFields;
import org.jax.mgi.shr.fe.query.SolrLocationTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindingResult;

/*
 * This controller maps all /diseasePortal/ uri's
 */
@Controller
@RequestMapping(value="/diseasePortal")
public class DiseasePortalController
{

    //--------------------//
    // instance variables
    //--------------------//

	// logger for the class
	private final Logger logger = LoggerFactory.getLogger(DiseasePortalController.class);

	// get the finders used by various methods
	@Autowired
	private DiseasePortalFinder hdpFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit;

    //--------------------//
    // static variables
    //--------------------//

    // values for defining how we sort facet results
    private static String ALPHA = "alphabetic";
    private static String RAW = "raw";

    //--------------------------//
    // Disease Portal Query Form
    //--------------------------//

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model,
			HttpSession session) {
		model.addAttribute(new DiseasePortalQueryForm());
		model.addAttribute("locationsFileName","");

		logger.debug("/diseasePortal --> GET");
		return "disease_portal_query";
	}

	public String truncateText(String in) {
		return truncateText(in, 30);
	}
	
	public String truncateText(String in, int bound) {
		if(in.length() > bound) {
			return in.substring(0, (bound - 3)) + "...";
		} else {
			return in;
		}
	}

    //----------------------------//
    // Disease Portal Summary Page
    //----------------------------//

    @RequestMapping("/summary")
    public String genericSummary(
		    @ModelAttribute DiseasePortalQueryForm query,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

    	logger.debug("generating generic DiseasePortal summary");

    	String queryString = request.getQueryString();
    	// if the queryString is empty, this might be a POST request
    	if(!notEmpty(queryString)) queryString = FormatHelper.queryStringFromPost(request);
//		else
//		{
//			// if this is a GET, resubmit as a POST
//			request.setAttribute("query", query);
//			return "forward:/mgi/diseasePortal/summary";
//		}

		logger.debug("query string: " + queryString);
		logger.debug("query form: " + query);


		//ModelAndView mav = new ModelAndView("disease_portal_query");
		if(notEmpty(queryString) && !queryString.contains("tab=")) queryString += "&tab=gridtab";
		model.addAttribute("querystring", queryString);

		boolean useGeneFile = DiseasePortalController.usingGenefileQuery(query,session);
		if(useGeneFile) model.addAttribute("geneFileName",session.getAttribute(DiseasePortalQueryForm.GENE_FILE_VAR_NAME));

		boolean useLocationsFile = DiseasePortalController.usingLocationsQuery(query,session);
		if(useLocationsFile) model.addAttribute("locationsFileName",session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_NAME));

		return "disease_portal_query";

    }

    @RequestMapping("/isLocationsFileCached")
    public @ResponseBody String checkIfLocationsFileIsCached(HttpServletRequest request,
    		HttpSession session,
    		@ModelAttribute DiseasePortalQueryForm query)
    {
    	boolean useLocationsFile = DiseasePortalController.usingLocationsQuery(query,session);
    	if(notEmpty(query.getLocationsFileName()) && !useLocationsFile)
    	{
    		return "(Warning: File must be re-uploaded to see results)";
    	}
    	return "";
    }

    @RequestMapping("/isGeneFileCached")
    public @ResponseBody String checkIfGeneFileIsCached(HttpServletRequest request,
    		HttpSession session,
    		@ModelAttribute DiseasePortalQueryForm query)
    {
    	boolean useGeneFile = DiseasePortalController.usingGenefileQuery(query,session);
    	if(notEmpty(query.getGeneFileName()) && !useGeneFile)
    	{
    		return "(Warning: File must be re-uploaded to see results)";
    	}
    	return "";
    }

    @RequestMapping("/uploadFile")
    public @ResponseBody ModelAndView uploadFile(MultipartHttpServletRequest request,
    		HttpSession session,
    		@RequestParam("file") MultipartFile file,
    		@RequestParam("field") String field,
    		@RequestParam("type") String type,
    		@RequestParam(value="enableVcfFilter",required=false) String enableVcfFilter,
    		@RequestParam(value="associatedFormInput",required=false) String associatedFormInput)
    {

        logger.debug("-> diseasePortal -> uploadFile -> POST started");

        ModelAndView mav = new ModelAndView("disease_portal_upload_results");

		logger.debug("file field: " + field);
		// default type is VCF
		if(!notEmpty(type))
		{
			type = DiseasePortalQueryForm.VCF_FILE_TYPE;
		}
		logger.debug("file field: " + field);
		logger.debug("file type: " + type);
		String filename = file.getOriginalFilename();
		logger.debug("filename: " + filename);
		mav.addObject("filename",filename);
		
		if(!notEmpty(associatedFormInput)) associatedFormInput = "File";

		if(DiseasePortalQueryForm.ACCEPTABLE_FILE_VARS.contains(field))
		{
			// clear the session variable for this field
			session.setAttribute(field,null);

			// set the filename
			if(DiseasePortalQueryForm.GENE_FILE_VAR.equals(field))
			{
				session.setAttribute(DiseasePortalQueryForm.GENE_FILE_VAR_NAME,filename);
			}
			else if(DiseasePortalQueryForm.LOCATIONS_FILE_VAR.equals(field))
			{
				session.setAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_NAME,filename);
			}
			if(notEmpty(filename))
			{
		        try {
					String dataString="";
					if(DiseasePortalQueryForm.VCF_FILE_TYPE.equalsIgnoreCase(type))
		        	{
		        		logger.debug("processing vcf file ["+file.getOriginalFilename()+"] for coordinates");

		        		boolean enableFilters = notEmpty(enableVcfFilter);
		        		boolean kickIds = enableFilters;
		        		boolean kickBadFilters = enableFilters;
		        		logger.debug("kickIds="+kickIds+", kickBadFilters="+kickBadFilters);
						VcfProcessorOutput vpo = FileProcessor.processVCFCoordinates(file,kickIds,kickBadFilters);
						dataString = vpo.getCoordinates();
		        		logger.debug("finished processing vcf file ["+file.getOriginalFilename()+"] for coordinates");
		        		mav.addObject("vcfOutput",vpo);
		        		mav.addObject("enableVcfFilter",enableFilters);

		        		if(!notEmpty(dataString))
		        		{
		        			logger.debug("no coordinates found in VCF file");
		        			mav.addObject("error","No coordinates found in VCF file after processing.");
		    				return mav;
		        		}

		        		// translate the coordinates into matching marker keys
		        		if(notEmpty(dataString) && DiseasePortalQueryForm.LOCATIONS_FILE_VAR.equals(field))
		        		{
			        		logger.debug("converting file locations to mouse marker keys");
			        		List<String> locationQueryTokens = QueryParser.tokeniseOnWhitespaceAndComma(dataString);
			        		List<String> mouseMarkerKeysFromLocationsFile = convertLocationsToMarkerKeys(locationQueryTokens,DiseasePortalQueryForm.MOUSE);
			        		session.setAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_MOUSE_KEYS,mouseMarkerKeysFromLocationsFile);
			        		logger.debug("finished converting file locations to mouse marker keys");
			        		logger.debug("found "+mouseMarkerKeysFromLocationsFile.size()+" mouse marker keys");
			        		mav.addObject("mouseMatch",mouseMarkerKeysFromLocationsFile.size());

			        		logger.debug("converting file locations to human marker keys");
			        		List<String> humanMarkerKeysFromLocationsFile = convertLocationsToMarkerKeys(locationQueryTokens,DiseasePortalQueryForm.HUMAN);
			        		session.setAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_HUMAN_KEYS,humanMarkerKeysFromLocationsFile);
			        		logger.debug("finished converting file locations to human marker keys");
			        		logger.debug("found "+humanMarkerKeysFromLocationsFile.size()+" human marker keys");
			        		mav.addObject("humanMatch",humanMarkerKeysFromLocationsFile.size());

			        		if((mouseMarkerKeysFromLocationsFile==null || mouseMarkerKeysFromLocationsFile.size()==0)
			        				&& (humanMarkerKeysFromLocationsFile==null || humanMarkerKeysFromLocationsFile.size()==0))
			        		{
			        			logger.debug("No matching genes found in VCF file after processing");
			        			mav.addObject("error","None of the provided coordinates matched a gene region in either human or mouse.");
			    				return mav;
			        		}
			        		dataString = "true";
		        		}
		        	}
					else if(DiseasePortalQueryForm.SINGLE_COL_TYPE.equalsIgnoreCase(type))
					{
						logger.debug("processing single column file ["+file.getOriginalFilename()+"]");

						FileProcessorOutput po = FileProcessor.processSingleCol(file);
						dataString = po.getValueString();
		        		logger.debug("finished processing single column file ["+file.getOriginalFilename()+"]");
		        		mav.addObject("singleColOutput",po);

		        		logger.debug("valueString = "+dataString);
		        		if(!notEmpty(dataString))
		        		{
		        			logger.debug("no data found in file");
		        			mav.addObject("error","No data found in uploaded file.");
		    				return mav;
		        		}
					}

		        	// save the data in the session;
		        	session.setAttribute(field,dataString);
				} catch (IOException e) {
					logger.error("error reading HMDC upload file",e);
					mav.addObject("error","File reading IOException");
					return mav;
				} catch (Exception e) {
					logger.error("error processing HMDC upload file",e);
					mav.addObject("error","Server threw Exception, "+e.getMessage());
					return mav;
				}
			}
		}

        logger.debug("-> diseasePortal -> uploadFile -> POST finished");


		if(!notEmpty(filename))
		{
			logger.debug("resetting file upload");
	        mav.addObject("success","file upload reset");
			return mav;
		}

        mav.addObject("success",associatedFormInput+" successfully processed.");
		return mav;
    }


    //-----------------------//
    // Disease Portal Grid
    //-----------------------//

    @RequestMapping(value="/grid")
    public ModelAndView diseaseGrid(
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session,
      @ModelAttribute DiseasePortalQueryForm query,
      @ModelAttribute Paginator page) throws Exception
    {
    	logger.debug("->diseaseGrid started ("
		+ page.getResults() + " results per page)");

      	// add headers to allow AJAX access
      	AjaxUtils.prepareAjaxHeaders(response);

      	// setup view object
      	ModelAndView mav = new ModelAndView("disease_portal_grid");

      	// search for grid cluster objects
      	SearchResults<SolrHdpGridCluster> searchResults = getGridClusters(request, query, page,session);
      	List<SolrHdpGridCluster> gridClusters = searchResults.getResultObjects();

		SearchResults<SolrString> columns = getHighlightedColumnHeaders(query, session);
		HashMap<String, String> highLightedColumns = new HashMap<String, String>();
		
		for(SolrString ss : columns.getResultObjects()) {
			highLightedColumns.put(ss.toString(), ss.toString());
      	}
      	
      	// search for diseases in result set - make column headers and ID list
      	SearchResults<SolrString> diseaseNamesResults = getGridDiseaseColumns(request, query,session);
      	List<String> diseaseNames = new ArrayList<String>();
      	for(SolrString ss : diseaseNamesResults.getResultObjects()) {
      		diseaseNames.add(ss.toString());
      	}
      	boolean moreDiseasesNotShown = diseaseNamesResults.getTotalCount() > diseaseNames.size();

		List<String> diseaseColumnsToDisplay = new ArrayList<String>();
		List<String> diseaseIds = new ArrayList<String>();
		
		for(String disease : diseaseNames) {
			if(highLightedColumns.containsKey(disease)) {
				diseaseColumnsToDisplay.add("<span class=\"highlight\">" + truncateText(disease) + "</span>");
			} else {
				diseaseColumnsToDisplay.add(truncateText(disease));
			}
		}

      	// search for mp headers in result set & make column headers
      	List<String> mpHeaders = getGridMpHeaderColumns(request,query,session);
      	List<String> mpHeaderColumnsToDisplay = new ArrayList<String>();

      	for(String mpHeader : mpHeaders) {
			if(highLightedColumns.containsKey(mpHeader)) {
				mpHeaderColumnsToDisplay.add("<span class=\"highlight\">" + truncateText(mpHeader) + "</span>");
			} else {
				mpHeaderColumnsToDisplay.add(truncateText(mpHeader));
			}
		}

		logger.info("diseasePortal/grid -> querying solr for grid data");
      	// Search for the genotype clusters used to generate the result set
      	// and save as map for later

	// now that we've chosen our gridClusters (the rows of the grid), we
	// want all data for those rows -- so remove the FeatureType filter.
	
	DiseasePortalQueryForm queryNoFilters = query;
	queryNoFilters.setFeatureTypeFilter(null);

      	List<SolrHdpGridData> annotationsInResults = getAnnotationsInResults(queryNoFilters,searchResults.getResultKeys(),session);
        Map<Integer,List<SolrHdpGridData>> gridClusterToMPResults = new HashMap<Integer,List<SolrHdpGridData>>();
        Map<Integer,List<SolrHdpGridData>> gridClusterToDiseaseResults = new HashMap<Integer,List<SolrHdpGridData>>();


        logger.info("diseasePortal/grid -> mapping Solr data to gridClusters");
        for(SolrHdpGridData dpa : annotationsInResults)
        {
        	// configure which map to use based on vocab name
        	Map<Integer,List<SolrHdpGridData>> dataMap = gridClusterToMPResults;
        	if("OMIM".equals(dpa.getVocabName())) dataMap = gridClusterToDiseaseResults;

        	// map each genocluster/header combo to its corresponding gridcluster key
            if (!dataMap.containsKey(dpa.getGridClusterKey()))
            {
            	dataMap.put(dpa.getGridClusterKey(),new ArrayList<SolrHdpGridData>());
            }
            dataMap.get(dpa.getGridClusterKey()).add(dpa);
        }

		logger.info("diseasePortal/grid -> creating grid row objects");
		// create grid row objects
		List<HdpGridClusterSummaryRow> summaryRows = new ArrayList<HdpGridClusterSummaryRow>();
		for(SolrHdpGridCluster gc : gridClusters)
		{
            // ensure the cross-reference genoInResults list exists for this row
			boolean foundGridCluster=false;

			List<SolrHdpGridData> mpResults = new ArrayList<SolrHdpGridData>();
			List<SolrHdpGridData> diseaseResults = new ArrayList<SolrHdpGridData>();

            if (gridClusterToMPResults.containsKey(gc.getGridClusterKey()))
            {
            	foundGridCluster = true;
            	mpResults = gridClusterToMPResults.get(gc.getGridClusterKey());
            }
            if (gridClusterToDiseaseResults.containsKey(gc.getGridClusterKey()))
            {
            	foundGridCluster = true;
            	diseaseResults = gridClusterToDiseaseResults.get(gc.getGridClusterKey());
            }
            if(!foundGridCluster)
            {
              logger.debug("->ERROR:: grid cluster key "+gc.getGridClusterKey()+" not found in solr set");
            }
            else
            {
            	// map the grid cluster rows and the MP/Disease data to their respective columns via the GridMapper class
        		//logger.info("diseasePortal/grid -> mapping mp column objects");
            	HdpGridMapper mpHeaderMapper = new HdpGridMapper(mpHeaders, mpResults);

        		//logger.info("diseasePortal/grid -> mapping disease column objects");
            	HdpGridMapper diseaseMapper = new HdpGridMapper(diseaseNames, diseaseResults);

            	// add this row
            	HdpGridClusterSummaryRow summaryRow = new HdpGridClusterSummaryRow(gc,diseaseMapper,mpHeaderMapper);
            	summaryRows.add(summaryRow);
            }
		}

        // derive the query string to pass along
        String queryString = FormatHelper.queryStringFromPost(request);

		mav.addObject("queryString", queryString);
		mav.addObject("gridClusters", summaryRows);
		mav.addObject("diseaseColumns", diseaseColumnsToDisplay);
		mav.addObject("diseaseIds", diseaseIds); // for testing
		mav.addObject("diseaseNames",diseaseNames);
		mav.addObject("moreDiseases",moreDiseasesNotShown);
		mav.addObject("mpHeaderColumns", mpHeaderColumnsToDisplay);
		mav.addObject("mpHeaders", mpHeaders);

		logger.info("diseasePortal/grid -> off to MAV");
   		return mav;
    }


    //--------------------------//
    // Grid - Phenotype System Cell Popup
    //--------------------------//

    @RequestMapping(value="/gridSystemCell")
    public ModelAndView gridSystemCell(
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session,
      @ModelAttribute DiseasePortalQueryForm query)
    {
    	logger.debug("->gridSystemCell started");

    	String gridClusterString = getGridClusterTitleForPopup(query.getGridClusterKey());

    	// get the geno cluster rows
        SearchResults<HdpGenoCluster> searchResults = getGenoClusters(request, query,session);
        List<HdpGenoCluster> genoClusters = searchResults.getResultObjects();
    	//logger.debug("->gridSystemCell; number of genoClusters=" + genoClusters.size());

        // get unique list of markers in IMSR
        Marker genoClusterMarker;
        List<Marker> markersInImsr = new ArrayList<Marker>();
        for (HdpGenoCluster genoCluster : genoClusters) {
        	genoClusterMarker = genoCluster.getMarker();
        	if ((genoCluster.getMarker().getCountForImsr() > 0) && (!markersInImsr.contains(genoCluster.getMarker()))) {
        		markersInImsr.add(genoClusterMarker);
			}
        }

        // get the mp term columns
    	List<SolrVocTerm> mpTerms = getGridMpTermColumns(request,query,session);
      	List<String> mpTermColumnsToDisplay = new ArrayList<String>();
		List<String> termColIds = new ArrayList<String>();
		List<String> termColNames = new ArrayList<String>(); // needed to automated tests

		SearchResults<SolrString> columns = getHighlightedColumns(request, query, session);
		HashMap<String, String> highLightedColumns = new HashMap<String, String>();
		for(SolrString ss : columns.getResultObjects()) {
			highLightedColumns.put(ss.toString(), ss.toString());
	  	}
		
		for(SolrVocTerm mpTerm : mpTerms)
		{
			if(highLightedColumns.containsKey(mpTerm.getTerm())) {
				mpTermColumnsToDisplay.add("<span class=\"highlight\">" + truncateText(mpTerm.getTerm(), 45) + "</span>");
			} else {
				mpTermColumnsToDisplay.add(truncateText(mpTerm.getTerm(), 45));
			}
			termColIds.add(mpTerm.getPrimaryId());
			termColNames.add(mpTerm.getTerm());
		}
		
		// cross reference them
		List<HdpGenoByHeaderPopupRow> popupRows = new ArrayList<HdpGenoByHeaderPopupRow>();
		// map the columns with the data
		for(HdpGenoCluster gc : genoClusters)
		{
			// map the diseases with column info
			HdpGridMapper mpMapper = new HdpGridMapper(termColIds, gc.getMpTerms());
			HdpGenoByHeaderPopupRow popupRow = new HdpGenoByHeaderPopupRow(gc, mpMapper);
			popupRows.add(popupRow);
		}

      	// setup view object
      	ModelAndView mav = new ModelAndView("disease_portal_grid_system_popup");
		mav.addObject("popupRows", popupRows);
		mav.addObject("genoClusters", genoClusters);
		mav.addObject("termColumns", mpTermColumnsToDisplay);
		mav.addObject("termNames", mpTerms);
		mav.addObject("terms", termColNames);
		mav.addObject("termHeader", query.getTermHeader());
		mav.addObject("markers", markersInImsr);
		mav.addObject("gridClusterString",gridClusterString);

   		return mav;
    }


    //--------------------------//
    // Grid - Disease Cell Popup
    //--------------------------//

    @RequestMapping(value="/gridDiseaseCell")
    public ModelAndView gridDiseaseCell(
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session,
      @ModelAttribute DiseasePortalQueryForm query)
    {
    	logger.debug("->gridDiseaseCell started");

    	String gridClusterString = getGridClusterTitle(query.getGridClusterKey(),true);

    	// gather the geno cluster rows
        SearchResults<HdpGenoCluster> searchResults = getGenoClusters(request, query,session);
        List<HdpGenoCluster> genoClusters = searchResults.getResultObjects();

        // get unique list of markers in IMSR
        Marker genoClusterMarker;
        List<Marker> markersInImsr = new ArrayList<Marker>();
        for (HdpGenoCluster genoCluster : genoClusters) {
        	genoClusterMarker = genoCluster.getMarker();
        	if ((genoCluster.getMarker().getCountForImsr() > 0) && (!markersInImsr.contains(genoCluster.getMarker()))) {
        		markersInImsr.add(genoClusterMarker);
			}
        }
    	//logger.debug("->markersInImsr=" + markersInImsr.size());

        // gather the disease columns
    	List<SolrVocTerm> diseaseTerms = getGridDiseaseTermColumns(request,query,session);
      	List<String> diseaseTermColumnsToDisplay = new ArrayList<String>();
		List<String> termColIds = new ArrayList<String>();
		List<String> termColNames = new ArrayList<String>(); // needed to automated tests

		SearchResults<SolrString> columns = getHighlightedColumns(request, query, session);
		HashMap<String, String> highLightedColumns = new HashMap<String, String>();
		for(SolrString ss : columns.getResultObjects()) {
			highLightedColumns.put(ss.toString(), ss.toString());
	  	}
		
		for(SolrVocTerm diseaseTerm : diseaseTerms)
		{
			if(highLightedColumns.containsKey(diseaseTerm.getTerm())) {
				diseaseTermColumnsToDisplay.add("<span class=\"highlight\">" + truncateText(diseaseTerm.getTerm(), 45) + "</span>");
			} else {
				diseaseTermColumnsToDisplay.add(truncateText(diseaseTerm.getTerm(), 45));
			}
			termColIds.add(diseaseTerm.getPrimaryId());
			termColNames.add(diseaseTerm.getTerm());
		}

		// cross reference them
		List<HdpGenoByHeaderPopupRow> popupRows = new ArrayList<HdpGenoByHeaderPopupRow>();
		// map the columns with the data
		for(HdpGenoCluster gc : genoClusters)
		{
			//logger.debug("genoClusterKey = "+gc.getGenoClusterKey());
			// map the diseases with column info
			HdpGridMapper diseaseMapper = new HdpGridMapper(termColIds, gc.getDiseases());
			HdpGenoByHeaderPopupRow popupRow = new HdpGenoByHeaderPopupRow(gc, diseaseMapper);
			popupRows.add(popupRow);
		}

		// get the human marker data as well
    	List<SolrDiseasePortalMarker> humanMarkers = getGridHumanMarkers(request,query,session);
    	SearchParams sp = new SearchParams();
    	sp.setFilter(parseQueryForm(query,session));
    	sp.setPageSize(10000);
    	SearchResults<SolrHdpGridData> sr = hdpFinder.searchAnnotationsInPopupResults(sp);
    	// map the results by marker key
    	Map<Integer,List<SolrHdpGridData>> humanResults = new HashMap<Integer,List<SolrHdpGridData>>();
    	for(SolrHdpGridData gir : sr.getResultObjects())
    	{
    		Integer mKey = gir.getMarkerKey();
    		if(mKey!=null)
    		{
        		logger.info("found human data for marker key "+mKey);
    			if(!humanResults.containsKey(mKey))
    			{
    				humanResults.put(mKey,new ArrayList<SolrHdpGridData>());
    			}
    			humanResults.get(mKey).add(gir);
    		}
    	}

    	// cross reference the human marker data
    	List<HdpMarkerByHeaderPopupRow> humanPopupRows = new ArrayList<HdpMarkerByHeaderPopupRow>();
    	for(SolrDiseasePortalMarker humanMarker : humanMarkers)
    	{
    		Integer mKey = Integer.parseInt(humanMarker.getMarkerKey());
    		List<SolrHdpGridData> data = new ArrayList<SolrHdpGridData>();
    		if(humanResults.containsKey(mKey))
    		{
    			data = humanResults.get(mKey);
    		}
    		else
    		{
    			logger.info("error-> missing data for human marker key "+mKey);
    		}
    		HdpGridMapper diseaseMapper = new HdpGridMapper(termColIds, data);
			HdpMarkerByHeaderPopupRow popupRow = new HdpMarkerByHeaderPopupRow(humanMarker, diseaseMapper);
			humanPopupRows.add(popupRow);
    	}

      	// setup view object
      	ModelAndView mav = new ModelAndView("disease_portal_grid_disease_popup");

		mav.addObject("popupRows", popupRows);
		mav.addObject("humanPopupRows", humanPopupRows);
		mav.addObject("humanMarkers", humanMarkers); // for tests
		mav.addObject("genoClusters", genoClusters);
		mav.addObject("gridClusterString",gridClusterString);
		mav.addObject("termColumns", diseaseTermColumnsToDisplay);
		mav.addObject("termNames", diseaseTerms);
		mav.addObject("terms", termColNames);
		mav.addObject("termHeader", query.getTermHeader());
		mav.addObject("markers", markersInImsr);
		mav.addObject("gridClusterString",gridClusterString);

   		return mav;
    }

    @RequestMapping(value="genoCluster/view/{genoClusterKey:.+}", method = RequestMethod.GET)
    public ModelAndView genoClusterView(@PathVariable("genoClusterKey") String genoClusterKey)
    {
    	List<HdpGenoCluster> genoClusters = hdpFinder.getGenoClusterByKey(genoClusterKey);
    	// there can be only one...
        if (genoClusters.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No GenoCluster Found");
            return mav;
        }
        HdpGenoCluster genoCluster = genoClusters.get(0);

        ModelAndView mav = new ModelAndView("disease_portal_all_geno_popups");
        mav.addObject("genoCluster",genoCluster);
    	return mav;
    }

    //----------------------------//
    // Disease Portal Marker Tab
    //----------------------------//

	@RequestMapping("/markers/json")
	public @ResponseBody JsonSummaryResponse<HdpMarkerSummaryRow> geneSummaryJson(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page) 
	{
		SearchResults<SolrDiseasePortalMarker> searchResults = getSummaryResultsByGene(request, query, page,session,true);
        List<SolrDiseasePortalMarker> mList = searchResults.getResultObjects();

        List<HdpMarkerSummaryRow> summaryRows
          = new ArrayList<HdpMarkerSummaryRow>();

        Map<String,Set<String>> highlights = searchResults.getResultSetMeta().getSetHighlights();

        for (SolrDiseasePortalMarker m : mList)
        {
			if (m != null)
			{
				HdpMarkerSummaryRow summaryRow = new HdpMarkerSummaryRow(m);
				if(highlights.containsKey(m.getMarkerKey()))
				{
					summaryRow.setHighlightedFields(new ArrayList<String>(highlights.get(m.getMarkerKey())));
				}
				summaryRows.add(summaryRow);
			} else {
				logger.debug("--> Null Object");
			}
		}
        JsonSummaryResponse<HdpMarkerSummaryRow> jsonResponse
        		= new JsonSummaryResponse<HdpMarkerSummaryRow>();
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());

		logger.debug("geneSummaryJson finished");
        return jsonResponse;
	}


    //--------------------------------//
    // Disease Portal Marker Downloads
    //--------------------------------//
    @RequestMapping("marker/report*")
    public ModelAndView resultsMarkerSummaryExport(
            HttpServletRequest request,
            HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query)  {

    	logger.debug("generating HDP marker report download");

		ModelAndView mav = new ModelAndView("hdpMarkersSummaryReport");

		Filter qf = parseQueryForm(query,session);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		List<Sort> sorts = genMarkerSorts(request);
		sp.setSorts(sorts);
		DiseasePortalBatchFinder batchFinder = new DiseasePortalBatchFinder(hdpFinder,sp);

		mav.addObject("markerFinder", batchFinder);

		logger.debug("controller finished - routing to view object");

		return mav;
    }


    //----------------------------//
    // Disease Portal Disease Tab
    //----------------------------//

	@RequestMapping("/diseases/json")
	public @ResponseBody JsonSummaryResponse<HdpDiseaseSummaryRow> diseaseSummaryJson(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page)
	{
		SearchResults<SolrVocTerm> searchResults = getSummaryResultsByDisease(request,query,page,session,true);
        List<HdpDiseaseSummaryRow> termList = new ArrayList<HdpDiseaseSummaryRow>();

        Map<String,Set<String>> highlights = searchResults.getResultSetMeta().getSetHighlights();

        for(SolrVocTerm term : searchResults.getResultObjects())
        {
        	HdpDiseaseSummaryRow summaryRow = new HdpDiseaseSummaryRow(term);
        	if(highlights.containsKey(term.getPrimaryId()))
			{
				summaryRow.setHighlightedFields(new ArrayList<String>(highlights.get(term.getPrimaryId())));
			}
        	termList.add(summaryRow);
        }

        JsonSummaryResponse<HdpDiseaseSummaryRow> jsonResponse
        		= new JsonSummaryResponse<HdpDiseaseSummaryRow>();
        jsonResponse.setSummaryRows(termList);
        jsonResponse.setTotalCount(searchResults.getTotalCount());

        return jsonResponse;
	}


    //--------------------------------//
    // Disease Portal Disease Downloads
    //--------------------------------//
    @RequestMapping("disease/report*")
    public ModelAndView resultsDiseaseSummaryExport(
            HttpServletRequest request,
            HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query) {

    	logger.debug("generating HDP disease report download");

		ModelAndView mav = new ModelAndView("hdpDiseaseSummaryReport");

    	Filter qf = parseQueryForm(query,session);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		List<Sort> sorts = genDiseaseSorts(request);
		sp.setSorts(sorts);
		DiseasePortalBatchFinder batchFinder = new DiseasePortalBatchFinder(hdpFinder,sp);

		mav.addObject("diseaseFinder", batchFinder);
		return mav;
    }


    //----------------------------//
    // Disease Portal Pheno Tab
    //----------------------------//

	@RequestMapping("/phenotypes/json")
	public @ResponseBody JsonSummaryResponse<String> phenotypeSummaryJson(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page)
	{
		SearchResults<SolrVocTerm> searchResults = getSummaryResultsByPhenotype(request,query,page,session);
        List<String> termList = new ArrayList<String>();
        for(SolrVocTerm term : searchResults.getResultObjects())
        {
        	termList.add(term.getTerm()+" ("+term.getPrimaryId()+")");
        }

        JsonSummaryResponse<String> jsonResponse
        		= new JsonSummaryResponse<String>();
        jsonResponse.setSummaryRows(termList);
        jsonResponse.setTotalCount(searchResults.getTotalCount());

        return jsonResponse;
	}


    //--------------------------------------------------------------------//
    // Public convenience methods
    //--------------------------------------------------------------------//

	public List<SolrHdpGridData> getAnnotationsInResults(
			@ModelAttribute DiseasePortalQueryForm query,
			List<String> gridClusterKeys,
			HttpSession session)
	{
		logger.debug("-->getAnnotationsInResults");

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		Filter originalQuery = parseQueryForm(query,session);
		Filter gridClusterFilter = new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,gridClusterKeys,Filter.Operator.OP_IN);
		params.setFilter(Filter.and(Arrays.asList(originalQuery,gridClusterFilter)));
		params.setPageSize(10000); // in theory I'm not sure how high this needs to be. 10k is just a start.

		// perform query
		logger.debug("getAnnotationsInResults finished");
		SearchResults<SolrHdpGridData> results = hdpFinder.searchAnnotationsInGridResults(params);

        List<SolrHdpGridData> annotations = results.getResultObjects();

		return annotations;
	}

	public SearchResults<SolrHdpGridCluster> getGridClusters(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page,
			HttpSession session)
	{

		logger.debug("getGridClusters query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		//params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);

		// determine and set the requested sorts, filters, and pagination

		params.setSorts(genGridSorts(request,query));
		params.setPaginator(page);
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getSummaryResultsByGene finished");

		return hdpFinder.getGridClusters(params);
	}

	public SearchResults<SolrString> getHighlightedColumns(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session) {

		logger.debug("getHighlightedColumns disease column query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		Filter or = Filter.extractTermsForNestedFilter(parseQueryForm(query,session));
		or.setFilterJoinClause(JoinClause.FC_OR);
		
		params.setFilter(or);
		params.setPageSize(100000);
		
		logger.debug("getHighlightedColumns finished");
		SearchResults<SolrString> results = hdpFinder.getHighlightedColumns(params);

		return results;
	}

	public SearchResults<SolrString> getHighlightedColumnHeaders(
			DiseasePortalQueryForm query,
			HttpSession session) {

		logger.debug("getHighlightedColumns disease column query: " + query.toString());

		
		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		
		Filter gridClusterFilter = parseQueryForm(query,session);
		Filter termSearchFilter = new Filter();
		Filter.extractTermsForNestedFilter(gridClusterFilter, termSearchFilter, Operator.OP_GREEDY_BEGINS);
		
		gridClusterFilter.replaceProperty(DiseasePortalFields.TERM, DiseasePortalFields.TERM_SEARCH_FOR_GRID_COLUMNS);
		termSearchFilter.replaceProperty(DiseasePortalFields.TERM, DiseasePortalFields.TERM_SEARCH);
	
		if(gridClusterFilter.getNestedFilters() != null && gridClusterFilter.getNestedFilters().size() > 0 && termSearchFilter.getNestedFilters() != null && termSearchFilter.getNestedFilters().size() > 0) {
			Filter andFilter = new Filter();
			andFilter.addNestedFilter(gridClusterFilter);
			andFilter.addNestedFilter(termSearchFilter);
			andFilter.setFilterJoinClause(JoinClause.FC_AND);
			params.setFilter(andFilter);
			params.setPageSize(100000);
		}
		
		logger.debug("getHighlightedColumns finished");
		SearchResults<SolrString> results = hdpFinder.getHighlightedColumnHeaders(params);

		return results;
	}
	
	public SearchResults<SolrString> getGridDiseaseColumns(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{

		logger.debug("getGridClusters disease column query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		params.setSorts(Arrays.asList(new Sort(SortConstants.VOC_TERM_HEADER)));
		params.setPageSize(query.getNumDCol());
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getGridDiseaseColumns finished");
		SearchResults<SolrString> results = hdpFinder.getGridDiseases(params);

		return results;
	}

	public List<String> getGridMpHeaderColumns(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{
		logger.debug("getGridClusters mp header column query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		params.setSorts(Arrays.asList(new Sort(SortConstants.VOC_TERM_HEADER)));
		params.setPageSize(200);
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getGridMpHeaderColumns finished");
		SearchResults<SolrString> results = hdpFinder.huntGridMPHeadersGroup(params);

		List<String> headerCols = new ArrayList<String>();
		for(SolrString ss : results.getResultObjects())
		{
			headerCols.add(ss.toString());
		}
		return headerCols;
	}

	public List<SolrVocTerm> getGridMpTermColumns(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{
		logger.debug("getGenoClusters mp term column query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		params.setSorts(Arrays.asList(new Sort(DiseasePortalFields.BY_TERM_DAG)));
		params.setPageSize(300);
		//params.setFilter(parseSystemPopup(query));
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getGridMpTermColumns finished");
		SearchResults<SolrVocTerm> results = hdpFinder.huntGridMPTermsGroup(params);
		return results.getResultObjects();
	}

	public List<SolrVocTerm> getGridDiseaseTermColumns(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{
		logger.debug("getGenoClusters disease term column query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		params.setSorts(Arrays.asList(new Sort(DiseasePortalFields.TERM)));
		params.setPageSize(300);
		//params.setFilter(parseSystemPopup(query));
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getGridDiseaseTermColumns finished");
		SearchResults<SolrVocTerm> results = hdpFinder.huntGridDiseaseTermsGroup(params);
		return results.getResultObjects();
	}

	public List<SolrDiseasePortalMarker> getGridHumanMarkers(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{
		logger.debug("getGridHumanMarkers query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		params.setSorts(Arrays.asList(new Sort(SortConstants.DP_BY_MRK_SYMBOL)));
		params.setPageSize(1000);
		//params.setFilter(parseSystemPopup(query));
		params.setFilter(parseQueryForm(query,session));


		// perform query and return results as json
		logger.debug("getGridHumanMarkers finished");
		SearchResults<SolrDiseasePortalMarker> results = hdpFinder.huntGridHumanMarkerGroup(params);


		return results.getResultObjects();
	}

	public SearchResults<SolrDiseasePortalMarker> getSummaryResultsByGene(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page,
			HttpSession session)
	{
			return getSummaryResultsByGene(request,query,page,session,false);
	}
	public SearchResults<SolrDiseasePortalMarker> getSummaryResultsByGene(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page,
			HttpSession session,
			boolean doHighlight)
	{

		logger.debug("getSummaryResults query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		//params.setIncludeMetaHighlight(doHighlight);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);

		// determine and set the requested sorts, filters, and pagination
		params.setSorts(genMarkerSorts(request));
		params.setPaginator(page);
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getSummaryResultsByGene finished");

		return hdpFinder.getMarkers(params);
	}

	public SearchResults<SolrVocTerm> getSummaryResultsByDisease(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page,
			HttpSession session)
	{
		return getSummaryResultsByDisease(request,query,page,session,false);
	}
	public SearchResults<SolrVocTerm> getSummaryResultsByDisease(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page,
			HttpSession session,
			boolean doHighlight)
	{

		logger.debug("getSummaryResults query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		//params.setIncludeMetaHighlight(doHighlight);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);

		//sorts.add(new Sort("score",true));
		//sorts.add(new Sort(DiseasePortalFields.TERM,false));
		params.setSorts(genDiseaseSorts(request));
		params.setPaginator(page);
		//params.setSorts(parseSorts(request));
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("params parsed");

		return hdpFinder.getDiseases(params);
	}

	// NOTE: We are only using this function for testing MP query at the momemnt
	public SearchResults<SolrVocTerm> getSummaryResultsByPhenotype(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page,
			HttpSession session)
	{

		logger.debug("getSummaryResultsByPhenotype query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setIncludeSetMeta(true);
		//params.setIncludeMetaHighlight(true);
		params.setIncludeRowMeta(true);
		params.setIncludeMetaScore(true);
		List<Sort> sorts = new ArrayList<Sort>();

		sorts.add(new Sort("score",true));
		sorts.add(new Sort(DiseasePortalFields.TERM,false));
		params.setSorts(sorts);
		params.setPaginator(page);
		//params.setSorts(parseSorts(request));
		params.setFilter(parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("params parsed");

		return hdpFinder.getPhenotypes(params);
	}

	public SearchResults<HdpGenoCluster> getGenoClusters(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{
		logger.debug("getGenoClusters query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		params.setSorts(Arrays.asList(new Sort(DiseasePortalFields.BY_GENOCLUSTER)));
		params.setPageSize(10000); // if this is too low, you can change it, but damn that's a lot of genotypes

		// determine and set the requested sorts, filters, and pagination
//		params.setFilter(parseQueryForm(query));
		//params.setFilter(parseSystemPopup(query));
		params.setFilter(parseQueryForm(query,session));

		// perform query
		logger.debug("getSummaryResultsByGene finished");

		return hdpFinder.getGenoClusters(params);
	}

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    // generate the sorts for the marker tab
    private List<Sort> genMarkerSorts(HttpServletRequest request) {

        logger.debug("->genMarkerSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // first, deal with sort direction
        String dirRequested  = request.getParameter("dir");
        boolean desc = false;
        boolean asc = true;
        if("desc".equalsIgnoreCase(dirRequested)){desc = true; asc=false;}

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");
        if ("organism".equalsIgnoreCase(sortRequested))
        {
          sorts.add(new Sort(SortConstants.DP_BY_ORGANISM, asc));
          sorts.add(new Sort(SortConstants.DP_BY_MRK_SYMBOL, desc));
        }
        else if ("homologeneId".equalsIgnoreCase(sortRequested))
        {
          sorts.add(new Sort(SortConstants.DP_BY_HOMOLOGENE_ID, desc));
          sorts.add(new Sort(SortConstants.DP_BY_MRK_SYMBOL, desc));
        }
        else if ("symbol".equalsIgnoreCase(sortRequested))
        {
          sorts.add(new Sort(SortConstants.DP_BY_MRK_SYMBOL, desc));
          sorts.add(new Sort(SortConstants.DP_BY_ORGANISM, asc));
        }
        else if ("type".equalsIgnoreCase(sortRequested))
        {
          sorts.add(new Sort(SortConstants.DP_BY_MRK_TYPE, desc));
          sorts.add(new Sort(SortConstants.DP_BY_MRK_SYMBOL, false));
        }
        else if ("coordinate".equalsIgnoreCase(sortRequested))
        {
          sorts.add(new Sort(SortConstants.DP_BY_ORGANISM, asc));
          sorts.add(new Sort(SortConstants.DP_BY_LOCATION, desc));
        }
        else
        { // default sort
          sorts.add(new Sort(SortConstants.DP_BY_MRK_SYMBOL, false));
          sorts.add(new Sort(SortConstants.DP_BY_ORGANISM, true));
		}

        return sorts;
    }

    // generate the sorts for the marker tab
    private List<Sort> genGridSorts(HttpServletRequest request,DiseasePortalQueryForm query) {

        logger.debug("->genGridSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        if(query.getHasLocationsQuery())
        {
        	String organism = query.getOrganism();
        	if("human".equalsIgnoreCase(organism))
        	{
        		logger.debug("using human location sort");
            	sorts.add(new Sort(DiseasePortalFields.GRID_BY_HUMAN_LOCATION, false));
    	        sorts.add(new Sort(DiseasePortalFields.GRID_BY_MOUSE_LOCATION, false));
        	}
        	else
        	{
        		logger.debug("using mouse location sort");
    	        sorts.add(new Sort(DiseasePortalFields.GRID_BY_MOUSE_LOCATION, false));
        		sorts.add(new Sort(DiseasePortalFields.GRID_BY_HUMAN_LOCATION, false));
        	}

        }
        else
        {
	        // default sort is by organism then gene symbol
	        sorts.add(new Sort(SortConstants.DP_BY_ORGANISM, true));
	        sorts.add(new Sort(SortConstants.DP_BY_MRK_SYMBOL, false));
        }


        return sorts;
    }

 // generate the sorts for the diseases tab
    private List<Sort> genDiseaseSorts(HttpServletRequest request) {

        logger.debug("->genDiseaseSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // first, deal with sort direction
        String dirRequested  = request.getParameter("dir");
        boolean desc = false;
        if("desc".equalsIgnoreCase(dirRequested)){desc = true;}

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");
        if ("disease".equalsIgnoreCase(sortRequested))
        {
        	sorts.add(new Sort(SortConstants.VOC_TERM,desc));
        }
        else if ("diseaseId".equalsIgnoreCase(sortRequested))
        {
        	sorts.add(new Sort(SortConstants.VOC_TERM_ID,desc));
        }

        else
        { // default sort
        	//sorts.add(new Sort("score",true));
    		sorts.add(new Sort(SortConstants.VOC_TERM,false));
		}

        return sorts;
    }

	// parses the query parameters pass into main queries
	private Filter parseQueryForm(DiseasePortalQueryForm query,HttpSession session)
	{
		List<Filter> qFilters = new ArrayList<Filter>();

		// handle any filters
		String fGenes = query.getFGene();
		if(notEmpty(fGenes))
		{
			List<String> tokens = Arrays.asList(fGenes.split("\\|"));
			qFilters.add( new Filter(SearchConstants.DP_GRID_CLUSTER_KEY, tokens, Filter.Operator.OP_IN));
		}

		String fHeaders = query.getFHeader();
		if(notEmpty(fHeaders))
		{
			List<String> tokens = Arrays.asList(fHeaders.split("\\|"));
			qFilters.add( new Filter(DiseasePortalFields.TERM_HEADER, tokens, Filter.Operator.OP_IN));
		}

		// retrict results based on the feature types (of the markers)
		List<String> featureTypes = query.getFeatureTypeFilter();
		if ((featureTypes != null) && (featureTypes.size() > 0)) {
			qFilters.add(new Filter(DiseasePortalFields.FILTERABLE_FEATURE_TYPES, featureTypes, Filter.Operator.OP_IN));
		}

        // grid cluster key
		String gridClusterKey = query.getGridClusterKey();
		if(notEmpty(gridClusterKey))
		{
			qFilters.add( new Filter(SearchConstants.DP_GRID_CLUSTER_KEY, gridClusterKey, Filter.Operator.OP_EQUAL));
		}

        // termHeader
		String termHeader = query.getTermHeader();
		if(notEmpty(termHeader))
		{
			qFilters.add(new Filter(SearchConstants.MP_HEADER, termHeader,Filter.Operator.OP_EQUAL));
		}

		// termId
		String termId = query.getTermId();
		if(notEmpty(termId))
		{
			qFilters.add(new Filter(SearchConstants.VOC_TERM_ID, termId,Filter.Operator.OP_EQUAL));
		}

        // phenotype entry box
		String phenotypes = query.getPhenotypes();
		if(notEmpty(phenotypes))
		{
			Filter phenoFilter;
			BooleanSearch bs = new BooleanSearch();
			phenoFilter = bs.buildSolrFilter(SearchConstants.VOC_TERM, phenotypes.replace(",", " "));
			if(phenoFilter == null) {
				logger.warn("Error with Query: \n" + phenotypes + " " + bs.getErrorMessage());
				phenoFilter = generateHdpNomenFilter(SearchConstants.VOC_TERM, phenotypes);
			}
			
			logger.info("Filters: " + phenoFilter);
			if(phenoFilter != null) qFilters.add(phenoFilter);
		}

        // genes entry box
		String genes = query.getGenes();
		if(notEmpty(genes))
		{
			Filter genesFilter = generateHdpNomenFilter(SearchConstants.MRK_NOMENCLATURE, genes);
			if(genesFilter != null) qFilters.add(genesFilter);
		}

		String geneFileValue = (String) session.getAttribute(DiseasePortalQueryForm.GENE_FILE_VAR);
		if(notEmpty(geneFileValue) && usingGenefileQuery(query,session))
		{
			Filter genesFilter = generateHdpNomenFilter(SearchConstants.MRK_NOMENCLATURE, geneFileValue);
			if(genesFilter != null) qFilters.add(genesFilter);
		}

        // location entry box
		String locations = query.getLocations();

		// add any file data that may be in the session
		boolean useLocationsFile = usingLocationsQuery(query,session);
		String locationsFileSet = (String) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR);
		@SuppressWarnings("unchecked")
		List<String> markerKeysFromLocationsFile= DiseasePortalQueryForm.HUMAN.equals(query.getOrganism())
				? (List<String>) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_HUMAN_KEYS)
						: (List<String>) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_MOUSE_KEYS);

		boolean hasMarkerKeysFromLocationsFile = useLocationsFile && notEmpty(locationsFileSet) && markerKeysFromLocationsFile!=null && markerKeysFromLocationsFile.size()>0;

		if(notEmpty(locations) || hasMarkerKeysFromLocationsFile)
		{
			List<Filter> locationFilters = new ArrayList<Filter>();
			if(notEmpty(locations))
			{
				List<String> tokens = QueryParser.tokeniseOnWhitespaceAndComma(locations);
				//logger.debug("location tokens : "+StringUtils.join(tokens,","));
				// decide whether to query mouse or human
				String coordField = DiseasePortalQueryForm.HUMAN.equals(query.getOrganism())
						? SearchConstants.HUMAN_COORDINATE : SearchConstants.MOUSE_COORDINATE;
				for(String token : tokens)
				{
					String spatialQueryString = SolrLocationTranslator.getQueryValue(token);
					if(notEmpty(spatialQueryString))
					{
						locationFilters.add(new Filter(coordField,spatialQueryString,Filter.Operator.OP_HAS_WORD));
					}
				}
			}

			if(locationFilters.size()>0) qFilters.add(Filter.or(locationFilters));
		}

		if(hasMarkerKeysFromLocationsFile)
		{
			//logger.debug("making a query with marker keys matched via locations file");
			qFilters.add(new Filter(SearchConstants.MRK_KEY,markerKeysFromLocationsFile,Filter.Operator.OP_IN));
		}


		if(qFilters.size()>0)
		{
			return Filter.and(qFilters);
		}
		return new Filter(SearchConstants.PRIMARY_KEY,"###NONE###",Filter.Operator.OP_HAS_WORD);
	}

	private boolean notEmpty(String str)
	{ return str!=null && !str.equals(""); }

	private Filter generateHdpNomenFilter(String property, String query){
		//logger.debug("splitting nomenclature query into tokens");
		Collection<String> nomens = QueryParser.parseNomenclatureSearch(query,false,"\"");
		List<Filter> nomenFilters = new ArrayList<Filter>();
		// we want to group all non-wildcarded tokens into one solr phrase search
		List<String> nomenTokens = new ArrayList<String>();

		for(String nomen : nomens) {
			if(nomen.endsWith("*") || nomen.startsWith("*")) {
				nomenTokens.add(nomen);
			} else {
				// use a phrase slop search
				nomenTokens.add("\""+nomen+"\"~100");
			}
		}

		for(String nomenToken : nomenTokens) {
			//logger.debug("token="+nomenToken);
			Filter nFilter = new Filter(property, nomenToken,Filter.Operator.OP_HAS_WORD);
			nomenFilters.add(nFilter);
		}

		if(nomenFilters.size() > 0) {
			// add the nomenclature search filter
			return Filter.or(nomenFilters);
		}
		// We don't want to return an empty filter object, because it screws up Solr.
		return null;
	}

	// -----------------------------------------------------------------//
	// Methods for getting query counts
	// -----------------------------------------------------------------//

	@RequestMapping("/grid/totalCount")
	public @ResponseBody Integer getGridClusterCount(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(parseQueryForm(query,session));
		params.setPageSize(0);
		return hdpFinder.getGridClusterCount(params);
	}
	@RequestMapping("/markers/totalCount")
	public @ResponseBody Integer getMarkerCount(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(parseQueryForm(query,session));
		params.setPageSize(0);
		return hdpFinder.getMarkerCount(params);
	}
	@RequestMapping("/diseases/totalCount")
	public @ResponseBody Integer getDiseaseCount(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query)
	{
		SearchParams params = new SearchParams();
		params.setFilter(parseQueryForm(query,session));
		params.setPageSize(0);
		return hdpFinder.getDiseaseCount(params);
	}

	// -----------------------------------------------------------------//
	// facets for HMDC pages
	// -----------------------------------------------------------------//

	// now unused, kept for future use
	private Map<String, List<String>> facetGeneric (DiseasePortalQueryForm query, BindingResult result, HttpSession session, String facetType) {
	    logger.debug(query.toString());
	    String order = ALPHA;

	    SearchParams params = new SearchParams();
	    params.setFilter(this.parseQueryForm(query, session));

	    SearchResults<SolrString> facetResults = null;

	    if (FacetConstants.MARKER_FEATURE_TYPE.equals(facetType)){
		facetResults = hdpFinder.getFeatureTypeFacet(params);
	    } else {
		facetResults = new SearchResults<SolrString>();
	    }
	    return this.parseFacetResponse(facetResults, order);
	}

	// now unused, kept for future use
	private Map<String, List<String>> parseFacetResponse (SearchResults<SolrString> facetResults, String order) {

	    Map<String, List<String>> m = new HashMap<String, List<String>>();
	    List<String> l = new ArrayList<String>();

	    if (facetResults.getResultFacets().size() >= facetLimit) {
		l.add("Too many results to display. Modify your search or try another filter first.");
		m.put("error", l);
	    } else if (ALPHA.equals(order)) {
		m.put("resultFacets", facetResults.getSortedResultFacets());
	    } else {
		m.put("resultFacets", facetResults.getResultFacets());
	    }
	    return m; 
	}

	/* gets a list of feature types for markers which match the
	 * current query, returned as JSON
	 */
	@RequestMapping("/facet/featureType")
	public @ResponseBody Map<String, List<String>> facetFeatureType(
		HttpServletRequest request,
		HttpSession session,
		@ModelAttribute DiseasePortalQueryForm query, 
		BindingResult result) {
	
	    // get all the markers on the Genes tab

	    SearchResults<SolrDiseasePortalMarker> searchResults =
		getSummaryResultsByGene(request, query, Paginator.ALL_PAGES,
			session, true);

	    // collect a set of all feature types for those markers

	    HashSet<String> featureTypes = new HashSet<String>();

	    for (SolrDiseasePortalMarker m : searchResults.getResultObjects()) {
		if (m != null) {
		    featureTypes.add(m.getType());
		}
	    }

	    // need to sort in a case insensitive manner

	    HashMap<String,String> lowerToUpper = new HashMap<String,String>();

	    for (String ft : featureTypes.toArray(new String[0])) {
		if (ft != null) {
		    lowerToUpper.put (ft.toLowerCase(), ft);
		}
	    }

	    String[] lowerFeatureTypes =
		lowerToUpper.keySet().toArray(new String[0]);
	    Arrays.sort(lowerFeatureTypes);

	    // now assemble the corresponding mixed-case list
	    
	    List<String> ftList = new ArrayList<String>();

	    for (String ft : lowerFeatureTypes) {
		ftList.add(lowerToUpper.get(ft));
	    }

	    // finally, compose the map to be sent out via JSON

	    Map<String, List<String>> m = new HashMap<String, List<String>>();

	    if (ftList.size() >= facetLimit) {
	    	List<String> l = new ArrayList<String>();
		l.add("Too many results to display. Modify your search or try another filter first.");
		m.put("error", l);
	    } else {
		m.put("resultFacets", ftList);
	    }
	    return m; 
	}

	// -----------------------------------------------------------------//
	// Utility Methods for specific information
	// -----------------------------------------------------------------//
	private String getGridClusterTitleForPopup(String gridClusterKey)
	{
		return getGridClusterTitle(gridClusterKey,false);
	}
	private String getGridClusterTitle(String gridClusterKey,boolean includeHumanSymbols)
	{
		logger.debug("resolving grid cluster key("+gridClusterKey+") gene symbols");
		String gridClusterString = "";
		DiseasePortalQueryForm dpQf = new DiseasePortalQueryForm();
		dpQf.setGridClusterKey(gridClusterKey);
		SearchParams sp = new SearchParams();
		sp.setPageSize(1);
		sp.setFilter(new Filter(SearchConstants.DP_GRID_CLUSTER_KEY, gridClusterKey, Filter.Operator.OP_EQUAL));
		SearchResults<SolrHdpGridCluster> gridClusters = hdpFinder.getGridClusters(sp);
		if(gridClusters.getResultObjects().size()>0)
		{
			SolrHdpGridCluster gridCluster = gridClusters.getResultObjects().get(0);
			List<String> symbols = new ArrayList<String>();
			if(includeHumanSymbols)
			{
			    for(String humanSymbol : gridCluster.getHumanSymbols())
			    {
				    symbols.add(humanSymbol);
			    }
			}
			for(SolrDpGridClusterMarker m : gridCluster.getMouseMarkers())
			{
				symbols.add(m.getSymbol());
			}
			gridClusterString = StringUtils.join(symbols,", ");
		}
		return gridClusterString;
	}

	private List<String> convertLocationsToMarkerKeys(List<String> locationCoordinateTokens,String organism)
	{
		/*
		 *  There is some limit on the number of coordinates we can query Solr with at a time.
		 *  All I know is that SolrJ throws a weird javabin format error at numbers exceeding roughly 37,000 coordinates
		 */
		Integer maxLocationsInAQuery = 1000;
		Set<String> markerKeys = new HashSet<String>();
		if(locationCoordinateTokens!=null && locationCoordinateTokens.size()>0)
		{
			List<List<String>> tokenBatches = FewiUtil.getBatches(locationCoordinateTokens, maxLocationsInAQuery);
			for(int i=0; i<tokenBatches.size(); i++)
			{
				if(i>0) logger.debug("convertLocationsToMarkerKeys-> Chunking through coordinate query. "+(tokenBatches.size()-i)+" chunks left.");
				List<String> tokens = tokenBatches.get(i);
				
				SearchParams sp = new SearchParams();
				sp.setPageSize(100000); // there should be no limit on this
				sp.setFetchKeysOnly(true);
				sp.setSuppressLogs(true);

				List<Filter> locationFilters = new ArrayList<Filter>();
				//logger.debug("location tokens : "+StringUtils.join(tokens,","));
				// decide whether to query mouse or human
				String coordField = DiseasePortalQueryForm.HUMAN.equals(organism)
						? SearchConstants.HUMAN_COORDINATE : SearchConstants.MOUSE_COORDINATE;
				for(String token : tokens)
				{
					String spatialQueryString = SolrLocationTranslator.getQueryValue(token);
					if(notEmpty(spatialQueryString))
					{
						locationFilters.add(new Filter(coordField,spatialQueryString,Filter.Operator.OP_HAS_WORD));
					}
				}

				if(locationFilters.size()>0)
				{
					sp.setFilter(Filter.or(locationFilters));
					// query solr for the matching marker keys
					if(tokenBatches.size()==1) return hdpFinder.getMarkerKeys(sp);
					else markerKeys.addAll(hdpFinder.getMarkerKeys(sp));
				}
			}
		}
		return new ArrayList<String>(markerKeys);
	}

	private static boolean usingLocationsQuery(DiseasePortalQueryForm query, HttpSession session)
	{
		String locationsFileName = query.getLocationsFileName();
		return locationsFileName!=null && locationsFileName.equals(session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_NAME));
	}

	private static boolean usingGenefileQuery(DiseasePortalQueryForm query, HttpSession session)
	{
		String geneFileName = query.getGeneFileName();
		return geneFileName!=null && geneFileName.equals(session.getAttribute(DiseasePortalQueryForm.GENE_FILE_VAR_NAME));
	}

}
