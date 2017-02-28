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

#mappingSummaryTable tr:nth-child(odd) { background-color: #dddddd; }
#mappingSummaryTable th { border: 1px solid gray; padding: 4px; background-color: #d0e0f0; font-weight: bold; }
#mappingSummaryTable td { border: 1px solid gray; padding: 4px; }
.nw { white-space: nowrap; }
.byMarker { width: 100%; }
.byReference { width: 60%; }
.header { padding-top: 10px; padding-bottom: 10px; }
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
<div id="titleBarWrapper" userdoc="mapping_experimental_data_help.shtml#mapsum" style="min-width: 1000px">
	<div id="pageHeaderWrapper" style='display:inline-block; margin-top: 5px;'>
	<span class="titleBarMainTitle">Mapping Data Summary</span>
	</div>
</div>

<c:if test="${not empty marker}">
  <!-- marker header -->
  <c:set var="tableClass" value="byMarker"/>
  <!-- header table -->
  <table class="summaryHeader">
  <tr>
    <td class="summaryHeaderCat1${isGxd}">
         <span class="label">Symbol</span><br/>
         <span class="label">Name</span><br/>
         <span class="label">ID</span><br/>
         <span class="label">Chromosome</span>
    </td>
    <td class="summaryHeaderData1">
      <a href="${configBean.FEWI_URL}marker/${marker.primaryID}" class="symbolLink"><fewi:super value="${marker.symbol}"/></a> 
      	  <c:if test="${marker.status == 'interim'}">(Interim)</c:if><br/>
      <span>${marker.name}</span><br/>
      <span>${marker.primaryID}</span><br/>
      <span>${marker.chromosome}</span>
  </td>
</tr>
</table>
</c:if>

<c:set var="fromJnum" value=""/>
<c:if test="${not empty reference}">
  <c:set var="fromJnum" value="${reference.jnumID}"/>
  <!-- reference header (copied from sequence summary) -->
  <table class="summaryHeader">
  <tr>
    <td class="summaryHeaderCat1"><b>Reference</b></td>
    <td class="summaryHeaderData1">
      <a style="font-size:large;  font-weight: bold; padding-bottom:10px;" href="${configBean.FEWI_URL}reference/${reference.jnumID}">
        ${reference.jnumID}
      </a>
      <div style="padding:4px;"> </div>
      ${reference.shortCitation}
    </td>
  </tr>
  </table>
  <c:set var="tableClass" value="byReference"/>
</c:if>

<div id="resultSummary">
	<div class="header">${fn:length(experiments)} mapping <fewi:plural value="experiment" size="${fn:length(experiments)}"/></div>
	<table id="mappingSummaryTable" class="${tableClass}">
		<tr>
			<th>Experiment Type</th>
			<th>Details</th>
			<th>Chromosome</th>
			<c:if test="${empty fromJnum}"><th>Reference</th></c:if>
		</tr>
		<c:forEach var="experiment" items="${experiments}" varStatus="status">
			<tr>
				<td class='nw'><a href="${configBean.FEWI_URL}mapping/${experiment.primaryID}"><fewi:super value="${experiment.type}"/></a></td>
				<td class='nw'><c:forEach var="detail" items="${experiment.details}" varStatus="cStatus">
						${detail}<br/>
					</c:forEach>
				</td>
				<td>${experiment.chromosome}</td>
				<c:if test="${empty fromJnum}">
					<td><a href="${configBean.FEWI_URL}reference/${experiment.jnumID}">${experiment.jnumID}</a>
						${experiment.citation}</td>
				</c:if>
			</tr>
		</c:forEach>
	</table>
</div>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
