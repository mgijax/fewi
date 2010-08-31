package org.jax.mgi.fewi.controller;

import java.util.*;

// fewi & data model objects
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
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

//	@RequestMapping(method=RequestMethod.GET)
//	public ModelAndView getQueryForm() {
//
//		ModelAndView mav = new ModelAndView("sequenceQuery");
//
//		return mav;
//	}


    /////////////////////////////////////////////////////////////////////////
    //  Sequence Detail
    /////////////////////////////////////////////////////////////////////////

	@RequestMapping(value="/{seqID}", method = RequestMethod.GET)
	public ModelAndView seqDetail(@PathVariable("seqID") String seqID) {

		SearchParams searchParams = new SearchParams();

		Filter seqIdFilter = new Filter(SearchConstants.SEQ_ID, seqID);
        searchParams.setFilter(seqIdFilter);

        sequenceFinder.getSequenceByID(searchParams);


		ModelAndView mav = new ModelAndView("sequence_detail");

		mav.addObject("seqID", seqID);

		return mav;
	}


    /////////////////////////////////////////////////////////////////////////
    //  Sequence Summary by Reference
    /////////////////////////////////////////////////////////////////////////

	@RequestMapping(value="/reference/{refID}", method = RequestMethod.GET)
	public ModelAndView seqSummeryByRef(@PathVariable("refID") String refID) {


		ModelAndView mav = new ModelAndView("sequence_summaryByRefID");

		mav.addObject("refID", refID);

		return mav;
	}




}
