<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${seoTitle}"
	canonical="${configBean.FEWI_URL}probe/${probe.primaryID}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/probe_detail.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="EXPRESSION_probe_detail_help.shtml" style="max-width: none;">
	<div name="centeredTitle">
		<span class="titleBarMainTitle"><fewi:super value="${probe.name}"/></span>
		<span class="titleBar_sub">
			${highLevelSegmentType} Detail
		</span>
	</div>
</div>

<div class="container detailStructureTable">
	<%@ include file="probe_detail_summary_ribbon.jsp" %>
	<%@ include file="probe_detail_source_ribbon.jsp" %>
	<%@ include file="probe_detail_marker_ribbon.jsp" %>
	<%@ include file="probe_detail_expression_ribbon.jsp" %>
	<%@ include file="probe_detail_sequence_ribbon.jsp" %>
	<%@ include file="probe_detail_polymorphism_ribbon.jsp" %>
	<%@ include file="probe_detail_reference_ribbon.jsp" %>
</div>

<!--	close page template -->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
