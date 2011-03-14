<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>Gene Expression Image Detail </title>

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

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="pheno_image_detail.shtml">	
  <span class="titleBarMainTitle">Gene Expression Image Detail </span>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- IMAGE -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Image
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <img src='http://www.informatics.jax.org/pixeldb/fetch_pixels.cgi?id=${image.pixeldbNumericID}'> 
    </td>
  </tr>


  <!-- CAPTION -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Caption
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${image.caption}
    </td>
  </tr>


  <!-- COPYRIGHT -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Copyright
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      ${image.copyright}
      <a href="${configBean.FEWI_URL}reference/${reference.jnumID}">
        ${reference.jnumID}
      </a>
    </td>
  </tr>

  <!-- ASSOC ASSAYS -->
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


<!-- close structural table and page template-->
</table>
${templateBean.templateBodyStopHtml}
