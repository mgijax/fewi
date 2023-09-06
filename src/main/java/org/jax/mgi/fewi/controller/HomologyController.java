package org.jax.mgi.fewi.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fe.datamodel.HomologyCluster;
import org.jax.mgi.fe.datamodel.Marker;
import org.jax.mgi.fe.datamodel.MarkerID;
import org.jax.mgi.fe.datamodel.MarkerLocation;
import org.jax.mgi.fe.datamodel.OrganismOrtholog;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.forms.HomologyQueryForm;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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

		// for keeping track of data for generating MGV link
		Marker mouseMarker = null;
		boolean hasRat = false;
		boolean hasHuman = false;
		boolean hasZebrafish = false;		// not for MGV, but is for GO Graph link
		
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

			if ("mouse".equals(oo.getOrganism())) {
				mouseMarker = oo.getMarkers().get(0);
			} else if ("rat".equals(oo.getOrganism())) {
				hasRat = true;
			} else if ("human".equals(oo.getOrganism())) {
				hasHuman = true;
			} else if ("zebrafish".equals(oo.getOrganism())) {
				hasZebrafish = true;
			}
		}

		// If we have a mouse marker (which we should) and it has coordinates, then we can use it to form a link to MGV.
		// If there are human and/or rat homologs, note them as comparison species.
		if (mouseMarker != null) {
			MarkerLocation mouseCoords = mouseMarker.getPreferredCoordinates();
			if (mouseCoords != null) {
				try {
					// take 200% of the marker size and add to each end for flank (so gene is 20% of the display)
					long flank = (new Double((mouseCoords.getEndCoordinate() - mouseCoords.getStartCoordinate()) * 2)).longValue();
					
					String chromosome = mouseCoords.getChromosome();
					String startCoord = String.format("%.0f", mouseCoords.getStartCoordinate() - flank);
					String endCoord = String.format("%.0f", mouseCoords.getEndCoordinate() + flank);

					String mgvParams = "#genomes=C57BL/6J";

					if (hasRat || hasHuman) {
						if (hasHuman) {
							mgvParams = mgvParams + ",H. sapiens";
						}
						if (hasRat) {
							mgvParams = mgvParams + ",R. norvegicus";
						}
					}

					mgvParams = mgvParams + "&landmark=" + mouseMarker.getPrimaryID() + "&flank=2x";
					mgvParams = mgvParams + "&highlight=" + mouseMarker.getPrimaryID() + "&lock=on&paralogs=off";
					mgvParams = mgvParams + "&params=" + ContextLoader.getExternalUrls().getProperty("MGV_Style");
					mav.addObject("mgvParams", mgvParams);

				} catch (NumberFormatException e) {
					// any errors? just fail and skip the link
					logger.error("Caught: " + e.toString());
				}
			}
		}
		
		// compose a GO Graph link and text string, if this cluster has a graph page
		if ((homology.getHasComparativeGOGraph() == 1) && (mouseMarker != null)) {
			String organisms = "mouse";
			if (hasHuman) { organisms += ", human"; }
			if (hasRat) { organisms += ", rat"; }
			if (hasZebrafish) { organisms += ", zebrafish"; }

			mav.addObject("goGraphUri", "homology/GOGraph/" + mouseMarker.getSymbol());
			mav.addObject("goGraphOrganisms", organisms);
		}
		
		mav.addObject("homology", homology);
		mav.addObject("source", homology.getSource());
		mav.addObject("seoDescription", "View " + firstSymbol + " mouse/human homology from " + homology.getSource() + " with: genes, location, sequences, " + "associated human diseases");
		mav.addObject("seoKeywords", keywords.toString());

		mav.addObject("browserTitle", firstSymbol + " Vertebrate HGNC");
		mav.addObject("pageTitle", "Vertebrate Homology");

		return mav;
	}

	//-----------------------------------------------
	// Forward to the right Alliance gene detail page for the given set of (cluster key, organism, marker symbol)
	//-----------------------------------------------
	@RequestMapping(value="/alliance/gene", method=RequestMethod.GET)
	public ModelAndView goToAlliance(HttpServletRequest request, @ModelAttribute HomologyQueryForm query) {
		String clusterKey = query.getClusterKey();
		String symbol = query.getSymbol();
		String organism = query.getOrganism();
		
		// check parameters
		
		if (clusterKey == null) {
			return errorMav("Missing clusterKey");
		} else if (organism == null) {
			return errorMav("Missing organism");
		} else if (symbol == null) {
			return errorMav("Missing symbol"); 
		}

		if (!FewiUtil.isPositiveInteger(clusterKey)) {
			return errorMav("Cannot find homology cluster");
		}
		
		// retrieve homology cluster
		
		HomologyCluster homology = null;
		try {
			homology = homologyFinder.getClusterByKey(clusterKey); 
			if (homology == null) { throw new Exception ("is null"); }
		} catch (Throwable t) {
			return errorMav("Cannot find homology cluster");
		}
		
		// Get the desired ortholog organism, then walk through 
		// its markers to find the right one by symbol.
		
		OrganismOrtholog oo = homology.getOrganismOrtholog(organism);
		if (oo == null) {
			return errorMav("Homology cluster does not contain specified organism");
		}
		
		Marker marker = null;
		for (Marker m : oo.getMarkers()) {
			if (m.getSymbol().equals(symbol)) {
				marker = m;
				break;
			}
		}
		
		if (marker == null) {
			return errorMav("Homology cluster does not contain desired marker");
		}

		String markerType = marker.getMarkerSubtype();
		if (markerType == null) {
			markerType = marker.getMarkerType();
		}
		
		String accID = marker.getPrimaryID();
		if ("human".equals(organism)) {
			// Rely on HGNC ID for link, not Entrez Gene.
			accID = marker.getHgncID().getAccID();
		}
		
		// fill parameters and ship off to the JSP
		
		ModelAndView mav = new ModelAndView("alliance_redirect");
		mav.addObject("pageTitle", "Retrieving " + organism + " " + markerType + " " + marker.getSymbol());
		mav.addObject("dataType", organism + " " + markerType);
		mav.addObject("accID", accID);
		return mav;
	}

	//---------------------------------------------------------------
	// Comparative GO Graph for a homology class (by mouse marker symbol)
	//---------------------------------------------------------------
	@RequestMapping(value="/GOGraph/{mouseSymbol:.+}", method = RequestMethod.GET)
	public ModelAndView comparativeGOGraphBySymbol(HttpServletRequest request, @PathVariable("mouseSymbol") String mouseSymbol) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->comparativeGOGraphBySymbol started");

		// First, find a single mouse marker with the given symbol.
		
		SearchResults<Marker> markers = markerFinder.getMarkerBySymbol(mouseSymbol);
		Marker mouseMarker = null;

		if ((markers != null) && (markers.getTotalCount() > 0)) {
			for (Marker m : markers.getResultObjects()) {
				if ("mouse".equals(m.getOrganism())) {
					mouseMarker = m;
					break;
				}
			}
		}

		if (mouseMarker == null) {
			return errorMav("Cannot find marker matching specified symbol");
		}
		
		// Then find the Alliance Direct homology cluster (if there is one) for that mouse marker.
		
		OrganismOrtholog oo = mouseMarker.getAllianceDirectOrganismOrtholog();
		HomologyCluster homology = null;
		
		if (oo != null) {
			homology = oo.getHomologyCluster();
		}

		if (homology == null) {
			return errorMav("No Homology Cluster Found");
		}

		// success - we have a single object

		// if this HomologyCluster has no comparative GO graph, it's an error
		if (homology.getHasComparativeGOGraph() == 0) {
			return errorMav("Homology Cluster has no comparative GO graph");
		}

		String goGraphText = null;
		try {
			String goGraphPath = 
					ContextLoader.getConfigBean().getProperty("GO_GRAPHS_PATH");

			if (!goGraphPath.endsWith("/")) {
				goGraphPath = goGraphPath + "/orthology/" + mouseSymbol + ".html";
			} else {
				goGraphPath = goGraphPath + "orthology/" + mouseSymbol + ".html";
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

		// assume mouse and determine which other organisms appear in the title (human, rat, zebrafish)
		StringBuffer organisms = new StringBuffer("mouse");

		if (homology.getHumanMarkerCount() > 0) { organisms.append(", human"); }
		if (homology.getRatMarkerCount() > 0) { organisms.append(", rat"); }
		if (homology.getZebrafishMarkerCount() > 0) { organisms.append(", zebrafish"); }

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
