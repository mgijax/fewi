package org.jax.mgi.fewi.util;

import java.util.*;


/**
* provides static methods to help with formatting of JSP pages
*/
public class FormatHelper
{

    /** convert 'verbatimString' to its HTML equivalent
    * @param verbatimString string of data in verbatim format (where what
    *        should display on the web is exactly what is typed, so HTML-relevant
    *        characters must be escaped)
    * @return String
    * @notes Currently, we only translate '<', '>', and '&' to their HTML
    *        equivalents.  ("&lt;", "&gt;", and "&amp;")
    */
    public static String formatVerbatim (String verbatimString)
    {
        if (verbatimString == null) { return null;}

        // This could all be coded in three lines using String.replaceAll(),
        // but that would involve multiple string traversals under the hood,
        // along with regular expression handling.  Coding our own loop to
        // traverse the string once should be faster.

        StringBuffer sb = new StringBuffer();
        int vsLength = verbatimString.length();
        char c;

        for (int i = 0; i < vsLength; i++){

            c = verbatimString.charAt (i);

            if (c == '<'){
                sb.append ("&lt;");
            }
            else if (c == '>'){
                sb.append ("&gt;");
            }
            else if (c == '&'){
                sb.append ("&amp;");
            }
            else{
                sb.append (c);
            }
        }
        return sb.toString();
    }

    /** convert all 'start' and 'stop' pair in 's' to be HTML
     *    superscript tags.
     * @param s the source String
     * @param start the String which indicates the position for the HTML
     *    superscript start tag "<SUP>"
     * @param stop the String which indicates the position for the HTML
     *    superscript stop tag "</SUP>"
     * @return String as 's', but with the noted replacement made.  returns
     *    null if 's' is null.  returns 's' if either 'start' or 'stop' is
     *    null.
     * @assumes nothing
     * @effects nothing
     * @throws nothing
     */
	public static String superscript (String s, String start, String stop) {
		// if any of the input parameters are null, just bail out
		if ((s == null) || (start == null) || (stop == null)) { return s; }

		// Otherwise, find the first instance of 'start' and 'stop' in 's'.
		// If either does not appear, then short-circuit and just return 's'
		// as-is.

		int startPos = s.indexOf(start);
		if (startPos == -1)	{ return s; }

		int stopPos = s.indexOf(stop);
		if (stopPos == -1) { return s; }

		int startLen = start.length();	// how many chars to cut out for start
		int stopLen = stop.length();	// how many chars to cut out for stop
		int sectionStart = 0;		// position of char starting section

		StringBuffer sb = new StringBuffer();

		while ((startPos != -1) && (stopPos != -1))	{
			sb.append (s.substring(sectionStart, startPos));
			sb.append ("<SUP>");
			sb.append (s.substring(startPos + startLen, stopPos));
			sb.append ("</SUP>");

			sectionStart = stopPos + stopLen;
			startPos = s.indexOf(start, sectionStart);
			stopPos = s.indexOf(stop, sectionStart);
		}
		sb.append (s.substring(sectionStart));

		return sb.toString();
	} 

	/** convenience wrapper over superscript(s, "<", ">")
	 */
	public static String superscript (String s) {
		return superscript(s, "<", ">");
	}
	
	/** returns the correct plural/singular form of the given 'singular'
     * string, based on the given 'count'.
     * @assumes that we make 'singular' plural by appending an 's'
     */
	public static String plural (int count, String singular) {
		return plural (count, singular, singular + "s");
	}

    /** returns value of 'singular' when count is 1, or value of 'plural'
     * when the count is 0 or more than 1
     */
	public static String plural (int count, String singular, String plural) {
		if (count == 1) {
			return singular;
		}
		return plural;
	}

} // end of class FormatHelper

