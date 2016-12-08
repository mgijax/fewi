<%@ attribute name="items" required="true" type="java.lang.Object" description="key,value map or simply a list of values" %>
<%@ attribute name="name" required="true" type="java.lang.String" description="name of the input field" %>
<%@ attribute name="values" required="false" type="java.util.List" description="User selected values" %>
<%@ attribute name="divider" required="false" type="java.lang.String" description="String to put between options (default <br/>)" %>

<%@ tag import = "java.util.*" %>
<%-- tag to print out  checkbox options based on a map of key,value pairs, can also supply list of pre-selected keys to select --%>
<%
	// items can be either a Map (preferably LinkedHashMap) or a Collection of values
	Map itemMap;
	if(items instanceof java.lang.Iterable)
	{
		itemMap = new LinkedHashMap();
		for(Object i : (java.lang.Iterable) items)
		{
			itemMap.put(i,i);
		}
	}
	else itemMap = (Map) items;
	
    if(values==null) values = new ArrayList();
    if(divider==null) divider = "<br/>";
	
	for(Object key : itemMap.keySet())
	{
		Object value = itemMap.get(key);
		Object checked = values.contains(key) ? "checked=\"checked\"" : "";
%>
	<label><input name="${name}" type="checkbox" value="<%=key %>" <%=checked %> /> <%=value %></label><%=divider %>
<% 	} %>