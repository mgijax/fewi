package org.jax.mgi.fewi.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.HomologyCluster;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;
import mgi.frontend.datamodel.OrganismOrtholog;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.HomologyFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.GOGraphConverter;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.file.TextFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*
 * This controller maps all /homology/ uri's
 */
@Controller
@RequestMapping(value="/homology")
public class HomologyController {


	private Logger logger = LoggerFactory.getLogger(HomologyController.class);

	@Autowired
	private HomologyFinder homologyFinder;

	@Autowired
	private MarkerFinder markerFinder;

	@RequestMapping(value="/marker/{markerID:.+}", method = RequestMethod.GET)
	public ModelAndView homologyClusterDetailByMarker(HttpServletRequest request, @PathVariable("markerID") String markerID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug ("-> homologyClusterDetailByMarker started");

		// find the requested marker by ID

		SearchParams searchParams = new SearchParams();
		Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
		searchParams.setFilter(markerIdFilter);

		SearchResults<Marker> searchResults = markerFinder.getMarkerByID(searchParams);
		List<Marker> markerList = searchResults.getResultObjects();

		// should only be one.  error condition if not.

		if (markerList.size() < 1) {
			return errorMav("No Marker Found");
		} else if (markerList.size() > 1) {
			return errorMav("Non-Unique Marker ID Found");
		}

		// So, we now have found our marker.  Get its HomoloGene ID.
		Marker mouse = markerList.get(0);
		String directClusterKey = mouse.getAllianceDirectClusterKey();

		if (directClusterKey == null) {
			return errorMav("Marker has no associated Alliance Direct homology cluster.");
		}

		// now we can go ahead and pass off to the normal code for links by cluster key

		return prepareHomologyClusterByKey(directClusterKey);
	}

	@RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
	public ModelAndView homologyClusterDetailByMarkerKey(HttpServletRequest request, @PathVariable("dbKey") String dbKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug ("-> homologyDetailByKey started");

		// find the requested marker by database key

		SearchResults<Marker> searchResults = markerFinder.getMarkerByKey(dbKey);
		List<Marker> markerList = searchResults.getResultObjects();

		// should only be one.  error condition if not.

		if (markerList.size() < 1) {
			return errorMav("No Marker Found");
		} else if (markerList.size() > 1) {
			return errorMav("Non-Unique Marker Key Found");
		}

		// So, we now have found our marker.  Get its HomoloGene ID.
		Marker mouse = markerList.get(0);
		String directClusterKey = mouse.getAllianceDirectClusterKey();

		if (directClusterKey == null) {
			return errorMav("Marker has no associated Alliance Direct homology cluster.");
		}

		// now we can go ahead and pass off to the normal code for links by cluster key

		return prepareHomologyClusterByKey(directClusterKey);
	}

	//--------------------//
	// HomoloGene Detail By ID
	//--------------------//
	@RequestMapping(value="/{homologyID:.+}", method = RequestMethod.GET)
	public ModelAndView homologyClusterDetailByID(HttpServletRequest request, @PathVariable("homologyID") String homologyID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->homologyDetailByID started");

		// homologyID is really a cluster key
		return prepareHomologyClusterByKey(homologyID);
	}

	//------------------------------------------------------------
	// homology cluster (either HGNC or HomoloGene) by cluster key
	//------------------------------------------------------------
	@RequestMapping(value="/cluster/key/{clusterKey:.+}", method = RequestMethod.GET)
	private ModelAndView homologyClusterDetailByKey(HttpServletRequest request, @PathVariable("clusterKey") String clusterKey) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		if (!FewiUtil.isPositiveInteger(clusterKey)) {
			return errorMav("Cannot find homology cluster");
		}
		
