<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

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

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="gxdlit_help.shtml">	
	<span class="titleBarMainTitle">Gene Expression Literature Detail</span>
</div>

<jsp:include page="marker_header.jsp"></jsp:include><br>
<!-- header table -->
<table class="summaryHeader">
<tbody><tr>
  <td class="summaryHeaderCat1">
       <div>Reference</div>
  </div></td>
  <td class="summaryHeaderData1">
	<a href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a> ${reference.longCitation}
  </td>
</tr>
</tbody></table><br>
<c:if test="${record.isFullyCoded}"> <b>Detailed expression data for these assays:</b> 
${record.fullyCodedResultCount} <a href="${configBean.FEWI_URL}gxd/result/key/${record.indexKey}">result<c:if test="${record.fullyCodedResultCount > 1}">s</c:if></a> in 
${record.fullyCodedAssayCount} <a href="${configBean.FEWI_URL}gxd/assay/key/${record.indexKey}">assay<c:if test="${record.fullyCodedAssayCount > 1}">s</c:if></a><br><br></c:if>
<img src="${configBean.WEBSHARE_URL}/images/redball.gif" alt="red ball"> Indicates gene expression was analyzed but not necessarily detected.
<c:if test="${not empty pairTable}">
	<table class="outline">
	<tr class="outline stripe3">
	<!-- Setup the age header -->
	<td class="outline"><a href="${configBean.USERHELP_URL}gxdindex_help.shtml#irbaa" onClick='openUserhelpWindow("gxdindex_help.shtml#irbaa"); return false;'">Age</a></td>
	<c:forEach var="age" items="${pairTable.ages}">
		<td class="outline"><c:if test="${age != 'E' && age != 'A'}">E</c:if>${age}</td>
	</c:forEach>
	</tr>
	<!-- Setup the x access header -->
	<c:forEach var="type" items="${pairTable.assayTypes}">
		<tr class="outline">
		<td class="outline">${type.assayType}</td>
			<c:forEach var="count" items="${type.counts}">
				<td class="outline"><c:if test="${not empty count}"><img src="${configBean.WEBSHARE_URL}/images/redball.gif" alt="red ball"></c:if></td>
			</c:forEach>
	    </tr>
	</c:forEach>
	</table>
</c:if>
<br>
<c:if test="${not empty record.comments}"><b>Comments:</b> ${record.comments}</c:if>

${templateBean.templateBodyStopHtml}
