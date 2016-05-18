<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="disease_portal_header.jsp" %>

<style>
#title {
    border: thin solid black;
    min-width: 600px;
    max-width: 600px;
    background-color: #dfefff;
    padding: 4px;
    font-weight: bold;
    font-size: 125%;
}

#legend {
    min-width: 600px;
    max-width: 600px;
    margin-top: 4px;
}

#hdpSystemPopupLegend td {
    border: thin solid black;
    padding: 3px;
    background-color: #dfefff;
}

span.highlight { background-color: #D6C6E8 }

td.bgsensitive {
    color: #FF4700;
    text-align: center;
    font-weight: bold;
}
</style>

<c:if test="${not empty humanMarkers}">
  <c:set var="hmarkers" value='${fn:join(humanMarkers, ", ")}'/>
</c:if>
<c:if test="${not empty mouseMarkers}">
  <c:set var="mmarkers" value='${fn:join(mouseMarkers, ", ")}'/>
</c:if>
<c:if test="${not empty isPhenotype}">
  <c:set var="suffix" value=" abnormalities"/>
</c:if>

<c:set var="pageTitle" value="Data for ${hmarkers}/${mmarkers} and ${headerTerm}${suffix}"/>

<script>
	// change window title on page load
    document.title = '${pageTitle}';
</script>

<style>
#markerList {
  font-family: Verdana,Arial,Helvetica;
  font-size: 12px;
  padding-bottom:4px;
}
</style>

</head>

<body style="margin: 8px; min-width: 1px;">

<div id="title">${pageTitle}</div>

<c:if test="${not empty isPhenotype}">
<div id="legend">
  <table id="hdpSystemPopupLegend">
    <tr>
	  <td>*</td><td>Aspects of the system are reported to show a normal phenotype.</td></tr><tr>
	  <td class="bgsensitive">!</td><td>Indicates phenotype varies with strain background.</td></tr><tr>
	  <td></td><td><span class="highlight">Highlighted Columns</span> contain at least one phenotype or disease result matching your search term(s).</td>
    </tr>
  </table>
</div>
</c:if>

<p/>
Debugging Info:<p/>

gridClusterKey: ${gridClusterKey}<br/>
header: ${headerTerm}<br/>
gridKey count: ${gridKeyCount}<br/>
annotation count: ${annotationCount}<br/>
terms: <c:forEach var="term" items="${highlightTerms}">${term}, </c:forEach><br/>
termIds: <c:forEach var="termId" items="${highlightTermIds}">${termId}, </c:forEach><br/>
isPhenotype: ${isPhenotype}<br/>
isDisease: ${isDisease}<br/>

<!-- Table and Wrapping div -->

<div id="markerList">
<c:forEach var="marker" items="${markers2}" varStatus="status">
  Find Mice: IMSR strains or lines carrying any ${marker.symbol} Mutation
  <a href='${configBean.IMSRURL}summary?gaccid=${marker.primaryID}&states=ES+Cell&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>
    (${marker.countForImsr} available)
  </a><br/>
</c:forEach>
</div>


<%@ include file="/WEB-INF/jsp/hmdc/disease_portal_grid_popup_grid.jsp" %>

</body>
</html>
