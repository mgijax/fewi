package org.jax.mgi.fewi.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import org.jax.mgi.shr.datafactory.ActualDBMap;
import org.jax.mgi.shr.datafactory.AlleleFactory;
import org.jax.mgi.shr.datafactory.LookupCache;
import org.jax.mgi.shr.datafactory.DBConstants;
import org.jax.mgi.shr.dbutils.SQLDataManager;
import org.jax.mgi.shr.dto.DTO;
import org.jax.mgi.shr.dto.DTOConstants;
import org.jax.mgi.shr.log.Logger;
import org.jax.mgi.shr.stringutil.Sprintf;
import org.jax.mgi.shr.webapp.WebAppLogger;*/

/**
* @module NotesTagConverter.java
* @author pf
*/

/**  The NotesTagConverter class contains methods which encapsulate
*   knowledge of the note tags found in note fields.  They allow for easy
*   conversions of tag->HTML anchor, to be displayed within the javawi2.
*
* @is a converter used to modify tags found in note fields to thier
*   expanded HTML anchor equivalents
* @has knowledge of all tags found within note fields
* @does converts the found tags
*
*    Public methods include:  (parameters not listed here)
*    <OL>
*    <LI> convertNotes(notes, delimiter) -- returns a string containing
*       the notes, with converted tags
*    </OL>
*/
public class NotesTagConverter
{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /////////////////////
    // instance variables
    /////////////////////

    // Maps the tag pattern to it's converted replacement string.
    // This is used to gather all tags expected in notes, and pre-build
    // their replacement strings
    private HashMap tagConversions = new HashMap();

    // maps from MGI ID to its corresponding allele symbol, to save looking
    // up the same symbol multiple times for the same page
    private HashMap alleleSymbols = new HashMap();

    /* -------------------------------------------------------------------- */
    ///////////////
    // Constructor
    ///////////////

    /** constructor; instantiates and initializes a new NotesTagConverter.
    * @param string; css classes to be applied to all created anchors
    * @assumes the resulting pages has access to the css; DB and config access
    * @effects nothing
    * @throws IOException
    */
    public NotesTagConverter (String cssAnchorClass)
        throws IOException
    {
        logger.info("Trying to make a tag map");
        tagConversions = makeTagMap(cssAnchorClass);
        logger.info("Did that successfully");

        return;
    }

    /** constructor; instantiates and initializes a new NotesTagConverter.
    * @param none
    * @assumes DB and config access
    * @effects nothing
    * @throws IOException
    */
    public NotesTagConverter ()
        throws IOException
    {
        String cssAnchorClass = "";
        tagConversions = makeTagMap(cssAnchorClass);

        return;
    }


    /* -------------------------------------------------------------------- */
    ////////////////////////
    // Public access methods
    ////////////////////////

    /** convert all known tags in notes to HTML anchors.
    * @param notes; the notes string
    * @param delimiter; delimiter used to seperate tag parameters
    * @assumes nothing
    * @effects notes parameter
    * @throws
    */
    public String convertNotes (String notes, char delimiter)
    {

        logger.info("Entered into the convert notes subroutine");
        
        String      parmStr             = "";
        String      convertedTag        = "";
        String      matchStr            = "";
        Iterator    matchStrIter;
        Set         matchStrSet;

        // first, handle the non-standard \AlleleSymbol() tag
        ///////////////////////////notes = this.doAlleleSymbolTag (notes, null);

        // Create iterator for pattern matching, from the pre-built
        // HashMap of searchPatterns
        matchStrSet         = tagConversions.keySet();
        matchStrIter        = matchStrSet.iterator();

        
        
        // for each tag we are lookin for...
        while (matchStrIter.hasNext())
        {

            // pre-compile a matcher to look for regular expression matching
            // the tag format
            matchStr    = (String)matchStrIter.next();
            Pattern p   = Pattern.compile(matchStr);
            Matcher m   = p.matcher(notes);

            // for each tag in the notes text...
            while(m.find()){

                parmStr= m.group(1);
                logger.info("Found this string." + parmStr);

                // Replace the tag with the new string
                convertedTag = convertTag(parmStr,
                                delimiter,
                                (String)tagConversions.get(matchStr));
                notes = m.replaceFirst(convertedTag);

                // reset the matcher for the next search
                m = p.matcher(notes);
            }
        }

        return notes;
    }


    /* -------------------------------------------------------------------- */

