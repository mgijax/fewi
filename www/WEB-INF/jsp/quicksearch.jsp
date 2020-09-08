<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>MGI Quick Search Results</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
    
%>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="QUICK_SEARCH_help.shtml">	
	<span class="titleBarMainTitle">Quick Search Results for ${query}</span>
</div>

Bucket 1 : Markers and Alleles<p/>

Bucket 2 : Vocabulary Terms<p/>

Bucket 3 : Matches by ID<p/>

Search MGI with Google<p/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

