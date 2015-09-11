	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Summary<br/>
			<!--<font color='#0000A0'><span onClick="showHideById('whatDoesThisGeneDoDiv');" onMouseOver="this.style.color = '#cc0000'; this.style.backgroundColor='#c7e3fe';" onMouseOut="this.style.color ='#0000A0'; this.style.backgroundColor='#FFFFFF';">Show/Hide</span></font> -->
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<table border="none">
				<tr>
					<td style="vertical-align: top;">
						<table width="450px">
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Symbol</font></td>
								<td class="padded" style="width: 100%">
									<font class="enhance"><fewi:super value="${marker.symbol}"/></font><c:if test="${marker.status == 'interim'}"> (Interim)</c:if>
								</td>
							</tr>
							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">Name</font></td>
								<td class="padded">${marker.name}</td>
							</tr>
							<c:if test="${not empty marker.synonyms}">
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Synonyms</font></td>
									<td class="padded">
										<c:forEach var="synonym" items="${marker.synonyms}" varStatus="status">
											<fewi:super value="${synonym.synonym}"/><c:if test="${!status.last}">, </c:if>
										</c:forEach>
									</td>
								</tr>
							</c:if>
							<!-- once we have the summary
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Gene&nbsp;Summary</font></td>
									<td class="padded"> from NCBI RefSeq </td>
								</tr>
							-->
						</table>
					</td>
					<td style="vertical-align:top;">
						<table width="450px">
							<c:if test="${not empty marker.markerSubtype}">
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Feature&nbsp;Type</font></td>
									<td class="padded">${marker.markerSubtype}</td>
								</tr>
							</c:if>

							<c:if test="${not empty biotypeConflictTable or not empty strainSpecificNote}">
								<tr>
									<td class="rightBorderThinGray label padded top right"></td>
									<td class="padded">
										<div class="biotypeConflictDiv" style="padding-bottom: 4px;padding-left: 4px;">
											<c:if test="${not empty biotypeConflictTable}">
												<a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'BL', 'BR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img style="position: relative; top: 4px;" src="${configBean.WEBSHARE_URL}images/warning2.gif" height="18" width="18" id="warning" border="0"></a>
												<a onClick="return overlib('${biotypeConflictTable}', STICKY, CAPTION, 'BioType Annotation Conflict', ANCHOR, 'warning', ANCHORALIGN, 'BL', 'BR', CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>BioType Conflict</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											</c:if>
											<c:if test="${not empty strainSpecificNote}">
												<a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'BL', 'BR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#"><img style="position: relative; top: 7px;" src="${configBean.WEBSHARE_URL}images/mice.jpg" height="25" width="25" id="mice" border="0"></a>
												<a onClick="return overlib('${strainSpecificNote}', STICKY, CAPTION, 'Strain-Specific Marker', ANCHOR, 'mice', ANCHORALIGN, 'BL', 'BR', WIDTH, 400, CLOSECLICK, CLOSETEXT, 'Close X');" href="#" class="markerNoteButton" style='display:inline;'>Strain-Specific Marker</a>
											</c:if>
										</div>
									</td>
								</tr>
							</c:if>

							<tr>
								<td class="rightBorderThinGray label padded top right"><font class="label">IDs</font></td>
								<td class="padded" style="width: 100%">${marker.primaryID}
								<c:if test="${not empty marker.entrezGeneID}">
								<br/>
								NCBI Gene: <a href="${fn:replace(externalUrls.Entrez_Gene, '@@@@', marker.entrezGeneID.accID)}" target="_blank">${marker.entrezGeneID.accID}</a>
								</c:if>
							</td>
							</tr>
							<c:if test="${hasClusters}">
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Member&nbsp;of</font></td>
									<td class="padded">
										<c:forEach var="cluster" items="${marker.clusters}" varStatus="status">
											<a href="${configBean.FEWI_URL}marker/${cluster.relatedMarkerID}">${cluster.relatedMarkerSymbol}</a> cluster<c:if test="${!status.last}">, </c:if>
										</c:forEach>
									</td>
								</tr>
							</c:if>
							<c:if test="${hasClusterMembers}">
								<tr>
									<td class="rightBorderThinGray label padded top right"><font class="label">Cluster&nbsp;Member<c:if test="${memberCount > 1}">s</c:if></font></td>
									<td class="padded">
										<c:forEach var="member" items="${marker.clusterMembers}" varStatus="status" end="2">
											<a href="${configBean.FEWI_URL}marker/${member.relatedMarkerID}">${member.relatedMarkerSymbol}</a><c:if test="${!status.last}">, </c:if>
										</c:forEach>
										<c:if test="${memberCount > 3}">...</c:if>
										(<span id="showClusterMembers" class="link">${memberCount}</span> member<c:if test="${memberCount > 1}">s</c:if>)
									</td>
								</tr>
							</c:if>
						</table>
					</td>
				</tr>
			</table>
			<div id="whatDoesThisGeneDoDiv" style="display:none;">
			</div>
		</div>
	</div>


