package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

// fewi
import org.jax.mgi.fewi.finder.FooFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.FooQueryForm;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.summary.FooSummaryRow;

// data model objects
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Reference;


/*--------------------------------------*/
/* standard imports for all controllers */
/*--------------------------------------*/

// internal
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;

// external
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
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
 * This controller maps all /foo/ uri's
 */
@Controller
@RequestMapping(value="/admin")
public class AdminController {


    //--------------------//
    // static variables
    //--------------------//

    // time (in ms) at which we last suggested a garbage collection
    private static long lastGarbageCollection = 0;

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(AdminController.class);

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // Admin Page
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getAdminPage() {

        logger.debug("->getAdminPage started");

        ModelAndView mav = new ModelAndView("admin");
	mav = this.populateMav(mav);

        return mav;
    }


    //-------------------------//
    // Admin Page with Garbage Collection
    //-------------------------//
    @RequestMapping("/gc")
    public ModelAndView getAdminPageWithGC(HttpServletRequest request) {

        logger.debug("->getAdminPageWithGC started");

        ModelAndView mav = new ModelAndView("admin");
	mav = this.populateMav(mav);

        mav.addObject("gc", "gc");

	long startTime = System.currentTimeMillis();

	// don't process a garbage collection within 30 seconds of the last
	// one, just to keep us from getting flooded and tying up the
	// processors maliciously

	if ((startTime - lastGarbageCollection) < 30000) {
	   mav = new ModelAndView("error");
	   mav.addObject ("errorMsg",
		"Too soon after last garbage collection"); 
	   return mav;
	}

	lastGarbageCollection = startTime;
	
	System.gc();
	long elapsed = System.currentTimeMillis() - startTime;
 
	mav.addObject ("elapsed", String.format("%8.3f",
	    new Float(elapsed / 1000.0)));
	mav.addObject ("finalFreeMemory", String.format("%,d",
	    new Long(Runtime.getRuntime().freeMemory())));

        return mav;
    }

    //-------------------------//
    // Admin Page with Caches cleared
    //-------------------------//
    @RequestMapping("/clear")
    public ModelAndView getAdminPageCleared(HttpServletRequest request) {

        logger.debug("->getAdminPageCleared started");

	MarkerController.clearMinimapCache();
 
        ModelAndView mav = new ModelAndView("admin");
	mav = this.populateMav(mav);
        mav.addObject("clear", "clear");

        return mav;
    }

    //-------------------------//
    // populate mav with standard items
    //-------------------------//
    private ModelAndView populateMav (ModelAndView mav) {
	Runtime rt = Runtime.getRuntime();

	mav.addObject ("maxMemory", String.format("%,d",
		new Long(rt.maxMemory())));
	mav.addObject ("totalMemory", String.format("%,d",
		new Long(rt.totalMemory())));
	mav.addObject ("initialFreeMemory", String.format("%,d",
		new Long(rt.freeMemory())));
	mav.addObject ("processors", new Integer(rt.availableProcessors()));
	mav.addObject ("minimaps",
		new Integer(MarkerController.getMinimapCacheCount()) );

	return mav;
    }
}
