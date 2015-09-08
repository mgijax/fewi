	<c:if test="${(not empty humanHomologs) or (marker.hasOneEnsemblGeneModelID) or (not empty marker.pirsfAnnotation) }">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">Homology</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="toggleHomologyRibbon" title="Show More" class="toggleImage hdExpand" onclick="toggleRibbon('HomologyRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedHomologyRibbon" style="display:block;">
								<%@ include file="MarkerDetail_HomologyRibbon_closed.jsp" %>
							</div>
							<div id="openedHomologyRibbon" style="display:none;">
								<%@ include file="MarkerDetail_HomologyRibbon_opened.jsp" %>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>

