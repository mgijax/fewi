<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${marker.symbol} MGI Mouse ${marker.markerType} Detail - ${marker.primaryID} - ${marker.name}"
	canonical="${configBean.FEWI_URL}marker/${marker.primaryID}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/marker_detail.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/marker_detail_new.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/marker_detail.js"></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_detail_help.shtml" style="max-width: none;">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${marker.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
	<div name="centeredTitle">
		<span class="titleBarMainTitle"><fewi:super value="${marker.symbol}"/></span>
		<span class="titleBar_sub">
			${marker.markerType} Detail
		</span>
	</div>
</div>

<div class="container detailStructureTable">

	<%@ include file="MarkerDetail_SummaryRibbon.jsp" %>
	<%@ include file="MarkerDetail_LocationRibbon.jsp" %>
	<%@ include file="MarkerDetail_HomologyRibbon.jsp" %>
	<%@ include file="MarkerDetail_DiseaseRibbon.jsp" %>
	<%@ include file="MarkerDetail_AlleleRibbon.jsp" %>
	<%@ include file="MarkerDetail_GORibbon.jsp" %>
	<%@ include file="MarkerDetail_ExpressionRibbon.jsp" %>

	<%@ include file="MarkerDetail_InteractionRibbon.jsp" %>
	<%@ include file="MarkerDetail_MolecularReagentRibbon.jsp" %>
	<%@ include file="MarkerDetail_DatabaseLinkRibbon.jsp" %>
	<%@ include file="MarkerDetail_SequenceRibbon.jsp" %>
	<%@ include file="MarkerDetail_PolymorphismRibbon.jsp" %>
	<%@ include file="MarkerDetail_ProteinRibbon.jsp" %>
	<%@ include file="MarkerDetail_ReferenceRibbon.jsp" %>
	<%@ include file="MarkerDetail_OtherAccessRibbon.jsp" %>

</div>

<!-- Elements not part of page structure that are hidden by default -->
<div class="hiddenItems">
	<!-- Cluster Relationship items -->
	<c:if test="${hasClusterMembers}">
		<form style='display:none;' id="batchWebForm" name='batchWeb' enctype='multipart/form-data' target='_blank' method='post' action='${configBean.FEWI_URL}batch/summary'>
			<input name='idType' value='current symbol' type='hidden'>
			<input name='attributes' value='Nomenclature' type='hidden'>
			<input name='attributes' value='Location' type='hidden'>
			<input name='ids' value='${memberSymbols}' id='batchSymbolListWeb' type='hidden'>
		</form>
	
		<div id="clusterMemberDiv" class="" style="visibility:hidden;">
			<div class="hd"> ${marker.symbol} Cluster contains:
				<div style="float:right; margin-right:25px"><span id="clusterBatchLink" style="cursor: pointer; text-decoration: underline; color: #0000ff">More Data</span> for these features</div>
			</div>
			<div class="bd" style="overflow:auto">
				<table id="clusterMemberTable">
					<tr><td class="bold leftAlign allBorders">Member</td><td class="bold leftAlign allBorders">Feature Type</td><td class="bold leftAlign allBorders">Location</td></tr>
					<c:forEach var="member" items="${marker.clusterMembers}">
						<tr><td class="leftAlign allBorders"><a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a>, ${member.relatedMarkerName}</td>
							<td class="leftAlign allBorders">${member.relatedMarkerFeatureType}</td>
							<td class="leftAlign allBorders">${member.relatedMarkerLocation}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</c:if>
</div>

<script language="Javascript">
YAHOO.namespace("gxd.container");
YAHOO.gxd.container.anatomyHelp = new YAHOO.widget.Panel("sgAnatomyHelp", { width:"400px", draggable:false, visible:false, constraintoviewport:true } );
YAHOO.gxd.container.anatomyHelp.render();
YAHOO.util.Event.addListener("sgAnatomyHelpImage", "click", YAHOO.gxd.container.anatomyHelp.show, YAHOO.gxd.container.anatomyHelp, true);

YAHOO.namespace("mp.container");
YAHOO.mp.container.phenoHelp = new YAHOO.widget.Panel("sgPhenoHelp", { width:"360px", draggable:false, visible:false, constraintoviewport:true } );
YAHOO.mp.container.phenoHelp.render();
YAHOO.util.Event.addListener("sgPhenoHelpImage", "click", YAHOO.mp.container.phenoHelp.show, YAHOO.mp.container.phenoHelp, true);
</script>

<!--	close page template -->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
