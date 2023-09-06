package org.jax.mgi.fewi.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.jax.mgi.fe.datamodel.BatchMarkerId;
import org.jax.mgi.fewi.finder.BatchFinder;
import org.jax.mgi.fewi.forms.BatchQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.BatchSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.UserMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /foo/ uri's
 */
@Controller
@RequestMapping(value="/batch")
public class BatchController {

	//--------------------//
	// instance variables
	//--------------------//

	private final Logger logger = LoggerFactory.getLogger(BatchController.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	private BatchFinder batchFinder;

	//--------------------------------------------------------------------//
	// public methods
	//--------------------------------------------------------------------//

	//--------------------//
	// Batch Query Form
	//--------------------//
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView getQueryForm(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("-> getQueryForm started");

		ModelAndView mav = new ModelAndView("batch_query");
		mav.addObject("sort", new Paginator());
		mav.addObject(new BatchQueryForm());
		return mav;
	}

	//-------------------------//
	// Batch Query Form Summary
	//-------------------------//
	@RequestMapping("/summary")
	public ModelAndView batchSummary(HttpServletRequest request, HttpSession session, @ModelAttribute BatchQueryForm queryForm) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("-> batchSummary POST started");       
		logger.debug("queryString: " + request.getQueryString());		
		return processSummary(session, queryForm);
	}

