package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.GlossaryTerm;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.jax.mgi.fewi.finder.GlossaryFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.MarkerTissueCountFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.FooQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.MarkerTissueCountSummaryRow;
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
@RequestMapping(value="/glossary")
public class GlossaryController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(GlossaryController.class);

    @Autowired
    private GlossaryFinder glossaryFinder;
    
    /* glossary index */
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getGlossaryIndex(HttpServletResponse response) {

        logger.debug("->getGlossaryIndex started");

        List<GlossaryTerm> terms = glossaryFinder.getGlossaryIndex();
        
        logger.debug("found "+terms.size()+" glossary terms for the index");
        
        ModelAndView mav = new ModelAndView("glossary_index");
        mav.addObject("glossaryTerms",terms);
        return mav;
    }
   
    /*
     * Glossary term pages
     */
    @RequestMapping(value="/{glossaryKey}")
    public ModelAndView glossaryTermByKey(@PathVariable("glossaryKey") String glossaryKey) 
    {
        logger.debug("->glossaryTermByKey started");

        GlossaryTerm term = glossaryFinder.getGlossaryTerm(glossaryKey);
        if(term==null)
        {
        	 ModelAndView mav = new ModelAndView("error");
             mav.addObject("errorMsg", "No Glossary Term Found for "+glossaryKey);
             return mav;
        }
        		
        logger.debug("found term for glossary key = "+glossaryKey);
        
        ModelAndView mav = new ModelAndView("glossary_term_detail");
        mav.addObject("glossaryTerm",term);
        return mav;
    }
}
