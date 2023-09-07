<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${seoTitle}"
	canonical="${configBean.FEWI_URL}mapping/${mapping.primaryID}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/mapping_detail.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="mapping_experimental_data_help.shtml" style="max-width: none;">
	<div name="centeredTitle">
		<span class="titleBarMainTitle">${mapping.type} Mapping Data</span>
	</div>
</div>

<div class="container detailStructureTable">
	<%@ include file="mapping_detail_summary_ribbon.jsp" %>
	<%@ include file="mapping_detail_marker_ribbon.jsp" %>
	<%@ include file="mapping_detail_notes_ribbon.jsp" %>
	<%@ include file="mapping_detail_rirc_ribbon.jsp" %>
	<%@ include file="mapping_detail_cross_ribbon.jsp" %>
	<%@ include file="mapping_detail_hybrid_ribbon.jsp" %>
	<%@ include file="mapping_detail_fish_ribbon.jsp" %>
	<%@ include file="mapping_detail_insitu_ribbon.jsp" %>
</div>

<script>
$('sup').css({'font-size' : '80%'});
</script>
<!--	close page template -->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
