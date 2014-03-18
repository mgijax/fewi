package org.jax.mgi.fewi.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mgi.frontend.datamodel.Allele;
import mgi.frontend.datamodel.AlleleCellLine;
import mgi.frontend.datamodel.AlleleID;
import mgi.frontend.datamodel.AlleleSynonym;
import mgi.frontend.datamodel.DatabaseInfo;
import mgi.frontend.datamodel.Genotype;
import mgi.frontend.datamodel.GenotypeDisease;
import mgi.frontend.datamodel.GenotypeDiseaseReference;
import mgi.frontend.datamodel.Image;
import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.MarkerLocation;
import mgi.frontend.datamodel.MarkerQtlExperiment;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceLocation;
import mgi.frontend.datamodel.phenotype.DiseaseTableDisease;
import mgi.frontend.datamodel.phenotype.PhenoTableGenotype;
import mgi.frontend.datamodel.phenotype.PhenoTableSystem;

import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.antlr.BooleanSearch.BooleanSearch;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.DbInfoFinder;
import org.jax.mgi.fewi.finder.GenotypeFinder;
import org.jax.mgi.fewi.finder.ImageFinder;
import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.forms.AlleleQueryForm;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.AlleleSummaryRow;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.util.AjaxUtils;
import org.jax.mgi.fewi.util.FilterUtil;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.IDLinker;
import org.jax.mgi.fewi.util.NotesTagConverter;
import org.jax.mgi.fewi.util.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /allele/ uri's
 */
@Controller
@RequestMapping(value="/allele")
public class AlleleController {

    //--------------------//
    // class variables
    //--------------------//

    // keep this around, so we know what version of the assembly we have (even
    // for alleles with a representative sequence without proper coordinates)
	//
	// TODO: surely there must be a more effective way of getting this information - kstone
    private static String assemblyVersion = null;

    private static Integer DOWNLOAD_ROW_CAP = new Integer(250000);

    //--------------------//
    // instance variables
    //--------------------//

    private Logger logger = LoggerFactory.getLogger(AlleleController.class);

    @Autowired
    private IDLinker idLinker;

    @Autowired
    private DbInfoFinder dbInfoFinder;

    @Autowired
    private AlleleFinder alleleFinder;

    @Autowired
    private ImageFinder imageFinder;

    @Autowired
    private GenotypeFinder genotypeFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private MarkerFinder markerFinder;

	@Autowired
	private SessionFactory sessionFactory;


    //--------------------------------------------------------------------//
    // public methods
    //--------------------------------------------------------------------//


    //------------------------------------//
    // Allele Query Form
    //------------------------------------//
	@RequestMapping(method=RequestMethod.GET)
	 public ModelAndView getQueryForm(
			  HttpServletRequest request,
			  HttpServletResponse response,
			  @ModelAttribute AlleleQueryForm query)
	 {
		ModelAndView mav = new ModelAndView("allele_query");

		this.initQFCache();
        List<String> collectionValues = query.getCollectionValues();

        // package data, and send to display layer
        mav.addObject("alleleQueryForm", query);
		mav.addObject("sort", new Paginator());
		mav.addObject("collectionValues", collectionValues);
		return mav;
	}


    //-------------------------//
    // Allele Query System Popup
    //-------------------------//
    @RequestMapping("/phenoPopup")
    public ModelAndView phenoPopup (HttpServletRequest request) {

		logger.debug("->systemPopup started");

        ModelAndView mav = new ModelAndView("allele_query_system_popup");
        return mav;
    }


    //------------------------------------//
    // Allele Summary Shell
    //------------------------------------//
	 @RequestMapping(value="/summary", method=RequestMethod.GET)
	 public ModelAndView alleleSummary(
			  HttpServletRequest request,
			  HttpServletResponse response,
			  @ModelAttribute AlleleQueryForm query)
	 {
		ModelAndView mav = new ModelAndView("allele_summary");

		// direct to marker summary if marker is in link
		if(notEmpty(query.getMarkerId()))
		{
			SearchResults<Marker> markerSR = markerFinder.getMarkerByID(query.getMarkerId());
			if(markerSR.getTotalCount()>0)
			{
				mav = new ModelAndView("allele_summary_by_marker");
				mav.addObject("marker",markerSR.getResultObjects().get(0));
			}
		}

		this.initQFCache();
        List<String> collectionValues = query.getCollectionValues();
		mav.addObject("alleleQueryForm",query);
		mav.addObject("queryString", request.getQueryString());
		mav.addObject("collectionValues", collectionValues);
		return mav;
	 }


