package mgi.frontend.util;


public class QueryUtil {

    public static String addAndClause(String qs, String clause) {

        String outString = "";

        if (!qs.equals("")) {
            outString = qs + " AND " + clause;
        }
        else {
            outString = clause;
        }

        return outString;
    }

    public static String addORClause(String qs, String clause) {

        String outString = "";

        if (!qs.equals("")) {
            outString = qs + " OR " + clause;
        }
        else {
            outString = clause;
        }

        return outString;
    }

    public static String addHighlight(String highlights, String value) {

        if (! highlights.equals("")) {
            highlights = highlights + ", " + value;
        }
        else {
            highlights = value;
        }

        return highlights;
    }

}