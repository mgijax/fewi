package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Reference;

import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.ReferenceQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.MySortPaginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/reference")
public class ReferenceController {
		
	private Logger logger = LoggerFactory.getLogger(ReferenceController.class);
	
	@Autowired
	private ReferenceFinder referenceFinder;

	@RequestMapping(method=RequestMethod.GET)
	public String getQueryForm(Model model) {
		model.addAttribute(new ReferenceQueryForm());
		model.addAttribute("sort", new MySortPaginator());
		return "reference_query";
	}
	
	@RequestMapping("/summary")
	public String referenceSummary(HttpServletRequest request, Model model) {

		model.addAttribute("queryString", request.getQueryString());
		logger.debug("queryString: " + request.getQueryString());

		return "reference_summary";
	}
	
	@RequestMapping("/json")
	public @ResponseBody SearchResults<Reference> referenceSummaryJson(
			@ModelAttribute ReferenceQueryForm query,
			@ModelAttribute MySortPaginator mySort) {
				
		logger.debug("mysort: " + mySort.toString());
		
		SearchParams params = new SearchParams();
		List<Sort> sorts = new ArrayList<Sort>();
		
		String sortStr = mySort.getSort();
		if (sortStr == null || "".equals(sortStr)){
			mySort.setSort("journal");
		}
		
		Sort sort = new Sort(mySort.getSort());
		if (mySort.getDir().equals("asc")){
			sort.setDesc(true);
		} else {
			sort.setDesc(false);
		}
		sorts.add(sort);
		params.setSorts(sorts);
		
		params.setStartIndex(mySort.getStartIndex());
		params.setPageSize(mySort.getResults());
		
		List<Filter> filterList = new ArrayList<Filter>();
		
		if(query.getAuthor() != null && !"".equals(query.getAuthor())){
			List<String> authors = Arrays.asList(query.getAuthor().split(";"));
			logger.debug("authors: ", authors.toArray());
			String scope = query.getAuthorScope();
			if ("first".equals(scope)){
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_FIRST, authors, Filter.OP_IN));
			} else if ("last".equals(scope)){
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_LAST, authors, Filter.OP_IN));
			} else {
				filterList.add(new Filter(SearchConstants.REF_AUTHOR_ANY, authors, Filter.OP_IN));
			}
		}

		if(query.getJournal() != null && !"".equals(query.getJournal())){
			List<String> journals = Arrays.asList(query.getJournal().split(";"));
			filterList.add(new Filter(SearchConstants.REF_JOURNAL, journals, Filter.OP_EQUAL));
		}
		
		if(query.getYear() != null ){
			filterList.add(new Filter(SearchConstants.REF_YEAR, query.getYear().toString(), Filter.OP_EQUAL));
		}
		
		if(query.getText() != null && !"".equals(query.getText())){
			Filter f = new Filter();
			List<Filter> textFilters = new ArrayList<Filter>();
			
			String text = query.getText();
			if(query.isInAbstract()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_ABSTRACT, text, Filter.OP_EQUAL));
			}
			if(query.isInTitle()){
				textFilters.add(new Filter(SearchConstants.REF_TEXT_TITLE, text, Filter.OP_EQUAL));
			}
			if (textFilters.size() == 1) {
				filterList.add(textFilters.get(0));
			} else {
				f.setFilterJoinClause(Filter.FC_OR);
				f.setNestedFilters(textFilters);
				filterList.add(f);
			}
		}
		
		if (filterList.size() > 0){
			Filter f = new Filter();
			f.setFilterJoinClause(Filter.FC_AND);
			f.setNestedFilters(filterList);
			params.setFilter(f);
		} else if (query.getId() != null && !"".equals(query.getId())){
			List<String> ids = Arrays.asList(query.getId().split(";"));	
			params.setFilter(new Filter(SearchConstants.REF_ID, ids, Filter.OP_EQUAL));
		}

		return referenceFinder.searchReferences(params);
	}

}
