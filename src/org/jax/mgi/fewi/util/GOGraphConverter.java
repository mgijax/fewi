package org.jax.mgi.fewi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jax.mgi.fewi.config.ContextLoader;

/** The GOGraphConverter class contains methods which encapsulate knowledge of
*    how to convert special markups in GO Graph HTML files.
*/
public class GOGraphConverter
{
	public static Pattern GO_REGEX_PATTERN;
	static
	{
		// group 1 == partial tag name, group 2 == ID
        String regex = "\\\\Url([^(]+)\\(([^)]*)\\)";

		// compiled version of 'regex'
        GO_REGEX_PATTERN = Pattern.compile (regex);
	}
	
    /* -------------------------------------------------------------------- */

    /////////////////////
    // instance variables
    /////////////////////

    /* -------------------------------------------------------------------- */
	public String pythonWI;
	public String fewi;
	public String mgiHome;
	public String imageUrl;
	
	public GOGraphConverter()
	{
		// base URL for the Python WI (its www directory)
		pythonWI = ContextLoader.getConfigBean().getProperty("WI_URL");
	
		// base URL for the Java WI (including 'servlet', above WIFetch)
		fewi = ContextLoader.getConfigBean().getProperty("FEWI_URL");
	
		// base URL for MGI Home (its www directory)
		mgiHome = ContextLoader.getConfigBean().getProperty("MGIHOME_URL");
	
		// base URL for GO graphs
	    imageUrl = ContextLoader.getConfigBean().getProperty("GO_GRAPH_URL");
	}

    /** go through 'lines' and convert all \Url*() mockups to their intended
    *    URLs
    * @param lines data lines to convert (from an HTML or SVG file)
    * @return modified version of 'lines'
    * @assumes that each \Url*() tag is complete within a given line; that is,
    *    we do not worry about a tag being split across multiple lines
    * @effects nothing
    * @throws nothing
    */
    public String translateMarkups (String lines)
    {
		// matcher for applying 're' to each string
		Matcher m = null;
	
		// did matcher 'm' find 'regex'?
		boolean found = false;
	
		// group 1 from 'regex'; the name of the tag minus the \Url prefix
		String tagType = null;
	
		// group 2 from 'regex'; the ID contained within the parentheses
		String id = null;
	
		// the last position in the current line which has been added to 'sb'
		int lastPos = 0;
	
		// updated version of the current line (built piece-by-piece as we
		// do find & replace on tags)
		StringBuffer sb = new StringBuffer();
	
		// replacement for the current tag, to be added to 'sb' in its place
		String rep = null;

	
		/* Our approach to the conversion of lines is...
		*    working in the String 'lines'...
		*	find the next \Url*() tag by using a matcher 'm'
		*	as long as we found a tag...
		*	   add to 'sb' characters between the last tag and this one
		*	   find & add to 'sb' the replacement for this tag type & id
		*	   look for the next tag in this line
		*	add to 'sb' any characters remaining after the last tag
		*	return 'sb' as the String for output
		*/
		m = GO_REGEX_PATTERN.matcher(lines);
		found = m.find();
	
		while (found) {
		    sb.append (lines.substring (lastPos, m.start()));
	
		    tagType = m.group(1).trim();
		    id = m.group(2).trim();
	
		    // All these tag strings are really prefaced by a "\Url" which is
		    // stripped off by the regular expression.  Most require use of
		    // the 'id' variable which is filled in from the regular
		    // expression.
	
		    // GO term detail page
		    if ("GOTerm".equals (tagType)) {
			rep = fewi + "vocab/gene_ontology/" + id;
	
		    // marker detail page
		    } else if ("Marker".equals (tagType)) {
			rep = fewi + "marker/" + id;
	
		    // GO annotations for a marker page
		    } else if ("MarkerGO".equals (tagType)) {
			rep = fewi + "go/marker/" + id;
	
		    // reference detail page
		    } else if ("Reference".equals (tagType)) {
			rep = fewi + "reference/" + id;
	
		    // PNG image for a marker's GO annotations
		    } else if ("GOMarkerImage".equals (tagType)) {
			rep = imageUrl + "marker/" + id.replace(':', '_') + ".png";
	
		    // PNG image for a homology cluster's DAG-specific GO annotations
		    // (assumes ID includes a one-character DAG identifier - P, C, F -
		    // then the HomoloGene ID, then the filename suffix)
		    } else if ("GOOrthologyImage".equals (tagType)) {
			rep = imageUrl + "orthology/" + id;
	
		    // homology detail page for a marker
		    } else if ("MarkerOrtho".equals (tagType)) {
			rep = fewi + "homology/marker/" + id;
	
		    // current table of GO annotations for a marker
		    } else if ("MarkerGOTable".equals (tagType)) {
			rep = fewi + "go/marker/" + id + "#tabular";
	
		    // base URL for mgihome product
		    } else if ("MGIHome".equals (tagType)) {
			rep = pythonWI;
	
		    // main help doc within mgihome product
		    } else if ("MGIHelp".equals (tagType)) {
			rep = mgiHome + "help/help.shtml";
	
		    } else {
			// unknown tag; keep as-is and do no replacement
			rep = m.group(0);
		    }
	
		    sb.append (rep);
		    lastPos = m.end();
		    found = m.find (lastPos);
	
		} // while loop over tags in current line
	
		// add on any leftover characters after the last tag found
		if (lastPos < lines.length()) {
		    sb.append (lines.substring (lastPos));
		}
		return sb.toString();
    }
}
