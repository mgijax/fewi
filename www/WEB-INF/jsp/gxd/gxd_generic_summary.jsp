<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<c:if test="${empty fromSlimgrid}">
  <c:set var="pageTitle" value="Mouse Gene Expression Data Search"/>
</c:if>
<c:if test="${not empty fromSlimgrid}">
  <c:set var="pageTitle" value="${marker.symbol} ${structure} gene expression"/>
</c:if>
<title>${e:forHtml(pageTitle)}</title>

<style>
.left { float: left; }
.right { float: right; }

#resultSummary {border:0;padding-left:5px; text-align: left; font-size: 12px;}
#resultSummary .selected a,
#resultSummary .selected a:focus,
#resultSummary .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#resultSummary .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#resultSummary .yui-nav {border-bottom:solid 1px black;}

#expressionSearch {border:0;padding-left:5px; text-align: left; font-size: 12px;}
#expressionSearch .selected a,
#expressionSearch .selected a:focus,
#expressionSearch .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#expressionSearch .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#expressionSearch .yui-nav {border-bottom:solid 1px black;}

table.noborder, table.noborder td , table.noborder th { border: none; }

#gxdQueryForm table tr td table tr td ul li span { font-size: 10px; font-style: italic; }

body.yui-skin-sam .yui-panel .hd,
body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
body.yui-skin-sam .yui-ac-hd {padding: 5px;}
body.yui-skin-sam div#outer {overflow:visible;}

.yui-dt table {width: 100%;}

td.yui-dt-col-assayID div.yui-dt-liner span {font-size: 75%;}

.yui-skin-sam .yui-tt .bd
{
	background-color:#ddf; 
	color:#005;
	border:2px solid #005;
}

.yui-skin-sam th.yui-dt-asc .yui-dt-liner { 
	    background:none;
} 
.yui-skin-sam th.yui-dt-desc .yui-dt-liner { 
	    background:none; 
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
body.yui-skin-sam div#outer {position:relative;}
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

<% // special help link for slimgrid %>
<c:set var="helpHash" value="#summary"/>
<c:if test="${not (empty marker or empty structure or empty structureId)}">
  <c:set var="helpHash" value="#tissuematrix"/>
</c:if>

<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_help.shtml${helpHash}">	
		<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Data</span>
</div>

<!-- include a marker header, if needed -->
<%@ include file="/WEB-INF/jsp/gxd/gxd_marker_header.jsp" %>

<!-- GXD Summary -->
<div class="summaryControl" style="">
	<%@ include file="/WEB-INF/jsp/gxd/gxd_summary.jsp" %>
</div>

<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var mgiMarkerId = "${e:forJavaScript(marker.primaryID)}";
    var searchedStage = "${e:forJavaScript(theilerStage)}";
    var searchedAssayType = "${e:forJavaScript(assayType)}";
    var searchedStructure = "${e:forJavaScript(structure)}";
    var searchedStructureId = "${e:forJavaScript(structureId)}";
    var nomenclature = "${e:forJavaScript(nomenclature)}";
    var querystring = "${e:forJavaScript(queryString)}";
    var assemblyBuild = "${configBean.ASSEMBLY_VERSION}";
</script>

<%@ include file="/WEB-INF/jsp/gxd/gxd_summary_js.jsp" %>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_by_marker_query.js">
</script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_filters.js"></script>
<script type="text/javascript">
prepFilters();
// Wait a few seconds for data retrieval, then alter the querystring so we'll be able to delete filters
// as desired.
setTimeout(function() {
	querystring = querystring.replaceAll(/ *&structureIDFilter=EMAPA:[0-9]*/g, ''); 
	}, 2000);
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
