<%@ page import = "org.jax.mgi.fe.datamodel.Marker" %>
<%@ page import = "org.jax.mgi.fe.datamodel.Term" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.DBConstants" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.summary.MPSummaryRow" %>
<%@ page import = "org.jax.mgi.fewi.searchUtil.entities.SolrMPAnnotation" %>
<%@ page import = "java.util.List" %>
<%@ page import = "org.slf4j.Logger" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
    String mpID = (String)request.getAttribute("mpID");
    Marker marker = (Marker)request.getAttribute("marker");
    Term term = (Term)request.getAttribute("term");
    Integer annotationCount = (Integer)request.getAttribute("annotationCount");
    Integer genotypeCount = (Integer)request.getAttribute("genotypeCount");
    List<MPSummaryRow> rows = (List<MPSummaryRow>)request.getAttribute("rows");
    StyleAlternator stripes = new StyleAlternator("stripe1", "stripe2");
    String stripe = stripes.getNext();

    Logger logger = (Logger) request.getAttribute("logger");
    logger.debug("Entering mp_annotation_summary.jsp");
    %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Mammalian Phenotype Ontology Annotations</title>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="VOCAB_mp_browser_help.shtml#mp_summary">    
    <span class="titleBarMainTitle">Mammalian Phenotype Ontology Annotations</span><br/>
    <span class="titleBarSubTitle">Query Results - Summary</span>
</div>


<!-- structural table -->
<style type="text/css">
td.mpAnnot { border: 1px solid gray;
	padding : 5px;
	border-collapse: collapse; 
	vertical-align: middle; } 
span.tall { line-height: 150%; }
</style>

<!-- separate one-row table : marker symbol, name, and ID (if available) with
	link to marker detail page -->

<c:if test="${not empty marker}">
<table id="markerTable" class="results" style="width:100%">
  <tr align="left" valign="top">
	  <td class="mpAnnot" style="vertical-align: bottom; text-align: right; width:8%; background-color:#DFEFFF"><font face="Arial,Helvetica">
      <b>Symbol</b><br/>
      <b>Name</b><br/>
      <b>ID</b><br/>
      </font>
    </td>
    <td class="mpAnnot" style="background-color:#FFFFFF">
      <font size="+2"><b><a href="${configBean.FEWI_URL}marker/${marker.primaryID}">${marker.symbol}</a></b></font><br/>
      <b>${marker.name}</b><br/>
      ${marker.primaryID}
    </td>
  </tr>
</table>
<p/>
</c:if>

<table id="resultsTable" style="width:100%">

<!-- row 1 : counts of genotypes and annotations -->
<tr class="pageInfo">
  <td colspan="3" class="mpAnnot">
    <span id="genotypeCount">${genotypeCount}</span> genotypes with <span id="annotationCount">${annotationCount}</span> annotations displayed
    of selected term and subterms
    <c:if test="${not empty marker}">for ${marker.symbol}</c:if>
  </td>
</tr>

<!-- row 2 : searched term with link to MP browser -->
<tr class="pageInfo">
  <td colspan="3" class="mpAnnot">
	<c:if test="${not empty mpID}">
	  <b>Searched Term:</b> <a href="${configBean.FEWI_URL}vocab/mp_ontology/${term.primaryID}">${term.term}</a>
	</c:if>
	<c:if test="${not empty emapaID}">
	  <b>Searched Anatomy Term:</b> <a href="${configBean.FEWI_URL}vocab/gxd/anatomy/${term.primaryID}">${term.term}</a>
	</c:if>
  </td>
</tr>

<!-- row 3 : header row for table of annotations -->
<tr class="stripe2">
	<td class="resultsHeader mpAnnot">Allelic Composition<br/>(Genetic Background)</td>
	<td class="resultsHeader mpAnnot">Annotated Term</td>
	<td class="resultsHeader mpAnnot">Reference</td>
</tr>

<% logger.debug("Beginning to generate data rows"); %>

<!-- rows 5-n : data rows, one per genotype with subrows for annotated terms -->

<c:set var="refDetail" value="${configBean.FEWI_URL}reference/"/>
<c:set var="termDetail" value="${configBean.FEWI_URL}vocab/mp_ontology/"/>

<c:forEach var="row" items="${rows}">
	<% stripe = stripes.getNext(); %>
	<c:set var="rowspan" value=""/>
	<c:if test="${fn:length(row.annotations) > 1}">
	  <c:set var="rowspan" value=" rowspan='${fn:length(row.annotations)}'"/>
	</c:if>
	<tr class="<%= stripe %>">
		<c:set var="annotation" value="${row.firstAnnotation}"/>

		<td class="mpAnnot"${rowspan}><span class="tall">${row.allelicComp}</span><br/><span class="tall">
			<c:choose>
				<c:when test="${not empty row.strainID}">
					(<a href="${configBean.FEWI_URL}strain/${row.strainID}" target="_blank">${row.genBackground}</a>)
				</c:when>
				<c:otherwise>
					(${row.genBackground})
				</c:otherwise>
			</c:choose>
		</span></td>
		<td class="mpAnnot"><a href="${termDetail}${annotation.termID}">${annotation.term}</a>
		</td>
		<td class="mpAnnot">
		    <c:forEach var="ref" items="${annotation.references}" varStatus="status">
		    <a href="${refDetail}${ref}">${ref}</a><c:if test="${!status.last}">, </c:if>
		    </c:forEach>
		</td>
	</tr>
	<c:forEach var="annotation" items="${row.extraAnnotations}">
		<tr class="<%= stripe %>">
		<td class="mpAnnot"><a href="${termDetail}${annotation.termID}">${annotation.term}</a>
		</td>
		<td class="mpAnnot">
		    <c:forEach var="ref" items="${annotation.references}" varStatus="status">
		    <a href="${refDetail}${ref}">${ref}</a><c:if test="${!status.last}">, </c:if>
		    </c:forEach>
		</td>
		</tr>
	</c:forEach>
</c:forEach>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
<% logger.debug ("JSP finished"); %>
