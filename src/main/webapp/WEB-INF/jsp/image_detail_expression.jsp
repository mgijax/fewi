<%@ page import = "org.jax.mgi.fe.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Gene Expression Image Detail </title>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  
    // pull to scriptlet 
    Image image = (Image)request.getAttribute("image");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1Gxd","detailCat2Gxd");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1Gxd","detailData2Gxd");

    NotesTagConverter ntc = new NotesTagConverter();
%>

<style type="text/css">
</style>

<script>
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapperGxd" userdoc="EXPRESSION_query_results_help.shtml#images">	
	<a href="${configBean.HOMEPAGES_URL}expression.shtml"><img class="gxdLogo" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" height="75"></a>
	<span class="titleBarMainTitleGxd" style='display:inline-block; margin-top: 20px;'>Gene Expression Image Detail</span>
</div>

<!-- structural table -->
<table class="detailStructureTable">


  <!-- REFERENCE -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
       <div style="padding-top:7px;">Reference</div>
       <div style="padding-top:3px;">Figure</div>
       <div style="padding-top:2px;">ID</div>
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
       <div style="padding-top:7px;">
         <a href="${configBean.FEWI_URL}reference/${reference.jnumID}">
           ${reference.jnumID}</a>
         ${reference.miniCitation}
       </div>
       <div style="padding-top:3px;">
         ${image.figureLabel}
       </div>
       <div style="padding-top:2px;">
         ${image.mgiID}
       </div>
    </td>
  </tr>


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
  <!-- ASSOC ASSAYS -->
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
    <td id="copyright" class="<%=leftTdStyles.getNext() %>">
      Copyright
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <%=ntc.convertNotes(image.getCopyright(), '|')%>
    </td>
  </tr>

  <!-- ASSOC ASSAYS -->
  <c:if test="${not empty imagePaneList}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Associated<br/>Assays
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >

	<table class="mgitable">
	  <tr class="stripe2Gxd">
	    <td class="resultsHeader" style="background-color:#EBCA6D">
	      Label
	    </td>
	    <td class="resultsHeader" style="background-color:#EBCA6D">
	       Assay & Result Details (Gene Symbol)
	    </td>
	    <td class="resultsHeader" style="background-color:#EBCA6D">
	      Spatial Mapping
	    </td>
	  </tr>

          <c:forEach var="imagePane" items="${imagePaneList}" >

            <tr class="stripe1Gxd">
              <td class="">
               <% // pull to scriptlet context
                 ImagePane thisImageAllele = (ImagePane)pageContext.getAttribute("imagePane"); 
                %>
               <%=FormatHelper.superscript(thisImageAllele.getPaneLabel())%>
              </td>
              <td class="">
                <c:forEach var="imagePaneDetails" items="${imagePane.details}" >
                  <a href="${configBean.FEWI_URL}assay/${imagePaneDetails.assayID}">${imagePaneDetails.assayID}</a>
                  <a href="${configBean.FEWI_URL}marker/${imagePaneDetails.markerID}">(${imagePaneDetails.markerSymbol})</a>
                </c:forEach>
              </td>
              <td class="">
                <c:forEach var="imagePaneId" items="${imagePane.imagePaneIds}" >
                  <a target='_blank'
                    href='http://www.emouseatlas.org/emagewebapp/router?id=${imagePaneId.accID}'>
                    ${imagePaneId.accID}
                  </a>
                </c:forEach>
              </td>
            </tr>

          </c:forEach>
        </table>   
      </td>
    </tr>
 
  </c:if>


  <!-- OTHER DB LINKS -->
  <c:if test="${not empty otherIdLinks}">

    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Other Database<br/>Links
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >

        <c:forEach var="otherId" items="${otherIdLinks}" >
          ${otherId}
        </c:forEach>

      </td>
    </tr>
 
  </c:if>
















<!-- close structural table and page template-->
</table>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
