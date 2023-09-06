<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${imageType} Image Detail </title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
    StyleAlternator leftTdStyles = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles = new StyleAlternator("detailData1","detailData2");
%>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="ALLELE_pheno_image_detail_help.shtml">	
  <span class="titleBarMainTitle">${imageType} Image Detail </span>
</div>

<!-- structural table -->
<table class="detailStructureTable">
  <!-- IMAGE -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      Image
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <img id="myImage" src='${configBean.PIXELDB_URL}${image.pixeldbNumericID}'> 
      <div id="player"></div>
    </td>
  </tr>

  <!-- CAPTION -->
  <c:if test="${not empty image.caption}">
    <tr>
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
    	<fewi:ntc value="${image.copyright}"/>
      <a href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
    </td>
  </tr>

  <!-- ASSOC ALLELES -->
  <c:if test="${not empty imageAlleleList}">
    <tr  valign=top align=left>
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
	        <tr class="stripe1">
	          <td class="">
	            <a href="${configBean.FEWI_URL}allele/${imageAllele.alleleID}">
	            	<fewi:super value="${imageAllele.alleleSymbol}"/>
	            </a>
	          </td>
	          <td class="">${imageAllele.alleleName}</td>
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
            <tr class="stripe1">
              <td class=""><fewi:genotype value="${genotype}"/></td>
              <td class=""><fewi:super value="${genotype.backgroundStrain}"/></td>
            </tr>

          </c:forEach>
        </table>   
      </td>
    </tr>
  </c:if>
  
<!-- close structural table and page template-->
</table>

<script>
      // This code loads the IFrame Player API code asynchronously.
      // from -- https://developers.google.com/youtube/iframe_api_reference
      console.log('adding script element');
      var tag = document.createElement('script');
      tag.src = "https://www.youtube.com/iframe_api";
      var firstScriptTag = document.getElementsByTagName('script')[0];
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
      console.log('done adding script element');
</script>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
