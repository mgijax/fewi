package org.jax.mgi.fewi.controller;

import java.util.List;

import mgi.frontend.datamodel.Disease; 
import mgi.frontend.datamodel.DiseaseRow; 
import mgi.frontend.datamodel.VocabTerm; 
import mgi.frontend.datamodel.VocabChild; 

import org.jax.mgi.fewi.finder.DiseaseFinder;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.fewi.util.DotInputStrFactory;

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

	//-----------------------------------------------------------------------
	// Disease Browser Shell -- header and tab setup
	//-----------------------------------------------------------------------

	
	// default to DOID:7 if no ID provided in URL
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getDefaultBrowserPage() {
		return prepareDisease("DOID:7", "disease_browser", "");
	}
	
	
	// mapping for disease browser via ID
	@RequestMapping(value="/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView diseaseBrowserByID(
			HttpServletRequest request,
			@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->diseaseBrowserByID started");
		
		// handle requests for a specific summary tab
		String openTab = request.getParameter("openTab");

		return prepareDisease(diseaseID, "disease_browser", openTab);
	}
	

	// code shared to send back a disease browser page
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
	

	//-----------------------------------------------------------------------
	// Disease Browser Tab Handling -- tab contents
	//-----------------------------------------------------------------------

	@RequestMapping(value="/termTab/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView getTermTab(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->getTermTab started");

		ModelAndView mav = new ModelAndView("disease_browser_termtab");

		List<Disease> diseaseList = diseaseFinder.getDiseaseByID(diseaseID);
		// there can be only one...
		if (diseaseList.size() < 1) { // none found
			ModelAndView errorMav = new ModelAndView("error");
			logger.info("No Disease Found");
			errorMav.addObject("errorMsg", "No Disease Found");
			return errorMav;
		} else if (diseaseList.size() > 1) { // dupe found
			ModelAndView errorMav = new ModelAndView("error");
			errorMav.addObject("errorMsg", "Duplicate Disease ID");
			return errorMav;
		}
		// success - we have a single object
		Disease disease = diseaseList.get(0);
		
		// prep input to dot graph generation
		DotInputStrFactory disFactory = new DotInputStrFactory();
		for (VocabChild vc : disease.getVocabTerm().getVocabChildren() ) {
			disFactory.addEdge(disease.getDisease(), vc.getChildTerm());
		}

		// add objects to mav, and return to display 
		mav.addObject("disease", disease);
		mav.addObject("dotInputStr", disFactory.getDotInputStr());
		return mav;
	}
	@RequestMapping(value="/geneTab/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView getGeneTab(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->getGeneTab started");

		return prepareDiseaseTab(diseaseID, "disease_browser_genetab");
	}
	@RequestMapping(value="/modelTab/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView getModelTab(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->getModelTab started");

		return prepareDiseaseTab(diseaseID, "disease_browser_modeltab");
	}

	/*
	 * code shared to send back a disease browser tab content
	 */
	private ModelAndView prepareDiseaseTab (String diseaseID, String view) {

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

		return mav;
	}

	//-------------------------------------------------------	
	// Disease Models Popup
	//-------------------------------------------------------

	@RequestMapping(value="/modelsPopup/{diseaseRowKey:.+}", method = RequestMethod.GET)
	public ModelAndView diseaseBrowserModelsPopup(
			@PathVariable("diseaseRowKey") String diseaseRowKey,
			HttpServletRequest request) {
				
		logger.debug("->diseaseBrowserModelsPopup started");
		
		// get the disease from which this popup was triggered
		String disease = request.getParameter("disease");

		// generate ModelAndView object to be passed to detail page
		ModelAndView mav = new ModelAndView("disease_browser_models_popup");
		
		DiseaseRow diseaseRow = diseaseFinder.getDiseaseRowByKey(Integer.parseInt(diseaseRowKey));
		mav.addObject("diseaseRow", diseaseRow);
		mav.addObject("disease", disease);

		return mav;
	}





}
