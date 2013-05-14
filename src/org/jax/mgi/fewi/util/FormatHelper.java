package org.jax.mgi.fewi.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.jax.mgi.fewi.util.DBConstants;
import org.jax.org.mgi.shr.fe.util.TextFormat;

import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceLocation;
import mgi.frontend.datamodel.util.DatamodelUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

/**
* provides static methods to help with formatting of JSP pages
*/
public class FormatHelper
{

    // logger for the class
    private static Logger logger = LoggerFactory.getLogger(FormatHelper.class);

    /** convert 'verbatimString' to its HTML equivalent
    * @param verbatimString string of data in verbatim format (where what
    *        should display on the web is exactly what is typed, so
    *        HTML-relevant characters must be escaped)
    * @return String
    */
    public static String formatVerbatim (String verbatimString)
    {
        if (verbatimString == null) { return null;}

        // This could all be coded in three lines using String.replaceAll(),
        // but that would involve multiple string traversals under the hood,
        // along with regular expression handling.  Coding our own loop to
        // traverse the string once should be faster.


//        StringBuffer sb = new StringBuffer();
//        int vsLength = verbatimString.length();
//        char c;
//
//        // loop over each character
//        for (int i = 0; i < vsLength; i++){
//
//            c = verbatimString.charAt (i);
//
//            // translate the relevant chars
//            if (c == '<'){
//                sb.append ("&lt;");
//            }
//            else if (c == '>'){
//                sb.append ("&gt;");
//            }
//            else if (c == '&'){
//                sb.append ("&amp;");
//            }
//            else{
//                sb.append (c);
//            }
//        }
//        return sb.toString();

        // Or you could do this in one (more robust) line, because string manipulation is not where our performance is lost
        // kstone
        return HtmlUtils.htmlEscape(verbatimString);
    }


    /** convert newline characters in a string to an html br markup.
     * @param str The string that needs newlines coverted to html line breaks
     * @return the original string with all newline characters converted to
     *         html line breaks.
     * @assumes this assumes that it is a unix new line '\n' that is being
     *          converted.  Also assumes that you don't want trailing newlines,
     *          and these are trimmed off.
     * @effects nothing
     * @throws nothing
     */
    public static String newline2HTMLBR(String str)
    {
        // In many cases there was trailing whitespace that had newlines in it.
        //  I'm trimming them off now.
        String newStr = "";
        if (str != null) {
            newStr = str.trim();
            newStr = newStr.replaceAll("\\n","<br>");
        }
        return newStr;
    }

    public static String newline2Comma(String str)
    {
    	 String newStr = "";
         if (str != null) {
             newStr = str.trim();
             newStr = newStr.replaceAll("\\n",",");
         }
         return newStr;
    }

    // specify any kind of text to replace the newlines, if the above two methods don't meet requirements
    public static String replaceNewline(String str,String replacement)
    {
   	 String newStr = "";
     if (str != null) {
         newStr = str.trim();
         newStr = newStr.replaceAll("\\n",replacement);
     }
     return newStr;
    }

    /** convert all 'start' and 'stop' pair in 's' to be HTML
     *    superscript tags.
     * @param s the source String
     * @param start the String which indicates the position for the HTML
     *    superscript start tag "<sup>"
     * @param stop the String which indicates the position for the HTML
     *    superscript stop tag "</sup>"
     * @return String as 's', but with the noted replacement made.  returns
     *    null if 's' is null.  returns 's' if either 'start' or 'stop' is
     *    null.
     * @assumes nothing
     * @effects nothing
     * @throws nothing
     */
    public static String superscript (String s, String start, String stop) 
    {
        return TextFormat.superscript(s,start,stop);
    }

