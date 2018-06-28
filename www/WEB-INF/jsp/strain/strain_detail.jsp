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

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/strain_detail.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<script TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="STRAIN_detail_help.shtml" style="max-width: none;">
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

<c:if test="${(not empty strain.mutations) or (not empty strain.qtls)}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_mutation_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${not empty strain.imsrData}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_imsr_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${(not empty strain.diseases) or (not empty strain.gridCells)}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_disease_ribbon.jsp" %>
	</div>
</c:if>

<c:if test="${not empty strain.referenceAssociations}">
	<div class="container detailStructureTable">
		<%@ include file="strain_detail_reference_ribbon.jsp" %>
	</div>
</c:if>

<!--	close page template -->
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
