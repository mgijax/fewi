package org.jax.mgi.fewi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.QuickSearchQueryForm;
import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public ModelAndView accessionSummary(HttpServletRequest request,
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

    //-------------------------//
    // QS Results - bucket 2 JSON (vocab terms and annotations)
    //-------------------------//

    //-------------------------//
    // QS Results - bucket 3 JSON (accession ID matches)
    //-------------------------//
    
}
