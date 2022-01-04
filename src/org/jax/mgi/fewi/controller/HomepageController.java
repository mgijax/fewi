package org.jax.mgi.fewi.controller;

import java.text.SimpleDateFormat;
import org.jax.mgi.fewi.util.UserMonitor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.StatisticFinder;
import org.jax.mgi.fewi.forms.DiseasePortalQueryForm;
import org.jax.mgi.fewi.forms.RecombinaseQueryForm;
import org.jax.mgi.fewi.forms.StrainQueryForm;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FewiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import mgi.frontend.datamodel.Statistic;

/*
 * This controller maps all /home/ URIs
 * Access to all the mini-home pages and related statistics about MGI data.
 */
@Controller
@RequestMapping(value="/home")
public class HomepageController {

	private final Logger logger = LoggerFactory.getLogger(HomepageController.class);

	@Autowired
	StatisticFinder statisticFinder;

	@Autowired
	DbInfoFinder dbInfoFinder;

	@Autowired
	private StrainController strainController;

	@Value("${solr.factetNumberDefault}")
	private Integer facetLimit; 

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->/home index started");
		ModelAndView mav = new ModelAndView("home_index");
		return mav;
	}

	@RequestMapping("/allele")
	public ModelAndView alleleHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->alleleHome started");
		ModelAndView mav = new ModelAndView("allele/allele_home");
		return mav;
	}

	@RequestMapping("/batchData")
	public ModelAndView Home(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->batchDataHome started");
		ModelAndView mav = new ModelAndView("batch_data/batch_data_home");
		return mav;
	}

	@RequestMapping("/gxd")
	public ModelAndView gxdHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->gxdHome started");
		ModelAndView mav = new ModelAndView("expression/gxd_home");
		return mav;
	}

	@RequestMapping("/genes")
	public ModelAndView genesHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->genesHome started");
		ModelAndView mav = new ModelAndView("genes/genes_home");
		return mav;
	}

	@RequestMapping("/diseasePortal")
	public ModelAndView diseasePortalHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->diseasePortalHome started");
		ModelAndView mav = new ModelAndView("diseasePortal/disease_portal_home");
		mav.addObject(new DiseasePortalQueryForm());
		return mav;
	}

	@RequestMapping("/go")
	public ModelAndView goHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->goHome started");
		ModelAndView mav = new ModelAndView("go/go_home");
		return mav;
	}

	@RequestMapping("/help")
	public ModelAndView helpHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->helpHome started");
		ModelAndView mav = new ModelAndView("help/help_home");
		return mav;
	}

	@RequestMapping("/homology")
	public ModelAndView homologyHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->homologyHome started");
		ModelAndView mav = new ModelAndView("homology/homology_home");
		return mav;
	}

	@RequestMapping("/recombinase")
	public ModelAndView recombinaseHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->recombinaseHome started");
		ModelAndView mav = new ModelAndView("recombinase/recombinase_home");
		RecombinaseQueryForm recombinaseQueryForm = new RecombinaseQueryForm();
		recombinaseQueryForm.setDetected("true");
		recombinaseQueryForm.setNotDetected("true");
		mav.addObject(recombinaseQueryForm);
		setDatabaseDate(mav);
		mav.addObject("statistics", statisticFinder.getStatisticsByGroup("Recombinase Mini Home") );

		return mav;
	}

	@RequestMapping("/strain")
	public ModelAndView strainHome(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->strainHome started");
		ModelAndView mav = new ModelAndView("strain/strain_home");
		setDatabaseDate(mav);
		mav.addObject("statistics", statisticFinder.getStatisticsByGroup("Polymorphisms Mini Home") );
		mav.addObject("strainQueryForm", new StrainQueryForm());

		strainController.initQFCache();
		mav.addObject("attributeChoices", StrainQueryForm.getAttributeChoices());
		mav.addObject("attributeOperatorChoices", StrainQueryForm.getAttributeOperatorChoices());
		
		return mav;
	}

	@RequestMapping("/static/{pageUrl:.+}")
	public ModelAndView homeStaticPages(HttpServletRequest request, @PathVariable("pageUrl") String pageUrl) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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

	@RequestMapping("/database_date")
	public ModelAndView getDatabaseDate (HttpServletRequest request, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		ModelAndView mav = new ModelAndView("database_date");
		setDatabaseDate(mav);
		return mav;
	}

	// allow Ajax access to tables of database statistics (groupName should be like group_name in database, but
	// with underscores substituted for spaces
	@RequestMapping("/statistics/{groupName:.+}")
	public ModelAndView getStatisticTable (HttpServletRequest request, HttpServletResponse response,
			@PathVariable("groupName") String groupName) {
		AjaxUtils.prepareAjaxHeaders(response);
		ModelAndView mav = new ModelAndView("statistic_table");
		mav.addObject("statistics", statisticFinder.getStatisticsByGroup(groupName.replaceAll("_", " ")));
		return mav;
	}

	// provide tab-delimited page of all statistics (mirrors the "All Statistics" page)
	@RequestMapping("/statistics_report.txt")
	public ModelAndView getStatisticsReport (HttpServletRequest request, HttpServletResponse response) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		List<Statistic> allStats = new ArrayList<Statistic>();
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page markers"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page phenotypes"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page gxd"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page recombinase"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page go"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page polymorphisms"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page orthology"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("stats page sequences"));
		allStats.addAll(statisticFinder.getStatisticsByGroup("references"));

		ModelAndView mav = new ModelAndView("statisticsReport");
		setDatabaseDate(mav);
		mav.addObject("statistics", allStats);
		return mav;
	}
}
