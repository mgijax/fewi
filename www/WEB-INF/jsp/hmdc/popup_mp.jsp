<%
	/* to be included by popup.jsp for table of Mammalian Phenotype (MP) annotations,
	 * mouse genotypes to MP terms
	 */
%>
<c:if test="${not empty mpGroup}">
	<!-- mouse phenotype table -->
	<c:set var="lastAllelePair" value=""/>
	<c:set var="mpCount" value="${fn:length(mpGroup.columns)}"/>
	<c:set var="countMap" value="${mpGroup.countMap}"/>
	<c:set var="allelePairMap" value="${hpoGroup.allelePairMap}"/>

	<c:set var="humanDiseaseMap" value="${hpoGroup.humanDiseaseMap}"/>
	<c:set var="columnIDMap" value="${hpoGroup.columnIDMap}"/>
	<c:set var="diseaseIDMap" value="${hpoGroup.diseaseIDMap}"/>
	<c:set var="clusterKeyMap" value="${hpoGroup.clusterKeyMap}"/>

	<div class="tableLabel">Mouse Phenotypes</div>
	<table>
	<c:forEach var="rowID" items="${mpGroup.mouseRowIDs}">
		<c:set var="allelePair" value="${allelePairMap[rowID]}"/>
		<c:set var="genoclusterUrl" value="${configBean.FEWI_URL}diseasePortal/genoCluster/view/${diseaseIDMap[rowID]}"/>

		<c:if test="${lastAllelePair != allelePair}">
			<c:set var="markerUrl" value="${configBean.FEWI_URL}homology/cluster/key/${clusterKeyMap[rowID]}"/>
			<c:set var="markerHeader" value="Disease(s) Associated with <a href='${markerUrl}' target='_blank'>${allelePair}</a>"/>
			<tr><td></td>
			<c:set var="showHeaders" value=""/>
			<c:forEach var="hpoHeader" items="${mpGroup.columns}">
				<td rowspan="2" class="header"><div class="header" title="${hpoHeader}">${hpoHeader}</div></td>
			</c:forEach>
			</tr>
			<tr>
			<td class="headerTitle border">${markerHeader}</td>
			</tr>
			<c:set var="lastAllelePair" value="${allelePair}"/>
		</c:if>
		<tr>
			<td class="border"><a href="${diseaseUrl}" target="_blank">${humanDiseaseMap[rowID]}</a></td>
			<c:forEach var="hpoHeader" items="${mpGroup.columns}">
				<c:set var="columnID" value="${columnIDMap[hpoHeader]}" />
				<c:set var="mouseColor" value=""/>
				<c:choose>
				<c:when test="${countMap[rowID][columnID] >= 100}"><c:set var="mouseColor" value="human100"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 6}"><c:set var="mouseColor" value="human6"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 2}"><c:set var="mouseColor" value="human2"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 1}"><c:set var="mouseColor" value="human1"/></c:when>
				</c:choose>
				<td class="border ${mouseColor}"></td>
			</c:forEach>
		</tr>
	</c:forEach>
	</table>
</c:if>
