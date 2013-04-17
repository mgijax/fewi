package org.jax.mgi.fewi.controller;

import java.util.List;

import mgi.frontend.datamodel.HomologyCluster;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerID;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.HomologyFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
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
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Marker Found");
	    return mav;
	} else if (markerList.size() > 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Unique Marker ID Found");
	    return mav;
	}

	// So, we now have found our marker.  Get its HomoloGene ID.
	Marker mouse = markerList.get(0);
	MarkerID mouseID = mouse.getHomoloGeneID();

	if (mouseID == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Mouse Marker Has No HomoloGene ID");
	    return mav;
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
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "No Marker Found");
	    return mav;
	} else if (markerList.size() > 1) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Unique Marker Key Found");
	    return mav;
	}

	// So, we now have found our marker.  Get its HomoloGene ID.
	Marker mouse = markerList.get(0);
	MarkerID mouseID = mouse.getHomoloGeneID();

	if (mouseID == null) {
	    ModelAndView mav = new ModelAndView("error");
	    mav.addObject ("errorMsg", "Non-Mouse Marker Has No HomoloGene ID");
	    return mav;
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

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter homologyIdFilter = new Filter(SearchConstants.HOMOLOGY_ID, homologyID);
        searchParams.setFilter(homologyIdFilter);

        // find the requested HomologyCluster
        SearchResults<HomologyCluster> searchResults
          = homologyFinder.getHomologyByID(searchParams);
        List<HomologyCluster> homologyList = searchResults.getResultObjects();

        // there can be only one...
        if (homologyList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            logger.info("No Homology Cluster Found");
            mav.addObject("errorMsg", "No Homology Cluster Found");
            return mav;
        } else if (homologyList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("homology_detail");
        
        //pull out the HomologyCluster, and add to mav
        HomologyCluster homology = homologyList.get(0);
        mav.addObject("homology", homology);

        return mav;
    }

}
