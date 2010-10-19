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

} // end of class FormatHelper

