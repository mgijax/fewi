<%
	/* to be included by strain_grid_popup.jsp for table of Mammalian Phenotype (MP) annotations,
	 * mouse genotypes to MP terms (for a particular mouse strain)
	 */
%>
<c:if test="${not empty strainPhenoGroup}">
	<!-- mouse phenotype table -->
	<table class="popupTable">
	<!-- table header rows : MP headers span two rows; left cell is only one with two rows showing -->
	<tr><td colspan="2" class="tableLabel">Mouse Phenotypes</td>
	<c:forEach var="mpHeader" items="${strainPhenoGroup.headers}">
		<td rowspan="2" class="popupHeader"><div title="${mpHeader}"><span>${mpHeader}</span></div></td>
	</c:forEach>
	</tr>
	<tr>
		<td class="headerTitle border">Availability</td>
		<td class="headerTitle border">Mouse Genotype</td>
	</tr>

	<c:forEach var="row" items="${strainPhenoGroup.rows}">
		<c:set var="genotypeUrl" value="${configBean.FEWI_URL}allele/genoview/${row.genotype.primaryID}"/>

		<tr class="highlightable" title="click row to see phenotype details" onclick="window.open('${genotypeUrl}'); return true;">
			<td class="border" title="click button to find models" onclick="event.cancelBubble=true;">
				<c:set var="buttonID" value="fm${row.genotype.genotypeKey}"/>
				<c:set var="gtKey" value="${row.genotype.genotypeKey}"/>
				<input id="${buttonID}" class="button" value="Find Mice" type="button" onClick='showDialog(event, ${gtKey})'>
			</td>
			<td id="${buttonID}a" class="border">${row.allelePairs}</td>
			<c:forEach var="cell" items="${row.cells}">
				<c:set var="mouseColor" value=""/>
				<c:choose>
				<c:when test="${cell.count >= 100}"><c:set var="mouseColor" value="mouse100"/></c:when>
				<c:when test="${cell.count >= 6}"><c:set var="mouseColor" value="mouse6"/></c:when>
				<c:when test="${cell.count >= 2}"><c:set var="mouseColor" value="mouse2"/></c:when>
				<c:when test="${cell.count >= 1}"><c:set var="mouseColor" value="mouse1"/></c:when>
				</c:choose>
				<td class="border mid ${mouseColor}">${flag}</td>
			</c:forEach>
		</tr>
	</c:forEach>
	</table>
</c:if>
