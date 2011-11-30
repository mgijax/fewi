package org.jax.mgi.fewi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.DatabaseInfo;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerBiotypeConflict;
import mgi.frontend.datamodel.MarkerCountSetItem;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.MarkerIDOtherMarker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerOrthology;
import mgi.frontend.datamodel.MarkerSequenceAssociation;
import mgi.frontend.datamodel.MarkerSynonym;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.SequenceSource;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.FooQueryForm;
import org.jax.mgi.fewi.forms.MarkerQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerSummaryRow;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.IDLinker;
import org.jax.mgi.fewi.util.ProviderLinker;
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
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(MarkerController.class);

    @Autowired
    private MarkerFinder MarkerFinder;
    
    @Autowired
    private DbInfoFinder dbInfoFinder;

    @Autowired
    private ReferenceFinder referenceFinder;


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // Marker Query Form
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQueryForm() {

        logger.debug("->getQueryForm started");

        ModelAndView mav = new ModelAndView("marker_query");
        mav.addObject("sort", new Paginator());
        mav.addObject(new MarkerQueryForm());
        return mav;
    }


    //-------------------------//
    // Marker Query Form Summary
    //-------------------------//
    @RequestMapping("/summary")
    public ModelAndView markerSummary(HttpServletRequest request,
            @ModelAttribute FooQueryForm queryForm) {

        logger.debug("->markerSummary started");
        logger.debug("queryString: " + request.getQueryString());

        ModelAndView mav = new ModelAndView("marker_summary");
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("queryForm", queryForm);

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
        SearchResults searchResults
          = MarkerFinder.getMarkerByID(searchParams);
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
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("marker_detail");

        // add an IDLinker to the mav for use at the JSP level
        IDLinker idLinker = ContextLoader.getIDLinker();
        mav.addObject("idLinker", idLinker);
        
        //pull out the marker, and add to mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);
        
        this.dbDate(mav);
        
        // add human ortholog to model if present
        Marker humanOrtholog = null;
        if (marker.getOrthologousMarkers().size() > 0){
        	for (MarkerOrthology mo: marker.getOrthologousMarkers()) {
        		if (mo.getOtherOrganism().equalsIgnoreCase("human")){
        	        SearchResults<Marker> orthalogResults
        	        	= MarkerFinder.getMarkerByKey(String.valueOf(mo.getOtherMarkerKey()));
        	        if (orthalogResults.getResultObjects().size() > 0) {
        	        	humanOrtholog = orthalogResults.getResultObjects().get(0);
        	        	if (humanOrtholog.getSynonyms().size() > 0) {
        	        		List<String> humanSynonyms = new ArrayList<String>();
        	        		for (MarkerSynonym syn: humanOrtholog.getSynonyms()){
        	        			humanSynonyms.add(syn.getSynonym());
        	        		}
        	        		mav.addObject("humanSynonyms", humanSynonyms.toArray(new String[humanSynonyms.size()]));
        	        	}
        	        	mav.addObject("humanOrtholog", humanOrtholog);
        	        }       			
        		}
        	}
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
        	
        if (!pAnnot.isEmpty()) {
        	if (pAnnot.size() > 2) {
        		mav.addObject ("processAnnot3", pAnnot.get(2));
        	}
        	if (pAnnot.size() > 1) {
        		mav.addObject ("processAnnot2", pAnnot.get(1));
        	}
       		mav.addObject ("processAnnot1", pAnnot.get(0));
        }

        if (!fAnnot.isEmpty()) {
        	if (fAnnot.size() > 2) {
        		mav.addObject ("functionAnnot3", fAnnot.get(2));
        	}
        	if (fAnnot.size() > 1) {
        		mav.addObject ("functionAnnot2", fAnnot.get(1));
        	}
       		mav.addObject ("functionAnnot1", fAnnot.get(0));
        }
        
        if (!cAnnot.isEmpty()) {
        	if (cAnnot.size() > 2) {
        		mav.addObject ("componentAnnot3", cAnnot.get(2));
        	}
        	if (cAnnot.size() > 1) {
        		mav.addObject ("componentAnnot2", cAnnot.get(1));
        	}
       		mav.addObject ("componentAnnot1", cAnnot.get(0));
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
           		myLink = myLink + " (" + FormatHelper.setNewWindow( 
           			idLinker.getLink("NCBI Gene Model Evidence", 
           				myID.getAccID(), "Evidence").replace ("^^^^",
           				ncbiEvidenceID.getAccID())) + ")";
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
       		}
       	}
        
        // links to genome browsers (complex rules so put them here and
        // keep the JSP simple)
        
        String vegaGenomeBrowserUrl = null;
        String ensemblGenomeBrowserUrl = null;
        String ucscGenomeBrowserUrl = null;
        String ncbiMapViewerUrl = null;
        String gbrowseUrl = null;
        String gbrowseThumbnailUrl = null;
        
        MarkerID vegaID = marker.getVegaGeneModelID();
        MarkerID ensemblID = marker.getEnsemblGeneModelID();
        MarkerID refseqID = marker.getRefSeqID();
        MarkerID entrezGeneID = marker.getEntrezGeneID();
        MarkerLocation coords = marker.getPreferredCoordinates();
        
        boolean isGene = markerType.equals("Gene");
        boolean isPseudogene = markerType.equals("Pseudogene");
        boolean isQTL = markerType.equals("QTL");
        boolean isMiRNA = markerType.equals("miRNA");
        boolean isMITMarker = false;
        
        String startCoordinate = null;
        String endCoordinate = null;
        String chromosome = null;
        
        if (coords != null) {
        	startCoordinate = Long.toString(coords.getStartCoordinate().longValue());
        	endCoordinate = Long.toString(coords.getEndCoordinate().longValue());
        	chromosome = coords.getChromosome();
        }
        
        if ((coords != null) && "DNA Segment".equals(markerType)) {
        	if ("UniSTS".equals(coords.getProvider())) {
        		isMITMarker = true;
        	}
        }
        
        Properties externalUrls = ContextLoader.getExternalUrls();
        
        // only genes & pseudogenes with a Vega Gene Model ID get a Vega link
        if (isGene || isPseudogene) {
        	if (vegaID != null) {
        		vegaGenomeBrowserUrl = externalUrls.getProperty(
        			"VEGA_Genome_Browser").replace("@@@@", vegaID.getAccID());
        	}
        }
        
       	// Ensembl Genome Browser -- prefer Ensembl ID for genes, then
        // coordinates for several marker types, then RefSeq ID for genes
        if (isGene && (ensemblID != null)) {
       		ensemblGenomeBrowserUrl = externalUrls.getProperty(
       			"Ensembl_Genome_Browser").replace("@@@@", ensemblID.getAccID());
        } else if ((isGene || isPseudogene || isQTL || isMiRNA || isMITMarker) && (coords != null)) {
       		ensemblGenomeBrowserUrl = externalUrls.getProperty("Ensembl_Genome_Browser").replace("g=@@@@", "r=@@@@:@@@@-@@@@");
       		ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replaceFirst("@@@@", chromosome);
       		ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replaceFirst("@@@@", startCoordinate);
       		ensemblGenomeBrowserUrl = ensemblGenomeBrowserUrl.replaceFirst("@@@@", endCoordinate);
        } else if (isGene && (refseqID != null)) {
       		ensemblGenomeBrowserUrl = externalUrls.getProperty(
       			"Ensembl_Genome_Browser").replace("@@@@", refseqID.getAccID());
        }
        
        // UCSC Genome Browser -- prefer coordinates, then RefSeq ID for genes
        // genes and pseudogenes share a URL; the others are different
        if ((isGene || isPseudogene || isQTL || isMiRNA || isMITMarker) && (coords != null)) {
        	if (isGene || isPseudogene) {
        		ucscGenomeBrowserUrl = externalUrls.getProperty("UCSC_Genome_Browser");
        	} else if (isQTL) {
        		ucscGenomeBrowserUrl = externalUrls.getProperty("UCSC_Genome_Browser_QTL");
        	} else if (isMiRNA) {
        		ucscGenomeBrowserUrl = externalUrls.getProperty("UCSC_Genome_Browser_miRNA");
        	} else if (isMITMarker) {
        		ucscGenomeBrowserUrl = externalUrls.getProperty("UCSC_Genome_Browser_MIT");
        	}
       		ucscGenomeBrowserUrl = ucscGenomeBrowserUrl.replaceFirst("@@@@", chromosome);
       		ucscGenomeBrowserUrl = ucscGenomeBrowserUrl.replaceFirst("@@@@", startCoordinate);
       		ucscGenomeBrowserUrl = ucscGenomeBrowserUrl.replaceFirst("@@@@", endCoordinate);
        } else if (isGene && (refseqID != null)) {
        	ucscGenomeBrowserUrl = externalUrls.getProperty("UCSC_Genome_Browser_RefSeq").replace("@@@@", refseqID.getAccID());
        }
        
        // NCBI Map Viewer -- prefer Entrez IDs for genes and pseudogenes,
        // then coordinates (except for miRNAs), then RefSeq IDs for genes
        if ((isGene || isPseudogene) && (entrezGeneID != null)) {
       		ncbiMapViewerUrl = externalUrls.getProperty(
       			"NCBI_Map_Viewer_by_Entrez").replace("@@@@", entrezGeneID.getAccID());
        } else if ((isGene || isPseudogene) && (coords != null)) {
       		ncbiMapViewerUrl = externalUrls.getProperty("NCBI_Map_Viewer_by_Coordinates");
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", chromosome);
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", startCoordinate);
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", endCoordinate);
        } else if (isQTL && (coords != null)) {
       		ncbiMapViewerUrl = externalUrls.getProperty("NCBI_Map_Viewer_by_Coordinates_QTL");
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", chromosome);
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", startCoordinate);
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", endCoordinate);
        } else if (isMITMarker && (coords != null)) {
       		ncbiMapViewerUrl = externalUrls.getProperty("NCBI_Map_Viewer_by_Coordinates_MIT");
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", chromosome);
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", startCoordinate);
       		ncbiMapViewerUrl = ncbiMapViewerUrl.replaceFirst("@@@@", endCoordinate);
        } else if (isGene && (refseqID != null)) {
       		ncbiMapViewerUrl = externalUrls.getProperty(
       			"NCBI_Map_Viewer_by_RefSeq").replace("@@@@", refseqID.getAccID());
        }
        
        // GBrowse -- coordinates for three marker types, thumbnail for genes
        if ((isPseudogene || isQTL || isMITMarker) && (coords != null)) {
        	gbrowseUrl = externalUrls.getProperty("GBrowse_by_Other");
        	gbrowseUrl = gbrowseUrl.replaceFirst("@@@@", chromosome);
        	gbrowseUrl = gbrowseUrl.replaceFirst("@@@@", startCoordinate);
        	gbrowseUrl = gbrowseUrl.replaceFirst("@@@@", endCoordinate);
        } else if (isGene && (coords != null)) {
        	gbrowseUrl = externalUrls.getProperty("GBrowse_by_Gene");
        	gbrowseUrl = gbrowseUrl.replaceFirst("@@@@", startCoordinate);
        	gbrowseUrl = gbrowseUrl.replaceFirst("@@@@", endCoordinate);
        	gbrowseUrl = gbrowseUrl.replaceFirst("@@@@", chromosome);
        	
        	gbrowseThumbnailUrl = externalUrls.getProperty("GBrowse_Thumbnail");
        	gbrowseThumbnailUrl = gbrowseThumbnailUrl.replaceFirst("@@@@", chromosome);
        	gbrowseThumbnailUrl = gbrowseThumbnailUrl.replaceFirst("@@@@", startCoordinate);
        	gbrowseThumbnailUrl = gbrowseThumbnailUrl.replaceFirst("@@@@", endCoordinate);
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
        
        // add minimap link
        MarkerLocation cmPos = marker.getPreferredCentimorgans();
        if ((cmPos != null) && (!cmPos.getChromosome().equalsIgnoreCase("UN"))) {
        	if (cmPos != null) {
        			Float cM = cmPos.getCmOffset();
        			if ((cM != null) && (cM.floatValue() >= 0.0)) {
        					mav.addObject ("miniMap", this.getMinimapUrl(marker.getMarkerKey()));
        			} else {
        				logger.debug ("cM offset is null or < 0");
        			}
        	} else {
        		logger.debug ("no cM location");
        	}
        } else {
        	logger.debug ("Unknown chromosome");
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
        SearchResults searchResults
          = MarkerFinder.getMarkerByKey(dbKey);
        List<Marker> markerList = searchResults.getResultObjects();

        // there can be only one...
        if (markerList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Marker Found");
            return mav;
        }// success

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("marker_detail");

        //pull out the marker, and add to mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

        // package referenes; gather via object traversal
        List<Reference> references = marker.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }
        
        this.dbDate(mav);

        return mav;
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


    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<MarkerSummaryRow> seqSummaryJson(
            HttpServletRequest request,
			@ModelAttribute FooQueryForm query,
            @ModelAttribute Paginator page) {

        logger.debug("->JsonSummaryResponse started");

        // parameter parsing
        String refKey = request.getParameter("refKey");
        
        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(query));
        
        if (refKey != null) {
            params.setFilter(new Filter(SearchConstants.REF_KEY, refKey));
        }        

        // perform query, and pull out the requested objects
        SearchResults searchResults
          = MarkerFinder.getMarkers(params);
        List<Marker> markerList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<MarkerSummaryRow> summaryRows = new ArrayList<MarkerSummaryRow> ();
        Iterator<Marker> it = markerList.iterator();
        while (it.hasNext()) {
            Marker marker = it.next();
            if (marker == null) {
                logger.debug("--> Null Object");
            }else {
                summaryRows.add(new MarkerSummaryRow(marker));
            }
        }

        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<MarkerSummaryRow> jsonResponse
          = new JsonSummaryResponse<MarkerSummaryRow>();

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        return jsonResponse;
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
    
    private String getMinimapUrl(int markerKey){
		String urlString = "";
		try {
			URL url = new URL(ContextLoader.getConfigBean().getProperty("WI_URL") + 
					"searches/markerMiniMap.cgi?" + markerKey);
			URLConnection urlConnection = url.openConnection();
			HttpURLConnection connection = null;
			
			if (urlConnection instanceof HttpURLConnection) {
				connection = (HttpURLConnection) urlConnection;
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
				String current;
				current = in.readLine();
				while (current != null) {
					urlString += current;
					current = in.readLine();
				}
			} else {
				logger.debug("miniMap URL not an HTTP URL.");
			}
		} catch (MalformedURLException mue) {
			logger.error(mue.getMessage());
		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		}
		return urlString;
    }

    // generate the sorts
    private List<Sort> genSorts(HttpServletRequest request) {

        logger.debug("->genSorts started");

        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");
        if (sortRequested == null) {
            sortRequested = SortConstants.FOO_SORT;
        }

        String dirRequested  = request.getParameter("dir");
        boolean desc = false;
        if("desc".equalsIgnoreCase(dirRequested)){
            desc = true;
        }

        Sort sort = new Sort(sortRequested, desc);
        sorts.add(sort);

        logger.debug ("sort: " + sort.toString());
        return sorts;
    }

    // generate the filters
    private Filter genFilters(FooQueryForm query){

        logger.debug("->genFilters started");
        logger.debug("QueryForm -> " + query);


        // start filter list to add filters to
        List<Filter> filterList = new ArrayList<Filter>();

        String param1 = query.getParam1();
        String param2 = query.getParam2();

        //
        if ((param1 != null) && (!"".equals(param1))) {
            filterList.add(new Filter (SearchConstants.MRK_ID, param1,
                Filter.OP_EQUAL));
        }

        //
        if ((param2 != null) && (!"".equals(param2))) {
            filterList.add(new Filter (SearchConstants.MRK_ID, param2,
                Filter.OP_EQUAL));
        }

        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }

        return containerFilter;
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
    	return a;
    }
}
