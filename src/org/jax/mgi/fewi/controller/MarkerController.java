package org.jax.mgi.fewi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.DatabaseInfo;
import mgi.frontend.datamodel.HomologyCluster;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerAlleleAssociation;
import mgi.frontend.datamodel.MarkerBiotypeConflict;
import mgi.frontend.datamodel.MarkerCountSetItem;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerIDOtherMarker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerProbeset;
import mgi.frontend.datamodel.MarkerSequenceAssociation;
import mgi.frontend.datamodel.MarkerSynonym;
import mgi.frontend.datamodel.OrganismOrtholog;
import mgi.frontend.datamodel.QueryFormOption;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.SequenceSource;

import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.antlr.BooleanSearch.BooleanSearch;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.QueryFormOptionFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.AlleleQueryForm;
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
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerSummaryRow;
import org.jax.mgi.fewi.util.FewiLinker;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.IDLinker;
import org.jax.mgi.fewi.util.ProviderLinker;
import org.jax.mgi.fewi.util.QueryParser;
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
 * This controller maps all /marker/ uri's
 */
@Controller
@RequestMapping(value="/marker")
public class MarkerController {


    //--------------------//
    // static variables
    //--------------------//

    // maps from marker key to the URL for the minimap image
    private static HashMap<Integer,String> minimaps =  new HashMap<Integer,String>();

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(MarkerController.class);

    @Autowired
    private MarkerFinder markerFinder;

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
    private String genomeBuild = null;
    private String mpHeaders = null;

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // Marker Query Form
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQueryForm() {

        logger.debug("->getQueryForm started");
        initQueryForm();

        ModelAndView mav = new ModelAndView("marker_query");
        mav.addObject("sort", new Paginator());
        mav.addObject("chromosomes", this.chromosomeOptions);
        mav.addObject("htmlMcv", featureTypeHtml);
        mav.addObject("jsonMcv", featureTypeJson);
        mav.addObject("genomeBuild", genomeBuild);
        mav.addObject("queryForm",new MarkerQueryForm());
        return mav;
    }


