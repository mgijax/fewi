package mgi.frontend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Reference;
import mgi.frontend.finder.MarkerFinder;
import mgi.frontend.results.MarkerResults;

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
@RequestMapping(value="/marker")
public class MarkerController {	
	
	private Logger logger = LoggerFactory.getLogger(MarkerController.class);
	
	@Autowired
	private MarkerFinder markerFinder;
	
	private @Value("#{jdbc.foo}") String solr;
	private @Value("${foo.url}") String s2;

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model) {
		MarkerQuery mq = new MarkerQuery();
		mq.addChromosome("Any");
		
		List<String> chromosomes = new ArrayList<String>();
		chromosomes.add("Any");
		chromosomes.add("1");
		chromosomes.add("2");
		chromosomes.add("3");
		chromosomes.add("4");
		chromosomes.add("5");
		chromosomes.add("6");
		chromosomes.add("7");
		chromosomes.add("8");
		chromosomes.add("9");
		chromosomes.add("10");
		chromosomes.add("11");
		chromosomes.add("12");
		chromosomes.add("13");
		chromosomes.add("14");
		chromosomes.add("15");
		chromosomes.add("16");
		chromosomes.add("17");
		chromosomes.add("18");
		chromosomes.add("19");
		chromosomes.add("X");
		chromosomes.add("Y");
		chromosomes.add("XY");
		chromosomes.add("MT");
		mq.setChrOptions(chromosomes);
		List<String> coordOptions = new ArrayList<String>();
		coordOptions.add("bp");
		coordOptions.add("Mbp");
		mq.setCoordOptions(coordOptions);
		model.addAttribute(mq);
		return "marker_query";
	}
	
	@RequestMapping("/summary")
	public ModelAndView markerSummary(HttpServletRequest request ) {
		
		// the view for this request
		ModelAndView mav = new ModelAndView("marker_list");
		mav.addObject("queryString", request.getQueryString());
		logger.info("queryString: " + request.getQueryString());
		logger.info("test: " + solr + " " + s2);
		return mav;
	}
	
	@RequestMapping("/json")
	public @ResponseBody Map<String, Object> markerSummaryJson(@ModelAttribute MarkerQuery query,
			HttpServletRequest request) {
		
		logger.info("json");
		logger.debug(query.toString());
		
		Map<String, Object> mr = new HashMap<String, Object>();
		MarkerResults results = this.markerFinder.searchMarkers(query);

		logger.debug("returning data");
		
		mr.put("maxCount", results.getMaxCount());
		mr.put("startIndex", query.getStartIndex());
		mr.put("pageSize", query.getResults());
		mr.put("sort", query.getSort());
		mr.put("dir", query.getDir());
		mr.put("markerList", markerFinder.getMarkersByID(results.getMarkerIDs()));

		return mr;
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public ModelAndView markerDetail(@PathVariable("id") int id) {
		logger.debug("marker detail");
		// lookup marker and return to default view
		return new ModelAndView("marker_detail", "marker", 
				this.markerFinder.getMarkerByID(id));
	}
	

	
	@RequestMapping(value="/refernce/{id}", method = RequestMethod.GET)
	public ModelAndView markerReferences(@PathVariable("id") int id) {
		
		logger.debug("referenceMarkers");
		
		// the view for this request
		ModelAndView mav = new ModelAndView("reference_markers");
		// marker object for view
		Reference reference;

		logger.debug("run dao");
		// lookup reference
		//reference = this.referenceFinder.getReferenceByID(id);
		// unpack markers
		//List<MarkerReferenceAssociation> markerList = reference.getMarkerAssociations();

		// load models into view
		logger.debug("return view");
		//mav.addObject("reference", reference);
		//mav.addObject("markerList", markerList);
		
		//return to results to view 
		return mav;
	}

}
