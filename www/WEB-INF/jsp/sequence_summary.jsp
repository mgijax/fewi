<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Marker marker = (Marker)request.getAttribute("marker"); %>
<% Reference reference = (Reference)request.getAttribute("reference"); %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Mouse Sequences Summary Report</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="SEQUENCE_summary_help.shtml">    
  <span class="titleBarMainTitle">Mouse Sequences Summary Report</span>
</div>


<!-- header table -->
<table class="summaryHeader">
	<tr >
	<c:if test="${not empty marker}">
	  <td class="summaryHeaderCat1">
	       <div style="padding-top:7px;">Symbol</div>
	       <div style="padding-top:3px;">Name</div>
	       <div style="padding-top:2px;">ID</span>
	  </td>
	  <td class="summaryHeaderData1">
	    <a style="font-size:large;  font-weight: bold;" 
	      href="${configBean.FEWI_URL}marker/${marker.primaryID}">${marker.symbol}</a>
	    <br/>
	    <span style="font-weight: bold;">${marker.name}</span>
	    <br/>
	    <span style="">${marker.primaryID}</span>
	  </td>
	</c:if>
	<c:if test="${empty marker and not empty reference}">
	  <td class="summaryHeaderCat1">
	    <b>Reference</b>
	  </td>
	  <td class="summaryHeaderData1">
	    <a style="font-size:x-large;  font-weight: bold; padding-bottom:10px;"
	      href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
	    <div style="padding:4px;"> </div>${reference.shortCitation}
	  </td>
	</c:if>
	</tr>
</table>


<div id="summary" style="width:1150px;">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
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

<div id="dynamicdata"></div>

<div id="paginationWrap" style="width:1150px;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/sequence_summary.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
