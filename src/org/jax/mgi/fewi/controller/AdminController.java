package org.jax.mgi.fewi.controller;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	mav = populateMav(mav);

        return mav;
    }


    //-------------------------//
    // Admin Page with Garbage Collection
    //-------------------------//
    @RequestMapping("/gc")
    public ModelAndView getAdminPageWithGC(HttpServletRequest request) {

        logger.debug("->getAdminPageWithGC started");

        ModelAndView mav = new ModelAndView("admin");
	mav = populateMav(mav);

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

        ModelAndView mav = new ModelAndView("admin");
	mav = populateMav(mav);
        mav.addObject("clear", "clear");

        return mav;
    }

    @RequestMapping("/traffic")
    public ModelAndView geTrafficReport(HttpServletRequest request) {

        logger.debug("->getTrafficReport started");
 
        ModelAndView mav = new ModelAndView("traffic_report");
        mav.addObject("monitor", UserMonitor.getSharedInstance());

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

	return mav;
    }
}
