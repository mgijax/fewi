<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% 
  Allele allele = (Allele)request.getAttribute("allele"); 

  StyleAlternator leftTdStyles 
    = new StyleAlternator("detailCat1","detailCat2");
  StyleAlternator rightTdStyles 
    = new StyleAlternator("detailData1","detailData2");
%>
    
${templateBean.templateHeadHtml}

<title>Phenotype Images Associated With This Allele </title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
</style>

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="pheno_images_help.shtml ">    
  <span class="titleBarMainTitle">
    Phenotype Images Associated With This Allele 
  </span>
</div>



<!-- structural table -->
<table class="detailStructureTable">

  <!-- nomenclature -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Nomenclature
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

      <table width="100%">

      <tr>
        <td class="rightBorderThinGray" align="right" width="1%" nowrap="nowrap">
           <span class="smallLabel">Symbol: </span>
        </td>
        <td nowrap="nowrap">
          <span class="enhance" class="small">
            <a href="${configBean.FEWI_URL}allele/${allele.primaryID}">
            <%=FormatHelper.superscript(allele.getSymbol())%>
            </a>
          </span>
        </td>
      </tr>

      <tr>
        <td class="rightBorderThinGray" align="right" width="1%" nowrap="nowrap">
         <span class="smallLabel">Name: </span>
        </td>
        <td nowrap="nowrap" >
          <span class="small">
            <c:if test="${allele.geneName!=allele.name}">
              ${allele.geneName};
            </c:if>
            ${allele.name}
          </span>
        </td>
      </tr>

      <tr>
        <td class="rightBorderThinGray" align="right" width="1%" nowrap="nowrap">
          <span class="smallLabel">MGI ID: </span>
        </td>
        <td nowrap="nowrap" >
          <span class="small">
             ${allele.primaryID}
          </span>
        </td>
      </tr>

      <c:if test="${not empty synonyms}">
      <tr>
        <td class="rightBorderThinGray" align="right" width="1%" >
          <span class="smallLabel">Synonyms: </span>
        </td>
        <td nowrap="nowrap">
          <span class="small">
          <c:forEach var="alleleSynonym" items="${synonyms}" varStatus="status">
              <% // pull to scriptlet context
                AlleleSynonym alleleSynonym
                  = (AlleleSynonym)pageContext.getAttribute("alleleSynonym"); 
              %>
              <%=FormatHelper.superscript(alleleSynonym.getSynonym())%><c:if test="${not status.last}">,</c:if>
          </c:forEach>
         </span>
        </td>
      </tr>
      </c:if>

      </table>
    </td>
  </tr>


  <!-- images -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Image for <br> Allele
    </td>
    <td class="<%=rightTdStyles.getNext() %>">

      <div style="font-style: italic; padding-bottom:0.5em;">
        Click images for details 
      </div>

      <table class="borderedTable" width="100%">
      <tr>
        <td rowspan=2 class="resultsHeader" width="1%">
          Image
        </td>
        <td rowspan=2 class="resultsHeader">
          Caption
        </td>
        <td colspan=2 class="resultsHeader">
          Genotypes involving this allele
        </td>
      </tr>
      <tr>
        <td class="resultsHeader">
          Allelic Composition
        </td>
        <td class="resultsHeader">
          Genetic Background
        </td>
      </tr>

      <c:forEach var="imageSummaryRow" items="${imageSummaryRows}" >
        ${imageSummaryRow.row}
      </c:forEach>

      </table>

    </td>
  </tr>


</table>
${templateBean.templateBodyStopHtml}

