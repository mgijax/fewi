<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Cre Allele Summary</title>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/recombinase/recombinase_summary.css">

<!-- These styles only here to use WEBSHARE_URL. Any other styles go in recombinase_summary.css -->
<style>
.yui-skin-sam .yui-dt th{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1300px;
}
.yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1400px;
}

.yui-skin-sam th.yui-dt-sortable .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creSortableArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-asc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creDownArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-desc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creUpArrow.png)
  no-repeat right;
}
.yui-dt a {
  text-decoration: none;
}
.yui-dt img {
  border: none;
}

.facetFilter .yui-panel .bd {
	width: 285px
}
.facetFilter .facetHelp {
	background-color: #F2F2F2;
	border-bottom: solid 1px #ccc;
}
.facetFilter .facetHelp p {
	padding: 4px 8px;
	margin-left: 4px;
	font-size: 10px;
}
.matrixIcon {
	width: 25px;
}
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="RECOMBINASE_summary_help.shtml">
	<!--myTitle -->
	<h2 class="titleBarMainTitle">Recombinase Alleles - Tissue Summary</h2>
</div>
<!-- end header bar -->

<br/>

<c:set var="operator" value="${recombinaseQueryForm.structureOperator}"/>
<c:if test="${empty operator}">
	<c:set var="operator" value="assayed"/>
</c:if>
<c:set var="operator" value="${e:forHtml(operator)}"/>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/recombinase/recombinase_summary_1.js"></script>
<div id="summary" style="width:1150px;">
	<div id="breadbox">
		<div id="contentcolumn" style="margin:0px;">
			<div class="innertube"></div>
		</div>
	</div>
	<div id="querySummary" style="width:700px;">
		<div class="innertube" >
				<span class="enhance">You searched for:</span><br/>
				<c:if test="${not empty recombinaseQueryForm.driver}"><span class="label">Driver</span> equals
					<span class="label">${fn:replace(e:forHtml(recombinaseQueryForm.driver),";", ",") }</span><br/></c:if>
				<c:if test="${not empty recombinaseQueryForm.structures}">
					<b>Activity detected</b>:
                                        <c:forTokens items="${recombinaseQueryForm.structures}" delims="|" var="struct" varStatus="status">
                                            <c:set var = "firstChar" value = "${fn:substring(struct, 0, 1)}" />
                                            <c:set var = "struct2" value = "${fn:substring(struct, 1, fn:length(struct))}" />
                                            <c:if test="${firstChar == '-'}">not </c:if> in 
                                            <b>${e:forHtml(struct2)}</b>
                                            <c:if test="${not status.last}"> and </c:if>
                                        </c:forTokens>
					<c:if test="${not empty recombinaseQueryForm.nowhereElse}">
						and <b>nowhere else</b>
					</c:if>
					<span class="smallGrey"> includes synonyms &amp; substructures</span>
					<br/>
					<span>System(s) in bold contain matching search terms.</span>
					<br/></c:if>

				<c:if test="${not empty recombinaseQueryForm.structure}">
					<b>Activity ${operator}</b> in <b>${e:forHtml(recombinaseQueryForm.structure)}</b>
					<c:if test="${not empty recombinaseQueryForm.nowhereElse}">
						and <b>nowhere else</b>
					</c:if>
					<span class="smallGrey"> includes synonyms &amp; substructures</span>
					<br/>
					<span>System(s) in bold contain matching search terms.</span>
					<br/></c:if>
    	<div class="pageAdvice" style="height: 20px;">
	    	Click column headings to sort table data.
    	</div>
    	<div class="pageAdvice" style="height: 20px;">
	    	Matrix view is not available for all alleles. Please see 
	    	<a href="" onclick="javascript:openUserhelpWindow('RECOMBINASE_summary_help.shtml#nomatrix'); return false;">Help</a> 
			for details.
    	</div>
		</div>
	</div>
	<div id="rightcolumn">
		<div class="innertube">
			<div id="paginationTop">&nbsp;</div>
		</div>
	</div>
	<div style="float: left; clear: left; margin-left: 30px;">
		<div id="filterDiv" style="width: 800px">
			<span id="filterLabel" class="label">Filter alleles by:</span>
			<a id="driverFilter" class="filterButton">Driver&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="inducerFilter" class="filterButton">Inducer&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="systemDetectedFilter" class="filterButton">Detected in System&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
			<a id="systemNotDetectedFilter" class="filterButton">Not Detected in System&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
                        <img style="cursor:help;" title="Not detected means the search will return alleles whose recombinase activity was absent (not detected), as well as alleles whose recombinase activity has not been analyzed or recorded in the database for the specified structure." src="${configBean.WEBSHARE_URL}images/blue_info_icon.gif">
		</div><br />
		<div id="breadbox" style="width: 765px; margin-top: 10px;">
			<div id="filterSummary" class="filters" style="display: none">
				<span class="label">Filtered by:</span>&nbsp; <span id="defaultText" style="display: none;">No filters selected.</span> <span id="filterList"></span><br />
			</div>
		</div>
	</div>

	<div class="facetFilter">
		<div id="facetDialog">
			<div class="hd">Filter</div>
			<div class="facetHelp">
			    <p>Selecting multiple terms will filter for results with any 
				(i.e. logical OR) of the selected terms.</p>
			</div>
			<div class="bd">
				<form:form method="GET"
					action="${configBean.FEWI_URL}recombinase/summary">
					<img src="/fewi/mgi/assets/images/loading.gif">
				</form:form>
			</div>
		</div>
	</div>


</div>

<div id="dynamicdata"></div>

<div id="paginationWrap" style="width:1150px;">
	<div id="paginationBottom">&nbsp;</div>
</div>

<!--
These JS files and local definitions need be defined in this order
-->

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${e:forJavaScript(queryString)}";
	function getQuerystring() {
		return querystring + filters.getUrlFragment();
	}
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/recombinase/recombinase_summary_2.js"></script>

<script type="text/javascript">
	filters.setQueryStringFunction(getQuerystring);
    filters.setFewiUrl("${configBean.FEWI_URL}");
    filters.setHistoryManagement("myDataTable", handleNavigationRaw);
    filters.setDataTable(getRecombinaseDataTable());
    filters.setGeneratePageRequestFunction(generateRequest);
 	filters.addFilter('driverFilter', 'Driver', 'driverFilter', 'driver', fewiurl + 'recombinase/facet/driver');
 	filters.addFilter('inducerFilter', 'Inducer', 'inducerFilter', 'inducer', fewiurl + 'recombinase/facet/inducer');
 	filters.addFilter('systemDetectedFilter', 'Detected in System', 'systemDetectedFilter', 'systemDetected', fewiurl + 'recombinase/facet/systemDetected');
 	filters.addFilter('systemNotDetectedFilter', 'Not Detected in System', 'systemNotDetectedFilter', 'systemNotDetected', fewiurl + 'recombinase/facet/systemNotDetected');
	filters.setSummaryNames('filterSummary', 'filterList');
</script>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
