<%
	/* to be included by popup.jsp for table of disease (DO) annotations, with diseases mapped to
	 * mouse genotypes and/or human genes (click disease cell from main HMDC grid to get to this popup)
	 */
%>
<c:if test="${not empty diseaseGroup}">
	<!-- disease table -->
	<c:set var="doCount" value="${fn:length(diseaseGroup.columns)}"/>
	<c:set var="countMap" value="${diseaseGroup.countMap}"/>
	<c:set var="allelePairMap" value="${diseaseGroup.allelePairMap}"/>
	<c:set var="humanSymbolMap" value="${diseaseGroup.humanSymbolMap}"/>
	<c:set var="diseaseIDMap" value="${diseaseGroup.diseaseIDMap}"/>
	<c:set var="columnIDMap" value="${diseaseGroup.columnIDMap}"/>
	<c:set var="genoClusterKeyMap" value="${diseaseGroup.genoClusterKeyMap}"/>
	<c:set var="homologyClusterKeyMap" value="${diseaseGroup.homologyClusterKeyMap}"/>
	<c:set var="conditionalRowIDs" value="${diseaseGroup.conditionalRowIDs}"/>

	<table class="popupTable">
	<!-- table header rows : disease headers span two rows; left cell is only one with two rows showing -->
	<tr><td class="tableLabel" <c:if test="${diseaseGroup.hasMouseRows}">colspan="2"</c:if>>Diseases</td>
	<c:forEach var="doHeader" items="${diseaseGroup.columns}">
		<c:set var="diseaseUrl" value="${configBean.FEWI_URL}disease/${diseaseIDMap[doHeader]}"/>
		<td rowspan="2" class="popupHeader"><div title="${doHeader} -- click to see disease details"><span><a href="${diseaseUrl}" target="_blank">${doHeader}</a></span></div></td>
	</c:forEach>
	</tr>
	
	<c:if test="${diseaseGroup.hasHumanRows}">
	<!-- human genes to diseases -->

		<tr>
			<c:if test="${diseaseGroup.hasMouseRows}">
				<td class="headerTitle border" colspan="2">Human Gene</td>
			</c:if>
			<c:if test="${not diseaseGroup.hasMouseRows}">
				<td class="headerTitle border">Human Gene</td>
			</c:if>
		</tr>

		<c:forEach var="rowID" items="${diseaseGroup.humanRowIDs}">
			<c:set var="markerUrl" value="${configBean.FEWI_URL}homology/alliance/gene?clusterKey=${homologyClusterKeyMap[rowID]}&organism=human&symbol=${humanSymbolMap[rowID]}"/>

			<tr class="highlightable" title="click row to see homology details" onclick="window.open('${markerUrl}'); return true;">
			<td class="border" <c:if test="${diseaseGroup.hasMouseRows}">colspan="2"</c:if>>${humanSymbolMap[rowID]}</td>
				<c:forEach var="doHeader" items="${diseaseGroup.columns}">
					<c:set var="columnID" value="${columnIDMap[doHeader]}" />
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
	</c:if>

	<c:if test="${diseaseGroup.hasMouseRows}">
	<!-- mouse genotypes to diseases -->
	<c:if test="${diseaseGroup.hasHumanRows}">
		<tr>
			<td class="headerTitle border">Availability</td>
			<td colspan="${doCount + 1}" class="headerTitle border">Mouse Genotype</td>
		</tr>
	</c:if>
	<c:if test="${not diseaseGroup.hasHumanRows}">
		<tr>
			<td class="headerTitle border">Availability</td>
			<td class="headerTitle border">Mouse Genotype</td>
		</tr>
	</c:if>
	<c:forEach var="rowID" items="${diseaseGroup.mouseRowIDs}">
		<c:set var="genoclusterUrl" value="${configBean.FEWI_URL}diseasePortal/genoCluster/view/${genoClusterKeyMap[rowID]}"/>

		<c:set var="conditional" value=""/>
		<c:if test="${conditionalRowIDs[rowID] == 1}">
			<c:set var="conditional" value="&nbsp;&nbsp;(conditional)"/>
		</c:if>

		<tr class="highlightable" title="click row to see phenotype details" onclick="window.open('${genoclusterUrl}'); return true;">
			<td class="border" title="click button to find models" onclick="event.cancelBubble=true;">
				<c:set var="buttonID" value="fm${genoClusterKeyMap[rowID]}"/>
				<c:set var="gcKey" value="${genoClusterKeyMap[rowID]}"/>
				<input id="${buttonID}" class="button" value="Find Mice" type="button" onClick='showDialog(event, ${gcKey})'>
			</td>
			<td id="${buttonID}a" class="border">${allelePairMap[rowID]}${conditional}</td>
			<c:forEach var="doHeader" items="${diseaseGroup.columns}">
				<c:set var="columnID" value="${columnIDMap[doHeader]}" />
				<c:set var="mouseColor" value=""/>
				<c:choose>
				<c:when test="${countMap[rowID][columnID] >= 100}"><c:set var="mouseColor" value="mouse100"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 6}"><c:set var="mouseColor" value="mouse6"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 2}"><c:set var="mouseColor" value="mouse2"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 1}"><c:set var="mouseColor" value="mouse1"/></c:when>
				</c:choose>
				<td class="border ${mouseColor}"></td>
			</c:forEach>
		</tr>
	</c:forEach>
	</c:if>

	</table>
</c:if>
