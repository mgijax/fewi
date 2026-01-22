package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionFactory;
import org.jax.mgi.fe.datamodel.Allele;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fe.datamodel.sort.SmartAlphaComparator;
import org.jax.mgi.fe.datamodel.sort.VocabTermComparator;
import org.jax.mgi.fewi.config.ContextLoader;
import org.jax.mgi.fewi.finder.AlleleFinder;
import org.jax.mgi.fewi.finder.VocabularyFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.searchUtil.entities.SolrAnatomyTerm;
import org.jax.mgi.fewi.summary.VocabBrowserSearchResult;
import org.jax.mgi.fewi.util.FormatHelper;
import org.jax.mgi.fewi.util.JSTreeNode;
import org.jax.mgi.fewi.util.TreeNode;
import org.jax.mgi.fewi.util.UserMonitor;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.jsonmodel.BrowserParent;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
import org.jax.mgi.shr.jsonmodel.BrowserChild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/*-------*/
/* class */
/*-------*/

/*
 * This controller maps all /glossary/ uri's
 */
@Controller
@RequestMapping(value="/vocab")
public class VocabularyController {

    //--- static variables ---//

    // cache of default paths for EMAPA/EMAPS terms;
    // maps from term ID to the JSON string for its path
    private static HashMap<String,String> pathCache = new HashMap<String,String>(); 
    
    public static String MA_VOCAB = "Adult Mouse Anatomy";
    public static String MP_VOCAB = "Mammalian Phenotype";
    public static String GO_VOCAB = "GO";
    public static String HPO_VOCAB = "Human Phenotype Ontology";
    public static String DO_VOCAB = "Disease Ontology";
    public static String CO_VOCAB = "Cell Ontology";
    
    // set of special words that must be matched exactly, rather than using wildcards
    private static Set<String> noWildcards = null;
    static {
    	noWildcards = new HashSet<String>();
    	noWildcards.add("of");
    	noWildcards.add("and");
    	noWildcards.add("to");
    	noWildcards.add("or");
    	noWildcards.add("for");
    	noWildcards.add("on");
    	noWildcards.add("the");
    }

    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger = LoggerFactory.getLogger(VocabularyController.class);

    @Autowired
    private AlleleFinder alleleFinder;
    
    @Autowired
    private VocabularyFinder vocabFinder;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private IDLinker idLinker;

    @Autowired
    private GXDController gxdController;
    
    @Autowired
    private GXDHTController gxdHtController;

    @Autowired
    private RecombinaseController recombinaseController;

    /* OMIM vocabulary browser */

    @RequestMapping("/omim")
    public String getOmimBrowserIndex() 
    {
    	logger.debug("Forwarding to /vocab/omim/A");
    	return "forward:/mgi/vocab/omim/A";
    }

    @RequestMapping("/omim/{subsetLetter}")
    public ModelAndView getOmimBrowser(HttpServletRequest request, @PathVariable("subsetLetter") String subsetLetter) 
    {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

        logger.debug("->getOmimBrowser->"+subsetLetter+" started");
        subsetLetter = subsetLetter.toUpperCase();
        
        // enable exclude NOT disease models filter for getting number of mouse models (Defined in VocabTerm)
        sessionFactory.getCurrentSession().enableFilter("termDiseaseModelExcludeNots");
        
        List<VocabTerm> terms = vocabFinder.getVocabSubset("OMIM",subsetLetter);
        
        logger.debug("found "+terms.size()+" omim terms for the subset '"+subsetLetter+"'");
        
        ModelAndView mav = new ModelAndView("omim_browser");

        mav.addObject("idLinker", idLinker);

        mav.addObject("subsetLetter",subsetLetter);
        mav.addObject("terms",terms);
        return mav;
    }

    /* PIRSF detail page */

    @RequestMapping("/pirsf/{id}")
    public ModelAndView getPirsfDetail(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

	logger.debug("->getPirsfDetail(" + id + ") started");

        // enable filter that will only return protein IDs for markers
        sessionFactory.getCurrentSession().enableFilter("onlyProteinSequences");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if ((terms == null) || (terms.size() < 1)) {
	    return errorMav("No Term Found");
	} else if (terms.size() > 1) {
	    return errorMav("Duplicate ID");
	}

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("pirsf_detail");

	mav.addObject("term", term);
	mav.addObject("title", term.getTerm() + " Protein Superfamily Detail");

	mav.addObject("idLinker", idLinker);

	return mav;
    }

    /*--- developmental anatomy browser ----------------------------------------------*/

    private void addAnatomyFooter (ModelAndView mav) {
	mav.addObject("message", "The Mouse Developmental Anatomy (EMAPA) Ontology was originally described in "
		+ "<a href='https://www.sciencedirect.com/science/article/pii/S0925477398000690?via%3Dihub' target='_blank'>Bard et al., 1998</a> "
		+ "and continues to be maintained, expanded, and refined by the GXD project "
		+ "(<a href='https://jbiomedsem.biomedcentral.com/articles/10.1186/2041-1480-4-15' target='_blank'>Hayamizu et al., 2013</a>; "
		+ "<a href='https://pmc.ncbi.nlm.nih.gov/articles/PMC4602063/' target='_blank'>Hayamizu et al. 2015</a>)."
		+ "<br/><br/>" 
		+ "Please <a href='mailto:MAontology@jax.org'>contact</a> us with suggestions, additions, or questions about the EMAPA Ontology. "
		+ "Your input is welcome."
		);
    }

