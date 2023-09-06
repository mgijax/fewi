package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import org.owasp.encoder.Encode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jax.mgi.shr.jsonmodel.AccessionID;
import org.jax.mgi.fe.datamodel.Reference;
import org.jax.mgi.fe.datamodel.Strain;
import org.jax.mgi.fe.datamodel.StrainAttribute;
import org.jax.mgi.fe.datamodel.StrainGridCell;
import org.jax.mgi.fe.datamodel.StrainGridPopupCell;
import org.jax.mgi.fe.datamodel.StrainGridPopupRow;
import org.jax.mgi.fe.datamodel.StrainSnpRow;
import org.jax.mgi.fe.datamodel.StrainSynonym;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.StrainFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.forms.StrainQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FewiUtil;
import org.jax.mgi.fewi.util.StrainPhenoGroup;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.fe.sort.SmartAlphaComparator;
import org.jax.mgi.shr.jsonmodel.SimpleStrain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
 * This controller maps all /strain/ uri's
 */
@Controller
@RequestMapping(value="/strain")
public class StrainController {
    //--------------------//
    // static variables
    //--------------------//

	private static int facetLimit = 100;		// max number of facet values to return
	private static int downloadRowMax = 250000;	// maximum number of strains to allow in download file
	private static int maxSnpCount = -1;		// max number of SNPs in a cell for the SNP ribbon on detail page

	private static Map<String,String> chromosomeSize;	// size the the chromosomes, for links to SNP summary (in bp)
	static {
		chromosomeSize = new HashMap<String,String>();
		chromosomeSize.put("1", "196000000");
		chromosomeSize.put("2", "183000000");
		chromosomeSize.put("3", "161000000");
		chromosomeSize.put("4", "157000000");
		chromosomeSize.put("5", "152000000");
		chromosomeSize.put("6", "150000000");
		chromosomeSize.put("7", "146000000");
		chromosomeSize.put("8", "131000000");
		chromosomeSize.put("9", "125000000");
		chromosomeSize.put("10", "131000000");
		chromosomeSize.put("11", "123000000");
		chromosomeSize.put("12", "121000000");
		chromosomeSize.put("13", "121000000");
		chromosomeSize.put("14", "126000000");
		chromosomeSize.put("15", "105000000");
		chromosomeSize.put("16", "99000000");
		chromosomeSize.put("17", "96000000");
		chromosomeSize.put("18", "91000000");
		chromosomeSize.put("19", "62000000");
		chromosomeSize.put("X", "172000000");
		chromosomeSize.put("X", "172000000");
		chromosomeSize.put("Y", "172000000");
		chromosomeSize.put("MT", "17000");
	}
	
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

	@Autowired
	private SnpController snpController;

	private static SmartAlphaComparator smartAlphaComparator = new SmartAlphaComparator();
	
    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//

	//------------------------//
	// strain phenogrid popup
	//------------------------//
	@RequestMapping(value="/phenotype/{strainID:.+}", method = RequestMethod.GET)
	public ModelAndView getStrainGridPopup(HttpServletRequest request, @PathVariable("strainID") String strainID,
			@RequestParam("header") String header) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->getStrainGridPopup started");

        List<Strain> strainList = strainFinder.getStrainByID(strainID);
        // there can be only one...
        if (strainList.size() < 1) { // none found
            return errorMav("No Strain Found for ID " + strainID);
        } else if (strainList.size() > 1) { // dupe found
            return errorMav("ID " + strainID + " is associated with multiple strains");
        }
        // success - we have a single strain


        // pull out the Strain and the header term, adding them to the mav
        Strain strain = strainList.get(0);
        StrainGridCell sgCell = strain.getGridCell(header);
        if (sgCell == null) {
        	return errorMav("Strain " + strainID + " has no annotations for term " + header);
        }

        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("strain/strain_grid_popup");
        mav.addObject("strain", strain);
        mav.addObject("headerTerm", header);
        
        // construct a StrainPhenoGroup and add it to the mav
        StrainPhenoGroup spg = new StrainPhenoGroup();
        for (StrainGridPopupRow row : sgCell.getPopupRows()) {
        	spg.addRow(row.getGenotype());
        	for (StrainGridPopupCell cell : row.getCells()) {
        		spg.addCell(cell.getTerm(), cell.getValue());
        	}
        }
        mav.addObject("strainPhenoGroup", spg);