	//-------------------------//
	// Batch Query Form Summary
	//-------------------------//
	@RequestMapping(value="/summary", method=RequestMethod.GET)
	public ModelAndView batchSummaryGet(HttpSession session, HttpServletRequest request, @ModelAttribute BatchQueryForm queryForm,Model model) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug(model.toString());
		logger.debug("-> batchSummary GET started");        
		return processSummary(session, queryForm);
	}

	//-------------------------//
	// Forward from other page/form
	//-------------------------//
	//@RequestMapping(value="/forwardSummary", method=RequestMethod.GET)
	@RequestMapping(value="/forwardSummary")
	public ModelAndView batchSummary2Get(HttpSession session,HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}


		BatchQueryForm queryForm = (BatchQueryForm) request.getAttribute("queryForm");
		logger.debug("-> batchSummary GET started");        
		return processSummary(session, queryForm);
	}

	/* If 'mpFile' is non-null and non-empty, extracts IDs from it based
	 * on the specified 'column' and 'fileType'.  If 'mpFile' is null,
	 * falls abck on set of IDs in 'ids'.
	 */
	protected List<String> getIDList (BatchQueryForm queryForm) {
		List<String> idList = null; 

		idList = parseIds(queryForm.getIds());
		
		// remove an odd Unicode character from each ID, if it exists (shows up from copy & paste on
		// some systems)  Then do generic sanitizing on them.
		List<String> cleanedIDs = new ArrayList<String>();
		for (String id : idList) {
			cleanedIDs.add(FewiUtil.sanitizeSymbol(id.replaceAll("\uFEFF", "")));
		}
		
		return cleanedIDs;
	}

	private ModelAndView processSummary (HttpSession session, BatchQueryForm queryForm){

		// logger.info(queryForm.toString());

		session.removeAttribute("idSet");        
		logger.debug("processSummary:sessionId: " + session.getId());

		List<String> idList = getIDList(queryForm);

		if (idList != null && idList.size() > 0){
			logger.debug("new id set: " + idList.iterator().next());
			session.setAttribute("idSet", idList);
			logger.debug("session timeout="+session.getMaxInactiveInterval());
			//session.setMaxInactiveInterval(1);
			queryForm.setIds(StringUtils.join(idList, "\n"));
		}
		int inputIdCount = idList != null ? idList.size() : 0;

		ModelAndView mav = new ModelAndView("batch_summary");
		System.out.println("queryString: " + queryForm.toQueryString());
		mav.addObject("queryString", queryForm.toQueryString());
		mav.addObject("batchQueryForm", queryForm);
		if (idList != null && idList.size() > 0) {
			StringBuffer ids = new StringBuffer();
			ids.append("[");
			for(String id : idList) {
				ids.append("\"" + FewiUtil.sanitizeID(id) + "\",");
			}
			ids.append("]");
			mav.addObject("markerIds", ids.toString());
		}
		mav.addObject("inputIdCount", inputIdCount);
		if (queryForm.isFromQueryForm()) {
			mav.addObject("isFromQueryForm", "true");
		}
		logger.debug("processSummary done");
		return mav;

	}


	//----------------------//
	// JSON summary results
	//----------------------//
	@RequestMapping("/json")
	public @ResponseBody JsonSummaryResponse<BatchSummaryRow> batchSummaryJson(HttpSession session, HttpServletRequest request, HttpServletResponse response, @ModelAttribute BatchQueryForm queryForm, @ModelAttribute Paginator page) {

		logger.debug("-> JsonSummaryResponse started");
		AjaxUtils.prepareAjaxHeaders(response);
		// perform query, and pull out the requested objects
		SearchResults<BatchMarkerId> searchResults = getSummaryResults(session, request, queryForm, page);
		List<BatchMarkerId> markerList = searchResults.getResultObjects();

		// create/load the list of SummaryRow wrapper objects
		List<BatchSummaryRow> summaryRows = new ArrayList<BatchSummaryRow>(); 
		logger.debug("json results: " + markerList.size());
		for (BatchMarkerId marker: markerList){
			if (marker == null) {
				logger.debug("--> Null Object");
			}else {
				summaryRows.add(new BatchSummaryRow(marker, queryForm));
			} 	
		}
		logger.debug("total: " + searchResults.getTotalCount());
		// The JSON return object will be serialized to a JSON response.
		// Client-side JavaScript expects this object
		JsonSummaryResponse<BatchSummaryRow> jsonResponse = new JsonSummaryResponse<BatchSummaryRow>();

		// place data into JSON response, and return
		jsonResponse.setSummaryRows(summaryRows);
		jsonResponse.setTotalCount(searchResults.getTotalCount());
		jsonResponse.setMeta(searchResults.getResultSetMeta());
		logger.debug("json -> complete!");
		return jsonResponse;
	}
	
	@RequestMapping("/idList")
	public @ResponseBody String batchSummaryIdList(HttpSession session, HttpServletRequest request, @ModelAttribute BatchQueryForm queryForm, @ModelAttribute Paginator page) {
		
		
		logger.debug("batchSummaryIdList:sessionId: " + session.getId());

		SearchParams params = new SearchParams();

		logger.debug("page: " + page);

		@SuppressWarnings("unchecked")
		List<String> idSet = (ArrayList<String>)session.getAttribute("idSet");
		if(idSet == null)
		{
			idSet = parseIds(queryForm.getIds());
		}
		
		SearchResults<BatchMarkerId> searchResults = null;
		
		if (idSet != null && idSet.size()>0){
			//logger.debug("ids: " + idSet.size());
			// generate params object;  add pagination, sorts, and filters
			params.setPaginator(Paginator.ALL_PAGES);
			//params.setSorts(genSorts(request));
			params.setFilter(genFilters(queryForm, idSet));
			// perform query, and pull out the requested objects
			searchResults = batchFinder.getBatch(params);
		} else {
			//logger.debug("no idSet");
			searchResults = new SearchResults<BatchMarkerId>();
		}
		
		ArrayList<String> list = new ArrayList<String>();
		for(BatchMarkerId marker: searchResults.getResultObjects()) {
			if (marker != null && marker.getMarker() != null) {
				list.add(marker.getMarker().getPrimaryID());
			} else {
				list.add(marker.getTerm());
			}
		}
		
		return StringUtils.join(list, ",");
	}

	public List<String> getMarkerIDs(BatchQueryForm form, List<String> idSet) {
		logger.debug("in BatchController.getMarkerIDs()");
		SearchParams params = new SearchParams();
		params.setPaginator(Paginator.ALL_PAGES);
		params.setFilter(genFilters(form, idSet));
		return batchFinder.getMarkerIDs(params);
	}

	/*
	 * Testing asynchronous processing in Spring 3.2
	 */
	@RequestMapping("/report*")
	public ModelAndView batchSummaryReport(final HttpServletRequest request, final HttpSession session,
			final @ModelAttribute BatchQueryForm queryForm, final @ModelAttribute Paginator page) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("batchSummaryReport");
		//logger.debug(queryForm.toString());
		// perform query, and pull out the requested objects
		SearchResults<BatchMarkerId> searchResults = getSummaryResults(session, request, queryForm, page);

		logger.debug("# of report results: " + searchResults.getResultObjects().size());

		ModelAndView mav = new ModelAndView("batchSummaryReport");
		mav.addObject("queryForm", queryForm);
		mav.addObject("totalCount", searchResults.getTotalCount());
		mav.addObject("markerCount", searchResults.getResultSetMeta().getCount("marker"));		
		mav.addObject("results", searchResults.getResultObjects());
		mav.addObject("sessionFactory", sessionFactory);
		return mav;

	}

	private SearchResults<BatchMarkerId> getSummaryResults(HttpSession session, HttpServletRequest request, BatchQueryForm query, Paginator page){
		logger.debug("getSummaryResults:sessionId: " + session.getId());

		SearchParams params = new SearchParams();

		logger.debug("page: " + page);

		@SuppressWarnings("unchecked")
		List<String> idSet = (ArrayList<String>)session.getAttribute("idSet");
		if(idSet == null)
		{
			idSet = parseIds(query.getIds());
		}
		if (idSet != null && idSet.size()>0){
			logger.debug("ids: " + idSet.size());
			// generate params object;  add pagination, sorts, and filters
			params.setPaginator(page);
			params.setSorts(genSorts(request));
			params.setFilter(genFilters(query, idSet));
			// perform query, and pull out the requested objects
			return batchFinder.getBatch(params);
		} else {
			logger.debug("no idSet");
		}        
		return new SearchResults<BatchMarkerId>();
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
	private Filter genFilters(BatchQueryForm query, List<String> idSet){

		logger.debug("->genFilters started");
		logger.debug("QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();

		// logger.debug("set ids");
		if (idSet.size() > 0){
			filterList.add(new Filter(SearchConstants.BATCH_TERM, idSet, Filter.Operator.OP_IN));
		}
		//logger.debug("set type");
		String idType = query.getIdType();
		if (idType != null && !"".equals(idType) && !"auto".equalsIgnoreCase(idType)){
			logger.debug(idType);
			filterList.add(new Filter(SearchConstants.BATCH_TYPE, query.getIdType().trim(), Filter.Operator.OP_EQUAL));
		}

		//logger.debug("build");
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		}

		//logger.debug("done");
		return containerFilter;
	}

	private List<String> parseColumn(String data, int column, String delimiter) {

		int maxRows = 200000;
		// hold strings from parsed column
		Set<String> parsedIds = new LinkedHashSet<String>();

		// convert mac \c to \n and split lines into rows
		String[] rows = data.replaceAll("\r", "\n").split("\n");
		// column cells
		String[] cols;

		// If a column is requested, loop through the rows and parse the
		// contents of the requested column.
		if (column > 0) {
			// holds contents of requested column
			String col;
			for (int i = 0; i < rows.length; i++) {
				if(i>maxRows) break; // make sure there is some limit to how much we will process

				if (!delimiter.equals("")) {
					// split cols by delimiter
					cols = rows[i].split(delimiter);
				} else {
					// no delimiter, col is entire row
					cols = new String[] { rows[i] };
				}
				if (cols.length >= column) {
					// trim column cell
					col = cols[column - 1].trim();
					// trim trailing comma
					if (col.endsWith(",")) {
						col = col.substring(0, ( col.length() - 1) );
					}
					// ignore blank cell
					if (!col.equals("")) {
						parsedIds.add(col);
					}
				}
			}
		}
		// return array of parsed column contents
		return new ArrayList<String>(parsedIds);
	}

	private List<String> parseIds(String data) {

		if(data==null) return new ArrayList<String>();

		Set<String> parsedIds = new LinkedHashSet<String>();

		StreamTokenizer tok = new StreamTokenizer(new StringReader(data));
		tok.resetSyntax();

		tok.wordChars(35, 126);
		tok.quoteChar('"');
		tok.whitespaceChars(0, ' ');

		try {
			String s = "";
			while(tok.nextToken() != StreamTokenizer.TT_EOF){
				if (tok.ttype == StreamTokenizer.TT_NUMBER){
					s = new Double(tok.nval).toString();
				} else if (tok.ttype == StreamTokenizer.TT_WORD) {
					s = tok.sval;
				} else {
					s = tok.toString().trim();
					s =  s.substring(
							s.indexOf('[') + 1,
							s.lastIndexOf(']'));
				}
				if (s.endsWith(",")) {
					s = s.substring(0,s.length()-1);
				}
				if (s != null) 
				{
					s=s.trim();
					if(!"".equals(s)) parsedIds.add(s);
				}
			}
		} catch (IOException e) {
			logger.error("IOException parsing batch query input",e);
			e.printStackTrace();
		}
		return new ArrayList<String>(parsedIds);
	}
}
