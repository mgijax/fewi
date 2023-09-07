<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Gene Expression Literature Detail</title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker foo = (Marker)request.getAttribute("foo");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
    
%>

<% Marker marker = (Marker)request.getAttribute("marker");
   Reference reference = (Reference)request.getAttribute("reference");
%>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_literature_help.shtml">	
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Literature Detail</span>
</div>

<c:set var="isGxd" value="Gxd"/>
<%@ include file="/WEB-INF/jsp/marker_header.jsp" %><br>

<table class="summaryHeader"><tbody>
  <tr>
  <td class="summaryHeaderCat1Gxd">
       <div>Reference</div>
  </div></td>
  <td class="summaryHeaderData1">
	<a href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a> ${reference.longCitation}
  </td>
  </tr></tbody>
</table><br>

<c:if test="${record.isFullyCoded}"> 
  <b>Detailed expression data for these assays:</b> 
  ${record.fullyCodedResultCount} 
  <a href="${configBean.FEWI_URL}gxd/summary?markerMgiId=${marker.primaryID}&jnum=${reference.jnumID}">
    result<c:if test="${record.fullyCodedResultCount > 1}">s</c:if>
  </a>
</c:if>

<br><br>
<img src="${configBean.WEBSHARE_URL}/images/redball.gif" alt="red ball"> Indicates gene expression was analyzed but not necessarily detected.

<c:if test="${not empty pairTable}">
	<table class="outline">
	<tr class="outline stripe3Gxd">
	<!-- Setup the age header -->
	<td class="outline"><a href="${configBean.USERHELP_URL}EXPRESSION_literature_help.shtml#irbaa" onClick='openUserhelpWindow("EXPRESSION_literature_help.shtml#irbaa"); return false;'">Age</a></td>
	<c:forEach var="age" items="${pairTable.ages}">
		<td class="outline" style="text-align:center"><c:if test="${age != 'E' && age != 'P'}">E</c:if>${age}</td>
	</c:forEach>
	</tr>
	<!-- Setup the x access header -->
	<c:forEach var="type" items="${pairTable.assayTypes}">
		<tr class="outline">
		<td class="outline">${type.assayType}</td>
			<c:forEach var="count" items="${type.counts}">
				<td class="outline"><c:if test="${not empty count}"><img style="display: block; margin-left: auto; margin-right: auto" src="${configBean.WEBSHARE_URL}/images/redball.gif" alt="red ball"></c:if></td>
			</c:forEach>
	    </tr>
	</c:forEach>
	</table>
</c:if>
<br>

<c:if test="${not empty record.comments}">
  <b>Comments:</b> ${record.comments}
</c:if>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
