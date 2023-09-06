<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Mouse Gene Expression Data Search</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style>
.left { float: left; }
.right { float: right; }

.autofilter { text-decoration: underline; color: blue; cursor: pointer; }
.autofilter:hover { text-decoration: none; color: #c00; background-color: #C7E3FE; }

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


body.yui-skin-sam .yui-panel .hd,
body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
body.yui-skin-sam .yui-ac-hd {padding: 5px;}
body.yui-skin-sam div#outerGxd {overflow:visible;}

.yui-dt table {width: 100%;}

td.yui-dt-col-assayID div.yui-dt-liner span {font-size: 75%;}

.yui-skin-sam .yui-tt .bd
{
	background-color:#ddf;
	color:#005;
	border:2px solid #005;
}
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
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_help.shtml">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Data</span>
</div>


<div id="outerGxd">
    <div id="toggleQF" class="summaryControl" style="display:none"><span id="toggleImg" class="qfExpand" style="margin-right:15px; margin-top:0px;"></span><span id="toggleLink" class="filterButton">Click to modify search</span></div>
    <div id="qwrap">
    	<%@ include file="/WEB-INF/jsp/gxd/gxd_form.jsp" %>
    </div>
</div>
<br clear="all" />
<div class="summaryControl" style="display:none;">
<div id="resultbar" class="goldbar">Results</div>
	<%@ include file="/WEB-INF/jsp/gxd/gxd_summary.jsp" %>
	<div id="nowhereElseMessage" style="display:none; width:900px"></div>
</div>

<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var assemblyBuild = "${configBean.ASSEMBLY_VERSION}";
</script>

<%@ include file="/WEB-INF/jsp/gxd/gxd_summary_js.jsp" %>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_query.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_filters.js"></script>

<c:if test="${showDifferentialQueryForm}">
<script>
$(function(){
	// open the differential query form
	showDifferentialForm();
});
</script>
</c:if>

<c:if test="${showProfileQueryForm}">
<script>
$(function(){
	// open the differential query form
	showProfileSearchForm();
});
</script>
</c:if>

<c:if test="${showBatchSearchForm}">
<script>
$(function(){
	// open the batch search tab
	showBatchSearchForm();
});
</script>
</c:if>

<script type="text/javascript">
prepFilters();
<c:if test="${not empty queryString}">
var querystring = "${queryString}";
</c:if>
<c:if test="${not empty markerIDs}">
$(function() {
	// fill in marker IDs from quick search
	$('#ids')[0].value = "${markerIDs}";
	// and execute the search
	$('#submit6')[0].click();
});
</c:if>

// hack to handle the odd case where we propagate structure filter values in from the Quick Search --
// If we don't remove the hidden fields and do an initial call to 'remove', then we need to click the
// remove buttons twice.
if ($('[name=structureIDFilter]').length > 0) {
	// need to wait a second for requests to be issued before removing the extra structure filter pieces...
	setTimeout(function() {
	    $('[name=structureIDFilter]').remove();
    	$('#clearFilter')[0].click();
	}, 1000);
}
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