    /** convert all instances of \AlleleSymbol(MGI ID, flag) to either a link
    *    or just an allele symbol.  We use a special method, as this is a
    *    non-standard tag (it needs to retrieve data from the database).
    */
 /*   public String doAlleleSymbolTag (String input, String cssClass)
    {
	// pattern to match; first parameter is MGI ID of an allele, second
	// parameter indicates whether to make a link (1) or not (0)
	String tag = "\\\\AlleleSymbol *\\((.*?[|].*?)\\)";

	// regular expression handlers...
        Pattern p = Pattern.compile (tag);
	Matcher m = p.matcher (input);

	String parameterString = null;	// parameters in tag when found
	String replacement = null;	// what tag should be replaced with
	String[] parameters = null;	// 'parameterString' after being split
	String mgiID = null;		// MGI ID of allele in this tag
	boolean makeLink = true;	// does this tag require a link?
	int alleleKey = -1;		// _Allele_key for allele in this tag
	String symbol = null;		// symbol for allele in this tag
	String classStr = "";		// string with css class attribute

	if ((cssClass != null) && (cssClass.length() > 0)) {
	    classStr = " CLASS='" + cssClass + "'";
	}

	// needed to look up allele symbols for each 'mgiID'...
	WICfg config = null;
	SQLDataManager sqlDM = null;
	Logger logger = null;

	try
	{
            config = new WICfg();
            sqlDM = new SQLDataManager(config);
	    logger = new WebAppLogger ("JavaWI");
	}
	catch (Exception e)
	{
	    return input;	// bail out if cannot do database lookups
	}
	LookupCache symbolCache = new LookupCache (sqlDM, logger,
	    "ALL_Allele", "_Allele_key", "symbol");
	AlleleFactory alleleFactory = new AlleleFactory (config,sqlDM,logger);

	// pattern to be used when creating an HTML link to an allele
	String linkPattern =
	    String.format ("<A HREF='%s", config.getJavaWIUrl());
	linkPattern = linkPattern
	       + "WIFetch?page=alleleDetail&id=%s'%s>%s</A>";

	// loop through 'input' to find all matches to 'tag'
	while (m.find())
	{
	    parameterString = m.group(1);
	    parameters = parameterString.split ("\\|");

	    if (parameters[0] == null)		// invalid ID, bailout
	    {
		try
		{
		    sqlDM.closeResources();
		}
		catch (Exception e)
		{
		    // clean-up failed, but bail out anyway
		}

	        return input;
	    }

	    // look up data for allele from our memory cache, if possible

            mgiID = parameters[0];
	    symbol = null;

	    if (this.alleleSymbols.containsKey(mgiID))
	    {
		symbol = (String) this.alleleSymbols.get(mgiID);
	    }
	    else	// get the allele symbol from the database
	    {
	        try
	        {
	            alleleKey = alleleFactory.getKeyForID (mgiID);
	            if (alleleKey != -1)
		    {
		        symbol = symbolCache.get(alleleKey);
			if (symbol != null) {
		    	    symbol = FormatHelper.superscript(symbol);
			}
		    }
	        }
	        catch (Exception e) {}		// let symbol remain null

		this.alleleSymbols.put (mgiID, symbol);
	    }

	    // determine whether this tag should be replaced by a link

	    if ((parameters[1] == null) || (parameters[1].trim().equals("0")))
	    {
	        makeLink = false;
	    }
	    else
	    {
	        makeLink = true;
	    }

	    // find what text string should replace this tag

	    if (symbol != null)
	    {
	        if (makeLink)
	        {
		    replacement = String.format (linkPattern, mgiID,
			classStr, symbol);
	        }
	        else
	        {
		    replacement = symbol;
	        }
	    }
	    else
	    {
	        replacement = "invalid allele";
	    }

	    // do the replacement and look for the next tag

	    input = m.replaceFirst (replacement);
	    m = p.matcher (input);
	}
	try
	{
             sqlDM.closeResources();
        }
        catch (Exception e) {
             //  Couldn't close resource we're leaving anyhow...
        }
	return input;
    }
*/
    /* -------------------------------------------------------------------- */

    public String useNewWindows (String inputString)
    {
	Pattern p = Pattern.compile (
	    "[hH][rR][eE][fF] ?= ?(\"[^\"]*\")");
	String s = inputString;
	Matcher m = p.matcher(s);
	String url;

	while (m.find()) {
	    url = m.group(1);
	    s = m.replaceFirst ("HHRREEFF=" + url + " onClick='newWindow(" +
		url + "); return false;' TARGET='_new'");
	    m = p.matcher(s);
	}
	return s.replaceAll ("HHRREEFF", "HREF");
    }

