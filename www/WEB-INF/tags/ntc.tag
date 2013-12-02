<%@ attribute name="value" required="true" type="java.lang.String" description="String to be notes tag converted" %>
<%@ attribute name="noLink" required="false" type="java.lang.Boolean" description="Whether linking should be omitted" %>
<%@ attribute name="newWindow" required="false" type="java.lang.Boolean" description="Whether links open in new windows" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- tag to print out a string run through the notes tag converter --%>
<%
	noLink = noLink!=null && noLink;
	newWindow = newWindow!=null && newWindow;
	
	NotesTagConverter ntc = new NotesTagConverter();
	String conversion = ntc.convertNotes(value, '|',noLink);
	if(newWindow) conversion = ntc.useNewWindows(value);
%>
<%=conversion%>