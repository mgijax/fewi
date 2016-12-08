<%@ attribute name="value" required="true" type="java.lang.String" description="allele pairs (from Genotype object) with MGI markup" %>
<%@ attribute name="noLink" required="false" type="java.lang.Boolean" description="Whether linking should be omitted" %>
<%@ attribute name="newWindow" required="false" type="java.lang.Boolean" description="Whether links open in new windows" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- tag to print out a set of allele pairs, as from a Solr field, rather than a db Genotype object --%>
<%
	noLink = noLink!=null && noLink;
	newWindow = newWindow!=null && newWindow;
	
	NotesTagConverter ntc = new NotesTagConverter();
	String conversion = ntc.convertNotes(value, '|',noLink);
	if(newWindow) conversion = ntc.useNewWindows(conversion);
%>
<%=FormatHelper.newline2HTMLBR(conversion)%>