<c:if test="${not empty experimentTable}">
	<c:if test="${not empty experimentTableName}">
		<div class="mappingTableHeader">${experimentTableName}</div>
	</c:if>
	<c:set var="headerRow" value="${experimentTable.headerRow}"/>
	<c:set var="dataRows" value="${experimentTable.dataRows}"/>
	<table class="mappingTable">
		<c:if test="${not empty headerRow}">
			<tr>
			<c:forEach var="cell" items="${headerRow.cells}">
				<c:choose>
					<c:when test="${not empty cell.markerID}">
						<th><a href="${configBean.FEWI_URL}marker/${cell.markerID}"><fewi:super value="${cell.label}"/></a></th>
					</c:when>
					<c:when test="${not superscriptLabels}">
						<th><fewi:verbatim value="${cell.label}"/></th>
					</c:when>
					<c:otherwise>
						<th><fewi:super value="${cell.label}"/></th>
					</c:otherwise>
				</c:choose>
			</c:forEach>
			</tr>
		</c:if>
		<c:if test="${not empty dataRows}">
			<c:forEach var="row" items="${dataRows}">
				<tr>
				<c:forEach var="cell" items="${row.cells}">
					<c:choose>
						<c:when test="${not empty cell.markerID}">
							<td><a href="${configBean.FEWI_URL}marker/${cell.markerID}"><fewi:super value="${cell.label}"/></a></td>
						</c:when>
						<c:when test="${not superscriptLabels}">
							<td><fewi:verbatim value="${cell.label}"/></td>
						</c:when>
						<c:otherwise>
							<td><fewi:super value="${cell.label}"/></td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</tr>
			</c:forEach>
		</c:if>
	</table>
</c:if>