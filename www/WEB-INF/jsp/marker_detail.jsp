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

<style type="text/css">
</style>

<script language="Javascript">
function formatForwardArgs() {
    document.sequenceForm.action = document.sequenceFormPullDown.seqPullDown.options[document.sequenceFormPullDown.seqPullDown.selectedIndex].value;
    document.sequenceForm.submit();
}
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="marker_detail.shtml">	
	<span class="titleBarMainTitle">${marker.symbol}</span><br/>
	 ${marker.markerType} Detail
</div>


<!-- structural table -->
<table class="detailStructureTable">


  <!-- ROW1 -->
  <tr >
    <td class="<%=leftTdStyles.getNext() %>">
      <font size="+2">&nbsp;</font>Symbol<br/>
      Name<br/>
      ID
    </td>
    <td class="<%=rightTdStyles.getNext() %>">
      <font size="+2">${marker.symbol}</font><br/>
      ${marker.name}<br/>
      ${marker.primaryID}
    </td>
  </tr>


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
  <c:if test="${not empty marker.preferredCentimorgans}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Genetic&nbsp;Map
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        Chromosome ${marker.preferredCentimorgans.chromosome}<br/>
        <fmt:formatNumber value="${marker.preferredCentimorgans.cmOffset}" minFractionDigits="2" maxFractionDigits="2"/> ${marker.preferredCentimorgans.mapUnits}<br/>
        <c:if test="${(marker.preferredCentimorgans.chromosome != 'UN') and (marker.preferredCentimorgans.cmOffset > 0.0)}">
          <c:set var="hasGeneticLocation" value="1"/>
          <a href="#">Detailed Genetic Map &#177; 1 cM</a>
        </c:if>
        <p/>
        Mapping data(<a href="#">${marker.countOfMappingExperiments}</a>)
      </td>
    </tr>
  </c:if>

  <!-- ROW5 -->
  <c:if test="${not empty marker.preferredCoordinates}">
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
        Chr${chromosome}:${startCoord}-${endCoord} 
        ${marker.preferredCoordinates.mapUnits}, ${marker.preferredCoordinates.strand} strand<br/>
        (From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier})<br/>
        <p/>
        <c:set var="vegaID" value="${marker.vegaGeneModelID.accID}"/>
        <c:set var="ensemblID" value="${marker.ensemblGeneModelID.accID}"/>
        <c:set var="ncbiID" value="${marker.ncbiGeneModelID.accID}"/>
        <c:set var="foundOne" value="0"/>
		<c:if test="${not empty vegaID}">
		  <a href="${fn:replace(externalUrls.VEGA_Genome_Browser, '@@@@', vegaID)}" target="_new">VEGA Genome Browser</a>
		  <c:set var="foundOne" value="1"/>
		</c:if>
		<c:if test="${not empty ensemblID}">
		  <c:if test="${foundOne > 0}"> | </c:if>
		  <a href="${fn:replace(externalUrls.Ensembl_Genome_Browser, '@@@@', ensemblID)}" target="_new">Ensembl Genome Browser</a>
		  <c:set var="foundOne" value="1"/>
		</c:if>
		<c:if test="${not empty coords}">
		  <c:if test="${foundOne > 0}"> | </c:if>
		  <a href="${fn:replace(externalUrls.UCSC_Genome_Browser, '@@@@', coords)}" target="_new">UCSC Browser</a>
		  <c:set var="foundOne" value="1"/>
		</c:if>
		<c:if test="${not empty ncbiID}">
		  <c:if test="${foundOne > 0}"> | </c:if>
		  <a href="${fn:replace(externalUrls.NCBI_Map_Viewer, '@@@@', ncbiID)}" target="_new">NCBI Map Viewer</a>
		</c:if>
		</td><td align="right" width="*">
		  <table><tr><td align="center">
		  <c:set var="gbrowseLink" value="${configBean.GBROWSE_URL}gbrowse/mouse_current?start=${startCoord};stop=${endCoord};ref=${chromosome}"/>
		  <a href="${gbrowseLink}"><img border="0" src="${configBean.GBROWSE_URL}gbrowse_img/thumbs_current?abs=1;options=Everything;width=200;name=${chromosome}:${startCoord}..${endCoord}"/></a>
		  <br/>
		  <a href="${gbrowseLink}">Mouse Genome Browser</a>
		  </td></tr></table>
		</td></tr>
		</table>
      </td>
    </tr>
  </c:if>

  <!-- ROW6 -->
  <c:if test="${not empty marker.orthologousMarkers}">
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
		&nbsp;&nbsp;&nbsp;(<a href="#">Mammalian Orthology</a>)<br/>
		
		<c:if test="${(hasHumanOrthology == 1) and (hasGeneticLocation == 1)}">
		  Comparative Map (<a href="#">Mouse/Human ${marker.symbol} &#177; 2 cM</a>)<P>
		</c:if>
		
		<c:set var="pirsf" value="${marker.pirsfAnnotation}"/>
		<c:if test="${not empty pirsf}">
		  Protein SuperFamily: <a href="${fn:replace(externalUrls.PIRSF, '@@@@', pirsf.termID)}">${pirsf.term}</a><br/>
		</c:if>

		<c:set var="treeFamDisplayID" value="${marker.treeFamDisplayID.accID}"/>
		<c:set var="treeFamLinkID" value="${marker.treeFamLinkID.accID}"/>
		<c:if test="${not (empty treeFamDisplayID or empty treeFamLinkID)}">
		  TreeFam: <a href="${fn:replace(externalUrls.TreeFam, '@@@@', treeFamLinkID)}">${treeFamDisplayID}</a><br/>
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW7 -->
  <c:if test="${not empty marker.sequenceIDs}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Sequences
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<form name="sequenceForm" method="GET">
		<table>
		  <tr><td colspan="4">Representative Sequences</td><td>Length</td><td>Strain/Species</td><td>Flank</td></tr>
		  <c:if test="${not empty marker.representativeGenomicSequence}">
			<c:set var="seq" value="${marker.representativeGenomicSequence}" scope="request"/>
			<% Sequence seqDna = (Sequence) request.getAttribute("seq"); %>
		    <tr><td><input type="checkbox" name="seq1" value="<%= FormatHelper.getSeqForwardValue(seqDna) %>"></td><td>genomic</td>
		      <td>${marker.representativeGenomicSequence.primaryID}</td>
		      <td>${fn:replace(genomicLink, "VEGA", "VEGA Gene Model")} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeGenomicSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td>${marker.representativeGenomicSequence.length}</td>
		      <td>${genomicSource}</td>
		      <td>&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;Kb</td></tr>
		  </c:if>
		  <c:if test="${not empty marker.representativeTranscriptSequence}">
			<c:set var="seq" value="${marker.representativeTranscriptSequence}" scope="request"/>
			<% Sequence seqRna = (Sequence) request.getAttribute("seq"); %>
		    <tr><td><input type="checkbox" name="seq2" value="<%= FormatHelper.getSeqForwardValue(seqRna) %>"></td><td>transcript</td>
		      <td>${marker.representativeTranscriptSequence.primaryID}</td>
		      <td>${transcriptLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativeTranscriptSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td>${marker.representativeTranscriptSequence.length}</td>
		      <td>${transcriptSource}</td><td>&nbsp;</td></tr>
		  </c:if>
		  <c:if test="${not empty marker.representativePolypeptideSequence}">
			<c:set var="seq" value="${marker.representativePolypeptideSequence}" scope="request"/>
			<% Sequence seqPoly = (Sequence) request.getAttribute("seq"); %>
		    <tr><td><input type="checkbox" name="seq3" value="<%= FormatHelper.getSeqForwardValue(seqPoly) %>"></td><td>polypeptide</td>
		      <td>${marker.representativePolypeptideSequence.primaryID}</td>
		      <td>${polypeptideLink} | <a href="${configBean.FEWI_URL}sequence/${marker.representativePolypeptideSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td>${marker.representativePolypeptideSequence.length}</td>
		      <td>${polypeptideSource}</td><td>&nbsp;</td></tr>
		  </c:if>
		</table>
		</form>
		<form name="sequenceFormPullDown">
		  <I>For the selected sequences</I>
		  <select name="seqPullDown">
		  <option value="${configBean.SEQFETCH_URL}tofasta.cgi?" selected> download in FASTA format</option>
		  <option value="${configBean.MOUSEBLAST_URL}seqSelect.cgi"> forward to MouseBLAST</option>
		  <input type="button" value="Go" onClick="formatForwardArgs()">
		  </select>
		</form>
		<c:if test="${marker.countOfSequences > 0}">
		  All sequences(<a href="${configBean.FEWI_URL}/sequence/marker/${marker.primaryID}">${marker.countOfSequences}</a>) 
		</c:if>
		<c:if test="${marker.countOfRefSeqSequences > 0}">
		  RefSeq(<a href="#">${marker.countOfRefSeqSequences}</a>)
		</c:if>
		<c:if test="${marker.countOfUniProtSequences > 0}">
		  UniProt(<a href="#">${marker.countOfUniProtSequences}</a>)
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW8 -->
  <c:if test="${not empty marker.alleleAssociations}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Alleles<br/>and<br/>phenotypes
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		All alleles(<a href="#">${marker.countOfAlleles}</a>) : 
		<c:forEach var="item" items="${marker.alleleCountsByType}">
		  ${item.countType}(<a href="#">${item.count}</a>)
		</c:forEach>
		<br/>
		<c:if test="${not empty marker.markerClip}">
		  &nbsp;<br/>
		  <blockquote>${marker.markerClip}</blockquote>
		  &nbsp;<br/>
		</c:if>
		<c:if test="${marker.countOfHumanDiseases > 0}">
		  Associated Human Diseases(<a href="${configBean.FEWI_URL}omim/marker/${marker.primaryID}">${marker.countOfHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfAllelesWithHumanDiseases > 0}">
		  Alleles Annotated to Human Diseases(<a href="#">${marker.countOfAllelesWithHumanDiseases}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfPhenotypeImages > 0}">
		  Phenotype Images(<a href="#">${marker.countOfPhenotypeImages}</a>)
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW9 -->
  <c:if test="${not empty marker.polymorphismCountsByType}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Polymorphisms
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
		  ${item.countType}(<a href="#">${item.count}</a>)
		  <c:if test="${status.first}">: </c:if>
		</c:forEach>
      </td>
    </tr>
  </c:if>

  <!-- ROW10 -->
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
	    		<td><a href="#">${processAnnot1.term}</a><c:if test="${not empty processAnnot2}">, </c:if>
	    			<a href="#">${processAnnot2.term}</a><c:if test="${not empty processAnnot3}">, ...</c:if>
	    		</td></tr>
	      </c:if>
	      <c:if test="${not empty componentAnnot1}">
	    	<tr><td>Component</td>
	    		<td><a href="#">${componentAnnot1.term}</a><c:if test="${not empty componentAnnot2}">, </c:if>
	    			<a href="#">${componentAnnot2.term}</a><c:if test="${not empty componentAnnot3}">, ...</c:if>
	    		</td></tr>
	      </c:if>
	      <c:if test="${not empty functionAnnot1}">
	    	<tr><td>Function</td>
	    		<td><a href="#">${functionAnnot1.term}</a><c:if test="${not empty functionAnnot2}">, </c:if>
	    			<a href="#">${functionAnnot2.term}</a><c:if test="${not empty functionAnnot3}">, ...</c:if>
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
  <c:if test="${not empty marker.gxdAssayCountsByType}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Expression
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<c:if test="${marker.countOfGxdLiterature > 0}">
		  Literature Summary: (<a href="#">${marker.countOfGxdLiterature}</a> records)<br/>
		</c:if>
		Data Summary:
		<c:if test="${marker.countOfGxdAssays > 0}">
		  Assays (<a href="#">${marker.countOfGxdAssays}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdResults > 0}">
		  Results (<a href="#">${marker.countOfGxdResults}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdTissues > 0}">
		  Tissues (<a href="#">${marker.countOfGxdTissues}</a>)&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdImages > 0}">
		  Images (<a href="#">${marker.countOfGxdImages}</a>)
		</c:if>
		<br/>
		<c:if test="${not empty marker.gxdResultCountsByStage}">
		  Theiler Stages: 
		  <c:forEach var="item" items="${marker.gxdResultCountsByStage}" varStatus="status"><a href="#">${item.countType}</a><c:if test="${!status.last}">,</c:if></c:forEach>
		  <br/>
		</c:if>

		<c:if test="${not empty gxdAssayTypes}">
		  <table>
		    <tr><td>Assay Type</td><td>Assays</td><td>Results</td></tr>
		    <c:forEach var="assayType" items="${gxdAssayTypes}">
		      <tr><td>${assayType}</td>
		        <td><a href="#">${gxdAssayCounts[assayType]}</a></td>
		        <td><a href="#">${gxdResultCounts[assayType]}</a></td>
		      </tr>
		    </c:forEach> 
		  </table>
		</c:if>
		
		<c:if test="${marker.countOfCdnaSources > 0}">cDNA source data(<a href="#">${marker.countOfCdnaSources}</a>)<br/></c:if>
		
		<c:set var="allenID" value="${marker.allenBrainAtlasID.accID}"/>
		<c:set var="gensatID" value="${marker.gensatID.accID}"/>
		<c:set var="geoID" value="${marker.geoID.accID}"/>
		<c:set var="arrayExpressID" value="${marker.arrayExpressID.accID}"/>
		<c:if test="${not (empty allenID and empty gensatID and empty geoID and empty arrayExpressID)}">
		  External Resources: 
		  <c:if test="${not empty allenID}">
		    <a href="${fn:replace (externalUrls.Allen_Brain_Atlas, '@@@@', allenID)}" target="_new">Allen Brain Atlas</a>&nbsp;&nbsp;
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
  <c:if test="${(not empty marker.molecularReagentCountsByType) || (marker.countOfMicroarrayProbesets > 0) || (marker.countOfAntibodies > 0)}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Molecular<br/>reagents
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<c:forEach var="item" items="${marker.molecularReagentCountsByType}">
		  ${item.countType}(<a href="#">${item.count}</a>) 
		</c:forEach>
		<c:if test="${marker.countOfAntibodies > 0}">
		  Antibodies(<a href="#">${marker.countOfAntibodies}</a>)
		</c:if>
		<br/>
		<c:if test="${marker.countOfMicroarrayProbesets > 0}">
		  Microarray probesets(<a href="#">${marker.countOfMicroarrayProbesets}</a>)
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
		  ${earliestRef.longCitation}<br/>
		</c:if>
		<c:if test="${not empty latestRef}">(Latest) <a href="${configBean.FEWI_URL}reference/${latestRef.jnumID}">${latestRef.jnumID}</a>
		  ${latestRef.longCitation}<br/>
		</c:if>
		All references(<a href="#">${marker.countOfReferences}</a>)<br/>
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
