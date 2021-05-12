<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "org.jax.mgi.fewi.util.link.ProviderLinker" %>
<%@ page import = "org.jax.mgi.fewi.util.link.IDLinker" %>
<%@ page import = "org.jax.mgi.fewi.util.DBConstants" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% Sequence sequence = (Sequence)request.getAttribute("sequence"); %>
<% IDLinker idLinker = (IDLinker)request.getAttribute("idLinker"); %>

<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

  <title>Sequence Detail</title>
  <%@ include file="/WEB-INF/jsp/includes.jsp" %>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<script>
  function formatForwardArgs() {
    if(document.offset.offset.value) {
        flank = eval(document.offset.offset.value) * 1000
    } else {
        flank = ''
    }
    document.sequenceForm.action = document.seqPullDownForm.seqPullDown.options[document.seqPullDownForm.seqPullDown.selectedIndex].value;
    document.sequenceForm.seq1.value = document.seqData.arg.value + flank;

    if (document.sequenceForm.action.indexOf("blast") >= 0) {
	document.sequenceForm.target = "_blank";
    } else {
	document.sequenceForm.target = "";
    }
    document.sequenceForm.submit();
  }
</script>


<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="SEQUENCE_detail_help.shtml">    
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
    <table width="100%" id="seqIdTable">
    <tr>
    <td>

       <b>${sequence.primaryID}</b>
       <c:if test="${not empty otherIDs}">
         <c:forEach var="otherID" items="${otherIDs}" >
           ${otherID.accID}
         </c:forEach>
       </c:if>
       <%
          String providerLink = ProviderLinker.getSeqProviderLinks(sequence);
          if ((providerLink != null) && (providerLink.length() > 0)) {
       %>
        	 (<%= providerLink.replace("Mouse Genomes Project", "Ensembl") %>)
       <%
          }
       %>

    </td>
    <c:if test="${not empty mgvUrl}">
    	<td align="center">
			<a href="${mgvUrl}" target="_blank" id="mgvLink">Multiple Genome Viewer (MGV)</a>
 		</td>
    </c:if>
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
    <fewi:ntc value="${sequence.description}" />
  </td>
</tr>


<!-- Provider -->
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Provider</b>
  </td>
  <td class="${rightTdStyles.next}" >
       ${sequence.provider}  
      <c:if test="${sequence.status=='DELETED'}">
        <b><i>This sequence has been deleted from the provider database.</i></b>
      </c:if>
  </td>
</tr>


<!-- sequence info/download -->
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Sequence</b>
  </td>
  <td class="${rightTdStyles.next}" >

    <div style="position: relative;;width:100%; height:1.5em;">

      ${sequence.sequenceType}
      ${sequence.length}
      ${sequence.lengthUnit}
    
      <form name="seqData">
       <input name ="arg" value="<%=FormatHelper.getSeqForwardValue(sequence)%>" type=hidden>
      </form>
      <form name="sequenceForm" method="get">
        <input name="seq1"  type="hidden">
      </form>
    
      <div style="width:22em; position: absolute; top: 0px; left: 160px; ">
      <form name="offset">
        <c:choose>
        <c:when test="${sequence.provider=='Ensembl Gene Model' || sequence.provider=='NCBI Gene Model'}">
          &#177; <input type="text" size=3 name="offset" value=0 > Kb of flanking sequence
        </c:when>
        <c:otherwise>
          <input type="hidden" name="offset" value="">
        </c:otherwise>
        </c:choose>
      </form>
      </div>

      <div style="width:30em; position: absolute; top: 0px; right: 4px; text-align:right;">
        <form name="seqPullDownForm" method="get">
        <i>For this sequence</i>
        <select name='seqPullDown'>
          <option value="${configBean.SEQFETCH_URL}" selectED>download in FASTA format
          <option value="${configBean.FEWI_URL}sequence/blast">forward to NCBI BLAST
        </select>
        <input type=button value="Go" onClick=formatForwardArgs()><br>
        </form>
      </div>

    </div>

  </td>
</tr>


