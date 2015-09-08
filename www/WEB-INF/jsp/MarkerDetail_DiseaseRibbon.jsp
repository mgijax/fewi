	<c:if test="${fn:length(MouseOMIMAnnotations) > 0 or fn:length(HumanOMIMAnnotations) > 0 or (not empty diseaseRefCount && diseaseRefCount > 0) or (marker.countOfAllelesWithHumanDiseases > 0)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Human Diseases
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<table>
					<tr>
						<td class="top">
							<span id="toggleDiseaseRibbon" title="Show More" class="toggleImage hdExpand" onclick="toggleRibbon('DiseaseRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedDiseaseRibbon" style="display:block;">
								<%@ include file="MarkerDetail_DiseaseRibbon_closed.jsp" %>
							</div>
							<div id="openedDiseaseRibbon" style="display:none;">
								<%@ include file="MarkerDetail_DiseaseRibbon_opened.jsp" %>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>
