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
	<c:set var="normalMap" value="${mpGroup.normalMap}"/>
	<c:set var="backgroundSensitiveMap" value="${mpGroup.backgroundSensitiveMap}"/>
	<c:set var="conditionalRowIDs" value="${mpGroup.conditionalRowIDs}"/>
	
	<c:set var="columnIDMap" value="${mpGroup.columnIDMap}"/>
	<c:set var="genoClusterKeyMap" value="${mpGroup.genoClusterKeyMap}"/>
 
	<table class="popupTable">
	<!-- table header rows : MP headers span two rows; left cell is only one with two rows showing -->
	<tr><td colspan="2" class="tableLabel">Mouse Phenotypes</td>
	<c:forEach var="mpHeader" items="${mpGroup.columns}">
		<c:set var="markStart" value=""/>
		<c:set var="markEnd" value=""/>
		<c:if test="${not empty highlights[mpHeader]}">
			<c:set var="markStart" value="<mark>"/>
			<c:set var="markEnd" value="</mark>"/>
		</c:if>
		<td rowspan="2" class="popupHeader"><div title="${mpHeader}"><span>${markStart}${mpHeader}${markEnd}</span></div></td>
	</c:forEach>
	</tr>
	<tr>
		<td class="headerTitle border">Availability</td>
		<td class="headerTitle border">Mouse Genotype</td>
	</tr>

	<c:forEach var="rowID" items="${mpGroup.mouseRowIDs}">
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
			<c:forEach var="mpHeader" items="${mpGroup.columns}">
				<c:set var="columnID" value="${columnIDMap[mpHeader]}" />

				<c:set var="flag" value=""/>
				<c:if test="${not empty normalMap[rowID][columnID]}">
					<c:set var="flag" value="<span class='normal'>*</span>"/>
				</c:if>
				<c:if test="${not empty backgroundSensitiveMap[rowID][columnID]}">
					<c:set var="flag" value="${flag}<span class='bg'>!</span>"/>
				</c:if>

				<c:set var="mouseColor" value=""/>
				<c:choose>
				<c:when test="${countMap[rowID][columnID] >= 100}"><c:set var="mouseColor" value="mouse100"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 6}"><c:set var="mouseColor" value="mouse6"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 2}"><c:set var="mouseColor" value="mouse2"/></c:when>
				<c:when test="${countMap[rowID][columnID] >= 1}"><c:set var="mouseColor" value="mouse1"/></c:when>
				</c:choose>
				<td class="border mid ${mouseColor}">${flag}</td>
			</c:forEach>
		</tr>
	</c:forEach>
	</table>
</c:if>
