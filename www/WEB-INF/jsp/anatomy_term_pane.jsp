<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "java.util.List" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailListCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailListBg1","detailListBg2");
    VocabTerm term = (VocabTerm) request.getAttribute("term");
%>
      <input type="hidden" id="pageTitle" value="${title}"/>
      <table style="width:75%">
        <tr style="width:100%"><td class="rightBorderThinGray padded label top">Term: </td>
	  <td class="padded top" style="width:*"><span class="highlight"><b>${term.term}</b></span></td>
	  <td rowspan="2" class="padded top" style="width=30%; vertical-align: bottom">
	    <div style="float:right">
	    <span style="font-size: 80%; font-style: italic;">${dropdownMsg}<br></span>
	    ${dropdown}
	    </div>
	  </td>
	</tr>
        <tr><td class="rightBorderThinGray padded label top">Present at: </td>
	  <td class="padded top">
	    <c:if test="${term.isEmapa}">
	    Theiler Stage<c:if test="${term.emapInfo.startStage != term.emapInfo.endStage}">s ${term.emapInfo.startStage}-${term.emapInfo.endStage}</c:if>
	    <c:if test="${term.emapInfo.startStage == term.emapInfo.endStage}">${term.emapInfo.startStage}</c:if>
	    </c:if>
	    <c:if test="${term.isEmaps}">Theiler Stage ${term.emapInfo.stage}</c:if>
	  </td></tr>
        <tr><td class="rightBorderThinGray padded label top">ID: </td>
	  <td class="padded top">${term.primaryId}</td></tr>
	<c:if test="${not empty term.synonyms}">
        <tr><td class="rightBorderThinGray padded label top">Synonyms: </td>
	  <td class="padded top">
	    <c:forEach var="synonym" items="${term.synonyms}" varStatus="status">
	      ${synonym.synonym}<c:if test="${!status.last}">, </c:if>
	    </c:forEach>
	  </td></tr>
	</c:if>
	<c:if test="${not empty term.definition}">
        <tr><td class="rightBorderThinGray padded label top">Definition: </td>
	  <td class="padded top">${term.definition}</td></tr>
	</c:if>
	<c:if test="${not empty term.parents}">
        <tr><td class="rightBorderThinGray padded label top">Parent Terms: </td>
	  <td class="padded top">
	    <c:forEach var="parent" items="${term.parents}" varStatus="status">
	    <i>${fn:replace(parent.edgeLabel, "_", "-")}</i>
	    <a href="${configBean.FEWI_URL}vocab/gxd/anatomy/${parent.ancestorID}" onClick="resetPanes('${parent.ancestorID}'); return false">${parent.ancestorTerm}</a><br/>
	    </c:forEach>
	  </td></tr>
	</c:if>
      </table>
