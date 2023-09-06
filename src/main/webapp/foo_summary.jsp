<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Foo Summary</title>

<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="foo_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Foo Summary</span>
</div>
<!-- end header bar -->

<div id="outer"  class="bluebar">
	<span id="toggleImg" class="qfExpand"></span>
	<div id="toggleQF"><span id="toggleLink" class="filterButton">Click to modify search</span></div>
	<div id="qwrap" style="display:none;">
		<%@ include file="/WEB-INF/jsp/foo_form.jsp" %>
	</div>
</div>

<div id="resultbar" class="bluebar">Results</div>

<div id="summary">
	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
					<span class="label">Filters:</span>
					&nbsp;<span id="defaultText"  style="display:none;">No filters selected. Filter these foo below.</span>
					<span id="filterList"></span><br/>					
					<span id="fCount" style="display:none;" ><span id="filterCount">0</span> foo(s) match after applying filter(s)</span>
				</div>
			</div>
		</div>
	</div>

	<div id="querySummary">
		<div class="innertube">
			<span class="title">You searched for:</span><br/>
			<c:if test="${not empty queryForm.param1}">
				<span class="label">Param 1:</span>			
				${queryForm.param1}<br/></c:if>
			<c:if test="${not empty queryForm.param2}">
				<span class="label">Param 2:</span>			
				${queryForm.param2}<br/></c:if>
			<c:if test="${not empty queryForm.param3}">
				<span class="label">Param 3:</span>			
				${queryForm.param3}<br/></c:if>
			<span id="totalCount" class="count">0</span> foo(s) match your unfiltered search.<br/>
		</div>
	</div>

	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
</div>
	
<div id="toolbar" class="bluebar">
	<div id="downloadDiv">
		<span class="label">Export:</span> <a id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a> 
	</div>
	<div id="filterDiv">
		<span class="label">Filter foo by:</span> 
		<a id="fooFilter" class="filterButton">Foo <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
	</div>
	<div id="otherDiv">
		
	</div>
</div>

<div id="dynamicdata"></div>
<div id="paginationWrap">
	<div id="paginationBottom">&nbsp;</div>
</div>

<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>	
		<div class="bd">
			<form:form method="GET" action="${configBean.FEWI_URL}reference/summary">
			<img src="/fewi/mgi/assets/images/loading.gif">	
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
	var qDisplay = true;
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/foo_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/foo_summary.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
