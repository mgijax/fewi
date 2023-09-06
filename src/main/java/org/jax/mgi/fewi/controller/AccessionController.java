package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fewi.summary.Accession;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.AccessionFinder;
import org.jax.mgi.fewi.forms.AccessionQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.ObjectTypes;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.AccessionSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.FewiLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /accession/ uri's
 */
@Controller
@RequestMapping(value="/accession")
public class AccessionController {


	//--------------------//
	// instance variables
	//--------------------//

	private final Logger logger = LoggerFactory.getLogger(AccessionController.class);

	@Autowired
	private AccessionFinder accessionFinder;

	//--------------------------------------------------------------------//
	// public methods
	//--------------------------------------------------------------------//

	//--------------------//
	// Accession Query Form
	//--------------------//
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}


		logger.debug("->getQueryForm started");

		ModelAndView mav = new ModelAndView("accession_query");
		mav.addObject("sort", new Paginator());
		mav.addObject(new AccessionQueryForm());
		return mav;
	}

	//-----------------------------//
	// Accession Query Form Summary
	//-----------------------------//
	@RequestMapping("/summary")
	public ModelAndView accessionSummary(HttpServletRequest request,
			@ModelAttribute AccessionQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->accessionSummary main started");
		logger.debug("queryString: " + request.getQueryString());

		// For accession we need to search twice, once before the summary
		// page is run, and once during the json.  If we only get back a 
		// single object, we want to go directly to the object page.

		SearchParams params = new SearchParams();
		params.setFilter( genFilters(queryForm));       
		params.setSorts(genSorts(request));

		SearchResults<Accession> searchResults = accessionFinder.getAccessions(queryForm.getId());
		logger.debug("About to check the size");

		ModelAndView mav;
		if (searchResults.getResultObjects().size() == 0){
			mav = new ModelAndView("error");
			mav.addObject("errorMsg", "No accession id found.  Please verify " +
					"that your request contains an id parameter.");

		} else if (searchResults.getResultObjects().size() == 1) {
			// If only a single object comes back, forward to the proper page.
			logger.debug("Found only 1, should be forwarding.");
			// We only have one object, seamlessly forward it!
			FewiLinker linker = FewiLinker.getInstance();
			Accession acc = searchResults.getResultObjects().get(0);
			String url = "";

			String objectType = acc.getObjectType();

			// Handle the Vocabulary Cases        	
			if (objectType.equals("Vocabulary Term")) { 
				logger.debug("This is a vocab match, should be a forward");
				url = linker.getFewiIDLink(acc.getDisplayType(), acc.getDisplayID());        		
			}

			// Handle the old wi cases, but with ID        	
			else if (objectType.equals(ObjectTypes.HOMOLOGY) ||
					objectType.equals(ObjectTypes.ASSAY) ||
					objectType.equals(ObjectTypes.MARKER_CLUSTER)) {
				url = linker.getFewiIDLink(objectType, acc.getDisplayID());
			}

			// Handle the old wi cases.        	
			else if (objectType.equals(ObjectTypes.GO_CC) ||
					objectType.equals(ObjectTypes.GO_MF) ||
					objectType.equals(ObjectTypes.GO_BP)) {
				logger.debug("Old WI Case");
				url = linker.getFewiKeyLink(objectType, "" + acc.getObjectKey());
			}

			// error case - antigen links now unsupported.
			else if (objectType.equals(ObjectTypes.ANTIGEN)) {
				mav = new ModelAndView("error");
				mav.addObject("errorMsg",
						"Links to antigens are no longer supported");
				return mav; 
			}

			else {
				logger.debug("Base case.");
				url = linker.getFewiIDLink(acc.getObjectType(), acc.getDisplayID());        			
			}
			// check if linker failed to match link type
			if(url==null || url.equals("") || url.contains("not available yet"))
			{
				mav = new ModelAndView("error");
				mav.addObject("errorMsg", "Link Type " + objectType + " not available yet");
				return mav;
			}
			;
			return new ModelAndView("redirect:" + url);
		} else {
			mav = new ModelAndView("accession_summary");

			String queryString = request.getQueryString();
			if (queryString == null || queryString.equals("")) {
				queryString = "id=" + queryForm.getId();
			}	        
			mav.addObject("queryString", queryString);
			mav.addObject("queryForm", queryForm);
			logger.debug("Going to summary for "
					+ searchResults.getResultObjects().size()
					+ " matches to " + queryForm.getId() );
		}
		return mav;
	}

	//----------------------//
	// JSON summary results
	//----------------------//
	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<AccessionSummaryRow> accessionSummaryJson(
			HttpServletRequest request,
			@ModelAttribute AccessionQueryForm queryForm,
			@ModelAttribute Paginator page) {

		logger.debug("->JsonSummaryResponse started");

		// generate search parms object;  add pagination, sorts, and filters
		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(request));
		params.setFilter(genFilters(queryForm));

		// perform query, and pull out the requested objects
		SearchResults<Accession> searchResults = accessionFinder.getAccessions(queryForm.getId());
		List<Accession> fooList = searchResults.getResultObjects();

		// create/load the list of SummaryRow wrapper objects

		logger.debug("Making the summary rows");

		Map <String, Integer> typeCount = new HashMap<String, Integer>();

		List<AccessionSummaryRow> summaryRows = new ArrayList<AccessionSummaryRow> ();
		Iterator<Accession> it = fooList.iterator();
		while (it.hasNext()) {
			Accession acc = it.next();
			if (acc == null) {
				logger.debug("--> Null Object");
			} else {
				if (!typeCount.containsKey(acc.getObjectType())) {
					typeCount.put(acc.getObjectType(), 0);
				}
				typeCount.put(acc.getObjectType(), typeCount.get(acc.getObjectType()) + 1);
				summaryRows.add(new AccessionSummaryRow(acc));
			}
		}

		for (AccessionSummaryRow row: summaryRows) {
			if (typeCount.get(row.getObjectType()) > 1) {
				row.setUseKey();
			}
		}


		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<AccessionSummaryRow> jsonResponse
		= new JsonSummaryResponse<AccessionSummaryRow>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		return jsonResponse;
	}

	//-----------------------------//
	// Accession By ID
	//-----------------------------//
	@RequestMapping("/{accID}")
	public ModelAndView accessionSummaryByID(@PathVariable("accID") String accID,
			HttpServletRequest request,
			@ModelAttribute AccessionQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("->accessionSummary by ID started");
		logger.debug("queryString: " + request.getQueryString());

		
		// This is a poor solution these ID's need to get propagated through
		// But with only a few hours to implement here it is.
		if(accID.toLowerCase().startsWith("rrid:")) {
			accID = accID.substring(5);
		}
		
		queryForm.setId(accID);
		logger.debug("Modified Query Form: " + queryForm.toString());

		request.setAttribute("queryString", "id=" + accID);

		return accessionSummary(request, queryForm);
	}

	@RequestMapping("/doi")
	public ModelAndView accessionSummaryByDoiParam(@RequestParam("id") String accID,
			HttpServletRequest request,
			@ModelAttribute AccessionQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("-> accessionSummaryByIDParam started");
		logger.debug("queryString: " + request.getQueryString());

		queryForm.setId(accID);
		SearchParams params = new SearchParams();
		params.setFilter( genFilters(queryForm));       
		params.setSorts(genSorts(request));

		SearchResults<Accession> searchResults = accessionFinder.getAccessions(accID);

		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", "No accession id found.  Please verify " +
				"that your request contains an id parameter.");
		if (searchResults.getResultObjects().size() == 1) {
			mav = new ModelAndView("reference_summary");

			if (searchResults.getResultObjects().size() == 1){
				String url = String.format("redirect:%sreference/key/%d", 
						ContextLoader.getConfigBean().getProperty("FEWI_URL"),
						searchResults.getResultObjects().get(0).getObjectKey());
				mav = new ModelAndView(url);
			}	        
		}
		return mav;
	}

	//--------------------------------------------------------------------//
	// private methods
	//--------------------------------------------------------------------//

	// generate the sorts
	private List<Sort> genSorts(HttpServletRequest request) {

		logger.debug("->genSorts started");

		List<Sort> sorts = new ArrayList<Sort>();

		// retrieve requested sort order; set default if not supplied
		String sortRequested = request.getParameter("sort");
		if (sortRequested == null) {
			sortRequested = SortConstants.FOO_SORT;
		}

		String dirRequested  = request.getParameter("dir");
		boolean desc = false;
		if("desc".equalsIgnoreCase(dirRequested)){
			desc = true;
		}

		Sort sort = new Sort(sortRequested, desc);
		sorts.add(sort);

		logger.debug ("sort: " + sort.toString());
		return sorts;
	}

	// generate the filters
	private Filter genFilters(AccessionQueryForm query){
		logger.debug("->genFilters started");
		logger.debug("QueryForm -> " + query);

		String accId = query.getId();
		// There can ONLY be accession at present, add it in.
		if (accId != null && !"".equals(accId.trim())) {
			accId = accId.trim().toLowerCase();
			return new Filter(SearchConstants.ACC_ID, accId.trim(), Filter.Operator.OP_EQUAL);
		}
		return new Filter();
	}
}
