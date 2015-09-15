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
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>
  Phenotype Images associated with this ${marker.markerType}
</title>

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

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_pheno_image_summary_help.shtml">    
  <span class="titleBarMainTitle">
    Phenotype Images associated with this ${marker.markerType} 
  </span>
</div>


<!-- standard marker header -->
<jsp:include page="marker_header.jsp"></jsp:include><br>

<form:form method="GET" commandName="pagination" action="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}">
<input type="hidden" name="startIndex" value="${paginationControls.startIndex}">

<table border=0 cellpadding=1 cellspacing=1 width="100%">
<tr>

  <td width="40%"><br>
    ${paginationControls.resultsTotal} phenotype image(s) for ${marker.symbol} alleles
    <br>
    Showing image(s) ${paginationControls.startIndexDisplay} 
    to ${paginationControls.lastResultDisplayed}
  </td>

  <c:if test="${paginationControls.resultsTotal - paginationControls.results > 0}">

    <td>
      Show&nbsp; ${paginationControls.dropList} &nbsp;figures&nbsp;per&nbsp;page
    </td>

    <td width="10%">

      <c:if test="${paginationControls.showStartLink}">
        <a href='${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}?startIndex=0&results=${paginationControls.results}'>
          Start</a>
      </c:if>

      <c:if test="${paginationControls.showPreviousLink}">
        <a href='${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}?startIndex=${paginationControls.startIndex - paginationControls.results}&results=${paginationControls.results}'>
          Previous</a>
      </c:if>

    </td>

    <td width="20%">

      <c:if test="${paginationControls.showNextLink}">
        <a href='${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}?startIndex=${paginationControls.startIndex + paginationControls.results}&results=${paginationControls.results}'>
          Next</a>
      </c:if>

      <c:if test="${paginationControls.showLastLink}">
        <a href='${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}?startIndex=${paginationControls.resultsTotal - (paginationControls.resultsTotal mod paginationControls.results)}&results=${paginationControls.results}'>
          Last</a>
      </c:if>

    </td>

  </c:if>

</tr>
</table>
</form:form>




<hr>
Click on thumbnail to view full size image with links to phenotype annotations.
<br>
Click on allele symbol for full phenotype details.
<br>
<br>

<table class="imageTable" style="" cellpadding=4 >

  <c:forEach var="imageSummaryRow" items="${imageSummaryRows}" >
    <tr>
      <td align='center' style="border: 1px solid black;" width="1%">
        ${imageSummaryRow.imgTag}
      </td>
      <td style="border: 1px solid black;">

        <div class='small' style='padding-top:4px; padding-bottom:4px;'>
          <b>Caption:</b> ${imageSummaryRow.caption} 
          <a href='${configBean.FEWI_URL}image/${imageSummaryRow.imageId}'>(details)</a>
        </div>

        <c:if test="${not empty imageSummaryRow.alleles}">
          <div class='small'><b>Represented Alleles:</b></div>
          <c:forEach var="imageAllele" items="${imageSummaryRow.alleles}" >
              <% // pull to scriptlet context
                ImageAllele imageAllele 
                  = (ImageAllele)pageContext.getAttribute("imageAllele"); 
              %>
              <div style='padding-top:2px; padding-bottom:2px;'>
              <a href="${configBean.FEWI_URL}allele/${imageAllele.alleleID}">
              <%=FormatHelper.superscript(imageAllele.getAlleleSymbol())%></a>,
              ${imageAllele.alleleName}
              </div>
          </c:forEach>
        </c:if>

        <div class='small' style='padding-top:8px; padding-bottom:4px;'>
          <b>Copyright:</b> ${imageSummaryRow.copyright} 
          <a href="${configBean.FEWI_URL}reference/${imageSummaryRow.reference.jnumID}">
            ${imageSummaryRow.reference.jnumID}
          </a>

        </div>

      </td>
    </tr>
  </c:forEach>

</table>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

