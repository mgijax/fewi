<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Sequence sequence = (Sequence)request.getAttribute("sequence"); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
 
${templateBean.templateHeadHtml}
  <title>Sequence Detail Page</title>
${templateBean.templateBodyStartHtml}


<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="sequence_detail.shtml">    
    <!--myTitle -->
    <span class="titleBarMainTitle">Sequence Detail</span>
</div>


<!-- structural table -->
<table border=1 cellpadding=2 cellspacing=1>

<!-- ID/Version -->
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
       <b>ID/Version</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>
    <table width=100%>
    <tr>
    <td>
       ${sequence.primaryID}
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
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Sequence<br>description<br>from provider</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>
    <%=FormatHelper.formatVerbatim(sequence.getDescription())%>
  </td>
</tr>


<!-- Provider -->
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Provider</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>
       ${sequence.provider}
  </td>
</tr>


<!-- sequence info/download -->
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Sequence</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>
    ${sequence.sequenceType}
    ${sequence.length}
    ${sequence.lengthUnit}

  </td>
</tr>


<!-- Source -->
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Source</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>

    <c:choose>
      <c:when test="${sequence.logicalDB=='Sequence DB' || sequence.logicalDB=='RefSeq'}">

      <c:if test="${not empty sequence.sources}">
      <table border=0>
        <tr>
        <td valign=top>
          <table border=0>
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
          <table border=0>
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
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Chromosome</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>
       ${chromosome}
  </td>
</tr>
</c:if>




<!-- Markers -->
<c:if test="${not empty markers}">
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Annotated genes and markers</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>

  <em>Follow the symbol links to get more information on the GO terms, 
  expression assays, orthologs, phenotypic alleles, and other information 
  for the genes or markers below.</em>

  <table border=1 width=95%>
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
<% Marker myMarker = (Marker)pageContext.getAttribute("marker"); %>
      <tr>
      <td valign=top>${marker.markerType} <%=myMarker.getMarkerType()%></td>
      <td valign=top>${marker.symbol}</td>
      <td valign=top>${marker.name}</td>
      <td valign=top>Count - Go Terms</td>
      <td valign=top>Count - Assays</td>
      <td valign=top>Count - Orthologs</td>
      <td valign=top>Count - Alleles</td>
      </tr>
    </c:forEach>
  </table>
  </td>
</tr>
</c:if>


<!-- References -->
<tr class="${trStyles.next}" valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" WIDTH=8% ALIGN=right>
    <b>Sequence references in MGI</b>
  </td>
  <td class=${rightTdStyles.next}" WIDTH=93%>



       ---REFERENCES---



  </td>
</tr>


<!-- close structural table and page template-->
</table>
${templateBean.templateBodyStopHtml}