    /** convenience wrapper over superscript(s, "<", ">"), which is the common use case
     */
    public static String superscript (String s) 
    {
        return TextFormat.superscript(s);
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


    /**
     * Init cap all words in a given string
     */
    public static String initCap(String in) {
        if (in == null || in.length() == 0)
            return new String("");

        boolean capitalize = true;
        char[] data = in.toCharArray();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == ' ' || Character.isWhitespace(data[i]))
                capitalize = true;
            else if (capitalize) {
                data[i] = Character.toUpperCase(data[i]);
                capitalize = false;
            } else
                data[i] = Character.toLowerCase(data[i]);
        }
        return new String(data);
    }


    /**
     * for a given collection, create a comma delimited string
     */
    public static String commaDelimit (Collection c)
    {
        String commaDelimString = new String("");

        for (Iterator i = c.iterator(); i.hasNext(); ) {
            String nextValue = (String)i.next();

            if (nextValue != null) {
                if (!commaDelimString.equals("")) {
                        commaDelimString = commaDelimString + ", ";
                }

            commaDelimString = commaDelimString + nextValue;
            }
        }

        return commaDelimString;
    }



    /** returns value used to forward a sequence to either the sequence
     * retrieval too, or mouse blast select-a-sequence report
     */
    public static String getSeqForwardValue (Sequence seq)
    {
        // buffer to collect/build value
        StringBuffer seqForwardValue = new StringBuffer();

        // sequence info
        String provider = getSeqProviderForward(seq);
        String accID = seq.getPrimaryID();

        //coords
        List<SequenceLocation> locList = seq.getLocations();
        if (provider.equals("mousegenome") && !locList.isEmpty()) {

          // first location is the primary loc to be used
          SequenceLocation seqLoc = locList.get(0);

          seqForwardValue.append (provider);
          seqForwardValue.append ("!");
          seqForwardValue.append (seq.getPrimaryID());
          seqForwardValue.append ("!");
          seqForwardValue.append (seqLoc.getChromosome());
          seqForwardValue.append ("!");
          seqForwardValue.append (String.valueOf(seqLoc.getStartCoordinate().intValue()));
          seqForwardValue.append ("!");
          seqForwardValue.append (String.valueOf(seqLoc.getEndCoordinate().intValue()));
          seqForwardValue.append ("!");
          seqForwardValue.append ("+");
          seqForwardValue.append ("!"); // offset may be appended later.
        }
        else {
          seqForwardValue.append (provider);
          seqForwardValue.append ("!");
          seqForwardValue.append (seq.getPrimaryID());
          seqForwardValue.append ("!");
          seqForwardValue.append ("!");
          seqForwardValue.append ("!");
          seqForwardValue.append ("!");
          seqForwardValue.append ("!"); // offset may be appended later.
       }

        return seqForwardValue.toString();
    }


    public static String getSeqProviderForward(Sequence seq)
    {
        // the primary key identifying the logical database
        String seqProvider = seq.getProvider();
        String providerForward = "";

        if (seqProvider.startsWith(DBConstants.PROVIDER_SEQUENCEDB)) {
            providerForward = "genbank";
        }else if (seqProvider.equals(DBConstants.PROVIDER_SWISSPROT)) {
            providerForward = "swissprot";
        }else if (seqProvider.equals(DBConstants.PROVIDER_TREMBL)) {
            providerForward = "trembl";
        }else if (seqProvider.equals(DBConstants.PROVIDER_REFSEQ)) {
            providerForward = "refseq";
        }else if (seqProvider.equals(DBConstants.PROVIDER_DOTS)) {
            providerForward = "dotsm";
        }else if (seqProvider.equals(DBConstants.PROVIDER_DFCI)) {
            providerForward = "dfcimgi";
        }else if (seqProvider.equals(DBConstants.PROVIDER_NIA)) {
            providerForward = "niamgi";
        }else if (seqProvider.equals(DBConstants.PROVIDER_VEGAPROTEIN)) {
            providerForward = "vega_mus_prot";
        }else if (seqProvider.equals(DBConstants.PROVIDER_VEGATRANSCRIPT)) {
            providerForward = "vega_mus_cdna";
        }else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLPROTEIN)) {
            providerForward = "ensembl_mus_prot";
        }else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLTRANSCRIPT)) {
            providerForward = "ensembl_mus_cdna";
        }else if (seqProvider.equals(DBConstants.PROVIDER_NCBI) ||
          seqProvider.equals(DBConstants.PROVIDER_ENSEMBL) ||
          seqProvider.equals(DBConstants.PROVIDER_VEGA)) {
            providerForward = "mousegenome";
        }

        return providerForward;
    }

    /** examine s, and upon finding an <a href> tag, set its target to be
     * that given in the parameter.
     */
    public static String setTarget (String s, String target) {
        StringBuffer t = new StringBuffer();
        String sLower = s.toLowerCase();

        int ltPos = sLower.indexOf("<a href");
        int gtPos = sLower.indexOf(">", ltPos);
        int targetPos = sLower.indexOf("target=");

        // Did we find a proper <a href...> set of angle brackets?  If not, bail.
        if ((ltPos < 0) || (gtPos < ltPos)) {
            return s;
        }

        // if we don't already have a target, then we can just insert one
        if ((targetPos < 0) || (targetPos > gtPos)) {
            t.append(s.substring(0, gtPos));
            t.append(" target='" + target + "'");
            t.append(s.substring(gtPos));
            return t.toString();
        }

        // otherwise, we need to modify an existing target
        t.append (s.substring(0, targetPos));
        t.append (s.substring(targetPos).replaceFirst(
            "[tT][Aa][rR][gG][eE][tT]= *['\"][^'\"]*['\"]",
            "target='" + target + "'"));
        return t.toString();
    }

    /** examine s, and upon finding an <a href> tag, set its target to be a
     * new window
     */
    public static String setNewWindow (String s) {
        return setTarget(s, "_new");
    }

    /**
     * formats location coordinates
     */
    public static String formatCoordinates(Double start, Double end)
    {
    	if(start==null && end == null) return "";
    	NumberFormat nf = new DecimalFormat("#0");
    	String s,e = "";
    	if(start!=null) s = nf.format(start);
    	if(end!=null) e = nf.format(end);
    	return nf.format(start)+"-"+nf.format(end);
    }

    public static String javascriptEncode(String s)
    {
    	return StringEscapeUtils.escapeJavaScript(s);
    }

    // try to keep this in sync with the DatamodelUtils version
    // it helps make consistent anchor links
    public static String makeCssSafe(String input)
    {
    	return DatamodelUtils.makeCssSafe(input);
    }
} // end of class FormatHelper

