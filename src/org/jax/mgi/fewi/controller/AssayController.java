package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

// fewi
import org.jax.mgi.fewi.finder.AssayFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
//import org.jax.mgi.fewi.forms.AssayQueryForm;

// data model objects
import mgi.frontend.datamodel.ExpressionAssay;
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
import javax.servlet.http.HttpServletResponse;

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
 * This controller maps all /assay/ uri's
 */
@Controller
@RequestMapping(value="/assay")
public class AssayController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(AssayController.class);

    @Autowired
    private AssayFinder assayFinder;
//
//    @Autowired
//    private ReferenceFinder referenceFinder;


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//

    //--------------------//
    // Assay Detail By ID
    //--------------------//
    @RequestMapping(value="/{assayID:.+}", method = RequestMethod.GET)
    public ModelAndView assayDetailByID(@PathVariable("assayID") String assayID) {

        logger.debug("->assayDetailByID started - requested ID " + assayID);

        // ModelAndView object to be returned
        ModelAndView mav = new ModelAndView();

        // find the requested assay
        List<ExpressionAssay> assayList = assayFinder.getAssayByID(assayID);

        logger.debug("->assayList.size " + assayList.size());

        // there can be only one assay...
        if (assayList.size() < 1) { // none found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Assay Found");
            return mav;
        }
        if (assayList.size() > 1) { // dupe found
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single assay
        ExpressionAssay assay = assayList.get(0);

        return getAssayDetail(assay);
    }
    
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView assayDetailByKey(@PathVariable("dbKey") String dbKey) {
    	logger.debug("->assayDetailByKey started - requested dbKey " + dbKey);
    	
    	ExpressionAssay assay = assayFinder.getAssayByKey(dbKey);
    	
    	if (assay == null) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Assay Found");
            return mav;
        }
    	return getAssayDetail(assay);
    }
    
    /*
     * Main logic for rendering the correct assay detail page (in situ or blot)
     */
    private ModelAndView getAssayDetail(ExpressionAssay assay)
    {
    	ModelAndView mav;
    	// direct to correct detail page (inSitu vs. gel)
        if (assay.getAssaySpecimens().size() > 0 )  { // in situ
          mav = new ModelAndView("assay_insitu_detail");
        } else { // gel
          mav = new ModelAndView("assay_gel_detail");
        }

        // add assay to mav
        mav.addObject("assay", assay);

        // find the reference for this assay
        mav.addObject("reference", assay.getReference());

        return mav;
    }

}
