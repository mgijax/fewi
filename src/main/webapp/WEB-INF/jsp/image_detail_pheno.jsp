<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Phenotype Image Detail </title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
    // pull to scriptlet 
    Image image = (Image)request.getAttribute("image");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");

    NotesTagConverter ntc = new NotesTagConverter();
%>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_pheno_image_detail_help.shtml">	
  <span class="titleBarMainTitle">Phenotype Image Detail </span>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- IMAGE -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Image
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <img src='${configBean.PIXELDB_URL}${image.pixeldbNumericID}'> 
    </td>
  </tr>


  <!-- CAPTION -->
  <c:if test="${not empty image.caption}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Caption
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        ${image.caption}
      </td>
    </tr>
  </c:if>


  <!-- COPYRIGHT -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Copyright
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <%=ntc.convertNotes(image.getCopyright(), '|')%>
      <a href="${configBean.FEWI_URL}reference/${reference.jnumID}">
        ${reference.jnumID}
      </a>
    </td>
  </tr>

  <!-- ASSOC ALLELES -->
  <c:if test="${not empty imageAlleleList}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Associated<br/>Alleles
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >

	<table class="mgitable">
	  <tr class="stripe2">
	    <td class="resultsHeader" style="background-color:#D0E0F0">
	      Symbol
	    </td>
	    <td class="resultsHeader" style="background-color:#D0E0F0">
	      Name
	    </td>
	  </tr>

          <c:forEach var="imageAllele" items="${imageAlleleList}" >

              <% // pull to scriptlet context
                ImageAllele imageAllele 
                  = (ImageAllele)pageContext.getAttribute("imageAllele"); 
              %>

            <tr class="stripe1">
              <td class="">
                <a href="${configBean.FEWI_URL}allele/${imageAllele.alleleID}">
                <%=FormatHelper.superscript(imageAllele.getAlleleSymbol())%>
                </a>
              </td>
              <td class="">
                ${imageAllele.alleleName}
              </td>
            </tr>

          </c:forEach>
        </table>   
      </td>
    </tr>
 
  </c:if>


  <!-- ASSOC GENOTYPES -->
  <c:if test="${not empty genotypeList}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Associated<br/>Genotypes
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >

	<table class="mgitable">
	  <tr class="stripe2">
	    <td class="resultsHeader" style="background-color:#D0E0F0">
	      Allelic Composition
	    </td>
	    <td class="resultsHeader" style="background-color:#D0E0F0">
	      Genetic Background
	    </td>
	  </tr>


          <c:forEach var="genotype" items="${genotypeList}" >

              <% // pull to scriptlet context
                Genotype genotype 
                  = (Genotype)pageContext.getAttribute("genotype"); 
              %>

            <tr class="stripe1">
              <td class="">
                <%=FormatHelper.newline2HTMLBR(ntc.convertNotes(genotype.getCombination3(), '|'))%>
              </td>
              <td class="">
                <%=FormatHelper.superscript(genotype.getBackgroundStrain())%>
              </td>
            </tr>

          </c:forEach>
        </table>   
      </td>
    </tr>
 
  </c:if>



<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
