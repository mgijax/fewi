<%@ attribute name="value" required="true" type="java.lang.String" description="String to be formatVerbatimized" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- Passes value through formatVerbatim function --%>
<%=FormatHelper.formatVerbatim(value)%>