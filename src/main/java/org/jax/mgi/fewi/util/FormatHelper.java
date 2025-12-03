package org.jax.mgi.fewi.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jax.mgi.fe.datamodel.QueryFormOption;
import org.jax.mgi.fe.datamodel.Sequence;
import org.jax.mgi.fe.datamodel.SequenceLocation;
import org.jax.mgi.fe.datamodel.VocabTerm;
import org.jax.mgi.fe.datamodel.util.DatamodelUtils;
import org.jax.mgi.shr.fe.util.TextFormat;
import org.jax.mgi.shr.jsonmodel.GenomicLocation;
import org.jax.mgi.shr.jsonmodel.SimpleSequence;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriUtils;

/**
 * provides static methods to help with formatting of JSP pages
 */
public class FormatHelper {

	// logger for the class
	private static Logger logger = LoggerFactory.getLogger(FormatHelper.class);

	// maps from Theiler stage (Integer) to corresponding DPC String
	private static HashMap<Integer, String> tsToDPC = null;

	// maps from Theiler stage (Integer) to stage description
	private static HashMap<Integer, String> tsToDescription = null;

	// populate tsToDPC
	private static void populateTsToDPC() {
		tsToDPC = new HashMap<Integer, String>();

		tsToDPC.put(0, "Any developmental stage");
		tsToDPC.put(1, "(0.0-2.5 dpc)");
		tsToDPC.put(2, "(1.0-2.5 dpc)");
		tsToDPC.put(3, "(1.0-3.5 dpc)");
		tsToDPC.put(4, "(2.0-4.0 dpc)");
		tsToDPC.put(5, "(3.0-5.5 dpc)");
		tsToDPC.put(6, "(4.0-5.5 dpc)");
		tsToDPC.put(7, "(4.5-6.0 dpc)");
		tsToDPC.put(8, "(5.0-6.5 dpc)");
		tsToDPC.put(9, "(6.25-7.25 dpc)");
		tsToDPC.put(10, "(6.5-7.75 dpc)");
		tsToDPC.put(11, "(7.25-8.0 dpc)");
		tsToDPC.put(12, "(7.5-8.75 dpc)");
		tsToDPC.put(13, "(8.0-9.25 dpc)");
		tsToDPC.put(14, "(8.5-9.75 dpc)");
		tsToDPC.put(15, "(9.0-10.25 dpc)");
		tsToDPC.put(16, "(9.5-10.75 dpc)");
		tsToDPC.put(17, "(10.0-11.25 dpc)");
		tsToDPC.put(18, "(10.5-11.25 dpc)");
		tsToDPC.put(19, "(11.0-12.25 dpc)");
		tsToDPC.put(20, "(11.5-13.0 dpc)");
		tsToDPC.put(21, "(12.5-14.0 dpc)");
		tsToDPC.put(22, "(13.5-15.0 dpc)");
		tsToDPC.put(23, "(15 dpc)");
		tsToDPC.put(24, "(16 dpc)");
		tsToDPC.put(25, "(17 dpc)");
		tsToDPC.put(26, "(18 dpc)");
		tsToDPC.put(27, "(newborn)");
		tsToDPC.put(28, "(postnatal)");
		return;
	}

	public static String getDPC(int theilerStage) {
		if (tsToDPC == null) {
			populateTsToDPC();
		}

		if (tsToDPC.containsKey(theilerStage)) {
			return tsToDPC.get(theilerStage);
		}
		return null;
	}

