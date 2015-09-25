	<c:if test="${not empty interactions}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>">
				<span style="white-space: nowrap; vertical-align: top;">
					Interactions
				</span>
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<div id="toggleInteractionRibbon" title="Show More" class="toggleImage hdCollapse"></div>

				<section class="summarySec1 extra open">
					<ul>
						<li>
							<div style="padding-left: 35px;">
								<span style="display: inline; line-height: 130%">
									<c:forEach var="interaction" items="${interactions}" varStatus="status">
										${interaction}<c:if test="${!status.last}"><br/></c:if>
									</c:forEach>
									<div style="display: inline; margin-left: 5px;">
										<a id="interactionLink" href="${configBean.FEWI_URL}interaction/explorer?markerIDs=${marker.primaryID}" class="markerNoteButton" style="display:inline;">View&nbsp;All</a>
									</div>
								</span>
							</div>
						</li>
					</ul>
				</section>

			</div>
		</div>
	</c:if>
