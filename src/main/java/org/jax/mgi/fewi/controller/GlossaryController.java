package org.jax.mgi.fewi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fe.datamodel.GlossaryTerm;
import org.jax.mgi.fewi.finder.GlossaryFinder;
import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public ModelAndView getGlossaryIndex(HttpServletRequest request, HttpServletResponse response) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
    public ModelAndView glossaryTermByKey(HttpServletRequest request, @PathVariable("glossaryKey") String glossaryKey) 
    {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
