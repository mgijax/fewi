<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Marker marker = (Marker)request.getAttribute("marker");
   NotesTagConverter ntc = new NotesTagConverter("GO");%>
    
${templateBean.templateHeadHtml}

<title>Gene Ontology Classifications</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="foo_help.shtml">	
	<span class="titleBarMainTitle">Gene Ontology Classifications</span>
</div>


<!-- header table -->
<table class="summaryHeader">
<tr >
  <td class="summaryHeaderCat1">
       <b>Symbol</b><br>
       <b>Name</b><br>
       <b>ID</b>
  </td>
  <td class="summaryHeaderData1">
    <a style="font-size:x-large;  font-weight: bold;" 
      href="${configBean.FEWI_URL}marker/${marker.primaryID}">${marker.symbol}</a>
    <br/>
    ${marker.name}<br>
    ${marker.primaryID}</td>
</tr>
</table>

<a href="#text"></a><h3><b>Go Annotations as Summary Text</b> <a href="tabular">(Tabular View)</a></h3>

<%=ntc.convertNotes(marker.getGOText(), '|')%>


${marker.GOText} <br><hr><br>
<a href="#tabular"></a><h3><b>Go Annotations in Tabular Form</b> <a href="text">(Text View)</a></h3>
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
  <%@ include file="/js/foo_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}

