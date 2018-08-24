<%@ attribute name="items" required="true" type="java.lang.Object" description="key,value map or simply a list of values" %>
<%@ attribute name="name" required="true" type="java.lang.String" description="name of the input field for comparison strains" %>
<%@ attribute name="values" required="false" type="java.util.List" description="User selected values for comparison strains" %>
<%@ attribute name="width" required="true" type="java.lang.Integer" description="The number of columns for the checkbox grid" %>
<%@ attribute name="refName" required="true" type="java.lang.String" description="name of the input field for reference strains" %>
<%@ attribute name="refValues" required="false" type="java.util.List" description="User selected values for reference strains" %>
<%@ tag import = "java.util.*" %>
<%@ tag import = "org.jax.mgi.fewi.util.*" %>

<%-- tag to print out  checkbox options based on a map of key,value pairs, can also supply list of pre-selected keys to select --%>
<%
	// items can be either a Map (preferably LinkedHashMap) or a Collection of values
	Map itemMap;
	if(items instanceof java.lang.Iterable) {
		itemMap = new LinkedHashMap();
		for(Object i : (java.lang.Iterable) items)
		{
			itemMap.put(i,i);
		}
	}
	else itemMap = (Map) items;
	
	if(values==null) values = new ArrayList();
	if(refValues==null) refValues = new ArrayList();
	
	int count = 0;
	int columnSize = (int)Math.ceil((double)itemMap.keySet().size() / (double)width);
	for(Object key : itemMap.keySet()) {
		Object value = itemMap.get(key);
		Object checked = values.contains(key) ? "checked=\"checked\"" : "";
		Object refChecked = refValues.contains(key) ? "checked=\"checked\"" : "";
		if(count % columnSize == 0) {
			if(count != 0) { %> </div> <% }
			%> <div style="float:left;width:240px;">
				<div class="refToggle">
					<div class="refHeader" title="Reference strains">R</div>
					<div class="cmpHeader" title="Comparison strains">C</div>
				</div>
			<%
		}
%>
	<div class="checkbox">
		<label class="refContainer refToggle">
			<input name="${refName}" type="checkbox" value="<%=key %>" <%=refChecked %> />
 			<span class="refCheckmark"></span>
		</label>
		<label class="cmpContainer">
			<input name="${name}" type="checkbox" value="<%=key %>" <%=checked %> /> <%=value %>
 			<span class="cmpCheckmark"></span>
		</label>
	</div>
<%
		count++;
	}
%>
	</div>
