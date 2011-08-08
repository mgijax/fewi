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

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="pheno_image_summary_help.shtml">    
  <span class="titleBarMainTitle">
    Phenotype Images associated with this ${marker.markerType} 
  </span>
</div>


<!-- standard marker header -->
<jsp:include page="marker_header.jsp"></jsp:include><br>

${totalImages} phenotype image(s) for ${marker.symbol}<br>
Showing image(s) 1 to ${totalImages}

<hr>
Click on thumbnail to view full size image with links to phenotype annotations.
<br>
Click on allele symbol for full phenotype details.
<br>
<br>

<table class="imageTable" style="" cellpadding=4 >

  <c:forEach var="imageSummaryRow" items="${imageSummaryRows}" >
    <tr>
      <td style="border: 1px solid black;" width="1%">
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

${templateBean.templateBodyStopHtml}

