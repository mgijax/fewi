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
	  <c:set var="urlPiece" value="marker/${marker.primaryID}"/>
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
	  <c:set var="urlPiece" value="reference/${reference.jnumID}"/>
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

<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>
		<div class="bd" style="width: 285px">
			<form:form method="GET" action="${configBean.FEWI_URL}sequence/${urlPiece}">
				<img src="${configBean.FEWI_URL}assets/images/loading.gif">
			</form:form>
		</div>
	</div>
</div>

<div id="summary" style="width:1150px;">
	<div id="breadbox">
		<div id="filterDiv" style="width: 260px; padding-top: 20px;">
			<span id="filterLabel" class="label">Filter sequences by:</span>
			<a id="strainFilter" class="filterButton">Strain&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="typeFilter" class="filterButton">Type&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
		</div><br />
		<div id="filterSummaryWrapper" style="width: 500px; display: inline-block;">
			<div id="filterSummary" class="filters" style="display: none">
				<span class="label">Filtered by:</span>&nbsp; <span id="defaultText" style="display: none;">No filters selected.</span> <span id="filterList"></span><br />
			</div>
		</div>
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
	var querystring = "${e:forJavaScript(queryString)}";
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/sequence_summary.js"></script>

<script type="text/javascript">
	function getQuerystring() {
		return querystring + filters.getUrlFragment();
	}
	filters.setFewiUrl(fewiurl);
	filters.setQueryStringFunction(getQuerystring);
	filters.setHistoryManagement("myDataTable", handleHistoryNavigation);
    filters.setGeneratePageRequestFunction(generateRequest);
	filters.setDataTable(myDataTable);
	filters.addFilter('strainFilter', 'Strain', 'strainFilter', 'strain', '${configBean.FEWI_URL}sequence/facet/strain');
	filters.addFilter('typeFilter', 'Type', 'typeFilter', 'type', '${configBean.FEWI_URL}sequence/facet/type');
	filters.setSummaryNames('filterSummary', 'filterList');
	filters.registerCallback('scrollAfterFilter', scrollUp);
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
