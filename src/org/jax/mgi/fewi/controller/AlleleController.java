package org.jax.mgi.fewi.controller;

import java.util.*;

/*------------------------------*/
/* to change in each controller */
/*------------------------------*/

// fewi
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.GenotypeFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;

// data model objects
import mgi.frontend.datamodel.Allele;
//import mgi.frontend.datamodel.AlleleDisease;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.GenotypeDisease;
import mgi.frontend.datamodel.GenotypeDiseaseReference;
import mgi.frontend.datamodel.phenotype.DiseaseTableDisease;
import mgi.frontend.datamodel.phenotype.DiseaseTableGenotype;
import mgi.frontend.datamodel.phenotype.PhenoTableGenotype;
import mgi.frontend.datamodel.phenotype.PhenoTableSystem;
import mgi.frontend.datamodel.phenotype.PhenoTableDisease;


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
import org.jax.mgi.fewi.util.AjaxUtils;

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
 * This controller maps all /allele/ uri's
 */
@Controller
@RequestMapping(value="/allele")
public class AlleleController {

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(AlleleController.class);

    @Autowired
    private AlleleFinder alleleFinder;

    @Autowired
    private GenotypeFinder genotypeFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

	@Autowired
	private SessionFactory sessionFactory;


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//

    //------------------------------------//
    // Allele QForm (not implemented yet)
    //------------------------------------//
//    @RequestMapping(method=RequestMethod.GET)
//    public ModelAndView getQueryForm(HttpServletResponse response) {
//
//        logger.debug("->getQueryForm started");
//        response.addHeader("Access-Control-Allow-Origin", "*");
//
//        ModelAndView mav = new ModelAndView("foo_query");
//        mav.addObject("sort", new Paginator());
//        mav.addObject(new FooQueryForm());
//        return mav;
//    }


    //---------------------//
    // Allele Pheno-Table
    //---------------------//
    @RequestMapping(value="/phenotable/{allID}")
    public ModelAndView phenoTableByAllId(
		  HttpServletRequest request,HttpServletResponse response,
		  @PathVariable("allID") String allID) {

        logger.debug("->phenoTableByAllId started");

        // need to add headers to allow AJAX access
        AjaxUtils.prepareAjaxHeaders(response);

    	// setup view object
        ModelAndView mav = new ModelAndView("phenotype_table");

    	// find the requested Allele
        logger.debug("->asking alleleFinder for allele");
    	List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
    	// there can be only one...
        if (alleleList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No allele found for " + allID);
            return mav;
        }
        if (alleleList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + allID);
            return mav;
        }
        Allele allele = alleleList.get(0);
        logger.debug("->1 allele found");

        List<PhenoTableSystem> phenoTableSystems =
          allele.getPhenoTableSystems();
        Hibernate.initialize(phenoTableSystems);
        logger.debug("->List<PhenoTableSystem> size - " + phenoTableSystems.size());

//        // tell hibernate to filter out disease headers
//        List<PhenoTableDisease> phenoTableDiseases = allele.getPhenoTableDiseases();
//        Hibernate.initialize(phenoTableDiseases);
//        logger.debug("->List<phenoTableDiseases> size - " + phenoTableDiseases.size());
//        logger.debug("->hasDiseaseModels - " + allele.getHasDiseaseModel());
//        mav.addObject("allele",allele);
//        allele.getPhenoTableDiseases().size(); // Trick to have hibernate fetch entire list at once

        // figure out in advance if there will be sex columns.
        boolean hasSexCols=false;
        // figure out if there will be any "source" columns
        boolean hasSourceCols=false;
        for(PhenoTableGenotype g : allele.getPhenoTableGenotypeAssociations())
        {
        	if(g.getSexDisplay()!=null && !g.getSexDisplay().trim().equals(""))
        	{
        		hasSexCols=true;
        	}
        	if(g.getPhenoTableProviders().size()>1 || (g.getPhenoTableProviders().size()==1 &&
        			!g.getPhenoTableProviders().get(0).getProvider().equalsIgnoreCase("MGI")))
        	{
        		hasSourceCols=true;
        	}
        }
        mav.addObject("allele",allele);
        mav.addObject("phenoTableSystems",phenoTableSystems);
       // mav.addObject("phenoTableDiseases",phenoTableDiseases);
        //mav.addObject("hasDiseaseModel",allele.getHasDiseaseModel());
        mav.addObject("phenoTableGenotypes",allele.getPhenoTableGenotypeAssociations());
        mav.addObject("hasSexCols",hasSexCols);
        mav.addObject("hasSourceCols",hasSourceCols);
        mav.addObject("phenoTableGenoSize",allele.getPhenoTableGenotypeAssociations().size());

