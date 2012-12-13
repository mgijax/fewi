package org.jax.mgi.fewi.util;

// standard java
import java.util.*;
import java.util.regex.*;
import java.io.IOException;

// internal
import org.jax.mgi.fewi.config.ContextLoader;

//external
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**  The NotesTagConverter class contains methods which encapsulate
*   knowledge of the note tags found in note fields.  They allow for easy
*   conversions of tag->HTML anchor, to be displayed within the fewi.
*
* @is a converter used to modify tags found in note fields to thier
*   expanded HTML anchor equivalents
* @has knowledge of all tags found within note fields
* @does converts the found tags
*/
public class NotesTagConverter
{
    /////////////////////
    // instance variables
    /////////////////////

    // This is used to map a tag pattern to it's converted replacement string.
    private HashMap tagConversions = new HashMap();

    // maps from MGI ID to its corresponding allele symbol, to save looking
    // up the same symbol multiple times for the same page
    private HashMap alleleSymbols = new HashMap();

    // logger for this class
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
        tagConversions = makeTagMap(cssAnchorClass);

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
    * @param noLink: (optional) does not generate an <a> tag if true
    * @assumes nothing
    * @effects notes parameter
    * @throws
    */
    public String convertNotes (String notes, char delimiter)
    {
    	return convertNotes(notes,delimiter,false);
    }
    public String convertNotes (String notes, char delimiter, boolean noLink)
    {
    	return convertNotes(notes,delimiter,noLink,false);
    }
    public String convertNotes (String notes, char delimiter,boolean noLink,boolean noSuperscript)
    {
        // ensure the notes aren't null
        if (notes == null) {return "  ";}
        
        String      parmStr             = "";
        String      convertedTag        = "";
        String      matchStr            = "";
        Iterator    matchStrIter;
        Set         matchStrSet;

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

                // Replace the tag with the new string
                convertedTag = convertTag(parmStr,
                                delimiter,
                                (String)tagConversions.get(matchStr),
                                noLink,noSuperscript);
                notes = m.replaceFirst(convertedTag);

                // reset the matcher for the next search
                m = p.matcher(notes);
            }
        }

        // update for wild type
        notes = notes.replace("<+>", "<sup>+</sup>");

        return notes;
    }

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
        String fewiURL          = ContextLoader.getConfigBean().getProperty("FEWI_URL");
        String javawiURL        = ContextLoader.getConfigBean().getProperty("JAVAWI_URL");
        String pywiURL          = ContextLoader.getConfigBean().getProperty("WI_URL");
        String mgihomeURL       = ContextLoader.getConfigBean().getProperty("MGIHOME_URL");
        String ipURL            = ContextLoader.getExternalUrls().getProperty("InterPro");
        String ecURL            = ContextLoader.getExternalUrls().getProperty("EC");
        String emblURL          = ContextLoader.getExternalUrls().getProperty("EMBL");
        String spURL            = ContextLoader.getExternalUrls().getProperty("SWISS_PROT");
        String ncbiqURL         = ContextLoader.getExternalUrls().getProperty("NCBIQuery");
        String ncbipqURL        = ContextLoader.getExternalUrls().getProperty("NCBIProteinQuery");
        String ncbinqURL        = ContextLoader.getExternalUrls().getProperty("NCBINucleotideQuery");
        String jbcURL           = ContextLoader.getExternalUrls().getProperty("JBiolChem");
        String jlrURL           = ContextLoader.getExternalUrls().getProperty("JLipidRes");
        String dxdoiURL         = ContextLoader.getExternalUrls().getProperty("DXDOI");
        String pantherURL       = ContextLoader.getExternalUrls().getProperty("PTHR");
        
        // Pre-build all anchor replacement strings for each known tag

        ///////////////////
        // fewi links //
        ///////////////////

        // Marker
        tags.put("\\\\Marker\\((.*?[|].*?[|].*?)\\)",
            String.format("<A class=\"%s\" HREF=\"%smarker/%s\" %s>%s</A>",
            cssAnchorClass, fewiURL, "%s", "%s", "%s"));

        // Sequence
        tags.put("\\\\Sequence\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%ssequence/%s\" %s>%s</A>",
            cssAnchorClass, fewiURL, "%s", "%s", "%s"));

        ///////////////
        // python wi //
        ///////////////

        // Accession
        tags.put("\\\\Acc\\((.*?[|].*?[|].*?)\\)",
            String.format("<A class=\"%s\" HREF=\"%saccession/%s\" %s>%s</A>",
            cssAnchorClass, fewiURL, "%s", "%s", "%s"));

        // Allele
        tags.put("\\\\Allele\\((.*?[|].*?[|].*?)\\)",
            String.format("<A style='white-space: normal; 'class=\"%s\" HREF=\"%sWIFetch?page=alleleDetail&id=%s\" %s>%s</A>",
            cssAnchorClass, javawiURL, "%s", "%s", "%s"));

        // AMA
        tags.put("\\\\AMA\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%sama/%s\" %s>%s</A>",
            cssAnchorClass, fewiURL, "%s", "%s", "%s"));

        // GO
        tags.put("\\\\GO\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%ssearches/GO.cgi?id=%s\" %s>%s</A>",
            cssAnchorClass, pywiURL, "%s", "%s", "%s"));

        // Reference
        tags.put("\\\\Ref\\((.*?[|].*?[|].*?)\\)",
            String.format ("<A class=\"%s\" HREF=\"%sreference/%s\" %s>%s</A>",
            cssAnchorClass, fewiURL, "%s", "%s", "%s"));

        // Elsevier (might be a temp solution...python wi renders this tag differently)
        tags.put("\\\\Elsevier\\((.*?[|].*?[|].*?)\\)",
            String.format (" in <A class=\"%s\" HREF=\"%saccession/%s\" %s>%s</A>",
            cssAnchorClass, fewiURL, "%s", "%s", "%s"));

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
        
        // Panther Classification System
        String pantherRepStr = String.format("<A class=\"%s\" HREF=\"%s\"", cssAnchorClass, pantherURL.replaceAll("@@@@","%s"));
        pantherRepStr = pantherRepStr.concat("%s>%s</A>");
        tags.put("\\\\PANTHER\\((.*?[|].*?[|].*?)\\)",pantherRepStr);

        return tags;
    }

    /* -------------------------------------------------------------------- */

    /** expands a note tag to required string
    * @param string; parms - parameters of the found tag
    * @param char; delimiter - character used to delimit parms
    * @param string; replaceStr - pre-build replacement string for this tag
    * @param boolean; noLink - (optional) does not generate <a> tag if true
    * @assumes nothing
    * @effects nothing
    * @throws
    */
    private String convertTag (String parms, char delimiter, String replaceStr)
    {
    	return convertTag(parms,delimiter,replaceStr,false);
    }
    private String convertTag (String parms,char delimiter, String replaceStr,boolean noLink)
    {
    	return convertTag(parms,delimiter,replaceStr,noLink,false);
    }
    private String convertTag (String parms, char delimiter, String replaceStr,boolean noLink,boolean noSuperscript)
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

        // Markup superscripts if any are found
        if(!noSuperscript)
        {
        	display = superscript(display);
        }
        // Apply parms to replacement string
        convertedTag = String.format(replaceStr, id, target, display);

        //HACK: escape route for times when stake-holders don't want to display a link.
        if(noLink) return display;
        
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
