<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Genes and Markers Query Form</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
td.padded { padding: 4px; }
td.top { vertical-align: top; }
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_help.shtml">	
	<span class="titleBarMainTitle">Genes and Markers Query Form</span>
</div>
<c:set var="helpPage" value="${configBean.USERHELP_URL}GENE_help.shtml"/>

<div id="outer" >
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to hide search</span></div>
	<div id="qwrap">
		<%@ include file="/WEB-INF/jsp/marker_form.jsp" %>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var qDisplay = false;
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker_query.js"></script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
