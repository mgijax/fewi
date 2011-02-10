package org.jax.mgi.fewi.util;

import java.util.*;

import org.jax.mgi.fewi.util.DBConstants;

import mgi.frontend.datamodel.Sequence;
import mgi.frontend.datamodel.SequenceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* provides static methods to help with formatting of JSP pages
*/
public class FormatHelper
{

    // logger for the class
    private static Logger logger = LoggerFactory.getLogger(FormatHelper.class);

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
        if (startPos == -1)    { return s; }

        int stopPos = s.indexOf(stop);
        if (stopPos == -1) { return s; }

        int startLen = start.length();    // how many chars to cut out for start
        int stopLen = stop.length();    // how many chars to cut out for stop
        int sectionStart = 0;        // position of char starting section

        StringBuffer sb = new StringBuffer();

        while ((startPos != -1) && (stopPos != -1))    {
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
          logger.debug("seqForwardValue -> " + seqForwardValue.toString());
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
          logger.debug("seqForwardValue -> " + seqForwardValue.toString());
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


} // end of class FormatHelper

