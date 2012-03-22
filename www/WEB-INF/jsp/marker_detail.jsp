<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.IDLinker" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% IDLinker idLinker = (IDLinker)request.getAttribute("idLinker"); %>
    
${templateBean.templateHeadHtml}

<title>${marker.symbol} MGI Mouse ${marker.markerType} Detail - ${marker.primaryID} - ${marker.name}</title>

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
</style>

<script language="Javascript">
function formatForwardArgs() {
    document.sequenceForm.action = document.sequenceFormPullDown.seqPullDown.options[document.sequenceFormPullDown.seqPullDown.selectedIndex].value;
    document.sequenceForm.submit();
}
</script>

${templateBean.templateBodyStartHtml}

<style type="text/css">
td.padded { padding:4px; } 
</style>


<!-- header bar -->
<div id="titleBarWrapper" userdoc="GENE_detail_help.shtml">	
	<div class="yourInputButton">
		<form name="YourInputForm">
			<input class="searchToolButton" value="Your Input Welcome" name="yourInputButton" onclick='window.open("${configBean.MGIHOME_URL}feedback/feedback_form.cgi?accID=${marker.primaryID}&amp;dataDate=<fmt:formatDate type='date' value='${databaseDate}' dateStyle='short'/>")' onmouseover="return overlib('We welcome your corrections and new data. Click here to contact us.', LEFT, WIDTH, 200, TIMEOUT, 2000);" onmouseout="nd();" type="button">
		</form>
	</div>
    <div name="centeredTitle">
	  <span class="titleBarMainTitle">${marker.symbol}</span><br/>
	  ${marker.markerType} Detail
	</div>
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 -->
  <tr>
    <td class="<%=leftTdStyles.getNext() %>">
      <font size="+2">&nbsp;</font>Symbol<br/>
      Name<br/>
      ID
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <table style='width: 100%'>
        <tr style='width: 100%'><td style="text-align: left; vertical-align: top;">
          <font size="+2"><B>${marker.symbol}</B><c:if test="${marker.status == 'interim'}"> (Interim)</c:if>
          </font><br/>
          <B>${marker.name}</B><br/>
          ${marker.primaryID}
      </td><td style="text-align: right; vertical-align: middle;">
        <c:if test="${not empty biotypeConflictTable}">
          <a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'UL', 'UR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img src="${configBean.WEBSHARE_URL}images/warning2.gif" height="26" width="26" id="warning" border="0"></a>
          <a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'UL', 'UR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>BioType Conflict</a>
        </c:if>
        <c:if test="${not empty strainSpecificNote}">
          <a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'UL', 'UR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img src="${configBean.WEBSHARE_URL}images/mice.jpg" height="38" width="38" id="mice" border="0"></a>
          <a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'UL', 'UR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>Strain-Specific Marker</a>
        </c:if>
      </td></tr>
      </table>
    </td>
  </tr>

  <!-- ROW1a -->
  <c:if test="${not empty marker.aliases}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        <c:if test="${marker.markerType == 'Gene'}">STS</c:if>
        <c:if test="${marker.markerType != 'Gene'}">STS for</c:if>
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <c:forEach var="alias" items="${marker.aliases}" varStatus="status">
          <a href="${configBean.FEWI_URL}marker/${alias.aliasID}">${alias.aliasSymbol}</a><c:if test="${!status.last}">, </c:if>
        </c:forEach>
      </td>
    </tr>
  </c:if>

  <!-- ROW2 -->
  <c:if test="${not empty marker.synonyms}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Synonyms
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <c:forEach var="synonym" items="${marker.synonyms}" varStatus="status">
          ${synonym.synonym}<c:if test="${!status.last}">, </c:if>
        </c:forEach>
      </td>
    </tr>
  </c:if>

  <!-- ROW3 -->
  <c:if test="${not empty marker.markerSubtype}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Feature&nbsp;Type
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        ${marker.markerSubtype}
      </td>
    </tr>
  </c:if>

  <!-- ROW4 -->
  <c:set var="hasGeneticLocation" value="0"/>
  <c:if test="${(not empty marker.preferredCentimorgans) 
  		or (not empty marker.preferredCytoband) 
  		or (marker.countOfMappingExperiments > 0)}">
    <tr>
      <td class="<%=leftTdStyles.getNext() %>">
        Genetic&nbsp;Map
      </td>
      <td class="<%=rightTdStyles.getNext() %>">

        <div>
        <c:if test="${not empty marker.preferredCentimorgans}">
          <c:if test="${marker.preferredCentimorgans.chromosome != 'UN'}">
            Chromosome ${marker.preferredCentimorgans.chromosome}<br/>
            <c:if test="${marker.preferredCentimorgans.cmOffset > 0.0}">              
              <c:set var="hasGeneticLocation" value="1"/>
              <c:set var="linkmapUrl" value="${configBean.WI_URL}searches/linkmap.cgi?chromosome=${marker.preferredCentimorgans.chromosome}&midpoint=${marker.preferredCentimorgans.cmOffset}&cmrange=1.0&dsegments=1&syntenics=0"/>
            
              <c:if test="${not empty miniMap}">
        	    <div style="float:right;text-align:left;"><a href="${linkmapUrl}"><img src="${miniMap}" border="0"></a></div>
              </c:if>
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
            Chromosome Unknown<br/>
          </c:if>
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
        <c:if test="${marker.countOfMappingExperiments > 0}">
          <br/></>Mapping data(<a href="${configBean.WI_URL}searches/mapdata_report_by_marker.cgi?${marker.markerKey}">${marker.countOfMappingExperiments}</a>)
        </c:if>
        </div>
      </td>
    </tr>
  </c:if>

  <!-- ROW5 -->
  <c:if test="${not (empty marker.preferredCoordinates and empty vegaGenomeBrowserUrl and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl)}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Sequence&nbsp;Map
      </td>
      <fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
      <fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
	  <c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
	  <c:set var="coord1" value="${fn:replace('Chr<chr>:<start>-<end>', '<chr>', chromosome)}"/>
	  <c:set var="coord2" value="${fn:replace(coord1, '<start>', startCoord)}"/>
	  <c:set var="coords" value="${fn:replace(coord2, '<end>', endCoord)}"/>

      <td class="<%=rightTdStyles.getNext() %>">
        <table width="100%">
        <tr><td>
        <c:if test="${not empty marker.preferredCoordinates}"> 
        Chr${chromosome}:${startCoord}-${endCoord}
          ${marker.preferredCoordinates.mapUnits}, ${marker.preferredCoordinates.strand} strand<br/>
        <c:if test="${empty marker.preferredCoordinates}"> 
          ${marker.preferredCoordinates.mapUnits}<br/>
        </c:if>
        </c:if>
        <c:if test="${not empty marker.qtlNote}">
          ${marker.qtlNote}<br/>
        </c:if>
        <c:if test="${not empty marker.preferredCoordinates}"> 
        (From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier})<br/>
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
		  <c:if test="${not empty gbrowseUrl}">
		    <table><tr><td align="center">
		    <c:if test="${not empty gbrowseThumbnailUrl}">
		    <a href="${gbrowseUrl}"><img border="0" src="${gbrowseThumbnailUrl}"/></a>
		    <br/>
		    </c:if>
		    <a href="${gbrowseUrl}">Mouse Genome Browser</a>
		  </td></tr></table>
		  </c:if>
		</td></tr>
		</table>
      </td>
    </tr>
  </c:if>

  <!-- Mammalian homology ribbon -->
  <c:if test="${(not empty marker.orthologousMarkers) or (not empty marker.ensemblGeneModelID)}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Mammalian<br/>homology
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <c:set var="hasHumanOrthology" value="0"/>
		<c:forEach var="orthology" items="${marker.orthologousMarkers}" varStatus="status">
		  ${orthology.otherOrganism}<c:if test="${!status.last}">; </c:if>
		  <c:if test="${orthology.otherOrganism == 'human'}">
		    <c:set var="hasHumanOrthology" value="1"/>
		  </c:if>
		</c:forEach>
		<c:if test="${not empty marker.orthologousMarkers}">
		&nbsp;&nbsp;&nbsp;(<a href="${configBean.WI_URL}searches/homology_report.cgi?_Marker_key=${marker.markerKey}">Mammalian Orthology</a>)<br/>
		</c:if>
		<c:if test="${(hasHumanOrthology == 1) and (hasGeneticLocation == 1)}">
          <c:set var="comparativemapUrl" value="${configBean.WI_URL}searches/linkmap.cgi?chromosome=${marker.preferredCentimorgans.chromosome}&midpoint=${marker.preferredCentimorgans.cmOffset}&cmrange=2&dsegments=0&syntenics=0&species=2&format=Web+Map&source=MGD"/>
		  Comparative Map (<a href="${comparativemapUrl}">Mouse/Human ${marker.symbol} &#177; 2 cM</a>)<P>
		</c:if>
		
		<c:set var="pirsf" value="${marker.pirsfAnnotation}"/>
		<c:if test="${not empty pirsf}">
		  Protein SuperFamily: <a href="${configBean.JAVAWI_URL}WIFetch?page=pirsfDetail&id=${pirsf.termID}">${pirsf.term}</a><br/>
		</c:if>
		<c:if test="${not empty marker.ensemblGeneModelID}">
			<c:set var="genetreeUrl" value="${configBean.GENETREE_URL}"/>			
			<c:set var="genetreeUrl" value="${fn:replace(genetreeUrl, '<model_id>', marker.ensemblGeneModelID.accID)}"/>
			Gene Tree: <a href="${configBean.GENETREE_URL}${marker.ensemblGeneModelID.accID}">${marker.symbol}</a><br/>
		</c:if>
      </td>
    </tr>
  </c:if>
  
  <!-- Human Ortholog ribbon -->
  <c:if test="${not empty humanOrtholog}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        <img src="${configBean.WEBSHARE_URL}images/new.gif"/> Human<br/>ortholog
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<a href="${fn:replace(externalUrls.NCBI_Gene_Model, '@@@@', humanOrtholog.entrezGeneID.accID)}">${humanOrtholog.symbol}</a> ${humanOrtholog.name}
			<span class="small">NCBI Gene ID ${humanOrtholog.entrezGeneID.accID}</span><br/>
		<c:if test="${not empty humanSynonyms}">
			Human Synonyms: ${fn:join(humanSynonyms, ", ")}<br/>
		</c:if>
		<c:if test="${not empty humanLocation}">
				<c:set var="humanLoc" value="Human Chr<chr><loc> <span class='small'>Reference GRCh37.p2 Primary Assembly</span><br/>"/>
				<c:set var="loc" value=":<start>-<end> bp, <strand> strand "/>
				<c:set var="humanLoc" value="${fn:replace(humanLoc, '<chr>', humanLocation.chromosome)}"/>
				<c:if test="${not empty humanLocation.startCoordinate}">
					<fmt:formatNumber value="${humanLocation.startCoordinate}" pattern="#0" var="humanStart"/>
	      			<fmt:formatNumber value="${humanLocation.endCoordinate}" pattern="#0" var="humanEnd"/>
					<c:set var="loc" value="${fn:replace(loc, '<start>', humanStart)}"/>
					<c:set var="loc" value="${fn:replace(loc, '<end>', humanEnd)}"/>
					<c:set var="loc" value="${fn:replace(loc, '<strand>', humanLocation.strand)}"/>
					<c:set var="humanLoc" value="${fn:replace(humanLoc, '<loc>', loc)}"/>
				</c:if>			
			 ${humanLoc}			 
		</c:if>
		<c:if test="${humanOrtholog.countOfHumanDiseases > 0}">
		Human Diseases Associated with Human ${humanOrtholog.symbol} 
		(<a href="" onclick="return overlib( '<table name=\'results\' border=\'0\' cellpadding=\'3\' cellspacing=\'0\' width=\'100%\'><tr ><th align=\'left\'>Human Disease</th><th width=\'4\'></th>' +
			'<th width=\'65\'>OMIM ID</th></tr>' +
			<c:set var="hMessage" value="&nbsp;" />
			<c:forEach var="annotation" items="${humanOrtholog.OMIMHumanAnnotations}" varStatus="status">
				<c:set var="rColor" value="" />
				<c:if test="${status.count % 2 == 0}">
					<c:set var="rColor" value="style=\\'background-color:#F8F8F8;\\'" />
				</c:if>
				'<tr ${rColor} align=\'left\' valign=\'top\'>' +
				'<td><a href=\'${configBean.JAVAWI_URL}WIFetch?page=humanDisease&amp;id=${annotation.termID}\'>${annotation.term}</a></td>' +
				'<td width=\'4\'>'  +
				<c:forEach var="star" items="${marker.OMIMAnnotations}">
					<c:if test="${annotation.termID eq star.termID}">
						<c:set var="hMessage" value="* Disease is associated with mutations in mouse ${marker.symbol}." />
						'*' +
					</c:if>
				</c:forEach>
				'</td>' +
				'<td><a href=\'${fn:replace(externalUrls.OMIM, '@@@@', annotation.termID)}\' target=\'_blank\'>${annotation.termID}</a></td></tr>' +
			</c:forEach>
			'<tr align=\'left\' valign=\'top\'><td  colspan=\'3\'>${hMessage}</td></tr></table>', STICKY, CAPTION, 'Human Disease Models Associated with Alleles of Human ${humanOrtholog.symbol}', RIGHT, BELOW, WIDTH, 500, DELAY, 250, CLOSECLICK, CLOSETEXT, 'Close X');" onmouseout="nd();">${humanOrtholog.countOfHumanDiseases}</a>)
  		</c:if>
  </c:if>

  <!-- Allele ribbon -->
  <c:if test="${(marker.countOfAlleles > 0) or (not empty marker.markerClip) or (marker.countOfHumanDiseases > 0) or (marker.countOfAllelesWithHumanDiseases > 0) or (marker.countOfPhenotypeImages > 0)}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Alleles<br/>and<br/>phenotypes
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <c:if test="${marker.countOfAlleles > 0}">
		  <c:set var="alleleUrl" value="${configBean.WI_URL}searches/allele_report.cgi?_Marker_key=${marker.markerKey}"/>
		  All alleles(<a href="${alleleUrl}">${marker.countOfAlleles}</a>) : 
		  <c:forEach var="item" items="${marker.alleleCountsByType}">
		    ${item.countType}(<a href="${alleleUrl}&alleleSet=${item.countType}">${item.count}</a>)
		  </c:forEach>
		  <br/>
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
				'<td><a href=\'${configBean.JAVAWI_URL}WIFetch?page=humanDisease&amp;id=${annotation.termID}\'>${annotation.term}</a></td>' +
				'<td width=\'4\'>' +
				<c:forEach var="star" items="${humanOrtholog.OMIMHumanAnnotations}">
					<c:if test="${annotation.termID eq star.termID}">
						<c:set var="mMessage" value="* Disease is associated with mutations in human ${humanOrtholog.symbol}." />
						'*' +
					</c:if>
				</c:forEach>
				'</td>' +
				'<td><a href=\'${fn:replace(externalUrls.OMIM, '@@@@', annotation.termID)}\' target=\'_blank\'>${annotation.termID}</a></td></tr>' +
			</c:forEach>
			'<tr align=\'left\' valign=\'top\'><td  colspan=\'3\'>${mMessage}</td></tr></table>', STICKY, CAPTION, 'Human Disease Models Associated with Alleles of Mouse ${marker.symbol}', RIGHT, BELOW, WIDTH, 500, DELAY, 250, CLOSECLICK, CLOSETEXT, 'Close X');" onmouseout="nd();">${marker.countOfHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
		  Alleles Annotated to Human Diseases(<a href="${configBean.WI_URL}searches/allele_report.cgi?_Marker_key=${marker.markerKey}&omimOnly=1">${marker.countOfAllelesWithHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfPhenotypeImages > 0}">
		  Phenotype Images(<a href="${configBean.FEWI_URL}image/phenoSummary/marker/${marker.primaryID}">${marker.countOfPhenotypeImages}</a>)
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- go classifications -->
  <c:if test="${marker.countOfGOTerms > 0}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Gene&nbsp;Ontology<br/>(GO)<br/>classifications
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
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
		<c:set var="funcbaseID" value="${marker.funcBaseID}"/>
		<c:if test="${not empty funcbaseID}">
		  External Resources: 
		  <a href="${fn:replace(externalUrls.FuncBase, '@@@@', funcbaseID.accID)}">FuncBase</a><br/>
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW11 -->
  <c:set var="allenID" value="${marker.allenBrainAtlasID.accID}"/>
  <c:set var="gensatID" value="${marker.gensatID.accID}"/>
  <c:set var="geoID" value="${marker.geoID.accID}"/>
  <c:set var="arrayExpressID" value="${marker.arrayExpressID.accID}"/>

  <c:if test="${not (empty marker.gxdAssayCountsByType and (marker.countOfGxdLiterature < 1) and (marker.countOfCdnaSources < 1) and empty allenID and empty gensatID and empty geoID and empty arrayExpressID)}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Expression
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<c:if test="${marker.countOfGxdLiterature > 0}">
		  Literature Summary: (<a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a> records)<br/>
		</c:if>
		<c:if test="${not empty gxdAssayTypes}">
		Data Summary:
		<c:if test="${marker.countOfGxdAssays > 0}">
		  Assays (<a href="${configBean.WI_URL}searches/expression_report.cgi?_Marker_key=${marker.markerKey}&returnType=assays&sort=Assay%20type">${marker.countOfGxdAssays}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdResults > 0}">
		  Results (<a href="${configBean.WI_URL}searches/expression_report.cgi?_Marker_key=${marker.markerKey}&returnType=assay%20results&sort=Anatomical%20structure">${marker.countOfGxdResults}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdTissues > 0}">
		  Tissues (<a href="${configBean.FEWI_URL}tissue/marker/${marker.primaryID}">${marker.countOfGxdTissues}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdImages > 0}">
		  Images (<a href="${configBean.FEWI_URL}image/gxdSummary/marker/${marker.primaryID}">${marker.countOfGxdImages}</a>)
		</c:if>
		<br/>
		<c:if test="${not empty marker.gxdResultCountsByStage}">
		  Theiler Stages: 
		  <c:forEach var="item" items="${marker.gxdResultCountsByStage}" varStatus="status"><a href="${configBean.WI_URL}searches/expression_report.cgi?_Marker_key=${marker.markerKey}&returnType=assay%20results&sort=Anatomical%20structure&_Stage_key=${item.countType}">${item.countType}</a><c:if test="${!status.last}">,</c:if></c:forEach>
		  <br/>
		</c:if>
		</c:if>

		<c:if test="${not empty gxdAssayTypes}">
 	      <c:set var="gxdAssayUrl" value="${configBean.WI_URL}searches/expression_report.cgi?_Marker_key=${marker.markerKey}&returnType=assays&sort=Assay%20type&assayType="/>
		  <c:set var="gxdResultUrl" value="${configBean.WI_URL}searches/expression_report.cgi?_Marker_key=${marker.markerKey}&returnType=assay%20results&sort=Anatomical%20structure&assayType="/>
		  <table>
		    <tr><td style="padding-left:2em;">Assay Type</td><td>Assays&nbsp;&nbsp;</td><td>Results</td></tr>
		    <c:forEach var="assayType" items="${gxdAssayTypes}">
		      <tr><td style="padding-left:4em;padding-right:1em;">${assayType}</td>
		        <td><a href="${gxdAssayUrl}${assayType}">${gxdAssayCounts[assayType]}</a></td>
		        <td><a href="${gxdResultUrl}${assayType}">${gxdResultCounts[assayType]}</a></td>
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
		    <a href="${fn:replace (externalUrls.ArrayExpress, '@@@@', arrayExpressID)}" target="_new">ArrayExpress</a>
		  </c:if><br/>
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW12 -->
  <c:if test="${(not empty marker.molecularReagentCountsByType) || (marker.countOfMicroarrayProbesets > 0)}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Molecular<br/>reagents
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
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
		  Microarray probesets(<a href="${configBean.JAVAWI_URL}WIFetch?page=AffySummary&key=${marker.markerKey}">${marker.countOfMicroarrayProbesets}</a>)
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW13 -->
  <c:if test="${not empty logicalDBs}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Other&nbsp;database<br/>links
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<table>
		  <c:forEach var="item" items="${logicalDBs}">
			<tr><td>${item}</td><td>${otherIDs[item]}</td></tr>
		  </c:forEach>
		</table>
      </td>
    </tr>
  </c:if>

  <!-- Sequences ribbon -->
  <c:if test="${not empty marker.sequenceIDs}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Sequences
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
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
		      <td class="padded">&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;Kb</td></tr>
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
		  <option value="${configBean.SEQFETCH_URL}tofasta.cgi?" selected> download in FASTA format</option>
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
      </td>
    </tr>
  </c:if>

  <!-- Polymorphisms ribbon -->
  <c:if test="${not empty marker.polymorphismCountsByType}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Polymorphisms
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
		  <c:set var="polyUrl" value="${configBean.WI_URL}searches/polymorphism_report.cgi?_Marker_key=${marker.markerKey}"/>
		  <c:set var="polyExtra" value=""/>
		  <c:if test="${(item.countType == 'PCR') or (item.countType == 'RFLP')}">
		    <c:set var="polyUrl" value="${polyUrl}&search=${item.countType}"/>
		  </c:if>
		  <c:set var="isSnp" value=""/>
		  <c:set var="pad" value=""/>
		  <c:if test="${fn:startsWith(item.countType, 'SNP')}">
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
		  ${pad}${item.countType}(<a href="${polyUrl}">${item.count}</a>${polyExtra})
		  <c:if test="${status.first and (empty isSnp)}">: </c:if>
		</c:forEach>
      </td>
    </tr>
  </c:if>
  
  <!-- ROW14 -->
  <c:set var="proteinAnnotations" value="${marker.proteinAnnotations}"/>
  <c:if test="${not empty proteinAnnotations}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Protein-related<br/>information
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <table>
        <tr><td>Resource</td><td>ID</td><td>Description</td></tr>
        <c:forEach var="item" items="${proteinAnnotations}">
          <c:set var="url" value=""/>
          <c:if test="${item.vocabName == 'InterPro Domains'}">
            <c:set var="url" value="${externalUrls.InterPro}"/>
          </c:if>
          <c:if test="${item.vocabName == 'Protein Ontology'}">
            <c:set var="url" value="${externalUrls.Protein_Ontology}"/>
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
            <c:set var="url" value="${externalUrls.InterPro_ISpy}"/>
		    <a href="${fn:replace(url, '@@@@', seq.primaryID)}" target="_new">Graphical View of Protein Domain Structure</a>
		  </c:if>
        </c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW15 -->

  <c:if test="${not empty marker.references}">
    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        References
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
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
      </td>
    </tr>
  </c:if>

  <!-- ROW16 -->

  <c:set var="otherMgiIDs" value="${marker.otherMgiIDs}"/>
  <c:if test="${not empty otherMgiIDs}">
    <tr  valign=top ALIGN=left>
      <td class="<%=leftTdStyles.getNext() %>" >
        Other<br/>accession&nbsp;IDs
      </td>
      <td class="<%=rightTdStyles.getNext() %>" >
		<c:forEach var="item" items="${otherMgiIDs}" varStatus="status">
		  ${item.accID}<c:if test="${not status.last}">, </c:if> 
		</c:forEach>
      </td>
    </tr>
  </c:if>
 
<!-- close structural table and page template-->
</table>
${templateBean.templateBodyStopHtml}
