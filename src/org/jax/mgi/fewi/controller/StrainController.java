package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.StrainFinder;
import org.jax.mgi.fewi.forms.StrainQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Strain;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /strain/ uri's
 */
@Controller
@RequestMapping(value="/strain")
public class StrainController {
    //--------------------//
    // static variables
    //--------------------//

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(StrainController.class);

    @Autowired
    private IDLinker idLinker;

	@Autowired
	private StrainFinder strainFinder;

	@Autowired
	private ReferenceFinder referenceFinder;

	private static SmartAlphaComparator smartAlphaComparator = new SmartAlphaComparator();
	
    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //--------------------//
    // strain detail page
    //--------------------//
    @RequestMapping(value="/{strainID:.+}", method = RequestMethod.GET)
    public ModelAndView getStrainDetailPage(@PathVariable("strainID") String strainID) {
        logger.debug("->getStrainDetailPage started");

        List<Strain> strainList = strainFinder.getStrainByID(strainID);
        // there can be only one...
        if (strainList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Strain Found for ID " + strainID);
            return mav;
        } else if (strainList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "ID " + strainID + " is associated with multiple strains");
            return mav;
        }
        // success - we have a single object

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("strain/strain_detail");
        
        //pull out the Probe, and add to mav
        Strain strain = strainList.get(0);
        mav.addObject("strain", strain);
//        addDetailSeo(strain, mav);

        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);
        return mav;
    }

    // add SEO data (seoDescription, seoTitle, and seoKeywords) to the given detail page's mav