	private static void populateTsToDescription() {
		tsToDescription = new HashMap<Integer, String>();

		tsToDescription.put(1, "One cell stage");
		tsToDescription.put(2, "Beginning of cell division; 2-4 cells");
		tsToDescription.put(3, "Morula; 4-16 cells");
		tsToDescription.put(4, "Blastocyst (inner cell mass apparent); 16-40 cells");
		tsToDescription.put(5, "Blastocyst (zona free)");
		tsToDescription.put(6, "Implantation");
		tsToDescription.put(7, "Formation of egg cylinder");
		tsToDescription.put(8, "Differentiation of egg cylinder");
		tsToDescription.put(9, "Prestreak; early streak");
		tsToDescription.put(10, "Midstreak; late streak; allantoic bud first appears; amnion forms");
		tsToDescription.put(11, "Neural plate stage; elongated allantoic bud; early headfold; late headfold");
		tsToDescription.put(12, "1-7 somites");
		tsToDescription.put(13, "8-12 somites; turning of embryo");
		tsToDescription.put(14, "13-20 somites; formation and closure of anterior neuropore");
		tsToDescription.put(15, "21-29 somites; formation of posterior neuropore and forelimb bud");
		tsToDescription.put(16, "30-34 somites; closure of posterior neuropore; formation of hindlimb and tail bud");
		tsToDescription.put(17, "35-39 somites; deep indentation of lens vesicle");
		tsToDescription.put(18, "40-44 somites; closure of lens vesicle");
		tsToDescription.put(19, "45-47 somites; complete separation of lens vesicle");
		tsToDescription.put(20, "48-51 somites; earliest sign of handplate digits");
		tsToDescription.put(21, "52-55 somites; indentation of handplate");
		tsToDescription.put(22, "56-~60 somites; distal separation of handplate digits");
		tsToDescription.put(23, "Separation of footplate digits");
		tsToDescription.put(24, "Reposition of umbilical hernia");
		tsToDescription.put(25, "Digits joined together; skin wrinkled");
		tsToDescription.put(26, "Long whiskers");
		tsToDescription.put(27, "Newborn mouse");
		tsToDescription.put(28, "Postnatal development");
		return;
	}

	public static String getDescription(int theilerStage) {
		if (tsToDescription == null) {
			populateTsToDescription();
		}

		if (tsToDescription.containsKey(theilerStage)) {
			return tsToDescription.get(theilerStage);
		}
		return null;
	}

	/**
	 * convert 'verbatimString' to its HTML equivalent
	 * 
	 * @param verbatimString string of data in verbatim format (where what should
	 *                       display on the web is exactly what is typed, so
	 *                       HTML-relevant characters must be escaped)
	 * @return String
	 */
	public static String formatVerbatim(String verbatimString) {
		if (verbatimString == null) {
			return null;
		}

		return HtmlUtils.htmlEscape(verbatimString);
	}

	/**
	 * convert newline characters in a string to an html br markup.
	 * 
	 * @param str The string that needs newlines coverted to html line breaks
	 * @return the original string with all newline characters converted to html
	 *         line breaks.
	 * @assumes this assumes that it is a unix new line '\n' that is being
	 *          converted. Also assumes that you don't want trailing newlines, and
	 *          these are trimmed off.
	 * @effects nothing
	 * @throws nothing
	 */
	public static String newline2HTMLBR(String str) {
		// In many cases there was trailing whitespace that had newlines in it.
		// I'm trimming them off now.
		String newStr = "";
		if (str != null) {
			newStr = str.trim();
			newStr = newStr.replaceAll("\\n", "<br>");
		}
		return newStr;
	}

	public static String newline2Comma(String str) {
		String newStr = "";
		if (str != null) {
			newStr = str.trim();
			newStr = newStr.replaceAll("\\n", ",");
		}
		return newStr;
	}

	// specify any kind of text to replace the newlines, if the above two methods
	// don't meet requirements
	public static String replaceNewline(String str, String replacement) {
		String newStr = "";
		if (str != null) {
			newStr = str.trim();
			newStr = newStr.replaceAll("\\n", replacement);
		}
		return newStr;
	}

