<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fe.datamodel.Genotype" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%
   StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
   StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
   Genotype genotype = (Genotype)request.getAttribute("genotype");
   NotesTagConverter ntc = new NotesTagConverter();	
%>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<fewi:simpleseo
   title="All Phenotypes ${allele.symbol} MGI Mouse"
   description="View all phenotypes for allele ${allele.symbol} by genotype and genetic background."
   keywords="${allele.symbol}, phenotypes, genotypes, genetic background, MP, Mammalian Phenotype, mouse, mice, murine, Mus"
/>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<%@ include file="phenotype_table_geno_popup_imports.jsp" %>

<fewi:pagetitle title="Phenotypes associated with this allele" userdoc="ALLELE_detail_pheno_summary_help.shtml#see_annot" />

<c:if test="${(not empty genotypeAssociations) and (fn:length(genotypeAssociations) > 0)}">
  <c:set var="showGenotypes" value="true"/>
</c:if>

<%@ include file="allele_header.jsp" %>
<br/><br/>
<c:forEach var="genotypeAssociation" items="${genotypeAssociations}" varStatus="genoStatus">
	<div style="overflow:hidden;">
		<!-- Set all the values that the genoview jsp expects -->
		<c:set var="genotype" value="${genotypeAssociation.genotype}" scope="request"/>
		<c:set var="counter" value="${genotypeAssociation.genotypeSeq }"  scope="request"/>
		<c:set var="mpSystems" value="${genotypeAssociation.genotype.MPSystems }"  scope="request"/>
		<c:set var="hasImage" value="${genotypeAssociation.genotype.hasPrimaryImage }"  scope="request"/>
		<c:set var="hasDiseaseModels" value="${not empty genotypeAssociation.genotype.diseases }"  scope="request"/>
		<%@ include file="phenotype_table_geno_popup_header.jsp" %>
		
		<%@ include file="phenotype_table_geno_popup_content.jsp" %>
		<div style="clear: both;"></div>
	</div>
	<br/><br/><br/>
</c:forEach>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
