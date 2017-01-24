package org.jax.mgi.fewi.controller;

import java.util.List;

import mgi.frontend.datamodel.Disease; 

import org.jax.mgi.fewi.finder.DiseaseFinder;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.util.link.IDLinker;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*
 * This controller maps all /disease/ uri's
 */
@Controller
@RequestMapping(value="/disease")
public class DiseaseController {


	private final Logger logger = LoggerFactory.getLogger(DiseaseController.class);

	@Autowired
	private DiseaseFinder diseaseFinder;

	@Autowired 
	DiseasePortalFinder diseasePortalFinder;

	@Autowired
	private IDLinker idLinker;

	//--------------------------------------//
	// Disease Detail by Disease Key
	//--------------------------------------//

	// default to DOID:7 if no ID provided in URL
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getDefaultBrowserPage() {
		return prepareDisease("DOID:7", "disease_browser", "");
	}
	
	
	@RequestMapping(value="/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView diseaseBrowserByID(
			HttpServletRequest request,
			@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->diseaseBrowserByID started");
		
		// handle requests for a specific summary tab
		String openTab = request.getParameter("openTab");

		return prepareDisease(diseaseID, "disease_browser", openTab);
	}
	
	@RequestMapping(value="/models/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView diseaseModelsByID(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->diseaseModelsByID started");

		return prepareDisease(diseaseID, "disease_models", "");
	}

	// code shared to send back a disease detail page, regardless of
	// whether the initial link was by disease ID or by database key
	private ModelAndView prepareDisease (String diseaseID, String view, String openTab) {

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
		mav.addObject("idLinker", idLinker);

		// add open tab value to the mav for use at the JSP level
		mav.addObject("openTab", openTab);

		// add a pre-computed link for the disease ID
		mav.addObject("linkOut", idLinker.getLink(disease.getLogicalDB(),
				disease.getPrimaryID(), disease.getPrimaryID() ) );

		// get disease reference count and add to MAV if > 0 to turn on the disease ref ribbon
		if (disease.getDiseaseReferenceCount() > 0) {
			mav.addObject("diseaseRefCount", disease.getDiseaseReferenceCount());
		}

		return mav;
	}


////////// TODO - remove this; using old jsp of detail page; using for testing new browser
	@RequestMapping(value="/old/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView oldDiseaseDetailByID(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->diseaseDetailByID started");

		return prepareDisease(diseaseID, "disease_detail", "");
	}






}
