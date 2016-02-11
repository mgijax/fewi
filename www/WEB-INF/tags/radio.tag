<%
  /* builds radio buttons with the following parameters:
   * 1. name (required) - name of the field
   * 2. items (required) - Collection of values; if a Map, then maps from key
   *	to display value
   * 3. idPrefix - Each radio button gets its own ID, using this as a prefix
   *	and an ascending integer as a suffix
   * 4. cssClass - CSS class to be applied to each button's display text.
   * 5. divider - HTML string used to divide between buttons (default is two
   *	non-breaking spaces)
   * 6. value - initial value to be selected by default
   */
%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ tag import = "java.util.*" %>
<%@ attribute name="name" required="true" type="java.lang.String" %>
<%@ attribute name="idPrefix" required="false" type="java.lang.String" %>
<%@ attribute name="cssClass" required="false" type="java.lang.String" %>
<%@ attribute name="divider" required="false" type="java.lang.String" %>

<%@ attribute name="items" required="true" type="java.lang.Object" description="key,value map, or simply a list of values" %>
<%@ attribute name="value" required="false" type="java.lang.Object" description="If there is only a single User selected value" %>

<%
    if (divider == null) { divider = " "; }
    if (cssClass == null) { cssClass = ""; }
    if (idPrefix == null) { idPrefix = name; }

    /* items can be either a Map (preferably LinkedHashMap) or a Collection
     * of values
     */

    if(items != null) {
	Map itemMap = null;
	if(items instanceof java.lang.Iterable) {
		itemMap = new LinkedHashMap();
		for(Object i : (java.lang.Iterable) items) {
			itemMap.put(i,i);
		}
	}
	else itemMap = (Map) items;
		
	int i = 0;
	for(Object key : itemMap.keySet()) {
	    String checked = value.equals(key) ? "checked=\"checked\"" : "";
	    Object displayValue = itemMap.get(key);
%>
	    <input type="radio" id="<%= idPrefix %><%= i++ %>" name="<%= name %>" value="<%= key %>"<%= checked %>> <span class="<%= cssClass %>"><%= displayValue %></span><%= divider %>
<%
	}
    }
%>
