<%@ attribute name="value" required="true" type="java.lang.String" description="Note to be displayed" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- Displays a QTL note, like QTL-TEXT or Candidate Genes --%>
<%-- Superscripts the string in value, then replaces all newlines with <p> tag --%>
<p><%=FormatHelper.superscript(value).replaceAll("[\n]+", "<p/>")%>