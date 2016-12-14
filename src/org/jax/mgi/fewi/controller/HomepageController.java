package org.jax.mgi.fewi.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.StatisticFinder;
import org.jax.mgi.fewi.forms.DiseasePortalQueryForm;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*
 * This controller maps all /home/ URIs
 * Access to all the mini-home pages
 */
@Controller
@RequestMapping(value="/home")
public class HomepageController {

	private final Logger logger = LoggerFactory.getLogger(HomepageController.class);

	@Autowired
	StatisticFinder statisticFinder;

	@Autowired
	DbInfoFinder dbInfoFinder;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView index() {
		logger.debug("->/home index started");
		ModelAndView mav = new ModelAndView("home_index");
		return mav;
	}

	@RequestMapping("/allele")
	public ModelAndView alleleHome() {
		logger.debug("->alleleHome started");
		ModelAndView mav = new ModelAndView("allele/allele_home");
		return mav;
	}

	@RequestMapping("/batchData")
	public ModelAndView Home() {
		logger.debug("->batchDataHome started");
		ModelAndView mav = new ModelAndView("batch_data/batch_data_home");
		return mav;
	}

	@RequestMapping("/gxd")
	public ModelAndView gxdHome() {
		logger.debug("->gxdHome started");
		ModelAndView mav = new ModelAndView("expression/gxd_home");
		return mav;
	}

	@RequestMapping("/genes")
	public ModelAndView genesHome() {
		logger.debug("->genesHome started");
		ModelAndView mav = new ModelAndView("genes/genes_home");
		return mav;
	}

	@RequestMapping("/diseasePortal")
	public ModelAndView diseasePortalHome() {
		logger.debug("->diseasePortalHome started");
		ModelAndView mav = new ModelAndView("diseasePortal/disease_portal_home");
		mav.addObject(new DiseasePortalQueryForm());
		return mav;
	}

	@RequestMapping("/go")
	public ModelAndView goHome() {
		logger.debug("->goHome started");
		ModelAndView mav = new ModelAndView("go/go_home");
		return mav;
	}

	@RequestMapping("/help")
	public ModelAndView helpHome() {
		logger.debug("->helpHome started");
		ModelAndView mav = new ModelAndView("help/help_home");
		return mav;
	}

	@RequestMapping("/homology")
	public ModelAndView homologyHome() {
		logger.debug("->homologyHome started");
		ModelAndView mav = new ModelAndView("homology/homology_home");
		return mav;
	}

	@RequestMapping("/pathways")
	public ModelAndView pathwaysHome() {
		logger.debug("->pathwaysHome started");
		ModelAndView mav = new ModelAndView("pathways/pathways_home");
		return mav;
	}

	@RequestMapping("/recombinase")
	public ModelAndView recombinaseHome() {
		logger.debug("->recombinaseHome started");
		ModelAndView mav = new ModelAndView("recombinase/recombinase_home");
		RecombinaseQueryForm recombinaseQueryForm = new RecombinaseQueryForm();
		recombinaseQueryForm.setDetected("true");
		recombinaseQueryForm.setNotDetected("true");
		mav.addObject(recombinaseQueryForm);
		setDatabaseDate(mav);
		mav.addObject("statistics", statisticFinder.getStatisticsByGroup("Cre Mini Home") );

		return mav;
	}

	@RequestMapping("/strain")
	public ModelAndView strainHome() {
		logger.debug("->strainHome started");
		ModelAndView mav = new ModelAndView("strain/strain_home");
		return mav;
	}

	@RequestMapping("/static/{pageUrl:.+}")
	public ModelAndView homeStaticPages(@PathVariable("pageUrl") String pageUrl) {
		logger.debug("->homeStaticPages started");
		logger.debug("pageUrl = " + pageUrl);
		ModelAndView mav = new ModelAndView("static_home_page");
		mav.addObject("pageUrl", pageUrl);
		return mav;
	}

	private void setDatabaseDate(ModelAndView mav) {
		Date databaseDate = dbInfoFinder.getSourceDatabaseDate();
		SimpleDateFormat dt = new SimpleDateFormat("dd MMM yyyy");
		mav.addObject("databaseDate", dt.format(databaseDate));
	}

}
