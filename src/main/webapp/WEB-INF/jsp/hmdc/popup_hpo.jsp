<%
	/* to be included by popup.jsp for table of human phenotype (HPO) annotations,
	 * generated from human marker/DO annotations and an DO/HPO mapping.
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

	<table class="popupTable">
	<c:forEach var="rowID" items="${hpoGroup.humanRowIDs}">
		<c:set var="marker" value="${humanSymbolMap[rowID]}"/>
		<c:set var="diseaseID" value="${diseaseIDMap[humanDiseaseMap[rowID]]}"/>
		<c:set var="diseaseUrl" value="${externalUrls.HPO_Home}"/>
		<c:if test="${not empty diseaseID}">
			<c:set var="diseaseUrl" value="${fn:replace(externalUrls.HPO_Disease, '@@@@', diseaseIDMap[humanDiseaseMap[rowID]])}"/>
		</c:if>

		<c:if test="${lastMarker != marker}">
			<c:set var="markerUrl" value="${configBean.FEWI_URL}homology/alliance/gene?clusterKey=${homologyClusterKeyMap[rowID]}&organism=human&symbol=${marker}"/>
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
					<c:set var="markStart" value=""/>
					<c:set var="markEnd" value=""/>
					<c:if test="${not empty highlights[hpoHeader]}">
						<c:set var="markStart" value="<mark>"/>
						<c:set var="markEnd" value="</mark>"/>
					</c:if>
					<td rowspan="2" class="popupHeader"><div title="${hpoHeader}"><span>${markStart}${hpoHeader}${markEnd}</span></div></td>
				</c:forEach>
				</tr>
				<tr>
				<td class="headerTitle border">${markerHeader}</td>
				</tr>
			</c:if>
			<c:set var="lastMarker" value="${marker}"/>
		</c:if>
		<tr class="highlightable" title="click row to see phenotype details" onclick="window.open('${diseaseUrl}'); return true;">
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