    /* support new GXD anatomy browser by dynamically looking up children for
     * a given node
     */
    @RequestMapping("/gxd/anatomyChildren/{id}")
    public @ResponseBody String getAnatomyChildren(
	@PathVariable("id") String id) {

	logger.debug("->getAnatomyChildren(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if ((terms == null) || (terms.size() < 1)) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if ((terms == null) || (terms.size() != 1)) { return "[]"; }

	VocabTerm term = terms.get(0);

	StringBuffer sb = new StringBuffer();
	sb.append("{ children: [");

	List<VocabTerm> children = term.getChildren();
	Collections.sort(children, new VocabTermComparator());
	
	for (VocabTerm child : children) {
	    sb.append ( (new TreeNode(child)).getJson() );
	}

	sb.append("]}");

	return sb.toString().replace("}{", "},{");
    }

    /* search pane for GXD anatomy browser (EMAPA terms only) */

    @RequestMapping("/gxd/anatomySearch")
    public ModelAndView getAnatomySearchPane(@RequestParam("term") String term) {
	if ((term == null) || term.equals("")) {
	    logger.debug("->getAnatomySearchPane(null) started");
	    ModelAndView mav = new ModelAndView("vocabBrowser/search");
	    return mav;
	}

	logger.debug("->getAnatomySearchPane(" + term + ") started");

	// clean up the term a little by:
	// 1. converting hyphens to spaces
	// 2. stripping non-alphanumerics and non-whitespace
	
	String cleanTerm = term.replaceAll("-", " ");
	cleanTerm = cleanTerm.replaceAll(",", " ");
	cleanTerm = cleanTerm.replaceAll("[^A-Za-z0-9:\\s]", "");
	cleanTerm = cleanTerm.replaceAll("\\s\\s+", " ");

	SearchParams sp = new SearchParams();

	// need to AND the requested tokens together, then OR the searches of
	// structure, synonyms, and crossRefs

	ArrayList<Filter> structureFilters = new ArrayList<Filter>();
	ArrayList<Filter> synonymFilters = new ArrayList<Filter>();
	ArrayList<Filter> crossRefFilters = new ArrayList<Filter>();
	ArrayList<String> tokens = new ArrayList<String>();

	for (String token : cleanTerm.split("\\s")) {
	    structureFilters.add(new Filter(SearchConstants.STRUCTURE, token));
	    synonymFilters.add(new Filter(SearchConstants.SYNONYM, token));
	    crossRefFilters.add(new Filter(SearchConstants.CROSS_REF, token));
	    tokens.add(token);
	}

	ArrayList<Filter> eitherFilters = new ArrayList<Filter>();

	eitherFilters.add(Filter.and(structureFilters));
	eitherFilters.add(Filter.and(synonymFilters));
	eitherFilters.add(Filter.and(crossRefFilters));

	sp.setFilter(Filter.or(eitherFilters));

	// We need to get 501 results, so we know if the count should display
	// as 500 or as 500+
	int maxToReturn = 500;
	sp.setPageSize(maxToReturn + 1);

	SearchResults<SolrAnatomyTerm> sr = vocabFinder.getAnatomyTerms(sp);
	List<SolrAnatomyTerm> resultObjects = sr.getResultObjects();
	
    boolean searchedByID = Pattern.matches("[A-Za-z]+:[0-9]+", term.trim());
	if (searchedByID) {
		Collections.sort(resultObjects, new SolrAnatomyTermComparator());
	}

	// each matching term needs to know the search tokens, so it can use
	// them for highlighting

	for (SolrAnatomyTerm matchedTerm : sr.getResultObjects()) {
	    matchedTerm.setTokens(tokens);
	}

	List<SolrAnatomyTerm> results = floatBeginsMatches(
	    sr.getResultObjects(), tokens);

	ModelAndView mav = new ModelAndView("anatomy_search");
	mav.addObject("results", results);
	mav.addObject("searchTerm", term);

	int numReturned = sr.getResultObjects().size();
	if (numReturned > maxToReturn) {
	    mav.addObject("resultCount", maxToReturn + "+");
	} else {
	    mav.addObject("resultCount", numReturned);
	}
	return mav;
    }

    /* float items to the top which matched the search tokens in order (for
     * structures which match the EMAPA search terms.  float exact matches
     * even higher than begins matches.
     */
    private List<SolrAnatomyTerm> floatBeginsMatches (
	List<SolrAnatomyTerm> matches, List<String> tokens) {

	ArrayList<SolrAnatomyTerm> structureExact = new ArrayList<SolrAnatomyTerm>();
	ArrayList<SolrAnatomyTerm> synonymExact = new ArrayList<SolrAnatomyTerm>();
	ArrayList<SolrAnatomyTerm> structureBegins = new ArrayList<SolrAnatomyTerm>();
	ArrayList<SolrAnatomyTerm> synonymBegins = new ArrayList<SolrAnatomyTerm>();
	ArrayList<SolrAnatomyTerm> other = new ArrayList<SolrAnatomyTerm>();

	String searchString = "";
	boolean isFirst = true;

	for (String s : tokens) {
	    if (isFirst) {
		searchString = s;
		isFirst = false;
	    } else {
		searchString = searchString + " " + s;
	    }
	}
	searchString = searchString.toLowerCase();

	for (SolrAnatomyTerm match : matches) {
	    String structure = match.getStructure().toLowerCase();
	    List<String> synonyms = match.getSynonyms();

	    // order of preference:
	    // 1. exact match to structure
	    // 2. exact match to a synonym
	    // 3. begins match to a structure
	    // 4. begins match to a synonym
	    // 5. everything else
	    
	    boolean done = false;
	    boolean exactSynonym = false;
	    boolean beginsSynonym = false;

	    if (structure.equals(searchString)) {
		structureExact.add(match);
		done = true;
	    } else if (structure.replace('-', ' ').equals(searchString)) {
		structureExact.add(match);
		done = true; 
	    } else if (synonyms != null) {
		for (String synonym : synonyms) {
		    String synLower = synonym.toLowerCase();

		    if (synLower.equals(searchString)) {
			exactSynonym = true;
			break;
		    } else if (synLower.startsWith(searchString)) {
			beginsSynonym = true;
		    }
		}
	    }

	    if (!done && exactSynonym) {
		synonymExact.add(match);
		done = true;
	    }

	    if (!done && structure.startsWith(searchString)) {
		structureBegins.add(match);
		done = true;
	    }

	    if (!done && beginsSynonym) {
		synonymBegins.add(match);
		done = true;
	    }

	    if (!done) {
		other.add(match);
	    }
	}

	structureExact.addAll(synonymExact);
	structureExact.addAll(structureBegins);
	structureExact.addAll(synonymBegins);
	structureExact.addAll(other);

	return structureExact;
    }

    /* default path from root to specified anatomy term, in JSON format */

    @RequestMapping("/gxd/anatomy/defaultPath/{id}")
    public @ResponseBody String getDefaultPath(@PathVariable("id") String id) {
	logger.debug("->getDefaultPath(" + id + ") started");

	if (pathCache.containsKey(id)) {
	    return pathCache.get(id);
	}

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if ((terms == null) || (terms.size() < 1)) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if ((terms == null) || (terms.size() < 1)) { return "[]"; }

	// shouldn't be multiple terms for an ID, but if so, we just deal
	// with the first anyway

	VocabTerm term = terms.get(0);

	// need to collect terms up the default path to the root

	VocabTerm parent = term.getDefaultParent();

	ArrayList<VocabTerm> path = new ArrayList<VocabTerm>();
	path.add(term);
	
	while (parent != null) {
	    path.add(0, parent);
	    parent = parent.getDefaultParent();
	}

	// now convert it to a string, cache it, and return it

	StringBuffer sb = new StringBuffer();
	sb.append("[ '");

	boolean isFirst = true;

	for (VocabTerm t : path) {
	    if (isFirst) { isFirst = false; }
	    else { sb.append("', '"); }

	    sb.append(t.getPrimaryId());
	}
	sb.append("' ]");

	String s = sb.toString();
	pathCache.put(id, s);
	return s;
    }

    /* get JSON for a single node for the new GXD Anatomy browser */

    @RequestMapping("/gxd/anatomy/node/{id}")
    public @ResponseBody String getAnatomyNode(@PathVariable("id") String id) {
	logger.debug("->getAnatomyNode(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if ((terms == null) || (terms.size() < 1)) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if ((terms == null) || (terms.size() < 1)) { return "{}"; }

	// shouldn't be multiple terms for an ID, but if so, we just deal
	// with the first anyway

	VocabTerm term = terms.get(0);
	TreeNode node = new TreeNode(term);
	return node.getJson();
    }

    // get the page title for the browser's title bar (anatomy browser)
    private String getAnatomyTermDetailTitle (VocabTerm term) {

	// EMAPA term has no Theiler stage
	if (term.getIsEmapa()) {
	    return term.getTerm() + " Anatomy Term GXD Mouse ("
		+ term.getPrimaryId() + ")";
	}

	// EMAPS term includes its theiler stage
	return term.getTerm() + " TS" + term.getEmapInfo().getStage()
	    + " Anatomy Term GXD Mouse (" + term.getPrimaryId() + ")";
    }

    /* term detail pane for new GXD Anatomy browser  */

    @RequestMapping("/gxd/anatomy/termPane/{id}")
    public ModelAndView getAnatomyTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

	logger.debug("->getAnatomyTermPane(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if ((terms == null) || (terms.size() < 1)) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if ((terms == null) || (terms.size() < 1)) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_term_pane");

	mav.addObject("term", term);
	addAnatomyFooter(mav);
	mav.addObject("title", getAnatomyTermDetailTitle(term)); 

	// compose the dropdown list for linking to term at other stages

	//String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	StringBuffer dropdown = new StringBuffer();
	dropdown.append ("<form id='stageLinkerForm'><select name='stageLinker' id='stageLinker' onChange='if (stageLinker.value != \"\") { resetPanes(stageLinker.value, true); }'></form>");
	dropdown.append ("<option value=''>Select developmental stage</option>");

	VocabTerm emapaTerm = null;
	String dropdownMsg = null;

	mav.addObject("title", getAnatomyTermDetailTitle(term));

	if (term.getIsEmapa()) {
	    emapaTerm = term;
	    dropdownMsg = "Use pick list to choose specific stage.";

	    mav.addObject("seoKeywords",
		"mouse, mice, murine, Mus, anatomical, anatomy, term, tree, "
		+ term.getTerm());
	    mav.addObject("seoDescription", "View "
		+ term.getTerm()
		+ " anatomical term: Theiler stages, synonyms, definition,"
		+ " parent terms, and tree structure.");

	} else {
	    emapaTerm = term.getEmapInfo().getEmapaTerm();
	    dropdown.append("<option value='");
	    dropdown.append(emapaTerm.getPrimaryId());
	    dropdown.append("'>Any Theiler stage</option>");
	    dropdownMsg = "Use pick list to choose other stage or full range view.";
	    mav.addObject("seoKeywords",
		"mouse, mice, murine, Mus, anatomical, anatomy, term, tree, "
		+ term.getTerm() + " TS" + term.getEmapInfo().getStage());
	    mav.addObject("seoDescription", "View "
		+ term.getTerm()
		+ " TS" + term.getEmapInfo().getStage()
		+ " anatomical term: Theiler stages, synonyms, definition,"
		+ " parent terms, and tree structure.");
	}

	String emapsID;
	for (VocabTerm emapsTerm : emapaTerm.getEmapsChildrenSorted()) {
	    emapsID = emapsTerm.getPrimaryId();
	    if (!emapsID.equals(id)) {
		dropdown.append("<option value='");
		dropdown.append(emapsID);
		dropdown.append("' title='");
		dropdown.append(FormatHelper.getDescription(
		    emapsTerm.getEmapInfo().getStage().intValue() ));
		dropdown.append("'>TS ");
		dropdown.append(emapsTerm.getEmapInfo().getStage());
		dropdown.append(" ");
		dropdown.append(FormatHelper.getDPC(
		    emapsTerm.getEmapInfo().getStage().intValue() ));
		dropdown.append("</option>");
	    }
	}

	dropdown.append ("</select>");

	mav.addObject("dropdown", dropdown.toString()); 
	mav.addObject("dropdownMsg", dropdownMsg);

//	logger.info("related term count: " + emapaTerm.getRelatedTerms("EMAPA to MP").size());
	if (emapaTerm.getRelatedTerms("EMAPA to MP").size() > 0) {
		mav.addObject("hasPhenotypeAssociations", 1);
	}
	return mav;
    }

    /* GXD Anatomy browser with terms that have specified crossReference ID (MP) */

    @SuppressWarnings("unchecked")
	@RequestMapping("/gxd/anatomy/by_phenotype/{id}")
    public ModelAndView getAnatomyDetailByPhenotype(@PathVariable("id") String id) {
	logger.info("->getAnatomyDetailByPhenotype(" + id + ") started");

	List<SolrAnatomyTerm> results = (List<SolrAnatomyTerm>) getAnatomySearchPane(id).getModel().get("results");
	if (results.size() == 0) {
		return errorMav("Phenotype ID has no anatomy cross-references: " + id);
	}
	
	List<VocabTerm> terms = vocabFinder.getTermByID(results.get(0).getAccID());

	if ((terms == null) || (terms.size() < 1)) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_detail");
	mav.addObject("term", term);
	addAnatomyFooter(mav);
	mav.addObject("crossRef", id);

	return mav;
    }

    /* GXD Anatomy browser with terms that can be tied to a specified allele */

    @SuppressWarnings("unchecked")
	@RequestMapping("/gxd/anatomy/by_allele/{id}")
    public ModelAndView getAnatomyDetailByAllele(@PathVariable("id") String id) {
	logger.info("->getAnatomyDetailByAllele(" + id + ") started");

	List<SolrAnatomyTerm> results = (List<SolrAnatomyTerm>) getAnatomySearchPane(id).getModel().get("results");
	if (results.size() == 0) {
		return errorMav("Allele ID has no related anatomy terms: " + id);
	}
	
	// pick up the first vocabulary term for the matches
	
	List<VocabTerm> terms = vocabFinder.getTermByID(results.get(0).getAccID());

	if ((terms == null) || (terms.size() < 1)) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	// pick up the allele, in case we want to add context info to the page in the future
	
	List<Allele> alleles = alleleFinder.getAlleleByID(id);

	if (alleles.size() < 1) { return errorMav("No Allele found"); }
	else if (alleles.size() > 1) { return errorMav("Duplicate ID"); }

	Allele allele = alleles.get(0);
	
	// compose the mav

	ModelAndView mav = new ModelAndView("anatomy_detail");
	mav.addObject("term", term);
	mav.addObject("allele", allele);
	mav.addObject("crossRef", id);
	addAnatomyFooter(mav);

	return mav;
    }

    /* new GXD Anatomy browser for EMAPA and EMAPS terms */

    @RequestMapping("/gxd/anatomy")
    public ModelAndView getAnatomyDetail() {
	logger.info("->getAnatomyDetail() started");

	// start with 'embryo' as a default
	List<VocabTerm> terms = vocabFinder.getTermByID("EMAPA:16039");

	if ((terms == null) || (terms.size() < 1)) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_detail");
	mav.addObject("term", term);
	addAnatomyFooter(mav);

	return mav;
    }

    /* new GXD Anatomy browser for EMAPA and EMAPS terms (given an ID) */

    @RequestMapping("/gxd/anatomy/{id}")
    public ModelAndView getAnatomyDetail(@PathVariable("id") String id) {
	logger.debug("->getAnatomyDetail(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if ((terms == null) || (terms.size() < 1)) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if ((terms == null) || (terms.size() < 1)) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_detail");
	mav.addObject("term", term);
	addAnatomyFooter(mav);

	return mav;
    }
    
    /*--------------------------------------------------------------------------------*/

    // convenience method -- construct a ModelAndView for the error page and
    // include the given 'msg' as the error String to be reported
    private ModelAndView errorMav (String msg) {
    	ModelAndView mav = new ModelAndView("error");
    	mav.addObject("errorMsg", msg);
    	return mav;
    }

    /*--- MA browser ----------------------------------------------------------------*/

    /* Adult Mouse Anatomy browser home page
     */
    @RequestMapping("/gxd/ma_ontology")
    public ModelAndView getMouseAnatomyDetail(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getMouseAnatomyDetail() started");

    	// start with 'anatomical structure' as a default
    	return getMouseAnatomyDetail("MA:0003000");
    }
    
    /* fill in the standard URLs for the Adult Mouse Anatomy browser
     */
    private ModelAndView fillMouseAnatomyUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/gxd/ma_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	mav.addObject("treeInitialUrl", baseUrl + "treeInitial");
    	mav.addObject("treeChildrenUrl", baseUrl + "treeChildren");
    	mav.addObject("autocompleteUrl", ContextLoader.getConfigBean().getProperty("FEWI_URL") + "autocomplete/ma_ontology?query=");
    	mav.addObject("message", "The Adult Mouse Anatomy (MA) Ontology was originally described in "
    		+ "<a href='https://genomebiology.biomedcentral.com/articles/10.1186/gb-2005-6-3-r29' target='_blank'>Hayamizu et al., 2005</a> "
		+ "and continues to be maintained, expanded, and refined by the GXD project "
		+ "(<a href='https://pmc.ncbi.nlm.nih.gov/articles/PMC4602063/' target='_blank'>Hayamizu et al, 2015</a>). "
		+ "<br/><br/> "
		+ "Please <a href='mailto:MAontology@jax.org'>contact</a> us with suggestions, additions, or questions about the MA Ontology. "
		+ "Your input is welcome.");
    	return mav;
    }
    
