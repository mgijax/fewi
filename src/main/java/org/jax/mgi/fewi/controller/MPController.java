package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.RelatedTermBackward;
import org.jax.mgi.fe.datamodel.Term;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.finder.MPAnnotationFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.TermFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrMPAnnotation;
import org.jax.mgi.fewi.summary.MPSummaryRow;
import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /mp/ uri's
 */
@Controller
@RequestMapping(value="/mp")
public class MPController {

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(MPController.class);

    @Autowired
    private MPAnnotationFinder mpAnnotationFinder;

    @Autowired
    private MarkerFinder markerFinder;

    @Autowired
    private TermFinder termFinder;

    @Autowired
    private VocabularyFinder vocabTermFinder;

    //-------------------------------//
    // MP annotation summary by term (and, optionally, marker ID)
    //-------------------------------//
    @RequestMapping(value="/annotations/{mpID}")
    public ModelAndView mpAnnotationsByTerm (HttpServletRequest request,
	@PathVariable("mpID") String mpID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->mpAnnotationsByTerm started");

	// 1. begin building the MAV, assuming a successful return
	ModelAndView mav = new ModelAndView("mp_annotation_summary");
	mav.addObject("mpID", mpID);

	// 2. get the marker ID, if one was supplied (may be null)
	String markerID = request.getParameter("markerID");

	// 3. translate the (optional) marker ID to be a Marker object

	Marker marker = null;

	if (markerID != null) {
	    SearchParams mrkParams = new SearchParams();
	    Filter mrkFilter = new Filter(SearchConstants.MRK_ID, markerID);
	    mrkParams.setFilter(mrkFilter);

	    SearchResults<Marker> mrkSearchResults =
		markerFinder.getMarkerByID(mrkParams);
	    List<Marker> markerList = mrkSearchResults.getResultObjects();

	    if (markerList.size() < 1) {
		// forward to error page
		mav = new ModelAndView("error");
		mav.addObject("errorMsg", "No marker found for " + markerID);
		return mav;
	    }

	    if (markerList.size() > 1) {
		// forward to error page
		mav = new ModelAndView("error");
		mav.addObject("errorMsg", "Dupe marker ID found for "
		    + markerID);
		return mav;
	    }

	    // only one marker matched the ID, so keep it
	    marker = markerList.get(0);
	    mav.addObject("marker", marker);
	}

	// 4. get the Term object for the given ID (we need the text of the
	// term for display)
	
	List<Term> termList = termFinder.getTermsByID(mpID);

	if (termList.size() < 1) {
	    // forward to error page
	    mav = new ModelAndView("error");
	    mav.addObject("errorMsg", "No term found for " + mpID);
	    return mav;
	}

	if (termList.size() > 1) {
	    // forward to error page
	    mav = new ModelAndView("error");
	    mav.addObject("errorMsg", "Two terms found for ID " + mpID);
	    return mav;
	}

	Term term = termList.get(0);
	mav.addObject("term", term);

	// At this point, we have a Term object and (optionally) a Marker
	// object.  We need to find annotations to that Term and its
	// descendents, optionally narrowing the list further to only include
	// genotypes involving alleles of the given Marker.

	// 5. set up our filters for MP ID and (optionally) marker key
	
	SearchParams searchParams = new SearchParams();
	Filter mpFilter = new Filter(SearchConstants.TERM_ID, mpID);

	if (marker == null) {
	    // no Marker, so only filter by MP ID
	    searchParams.setFilter(mpFilter);

	} else {
	    // have both an MP ID and a Marker

	    Filter markerFilter = new Filter(SearchConstants.MRK_KEY,
		Integer.toString(marker.getMarkerKey()) );

	    List<Filter> filterList = new ArrayList<Filter>();
	    filterList.add (mpFilter);
	    filterList.add (markerFilter);

	    searchParams.setFilter(Filter.and(filterList));
	}

	// 6. set up our sorting
	
	List<Sort> sorts = new ArrayList<Sort>();
	Sort sort = new Sort(SortConstants.GENOTYPE_TERM, false);
	sorts.add(sort);
	searchParams.setSorts(sorts);

	// 7. get our annotations
	
	searchParams.setPageSize(10000000);
	SearchResults<SolrMPAnnotation> searchResults =
	    mpAnnotationFinder.getAnnotations (searchParams);

	List<SolrMPAnnotation> annotList = searchResults.getResultObjects();
	mav.addObject("annotationCount", annotList.size());

	// 8. bundle into MPSummaryRow objects

	List<MPSummaryRow> rows = buildSummaryRows(annotList); 
	mav.addObject("genotypeCount", rows.size());

	// 9. add the rows to the MAV and proceed to the JSP for formatting

	mav.addObject("rows", rows);
	mav.addObject("logger", logger);
	return mav;
    }

