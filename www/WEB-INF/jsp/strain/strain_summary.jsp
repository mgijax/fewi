<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${title}</title>
<meta http-equiv="X-UA-Compatible" content="chrome=1">
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />
<meta name="description" content="${description}"/>
<meta name="keywords" content="${keywords}"/>

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

#strainSummaryTable { width: 100%; }
#strainSummaryTable tr:nth-child(odd) { background-color: #dddddd; }
#strainSummaryTable th { border: 1px solid gray; padding: 4px; background-color: #d0e0f0; font-weight: bold; }
#strainSummaryTable td { border: 1px solid gray; padding: 4px; }

#ysf { float: left; margin-right: 15px; }
.ysf { font-weight: bold; text-decoration: underline; font-size: 100%; }
.smallGray { font-size: 75%; color: #999999; }
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
<div id="titleBarWrapper" userdoc="STRAIN_summary_help.shtml" style="min-width: 1000px">
	<div id="pageHeaderWrapper" style='display:inline-block; margin-top: 5px;'>
	<span class="titleBarMainTitle">Strain Summary</span>
	</div>
</div>

<c:if test="${not empty reference}">
<!-- if we came from a reference page, we need a special reference header (taken from marker_summary_reference) -->
	<table class="summaryHeader">
	<tr>
  		<td class="summaryHeaderCat1"><b>Reference</b></td>
  		<td class="summaryHeaderData1">
  			<a style="font-size:x-large;  font-weight: bold; padding-bottom:10px;"
  				href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
  			<div style="padding:4px;"> </div>
  			${reference.shortCitation}
  		</td>
  	</tr>
  	</table>
  	<script>$('.titleBarMainTitle').html('<b>Strain Summary for Reference</b>');</script>
</c:if>

<br clear="all" />
<%@ include file="/WEB-INF/jsp/strain/strain_summary_results.jsp" %>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/strain/strain_summary.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/jquery.paging.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/filters.js"></script>

<script type="text/javascript">
	var querystring = "${e:forJavaScript(queryString)}";
	fewiurl = "${configBean.FEWI_URL}";
	ss_search();
	<c:if test="${not empty reference}">
		$('#ysf').css('display', 'none');
	</c:if>
	
	function getQuerystring() {
	  return querystring + filters.getUrlFragment();
	}
	filters.setFewiUrl(fewiurl);
	filters.setQueryStringFunction(getQuerystring);
	filters.addFilter('attributeFilter', 'Attributes', 'attributeFilter', 'attributeFilter', fewiurl + 'strain/facet/attribute');
	filters.setSummaryNames('filterSummary', 'filterList');
	filters.registerCallback("strainCallback", updateRequest);
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
