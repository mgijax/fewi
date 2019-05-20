<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>cDNA Clone Source Data Summary for ${marker.symbol}</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<meta name="description" content="cDNA clones for a gene that includes Source Age, Source Tissue, and Source Cell Line"/>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style>
.left { float: left; }
.right { float: right; }

#resultSummary {border:0;padding-right:5px; text-align: left; font-size: 12px; width: 100%; }
#resultSummary .selected a,
#resultSummary .selected a:focus,
#resultSummary .selected a:hover{ margin-left: 0px;border:1px solid #808080;border-bottom:solid 1px #eee; color:black; background:none; background-color:#eee;}
#resultSummary .yui-content{background-color:#eee;border:1px solid #808080; border-top: none;}
#resultSummary .yui-nav {border-bottom:solid 1px black;}

table.noborder, table.noborder td , table.noborder th { border: none; }

body.yui-skin-sam .yui-panel .hd,
body.yui-skin-sam .yui-ac-hd { background:none; background-color:#025; color:#fff; font-weight: bold;}
body.yui-skin-sam .yui-ac-hd {padding: 5px;}
body.yui-skin-sam div#outerGxd {overflow:visible; padding-top: 5px; padding-bottom: 5px; }

.yui-dt table {width: 100%;}

td.yui-dt-col-assayID div.yui-dt-liner span {font-size: 75%;}
td.summaryHeaderCat1Gxd { font-weight: bold; }

.yui-skin-sam .yui-tt .bd
{
	background-color:#ddf;
	color:#005;
	border:2px solid #005;
}

.ui-menu .ui-menu-item a { padding: 0px; line-height: 1; }

#cdnaSummaryTable { width: 100%; }
#cdnaSummaryTable tr:nth-child(odd) { background-color: #dddddd; }
#cdnaSummaryTable th { border: 1px solid gray; padding: 4px; background-color: #ebca6d; font-weight: bold; }
#cdnaSummaryTable td { border: 1px solid gray; padding: 4px; }
</style>

<!--[if IE]>
<style>
#toggleImg{height:1px;}
body.yui-skin-sam div#outerGxd {position:relative;}
</style>
<![endif]-->

<!--begin custom header content for this example-->
<script type="text/javascript">
    document.documentElement.className = "yui-pe";
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- iframe for history manager's use -->
<iframe id="yui-history-iframe" src="/fewi/blank.html"></iframe>
<input id="yui-history-field" type="hidden">


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_cDNA_summary_help.shtml" style="min-width: 1000px">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<div id="pageHeaderWrapper" style='display:inline-block; margin-top: 20px;'>
	<span class="titleBarMainTitleGxd">cDNA Clone Source Data Summary</span>
	</div>
</div>

<%@ include file="/WEB-INF/jsp/gxd/gxd_marker_header.jsp" %>

<br clear="all" />
<%@ include file="/WEB-INF/jsp/gxd/cdna_summary.jsp" %>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd/cdna_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/jquery.paging.min.js"></script>

<script type="text/javascript">
	var markerID = "${marker.primaryID}";
	var querystring = "${e:forJavaScript(querystring)}";
	if (querystring == "") {
		querystring = "markerID=${marker.primaryID}";
	}
	fewiurl = "${configBean.FEWI_URL}";
	cs_search();
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
