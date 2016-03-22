<%@ attribute name="value" required="true" type="java.lang.String" description="URL encodes string value" %>
<%= java.net.URLEncoder.encode(value , "UTF-8") %>