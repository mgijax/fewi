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

#polymorphismSummaryTable th { border: 1px solid gray; padding: 4px; background-color: #d0e0f0; font-weight: bold; }
#polymorphismSummaryTable td { border: 1px solid gray; padding: 4px; }
.shaded { background-color: #dddddd; }
.nw { white-space: nowrap; }
.byMarker { width: 100%; }
.header { padding-top: 10px; padding-bottom: 10px; }
.vtop { vertical-align: top; }
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
<div id="titleBarWrapper" userdoc="POLYMORPHISM_help.shtml" style="min-width: 1000px">
	<div id="pageHeaderWrapper" style='display:inline-block; margin-top: 5px;'>
	<span class="titleBarMainTitle">${polymorphismType} Polymorphisms</span>
	</div>
</div>

<c:if test="${not empty marker}">
  <c:set var="cmLocation" value="${marker.preferredCentimorgans}"/>
  <!-- header table -->
  <table class="summaryHeader">
  <tr>
    <td class="summaryHeaderCat1">
         <span class="label">Symbol</span><br/>
         <span class="label">Name</span><br/>
         <span class="label">ID</span><br/>
         <span class="label">Location</span>
    </td>
    <td class="summaryHeaderData1">
      <a href="${configBean.FEWI_URL}marker/${marker.primaryID}" class="symbolLink"><fewi:super value="${marker.symbol}"/></a> 
      	  <c:if test="${marker.status == 'interim'}">(Interim)</c:if><br/>
      <span>${marker.name}</span><br/>
      <span>${marker.primaryID}</span><br/>
      <span>Chr ${marker.chromosome}
      	<c:if test="${(not empty cmLocation) and (cmLocation.cmOffset > 0.0)}">
      		(<fmt:formatNumber type="number" value="${cmLocation.cmOffset}" minFractionDigits="2" maxFractionDigits="2" /> cM)
      	</c:if>
      </span>
  </td>
</tr>
</table>
</c:if>

<div id="resultSummary">
	<div class="header">${fn:length(polymorphisms)} <fewi:plural value="polymorphism" size="${fn:length(polymorphisms)}"/></div>
	<table id="polymorphismSummaryTable" class="byMarker">
		<tr>
			<th>Reference</th>
			<th>Probe</th>
			<th>Endonuclease</th>
			<th>Allele</th>
			<th>Fragments</th>
			<th>Strains</th>
		</tr>
		<c:set var="vtop" value=" CLASS='vtop'"/>

		<c:forEach var="polymorphism" items="${polymorphisms}" varStatus="pStatus">
			<c:set var="rowspan" value=""/>
			<c:if test="${fn:length(polymorphism.alleles) > 1}">
				<c:set var="rowspan" value=" ROWSPAN='${fn:length(polymorphism.alleles)}'"/>
			</c:if>

			<c:set var="shaded" value=""/>
			<c:if test="${(pStatus.count % 2) == 0}">
				<c:set var="shaded" value=" CLASS='shaded'"/>
			</c:if>

			<c:forEach var="detail" items="${polymorphism.alleles}" varStatus="dStatus">
				<c:choose>
					<c:when test="${dStatus.first}">
						<tr${shaded}>
							<td${rowspan}${vtop}><a href="${configBean.FEWI_URL}reference/${polymorphism.jnumID}">${polymorphism.jnumID}</a></td>
							<td${rowspan}${vtop}><a href="${configBean.FEWI_URL}probe/${polymorphism.probeID}">${polymorphism.probeName}</a></td>
							<td${rowspan}${vtop}>${polymorphism.endonuclease}</td>
							<td><fewi:super value="${detail.allele}"/></td>
							<td>${detail.fragments}</td>
							<td><fewi:super value="${detail.strains}"/></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr${shaded}>
							<td><fewi:super value="${detail.allele}"/></td>
							<td>${detail.fragments}</td>
							<td><fewi:super value="${detail.strains}"/></td>
						</tr>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:forEach>
	</table>
</div>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
