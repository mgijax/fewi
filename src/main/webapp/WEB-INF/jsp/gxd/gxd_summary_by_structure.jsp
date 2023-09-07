<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Mouse Gene Expression Data Search</title>

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


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_help.shtml#summary">	
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Data</span>
</div>



<!-- marker header table -->
<table class="summaryHeader">
	<tr >
	  <td class="summaryHeaderCat1Gxd" style="vertical-align:bottom">
		  <div style="padding-top:7px; font-weight: bold"><b>Structure</b></div>
	       <div style="padding-top:3px; font-weight: bold">Present at</div>
	       <div style="padding-top:2px; font-weight: bold">ID</span>
	  </td>
	  <td class="summaryHeaderData1" style="vertical-align:bottom">
	    <div style="">
		<a style="font-size:large;  font-weight: bold;" href="${configBean.FEWI_URL}vocab/gxd/anatomy/${structure.primaryID}">${e:forHtml(structure.term)}</a>
		<span style="color: gray; font-size: 90%">includes substructures</span></div>
	    <div style="padding-top:3px;">${structure.stageRange}</div>
	    <div style="padding-top:2px;">${structure.primaryID}</div>
	  </td>
	</tr>
</table>


<!-- GXD Summary -->
<div class="summaryControl" style="">
	<%@ include file="/WEB-INF/jsp/gxd/gxd_summary.jsp" %>
</div>

<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var querystring = "structureID=${e:forJavaScript(structure.primaryID)}&tab=${e:forHtml(tab)}";
    var assemblyBuild = "${configBean.ASSEMBLY_VERSION}";
</script>

<%@ include file="/WEB-INF/jsp/gxd/gxd_summary_js.jsp" %>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_filters.js"></script>
<script type="text/javascript">
prepFilters();
</script>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
