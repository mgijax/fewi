<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
${templateBean.templateHeadHtml}

<title>${marker.symbol} Detail</title>

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

<script>
</script>

${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" userdoc="marker_help.shtml">	
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
          Detailed Genetic Map &#177; 1 cM
        </c:if>
        <p/>
        Mapping data(${marker.countOfMappingExperiments})
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
        Chr${marker.preferredCoordinates.chromosome}:${startCoord}-${endCoord} 
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
		&nbsp;&nbsp;&nbsp;(Mammalian Orthology)<br/>
		
		<c:if test="${(hasHumanOrthology == 1) and (hasGeneticLocation == 1)}">
		  Comparative Map (Mouse/Human ${marker.symbol} &#177; 2 cM)<P>
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
		    <tr><td><input type="checkbox" name="seq1"></td><td>genomic</td>
		      <td>${marker.representativeGenomicSequence.primaryID}</td>
		      <td><a href="${configBean.FEWI_URL}sequence/${marker.representativeGenomicSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td>${marker.representativeGenomicSequence.length}</td>
		      <td>source</td>
		      <td>&#177; <input type="text" size="3" name="flank" value="0">&nbsp;Kb</td></tr>
		  </c:if>
		  <c:if test="${not empty marker.representativeTranscriptSequence}">
		    <tr><td><input type="checkbox" name="seq2"></td><td>transcript</td>
		      <td>${marker.representativeTranscriptSequence.primaryID}</td>
		      <td><a href="${configBean.FEWI_URL}sequence/${marker.representativeTranscriptSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td>${marker.representativeTranscriptSequence.length}</td>
		      <td>source</td><td>&nbsp;</td></tr>
		  </c:if>
		  <c:if test="${not empty marker.representativePolypeptideSequence}">
		    <tr><td><input type="checkbox" name="seq3"></td><td>polypeptide</td>
		      <td>${marker.representativePolypeptideSequence.primaryID}</td>
		      <td><a href="${configBean.FEWI_URL}sequence/${marker.representativePolypeptideSequence.primaryID}">MGI Sequence Detail</a></td>
		      <td>${marker.representativePolypeptideSequence.length}</td>
		      <td>source</td><td>&nbsp;</td></tr>
		  </c:if>
		</table>
		</form>
		<form name="sequenceFormPullDown">
		  <I>For the selected sequences</I>
		  <select name="seqPullDown">
		  <option value="foo" selected> download in FASTA format</option>
		  <option value="foo2"> forward to MouseBLAST</option>
		  <input type="button" value="Go" onClick="alert('Not yet implemented')">
		  </select>
		</form>
		<c:if test="${marker.countOfSequences > 0}">
		  All sequences(<a href="${configBean.FEWI_URL}/sequence/marker/${marker.primaryID}">${marker.countOfSequences}</a>) 
		</c:if>
		<c:if test="${marker.countOfRefSeqSequences > 0}">
		  RefSeq(${marker.countOfRefSeqSequences})
		</c:if>
		<c:if test="${marker.countOfUniProtSequences > 0}">
		  UniProt(${marker.countOfUniProtSequences})
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
		All alleles(${marker.countOfAlleles}) : 
		<c:forEach var="item" items="${marker.alleleCountsByType}">
		  ${item.countType}(${item.count})
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
		  Alleles Annotated to Human Diseases(${marker.countOfAllelesWithHumanDiseases})&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfPhenotypeImages > 0}">
		  Phenotype Images(${marker.countOfPhenotypeImages})
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
		  ${item.countType}(${item.count})
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
		  Literature Summary: (${marker.countOfGxdLiterature} records)<br/>
		</c:if>
		Data Summary:
		<c:if test="${marker.countOfGxdAssays > 0}">
		  Assays (${marker.countOfGxdAssays})&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdResults > 0}">
		  Results (${marker.countOfGxdResults})&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdTissues > 0}">
		  Tissues (${marker.countOfGxdTissues})&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${marker.countOfGxdImages > 0}">
		  Images (${marker.countOfGxdImages})
		</c:if>
		<br/>
		<c:if test="${not empty marker.gxdResultCountsByStage}">
		  Theiler Stages: 
		  <c:forEach var="item" items="${marker.gxdResultCountsByStage}" varStatus="status">${item.countType}<c:if test="${!status.last}">,</c:if></c:forEach>
		  <br/>
		</c:if>
		
		<c:if test="${marker.countOfCdnaSources > 0}">cDNA source data(${marker.countOfCdnaSources})<br/></c:if>
		
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
		  ${item.countType}(${item.count}) 
		</c:forEach>
		<c:if test="${marker.countOfAntibodies > 0}">
		  Antibodies(${marker.countOfAntibodies})
		</c:if>
		<br/>
		<c:if test="${marker.countOfMicroarrayProbesets > 0}">
		  Microarray probesets(${marker.countOfMicroarrayProbesets})
		</c:if>
      </td>
    </tr>
  </c:if>

  <!-- ROW13 -->
  <c:set var="otherIDs" value="${marker.otherIDs}"/>
  <c:if test="${not empty otherIDs}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Other&nbsp;database<br/>links
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <table>
			<c:set var="lastLogicalDB" value="0"/>
			<!--  Yes, this loop is ugly.  It is needed to remove the spaces
				  before the commas, however.  For maintenance, you may want
				  to put it on separate lines, then combine them again before
				  check-in. -->
			<c:forEach var="item" items="${otherIDs}" varStatus="status"><c:if test="${status.first}"><c:set var="prevLDB" value="${item.logicalDB}"/><tr><td>${item.logicalDB}</td><td>${item.accID}</c:if><c:if test="${not status.first}"><c:if test="${prevLDB == item.logicalDB}">, ${item.accID}</c:if><c:if test="${prevLDB != item.logicalDB}"></td></tr><c:set var="prevLDB" value="${item.logicalDB}"/><tr><td>${item.logicalDB}</td><td>${item.accID}</c:if></c:if></c:forEach>
		  </td></tr>
		</table>
      </td>
    </tr>
  </c:if>

  <!-- ROW14 -->
  <c:set var="interproAnnotations" value="${marker.interproAnnotations}"/>
  <c:if test="${not empty interproAnnotations}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Protein-related<br/>information
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        <table>
        <tr><td>Resource</td><td>ID</td><td>Description</td></tr>
        <c:forEach var="item" items="${interproAnnotations}">
          <tr><td>InterPro</td><td>${item.termID}</td><td>${item.term}</td></tr>
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
		All references(${marker.countOfReferences})<br/>
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
