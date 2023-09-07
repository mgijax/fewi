<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>
<%@ page import = "org.jax.mgi.fewi.util.DiseaseModelFilter" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
// Pull detail object into servlet scope
Disease disease = (Disease) request.getAttribute("disease");

List<DiseaseModel> models;
Iterator<DiseaseModel> dmIt;
Iterator<Reference> refIt;
DiseaseModel model;
Reference ref;

StyleAlternator leftTdStyles 
  = new StyleAlternator("detailCat1","detailCat2");
StyleAlternator rightTdStyles 
  = new StyleAlternator("detailData1","detailData2");

String fewiUrl = ContextLoader.getConfigBean().getProperty("FEWI_URL");
NotesTagConverter ntc = new NotesTagConverter();

// used to filter out disease models which would otherwise be shown
// multiple times on the page
DiseaseModelFilter dmFilter = new DiseaseModelFilter();

%>

<style type="text/css">
.tableHeaderText {height:1.8em;}
</style>

<div class="tabContainer">


<c:if test="${(empty disease.mouseAndHumanModels) and (empty disease.mouseOnlyModels) and (empty disease.humanOnlyModels) and (empty disease.otherModels) and (empty disease.additionalModels)}">
  <em>No mouse models with similarity to the expected human disease phenotype are reported in MGI.</em>
</c:if>

<c:if test="${not ((empty disease.mouseAndHumanModels) and (empty disease.mouseOnlyModels) and (empty disease.humanOnlyModels) and (empty disease.otherModels) and (empty disease.additionalModels))}">

	<c:set var="rowCount" value="0"/>

	<table id='modelTabTable' cellpadding="4" cellspacing="0" width="!">
	<tbody>

	<!-- descriptive text -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td colspan="5" style="">
	  <div class='tableHeaderText'>
	  <a href="${configBean.FEWI_URL}/disease/models/report.xlsx?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Excel File</a>
	  <a href="${configBean.FEWI_URL}/disease/models/report.txt?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
	  All mouse models of ${disease.disease} with phenotypic similarity to the human disease
	  </div>
	</td>
	</tr>

	<!-- heading row -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td class="headerStripe allBorders">Disease Term</td>
	<td class="headerStripe allBorders">Allelic Composition</td>
	<td class="headerStripe allBorders">Genetic Background</td>
	<td class="headerStripe allBorders">Reference</td>
	<td class="headerStripe allBorders">Phenotypes</td>
	</tr>

	<c:set var="mouseIcon" value="<img src='${configBean.WEBSHARE_URL}images/black_mouse_small.gif'>"/>
	<c:set var="humanIcon" value="<img src='${configBean.WEBSHARE_URL}images/man_icon.gif'>"/>

	<c:set var="sectionCount" value="0"/>

	<!-- both mouse and human markers known to cause the disease -->
<%
	models = disease.getMouseAndHumanModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'>${humanIcon}${mouseIcon}&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>

	<!-- only mouse markers known to cause the disease -->
<%
	models = disease.getMouseOnlyModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'>${mouseIcon}&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>

	<!-- only human markers known to cause the disease -->
<%
	models = disease.getHumanOnlyModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'>${humanIcon}&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>


	<!-- only transgenes and other genome features known to cause the
	     disease -->
<%
	models = disease.getOtherModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'>Transgenes and<br/>Other Mutations</td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>


	<!-- additional models known to cause the disease -->
<%
	models = disease.getAdditionalModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'>Additional<br/>Complex<br/>Models</td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>


	</tbody>
	</table>
  </c:if><!-- we have models -->



<c:if test="${not ((empty disease.notModels))}">

    <br/>
    <hr id='tableSeparater'>

	<c:set var="rowCount" value="0"/>

	<table id='modelTabNotTable' cellpadding="4" cellspacing="0" width="!">
	<tbody>

	<!-- descriptive text -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td colspan="5" style="">
	  <div class='tableHeaderText'>
	  <c:if test="${empty disease.mouseAndHumanModels and empty disease.mouseOnlyModels and empty disease.humanOnlyModels and empty disease.otherModels and empty disease.additionalModels}">
	  <a href="${configBean.FEWI_URL}/disease/models/report.xlsx?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Excel File</a>
	  <a href="${configBean.FEWI_URL}/disease/models/report.txt?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
	  </c:if>
	  No similarity to the expected human disease phenotype was found.
	  </div>
	</td>
	</tr>

	<!-- heading row -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td class="headerStripe allBorders">Disease Term</td>
	<td class="headerStripe allBorders">Allelic Composition</td>
	<td class="headerStripe allBorders">Genetic Background</td>
	<td class="headerStripe allBorders">Reference</td>
	<td class="headerStripe allBorders">Phenotypes</td>
	</tr>

	<c:set var="mouseIcon" value="<img src='${configBean.WEBSHARE_URL}images/black_mouse_small.gif'>"/>
	<c:set var="humanIcon" value="<img src='${configBean.WEBSHARE_URL}images/man_icon.gif'>"/>

	<c:set var="sectionCount" value="0"/>

	<!-- 'Not' models for this disease -->
<%
	models = disease.getNotModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'>NOT Models&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>

	</tbody>
	</table>
  </c:if><!-- we have NOT models -->






</div>
