<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<style type="text/css">
</style>
<!-- must fill in: pageTitle, dataType, accID -->

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>${pageTitle}</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px">	
	<!--myTitle -->
	<span class="titleBarMainTitle">${pageTitle}</span>
</div>
<!-- end header bar -->

Forwarding one ${dataType} to the Alliance of Genome Resources...
<p>
If this page does not redirect automatically in 10 seconds, please click
<a href="${externalUrls.Alliance}gene/${accID}">here</a>.

<script>
window.onload = function() {
    window.location.replace("${externalUrls.Alliance}gene/${accID}");
}
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
