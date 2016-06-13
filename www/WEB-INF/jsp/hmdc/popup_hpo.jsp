<%
	/* to be included by popup.jsp for table of human phenotype (HPO) annotations,
	 * generated from human marker/OMIM annotations and an OMIM/HPO mapping.
	 */
%>
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
	<c:set var="homologyClusterKeyMap" value="${hpoGroup.homologyClusterKeyMap}"/>

	<table>
	<c:forEach var="rowID" items="${hpoGroup.humanRowIDs}">
		<c:set var="marker" value="${humanSymbolMap[rowID]}"/>
		<c:set var="diseaseUrl" value="${fn:replace(externalUrls.HPO_Disease, '@@@@', diseaseIDMap[rowID])}"/>

		<c:if test="${lastMarker != marker}">
			<c:set var="markerUrl" value="${configBean.FEWI_URL}homology/cluster/key/${homologyClusterKeyMap[rowID]}"/>
			<c:set var="markerHeader" value="Disease(s) Associated with <a href='${markerUrl}' target='_blank'>${marker}</a>"/>
			<c:if test="${empty showHeaders}">
				<tr>
				<td colspan="${hpoCount + 1}" class="headerTitle border">${markerHeader}</td>
				</tr>
			</c:if>
			<c:if test="${not empty showHeaders}">
				<tr><td class="tableLabel">Human Phenotypes</td>
				<c:set var="showHeaders" value=""/>
				<c:forEach var="hpoHeader" items="${hpoGroup.columns}">
					<td rowspan="2" class="header"><div class="header" title="${hpoHeader}"><span class="header">${hpoHeader}</span></div></td>
				</c:forEach>
				</tr>
				<tr>
				<td class="headerTitle border">${markerHeader}</td>
				</tr>
			</c:if>
			<c:set var="lastMarker" value="${marker}"/>
		</c:if>
		<tr class="highlight" title="click row to see phenotype details" onclick="window.open('${diseaseUrl}'); return true;">
			<td class="border">${humanDiseaseMap[rowID]}</td>
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
