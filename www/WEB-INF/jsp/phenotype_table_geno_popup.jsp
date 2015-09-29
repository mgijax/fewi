<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%
   StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
   StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<c:set var="markerSymbols" value=""/>
<c:forEach var="marker" items="${genotype.mutatedMarkers}">
    <c:set var="markerSymbols" value="${markerSymbols} ${marker.symbol}"/>
</c:forEach>

<fewi:simpleseo title="Phenotypes for${markerSymbols} ${genotype.primaryID} MGI Mouse"
	description="${seodescription}"
	keywords="${seokeywords}"
/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<%@ include file="phenotype_table_geno_popup_imports.jsp" %>

<fewi:pagetitle title="Phenotypes Associated with This Genotype" userdoc="ALLELE_detail_pheno_summary_help.shtml#see_annot" />

<%@ include file="phenotype_table_geno_popup_header.jsp" %>

<%@ include file="phenotype_table_geno_popup_content.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
