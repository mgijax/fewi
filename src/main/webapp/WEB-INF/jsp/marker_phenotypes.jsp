<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<% /* customizations based on whether we are showing rolled-up or multigenic
	genotypes */
%>
<c:if test="${empty multigenic}">
	<c:set var="mpGenotypes" value="${marker.rolledUpMpGenotypes}"/>
	<c:set var="headerLine" value="${marker.countOfAnnotationsMP} phenotypes from ${marker.countOfAllelesMP} alleles in ${marker.countOfBackgroundStrains} genetic backgrounds"/>
	<c:if test="${marker.countOfAnnotationsMP == 1}">
	    <c:set var="headerLine" value="${fn:replace(headerLine, 'phenotypes', 'phenotype')}"/>
	</c:if>
	<c:if test="${marker.countOfAllelesMP == 1}">
	    <c:set var="headerLine" value="${fn:replace(headerLine, 'alleles', 'allele')}"/>
	</c:if>
	<c:if test="${marker.countOfBackgroundStrains == 1}">
	    <c:set var="headerLine" value="${fn:replace(headerLine, 'backgrounds', 'background')}"/>
	</c:if>
</c:if>
<c:if test="${not empty multigenic}">
	<c:set var="multigenicSubtitle" value=" - Multigenic Genotypes"/>
	<c:set var="mpGenotypes" value="${marker.multigenicMpGenotypes}"/>
	<c:set var="headerLine" value="${marker.countOfOtherPhenotypeAnnotations} phenotypes from multigenic genotypes"/>
	<c:if test="${marker.countOfOtherPhenotypeAnnotations == 1}">
	    <c:set var="headerLine" value="${fn:replace(headerLine, 'phenotypes', 'phenotype')}"/>
	</c:if>
	<c:set var="mgParam" value="&multigenic=1"/>
</c:if>

<% /* back to the process of building the page */ %>

<title>${marker.symbol} Phenotype Annotations${multigenicSubtitle}</title>

<meta name="description" content="Mammalian Phenotype annotations for ${marker.symbol}${multigenicSubtitle}"/>
<meta name="keywords" content="MGI, mouse, mice, Mus musculus, murine, gene, phenotypes, MP, ${marker.symbol}"/>
<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/> 

<% Marker marker = (Marker)request.getAttribute("marker");
   NotesTagConverter ntc = new NotesTagConverter();
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="VOCAB_mp_browser_help.shtml#mp_summary" style="padding-bottom: 5px;">	
	<div name="centeredTitle">
		<span class="titleBarMainTitle">Mammalian Phenotype Ontology Annotations</span>
		<br/>
		Query Results - Summary
	</div>
</div>

<%@ include file="/WEB-INF/jsp/marker_header.jsp" %>

<div id="headerLine" style="margin-top: 10px; margin-bottom: 10px;">${headerLine}</div>

<div id="toolbar" class="bluebar" style="">
	<div id="downloadDiv">
		<span class="label">Export:</span>
		<a id="excelDownload" class="filterButton" href="${configBean.FEWI_URL}marker/phenotypes/report.xlsx?markerId=${marker.primaryID}${mgParam}"><img src="${configBean.WEBSHARE_URL}images/excel.jpg" width="10" height="10" /> Excel File</a>
	</div>
</div>

<style>
td.header { text-align: center; font-weight: bold; vertical-align: middle; background-color: #d0e0f0; border: solid thin gray; padding: 3px; }
td.body { text-align: left; vertical-align: middle; border: solid thin gray; padding: 3px; }
td.spaced { line-height: 1.5em; }
</style>

<c:set var='stripe' value='stripe1'/>

<div id="phenotypeTableDiv">
<table id="phenotypeTable" width="100%">
  <tr>
  <td class="header">Allelic Composition<br/>Genetic Background</td>
  <td class="header">Annotated Term</td>
  <td class="header">Reference</td>
  </tr>
  <c:forEach var="genotype" items="${mpGenotypes}" varStatus="gStatus">
    <c:set var="annotations" value="${genotype.mpAnnotations}"/>
    <c:set var="annotationCount" value="${fn:length(annotations)}"/>

    <c:choose>
    <c:when test="${stripe == 'stripe1'}">
      <c:set var="stripe" value="stripe2"/></c:when>
    <c:when test="${stripe == 'stripe2'}">
      <c:set var="stripe" value="stripe1"/></c:when>
    </c:choose>

    <%
    MPGenotype genotype = (MPGenotype) pageContext.getAttribute("genotype");
    String allelePairs = genotype.getAllelePairs();
    String convertedPairs = FormatHelper.newline2HTMLBR(ntc.convertNotes(allelePairs, '|'));
    pageContext.setAttribute("convertedPairs", convertedPairs);
    String superStrain = FormatHelper.superscript(genotype.getStrain());
    %>
    <tr class="${stripe}">
      <td rowspan="${annotationCount}" class="body spaced">${convertedPairs}<br/>
      	<c:choose>
      		<c:when test="${not empty genotype.strainID}">
      			<a href="${configBean.FEWI_URL}strain/${genotype.strainID}" target="_blank"><fewi:super value="${genotype.strain}" /></a>
      		</c:when>
      		<c:otherwise>
		      	<%= superStrain %>
      		</c:otherwise>
      	</c:choose>
      </td>
    <c:forEach var="annotation" items="${annotations}" varStatus="aStatus">
    <c:if test="${not aStatus.first}"><tr class="${stripe}"></c:if>
      <td class="body">
	<c:if test="${not empty annotation.qualifier}">
	  <strong>${annotation.qualifier}</strong>
	</c:if>
	<a href="${configBean.FEWI_URL}vocab/mp_ontology/${annotation.termID}">${annotation.term}</a></td>
      <td class="body">
	<c:forEach var="ref" items="${annotation.mpReferences}" varStatus="rStatus">
	<a href="${configBean.FEWI_URL}reference/${ref.jnumID}">${ref.jnumID}</a><c:if test="${not rStatus.last}">, </c:if>
	</c:forEach>
      </td>
      <c:if test="${not aStatus.last}"></tr></c:if>
    </c:forEach>
    </tr>
  </c:forEach>
</table>
</div>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

