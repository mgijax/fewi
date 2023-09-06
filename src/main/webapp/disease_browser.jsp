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

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/viz.js"></script>

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
  .headerStripe { 
    background-color: #D0E0F0; 
    font-size: 100%;
    font-family: Arial,Helvetica;
    color: #000001;
    font-weight: bold;  
  }
  .topBorder { border-top-color: #000000;
    border-top-style:solid;
    border-top-width:1px; 
  }
  .bottomBorder { border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:1px;
  }
  .leftBorder { border-left-color :#000000; 
    border-left-style:solid;
    border-left-width:1px; 
  }
  .rightBorder { border-right-color :#000000; 
    border-right-style:solid;
    border-right-width:1px; 
  }
  .allBorders {
    border-top: thin solid gray;
    border-bottom: thin solid gray;
    border-left: thin solid gray;
    border-right: thin solid gray;
    padding:3px;
    text-align:center;
  }
  .groupSeparater { 
    border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:2px; 
  }
  #tableSeparater {
    border-top: 2px solid #D0E0F0;
  }
  .stripe1 { background-color: #FFFFFF; }
  .stripe2 { background-color: #DDDDDD; }
  .leftAlign {text-align:left;}

  #overDiv table {
    background-color: #fff; 
  }

</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- header bar -->
<div id="titleBarWrapper" userdoc="VOCAB_do_browser_help.shtml">	
	<span class="titleBarMainTitle">Disease Ontology Browser</span>
</div>

<!-- to enable bootstrap -->
<div class="container-fluid">

  <!-- HEADER -->
  <div class="row" id="diseaseHeader">
    <div class="col-lg-12">
    <span class="diseaseHeaderDisease">
      <span id="diseaseNameID">${disease.disease} (<%= idLinker.getLink("Disease Ontology", disease.getPrimaryID()) %>)</span>
    </span><br/>  
    <span class="bold">Alliance:</span>
    <span id="allianceLink">
      <a href="${fn:replace(externalUrls.AGR_Disease, '@@@@', disease.primaryID)}" target="_blank">disease page</a>
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

		<!-- only link certain types of IDs -->
		<c:choose>
		  <c:when test="${id.logicalDB == 'OMIM' || 
		                  id.logicalDB == 'ORDO' ||
		                  id.logicalDB == 'KEGG' ||
		                  id.logicalDB == 'NCI' ||
		                  id.logicalDB == 'OMIM:PS' ||
		                  id.logicalDB == 'MESH' ||
		                  id.logicalDB == 'EFO'}">
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
  <ul class="nav nav-tabs tabs-up" id="review">
    <li><a href="${configBean.FEWI_URL}disease/termTab/${disease.primaryID}" 
    	data-target="#termTabContent" id="termTabButton" data-toggle="tabajax" class="active">Term Browser</a></li>
    <li><a href="${configBean.FEWI_URL}disease/geneTab/${disease.primaryID}" 
    	data-target="#geneTabContent" id="genesTabButton" data-toggle="tabajax">Genes (${disease.genesTabCount})</a></li>
    <li><a href="${configBean.FEWI_URL}disease/modelTab/${disease.primaryID}" 
    	data-target="#modelTabContent" id="modelsTabButton" data-toggle="tabajax">Models (${disease.modelsTabCount})</a></li>
  </ul>

  <!-- TAB CONTENT - filled dynamically via subsequent request -->
  <div class="tab-content">
    <div class="tab-pane active" id="termTabContent">
    </div>
    <div class="tab-pane" id="geneTabContent">
      <img src="/fewi/mgi/assets/images/loading.gif">	
    </div>
    <div class="tab-pane" id="modelTabContent">
    </div>
  </div>


  <!-- Include reference link -->
  <c:if test="${disease.diseaseReferenceCount > 0}">
    Disease References using Mouse Models 
    <a href="${configBean.FEWI_URL}reference/disease/${disease.primaryID}?typeFilter=Literature">(${diseaseRefCount})</a>
   </c:if>

</div>


<script type="text/javascript">

<!-- tab retrieval functionality -->
$('[data-toggle="tabajax"]').click(function(e) {
    var $this = $(this),
        loadurl = $this.attr('href'),
        targ = $this.attr('data-target');

    $.get(loadurl, function(data) {
        $(targ).html(data);
    });

    $this.tab('show');
    return false;
});

<!-- default tab action -->
$(document).ready(function(){

	<c:choose>
	  <c:when test="${openTab == 'genes'}">
		$("#genesTabButton").click(); 
	  </c:when>
	  <c:when test="${openTab == 'models'}">
		$("#modelsTabButton").click(); 
	  </c:when>
	  <c:otherwise>
		$("#termTabButton").click(); 
	  </c:otherwise>
	</c:choose>

});
</script>

<%@ include file="/WEB-INF/jsp/disease_detail_popups.jsp" %> 
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
