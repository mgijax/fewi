<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.NotesTagConverter" %>
<%@ page import = "org.jax.mgi.fewi.config.ContextLoader" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.HashMap" %>
<%@ page import = "java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    Disease disease = (Disease) request.getAttribute("disease");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>
<style type="text/css">
#imageWrapper {width:100%;}
#imageHolder svg {
    display: block;
    margin: auto;
    width: 96%;
}

</style>

<div class="tabContainer">

    <div class="row tabWrapper" id="termTabWrapper">
    <div class="col-sm-4">
      <span class='bubbleHeading'>Parent term(s)</span>
      <div class="termWrapper" id="termTabParentWrapper">
        <c:forEach var="parent" items="${disease.vocabTerm.parentEdges}" varStatus="status">
          <a href="${configBean.FEWI_URL}disease/${parent.parent.primaryID}">${parent.parent.term}</a> +
          <img src="${configBean.WEBSHARE_URL}images/is-a.gif" alt="is-a" height="12" width="12" border="0">
          <c:if test="${!status.last}"><br> </c:if>
        </c:forEach>
      </div>
      </div>
      <div class="col-sm-4">
      <span class='bubbleHeading'>Term with siblings</span>
      <div class="termWrapper" id="termTabTermWrapper">
        <div id='termInTermTabBubble'>
        ${disease.disease}<c:if test="${disease.vocabTerm.isLeaf != 1}"> + </c:if>
        </div><br>
        <c:forEach var="sibling" items="${disease.vocabTerm.siblings}" varStatus="status">
          <a href="${configBean.FEWI_URL}disease/${sibling.primaryID}">${sibling.term}</a> <c:if test="${sibling.isLeaf != 1}"> + </c:if>
          <c:if test="${!status.last}"><br> </c:if>
        </c:forEach>
       </div>
      </div>
      <div class="col-sm-4">
      <span class='bubbleHeading'>Child term(s)</span>
      <div class="termWrapper" id="termTabChildWrapper">
        <c:forEach var="child" items="${disease.vocabTerm.vocabChildren}" varStatus="status">
          <img src="${configBean.WEBSHARE_URL}images/is-a.gif" alt="is-a" height="12" width="12" border="0">
          <a href="${configBean.FEWI_URL}disease/${child.childPrimaryId}">${child.childTerm}</a> <c:if test="${child.isLeaf != 1}"> + </c:if>
          <c:if test="${!status.last}"><br> </c:if>
        </c:forEach>
      </div>
      </div>
    </div>
    <br/>

    <img src="${configBean.WEBSHARE_URL}images/is-a.gif" alt="is-a" height="12" width="12" border="0"> denotes an 'is-a' relationship

    <div id='imageWrapper'>
      <div id='imageHolder'></div>
    </div>

	<script type="text/javascript">
        image = Viz('${dotInputStr}', 
                { format: "svg", engine: "dot" });
         document.getElementById('imageHolder').innerHTML += image
	</script>

</div>