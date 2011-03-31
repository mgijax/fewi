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


  <!-- image -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Image for <br> Allele
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <em>Click images for details </em><br>
    </td>
  </tr>



</table>




${templateBean.templateBodyStopHtml}

