<%@ attribute name="value" required="true" type="java.lang.String" description="Value to be pluralized" %>
<%@ attribute name="list" required="false" type="java.util.Collection" description="collection to determine plurals" %>
<%@ attribute name="size" required="false" type="java.lang.Integer" description="size to determine plurals" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>
<%-- adds an 's' if list.size()!=1 or size!=1--%>
<%
	String plural="s";
	if(list!=null && list.size()==1)
	{
		plural="";
	}
	else if(size!=null && size==1)
	{
		plural="";
	}
%>
<%=value%><%=plural%>