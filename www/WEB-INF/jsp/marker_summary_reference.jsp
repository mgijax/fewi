<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>
    
${templateBean.templateHeadHtml}

<title>Marker Query Summary</title>

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
    <a style="font-size:x-large;  font-weight: bold;" 
      href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
    <br/>
  </td>
</tr>
</table>


<!-- paginator -->
<div style="width:700px;"></div>
<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
</div>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<script type="text/javascript">
window.querystring="${queryString}";
window.fewiurl="${configBean.FEWI_URL}";
</script>
<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker_summary.js"></script>

${templateBean.templateBodyStopHtml}

