<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Comparative GO Graph (${organisms})</title>

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
<div id="titleBarWrapper" userdoc="HOMOLOGY_class_help.shtml">	
	<span class="titleBarMainTitle">Comparative GO Graph (${organisms})</span>
</div>

<%@ include file="/WEB-INF/jsp/homology_header.jsp" %>
<br/>

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

${goGraphText}

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
