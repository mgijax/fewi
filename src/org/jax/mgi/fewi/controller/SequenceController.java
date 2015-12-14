package org.jax.mgi.fewi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mgi.frontend.datamodel.Marker;
import mgi.frontend.datamodel.Probe;
import mgi.frontend.datamodel.Reference;
import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceID;
import mgi.frontend.datamodel.SequenceLocation;

import org.jax.mgi.fewi.finder.MarkerFinder;
import org.jax.mgi.fewi.finder.ReferenceFinder;
import org.jax.mgi.fewi.finder.SequenceFinder;
import org.jax.mgi.fewi.searchUtil.Filter;
import org.jax.mgi.fewi.searchUtil.Paginator;
import org.jax.mgi.fewi.searchUtil.SearchConstants;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.jax.mgi.fewi.searchUtil.Sort;
import org.jax.mgi.fewi.searchUtil.SortConstants;
import org.jax.mgi.fewi.summary.JsonSummaryResponse;
import org.jax.mgi.fewi.summary.SeqSummaryRow;
import org.jax.mgi.fewi.util.BlastableSequence;
import org.jax.mgi.fewi.util.StyleAlternator;
import org.jax.mgi.fewi.util.link.IDLinker;
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
 * This controller maps all /sequence/ uri's
 */
@Controller
@RequestMapping(value="/sequence")
public class SequenceController {


    //--------------------//
    // instance variables
    //--------------------//

    private final Logger logger
      = LoggerFactory.getLogger(SequenceController.class);

    @Autowired
    private IDLinker idLinker;
    
    @Autowired
    private SequenceFinder sequenceFinder;

    @Autowired
    private ReferenceFinder referenceFinder;

    @Autowired
    private MarkerFinder markerFinder;

    //--------------------//
    // public methods
    //--------------------//

    /*
     * Sequence Detail by ID
     */
    @RequestMapping(value="/{seqID:.+}", method = RequestMethod.GET)
    public ModelAndView seqDetailByID(@PathVariable("seqID") String seqID) {

        logger.debug("->seqDetail started");

        // setup search parameters object
        SearchParams searchParams = new SearchParams();
        Filter seqIdFilter = new Filter(SearchConstants.SEQ_ID, seqID);
        searchParams.setFilter(seqIdFilter);

        // find the requested sequence
        SearchResults<Sequence> searchResults
          = sequenceFinder.getSequenceByID(searchParams);
        List<Sequence> seqList = searchResults.getResultObjects();

        // handle error conditions
        if (seqList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return mav;
        }
        if (seqList.size() > 1) { // dupe found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Duplicate ID");
            return mav;
        }
        // success - we have a single object;
        
        return renderSequenceDetail(seqList.get(0));
    }
    

    /*
     * Sequence Detail by key
     */
    @RequestMapping(value="/key/{dbKey:.+}", method = RequestMethod.GET)
    public ModelAndView seqDetailByKey(@PathVariable("dbKey") String dbKey) {

        logger.debug("->seqDetailByKey started");

        // find the requested sequence
        SearchResults<Sequence> searchResults
          = sequenceFinder.getSequenceByKey(dbKey);
        List<Sequence> seqList = searchResults.getResultObjects();

        // handle error conditions
        if (seqList.size() < 1) { // none found
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No Sequence Found");
            return mav;
        }
        // success - we have a single object;

        return renderSequenceDetail(seqList.get(0));
    }

