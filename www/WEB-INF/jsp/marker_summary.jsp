<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Marker Query Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
.ysf
	{
		font-weight: bold;
		text-decoration: underline;
		font-size: 110%;
	}
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_summary_help.shtml">	
	<span class="titleBarMainTitle">Marker Query Summary</span>
</div>

<div id="outer" >
	<span id="toggleImg" class="qfExpand"></span>
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to modify search</span></div>
	<div id="qwrap" style="display:none;">
		<%@ include file="/WEB-INF/jsp/marker_form.jsp" %>
	</div>
</div>

<div id="summary">
	<div id="breadbox" style="">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
				</div>
			</div>
		</div>
	</div>
	<div id="querySummary" style="width:700px;">
		<div class="innertube">
		<div id="query-ysf">
			<span class="ysf">You searched for...</span>
			<%@ include file="/WEB-INF/jsp/marker_ysf.jsp" %>
		</div>
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>

<div id="toolbar" class="bluebar" style="">
	<div id="downloadDiv">
		<span class="label">Export:</span>
		<a id="textDownload"  class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
        <a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
	</div>
</div>
<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<div id="paginationWrap" style="width: 468px; float:right;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<script type="text/javascript">
window.querystring="${queryString}";
window.fewiurl="${configBean.FEWI_URL}";
window.doHighlights=false;
<c:if test="${not empty queryForm.nomen}">window.doHighlights=true;</c:if>
var qDisplay = true;
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker_summary.js"></script>
${templateBean.templateBodyStopHtml}

