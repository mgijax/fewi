<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!--begin custom header content for this example-->
<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<script src="${configBean.FEWI_URL}js/rowexpansion.js"></script>

<title>References</title>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References</span>
</div>
<!-- end header bar -->


<table id="objHeader" class="pad5 borderedTable">
	<tr>
	<td class="label">
		ID:<br/>
		Type:<br/>
		Length:<br/>
	</td>
	<td class="text">
		${sequence.primaryID}<br/>
		${sequence.sequenceType}<br/>
		${sequence.length}
	</td>
	</tr>
</table>

<table style="width:100%;">
	<tr>
	<td class="filters">
		<a id="authorFilter" class="filterButton">Author Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="journalFilter" class="filterButton">Journal Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="yearFilter" class="filterButton">Year Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="curatedDataFilter" class="filterButton">Data Filter <img src="${configBean.FEWI_URL}images/filter.png" width="8" height="8" /></a>
	</td>
	<td class="paginator">
		<div id="paginationTop">&nbsp;</div>
	</td>
	</tr>
</table>

<div id="dynamicdata"></div>

<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>	
		<div class="bd">
			<form:form method="GET" action="${configBean.FEWI_URL}reference/summary">		
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	<%@ include file="/js/reference_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}
