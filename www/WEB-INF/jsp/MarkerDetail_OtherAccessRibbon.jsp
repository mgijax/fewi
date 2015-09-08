	<c:set var="otherMgiIDs" value="${marker.otherMgiIDs}"/>
	<c:if test="${not empty otherMgiIDs}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>" >
				Other<br/>Accession&nbsp;IDs
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>" >
				<table>
					<tr>
						<td class="top">
							<span id="toggleOtherAccessRibbon" title="Show More" class="toggleImage hdCollapse" onclick="toggleRibbon('OtherAccessRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedOtherAccessRibbon" style="display:none;">
							</div>
							<div id="openedOtherAccessRibbon" style="display:block;">
								<c:forEach var="item" items="${otherMgiIDs}" varStatus="status">
									${item.accID}<c:if test="${not status.last}">, </c:if>
								</c:forEach>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>
