<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Reference reference = (Reference)request.getAttribute("reference"); %>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Foo Query Summary</title>

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
<div id="titleBarWrapper" userdoc="foo_help.shtml">	
	<span class="titleBarMainTitle">Foo Summary for Reference</span>
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

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

