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
import javax.servlet.http.HttpSession;

import mgi.frontend.datamodel.Annotation;
import mgi.frontend.datamodel.BatchMarkerAllele;
import mgi.frontend.datamodel.BatchMarkerId;
import mgi.frontend.datamodel.ExpressionAssay;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerAlleleAssociation;
import mgi.frontend.datamodel.MarkerTissueCount;

import org.apache.commons.io.IOUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
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

    private Logger logger
      = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private BatchFinder batchFinder;
    

    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // Foo Query Form
    //--------------------//
    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView getQueryForm() {

        logger.debug("-> getQueryForm started");

        ModelAndView mav = new ModelAndView("batch_query");
        mav.addObject("sort", new Paginator());
        mav.addObject(new BatchQueryForm());
        return mav;
    }


    //-------------------------//
    // Foo Query Form Summary
    //-------------------------//
    @RequestMapping("/summary")
    public ModelAndView batchSummary(MultipartHttpServletRequest request,
    		HttpSession session,
            @ModelAttribute BatchQueryForm queryForm) {

        logger.debug("-> batchSummary POST started");       
		logger.debug("queryString: " + request.getQueryString());		
		return processSummary(session, queryForm);
    }
    
    //-------------------------//
    // Foo Query Form Summary
    //-------------------------//
    @RequestMapping(value="/summary", method=RequestMethod.GET)
    public ModelAndView batchSummaryGet(HttpSession session,
            @ModelAttribute BatchQueryForm queryForm) {

        logger.debug("-> batchSummary GET started");        
        return processSummary(session, queryForm);
    }
    
    private ModelAndView processSummary (HttpSession session,
            BatchQueryForm queryForm){

        logger.info(queryForm.toString());
        
        session.removeAttribute("idSet");        
        logger.debug("sessionId: " + session.getId());
        
        Set<String> idSet = null; 
        MultipartFile file = queryForm.getIdFile();

        if (file != null && !file.isEmpty()){        	
            String sep = "";
            String fileType = queryForm.getFileType();
            if (fileType != null && "".equals("cvs")) {
            	sep = ",";             
            } else {
                sep = "\t";
            }
            
            Integer col = queryForm.getIdColumn();

            InputStream idStream;
            StringWriter writer = new StringWriter();
            try {
            	idStream = (InputStream) file.getInputStream();
    			IOUtils.copy(idStream , writer);
    			idSet = parseColumn(writer.toString(), col, sep);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        	    	
        } else {     	
        	idSet = parseIds(queryForm.getIds());
        }

		if (idSet != null && idSet.size() > 0){
			session.setAttribute("idSet", new ArrayList<String>(idSet));
		}  
        ModelAndView mav = new ModelAndView("batch_summary");
        mav.addObject("queryString", queryForm.toQueryString());
        mav.addObject("batchQueryForm", queryForm);
        mav.addObject("inputIdCount", idSet.size());
        return mav;
    	
    }


    //----------------------//
    // JSON summary results
    //----------------------//
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<BatchSummaryRow> batchSummaryJson(
    		HttpSession session,
    		HttpServletRequest request,
			@ModelAttribute BatchQueryForm queryForm,
            @ModelAttribute Paginator page) {

        logger.debug("-> JsonSummaryResponse started");

        // perform query, and pull out the requested objects
        SearchResults<BatchMarkerId> searchResults = 
        	getSummaryResults(session, request, queryForm, page);
        List<BatchMarkerId> markerList = searchResults.getResultObjects();

        // create/load the list of SummaryRow wrapper objects
        List<BatchSummaryRow> summaryRows = new ArrayList<BatchSummaryRow>();
        BatchSummaryRow row;
        List<BatchSummaryRow> rows = new ArrayList<BatchSummaryRow>();
        Marker m;
        for (BatchMarkerId marker: markerList){
        	logger.debug("marker: " + marker.getTerm());
        	m = marker.getMarker();

        	if (queryForm.getAllele() && m.getBatchMarkerAlleles().size() > 0) {
        		for (BatchMarkerAllele a:
        			m.getBatchMarkerAlleles()){
        			row = new BatchSummaryRow(marker, queryForm);
        			row.setMarkerAllele(a);
        			rows.add(row);
        		}
        	} else if (queryForm.getGo() && m.getGoAnnotations().size() > 0) {
        		for (Annotation a: m.getGoAnnotations()){
        			row = new BatchSummaryRow(marker, queryForm);
        			row.setGoAnnot(a);
        			rows.add(row);
        		}
        	} else if (queryForm.getOmim() && m.getOMIMAnnotations().size() > 0) {
        		for (Annotation omim: m.getOMIMAnnotations()){
        			row = new BatchSummaryRow(marker, queryForm);
        			row.setOmimAnnot(omim);
        			rows.add(row);
        		}
        	} else if (queryForm.getMp() && m.getMPAnnotations().size() > 0) {
        		for (Annotation mp: m.getMPAnnotations()){
        			row = new BatchSummaryRow(marker, queryForm);
        			row.setMpAnnot(mp);
        			rows.add(row);
        		}
        	} else if (queryForm.getExp() && m.getMarkerTissueCounts().size() > 0) {
        		for (MarkerTissueCount ex: m.getMarkerTissueCounts()){
        			row = new BatchSummaryRow(marker, queryForm);
        			row.setExpCount(ex);
        			rows.add(row);
        		}
        	} else {
        		row = new BatchSummaryRow(marker, queryForm);
        		rows.add(row);
        	}
            summaryRows.addAll(rows);
 	
        }

        // The JSON return object will be serialized to a JSON response.
        // Client-side JavaScript expects this object
        JsonSummaryResponse<BatchSummaryRow> jsonResponse
          = new JsonSummaryResponse<BatchSummaryRow>();

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        jsonResponse.setMeta(searchResults.getResultSetMeta());
        logger.debug("json -> complete!");
        return jsonResponse;
    }

	@RequestMapping("/report*")
	public ModelAndView batchSummaryReport(
			HttpServletRequest request,
			HttpSession session,
			@ModelAttribute BatchQueryForm queryForm,
            @ModelAttribute Paginator page) {
				
		logger.debug("batchSummaryReport");		
		ModelAndView mav = new ModelAndView("batchSummaryReport");
		mav.addObject("results", getSummaryResults(session, request, queryForm, page).getResultObjects());
		return mav;
	}
	
	private SearchResults<BatchMarkerId> getSummaryResults(    		
			HttpSession session,
    		HttpServletRequest request,
			BatchQueryForm query,
            Paginator page){

        logger.debug("sessionId: " + session.getId());
        
        List<String> idSet = (ArrayList<String>)session.getAttribute("idSet");
        if (idSet != null){
        	logger.debug("ids: " + idSet.size());
        } else {
        	logger.debug("no idSet");
        }

        // generate search parms object;  add pagination, sorts, and filters
        SearchParams params = new SearchParams();
        params.setPaginator(page);
        params.setSorts(this.genSorts(request));
        params.setFilter(this.genFilters(query, idSet));

        // perform query, and pull out the requested objects
         return batchFinder.getBatch(params);
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
        
        if (idSet.size() > 0){
        	filterList.add(new Filter(SearchConstants.BATCH_TERM, idSet, Filter.OP_IN));
        }
        String idType = query.getIdType();
        if (idType != null && !"".equals(idType) && !"auto".equals(idType)){
        	filterList.add(new Filter(SearchConstants.BATCH_TYPE, query.getIdType().trim(), Filter.OP_EQUAL));
        }
        
        // if we have filters, collapse them into a single filter
        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.FC_AND);
            containerFilter.setNestedFilters(filterList);
        }

        return containerFilter;
    }

    private Set<String> parseColumn(String data, int column, String delimiter) {

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
                    	parsedIds.add(col.toLowerCase());
                    }
                }
            }
        }
        // return array of parsed column contents
        return parsedIds;
    }
    
    private Set<String> parseIds(String data) {
    	
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
	            	s = s.substring(0, ( s.length() - 1) );
	            }
				if (s != null && !s.trim().equals("")) {
					parsedIds.add(s.trim());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parsedIds;
    }
}