    /* get the browser title string for a term in the Adult Mouse Anatomy
     */
    private String getMouseAnatomyTitle(BrowserTerm term) {
    	if (term == null) { return "Adult Mouse Anatomy Browser"; }
    	return term.getTerm() + " Adult Mouse Anatomy Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* Adult Mouse Anatomy browser for a specified MA ID */

    @RequestMapping("/gxd/ma_ontology/{id}")
    public ModelAndView getMouseAnatomyDetail(@PathVariable("id") String id) {
    	logger.debug("->getMouseAnatomyDetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, MA_VOCAB);
    	mav.addObject("pageTitle", "Adult Mouse Anatomy Browser");
    	mav.addObject("searchPaneTitle", "Anatomy Search");
    	mav.addObject("termPaneTitle", "Adult Anatomy Term Detail");
    	mav.addObject("treePaneTitle", "Anatomy Tree View");
    	mav.addObject("helpDoc", "VOCAB_amad_browser_help.shtml");
    	mav.addObject("branding", "GXD");
    	mav.addObject("seoDescription", "The Adult Mouse Anatomy Ontology organizes anatomical structures "
    		+ "for the postnatal mouse (Theiler stage 28) spatially and functionally, using 'is a' and "
    		+ "'part of' relationships.  This browser can be used to view anatomical terms and their "
    		+ "relationships in a hierarchical display.");
    	mav.addObject("title", getMouseAnatomyTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMouseAnatomyUrls(mav);
    	return mav;
    }
    
    /* Adult Mouse Anatomy term detail pane
     */
    @RequestMapping("/gxd/ma_ontology/termPane/{id}")
    public ModelAndView getMouseAnatomyTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getMouseAnatomyTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, MA_VOCAB);
    	mav.addObject("title", getMouseAnatomyTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMouseAnatomyUrls(mav);
    	return mav;
    }
    
    /* Adult Mouse Anatomy search pane
     */
    @RequestMapping("/gxd/ma_ontology/search")
    public ModelAndView getMouseAnatomySearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, MA_VOCAB);
    	fillMouseAnatomyUrls(mav);
    	return mav;
    }