	/**
	 * convert all 'start' and 'stop' pair in 's' to be HTML superscript tags.
	 * 
	 * @param s     the source String
	 * @param start the String which indicates the position for the HTML superscript
	 *              start tag "<sup>"
	 * @param stop  the String which indicates the position for the HTML superscript
	 *              stop tag "</sup>"
	 * @return String as 's', but with the noted replacement made. returns null if
	 *         's' is null. returns 's' if either 'start' or 'stop' is null.
	 * @assumes nothing
	 * @effects nothing
	 * @throws nothing
	 */
	public static String superscript(String s, String start, String stop) {
		return TextFormat.superscript(s, start, stop);
	}

	/**
	 * convenience wrapper over superscript(s, "<", ">"), which is the common use
	 * case
	 */
	public static String superscript(String s) {
		return TextFormat.superscript(s);
	}

	/**
	 * take an allele combination string (with MGI markup for alleles) and render it
	 * as superscripted HTML, but without making the alleles into links
	 */
	public static String formatUnlinkedAlleles(String s) {
		Pattern p = Pattern.compile("\\\\Allele\\(.*?[|](.*?)[|].*?\\)");
		Matcher m = p.matcher(s);
		String t = s;

		while (m.find()) {
			t = m.replaceFirst(m.group(1));
			m = p.matcher(t);
		}
		t = t.replaceAll("\\n", "<br/>"); // convert newlines to line breaks, remove last one
		if (t.endsWith("<br/>")) {
			t = t.substring(0, t.length() - 5);
		}
		return t;
	}

	/**
	 * returns the correct plural/singular form of the given 'singular' string,
	 * based on the given 'count'.
	 * 
	 * @assumes that we make 'singular' plural by appending an 's'
	 */
	public static String plural(int count, String singular) {
		return plural(count, singular, singular + "s");
	}

