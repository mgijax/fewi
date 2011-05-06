<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Gene Expression Tissue Results By Genome Feature - MGI</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<meta name="description" content="Gene Expression tissue results associated with genome feature {gene symbol}">
<meta name="keywords" content="MGI, mouse, gene, expression, GXD, tissue, MGI gene, MGI expression, GXD tissue, expression tissue, GXD gene tissue, MGI gene expression, MGI gene expression tissue"> 
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/> 

<style type="text/css">
</style>

<script>
</script>

<% Marker marker = (Marker)request.getAttribute("marker"); %>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="gxd_tissue_result_help.shtml">	
	<span class="titleBarMainTitle">Gene Expression Tissue Results By Genome Feature </span>
</div>


<jsp:include page="marker_header.jsp"></jsp:include><br>


<!-- paginator -->
<table style="width:100%;">
  <tr>
    <td class="paginator">
      <div id="paginationTop">&nbsp;</div>
    </td>
  </tr>
</table>

<!-- data table div: filled by YUI, called via js below -->
<div id="dynamicdata"></div>

<!-- including this file will start the data injection -->
<script type="text/javascript">
  <%@ include file="/js/marker_tissue_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}