    //-------------------------//
    // Marker Query Form - phenotype popup
    //-------------------------//
    @RequestMapping("/phenoPopup")
    public ModelAndView phenoPopup (HttpServletRequest request) {
	if (mpHeaders == null) {
	    SearchResults<QueryFormOption> mpResults =
		queryFormOptionFinder.getQueryFormOptions("allele",
		"phenotype");
	    List<QueryFormOption> headers = mpResults.getResultObjects();
	    logger.debug("phenoPopup() received " + headers.size()
		+ " MP header options");

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

    //-------------------------//
    // Marker Query Form Summary
    //-------------------------//
    @RequestMapping("/summary")
    public ModelAndView markerSummary(HttpServletRequest request,
            @ModelAttribute MarkerQueryForm queryForm)
    {
        logger.debug("->markerSummary started");
        logger.debug("queryString: " + request.getQueryString());

        // flag any errors for start and end marker, if specified
        String startMarker = queryForm.getStartMarker();
        String endMarker = queryForm.getEndMarker();
        if(notEmpty(startMarker) || notEmpty(endMarker))
        {
        	if(!notEmpty(startMarker))
        	{
        		return errorMav("start marker not specified");
        	}
        	else if(!notEmpty(endMarker))
        	{
        		return errorMav("end marker not specified");
        	}
        	// query for the start and end markers to check their coordinates and chromosomes
        	// check that we can find the start marker
        	SearchParams sp = new SearchParams();
        	sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,startMarker,Filter.OP_EQUAL));
        	SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);
        	if(sr.getTotalCount()<1)
        	{
        		return errorMav("start marker <b>"+startMarker+"</b> not found");
        	}
        	else if(sr.getTotalCount()>1)
        	{
        		return errorMav("start marker symbol <b>"+startMarker+"</b> matches multiple markers");
        	}
        	SolrSummaryMarker startMarkerObj = sr.getResultObjects().get(0);

        	// check that we can find the end marker
        	sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,endMarker,Filter.OP_EQUAL));
        	sr = markerFinder.getSummaryMarkers(sp);
        	if(sr.getTotalCount()<1)
        	{
        		return errorMav("end marker <b>"+endMarker+"</b> not found");
        	}
        	else if(sr.getTotalCount()>1)
        	{
        		return errorMav("end marker symbol <b>"+endMarker+"</b> matches multiple markers");
        	}
        	SolrSummaryMarker endMarkerObj = sr.getResultObjects().get(0);

        	// check chromosome
        	if(!startMarkerObj.getChromosome().equals(endMarkerObj.getChromosome()))
        	{
        		return errorMav("Marker <b>"+startMarker+"("+startMarkerObj.getChromosome()+")</b> not on same chromosome as " +
        				"<b>"+endMarker+"("+endMarkerObj.getChromosome()+")</b>");
        	}

        	// check that coordinate exist for both
        	if(!notEmpty(startMarkerObj.getCoordStart()) || !notEmpty(startMarkerObj.getCoordEnd()))
        	{
        		return errorMav("start marker <b>"+startMarker+"</b> has no coordinates");
        	}
        	if(!notEmpty(endMarkerObj.getCoordStart()) || !notEmpty(endMarkerObj.getCoordEnd()))
        	{
        		return errorMav("end marker <b>"+endMarker+"</b> has no coordinates");
        	}
        }

        initQueryForm();

        ModelAndView mav = new ModelAndView("marker_summary");
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);

    	mav.addObject("chromosomes", this.chromosomeOptions);
    	mav.addObject("htmlMcv", featureTypeHtml);
    	mav.addObject("jsonMcv", featureTypeJson);
    	mav.addObject("genomeBuild", genomeBuild);

        return mav;
    }

    private ModelAndView errorMav(String msg)
    {
    	ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMsg", msg);
        return mav;
    }

    // Marker Summary (By Refernce) Shell
    //------------------------------------//
    @RequestMapping(value="/reference/key/{refKey}")
    public ModelAndView markerSummeryByRefKey(
          HttpServletRequest request,
          @PathVariable("refKey") String refKey)
    {
        logger.debug("->markerSummeryByRefKey started");

        // setup view object
        ModelAndView mav = new ModelAndView("marker_summary_reference");

		// setup search parameters object to gather the requested marker
        SearchParams referenceSearchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_KEY, refKey);
        referenceSearchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults<Reference> referenceSearchResults = referenceFinder.searchReferences(referenceSearchParams);
        List<Reference> referenceList = referenceSearchResults.getResultObjects();

        // there can be only one...
        if (referenceList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refKey);
            return mav;
        }
        if (referenceList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + refKey);
            return mav;
        }
        Reference reference = referenceList.get(0);

        // prep query form for summary js request
        MarkerQueryForm query = new MarkerQueryForm();
        request.setAttribute("refKey",refKey);

        // package data, and send to view layer
        mav.addObject("reference", reference);
		mav.addObject("queryForm",query);
		mav.addObject("queryString", "refKey=" + refKey);

        logger.debug("markerSummeryByRefKey routing to view ");
		return mav;
    }

    //--------------------//
    // Marker Detail By ID
    //--------------------//
    @RequestMapping(value="/{markerID:.+}", method = RequestMethod.GET)
    public ModelAndView markerDetailByID(@PathVariable("markerID") String markerID) {

        logger.debug("->markerDetailByID started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIdFilter);

        // find the requested marker
        SearchResults searchResults = markerFinder.getMarkerByID(searchParams);
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

	return this.prepareMarker(markerList.get(0));
    }

    /* look up a bunch of extra data and toss it in the MAV; a convenience
     * method for use by the individual methods to render markers by ID or
     * by key
     */
    private ModelAndView prepareMarker(Marker marker) {

	// if this marker is a transgene with only one allele, then we should
	// go to the allele detail page (special case)

	if ("Transgene".equals(marker.getMarkerType()) &&
	    (marker.getCountOfAlleles() == 1)) {
		// need to look up the allele ID for this transgene marker

	    try {
		MarkerAlleleAssociation assoc =
			marker.getAlleleAssociations().get(0);

		String alleleID = assoc.getAllele().getPrimaryID();

		FewiLinker linker = FewiLinker.getInstance();
		ModelAndView mav = new ModelAndView(
			"redirect:" + linker.getFewiIDLink(
				ObjectTypes.ALLELE, alleleID) );
		return mav;
	    } catch (Exception e) {
		ModelAndView mav = new ModelAndView("error");
                mav.addObject("errorMsg",
			"Could not find allele ID for transgene marker");
		return mav;
	    }
	}

	// search engine optimization data
	ArrayList<String> seoKeywords = new ArrayList<String>();
	ArrayList<String> seoDataTypes = new ArrayList<String>();
	StringBuffer seoDescription = new StringBuffer();

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

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("marker_detail");

        // set specific hibernate filters to omit data that does not appear on this page. (for performance)
        sessionFactory.getCurrentSession().enableFilter("markerDetailRefs");

        // add an IDLinker to the mav for use at the JSP level
	idLinker.setup();
        mav.addObject("idLinker", idLinker);

	// add a Properties object with URLs for use at the JSP level
	Properties urls = idLinker.getUrlsAsProperties();
	mav.addObject("urls", urls);

        // add the marker to mav
        mav.addObject("marker", marker);

        this.dbDate(mav);

    // add human homologs to model if present
	OrganismOrtholog mouseOO = marker.getOrganismOrtholog();
	if (mouseOO != null) {
		HomologyCluster homologyCluster = mouseOO.getHomologyCluster();
		if (homologyCluster != null) {
			mav.addObject("homologyClass", homologyCluster);
			seoDataTypes.add("homology");

			OrganismOrtholog humanOO = homologyCluster.getOrganismOrtholog("human");
			if (humanOO != null) {
				List<Marker> humanHomologs = humanOO.getMarkers();
				for(Marker hh : humanHomologs)
				{
					// preload these associations for better hibernate query planning
					hh.getLocations().size();
					hh.getAliases().size();
					hh.getSynonyms().size();
					hh.getBiotypeConflicts().size();
					hh.getIds().size();
				}
				mav.addObject("humanHomologs", humanHomologs);
			}
		}
	}

	// phenotypes keyword needs to come before function

	if (marker.getMPAnnotations().size() > 0) {
	    seoDataTypes.add("phenotypes");
	}

        // We need to pull out the GO terms we want to use as teasers for
        // each ontology.  (This is easier in Java than JSTL, so we do
        // it here.)

        List<mgi.frontend.datamodel.Annotation> pAnnot = this.noDuplicates(
        		marker.getGoProcessAnnotations());
        List<mgi.frontend.datamodel.Annotation> cAnnot = this.noDuplicates(
        		marker.getGoComponentAnnotations());
        List<mgi.frontend.datamodel.Annotation> fAnnot = this.noDuplicates(
        		marker.getGoFunctionAnnotations());

	boolean hasGO = false;

        if (!pAnnot.isEmpty()) {
        	if (pAnnot.size() > 2) {
        		mav.addObject ("processAnnot3", pAnnot.get(2));
        	}
        	if (pAnnot.size() > 1) {
        		mav.addObject ("processAnnot2", pAnnot.get(1));
        	}
       		mav.addObject ("processAnnot1", pAnnot.get(0));
		hasGO = true;
        }

        if (!fAnnot.isEmpty()) {
        	if (fAnnot.size() > 2) {
        		mav.addObject ("functionAnnot3", fAnnot.get(2));
        	}
        	if (fAnnot.size() > 1) {
        		mav.addObject ("functionAnnot2", fAnnot.get(1));
        	}
       		mav.addObject ("functionAnnot1", fAnnot.get(0));
		hasGO = true;
        }

        if (!cAnnot.isEmpty()) {
        	if (cAnnot.size() > 2) {
        		mav.addObject ("componentAnnot3", cAnnot.get(2));
        	}
        	if (cAnnot.size() > 1) {
        		mav.addObject ("componentAnnot2", cAnnot.get(1));
        	}
       		mav.addObject ("componentAnnot1", cAnnot.get(0));
		hasGO = true;
        }

	if (hasGO) {
		seoDataTypes.add("function");
	}

        // need to pull out and re-package the expression counts for assays
        // and results

        Iterator<MarkerCountSetItem> assayIterator =
        	marker.getGxdAssayCountsByType().iterator();
        Iterator<MarkerCountSetItem> resultIterator =
        	marker.getGxdResultCountsByType().iterator();
        MarkerCountSetItem item;

        ArrayList<String> gxdAssayTypes = new ArrayList<String>();
        HashMap<String,String> gxdAssayCounts = new HashMap<String,String>();
        HashMap<String,String> gxdResultCounts = new HashMap<String,String>();

        while (assayIterator.hasNext()) {
        	item = assayIterator.next();
        	gxdAssayTypes.add(item.getCountType());
        	gxdAssayCounts.put(item.getCountType(),
        		Integer.toString(item.getCount()) );
        }
        while (resultIterator.hasNext()) {
        	item = resultIterator.next();
        	gxdResultCounts.put(item.getCountType(),
        		Integer.toString(item.getCount()) );
        }

        mav.addObject ("gxdAssayTypes", gxdAssayTypes);
        mav.addObject ("gxdAssayCounts", gxdAssayCounts);
        mav.addObject ("gxdResultCounts", gxdResultCounts);

	// expression, sequences, polymorphisms, proteins, references keywords

	if ((gxdAssayTypes.size() > 0) ||
		(marker.getCountOfGxdLiterature() > 0)) {
	    seoDataTypes.add("expression");
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

        // pull out the strain/species and provider links for each
        // representative sequence
        mgi.frontend.datamodel.Sequence repGenomic = marker.getRepresentativeGenomicSequence();
        if (repGenomic != null) {
        	List<SequenceSource> genomicSources = repGenomic.getSources();
        	if ((genomicSources != null) && (genomicSources.size() > 0)) {
        		mav.addObject ("genomicSource", genomicSources.get(0).getStrain());
        	}
        	mav.addObject ("genomicLink", ProviderLinker.getSeqProviderLinks(repGenomic));
        }
        mgi.frontend.datamodel.Sequence repTranscript = marker.getRepresentativeTranscriptSequence();
        if (repTranscript != null) {
        	List<SequenceSource> transcriptSources = repTranscript.getSources();
        	if ((transcriptSources != null) && (transcriptSources.size() > 0)) {
        		mav.addObject ("transcriptSource", transcriptSources.get(0).getStrain());
        	}
        	mav.addObject ("transcriptLink", ProviderLinker.getSeqProviderLinks(repTranscript));
        }
        mgi.frontend.datamodel.Sequence repPolypeptide = marker.getRepresentativePolypeptideSequence();
        if (repPolypeptide != null) {
        	List<SequenceSource> polypeptideSources = repPolypeptide.getSources();
        	if ((polypeptideSources != null) && (polypeptideSources.size() > 0)) {
        		mav.addObject ("polypeptideSource", polypeptideSources.get(0).getStrain());
        	}
        	mav.addObject ("polypeptideLink", ProviderLinker.getSeqProviderLinks(repPolypeptide));
        }

        ArrayList<String> qtlIDs = new ArrayList<String>();

        for (MarkerID anId: marker.getIds()) {
        	if (anId.getLogicalDB().equalsIgnoreCase("Download data from the QTL Archive")) {
        		qtlIDs.add(idLinker.getLink(anId));
        	}
        }
        mav.addObject ("qtlIDs", qtlIDs);

        // slice and dice the data for the "Other database links" section, to
        // ease the formatting requirements that would be cumbersome in JSTL
        ArrayList<String> logicalDBs = new ArrayList<String>();
        HashMap<String,String> otherIDs = new HashMap<String,String>();

        Iterator<MarkerID> it = marker.getOtherIDs().iterator();
        MarkerID myID;
        String myLink;
        String logicaldb;
        String otherLinks;
    	MarkerID ncbiEvidenceID = marker.getSingleID("NCBI Gene Model Evidence");
    	boolean isGeneModelID = false;

    	String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");

        while (it.hasNext()) {
        	myID = it.next();
        	logicaldb = myID.getLogicalDB();
        	myLink = idLinker.getLink(myID);

        	// for gene model sequences, we need to add evidence links where possible
        	if ("VEGA Gene Model".equals(logicaldb)) {
        		myLink = myLink + " (" + FormatHelper.setNewWindow(
        				idLinker.getLink("VEGA Gene Model Evidence",
        						myID.getAccID(), "Evidence")) + ")";
        		isGeneModelID = true;
        	} else if ("Ensembl Gene Model".equals(logicaldb)) {
        		myLink = myLink + " (" + FormatHelper.setNewWindow(
        				idLinker.getLink("Ensembl Gene Model Evidence",
        						myID.getAccID(), "Evidence")) + ")";
        		isGeneModelID = true;
        	} else if ("Entrez Gene".equals(logicaldb) && (ncbiEvidenceID != null)) {
        		logger.info(ncbiEvidenceID.getAccID());
           		myLink = myLink + " (" + FormatHelper.setNewWindow(
               			idLinker.getLink("NCBI Gene Model Evidence",
               				myID.getAccID(), "Evidence").replace ("contig=",
               				"contig=" + ncbiEvidenceID.getAccID())) + ")";
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

        				myLink = myLink + "<a href='" + fewiUrl + "marker/"
        					+ otherMarker.getPrimaryID() + "'>"
        					+ otherMarker.getSymbol() + "</a>";

        				if (markerIterator.hasNext()) {
        					myLink = myLink + ", ";
        				}
        			}
        			myLink = myLink + ") ";
        		}
        	}

        	if (!otherIDs.containsKey(logicaldb)) {
        		logicalDBs.add(logicaldb);
        		otherIDs.put(logicaldb, myLink);
        	} else {
        		otherLinks = otherIDs.get(logicaldb);
        		otherIDs.put(logicaldb, otherLinks + ", " + myLink);
        	}
        }

    	String refGenomeUrl = ContextLoader.getConfigBean().getProperty(
		"MGIHOME_URL");

	if (!refGenomeUrl.endsWith("/")) {
		refGenomeUrl = refGenomeUrl + "/";
	}
	refGenomeUrl = refGenomeUrl + "GO/reference_genome_project.shtml";
	mav.addObject ("referenceGenomeURL", refGenomeUrl);

        mav.addObject ("otherIDs", otherIDs);
        mav.addObject ("logicalDBs", logicalDBs);

        // determine if we need a KOMP linkout (complex rules, so better in
        // Java than JSTL); for a link we must have (new per TR10311):
        // 1. has marker type of Pseudogene or Gene
        // 2. has bp coordinates
        // 3. has a strand
        // 4. has at least one sequence

        String markerType = marker.getMarkerType();
       	MarkerLocation location = marker.getPreferredCoordinates();
       	List<MarkerSequenceAssociation> seqs = marker.getSequenceAssociations();
	boolean hadCoords = false;

        if (markerType.equals("Pseudogene") || markerType.equals("Gene")) {
       		if ((location != null) &&
       				(location.getStartCoordinate() != null) &&
       				(location.getEndCoordinate() != null) &&
       				(location.getStrand() != null) &&
       				(seqs != null) && (seqs.size() > 0)	) {
       			mav.addObject ("needKompLink", "yes");
       			otherIDs.put("International Mouse Knockout Project Status",
       				idLinker.getLink("KnockoutMouse", marker.getPrimaryID(),
       						marker.getSymbol()) );
       			logicalDBs.add("International Mouse Knockout Project Status");
			seoDescription.append(" Chr");
			seoDescription.append(location.getChromosome());
			seoDescription.append(":");
			seoDescription.append(location.getStartCoordinate().longValue());
			seoDescription.append("-");
			seoDescription.append(location.getEndCoordinate().longValue());
			hadCoords = true;
       		}
       	}

	if (!hadCoords) {
	    if (!"UN".equals(marker.getChromosome())) {
		seoDescription.append(" Chr");
		seoDescription.append(marker.getChromosome());
	    }
	}

        // links to genome browsers (complex rules so put them here and
        // keep the JSP simple)

	// new simpler rules as of C4AM (coordinates for any marker) project:
	// 1. Any marker with coordinates gets links to all five genome
	//    browsers (VEGA, Ensembl, NCBI, UCSC, GBrowse).  No more
	//    restrictions by marker type.
	// 2. If a marker has multiple IDs, use the first one returned to make
	//    the link.
	// 3. External links go to build 38 (GRCm38) data.
	// 4. For VEGA or Ensembl, use either a VEGA ID or coordinates -- or
	//    both if both are available.
	// 5. NCBI's map viewer needs to use two different URLs for cases
	//    where:
	//	a. there are coordinates and marker is not a dna segment, or
	//	b. there are coordinates and marker is a dna segment.
	// 6. If a marker has coordinates, both link to GBrowse and show a
	//    thumbnail image from GBrowse.
	// 7. The UCSC genome browser (when the marker has coordinates)

        String vegaGenomeBrowserUrl = null;
        String ensemblGenomeBrowserUrl = null;
        String ucscGenomeBrowserUrl = null;
        String ncbiMapViewerUrl = null;
        String gbrowseUrl = null;
        String gbrowseThumbnailUrl = null;

        boolean isDnaSegment = "DNA Segment".equals(markerType);

        String startCoordinate = null;
        String endCoordinate = null;
        String chromosome = null;
	String vegaEnsemblLocation = null;

        MarkerLocation coords = marker.getPreferredCoordinates();
        if (coords != null) {
        	startCoordinate = Long.toString(coords.getStartCoordinate().longValue());
        	endCoordinate = Long.toString(coords.getEndCoordinate().longValue());
        	chromosome = coords.getChromosome();
		vegaEnsemblLocation = chromosome + ":" + startCoordinate + "-" + endCoordinate;
        }

        Properties externalUrls = ContextLoader.getExternalUrls();

	// VEGA Genome Browser
	if (vegaEnsemblLocation != null) {
		vegaGenomeBrowserUrl = externalUrls.getProperty (
			"VEGA_Genome_Browser");

        	MarkerID vegaID = marker.getVegaGeneModelID();

		// plug in VEGA ID, if available
		if (vegaID != null) {
			vegaGenomeBrowserUrl = vegaGenomeBrowserUrl.replace (
				"<id>", vegaID.getAccID());
		} else {
			vegaGenomeBrowserUrl = vegaGenomeBrowserUrl.replace (
				"g=<id>", "");
			vegaGenomeBrowserUrl = vegaGenomeBrowserUrl.replace (
				";", "");
		}

		// plug in coordinates, if available
		vegaGenomeBrowserUrl = vegaGenomeBrowserUrl.replace (
			"<location>", vegaEnsemblLocation);
	}

	// Ensembl Genome Browser
	if (vegaEnsemblLocation != null) {
		ensemblGenomeBrowserUrl = externalUrls.getProperty (
			"Ensembl_Genome_Browser");

        	MarkerID ensemblGmID = marker.getEnsemblGeneModelID();

		// plug in Ensembl gene model ID, if available
		if (ensemblGmID != null) {
			ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace (
				"<id>", ensemblGmID.getAccID());
		} else {
			ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace (
				"g=<id>", "");
			ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace (
				";", "");
		}

		// plug in coordinates, if available
		ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replace (
			"<location>", vegaEnsemblLocation);
	}

	// NCBI Map Viewer (2 separate URLs -- see notes above)
	if ((coords != null) && !isDnaSegment) {
		ncbiMapViewerUrl = externalUrls.getProperty (
			"NCBI_Map_Viewer_by_Coordinates");
		ncbiMapViewerUrl = ncbiMapViewerUrl.replace (
			"<chromosome>", chromosome).replace (
			"<start>", startCoordinate).replace (
			"<end>", endCoordinate);

	} else if ((coords != null) && isDnaSegment) {
		ncbiMapViewerUrl = externalUrls.getProperty (
			"NCBI_Map_Viewer_by_Coordinates_DNA_Segment");
		ncbiMapViewerUrl = ncbiMapViewerUrl.replace (
			"<chromosome>", chromosome).replace (
			"<start>", startCoordinate).replace (
			"<end>", endCoordinate);
	}

	// GBrowse
	if (coords != null) {
		gbrowseUrl = externalUrls.getProperty(
		"GBrowse").replace(
		"<chromosome>", chromosome).replace(
		"<start>", startCoordinate).replace(
		"<end>", endCoordinate);

		gbrowseThumbnailUrl = externalUrls.getProperty(
		"GBrowse_Thumbnail").replace(
		"<chromosome>", chromosome).replace(
		"<start>", startCoordinate).replace(
		"<end>", endCoordinate);

		// add tracks for special marker "types"
		if(marker.getIsSTS())
		{
			gbrowseUrl = gbrowseUrl.replace("label=","label=STS-");
			gbrowseThumbnailUrl = gbrowseThumbnailUrl.replace("t=","t=STS;t=");
		}
	}

	// UCSC Genome Browser
	if (coords != null) {
		ucscGenomeBrowserUrl = externalUrls.getProperty(
			"UCSC_Genome_Browser").replace(
			"<chromosome>", chromosome).replace(
			"<start>", startCoordinate).replace(
			"<end>", endCoordinate);
	}

        // whichever genome browser URLs we found, fill them in the mav

        if (vegaGenomeBrowserUrl != null) {
        	mav.addObject ("vegaGenomeBrowserUrl", vegaGenomeBrowserUrl);
        }
        if (ensemblGenomeBrowserUrl != null) {
        	mav.addObject ("ensemblGenomeBrowserUrl", ensemblGenomeBrowserUrl);
        }
        if (ucscGenomeBrowserUrl != null) {
        	mav.addObject ("ucscGenomeBrowserUrl", ucscGenomeBrowserUrl);
        }
        if (ncbiMapViewerUrl != null) {
        	mav.addObject ("ncbiMapViewerUrl", ncbiMapViewerUrl);
        }
        if (gbrowseUrl != null) {
        	mav.addObject ("gbrowseUrl", gbrowseUrl);
        }
        if (gbrowseThumbnailUrl != null) {
        	mav.addObject ("gbrowseThumbnailUrl", gbrowseThumbnailUrl);
        }

        // add data for biotype conflicts

        List<MarkerBiotypeConflict> conflicts = marker.getBiotypeConflicts();
        if ((conflicts != null) && (conflicts.size() > 0)) {
        	StringBuffer conflictTable = new StringBuffer();
        	conflictTable.append ("<table class=bioMismatch>");
        	conflictTable.append ("<tr class=header><td>Source</td><td>BioType</td><td>Gene ID</td></tr>");
        	conflictTable.append ("<tr><td>MGI</td><td>" + marker.getMarkerSubtype() + "</td><td>"
        		+ "<a href=" + fewiUrl + "marker/" + marker.getPrimaryID() + ">" + marker.getPrimaryID() + "</a></td></tr>");
            for (MarkerBiotypeConflict conflict : conflicts) {
            	conflictTable.append ("<tr>");
            	conflictTable.append ("<td>" + conflict.getLogicalDB() + "</td>");
            	conflictTable.append ("<td>" + conflict.getBiotype() + "</td>");
            	conflictTable.append ("<td>" + idLinker.getLink(conflict.getLogicalDB(), conflict.getAccID()).replaceAll("'", "") + "</td>");
            	conflictTable.append ("</tr>");
            }
            conflictTable.append ("</table>");
            mav.addObject ("biotypeConflictTable", conflictTable.toString());
        }

        // add data for strain-specific markers

        String ssNote = marker.getStrainSpecificNote();
        if (ssNote != null) {
        	List<Reference> ssRefs = marker.getStrainSpecificReferences();
        	boolean isFirst = true;

        	if ((ssRefs != null) && (ssRefs.size() > 0)) {
        		ssNote = ssNote + "(";
        		for (Reference ref : ssRefs) {
        			if (!isFirst) { ssNote = ssNote + ", "; }
        			else { isFirst = false; }

        			ssNote = ssNote + "<a href=" + fewiUrl + "reference/" + ref.getJnumID() + " target=_new>"
        				+ ref.getJnumID() + "</a>";
        		}
        		ssNote = ssNote + ")";
        	}
        	mav.addObject ("strainSpecificNote", ssNote);
        }

	// if we have not yet looked up the full suite of marker minimaps that
	// have already been generated, then do so

	if (minimaps.size() == 0) {
	    String returnedString = this.getAllMinimapUrls();
	    String[] allMinimaps = returnedString.split("\n");

	    logger.debug ("Got " + allMinimaps.length + " minimap URL lines");

	    Pattern findKey = Pattern.compile("@([0-9]+).gif");
	    Matcher matcher = null;

	    for (String url : allMinimaps) {
		    matcher = findKey.matcher(url);
		    if (matcher.find()) {
			minimaps.put (new Integer(matcher.group(1)), url);
		    }
	    }
	}

	// if we already know what the minimap URL needs to be for this
	// marker, then just put it in.

	String minimapUrl = null;
	Integer markerKey = new Integer(marker.getMarkerKey());

	if (minimaps.containsKey(markerKey)) {
	    minimapUrl = minimaps.get(markerKey);
	} else {
	    // otherwise, if this marker has a cM location, we can request the
	    // URL for its minimap individually

            MarkerLocation cmPos = marker.getPreferredCentimorgans();
	    if (cmPos != null) {
		if (!cmPos.getChromosome().equalsIgnoreCase("UN")) {
        	    Float cM = cmPos.getCmOffset();
		    if ((cM != null) && (cM.floatValue() >= 0.0)) {
       			minimapUrl = this.getMinimapUrl(markerKey);
			minimaps.put (markerKey, minimapUrl);
        	    }
        	}
            }
	}

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

	// finally, add the minimap URL to the mav

	if (minimapUrl != null) {
	    mav.addObject ("miniMap", minimapUrl);
	}

        return mav;
    }


    //--------------------//
    // Marker Detail By Key
    //--------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView markerDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->markerDetailByKey started");

        // find the requested marker
        SearchResults searchResults = markerFinder.getMarkerByKey(dbKey);
        List<Marker> markerList = searchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        }// success

	return this.prepareMarker(markerList.get(0));
    }


    //-------------------------------//
    // Marker Summary by Reference
    //-------------------------------//
    @RequestMapping(value="/reference/{refID}")
    public ModelAndView markerSummeryByRef(@PathVariable("refID") String refID) {

        logger.debug("->markerSummeryByRef started");

        ModelAndView mav = new ModelAndView("marker_summary_reference");

        // setup search parameters object to gather the requested object
        SearchParams searchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults searchResults
          = referenceFinder.searchReferences(searchParams);
        List<Reference> refList = searchResults.getResultObjects();

        // there can be only one...
        if (refList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refID);
            return mav;
        }
        if (refList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe references found for " + refID);
            return mav;
        }

        // pull out the reference, and place into the mav
        Reference reference = refList.get(0);
        mav.addObject("reference", reference);

        // pre-generate query string
        mav.addObject("queryString", "refKey=" + reference.getReferenceKey());

        return mav;
    }


    //-------------------------------------//
    // Marker's Microarray Probeset Summary
    //-------------------------------------//
    @RequestMapping(value="/probeset/{markerID}")
    public ModelAndView markerProbesets(@PathVariable("markerID") String markerID) {

        logger.debug("->markerProbesets started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
        searchParams.setFilter(markerIdFilter);

        // find the requested marker
        SearchResults searchResults = markerFinder.getMarkerByID(searchParams);
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

	ArrayList<String> reportList = new ArrayList(reports.keySet());
	Collections.sort(reportList);

	mav.addObject("reportsOrdered", reportList);
	mav.addObject("reports", reports);

	// add the date
        this.dbDate(mav);

	return mav;
    }


    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<MarkerSummaryRow> seqSummaryJson(
            HttpServletRequest request,
			@ModelAttribute MarkerQueryForm query,
            @ModelAttribute Paginator page) throws org.antlr.runtime.RecognitionException
            {
        logger.debug("->JsonSummaryResponse started");


        SearchResults<SolrSummaryMarker> searchResults = getSummaryMarkers(request,query,page);
        List<SolrSummaryMarker> markerList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<MarkerSummaryRow> summaryRows = new ArrayList<MarkerSummaryRow>();
        for(SolrSummaryMarker marker : markerList)
        {
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
    public SearchResults<SolrSummaryMarker> getSummaryMarkers(HttpServletRequest request,
			MarkerQueryForm query,
            Paginator page) throws org.antlr.runtime.RecognitionException
    {
    	// parameter parsing
        String refKey = request.getParameter("refKey");

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        // if we have nomen query, do text highlighting
        if(notEmpty(query.getNomen()) || notEmpty(query.getGo()))
        {
	        params.setIncludeMetaHighlight(true);
	        params.setIncludeSetMeta(true);
        }
        params.setSorts(this.genSorts(request,query));
        params.setFilter(this.genFilters(query));

        if (refKey != null) {
            params.setFilter(new Filter(SearchConstants.REF_KEY, refKey));
        }

        // perform query, and pull out the requested objects
        return markerFinder.getSummaryMarkers(params);
    }

	//--------------------------------//
	// Marker Summary Reports
	//--------------------------------//
	@RequestMapping("/report*")
	public ModelAndView markerSummaryExport(
            HttpServletRequest request,
			@ModelAttribute MarkerQueryForm query) throws org.antlr.runtime.RecognitionException
	{

    	logger.debug("generating report");

        SearchParams sp = new SearchParams();
        sp.setPageSize(250000);
        sp.setFilter(this.genFilters(query));

        // if we have nomen query, do text highlighting
        if(notEmpty(query.getNomen()))
        {
	        sp.setIncludeMetaHighlight(true);
	        sp.setIncludeSetMeta(true);
	        sp.setIncludeHighlightMarkup(false);
        }

        List<Sort> sorts = new ArrayList<Sort>();
        SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);

        List<SolrSummaryMarker> markers = sr.getResultObjects();

		ModelAndView mav = new ModelAndView("markerSummaryReport");
		mav.addObject("markers", markers);
		return mav;

    }


    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    private void dbDate(ModelAndView mav) {
        List<DatabaseInfo> dbInfo = dbInfoFinder.getInfo(new SearchParams()).getResultObjects();
        for (DatabaseInfo db: dbInfo) {
        	if (db.getName().equalsIgnoreCase("built from mgd database date")){
        		DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        		try {
					mav.addObject("databaseDate", df.parse(db.getValue()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }

    /** get a String containing URLs to all currently rendered minimaps; they
     * will be delimited by line-breaks
     */
    private String getAllMinimapUrls() {
	return this.getMinimapUrl("all");
    }

    /** get a String containing the URL to the minimap for the marker with the
     * given markerKey
     */
    private String getMinimapUrl(Integer markerKey){
	return this.getMinimapUrl(markerKey.toString());
    }

    /** get a String containing one or more URLs for minimaps.  If parm
     * contains an integer, then it will be the URL for a single marker.  If
     * parm is "all", then it will be a line break-delimited string containing
     * the URLs for all minimaps currently rendered.
     */
    private String getMinimapUrl(String parm){
		StringBuffer urlString = new StringBuffer();
		logger.debug("get minimap: " + parm);
		try {
			URL url = new URL(ContextLoader.getConfigBean().getProperty("WI_URL") +
					"searches/markerMiniMap.cgi?" + parm);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection connection = null;

			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String current;
				current = in.readLine();
				while (current != null) {
					if (urlString.length() > 0) {
					    urlString.append("\n");
					}
					urlString.append(current);
					current = in.readLine();
				}
				in.close();
				connection.disconnect();
			} else {
				logger.debug("miniMap URL not an HTTP URL.");
			}
		} catch (MalformedURLException mue) {
			logger.error(mue.getMessage());
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		}
		logger.debug("done");
		return urlString.toString();
    }

    // generate the sorts
    private List<Sort> genSorts(HttpServletRequest request,MarkerQueryForm query)
    {
        logger.debug("->genSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");

        String dirRequested  = request.getParameter("dir");
        boolean desc = false;
        if("desc".equalsIgnoreCase(dirRequested)){
            desc = true;
        }

        if("symbol".equalsIgnoreCase(sortRequested))
        {
        	sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL, desc));
        }
        else if("location".equalsIgnoreCase(sortRequested))
        {
        	sorts.add(new Sort(SortConstants.MRK_BY_LOCATION, desc));
        }
        else if(notEmpty(query.getNomen()))
        {
        	sorts.add(new Sort("score",true));
        	sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL,false));
        }
        else
        {
        	// default is by symbol
        	sorts.add(new Sort(SortConstants.MRK_BY_SYMBOL,false));
        }
        return sorts;
    }

    // generate the filters
    private Filter genFilters(MarkerQueryForm query) throws org.antlr.runtime.RecognitionException
    {
        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);

        // start filter list to add filters to
        List<Filter> queryFilters = new ArrayList<Filter>();

        String nomen = query.getNomen();
        if(notEmpty(nomen))
        {
        	 Filter nomenFilter = FilterUtil.generateNomenFilter(SearchConstants.MRK_NOMENCLATURE,nomen);
			 if(nomenFilter!=null) queryFilters.add(nomenFilter);
        }

        // Phenotypes
		 String phenotype = query.getPhenotype();
		 if(notEmpty(phenotype))
		 {
			 queryFilters.add(BooleanSearch.buildSolrFilter(SearchConstants.PHENOTYPE,phenotype));
		 }

		 //InterPro
		 String interpro = query.getInterpro();
		 if(notEmpty(interpro))
		 {
			 List<Filter> interproFilters = new ArrayList<Filter>();

			 for(String token : QueryParser.tokeniseOnWhitespaceAndComma(interpro))
			 {
				 interproFilters.add(new Filter(SearchConstants.INTERPRO_TERM,token,Filter.OP_STRING_CONTAINS));
			 }
			 queryFilters.add(Filter.and(interproFilters));
		 }

		 //GO
		 String go = query.getGo();
		 if(notEmpty(go))
		 {
			 List<String> goVocabs = query.getGoVocab();
			 List<Filter> goVocabFilters = new ArrayList<Filter>();
			 if(!(notEmpty(goVocabs) && goVocabs.size()<3))
			 {
				 goVocabs = Arrays.asList(SearchConstants.GO_TERM);
			 }
			 for(String goVocab : goVocabs)
			 {
				 List<Filter> containsFilters = new ArrayList<Filter>();
				 for(String token : QueryParser.tokeniseOnWhitespaceAndComma(go))
				 {
					 containsFilters.add(BooleanSearch.buildSolrFilter(goVocab,token));
					 //containsFilters.add(new Filter(goVocab,token,Filter.OP_STRING_CONTAINS));
				 }
				 // AND the contains search tokens
				 goVocabFilters.add(Filter.and(containsFilters));
			 }
			 // OR the query against each vocabulary (e.g. function,process,component)
			 queryFilters.add(Filter.or(goVocabFilters));
		 }

        // feature type
        Filter featureTypeFilter = makeListFilter(query.getFeatureType(),SearchConstants.FEATURE_TYPE);
		if(featureTypeFilter!=null) queryFilters.add(featureTypeFilter);

		Filter mcvFilter = makeListFilter(query.getMcv(),SearchConstants.FEATURE_TYPE_KEY);
		if(mcvFilter!=null) queryFilters.add(mcvFilter);

        // Chromosome
		 List<String> chr = query.getChromosome();
		 if(notEmpty(chr) && !chr.contains(AlleleQueryForm.COORDINATE_ANY))
		 {
			 Filter chrFilter = makeListFilter(chr,SearchConstants.CHROMOSOME);
			 if(chrFilter!=null) queryFilters.add(chrFilter);
		 }

		 // Coordinate Search
		 String coord = query.getCoordinate();
		 String coordUnit = query.getCoordUnit();
		 if(notEmpty(coord))
		 {
			 Filter coordFilter = FilterUtil.genCoordFilter(coord,coordUnit);
			 if(coordFilter==null) coordFilter = nullFilter();
			 queryFilters.add(coordFilter);
		 }

		 // CM Search
		 String cm = query.getCm();
		 if(notEmpty(cm))
		 {
			 Filter cmFilter = FilterUtil.genCmFilter(cm);
			 if(cmFilter==null) cmFilter = nullFilter();
			 queryFilters.add(cmFilter);
		 }

		String startMarker = query.getStartMarker();
        String endMarker = query.getEndMarker();
        if(notEmpty(startMarker) && notEmpty(endMarker))
        {
        	// query for the start and end markers to check their coordinates and chromosomes
        	// check that we can find the start marker
        	SearchParams sp = new SearchParams();
        	sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,startMarker,Filter.OP_EQUAL));
        	SearchResults<SolrSummaryMarker> sr = markerFinder.getSummaryMarkers(sp);
        	if(sr.getTotalCount()>0)
        	{
        		SolrSummaryMarker startMarkerObj = sr.getResultObjects().get(0);
        		// check that we can find the end marker
            	sp.setFilter(new Filter(SearchConstants.MRK_SYMBOL,endMarker,Filter.OP_EQUAL));
            	sr = markerFinder.getSummaryMarkers(sp);
            	if(sr.getTotalCount()>0)
            	{
            		SolrSummaryMarker endMarkerObj = sr.getResultObjects().get(0);

            		if(notEmpty(startMarkerObj.getChromosome())
            				&& notEmpty(startMarkerObj.getCoordStart())
            				&& notEmpty(startMarkerObj.getCoordEnd())
            				&& notEmpty(endMarkerObj.getChromosome())
            				&& notEmpty(endMarkerObj.getCoordStart())
            				&& notEmpty(endMarkerObj.getCoordEnd()))
            		{
            			Integer startCoord = Math.min(startMarkerObj.getCoordStart(),endMarkerObj.getCoordStart());
            			Integer endCoord = Math.max(startMarkerObj.getCoordEnd(),endMarkerObj.getCoordEnd());

            			String coordString = startCoord+"-"+endCoord;
            			logger.info("build coord string from markers "+startMarker+" to "+endMarker+" = "+coordString);
            			Filter chromosomeFilter = new Filter(SearchConstants.CHROMOSOME,startMarkerObj.getChromosome(),Filter.OP_EQUAL);
            			Filter coordFilter = FilterUtil.genCoordFilter(coordString,"bp");
            			if(coordFilter==null) coordFilter = nullFilter();
            			queryFilters.add(Filter.and(Arrays.asList(chromosomeFilter,coordFilter)));
            		}
            	}
        	}
        }

        if(queryFilters.size()<1) return nullFilter();
        return Filter.and(queryFilters);
    }

    // returns a filter that should always fail to retrieve results
    private Filter nullFilter()
    {
    	return new Filter("markerKey","-99999",Filter.OP_EQUAL);
    }

    /** return a List comparable to 'annotations' but with the duplicate
     * terms stripped out.  Also strip out any annotations with a NOT
     * qualifier.
     */
    private List<mgi.frontend.datamodel.Annotation> noDuplicates (
    		List<mgi.frontend.datamodel.Annotation> annotations) {

    	// the list we will compose to be returned
    	ArrayList<mgi.frontend.datamodel.Annotation> a = new
    		ArrayList<mgi.frontend.datamodel.Annotation>();

    	// iterates over the input list
    	Iterator<mgi.frontend.datamodel.Annotation> it = annotations.iterator();

    	// tracks which terms we've seen already
    	HashMap<String, String> done = new HashMap<String, String>();

    	// which term we are looking at currently
    	mgi.frontend.datamodel.Annotation annot;

    	while (it.hasNext()) {
    		annot = it.next();
    		if (!done.containsKey(annot.getTerm())) {
    			// if we have no qualifier or we have a qualifier that's
    			// not "NOT" then we ca use this term
    			if ((annot.getQualifier() == null) ||
    				(!annot.getQualifier().toLowerCase().equals("not"))) {
    					done.put (annot.getTerm(), "");
    					a.add (annot);
    			}
    		}
    	}
    	done = new HashMap<String, String>();
    	return a;
    }

    /** force the cache of minimap URLs to be cleared, allowing it to be
     * repopulated from scratch
     */
    protected static void clearMinimapCache() {
		minimaps = new HashMap<Integer,String>();
		return;
    }

    /** report how many minimap URLs are currently cacahed
     */
    protected static int getMinimapCacheCount() {
	return minimaps.size();
    }

    private Filter makeListFilter(List<String> values, String searchConstant)
    {
    	Filter f = null;
   		if(values!=null && values.size()>0)
   		{
   			 List<Filter> vFilters = new ArrayList<Filter>();
   			 for(String value : values)
   			 {
   				vFilters.add(new Filter(searchConstant,value,Filter.OP_EQUAL));
   			 }
   			 f = Filter.or(vFilters);
   		}
   		return f;
    }


    private void initQueryForm(List<QueryFormOption> options)
    {
    	if(!MarkerQueryForm.initialized)
    	{
    		// do stuff
    		Map<String,String> mcvToDisplay = new HashMap<String,String>();
    		for(QueryFormOption option : options)
    		{
    			mcvToDisplay.put(option.getSubmitValue(),option.getDisplayValue());
    		}
    		MarkerQueryForm.setMarkerTypeKeyToDisplayMap(mcvToDisplay);
    	}
    }
    /* For automated test access */
    public void initQueryForm()
    {
    	/* if we don't have a cached version of the chromosome options (for
    	 * the selction list), then we need to pull them out of the database,
    	 * generate it, and cache it
    	 */
    	if (this.chromosomeOptions == null) {
    	    SearchResults<QueryFormOption> chrResults = queryFormOptionFinder.getQueryFormOptions("marker","chromosome");
    	    List<QueryFormOption> chromosomes = chrResults.getResultObjects();

    	    this.chromosomeOptions = new LinkedHashMap<String,String>();
    	    this.chromosomeOptions.put(MarkerQueryForm.CHROMOSOME_ANY,"Any");
    	    for (QueryFormOption chromosome : chromosomes)
    	    {
    	    	this.chromosomeOptions.put(chromosome.getSubmitValue(),chromosome.getDisplayValue());
    	    }
    	}

    	/* if we don't have a cached version of the feature type options (for
    	 * the feature type section, then we need to pull them out of the
    	 * database, generate it, and cache it
    	 */
    	if (featureTypeHtml == null) {
    	    SearchResults<QueryFormOption> mtResults =
    		queryFormOptionFinder.getQueryFormOptions("marker", "mcv");
    	    List<QueryFormOption> markerTypes = mtResults.getResultObjects();
    	    logger.debug("getQueryForm() received " + markerTypes.size()
    		+ " Feature Type options");

    	    featureTypeHtml = FormatHelper.buildHtmlTree(markerTypes);
    	    featureTypeJson = FormatHelper.buildJsonTree(markerTypes);

    	    initQueryForm(markerTypes);
    	}

    	// if we don't have a cached version of the build number, retrieve it
    	// and cache it
    	if (genomeBuild == null) {
    	    SearchResults<QueryFormOption> gbResults =
    		queryFormOptionFinder.getQueryFormOptions("marker",
    		"build_number");
    	    List<QueryFormOption> genomeBuilds = gbResults.getResultObjects();
    	    logger.debug("getQueryForm() received " + genomeBuilds.size()
    		+ " Genome Build options");

    	    if (genomeBuilds.size() > 0) {
    		genomeBuild = genomeBuilds.get(0).getDisplayValue();
    	    }
    	}
//    	if(!MarkerQueryForm.initialized)
//    	{
//    		SearchResults<QueryFormOption> mtResults = queryFormOptionFinder.getQueryFormOptions("marker", "mcv");
//    		initQueryForm(mtResults.getResultObjects());
//    	}
    }
    private boolean notEmpty(String s)
    {
    	return s!=null && !s.equals("");
    }
    private boolean notEmpty(Integer i)
    {
    	return i!=null && i>0;
    }
    private boolean notEmpty(List l)
    {
    	return l!=null && l.size()>0;
    }
}
