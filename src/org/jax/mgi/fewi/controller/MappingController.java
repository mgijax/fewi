package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.MappingFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.jsonmodel.MappingExperimentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;

import org.springframework.web.bind.annotation.PathVariable;

/*
 * This controller maps all /mapping/ uri's
 */
@Controller
@RequestMapping(value="/mapping")
public class MappingController {

	//--- static variables ---//
	
	private static String MARKER = "marker";
	private static String REFERENCE = "reference";

	//--- instance variables ---//
	
	private Logger logger = LoggerFactory.getLogger(MappingController.class);

	@Autowired
	private MarkerFinder markerFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	@Autowired
	private MappingFinder mappingFinder;

    @Autowired
    private IDLinker idLinker;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	//--- public methods ---//

	// no query form for mapping experiments; summary is only accessible from marker detail
	// and reference summary/detail
	
	/* summary page for a marker
	 */
	@RequestMapping(value="/marker/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView markerSummary(@PathVariable("markerID") String markerID) {
		logger.debug("->markerSummary started (" + markerID + ")");
		return summaryPage(markerID, MARKER);
	}

	/* summary page for a reference
	 */
	@RequestMapping(value="/reference/{referenceID:.+}", method = RequestMethod.GET)
	public ModelAndView referenceSummary(@PathVariable("referenceID") String referenceID) {
		logger.debug("->referenceSummary started (" + referenceID + ")");
		return summaryPage(referenceID, REFERENCE);
	}

	
	
/*	
    @RequestMapping(value="/{probeID:.+}", method = RequestMethod.GET)
    public ModelAndView probeDetail(@PathVariable("probeID") String probeID) {

        logger.debug("->probeDetail started");

        List<MappingExperiment> experimentList = mappingFinder.getExperimentByID(probeID);
        // there can be only one...
        if (probeList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Probe Found for ID " + probeID);
            return mav;
        } else if (probeList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "ID " + probeID + " is associated with multiple probes");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("probe/probe_detail");
        
        //pull out the Probe, and add to mav
        Probe probe = probeList.get(0);
        mav.addObject("probe", probe);
        addDetailSeo(probe, mav);
        flagDisplayFields(probe, mav);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);
        return mav;
    }
*/
    /* Probe Detail by key
     */
/*
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView probeDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->probeDetailByKey started");

        List<Probe> probeList = probeFinder.getProbeByKey(dbKey);
        // there can be only one...
        if (probeList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Probe Found for key " + dbKey);
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("probe/probe_detail");
        
        // pull out the Probe, and add to mav
        Probe probe = probeList.get(0);
        mav.addObject("probe", probe);
        addDetailSeo(probe, mav);
        flagDisplayFields(probe, mav);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);
        return mav;
    }
*/
	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

/*
     // add SEO data (seoDescription, seoTitle, and seoKeywords) to the given detail page's mav
    private void addDetailSeo (Probe p, ModelAndView mav) {
    	List<String> synonyms = p.getSynonyms();
    	
    	// identify high-level segment type (probe or primer)
    	
    	String highLevelSegmentType = "Probe";
    	if ("primer".equals(p.getSegmentType()) ) {
    		highLevelSegmentType = "Primer";
    	}
    	mav.addObject("highLevelSegmentType", highLevelSegmentType);
    	
    	// compose browser / SEO title
    	
    	StringBuffer seoTitle = new StringBuffer();
    	seoTitle.append(p.getName());
    	seoTitle.append(" ");
    	seoTitle.append(highLevelSegmentType);
    	seoTitle.append(" Detail MGI Mouse ");
    	seoTitle.append(p.getPrimaryID());
    	mav.addObject("seoTitle", seoTitle.toString());
    	
    	// compose set of SEO keywords
    	
    	StringBuffer seoKeywords = new StringBuffer();
    	seoKeywords.append(p.getName());
    	if (!"Probe".equals(highLevelSegmentType)) {		// already adding lowercase 'probe' below
    		seoKeywords.append(", ");
    		seoKeywords.append(highLevelSegmentType); 
    	}
    	if ((synonyms != null) && (synonyms.size() > 0)) {
    		for (String synonym : synonyms) {
    			seoKeywords.append(", ");
    			seoKeywords.append(synonym);
    		}
    	}
    	seoKeywords.append(", probe, clone, mouse, mice, murine, Mus");
    	mav.addObject("seoKeywords", seoKeywords);
    	
    	// compose SEO description
    	
    	StringBuffer seoDescription = new StringBuffer();
    	seoDescription.append("View ");
    	seoDescription.append(highLevelSegmentType);
    	seoDescription.append(" ");
    	seoDescription.append(p.getName());
    	seoDescription.append(" : location, molecular source, gene associations, expression, sequences, polymorphisms, and references.");
    	mav.addObject("seoDescription", seoDescription);
    }
 */
    
