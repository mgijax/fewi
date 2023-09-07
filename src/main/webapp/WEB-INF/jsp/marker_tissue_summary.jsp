<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${marker.symbol} Gene Expression Tissue Summary - GXD</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="GXD Gene Expression tissue results for ${marker.symbol}"/>
<meta name="keywords" content="MGI, GXD, mouse, mice, Mus musculus, murine, gene, expression, tissue, ${marker.symbol}"/>
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/> 

<style type="text/css">
</style>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>

<% Marker marker = (Marker)request.getAttribute("marker"); %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_tissue_results_help.shtml">	
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Tissue Summary</span>
</div>

<c:set var="isGxd" value="Gxd"/>
<%@ include file="/WEB-INF/jsp/marker_header.jsp" %><br>

<div id="summary">

	<div id="breadbox" style="width: 580px">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
				</div>
			</div>
		</div>
	</div>

	<div id="querySummary">
		<div class="innertube">
		</div>
	</div>

	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>

<div id="toolbar" class="goldbar" style="width:544px; min-width: 400px">
	<div id="downloadDiv">
		<span class="label">Export:</span> <a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
		<a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a> 
	</div>
	<div id="filterDiv">
	</div>
	<div id="otherDiv"> 
	</div>
</div>



<!-- data table div: filled by YUI, called via js below -->
<style type="text/css">
.yui-skin-sam .yui-dt th {
    background:#EBCA6D;
}
</style>
<div id="dynamicdata"></div>

<div id="paginationWrap" style="width: 468px">
	<div id="paginationBottom">&nbsp;</div>
</div>


<!-- including this file will start the data injection -->
<script type="text/javascript">
  <%@ include file="/js/marker_tissue_summary.js" %>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

