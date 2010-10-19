<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Sequence sequence = (Sequence)request.getAttribute("sequence"); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

${templateBean.templateHeadHtml}

  <title>Sequence Detail Page</title>
  <%@ include file="/WEB-INF/jsp/includes.jsp" %>

${templateBean.templateBodyStartHtml}



<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="sequence_detail.shtml">    
    <!--myTitle -->
    <span class="titleBarMainTitle">Sequence Detail</span>
</div>


<!-- structural table -->
<table class="detailStructureTable">

<!-- ID/Version -->
<tr >
  <td class="${leftTdStyles.next}">
       <b>ID/Version</b>
  </td>
  <td class="${rightTdStyles.next}">
    <table width=100%>
    <tr>
    <td>
       <b>${sequence.primaryID}</b>  
    </td>
    <td align=right>
      <c:if test="${not empty sequence.version}">
        <b>Version:</b> ${sequence.primaryID}.${sequence.version}
      </c:if>
      <c:if test="${sequence.logicalDB=='SWISS-PROT' || sequence.logicalDB=='TrEMBL'}">
        <b>Last sequence update:</b> ${sequence.sequenceDate} <br>
        <b>Last annotation update:</b> ${sequence.recordDate}
      </c:if>
    </td>
    </tr>
    </table>
  </td>
</tr>


<!-- seq description -->
<tr >
  <td class="${leftTdStyles.next}" >
    <b>Sequence<br>description<br>from provider</b>
  </td>
  <td class="${rightTdStyles.next}" >
    <%=FormatHelper.formatVerbatim(sequence.getDescription())%>
  </td>
</tr>


<!-- Provider -->
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Provider</b>
  </td>
  <td class="${rightTdStyles.next}" >
       <!--${sequence.provider}-->
  </td>
</tr>


<!-- sequence info/download -->
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Sequence</b>
  </td>
  <td class="${rightTdStyles.next}" >
    ${sequence.sequenceType}
    ${sequence.length}
    ${sequence.lengthUnit}
  </td>
</tr>


<!-- Source -->
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Source</b>
  </td>
  <td class="${rightTdStyles.next}" >

    <c:choose>
      <c:when test="${sequence.logicalDB=='Sequence DB' || sequence.logicalDB=='RefSeq'}">

      <c:if test="${not empty sequence.sources}">
      <table>
        <tr>
        <td valign=top>
          <table style="padding:3px;" >
          <tr>
            <td align=right><B>Library</B></td>
            <td>${sequence.library}</td>
          </tr>
          <tr>
            <td align=right><B>Organism</B></td>
            <td>${sequence.organism}</td>
          </tr>
          <tr>
            <td align=right><B>Strain/Species</B></td>
            <td>${sequence.sources[0].strain}</td>
          </tr>
          <tr>
            <td align=right><B>Sex</B></td>
            <td>${sequence.sources[0].sex}</td>
          </tr>
          </table>
        </td>
        <td valign=top>
          <table style="padding:3px;">
          <tr>
            <td align=right><B>Age</B></td>
            <td>${sequence.sources[0].age}</td>
          </tr>
          <tr>
            <td align=right><B>Tissue</B></td>
            <td>${sequence.sources[0].tissue}</td>
          </tr>
          <tr>
            <td align=right><B>Cell line</B></td>
            <td>${sequence.sources[0].cellLine}</td>
          </tr>
          </table>
        </td>
        </tr>
      </table>
      </c:if>
      
      </c:when>
      <c:otherwise>
        <b>Organism</b> ${sequence.organism}
      </c:otherwise>
    </c:choose>
  </td>
</tr>


<!-- Chromosome -->
<c:if test="${not empty chromosome}">
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Chromosome</b>
  </td>
  <td class="${rightTdStyles.next}" >
       ${chromosome}
  </td>
</tr>
</c:if>




<!-- Markers -->
<c:if test="${not empty markers}">
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Annotated genes and markers</b>
  </td>
  <td class="${rightTdStyles.next}" >

  <em>Follow the symbol links to get more information on the GO terms, 
  expression assays, orthologs, phenotypic alleles, and other information 
  for the genes or markers below.</em>

  <table class="borderedTable" style="margin-top:5px; width:95%;" >
    <tr>
    <th>Type</th>
    <th>Symbol</th>
    <th>Name</th>
    <th>GO Terms</th>
    <th>Expression<BR>Assays</th>
    <th>Orthologs</th>
    <th>Phenotypic<BR>Alleles</th>
    </tr>

    <c:forEach var="marker" items="${markers}" >
      <tr>
      <td valign=top>${marker.markerType}</td>
      <td valign=top><a href="${configBean.fewiUrl}marker/${marker.symbol}">${marker.symbol}</a></td>
      <td valign=top>${marker.name}</td>
      <td valign=top>${marker.countOfGOTerms}</td>
      <td valign=top>${marker.countOfGXDAssays}</td>
      <td valign=top>${marker.countOfOrthologs}</td>
      <td valign=top>${marker.countOfAlleles}</td>
      </tr>
    </c:forEach>
  </table>
  </td>
</tr>
</c:if>


<!-- Probes -->
<c:if test="${not empty probes}">
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>MGI curated clones/probes annotated to sequence</b>
  </td>
  <td class="${rightTdStyles.next}" >
    
    <table class="borderedTable" width=75%>
    <tr>
      <th><b>Name</b></th>
      <th><b>Clone<br>Collection</b></th>
      <th><b>Clone ID</b></th>
      <th><b>Type</b></th>
    </tr>

    <c:forEach var="probe" items="${probes}" >
      <% Probe myProbe = (Probe)pageContext.getAttribute("probe"); %>
      <tr>
        <td><%=FormatHelper.formatVerbatim(myProbe.getName())%></td>
        <td>
          <c:forEach var="collection" items="${probe.probeCloneCollection}" >
            ${collection.collection}
          </c:forEach>
        </td>
        <td><a href="${configBean.fewiUrl}probe/${probe.cloneid}">${probe.cloneid}</a></td>
        <td>${probe.segmenttype}</td>
      </tr>
    </c:forEach>

    </table>
  </td>
</tr>
</c:if>


<!-- References -->
<c:if test="${not empty references}">
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Sequence references in MGI</b>
  </td>
  <td class="${rightTdStyles.next}" >

    <c:forEach var="reference" items="${references}" >
       <a href="${configBean.fewiUrl}reference/${reference.jnumID}">${reference.jnumID}</a>
       ${reference.primaryAuthor},
       "${reference.title}"
       ${reference.citation}
       <br>
    </c:forEach>

  </td>
</tr>
</c:if>

<!-- close structural table and page template-->
</table>
${templateBean.templateBodyStopHtml}
