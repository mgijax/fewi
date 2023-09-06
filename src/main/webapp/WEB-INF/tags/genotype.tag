<%@ attribute name="value" required="true" type="mgi.frontend.datamodel.Genotype" description="Genotype object" %>
<%@ attribute name="noLink" required="false" type="java.lang.Boolean" description="Whether linking should be omitted" %>
<%@ attribute name="newWindow" required="false" type="java.lang.Boolean" description="Whether links open in new windows" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- tag to print out a Genotype datamodel object as allele pairs --%>
<%
	noLink = noLink!=null && noLink;
	newWindow = newWindow!=null && newWindow;
	
	NotesTagConverter ntc = new NotesTagConverter();
	String combo = value.getCombination3();
	String conversion = ntc.convertNotes(combo, '|',noLink);
	if(newWindow) conversion = ntc.useNewWindows(conversion);
%>
<%=FormatHelper.newline2HTMLBR(conversion)%>
