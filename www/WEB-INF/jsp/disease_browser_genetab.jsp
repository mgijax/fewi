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

<%  // Pull detail object into servlet scope
    Disease disease = (Disease) request.getAttribute("disease");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>

<style type="text/css">
</style>

<div class="tabContainer">

  <c:set var="asterisk" value="<font color='maroon'>*</font>"/>

  <c:if test="${empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup}">
	<em>There are currently no human or mouse genes associated with this
	disease in the MGI database.</em>
  </c:if>

  <c:if test="${not (empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup)}">

	<table id='geneTabTable' cellpadding="4" cellspacing="0" width="!">
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
	<td class="headerStripe allBorders">Human Homologs</td>
	<td class="headerStripe allBorders">Mouse Homologs</td>
	<td class="headerStripe allBorders">Mouse Models</td>
	<td class="headerStripe allBorders">Homology Source</td>
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
 
  </c:if>













</div>