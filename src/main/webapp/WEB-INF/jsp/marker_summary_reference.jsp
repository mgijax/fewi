<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Marker Query Summary</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_summary_help.shtml">	
	<span class="titleBarMainTitle">Marker Summary for Reference</span>
</div>


<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Reference</b>
  </td>
  <td class="summaryHeaderData1">
    <a style="font-size:x-large;  font-weight: bold; padding-bottom:10px;"
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">
      ${reference.jnumID}
    </a>

    <div style="padding:4px;"> </div>

    ${reference.shortCitation}
  </td>
</tr>
</table>

<div id="summary">
	<div id="rightcolumn" style="float: right;">
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
</script>
<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker_summary.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

