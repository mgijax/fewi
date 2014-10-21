<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.IDLinker" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% IDLinker idLinker = (IDLinker)request.getAttribute("idLinker"); %>

${templateBean.templateHeadHtml}

<SCRIPT TYPE="text/javascript" SRC='${configBean.WEBSHARE_URL}js/hideshow.js'></SCRIPT>

<title>${marker.symbol} MGI Mouse ${marker.markerType} Detail - ${marker.primaryID} - ${marker.name}</title>

<meta name="description" content="${seoDescription}" />
<meta name="keywords" content="${seoKeywords}" />
<meta name="robots" content="NOODP" />
<meta name="robots" content="NOYDIR" />
<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%  // Pull detail object into servlet scope
    // EXAMPLE - Marker marker = (Marker)request.getAttribute("marker");

    StyleAlternator leftTdStyles
      = new StyleAlternator("detailCat1","detailCat2");
    StyleAlternator rightTdStyles
      = new StyleAlternator("detailData1","detailData2");

%>

<style>
.bioMismatch td { text-align: center; border-style: solid; border-width: 1px; }
.bioMismatch .header { font-weight: bold; color:#002255; background-color:#aaaaaa; }

.link { color:#000099;
    cursor: pointer;
    border-bottom: 1px #000099 solid;
    text-decoration: none; }

.bold { font-weight: bold; }

.leftAlign { text-align: left; }

.allBorders {
    border-top: thin solid gray;
    border-bottom: thin solid gray;
    border-left: thin solid gray;
    border-right: thin solid gray;
    padding:3px;
}

.bottomBorder { border-bottom-color: #000000;
    border-bottom-style:solid;
    border-bottom-width:1px; }
    
    
/* Turning divs into table layout */
.container {
	border-bottom: 1px solid black;
}
.container .row {
	overflow: hidden;
}
.container .row > div {
	padding-bottom: 10003px;
	margin-bottom: -10000px;
}

.container .header {
	float: left;
	width: 130px;
	border: 1px solid black;
	text-align: right;
	font-weight: bold;
	font-size: 12px;
	font-family: Verdana,Arial,Helvetica;
	color: #000001;
	padding: 3px;
}
.container .detail {
	margin-left: 138px;
	border: 1px solid black;
	border-left: none;
	font-size: 12px;
	font-family: Verdana,Arial,Helvetica;
	color: #000001;
	padding: 3px;
}

.container .detailCat1 {
	background-color: #DFEFFF;
}
.container .detailCat2 {
	background-color: #D0E0F0;
}

.container .detailData2 {
	background-color: #F0F0F0;
}

</style>

<script language="Javascript">
$(function(){
	 window.isIntegerFlank = function(flank) {
	    // error if non-numeric flank
	    if (isNaN(flank)) {
	        alert ("An invalid value is specified for Flank (" + flank + ").  Flank must be an integer.");
	        return 0;
	    }
	
	    // error if flank is a float, not an integer
	    if (flank.indexOf('.') != -1) {
	        alert ("An invalid value is specified for Flank (" + flank + ").  Flank must be an integer -- without a decimal point.");
	        return 0
	    }
	
	    // error if flank has extra spaces
	    if (flank.indexOf(' ') != -1) {
	        alert ("An invalid value is specified for Flank (" + flank + ").  Flank must be an integer -- without extra spaces.");
	        return 0
	    }
	    return 1;
	}
	
	window.formatForwardArgs = function() {
	    document.sequenceForm.action = document.sequenceFormPullDown.seqPullDown.options[document.sequenceFormPullDown.seqPullDown.selectedIndex].value;
	
	    // ensure we have a valid value for Flank before proceeding
	    if (document.sequenceForm.flank1 && !isIntegerFlank(document.sequenceForm.flank1.value)) {
	        return 1;
	    }
	    document.sequenceForm.submit();
	}
	
	window.formatFastaArgs = function() {
	    // ensure we have a valid value for Flank before proceeding
	    if (document.markerCoordForm.flank1 && !isIntegerFlank(document.markerCoordForm.flank1.value)) {
	        return 1;
	    }
	    document.markerCoordForm.submit();
	}
	
	window.toggleHomologyDetails = function()
	{
	    toggle ("downArrowHomologs");
	    toggle ("rightArrowHomologs");
	    toggle ("humanHomologDetails");
	    if (mgihomeUrl != null) {
	        hitUrl (mgihomeUrl + "other/monitor.html", "toggleHomologyDetails=1");
	    }
	}
	
	/* cluster membership */
	
	function initializeClusterMembersPopup () {
		var elem = document.getElementById("clusterMemberTable");
		if (elem != null) {
		    YAHOO.namespace("markerDetail.container");
		
		    var props = { 
		    	visible:false, 
		    	constraintoviewport:true,
				context:['showClusterMembers', 'tl', 'br',
					[ 'beforeShow', 'windowResize' ] ] 
		    };
		
		    if (${memberCount} > 12) {
				props.height = "300px";
				props.width = (elem.offsetWidth + 40) + "px";
		    }
		    
		 	// make the div visible
		    elem.style.display = '';
		
			/* Wire up cluster members popup show link */
			YAHOO.markerDetail.container.clusterMemberPanel = new YAHOO.widget.Panel(
			        "clusterMemberDiv", 
			        props
			);
		    YAHOO.markerDetail.container.clusterMemberPanel.render();
		    YAHOO.util.Event.addListener ("showClusterMembers", "click",
		    	YAHOO.markerDetail.container.clusterMemberPanel.show,
		        YAHOO.markerDetail.container.clusterMemberPanel, 
		        true
		    );
		    YAHOO.util.Event.addListener (
		        "YAHOO.markerDetail.container.clusterMemberPanel", "move",
		        YAHOO.markerDetail.container.clusterMemberPanel.forceContainerRedraw
		    );
		    YAHOO.util.Event.addListener (
		        "YAHOO.markerDetail.container.clusterMemberPanel", "mouseover",
		        YAHOO.markerDetail.container.clusterMemberPanel.forceContainerRedraw
		    );
		}
	}
	initializeClusterMembersPopup();
	
	/* Wire up batch submit in cluster members popup */
	$("#clusterBatchLink").click(function(){
		$("#batchWebForm").submit();
	});
	
	window.log = function(msg) {
	    // log a message to the browser console
	    //setTimeout(function() { throw new Error(msg); }, 0);
	    console.log(msg);
	}
	
	/* formatting of GXD section */
	function formatGxdSection () {
		var gxdHeading = $("#gxdHeading");
		if (gxdHeading.length > 0) {
		    var gxdHeight = $('#gxd').height();
		    var headingHeight = $('#gxdHeading').height();
		    var logo = $('#gxdLogo');
		    var imageHeight = logo.height();
		
		    var imagePad = Math.round( (gxdHeight - imageHeight) / 2) - headingHeight;
		
		    // add padding to center the logo vertically in the expression ribbon
		    if (imagePad > 0) {
		        logo.css('padding-top', imagePad + 'px');
		    }
		}
	}
	formatGxdSection();
});
</script>

${templateBean.templateBodyStartHtml}

<style type="text/css">
td.padded { padding:4px; }
</style>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_detail_help.shtml" style="max-width: none;">
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${marker.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
    <div name="centeredTitle">
	  <span class="titleBarMainTitle"><fewi:super value="${marker.symbol}"/></span><br/>
	  ${marker.markerType} Detail
	</div>
</div>

<!-- structural table -->
<div class="container detailStructureTable">


  <!-- ROW1 -->
  <div class="row">
    <div class="header <%=leftTdStyles.getNext() %>">
      Symbol<br/><br/>
      Name<br/>
      ID
      <c:if test="${hasClusters}">
        <br/>Member&nbsp;of
      </c:if>
      <c:if test="${hasClusterMembers}">
      <br/>Cluster&nbsp;member<c:if test="${memberCount > 1}">s</c:if>
      </c:if>
    </div>
    <div class="detail <%=rightTdStyles.getNext() %>">
    	<div class="biotypeConflictDiv" style="float: right;">
    	<c:if test="${not empty biotypeConflictTable}">
          <a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'UL', 'UR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img src="${configBean.WEBSHARE_URL}images/warning2.gif" height="26" width="26" id="warning" border="0"></a>
          <a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'UL', 'UR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>BioType Conflict</a>
        </c:if>
        <c:if test="${not empty strainSpecificNote}">
          <a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'UL', 'UR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img src="${configBean.WEBSHARE_URL}images/mice.jpg" height="38" width="38" id="mice" border="0"></a>
          <a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'UL', 'UR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>Strain-Specific Marker</a>
        </c:if>
        </div>
        <div class="geneSymbolSection">
          <b style="font-size:x-large;"><fewi:super value="${marker.symbol}"/></b><c:if test="${marker.status == 'interim'}"> (Interim)</c:if>
          <br/>
          <b>${marker.name}</b><br/>
	  		${marker.primaryID}

	      <c:if test="${hasClusters}">
	          <br/>
	          <c:forEach var="cluster" items="${marker.clusters}" varStatus="status">
	              <a href="${configBean.FEWI_URL}marker/${cluster.relatedMarkerID}">${cluster.relatedMarkerSymbol}</a> cluster<c:if test="${!status.last}">, </c:if>
	          </c:forEach>
	      </c:if>

	      <c:if test="${hasClusterMembers}">
	        <br/>
			<c:forEach var="member" items="${marker.clusterMembers}" varStatus="status" end="2">
			    <a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a><c:if test="${!status.last}">, </c:if>
			</c:forEach>
			<c:if test="${memberCount > 3}">...</c:if>
				(<span id="showClusterMembers" class="link">${memberCount}</span> member<c:if test="${memberCount > 1}">s</c:if>)
		  </c:if>
	   </div>
    </div>
  </div>

  <!-- ROW1a -->
  <c:if test="${not empty marker.aliases}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        <c:if test="${marker.markerType == 'Gene'}">STS</c:if>
        <c:if test="${marker.markerType != 'Gene'}">STS for</c:if>
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
        <c:forEach var="alias" items="${marker.aliases}" varStatus="status">
          <a href="${configBean.FEWI_URL}marker/${alias.aliasID}">${alias.aliasSymbol}</a><c:if test="${!status.last}">, </c:if>
        </c:forEach>
      </div>
    </div>
  </c:if>

  <!-- ROW2 -->
  <c:if test="${not empty marker.synonyms}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Synonyms
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
        <c:forEach var="synonym" items="${marker.synonyms}" varStatus="status">
          <fewi:super value="${synonym.synonym}"/><c:if test="${!status.last}">, </c:if>
        </c:forEach>
      </div>
    </div>
  </c:if>

  <!-- ROW3 -->
  <c:if test="${not empty marker.markerSubtype}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Feature&nbsp;Type
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
        ${marker.markerSubtype}
      </div>
    </div>
  </c:if>

  <!-- ROW4 -->
  <c:set var="hasGeneticLocation" value="0"/>
  <c:if test="${(not empty marker.preferredCentimorgans)
  		or (not empty marker.preferredCytoband)
  		or (marker.countOfMappingExperiments > 0)}">
    <div class="row">
      <div class="header <%=leftTdStyles.getNext() %>">
        Genetic&nbsp;Map
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">

        <div>
        <c:if test="${not empty marker.preferredCentimorgans}">
          <c:if test="${marker.preferredCentimorgans.chromosome != 'UN'}">
          	<c:set var="linkmapUrl" value="${configBean.WI_URL}searches/linkmap.cgi?chromosome=${marker.preferredCentimorgans.chromosome}&midpoint=${marker.preferredCentimorgans.cmOffset}&cmrange=1.0&dsegments=1&syntenics=0"/>
	        <c:if test="${marker.preferredCentimorgans.cmOffset > 0.0}">
              <c:if test="${not empty miniMap}">
        	    <div style="float:right;text-align:left;"><a href="${linkmapUrl}"><img src="${miniMap}" border="0"></a></div>
              </c:if>
	         </c:if>
            Chromosome ${marker.preferredCentimorgans.chromosome}<br/>
            <c:if test="${marker.preferredCentimorgans.cmOffset > 0.0}">
              <c:set var="hasGeneticLocation" value="1"/>
			  <c:if test="${marker.markerType == 'QTL'}">
			    cM position of peak correlated region/marker:
			  </c:if>
              <fmt:formatNumber value="${marker.preferredCentimorgans.cmOffset}" minFractionDigits="2" maxFractionDigits="2"/> ${marker.preferredCentimorgans.mapUnits}<c:if test="${not empty marker.preferredCytoband}">, cytoband ${marker.preferredCytoband.cytogeneticOffset}</c:if>
              <br/>
              <a href="${linkmapUrl}">Detailed Genetic Map &#177; 1 cM</a><br/>
            </c:if>
            <c:if test="${marker.preferredCentimorgans.cmOffset == -1.0}">
			  <c:if test="${marker.markerType == 'QTL'}">
			    cM position of peak correlated region/marker:
			  </c:if>
			  Syntenic
            </c:if>
          </c:if>
          <c:if test="${marker.preferredCentimorgans.chromosome == 'UN'}">
            Chromosome Unknown
          </c:if>
          <br/>
        </c:if>
        <c:if test="${(empty marker.preferredCentimorgans) and (not empty marker.preferredCytoband)}">
          <c:if test="${marker.preferredCytoband.chromosome != 'UN'}">
            Chromosome ${marker.preferredCytoband.chromosome}<br/>
          </c:if>
          <c:if test="${marker.preferredCytoband.chromosome == 'UN'}">
            Chromosome Unknown<br/>
          </c:if>
          cytoband ${marker.preferredCytoband.cytogeneticOffset}<br/>
        </c:if>
        <c:if test="${not empty qtlIDs}">
        	Download data from the QTL Archive:
			<c:forEach var="qtlID" items="${qtlIDs}" varStatus="status">
			  ${qtlID}<c:if test="${!status.last}">, </c:if>
			</c:forEach>
			<br/>
        </c:if>
        <c:if test="${marker.countOfMappingExperiments > 0}">
          <br/></>Mapping data(<a href="${configBean.WI_URL}searches/mapdata_report_by_marker.cgi?${marker.markerKey}">${marker.countOfMappingExperiments}</a>)
        </c:if>
        </div>
      </div>
    </div>
  </c:if>

  <!-- ROW5 -->
  <c:if test="${not (empty marker.preferredCoordinates and empty vegaGenomeBrowserUrl and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Sequence&nbsp;Map
      </div>
      <fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
      <fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
	  <c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
	  <c:set var="coord1" value="${fn:replace('Chr<chr>:<start>-<end>', '<chr>', chromosome)}"/>
	  <c:set var="coord2" value="${fn:replace(coord1, '<start>', startCoord)}"/>
	  <c:set var="coords" value="${fn:replace(coord2, '<end>', endCoord)}"/>

      <div class="detail <%=rightTdStyles.getNext() %>">
        <table style="width: 100%;">
        <tr><td>
        <c:if test="${not empty marker.preferredCoordinates}">
        Chr${chromosome}:${startCoord}-${endCoord}
          ${marker.preferredCoordinates.mapUnits}<c:if test="${not empty marker.preferredCoordinates.strand}">, ${marker.preferredCoordinates.strand} strand</c:if><br/>
        <c:if test="${empty marker.preferredCoordinates}">
          ${marker.preferredCoordinates.mapUnits}<br/>
        </c:if>
        </c:if>
        <c:if test="${not empty marker.qtlNote}">
          ${marker.qtlNote}<br/>
        </c:if>
        <c:if test="${not empty marker.preferredCoordinates}">
        From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier}<br/>
	<p/>
        </c:if>

        <c:if test="${not empty marker.preferredCoordinates}">
	    <form name="markerCoordForm" method="GET" action="${configBean.SEQFETCH_URL}">
	    <c:set var="length" value="${marker.preferredCoordinates.endCoordinate - marker.preferredCoordinates.startCoordinate + 1}"/>
	    <c:set var="seqfetchParms" value="mousegenome!!${marker.preferredCoordinates.chromosome}!${startCoord}!${endCoord}!!"/>

	    <!-- handle end < start, which is very atypical -->
	    <c:if test="${length < 0}">
	        <c:set var="length" value="${marker.preferredCoordinates.startCoordinate - marker.preferredCoordinates.endCoordinate + 1}"/>
	        <c:set var="seqfetchParms" value="mousegenome!!${marker.preferredCoordinates.chromosome}!${endCoord}!${startCoord}!!"/>
	    </c:if>

      	    <fmt:formatNumber value="${length}" pattern="#0" var="lengthStr"/>

	    <input type="hidden" name="seq1" value="${seqfetchParms}">
	    <input type="button" value="Get FASTA" onClick="formatFastaArgs()">
	    &nbsp;&nbsp;${lengthStr} bp
	    &nbsp;&nbsp;&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb flank
	    </form>
	    <p/>
	</c:if>

        <c:set var="vegaID" value="${marker.vegaGeneModelID.accID}"/>
        <c:set var="ensemblID" value="${marker.ensemblGeneModelID.accID}"/>
        <c:set var="ncbiID" value="${marker.ncbiGeneModelID.accID}"/>
        <c:set var="foundOne" value="0"/>
		<c:if test="${not empty vegaGenomeBrowserUrl}">
		  <a href="${vegaGenomeBrowserUrl}" target="_new">VEGA Genome Browser</a>
		  <c:set var="foundOne" value="1"/>
		</c:if>
		<c:if test="${not empty ensemblGenomeBrowserUrl}">
		  <c:if test="${foundOne > 0}"> | </c:if>
		  <a href="${ensemblGenomeBrowserUrl}" target="_new">Ensembl Genome Browser</a>
		  <c:set var="foundOne" value="1"/>
		</c:if>
		<c:if test="${not empty ucscGenomeBrowserUrl}">
		  <c:if test="${foundOne > 0}"> | </c:if>
		  <a href="${ucscGenomeBrowserUrl}" target="_new">UCSC Browser</a>
		  <c:set var="foundOne" value="1"/>
		</c:if>
		<c:if test="${not empty ncbiMapViewerUrl}">
		  <c:if test="${foundOne > 0}"> | </c:if>
		  <a href="${ncbiMapViewerUrl}" target="_new">NCBI Map Viewer</a>
		</c:if>
		</td><td align="right" width="*">
		  <c:if test="${not empty jbrowseUrl}">
		    <table><tr><td align="center">
		    <c:if test="${not empty gbrowseThumbnailUrl}">
		    <a href="${jbrowseUrl}"><img border="0" src="${gbrowseThumbnailUrl}"/></a>
		    <br/>
		    </c:if>
		    <a href="${jbrowseUrl}">Mouse Genome Browser</a>
		  </td></tr></table>
		  </c:if>
		</td></tr>
		</table>
      </div>
    </div>
  </c:if>

  <!-- Vertebrate homology ribbon -->
  <c:if test="${(not empty homologyClass) or (marker.hasOneEnsemblGeneModelID) or (not empty marker.pirsfAnnotation)}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Vertebrate<br/>homology
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
	<c:if test="${not empty homologyClass}">
	  HomoloGene:${homologyClass.primaryID}&nbsp;&nbsp;<a href="${configBean.FEWI_URL}homology/${homologyClass.primaryID}">Vertebrate Homology Class</a><br/>
	  <c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">${organismOrthology.markerCount} ${organismOrthology.organism}<c:if test="${!status.last}">;</c:if> </c:forEach><p/>
	</c:if>

		<c:set var="pirsf" value="${marker.pirsfAnnotation}"/>
		<c:if test="${not empty pirsf}">
		  Protein SuperFamily: <a href="${configBean.FEWI_URL}vocab/pirsf/${pirsf.termID}">${pirsf.term}</a><br/>
		</c:if>
		<c:if test="${marker.hasOneEnsemblGeneModelID}">
			<c:set var="genetreeUrl" value="${configBean.GENETREE_URL}"/>
			<c:set var="genetreeUrl" value="${fn:replace(genetreeUrl, '<model_id>', marker.ensemblGeneModelID.accID)}"/>
			Gene Tree: <a href="${configBean.GENETREE_URL}${marker.ensemblGeneModelID.accID}">${marker.symbol}</a><br/>
		</c:if>
      </div>
    </div>
  </c:if>

  <!-- Human Homologs ribbon -->
  <c:if test="${not empty humanHomologs}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">Human<br/>homologs</div>
      <div class="detail <%=rightTdStyles.getNext() %>">
	<!-- details for each human marker -->
	<table id="humanHomologDetails">

	  <c:forEach var="humanHomolog" items="${humanHomologs}" varStatus="status">
	    <c:set var="humanCytoband" value="${humanHomolog.preferredCytoband}"/>
	    <c:set var="humanCoords" value="${humanHomolog.preferredCoordinates}"/>
	    <fmt:formatNumber value="${humanCoords.startCoordinate}" pattern="#0" var="humanStartCoord"/>
	    <fmt:formatNumber value="${humanCoords.endCoordinate}" pattern="#0" var="humanEndCoord"/>

	    <c:set var="borders" value=""/>
	    <c:if test="${!status.first}">
	      <c:set var="borders" value=' style="border-bottom:none;border-right:none;border-left:none;border-top:thin solid grey"'/>
	    </c:if>

	    <tr><td${borders}>Human Homolog</td><td${borders}>&nbsp;</td><td${borders}>${humanHomolog.symbol}, ${humanHomolog.name}</td></tr>
	  <tr><td>NCBI Gene ID</td><td>&nbsp;</td><td><a href="${fn:replace(urls.Entrez_Gene, '@@@@', humanHomolog.entrezGeneID.accID)}" target="_blank">${humanHomolog.entrezGeneID.accID}</a></td></tr>

	  <c:if test="${not empty humanHomolog.neXtProtIDs}">
	  <tr><td>neXtProt AC</td><td>&nbsp;</td><td>
	    <c:forEach var="neXtProt" items="${humanHomolog.neXtProtIDs}" varStatus="neXtProtStatus">
	    <a href="${fn:replace(urls.neXtProt, '@@@@', neXtProt.accID)}" target="_blank">${neXtProt.accID}</a><c:if test="${!neXtProtStatus.last}">, </c:if>
	    </c:forEach>
	  </td></tr>
	  </c:if>

	  <c:if test="${not empty humanHomolog.synonyms}">
	  <tr><td>Human Synonyms</td><td>&nbsp;</td><td>
	    <c:forEach var="synonym" items="${humanHomolog.synonyms}" varStatus="synonymStatus">
	      ${synonym.synonym}<c:if test="${!synonymStatus.last}">, </c:if>
	    </c:forEach>
	  </td></tr>
	  </c:if>
	  <tr><td>Human Chr (Location)</td><td>&nbsp;</td><td>
	    <c:if test="${not empty humanCytoband}">${humanCytoband.chromosome}${humanCytoband.cytogeneticOffset}<c:if test="${not empty humanCoords}">; </c:if></c:if>
	    <c:if test="${not empty humanCoords}">
	      chr${humanCoords.chromosome}:${humanStartCoord}-${humanEndCoord}
	      <c:if test="${not empty humanCoords.strand}">(${humanCoords.strand})</c:if>&nbsp;&nbsp;<I>${humanCoords.buildIdentifier}</I>
	    </c:if>
	  </td></tr>
	  <c:if test="${not empty humanHomolog.OMIMHumanAnnotations}">
	  <tr><td>Disease Associations</td><td>&nbsp;</td><td>
		(<a href="" onclick="return overlib( '<table name=\'results\' border=\'0\' cellpadding=\'3\' cellspacing=\'0\' width=\'100%\'><tr ><th align=\'left\'>Human Disease</th><th width=\'4\'></th>' +
			'<th width=\'65\'>OMIM ID</th></tr>' +
			<c:set var="hMessage" value="&nbsp;" />
			<c:forEach var="annotation" items="${humanHomolog.OMIMHumanAnnotations}" varStatus="status">
				<c:set var="rColor" value="" />
				<c:if test="${status.count % 2 == 0}">
					<c:set var="rColor" value="style=\\'background-color:#F8F8F8;\\'" />
				</c:if>
				'<tr ${rColor} align=\'left\' valign=\'top\'>' +
				'<td><a href=\'${configBean.FEWI_URL}disease/${annotation.termID}\'>${annotation.term}</a></td>' +
				'<td width=\'4\'>'  +
				<c:forEach var="star" items="${marker.OMIMAnnotations}">
					<c:if test="${annotation.termID eq star.termID}">
						<c:set var="hMessage" value="* Disease is associated with mutations in mouse ${marker.symbol}." />
						'*' +
					</c:if>
				</c:forEach>
				'</td>' +
				'<td><a href=\'${fn:replace(urls.OMIM, '@@@@', annotation.termID)}\' target=\'_blank\'>${annotation.termID}</a></td></tr>' +
			</c:forEach>
			'<tr align=\'left\' valign=\'top\'><td  colspan=\'3\'>${hMessage}</td></tr></table>', STICKY, CAPTION, 'Human Disease Models Associated with Alleles of Human ${humanHomolog.symbol}', RIGHT, BELOW, WIDTH, 500, DELAY, 250, CLOSECLICK, CLOSETEXT, 'Close X');" onmouseout="nd();">${humanHomolog.countOfHumanDiseases}</a>)
		Disease<c:if test="${humanHomolog.countOfHumanDiseases > 0}">s</c:if> Associated with Human ${humanHomolog.symbol}
	  </td></tr>
	  </c:if>
	  </c:forEach>

	</table>
    </div></div>
  </c:if>

  <!-- Allele ribbon -->
  <c:if test="${(marker.countOfAlleles > 0) or (not empty marker.markerClip) or (not empty marker.incidentalMutations) or (marker.countOfHumanDiseases > 0) or (marker.countOfAllelesWithHumanDiseases > 0) or (marker.countOfPhenotypeImages > 0)}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
      Mutations,<br/>alleles, and<br/>phenotypes
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
        <c:if test="${marker.countOfAlleles > 0}">
		  <c:set var="alleleUrl" value="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}"/>
		  All mutations/alleles(<a href="${alleleUrl}">${marker.countOfAlleles}</a>) :
		  <c:forEach var="item" items="${marker.alleleCountsByType}">
		    ${item.countType}(<a href="${alleleUrl}&alleleType=${item.countType}">${item.count}</a>)
		  </c:forEach>
		  <br/>
		</c:if>
		<c:if test="${marker.countOfMutationInvolves > 0}">
		    Genomic Mutations involving ${marker.symbol} (<a href="${alleleUrl}&mutationInvolves=1">${marker.countOfMutationInvolves}</a>)<br/>
		</c:if>
		<c:if test="${not empty marker.incidentalMutations}">
			Incidental mutations (data from
			<c:forEach var="incidentalMutation" items="${marker.incidentalMutations}" varStatus="imStatus">
				<c:if test="${imStatus.index>0}">, </c:if><a href="${configBean.FTP_BASE_URL}datasets/incidental_muts/${incidentalMutation.filename}">${incidentalMutation.filenameNoExtension}</a>
			</c:forEach>
			)<br/>
		</c:if>
		<c:if test="${not empty marker.markerClip}">
		  &nbsp;<br/>
		  <blockquote>${marker.markerClip}</blockquote>
		  &nbsp;<br/>
		</c:if>
		<c:if test="${marker.countOfHumanDiseases > 0}">
		  Human Diseases Modeled Using Mouse ${marker.symbol} (<a href="" onclick="return overlib( '<span style=\'font-size:12px;\'>Diseases ' +
			  'listed here are those where a mutant allele of this gene is involved in a mouse genotype used as ' +
			  'a model. This does not mean that mutations in this gene contribute to or are causative of the disease.</span>' +
			  '<table name=\'results\' border=\'0\' cellpadding=\'3\' cellspacing=\'0\' width=\'100%\'>' +
			'<tr ><th align=\'left\'>Human Disease</th><th width=\'4\'></th>' +
			'<th width=\'65\'>OMIM ID</th></tr>' +
			<c:set var="mMessage" value="&nbsp;" />
			<c:forEach var="annotation" items="${marker.OMIMAnnotations}" varStatus="status">
				<c:set var="rColor" value="" />
				<c:if test="${status.count % 2 == 0}">
					<c:set var="rColor" value="style=\\'background-color:#F8F8F8;\\'" />
				</c:if>
				'<tr ${rColor} align=\'left\' valign=\'top\'>' +
				'<td><a href=\'${configBean.FEWI_URL}disease/${annotation.termID}\'>${annotation.term}</a></td>' +
				'<td width=\'4\'>' +
				<c:forEach var="star" items="${humanOrtholog.OMIMHumanAnnotations}">
					<c:if test="${annotation.termID eq star.termID}">
						<c:set var="mMessage" value="* Disease is associated with mutations in human ${humanOrtholog.symbol}." />
						'*' +
					</c:if>
				</c:forEach>
				'</td>' +
				'<td><a href=\'${fn:replace(urls.OMIM, '@@@@', annotation.termID)}\' target=\'_blank\'>${annotation.termID}</a></td></tr>' +
			</c:forEach>
			'<tr align=\'left\' valign=\'top\'><td  colspan=\'3\'>${mMessage}</td></tr></table>', STICKY, CAPTION, 'Human Disease Models Associated with Alleles of Mouse ${marker.symbol}', RIGHT, BELOW, WIDTH, 500, DELAY, 250, CLOSECLICK, CLOSETEXT, 'Close X');" onmouseout="nd();">${marker.countOfHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
		  Alleles Annotated to Human Diseases(<a href="${configBean.FEWI_URL}allele/summary?markerId=${marker.primaryID}&hasOMIM=1">${marker.countOfAllelesWithHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfPhenotypeImages > 0}">
		  Phenotype Images(<a href="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}">${marker.countOfPhenotypeImages}</a>)
		</c:if>
      </div>
    </div>
  </c:if>

  <!-- interactions ribbon -->
  <c:if test="${not empty interactions}">
    <div class="row">
      <div class="header <%=leftTdStyles.getNext() %>">
    	<span style="white-space: nowrap; vertical-align: top;">
	      	<img id="interactionNewImage" src="${configBean.WEBSHARE_URL}images/new_icon.png" style="width: 35px; vertical-align: top;">
	        Interactions
        </span>
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
	
		<span style="display: inline;">
			<c:forEach var="interaction" items="${interactions}" varStatus="status">
			${interaction}<c:if test="${!status.last}"><br/></c:if>
			</c:forEach>
			<div style="display: inline; margin-left: 5px;">
			  <a id="interactionLink" href="${configBean.FEWI_URL}interaction/explorer?markerIDs=${marker.primaryID}" class="markerNoteButton" style="display:inline;">View All</a>
			</div>
		</span>
      </div>
    </div>
  </c:if>

  <!-- go classifications -->
  <c:if test="${(marker.countOfGOTerms > 0) or (marker.isInReferenceGenome > 0)}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Gene&nbsp;Ontology<br/>(GO)<br/>classifications
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
		All GO classifications: (<a href="${configBean.FEWI_URL}go/marker/${marker.primaryID}">${marker.countOfGOTerms}</a> annotations)<br/>
		<table>
	      <c:if test="${not empty processAnnot1}">
	    	<tr><td>Process</td>
	    		<td style="padding-left:6em;"><a href="${configBean.WI_URL}searches/GO.cgi?id=${processAnnot1.termID}">${processAnnot1.term}</a><c:if test="${not empty processAnnot2}">, </c:if>
	    			<a href="${configBean.WI_URL}searches/GO.cgi?id=${processAnnot2.termID}">${processAnnot2.term}</a><c:if test="${not empty processAnnot3}">, ...</c:if>
	    		</td></tr>
	      </c:if>
	      <c:if test="${not empty componentAnnot1}">
	    	<tr><td>Component</td>
	    		<td style="padding-left:6em;"><a href="${configBean.WI_URL}searches/GO.cgi?id=${componentAnnot1.termID}">${componentAnnot1.term}</a><c:if test="${not empty componentAnnot2}">, </c:if>
	    			<a href="${configBean.WI_URL}searches/GO.cgi?id=${componentAnnot2.termID}">${componentAnnot2.term}</a><c:if test="${not empty componentAnnot3}">, ...</c:if>
	    		</td></tr>
	      </c:if>
	      <c:if test="${not empty functionAnnot1}">
	    	<tr><td>Function</td>
	    		<td style="padding-left:6em;"><a href="${configBean.WI_URL}searches/GO.cgi?id=${functionAnnot1.termID}">${functionAnnot1.term}</a><c:if test="${not empty functionAnnot2}">, </c:if>
	    			<a href="${configBean.WI_URL}searches/GO.cgi?id=${functionAnnot2.termID}">${functionAnnot2.term}</a><c:if test="${not empty functionAnnot3}">, ...</c:if>
	    		</td></tr>
	      </c:if>
		</table>
		<c:if test="${marker.isInReferenceGenome > 0}">
		  This is a <a href="${referenceGenomeURL}">GO Consortium Reference Genome Project</a> gene.<br>
		</c:if>
		<c:set var="funcbaseID" value="${marker.funcBaseID}"/>
		<c:if test="${not empty funcbaseID}">
		  External Resources:
		  <a href="${fn:replace(urls.FuncBase, '@@@@', funcbaseID.accID)}">FuncBase</a><br/>
		</c:if>
      </div>
    </div>
  </c:if>

  <!-- expression row -->
  <c:set var="allenID" value="${marker.allenBrainAtlasID.accID}"/>
  <c:set var="gensatID" value="${marker.gensatID.accID}"/>
  <c:set var="geoID" value="${marker.geoID.accID}"/>
  <c:set var="arrayExpressID" value="${marker.arrayExpressID.accID}"/>

  <c:if test="${not (empty marker.gxdAssayCountsByType and (marker.countOfGxdLiterature < 1) and (marker.countOfCdnaSources < 1) and empty allenID and empty gensatID and empty geoID and empty arrayExpressID)}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
			<div id="gxdHeading">
				Expression
			</div>
		<div id="gxdLogo" style="margin-top: auto;">
		      <a href="${configBean.HOMEPAGES_URL}expression.shtml">
		      <img id="gxdLogoImage" src="${configBean.WEBSHARE_URL}images/gxd_logo.png" style='width: 90%;'>
		      </a>
	    </div>
      </div>
      <div id="gxd" class="detail <%=rightTdStyles.getNext() %>">
		<c:if test="${marker.countOfGxdLiterature > 0}">
		  <div id="gxdLit" style="vertical-align:top">
		  Literature Summary: (<a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a> records)<br/>
		  </div>
		</c:if>
		<div id="gxdOther" style="margin-top: 5px;">
		<c:if test="${not empty gxdAssayTypes}">
		Data Summary:
		<c:if test="${marker.countOfGxdResults > 0}">
		  Results (<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}">${marker.countOfGxdResults}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdTissues > 0}">
		  Tissues (<a href="${configBean.FEWI_URL}tissue/marker/${marker.primaryID}">${marker.countOfGxdTissues}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdImages > 0}">
		  Images (<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=imagestab">${marker.countOfGxdImages}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdResults > 0}">
		  Tissue x Stage Matrix (<a href="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?tab=stagegridtab">view</a>)
		  <span style="position: absolute;">
		  	<img id="gxdNewImage" src="${configBean.WEBSHARE_URL}images/new_icon.png" style="position: relative; top: -14px; width: 35px;">
		  </span>
		</c:if>
		<br/>
		</c:if>

		<c:if test="${not empty gxdAssayTypes}">
		  <c:set var="gxdResultUrl" value="${configBean.FEWI_URL}gxd/marker/${marker.primaryID}?assayType="/>
		  <table>
		    <tr><td style="padding-left:2em;">Assay Type</td><td>Results</td></tr>
		    <c:forEach var="assayType" items="${gxdAssayTypes}">
		      <tr><td style="padding-left:4em;padding-right:1em;">${assayType}</td>
		        <td align="right"><a href="${gxdResultUrl}${assayType}">${gxdResultCounts[assayType]}</a></td>
		      </tr>
		    </c:forEach>
		  </table>
		</c:if>

		<c:if test="${marker.countOfCdnaSources > 0}">cDNA source data(<a href="${configBean.WI_URL}searches/estclone_report.cgi?_Marker_key=${marker.markerKey}&sort=Tissue">${marker.countOfCdnaSources}</a>)<br/></c:if>

		<c:if test="${not (empty allenID and empty gensatID and empty geoID and empty arrayExpressID)}">
		  External Resources:
		  <c:if test="${not empty allenID}">
		    <a href="${fn:replace (externalUrls.Allen_Brain_Atlas, '@@@@', allenID)}" target="_new">Allen Institute</a>&nbsp;&nbsp;
		  </c:if>
		  <c:if test="${not empty gensatID}">
		    <a href="${fn:replace (externalUrls.GENSAT, '@@@@', gensatID)}" target="_new">GENSAT</a>&nbsp;&nbsp;
		  </c:if>
		  <c:if test="${not empty geoID}">
		    <a href="${fn:replace (externalUrls.GEO, '@@@@', geoID)}" target="_new">GEO</a>&nbsp;&nbsp;
		  </c:if>
		  <c:if test="${not empty arrayExpressID}">
		    <a href="${fn:replace (externalUrls.ArrayExpress, '@@@@', arrayExpressID)}" target="_new">Expression Atlas</a>
		  </c:if>
		  <br/>

		</c:if>
		</div>
	</div>
   </div>
  </c:if>

  <!-- Molecular reagents -->
  <c:if test="${(not empty marker.molecularReagentCountsByType) || (marker.countOfMicroarrayProbesets > 0)}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Molecular<br/>reagents
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
        <c:set var="reagentUrl" value="${configBean.WI_URL}searches/probe_report.cgi?_Marker_key=${marker.markerKey}"/>
		<c:forEach var="item" items="${marker.molecularReagentCountsByType}">
		  <c:set var="reagentType" value="&DNAtypes=${item.countType}"/>
		  <c:if test="${fn:startsWith(item.countType, 'Primer')}">
		    <c:set var="reagentType" value="&DNAtypes=primer"/>
		  </c:if>
		  <c:if test="${item.countType == 'Other'}">
		    <c:set var="reagentType" value="&notDNAtypes=genomic,primer,cDNA"/>
		  </c:if>
		  <c:if test="${fn:startsWith(item.countType, 'All')}">
		    <c:set var="reagentType" value=""/>
		  </c:if>
		  ${item.countType}(<a href="${reagentUrl}${reagentType}">${item.count}</a>)
		</c:forEach>
		<br/>
		<c:if test="${marker.countOfMicroarrayProbesets > 0}">
		  Microarray probesets(<a href="${configBean.FEWI_URL}marker/probeset/${marker.primaryID}">${marker.countOfMicroarrayProbesets}</a>)
		</c:if>
      </div>
    </div>
  </c:if>

  <!-- Other database links -->
  <c:if test="${not empty logicalDBs}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Other&nbsp;database<br/>links
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
		<table>
		  <c:forEach var="item" items="${logicalDBs}">
			<tr><td>${item}</td><td>${otherIDs[item]}</td></tr>
		  </c:forEach>
		</table>
      </div>
    </div>
  </c:if>


  <!-- Sequences ribbon -->
  <c:if test="${marker.countOfSequences > 0}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Sequences
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
		<form name="sequenceForm" method="GET">
		<table class="padded">
		  <tr><td class="padded" colspan="4">Representative Sequences</td><td class="padded">Length</td><td class="padded">Strain/Species</td><td class="padded">Flank</td></tr>
		  <c:if test="${not empty marker.representativeGenomicSequence}">
			<c:set var="seq" value="${marker.representativeGenomicSequence}" scope="request"/>
			<% Sequence seqDna = (Sequence) request.getAttribute("seq"); %>
		    <tr><td class="padded"><input type="checkbox" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqDna) %>"></td><td>genomic</td>
		      <td class="padded">${marker.representativeGenomicSequence.primaryID}</td>
		      <td class="padded">${fn:replace(genomicLink, "VEGA", "VEGA Gene Model")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeGenomicSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td class="padded">${marker.representativeGenomicSequence.length}</td>
		      <td class="padded">${genomicSource}</td>
		      <td class="padded">&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb</td></tr>
		  </c:if>
		  <c:if test="${not empty marker.representativeTranscriptSequence}">
			<c:set var="seq" value="${marker.representativeTranscriptSequence}" scope="request"/>
			<% Sequence seqRna = (Sequence) request.getAttribute("seq"); %>
		    <tr><td class="padded"><input type="checkbox" name="seq2" value="<%= FormatHelper.getSeqForwardValue(seqRna) %>"></td><td>transcript</td>
		      <td class="padded">${marker.representativeTranscriptSequence.primaryID}</td>
		      <td class="padded">${transcriptLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeTranscriptSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td class="padded">${marker.representativeTranscriptSequence.length}</td>
		      <td class="padded">${transcriptSource}</td><td>&nbsp;</td></tr>
		  </c:if>
		  <c:if test="${not empty marker.representativePolypeptideSequence}">
			<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
			<% Sequence seqPoly = (Sequence) request.getAttribute("seq"); %>
		    <tr><td class="padded"><input type="checkbox" name="seq3" value="<%= FormatHelper.getSeqForwardValue(seqPoly) %>"></td><td>polypeptide</td>
		      <td class="padded">${marker.representativePolypeptideSequence.primaryID}</td>
		      <td class="padded">${polypeptideLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativePolypeptideSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td class="padded">${marker.representativePolypeptideSequence.length}</td>
		      <td class="padded">${polypeptideSource}</td><td>&nbsp;</td></tr>
		  </c:if>
		</table>
		</form>
		<p>
		<form name="sequenceFormPullDown">
		  <I>For the selected sequences</I>
		  <select name="seqPullDown">
		  <option value="${configBean.SEQFETCH_URL}" selected> download in FASTA format</option>
		  <option value="${configBean.MOUSEBLAST_URL}seqSelect.cgi"> forward to MouseBLAST</option>
		  <input type="button" value="Go" onClick="formatForwardArgs()">
		  </select>
		</form>
		<c:set var="seqUrl" value="${configBean.FEWI_URL}sequence/marker/${marker.primaryID}"/>
		<c:if test="${marker.countOfSequences > 0}">
		  All sequences(<a href="${seqUrl}">${marker.countOfSequences}</a>)
		</c:if>
		<c:if test="${marker.countOfRefSeqSequences > 0}">
		  RefSeq(<a href="${seqUrl}?provider=RefSeq">${marker.countOfRefSeqSequences}</a>)
		</c:if>
		<c:if test="${marker.countOfUniProtSequences > 0}">
		  UniProt(<a href="${seqUrl}?provider=UniProt">${marker.countOfUniProtSequences}</a>)
		</c:if>
      </div>
    </div>
  </c:if>

  <!-- Polymorphisms ribbon -->
  <c:if test="${not empty marker.polymorphismCountsByType}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Polymorphisms
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
		<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
		  <c:set var="countText" value="${item.countType}"/>
		  <c:set var="polyUrl" value="${configBean.WI_URL}searches/polymorphism_report.cgi?_Marker_key=${marker.markerKey}"/>
		  <c:set var="polyExtra" value=""/>
		  <c:if test="${(item.countType == 'PCR') or (item.countType == 'RFLP')}">
		    <c:set var="polyUrl" value="${polyUrl}&search=${item.countType}"/>
		  </c:if>
		  <c:set var="isSnp" value=""/>
		  <c:set var="pad" value=""/>
		  <c:if test="${fn:startsWith(item.countType, 'SNP')}">
		    <c:choose>
  				<c:when test="${configBean.BUILDS_IN_SYNC == '0'}">
  					<c:set var="countText" value="SNPs"/>
  				</c:when>
  				<c:otherwise>
  					<c:set var="countText" value="${item.countType}"/>
  				</c:otherwise>
  			</c:choose>

		    <c:set var="isSnp" value="1"/>
		    <c:set var="polyUrl" value="${configBean.WI_URL}searches/snp_report.cgi?_Marker_key=${marker.markerKey}"/>
		    <c:if test="${not empty configBean.SNP_BUILD}">
		      <c:set var="polyExtra" value=" from ${configBean.SNP_BUILD}"/>
		    </c:if>
		    <c:if test="${fn:contains(item.countType, 'multiple')}">
		      <c:set var="polyUrl" value="${polyUrl}&includeMultiples=1"/>
		      <c:set var="polyExtra" value=""/>
		      <c:set var="pad" value="&nbsp;&nbsp;&nbsp;"/>
		    </c:if>
		  </c:if>

		  ${pad}${countText}(<a href="${polyUrl}">${item.count}</a>${polyExtra})
		  <c:if test="${status.first and (empty isSnp)}">: </c:if>
		</c:forEach>
      </div>
    </div>
  </c:if>

  <!-- ROW14 -->
  <c:set var="proteinAnnotations" value="${marker.proteinAnnotations}"/>
  <c:if test="${not empty proteinAnnotations}">
    <div class="row" >
      <div class="header <%=leftTdStyles.getNext() %>">
        Protein-related<br/>information
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>">
        <table>
        <tr><td>Resource</td><td>ID</td><td>Description</td></tr>
        <c:forEach var="item" items="${proteinAnnotations}">
          <c:set var="url" value=""/>
          <c:if test="${item.vocabName == 'InterPro Domains'}">
            <c:set var="url" value="${urls.InterPro}"/>
          </c:if>
          <c:if test="${item.vocabName == 'Protein Ontology'}">
            <c:set var="url" value="${urls.Protein_Ontology}"/>
          </c:if>
          <tr><td>${fn:replace (item.vocabName, " Domains", "")}</td>
            <c:if test="${url != ''}">
	          <td><a href="${fn:replace(url, '@@@@', item.termID)}">${item.termID}</a></td>
            </c:if>
            <c:if test="${url == ''}">
	          <td>${item.termID}</td>
            </c:if>
          <td>${item.term}</td></tr>
        </c:forEach>
        </table>
        <c:if test="${not empty marker.representativePolypeptideSequence}">
		  <c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
		  <c:if test="${seq.provider == 'SWISS-PROT'}">
            <c:set var="url" value="${urls.InterPro_ISpy}"/>
		    <a href="${fn:replace(url, '@@@@', seq.primaryID)}" target="_new">Graphical View of Protein Domain Structure</a>
		  </c:if>
        </c:if>
      </div>
    </div>
  </c:if>

  <!-- References -->
  <c:if test="${not empty marker.references}">
    <div class="row">
      <div class="header <%=leftTdStyles.getNext() %>" >
        References
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>" >
        <c:set var="earliestRef" value="${marker.earliestReference}"/>
        <c:set var="latestRef" value="${marker.latestReference}"/>
	    <c:if test="${not empty earliestRef}">(Earliest) <a href="${configBean.FEWI_URL}reference/${earliestRef.jnumID}">${earliestRef.jnumID}</a>
		  ${earliestRef.shortCitation}<br/>
		</c:if>
		<c:if test="${not empty latestRef}">(Latest) <a href="${configBean.FEWI_URL}reference/${latestRef.jnumID}">${latestRef.jnumID}</a>
		  ${latestRef.shortCitation}<br/>
		</c:if>
		<c:if test="${marker.countOfReferences > 0}">
		  All references(<a href="${configBean.FEWI_URL}reference/marker/${marker.primaryID}">${marker.countOfReferences}</a>)<br/>
		</c:if>
		<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
			Disease annotation references (<a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}">${diseaseRefCount}</a>)
		</c:if>
      </div>
    </div>
  </c:if>

  <!-- ROW16 -->
  <c:set var="otherMgiIDs" value="${marker.otherMgiIDs}"/>
  <c:if test="${not empty otherMgiIDs}">
    <div class="row">
      <div class="header <%=leftTdStyles.getNext() %>" >
        Other<br/>accession&nbsp;IDs
      </div>
      <div class="detail <%=rightTdStyles.getNext() %>" >
		<c:forEach var="item" items="${otherMgiIDs}" varStatus="status">
		  ${item.accID}<c:if test="${not status.last}">, </c:if>
		</c:forEach>
      </div>
    </div>
  </c:if>

<!-- close structural table -->
</div>

<!-- Elements not part of page structure that are hidden by default -->
<div class="hiddenItems">
	<!-- Cluster Relationship items -->
	<c:if test="${hasClusterMembers}">
		 <form style='display:none;' id="batchWebForm" name='batchWeb' enctype='multipart/form-data'
		      target='_blank' method='post'
		      action='${configBean.FEWI_URL}batch/summary'>
		      <input name='idType' value='current symbol' type='hidden'>
		      <input name='attributes' value='Nomenclature' type='hidden'>
		      <input name='attributes' value='Location' type='hidden'>
		      <input name='ids' value='${memberSymbols}' id='batchSymbolListWeb' type='hidden'>
	      </form>
	
	      <div id="clusterMemberDiv" class="" style="visibility:hidden;">
			 <div class="hd"> ${marker.symbol} Cluster contains:
			   <div style="float:right; margin-right:25px"><span id="clusterBatchLink" style="cursor: pointer; text-decoration: underline; color: #0000ff">More Data</span> for these features</div>
		     </div>
			 <div class="bd" style="overflow:auto">
			    <table id="clusterMemberTable">
				<tr><td class="bold leftAlign allBorders">Member</td><td class="bold leftAlign allBorders">Feature Type</td><td class="bold leftAlign allBorders">Location</td></tr>
				<c:forEach var="member" items="${marker.clusterMembers}">
				   <tr><td class="leftAlign allBorders"><a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a>, ${member.relatedMarkerName}</td>
				   <td class="leftAlign allBorders">${member.relatedMarkerFeatureType}</td>
				   <td class="leftAlign allBorders">${member.relatedMarkerLocation}</td>
				   </tr>
				</c:forEach>
			    </table>
			 </div>
	      </div>
	</c:if>
</div>
<!--  close page template -->
${templateBean.templateBodyStopHtml}