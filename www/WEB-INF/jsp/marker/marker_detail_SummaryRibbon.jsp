	<div class="row" id="summaryRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Summary
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
					<li>
						<div class="label">Symbol</div>
						<div class="value emphasis">
							<fewi:super value="${marker.symbol}"/><c:if test="${marker.status == 'interim'}"> (Interim)</c:if>
						</div>
					</li>
					<li>
						<div class="label">Name</div>
						<div class="value">${marker.name}</div>
					</li>

					<c:if test="${not empty marker.synonyms}">
						<li>
							<div class="label">Synonyms</div>
							<div class="value">
								<c:forEach var="synonym" items="${marker.synonyms}" varStatus="status">
									<fewi:super value="${synonym.synonym}"/><c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>
				</ul>
			</section>

			<section class="summarySec2">
				<ul>
					<c:if test="${not empty marker.markerSubtype}">
						<li>
							<div class="label">Feature Type</div>
							<div class="value">${marker.markerSubtype}</div>
							<div class="value">
								<c:if test="${not empty biotypeConflictTable}">
									<nobr><a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'BL', 'BR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img style="position: relative; top: 4px;" src="${configBean.WEBSHARE_URL}images/warning2.gif" height="18" width="18" id="warning" border="0"></a>
									<a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'BL', 'BR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>BioType Conflict</a></nobr>
									<br/>
								</c:if>
							</div>
						</li>
					</c:if>

					<c:if test="${not empty tssFor}">
						<li>
							<div class="label">Transcription Start Site for</div>
							<div class="value"><a href="${configBean.FEWI_URL}marker/${tssFor.relatedMarkerID}">${tssFor.relatedMarkerSymbol}</a>
								<c:if test="${not empty distanceFrom}">(${distanceFrom} bp from 5'-end of gene)</c:if>
								<c:if test="${not empty marker.preferredCoordinates and not empty tssFor.relatedMarker.preferredCoordinates}">
									<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
									<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
									<fmt:formatNumber value="${tssFor.relatedMarker.preferredCoordinates.startCoordinate}" pattern="#0" var="tssForStart"/>
									<fmt:formatNumber value="${tssFor.relatedMarker.preferredCoordinates.endCoordinate}" pattern="#0" var="tssForEnd"/>
									<c:set var="tssAtJbrowse" value="${externalUrls.JBrowseTSS_Highlight}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<chromosome>', marker.preferredCoordinates.chromosome)}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<start>', tssForStart)}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<end>', tssForEnd)}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<startHighlight>', startCoord)}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<endHighlight>', endCoord)}"/>
									(view in <a href="${tssAtJbrowse}" target="_blank">JBrowse</a>)
								</c:if>
							</div>
						</li>
					</c:if>

					<li>
						<div class="label">IDs</div>
						<div class="value">${marker.primaryID}
							<c:if test="${not (empty marker.entrezGeneIDs or fn:length(marker.entrezGeneIDs) < 1)}">
								<br/>
								NCBI Gene:
								<c:forEach var="egID" items="${marker.entrezGeneIDs}" varStatus="status"><a href="${fn:replace(externalUrls.Entrez_Gene, '@@@@', egID.accID)}" target="_blank">${egID.accID}</a><c:if test="${!status.last}">, </c:if></c:forEach>
							</c:if>
						</div>
					</li>

					<c:if test="${hasClusters}">
						<li>
							<div class="label">Member of</div>
							<div class="value">
								<c:forEach var="cluster" items="${marker.clusters}" varStatus="status">
									<a href="${configBean.FEWI_URL}marker/${cluster.relatedMarkerID}">${cluster.relatedMarkerSymbol}</a> cluster<c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>

					<c:if test="${hasClusterMembers}">
						<li>
							<div class="label">Cluster&nbsp;Member<c:if test="${memberCount > 1}">s</c:if></div>
							<div class="value">
								<c:forEach var="member" items="${marker.clusterMembers}" varStatus="status" end="2">
									<a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a><c:if test="${!status.last}">, </c:if>
								</c:forEach>
								<c:if test="${memberCount > 3}">...</c:if>
								(<span id="showClusterMembers" class="link">${memberCount}</span> member<c:if test="${memberCount > 1}">s</c:if>)
							</div>
						</li>
					</c:if>
					<c:if test="${(not empty marker.myGeneSymbol) and (not empty marker.myGeneID)}">
						<li>
							<div class="label">Gene Overview</div>
							<div class="value">
								MyGene.info: <a href="${fn:replace(externalUrls.MyGene, '@@@@', marker.myGeneID.accID)}" target="_blank">${marker.myGeneSymbol}</a>
							</div>
						</li>
					</c:if>
					<c:if test="${(marker.markerType == 'Gene') or (marker.markerType == 'Pseudogene')}">
						<li>
							<div class="label">Alliance</div>
							<div class="value">
								<a id="allianceLink" href="${fn:replace(externalUrls.AGR_Gene, '@@@@', marker.primaryID)}" target="_blank">gene page</a>
							</div>
						</li>
					</c:if>

					<c:if test="${hasTss}">
						<li>
							<div class="label">Transcription Start Sites</div>
							<div class="value">
								<span id="showTss" class="link">${tssCount} TSS</span>
								<c:if test="${not empty marker.preferredCoordinates}">
									<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
									<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
									<c:set var="tssAtJbrowse" value="${externalUrls.JBrowseTSS}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<chromosome>', marker.preferredCoordinates.chromosome)}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<start>', startCoord)}"/>
									<c:set var="tssAtJbrowse" value="${fn:replace(tssAtJbrowse, '<end>', endCoord)}"/>
									(view in <a href="${tssAtJbrowse}" target="_blank">JBrowse</a>)
								</c:if>
							</div>
						</li>
					</c:if>
				</ul>
			</section>

			<div id="whatDoesThisGeneDoDiv" style="display:none;"></div>
		</div>
	</div>