    /**
     * Render the sequence detail page
     */
    private ModelAndView renderSequenceDetail(Sequence sequence)
    {
    	// generate ModelAndView object to be passed to detail page
        ModelAndView mav = new ModelAndView("sequence_detail");

        // package detail page style alternators
        mav.addObject("leftTdStyles",
          new StyleAlternator("detailCat1","detailCat2"));
        mav.addObject("rightTdStyles",
          new StyleAlternator("detailData1","detailData2"));

        //pull out the sequence, and add to mav
        mav.addObject("sequence", sequence);

        // package annotated markers
        Set<Marker> markers = sequence.getMarkers();
        if (!markers.isEmpty()) {
            mav.addObject("markers", markers);
        }

        // package probes
        Set<Probe> probes = sequence.getProbes();
        if (!probes.isEmpty()) {
            mav.addObject("probes", probes);
        }

        // package referenes
        List<Reference> references = sequence.getReferences();
        if (!references.isEmpty()) {
            mav.addObject("references", references);
        }

        // package chromosome value
        List<SequenceLocation> locList = sequence.getLocations();
        if (!locList.isEmpty()) {
            mav.addObject("chromosome", locList.get(0).getChromosome());
        }

        // package other IDs for this sequence
        Set<SequenceID> ids = sequence.getIds();
        if (!ids.isEmpty() && ids.size() > 1) {

            List<SequenceID> otherIDs = new ArrayList<SequenceID>();
            Iterator<SequenceID> it = ids.iterator();

            // first is the primary ID;  skip it - we only want secondary IDs
            // --- this logic is false, there is no way to know which one will come first - kstone
            //it.next();
            // added correct logic below

            // make list of secondary IDs
            while (it.hasNext()) {
              SequenceID secondaryID = it.next();
              
              if(sequence.getPrimaryID() != null && sequence.getPrimaryID().equals(secondaryID.getAccID()))
              {
            	  // skip the primary ID.
            	  continue;
              }
              otherIDs.add(secondaryID);
            }

            // package other IDs
            mav.addObject("otherIDs", otherIDs);
        }

        // package source notificaiton
        if (sequence.hasRawValues()) {
            mav.addObject("sourceNotice", "* Value from GenBank/EMBL/DDBJ "
              + "could not be resolved to an MGI controlled vocabulary.");
        }

        mav.addObject("idLinker",idLinker);
        
        return mav;
    }


    /*
     * Sequence Summary by Reference
     */
    @RequestMapping(value="/reference/{refID}")
    public ModelAndView seqSummeryByRefId(@PathVariable("refID") String refID) {
        logger.debug("->seqSummeryByRef started");

        // setup search parameters object to gather the requested reference
        SearchParams searchParams = new SearchParams();
        Filter refIdFilter = new Filter(SearchConstants.REF_ID, refID);
        searchParams.setFilter(refIdFilter);        
        
        // find the requested reference
        SearchResults<Reference> searchResults
          = referenceFinder.searchReferences(searchParams);

        return seqSummaryByRef(searchResults.getResultObjects(), refID);
    }

    @RequestMapping(value={"/reference", "/summary"})
    public ModelAndView seqSummeryByRefKey(@RequestParam("_Refs_key") String dbKey,
    		HttpServletRequest request) {
        logger.debug("->referenceSummaryByMarkerKey started: " + dbKey);       
        
        // find the requested reference
        SearchResults<Reference> searchResults
        	= referenceFinder.getReferenceByKey(dbKey);

        return seqSummaryByRef(searchResults.getResultObjects(), dbKey);
    }
    
    private ModelAndView seqSummaryByRef(List<Reference> refList, String refKey){
        ModelAndView mav = new ModelAndView("sequence_summary_reference");

        // there can be only one...
        if (refList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No reference found for " + refKey);
            return mav;
        }
        if (refList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe references found for " + refKey);
            return mav;
        }

        // pull out the reference, and place into the mav
        Reference reference = refList.get(0);

        mav.addObject("queryString", "refKey=" + reference.getReferenceKey());
        mav.addObject("reference", reference);

        return mav;
    }

    /*
     * Sequence Summary by Marker
     */
    @RequestMapping(value="/marker/{mrkID}")
    public ModelAndView seqSummeryByMarkerId(
		HttpServletRequest request,
        @PathVariable("mrkID") String mrkID)
    {

        logger.debug("->seqSummeryByMarker started");

        // setup search parameters object to gather the requested marker
        SearchParams searchParams = new SearchParams();
        Filter mrkIdFilter = new Filter(SearchConstants.MRK_ID, mrkID);
        searchParams.setFilter(mrkIdFilter);

        // find the requested marker
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByID(searchParams);
        
        String provider = request.getParameter("provider");

        return seqSummeryByMarker(searchResults.getResultObjects(), mrkID, provider);
    }

