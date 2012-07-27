<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% 
  Marker marker = (Marker)request.getAttribute("marker"); 

  StyleAlternator leftTdStyles 
    = new StyleAlternator("detailCat1","detailCat2");
  StyleAlternator rightTdStyles 
    = new StyleAlternator("detailData1","detailData2");
%>
    
${templateBean.templateHeadHtml}

<title>Gene Expression Images Associated With This Gene  </title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
table.imageTable {
  border: 1px solid black;
  width:100%;
}
table.imageTable td{
  padding:3px;
}
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="EXPRESSION_image_summary_help.shtml">    
  <span class="titleBarMainTitle">
    Gene Expression Images Associated With This Gene 
  </span>
</div>


<!-- standard marker header -->
<jsp:include page="marker_header.jsp"></jsp:include><br>


<!-- counts and pagination -->
<form:form method="GET" commandName="pagination" action="${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}">
<input type="hidden" name="startIndex" value="${paginationControls.startIndex}">

<table border=0 cellpadding=1 cellspacing=1 width="100%">
<tr>

  <td width="40%"><br>
    ${marker.countOfGxdImages} expression image(s) for ${marker.symbol} in ${paginationControls.resultsTotal} figure(s)
    <br>
    Showing figure(s) ${paginationControls.startIndexDisplay} to ${paginationControls.lastResultDisplayed} 
  </td>

  <c:if test="${paginationControls.resultsTotal - paginationControls.results > 0}">

    <td>
      Show&nbsp; ${paginationControls.dropList} &nbsp;figures&nbsp;per&nbsp;page
    </td>

    <td width="10%">

      <c:if test="${paginationControls.showStartLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=0&results=${paginationControls.results}'>
          Start</a>
      </c:if>

      <c:if test="${paginationControls.showPreviousLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=${paginationControls.startIndex - paginationControls.results}&results=${paginationControls.results}'>
          Previous</a>
      </c:if>

    </td>

    <td width="20%">

      <c:if test="${paginationControls.showNextLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=${paginationControls.startIndex + paginationControls.results}&results=${paginationControls.results}'>
          Next</a>
      </c:if>

      <c:if test="${paginationControls.showLastLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=${paginationControls.resultsTotal - paginationControls.results}&results=${paginationControls.results}'>
          Last</a>
      </c:if>

    </td>

  </c:if>

</tr>
</table>
</form:form>


<hr>
Click on thumbnail or figure label to view full size image with links to expression annotations. 
<br>
<br>

<table class="imageTable" style="" cellpadding=4 >

  <c:forEach var="imageSummaryRow" items="${imageSummaryRows}" >
    <tr>

    <!-- LEFT CELL: Linked Image -->
    <td style="background-color:#F0F0F0; border: 1px solid black;" width="1%">
      ${imageSummaryRow.imgTag}
    </td>

    <!-- RIGHT CELL: Linked Image -->  
    <td class="<%=rightTdStyles.getNext() %>" style="border: 1px solid black;">

      <div class='' style='padding-top:4px; padding-bottom:4px;'>

        <b>Reference:</b> 
        ${imageSummaryRow.reference.jnumID}
        ${imageSummaryRow.reference.shortCitation}

      </div>


      <div class='' style='padding-top:4px; padding-bottom:4px;'>
        <a href='${configBean.FEWI_URL}image/${imageSummaryRow.image.mgiID}'>
          Figure  ${imageSummaryRow.image.figureLabel} 
        </a>
        <div class='' style='padding-top:4px;padding-left:14px;'>
          ${imageSummaryRow.assayTypesInPage}
        </div>
      </div>


    </td>
    </tr>
  </c:forEach>

</table>


<!-- counts and pagination -->
<form:form method="GET" commandName="pagination" action="${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}">
<input type="hidden" name="startIndex" value="${paginationControls.startIndex}">

<table border=0 cellpadding=1 cellspacing=1 width="100%">
<tr>

  <td width="40%"><br>
  </td>

  <c:if test="${paginationControls.resultsTotal - paginationControls.results > 0}">

    <td>
      &nbsp;
    </td>

    <td width="10%">

      <c:if test="${paginationControls.showStartLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=0&results=${paginationControls.results}'>
          Start</a>
      </c:if>

      <c:if test="${paginationControls.showPreviousLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=${paginationControls.startIndex - paginationControls.results}&results=${paginationControls.results}'>
          Previous</a>
      </c:if>

    </td>

    <td width="20%">

      <c:if test="${paginationControls.showNextLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=${paginationControls.startIndex + paginationControls.results}&results=${paginationControls.results}'>
          Next</a>
      </c:if>

      <c:if test="${paginationControls.showLastLink}">
        <a href='${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}?startIndex=${paginationControls.resultsTotal - paginationControls.results}&results=${paginationControls.results}'>
          Last</a>
      </c:if>

    </td>

  </c:if>

</tr>
</table>
</form:form>


${templateBean.templateBodyStopHtml}

