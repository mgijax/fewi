<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="${seoTitle}"
	canonical="${configBean.FEWI_URL}strain/${strain.primaryID}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/strain/strain_detail.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="STRAIN_detail_help.shtml" style="max-width: none;">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="largeButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${strain.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
	<div name="centeredTitle">
		<span class="titleBarMainTitle"><fewi:super value="${strain.name}"/></span>
		<span class="titleBar_sub">
			Strain Detail
		</span>
	</div>
</div>

<div class="container detailStructureTable">
	<%@ include file="strain_detail_summary_ribbon.jsp" %>
</div>

<c:if test="${not empty strain.description}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_description_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${not empty strain.snpRows}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_snp_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${(not empty strain.mutations) or (not empty strain.qtls)}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_mutation_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${(not empty strain.diseases) or (not empty strain.gridCells)}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_disease_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${not empty strain.imsrData}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_imsr_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${not empty strain.referenceAssociations}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_reference_ribbon.jsp" %>
	</div>
</c:if>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/strain/strain_detail.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/yoyo.js"></script>
<script>
	initialize('${configBean.FEWI_URL}', '${strain.primaryID}');
<c:if test="${not empty strain.snpRows}">
	loadSnpTable('strain', 'all');
</c:if>
</script>

<!--	close page template -->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
