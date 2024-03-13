package org.jax.mgi.fewi.controller;

import java.util.List;

import org.jax.mgi.fe.datamodel.Disease; 
import org.jax.mgi.fe.datamodel.DiseaseRow; 
import org.jax.mgi.fe.datamodel.VocabTerm; 
import org.jax.mgi.fe.datamodel.VocabChild; 

import org.jax.mgi.fewi.finder.DiseaseFinder;
import org.jax.mgi.fewi.hmdc.finder.DiseasePortalFinder;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.fewi.util.DotInputStrFactory;
import org.jax.mgi.fewi.util.UserMonitor;

import org.jax.mgi.fe.datamodel.Reference; 
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.util.NotesTagConverter;

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
        private ReferenceFinder referenceFinder;

	@Autowired 
	DiseasePortalFinder diseasePortalFinder;

	@Autowired
	private IDLinker idLinker;

	//-----------------------------------------------------------------------
	// Disease Browser Shell -- header and tab setup
	//-----------------------------------------------------------------------

	
	// default to DOID:7 if no ID provided in URL
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getDefaultBrowserPage(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		return prepareDisease("DOID:7", "disease_browser", "");
	}
	
       //------------------------------------//
       // Disease Model Summary (By Reference) Shell
       //------------------------------------//
       @RequestMapping(value="/reference/{refID}")
       public ModelAndView diseaseModelSummaryByRefId(HttpServletRequest request, @PathVariable("refID") String refID) {
               if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
                       return UserMonitor.getSharedInstance().getLimitedMessage();
               }   

               logger.debug("->diseaseModelSummaryByRefId started");

               // setup view object
               ModelAndView mav = new ModelAndView("disease_model_summary_by_reference");

               // setup search parameters object to gather the reference
               SearchParams referenceSearchParams = new SearchParams();
               Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
               referenceSearchParams.setFilter(refIdFilter);

               // find the reference
               SearchResults<Reference> referenceSearchResults = referenceFinder.searchReferences(referenceSearchParams);
               List<Reference> referenceList = referenceSearchResults.getResultObjects();

               // there can be only one...
               if (referenceList.size() < 1) {
                       // forward to error page
                       mav = new ModelAndView("error");
                       mav.addObject("errorMsg", "No reference found for " + refID);
                       return mav;
               }   
               if (referenceList.size() > 1) {
                       // forward to error page
                       mav = new ModelAndView("error");
                       mav.addObject("errorMsg", "Dupe reference found for " + refID);
                       return mav;
               }   
               Reference reference = referenceList.get(0);

               // package data, and send to view layer
               mav.addObject("reference", reference);
                mav.addObject("ntc", new NotesTagConverter());

              logger.debug("diseaseModelSummaryByRefId routing to view "); 
               return mav;
       }   

	// mapping for disease browser via ID
	@RequestMapping(value="/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView diseaseBrowserByID(
			HttpServletRequest request,
			@PathVariable("diseaseID") String diseaseID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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

	// term tab
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
		
		String dotInputStr = this.getDotInputStr(disease);
		//logger.info(dotInputStr);
		
		// add objects to mav, and return to display 
		mav.addObject("disease", disease);
		mav.addObject("dotInputStr", dotInputStr);
		return mav;
	}
		
	// genes tab
	@RequestMapping(value="/geneTab/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView getGeneTab(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->getGeneTab started");

		return prepareDiseaseTab(diseaseID, "disease_browser_genetab");
	}
	
	// models tab
	@RequestMapping(value="/modelTab/{diseaseID:.+}", method = RequestMethod.GET)
	public ModelAndView getModelTab(@PathVariable("diseaseID") String diseaseID) {

		logger.debug("->getModelTab started");

		return prepareDiseaseTab(diseaseID, "disease_browser_modeltab");
	}

	// shared code to generate disease browser tab content
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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}
				
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


	//-------------------------------------------------------	
	// Disease File Downloads
	//-------------------------------------------------------

	@RequestMapping("genes/report*")
	public ModelAndView geneTabExport(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.info("in geneTabExport");
		
		// get the disease id from URL
		String diseaseID = request.getParameter("doid");

		List<Disease> diseaseList = diseaseFinder.getDiseaseByID(diseaseID);
		Disease disease = diseaseList.get(0);

		ModelAndView mav = new ModelAndView("diseaseGeneTabReport");
		mav.addObject("disease", disease);
		return mav;

	}	
	
	@RequestMapping("models/report*")
	public ModelAndView modelTabExport(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.info("in modelTabExport");
		
		// get the disease id from URL
		String diseaseID = request.getParameter("doid");

		List<Disease> diseaseList = diseaseFinder.getDiseaseByID(diseaseID);
		Disease disease = diseaseList.get(0);

		ModelAndView mav = new ModelAndView("diseaseModelTabReport");
		mav.addObject("disease", disease);
		return mav;

	}	

	
	//-------------------------------------------------------	
	// DOT graph generation
	//-------------------------------------------------------
	
	// responsible for generating the dot input string
	private String getDotInputStr (Disease disease) {

		// factory for generating the dot input string
		DotInputStrFactory disFactory = new DotInputStrFactory();
		
		// name of graph
		disFactory.setGraphName("Directional Graph of " + disease.getPrimaryID());

		// add label to disease we're displaying
		disFactory.setHomeNode(disease.getPrimaryID());
		disFactory.setHomeNodeLabel(disease.getDisease());

		// children of this term
		for (VocabChild vc : disease.getVocabTerm().getVocabChildren() ) {
			disFactory.addEdge(disease.getPrimaryID(), vc.getChildPrimaryId());
			disFactory.addNodeLabel(vc.getChildPrimaryId(), vc.getChildTerm());
		}

		// parents of this term
		VocabTerm diseaseVocabTerm = disease.getVocabTerm();
		disFactory = this.handleDiseaseParents(diseaseVocabTerm, disFactory);

		return disFactory.getDotInputStr();
	}	
	
	// recursive method to gather ancestor nodes up to root node
	private DotInputStrFactory handleDiseaseParents (VocabTerm vocabTerm, DotInputStrFactory disFactory) {

		// for each parent of this term...
		for (VocabChild p : vocabTerm.getParentEdges() ) {

			// recursion to handle if this term had parents 
			handleDiseaseParents(p.getParent(), disFactory);
						
			disFactory.addEdge(p.getParent().getPrimaryID(), vocabTerm.getPrimaryID());
			disFactory.addNodeLabel(p.getParent().getPrimaryID(), p.getParent().getTerm());
			
		}
		
		return disFactory;
	}



}