	/**
	 * returns value of 'singular' when count is 1, or value of 'plural' when the
	 * count is 0 or more than 1
	 */
	public static String plural(int count, String singular, String plural) {
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
	public static String commaDelimit(Collection<String> collection) {
		return StringUtils.join(collection, ", ");
	}

	/**
	 * for a given collection, create a pipe delimited string
	 */
	public static String pipeDelimit(Collection<String> collection) {
		return StringUtils.join(collection, " | ");
	}

	/**
	 * returns value used to forward a sequence to either the sequence retrieval
	 * tool, or mouse blast select-a-sequence report
	 */
	public static String getSeqForwardValue(Sequence seq) {
		List<SequenceLocation> locList = seq.getLocations();
		String seqProvider = getSeqProviderForward(seq.getProvider());

		// if genomic location, include coordinates

		if ("mousegenome".equals(seqProvider) || "straingene".equals(seqProvider) ) {
			String strain = seq.getSources().get(0).getStrain();
			SequenceLocation loc = locList.get(0);
			return getSeqForwardValue(seq.getProvider(), strain, seq.getPrimaryID(), loc.getChromosome(), String.valueOf(loc.getStartCoordinate().intValue()), String.valueOf(loc.getEndCoordinate().intValue()), loc.getStrand());
		}

		// no genomic location, so use genbank ID if we have one, primary ID if not

		String seqID = seq.getPrimaryID();
		if (seq.getPreferredGenBankID() != null) {
			seqID = seq.getPreferredGenBankID().getAccID();
		}
		return getSeqForwardValue(seq.getProvider(), "", seqID, "", "", "", "");
	}

	/**
	 * returns value used to forward a sequence to either the sequence retrieval
	 * tool, or mouse blast select-a-sequence report
	 */
	public static String getSeqForwardValue(SimpleSequence seq) {
		// if genomic location, include coordinates
		String seqProvider = getSeqProviderForward(seq.getProvider());

		if ("mousegenome".equals(seqProvider) || "straingene".equals(seqProvider)) {
			GenomicLocation loc = seq.getLocation();
			return getSeqForwardValue(seq.getProvider(), seq.getStrain(), seq.getPrimaryID(), loc.getChromosome(), loc.getStartCoordinate(), loc.getEndCoordinate(), loc.getStrand());
		}

		// no genomic location, so use genbank ID if we have one, primary ID if not

		String seqID = seq.getPrimaryID();
		if (seq.getPreferredGenbankID() != null) {
			seqID = seq.getPreferredGenbankID();
		}
		return getSeqForwardValue(seq.getProvider(), "", seqID, "", "", "", "");
	}

	public static String getSeqForwardValue(String seqProvider, String strain, String seqID, String chromosome, String startCoord, String endCoord, String strand) {
		logger.info("getSeqForwardValue: " + seqProvider + "," + strain + "," + seqID + "," + chromosome + "," + startCoord + "," + endCoord + "," + strand);
		// buffer to collect/build value
		StringBuffer seqForwardValue = new StringBuffer();
		String provider = getSeqProviderForward(seqProvider);

		seqForwardValue.append(provider);
		seqForwardValue.append("!");
		if ("mousegenome".equals(provider) || "straingene".equals(provider)) {
			seqForwardValue.append(strain + ":");
		}
		seqForwardValue.append(seqID);
		seqForwardValue.append("!");
		seqForwardValue.append(chromosome);
		seqForwardValue.append("!");
		seqForwardValue.append(startCoord);
		seqForwardValue.append("!");
		seqForwardValue.append(endCoord);
		seqForwardValue.append("!");
		seqForwardValue.append(strand);
		seqForwardValue.append("!"); // offset may be appended later.

		return seqForwardValue.toString();
	}

	public static String getSeqProviderForward(String seqProvider) {
		// the primary key identifying the logical database
		String providerForward = "";

		if (seqProvider.startsWith(DBConstants.PROVIDER_SEQUENCEDB)) {
			providerForward = "genbank";
		} else if (seqProvider.equals(DBConstants.PROVIDER_SWISSPROT)) {
			providerForward = "swissprot";
		} else if (seqProvider.equals(DBConstants.PROVIDER_TREMBL)) {
			providerForward = "trembl";
		} else if (seqProvider.equals(DBConstants.PROVIDER_REFSEQ)) {
			providerForward = "refseq";
		} else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLPROTEIN)) {
			providerForward = "ensembl_mus_prot";
		} else if (seqProvider.equals(DBConstants.PROVIDER_ENSEMBLTRANSCRIPT)) {
			providerForward = "ensembl_mus_cdna";
		} else if (seqProvider.equals(DBConstants.PROVIDER_NCBI) || seqProvider.equals(DBConstants.PROVIDER_ENSEMBL) || seqProvider.equals(DBConstants.PROVIDER_ENSEMBLREG) || seqProvider.equals(DBConstants.PROVIDER_VISTA)) {
			providerForward = "mousegenome";
		} else if (seqProvider.endsWith("(MGP) Strain Gene Model") || (seqProvider.startsWith("MGI") && seqProvider.endsWith("Gene Model"))) {
			providerForward = "straingene";
		}