    //---------------------------------------------//
    // MP annotation summary by EMAPA anatomy term
    //---------------------------------------------//
    @RequestMapping(value="/annotations/by_anatomy/{emapaID}")
    public ModelAndView mpAnnotationsByAnatomyTerm (HttpServletRequest request, @PathVariable("emapaID") String emapaID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->mpAnnotationsByAnatomyTerm started");

        // 1. begin building the MAV, assuming a successful return
        ModelAndView mav = new ModelAndView("mp_annotation_summary");
        mav.addObject("emapaID", emapaID);

        // 2. get the Term object for the given ID (we need the text of the term for display)
	
        List<Term> termList = termFinder.getTermsByID(emapaID);

        if (termList.size() < 1) {
        	// forward to error page
        	mav = new ModelAndView("error");
        	mav.addObject("errorMsg", "No term found for " + emapaID);
        	return mav;
        }

        if (termList.size() > 1) {
        	// forward to error page
        	mav = new ModelAndView("error");
        	mav.addObject("errorMsg", "Two terms found for ID " + emapaID);
        	return mav;
        }

        Term term = termList.get(0);
        mav.addObject("term", term);

        // At this point, we have an anatomy Term object.  We need to find phenotype annotations for MP terms
        // that are associated with that term or its descendants.  (excluding normal annotations)

	    List<SolrMPAnnotation> annotList = this.getAnnotationsByAnatomy(term.getPrimaryID());
	    mav.addObject("annotationCount", annotList.size());

	    // 6. bundle into MPSummaryRow objects

	    List<MPSummaryRow> rows = buildSummaryRows(annotList); 
	    mav.addObject("genotypeCount", rows.size());

	    // 7. add the rows to the MAV and proceed to the JSP for formatting

	    mav.addObject("rows", rows);
	    mav.addObject("logger", logger);
	    return mav;
    }

    //---------------------------------------------//
    // count of MP annotations by EMAPA anatomy term (excluding those with a normal qualifier)
    // special values:
    //		0 = mapped to MP, but with no annotations; -1 = bad ID or not mapped to MP
    //---------------------------------------------//
    @RequestMapping(value="/annotations/count_by_anatomy/{emapaID}")
    public @ResponseBody Integer mpAnnotationCountByAnatomyTerm (@PathVariable("emapaID") String emapaID) {
        logger.debug("->mpAnnotationCountByAnatomyTerm started");
	    int annotationCount = this.getAnnotationsByAnatomy(emapaID).size();
	    
	    // If the count from the index is 0, that could be a mapped term with no annotations, or it could be
	    // an unmapped term.  We need different behavior in the display layer, so we need to determine which.
	    if (annotationCount == 0) {
	    	List<VocabTerm> terms = vocabTermFinder.getTermByID(emapaID);
	    	if (terms.size() == 0) {
	    		return -1;
	    	} else {
	    		List<RelatedTermBackward> mpTerms = terms.get(0).getRelatedTermsBackward();
	    		if ((mpTerms == null) || (mpTerms.size() == 0)) {
	    			return -1;
	    		}
	    	}
	    }
	    return annotationCount;
    }

    /* return a list of annotations, given an EMAPA ID (excluding annotations with a normal qualifier)
     */
    private List<SolrMPAnnotation> getAnnotationsByAnatomy (String emapaID) {
        // set up our filters for EMAPA ID (a cross-reference)
	
        SearchParams searchParams = new SearchParams();
        List<Filter> filters = new ArrayList<Filter>();
        filters.add(new Filter(SearchConstants.CROSS_REF, emapaID));
        filters.add(new Filter(SearchConstants.VOC_RESTRICTION, "normal", Operator.OP_NOT_EQUAL));
	    searchParams.setFilter(Filter.and(filters));

	    // set up our sorting
	
	    List<Sort> sorts = new ArrayList<Sort>();
	    Sort sort = new Sort(SortConstants.GENOTYPE_TERM, false);
	    sorts.add(sort);
	    searchParams.setSorts(sorts);

	    // get our annotations
	
	    searchParams.setPageSize(10000000);
	    SearchResults<SolrMPAnnotation> searchResults = mpAnnotationFinder.getAnnotations (searchParams);

	    return searchResults.getResultObjects();
    }
    
    /* group annotations by genotype into MPSummaryRow objects
     */
    private List<MPSummaryRow> buildSummaryRows (
	List<SolrMPAnnotation> annotList) {

	ArrayList<MPSummaryRow> rows = new ArrayList<MPSummaryRow>();

	MPSummaryRow row = null;
/*
* 	Genotype genotype = null;
*	List<Genotype> genotypes = null;
*/
	int genotypeKey = -1;

	for (SolrMPAnnotation annot: annotList) {

	    // find the genotype for this annotation

	    genotypeKey = Integer.valueOf(annot.getGenotypeKey()).intValue();

	    // on first pass through, start a new MPSummaryRow
	    if (row == null) {
		row = new MPSummaryRow(genotypeKey);
		row.addAnnotation(annot);
		rows.add(row);

	    // if matching genotype keys, add to current MPSummaryRow
	    } else if (row.getGenotypeKey() == genotypeKey) {
		row.addAnnotation(annot);

	    // not a matching genotype key, start a new MPSummaryRow
	    } else {
		row = new MPSummaryRow(genotypeKey);
		row.addAnnotation(annot);
		rows.add(row);
	    }
	}
	return rows;
    }
}