    /* -------------------------------------------------------------------- */

    ////////////////////////
    // private methods
    ////////////////////////

    /** builds internal HashMap containing key:value pairs with keys being
    *   tag search patterns and values being replacement string for that tag
    * @param string; name of css anchor tag to use
    * @assumes nothing
    * @effects local makeTagMap HashMap
    * @throws
    */
    private HashMap makeTagMap (String cssAnchorClass)
    {
        HashMap tags = new HashMap();

        // Base URLs used in creating the replacement string patterns
        String javawiURL        = "";
        String wiURL            = "";
        String mgihomeURL       = "";

        String ipURL            = "";
        String ecURL            = "";
        String emblURL          = "";
        String spURL            = "";
        String ncbiqURL         = "";
        String ncbipqURL        = "";
        String ncbinqURL        = "";
        String jbcURL           = "";
        String jlrURL           = "";
        String dxdoiURL         = "";

        // get a WICfg and ActualDBMap, and extract all needed data
        try
        {

/*            Integer ec_ldb          = new Integer(8);   // Enzyme Commission
            Integer embl_ldb        = new Integer(9);   // EMBL
            Integer sp_ldb          = new Integer(DBConstants.LogicalDB_SwissProt);  // SWISS-PROT
            Integer ip_ldb          = new Integer(DBConstants.LogicalDB_Interpro);   // InterPro
            Integer dxdoi_ldb       = new Integer(65);  // NCBI Query
            Integer ncbiq_ldb       = new Integer(68);  // NCBI Query
            Integer ncbipq_ldb      = new Integer(69);  // NCBI Protein Query
            Integer ncbinq_ldb      = new Integer(89);  // NCBI Nucleotide Query
            Integer jbc_ldb         = new Integer(DBConstants.LogicalDB_JBiolChem);  // Journal of Biological Chemistry
            Integer jlr_ldb         = new Integer(DBConstants.LogicalDB_JLipidRes);  // Journal of Lipid Research
*/
/*            ArrayList spURL_list    = adbMap.getActualDBUrl(sp_ldb);
            ArrayList ecURL_list    = adbMap.getActualDBUrl(ec_ldb);
            ArrayList emblURL_list  = adbMap.getActualDBUrl(embl_ldb);
            ArrayList dxdoiURL_list = adbMap.getActualDBUrl(dxdoi_ldb);
            ArrayList ipURL_list    = adbMap.getActualDBUrl(ip_ldb);
            ArrayList ncbiq_list    = adbMap.getActualDBUrl(ncbiq_ldb);
            ArrayList ncbipq_list   = adbMap.getActualDBUrl(ncbipq_ldb);
            ArrayList ncbinq_list   = adbMap.getActualDBUrl(ncbinq_ldb);
            ArrayList jbc_list      = adbMap.getActualDBUrl(jbc_ldb);
            ArrayList jlr_list      = adbMap.getActualDBUrl(jlr_ldb);*/

/*            spURL           = (String)spURL_list.get(0);
            ecURL           = (String)ecURL_list.get(0);
            emblURL         = (String)emblURL_list.get(0);
            ipURL           = (String)ipURL_list.get(0);
            ncbiqURL        = (String)ncbiq_list.get(0);
            ncbipqURL       = (String)ncbipq_list.get(0);
            ncbinqURL       = (String)ncbinq_list.get(0);
            jbcURL          = (String)jbc_list.get(0);
            jlrURL          = (String)jlr_list.get(0);
            dxdoiURL        = (String)dxdoiURL_list.get(0);*/

            spURL           = "spURL";
            ecURL           = "ecURL";
            emblURL         = "emblURL";
            ipURL           = "ipURL";
            ncbiqURL        = "ncbiqURL";
            ncbipqURL       = "ncbipqURL";
            ncbinqURL       = "ncbinqURL";
            jbcURL          = "jbcURL";
            jlrURL          = "jlrURL";
            dxdoiURL        = "dxdoiURL";            
            
/*            javawiURL       = config.getJavaWIUrl();
            wiURL           = config.getPythonWIUrl();
            mgihomeURL      = config.getMgiHomeUrl();*/
            javawiURL       = "javawiurl";
            wiURL           = "wiUrl";
            mgihomeURL      = "mgihomeURL";

        }
        catch (Exception exc)
        {
            System.out.println(
                "noteTagConverter could not gather all needed URLs");
        }

        // Pre-build all anchor replacement strings for each known tag

        ///////////////////
        // javawi2 links //
        ///////////////////

        // Marker
        tags.put("\\\\Marker\\((.*?[|].*?[|].*?)\\)",
            String.format("<A class=\"%s\" HREF=\"%sWIFetch?page=markerDetail&id=%s\" %s>%s</A>",
            cssAnchorClass, javawiURL, "%s", "%s", "%s"));

        // Sequence
        tags.put("\\\\Sequence\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%sWIFetch?page=sequenceDetail&id=%s\" %s>%s</A>",
            cssAnchorClass, javawiURL, "%s", "%s", "%s"));

