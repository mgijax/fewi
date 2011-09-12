<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Gene Expression Tissue Summary - MGI</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Gene Expression tissue results associated with genome feature {gene symbol}">
<meta name="keywords" content="MGI, mouse, gene, expression, GXD, tissue, MGI gene, MGI expression, GXD tissue, expression tissue, GXD gene tissue, MGI gene expression, MGI gene expression tissue"> 
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/> 

<style type="text/css">
</style>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>

<% Marker marker = (Marker)request.getAttribute("marker"); %>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="EXPRESSION_tissue_results_help.shtml">	
  <span class="titleBarMainTitle">Gene Expression Tissue Summary</span>
</div>


<jsp:include page="marker_header.jsp"></jsp:include><br>

<div id="summary">

	<div id="breadbox" style="width: 480px">
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

<div id="toolbar" class="bluebar" style="width:444px; min-width: 400px">
	<div id="filterDiv">
	</div>
	<div id="otherDiv"> 
	</div>
	<div id="downloadDiv">
		<a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
	</div>
</div>



<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<div id="paginationWrap" style="width: 468px">
	<div id="paginationBottom">&nbsp;</div>
</div>


<!-- including this file will start the data injection -->
<script type="text/javascript">
  <%@ include file="/js/marker_tissue_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}

