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


private @Value("${foo.url}") String foourl;         //in fewi.prop
//private @Value("${BIOMART_URL}") String biomart;    //in fewi.prop
//private @Value("${hostname}") String silverURL;   //in GlobalConfig.prop

	@Autowired
	private SequenceFinder sequenceFinder;

    private String hostname;
    @Value("#{GlobalConfig.hostname}")
    public void setHostName(String hostname) {
        this.hostname = hostname;
    }

    /////////////////////////////////////////////////////////////////////////
    //  Sequence Detail
    /////////////////////////////////////////////////////////////////////////

	@RequestMapping(value="/{seqID}", method = RequestMethod.GET)
	public ModelAndView seqDetail(@PathVariable("seqID") String seqID) {

		// returned ModelAndView object;  dictates view to forward to
		ModelAndView mav = new ModelAndView("sequence_detail");

		// setup search parameters object
		SearchParams searchParams = new SearchParams();
		Filter seqIdFilter = new Filter(SearchConstants.SEQ_ID, seqID);
        searchParams.setFilter(seqIdFilter);

        // find the requested sequence
        SearchResults searchResults
          = sequenceFinder.getSequenceByID(searchParams);

        List<Sequence> seqList = searchResults.getResultObjects();

        if (seqList.size() > 1) {
			//TODO log error; exit; forward to summary
		}

        Sequence sequence = seqList.get(0);

        // package objects for view layer
		mav.addObject("seqID", seqID);
		mav.addObject("sequence", sequence);


System.out.println("--->" + foourl);
//System.out.println("--->" + hostname);


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
