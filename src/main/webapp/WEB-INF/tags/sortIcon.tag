<%@ attribute name="column" required="true" type="java.lang.String" description="Column we are looking at" %>
<%@ attribute name="sortBy" required="true" type="java.lang.String" description="Column chosen by the user for sorting" %>
<%@ attribute name="dir" required="true" type="java.lang.String" description="direction of sort (asc or desc)" %>
<%@ tag import = "org.jax.mgi.fewi.config.ContextLoader" %>
<%@ tag import = "java.util.Properties" %>
<%-- adds the appropriate image to a column header, showing whether the column is already sorted ascending or descending,
  or if it is available for sorting --%>
<%
	Properties configBean = ContextLoader.getConfigBean();
	String image = "creSortableArrow.png";
        String webshareUrl = configBean.getProperty("WEBSHARE_URL");

	if ((column != null) && (column.equals(sortBy))) {
		if ("asc".equals(dir)) {
			image = "creDownArrow.png";		
		} else {
			image = "creUpArrow.png";
		}
	}
%>
<img class="sortArrow" src="<%= webshareUrl %>images/cre/<%= image %>"/>
