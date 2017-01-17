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
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>${disease.disease} Disease Ontology Browserr - ${disease.primaryID}</title>

<meta name="description" content="<c:choose><c:when test="${empty disease.mouseHumanGroup and empty disease.mouseOnlyGroup and empty disease.humanOnlyGroup}">There are currently no human or mouse genes associated with this disease in the MGI database.</c:when><c:otherwise>Mutations in human and/or mouse homologs are associated with this disease.</c:otherwise></c:choose><c:if test="${not empty disease.diseaseSynonyms}"> Synonyms: <c:forEach var="synonym" items="${disease.diseaseSynonyms}" varStatus="status">${synonym.synonym}<c:if test="${!status.last}">; </c:if></c:forEach></c:if>">


<%  // Pull detail object into servlet scope
    Disease disease = (Disease) request.getAttribute("disease");

    StyleAlternator leftTdStyles 
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles 
      = new StyleAlternator("detailData1","detailData2");
%>


<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>



<style type="text/css">

  .diseaseHeader { 
    border: 2px solid;
    border-color: #54709B;
    padding: 5px;
  }

  .diseaseHeaderDisease { 
    font-size: 1.5em;
  }

  .tabContainer { 
    width: 100%;
    min-height: 200px;
    border-top: 1px solid;
    border-bottom: 1px solid;
    border-color: #54709B;
    padding: 5px;
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
  <div class="row diseaseHeader">
    <div class="col-sm-12">
    <span class="diseaseHeaderDisease">
      ${disease.disease} (${disease.primaryID})
    </span><br/>  
    <c:if test="${not empty disease.diseaseSynonyms}">
      <span class="bold">Synonyms:</span>
      <c:forEach var="synonym" items="${disease.diseaseSynonyms}" varStatus="status">
        ${synonym.synonym}<c:if test="${!status.last}">; </c:if>
      </c:forEach>
    </c:if><br/>
    <c:if test="${not empty disease.orderedSecondaryIDs}">
      <span class="bold">Alt IDs:</span>
      <c:forEach var="id" items="${disease.orderedSecondaryIDs}" varStatus="status">
        ${id.accID}<c:if test="${!status.last}">, </c:if>
      </c:forEach>
    </c:if><br/>


    </div>
  </div>
  <br>

  <!-- TAB DEFINITIONS -->
  <ul class="nav nav-tabs">
    <li class="active"><a data-toggle="tab" href="#termTab">Term Details</a></li>
    <li><a data-toggle="tab" href="#genesTab">Genes</a></li>
    <li><a data-toggle="tab" href="#modelsTab">Models</a></li>
  </ul>

  <!-- TAB CONTENTS -->
  <div class="tab-content">
    <div id="termTab" class="tab-pane fade in active">
      <div class="tabContainer">
      <p>Future term detail tab</p>
      <c:if test="${not empty disease.vocabTerm.definition}">
        <span class="bold">Definition:</span>
        ${disease.vocabTerm.definition}
      </c:if><br/>
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


<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
