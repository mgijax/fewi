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

import mgi.frontend.datamodel.HdpGenoCluster;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fewi.finder.DiseasePortalBatchFinder;
import org.jax.mgi.fewi.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.forms.DiseasePortalQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrDiseasePortalMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGenoInResult;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster;
import org.jax.mgi.fewi.searchUtil.entities.SolrDpGridCluster.SolrDpGridClusterMarker;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.summary.DiseasePortalDiseaseSummaryRow;
import org.jax.mgi.fewi.summary.DiseasePortalMarkerSummaryRow;
import org.jax.mgi.fewi.summary.HdpGenoByHeaderPopupRow;
import org.jax.mgi.fewi.summary.HdpGridClusterSummaryRow;
import org.jax.mgi.fewi.summary.HdpMarkerByHeaderPopupRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FileProcessor;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.HdpGridMapper;
import org.jax.mgi.fewi.util.ImageUtils;
import org.jax.mgi.fewi.util.QueryParser;
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

/*
 * This controller maps all /diseasePortal/ uri's
 */
@Controller
@RequestMapping(value="/diseasePortal")
public class DiseasePortalController
{
	
	// logger for the class
	private Logger logger = LoggerFactory.getLogger(DiseasePortalController.class);

	// get the finders used by various methods
	@Autowired
	private DiseasePortalFinder hdpFinder;

    @Value("${solr.factetNumberDefault}")
    private Integer facetLimit;


