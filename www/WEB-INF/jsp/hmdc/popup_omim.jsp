<%
	/* to be included by popup.jsp for table of disease (OMIM) annotations, with diseases mapped to
	 * mouse genotypes and/or human genes
	 */
%>
<c:if test="${not empty omimGroup}">
	<!-- disease table -->
	<c:set var="omimCount" value="${fn:length(omimGroup.columns)}"/>
	<c:set var="countMap" value="${omimGroup.countMap}"/>
	<c:set var="allelePairMap" value="${omimGroup.allelePairMap}"/>
	<c:set var="humanSymbolMap" value="${omimGroup.humanSymbolMap}"/>
	<c:set var="diseaseIDMap" value="${omimGroup.diseaseIDMap}"/>
	<c:set var="columnIDMap" value="${omimGroup.columnIDMap}"/>
	<c:set var="genoClusterKeyMap" value="${omimGroup.genoClusterKeyMap}"/>
	<c:set var="homologyClusterKeyMap" value="${omimGroup.homologyClusterKeyMap}"/>
	<c:set var="conditionalRowIDs" value="${omimGroup.conditionalRowIDs}"/>

	<table class="popupTable">
	<!-- table header rows : disease headers span two rows; left cell is only one with two rows showing -->
	<tr><td class="tableLabel" <c:if test="${omimGroup.hasMouseRows}">colspan="2"</c:if>>Diseases</td>
	<c:forEach var="omimHeader" items="${omimGroup.columns}">
		<c:set var="diseaseUrl" value="${configBean.FEWI_URL}disease/${diseaseIDMap[omimHeader]}"/>
		<td rowspan="2" class="popupHeader"><div title="${omimHeader} -- click to see disease details"><span><a href="${diseaseUrl}" target="_blank">${omimHeader}</a></span></div></td>
	</c:forEach>
	</tr>
	
	<c:if test="${omimGroup.hasHumanRows}">
	<!-- human genes to diseases -->

		<tr>
			<c:if test="${omimGroup.hasMouseRows}">
				<td class="headerTitle border" colspan="2">Human Gene</td>
			</c:if>
			<c:if test="${not omimGroup.hasMouseRows}">
				<td class="headerTitle border">Human Gene</td>
			</c:if>
		</tr>

		<c:forEach var="rowID" items="${omimGroup.humanRowIDs}">
			<c:set var="markerUrl" value="${configBean.FEWI_URL}homology/cluster/key/${homologyClusterKeyMap[rowID]}"/>

			<tr class="highlightable" title="click row to see homology details" onclick="window.open('${markerUrl}'); return true;">
			<td class="border" <c:if test="${omimGroup.hasMouseRows}">colspan="2"</c:if>>${humanSymbolMap[rowID]}</td>
				<c:forEach var="omimHeader" items="${omimGroup.columns}">
					<c:set var="columnID" value="${columnIDMap[omimHeader]}" />
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

	<c:if test="${omimGroup.hasMouseRows}">
	<!-- mouse genotypes to diseases -->
	<c:if test="${omimGroup.hasHumanRows}">
		<tr>
			<td class="headerTitle border">Availability</td>
			<td colspan="${omimCount + 1}" class="headerTitle border">Mouse Genotype</td>
		</tr>
	</c:if>
	<c:if test="${not omimGroup.hasHumanRows}">
		<tr>
			<td class="headerTitle border">Availability</td>
			<td class="headerTitle border">Mouse Genotype</td>
		</tr>
	</c:if>
	<c:forEach var="rowID" items="${omimGroup.mouseRowIDs}">
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
			<c:forEach var="omimHeader" items="${omimGroup.columns}">
				<c:set var="columnID" value="${columnIDMap[omimHeader]}" />
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
