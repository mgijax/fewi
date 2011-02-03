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
  <c:if test="${not empty marker.preferredCentimorgans}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Genetic&nbsp;Map
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
        Chromosome ${marker.preferredCentimorgans.chromosome}<br/>
        <fmt:formatNumber value="${marker.preferredCentimorgans.cmOffset}" minFractionDigits="2" maxFractionDigits="2"/> ${marker.preferredCentimorgans.mapUnits}<br/>
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
      <td class="<%=rightTdStyles.getNext() %>">
        Chr${marker.preferredCoordinates.chromosome}:${startCoord}-${endCoord} 
        ${marker.preferredCoordinates.mapUnits}, ${marker.preferredCoordinates.strand} strand<br/>
        (From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier})<br/>
        <p/>
        VEGA: ${marker.vegaGeneModelID}<br/> 
        Ensembl: ${marker.ensemblGeneModelID}<br/> 
        NCBI: ${marker.ncbiGeneModelID}<br/> 
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
		<c:forEach var="orthology" items="${marker.orthologousMarkers}" varStatus="status">
		  ${orthology.otherOrganism}<c:if test="${!status.last}">; </c:if>
		</c:forEach>
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
		Representative Sequences<br/>
		${marker.representativeGenomicSequence.primaryID}<br/>
		${marker.representativeTranscriptSequence.primaryID}<br/>
		${marker.representativePolypeptideSequence.primaryID}<br/>
		<c:if test="${marker.countOfSequences > 0}">
		  All sequences(${marker.countOfSequences}) 
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
		  Associated Human Diseases(${marker.countOfHumanDiseases})&nbsp;&nbsp;&nbsp;
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
		All GO classifications: (${marker.countOfGOTerms} annotations)<br/>
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
  <c:if test="${not empty marker.ids}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Other&nbsp;database<br/>links
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
		<c:forEach var="item" items="${marker.ids}">
		  ${item.logicalDB} : (${item.accID})<br/> 
		</c:forEach>
      </td>
    </tr>
  </c:if>

  <!-- ROW14 -->
  <c:if test="${not empty marker.ids}">
    <tr >
      <td class="<%=leftTdStyles.getNext() %>">
        Protein-related<br/>information
      </td>
      <td class="<%=rightTdStyles.getNext() %>">
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
	    <c:if test="${not empty earliestRef}">(Earliest) ${earliestRef.jnumID}
		  ${earliestRef.longCitation}<br/>
		</c:if>
		<c:if test="${not empty latestRef}">(Latest) ${latestRef.jnumID}
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
