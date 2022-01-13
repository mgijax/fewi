<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Marker marker = (Marker)request.getAttribute("marker");
   NotesTagConverter ntc = new NotesTagConverter("GO");%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<c:if test="${empty headerTerm}">
<title>Gene Ontology Classifications</title>
<c:set var="helpHash" value=""/>
</c:if>
<c:if test="${not empty headerTerm}">
<title>${marker.symbol} ${e:forHtml(headerTerm)} gene ontology</title>
<c:set var="helpHash" value="#tabular"/>
</c:if>

<style type="text/css">
.yui-skin-sam .yui-dt th{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1300px;
}
.yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1400px;
}
.facetFilter .yui-panel .bd {
	width: 285px
}
</style>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/go_summary.css" />


<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GO_classification_report_help.shtml${helpHash}">
	<span class="titleBarMainTitle">Gene Ontology Classifications</span>
</div>

<jsp:include page="marker_header.jsp"></jsp:include>

<c:if test="${empty headerTerm}">
<br>
<div class="GO">
<a name="text"></a><h3 class="extraLarge"><b>Go Annotations as Summary Text</b> <a href="#tabular" class="GO">(Tabular View)</a>
<c:if test="${marker.hasGOGraph == 1}"><a href="${configBean.FEWI_URL}marker/gograph/${marker.primaryID}" class="GO">(GO Graph)</a></c:if></h3>

<%=ntc.convertNotes(marker.getGOText(), '|')%><br><hr><br>
</div>
</c:if>

<span id="tableTop"></span>
<c:if test="${not empty headerTerm}">
<style>
div.headerTerm {
    background-color: #dfefff;
    border: thin solid #002255;
    font-family: Verdana,Arial,Helvetica;
    font-size: 20px;
    margin-bottom: 12px;
    min-height: 32px;
    min-width: 500px;
    padding: 3px;
}
div.allAnnot {
    float: right; font-size: 12px; font-weight: bold; padding-top: 8px;
}
</style>
<div class="headerTerm">Gene Ontology (GO) annotations for ${e:forHtml(headerTerm)}
<div id="allClassificationsLink" class="allAnnot">
All GO annotations for ${marker.symbol} (<a href="${configBean.FEWI_URL}go/marker/${marker.primaryID}#tabular">${marker.countOfGOTerms}</a>)
</div>
</div>
</c:if>
<c:if test="${empty headerTerm}">
<a name="tabular"></a><h3 class="extraLarge"><b>Go Annotations in Tabular Form</b>
<a href="#text" class="GO">(Text View)</a>
<c:if test="${marker.hasGOGraph == 1}"><a href="${configBean.FEWI_URL}marker/gograph/${marker.primaryID}" class="GO">(GO Graph)</a></c:if>
</h3>
</c:if>

<!-- paginator -->
<div id="summary" style="width:1080px;">
	<div id="breadbox">
		<div id="contentcolumn" style="margin: 0 0 0 0;">
			<div class="innertube">
				<div style="width: 470px; margin-top: 20px;">
					<span id="filterLabel" class="label">Filter annotations by:</span>
					<a id="aspectFilterMenu" class="filterButton">Aspect&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
					<a id="categoryFilterMenu" class="filterButton">Category&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
					<a id="evidenceFilterMenu" class="filterButton">Evidence&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
					<!-- Implemented by ref id.  oblod <a id="referenceMenu" class="filterButton">Reference&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> -->
				</div><br />
				<div id="breadbox" style="width: 765px; margin-top: 10px;">
					<div id="filterSummary" class="filters" style="display: none">
						<span class="label">Filtered by:</span>&nbsp; <span id="defaultText" style="display: none;">No filters selected.</span> <span id="filterList"></span><br />
					</div>
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

<div id="toolbar" class="bluebar" style="width:1453px;">
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

<jsp:include page="go_summary_legend.jsp"></jsp:include>

   <div class="facetFilter">
      <div id="facetDialog">
         <div class="hd">Filter</div>
         <div class="bd">
            <form:form method="GET" action="${configBean.FEWI_URL}go/marker/${marker.primaryID}">
               <img src="/fewi/mgi/assets/images/loading.gif">
            </form:form>
         </div>
      </div>
   </div>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>
<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${e:forJavaScript(queryString)}";
	function getQuerystring() {
		return querystring + filters.getUrlFragment();
	}

</script>

<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/go_summary_marker.js"></script>
<script type="text/javascript">
    filters.setQueryStringFunction(getQuerystring);
    filters.setFewiUrl("${configBean.FEWI_URL}");
    filters.setHistoryManagement("myDataTable", handleNavigationRaw);
    filters.setDataTable(getPageDataTable());
    filters.setGeneratePageRequestFunction(generatePageRequest);
    filters.addFilter('aspectFilter', 'Aspect', 'aspectFilterMenu', 'aspectFilter', fewiurl + 'go/facet/aspect');
    filters.addFilter('categoryFilter', 'Category', 'categoryFilterMenu', 'categoryFilter', fewiurl + 'go/facet/category');
    filters.addFilter('evidenceFilter', 'Evidence', 'evidenceFilterMenu', 'evidenceFilter', fewiurl + 'go/facet/evidence');
    filters.addFilter('referenceFilter', 'Reference', 'referenceFilterMenu', 'referenceFilter', fewiurl + 'go/facet/reference');
    filters.setSummaryNames('filterSummary', 'filterList');
    filters.registerCallback('scrollUp', scrollToTableTop);
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