    //--------------------------//
    // Disease Portal Query Form
    //--------------------------//

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model,
			HttpSession session) {
		model.addAttribute(new DiseasePortalQueryForm());
		//String locationsData = (String) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR);
		//if(notEmpty(locationsData)) model.addAttribute("locationsFileName",);
		
		return "disease_portal_query";
	}


   	public String getRotatedTextImgTag(String text)
   	{
   		return getRotatedTextImgTag(text,30);
   	}
	public String getRotatedTextImgTag(String text,int maxChars)
	{	
		try{
			String rotatedTextTag = ImageUtils.getRotatedTextImageTagAbbreviated(text,310.0,maxChars);

			return rotatedTextTag;
		}catch(Exception e){
			e.printStackTrace();
			logger.error("error genning image",e);			
		}
		
		return "";
	}

    //----------------------------//
    // Disease Portal Summary Page
    //----------------------------//

    @RequestMapping("/summary")
    public ModelAndView genericSummary(
		    @ModelAttribute DiseasePortalQueryForm query,
            HttpServletRequest request,
            HttpSession session) {

    	logger.debug("generating generic DiseasePortal summary");
    	
    	String queryString = request.getQueryString();
    	// if the queryString is empty, this might be a POST request
    	if(!notEmpty(queryString)) queryString = FormatHelper.queryStringFromPost(request);
    	
		logger.debug("query string: " + queryString);
		logger.debug("query form: " + query);
		

		ModelAndView mav = new ModelAndView("disease_portal_query");
		if(notEmpty(queryString) && !queryString.contains("tab=")) queryString += "&tab=gridtab";
		mav.addObject("querystring", queryString);
		
		boolean useLocationsFile = DiseasePortalController.usingLocationsQuery(query,session);
		if(useLocationsFile) mav.addObject("locationsFileName",(String) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_NAME));
		
		return mav;

    }
    
    @RequestMapping("/isFileCached")
    public @ResponseBody String checkIfLocationsFileIsCached(HttpServletRequest request,
    		HttpSession session,
    		@ModelAttribute DiseasePortalQueryForm query)
    {
    	boolean useLocationsFile = DiseasePortalController.usingLocationsQuery(query,session);
    	if(!useLocationsFile) return "(Warning: File must be re-uploaded to see results)";
    	return "";
    }
    
    @RequestMapping("/uploadFile")
    public @ResponseBody ModelAndView uploadFile(MultipartHttpServletRequest request,
    		HttpSession session,
    		@RequestParam("file") MultipartFile file,
    		@RequestParam("field") String field,
    		@RequestParam("type") String type) {

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
		
		if(DiseasePortalQueryForm.ACCEPTABLE_FILE_VARS.contains(field))
		{
			// clear the session variable for this field
			session.setAttribute(field,null);

			// set the filename
			if(DiseasePortalQueryForm.LOCATIONS_FILE_VAR.equals(field))
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

						dataString = FileProcessor.processVCFCoordinates(file);
		        		logger.debug("finished processing vcf file ["+file.getOriginalFilename()+"] for coordinates");
		        		
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
			        		
			        		logger.debug("converting file locations to human marker keys");
			        		List<String> humanMarkerKeysFromLocationsFile = convertLocationsToMarkerKeys(locationQueryTokens,DiseasePortalQueryForm.HUMAN);
			        		session.setAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_HUMAN_KEYS,humanMarkerKeysFromLocationsFile);
			        		logger.debug("finished converting file locations to human marker keys");
			        		logger.debug("found "+humanMarkerKeysFromLocationsFile.size()+" human marker keys");
			        		
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
	
		        	// save the data in the session;
		        	session.setAttribute(field,dataString);
				} catch (IOException e) {
					logger.error("error reading HDP upload file",e);
					mav.addObject("error","File reading IOException");
					return mav;
				} catch (Exception e) {
					logger.error("error processing HDP upload file",e);
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
		
        mav.addObject("success","File successfully processed. <br/>Please verify your organism selection before hitting \"GO\".");
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
    	logger.debug("->diseaseGrid started");

      	// add headers to allow AJAX access
      	AjaxUtils.prepareAjaxHeaders(response);

      	// setup view object
      	ModelAndView mav = new ModelAndView("disease_portal_grid");

      	// search for grid cluster objects
      	SearchResults<SolrDpGridCluster> searchResults = this.getGridClusters(request, query, page,session);
      	List<SolrDpGridCluster> gridClusters = searchResults.getResultObjects();

      	// search for diseases in result set - make column headers and ID list
      	List<String> diseaseNames = this.getGridDiseaseColumns(request, query,session);
		List<String> diseaseColumnsToDisplay = new ArrayList<String>();
		List<String> diseaseIds = new ArrayList<String>();
		for(String disease : diseaseNames)
		{
			String headerText = disease;
			diseaseColumnsToDisplay.add(this.getRotatedTextImgTag(headerText));
			//diseaseIds.add(vt.getPrimaryId());
			//diseaseNames.add(disease);
		}

      	// search for mp headers in result set & make column headers
      	List<String> mpHeaders = this.getGridMpHeaderColumns(request,query,session);
      	List<String> mpHeaderColumnsToDisplay = new ArrayList<String>();
		for(String mpHeader : mpHeaders)
		{
			mpHeaderColumnsToDisplay.add(this.getRotatedTextImgTag(mpHeader));
		}

		logger.info("diseasePortal/grid -> querying solr for grid data");
      	// Search for the genotype clusters used to generate the result set
      	// and save as map for later
      	List<SolrDpGenoInResult> annotationsInResults = this.getAnnotationsInResults(query,searchResults.getResultKeys(),session);
        Map<Integer,List<SolrDpGenoInResult>> gridClusterToMPResults = new HashMap<Integer,List<SolrDpGenoInResult>>();
        Map<Integer,List<SolrDpGenoInResult>> gridClusterToDiseaseResults = new HashMap<Integer,List<SolrDpGenoInResult>>();

        
        logger.info("diseasePortal/grid -> mapping Solr data to gridClusters");
        for(SolrDpGenoInResult dpa : annotationsInResults)
        {
        	// configure which map to use based on vocab name
        	Map<Integer,List<SolrDpGenoInResult>> dataMap = gridClusterToMPResults;
        	if("OMIM".equals(dpa.getVocabName())) dataMap = gridClusterToDiseaseResults;
        	
        	// map each genocluster/header combo to its corresponding gridcluster key
            if (!dataMap.containsKey(dpa.getGridClusterKey()))
            {
            	dataMap.put(dpa.getGridClusterKey(),new ArrayList<SolrDpGenoInResult>());
            }
            dataMap.get(dpa.getGridClusterKey()).add(dpa);
        }

		logger.info("diseasePortal/grid -> creating grid row objects");
		// create grid row objects
		List<HdpGridClusterSummaryRow> summaryRows = new ArrayList<HdpGridClusterSummaryRow>();
		for(SolrDpGridCluster gc : gridClusters)
		{
            // ensure the cross-reference genoInResults list exists for this row
			boolean foundGridCluster=false;
			
			List<SolrDpGenoInResult> mpResults = new ArrayList<SolrDpGenoInResult>();
			List<SolrDpGenoInResult> diseaseResults = new ArrayList<SolrDpGenoInResult>();
			
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
        SearchResults<HdpGenoCluster> searchResults = this.getGenoClusters(request, query,session);
        List<HdpGenoCluster> genoClusters = searchResults.getResultObjects();
    	//logger.debug("->gridSystemCell; number of genoClusters=" + genoClusters.size());

        // get the mp term columns
    	List<SolrVocTerm> mpTerms = this.getGridMpTermColumns(request,query,session);
      	List<String> mpTermColumnsToDisplay = new ArrayList<String>();
		List<String> termColIds = new ArrayList<String>();
		List<String> termColNames = new ArrayList<String>(); // needed to automated tests

		for(SolrVocTerm mpTerm : mpTerms)
		{
			// use 30 max characters for the popup
			mpTermColumnsToDisplay.add(this.getRotatedTextImgTag(mpTerm.getTerm(),30));
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
		mav.addObject("terms", termColNames);
		mav.addObject("termHeader", query.getTermHeader());
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
        SearchResults<HdpGenoCluster> searchResults = this.getGenoClusters(request, query,session);
        List<HdpGenoCluster> genoClusters = searchResults.getResultObjects();

        // gather the disease columns
    	List<SolrVocTerm> diseaseTerms = this.getGridDiseaseTermColumns(request,query,session);
      	List<String> diseaseTermColumnsToDisplay = new ArrayList<String>();
		List<String> termColIds = new ArrayList<String>();
		List<String> termColNames = new ArrayList<String>(); // needed to automated tests

		for(SolrVocTerm diseaseTerm : diseaseTerms)
		{
			// use 30 max characters for the popup
			diseaseTermColumnsToDisplay.add(this.getRotatedTextImgTag(diseaseTerm.getTerm(),30));
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
    	List<SolrDiseasePortalMarker> humanMarkers = this.getGridHumanMarkers(request,query,session);
    	SearchParams sp = new SearchParams();
    	sp.setFilter(this.parseQueryForm(query,session));
    	sp.setPageSize(10000);
    	SearchResults<SolrDpGenoInResult> sr = hdpFinder.searchAnnotationsInPopupResults(sp);
    	// map the results by marker key
    	Map<Integer,List<SolrDpGenoInResult>> humanResults = new HashMap<Integer,List<SolrDpGenoInResult>>();
    	for(SolrDpGenoInResult gir : sr.getResultObjects())
    	{
    		Integer mKey = gir.getMarkerKey();
    		if(mKey!=null)
    		{
        		logger.info("found human data for marker key "+mKey);
    			if(!humanResults.containsKey(mKey))
    			{
    				humanResults.put(mKey,new ArrayList<SolrDpGenoInResult>());
    			}
    			humanResults.get(mKey).add(gir);
    		}
    	}
    	
    	// cross reference the human marker data
    	List<HdpMarkerByHeaderPopupRow> humanPopupRows = new ArrayList<HdpMarkerByHeaderPopupRow>();
    	for(SolrDiseasePortalMarker humanMarker : humanMarkers)
    	{
    		Integer mKey = Integer.parseInt(humanMarker.getMarkerKey());
    		List<SolrDpGenoInResult> data = new ArrayList<SolrDpGenoInResult>();
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
		mav.addObject("terms", termColNames);
		mav.addObject("termHeader", query.getTermHeader());
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
	public @ResponseBody JsonSummaryResponse<DiseasePortalMarkerSummaryRow> geneSummaryJson(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page)
	{
		SearchResults<SolrDiseasePortalMarker> searchResults
		  = this.getSummaryResultsByGene(request, query, page,session,true);
        List<SolrDiseasePortalMarker> mList = searchResults.getResultObjects();

        List<DiseasePortalMarkerSummaryRow> summaryRows
          = new ArrayList<DiseasePortalMarkerSummaryRow>();

        Map<String,Set<String>> highlights = searchResults.getResultSetMeta().getSetHighlights();

        for (SolrDiseasePortalMarker m : mList)
        {
			if (m != null)
			{
				DiseasePortalMarkerSummaryRow summaryRow = new DiseasePortalMarkerSummaryRow(m);
				if(highlights.containsKey(m.getMarkerKey()))
				{
					summaryRow.setHighlightedFields(new ArrayList<String>(highlights.get(m.getMarkerKey())));
				}
				summaryRows.add(summaryRow);
			} else {
				logger.debug("--> Null Object");
			}
		}
        JsonSummaryResponse<DiseasePortalMarkerSummaryRow> jsonResponse
        		= new JsonSummaryResponse<DiseasePortalMarkerSummaryRow>();
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
			@ModelAttribute DiseasePortalQueryForm query) {

    	logger.debug("generating HDP marker report download");

		ModelAndView mav = new ModelAndView("hdpMarkersSummaryReport");
		
		Filter qf = this.parseQueryForm(query,session);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		List<Sort> sorts = this.genMarkerSorts(request);
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
	public @ResponseBody JsonSummaryResponse<DiseasePortalDiseaseSummaryRow> diseaseSummaryJson(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute DiseasePortalQueryForm query,
			@ModelAttribute Paginator page)
	{
		SearchResults<SolrVocTerm> searchResults = this.getSummaryResultsByDisease(request,query,page,session,true);
        List<DiseasePortalDiseaseSummaryRow> termList = new ArrayList<DiseasePortalDiseaseSummaryRow>();

        Map<String,Set<String>> highlights = searchResults.getResultSetMeta().getSetHighlights();

        for(SolrVocTerm term : searchResults.getResultObjects())
        {
        	DiseasePortalDiseaseSummaryRow summaryRow = new DiseasePortalDiseaseSummaryRow(term);
        	if(highlights.containsKey(term.getPrimaryId()))
			{
				summaryRow.setHighlightedFields(new ArrayList<String>(highlights.get(term.getPrimaryId())));
			}
        	termList.add(summaryRow);
        }

        JsonSummaryResponse<DiseasePortalDiseaseSummaryRow> jsonResponse
        		= new JsonSummaryResponse<DiseasePortalDiseaseSummaryRow>();
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
    	
    	Filter qf = this.parseQueryForm(query,session);
		SearchParams sp = new SearchParams();
		sp.setFilter(qf);
		List<Sort> sorts = this.genDiseaseSorts(request);
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
		SearchResults<SolrVocTerm> searchResults = this.getSummaryResultsByPhenotype(request,query,page,session);
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

	public List<SolrDpGenoInResult> getAnnotationsInResults(
			@ModelAttribute DiseasePortalQueryForm query,
			List<String> gridClusterKeys,
			HttpSession session)
	{
		logger.debug("-->getAnnotationsInResults");

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();
		Filter originalQuery = this.parseQueryForm(query,session);
		Filter gridClusterFilter = new Filter(SearchConstants.DP_GRID_CLUSTER_KEY,gridClusterKeys,Filter.OP_IN);
		params.setFilter(Filter.and(Arrays.asList(originalQuery,gridClusterFilter)));
		params.setPageSize(10000); // in theory I'm not sure how high this needs to be. 10k is just a start.

		// perform query
		logger.debug("getAnnotationsInResults finished");
		SearchResults<SolrDpGenoInResult> results = hdpFinder.searchAnnotationsInGridResults(params);

        List<SolrDpGenoInResult> annotations = results.getResultObjects();

		return annotations;
	}

	public SearchResults<SolrDpGridCluster> getGridClusters(
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
// TODO - setup sorts and pagination
		params.setSorts(this.genGridSorts(request,query));
		params.setPaginator(page);
		params.setFilter(this.parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getSummaryResultsByGene finished");

		return hdpFinder.getGridClusters(params);
	}

	public List<String> getGridDiseaseColumns(
			HttpServletRequest request,
			@ModelAttribute DiseasePortalQueryForm query,
			HttpSession session)
	{

		logger.debug("getGridClusters disease column query: " + query.toString());

		// parse the various query parameter to generate SearchParams object
		SearchParams params = new SearchParams();

		params.setSorts(Arrays.asList(new Sort(SortConstants.VOC_TERM_HEADER)));
		params.setPageSize(500); // I'm not sure we want to display more than this...
		params.setFilter(this.parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getGridDiseaseColumns finished");
		SearchResults<String> results = hdpFinder.getGridDiseases(params);

		return results.getResultObjects();
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
		params.setPageSize(200); // I'm not sure we want to display more than this...
		params.setFilter(this.parseQueryForm(query,session));

		// perform query and return results as json
		logger.debug("getGridMpHeaderColumns finished");
		SearchResults<String> results = hdpFinder.huntGridMPHeadersGroup(params);

		return results.getResultObjects();
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
		params.setPageSize(300); // I'm not sure we want to display more than this...
		//params.setFilter(this.parseSystemPopup(query));
		params.setFilter(this.parseQueryForm(query,session));
		
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
		params.setPageSize(300); // I'm not sure we want to display more than this...
		//params.setFilter(this.parseSystemPopup(query));
		params.setFilter(this.parseQueryForm(query,session));
		
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
		//params.setFilter(this.parseSystemPopup(query));
		params.setFilter(this.parseQueryForm(query,session));

		
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
		params.setSorts(this.genMarkerSorts(request));
		params.setPaginator(page);
		params.setFilter(this.parseQueryForm(query,session));

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
		List<Sort> sorts = new ArrayList<Sort>();

		//sorts.add(new Sort("score",true));
		//sorts.add(new Sort(DiseasePortalFields.TERM,false));
		params.setSorts(this.genDiseaseSorts(request));
		params.setPaginator(page);
		//params.setSorts(this.parseSorts(request));
		params.setFilter(this.parseQueryForm(query,session));

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
		//params.setSorts(this.parseSorts(request));
		params.setFilter(this.parseQueryForm(query,session));

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
//		params.setFilter(this.parseQueryForm(query));
		//params.setFilter(this.parseSystemPopup(query));
		params.setFilter(this.parseQueryForm(query,session));
		
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
        	logger.debug("using location sort");
        	String organism = query.getOrganism();
        	boolean orgSort = "human".equalsIgnoreCase(organism) ? false : true;
        	
        	sorts.add(new Sort(SortConstants.DP_BY_ORGANISM, orgSort));
	        sorts.add(new Sort(SortConstants.DP_BY_LOCATION, false));
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
        boolean asc = true;
        if("desc".equalsIgnoreCase(dirRequested)){desc = true; asc=false;}

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
			qFilters.add( new Filter(SearchConstants.DP_GRID_CLUSTER_KEY, tokens, Filter.OP_IN));
		}
		
		String fHeaders = query.getFHeader();
		if(notEmpty(fHeaders))
		{
			List<String> tokens = Arrays.asList(fHeaders.split("\\|"));
			qFilters.add( new Filter(DiseasePortalFields.TERM_HEADER, tokens, Filter.OP_IN));
		}

        // grid cluster key
		String gridClusterKey = query.getGridClusterKey();
		if(notEmpty(gridClusterKey))
		{
			qFilters.add( new Filter(SearchConstants.DP_GRID_CLUSTER_KEY, gridClusterKey, Filter.OP_EQUAL));
		}

        // termHeader
		String termHeader = query.getTermHeader();
		if(notEmpty(termHeader))
		{
			qFilters.add(new Filter(SearchConstants.MP_HEADER, termHeader,Filter.OP_EQUAL));
		}

		// termId
		String termId = query.getTermId();
		if(notEmpty(termId))
		{
			qFilters.add(new Filter(SearchConstants.VOC_TERM_ID, termId,Filter.OP_EQUAL));
		}

        // phenotype entry box
		String phenotypes = query.getPhenotypes();
		if(notEmpty(phenotypes))
		{
			Filter phenoFilter = generateHdpNomenFilter(SearchConstants.VOC_TERM, phenotypes);
			if(phenoFilter != null) qFilters.add(phenoFilter);
		}

        // genes entry box
		String genes = query.getGenes();
		if(notEmpty(genes))
		{
			Filter genesFilter = generateHdpNomenFilter(SearchConstants.MRK_NOMENCLATURE, genes);
			if(genesFilter != null) qFilters.add(genesFilter);
		}

        // location entry box
		String locations = query.getLocations();
		
		// add any file data that may be in the session
		boolean useLocationsFile = usingLocationsQuery(query,session);
		String locationsFileSet = (String) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR);
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
						locationFilters.add(new Filter(coordField,spatialQueryString,Filter.OP_HAS_WORD));
					}
				}
			}
			
			if(locationFilters.size()>0) qFilters.add(Filter.or(locationFilters));
		}
		
		if(hasMarkerKeysFromLocationsFile)
		{
			//logger.debug("making a query with marker keys matched via locations file");
			qFilters.add(new Filter(SearchConstants.MRK_KEY,markerKeysFromLocationsFile,Filter.OP_IN));
		}


		if(qFilters.size()>0)
		{
			return Filter.and(qFilters);
		}
		return new Filter(SearchConstants.PRIMARY_KEY,"###NONE###",Filter.OP_HAS_WORD);
	}

	private boolean notEmpty(String str)
	{ return str!=null && !str.equals(""); }

	private Filter generateHdpNomenFilter(String property, String query){
		//logger.debug("splitting nomenclature query into tokens");
		Collection<String> nomens = QueryParser.parseNomenclatureSearch(query,false,"\"");
		Filter nomenFilter = new Filter();
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
			Filter nFilter = new Filter(property, nomenToken,Filter.OP_HAS_WORD);
			nomenFilters.add(nFilter);
		}

		if(nomenFilters.size() > 0) {
			nomenFilter.setNestedFilters(nomenFilters,Filter.FC_OR);
			// add the nomenclature search filter
			return nomenFilter;
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
		params.setFilter(this.parseQueryForm(query,session));
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
		params.setFilter(this.parseQueryForm(query,session));
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
		params.setFilter(this.parseQueryForm(query,session));
		params.setPageSize(0);
		return hdpFinder.getDiseaseCount(params);
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
		sp.setFilter(new Filter(SearchConstants.DP_GRID_CLUSTER_KEY, gridClusterKey, Filter.OP_EQUAL));
		SearchResults<SolrDpGridCluster> gridClusters = hdpFinder.getGridClusters(sp);
		if(gridClusters.getResultObjects().size()>0)
		{
			SolrDpGridCluster gridCluster = gridClusters.getResultObjects().get(0);
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
		Integer maxLocationsInAQuery = 30000; 
		Set<String> markerKeys = new HashSet<String>();
		if(locationCoordinateTokens!=null && locationCoordinateTokens.size()>0)
		{
			int chunks = (int) Math.ceil(locationCoordinateTokens.size() / maxLocationsInAQuery);
			if(chunks==0) chunks=1;
			for(int i=0; i<chunks; i++)
			{
				// chunk through the list 
				int offsetStart = (i) * maxLocationsInAQuery;
				int offsetEnd = (i+1) * maxLocationsInAQuery;
				if(locationCoordinateTokens.size() < offsetEnd) offsetEnd = locationCoordinateTokens.size();
				List<String> tokens = locationCoordinateTokens.subList(offsetStart,offsetEnd);
				
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
						locationFilters.add(new Filter(coordField,spatialQueryString,Filter.OP_HAS_WORD));
					}
				}
		
				if(locationFilters.size()>0)
				{
					sp.setFilter(Filter.or(locationFilters));
					// query solr for the matching marker keys
					if(chunks==1) return hdpFinder.getMarkerKeys(sp);
					else markerKeys.addAll(hdpFinder.getMarkerKeys(sp));
				}
			}
		}
		return new ArrayList(markerKeys);
	}
	
	private static boolean usingLocationsQuery(DiseasePortalQueryForm query, HttpSession session)
	{
		String locationsFileName = query.getLocationsFileName();
		return locationsFileName!=null && locationsFileName.equals((String) session.getAttribute(DiseasePortalQueryForm.LOCATIONS_FILE_VAR_NAME));
	}

}