    /* Adult Mouse Anatomy browser - initial load of terms for tree view, for the term with the
     * given ID.  Rules:
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children
     * 4. repeat #3 all the way up to the root node
     */
    @RequestMapping("/gxd/ma_ontology/treeInitial")
    public @ResponseBody String getMouseAnatomyTreeInitial(@RequestParam("id") String id) {
    	return this.getSharedBrowserTreeInitial(id, MA_VOCAB);
    }

    /* Adult Mouse Anatomy browser - load children of the term with the given ID
     */
    @RequestMapping("/gxd/ma_ontology/treeChildren")
    public @ResponseBody String getMouseAnatomyTreeChildren(@RequestParam("id") String id,
    		@RequestParam("nodeID") String nodeID, @RequestParam("edgeType") String edgeType) {

    	// remove any script tags or alert() calls embedded in edgeType
    	edgeType = FormatHelper.noScript(FormatHelper.noAlert(edgeType));
    	return this.getSharedBrowserTreeChildren(id, nodeID, edgeType, MA_VOCAB);
    }


    /*--- Cell Ontology browser ----------------------------------------------------------------*/

    /* Cell Ontology browser home page
     */
    @RequestMapping("/cell_ontology")
    public ModelAndView getCellOntologyDetail(HttpServletRequest request) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getCellOntologyDetail() started");

