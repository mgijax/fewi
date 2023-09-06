<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${disease.disease} MGI Mouse Model Detail - ${disease.primaryID}</title>
<meta name="description" content="<c:choose><c:when test="${empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup}">There are currently no human or mouse genes associated with this disease in the MGI database.</c:when><c:otherwise>Mutations in human and/or mouse homologs are associated with this disease.</c:otherwise></c:choose><c:if test="${not empty disease.diseaseSynonyms}"> Synonyms: <c:forEach var="synonym" items="${disease.diseaseSynonyms}" varStatus="status">${synonym.synonym}<c:if test="${!status.last}">; </c:if></c:forEach></c:if>">


<%  // Pull detail object into servlet scope
    Disease disease = (Disease) request.getAttribute("disease");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>

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

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="VOCAB_do_browser_detail_help.shtml">	
	<span class="titleBarMainTitle">Human Disease and Mouse Model Detail</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">
<c:set var="hpoUrl" value="${fn:replace(externalUrls.HPO_Disease, '@@@@', disease.primaryID)}"/>

  <!-- ROW1 : Human Disease -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Human Disease
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <span class="enhance">${disease.disease}</span><br/>
      <span class="label">${disease.logicalDB} ID:</span> ${linkOut}
      <c:if test="${disease.hpoTermCount > 0}">
	      <br/><span class="label">Human Phenotype Ontology</span> <a href="${hpoUrl}" target="_blank">associations</a>
      </c:if>
    </td>
  </tr>


  <!-- ROW2 : synonyms -->
  <c:if test="${not empty disease.diseaseSynonyms}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Synonyms
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <c:forEach var="synonym" items="${disease.diseaseSynonyms}" varStatus="status">
        ${synonym.synonym}<c:if test="${!status.last}">; </c:if>
      </c:forEach>
    </td>
  </tr>
  </c:if>

  <!-- ROW3 : all models and references -->
  <c:if test="${(disease.countOfModels > 0) or (disease.countOfNotModels > 0)}">
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      View all models
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <c:set var="allModels" value=""/>
      <c:set var="count" value="${disease.countOfModels}"/>
      <c:set var="extra" value=""/>
      <c:if test="${count < 1}">
        <c:set var="allModels" value='"NOT" '/>
        <c:set var="count" value="${disease.countOfNotModels}"/>
	<c:set var="extra" value="No mouse models with similarity to the expected human disease phenotype are reported in MGI."/>
      </c:if>
      View ALL (<a href="${configBean.FEWI_URL}disease/models/${disease.primaryID}">${count}</a>) ${allModels}mouse models for this human disease.
      ${extra}
    </td>
  </tr>
  </c:if>

  <!-- ROW4 : genes and mouse models -->

  <c:set var="asterisk" value="<font color='maroon'>*</font>"/>
  <c:if test="${empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup}">
    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
	Genes and<br/>mouse models
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
	<em>There are currently no human or mouse genes associated with this
	disease in the MGI database.</em>
      </td>
    </tr>

  </c:if>

  <c:if test="${not (empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup)}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
	Genes and<br/>mouse models
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
	<font class="label">Mutations in human and/or mouse homologs are associated with this disease</font><p>

	<table cellpadding="4" cellspacing="0" width="!">
	<tbody>

	<!-- explanation for asterisk -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td colspan="4" style="">${asterisk}<font size="-1">Disease is associated/modeled with this <b>Gene</b> or a homolog. <a onclick="javascript:openUserhelpWindow('VOCAB_do_browser_detail_help.shtml#how_interpret'); return false;" href="${configBean.USERHELP_URL}VOCAB_do_browser_detail_help.shtml#how_interpret">More...</a></td>
	</tr>
	
	<!-- heading row -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Human Homologs</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Mouse Homologs</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Mouse Models</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Homology Source</font></font></td>
	</tr>

	<c:set var="mouseIcon" value="<img src='${configBean.WEBSHARE_URL}images/black_mouse_small.gif'>"/>
	<c:set var="humanIcon" value="<img src='${configBean.WEBSHARE_URL}images/man_icon.gif'>"/>

	<c:set var="rowCount" value="0"/>

	<!-- both mouse and human markers known to cause the disease -->
	<c:if test="${not empty disease.mouseHumanGroup}">
	  <c:set var="diseaseGroup" value="${disease.mouseHumanGroup}"/>
	  <c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseRows)}"/>
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseRowCount}' class='centerMiddle'>${humanIcon}${mouseIcon}&nbsp;</td><td rowspan='${diseaseRowCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseRowCount}'>&nbsp;</td>"/>

	  <%@ include file="/WEB-INF/jsp/disease_detail_subtable.jsp" %> 
	</c:if>

	<!-- only mouse markers known to cause the disease -->
	<c:if test="${not empty disease.mouseOnlyGroup}">
	  <c:set var="diseaseGroup" value="${disease.mouseOnlyGroup}"/>
	  <c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseRows)}"/>
	  <c:set var="sectionBorder" value="allBorders bottomBorderDark"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseRowCount}' class='centerMiddle'>${mouseIcon}&nbsp;</td><td rowspan='${diseaseRowCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseRowCount}'>&nbsp;</td>"/>

	  <%@ include file="/WEB-INF/jsp/disease_detail_subtable.jsp" %> 
	</c:if>

	<!-- only human markers known to cause the disease -->
	<c:if test="${not empty disease.humanOnlyGroup}">
	  <c:set var="diseaseGroup" value="${disease.humanOnlyGroup}"/>
	  <c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseRows)}"/>
	  <c:set var="sectionBorder" value="allBorders"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseRowCount}' class='centerMiddle'>${humanIcon}&nbsp;</td><td rowspan='${diseaseRowCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseRowCount}'>&nbsp;</td>"/>

	  <%@ include file="/WEB-INF/jsp/disease_detail_subtable.jsp" %> 
	</c:if>

	</table>
      </td>
    </tr>
 
  </c:if>

<!-- ROW5 : transgenes and other non-gene marker types -->

  <c:if test="${not empty disease.otherGroup}">
    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
	Transgenes and<br/>other genome features
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
	<c:set var="rowCount" value="0"/>
	<font class="label">Transgenes and other genome features developed in mice to model this disease.</font><p>
	<table cellpadding="4" cellspacing="0" width="!">
	<tbody>
	<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Transgenes and Other Genome Features</font></font></td>
	<td class="headerStripe allBorders"><font class="label"><font size="-1">Mouse Models</font></font></td>
	</tr>

	<c:set var="diseaseGroup" value="${disease.otherGroup}"/>
	<c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseRows)}"/>
	<c:set var="sectionBorder" value="allBorders"/>

	<%@ include file="/WEB-INF/jsp/disease_detail_subtable_tg.jsp" %> 

	</table>
      </td>
    </tr>
  </c:if>

<!-- ROW6 : references for this disease -->
<c:if test="${diseaseRefCount>0}">
	<tr>
      <td class="<%=leftTdStyles.getNext() %>" >
		References
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
		Disease References using Mouse Models <a href="${configBean.FEWI_URL}reference/disease/${disease.primaryID}?typeFilter=Literature">(${diseaseRefCount})</a>
      </td>
    </tr>
</c:if>

<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/disease_detail_popups.jsp" %> 
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
