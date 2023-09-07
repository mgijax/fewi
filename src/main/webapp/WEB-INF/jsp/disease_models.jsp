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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
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
%>

<title>Mouse Models of ${disease.disease}</title>

<style type="text/css">
.topBorder { border-top-color: #000000;
    border-top-style:solid;
    border-top-width:1px; }

.bottomBorder { border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:1px; }

.leftBorder { border-left-color :#000000; 
    border-left-style:solid;
    border-left-width:1px; }

.rightBorder { border-right-color :#000000; 
    border-right-style:solid;
    border-right-width:1px; }

.allBorders {
    border-top: thin solid gray;
    border-bottom: thin solid gray;
    border-left: thin solid gray;
    border-right: thin solid gray;
    padding:3px;
    text-align:center;
}

.leftAlign {
    text-align:left;
}

.bottomBorderDark { border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:2px; }

.stripe1 { background-color: #FFFFFF; }
.stripe2 { background-color: #DDDDDD; }
.headerStripe { background-color: #D0E0F0; }
.underline { border-bottom: 1px gray dotted; }

.link { color:#000099;
    cursor: pointer;
    border-bottom: 1px #000099 solid;
    text-decoration: none; }

.superscript { vertical-align: super; font-size: 90%}
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="VOCAB_do_browser_detail_help.shtml#mouse_models">	
	<span class="titleBarMainTitle">Mouse Models of ${disease.disease}</span>
</div>


<%
  // used to filter out disease models which would otherwise be shown
  // multiple times on the page
  DiseaseModelFilter dmFilter = new DiseaseModelFilter();
%>

<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 : Human Disease -->
  <tr>
    <td class="<%=leftTdStyles.getNext() %>">
      Mouse Models
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <span class="label">All mouse models of ${disease.disease}</span>
      (${disease.logicalDB} ID:</span> ${linkOut})<p/>

      <c:if test="${(empty disease.mouseAndHumanModels) and (empty disease.mouseOnlyModels) and (empty disease.humanOnlyModels) and (empty disease.otherModels) and (empty disease.additionalModels)}">
        <p/>
	<em>No mouse models with similarity to the expected human disease phenotype are reported in MGI.</em>
      </c:if>

      <c:if test="${not ((empty disease.mouseAndHumanModels) and (empty disease.mouseOnlyModels) and (empty disease.humanOnlyModels) and (empty disease.otherModels) and (empty disease.additionalModels))}">
	<table cellpadding="4" cellspacing="0" width="!">
	<tbody>

	<!-- heading row -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Allelic Composition</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Genetic Background</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Reference</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Phenotypes</font></font></td>
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
	  <%@ include file="/WEB-INF/jsp/disease_models_stripe.jsp" %> 
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

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
	  <%@ include file="/WEB-INF/jsp/disease_models_stripe.jsp" %> 
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

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
	  <%@ include file="/WEB-INF/jsp/disease_models_stripe.jsp" %> 
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

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
	  <%@ include file="/WEB-INF/jsp/disease_models_stripe.jsp" %> 
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'><font class='label'>Transgenes and<br/>Other Mutations</font></td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>


	<!-- additional models known to cause the disease -->
<%
	models = disease.getAdditionalModels();
	models = dmFilter.filter(models);
	pageContext.setAttribute("diseaseModelCount", models.size());
%>
	<c:if test="${diseaseModelCount > 0}">
	  <%@ include file="/WEB-INF/jsp/disease_models_stripe.jsp" %> 
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'><font class='label'>Additional Complex Models</font></td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</c:if>


	</tbody>
	</table>
      </c:if><!-- we have models -->
    </td>
  </tr>

<!-- ROW2 : NOT models -->

<%
  models = disease.getNotModels();
  models = dmFilter.filter(models);
  pageContext.setAttribute("diseaseModelCount", models.size());
%>
  <c:if test="${diseaseModelCount > 0}">
    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Disease phenotype<br/>not observed
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
	<font class="label">No similarity to the expected human disease phenotype was found.</font><p/>
	<table cellpadding="4" cellspacing="0" width="!">
	<tbody>

	<!-- heading row -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Allelic Composition</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Genetic Background</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Reference</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Phenotypes</font></font></td>
	</tr>
	  <%@ include file="/WEB-INF/jsp/disease_models_stripe.jsp" %> 
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseModelCount}' class='centerMiddle'><font class='label'>NOT Models</font></td><td rowspan='${diseaseModelCount}'>&nbsp;</td><td rowspan='${diseaseModelCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseModelCount}'>&nbsp;</td>"/>
	  <%@ include file="/WEB-INF/jsp/disease_models_subtable.jsp" %> 
	</tbody>
	</table>
      </td>
    </tr>
  </c:if>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
