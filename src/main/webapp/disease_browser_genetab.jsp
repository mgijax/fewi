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
.tableHeaderText {height:1.8em;}
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
	<td colspan="5" style="">
		<div class='tableHeaderText'>
		<a href="${configBean.FEWI_URL}/disease/genes/report.xlsx?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Excel File</a>
		<a href="${configBean.FEWI_URL}/disease/genes/report.txt?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
		${asterisk}<font size="-1">Disease is associated/modeled with this <b>Gene</b> or a homolog. <a onclick="javascript:openUserhelpWindow('VOCAB_do_browser_detail_help.shtml#how_interpret'); return false;" href="${configBean.USERHELP_URL}VOCAB_do_browser_detail_help.shtml#how_interpret">More...</a></td>
		</div>
	</tr>
	
	<!-- heading row -->
	<tr>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td style="">&nbsp;</td>
	<td class="headerStripe allBorders">Disease Term</td>
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
	  <c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseGroupRows)}"/>
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseRowCount}' class='centerMiddle'>${humanIcon}${mouseIcon}&nbsp;</td><td rowspan='${diseaseRowCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseRowCount}'>&nbsp;</td>"/>

	  <%@ include file="/WEB-INF/jsp/disease_detail_subtable.jsp" %> 
	</c:if>

	<!-- only mouse markers known to cause the disease -->
	<c:if test="${not empty disease.mouseOnlyGroup}">
	  <c:set var="diseaseGroup" value="${disease.mouseOnlyGroup}"/>
	  <c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseGroupRows)}"/>
	  <c:set var="sectionBorder" value="allBorders groupSeparater"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseRowCount}' class='centerMiddle'>${mouseIcon}&nbsp;</td><td rowspan='${diseaseRowCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseRowCount}'>&nbsp;</td>"/>

	  <%@ include file="/WEB-INF/jsp/disease_detail_subtable.jsp" %> 
	</c:if>

	<!-- only human markers known to cause the disease -->
	<c:if test="${not empty disease.humanOnlyGroup}">
	  <c:set var="diseaseGroup" value="${disease.humanOnlyGroup}"/>
	  <c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseGroupRows)}"/>
	  <c:set var="sectionBorder" value="allBorders"/>

	  <c:set var="prefix" value="<td rowspan='${diseaseRowCount}' class='centerMiddle'>${humanIcon}&nbsp;</td><td rowspan='${diseaseRowCount}' class='topBorder bottomBorder leftBorder'>&nbsp;&nbsp;</td><td rowspan='${diseaseRowCount}'>&nbsp;</td>"/>

	  <%@ include file="/WEB-INF/jsp/disease_detail_subtable.jsp" %> 
	</c:if>

	</table>

  </c:if>

  <c:if test="${not empty disease.otherGroup}">

    <hr id='tableSeparater'>

	<c:set var="rowCount" value="0"/>
	<font class="label"></font><p>
	<table id="transgeneTable" cellpadding="4" cellspacing="0" width="!">
	<tbody>
	<tr>
	<td style="">&nbsp;</td>
	<td colspan="3" style="">
		<div class='tableHeaderText'>
		<c:if test="${empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup}">
		  <a href="${configBean.FEWI_URL}/disease/genes/report.xlsx?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Excel File</a>
		  <a href="${configBean.FEWI_URL}/disease/genes/report.txt?doid=${disease.primaryID}" id="textDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> Text File</a>
		</c:if>
		Transgenes and other genome features developed in mice to model this disease.
		</div>
	</td>
	</tr>
	<tr>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td class="headerStripe allBorders">Disease Term</td>
	<td class="headerStripe allBorders">Transgenes and Other Genome Features</td>
	<td class="headerStripe allBorders">Mouse Models</td>
	</tr>

	<c:set var="diseaseGroup" value="${disease.otherGroup}"/>
	<c:set var="diseaseRowCount" value="${fn:length(diseaseGroup.diseaseGroupRows)}"/>
	<c:set var="sectionBorder" value="allBorders"/>

	<%@ include file="/WEB-INF/jsp/disease_detail_subtable_tg.jsp" %> 

	</table>
      </td>
    </tr>
  </c:if>

<br/>












</div>