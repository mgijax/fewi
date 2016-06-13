<%
	/* to be included by popup.jsp for table of Mammalian Phenotype (MP) annotations,
	 * mouse genotypes to MP terms
	 */
%>
<c:if test="${not empty mpGroup}">
	<!-- mouse phenotype table -->
	<c:set var="mpCount" value="${fn:length(mpGroup.columns)}"/>
	<c:set var="countMap" value="${mpGroup.countMap}"/>
	<c:set var="allelePairMap" value="${mpGroup.allelePairMap}"/>

	<c:set var="columnIDMap" value="${mpGroup.columnIDMap}"/>
	<c:set var="genoClusterKeyMap" value="${mpGroup.genoClusterKeyMap}"/>

	<table>
	<!-- table header rows : MP headers span two rows; left cell is only one with two rows showing -->
	<tr><td class="tableLabel">Mouse Phenotypes</td>
	<c:forEach var="mpHeader" items="${mpGroup.columns}">
		<td rowspan="2" class="header"><div class="header" title="${mpHeader}"><span class="header">${mpHeader}</span></div></td>
	</c:forEach>
	</tr>
	<tr>
		<td class="headerTitle border">Mouse Genotype</td>
	</tr>

	<c:forEach var="rowID" items="${mpGroup.mouseRowIDs}">
		<c:set var="genoclusterUrl" value="${configBean.FEWI_URL}diseasePortal/genoCluster/view/${genoClusterKeyMap[rowID]}"/>

		<tr class="highlight" title="click row to see phenotype details" onclick="window.open('${genoclusterUrl}'); return true;">
			<td class="border">${allelePairMap[rowID]}</td>
			<c:forEach var="mpHeader" items="${mpGroup.columns}">
				<c:set var="columnID" value="${columnIDMap[mpHeader]}" />
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
	</table>
</c:if>