/*    private void addDetailSeo (Strain p, ModelAndView mav) {
    	List<String> synonyms = p.getSynonyms();
    	
    	// identify high-level segment type (probe or primer)
    	
    	String highLevelSegmentType = "Probe";
    	if ("primer".equals(p.getSegmentType()) ) {
    		highLevelSegmentType = "Primer";
    	}
    	mav.addObject("highLevelSegmentType", highLevelSegmentType);
    	
    	// compose browser / SEO title
    	
    	StringBuffer seoTitle = new StringBuffer();
    	seoTitle.append(p.getName());
    	seoTitle.append(" ");
    	seoTitle.append(highLevelSegmentType);
    	seoTitle.append(" Detail MGI Mouse ");
    	seoTitle.append(p.getPrimaryID());
    	mav.addObject("seoTitle", seoTitle.toString());
    	
    	// compose set of SEO keywords
    	
    	StringBuffer seoKeywords = new StringBuffer();
    	seoKeywords.append(p.getName());
    	if (!"Probe".equals(highLevelSegmentType)) {		// already adding lowercase 'probe' below
    		seoKeywords.append(", ");
    		seoKeywords.append(highLevelSegmentType); 
    	}
    	if ((synonyms != null) && (synonyms.size() > 0)) {
    		for (String synonym : synonyms) {
    			seoKeywords.append(", ");
    			seoKeywords.append(synonym);
    		}
    	}
    	seoKeywords.append(", probe, clone, mouse, mice, murine, Mus");
    	mav.addObject("seoKeywords", seoKeywords);
    	
    	// compose SEO description
    	
    	StringBuffer seoDescription = new StringBuffer();
    	seoDescription.append("View ");
    	seoDescription.append(highLevelSegmentType);
    	seoDescription.append(" ");
    	seoDescription.append(p.getName());
    	seoDescription.append(" : location, molecular source, gene associations, expression, sequences, polymorphisms, and references.");
    	mav.addObject("seoDescription", seoDescription);
    }
*/
    private class AttributeComparator implements Comparator<String> {
		@Override
		public int compare(String arg0, String arg1) {
			// Some attributes begin with an asterisk, which we want to ignore for sorting purposes.
			String a = arg0;
			String b = arg1;
			if (arg0.startsWith("*")) { a = arg0.substring(1); }
			if (arg1.startsWith("*")) { b = arg1.substring(1); }
			return smartAlphaComparator.compare(a, b);
		}
    }
    
	// checks any cachable fields of the strain query form, and initializes them if needed
	public void initQFCache() {
		if (StrainQueryForm.getAttributeChoices() == null) {
			// get collection facets
			SearchParams sp = new SearchParams();
			sp.setPageSize(0);
			sp.setFilter(new Filter(SearchConstants.STRAIN_KEY,"[* TO *]",Filter.Operator.OP_HAS_WORD));

			SearchResults<SimpleStrain> sr = strainFinder.getAttributeFacet(sp);
			List<String> attributeChoices = sr.getResultFacets();
			Collections.sort(attributeChoices, new AttributeComparator());
			
			// remove four choices that we don't want to give the user
			attributeChoices.remove("mutant strain");
			attributeChoices.remove("mutant stock");
			attributeChoices.remove("Not Applicable");
			attributeChoices.remove("Not Specified");

			StrainQueryForm.setAttributeChoices(attributeChoices);
		}
	}

	// shell of the strain summary page (The actual results are retrieved via Ajax from the /table endpoint.)
    @RequestMapping("/summary")
    public ModelAndView strainSummary(HttpServletRequest request, @ModelAttribute StrainQueryForm queryForm) {
        logger.debug("In strainSummary, query string: " + request.getQueryString());

        // objects needed by display
        ModelAndView mav = new ModelAndView("strain/strain_summary");
        mav.addObject("strainQueryForm", queryForm);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("title", "Strain Summary");
        mav.addObject("ysf", getYouSearchedFor(queryForm));
        request.setAttribute("externalUrls", ContextLoader.getExternalUrls());
        request.setAttribute("configBean", ContextLoader.getConfigBean());

        return mav;
    }

	// shell of the strain summary page, coming from a reference 
    // (The actual results are retrieved via Ajax from the /table endpoint.)
    @RequestMapping(value="reference/{refID:.+}", method = RequestMethod.GET)
    public ModelAndView strainForReference(HttpServletRequest request,
    		@ModelAttribute StrainQueryForm queryForm,
    		@PathVariable("refID") String refID) {
        logger.debug("In strainForReference, ref ID: " + refID);
        queryForm.setReferenceID(refID);

        // look up the reference
        SearchResults<Reference> referenceSR = referenceFinder.getReferenceByID(refID);
        List<Reference> references = referenceSR.getResultObjects();
        if ((references == null) && (references.size() == 0)) {
        	return errorMav("No references match ID: " + refID);
        }
        
        String queryString = "referenceID=" + refID;
        if (request.getQueryString() != null) {
        	queryString = queryString + "&" + request.getQueryString().replaceAll("referenceID=[^&]*", "");
        }
        
        // objects needed by display
        ModelAndView mav = new ModelAndView("strain/strain_summary");
        mav.addObject("strainQueryForm", queryForm);
        mav.addObject("queryString", queryString);
        mav.addObject("title", "Strain Summary for Reference : " + refID);
        mav.addObject("reference", references.get(0));
        request.setAttribute("externalUrls", ContextLoader.getExternalUrls());
        request.setAttribute("configBean", ContextLoader.getConfigBean());

        return mav;
    }

	/* table of strain data for summary page, to be requested via Ajax
	 */
	@RequestMapping("/table")
	public ModelAndView strainTable (@ModelAttribute StrainQueryForm query, @ModelAttribute Paginator page) {

		logger.debug("->strainTable started");

		// perform query, and pull out the requested objects
		SearchResults<SimpleStrain> searchResults = getSummaryResults(query, page);
		List<SimpleStrain> strainList = searchResults.getResultObjects();

		ModelAndView mav = new ModelAndView("strain/strain_summary_table");
		mav.addObject("strains", strainList);
		mav.addObject("count", strainList.size());
		mav.addObject("totalCount", searchResults.getTotalCount());

		return mav;
	}

	/* generate the sort options
	 */
	public List<Sort> genSorts(StrainQueryForm queryForm) {
		logger.debug("->genSorts started");

		// marker summary sorts by type; reference summary sorts by name

		String sort = SortConstants.BY_DEFAULT;		// default to type sort
		boolean desc = false;						// always sort ascending

		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort(sort, desc));
		return sorts;
	}
	
    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

	/* build the "You Searched For" string, based on data in the query form
	 */
	private String getYouSearchedFor (StrainQueryForm qf) {
		StringBuffer sb = new StringBuffer();
		sb.append("<span class='ysf'>You Searched For...</span><br/>");

		String strainName = qf.getStrainName();
		List<String> attributes = qf.getAttributes();
		
		if ((strainName != null) && (strainName.length() > 0)) {
			String op = "equals";
			if (strainName.indexOf("*") >= 0) {
				if (strainName.startsWith("*")) {
					if (strainName.endsWith("*")) {
						op = "contains";
					} else {
						op = "begins with";
					}
				} else if (strainName.endsWith("*")) {
					op = "ends with";
				} else {
					op = "matches";
				}
			}
			sb.append("Name: ");
			sb.append(op);
			sb.append(" <b>");
			sb.append(strainName);
			sb.append("</b>");
			sb.append(" <span class='smallGray'>searching current names and synonyms</span>");
			sb.append("<br/>");
		}
		
		if ((attributes != null) && (attributes.size() > 0)) {
			if (attributes.size() > 1) {
				sb.append("Attributes: any of <b>[");
				sb.append(String.join(", ", attributes));
				sb.append("]</b><br/>");
			} else {
				sb.append("Attributes: <b>");
				sb.append(attributes.get(0));
				sb.append("</b><br/>");
			}
		}
		return sb.toString();
	}
	
	/* return a mav for an error screen with the given message filled in
	 */
	private ModelAndView errorMav(String msg) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("errorMsg", msg);
		return mav;
	}

	// This is a convenience method to handle packing the SearchParams object
	// and return the SearchResults from the finder.
	private SearchResults<SimpleStrain> getSummaryResults(@ModelAttribute StrainQueryForm query, @ModelAttribute Paginator page) {

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(query));
		params.setFilter(genFilters(query));
		
		// perform query, return SearchResults 
		return strainFinder.getStrains(params);
	}

	/* generate the filters (translate the query parameters into Solr filters)
	 */
	private Filter genFilters(StrainQueryForm query){
		logger.debug("->genFilters started");
		logger.debug("  - QueryForm -> " + query);

		// start filter list to add filters to
		List<Filter> filterList = new ArrayList<Filter>();

		// strain name (may also be an ID)
		String name = query.getStrainName();
		if ((name != null) && (name.length() > 0)) {
			List<Filter> nameOrID = new ArrayList<Filter>();

			if (name.indexOf("*") == -1) {
				// name has no wildcard, so do exact match on name & synonyms and IDs
				// Note: We don't actually want wildcards in IDs, but use this operator to get the surrounding
				// double-quotes.  This allows the IDs to contain colons.
				nameOrID.add(new Filter(SearchConstants.STRAIN_NAME_LOWER, name, Filter.Operator.OP_EQUAL));
				nameOrID.add(new Filter(SearchConstants.ACC_ID, name, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED));
			} else {
				// any search with wildcards does not look at IDs
				nameOrID.add(new Filter(SearchConstants.STRAIN_NAME_LOWER, name, Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED));
			}
			filterList.add(Filter.or(nameOrID));
		}
		
		// reference ID
		String refID = query.getReferenceID();
		if ((refID != null) && (refID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.ACC_ID, refID, Filter.Operator.OP_EQUAL));
		}

		// strain type
		List<String> attributes = query.getAttributes();
		if ((attributes != null) && (attributes.size() > 0)) {
			List<Filter> attributeList = new ArrayList<Filter>();
			for (String attribute : attributes) {
				attributeList.add(new Filter(SearchConstants.STRAIN_ATTRIBUTE_LOWER, attribute, Filter.Operator.OP_EQUAL));
			}
			filterList.add(Filter.or(attributeList));
		}
		
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		}

		return containerFilter;
	}
}
