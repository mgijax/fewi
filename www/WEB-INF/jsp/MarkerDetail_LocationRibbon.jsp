	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Location & Maps
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">
			<table>
				<tr>
					<td class="top">
						<span id="toggleLocationRibbon" title="Show More" class="toggleImage hdExpand" onclick="toggleRibbon('LocationRibbon'); return false;"></span>
					</td>
					<td>
						<div id="closedLocationRibbon" style="display:block;">
							<%@ include file="MarkerDetail_LocationRibbon_closed.jsp" %>
						</div>
						<div id="openedLocationRibbon" style="display:none;">
							<%@ include file="MarkerDetail_LocationRibbon_opened.jsp" %>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</div>