    @RequestMapping(value={"/marker", "/summary"})
    public ModelAndView seqSummeryByMarkerKey(@RequestParam("_Marker_key") String markerKey,
    		HttpServletRequest request) {
        logger.debug("->referenceSummaryByMarkerKey started: " + markerKey);

        // find the requested reference
        SearchResults<Marker> searchResults
          = markerFinder.getMarkerByKey(markerKey);
        
        String provider = request.getParameter("provider");

        return seqSummeryByMarker(searchResults.getResultObjects(), markerKey, provider);
    }
    
    private ModelAndView seqSummeryByMarker(List<Marker> markerList, String markerKey, String provider){
        ModelAndView mav = new ModelAndView("sequence_summary_marker");
        
        // there can be only one...
        if (markerList.size() < 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "No marker found for " + markerKey);
            return mav;
        }
        if (markerList.size() > 1) {
            // forward to error page
            mav = new ModelAndView("error");
            mav.addObject("errorMsg", "Dupe marker ID found for " + markerKey);
            return mav;
        }

        // pull out the marker, and place into the mav
        Marker marker = markerList.get(0);
        mav.addObject("marker", marker);

        // build JSON querystring
        String queryString = new String("mrkKey=" + marker.getMarkerKey());
        if ((provider != null) && (!"".equals(provider))) {
          queryString = queryString + "&provider=" + provider;
		}

        mav.addObject("queryString", queryString);

