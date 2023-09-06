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
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title> ${pageTitle} </title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<style type="text/css">
table.nomenInfo td{
  padding:3px;
  font-size:12px;
  font-family:Verdana,Arial,Helvetica;
  color:#000001;
  vertical-align:top;
}
</style>

<script>
</script>

<meta name="description" content="Mouse phenotype images associated with this allele.">
<meta name="keywords" content="MGI, mouse, phenotype, allele, mutant, image, picture, mutant image, allele image, phenotype image, MGI mouse images"> 

<meta name="robots" content="NOODP"/>
<meta name="robots" content="NOYDIR"/> 



<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_pheno_all_images_help.shtml">    
  <span class="titleBarMainTitle">
    ${pageHeading}
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

      <table width="100%" class='nomenInfo'>

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
      Images for <br> Allele
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
          Genotypes&nbsp;involving&nbsp;this&nbsp;allele
        </td>
      </tr>
      <tr>
        <td class="resultsHeader">
          Allelic&nbsp;Composition
        </td>
        <td class="resultsHeader">
          Genetic&nbsp;Background
        </td>
      </tr>

      <c:forEach var="imageSummaryRow" items="${imageSummaryRows}" >
        ${imageSummaryRow.phenoByAlleleRow}
      </c:forEach>

      </table>

    </td>
  </tr>


</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>