<!-- Source -->
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Source</b>
  </td>
  <td class="${rightTdStyles.next}" >
    <c:choose>
    <c:when test="${sequence.logicalDB=='Sequence DB' || sequence.logicalDB=='RefSeq' || sequence.logicalDB=='Mouse Genome Project' || sequence.logicalDB=='MGI Strain Gene'}">

      <c:if test="${not empty sequence.sources}">
	  <c:set var="seqSource" value="${sequence.sources[0]}"/>

      <table id="sourceTable">
        <tr>
        <td valign=top>
          <table style="padding:3px;" id="sourceTableInner1">
          <tr>
            <td align=right><B>
				<c:choose>
				<c:when test="${sequence.logicalDB == 'Mouse Genome Project' || sequence.logicalDB=='MGI Strain Gene'}">Source Name</c:when>
				<c:otherwise>Library</c:otherwise>
				</c:choose>
			</B></td>
            <td>${sequence.library}</td>
          </tr>
          <tr>
            <td align=right><B>Organism</B></td>
            <td>${sequence.organism}</td>
          </tr>
          <tr>
            <td align=right><B>Strain/Species</B></td>
			<td>
			<c:choose>
			<c:when test="${not empty seqSource.strainID}"><a href="${configBean.FEWI_URL}strain/${seqSource.strainID}" id="strainLink">${seqSource.strain}</a>
			</c:when>
			<c:otherwise>${seqSource.strain}</c:otherwise>
			</c:choose>
			</td>
          </tr>
          <tr>
            <td align=right><B>Sex</B></td>
            <td>${seqSource.sex}</td>
          </tr>
          </table>
        </td>
        <td valign=top>
          <table style="padding:3px;" id="sourceTableInner2">
          <tr>
            <td align=right><B>Age</B></td>
            <td>${seqSource.age}</td>
          </tr>
          <tr>
            <td align=right><B>Tissue</B></td>
            <td>${seqSource.tissue}</td>
          </tr>
          <tr>
            <td align=right><B>Cell line</B></td>
            <td>${seqSource.cellLine}</td>
          </tr>
          </table>
        </td>
        </tr>

        <c:if test="${not empty sourceNotice}">
        <tr  valign=top ALIGN=left>
          <td>
            <i>${sourceNotice}</i>
         </td>
        </tr>
        </c:if>

      </table>
      </c:if>

    </c:when>

    <c:otherwise>
      <div style="position: relative;;width:100%; height:1.5em;">
        <div style="position: absolute; top: 0px; left: 4px; ">
          <b>Organism</b> ${sequence.organism}
        </div>
        <div style="position: absolute; top: 0px; right: 4px; text-align:right;">
		<%
			if ((providerLink != null) && (providerLink.length() > 0)) {
		%>
        		See <%= providerLink.replace("Mouse Genomes Project", "Ensembl") %> for source
		<%
			}
		%>
        </div>
      </div>
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

<c:set var="width" value="95%"/>
<c:set var="hideColumns" value="false"/>
<c:if test="${sequence.organism != 'mouse'}">
	<!-- For non-mouse sequence, assume we will hide the extra columns.  However, if it is
		associated with at least one mouse marker, we'll show them.  (This happens for a
		few sequences with a "Not Loaded" organism.) -->
	<c:set var="hideColumns" value="true"/>
	<c:set var="width" value="65%"/>
	<c:forEach var="marker" items="${markers}">
		<c:if test="${marker.organism == 'mouse'}">
			<c:set var="hideColumns" value="false"/>
			<c:set var="width" value="95%"/>
		</c:if>
	</c:forEach>
</c:if>

<!-- Markers -->
<c:if test="${not empty markers}">
<tr  valign=top ALIGN=left>
  <td class="${leftTdStyles.next}" >
    <b>Annotated genes and markers</b>
  </td>
  <td class="${rightTdStyles.next}" >

  <c:if test="${hideColumns == 'false'}">
  <em>Follow the symbol links to get more information on the GO terms, 
  expression assays, orthologs, phenotypic alleles, and other information 
  for the genes or markers below.</em>
  </c:if>

  <table class="borderedTable" style="margin-top:5px; width:${width};" id="markerTable">
    <tr>
    <th>Type</th>
    <th>Symbol</th>
    <th>Name</th>
	<c:if test="${hideColumns == 'false'}">
    <th>GO Terms</th>
    <th>Expression<BR>Assays</th>
    <th>Orthologs</th>
    <th>Phenotypic<BR>Alleles</th>
	</c:if>
    </tr>

    <c:forEach var="marker" items="${markers}" >
      <% Marker marker = (Marker)pageContext.getAttribute("marker"); %>
      <tr>
      <td valign=top>${marker.markerType}</td>
      <td valign=top><%=idLinker.getDefaultMarkerLink(marker)%></td>
      <td valign=top>${marker.name}</td>
	  <c:if test="${hideColumns == 'false'}">
      <td valign=top>${marker.countOfGOTerms}</td>
      <td valign=top>${marker.countOfGxdAssays}</td>
      <td valign=top>${marker.countOfOrthologs}</td>
      <td valign=top>${marker.countOfAlleles}</td>
	  </c:if>
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
    
    <table class="borderedTable" width=75% id="cloneTable">
    <tr>
      <th><b>Name</b></th>
      <th><b>Clone<br>Collection</b></th>
      <th><b>Clone ID</b></th>
      <th><b>Type</b></th>
    </tr>

    <c:forEach var="probe" items="${probes}" >
      <tr>
        <td>
          <a href="${configBean.FEWI_URL}probe/${probe.primaryID}">
          <fewi:verbatim value="${probe.name}" />
          </a>
        </td>
        <td>
          <c:forEach var="collection" items="${probe.probeCloneCollection}" >
            ${collection.collection}<br/>
          </c:forEach>
        </td>
        <td>
          ${probe.cloneID}
        </td>
        <td>${probe.segmentType}</td>
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
       <a href="${configBean.FEWI_URL}reference/${reference.jnumID}">${reference.jnumID}</a>
       ${reference.shortCitation}
       <br>
    </c:forEach>

  </td>
</tr>
</c:if>

<!-- close structural table and page template-->
</table>
<style>
#sourceTable td {
	padding-bottom: 5px;
	padding-left: 3px;
	padding-right: 3px;
}
#seqIdTable td {
	width: 33%;
}
</style>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
