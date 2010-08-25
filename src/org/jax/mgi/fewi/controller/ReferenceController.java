package org.jax.mgi.fewi.controller;

import java.util.*;

// fewi & data model objects
//import org.jax.mgi.fewi.finder.SequenceFinder;
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
@RequestMapping(value="/reference")
public class ReferenceController {

	private Logger logger = LoggerFactory.getLogger(ReferenceController.class);

//	@Autowired
//	private SequenceFinder sequenceFinder;

	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm() {

		ModelAndView mav = new ModelAndView("referenceQueryForm");

		return mav;
	}


	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ModelAndView refDetail(@PathVariable("id") String refID) {


		ModelAndView mav = new ModelAndView("referenceDetail");

		mav.addObject("refID", refID);

		return mav;
	}




}
