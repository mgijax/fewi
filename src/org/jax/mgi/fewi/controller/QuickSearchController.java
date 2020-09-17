package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Filter.Operator;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.QuickSearchFinder;
import org.jax.mgi.fewi.forms.AccessionQueryForm;
import org.jax.mgi.fewi.forms.QuickSearchQueryForm;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.AccessionSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.QSVocabResult;
import org.jax.mgi.fewi.summary.QSFeatureResult;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /quicksearch/ uri's
 */
@Controller
@RequestMapping(value="/quicksearch")
public class QuickSearchController {


    //--------------------//
    // static variables
    //--------------------//

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(QuickSearchController.class);

    @Autowired
    private AccessionController accessionController;

    @Autowired
    private QuickSearchFinder qsFinder;
    
    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // QS Main Page -- redirect to MGI Home page
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQSMainPage() {
        logger.info("->getQSMainPage started");

        ModelAndView mav = new ModelAndView("redirect:" + ContextLoader.getConfigBean().getProperty("FEWI_URL"));
        return mav;
    }

    //-------------------------//
    // QS Results Page shell
    //-------------------------//
	@RequestMapping("/summary")
	public ModelAndView getQSSummary(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}
        logger.info("->getQSSummary started");

        ModelAndView mav = new ModelAndView("quicksearch");
        mav.addObject("query", queryForm.getQuery());
		mav.addObject("queryString", request.getQueryString());
        return mav;
    }

    //-------------------------//
    // QS Results - bucket 1 JSON (markers and alleles)
    //-------------------------//
	@RequestMapping("/featureBucket")
	public @ResponseBody JsonSummaryResponse<QSFeatureResult> getFeatureBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getFeatureBucket started");
        
        Paginator page = new Paginator(1000);				// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);	// sort by descending Solr score (best first)

        String[] terms = queryForm.getQuery().replace(',', ' ').split(" ");
        
        // Search in order of priority:  ID matches, then symbol/name/synonym matches, then other matches
        
        List<Filter> idFilters = new ArrayList<Filter>();
        List<Filter> nomenFilters = new ArrayList<Filter>();
        List<Filter> otherFilters = new ArrayList<Filter>();

        for (String term : terms) {
        	idFilters.add(new Filter(SearchConstants.QS_ACC_ID, term, Operator.OP_EQUAL));
        	
        	List<Filter> nomenSet = new ArrayList<Filter>();
        	nomenSet.add(new Filter(SearchConstants.QS_SYMBOL, term, Operator.OP_CONTAINS));
        	nomenSet.add(new Filter(SearchConstants.QS_NAME, term, Operator.OP_CONTAINS));
        	nomenSet.add(new Filter(SearchConstants.QS_SYNONYM, term, Operator.OP_CONTAINS));
        	nomenFilters.add(Filter.or(nomenSet));
        	
        	otherFilters.add(new Filter(SearchConstants.QS_SEARCH_TEXT, term, Operator.OP_CONTAINS));
        }

        SearchParams idSearch = new SearchParams();
        idSearch.setPaginator(page);
        idSearch.setFilter(Filter.or(idFilters));

        SearchParams nomenSearch = new SearchParams();
        nomenSearch.setPaginator(page);
        nomenSearch.setFilter(Filter.or(nomenFilters));
        
        SearchParams otherSearch = new SearchParams();
        otherSearch.setPaginator(page);
        otherSearch.setFilter(Filter.or(otherFilters));
        
        List<QSFeatureResult> idMatches = qsFinder.getFeatureResults(idSearch).getResultObjects();
        logger.info("Got " + idMatches.size() + " ID matches");
        List<QSFeatureResult> nomenMatches = qsFinder.getFeatureResults(nomenSearch).getResultObjects();
        logger.info("Got " + nomenMatches.size() + " nomen matches");
        List<QSFeatureResult> otherMatches = qsFinder.getFeatureResults(otherSearch).getResultObjects();
        logger.info("Got " + otherMatches.size() + " other matches");
        
        List<QSFeatureResult> out = idMatches;
        out.addAll(nomenMatches);
        out.addAll(otherMatches);
        
        JsonSummaryResponse<QSFeatureResult> response = new JsonSummaryResponse<QSFeatureResult>();
        response.setSummaryRows(out);
        response.setTotalCount(out.size());
        logger.info("Returning " + out.size() + " matches");

        return response;
    }

    //-------------------------//
    // QS Results - bucket 2 JSON (vocab terms and annotations)
    //-------------------------//
	@RequestMapping("/vocabBucket")
	public @ResponseBody JsonSummaryResponse<QSVocabResult> getVocabBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getVocabBucket started");
        
        Paginator page = new Paginator(1000);				// max results per search
        Sort byScore = new Sort(SortConstants.SCORE, true);	// sort by descending Solr score (best first)

        String[] terms = queryForm.getQuery().replace(',', ' ').split(" ");
        
        // Search in order of priority:  ID matches, then term matches, then synonym matches
        
        List<Filter> idFilters = new ArrayList<Filter>();
        List<Filter> termFilters = new ArrayList<Filter>();
        List<Filter> synonymFilters = new ArrayList<Filter>();

        for (String term : terms) {
        	idFilters.add(new Filter(SearchConstants.QS_ACC_ID, term, Operator.OP_EQUAL));
        	termFilters.add(new Filter(SearchConstants.QS_TERM, term, Operator.OP_CONTAINS));
        	synonymFilters.add(new Filter(SearchConstants.QS_SYNONYM, term, Operator.OP_CONTAINS));
        }

        SearchParams idSearch = new SearchParams();
        idSearch.setPaginator(page);
        idSearch.setFilter(Filter.or(idFilters));

        SearchParams termSearch = new SearchParams();
        termSearch.setPaginator(page);
        termSearch.setFilter(Filter.or(termFilters));
        
        SearchParams synonymSearch = new SearchParams();
        synonymSearch.setPaginator(page);
        synonymSearch.setFilter(Filter.or(synonymFilters));
        
        List<QSVocabResult> idMatches = qsFinder.getVocabResults(idSearch).getResultObjects();
        logger.info("Got " + idMatches.size() + " ID matches");
        List<QSVocabResult> termMatches = qsFinder.getVocabResults(termSearch).getResultObjects();
        logger.info("Got " + termMatches.size() + " term matches");
        List<QSVocabResult> synonymMatches = qsFinder.getVocabResults(synonymSearch).getResultObjects();
        logger.info("Got " + synonymMatches.size() + " synonym matches");
        
        List<QSVocabResult> out = idMatches;
        out.addAll(termMatches);
        out.addAll(synonymMatches);
        
        JsonSummaryResponse<QSVocabResult> response = new JsonSummaryResponse<QSVocabResult>();
        response.setSummaryRows(out);
        response.setTotalCount(out.size());
        logger.info("Returning " + out.size() + " matches");

        return response;
    }

    //-------------------------//
    // QS Results - bucket 3 JSON (accession ID matches)
    //-------------------------//
	@RequestMapping("/idBucket")
	public @ResponseBody JsonSummaryResponse<AccessionSummaryRow> getIDBucket(HttpServletRequest request,
			@ModelAttribute QuickSearchQueryForm queryForm) {

        logger.info("->getIDBucket started");
        
        JsonSummaryResponse<AccessionSummaryRow> out = new JsonSummaryResponse<AccessionSummaryRow>();
        
        // handle multiple terms joined by commas or spaces
        String[] terms = queryForm.getQuery().replace(',', ' ').split(" ");

       	AccessionQueryForm accQF = new AccessionQueryForm();
        Paginator page = new Paginator(1000);

        for (String term : terms) {
        	accQF.setId(term);
        	if (out.getTotalCount() == 0) {
        		out = accessionController.accessionSummaryJson(request, accQF, page);
        	} else {
        		// could merge sets to avoid duplication, but just append for simplicity for now
        		List<AccessionSummaryRow> oldResults = out.getSummaryRows();
        		out = accessionController.accessionSummaryJson(request, accQF, page);

        		oldResults.addAll(out.getSummaryRows());

        		out.setSummaryRows(oldResults);
        		out.setTotalCount(oldResults.size());
        	}
        }
        return out;
    }
}
