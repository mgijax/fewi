<%@ attribute name="items" required="true" type="java.lang.Object" description="key,value map, or simply a list of values" %>
<%@ attribute name="value" required="false" type="java.lang.String" description="If there is only a single User selected value" %>
<%@ attribute name="values" required="false" type="java.util.List" description="User selected values" %>
<%@ tag import = "java.util.*" %>
<%-- tag to print out select box options based on a map of key,value pairs, can also supply list of pre-selected keys to select --%>
<%
	// items can be either a Map (preferably LinkedHashMap) or a Collection of values
	Map itemMap;
	if(items instanceof java.lang.Iterable) {
		itemMap = new LinkedHashMap();
		for(Object i : (java.lang.Iterable) items) {
			itemMap.put(i,i);
		}
	}
	else itemMap = (Map) items;
	
	// Defaults the selected value to the first item
	if(values==null) {
		values = new ArrayList();
		if(value!=null) values.add(value);
	}
	
	for(Object key : itemMap.keySet()) {
		Object value = itemMap.get(key);
		Object selected = values.contains(key) ? "selected=\"selected\"" : "";
%>
		<option value="<%=key %>" <%=selected %>><%=value %></option>
<%
 	}
%>