        ///////////////
        // python wi //
        ///////////////

        // Accession
        tags.put("\\\\Acc\\((.*?[|].*?[|].*?)\\)",
            String.format("<A class=\"%s\" HREF=\"%ssearches/accession_report.cgi?id=%s\" %s>%s</A>",
            cssAnchorClass, wiURL, "%s", "%s", "%s"));

        // Allele
        tags.put("\\\\Allele\\((.*?[|].*?[|].*?)\\)",
            String.format("<A style='white-space: normal; 'class=\"%s\" HREF=\"%sWIFetch?page=alleleDetail&id=%s\" %s>%s</A>",
            cssAnchorClass, javawiURL, "%s", "%s", "%s"));

        // AMA
        tags.put("\\\\AMA\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%ssearches/AMA.cgi?id=%s\" %s>%s</A>",
            cssAnchorClass, wiURL, "%s", "%s", "%s"));

        // GO
        tags.put("\\\\GO\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%ssearches/GO.cgi?id=%s\" %s>%s</A>",
            cssAnchorClass, wiURL, "%s", "%s", "%s"));

        // Reference
        tags.put("\\\\Ref\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%ssearches/accession_report.cgi?id=%s\" %s>%s</A>",
            cssAnchorClass, wiURL, "%s", "%s", "%s"));

        // Elsevier (might be a temp solution...python wi renders this tag differently)
        tags.put("\\\\Elsevier\\((.*?[|].*?[|].*?)\\)",
            String.format (" in <A class=\"%s\" HREF=\"%ssearches/accession_report.cgi?id=%s\" %s>%s</A>",
            cssAnchorClass, wiURL, "%s", "%s", "%s"));

        /////////////
        // mgihome //
        /////////////

        // GoCurators
        tags.put("\\\\GoCurators\\((.*?[|].*?[|].*?)\\)",
            String.format("<A class=\"%s\" HREF=\"%sGO/go_curators.shtml%s\" %s>%s</A>",
            cssAnchorClass, mgihomeURL, "%s", "%s", "%s"));

        // GoRefGenome
        tags.put("\\\\GoRefGenome\\((.*?[|].*?[|].*?)\\)",
            String.format("<A class=\"%s\" HREF=\"%sGO/reference_genome_project.shtml%s\" %s>%s</A>",
            cssAnchorClass, mgihomeURL, "%s", "%s", "%s"));

        ///////////////////
        // email aliases //
        ///////////////////

        // GoCurators
        tags.put("\\\\GoEmail\\((.*?[|].*?[|].*?)\\)",
            "<A HREF=\"mailto:mgi-go@informatics.jax.org%s\" %s>%s</A>");


        ///////////////
        // external
        ///////////////

        // These are created in a three step process due to not being able
        // to place two '%s' together.  (e.g. foo%s%s currently errors)

        // InterPro
        String ipRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, ipURL.replaceAll("@@@@","%s"));
        ipRepStr = ipRepStr.concat(" %s>%s</A>");
        tags.put("\\\\InterPro\\((.*?[|].*?[|].*?)\\)",ipRepStr);

        // Enzyme Commission Number
        String ecRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, ecURL.replaceAll("@@@@","%s"));
        ecRepStr = ecRepStr.concat("%s>%s</A>");
        tags.put("\\\\EC\\((.*?[|].*?[|].*?)\\)",ecRepStr);

        // EMBL
        String emblRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, emblURL.replaceAll("@@@@","%s"));
        emblRepStr = emblRepStr.concat("%s>%s</A>");
        tags.put("\\\\EMBL\\((.*?[|].*?[|].*?)\\)",emblRepStr);

        // SwissProt
        String spRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, spURL.replaceAll("@@@@","%s"));
        spRepStr = spRepStr.concat("%s>%s</A>");
        tags.put("\\\\SwissProt\\((.*?[|].*?[|].*?)\\)",spRepStr);

        // NCBI Query
        String ncbiqRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, ncbiqURL.replaceAll("@@@@","%s"));
        ncbiqRepStr = ncbiqRepStr.concat("%s>%s</A>");
        tags.put("\\\\NCBIQuery\\((.*?[|].*?[|].*?)\\)",ncbiqRepStr);

        // NCBI Protein Query
        String ncbipqRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, ncbipqURL.replaceAll("@@@@","%s"));
        ncbipqRepStr = ncbipqRepStr.concat("%s>%s</A>");
        tags.put("\\\\NCBIProteinQuery\\((.*?[|].*?[|].*?)\\)",ncbipqRepStr);

        // NCBI Nucleotide Query
        String ncbinqRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, ncbinqURL.replaceAll("@@@@","%s"));
        ncbinqRepStr = ncbinqRepStr.concat("%s>%s</A>");
        tags.put("\\\\NCBINucleotideQuery\\((.*?[|].*?[|].*?)\\)",ncbinqRepStr);

        // Journal of Biological Chemistry
        String jbcRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, jbcURL.replaceAll("@@@@","%s"));
        jbcRepStr = jbcRepStr.concat("%s>%s</A>");
        tags.put("\\\\JBiolChem\\((.*?[|].*?[|].*?)\\)",jbcRepStr);

        // Journal of Lipid Research
        String jlrRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, jlrURL.replaceAll("@@@@","%s"));
        jlrRepStr = jlrRepStr.concat("%s>%s</A>");
        tags.put("\\\\JLipidRes\\((.*?[|].*?[|].*?)\\)",jlrRepStr);


        // DXDOI
        String dxdoiStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, dxdoiURL.replaceAll("@@@@","%s"));
        dxdoiStr = dxdoiStr.concat("%s>%s</A>");
        tags.put("\\\\DXDOI\\((.*?[|].*?[|].*?)\\)",dxdoiStr);




        return tags;
    }

    /* -------------------------------------------------------------------- */

    /** expands a note tag to required string
    * @param string; parms - parameters of the found tag
    * @param char; delimiter - character used to delimit parms
    * @param string; replaceStr - pre-build replacement string for this tag
    * @assumes nothing
    * @effects nothing
    * @throws
    */
    private String convertTag (String parms, char delimiter, String replaceStr)
    {
        String      convertedTag    = "";
        String      id              = "";
        String      display         = "";
        String      target          = "";  // used for opening in new window

        // Seperate the tag parameters by the given delimiter
        String      delimStr        = String.format("[%s]",
                                      Character.toString(delimiter));
        String[]    parmArray       =  parms.split(delimStr);

        if (parmArray[0] != null) {
            id = parmArray[0];

            if (id.equals("none")){ //the url has no parameter
                id = "";
            }
        }

        if (parmArray.length == 1) {
            display = id;
        } else {
              display  = parmArray[1];
        }

        if ((parmArray.length > 2) && (parmArray[2] != null)) {
            String parm2 = parmArray[2];
            if (parm2.equals("newwin")) {
                target = "TARGET=\"NEW\"";
            }
        }

        // Markup superscripts is any are found
        display = superscript(display);

        // Apply parms to replacement string
        convertedTag = String.format(replaceStr, id, target, display);

        return convertedTag;
    }

    /* -------------------------------------------------------------------- */

    /** superscripts alleles as needed
    * @param string; string to be displayed, that may have a superscript
    * @assumes nothing
    * @effects nothing
    * @throws
    */
    private String superscript (String displayParm)
    {
        // setup to find greater/less than symbols
        Pattern pGT   = Pattern.compile(">");
        Pattern pLT   = Pattern.compile("<");
        Matcher mGT   = pGT.matcher(displayParm);
        Matcher mLT   = pLT.matcher(displayParm);

        // if both are found, replace them with the proper HTML
        if (mGT.find() & mLT.find()){
            displayParm = displayParm.replaceAll("<", "<sup");
            displayParm = displayParm.replaceAll(">", "</sup>");
            displayParm = displayParm.replaceAll("<sup", "<sup>");
        }

        return displayParm;
    }

}
