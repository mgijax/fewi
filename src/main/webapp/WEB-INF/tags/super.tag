<%@ attribute name="value" required="true" type="java.lang.String" description="Value to be superscripted" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- Superscripts the string in value --%>
<%=FormatHelper.superscript(value)%>