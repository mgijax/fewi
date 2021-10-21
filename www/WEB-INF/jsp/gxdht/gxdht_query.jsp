<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>RNA-Seq and Microarray Experiment ${pageType}</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style>
.left { float: left; }
.right { float: right; }

#resultSummary {border:0;padding-left:5px; text-align: left; font-size: 12px;}
#resultSummary .selected a,
#resultSummary .selected a:focus,
#resultSummary .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#resultSummary .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#resultSummary .yui-nav {border-bottom:solid 1px black;}

#expressionSearch {border:0;padding-left:2px; padding-right:2px; text-align: left; font-size: 12px;}
#expressionSearch .selected a,
#expressionSearch .selected a:focus,
#expressionSearch .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#expressionSearch .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#expressionSearch .yui-nav {border-bottom:solid 1px black;}

table.noborder, table.noborder td , table.noborder th { border: none; }

#standard-qf { padding-top: 5px; }

body.yui-skin-sam .yui-panel .hd,
body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
body.yui-skin-sam .yui-ac-hd {padding: 5px;}
body.yui-skin-sam div#outerGxd {overflow:visible; padding-top: 5px; padding-bottom: 5px; }

.yui-dt table {width: 100%;}

td.yui-dt-col-assayID div.yui-dt-liner span {font-size: 75%;}

.yui-skin-sam .yui-tt .bd
{
	background-color:#ddf;
	color:#005;
	border:2px solid #005;
}

.ui-autocomplete {
	max-height: 250px;
	overflow-y: auto;
	/* prevent horizontal scrollbar */
	overflow-x: hidden;
	font-size:90%;
}
.ui-menu .ui-menu-item a { padding: 0px; line-height: 1; }
ul.ui-autocomplete { padding: 0px; }
</style>
<!--[if IE]>
<style>

#toggleImg{height:1px;}

/*
body.yui-skin-sam div#outer {position:relative;}
#qwrap, #expressionSearch .yui-navset {position:absolute;}
#toggleQF { overflow:auto; }
*/
body.yui-skin-sam div#outerGxd {position:relative;}
#expressionSearch .yui-navset {position:absolute;}
</style>
<![endif]-->

<!--begin custom header content for this example-->
<script type="text/javascript">
    document.documentElement.className = "yui-pe";
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" src="/fewi/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_htexp_index_help.shtml" style="min-width: 1000px">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<div id="pageHeaderWrapper" style='display:inline-block; margin-top: 20px;'>
	<span class="titleBarMainTitleGxd">RNA-Seq and Microarray Experiment ${pageType}</span>
	<c:if test="${pageType == 'Search'}">
	<br/><span class="titleBarSubTitleGxd">Using GXD Metadata Annotations</span>
	</c:if>
	</div>
</div>


<div id="outerGxd">
    <div id="toggleQF" class="summaryControl" style="display:none"><span id="toggleImg" class="qfExpand" style="margin-right:15px; margin-top:0px;" onClick="toggleQueryForm()"></span><span id="toggleLink" class="filterButton" onClick="toggleQueryForm()">Click to modify search</span></div>
    <div id="qwrap">
    	<%@ include file="/WEB-INF/jsp/gxdht/gxdht_form.jsp" %>
    </div>
</div>
<br clear="all" />
<div id="summaryControl" class="summaryControl" style="display: none">
	<div id="resultbar" class="goldbar">Results</div>
	<%@ include file="/WEB-INF/jsp/gxdht/gxdht_summary.jsp" %>
</div>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxdht/gxdht_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxdht/gxdht_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/jquery.paging.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>

<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var assemblyBuild = "${configBean.ASSEMBLY_VERSION}";
</script>

<script type="text/javascript">
gs_setFewiUrl("${configBean.FEWI_URL}");
$(".gxdQf").on("reset",gq_reset);
<c:if test="${not empty queryString}">
  var querystring = "${queryString}";
  var pageType = "${pageType}";
  $('#toggleQF').css({'display' : 'inline'});
  $('#summaryControl').css({'display' : 'inline'});
  hideQF();
  gs_search();
</c:if>
<c:if test="${empty queryString}">
  var querystring = "";
</c:if>

function initializeFilterLibrary(delay) {
	if (window.filtersLoaded) {
		console.log('initializing filters');
		function getQuerystring() {
	  		return querystring + filters.getUrlFragment();
		}
		filters.setFewiUrl(fewiurl);
		filters.setQueryStringFunction(getQuerystring);
		filters.addFilter('variableFilter', 'Variable', 'variableFilter', 'variableFilter', fewiurl + 'gxd/htexp_index/facet/variable');
		filters.addFilter('studyTypeFilter', 'Study Type', 'studyTypeFilter', 'studyTypeFilter', fewiurl + 'gxd/htexp_index/facet/studyType');
		filters.setSummaryNames('filterSummary', 'filterList');
		filters.registerCallback("htCallback", gs_updateRequest);
		filters.registerCallback("htCallback2", gs_logFilters);
		filters.setRemovalDivStyle('block');
	} else {
		setTimeout(function() { initializeFilterLibrary(delay) }, delay);
	}
}
initializeFilterLibrary(250);	// check for filters.js library being loaded every 250ms
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