		HomologyCluster homology = null;
		try {
			homology = homologyFinder.getClusterByKey(clusterKey); 
			if (homology == null) { throw new Exception ("is null"); }
		} catch (Throwable t) {
			return errorMav("Cannot find homology cluster");
		}
		return prepareHomologyClass(homology);
	}

	// code shared to send back a HomoloGene class detail page, regardless of
	// whether the initial link was by HG ID or by non-mouse marker key
	private ModelAndView prepareHomologyClusterByKey (String clusterKey) {

		HomologyCluster homologyCluster = homologyFinder.getClusterByKey(clusterKey);

		// there can be only one...
		if (homologyCluster == null) {						// none found
			return errorMav("No Homology Cluster Found");
		}

		return prepareHomologyClass(homologyCluster);
	}

	// code shared to send back a HomoloGene class detail page, regardless of
	// whether the initial link was by HG ID or by non-mouse marker key
	private ModelAndView prepareHomologyClass (HomologyCluster homology) {

		// generate ModelAndView object to be passed to detail page
		ModelAndView mav = new ModelAndView("homology_detail");

		// first marker symbol (for page title)
		String firstSymbol = null;

		// SEO keywords
		StringBuffer keywords = new StringBuffer("MGI");

		// kstone - Somehow pre-looping through the markers and sequences makes the MAV do half as many queries on average.
		// I have no idea why, but it cuts a second or two off the load time.
		homology.getOrthologs().size();
		for(OrganismOrtholog oo : homology.getOrthologs())
		{
			oo.getMarkers().size();
			for(Marker m : oo.getMarkers())
			{
				m.getSequenceAssociations().size();
				if (firstSymbol == null) {
					firstSymbol = m.getSymbol();
				}
				keywords.append(", ");
				keywords.append(m.getSymbol());
			}
		}

		mav.addObject("homology", homology);
		mav.addObject("source", homology.getSource());
		mav.addObject("seoDescription", "View " + firstSymbol + " mouse/human homology from " + homology.getSource() + " with: genes, location, sequences, " + "associated human diseases");
		mav.addObject("seoKeywords", keywords.toString());

		mav.addObject("browserTitle", firstSymbol + " Vertebrate HGNC");
		mav.addObject("pageTitle", "Vertebrate Homology");

		return mav;
	}

	//---------------------------------------------------------------
	// Comparative GO Graph for a HomoloGene class (by HomoloGene ID)
	//---------------------------------------------------------------
	@RequestMapping(value="/GOGraph/{homologyID:.+}", method = RequestMethod.GET)
	public ModelAndView comparativeGOGraphByID(HttpServletRequest request, @PathVariable("homologyID") String homologyID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->comparativeGOGraphByID started");

		List<HomologyCluster> homologyList = homologyFinder.getHomologyClusterByID(homologyID);

		// there can be only one...
		if (homologyList.size() < 1) { // none found
			return errorMav("No Homology Cluster Found");
		} else if (homologyList.size() > 1) { // dupe found
			return errorMav("Duplicate ID");
		}
		// success - we have a single object

		//pull out the HomologyCluster
		HomologyCluster homology = homologyList.get(0);

		// if this HomologyCluster has no comparative GO graph, it's an error
		if (homology.getHasComparativeGOGraph() == 0) {
			return errorMav("Homology Cluster has no comparative GO graph");
		}

		String goGraphText = null;
		try {
			String goGraphPath = 
					ContextLoader.getConfigBean().getProperty("GO_GRAPHS_PATH");

			if (!goGraphPath.endsWith("/")) {
				goGraphPath = goGraphPath + "/orthology/" + homologyID + ".html";
			} else {
				goGraphPath = goGraphPath + "orthology/" + homologyID + ".html";
			}
			logger.debug("Reading GO Graph from: " + goGraphPath);

			goGraphText = TextFileReader.readFile(goGraphPath);

			if (goGraphText == null) {
				logger.debug ("GO Graph text is null");
			} else {
				logger.debug ("GO Graph text length: " + goGraphText.length());
			}

			// convert special MGI markups to their full HTML equivalents
			NotesTagConverter ntc = new NotesTagConverter();
			goGraphText = ntc.convertNotes(goGraphText, '|');

			GOGraphConverter ggc = new GOGraphConverter();
			goGraphText = ggc.translateMarkups(goGraphText);

		} catch (IOException e) {
			return errorMav("Could not read comparative GO graph from file");
		}

		// determine which organisms appear in the title (mouse, human, rat)
		StringBuffer organisms = new StringBuffer();

		if (homology.getMouseMarkerCount() > 0) {
			organisms.append("mouse");
		}

		if (homology.getHumanMarkerCount() > 0) {
			if (organisms.length() > 0) {
				organisms.append(", ");
			}
			organisms.append("human");
		}

		if (homology.getRatMarkerCount() > 0) {
			if (organisms.length() > 0) {
				organisms.append(", ");
			}
			organisms.append("rat");
		}

		// generate ModelAndView object to be passed to detail page
		ModelAndView mav = new ModelAndView("homology_go_graph");
		mav.addObject("homology", homology);
		mav.addObject("goGraphText", goGraphText);
		mav.addObject("organisms", organisms.toString());
		mav.addObject("source", homology.getSource());

		return mav;
	}

	// convenience method -- construct a ModelAndView for the error page and
	// include the given 'msg' as the error String to be reported
	private ModelAndView errorMav (String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}
}