    //------------------------------------//
    // Allele Summary (By Refernce) Shell
    //------------------------------------//
    @RequestMapping(value="/reference/{refID}")
    public ModelAndView alleleSummeryByRefId(
          HttpServletRequest request,
          @PathVariable("refID") String refID)
    {
        logger.debug("->alleleSummeryByRefId started");

        // setup view object
        ModelAndView mav = new ModelAndView("allele_summary_by_reference");

		// setup search parameters object to gather the requested marker
        SearchParams referenceSearchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        referenceSearchParams.setFilter(refIdFilter);

        // find the requested reference
        SearchResults<Reference> referenceSearchResults
          = referenceFinder.searchReferences(referenceSearchParams);
        List<Reference> referenceList = referenceSearchResults.getResultObjects();

        // there can be only one...
        if (referenceList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refID);
            return mav;
        }
        if (referenceList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + refID);
            return mav;
        }
        Reference reference = referenceList.get(0);

        // prep query form for summary js request
        AlleleQueryForm query = new AlleleQueryForm();
        query.setJnumId(refID);

        // package data, and send to view layer
        mav.addObject("reference", reference);
		mav.addObject("alleleQueryForm",query);
		mav.addObject("queryString", "jnumId=" + refID);

        logger.debug("alleleSummeryByRefId routing to view ");
		return mav;
    }


    //--------------------//
    // Allele Detail by ID
    //--------------------//
    @RequestMapping(value="/{alleleID:.+}", method=RequestMethod.GET)
    public ModelAndView alleleDetailByID(
		  HttpServletRequest request,
		  HttpServletResponse response,
	    @PathVariable("alleleID") String alleleID) {

	logger.debug("->alleleDetailByID started");

	// find the requested Allele
	List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);

	// there can only be one Allele
	if (alleleList.size() < 1) {
	    return errorMav("No Allele Found");
	} else if (alleleList.size() > 1) {
	    return errorMav("Duplicate ID");
	}

	// pass on to shared code to flesh out data for the allele detail page
	return this.prepareAllele(request, alleleList.get(0));
    }


    //---------------------//
    // Allele Detail by Key
    //---------------------//
    @RequestMapping(value="/key/{dbKey:.+}", method=RequestMethod.GET)
    public ModelAndView alleleDetailByKey(
		  HttpServletRequest request,
		  HttpServletResponse response,
	    @PathVariable("dbKey") String dbKey) {

	logger.debug("->alleleDetailByKey started");

	// find the requested Allele
	SearchResults searchResults = alleleFinder.getAlleleByKey(dbKey);
	List<Allele> alleleList = searchResults.getResultObjects();

	// there can only be one Allele
	if (alleleList.size() < 1) {
	    return errorMav("No Allele Found");
	} else if (alleleList.size() > 1) {
	    // should not happen
	    return errorMav("Duplicate Key");
	}

	// pass on to shared code to flesh out data for the allele detail page
	return this.prepareAllele(request, alleleList.get(0));
    }


    //------------------------------------//
    // Allele Summary Data Retrieval
    //------------------------------------//
	 @RequestMapping("/summary/json")
	 public @ResponseBody JsonSummaryResponse<AlleleSummaryRow> alleleSummaryJson(
	            HttpServletRequest request,
	            @ModelAttribute AlleleQueryForm query,
	            @ModelAttribute Paginator page) throws RecognitionException
	 {
		 SearchResults<Allele> sr = getAlleles(request,query,page);
		 logger.info("found "+sr.getTotalCount()+" alleles");
		 List<AlleleSummaryRow> sRows = new ArrayList<AlleleSummaryRow>();
		 for(Allele allele : sr.getResultObjects())
		 {
			 sRows.add(new AlleleSummaryRow(allele));
		 }

		 logger.info("building summary rows");
		 JsonSummaryResponse<AlleleSummaryRow> jsr = new JsonSummaryResponse<AlleleSummaryRow>();
		 jsr.setTotalCount(sr.getTotalCount());
		 jsr.setSummaryRows(sRows);
		 return jsr;
	 }

	 public SearchResults<Allele> getAlleles(
	            HttpServletRequest request,
	            @ModelAttribute AlleleQueryForm query,
	            @ModelAttribute Paginator page) throws RecognitionException
	 {
		 SearchParams sp = new SearchParams();
		 sp.setPaginator(page);
		 sp.setFilter(parseQueryForm(query));
		 sp.setSorts(parseAlleleSorts(request));
		 SearchResults<Allele> sr = alleleFinder.getAlleleByID(sp);
		 return sr;
	 }


    //--------------------------------//
    // Allele Summary Tab-Delim Report
    //--------------------------------//
    @RequestMapping("/report*")
    public ModelAndView alleleSummaryExport(
            HttpServletRequest request,
			@ModelAttribute AlleleQueryForm query,
			@ModelAttribute Paginator page) throws RecognitionException
	{

    	logger.debug("generating report");

        SearchParams sp = new SearchParams();
        page.setResults(DOWNLOAD_ROW_CAP);
        sp.setPaginator(page);
        sp.setFilter(parseQueryForm(query));
        List<Sort> sorts = new ArrayList<Sort>();
        sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL));
        sp.setSorts(sorts);
        SearchResults<Allele> sr = alleleFinder.getAlleleByID(sp);

        List<Allele> alleles = sr.getResultObjects();

		ModelAndView mav = new ModelAndView("alleleSummaryReport");
		mav.addObject("alleles", alleles);
		return mav;

    }


    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//
	 private Filter parseQueryForm(AlleleQueryForm query) throws RecognitionException
	 {
		 List<Filter> filters = new ArrayList<Filter>();
		 // Allele Key
		 String allKey = query.getAllKey();
		 if(notEmpty(allKey))
		 {
			 filters.add(new Filter(SearchConstants.ALL_KEY,allKey,Filter.OP_EQUAL));
		 }
		 // Allele IDs
		 String allIds = query.getAllIds();
		 if(notEmpty(allIds))
		 {
			 List<String> allIdTokens = QueryParser.tokeniseOnWhitespaceAndComma(allIds);
			 filters.add(new Filter(SearchConstants.ALL_ID,allIdTokens,Filter.OP_IN));
		 }
		 // Allele Type
		 Filter alleleTypeFilter = makeListFilter(query.getAlleleType(),SearchConstants.ALL_TYPE);
		 if(alleleTypeFilter!=null) filters.add(alleleTypeFilter);

		 // Allele Sub Type
		 Filter alleleSubTypeFilter = makeListFilter(query.getAlleleSubType(),SearchConstants.ALL_SUBTYPE);
		 if(alleleSubTypeFilter!=null) filters.add(alleleSubTypeFilter);

		 // Allele Collection
		 Filter collectionFilter = makeListFilter(query.getCollection(),SearchConstants.ALL_COLLECTION);
		 if(collectionFilter!=null) filters.add(collectionFilter);

		 // Phenotypes
		 String phenotype = query.getPhenotype();
		 if(notEmpty(phenotype))
		 {
			 filters.add(BooleanSearch.buildSolrFilter(SearchConstants.ALL_PHENOTYPE,phenotype));
		 }

		 // Nomenclature
		 String nomen = query.getNomen();
		 if(notEmpty(nomen))
		 {
			 Filter nomenFilter = FilterUtil.generateNomenFilter(SearchConstants.ALL_NOMEN,nomen);
			 if(nomenFilter!=null) filters.add(nomenFilter);
		 }

		 // Reference Key
		 String refKey = query.getRefKey();
		 if(notEmpty(refKey))
		 {
			 filters.add(new Filter(SearchConstants.REF_KEY,refKey,Filter.OP_EQUAL));
		 }

		 // Reference JNUM ID
		 String jnumId = query.getJnumId();
		 if(notEmpty(jnumId))
		 {
			 filters.add(new Filter(SearchConstants.JNUM_ID,jnumId,Filter.OP_EQUAL));
		 }

		 // Has OMIM
		 String hasOMIM = query.getHasOMIM();
		 if(notEmpty(hasOMIM))
		 {
			 filters.add(new Filter(SearchConstants.ALL_HAS_OMIM,hasOMIM,Filter.OP_EQUAL));
		 }

		 // Exclude Cell Lines
		 String isCellLine = query.getIsCellLine();
		 if(notEmpty(isCellLine))
		 {
			 filters.add(new Filter(SearchConstants.ALL_IS_CELLLINE,isCellLine,Filter.OP_EQUAL));
		 }

		 //Marker ID
		 String mrkId = query.getMarkerId();
		 if(notEmpty(mrkId))
		 {
			 filters.add(new Filter(SearchConstants.MRK_ID,mrkId,Filter.OP_EQUAL));
		 }

		 // Chromosome
		 List<String> chr = query.getChromosome();
		 if(notEmpty(chr) && !chr.contains(AlleleQueryForm.COORDINATE_ANY))
		 {
			 Filter chrFilter = makeListFilter(chr,SearchConstants.CHROMOSOME);
			 if(chrFilter!=null) filters.add(chrFilter);
		 }
		 // Coordinate Search
		 String coord = query.getCoordinate();
		 String coordUnit = query.getCoordUnit();
		 if(notEmpty(coord))
		 {
			 coord = coord.trim();
			 BigDecimal unitMultiplier = new BigDecimal(1);
			 if(AlleleQueryForm.COORD_UNIT_MBP.equalsIgnoreCase(coordUnit))
			 {
				 // convert to Mbp
				unitMultiplier = new BigDecimal(1000000);
			 }
			 // split on either -, periods, or whitespaces
			 String[] coordTokens = coord.split("\\s*(-|\\.\\.|\\s+)\\s*");
			 Long start=null,end=null;
			 try
			 {
				 logger.info("parsing: "+coordTokens[0]);
				 BigDecimal startDec = QueryParser.parseDoubleInput(coordTokens[0]);
				 startDec = startDec.multiply(unitMultiplier);
				 start = bdToLong(startDec);
				 logger.info("into "+start);
				 // support single coordinate by setting end to be same as start
				 if(coordTokens.length<2) end=start;
				 else
				 {
					 BigDecimal endDec = QueryParser.parseDoubleInput(coordTokens[1]);
					 logger.info("parsing: "+coordTokens[1]);
					 endDec = endDec.multiply(unitMultiplier);
					 end = bdToLong(endDec);
					 logger.info("into "+end);
				 }
			 }catch(Exception e)
			 {
				 // ignore any errors, we just won't do a coord query
				 logger.debug("failed to parse coordinates",e);
			 }
			 if(start!=null && end!=null)
			 {
				 Filter endF = new Filter(SearchConstants.START_COORD,end.toString(),Filter.OP_LESS_OR_EQUAL);
				 Filter startF = new Filter(SearchConstants.END_COORD,start.toString(),Filter.OP_GREATER_OR_EQUAL);
				 filters.add(Filter.and(Arrays.asList(endF,startF)));
			 }
			 else
			 {
				 filters.add(new Filter(SearchConstants.ALL_KEY,"-9999",Filter.OP_EQUAL));
			 }
		 }
		 // CM Search
		 String cm = query.getCm();
		 if(notEmpty(cm))
		 {
			 cm = cm.trim();
			 // split on either -, periods, or whitespaces
			 String[] cmTokens = cm.split("\\s*(-|\\.\\.|\\s+)\\s*");
			 Double start=null,end=null;
			 try
			 {
				 BigDecimal startDec = QueryParser.parseDoubleInput(cmTokens[0]);
				 start=startDec.doubleValue();
				 // support single coordinate by setting end to be same as start
				 if(cmTokens.length<2) end=start;
				 else
				 {
					 BigDecimal endDec = QueryParser.parseDoubleInput(cmTokens[1]);
					 end = endDec.doubleValue();
				 }
			 }catch(Exception e)
			 {
				 // ignore any errors, we just won't do a cm query
				 logger.debug("failed to parse cm",e);
			 }
			 if(start!=null && end!=null)
			 {
				 Filter endF = new Filter(SearchConstants.CM_OFFSET,end.toString(),Filter.OP_LESS_OR_EQUAL);
				 Filter startF = new Filter(SearchConstants.CM_OFFSET,start.toString(),Filter.OP_GREATER_OR_EQUAL);
				 filters.add(Filter.and(Arrays.asList(endF,startF)));
			 }
			 else
			 {
				 filters.add(new Filter(SearchConstants.ALL_KEY,"-9999",Filter.OP_EQUAL));
			 }
		 }
		 // Cytoband
		 String cyto = query.getCyto();
		 if(notEmpty(cyto))
		 {
			 filters.add(new Filter(SearchConstants.CYTOGENETIC_OFFSET,cyto,Filter.OP_EQUAL));
		 }

		 Filter f;
		 if(filters.size()>0)
		 {
			 // make sure not specified alleles are excluded
			 filters.add(new Filter(SearchConstants.ALL_IS_WILD_TYPE,0,Filter.OP_EQUAL));
			 f=Filter.and(filters);
		 }
		 else f = new Filter(SearchConstants.ALL_KEY,"-9999",Filter.OP_EQUAL); // return nothing if no valid filters
		 return f;
	 }

	 /*
	 * Parses requested sort parameters for gxd marker assay summary.
	 */
	private List<Sort> parseAlleleSorts(HttpServletRequest request)
	{
        logger.debug("->parseAlleleSorts started");
        List<Sort> sorts = new ArrayList<Sort>();

        // retrieve requested sort order; set default if not supplied
        String sortRequested = request.getParameter("sort");

        String dirRequested  = request.getParameter("dir");
        boolean desc = true;
        if("asc".equalsIgnoreCase(dirRequested))
        {
        	desc=false;
        }
        boolean asc=!desc;

        // expected sort values
        if ("nomen".equalsIgnoreCase(sortRequested))
        {
            sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,desc));
        }
        else if ("category".equalsIgnoreCase(sortRequested))
        {
            sorts.add(new Sort(SortConstants.ALL_BY_TYPE,desc));
            sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,desc));
        }
        else if ("chr".equalsIgnoreCase(sortRequested))
        {
            sorts.add(new Sort(SortConstants.ALL_BY_CHROMOSOME,desc));
            sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,desc));
        }
        else if ("diseases".equalsIgnoreCase(sortRequested))
        {
            sorts.add(new Sort(SortConstants.ALL_BY_DISEASE,desc));
            sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,false));
        }
        else
        {
        	// default sort
        	sorts.add(new Sort(SortConstants.ALL_BY_TRANSMISSION,asc));
            sorts.add(new Sort(SortConstants.ALL_BY_SYMBOL,asc));
		}

        logger.debug ("sort: " + sortRequested);

        return sorts;
	}

    //----------------------------//
    // Allele Detail (shared code)
    //----------------------------//
    /* look up a bunch of extra data and toss it in the MAV; a convenience
     * method for use by the individual methods to render allele detail pages
     * either by key or by ID
     */
    private ModelAndView prepareAllele(HttpServletRequest request,Allele allele)
    {
		ModelAndView mav = new ModelAndView("allele_detail");
		mav.addObject ("allele", allele);

		dbDate(mav);

		idLinker.setup();
	        mav.addObject("idLinker", idLinker);

		String alleleType = allele.getAlleleType();

		// begin setting up keywords for SEO (search engine optimization)
		// meta tags.  Also set up a description snippet for display by search
		// engines when returning results.
		ArrayList<String> seoKeywords = new ArrayList<String>();
		seoKeywords.add(allele.getSymbol());
		seoKeywords.add("mouse");
		seoKeywords.add("mice");
		seoKeywords.add("murine");
		seoKeywords.add("Mus");
		seoKeywords.add("allele");

		StringBuffer seoDescription = new StringBuffer();
		seoDescription.append ("View ");
		seoDescription.append (allele.getSymbol());
		seoDescription.append (" allele: origin");

		if (allele.getMolecularDescription() != null &&
			allele.getMolecularDescription().length() != 0)
		{
		    seoDescription.append (", molecular description");
		    mav.addObject("description", allele.getMolecularDescription());
		}

		if (alleleType.equals("Targeted"))
		{
		    seoKeywords.add("null");
		    seoKeywords.add("KO");
		    seoKeywords.add("knock out");
		    seoKeywords.add("knockout");
		    seoKeywords.add("floxed");
		    seoKeywords.add("loxP");
		    seoKeywords.add("frt");

		}
		else if (alleleType.indexOf("Cre") >= 0 ||
			alleleType.indexOf("Flp") >= 0)
		{
		    seoKeywords.add("recombinase");
		    seoKeywords.add("cre");
		    seoKeywords.add("flp");
		    seoKeywords.add("flpe");

		}
		else if (alleleType.indexOf("Transposase") >= 0)
		{
		    seoKeywords.add("transposase");
		}

		if (!alleleType.equals("QTL") &&
		    !alleleType.equals("Not Applicable") &&
		    !alleleType.equals("Other"))
		{
		    // all allele types except QTL and N/A get two standard seoKeywords
		    seoKeywords.add("mutant");
		    seoKeywords.add("mutation");
		}

		if (!alleleType.equals("Not Applicable") &&
		    !alleleType.equals("Not Specified")	&&
		    !alleleType.equals("Other"))
		{
		    // all allele types except N/A and N/S get their allele type as a
		    // keyword
		    seoKeywords.add(alleleType);
		}

		if (allele.getHasDiseaseModel())
		{
		    seoKeywords.add("disease model");
		    seoDescription.append(" and human disease models");
		}

		if (allele.getPhenotypeImages().size() > 0)
		{
		    seoDescription.append(", images");
		}

		seoDescription.append(", gene associations, and references.");

		List<AlleleSynonym> synonyms = (List<AlleleSynonym>) allele.getSynonyms();

	    for (AlleleSynonym synonym : synonyms)
	    {
	        seoKeywords.add (synonym.getSynonym());
	    }

		// set up page title, subtitle, and labels dependent on allele type
		String title = FormatHelper.superscript(allele.getSymbol());
		String subtitle = null;
		String markerLabel = "Gene";
		String typeCategory = "Mutation";
		String symbolLabel = "Symbol";

		if (alleleType.equals("QTL")) {
		    // is a QTL
		    subtitle = "QTL Variant Detail";
		    markerLabel = "QTL";
		    typeCategory = "Variant";
		    symbolLabel = "QTL variant";

		}
		else if (alleleType.equals("Transgenic"))
		{
		    // is a transgene
		    subtitle = "Transgenic Allele Detail";
		    typeCategory = "Transgene";
		}
		else if (alleleType.equals("Not Specified") ||
			     alleleType.equals("Not Applicable") ||
			     alleleType.equals("Other"))
		{
		    // do not show allele type in labels if Not Specified/Applicable
		    subtitle = "Allele Detail";
		}
		else if (alleleType.indexOf('(') >= 0)
		{
		    // trim out subtypes when putting allele type in labels
		    subtitle = alleleType.replaceFirst("\\(.*\\)", "") + " Allele Detail";
		}
		else
		{
		    // add allele type to labels
		    subtitle = alleleType + " Allele Detail";
		}

		String keywords = StringUtils.join(seoKeywords,", ");

		mav.addObject("symbolLabel", symbolLabel);
		mav.addObject("alleleType", alleleType);
		mav.addObject("typeCategory", typeCategory);
		mav.addObject("seoKeywords", keywords.toString());
		mav.addObject("seoDescription", seoDescription.toString());

		// add the allele's marker(s) to the mav

		// If more than one associated marker, need code changes here.
		// For now, we just include the first one.

		String namesRaw = allele.getName();
		boolean hasQtlExpts = false;

		Marker marker = allele.getMarker();
		if (marker != null)
		{
		    mav.addObject("marker", marker);

		    // adjust the marker label for transgene markers, regardless of
		    // the allele type.  Also, we do not want to link to the marker
		    // detail page for transgene markers.
		    if ("Transgene".equals(marker.getMarkerType()))
		    {

				// only update the page subtitle if the allele type is not
				// Not Applicable or Not Specified
				if (!"Allele Detail".equals(subtitle))
				{
				    subtitle = "Transgene Detail";
				}

				markerLabel = "Transgene";
				mav.addObject("linkToMarker", null);
		    }
		    else
		    {
				// if not a transgene, link to the marker detail page
				mav.addObject("linkToMarker", "yes");
		    }

		    if (!marker.getName().equals(namesRaw))
		    {
		    	namesRaw = marker.getName() + "; " + namesRaw;
		    }

		    // if this is a QTL marker, then add in the QTL notes (two types)
		    if ("QTL".equals(allele.getAlleleType()))
		    {
				List<MarkerQtlExperiment> qtlExpts = marker.getQtlMappingNotes();
				if (qtlExpts != null && qtlExpts.size() > 0)
				{
				    mav.addObject("qtlExpts", qtlExpts);
				    hasQtlExpts = true;
				}

				List<MarkerQtlExperiment> qtlCandidateGenes = marker.getQtlCandidateGeneNotes();
				if (qtlCandidateGenes != null && qtlCandidateGenes.size() > 0)
				{
				    mav.addObject("qtlCandidateGenes", qtlCandidateGenes);
				    hasQtlExpts = true;
				}

				String qtlNote = marker.getQtlNote();
				if (qtlNote != null && qtlNote.length() > 0)
				{
				    mav.addObject("qtlNote", qtlNote);
				}
		    }
		}

		mav.addObject("markerLabel", markerLabel);
		mav.addObject("title", title);
		mav.addObject("subtitle", subtitle);

		// start colleting values needed for GBrowse thumbnail and link, with
		// priorities as follows:
		//    1. coordinates from an associated marker
		//    2. coordinates from representative sequence + a 5kb buffer
		long pointCoord = -1;
		long startCoord = -1;
		long endCoord = -1;
		String chromosome = null;

		// default text strings for GBrowse for the case where we do not have
		// coordinates from a marker, but instead from a representative
		// sequence.
		String gbrowseExtraLine = "";
		String gbrowseLabel = "View all gene trap sequence tags within a 10kb region";

		// genomic and genetic locations of the marker
		if (marker != null)
		{
		    MarkerLocation coords = marker.getPreferredCoordinates();
		    MarkerLocation cmOffset = marker.getPreferredCentimorgans();
		    MarkerLocation cytoband = marker.getPreferredCytoband();

		    StringBuffer genomicLocation = new StringBuffer();
		    StringBuffer geneticLocation = new StringBuffer();

		    if (coords == null)
		    {
		    	genomicLocation.append ("unknown");
		    }
		    else
		    {
				startCoord = coords.getStartCoordinate().longValue();
				endCoord = coords.getEndCoordinate().longValue();
				chromosome = coords.getChromosome();

				genomicLocation.append ("Chr");
				genomicLocation.append (chromosome);
				genomicLocation.append (":");
				genomicLocation.append (startCoord);
				genomicLocation.append ("-");
				genomicLocation.append (endCoord);
				genomicLocation.append (" bp");

				String strand = coords.getStrand();
				if (strand != null)
				{
				    genomicLocation.append (", ");
				    genomicLocation.append (strand);
				    genomicLocation.append (" strand");
				}
		    }
		    mav.addObject("genomicLocation", genomicLocation.toString());

		    if ((cmOffset != null) && (cmOffset.getCmOffset().floatValue() != -999.0))
		    {
				geneticLocation.append ("Chr");
				geneticLocation.append (cmOffset.getChromosome());

				float cM = cmOffset.getCmOffset().floatValue();

				if (cM >= 0.0)
				{
				    geneticLocation.append (", ");

				    // need to include a special label for QTLs
				    if ("QTL".equals(marker.getMarkerType()))
				    {
				    	geneticLocation.append ("cM position of peak correlated region/allele: ");
				    }
				    geneticLocation.append (cmOffset.getCmOffset().toString());
				    geneticLocation.append (" cM");
				}
				else if (cM == -1.0 && cytoband == null)
				{
				    geneticLocation.append (", Syntenic");
				}
		    }

		    if (cytoband != null)
		    {
				if (cmOffset == null)
				{
				    geneticLocation.append ("Chr");
				    geneticLocation.append (cytoband.getChromosome());
				}
				geneticLocation.append (", cytoband ");
				geneticLocation.append (cytoband.getCytogeneticOffset());
		    }
		    mav.addObject("geneticLocation", geneticLocation.toString());
		}

		// allele symbol, name, and synonyms with HTML superscript tags
		String symbolSup = FormatHelper.superscript(allele.getSymbol());
		String nameSup = FormatHelper.superscript(namesRaw);
		if (marker != null)
		{
		    String markerSymbolSup = FormatHelper.superscript(marker.getSymbol());
		    mav.addObject ("markerSymbolSup", markerSymbolSup);
		}

		mav.addObject ("symbolSup", symbolSup);
		mav.addObject ("nameSup", nameSup);

		List<AlleleSynonym> synonymList = allele.getSynonyms();
		if (synonymList.size() > 0)
		{
		    List<String> formattedSynonyms = new ArrayList<String>();
		    for(AlleleSynonym synonym : synonymList)
		    {
		    	formattedSynonyms.add(FormatHelper.superscript(synonym.getSynonym()));
		    }
		    mav.addObject ("synonyms", StringUtils.join(formattedSynonyms,", "));
		}

		// mutations and mutation label
		StringBuffer mutations = new StringBuffer();
		String mutationLabel = "Mutation";

		List<String> mutationList = allele.getMutations();
		if (mutationList == null || mutationList.size() == 0)
		{
		    mutations.append ("Undefined");
		}
		else if (mutationList.size() == 1)
		{
		    mutations.append (mutationList.get(0));
		}
		else
		{
		    for (String m : mutationList)
		    {
				if (mutations.length() > 0) mutations.append(", ");
				mutations.append(m);
		    }
		    mutationLabel = "Mutations";
		}

		String mutationString = mutations.toString();

		// We only want to show an Undefined mutation type if there is a
		// molecular note as well.
		if (!"Undefined".equals(mutationString) || allele.getMolecularDescription() != null)
		{
		    mav.addObject("mutations", mutationString);
		    mav.addObject("mutationLabel", mutationLabel);
		}

		// vector and vector type
		boolean hasMcl = false;
		List<AlleleCellLine> mutantCellLines = allele.getMutantCellLines();
		if (mutantCellLines.size() > 0)
		{
		    hasMcl = true;
		    AlleleCellLine mcl = mutantCellLines.get(0);
		    String vector = mcl.getVector();
		    String vectorType = mcl.getVectorType();

		    if ((vector != null) && (!"Not Specified".equals(vector)))
		    {
		    	mav.addObject("vector", vector);
		    }

		    if ((vectorType != null) && (!"Not Specified".equals(vectorType)))
		    {
		    	mav.addObject("vectorType", vectorType);
		    }
		}

		// mutant cell lines; ugly, but we need to do some slicing and dicing,
		// so actually compose HTML links here

		AlleleID kompID = allele.getKompID();

		if (hasMcl) {
		    ArrayList<String> mcLines = new ArrayList<String>();
		    String lastProvider = null;
		    String idToLink = null;
		    String provider = "";
		    StringBuffer out = new StringBuffer();
		    String name = null;

		    for (AlleleCellLine mcl : mutantCellLines)
		    {
		    	provider = mcl.getCreator();

				name = mcl.getPrimaryID();
				if ((name == null) || "null".equals(name)) {
				    name = mcl.getName();
				}

				if (lastProvider == null) {
				    idToLink = name;
				    lastProvider = provider;
				    mcLines.add(idToLink);

				} else if (lastProvider.equals(provider)) {
				    mcLines.add(name);

				} else {
				    if (out.length() > 0) {
					out.append (", ");
				    }
				    out.append (formatMutantCellLines (mcLines, lastProvider,
					idToLink, kompID));
				    idToLink = name;
				    lastProvider = provider;
				    mcLines = new ArrayList<String>();
				    mcLines.add(idToLink);
				}
		    }
		    if (mcLines.size() > 0) {
		        if (out.length() > 0) {
			    out.append (", ");
		        }
			out.append (formatMutantCellLines (mcLines, lastProvider,
			    idToLink, kompID));
		    }

		    // mixed alleles need a special message

		    if (allele.getIsMixed() == 1) {
		        out.append ("&nbsp;&nbsp;<B>More than one mutation may be present.</B>");
		    }

		    mav.addObject("mutantCellLines", out.toString());

		    if (mutantCellLines.size() > 1) {
			mav.addObject("mutantCellLineLabel", "Mutant Cell Lines");
		    } else {
			mav.addObject("mutantCellLineLabel", "Mutant Cell Line");
		    }
		}

		// parent cell line, background strain

		AlleleCellLine pcl = allele.getParentCellLine();
		if (pcl != null) {
		    if (pcl.getPrimaryID() != null) {
		        mav.addObject("parentCellLine", pcl.getPrimaryID());
		    } else {
			mav.addObject("parentCellLine", pcl.getName());
		    }

		    if ("Embryonic Stem Cell".equals(pcl.getCellLineType())) {
			mav.addObject("parentCellLineType", "ES Cell");
		    } else {
			mav.addObject("parentCellLineType", pcl.getCellLineType());
		    }
		}

		if (allele.getStrain() != null) {
		    mav.addObject("strainLabel", allele.getStrainLabel());
		    mav.addObject("backgroundStrain",
		        FormatHelper.superscript(allele.getStrain()) );
		}

		// add the allele's primary image and count of images (as a String)

		Image image = allele.getPrimaryImage();
		if (image != null) {
		    mav.addObject("primaryImage", image);

		    Integer thumbnailKey = image.getThumbnailImageKey();

		    if (thumbnailKey != null) {
		        Image thumbnail = imageFinder.getImageObjectByKey(
			    thumbnailKey.intValue());
			mav.addObject("thumbnailImage", thumbnail);

			Integer width = thumbnail.getWidth();
			Integer height = thumbnail.getHeight();

			StringBuffer xy = new StringBuffer();
			if (width != null) {
			    xy.append (" width='" + width.toString() + "'");
			}
			if (height != null) {
			    xy.append (" heigh='" + height.toString() + "'");
			}

			mav.addObject("thumbnailDimensions", xy.toString());
		    }
		}

		if (allele.getPhenotypeImages().size() > 0) {
		    mav.addObject("imageCount",
			"" + allele.getPhenotypeImages().size());

		    mav.addObject("imageLabel", FormatHelper.plural (
			allele.getPhenotypeImages().size(), "Image") );
		}

		if(allele.getMolecularImages().size() > 0)
		{
			// just pick the first one
			Image molImage = allele.getMolecularImages().get(0);
			Integer thumbnailKey = molImage.getThumbnailImageKey();

			logger.info("looking up thumbnail key="+thumbnailKey);
		    if (thumbnailKey != null)
		    {
		        Image thumbnail = imageFinder.getImageObjectByKey(thumbnailKey.intValue());
		        logger.info("found thumbnail = "+thumbnail);
		        logger.info("thumbnail id="+thumbnail.getPixeldbNumericID());
		        mav.addObject("molecularThumbnail",thumbnail);
				mav.addObject("molecularImage",molImage);
		    }
		}

		// add the allele's original reference

		Reference ref = allele.getOriginalReference();
		if (ref != null) {
		    mav.addObject("originalReference", ref);
		}

		// add various notes for convenience

		String driverNote = allele.getDriverNote();
		String generalNote = allele.getGeneralNote();
		String derivationNote = allele.getDerivationNote();

		mav.addObject("driverNote", driverNote);
		mav.addObject("generalNote", generalNote);
		mav.addObject("derivationNote", derivationNote);

		// already added qtlExpts, if available

	        String knockoutNote = null;
		if ((allele.getHolder() != null) && (allele.getCompanyID() != null)) {
		    String company = allele.getHolder();

		    if (company.equals("Lexicon")) {
			company = "Lexicon Genetics, Inc.";
		    } else if (company.equals("Deltagen")) {
			company = "Deltagen, Inc.";
		    }

		    knockoutNote = "See also, <a href='"
			+ ContextLoader.getConfigBean().getProperty("WI_URL")
			+ "external/ko/"
			+ allele.getHolder().toLowerCase()
			+ "/"
			+ allele.getCompanyID()
			+ ".html' class='MP'>data</a> as provided by " + company;

		    mav.addObject("knockoutNote", knockoutNote);
		}

		// IMSR counts, labels, and links

		int imsrCellLineCount = 0;
		int imsrStrainCount = 0;
		int imsrForMarkerCount = 0;

		if (allele.getImsrCellLineCount() != null) {
		    imsrCellLineCount = allele.getImsrCellLineCount().intValue();
		}
		if (allele.getImsrStrainCount() != null) {
		    imsrStrainCount = allele.getImsrStrainCount().intValue();
		}
		if (allele.getImsrCountForMarker() != null) {
		    imsrForMarkerCount = allele.getImsrCountForMarker().intValue();
		}

		String imsrCellLines = imsrCellLineCount + " "
			+ FormatHelper.plural (imsrCellLineCount, "line")
			+ " available";
		String imsrStrains = imsrStrainCount + " "
			+ FormatHelper.plural (imsrStrainCount, "strain")
			+ " available";
		String imsrForMarker = imsrForMarkerCount + " "
			+ FormatHelper.plural (imsrForMarkerCount,
			    "strain or line", "strains or lines")
			+ " available";

		String imsrUrl = ContextLoader.getConfigBean().getProperty("IMSRURL");

		if (imsrCellLineCount > 0)
		{
		    imsrCellLines = "<a href='" + imsrUrl + "summary?gaccid="
				+ allele.getPrimaryID()
				+ "&states=ES+Cell' class='MP'>" + imsrCellLines + "</a>";
		}
		if (imsrStrainCount > 0)
		{
		    imsrStrains = "<a href='" + imsrUrl + "summary?gaccid="
				+ allele.getPrimaryID()
				+ "&states=embryo&states=live&states=ovaries&states=sperm'"
		        + " class='MP'>" + imsrStrains + "</a>";
		}
		if (imsrForMarkerCount > 0)
		{
		    imsrForMarker = "<a href='" + imsrUrl + "summary?gaccid="
				+ marker.getPrimaryID()
				+ "&states=ES+Cell&states=embryo&states=live&states=ovaries"
				+ "&states=sperm' class='MP'>" + imsrForMarker + "</a>";
		}

		mav.addObject("imsrCellLines", imsrCellLines);
		mav.addObject("imsrStrains", imsrStrains);
		mav.addObject("imsrForMarker", imsrForMarker);

		// germline transmission / chimera generation

		String transmissionType = allele.getTransmissionType();
		String transmissionPhrase = allele.getTransmissionPhrase();
		String transmissionLabel = "Germline Transmission";

		if (transmissionType != null
				&& !"Not Applicable".equals(transmissionType))
		{
		    if (transmissionType.equals("Chimeric"))
		    {
		    	transmissionLabel = "Mouse Generated";
		    }

		    Reference transmissionRef = allele.getTransmissionReference();
		    if (transmissionRef != null)
		    {
		    	mav.addObject("transmissionReference", transmissionRef);
		    }

		    mav.addObject("transmissionLabel", transmissionLabel);
		    mav.addObject("transmissionPhrase", transmissionPhrase);
		}

		// GBrowse text strings -- adjust if we got coords from a marker, then
		// add to the mav

		if (startCoord > 0)
		{
		    gbrowseExtraLine = marker.getCountOfGeneTraps()+" gene trap insertions have trapped this gene<br/>";
		    gbrowseLabel = "View all gene trap sequence tags in this region";
		}

		mav.addObject("gbrowseExtraLine", gbrowseExtraLine);
		mav.addObject("gbrowseLabel", gbrowseLabel);

		// sequence tags

		Sequence representativeSeq = allele.getRepresentativeSequence();
		List<Sequence> otherSeq = allele.getNonRepresentativeSequences();
		String asmVersion = null;

		if (representativeSeq != null)
		{
		    mav.addObject("representativeSeq", representativeSeq);

		    // need to pick up coords for GBrowse from representative sequence?
		    // if so, add 5kb to each end.
		    if (startCoord < 0)
		    {
				List<SequenceLocation> seqLocs =
				    representativeSeq.getLocations();

				if (seqLocs != null && seqLocs.size() > 0)
				{
				    SequenceLocation seqLoc = seqLocs.get(0);
				    startCoord = seqLoc.getStartCoordinate().longValue() - 5000;
				    endCoord = seqLoc.getEndCoordinate().longValue() + 5000;
				    chromosome = seqLoc.getChromosome();
				    asmVersion = seqLoc.getVersion();
				}
		    }

		    // need to pick up point coordinate from representative sequence

		    Float point = representativeSeq.getPointCoordinate();
		    if (point != null)
		    {
		        pointCoord = point.longValue();
		    }
		}
		if (otherSeq != null && otherSeq.size() > 0)
		{
		    mav.addObject("otherSequences", otherSeq);

		    // if we couldn't get an assembly version from the representative
		    // sequence, then try to pull it from another sequence
		    for (Sequence seq : otherSeq)
		    {
		        if (asmVersion == null)
		        {
				    List<SequenceLocation> seqLocs = seq.getLocations();

				    if (seqLocs != null && seqLocs.size() > 0)
				    {
				        SequenceLocation seqLoc = seqLocs.get(0);
				        asmVersion = seqLoc.getVersion();
				    }
		        }
		    }
		}

		if (allele.getSequenceAssociations() != null)
		{
		    mav.addObject("sequenceCount",allele.getSequenceAssociations().size());
		}

		// assemble and include GBrowse URLs (do here because of complexity,
		// rather than in the JSP)

		if (startCoord >= 0 && endCoord >= 0 && chromosome != null)
		{
		    Properties externalUrls = ContextLoader.getExternalUrls();

		    // link to gbrowse
		    String gbrowseUrl = externalUrls.getProperty(
				"GBrowse_Allele").replace(
				"<chromosome>", chromosome).replace(
				"<start>", Long.toString(startCoord)).replace(
				"<end>", Long.toString(endCoord));

		    // thumbnail image for gbrowse
		    String gbrowseThumbnailUrl = "foo";

		    if (pointCoord < 0)
		    {
				gbrowseThumbnailUrl = externalUrls.getProperty(
				    "GBrowse_Allele_Thumbnail").replace(
				    "<chromosome>", chromosome).replace(
				    "<start>", Long.toString(startCoord)).replace(
				    "<end>", Long.toString(endCoord));
		    }
		    else
		    {
				gbrowseThumbnailUrl = externalUrls.getProperty(
				    "GBrowse_Allele_Thumbnail_With_Highlight").replace(
				    "<chromosome>", chromosome).replace(
				    "<start>", Long.toString(startCoord)).replace(
				    "<end>", Long.toString(endCoord)).replace(
				    "<point>", Long.toString(pointCoord));
		    }

		    // we only actually want the gbrowse thumbnail and link if we have
		    // a point coordinate.
		    if (pointCoord >= 0)
		    {
		        mav.addObject("gbrowseLink", gbrowseUrl);
		        mav.addObject("gbrowseThumbnail", gbrowseThumbnailUrl);
		    }
		}

		// if we had sequence tags, then we need to have an assembly version.
		// order of preference for determining the assembly version:
		//   1. from sequence coordinates
		//   	a. already tried to get from representative sequence first
		//   	b. already tried to get from other sequences secondarily
		//   2. from marker
		//   3. from class variable
		//   4. fall back on "GRCm38" if none for 1-3

		if(asmVersion==null)
		{
		    if (marker!=null)
		    {
				MarkerLocation mrkLoc = marker.getPreferredCoordinates();
				if(mrkLoc!=null)
				{
				    asmVersion = mrkLoc.getBuildIdentifier();
				}
		    }
		}

		// if still null, try the class variable
		if(asmVersion==null)
		{
			asmVersion = assemblyVersion!=null ? assemblyVersion : "GRCm38";
		}
		else
		{
			// cache the asmVersion in class variable
			assemblyVersion=asmVersion;
		}

		mav.addObject("assemblyVersion", asmVersion);

		// do we need to show the recombinase ribbon as Open?
		if ("open".equals(request.getParameter("recomRibbon")))
		{
		    mav.addObject("recomTeaserStyle", "display:none");
		    mav.addObject("recomWrapperStyle", "display:");
		}
		else
		{
		    mav.addObject("recomTeaserStyle", "display:");
		    mav.addObject("recomWrapperStyle", "display:none");
		}

		String userNote = allele.getRecombinaseUserNote();
		if (userNote != null && !userNote.equals(""))
		{
		    try {
				NotesTagConverter ntc = new NotesTagConverter();
				mav.addObject("recombinaseUserNote",ntc.convertNotes(userNote, '|'));
		    } catch (Exception e) {
		    	mav.addObject("recombinaseUserNote", userNote);
		    }
		}

		// identify which sections will appear, based on what data is present
		// (needed for table of contents)

		mav.addObject("hasNomenclature", "yes");
		mav.addObject("hasMutationDescription", "yes");

		if (hasMcl || (transmissionType != null) || (pcl != null) || (allele.getStrain() != null)) {
		    mav.addObject("hasMutationOrigin", "yes");
		}
		if (!alleleType.equals("QTL")) {
		    mav.addObject("hasIMSR", "yes");
		}
		if (allele.getCountOfExpressionAssayResults() > 0) {
		    mav.addObject("hasExpression", "yes");
		}
		if (allele.getCountOfReferences() > 0) {
		    mav.addObject("hasReferences", "yes");
		}
		if (allele.getHasDiseaseModel()) {
		    mav.addObject("hasDiseaseModel", "yes");
		}
		List<PhenoTableSystem> pheno = allele.getPhenoTableSystems();
		if ((pheno != null) && (pheno.size() > 0)) {
		    mav.addObject("hasPhenotypes", "yes");
		}
		if ((driverNote != null) && (driverNote.length() > 0)) {
		    mav.addObject("hasRecombinaseData", "yes");
		}
		if (hasQtlExpts || (knockoutNote != null) ||
			(derivationNote != null) || (generalNote != null)) {
		    mav.addObject("hasNotes", "yes");
		}

		return mav;
    }

    //---------------------//
    // Allele Pheno-Table
    //---------------------//
    @RequestMapping(value="/phenotable/{allID}")
    public ModelAndView phenoTableByAllId(
		  HttpServletRequest request,HttpServletResponse response,
		  @PathVariable("allID") String allID) {

        logger.debug("->phenoTableByAllId started");

        // need to add headers to allow AJAX access
        AjaxUtils.prepareAjaxHeaders(response);

    	// setup view object
        ModelAndView mav = new ModelAndView("phenotype_table");

    	// find the requested Allele
        logger.debug("->asking alleleFinder for allele");
    	List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
    	// there can be only one...
        if (alleleList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No allele found for " + allID);
            return mav;
        }
        if (alleleList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + allID);
            return mav;
        }
        Allele allele = alleleList.get(0);
        logger.debug("->1 allele found");

        List<PhenoTableSystem> phenoTableSystems =
          allele.getPhenoTableSystems();
        Hibernate.initialize(phenoTableSystems);
        logger.debug("->List<PhenoTableSystem> size - " + phenoTableSystems.size());

        // predetermine existance of a few columns.
        boolean hasSexCols=false;
        boolean hasSourceCols=false;

        for(PhenoTableGenotype g : allele.getPhenoTableGenotypeAssociations())
        {
        	if(g.getSexDisplay()!=null && !g.getSexDisplay().trim().equals(""))
        	{
        		hasSexCols=true;
        	}
        	if(g.getPhenoTableProviders().size()>1 || (g.getPhenoTableProviders().size()==1 &&
        			!g.getPhenoTableProviders().get(0).getProvider().equalsIgnoreCase("MGI")))
        	{
        		hasSourceCols=true;
        	}
        }
        mav.addObject("allele",allele);
        mav.addObject("phenoTableSystems",phenoTableSystems);
        mav.addObject("phenoTableGenotypes",allele.getPhenoTableGenotypeAssociations());
        mav.addObject("hasSexCols",hasSexCols);
        mav.addObject("hasSourceCols",hasSourceCols);
        mav.addObject("phenoTableGenoSize",allele.getPhenoTableGenotypeAssociations().size());

    	return mav;
    }


    /*
     *
     * Test genotype IDs [MGI:2166662]
     */
    @RequestMapping(value="/genoview/{genoID}")
    public ModelAndView genoview(
		  HttpServletRequest request,
		  HttpServletResponse response,
		  @PathVariable("genoID") String genoID) {

        logger.debug("->genoview started");

     // need to add headers to allow AJAX access
        AjaxUtils.prepareAjaxHeaders(response);

    	// setup view object
        ModelAndView mav = new ModelAndView("phenotype_table_geno_popup");

    	// find the requested Allele
        logger.debug("->asking genotypeFinder for genotype");

        List<Genotype> genotypeList = genotypeFinder.getGenotypeByID(genoID);
    	// there can be only one...
        if (genotypeList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No genotype found for " + genoID);
            return mav;
        }
        if (genotypeList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe reference found for " + genoID);
            return mav;
        }
        Genotype genotype = genotypeList.get(0);

        logger.debug("->1 genotype found");

        // eager load the entire collection
        // calling .size() is another trick eager load the entire collection
        Hibernate.initialize(genotype.getMPSystems());
        mav.addObject("genotype",genotype);
        mav.addObject("mpSystems", genotype.getMPSystems());

        for (GenotypeDisease gd : genotype.getDiseases())
        {
        	logger.info(" found disease: "+gd.getTerm());
        	for(GenotypeDiseaseReference gr : gd.getReferences())
        	{
        		logger.info(" found disease reference: "+gr.getJnumID());
        	}
        }
        if(genotype.hasPrimaryImage())
        {
        	logger.info(" has Image: "+genotype.getPrimaryImage().getMgiID());
        }
        mav.addObject("hasDiseaseModels", genotype.getDiseases().size()>0);
        mav.addObject("hasImage",genotype.hasPrimaryImage());
        mav.addObject("counter", request.getParameter("counter") );

    	return mav;
    }

    /*
    *
    * Test allele IDs [MGI:2166662]
    */
   @RequestMapping(value="/allgenoviews/{alleleID}")
   public ModelAndView allGenoviews(
		  HttpServletRequest request,
		  HttpServletResponse response,
		  @PathVariable("alleleID") String alleleID) {

       logger.debug("->all genoviews started");

       // need to add headers to allow AJAX access
       AjaxUtils.prepareAjaxHeaders(response);

   	// setup view object
       ModelAndView mav = new ModelAndView("phenotype_table_all_geno_popups");

   	// find the requested Allele
       logger.debug("->asking alleleFinder for allele");

       List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
   	// there can be only one...
       if (alleleList.size() < 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "No allele found for " + alleleID);
           return mav;
       }
       if (alleleList.size() > 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "Dupe reference found for " + alleleID);
           return mav;
       }
       Allele allele = alleleList.get(0);

       logger.debug("->1 allele found");

       // eager load the entire collection
       // calling .size() is another trick eager load the entire collection
       Hibernate.initialize(allele.getPhenoTableGenotypeAssociations());
       mav.addObject("genotypeAssociations",allele.getPhenoTableGenotypeAssociations());
   	return mav;
   }

   @RequestMapping(value="/alldiseasegenoviews/{alleleID}")
   public ModelAndView allDiseaseGenoviews(
		  HttpServletRequest request,
		  HttpServletResponse response,
		  @PathVariable("alleleID") String alleleID) {

       logger.debug("->all disease genoviews started");

       // need to add headers to allow AJAX access
       AjaxUtils.prepareAjaxHeaders(response);

   	// setup view object
       ModelAndView mav = new ModelAndView("phenotype_table_all_geno_popups");

   	// find the requested Allele
       logger.debug("->asking alleleFinder for allele");

       List<Allele> alleleList = alleleFinder.getAlleleByID(alleleID);
   	// there can be only one...
       if (alleleList.size() < 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "No allele found for " + alleleID);
           return mav;
       }
       if (alleleList.size() > 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "Dupe reference found for " + alleleID);
           return mav;
       }
       Allele allele = alleleList.get(0);

       logger.debug("->1 allele found");

       // eager load the entire collection
       // calling .size() is another trick eager load the entire collection
       Hibernate.initialize(allele.getDiseaseTableGenotypeAssociations());
       mav.addObject("genotypeAssociations",allele.getDiseaseTableGenotypeAssociations());

   	return mav;
   }

   //---------------------//
   // Allele Disease-Table
   //---------------------//
   @RequestMapping(value="/diseasetable/{allID}")
   public ModelAndView diseaseTableByAllId(
		  HttpServletRequest request,HttpServletResponse response,
		  @PathVariable("allID") String allID) {

       logger.debug("->diseaseTableByAllId started");

       // need to add headers to allow AJAX access
       AjaxUtils.prepareAjaxHeaders(response);

   	// setup view object
       ModelAndView mav = new ModelAndView("disease_table");

   	// find the requested Allele
       logger.debug("->asking alleleFinder for allele");
   	List<Allele> alleleList = alleleFinder.getAlleleByID(allID);
   	// there can be only one...
       if (alleleList.size() < 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "No allele found for " + allID);
           return mav;
       }
       if (alleleList.size() > 1) {
           // forward to error page
           mav = new ModelAndView("error");
           mav.addObject("errorMsg", "Dupe reference found for " + allID);
           return mav;
       }
       Allele allele = alleleList.get(0);
       logger.debug("->1 allele found");

       List<DiseaseTableDisease> diseaseTableDiseases =
         allele.getDiseaseTableDiseases();
       Hibernate.initialize(diseaseTableDiseases);
       logger.debug("->List<DiseaseTableDisease> size - " + diseaseTableDiseases.size());
       mav.addObject("allele",allele);
       mav.addObject("diseases",diseaseTableDiseases);
       mav.addObject("genotypes",allele.getDiseaseTableGenotypeAssociations());
       mav.addObject("diseaseTableGenoSize",allele.getDiseaseTableGenotypeAssociations().size());

   	return mav;
   }

    // convenience method -- construct a ModelAndView for the error page and
    // include the given 'msg' as the error String to be reported
    private ModelAndView errorMav (String msg) {
	ModelAndView mav = new ModelAndView("error");
	mav.addObject("errorMsg", msg);
	return mav;
    }

    // format mutant cell lines for output on allele detail page, with link
    // to provider
    private String formatMutantCellLines (List<String> mcLines,
	String provider, String idToLink, AlleleID kompID)
    {
		StringBuffer s = new StringBuffer();

		// comma-delimited list of mutant cell line IDs
		if(mcLines.size()>0)
		{
			s.append("<b>")
				.append(StringUtils.join(mcLines,"</b>, <b>"))
				.append("</b>");
		}

		// link to provider, if possible.  provider name if not.
		String link = "";

		if (kompID == null)
		{
			link = idLinker.getLink (provider, idToLink, provider);
		}
		else
		{
			link = idLinker.getLink ("KOMP-CSD-Project", kompID.getAccID(),"PROVIDER_HERE");
			if(provider!=null) link = link.replace("PROVIDER_HERE", provider);
		}

		if (link.equals(idToLink)) link = provider;

		s.append (" (")
			.append (link.replace(">", " CLASS='MP'>"))
			.append (")");

		return s.toString();
    }

        private void dbDate(ModelAndView mav) {
	        List<DatabaseInfo> dbInfo = dbInfoFinder.getInfo(new SearchParams()).getResultObjects();
	        for (DatabaseInfo db: dbInfo)
	        {
	        	if (db.getName().equalsIgnoreCase("built from mgd database date"))
	        	{
	        		DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	        		try {
						mav.addObject("databaseDate", df.parse(db.getValue()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        }
	    }

        private boolean notEmpty(String s)
        {
        	return s!=null && !s.equals("");
        }
        private boolean notEmpty(List l)
        {
        	return l!=null && l.size()>0;
        }
        // checks any cachable fields of the allele query form, and initializes them if needed
        private void initQFCache()
        {
        	if(AlleleQueryForm.getCollectionValues()==null)
        	{
        		// get collection facets
        		SearchParams sp = new SearchParams();
        		sp.setPageSize(0);
        		sp.setFilter(new Filter(SearchConstants.ALL_KEY,"[* TO *]",Filter.OP_HAS_WORD));
        		SearchResults sr = alleleFinder.getCollectionFacet(sp);
        		List<String> collectionValues = sr.getResultFacets();
        		AlleleQueryForm.setCollectionValues(collectionValues);
        	}
        }
        private Filter makeListFilter(List<String> values, String searchConstant)
        {
        	Filter f = null;
	   		if(values!=null && values.size()>0)
	   		{
	   			 List<Filter> vFilters = new ArrayList<Filter>();
	   			 for(String value : values)
	   			 {
	   				vFilters.add(new Filter(searchConstant,value,Filter.OP_EQUAL));
	   			 }
	   			 f = Filter.or(vFilters);
	   		}
	   		return f;
        }

       private long bdToLong(BigDecimal bd)
       {
    	   if(bd.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0) return Integer.MAX_VALUE;
    	   return bd.longValue();
       }
}