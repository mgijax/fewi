<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.IDLinker" %>
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
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${disease.disease} Disease Ontology Browser - ${disease.primaryID}</title>

<meta name="description" content="<c:choose>
  <c:when test="${empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup}">
    There are currently no human or mouse genes associated with this disease in the MGI database.</c:when>
  <c:otherwise>Mutations in human and/or mouse homologs are associated with this disease.</c:otherwise>
  </c:choose>
  <c:if test="${not empty disease.diseaseSynonyms}"> Synonyms: 
  <c:forEach var="synonym" items="${disease.diseaseSynonyms}" varStatus="status">${synonym.synonym}<c:if test="${!status.last}">;</c:if>
  </c:forEach>
  </c:if>">

<%  // Pull detail object into servlet scope
    Disease disease = (Disease) request.getAttribute("disease");

	// ID linker use to generate external urls for a given ID/LogicalDB
	IDLinker idLinker = (IDLinker) request.getAttribute("idLinker");
	VocabTermID id;
%>

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<style type="text/css">

  #diseaseHeader { 
    border: 3px solid;
    border-color: #54709B;
    padding: 5px;
  }
  .diseaseHeaderDisease { 
    font-size: 1.5em;
  }
  .tabContainer { 
    width: 100%;
    min-height: 250px;
    border-top: 1px solid;
    border-bottom: 1px solid;
    border-color: #54709B;
    padding: 5px;
  }
  .termWrapper {
    width: 100%;
    min-height: 200px;
	background-color: #DFEFFF;
    border: 4px solid #54709B;
    border-radius: 12px;
    padding: 8px;
  }
  #termInTermTabBubble  {
    background-color: #FFFFE0;
    padding-left: 3px;
  }
  .bubbleHeading {
    padding-left: 12px;
  }
  .superscript { 
    vertical-align: super; 
    font-size: 90%
  }
  .bold { 
    font-weight: bold;
  }

</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="VOCAB_do_browser_detail_help.shtml">	
	<span class="titleBarMainTitle">Disease Ontology Browser</span>
</div>

<!-- to enable bootstrap -->
<div class="container-fluid">

  <!-- HEADER -->
  <div class="row" id="diseaseHeader">
    <div class="col-lg-12">
    <span class="diseaseHeaderDisease">
      <span id="diseaseNameID">${disease.disease} (${disease.primaryID})</span>
    </span><br/>  
    <c:if test="${not empty disease.diseaseSynonyms}">
      <span class="bold">Synonyms:</span>
      <span id="diseaseSynonym">
      <c:forEach var="synonym" items="${disease.diseaseSynonyms}" varStatus="status">
        ${synonym.synonym}<c:if test="${!status.last}">; </c:if>
      </c:forEach>
      </span><br/>
    </c:if>



    <c:if test="${not empty disease.orderedSecondaryIDs}">
      <span class="bold">Alt IDs:</span>
      <span id="diseaseSecondaryIDs">
      <c:forEach var="id" items="${disease.orderedSecondaryIDs}" varStatus="status">


		<c:choose>
		  <c:when test="${id.logicalDB == 'OMIM'}">
		    <% id = (VocabTermID) pageContext.getAttribute("id"); %>
		    <%= idLinker.getLink(id.getLogicalDB(), id.getAccID()) %><c:if test="${!status.last}">, </c:if>
		  </c:when>
		  <c:otherwise>
            ${id.accID}<c:if test="${!status.last}">, </c:if>
		  </c:otherwise>
		</c:choose>
               
        
      </c:forEach>
      </span><br/>
    </c:if>





    <c:if test="${not empty disease.vocabTerm.definition}">
      <span class="bold">Definition:</span>
      <span id='diseaseDefinition'>${disease.vocabTerm.definition}</span>
    </c:if>
    </div>
  </div>
  <br>

  <!-- TAB DEFINITIONS -->
  <ul class="nav nav-tabs">
    <li class="active" ><a data-toggle="tab" href="#termTab">Term Details</a></li>
    <li><a id="genesTabButton" data-toggle="tab" href="#genesTab">Genes</a></li>
    <li><a id="modelsTabButton" data-toggle="tab" href="#modelsTab">Models</a></li>
  </ul>

  <!-- TAB CONTENTS -->
  <div class="tab-content">

  <div id="termTab" class="tab-pane fade in active">
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
      <span class='bubbleHeading'>Child terms(s)</span>
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
    
    </div>
  </div>



  <div id="genesTab" class="tab-pane fade">
      <div class="tabContainer">
      <p>Future genes tab</p>
      </div>
  </div>

  <div id="modelsTab" class="tab-pane fade">
      <div class="tabContainer">
      <p>Future models tab</p>
      </div>
    </div>
  </div>

  Disease References using Mouse Models 
  <a href="${configBean.FEWI_URL}reference/disease/${disease.primaryID}?typeFilter=Literature">(${diseaseRefCount})</a>
  
</div>


<script type="text/javascript">
$(document).ready(function(){
	<c:if test = "${openTab == 'genes'}">
		$("#genesTabButton").click(); 
	</c:if>
	<c:if test = "${openTab == 'models'}">
		$("#modelsTabButton").click(); 
	</c:if>
});
</script>


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
