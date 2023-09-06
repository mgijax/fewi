package org.jax.mgi.fewi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.Antibody;
import org.jax.mgi.fewi.finder.AntibodyFinder;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
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
 * This controller maps all /antibody/ uri's
 */
@Controller
@RequestMapping(value="/antibody")
public class AntibodyController {


    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger
      = LoggerFactory.getLogger(AntibodyController.class);

    @Autowired
    private AntibodyFinder antibodyFinder;
 
    @Autowired
    private IDLinker idLinker;

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------------------------//
    // Antibody Detail by Antibody Key
    //--------------------------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView antibodyDetailByKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

	logger.debug ("-> antibodyDetailByKey started");

	// find the requested antibody by database key

	List<Antibody> antibodyList = antibodyFinder.getAntibodyByKey(dbKey);

	// should only be one.  error condition if not.

	if (antibodyList == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Antibody Found");
	    return mav;
	} else if (antibodyList.size() < 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Antibody Found");
	    return mav;
	} else if (antibodyList.size() > 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Unique Antibody Key Found");
	    return mav;
	}

        Antibody antibody = antibodyList.get(0);
	if (antibody == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Antibody Found");
	    return mav;
	}

	return prepareAntibody(antibody.getPrimaryID(), "antibody_detail");
    }

    //--------------------//
    // Antibody Detail By ID
    //--------------------//
    @RequestMapping(value="/{antibodyID:.+}", method = RequestMethod.GET)
    public ModelAndView antibodyDetailByID(HttpServletRequest request, @PathVariable("antibodyID") String antibodyID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->antibodyDetailByID started");

	return prepareAntibody(antibodyID, "antibody_detail");
    }

    // --------------------------------------------------//
    // Shared code for populating the ModelAndView object
    // --------------------------------------------------//

    // code shared to send back a antibody detail page, regardless of
    // whether the initial link was by antibody ID or by database key
    private ModelAndView prepareAntibody (String antibodyID, String view) {

        List<Antibody> antibodyList = antibodyFinder.getAntibodyByID(antibodyID);
        // there can be only one...
        if (antibodyList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            logger.info("No Antibody Found");
            mav.addObject("errorMsg", "No Antibody Found");
            return mav;
        } else if (antibodyList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate Antibody ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView(view);
        
        //pull out the Antibody, and add to mav
        Antibody antibody = antibodyList.get(0);
        mav.addObject("antibody", antibody);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);

        return mav;
    }
}