	// This is a convenience method to handle packing the SearchParams object
	// and return the SearchResults from the finder.
	private SearchResults<MappingExperimentSummary> getSummaryResults(String accID, String objectType) {

		SearchParams params = new SearchParams();
		params.setSorts(genSorts());
		params.setFilter(genFilters(accID, objectType));
		params.setPageSize(100000);
		
		// perform query, return SearchResults 
		return mappingFinder.getExperiments(params);
	}

	/* generic method for summary pages by both marker and reference
	 */
	private ModelAndView summaryPage(String accID, String objectType) {
		if ((accID == null) || (accID.length() == 0)) { return errorMav("No object ID was specified"); }

		ModelAndView mav = new ModelAndView("mapping/mapping_summary");

		// First, we need to find either the Marker or Reference object and add them to the mav.
		
		// if we have a marker ID, then look up the marker and add it to the mav
		if (MARKER.equals(objectType)) {
			SearchResults<Marker> markerResults = markerFinder.getMarkerByID(accID);
			if (markerResults.getTotalCount() != 1) {
				return errorMav("Marker ID " + accID + " does not return a single marker.");
			}
			Marker marker = markerResults.getResultObjects().get(0);
			mav.addObject("marker", marker);
			mav.addObject("title", "Mapping Data Summary for " + marker.getSymbol());
			mav.addObject("description", "Genetic mapping experiments associated with mouse " + marker.getMarkerType()
				+ " " + marker.getSymbol() + ", " + marker.getPrimaryID());

		} else if (REFERENCE.equals(objectType)) {
			// or if we have a reference ID, look up the reference and add it to the mav
			SearchResults<Reference> referenceResults = referenceFinder.getReferenceByID(accID);
			if (referenceResults.getTotalCount() != 1) {
				return errorMav("Reference ID " + accID + " does not return a single reference.");
			}
			Reference reference = referenceResults.getResultObjects().get(0);
			mav.addObject("reference", reference);
			mav.addObject("title", "Mapping Data Summary for " + reference.getJnumID());
			mav.addObject("description", "Genetic mapping experiments associated with reference " + reference.getJnumID()
				+ ", " + reference.getMiniCitation());

		} else {
			return errorMav("Mapping data summary must be by either marker ID or reference ID.");
		}
		
		// Second, we need to get our list of data rows.  There's no pagination on this page, so we'll skip
		// all the JSON coordination and such, and just build out the rows when we build the page itself.
		
		// perform query, and pull out the requested objects
		SearchResults<MappingExperimentSummary> searchResults = getSummaryResults(accID, objectType);
		List<MappingExperimentSummary> experimentList = searchResults.getResultObjects();
		mav.addObject("experiments", experimentList);
		mav.addObject("count", experimentList.size());

		return mav;
	}

	/* return a mav for an error screen with the given message filled in
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

	/* generate the sorts (only one type currently)
	 */
	private List<Sort> genSorts() {
		logger.debug("->genSorts started");

		// marker summary sorts by type; reference summary sorts by name

		String sort = SortConstants.BY_DEFAULT;		// default sort
		boolean desc = false;						// always sort ascending

		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(sort, desc));
		return sorts;
	}
	
	/* generate the filters (translate the query parameters into Solr filters)
	 */
	private Filter genFilters(String accID, String objectType){
		logger.debug("->genFilters started (" + accID + ", " + objectType + ")");

		// marker ID
		if (MARKER.equals(objectType)) {
			return new Filter(SearchConstants.MLD_MARKER_ID, accID, Filter.Operator.OP_EQUAL);
		}
		
		// reference ID
		if (REFERENCE.equals(objectType)) {
			return new Filter(SearchConstants.MLD_REFERENCE_ID, accID, Filter.Operator.OP_EQUAL);
		}
		
		return null;
	}
}
