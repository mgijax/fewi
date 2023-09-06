<%@ attribute name="value" required="true" type="java.lang.String" description="text string to work on" %>
<%@ attribute name="newWindow" required="false" type="java.lang.Boolean" description="Whether links open in new windows" %>
<%@ attribute name="cssClass" required="false" type="java.lang.String" description="CSS classes to apply to the links" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- searches in value for http://.../... and converts any found into links --%>
<%
	if (newWindow == null) { newWindow = true; }
	if (cssClass == null) { cssClass = ""; }
	
	String target = "";
	String css = "";
	
	if (newWindow) { target = " target='_blank'"; }
	if ((cssClass != null) && (cssClass.length() > 0)) { css = " class='" + cssClass + "'"; }

	String template = "<a href='$1'" + target + cssClass + ">$1</a>";
	String withLinks = value.replaceAll("(https?://[^ )]+)", template);
%>
<%= withLinks %>