		return mav;
	}

    //--------------------//
    // strain detail page
    //--------------------//
    @RequestMapping(value="/{strainID:.+}", method = RequestMethod.GET)
    public ModelAndView getStrainDetailPage(HttpServletRequest request, @PathVariable("strainID") String strainID) {
    	if (request != null) {
    		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
				return UserMonitor.getSharedInstance().getLimitedMessage();
			}
    	}

        logger.debug("->getStrainDetailPage started");

        List<Strain> strainList = strainFinder.getStrainByID(strainID);
        // there can be only one...
        if (strainList.size() < 1) { // none found

        	// The incoming ID has a "JAX:" prefix, so try again as a search to find the right strain.
        	if (strainID.toUpperCase().startsWith("JAX:")) {
        		Paginator page = new Paginator(10);
        		
        		// For resiliency, test both with the JAX prefix and without when searching.
        		List<String> jaxIDs = new ArrayList<String>();
        		jaxIDs.add(strainID.toUpperCase().replaceAll("JAX:", ""));
        		jaxIDs.add(strainID.toUpperCase());
        		
        		for (String jaxID : jaxIDs) {
        			StrainQueryForm query = new StrainQueryForm();
        			query.setStrainName(jaxID);

        			SearchResults<SimpleStrain> searchResults = getSummaryResults(query, page);
        			for (SimpleStrain strain : searchResults.getResultObjects()) {
        				for (AccessionID accID : strain.getAccessionIDs()) {
        					if (accID.getAccID().equalsIgnoreCase(jaxID) && accID.getLogicalDB().equals("JAX Registry")) {
        						return this.getStrainDetailPage(null, strain.getPrimaryID());
        					}
        				}
        			}
        		}
        	}
        	
        	// failed to find an ID (even searching as a Jax ID, if needed)
       		ModelAndView mav = new ModelAndView("error");
       		mav.addObject("errorMsg", "No Strain Found for ID " + strainID);
       		return mav;

        } else if (strainList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "ID " + strainID + " is associated with multiple strains");
            return mav;
        }
        // success - we have a single object

        if (maxSnpCount <= 0) {
        	maxSnpCount = strainFinder.getMaxSnpCount();
        }
        
        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("strain/strain_detail");
        mav.addObject("maxSnpCount", maxSnpCount);
        
        //pull out the Strain, and add to mav
        Strain strain = strainList.get(0);
        mav.addObject("strain", strain);
        addDetailSeo(strain, mav);

        mav.addObject("snpBuildNumber", snpController.getSnpBuildNumber());
        
        // See if there are any related strains (strains with names that have the current strain
        // name as a prefix).  If so, add a count to the mav.
        
        Paginator page = new Paginator(1);
        List<String> attributes = new ArrayList<String>();
        attributes.add("inbred strain");
        StrainQueryForm query = new StrainQueryForm();
        query.setStrainName(strain.getName() + "*");
        query.setAttributes(attributes);
		SearchResults<SimpleStrain> searchResults = getSummaryResults(query, page);
		if (searchResults.getTotalCount() > 0) {
			mav.addObject("relatedStrainCount", searchResults.getTotalCount());
		}
        
        // add an IDLinker to the mav for use at the JSP level
        mav.addObject("idLinker", idLinker);
        return mav;
    }

    // SNP table (loaded via Ajax) for strain detail page
    @RequestMapping(value="/snpTable/{strainID}", method = RequestMethod.GET)
    public ModelAndView getSnpTable(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable("strainID") String strainID,
    		@RequestParam(value="sortBy", required=false) String sortBy,
    		@RequestParam(value="dir", required=false) String dir,
    		@RequestParam(value="mode", required=false) String mode
    		) {
        logger.debug("->getSnpTable started");

        // sortBy should be either 'strain' or a chromosome name
        if ((sortBy == null) || sortBy.equals("")) {
        	sortBy = "strain";
        }

        // dir should be either 'asc' or 'desc'
        if ((dir == null) || dir.equals("")) {
        	if (sortBy.equals("strain")) {
        		dir = "asc";
        	} else {
        		dir = "desc";
        	}
        }

        // mode should be 'all', 'same', or 'diff'
        if ((mode == null) || (mode.equals(""))) {
        	mode = "all";
        }
        
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

        // need the maximum count of SNPs for a cell to aid with coloring computations
        if (maxSnpCount <= 0) {
        	maxSnpCount = strainFinder.getMaxSnpCount();
        }
        
        // generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("strain/strain_detail_snp_table");
        mav.addObject("maxSnpCount", maxSnpCount);
        mav.addObject("sortBy", sortBy);
        mav.addObject("dir", dir);
        mav.addObject("same_reference", SnpController.comparisonAllelesAgree);
        mav.addObject("diff_reference", SnpController.comparisonAllelesDiffer);
		mav.addObject("chromosomeSize", chromosomeSize);
        
        //pull out the Strain, sort its SNP rows as requested, and add them to the mav
        Strain strain = strainList.get(0);
        mav.addObject("strain", strain);
        mav.addObject("snpRows", getSortedStrainRows(strain, sortBy, dir, mode));
        mav.addObject("mode", mode);

        return mav;
    }
    
    // sort the strain's rows according to a field specified in 'sortBy' (either 'strain' or a chromosome)
    private List<StrainSnpRow> getSortedStrainRows(Strain s, String sortBy, String dir, String mode) {
    	if ((sortBy == null) || sortBy.equals("") || (sortBy.equals("strain") && "all".equals(mode) && "asc".equals(dir)) ) {
    		// default order is already managed by Hibernate
    		return s.getSnpRows();
    	}

    	// make a duplicate list that we can sort without messing up the original
    	List<StrainSnpRow> rows = new ArrayList<StrainSnpRow>();
    	for (StrainSnpRow row : s.getSnpRows()) {
    		rows.add(row);
    	}
    	
    	Collections.sort(rows, rows.get(0).getComparator(sortBy, mode));
    	if ("desc".equals(dir)) {
    		Collections.reverse(rows);
    	}
    	return rows;
    }

    // add SEO data (seoDescription, seoTitle, and seoKeywords) to the given detail page's mav
    private void addDetailSeo (Strain p, ModelAndView mav) {
    	// compose browser / SEO title
    	
    	StringBuffer seoTitle = new StringBuffer();
    	seoTitle.append(p.getName());
    	seoTitle.append(" ");
    	seoTitle.append(" Strain Detail MGI Mouse ");
    	seoTitle.append(p.getPrimaryID());
    	mav.addObject("seoTitle", seoTitle.toString());
    	
    	// compose set of SEO keywords
    	
    	List<StrainSynonym> synonyms = p.getStrainSynonyms();
    	List<StrainAttribute> attributes = p.getStrainAttributes();
    	
    	StringBuffer seoKeywords = new StringBuffer();
    	seoKeywords.append(p.getName());
    	if ((synonyms != null) && (synonyms.size() > 0)) {
    		for (StrainSynonym synonym : synonyms) {
    			seoKeywords.append(", ");
    			seoKeywords.append(synonym.getSynonym());
    		}
    	}
    	if ((attributes != null) && (attributes.size() > 0)) {
    		for (StrainAttribute attribute : attributes) {
    			seoKeywords.append(", ");
    			seoKeywords.append(attribute.getAttribute());
    		}
    	}

    	seoKeywords.append(", strain, mouse, mice, murine, Mus");
    	mav.addObject("seoKeywords", seoKeywords);
    	
    	// compose SEO description
    	
    	StringBuffer seoDescription = new StringBuffer();
    	seoDescription.append("View mouse strain ");
    	seoDescription.append(p.getName());
    	seoDescription.append(" : mutations, QTL, phenotypes, diseases, and references.");
    	mav.addObject("seoDescription", seoDescription);
    }

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
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("In strainSummary, query string: " + request.getQueryString());

        // objects needed by display
        ModelAndView mav = new ModelAndView("strain/strain_summary");
        mav.addObject("strainQueryForm", queryForm);
        mav.addObject("queryString", request.getQueryString());
        mav.addObject("title", "Strain Summary");
        mav.addObject("ysf", getYouSearchedFor(queryForm));
        request.setAttribute("externalUrls", ContextLoader.getExternalUrls());
        request.setAttribute("configBean", ContextLoader.getConfigBean());
        mav.addObject("description", "View mouse strains with their synonyms, attributes, accession IDs, and references.");
        mav.addObject("keywords", "MGI, mouse, mice, murine, Mus musculus, inbred, strain, consomic, congenic, conplastic, coisogenic, hybrid, insertion, inversion, recombinant, outbred, transgenic");

        return mav;
    }

	// shell of the strain summary page, coming from a reference 
    // (The actual results are retrieved via Ajax from the /table endpoint.)
    @RequestMapping(value="reference/{refID:.+}", method = RequestMethod.GET)
    public ModelAndView strainForReference(HttpServletRequest request,
    		@ModelAttribute StrainQueryForm queryForm,
    		@PathVariable("refID") String refID) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

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
        mav.addObject("description", "View mouse strains associated with reference " + refID + " with their synonyms, attributes, accession IDs, and references.");
        mav.addObject("keywords", "MGI, mouse, mice, murine, Mus musculus, inbred, strain, consomic, congenic, conplastic, coisogenic, hybrid, insertion, inversion, recombinant, outbred, transgenic, reference, " + refID);

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

	/* get the set of strain attribute filter options for the current result set
	 */
	@RequestMapping("/facet/attribute")
	public @ResponseBody Map<String, List<String>> getAttributeFacet (@ModelAttribute StrainQueryForm qf, BindingResult result, HttpServletResponse response) {
		AjaxUtils.prepareAjaxHeaders(response);
		Map<String, List<String>> out = new HashMap<String, List<String>>();
		List<String> messages = new ArrayList<String>();
		
		SearchParams params = new SearchParams();
		params.setPageSize(0);
		params.setFilter(genFilters(qf));
		SearchResults<SimpleStrain> searchResults = strainFinder.getAttributeFacet(params);
		List<String> facetChoices = searchResults.getResultFacets();
		
		if (facetChoices.size() > facetLimit) {
			messages.add("Too many results to display.  Modify your search or try another filter first.");
			out.put("error", messages);

		} else if (facetChoices.size() == 0) {
			messages.add("No values in results to filter.");
			out.put("error", messages);
			
		} else {
			// remove choices that we don't want to give the user
			// facetChoices.remove("mutant strain");
			// facetChoices.remove("mutant stock");
			facetChoices.remove("Not Applicable");
			facetChoices.remove("Not Specified");

			Collections.sort(facetChoices, new AttributeComparator());
			out.put("resultFacets", facetChoices);
		}
		return out;
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
	
	// strain summary reports (txt and xls -- specified by suffix)
	@RequestMapping("/report*")
	public ModelAndView strainSummaryExport(HttpServletRequest request, @ModelAttribute StrainQueryForm query, @ModelAttribute Paginator page) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

		logger.debug("generating report");

		SearchParams sp = new SearchParams();
		page.setResults(downloadRowMax);
		sp.setPaginator(page);
		sp.setFilter(genFilters(query));
		sp.setSorts(genSorts(query));

		SearchResults<SimpleStrain> sr = strainFinder.getStrains(sp);
		List<SimpleStrain> strains = sr.getResultObjects();
		logger.debug(" - got " + strains.size() + " strains");

		ModelAndView mav = new ModelAndView("strainSummaryReport");
		mav.addObject("strains", strains);
		return mav;

	}

	// This is a convenience method to handle packing the SearchParams object
	// and return the SearchResults from the finder.
	public SearchResults<SimpleStrain> getSummaryResults(StrainQueryForm query, Paginator page) {

		SearchParams params = new SearchParams();
		params.setPaginator(page);
		params.setSorts(genSorts(query));
		params.setFilter(genFilters(query));
		
		// perform query, return SearchResults 
		return strainFinder.getStrains(params);
	}

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

	/* build the "You Searched For" string, based on data in the query form
	 */
	private String getYouSearchedFor (StrainQueryForm qf) {
		StringBuffer sb = new StringBuffer();
		sb.append("<span class='ysf'>You Searched For...</span><br/>");

		// remember the initial size so we can tell if we add something
		int initialSize = sb.length();
		
		String collection = null;
		String strainName = qf.getStrainName();
		List<String> attributes = qf.getAttributes();
		Integer isSequenced = qf.getIsSequenced();
		String group = qf.getGroup();
		String refID = qf.getReferenceID();
		
		if (isSequenced != null) {
			if (isSequenced == 1) {
				collection = "Mouse Genomes Project Strains";
			}
		}
		
		if ((group != null) && (group.length() > 0)) {
			if ("HDP".equals(group)) {
				collection = "Hybrid Diversity Panel (HDP) Strains";
			} else if ("CC".equals(group)) {
				collection = "Collaborative Cross Strains";
			} else if ("DOCCFounders".equals(group)) {
				collection = "DO/CC Founders";
			}
		}
		
		if ((refID != null) && (refID.length() > 0)) {
			sb.append("Reference: <b>");
			sb.append(FewiUtil.sanitizeID(refID));
			sb.append("</b><br/>");
		}
		
		if ((strainName != null) && (strainName.length() > 0)) {
			String op = "equals";
			if (strainName.indexOf("*") >= 0) {
				if (strainName.startsWith("*")) {
					if (strainName.endsWith("*")) {
						op = "contains";
					} else {
						op = "ends with";
					}
				} else if (strainName.endsWith("*")) {
					op = "begins with";
				} else {
					op = "matches";
				}
			}
			sb.append("Name/Synonym/ID: ");
			sb.append(op);
			sb.append(" <b>");
			sb.append(Encode.forHtml(strainName));
			sb.append("</b>");
			sb.append(" <span class='smallGray'>searching current names and synonyms</span>");
			sb.append("<br/>");
		}
		
		if ((attributes != null) && (attributes.size() > 0)) {
			if (attributes.size() > 1) {
				String op = "any";
				if (qf.getAttributeOperator() != null) {
					op = qf.getAttributeOperator();
				}
				sb.append("Attributes: " + op + " of <b>[");
				sb.append(Encode.forHtml(StringUtils.join(attributes, ", ")));
				sb.append("]</b><br/>");
			} else {
				sb.append("Attributes: <b>");
				sb.append(Encode.forHtml(attributes.get(0)));
				sb.append("</b><br/>");
			}
		}

		// Did we identify a collection to mention?
		if ((collection != null) && (collection.length() > 0)) {
			sb.append("Collection: <b>");
			sb.append(Encode.forHtml(collection));
			sb.append("</b><br/>");
		}

		if (sb.length() == initialSize) {
			sb.append("All strains");
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
		
		// groups that the strain is part of (HDP, CCParental, etc.)
		String group = query.getGroup();
		if ((group != null) && (group.length() > 0)) {
			filterList.add(new Filter(SearchConstants.STRAIN_GROUPS, group, Filter.Operator.OP_EQUAL));
		}
		
		// tag that identifies a particular subset of strains (eg- GXDHT)
		String tag = query.getTag();
		if ((tag != null) && (tag.length() > 0)) {
			filterList.add(new Filter(SearchConstants.STRAIN_TAGS, tag, Filter.Operator.OP_EQUAL));
		}
		
		// Has the strain been sequenced (1) or not (0)?
		Integer isSequenced = query.getIsSequenced();
		if (isSequenced != null) {
			filterList.add(new Filter(SearchConstants.STRAIN_IS_SEQUENCED, isSequenced, Filter.Operator.OP_EQUAL));
		}
		
		// reference ID
		String refID = query.getReferenceID();
		if ((refID != null) && (refID.length() > 0)) {
			filterList.add(new Filter(SearchConstants.ACC_ID, refID, Filter.Operator.OP_EQUAL));
		}

		// strain attributes (chosen from query form options)
		List<String> attributes = query.getAttributes();
		if ((attributes != null) && (attributes.size() > 0)) {
			List<Filter> attributeList = new ArrayList<Filter>();
			for (String attribute : attributes) {
				attributeList.add(new Filter(SearchConstants.STRAIN_ATTRIBUTE_LOWER, attribute, Filter.Operator.OP_EQUAL));
			}
			
			if ("all".equals(query.getAttributeOperator())) {
				filterList.add(Filter.and(attributeList)); 
			} else {
				filterList.add(Filter.or(attributeList)); 
			}
		}
		
		// strain attribute filter (chosen from filter button, not from query form options)
		List<String> attributeFilter = query.getAttributeFilter();
		if ((attributeFilter != null) && (attributeFilter.size() > 0)) {
			List<Filter> attributeFilterList = new ArrayList<Filter>();
			for (String attribute : attributeFilter) {
				attributeFilterList.add(new Filter(SearchConstants.STRAIN_ATTRIBUTE_LOWER, attribute, Filter.Operator.OP_EQUAL));
			}
			filterList.add(Filter.or(attributeFilterList));
		}
		
		// if we have filters, collapse them into a single filter
		Filter containerFilter = new Filter();
		if (filterList.size() > 0){
			containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
			containerFilter.setNestedFilters(filterList);
		} else {
			// if no filters, then we want to return all strains
			containerFilter = new Filter(SearchConstants.STRAIN_NAME, "*", Filter.Operator.OP_EQUAL_WILDCARD_ALLOWED);
		}

		return containerFilter;
	}
}
