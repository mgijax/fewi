<%@ attribute name="value" required="true" type="java.lang.String" description="string in which to highlight words from searchString" %>
<%@ attribute name="searchString" required="true" type="java.lang.String" description="user's search string, containing words to highlight" %>
<%@ attribute name="highlightClass" required="true" type="java.lang.String" description="CSS class to apply to highlight individual word matches" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%@ tag import = "java.util.regex.Matcher" %>
<%@ tag import = "java.util.regex.Pattern" %>
<%@ tag import = "java.util.List" %>
<%@ tag import = "java.util.ArrayList" %>
<%-- tag to highlight occurrences of a word or set of words in a text string (value) --%>
<%-- If any of the required parameters are null or empty, we skip the highlighting. --%>
<%
	StringBuffer sb = new StringBuffer();

	if ((searchString != null) && (searchString.length() > 0) && (value != null) && (value.length() > 0)
		&& (highlightClass != null) && (highlightClass.length() > 0)) {
	
		/* special considerations:
		 *	1. need to not highlight matches inside HTML tags (like <a href="...">)
		 *	2. highlight only full words (use \btoken\b to ensure word boundaries on either side)
		 * plan:
		 *	1. look for the first HTML tag
		 *	2. highlight text up to it and add to stringbuffer
		 *	3. add HTML tag to stringbuffer
		 *	4. repeat while still HTML tags
		 *	5. highlight remaining text and add to stringbuffer
		 */
	
		Pattern htmlTagPattern = Pattern.compile("(<[^>]*>)");
		List<Pattern> wordPatterns = new ArrayList<Pattern>();
		for (String word : searchString.replaceAll("[^a-zA-Z0-9]", " ").split(" +")) {
			wordPatterns.add(Pattern.compile("\\b(" + word + ")", Pattern.CASE_INSENSITIVE));
		}

		String toDo = value;
		for (Pattern p : wordPatterns) {
			sb = new StringBuffer();
			
			while ((toDo != null) && (toDo.length() > 0)) {
				Matcher m = htmlTagPattern.matcher(toDo);
				String toHighlight = null;
				String htmlTag = null;
			
				// is an HTML tag embedded in what we have left to process?
				if (m.find()) {
					toHighlight = toDo.substring(0, m.start());
					toDo = toDo.substring(m.end());
					htmlTag = m.group();
				} else {
					toHighlight = toDo;
					toDo = null;
				}
			
				if (toHighlight != null) {
					Matcher w = p.matcher(toHighlight);
					toHighlight = w.replaceAll("<span class='" + highlightClass + "'>$1</span>");
					sb.append(toHighlight);
				}
				if (htmlTag != null) {
					sb.append(htmlTag);
				}
			}
			toDo = sb.toString();
		}
	}

	if ((sb.length() == 0) && (value != null)) {
		sb.append(value);
	}
%>
<%=sb.toString()%>