    	// this is the default term opened
    	return getCellOntologyDetail("CL:0000000");
    }
    
    /* fill in the standard URLs for the Cell Ontology browser
     */
    private ModelAndView fillCellOntologyUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/cell_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	mav.addObject("treeInitialUrl", baseUrl + "treeInitial");
    	mav.addObject("treeChildrenUrl", baseUrl + "treeChildren");
    	mav.addObject("autocompleteUrl", ContextLoader.getConfigBean().getProperty("FEWI_URL") + "autocomplete/cell_ontology?query=");
    	mav.addObject("message", "The Cell Ontology (CL) is an OBO Foundry ontology and is available at: "
		+ "<a href='https://obofoundry.org/ontology/cl.html' target='_blank'>https://obofoundry.org/ontology/cl.html</a>. "
		+ "<br/><br/>Additional terms or revisions may be requested through the CL issue tracker at GitHub. "
		+ "Your input is welcome."
	);
    	return mav;
    }
    
    /* get the browser title string for a term in the Cell Ontology browser
     */
    private String getCellOntologyTitle(BrowserTerm term) {
    	if (term == null) { return "Cell Ontology Browser"; }
    	return term.getTerm() + " Cell Ontology Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* Cell Ontology browser for a specified ID */

    @RequestMapping("/cell_ontology/{id}")
    public ModelAndView getCellOntologyDetail(@PathVariable("id") String id) {
    	logger.debug("->getCellOntologyDetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, CO_VOCAB);
    	mav.addObject("pageTitle", "Cell Ontology Browser");
    	mav.addObject("searchPaneTitle", "Cell Type Search");
    	mav.addObject("termPaneTitle", "Cell Type Detail");
    	mav.addObject("treePaneTitle", "Cell Type Tree View");
    	mav.addObject("helpDoc", "VOCAB_cl_browser_help.shtml");
    	mav.addObject("branding", "GXD");
    	mav.addObject("hideIsaIcon", "true");
    	mav.addObject("seoDescription", "The Cell Ontology (CL) is a structured controlled "
    		+ "vocabulary that describes a broad range of canonical biological cell types. "
    		+ "The CL is not organism-specific, covering cell types from prokaryotes to mammals, "
    		+ "but excludes plant cell types. The ontology organizes cell type classes into a DAG "
    		+ "hierarchy composed exclusively of 'is a' relationships. The Cell Ontology Browser "
    		+ "can be used to find and view descriptions of cell types, and links to GXD expression "
    		+ "results, GXD HT experiments and MGI Recombinase Alleles.");

    	mav.addObject("title", getCellOntologyTitle((BrowserTerm) mav.getModel().get("term")));
    	fillCellOntologyUrls(mav);
    	return mav;
    }
    
    /* Cell Ontology term detail pane
     */
    @RequestMapping("/cell_ontology/termPane/{id}")
    public ModelAndView getCellOntologyTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getCellOntologyTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, CO_VOCAB);
    	mav.addObject("title", getCellOntologyTitle((BrowserTerm) mav.getModel().get("term")));
    	fillCellOntologyUrls(mav);
    	return mav;
    }
    
    /* Cell Ontology search pane
     */
    @RequestMapping("/cell_ontology/search")
    public ModelAndView getCellOntologySearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, CO_VOCAB);
    	fillCellOntologyUrls(mav);
    	return mav;
    }

    /* Rules of initial load of terms for tree view for given ID
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children
     * 4. repeat #3 all the way up to the root node
     */
    @RequestMapping("/cell_ontology/treeInitial")
    public @ResponseBody String getCellOntologyTreeInitial(@RequestParam("id") String id) {
    	return this.getSharedBrowserTreeInitial(id, CO_VOCAB);
    }

    /* CellOntology browser - load children of the term with the given ID
     */
    @RequestMapping("/cell_ontology/treeChildren")
    public @ResponseBody String getCellOntologyTreeChildren(@RequestParam("id") String id,
    		@RequestParam("nodeID") String nodeID, @RequestParam("edgeType") String edgeType) {

    	// remove any script tags or alert() calls embedded in edgeType
    	edgeType = FormatHelper.noScript(FormatHelper.noAlert(edgeType));
    	return this.getSharedBrowserTreeChildren(id, nodeID, edgeType, CO_VOCAB);
    }
    
    /*--- MP browser ----------------------------------------------------------------*/

    /* Mammalian Phenotype browser home page
     */
    @RequestMapping("/mp_ontology")
    public ModelAndView getMPDetail() {
    	logger.debug("->getMPDetail() started");

    	// start with 'mammalian phenotype' as a default
    	return getMPDetail("MP:0000001");
    }
    
    /* fill in the standard URLs for the Mammalian Phenotype browser
     */
    private ModelAndView fillMPUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/mp_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	mav.addObject("treeInitialUrl", baseUrl + "treeInitial");
    	mav.addObject("treeChildrenUrl", baseUrl + "treeChildren");
    	mav.addObject("autocompleteUrl", ContextLoader.getConfigBean().getProperty("FEWI_URL") + "autocomplete/mp_ontology?query=");
    	mav.addObject("message", "Your input is welcome. Additional terms or revisions may be requested through "
    		+ "our <a href='https://github.com/obophenotype/mammalian-phenotype-ontology/issues' target='_blank'>issue tracker</a> "
   			+ "at GitHub. Please <a href='mailto:pheno@jax.org'>contact us</a> with any other questions about the "
    		+ "Mammalian Phenotype Ontology.<P>This ontology is also used by the "
   			+ "<a href='http://rgd.mcw.edu/rgdweb/ontology/search.html?term' target='_blank'>Rat Genome Database</a>.");
    	return mav;
    }
    
    /* get the browser title string for a term in the MP browser
     */
    private String getMPTitle(BrowserTerm term) {
    	if (term == null) { return "Mammalian Phenotype Browser"; }
    	return term.getTerm() + " Mammalian Phenotype Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* Mammalian Phenotype browser for a specified EMAPA ID */

   	@SuppressWarnings("unchecked")
    @RequestMapping("/mp_ontology/by_anatomy/{id}")
    public ModelAndView getMPAnatomySearch(@PathVariable("id") String id) {
    	logger.debug("->getMPAnatomySearch(" + id + ") started");

    	ModelAndView tempMav = getSharedBrowserSearchPane(id, MP_VOCAB);
		List<VocabBrowserSearchResult> results = (List<VocabBrowserSearchResult>) tempMav.getModel().get("results");
    	String topID = "MP:0000001";
    	if (results.size() > 0) {
    		topID = results.get(0).getAccID();
    	}

    	ModelAndView mav = getSharedBrowserDetail(topID, MP_VOCAB);
    	mav.addObject("searchTerm", id);
    	mav.addObject("pageTitle", "Mammalian Phenotype Browser");
    	mav.addObject("searchPaneTitle", "Phenotype Search");
    	mav.addObject("termPaneTitle", "Phenotype Term Detail");
    	mav.addObject("treePaneTitle", "Phenotype Tree View");
    	mav.addObject("helpDoc", "VOCAB_mp_browser_help.shtml");
    	mav.addObject("branding", "MGI");
    	mav.addObject("seoDescription", "The Mammalian Phenotype (MP) Ontology is a community effort to "
    		+ "provide standard terms for annotating phenotypic data. You can use this browser to view terms, "
    		+ "definitions, and term relationships in a hierarchical display. Links to summary annotated "
    		+ "phenotype data at MGI are provided in Term Detail reports.");
    	mav.addObject("title", getMPTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMPUrls(mav);
    	return mav;
    }
    
    /* Mammalian Phenotype browser for a specified MP ID */

    @RequestMapping("/mp_ontology/{id}")
    public ModelAndView getMPDetail(@PathVariable("id") String id) {
    	logger.debug("->getMPDetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, MP_VOCAB);
    	mav.addObject("pageTitle", "Mammalian Phenotype Browser");
    	mav.addObject("searchPaneTitle", "Phenotype Search");
    	mav.addObject("termPaneTitle", "Phenotype Term Detail");
    	mav.addObject("treePaneTitle", "Phenotype Tree View");
    	mav.addObject("helpDoc", "VOCAB_mp_browser_help.shtml");
    	mav.addObject("branding", "MGI");
    	mav.addObject("seoDescription", "The Mammalian Phenotype (MP) Ontology is a community effort to "
    		+ "provide standard terms for annotating phenotypic data. You can use this browser to view terms, "
    		+ "definitions, and term relationships in a hierarchical display. Links to summary annotated "
    		+ "phenotype data at MGI are provided in Term Detail reports.");
    	mav.addObject("title", getMPTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMPUrls(mav);
    	return mav;
    }
    
    /* Mammalian Phenotype term detail pane
     */
    @RequestMapping("/mp_ontology/termPane/{id}")
    public ModelAndView getMPTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getMPTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, MP_VOCAB);
    	mav.addObject("title", getMPTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMPUrls(mav);
    	return mav;
    }
    
    /* Mammalian Phenotype search pane
     */
    @RequestMapping("/mp_ontology/search")
    public ModelAndView getMPSearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, MP_VOCAB);
    	fillMPUrls(mav);
    	return mav;
    }

    /* Mammalian Phenotype browser - initial load of terms for tree view, for the term with the
     * given ID.  Rules:
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children
     * 4. repeat #3 all the way up to the root node
     */
    @RequestMapping("/mp_ontology/treeInitial")
    public @ResponseBody String getMPTreeInitial(@RequestParam("id") String id) {
    	return this.getSharedBrowserTreeInitial(id, MP_VOCAB);
    }

    /* Mammalian Phenotype browser - load children of the term with the given ID
     */
    @RequestMapping("/mp_ontology/treeChildren")
    public @ResponseBody String getMPTreeChildren(@RequestParam("id") String id,
    		@RequestParam("nodeID") String nodeID, @RequestParam("edgeType") String edgeType) {
    	return this.getSharedBrowserTreeChildren(id, nodeID, edgeType, MP_VOCAB);
    }
    
    /*--- GO browser ----------------------------------------------------------------*/

    /* Gene Ontology (GO) browser home page
     */
    @RequestMapping("/gene_ontology")
    public ModelAndView getGODetail() {
    	logger.debug("->getGODetail() started");

    	// start with 'molecular function' as a default
    	return getGODetail("GO:0003674");
    }
    
    /* fill in the standard URLs for the GO browser
     */
    private ModelAndView fillGOUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/gene_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	mav.addObject("treeInitialUrl", baseUrl + "treeInitial");
    	mav.addObject("treeChildrenUrl", baseUrl + "treeChildren");
    	mav.addObject("autocompleteUrl", ContextLoader.getConfigBean().getProperty("FEWI_URL") + "autocomplete/gene_ontology?query=");
    	return mav;
    }
    
    /* get the browser title string for a term in the GO browser
     */
    private String getGOTitle(BrowserTerm term) {
    	if (term == null) { return "Gene Ontology Browser"; }
    	return term.getTerm() + " Gene Ontology Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* GO browser for a specified GO ID */

    @RequestMapping("/gene_ontology/{id}")
    public ModelAndView getGODetail(@PathVariable("id") String id) {
    	logger.debug("->getGODetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, GO_VOCAB);
    	mav.addObject("pageTitle", "Gene Ontology Browser");
    	mav.addObject("searchPaneTitle", "GO Search");
    	mav.addObject("termPaneTitle", "GO Term Detail");
    	mav.addObject("treePaneTitle", "GO Tree View");
    	mav.addObject("helpDoc", "VOCAB_go_browser_help.shtml");
    	mav.addObject("branding", "GO");
    	mav.addObject("seoDescription", "The Gene Ontology (GO) project is a collaborative effort to address "
   			+ "the need for consistent descriptions of gene products across databases.  You can use this "
    		+ "browser to view terms, definitions, and term relationships in a hierarchical display. Links "
   			+ "to summary annotated gene data at MGI are provided in Term Detail reports.");
    	mav.addObject("title", getGOTitle((BrowserTerm) mav.getModel().get("term")));
    	fillGOUrls(mav);
    	return mav;
    }
    
    /* GO term detail pane
     */
    @RequestMapping("/gene_ontology/termPane/{id}")
    public ModelAndView getGOTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getGOTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, GO_VOCAB);
    	mav.addObject("title", getGOTitle((BrowserTerm) mav.getModel().get("term")));
    	fillGOUrls(mav);
    	return mav;
    }
    
    /* GO search pane
     */
    @RequestMapping("/gene_ontology/search")
    public ModelAndView getGOSearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, GO_VOCAB);
    	fillGOUrls(mav);
    	return mav;
    }

    /* GO browser - initial load of terms for tree view, for the term with the given ID.  Rules:
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children
     * 4. repeat #3 all the way up to the root node
     */
    @RequestMapping("/gene_ontology/treeInitial")
    public @ResponseBody String getGOTreeInitial(@RequestParam("id") String id) {
    	return this.getSharedBrowserTreeInitial(id, GO_VOCAB);
    }

    /* GO browser - load children of the term with the given ID
     */
    @RequestMapping("/gene_ontology/treeChildren")
    public @ResponseBody String getGOTreeChildren(@RequestParam("id") String id,
    		@RequestParam("nodeID") String nodeID, @RequestParam("edgeType") String edgeType) {
    	return this.getSharedBrowserTreeChildren(id, nodeID, edgeType, GO_VOCAB);
    }
    
    /*--- HPO browser ----------------------------------------------------------------*/

    /* Human Phenotype Ontology (HPO) browser home page
     */
    @RequestMapping("/hp_ontology")
    public ModelAndView getHPDetail() {
    	logger.debug("->getHPDetail() started");

    	// start with the phenotypic abnormality node as a default
    	return getHPDetail("HP:0000118");
    }
    
    /* fill in the standard URLs for the HPO browser
     */
    private ModelAndView fillHPUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/hp_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	mav.addObject("treeInitialUrl", baseUrl + "treeInitial");
    	mav.addObject("treeChildrenUrl", baseUrl + "treeChildren");
    	mav.addObject("autocompleteUrl", ContextLoader.getConfigBean().getProperty("FEWI_URL") + "autocomplete/hp_ontology?query=");
    	mav.addObject("message", "The Human Phenotype Ontology is developed by "
    		+ "<a href='http://human-phenotype-ontology.github.io/' target='_blank'>Peter Robinson's</a> group. Additional "
    		+ "terms or revisions may be requested through their "
    		+ "<a href='https://github.com/obophenotype/human-phenotype-ontology/issues' target='_blank'>issue tracker</a> at GitHub.");
    	return mav;
    }
    
    /* get the browser title string for a term in the HPO browser
     */
    private String getHPTitle(BrowserTerm term) {
    	if (term == null) { return "Human Phenotype Ontology Browser"; }
    	return term.getTerm() + " Human Phenotype Ontology Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* HPO browser for a specified HP ID */

    @RequestMapping("/hp_ontology/{id}")
    public ModelAndView getHPDetail(@PathVariable("id") String id) {
    	logger.debug("->getHPDetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, HPO_VOCAB);
    	mav.addObject("pageTitle", "Human Phenotype Ontology Browser");
    	mav.addObject("searchPaneTitle", "HPO Search");
    	mav.addObject("termPaneTitle", "HPO Term Detail");
    	mav.addObject("treePaneTitle", "HPO Tree View");
    	mav.addObject("helpDoc", "VOCAB_hpo_browser_help.shtml");
    	mav.addObject("branding", "HPO");
    	mav.addObject("seoDescription", "The Human Phenotype Ontology (HPO) aims to provide a standardized "
    		+ "vocabulary of phenotypic abnormalities encountered in human disease."
   			+ "You can use this browser to view terms, definitions, and term relationships in a hierarchical "
   			+ "display. Links to diseases with gene annotations at MGI are provided in Term Detail reports.");
    	mav.addObject("title", getHPTitle((BrowserTerm) mav.getModel().get("term")));
    	fillHPUrls(mav);
    	return mav;
    }
    
    /* HPO term detail pane
     */
    @RequestMapping("/hp_ontology/termPane/{id}")
    public ModelAndView getHPTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getHPTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, HPO_VOCAB);
    	mav.addObject("title", getHPTitle((BrowserTerm) mav.getModel().get("term")));
    	fillHPUrls(mav);
    	return mav;
    }
    
    /* HPO search pane
     */
    @RequestMapping("/hp_ontology/search")
    public ModelAndView getHPSearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, HPO_VOCAB);
    	fillHPUrls(mav);
    	return mav;
    }

    /* HPO browser - initial load of terms for tree view, for the term with the given ID.  Rules:
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children
     * 4. repeat #3 all the way up to the root node
     */
    @RequestMapping("/hp_ontology/treeInitial")
    public @ResponseBody String getHPTreeInitial(@RequestParam("id") String id) {
    	return this.getSharedBrowserTreeInitial(id, HPO_VOCAB);
    }

    /* HPO browser - load children of the term with the given ID
     */
    @RequestMapping("/hp_ontology/treeChildren")
    public @ResponseBody String getHPTreeChildren(@RequestParam("id") String id,
    		@RequestParam("nodeID") String nodeID, @RequestParam("edgeType") String edgeType) {
    	return this.getSharedBrowserTreeChildren(id, nodeID, edgeType, HPO_VOCAB);
    }
    
    /*--- DO browser ----------------------------------------------------------------*/

    /* Disease Ontology (DO) browser home page
     */
    @RequestMapping("/mmxvii_disease_ontology")
    public ModelAndView getDODetail() {
    	logger.debug("->getDODetail() started");

    	// start with the disease node as a default
    	return getDODetail("DOID:4");
    }
    
    /* fill in the standard URLs for the DO browser
     */
    private ModelAndView fillDOUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/mmxvii_disease_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	mav.addObject("treeInitialUrl", baseUrl + "treeInitial");
    	mav.addObject("treeChildrenUrl", baseUrl + "treeChildren");
    	mav.addObject("autocompleteUrl", ContextLoader.getConfigBean().getProperty("FEWI_URL") + "autocomplete/disease_ontology?query=");
    	return mav;
    }
    
    /* get the browser title string for a term in the DO browser
     */
    private String getDOTitle(BrowserTerm term) {
    	if (term == null) { return "Disease Ontology Browser"; }
    	return term.getTerm() + " Disease Ontology Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* DO browser for a specified DO ID */

    @RequestMapping("/mmxvii_disease_ontology/{id}")
    public ModelAndView getDODetail(@PathVariable("id") String id) {
    	logger.debug("->getDODetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, DO_VOCAB);
    	mav.addObject("pageTitle", "Disease Ontology Browser");
    	mav.addObject("searchPaneTitle", "DO Search");
    	mav.addObject("termPaneTitle", "DO Term Detail");
    	mav.addObject("treePaneTitle", "DO Tree View");
    	mav.addObject("helpDoc", "VOCAB_do_browser_help.shtml");
    	mav.addObject("branding", "DO");
    	mav.addObject("seoDescription", "The Disease Ontology (DO) aims to provide a standardized "
    		+ "vocabulary of human diseases."
   			+ "You can use this browser to view terms, definitions, and term relationships in a hierarchical "
   			+ "display. Links to diseases with gene annotations at MGI are provided in Term Detail reports.");
    	mav.addObject("title", getDOTitle((BrowserTerm) mav.getModel().get("term")));
    	fillDOUrls(mav);
    	return mav;
    }
    
    /* DO term detail pane
     */
    @RequestMapping("/mmxvii_disease_ontology/termPane/{id}")
    public ModelAndView getDOTermPane(HttpServletRequest request, @PathVariable("id") String id) {
		if (!UserMonitor.getSharedInstance().isOkay(request.getRemoteAddr())) {
			return UserMonitor.getSharedInstance().getLimitedMessage();
		}

    	logger.debug("->getDOTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, DO_VOCAB);
    	mav.addObject("title", getDOTitle((BrowserTerm) mav.getModel().get("term")));
    	fillDOUrls(mav);
    	return mav;
    }
    
    /* DO search pane
     */
    @RequestMapping("/mmxvii_disease_ontology/search")
    public ModelAndView getDOSearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, DO_VOCAB);
    	fillDOUrls(mav);
    	return mav;
    }

    /* DO browser - initial load of terms for tree view, for the term with the given ID.  Rules:
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children
     * 4. repeat #3 all the way up to the root node
     */
    @RequestMapping("/mmxvii_disease_ontology/treeInitial")
    public @ResponseBody String getDOTreeInitial(@RequestParam("id") String id) {
    	return this.getSharedBrowserTreeInitial(id, DO_VOCAB);
    }

    /* DO browser - load children of the term with the given ID
     */
    @RequestMapping("/mmxvii_disease_ontology/treeChildren")
    public @ResponseBody String getDOTreeChildren(@RequestParam("id") String id,
    		@RequestParam("nodeID") String nodeID, @RequestParam("edgeType") String edgeType) {
    	return this.getSharedBrowserTreeChildren(id, nodeID, edgeType, DO_VOCAB);
    }
    
    /*--- shared vocab browser -------------------------------------------------------*/

    /* get shared browser's search pane
     */
    public ModelAndView getSharedBrowserSearchPane(String term, String vocabName) {
    	if ((term == null) || term.equals("")) {
    		logger.debug("->getSharedBrowserSearchPane(null)");
    		ModelAndView mav = new ModelAndView("vocabBrowser/search");
    		return mav;
    	}

    	logger.debug("->getSharedBrowserSearchPane(" + term + ", " + vocabName + ") started");

    	// biggest vocab is GO with 47k terms, so this is an unlimited search
    	int maxToReturn = 50000;

    	List<VocabBrowserSearchResult> sortedResults = getSharedBrowserSearchResults(term, vocabName, maxToReturn);

    	ModelAndView mav = new ModelAndView("vocabBrowser/search");
    	mav.addObject("searchTerm", term);

    	int numReturned = sortedResults.size();
    	if (numReturned > maxToReturn) {
    		mav.addObject("resultCount", maxToReturn + "+");
    		mav.addObject("results", sortedResults.subList(0, maxToReturn));
    	} else {
    		mav.addObject("resultCount", numReturned);
    		mav.addObject("results", sortedResults);
    	}
    	return mav;
    }

    /* get the set of vocabulary terms that match the given search string for the given vocab
     */
    protected List<VocabBrowserSearchResult> getSharedBrowserSearchResults(String term, String vocabName,
    		int maxToReturn) {

   		ArrayList<String> tokens = new ArrayList<String>();		// search tokens
   		
    	SearchParams sp = new SearchParams();
    	sp.addSort(new Sort(SortConstants.SCORE, true));		// sort by descending score
    	sp.setPageSize(maxToReturn + 1);						// use 1 more, so know to display like "50+"

    	boolean searchedByID = false;		// is the user searching by an ID?
    	
    	// short-circuit for searches by ID
    	if (Pattern.matches("[A-Za-z]+:[A-Za-z0-9]+", term.trim())) {
    		ArrayList<Filter> idFilters = new ArrayList<Filter>();
    		ArrayList<Filter> subFilters = new ArrayList<Filter>();
    		idFilters.add(new Filter(SearchConstants.VB_VOCAB_NAME, vocabName));
    		subFilters.add(new Filter(SearchConstants.VB_ACC_ID, term.trim()));
    		subFilters.add(new Filter(SearchConstants.VB_CROSSREF, term.trim()));
    		idFilters.add(Filter.or(subFilters));
    		sp.setFilter(Filter.and(idFilters));
    		searchedByID = true;

    	} else {
    		// not an ID search, so we do a little more work on cleansing term and synonym searches
    	
    		// clean up the term a little by:
    		// 1. converting hyphens to spaces
    		// 2. stripping non-alphanumerics and non-whitespace

    		String cleanTerm = term.replaceAll("-", " ");
    		cleanTerm = cleanTerm.replaceAll(",", " ");
    		cleanTerm = cleanTerm.replaceAll("/", " ");
    		cleanTerm = cleanTerm.replaceAll("[^A-Za-z0-9\\s]", "");
    		cleanTerm = cleanTerm.replaceAll("\\s\\s+", " ");

    		// need to AND the requested tokens together, then OR the searches of terms and synonyms

    		ArrayList<Filter> termFilters = new ArrayList<Filter>();
    		ArrayList<Filter> synonymFilters = new ArrayList<Filter>();

    		termFilters.add(new Filter(SearchConstants.VB_VOCAB_NAME, vocabName));
    		synonymFilters.add(new Filter(SearchConstants.VB_VOCAB_NAME, vocabName));
    	
    		for (String token : cleanTerm.split("\\s")) {
    			/* odd hack...  Solr is not recognizing certain short words when ending with a wildcard; these
    			 * must be matched by exact match.  So, adjust the operator as needed...
    			 */
    			if (noWildcards.contains(token)) {
    				termFilters.add(new Filter(SearchConstants.VB_TERM, token, Filter.Operator.OP_EQUAL));
    				synonymFilters.add(new Filter(SearchConstants.VB_SYNONYM, token, Filter.Operator.OP_EQUAL));
    			} else {
    				termFilters.add(new Filter(SearchConstants.VB_TERM, token, Filter.Operator.OP_BEGINS));
    				synonymFilters.add(new Filter(SearchConstants.VB_SYNONYM, token, Filter.Operator.OP_BEGINS));
    			}
    			tokens.add(token);
    		}

    		ArrayList<Filter> eitherFilters = new ArrayList<Filter>();

    		eitherFilters.add(Filter.and(termFilters));
    		eitherFilters.add(Filter.and(synonymFilters));

    		sp.setFilter(Filter.or(eitherFilters));
    	}


    	SearchResults<BrowserTerm> sr = vocabFinder.getBrowserTerms(sp);
    	List<BrowserTerm> resultObjects = sr.getResultObjects();
    	
    	if (searchedByID) {
    		Collections.sort(resultObjects, new BrowserTermComparator());
    	}
    	
    	// need to wrap each resulting BrowserTerm in a VocabBrowserSearchResults, then pass it the
    	// list of search tokens (needed for highlighting)

    	List<VocabBrowserSearchResult> results = new ArrayList<VocabBrowserSearchResult>();
    	for (BrowserTerm matchedTerm : resultObjects) {
    		VocabBrowserSearchResult result = new VocabBrowserSearchResult(matchedTerm);
    		result.setTokens(tokens);
    		results.add(result);
    	}

    	List<VocabBrowserSearchResult> sortedResults = prioritizeMatches(results, tokens);
    	return sortedResults;
    }
    
    /* prioritize our matches according to several criteria:
     * 1. exact matches to term
     * 2. exact matches to synonym
     * 3. begins matches to term
     * 4. begins matches to synonym
     * 5. other
     */
    private List<VocabBrowserSearchResult> prioritizeMatches (List<VocabBrowserSearchResult> matches,
    		List<String> tokens) {

    	ArrayList<VocabBrowserSearchResult> termExact = new ArrayList<VocabBrowserSearchResult>();
    	ArrayList<VocabBrowserSearchResult> synonymExact = new ArrayList<VocabBrowserSearchResult>();
    	ArrayList<VocabBrowserSearchResult> termBegins = new ArrayList<VocabBrowserSearchResult>();
    	ArrayList<VocabBrowserSearchResult> synonymBegins = new ArrayList<VocabBrowserSearchResult>();
    	ArrayList<VocabBrowserSearchResult> other = new ArrayList<VocabBrowserSearchResult>();

    	String searchString = "";
    	boolean isFirst = true;

    	for (String s : tokens) {
    		if (isFirst) {
    			searchString = s;
    			isFirst = false;
    		} else {
    			searchString = searchString + " " + s;
    		}
    	}
    	searchString = searchString.toLowerCase();

    	for (VocabBrowserSearchResult match : matches) {
    		String term = match.getTerm().toLowerCase();
    		List<String> synonyms = match.getSynonyms();

    		// order of preference:
    		// 1. exact match to term
    		// 2. exact match to a synonym
    		// 3. begins match to a term
    		// 4. begins match to a synonym
    		// 5. everything else

    		boolean done = false;
    		boolean exactSynonym = false;
    		boolean beginsSynonym = false;

    		term = term.replaceAll("-",  " ").replaceAll("/",  " ").replaceAll("'", "");
    		if (term.equals(searchString)) {
    			termExact.add(match);
    			done = true;
    		} else if (synonyms != null) {
    			for (String synonym : synonyms) {
    				String synLower = synonym.toLowerCase();
    				synLower = synLower.replaceAll("-",  " ").replaceAll("/",  " ").replaceAll("'", "");

    				if (synLower.equals(searchString)) {
    					exactSynonym = true;
    					break;
    				} else if (synLower.startsWith(searchString)) {
    					beginsSynonym = true;
    				}
    			}
    		}

    		if (!done && exactSynonym) {
    			synonymExact.add(match);
    			done = true;
    		}

    		if (!done && term.startsWith(searchString)) {
    			termBegins.add(match);
    			done = true;
    		}

    		if (!done && beginsSynonym) {
    			synonymBegins.add(match);
    			done = true;
    		}

    		if (!done) {
    			other.add(match);
    		}
    	}
    	
    	termExact.addAll(synonymExact);
    	termExact.addAll(termBegins);
    	termExact.addAll(synonymBegins);
    	termExact.addAll(other);

    	return termExact;
    }

    /* shared method for building the shared vocab browser detail page
     */
    private ModelAndView getSharedBrowserDetail(String id, String vocab) {
    	BrowserTerm term = getBrowserTerm(id, vocab);
    	if (term == null) {
    		return errorMav("No term found for " + id); 
    	}

    	ModelAndView mav = new ModelAndView("vocabBrowser/frames");
    	mav.addObject("termID", id);
    	mav.addObject("term", term);
    	mav.addObject("vocab", vocab);
    	return mav;
    }

    /* term detail pane for shared vocabulary browser
     */
    private ModelAndView getSharedBrowserTermPane(String id, String vocab) {
    	BrowserTerm term = getBrowserTerm(id, vocab);
    	if (term == null) {
    		return errorMav("No term found for " + id); 
    	}

    	ModelAndView mav = new ModelAndView("vocabBrowser/term");
    	mav.addObject("term", term);
    	mav.addObject("title", term.getTerm());
    	mav.addObject("vocab", term);
    	return mav;
    }

    /* shared vocabulary browser - initial load of terms for tree view, for the term with the
     * given ID in the given vocabulary.  Rules:
     * 1. retrieve specified node
     * 2. retrieve its children
     * 3. retrieve its default parent and its children (siblings of specified node)
     * 4. repeat #3 all the way up to the root node
     */
    private String getSharedBrowserTreeInitial(String id, String vocab) {
    	BrowserTerm term = getBrowserTerm(id, vocab);
    	if (term == null) { return "[]"; }
    	
    	BrowserParent parent = term.getDefaultParent();
    	String edgeType = null;
    	if (parent != null) {
    		edgeType = parent.getEdgeType();
    	}
    	
    	// list of nodes with initial term first and its ancestors (up the default path)
    	// from left to right
    	List<JSTreeNode> nodes = new ArrayList<JSTreeNode>();
    	nodes.add(new JSTreeNode(term, edgeType, true, true));	// opened and selected
    	
    	while ((term != null) && (parent != null)) {
    		term = getBrowserTerm(parent.getPrimaryID(), vocab);
    		if (term != null) {
    			parent = term.getDefaultParent();
    			edgeType = null;
    			if (parent != null) {
    				edgeType = parent.getEdgeType();
    			}
    			nodes.add(new JSTreeNode(term, edgeType, true, false));		// opened, not selected
    			
    			// and note the opened child node at each level of ancestry
    			nodes.get(nodes.size() - 1).setChosenChild(nodes.get(nodes.size() - 2));
    		}
    	}
    	
    	return "[" + nodes.get(nodes.size() - 1).toStringWithChildren() + "]";
    }

    /* shared vocabulary browser - load children of the term with the given ID in the given vocabulary
     */
    private String getSharedBrowserTreeChildren(String id, String nodeID, String edgeType, String vocab) {
   		StringBuffer sb = new StringBuffer();
   		sb.append("[");

    	BrowserTerm term = getBrowserTerm(id, vocab);
    	if (term != null) {
    		sb.append((new JSTreeNode(term, edgeType, true, true)).setNodeID(nodeID).toStringWithChildren());
    	}
   		sb.append("]");
    	return sb.toString();
    }
    
    /* get the first term for the given id in the given vocabulary.  Return null if there are none.
     */
    private BrowserTerm getBrowserTerm(String id, String vocab) {

    	List<BrowserTerm> terms = vocabFinder.getBrowserTerm(id, vocab);
    	if (terms.size() < 1) {
    		terms = vocabFinder.getBrowserTerm(id.toUpperCase(), vocab);
    	}

    	if (terms.size() >= 1) {

            BrowserTerm selectedTerm = terms.get(0); // the selected browser term
            
            // Special handling for Cell Type Ontology.  Normally these counts are added in the
            // indexer layer (which was easier for other vocabs).  Cell Ontology result counts
            // are easiest to gather via controller-to-controller request.
            if (id.startsWith("CL:")) { // if it's a cell type ontology term

				String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");            

                // gather all needed counts for this term
                Integer resultCount = gxdController.getResultCountForCoID(id);
                Integer htCount     = gxdHtController.getExperimentCountForCoID(id); 
                Integer recomCount  = recombinaseController.getAlleleCountByCellType(id); 
                
                logger.info("--- " + selectedTerm.getTerm() + " resultCount & htCount & recomCount: " 
                	+ resultCount + " " + htCount + " " + recomCount);

               	selectedTerm.setHasAnnotations("");
                if (resultCount > 0 || htCount > 0 || recomCount >0) {

                	selectedTerm.setHasAnnotations("true");

                    // set label here; other browsers have this set upstream in indexer
                    StringBuilder termAnnotLabel = new StringBuilder("");
                    if (resultCount > 0) {
                    	termAnnotLabel.append("<a id='resultLink_" + selectedTerm.getPrimaryID().getAccID().replace(":", "") 
                    	+ "' href='" + baseUrl + "gxd/celltype/" + selectedTerm.getPrimaryID().getAccID() +"' target='_blank'>"
                    	+ resultCount.toString() + "</a> expression results" );
                    }
                    if (htCount > 0) {
                    	if (termAnnotLabel.length() != 0 ) {termAnnotLabel.append("; "); }
                    	termAnnotLabel.append("<a id='htLink_" + selectedTerm.getPrimaryID().getAccID().replace(":", "") 
                    	+ "' href='" + baseUrl + "gxdhtexp_index/summary?cellType=" + selectedTerm.getTerm() +"' target='_blank'>"
                    	+ htCount.toString() + "</a> RNA-seq or microarray experiments");
                    }
                    if (recomCount > 0) {
                    	if (termAnnotLabel.length() != 0 ) {termAnnotLabel.append("; "); }
                    	termAnnotLabel.append("<a id='recomLink_" + selectedTerm.getPrimaryID().getAccID().replace(":", "") 
                    	+ "' href='" + baseUrl + "recombinase/summary?cellTypeID=" + selectedTerm.getPrimaryID().getAccID() 
                    	+ "&cellType=" + selectedTerm.getTerm() + "' target='_blank'>"
                    	+ recomCount.toString() + "</a> recombinase alleles");
                    }
                    selectedTerm.setAnnotationLabel( termAnnotLabel.toString().trim() );

                   
                    // set count labels for all the children of this term
                    if (selectedTerm.getChildren() != null) {
                      for (BrowserChild child : selectedTerm.getChildren() ) {

                        Integer childResultCount = gxdController.getResultCountForCoID(child.getPrimaryID());
                        Integer childHtCount     = gxdHtController.getExperimentCountForCoID(child.getPrimaryID()); 
                        Integer childRecomCount   = recombinaseController.getAlleleCountByCellType(child.getPrimaryID()); 
                        
                        logger.info("--- " + child.getTerm() + " childResultCount & childHtCount & childRecomCount: " 
                        	+ childResultCount + " " + childHtCount + " " + childRecomCount);
                        
                        child.setHasAnnotations("");
                        if (childResultCount > 0 || childHtCount > 0 || childRecomCount > 0) {

                            child.setHasAnnotations("true"); 

                            // set label here; other browsers have this set upstream in indexer
                            StringBuilder childAnnotLabel = new StringBuilder("");
                            if (childResultCount > 0) {
                            	childAnnotLabel.append("<a id='resultLink_" + child.getPrimaryID().replace(":", "")
                            	+ "' href='" + baseUrl + "gxd/celltype/" + child.getPrimaryID() +"' target='_blank'>"
                            	+ childResultCount.toString() + "</a> expression results");
                            }
                            if (childHtCount > 0) {
                            	if (childAnnotLabel.length() != 0 ) {childAnnotLabel.append("; "); }
                    	        childAnnotLabel.append("<a id='htLink_" + child.getPrimaryID().replace(":", "")
                    	        + "' href='" + baseUrl + "gxdhtexp_index/summary?cellType=" + child.getTerm() +"' target='_blank'>"
                    	        + childHtCount.toString() + "</a> RNA-seq or microarray experiments");
                            }
                            if (childRecomCount > 0) {
                            	if (childAnnotLabel.length() != 0 ) {childAnnotLabel.append("; "); }
                    	        childAnnotLabel.append("<a id='recomLink_" + child.getPrimaryID().replace(":", "")
                    	        + "' href='" + baseUrl + "recombinase/summary?cellTypeID=" + child.getPrimaryID() 
                    	        + "&cellType=" + child.getTerm() + "' target='_blank'>"
                    	        + childRecomCount.toString() + "</a> recombinase alleles");
                            }
                            child.setAnnotationLabel( childAnnotLabel.toString() );
                        }
                      }
                    }
                }
            }
    		return selectedTerm;
    	}
    	return null;
    }
    


    /***--- special comparators for smart-alpha sorting vocab browser results (shared and EMAPA-specific) ---***/
    
    private class BrowserTermComparator extends SmartAlphaComparator<BrowserTerm> {
    	public int compare(BrowserTerm t1, BrowserTerm t2) {
    		// smart-alpha sort of BrowserTerm objects by term (fall back on ID sorting, if terms match)
    		
    		int i = super.compare(t1.getTerm(), t2.getTerm());
    		if (i == 0) {
    			i = super.compare(t1.getPrimaryID().getAccID(), t2.getPrimaryID().getAccID());
    		}
    		return i;
    	}
    }

    private class SolrAnatomyTermComparator extends SmartAlphaComparator<SolrAnatomyTerm> {
    	public int compare(SolrAnatomyTerm t1, SolrAnatomyTerm t2) {
    		// smart-alpha sort of SolrAnatomyTerm objects by term (fall back on stage range, then ID sorting)
    		
    		int i = super.compare(t1.getStructure(), t2.getStructure());
    		if (i == 0) {
    			i = super.compare(t1.getStageRange(), t2.getStageRange());
    		}
    		if (i == 0) {
    			i = super.compare(t1.getAccID(), t2.getAccID());
    		}
    		return i;
    	}
    }
}
