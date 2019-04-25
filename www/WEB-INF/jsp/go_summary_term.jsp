<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Term term = (Term)request.getAttribute("term");
   NotesTagConverter ntc = new NotesTagConverter("GO");%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Gene Ontology Annotations</title>

<style type="text/css">
</style>
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/go_summary.css" />

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GO_classification_report_help.shtml#tabular">
	<span class="titleBarMainTitle">Gene Ontology Annotations</span>
</div>

<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <span class="label">Term</span><br/>
       <span class="label">ID</span>
  </td>
  <td class="summaryHeaderData1">
       <span class="">${term.term}</span><br/>
       <span class="">${term.primaryID}</span>
  </td>
</tr>
</table>

<!-- paginator -->
<div id="summary" style="width:1240px;">
	<div id="breadbox">
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

<form action="${configBean.MOUSEMINE_URL}mousemine/portal.do" method="post" name="mousemine" target="_blank">
	<input id="mousemineids" type="hidden" value="" name="externalids">
	<input type="hidden" value="SequenceFeature" name="class">
</form>

<div id="toolbar" class="bluebar" style="width:1353px;">
	<div id="downloadDiv">
		<span class="label">Export:</span>
		<a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
		<a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
		<a id="mouseMineLink" target="_blank" class="filterButton" style="display: none" onClick="javascript: mousemine.submit();"><img src="${configBean.WEBSHARE_URL}images/arrow_right.gif" width="10" height="10" /> MouseMine</a>
	</div>
	<div id="filterDiv"></div>
	<div id="otherDiv"></div>
</div>
<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>
<div id="paginationWrap" style="width:1080px;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<jsp:include page="go_summary_legend.jsp"></jsp:include>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>

<script type="text/javascript">
	// look up (via Ajax request) matching marker IDs for the MouseMine link
	var mmUrl = fewiurl + "go/jsonMarkers?goID=${term.primaryID}";
	$.ajax({
		url : mmUrl,
		type : 'GET',
		dataType : 'json',
		success : function(data) {
			$("#mousemineids").val(data.summaryRows.join(','));
//			$("#mousemineids").val(data.summaryRows.join(','));
			$('#mouseMineLink').css({display: 'inline'});
		} 
	});
</script>

<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/go_summary_term.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