        return mav;	
    }

    /*
     * JSON summary results
     */
    @RequestMapping("/json")
    public @ResponseBody JsonSummaryResponse<SeqSummaryRow> seqSummaryJson(
            HttpServletRequest request,
            @ModelAttribute Paginator page) {

        logger.debug("->JsonSummaryResponse started");

        // generate search parms object to pass to finders
        SearchParams params = new SearchParams();
        params.setSorts(this.genSorts(request));
        params.setPaginator(page);

        // parameter parsing
        List<Filter> filterList = new ArrayList<Filter>();
        String refKey = request.getParameter("refKey");
        String mrkKey = request.getParameter("mrkKey");
        String provider = request.getParameter("provider");

        if (refKey != null) {
            filterList.add(new Filter(SearchConstants.REF_KEY, refKey));
	    }
        if (mrkKey != null) {
            filterList.add(new Filter(SearchConstants.MRK_KEY, mrkKey));
	    }
        if (provider != null) {
            filterList.add(new Filter(SearchConstants.SEQ_PROVIDER, provider));
	    }

        Filter containerFilter = new Filter();
        if (filterList.size() > 0){
            containerFilter.setFilterJoinClause(Filter.JoinClause.FC_AND);
            containerFilter.setNestedFilters(filterList);
		}
        params.setFilter(containerFilter);


        // perform query, and pull out the sequences requested
        SearchResults<Sequence> searchResults
          = sequenceFinder.getSequences(params);
        List<Sequence> seqList = searchResults.getResultObjects();

        // create/load the list of SeqSummaryRow wrapper objects
        List<SeqSummaryRow> summaryRows = new ArrayList<SeqSummaryRow> ();
        Iterator<Sequence> it = seqList.iterator();
        while (it.hasNext()) {
            Sequence sequence = it.next();
            if (sequence == null) {
                logger.debug("--> Null Sequence Object");
            }else {
                summaryRows.add(new SeqSummaryRow(sequence));
            }
        }

        // The JSON return object will be serialized to a JSON response for YUI table.
        JsonSummaryResponse<SeqSummaryRow> jsonResponse
          = new JsonSummaryResponse<SeqSummaryRow>();

        // place data into JSON response, and return
        jsonResponse.setSummaryRows(summaryRows);
        jsonResponse.setTotalCount(searchResults.getTotalCount());
        return jsonResponse;
    }

    /*
     * Handles submission of sequences to (NCBI) BLAST.  This involves 
     * receiving seqfetch-style parameters, loading Sequence objects, working
     * through some rules to build a suitable URL to redirect us to NCBI's
     * BLAST query form.  Added Summer 2015 in MGI 5.23 (TR11726).
     */
    @RequestMapping(value="/blast")
    public ModelAndView forwardToBlast(HttpServletRequest request)
    {
	/* Parameters are submitted named as seq1, seq2, seq3, ... seqN.  Need
	 * to collect the associated strings of values for each.  Each string
	 * of values is delimited by exclamation points and includes:
	 * 1. provider
	 * 2. seq ID
	 * 3. chromosome (optional)
	 * 4. start coordinate (optional)
	 * 5. end coord (optional)
	 * 6. strand (optional)
	 * 7. centimorgan offset (optional and currently unused)
	 */
	Map<String,String[]> parameters = request.getParameterMap();

	/* maps from the index of the parameter to its corresponding BLASTable
	 * sequence
	 */
	Map<String,BlastableSequence> seqs =
	    new HashMap<String,BlastableSequence>();

	// used to split the parameters into alphabetic prefix / numeric suffix
	// pieces (eg.- seq1 --> seq + 1)
	Pattern paramPattern = Pattern.compile("([a-zA-Z]+)([0-9]+)");

	// list of errors which occurred when seeking Sequence objects
	List<String> errors = new ArrayList<String>();

	// list of integers which are (as Strings) keys of 'seqs', so we can
	// properly order the sequences later on
	List<Integer> integerKeys = new ArrayList<Integer>();

	String blastSpec = null;	// special param for mouse-specific QF

	BlastableSequence seq = null;  // the object for the current index
	    
	for (String key : parameters.keySet()) {
	    /* We only support a single value for any given parameter name, so
	     * give an error message if this one has multiple.
	     */

	    // all values for this parameter
	    String[] values = parameters.get(key);

    	    // the one (valid) value for this parameter
	    String value = null;

	    if ("seqs".equals(key)) {
		// this parameter is allowed to have multiple values
	    } else if (values.length > 1) {
		errors.add("Parameter (" + key + ") has multiple values");
	    } else if (values.length == 0) {
		errors.add("Parameter (" + key + ") is missing a value");
	    } else {
		value = values[0];
	    }

	    /* We support two types of parameters:
	     *   seq1, seq2, ... seqN -- seqfetch-style sequence descriptions
	     *   flank1, flank2, ... flankN -- amount of flanking sequence to
	     *   	add to each end of the sequence
	     * Any other fields will be flagged as errant.
	     */
	    Matcher matcher = paramPattern.matcher(key);

	    String prefix;	// the alphabetic part of the parameter name
	    String index;	// the numeric part of the parameter name

	    if (matcher.matches()) {
		// If we've already dealt with a sequence for this index, then
		// retrieve it.  Otherwise, instantiate one and remember it
		// for this index.

		prefix = matcher.group(1);
		index = matcher.group(2);

		if (seqs.containsKey(index)) {
		    seq = seqs.get(index);
		} else {
		    seq = new BlastableSequence();
		    seqs.put(index, seq);
		    integerKeys.add(new Integer(index));
		}

		if ("flank".equals(prefix)) {
		    // remember the flank amount specified for this sequence
		    seq.setFlankAmount(value);

		} else if ("seq".equals(prefix)) {
		    if (value != null) {
			seq.setParameters(value);

			// The set of parameters must at least specify the
			// provider and the seq ID.  If not, log the error.

			String[] fields = value.split("!");
			if (fields.length < 2) {
			    errors.add("Invalid set of fields for " + key
				+ ": " + value);
			} else {
			    // We have an ID, so try to load the Sequence
			    // object and add it to 'seq'.

			    String seqID = fields[1];
			    seq.setSequenceID(seqID);

			    Sequence seqObj = sequenceFinder.getSequenceByID(
				seqID);
			    if (seqObj == null) {
				errors.add(seqID
				    + " did not uniquely identify a sequence");
			    } else {
				seq.setSequence(seqObj);
			    }
			}
		    }
		} else {
		    errors.add("Unexpected parameter: " + key);
		}

	    } else if ("blastSpec".equals(key)) {
		// special parameter for the mouse-specific query form
		blastSpec = value;

	    } else if ("snpID".equals(key)) {
		// special handling for flanking sequence submitted for a SNP.
		// assumes no other sequences submitted at the same time.

		if (seq == null) {
		    seq = new BlastableSequence();
		    seqs.put("1", seq);
		    integerKeys.add(new Integer(1));
		}
		seq.setSnpID(value);

	    } else if ("snpFlank".equals(key)) {
		// special handling for flanking sequence submitted for a SNP.
		// assumes no other sequences submitted at the same time.

		if (seq == null) {
		    seq = new BlastableSequence();
		    seqs.put("1", seq);
		    integerKeys.add(new Integer(1));
		}
		seq.setSnpFlank(value);

	    } else if ("seqs".equals(key)) {
		// special field can have multiple sequence values and to have
		// multiple sequences in a single value, separated by "#SEP#".
		// We assume there are no numbered seq fields at the same
		// time.

		int i = 0;

		for (String seq1 : values) {
		    for (String s : seq1.split("#SEP#")) {
			seq = new BlastableSequence();
			seq.setParameters(s);

			String[] fields = s.split("!");
			if (fields.length < 2) {
			    errors.add("Invalid sequence string: " + s);
			} else {
			    // We have an ID, so try to load the sequence
			    // object and add it to 'seq'.

			    String seqID = fields[1];
			    seq.setSequenceID(seqID);

			    Sequence seqObj = sequenceFinder.getSequenceByID(
				seqID);
			    if (seqObj == null) {
				errors.add(seqID +
				    " did not uniquely identify a sequence");
			    } else {
				seq.setSequence(seqObj);
			    }
			}
			i++;
			index = i + "";
			seqs.put(index, seq);
		        integerKeys.add(new Integer(index));
		    }
		}

	    } else if ("seqPullDown".equals(key)) {
		// ignore this parameter (homology detail page)

	    } else {
		errors.add("Unexpected parameter: " + key);
	    }
	}

	// if we had a flank1 parameter and no seq1 parameter, then we can
	// just remove that from consideration
	
	if (seqs.containsKey("1")) {
	    BlastableSequence seq1 = seqs.get("1");
	    if ((seq1.getSeqID() == null) && (seq1.getSnpFlank() == null)) {
		seqs.remove("1");
		integerKeys.remove(integerKeys.indexOf(new Integer(1)));
	    }
	}

	// put our keys in order and retrieve the FASTA format for any
	// sequences where we need to

	Collections.sort(integerKeys);
	List<BlastableSequence> byID = new ArrayList<BlastableSequence>();
	List<BlastableSequence> byFasta = new ArrayList<BlastableSequence>();

	for (Integer index : integerKeys) {
		seq = seqs.get(index.toString());

		// Try to get the FASTA text for the sequence (which detects
		// errors and caches the text in the object itself).  Also,
		// add this sequence to its proper list, as we need to ensure
		// that ID submissions occur before FASTA submissions.

		if (!seq.canSubmitByID()) {
		    byFasta.add(seq);
		} else {
		    byID.add(seq);
		}

		// collect any error messages for this sequence

		for (String s : seq.getErrors()) {
		    errors.add(s);
		}
	} 

	if (integerKeys.size() == 0) {
	    errors.add("No sequences were selected to forward to NCBI BLAST.");
	}

	// if we encountered errors, report them

	if (errors.size() > 0) {
            ModelAndView mav = new ModelAndView("error");
	    StringBuffer sb = new StringBuffer();
	    sb.append("The following error(s) were found:<P><UL>");
	    for (String e : errors) {
		sb.append("<LI>" + e + "</LI>");
	    }
	    sb.append("</UL>");
            mav.addObject("errorMsg", sb.toString());
            return mav;
	}

	// no problems with the sequences, so pass them along to NCBI BLAST

	List<BlastableSequence> sequences = new ArrayList<BlastableSequence>();
	if (!sequences.addAll(byID)) {
	    for (BlastableSequence s : byID) {
		sequences.add(s);
	    }
	}
	if (!sequences.addAll(byFasta)) {
	    for (BlastableSequence s : byFasta) {
		sequences.add(s);
	    }
	}

        ModelAndView mav = new ModelAndView("blast_redirect");
        mav.addObject("sequences", sequences);

	addBlastParameters(sequences, blastSpec, mav);

        return mav;
    }

    //--------------------------------------------------------------------//
    // private methods
    //--------------------------------------------------------------------//

    /* go through the 'sequences' and analyze them to determine what to set
     * for BLAST parameters in 'mav', then do it.
     */
    private void addBlastParameters(List<BlastableSequence> sequences,
	String blastSpec, ModelAndView mav) {

	String pageType = "BlastSearch";	// always BlastSearch
	String linkLoc = "blasthome";		// blasthome or blasttab
	String program = null;			// blastp or blastn
	String filter = null;			// R for rodent filter
	String repeats = null;			// repeat_9989 for rodent filter
	StringBuffer query = null;		// IDs and FASTA sequence

	for (BlastableSequence seq : sequences) {
	    String seqType = seq.getType();

	    // if we find a mouse nucleotide sequence, that trumps everything
	    // else and should set both the program and the filter info

	    if ("mouse".equals(seq.getOrganism()) &&
		("DNA".equals(seqType) || "RNA".equals(seqType) )
		) {
		program = "blastn";
		filter = "R";
		repeats = "repeat_9989";
	    } else if (program == null) {

		// otherwise, we just pick up the program from the first
		// sequence in the bunch

		// if we know it's a nucleotide sequence, then pick blastn

		if ("DNA".equals(seqType) || "RNA".equals(seqType)){
		    program = "blastn";

		} else if ("Not Loaded".equals(seqType)) {
		    // if the type is "not loaded", then we can identify
		    // polypeptide sequences as those beginning with "NP_" or
		    // "XP_" (both RefSeqs).  Otherwise, they're going to be
		    // nucleotide sequences (either RefSeqs which start with
		    // NB, XR, XM, and NM, or GenBank sequences)

		    String seqID = seq.getSeqID();
		    if (seqID == null) {
			program = "blastn";
		    } else if (seqID.startsWith("NP_")
			    || seqID.startsWith("XP_") ) {
			program = "blastp";
		    } else {
			program = "blastn";
		    }

		} else {
		    // We've fallen through to this for polypeptide sequences.
		    program = "blastp";
		}
	    }

	    if (query != null) { query.append("\n"); }
	    else { query = new StringBuffer(); }

	    if (seq.canSubmitByID()) { query.append(seq.getSeqID()); }
	    else if (seq.getFasta() != null ) { query.append(seq.getFasta()); }
	}

	mav.addObject("pageType", pageType);
	mav.addObject("linkLoc", linkLoc);

	// if blastSpec is not specified, we need to set the program manually
	if (blastSpec == null) {
	    if (program != null) { mav.addObject("program", program); }

	} else {
	    // otherwise, blastn is assumed when we go to the special mouse-
	    // specific query form

	    mav.addObject("blastSpec", blastSpec);
	}
	if (filter != null) { mav.addObject("filter", filter); }
	if (repeats != null) { mav.addObject("repeats", repeats); }
	if (query != null) { mav.addObject("query", query.toString()); }
    }

    // generate the sorts
    private List<Sort> genSorts(HttpServletRequest request) {

        logger.debug("->genSorts started");

        Sort typeSort = new Sort(SortConstants.SEQUENCE_TYPE);
        Sort provSort = new Sort(SortConstants.SEQUENCE_PROVIDER);
        Sort lenSort  = new Sort(SortConstants.SEQUENCE_LENGTH, true);

        List<Sort> sorts = new ArrayList<Sort>();
        sorts.add(typeSort);
        sorts.add(provSort);
        sorts.add(lenSort);
        return sorts;
    }

}
