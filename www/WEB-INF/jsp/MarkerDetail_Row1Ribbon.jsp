	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Symbol<br/><br/>
			Name<br/>
			ID
			<c:if test="${hasClusters}">
				<br/>Member&nbsp;of
			</c:if>
			<c:if test="${hasClusterMembers}">
				<br/>Cluster&nbsp;member<c:if test="${memberCount > 1}">s</c:if>
			</c:if>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<div class="biotypeConflictDiv" style="float: right;">
				<c:if test="${not empty biotypeConflictTable}">
					<a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'UL', 'UR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img src="${configBean.WEBSHARE_URL}images/warning2.gif" height="26" width="26" id="warning" border="0"></a>
					<a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'UL', 'UR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>BioType Conflict</a>
				</c:if>
				<c:if test="${not empty strainSpecificNote}">
					<a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'UL', 'UR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img src="${configBean.WEBSHARE_URL}images/mice.jpg" height="38" width="38" id="mice" border="0"></a>
					<a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'UL', 'UR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>Strain-Specific Marker</a>
				</c:if>
			</div>
			<div class="geneSymbolSection">
				<b style="font-size:x-large;"><fewi:super value="${marker.symbol}"/></b><c:if test="${marker.status == 'interim'}"> (Interim)</c:if>
				<br/>
				<b>${marker.name}</b><br/>
					${marker.primaryID}

				<c:if test="${hasClusters}">
					<br/>
					<c:forEach var="cluster" items="${marker.clusters}" varStatus="status">
						<a href="${configBean.FEWI_URL}marker/${cluster.relatedMarkerID}">${cluster.relatedMarkerSymbol}</a> cluster<c:if test="${!status.last}">, </c:if>
					</c:forEach>
				</c:if>

				<c:if test="${hasClusterMembers}">
					<br/>
					<c:forEach var="member" items="${marker.clusterMembers}" varStatus="status" end="2">
						<a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a><c:if test="${!status.last}">, </c:if>
					</c:forEach>
					<c:if test="${memberCount > 3}">...</c:if>
					(<span id="showClusterMembers" class="link">${memberCount}</span> member<c:if test="${memberCount > 1}">s</c:if>)
				</c:if>
			</div>
		</div>
	</div>


