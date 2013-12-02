package org.jax.mgi.fewi.controller;

import java.io.IOException;
import java.util.List;

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
import org.jax.mgi.fewi.util.GOGraphConverter;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.TextFileReader;
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
 * This controller maps all /homology/ uri's
 */
@Controller
@RequestMapping(value="/homology")
public class HomologyController {


    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger
      = LoggerFactory.getLogger(HomologyController.class);

    @Autowired
    private HomologyFinder homologyFinder;

    @Autowired
    private MarkerFinder markerFinder;

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------------------------//
    // Homology Detail by Marker ID
    //--------------------------------------//
    @RequestMapping(value="/marker/{markerID:.+}", method = RequestMethod.GET)
    public ModelAndView homologyClusterDetailByMarker(@PathVariable("markerID") String markerID) {
	logger.debug ("-> homologyClusterDetailByMarker started");

	// find the requested marker by ID

	SearchParams searchParams = new SearchParams();
	Filter markerIdFilter = new Filter(SearchConstants.MRK_ID, markerID);
	searchParams.setFilter(markerIdFilter);

	SearchResults searchResults = markerFinder.getMarkerByID(searchParams);
	List<Marker> markerList = searchResults.getResultObjects();

	// should only be one.  error condition if not.

	if (markerList.size() < 1) {
	    return errorMav("No Marker Found");
	} else if (markerList.size() > 1) {
	    return errorMav("Non-Unique Marker ID Found");
	}

	// So, we now have found our marker.  Get its HomoloGene ID.
	Marker mouse = markerList.get(0);
	MarkerID mouseID = mouse.getHomoloGeneID();

	if (mouseID == null) {
	    return errorMav("Non-Mouse Marker Has No HomoloGene ID");
	}

	String hgID = mouseID.getAccID();

	// now we can go ahead and pass off to the normal code for links by
	// HomoloGene ID
	
	return this.prepareHomoloGeneClass(hgID);
    }

    //--------------------------------------//
    // Homology Detail by Marker Key
    //--------------------------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView homologyClusterDetailByKey(@PathVariable("dbKey") String dbKey) {
	logger.debug ("-> homologyDetailByKey started");

	// find the requested marker by database key

	SearchResults searchResults = markerFinder.getMarkerByKey(dbKey);
	List<Marker> markerList = searchResults.getResultObjects();

	// should only be one.  error condition if not.

	if (markerList.size() < 1) {
	    return errorMav("No Marker Found");
	} else if (markerList.size() > 1) {
	    return errorMav("Non-Unique Marker Key Found");
	}

	// So, we now have found our marker.  Get its HomoloGene ID.
	Marker mouse = markerList.get(0);
	MarkerID mouseID = mouse.getHomoloGeneID();

	if (mouseID == null) {
	    return errorMav("Non-Mouse Marker Has No HomoloGene ID");
	}

	String hgID = mouseID.getAccID();

	// now we can go ahead and pass off to the normal code for links by
	// HomoloGene ID
	
	return this.prepareHomoloGeneClass(hgID);
    }

    //--------------------//
    // Homology Detail By ID
    //--------------------//
    @RequestMapping(value="/{homologyID:.+}", method = RequestMethod.GET)
    public ModelAndView homologyClusterDetailByID(@PathVariable("homologyID") String homologyID) {

        logger.debug("->homologyDetailByID started");

	return this.prepareHomoloGeneClass(homologyID);
    }

    // code shared to send back a HomoloGene class detail page, regardless of
    // whether the initial link was by HG ID or by non-mouse marker key
    private ModelAndView prepareHomoloGeneClass (String homologyID) {

        List<HomologyCluster> homologyList = homologyFinder.getHomologyClusterByID(homologyID);

        // there can be only one...
        if (homologyList.size() < 1) { // none found
            return errorMav("No Homology Cluster Found");
        } else if (homologyList.size() > 1) { // dupe found
            return errorMav("Duplicate ID");
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("homology_detail");
        
        //pull out the HomologyCluster, and add to mav
        HomologyCluster homology = homologyList.get(0);
        
        
        //logger.debug("PRE_INIT SEQUENCES");
        // kstone - Somehow pre-looping through the markers and sequences makes the MAV do half as many queries on average.
        // I have no idea why, but it cuts a second or two off the load time.
        homology.getOrthologs().size();
        for(OrganismOrtholog oo : homology.getOrthologs())
        {
        	oo.getMarkers().size();
        	for(Marker m : oo.getMarkers())
        	{
        		m.getSequenceAssociations().size();
        	}
        }
        //logger.debug("SEQUENCES INITIALISED");
        
        mav.addObject("homology", homology);

        return mav;
    }

    //---------------------------------------------------------------
    // Comparative GO Graph for a HomoloGene class (by HomoloGene ID)
    //---------------------------------------------------------------
    @RequestMapping(value="/GOGraph/{homologyID:.+}", method = RequestMethod.GET)
    public ModelAndView comparativeGOGraphByID(@PathVariable("homologyID") String homologyID) {

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
		    logger.debug ("GO Graph text length: "
			+ goGraphText.length());
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
