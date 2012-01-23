<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.Sequence" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!--begin custom header content for this example-->
<script type="text/javascript">
	document.documentElement.className = "yui-pe";
</script>

<script src="${configBean.FEWI_URL}assets/js/rowexpansion.js"></script>

<title>References</title>

${templateBean.templateBodyStartHtml}

<iframe id="yui-history-iframe" src="${configBean.FEWI_URL}assets/js/blank.html"></iframe>
<input id="yui-history-field" type="hidden">

<!-- begin header bar -->
<div id="titleBarWrapper" userdoc="reference_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References associated with this ${marker.markerType}</span>
</div>
<!-- end header bar -->

<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>

<div id="summary">

	<div id="breadbox">
		<div id="contentcolumn">
			<div class="innertube">
				<div id="filterSummary" class="filters">
					<span class="label">Filters:</span>
					&nbsp;<span id="defaultText"  style="display:none;">No filters selected. Filter these references below.</span>
					<span id="filterList"></span><br/>
					<span id="fCount" style="display:none;"><span id="filterCount">0</span> reference(s) match after applying filter(s)</span>
				</div>
			</div>
		</div>
	</div>

	<div id="querySummary">
		<div class="innertube">
			<span id="totalCount" class="count">0</span> reference(s)<br/>
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
		<span class="label">Filter references by:</span> 
		<a id="authorFilter" class="filterButton">Author <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="journalFilter" class="filterButton">Journal <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="yearFilter" class="filterButton">Year <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a> 
		<a id="curatedDataFilter" class="filterButton">Data <img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8" /></a>
	</div>
	<div id="otherDiv">
		<a id="toggleAbstract" class="filterButton">Show All Abstracts</a> 
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
			</form:form>
		</div>
	</div>
</div>

<script type="text/javascript">
	var fewiurl = "${configBean.FEWI_URL}";
	var querystring = "${queryString}";
	var defaultSort = "${defaultSort}";
</script>


<script type="text/javascript">
	<%@ include file="/js/reference_summary.js" %>
</script>

${templateBean.templateBodyStopHtml}
