/*
 * NotesTagConverter.java
 *
 * An implementation of a service to translate "note tags" into links and such.
 * Also converts allele superscript notation into HTML.
 *
 * Note tag conversion is needed in both the PWI and the FEWI.
 * This is the Java implementation used in the FEWI.
 *
 * See also the Javascript implementation used in the PWI:
 * (https://github.com/mgijax/pwi/blob/master/pwi/static/app/services/NoteTagConverterService.js)
 *
 * THE JAVA AND JAVASCRIPT IMPLEMENTATIONS SHOULD BE KEPT IN SYNC!
 * (We're sorry. Truly we are. But that's the way it is.)
 */

package org.jax.mgi.fewi.util;

// standard java
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.config.ContextLoader;

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
	// the Pattern objects are cached in this class variable to make for speedy conversions
	private static List<TagConversion> tagConversionList = null;

	private String cssAnchorClass = "";

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
		initTagConversions();
		this.cssAnchorClass=cssAnchorClass;
		//tagConversions = makeTagMap(cssAnchorClass);

		return;
	}

	/** constructor; instantiates and initializes a new NotesTagConverter.
	 * @param none
	 * @assumes DB and config access
	 * @effects nothing
	 * @throws IOException
	 */
	public NotesTagConverter () {
		initTagConversions();
		//tagConversions = makeTagMap(cssAnchorClass);
		return;
	}

	private void initTagConversions()
	{
		// init the list once and cache it
		if(tagConversionList == null)
		{
			tagConversionList = new ArrayList<TagConversion>();
			// Base URLs used in creating the replacement string patterns
			String fewiURL          = ContextLoader.getConfigBean().getProperty("FEWI_URL");
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
			tagConversionList.add(new TagConversion("\\\\Marker\\((.*?[|].*?[|].*?)\\)",
					String.format("<a class=\"%s\" href=\"%smarker/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			// Sequence
			tagConversionList.add(new TagConversion("\\\\Sequence\\((.*?[|].*?[|].*?)\\)",
					String.format ("<a class=\"%s\" href=\"%ssequence/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			///////////////
			// python wi //
			///////////////

			// Accession
			tagConversionList.add(new TagConversion("\\\\Acc\\((.*?[|].*?[|].*?)\\)",
					String.format("<a class=\"%s\" href=\"%saccession/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			// Allele
			tagConversionList.add(new TagConversion("\\\\Allele\\((.*?[|].*?[|].*?)\\)",
					String.format("<a style='white-space: normal; 'class=\"%s\" href=\"%sallele/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			// AMA
			tagConversionList.add(new TagConversion("\\\\AMA\\((.*?[|].*?[|].*?)\\)",
					String.format ("<a class=\"%s\" href=\"%sama/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));
			
			// EMAPA
			tagConversionList.add(new TagConversion("\\\\EMAPA\\((.*?[|].*?[|].*?)\\)",
					String.format ("<a class=\"%s\" href=\"%svocab/gxd/anatomy/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			// GO
			tagConversionList.add(new TagConversion("\\\\GO\\((.*?[|].*?[|].*?)\\)",
					String.format ("<a class=\"%s\" href=\"%svocab/gene_ontology/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			// Reference
			tagConversionList.add(new TagConversion("\\\\Ref\\((.*?[|].*?[|].*?)\\)",
					String.format ("<a class=\"%s\" href=\"%sreference/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));


			// Elsevier (might be a temp solution...python wi renders this tag differently)
			tagConversionList.add(new TagConversion("\\\\Elsevier\\((.*?[|].*?[|].*?)\\)",
					String.format (" in <a class=\"%s\" href=\"%saccession/%s\" %s>%s</a>",
							"%s", fewiURL, "%s", "%s", "%s")));

			/////////////
			// mgihome //
			/////////////

			// GoCurators
			tagConversionList.add(new TagConversion("\\\\GoCurators\\((.*?[|].*?[|].*?)\\)",
					String.format("<a class=\"%s\" href=\"%sGO/go_curators.shtml%s\" %s>%s</a>",
							"%s", mgihomeURL, "%s", "%s", "%s")));

			// GoRefGenome
			tagConversionList.add(new TagConversion("\\\\GoRefGenome\\((.*?[|].*?[|].*?)\\)",
					String.format("<a class=\"%s\" href=\"%sGO/reference_genome_project.shtml%s\" %s>%s</a>",
							"%s", mgihomeURL, "%s", "%s", "%s")));

			///////////////////
			// email aliases //
			///////////////////

			// GoCurators
			tagConversionList.add(new TagConversion("\\\\GoEmail\\((.*?[|].*?[|].*?)\\)",
					"<a class=\"%s\" href=\"mailto:mgi-go@informatics.jax.org%s\" %s>%s</a>"));


			///////////////
			// external
			///////////////

			// These are created in a three step process due to not being able
			// to place two '%s' together.  (e.g. foo%s%s currently errors)

			// InterPro
			String ipRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", ipURL.replaceAll("@@@@","%s"));
			ipRepStr = ipRepStr.concat(" %s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\InterPro\\((.*?[|].*?[|].*?)\\)",ipRepStr));

			// Enzyme Commission Number
			String ecRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", ecURL.replaceAll("@@@@","%s"));
			ecRepStr = ecRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\EC\\((.*?[|].*?[|].*?)\\)",ecRepStr));

			// EMBL
			String emblRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", emblURL.replaceAll("@@@@","%s"));
			emblRepStr = emblRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\EMBL\\((.*?[|].*?[|].*?)\\)",emblRepStr));

			// SwissProt
			String spRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", spURL.replaceAll("@@@@","%s"));
			spRepStr = spRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\SwissProt\\((.*?[|].*?[|].*?)\\)",spRepStr));

			// NCBI Query
			String ncbiqRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", ncbiqURL.replaceAll("@@@@","%s"));
			ncbiqRepStr = ncbiqRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\NCBIQuery\\((.*?[|].*?[|].*?)\\)",ncbiqRepStr));

			// NCBI Protein Query
			String ncbipqRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", ncbipqURL.replaceAll("@@@@","%s"));
			ncbipqRepStr = ncbipqRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\NCBIProteinQuery\\((.*?[|].*?[|].*?)\\)",ncbipqRepStr));

			// NCBI Nucleotide Query
			String ncbinqRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", ncbinqURL.replaceAll("@@@@","%s"));
			ncbinqRepStr = ncbinqRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\NCBINucleotideQuery\\((.*?[|].*?[|].*?)\\)",ncbinqRepStr));

			// Journal of Biological Chemistry
			String jbcRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", jbcURL.replaceAll("@@@@","%s"));
			jbcRepStr = jbcRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\JBiolChem\\((.*?[|].*?[|].*?)\\)",jbcRepStr));

			// Journal of Lipid Research
			String jlrRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", jlrURL.replaceAll("@@@@","%s"));
			jlrRepStr = jlrRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\JLipidRes\\((.*?[|].*?[|].*?)\\)",jlrRepStr));

			// DXDOI
			String dxdoiStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", dxdoiURL.replaceAll("@@@@","%s"));
			dxdoiStr = dxdoiStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\DXDOI\\((.*?[|].*?[|].*?)\\)",dxdoiStr));

			// Panther Classification System
			String pantherRepStr = String.format("<a class=\"%s\" href=\"%s\"", "%s", pantherURL.replaceAll("@@@@","%s"));
			pantherRepStr = pantherRepStr.concat("%s>%s</a>");
			tagConversionList.add(new TagConversion("\\\\PANTHER\\((.*?[|].*?[|].*?)\\)",pantherRepStr));


			// Generic Link
			tagConversionList.add(new TagConversion("\\\\Link\\((.*?[|].*?[|].*?)\\)",
					String.format ("<a class=\"%s\" target=\"_blank\" href=\"%s\" %s>%s</a>",
							"%s", "%s", "%s", "%s")));
		}
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

		// for each tag we are lookin for...
		//while (matchStrIter.hasNext())
		for(TagConversion tc : tagConversionList)
		{
			Matcher m   = tc.pattern.matcher(notes);

			// for each tag in the notes text...
			while(m.find()){
				parmStr= m.group(1);

				// Replace the tag with the new string
				convertedTag = convertTag(parmStr,
						delimiter,
						tc.formatString,
						noLink,noSuperscript);
				notes = m.replaceFirst(convertedTag);

				// reset the matcher for the next search
				m = tc.pattern.matcher(notes);
			}
		}

		// update for wild type
		if(!noSuperscript) notes = notes.replace("<+>", "<sup>+</sup>");

		return notes;
	}
	
	public String useNewWindowWithoutPopup(String inputString) {
		Pattern p = Pattern.compile ("[hH][rR][eE][fF] ?= ?(\"[^\"]*\")");
		String s = inputString;
		Matcher m = p.matcher(s);
		String url;

		while (m.find()) {
			url = m.group(1);
			s = m.replaceFirst ("HHRREEFF=" + url + " TARGET='_blank'");
			m = p.matcher(s);
		}
		return s.replaceAll ("HHRREEFF", "HREF");
	}

	public String useNewWindows (String inputString) {
		Pattern p = Pattern.compile ("[hH][rR][eE][fF] ?= ?(\"[^\"]*\")");
		String s = inputString;
		Matcher m = p.matcher(s);
		String url;

		while (m.find()) {
			url = m.group(1);
			s = m.replaceFirst ("HHRREEFF=" + url + " onClick='newWindow(" + url + "); return false;' TARGET='_blank'");
			m = p.matcher(s);
		}
		return s.replaceAll ("HHRREEFF", "HREF");
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
	//    private String convertTag (String parms, char delimiter, String replaceStr)
	//    {
	//    	return convertTag(parms,delimiter,replaceStr,false);
	//    }
	//    private String convertTag (String parms,char delimiter, String replaceStr,boolean noLink)
	//    {
	//    	return convertTag(parms,delimiter,replaceStr,noLink,false);
	//    }
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
				target = "TARGET=\"_blank\"";
			}
		}

		// Markup superscripts if any are found
		if(!noSuperscript)
		{
			display = superscript(display);
		}

                //HACK: need to replace this in link, but not in display 
		id = id.replace("PR:", "PR_"); 

		// Apply parms to replacement string
		convertedTag = String.format(replaceStr,this.cssAnchorClass, id, target, display);

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
		if (mGT.find() && mLT.find()){
			displayParm = displayParm.replaceAll("<", "<sup");
			displayParm = displayParm.replaceAll(">", "</sup>");
			displayParm = displayParm.replaceAll("<sup", "<sup>");
		}

		return displayParm;
	}

	public String convertToHTML(String in) {
		String convert = convertNotes(in, '|').replace("\n", "<br/>").replace("</sup>", "</span>").replace("<sup>", "<span class='superscript'>");
		return convert;
	}

	public String convertToHTMLNewWindowWithoutPopup(String in) {
		String convert = convertToHTML(in);
		String ret = useNewWindowWithoutPopup(convert);
		return ret;
	}

	// helper class to group tag conversions (and pre-compile the regular expressions)
	private class TagConversion
	{
		public Pattern pattern;
		public String formatString;

		public TagConversion(String matchStr,String formatString)
		{
			this.pattern = Pattern.compile(matchStr);
			this.formatString = formatString;
		}
	}

}