    	return mav;
    }


    /*
     *
     * Test genotype IDs [MGI:2166662]
     */
    @RequestMapping(value="/genoview/{genoID}")
    public ModelAndView genoview(
		  HttpServletRequest request,
		  HttpServletResponse response,
		  @PathVariable("genoID") String genoID) {

        logger.debug("->genoview started");

     // need to add headers to allow AJAX access
        AjaxUtils.prepareAjaxHeaders(response);

    	// setup view object
        ModelAndView mav = new ModelAndView("phenotype_table_geno_popup");

    	// find the requested Allele
        logger.debug("->asking genotypeFinder for genotype");

        List<Genotype> genotypeList = genotypeFinder.getGenotypeByID(genoID);
    	// there can be only one...
        if (genotypeList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No genotype found for " + genoID);
            return mav;
        }
        if (genotypeList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + genoID);
            return mav;
        }
        Genotype genotype = genotypeList.get(0);

        logger.debug("->1 genotype found");

        // eager load the entire collection
        // calling .size() is another trick eager load the entire collection
        Hibernate.initialize(genotype.getMPSystems());
        mav.addObject("genotype",genotype);
        mav.addObject("mpSystems", genotype.getMPSystems());

        for (GenotypeDisease gd : genotype.getDiseases())
        {
        	logger.info(" found disease: "+gd.getTerm());
        	for(GenotypeDiseaseReference gr : gd.getReferences())
        	{
        		logger.info(" found disease reference: "+gr.getJnumID());
        	}
        }
        if(genotype.hasPrimaryImage())
        {
        	logger.info(" has Image: "+genotype.getPrimaryImage().getMgiID());
        }
        mav.addObject("hasDiseaseModels", genotype.getDiseases().size()>0);
        mav.addObject("hasImage",genotype.hasPrimaryImage());
        mav.addObject("counter", request.getParameter("counter") );

    	return mav;
    }

    /*
    *
    * Test allele IDs [MGI:2166662]
    */
   @RequestMapping(value="/allgenoviews/{alleleID}")
   public ModelAndView allGenoviews(
		  HttpServletRequest request,
		  HttpServletResponse response,
		  @PathVariable("alleleID") String alleleID) {

       logger.debug("->all genoviews started");

       // need to add headers to allow AJAX access
       AjaxUtils.prepareAjaxHeaders(response);

   	// setup view object
       ModelAndView mav = new ModelAndView("phenotype_table_all_geno_popups");

   	// find the requested Allele
       logger.debug("->asking alleleFinder for allele");

       List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
   	// there can be only one...
       if (alleleList.size() < 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "No allele found for " + alleleID);
           return mav;
       }
       if (alleleList.size() > 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "Dupe reference found for " + alleleID);
           return mav;
       }
       Allele allele = alleleList.get(0);

       logger.debug("->1 allele found");

       // eager load the entire collection
       // calling .size() is another trick eager load the entire collection
       Hibernate.initialize(allele.getPhenoTableGenotypeAssociations());
       mav.addObject("genotypeAssociations",allele.getPhenoTableGenotypeAssociations());
   	return mav;
   }

   @RequestMapping(value="/alldiseasegenoviews/{alleleID}")
   public ModelAndView allDiseaseGenoviews(
		  HttpServletRequest request,
		  HttpServletResponse response,
		  @PathVariable("alleleID") String alleleID) {

       logger.debug("->all disease genoviews started");

       // need to add headers to allow AJAX access
       AjaxUtils.prepareAjaxHeaders(response);

   	// setup view object
       ModelAndView mav = new ModelAndView("phenotype_table_all_geno_popups");

   	// find the requested Allele
       logger.debug("->asking alleleFinder for allele");

       List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
   	// there can be only one...
       if (alleleList.size() < 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "No allele found for " + alleleID);
           return mav;
       }
       if (alleleList.size() > 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "Dupe reference found for " + alleleID);
           return mav;
       }
       Allele allele = alleleList.get(0);

       logger.debug("->1 allele found");

       // eager load the entire collection
       // calling .size() is another trick eager load the entire collection
       Hibernate.initialize(allele.getDiseaseTableGenotypeAssociations());
       mav.addObject("genotypeAssociations",allele.getDiseaseTableGenotypeAssociations());

   	return mav;
   }

   //---------------------//
   // Allele Disease-Table
   //---------------------//
   @RequestMapping(value="/diseasetable/{allID}")
   public ModelAndView diseaseTableByAllId(
		  HttpServletRequest request,HttpServletResponse response,
		  @PathVariable("allID") String allID) {

       logger.debug("->diseaseTableByAllId started");

       // need to add headers to allow AJAX access
       AjaxUtils.prepareAjaxHeaders(response);

   	// setup view object
       ModelAndView mav = new ModelAndView("disease_table");

   	// find the requested Allele
       logger.debug("->asking alleleFinder for allele");
   	List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
   	// there can be only one...
       if (alleleList.size() < 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "No allele found for " + allID);
           return mav;
       }
       if (alleleList.size() > 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "Dupe reference found for " + allID);
           return mav;
       }
       Allele allele = alleleList.get(0);
       logger.debug("->1 allele found");

       List<DiseaseTableDisease> diseaseTableDiseases =
         allele.getDiseaseTableDiseases();
       Hibernate.initialize(diseaseTableDiseases);
       logger.debug("->List<DiseaseTableDisease> size - " + diseaseTableDiseases.size());
       mav.addObject("allele",allele);
       mav.addObject("diseases",diseaseTableDiseases);
       mav.addObject("genotypes",allele.getDiseaseTableGenotypeAssociations());
       mav.addObject("diseaseTableGenoSize",allele.getDiseaseTableGenotypeAssociations().size());

   	return mav;
   }

}
