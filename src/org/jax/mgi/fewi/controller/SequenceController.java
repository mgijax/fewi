package org.jax.mgi.fewi.controller;

import java.util.*;
import java.io.*;

// fewi & data model objects
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.util.StyleAlternator;
import mgi.frontend.datamodel.*;

//external libs
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value="/sequence")
public class SequenceController {

    private Logger logger = LoggerFactory.getLogger(SequenceController.class);

    @Autowired
    private SequenceFinder sequenceFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

    /////////////////////////////////////////////////////////////////////////
    //  Sequence Detail
    /////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/{seqID}", method = RequestMethod.GET)
    public ModelAndView seqDetail(@PathVariable("seqID") String seqID) {

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter seqIdFilter = new Filter(SearchConstants.SEQ_ID, seqID);
        searchParams.setFilter(seqIdFilter);

        // find the requested sequence
        SearchResults searchResults
          = sequenceFinder.getSequenceByID(searchParams);

        List<Sequence> seqList = searchResults.getResultObjects();

        if (seqList.size() < 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return mav;
        }
        if (seqList.size() > 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }

        // success;  pull out the sequence, and forward to the detail page,
        Sequence sequence = seqList.get(0);

        // returned ModelAndView object;  dictates view to forward to
        ModelAndView mav = new ModelAndView("sequence_detail");

        // package style objects for view layer
        mav.addObject("leftTdStyles", new StyleAlternator("detailCat1","detailCat2"));
        mav.addObject("rightTdStyles", new StyleAlternator("detailData1","detailData2"));

        // package data objects for view layer
        mav.addObject("seqID", seqID);
        mav.addObject("sequence", sequence);

        // package chromosome value
        List<SequenceLocation> locList = sequence.getLocations();
        if (!locList.isEmpty()) {
            mav.addObject("chromosome", locList.get(0).getChromosome());
        }

        // package annotated markers
        Set<Marker> markers = sequence.getMarkers();
        if (!markers.isEmpty()) {
            mav.addObject("markers", markers);
        }

        // package probes
        Set<Probe> probes = sequence.getProbes();
        if (!probes.isEmpty()) {
            mav.addObject("probes", probes);
        }

        // package referenes
        List<Reference> references = sequence.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        return mav;
    }


    /////////////////////////////////////////////////////////////////////////
    //  Sequence Summary by Reference
    /////////////////////////////////////////////////////////////////////////

    @RequestMapping(value="/reference/{refID}", method = RequestMethod.GET)
    public ModelAndView seqSummeryByRef(@PathVariable("refID") String refID) {


        // setup search parameters object to gather the requested reference
        SearchParams searchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults searchResults
          = referenceFinder.searchReferences(searchParams);
        List<Reference> refList = searchResults.getResultObjects();
        if (refList.size() < 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for ID -> " + refID);
            return mav;
        }
        Reference ref = refList.get(0); //there should be only one

        // setup search parameters object to gather seqs for this reference
        searchParams = new SearchParams();
        String refKeyStr = Integer.toString(ref.getReferenceKey());
        Filter refKeyFilter = new Filter(SearchConstants.REF_KEY, refKeyStr);
        searchParams.setFilter(refKeyFilter);

        // find the requested sequences for the ref id
        searchResults = sequenceFinder.getSequenceByRefID(searchParams);

        List<Sequence> seqList = searchResults.getResultObjects();
        if (seqList.size() < 1) {
            // forward to error page
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return mav;
        }

        ModelAndView mav = new ModelAndView("sequence_summaryByRefID");

        mav.addObject("seqList", seqList);
        mav.addObject("reference", ref);
        mav.addObject("refID", refID);

        return mav;
    }




}
