package org.jax.mgi.fewi.controller;

import java.util.List;

import mgi.frontend.datamodel.Disease;

import org.jax.mgi.fewi.finder.DiseaseFinder;
import org.jax.mgi.fewi.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.entities.SolrVocTerm;
import org.jax.mgi.fewi.util.IDLinker;
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
 * This controller maps all /disease/ uri's
 */
@Controller
@RequestMapping(value="/disease")
public class DiseaseController {


    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger
      = LoggerFactory.getLogger(DiseaseController.class);

    @Autowired
    private DiseaseFinder diseaseFinder;
    
    @Autowired 
    DiseasePortalFinder diseasePortalFinder;
 
    @Autowired
    private IDLinker idLinker;

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------------------------//
    // Disease Detail by Disease Key
    //--------------------------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView diseaseDetailByKey(@PathVariable("dbKey") String dbKey) {
	logger.debug ("-> diseaseDetailByKey started");

	// find the requested disease by database key

	SearchResults<Disease> searchResults = diseaseFinder.getDiseaseByKey(dbKey);
	List<Disease> diseaseList = searchResults.getResultObjects();

	// should only be one.  error condition if not.

	if (diseaseList == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Disease Found");
	    return mav;
	} else if (diseaseList.size() < 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Disease Found");
	    return mav;
	} else if (diseaseList.size() > 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Unique Disease Key Found");
	    return mav;
	}

        Disease disease = diseaseList.get(0);
	if (disease == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Disease Found");
	    return mav;
	}

	return this.prepareDisease(disease.getPrimaryID(), "disease_detail");
    }

    //--------------------//
    // Disease Detail By ID
    //--------------------//
    @RequestMapping(value="/{diseaseID:.+}", method = RequestMethod.GET)
    public ModelAndView diseaseDetailByID(@PathVariable("diseaseID") String diseaseID) {

        logger.debug("->diseaseDetailByID started");

	return this.prepareDisease(diseaseID, "disease_detail");
    }

    //---------------------------------//
    // All disease models by disease ID
    //---------------------------------//
    @RequestMapping(value="/models/{diseaseID:.+}", method = RequestMethod.GET)
    public ModelAndView diseaseModelsByID(@PathVariable("diseaseID") String diseaseID) {

        logger.debug("->diseaseModelsByID started");

	return this.prepareDisease(diseaseID, "disease_models");
    }

    // --------------------------------------------------//
    // Shared code for populating the ModelAndView object
    // --------------------------------------------------//

    // code shared to send back a disease detail page, regardless of
    // whether the initial link was by disease ID or by database key
    private ModelAndView prepareDisease (String diseaseID, String view) {

        List<Disease> diseaseList = diseaseFinder.getDiseaseByID(diseaseID);
        // there can be only one...
        if (diseaseList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            logger.info("No Disease Found");
            mav.addObject("errorMsg", "No Disease Found");
            return mav;
        } else if (diseaseList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate Disease ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView(view);
        
        //pull out the Disease, and add to mav
        Disease disease = diseaseList.get(0);
        mav.addObject("disease", disease);

        // add an IDLinker to the mav for use at the JSP level
        idLinker.setup();
        mav.addObject("idLinker", idLinker);

		// add a pre-computed link for the disease ID
		mav.addObject("linkOut", idLinker.getLink(disease.getLogicalDB(),
				disease.getPrimaryID(), disease.getPrimaryID() ) );
		
		// get disease reference count from DiseasePortal logic
		logger.debug("hitting diseasePortal index for diseaseRefCount");
		List<SolrVocTerm> dpDiseases = diseasePortalFinder.getDiseaseByID(diseaseID);
		if(dpDiseases.size()>0)
		{
			SolrVocTerm dpDisease = dpDiseases.get(0);
			mav.addObject("diseaseRefCount",dpDisease.getDiseaseRefCount());
		}
    
        return mav;
    }
}
