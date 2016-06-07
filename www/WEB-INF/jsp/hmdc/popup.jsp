<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ page import = "org.jax.mgi.fewi.util.*" %>

<%@ page trimDirectiveWhitespaces="true" %>

<%@ include file="header.jsp" %>

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

<style>
table tr td {
	padding: 3px;
	text-align: left;
	vertical-align: middle;
}
td.headerTitle {
	background-color: #dddddd;
	height: 50px;
	white-space: nowrap;
}
td.header {
	height: 150px;
	padding-bottom: 0px;
	text-align: left;
	vertical-align: bottom;
	white-space: nowrap;
	font-weight: normal;
}
div.header {
	color: black;
	text-indent: 2px;
	-webkit-transform: rotate(-45deg);
	transform: rotate(-45deg);
	width: 20px;
}
td.border {
	border: thin solid black;
}

td.human100 { background-color: #F7861D; }
td.human6 { background-color: #F4A041; }
td.human2 { background-color: #F2BF79; }
td.human1 { background-color: #FBDBB4; }

td.mouse100 { background-color: #0C2255; }
td.mouse6 { background-color: #49648B; }
td.mouse2 { background-color: #879EBA; }
td.mouse1 { background-color: #C6D6E8; }
</style>

<c:if test="${not empty hpoGroup}">
	<!-- human phenotype table -->
	<c:set var="lastMarker" value=""/>
	<c:set var="showHeaders" value="true"/>
	<c:set var="hpoCount" value="${fn:length(hpoGroup.columns)}"/>
	<c:set var="countMap" value="${hpoGroup.countMap}"/>
	<c:set var="humanSymbolMap" value="${hpoGroup.humanSymbolMap}"/>
	<c:set var="humanDiseaseMap" value="${hpoGroup.humanDiseaseMap}"/>
	<c:set var="columnIDMap" value="${hpoGroup.columnIDMap}"/>
	<c:set var="diseaseIDMap" value="${hpoGroup.diseaseIDMap}"/>
	<c:set var="clusterKeyMap" value="${hpoGroup.clusterKeyMap}"/>

	<div class="label">Human Phenotypes</div>
	<table>
	<c:forEach var="rowID" items="${hpoGroup.humanRowIDs}">
		<c:set var="marker" value="${humanSymbolMap[rowID]}"/>
		<c:set var="diseaseUrl" value="${configBean.FEWI_URL}disease/${diseaseIDMap[rowID]}"/>

		<c:if test="${lastMarker != marker}">
			<c:set var="markerUrl" value="${configBean.FEWI_URL}homology/cluster/key/${clusterKeyMap[rowID]}"/>
			<c:set var="markerHeader" value="Disease(s) Associated with <a href='${markerUrl}' target='_blank'>${marker}</a>"/>
			<c:if test="${empty showHeaders}">
				<tr>
				<td colspan="${hpoCount + 1}" class="headerTitle border">${markerHeader}</td>
				</tr>
			</c:if>
			<c:if test="${not empty showHeaders}">
				<tr><td></td>
				<c:set var="showHeaders" value=""/>
				<c:forEach var="hpoHeader" items="${hpoGroup.columns}">
					<td rowspan="2" class="header"><div class="header" title="${hpoHeader}">${hpoHeader}</div></td>
				</c:forEach>
				</tr>
				<tr>
				<td class="headerTitle border">${markerHeader}</td>
				</tr>
			</c:if>
			<c:set var="lastMarker" value="${marker}"/>
		</c:if>
		<tr>
			<td class="border"><a href="${diseaseUrl}" target="_blank">${humanDiseaseMap[rowID]}</a></td>
			<c:forEach var="hpoHeader" items="${hpoGroup.columns}">
				<c:set var="columnID" value="${columnIDMap[hpoHeader]}" />
				<c:set var="humanColor" value=""/>
				<c:choose>
				<c:when test="${countMap[rowID][columnID] >= 100}"><c:set var="humanColor" value="human100"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 6}"><c:set var="humanColor" value="human6"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 2}"><c:set var="humanColor" value="human2"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 1}"><c:set var="humanColor" value="human1"/></c:when>
				</c:choose>
				<td class="border ${humanColor}"></td>
			</c:forEach>
		</tr>
	</c:forEach>
	</table>
</c:if>


<div style="margin-top: 50px">
Debugging Info:<p/>

gridClusterKey: ${gridClusterKey}<br/>
header: ${headerTerm}<br/>
gridKey count: ${gridKeyCount}<br/>
annotation count: ${annotationCount}<br/>
terms: <c:forEach var="term" items="${highlightTerms}">${term}, </c:forEach><br/>
termIds: <c:forEach var="termId" items="${highlightTermIds}">${termId}, </c:forEach><br/>
isPhenotype: ${isPhenotype}<br/>
isDisease: ${isDisease}<br/>
<p/>
omimGroup: ${omimGroup}<br/>
mpGroup: ${mpGroup}<br/>
hpoGroup: ${hpoGroup}<br/>
</div>

<!-- Table and Wrapping div -->

<div id="markerList">
<c:forEach var="marker" items="${markers2}" varStatus="status">
  Find Mice: IMSR strains or lines carrying any ${marker.symbol} Mutation
  <a href='${configBean.IMSRURL}summary?gaccid=${marker.primaryID}&states=ES+Cell&states=embryo&states=live&states=ovaries&states=sperm' target='_blank'>
    (${marker.countForImsr} available)
  </a><br/>
</c:forEach>
</div>

</body>
</html>