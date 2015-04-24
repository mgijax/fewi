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

${templateBean.templateBodyStartHtml}


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GO_classification_report_help.shtml">	
	<span class="titleBarMainTitle">Gene Ontology Classifications</span>
</div>

<jsp:include page="marker_header.jsp"></jsp:include><br>

<div class="GO">
<a name="text"></a><h3 class="extraLarge"><b>Go Annotations as Summary Text</b> <a href="#tabular" class="GO">(Tabular View)</a>
<c:if test="${marker.hasGOGraph == 1}"><a href="${configBean.JAVAWI_URL}WIFetch?page=GOMarkerGraph&id=${marker.primaryID}" class="GO">(GO Graph)</a></c:if></h3>

<%=ntc.convertNotes(marker.getGOText(), '|')%><br><hr><br>
</div>

<a name="tabular"></a><h3 class="extraLarge"><b>Go Annotations in Tabular Form</b> <a href="#text" class="GO">(Text View)</a>
<c:if test="${marker.hasGOGraph == 1}"><a href="${configBean.JAVAWI_URL}WIFetch?page=GOMarkerGraph&id=${marker.primaryID}" class="GO">(GO Graph)</a></c:if></h3>

<!-- paginator -->
<div id="summary" style="width:1080px;">
	<div id="breadbox">
		<div id="contentcolumn" style="margin: 0 0 0 0;">
			<div class="innertube">
				<div style="width: 470px; margin-top: 20px;">
					<span id="filterLabel" class="label">Filter Markers by:</span>
					<a id="categoryFilterMenu" class="filterButton">Category&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
					<a id="evidenceFilterMenu" class="filterButton">Evidence Code&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
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

<div id="toolbar" class="bluebar" style="width:1065px;">
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

<div class="GO">
<br>
<hr><b>
Gene Ontology Evidence Code Abbreviations:</b><br><br>
<table>
<tbody>
<tr><td>&nbsp;&nbsp;<b>EXP</b> Inferred from experiment</td></tr>

<tr><td>&nbsp;&nbsp;<b>IAS</b> Inferred from ancestral sequence</td></tr>
<tr><td>&nbsp;&nbsp;<b>IBA</b> Inferred from biological aspect of ancestor</td></tr>
<tr><td>&nbsp;&nbsp;<b>IBD</b> Inferred from biological aspect of descendant</td></tr>
<tr><td>&nbsp;&nbsp;<b>IC</b> Inferred by curator</td></tr>
<tr><td>&nbsp;&nbsp;<b>IDA</b> Inferred from direct assay</td></tr>
<tr><td>&nbsp;&nbsp;<b>IEA</b> Inferred from electronic annotation</td></tr>
<tr><td>&nbsp;&nbsp;<b>IGI</b> Inferred from genetic interaction</td></tr>
<tr><td>&nbsp;&nbsp;<b>IKR</b> Inferred from key residues</td></tr>
<tr><td>&nbsp;&nbsp;<b>IMP</b> Inferred from mutant phenotype</td></tr>
<tr><td>&nbsp;&nbsp;<b>IMR</b> Inferred from missing residues</td></tr>
<tr><td>&nbsp;&nbsp;<b>IPI</b> Inferred from physical interaction</td></tr>
<tr><td>&nbsp;&nbsp;<b>IRD</b> Inferred from rapid divergence</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISS</b> Inferred from sequence or structural similarity</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISO</b> Inferred from sequence orthology</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISA</b> Inferred from sequence alignment</td></tr>
<tr><td>&nbsp;&nbsp;<b>ISM</b> Inferred from sequence model</td></tr>
<tr><td>&nbsp;&nbsp;<b>NAS</b> Non-traceable author statement</td></tr>


<tr><td>&nbsp;&nbsp;<b>ND</b> No biological data available</td></tr>
<tr><td>&nbsp;&nbsp;<b>RCA</b> Reviewed computational analysis</td></tr>
<tr><td>&nbsp;&nbsp;<b>TAS</b> Traceable author statement</td></tr>
</tbody></table>
<hr>
</div>

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
	var querystring = "${queryString}";
	function getQuerystring() {
		return querystring + filters.getUrlFragment();
	}

</script>

<!-- including this file will start the data injection -->
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/go_summary_marker.js"></script>
<script type="text/javascript">
    filters.setQueryStringFunction(getQuerystring);
    filters.setSummaryNames('filterSummary', 'filterList');
    filters.setFewiUrl("${configBean.FEWI_URL}");
    filters.setHistoryManagement("myDataTable", handleNavigationRaw);
    filters.setDataTable(getPageDataTable());
    filters.setGeneratePageRequestFunction(generatePageRequest);
    filters.addFilter('categoryFilter', 'Category', 'categoryFilterMenu', 'categoryFilter', fewiurl + 'go/facet/category');
    filters.addFilter('evidenceFilter', 'Evidence', 'evidenceFilterMenu', 'evidenceFilter', fewiurl + 'go/facet/evidence');
    filters.addFilter('referenceFilter', 'Reference', 'referenceFilterMenu', 'referenceFilter', fewiurl + 'go/facet/reference');
</script>

${templateBean.templateBodyStopHtml}

