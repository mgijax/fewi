<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${title}</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<meta name="description" content="${description}"/>

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

#antibodySummaryTable { width: 100%; }
#antibodySummaryTable tr:nth-child(odd) { background-color: #dddddd; }
#antibodySummaryTable th { border: 1px solid gray; padding: 4px; background-color: #EBCA6D; font-weight: bold; }
#antibodySummaryTable td { border: 1px solid gray; padding: 4px; }

#sequenceType { margin-top: 5px; margin-bottom: 5px; }
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
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_antibody_summary_help.shtml">	
    <div id="gxdLogoDiv">
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
    </div>
    <div id="gxdCenteredTitle">
	<span class="titleBarMainTitleGxd">Antibody Summary</span>
	<br>Detail
    </div>
    <div id="headerRightGxd">
    </div>
</div>


<c:if test="${not empty marker}">
  <!-- marker header -->
  <%@ include file="/WEB-INF/jsp/gxd/gxd_marker_header.jsp" %>
</c:if>

<c:if test="${not empty reference}">
  <!-- reference header (copied from sequence summary) -->
  <table class="summaryHeader">
  <tr>
    <td class="summaryHeaderCat1Gxd"><b>Reference</b></td>
    <td class="summaryHeaderData1">
      <a style="font-size:large;  font-weight: bold; padding-bottom:10px;" 
        href="${configBean.FEWI_URL}reference/${reference.jnumID}">
        ${reference.jnumID}
      </a>
      <div style="padding:4px;"> </div>
      ${reference.shortCitation}
    </td>
  </tr>
  </table>
</c:if>

<br clear="all" />
<%@ include file="/WEB-INF/jsp/antibody/antibody_summary_results.jsp" %>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/antibody/antibody_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/jquery.paging.min.js"></script>

<script type="text/javascript">
	var markerID = "${marker.primaryID}";
	var querystring = "${queryString}";
	fewiurl = "${configBean.FEWI_URL}";
	ps_search();
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
