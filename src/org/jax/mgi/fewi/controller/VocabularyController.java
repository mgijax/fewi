package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.VocabTerm;

import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /glossary/ uri's
 */
@Controller
@RequestMapping(value="/vocab")
public class VocabularyController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(VocabularyController.class);

    @Autowired
    private VocabularyFinder vocabFinder;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    
    /* omim vocabulary browser */
    @RequestMapping("/omim")
    public String getOmimBrowserIndex() 
    {
    	logger.debug("Forwarding to /vocab/omim/A");
    	return "forward:/mgi/vocab/omim/A";
    }
    @RequestMapping("/omim/{subsetLetter}")
    public ModelAndView getOmimBrowser(@PathVariable("subsetLetter") String subsetLetter) 
    {
        logger.debug("->getOmimBrowser->"+subsetLetter+" started");
        subsetLetter = subsetLetter.toUpperCase();
        
        // enable exclude NOT disease models filter for getting number of mouse models (Defined in VocabTerm)
        sessionFactory.getCurrentSession().enableFilter("termDiseaseModelExcludeNots");
        
        List<VocabTerm> terms = vocabFinder.getVocabSubset("OMIM",subsetLetter);
        
        logger.debug("found "+terms.size()+" omim terms for the subset '"+subsetLetter+"'");
        
        ModelAndView mav = new ModelAndView("omim_browser");
        mav.addObject("subsetLetter",subsetLetter);
        mav.addObject("terms",terms);
        return mav;
    }
}
