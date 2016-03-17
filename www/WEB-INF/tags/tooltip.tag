<%@ attribute name="text" required="true" type="java.lang.String" description="Text for tooltip, including HTML markup" %>
<%@ attribute name="timeout" required="false" type="java.lang.Integer" description="Number of ms at which the tooltip will go away automatically (default 0 for no timeout)" %>
<%@ attribute name="width" required="false" type="java.lang.Integer" description="Width of the popup (default 9 pixels per character in text, max of 300)" %>
<%@ attribute name="superscript" required="false" type="java.lang.String" description="Should the tooltip convert angle bracket notation to HTML superscripts? (default true)" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- returns the onMouseOver and onMouseOut attributes needed for an HTML-enabled tooltip --%>
<%
	Integer myWidth = Math.min(300, 9 * text.length());
	Integer myTimeout = 0;
	String mySuper = "false";

	if (timeout != null) { myTimeout = timeout; }
	if (width != null) { myWidth = width; }
	if (superscript != null) { mySuper = superscript; }

	String myText = text;
	if (mySuper.equals("true")) {
		myText = FormatHelper.superscript(text);
		if (!myText.equals(text)) {
			myText = "<br style=\\'line-height: 4px\\'/>" + myText;
			myWidth = Math.min(300, 9 * text.replaceAll("[<>]", "").length());
		}

	}

	String mouseOver = "return overlib('" + myText + "', LEFT, WIDTH, " + myWidth + ", TIMEOUT, " + myTimeout + ");";
%>
onMouseOut="nd();" onMouseOver="<%= mouseOver %>"
