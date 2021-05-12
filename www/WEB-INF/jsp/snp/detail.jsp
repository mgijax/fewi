<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<fewi:simpleseo
	title="SNP Detail ${snp.accid}"
	canonical="${configBean.FEWI_URL}snp/${snp.accid}"
	description="${seoDescription}"
	keywords="${seoKeywords}"
/>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/marker_detail.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/marker_detail_new.css" />
<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/snp.css" />

<%
	StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2"); 
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<div id="titleBarWrapper" userdoc="SNP_detail_help.shtml" style="max-width: none;">
	<div name="centeredTitle">
		<span class="titleBarMainTitle"><fewi:super value="${snp.accid}"/></span>
		<span class="titleBar_sub">
			SNP Detail
		</span>
	</div>
</div>

<c:if test="${configBean.snpsOutOfSync == 'true'}">
<style>
#outOfSync { background-color:#FFFFCC; border: 1px solid black; font-size: 0.9em; padding: 5px; }
#outOfSyncLabel { font-size: 1em; font-weight: bold; }
</style>
<div id="outOfSync">
  <span id="outOfSyncLabel">Genome Coordinate Discrepancy</span><BR/>
  The genome coordinates for mouse SNPS shown in the results are from ${assemblyVersion}. The genome coordinates for mouse genes are
  from the most recent
  mouse genome reference assembly (${buildNumber}). Mouse SNP coordinates will be updated after they have been updated to ${buildNumber} by
  the European Variation Archive (EVA) (<a href="${configBean.USERHELP_URL}SNP_discrepancy_help.shtml" target="_blank">see details</a>).
</div>
</c:if>

<div class="container detailStructureTable">

	<%@ include file="SNPDetail_Summary.jsp" %>
	<%@ include file="SNPDetail_GenomeLocation.jsp" %>
	<%@ include file="SNPDetail_Assays.jsp" %>
	<%@ include file="SNPDetail_Markers.jsp" %>

</div>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
