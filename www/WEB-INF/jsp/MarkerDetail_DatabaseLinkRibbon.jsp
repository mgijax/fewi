	<c:if test="${not empty logicalDBs}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Other&nbsp;Database<br/>Links
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="toggleDatabaseLinkRibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('DatabaseLinkRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedDatabaseLinkRibbon" style="display:none;">
							</div>
							<div id="openedDatabaseLinkRibbon" style="display:block;">
								<table cellspacing=2 cellpadding=2>
									<c:forEach var="item" items="${logicalDBs}">
										<tr><td>${item}&nbsp;</td><td>${otherIDs[item]}</td></tr>
									</c:forEach>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>
