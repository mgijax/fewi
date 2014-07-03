<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference");
   NotesTagConverter ntc = new NotesTagConverter("GO");%>
    
${templateBean.templateHeadHtml}

<title>Gene Ontology Annotations</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
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

<div id="toolbar" class="bluebar" style="width:1245px;">
	<div id="downloadDiv">
		<span class="label">Export:</span> 
		<a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
		<a id="excelDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a> 
	</div>
	<div id="filterDiv"></div>
	<div id="otherDiv"></div>
</div>
<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>
<div id="paginationWrap" style="width:1080px;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<div class="GO">
<br>
<hr><b>
Gene Ontology Evidence Code Abbreviations:</b><br><br>
<table>
<tbody><tr><td>&nbsp;&nbsp;<b>EXP</b> Inferred from experiment</td></tr>
<tr><td>&nbsp;&nbsp;<b>IC</b> Inferred by curator</td></tr>

<tr><td>&nbsp;&nbsp;<b>IDA</b> Inferred from direct assay</td></tr>
<tr><td>&nbsp;&nbsp;<b>IEA</b> Inferred from electronic annotation</td></tr>
<tr><td>&nbsp;&nbsp;<b>IGI</b> Inferred from genetic interaction</td></tr>
<tr><td>&nbsp;&nbsp;<b>IMP</b> Inferred from mutant phenotype</td></tr>
<tr><td>&nbsp;&nbsp;<b>IPI</b> Inferred from physical interaction</td></tr>

<tr><td>&nbsp;&nbsp;<b>ISS</b> Inferred from sequence or structural similarity</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISO</b> Inferred from sequence orthology</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISA</b> Inferred from  sequence alignment</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISM</b> Inferred from sequence model</td></tr>
<tr><td>&nbsp;&nbsp;<b>NAS</b> Non-traceable author statement</td></tr>

<tr><td>&nbsp;&nbsp;<b>ND</b> No biological data available</td></tr>
<tr><td>&nbsp;&nbsp;<b>RCA</b> Reviewed computational analysis</td></tr>
<tr><td>&nbsp;&nbsp;<b>TAS</b> Traceable author statement</td></tr>
</tbody></table>
<hr>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>

<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/go_summary_reference.js"></script>

${templateBean.templateBodyStopHtml}

