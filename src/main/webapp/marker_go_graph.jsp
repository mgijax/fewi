<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${marker.symbol} GO Annotations Graph</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
%>

<script language="Javascript">
</script>

<c:set var="sCount" value="1" scope="page"/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper">	
	<span class="titleBarMainTitle">GO Annotations Graph</span>
</div>
<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>

<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
			</div>
		</div>
	</div>
	<div id="querySummary">
		<div class="innertube">
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
		</div>
	</div>
</div>
<div style="clear:both;"></div>

<style>
#goSummaryLine { font-weight: bold; font-size: 125%; line-height: 3em }
#goGraphImg { border: solid thin blue; margin-bottom: 5px }
#goGraphTbl td { border: solid thin black; padding: 3px; line-height: 1.3em }
#goGraphTbl th { border: solid thin black; padding: 3px; line-height: 1.3em }
#goGraphCodes td { border: none; padding: 2px; }
</style>

${goGraphText}

<jsp:include page="go_summary_legend.jsp"></jsp:include>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