		return providerForward;
	}

	/**
	 * examine s, and upon finding an <a href> tag, set its target to be that given
	 * in the parameter.
	 */
	public static String setTarget(String s, String target) {
		StringBuffer t = new StringBuffer();
		String sLower = s.toLowerCase();

		int ltPos = sLower.indexOf("<a href");
		int gtPos = sLower.indexOf(">", ltPos);
		int targetPos = sLower.indexOf("target=");

		// Did we find a proper <a href...> set of angle brackets? If not, bail.
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
		t.append(s.substring(0, targetPos));
		t.append(s.substring(targetPos).replaceFirst("[tT][Aa][rR][gG][eE][tT]= *['\"][^'\"]*['\"]", "target='" + target + "'"));
		return t.toString();
	}

	/**
	 * examine s, and upon finding an <a href> tag, set its target to be a new
	 * window
	 */
	public static String setNewWindow(String s) {
		return setTarget(s, "_blank");
	}

	/**
	 * formats location coordinates
	 */
	public static String formatCoordinates(Double start, Double end) {
		if (start == null && end == null)
			return "";
		NumberFormat nf = new DecimalFormat("#0");
		if (start != null)
			nf.format(start);
		if (end != null)
			nf.format(end);
		return nf.format(start) + "-" + nf.format(end);
	}

	public static String javascriptEncode(String s) {
		return StringEscapeUtils.escapeJavaScript(s);
	}

	// try to keep this in sync with the DatamodelUtils version
	// it helps make consistent anchor links
	public static String makeCssSafe(String input) {
		return DatamodelUtils.makeCssSafe(input);
	}

	// returns http query string by reverse engineering HttpRequest
	public static String queryStringFromPost(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder("");
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String param = e.nextElement();
			sb.append(param).append("=").append(request.getParameter(param)).append("&");
		}
		String queryString = sb.toString();
		if (queryString.length() > 0)
			queryString = queryString.substring(0, sb.length() - 1);
		return queryString;
	}

	/*
	 * build a tree-like structure of HTML checkboxes for a list of query form
	 * options. (like the Feature Type vocabulary on the marker QF when the browser
	 * has javascript turned off)
	 */
	public static String buildHtmlTree(List<QueryFormOption> options) {
		StringBuffer sb = new StringBuffer();

		int prevIndentLevel = 1;
		int indentLevel = 1;

		for (QueryFormOption option : options) {
			if (option.getIndentLevel() != null) {
				indentLevel = option.getIndentLevel().intValue();
			}

			if (indentLevel < prevIndentLevel) {
				sb.append("&nbsp;<br/>");
			}

			for (int i = 0; i < indentLevel; i++) {
				sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			}

			sb.append("<input type='checkbox' name='mcv' value='");
			sb.append(option.getSubmitValue());
			sb.append("'/><span class='ygtvlabel' style='line-height: 1.5em'>");
			sb.append(option.getDisplayValue());
			if (option.getObjectCount() != null) {
				sb.append(" (");
				sb.append(String.format("%,d", option.getObjectCount()));
				sb.append(")");
			}
			sb.append("</span><br/>");

			prevIndentLevel = indentLevel;
		}

		return sb.toString();
	}

	public static String buildJsonTree(List<QueryFormOption> options) {
		StringBuffer sb = new StringBuffer();

		int prevIndentLevel = 1;
		int indentLevel = 1;
		boolean firstNode = true;

		// start the list of nodes
		sb.append("[");

		for (QueryFormOption option : options) {
			if (option.getIndentLevel() != null) {
				indentLevel = option.getIndentLevel().intValue();
			}

			if (firstNode) {
				sb.append("{");
				firstNode = false;

			} else if (indentLevel == prevIndentLevel) {
				// end one node, start a sibling node
				sb.append("},{");

			} else if (indentLevel > prevIndentLevel) {
				// cannot end the prior node, as we need to begin its list of
				// child nodes
				sb.append(",children:[{");

			} else { // (indentLevel < prevIndentLevel)
				// close the previously open node
				sb.append("}");

				// close one or more nodes previously left open for children
				for (int i = indentLevel; i < prevIndentLevel; i++) {
					sb.append("]}");
				}

				sb.append(",{");
			}

			// contents of the current node

			sb.append("type:\"text\",");
			sb.append("label:\"");
			sb.append(option.getDisplayValue());
			if (option.getObjectCount() != null) {
				sb.append(" (");
				sb.append(String.format("%,d", option.getObjectCount()));
				sb.append(")\"");
			}
			if (option.getShowExpanded() == 1) {
				sb.append(",expanded:");
				sb.append("true");
			}
			sb.append(",key:\"");
			sb.append(option.getSubmitValue());
			sb.append("\",head:\"");
			sb.append(option.getDisplayValue());
			sb.append("\",help:\"");
			sb.append(option.getHelpText().trim());
			sb.append("\"");

			prevIndentLevel = indentLevel;
		}

		// close final node
		sb.append("}");

		// close any nodes which had lists of children still open
		for (int i = 1; i < prevIndentLevel; i++) {
			sb.append("]}");
		}

		// close the list of nodes itself
		sb.append("]");

		return sb.toString();
	}

	/*
	 * build the default JSON tree for the given anatomy term (for the anatomy term
	 * detail page). This shows the term's default parent (open), siblings (closed),
	 * and following the trail of ancestors (closed) from the default parent up
	 * through its default parent (and so on) until reaching the root node. The JSON
	 * format we are generating is: [ { type:"text",
	 * label:"HTML string to display for the node", expanded:true or false,
	 * key:"database key for term", head:"minimal label",
	 * help:"help text for mouse-over", children: [ { other similar nodes for
	 * children, recursively } ] } ]
	 */
	public static String buildDefaultJsonTree(VocabTerm anatomyTerm) {
		TreeNode startNode = new TreeNode(anatomyTerm);
		startNode.setExpanded(true);
		// startNode.setHighlighted(true);
		startNode.setSelected(true);

		// add children of the starting node

		for (VocabTerm child : anatomyTerm.getChildren()) {
			startNode.addChild(new TreeNode(child));
		}

		// add immediate parent of the starting node

		VocabTerm parent = anatomyTerm.getDefaultParent();
		if (parent == null) {
			// startNode is a root (no parent), so just return it
			return "[" + startNode.getJson() + "]";
		}

		TreeNode parentNode = new TreeNode(parent);
		parentNode.setExpanded(true);
		parentNode.setIsOnDefaultPath(true);

		// add siblings of the starting node

		for (VocabTerm sibling : parent.getChildren()) {
			if (sibling.getTermKey() == anatomyTerm.getTermKey()) {
				parentNode.addChild(startNode);
			} else {
				parentNode.addChild(new TreeNode(sibling));
			}
		}

		// add other ancestors from the parent up to the root

		VocabTerm priorParent = parent.getDefaultParent();
		TreeNode priorParentNode = null;

		while (priorParent != null) {
			priorParentNode = new TreeNode(priorParent);
			priorParentNode.setIsOnDefaultPath(true);
			priorParentNode.setExpanded(true);

			// siblings of the ancestor node...

			for (VocabTerm otherChild : priorParent.getChildren()) {
				if (otherChild.getTermKey() == parent.getTermKey()) {
					priorParentNode.addChild(parentNode);
				} else {
					priorParentNode.addChild(new TreeNode(otherChild));
				}
			}

			parent = priorParent;
			parentNode = priorParentNode;

			priorParent = parent.getDefaultParent();
		}

		return "[" + parentNode.getJson() + "]";
	}

	public static String encodeQueryString(String query) {
		if (query == null) {
			return null;
		}
		try {
			return UriUtils.encodeQuery(query, "UTF-8").replaceAll("%E2%80%99", "%27").replaceAll("'", "%27");
			// Removed the single quote for %27 if this is not done all links will break due
			// to "javascript('var=peyer's')"
			// The %27 gets decoded correctly with the link to the other page
			// The %E2%80%99 is a right single quote Microsoft products convert single
			// quotes to this in all their products :-(
		} catch (UnsupportedEncodingException e) {
			logger.error("query encode failed", e);
			return query;
		}
	}

	/*
	 * look for all "\Allele(ID|symbol|)" tags in the input string and convert them
	 * to just be their corresponding allele symbols
	 */
	public static String stripAlleleTags(String s) {
		if (s == null) {
			return null;
		}

		Pattern p = Pattern.compile("\\\\Allele\\([^\\|]+\\|([^\\|]+)\\|\\)");
		String t = s.trim();
		Matcher m = p.matcher(t);

		while (m.find()) {
			t = t.replace(m.group(0), m.group(1));
			m = p.matcher(t);
		}
		return t;
	}

	/*
	 * find the length of the longest line in the input string (assumes lines are
	 * delimited by a newline character)
	 */
	public static int maxWidth(String s) {
		if (s == null) {
			return 0;
		}

		int prevPos = 0;
		int pos = s.indexOf("\n");
		int width = 0;

		while (pos >= 0) {
			width = Math.max(width, pos - prevPos);
			prevPos = pos;
			pos = s.indexOf("\n", pos + 1);
		}

		width = Math.max(width, s.length() - prevPos);
		return width;
	}

	/*
	 * Look for any URLs in the input string 's' and convert them to be links. If
	 * 'targetNewWindow' is true, set the link target to be '_blank' for a new
	 * window. If cssClass is non-null, then apply the specified CSS class to the
	 * link.
	 */
	public static String makeUrlsIntoLinks(String s, boolean targetNewWindow, String cssClass) {
		if ((s == null) || (s.indexOf("http") == -1)) {
			return s;
		}

		String target = "";
		String css = "";

		if (targetNewWindow) {
			target = " target='_blank'";
		}
		if ((cssClass != null) && (cssClass.length() > 0)) {
			css = " class='" + cssClass + "'";
		}

		String template = "<a href='$1'" + target + css + ">$1</a>";
		return s.replaceAll("(https?://[^ )]+)", template);
	}

	// 'color1' and 'color2' are integer arrays with length = 3. The three integers
	// range from 0-255
	// and specify the red, green, and blue components of a color. 'index' specifies
	// which of the
	// three components we're getting a shade for. 'fraction' specifies how far from
	// color1 we have
	// progressed to color2 on a scale of 0.0 to 1.0. Returns a two-digit hex string
	// ranging from
	// 00 to FF.
	private static String hexComponent(int[] color1, int[] color2, int index, Double fraction) {
		Double shade = ((1.0 - fraction) * color1[index]) + (fraction * color2[index]);
		return String.format("%02X", Math.round(shade));
	}

	// get the shade between 'color1' and 'color2' when we have a given 'count' of
	// of a given 'total',
	// using a logarithmic scale because 'total' is very large.
	private static String getShade(int[] color1, int[] color2, int count, int minCount, int maxCount, boolean logScale) {
		if ((maxCount == 0) || (count == 0)) {
			return "rgb(0,0,0,0)"; // fully transparent cell if no data
		}
		Double fraction = 0.0;
		if (count > 0) {
			// fraction = Math.log(count) / Math.log(maxCount);
                        if (logScale) {
                                fraction = Math.log(count - minCount) / Math.log(maxCount - minCount);
                        } else {
			        fraction = new Double(count - minCount) / new Double(Math.max(1, (maxCount - minCount)));
                        }
		}
		StringBuffer sb = new StringBuffer("#");
		sb.append(hexComponent(color1, color2, 0, fraction));
		sb.append(hexComponent(color1, color2, 1, fraction));
		sb.append(hexComponent(color1, color2, 2, fraction));

		String color = sb.toString();
		if ("#000000".equals(color)) {
			StringBuffer sb2 = new StringBuffer("#");
			sb2.append(hexComponent(color1, color2, 0, 0.0));
			sb2.append(hexComponent(color1, color2, 1, 0.0));
			sb2.append(hexComponent(color1, color2, 2, 0.0));
			return sb2.toString();
		}
		return color;
	}

	private static String getShade(int[] color1, int[] color2, int count, int minCount, int maxCount) {
                return getShade(color1, color2, count, minCount, maxCount, true);
        }

	/*
	 * get the hex color code for the given 'snpCount', where the highest number of
	 * SNPs per cell is given as 'maxSnpCount'.
	 */
	public static String getSnpColorCode(int snpCount, int minSnpCount, int maxSnpCount, boolean logScale) {
		int[] color1 = { 0, 153, 255 }; // light blue
		//int[] color2 = { 128, 255, 0 }; // light green
		int[] color3 = { 255, 80, 80 }; // light red

		return getShade(color1, color3, snpCount, minSnpCount, maxSnpCount, logScale);
	}

	public static String getSnpColorCode(int snpCount, int minSnpCount, int maxSnpCount) {
                return getSnpColorCode(snpCount, minSnpCount, maxSnpCount, true);
        }

	/*
	 * Get a pretty-printed version of the given coordinate.
	 */
	public static String getPrettyCoordinate(long coord) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%,d", coord));
		sb.append(" bp");
		return sb.toString();
	}

	public static String doubleToString(Double d) {
		if (d == null) {
			return "";
		}
		return String.format("%d", Math.round(d));
	}

	public static String integerToString(Integer i) {
		if (i == null) {
			return "";
		}
		return String.format("%d", i);
	}

	public static String bold(String s) {
		return "<B>" + s + "</B>";
	}

	public static String smallGrey(String s) {
		return "<span class='smallGrey'>" + s + "</span>";
	}

	// return a String for a progress meter showing the percentage done given the
	// two numbers given
	public static String progressMeter(long numberDone, long totalNumber) {
		if (totalNumber == 0)
			return "-";

		long pct = Math.round((100.0 * numberDone) / totalNumber);
		StringBuffer sb = new StringBuffer();
		sb.append("<div id='progressMeter' style='border: 1px solid black; width:200px; height:20px; margin-top: 5px;' title='" + String.format("%,d", numberDone) + " of " + String.format("%,d", totalNumber) + "'>");
		sb.append("<div id='shadedPart' style='height: 100%; background-color: darkblue; float:left; color: white; width: " + pct + "%'>");
		if (pct > 15) {
			sb.append(pct + "%");
			sb.append("</div><!-- shadedPart -->");
		} else {
			sb.append("</div><!-- shadedPart -->");
			sb.append(pct + "%");
		}
		sb.append("</div><!-- progressMeter -->");
		return sb.toString();
	}

	// Remove any HTML script tags from string 's', replacing them with an asterisk
	// (to help prevent
	// reflected XSS attacks). This relies on whatever data we run through this not
	// having script
	// tags as a valid part of the expected string.
	public static String noScript(String s) {
		if (s == null) {
			return s;
		}
		String t = s.replaceAll("(?i)" + Pattern.quote("<script>"), "*");
		return t.replaceAll("(?i)" + Pattern.quote("</script>"), "*");
	}

	// Remove any Javascript alert commands from string 's' (to help prevent
	// reflected XSS attacks).
	// This relies on valid data not having "alert" followed a pair of parentheses.
	public static String noAlert(String s) {
		if (s == null) {
			return s;
		}
		return s.replaceAll("(?i)alert *\\([^)]*\\)", "");
	}

	// Do any needed cleaning of string 's' to help prevent reflected XSS attacks,
	// for HTML-displayed text.
	public static String cleanHtml(String s) {
		if (s == null) {
			return s;
		}
		return Encode.forHtml(noScript(noAlert(s)));
	}

	// Do any needed cleaning of string 's' to help prevent reflected XSS attacks,
	// for JavaScript strings.
	public static String cleanJavaScript(String s) {
		if (s == null) {
			return s;
		}
		return Encode.forJavaScript(noScript(noAlert(s)));
	}

	// Remove any {xref...} or {comment...} strings from 's'. For use in vocab
	// browser comment display
	// for MP terms.
	public static String cleanComment(String s) {
		if (s != null) {
			String t = s.replaceAll("\\{xref[^}]*\\}", "");
			t = t.replaceAll("\\{comment[^}]*\\}", "");
			return t;
		}
		return s;
	}
} // end of class FormatHelper
