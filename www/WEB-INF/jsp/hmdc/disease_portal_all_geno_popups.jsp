<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "mgi.frontend.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
<fewi:simpleseo
   title="Page title, Sue?"
   description="Page description TBD"
   keywords="Keywords TBD, phenotypes, genotypes, genetic background, MP, Mammalian Phenotype, mouse, mice, murine, Mus"
/>

<%@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" %>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_imports.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<fewi:pagetitle title="Page title, Sue?" userdoc="ALLELE_detail_pheno_summary_help.shtml#see_annot" />

<c:forEach var="genotype" items="${genoCluster.genotypes}" varStatus="genoStatus">

<div style="overflow:hidden;">
	<!-- Set all the values that the genoview jsp expects -->
	<c:set var="genotype" value="${genotype}" scope="request"/>
	<c:set var="counter" value="${''}"  scope="request"/>
	<c:set var="mpSystems" value="${genotype.MPSystems }"  scope="request"/>
	<c:set var="hasImage" value="${genotype.hasPrimaryImage }"  scope="request"/>
	<c:set var="hasDiseaseModels" value="${not empty genotype.diseases }"  scope="request"/>
	<% Genotype genotype = (Genotype)request.getAttribute("genotype"); 
	  NotesTagConverter ntc = new NotesTagConverter();
	  StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
	  StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
	%>
	<%@ include file="../phenotype_table_geno_popup_header.jsp" %>

	<!-- Header -->
	<%@ include file="/WEB-INF/jsp/phenotype_table_geno_popup_content.jsp" %>
	<div style="clear: both;"></div>
	</div>
    <br/>
</c:forEach>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
