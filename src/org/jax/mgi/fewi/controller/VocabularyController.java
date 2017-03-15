package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mgi.frontend.datamodel.VocabTerm;
import mgi.frontend.datamodel.sort.VocabTermComparator;

import org.hibernate.SessionFactory;
import org.jax.mgi.fewi.config.ContextLoader;
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
import org.jax.mgi.fewi.util.TreeNode;
import org.jax.mgi.fewi.util.link.IDLinker;
import org.jax.mgi.shr.jsonmodel.BrowserTerm;
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

    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger = LoggerFactory.getLogger(VocabularyController.class);

    @Autowired
    private VocabularyFinder vocabFinder;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private IDLinker idLinker;
    
    
    /* OMIM vocabulary browser */

    @RequestMapping("/omim")
    public String getOmimBrowserIndex() 
    {
    	logger.debug("Forwarding to /vocab/omim/A");
    	return "forward:/mgi/vocab/omim/A";
    }

    @RequestMapping("/omim/{subsetLetter}")
    public ModelAndView getOmimBrowser(@PathVariable("subsetLetter") String subsetLetter) 
    {
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
    public ModelAndView getPirsfDetail(@PathVariable("id") String id) {
	logger.debug("->getPirsfDetail(" + id + ") started");

        // enable filter that will only return protein IDs for markers
        sessionFactory.getCurrentSession().enableFilter("onlyProteinSequences");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if (terms.size() < 1) {
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

    /* support new GXD anatomy browser by dynamically looking up children for
     * a given node
     */
    @RequestMapping("/gxd/anatomyChildren/{id}")
    public @ResponseBody String getAnatomyChildren(
	@PathVariable("id") String id) {

	logger.debug("->getAnatomyChildren(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if (terms.size() < 1) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if (terms.size() != 1) { return "[]"; }

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
	cleanTerm = cleanTerm.replaceAll("[^A-Za-z0-9\\s]", "");
	cleanTerm = cleanTerm.replaceAll("\\s\\s+", " ");

	SearchParams sp = new SearchParams();

	// need to AND the requested tokens together, then OR the searches of
	// structure and synonyms

	ArrayList<Filter> structureFilters = new ArrayList<Filter>();
	ArrayList<Filter> synonymFilters = new ArrayList<Filter>();
	ArrayList<String> tokens = new ArrayList<String>();

	for (String token : cleanTerm.split("\\s")) {
	    structureFilters.add(new Filter(SearchConstants.STRUCTURE, token));
	    synonymFilters.add(new Filter(SearchConstants.SYNONYM, token));
	    tokens.add(token);
	}

	ArrayList<Filter> eitherFilters = new ArrayList<Filter>();

	eitherFilters.add(Filter.and(structureFilters));
	eitherFilters.add(Filter.and(synonymFilters));

	sp.setFilter(Filter.or(eitherFilters));

	// We need to get 501 results, so we know if the count should display
	// as 500 or as 500+
	int maxToReturn = 500;
	sp.setPageSize(maxToReturn + 1);

	SearchResults<SolrAnatomyTerm> sr = vocabFinder.getAnatomyTerms(sp);

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

	if (terms.size() < 1) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if (terms.size() < 1) { return "[]"; }

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

	if (terms.size() < 1) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if (terms.size() < 1) { return "{}"; }

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
    public ModelAndView getAnatomyTermPane(@PathVariable("id") String id) {
	logger.debug("->getAnatomyTermPane(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if (terms.size() < 1) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if (terms.size() < 1) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_term_pane");

	mav.addObject("term", term);
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

	return mav;
    }

    /* new GXD Anatomy browser for EMAPA and EMAPS terms */

    @RequestMapping("/gxd/anatomy")
    public ModelAndView getAnatomyDetail() {
	logger.debug("->getAnatomyDetail() started");

	// start with 'embryo' as a default
	List<VocabTerm> terms = vocabFinder.getTermByID("EMAPA:16039");

	if (terms.size() < 1) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_detail");
	mav.addObject("term", term);

	return mav;
    }

    /* new GXD Anatomy browser for EMAPA and EMAPS terms (given an ID) */

    @RequestMapping("/gxd/anatomy/{id}")
    public ModelAndView getAnatomyDetail(@PathVariable("id") String id) {
	logger.debug("->getAnatomyDetail(" + id + ") started");

	List<VocabTerm> terms = vocabFinder.getTermByID(id);

	if (terms.size() < 1) {
	    terms = vocabFinder.getTermByID(id.toUpperCase());
	}

	if (terms.size() < 1) { return errorMav("No Anatomy term found"); }
	else if (terms.size() > 1) { return errorMav("Duplicate ID"); }

	VocabTerm term = terms.get(0);

	ModelAndView mav = new ModelAndView("anatomy_detail");
	mav.addObject("term", term);

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

    /*--- AMA browser ----------------------------------------------------------------*/

    /* [Adult] Mouse Anatomy browser home page
     */
    @RequestMapping("/gxd/ma_ontology")
    public ModelAndView getMouseAnatomyDetail() {
    	logger.debug("->getMouseAnatomyDetail() started");

    	// start with 'postnatal mouse' as a default
    	return getMouseAnatomyDetail("MA:0002405");
    }
    
    /* fill in the standard URLs for the Adult Mouse Anatomy browser
     */
    private ModelAndView fillMouseAnatomyUrls(ModelAndView mav) {
    	String baseUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL") + "vocab/gxd/ma_ontology/";
    	mav.addObject("browserUrl", baseUrl);
    	mav.addObject("termPaneUrl", baseUrl + "termPane/");
    	mav.addObject("searchPaneUrl", baseUrl + "/search?term=");
    	return mav;
    }
    
    /* get the browser title string for a term in the Adult Mouse Anatomy
     */
    private String getMouseAnatomyTitle(BrowserTerm term) {
    	if (term == null) { return "Adult Mouse Anatomy Browser"; }
    	return term.getTerm() + " Adult Mouse Anatomy Term (" + term.getPrimaryID().getAccID() + ")";
    }

    /* [Adult] Mouse Anatomy browser for a specified MA ID */

    @RequestMapping("/gxd/ma_ontology/{id}")
    public ModelAndView getMouseAnatomyDetail(@PathVariable("id") String id) {
    	logger.debug("->getMouseAnatomyDetail(" + id + ") started");
    	ModelAndView mav = getSharedBrowserDetail(id, MA_VOCAB);
    	mav.addObject("pageTitle", "Adult Mouse Anatomy Browser");
    	mav.addObject("searchPaneTitle", "Anatomy Search");
    	mav.addObject("termPaneTitle", "Adult Anatomy Term Detail");
    	mav.addObject("treePaneTitle", "Anatomy Tree View");
    	mav.addObject("helpDoc", "VOCAB_amad_browser_help.shtml#td_page");
    	mav.addObject("branding", "GXD");
    	mav.addObject("seoDescription", "The Adult Mouse Anatomy Ontology organizes anatomical structures "
    		+ "for the postnatal mouse (Theiler stage 28) spatially and functionally, using 'is a' and "
    		+ "'part of' relationships.  This browser can be used to view anatomical terms and their "
    		+ "relationships in a hierarchical display.");
    	mav.addObject("title", getMouseAnatomyTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMouseAnatomyUrls(mav);
    	return mav;
    }
    
    /* [Adult] Mouse Anatomy term detail pane
     */
    @RequestMapping("/gxd/ma_ontology/termPane/{id}")
    public ModelAndView getMouseAnatomyTermPane(@PathVariable("id") String id) {
    	logger.debug("->getMouseAnatomyTermPane(" + id + ") started");
    	ModelAndView mav = getSharedBrowserTermPane(id, MA_VOCAB);
    	mav.addObject("title", getMouseAnatomyTitle((BrowserTerm) mav.getModel().get("term")));
    	fillMouseAnatomyUrls(mav);
    	return mav;
    }
    
    /* [Adult] Mouse Anatomy search pane
     */
    @RequestMapping("/gxd/ma_ontology/search")
    public ModelAndView getMouseAnatomySearchPane(@RequestParam("term") String term) {
    	ModelAndView mav = getSharedBrowserSearchPane(term, MA_VOCAB);
    	fillMouseAnatomyUrls(mav);
    	return mav;
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

    	// We need to get 51 results, so we know if the count should display
    	// as 50 or as 50+
    	int maxToReturn = 50;

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

    	// clean up the term a little by:
    	// 1. converting hyphens to spaces
    	// 2. stripping non-alphanumerics and non-whitespace

    	String cleanTerm = term.replaceAll("-", " ");
    	cleanTerm = cleanTerm.replaceAll(",", " ");
    	cleanTerm = cleanTerm.replaceAll("/", " ");
    	cleanTerm = cleanTerm.replaceAll("[^A-Za-z0-9\\s]", "");
    	cleanTerm = cleanTerm.replaceAll("\\s\\s+", " ");

    	SearchParams sp = new SearchParams();

    	// need to AND the requested tokens together, then OR the searches of terms and synonyms

    	ArrayList<Filter> termFilters = new ArrayList<Filter>();
    	ArrayList<Filter> synonymFilters = new ArrayList<Filter>();
    	ArrayList<String> tokens = new ArrayList<String>();

    	termFilters.add(new Filter(SearchConstants.VB_VOCAB_NAME, vocabName));
    	synonymFilters.add(new Filter(SearchConstants.VB_VOCAB_NAME, vocabName));
    	
    	for (String token : cleanTerm.split("\\s")) {
    		termFilters.add(new Filter(SearchConstants.VB_TERM, token));
    		synonymFilters.add(new Filter(SearchConstants.VB_SYNONYM, token));
    		tokens.add(token);
    	}

    	ArrayList<Filter> eitherFilters = new ArrayList<Filter>();

    	eitherFilters.add(Filter.and(termFilters));
    	eitherFilters.add(Filter.and(synonymFilters));

    	sp.setFilter(Filter.or(eitherFilters));
    	sp.addSort(new Sort(SortConstants.VB_SEQUENCE_NUM));
    	sp.setPageSize(maxToReturn + 1);

    	SearchResults<BrowserTerm> sr = vocabFinder.getBrowserTerms(sp);

    	// need to wrap each resulting BrowserTerm in a VocabBrowserSearchResults, then pass it the
    	// list of search tokens (needed for highlighting)

    	List<VocabBrowserSearchResult> results = new ArrayList<VocabBrowserSearchResult>();
    	for (BrowserTerm matchedTerm : sr.getResultObjects()) {
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
    	logger.info("searchString: " + searchString);

    	for (VocabBrowserSearchResult match : matches) {
    		String term = match.getTerm().toLowerCase();
    		List<String> synonyms = match.getSynonyms();

    		logger.info("term: " + term);
    		
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
    	List<BrowserTerm> terms = vocabFinder.getBrowserTerm(id, vocab);

    	if (terms.size() < 1) {
    		terms = vocabFinder.getBrowserTerm(id.toUpperCase(), vocab);
    	}

    	if (terms.size() < 1) { return errorMav("No term found for " + id); }
    	else if (terms.size() > 1) { return errorMav("Duplicate ID; " + id + " has multiple terms."); }

    	BrowserTerm term = terms.get(0);

    	ModelAndView mav = new ModelAndView("vocabBrowser/frames");
    	mav.addObject("termID", id);
    	mav.addObject("term", term);
    	mav.addObject("vocab", vocab);
    	return mav;
    }

    /* term detail pane for shared vocabulary browser
     */
    private ModelAndView getSharedBrowserTermPane(String id, String vocab) {

    	List<BrowserTerm> terms = vocabFinder.getBrowserTerm(id, vocab);

    	if (terms.size() < 1) {
    		terms = vocabFinder.getBrowserTerm(id.toUpperCase(), vocab);
    	}

    	if (terms.size() < 1) { return errorMav("No term found for " + id); }
    	else if (terms.size() > 1) { return errorMav("Duplicate ID; " + id + " has multiple terms."); }

    	BrowserTerm term = terms.get(0);

    	ModelAndView mav = new ModelAndView("vocabBrowser/term");
    	mav.addObject("term", term);
    	mav.addObject("title", term.getTerm());
    	mav.addObject("vocab", term);
    	return mav;
    }
}
