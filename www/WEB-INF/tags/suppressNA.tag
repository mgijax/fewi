<%@ attribute name="value" required="true" type="java.lang.String" description="Value to be output, if not Not Applicable" %>
<%-- outputs the given 'value', unless it is "Not Applicable" --%>
<%
	String outputValue = value;
	if ("Not Applicable".equals(value)) {
		outputValue = "";
	}
%><%= outputValue %>