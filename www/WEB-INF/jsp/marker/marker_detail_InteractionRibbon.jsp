	<c:if test="${not empty interactions}">
		<div class="row" id="interactionRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">
				<span style="white-space: nowrap; vertical-align: top;">
					Interactions
				</span>
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<div id="intToggle" title="Show Less" class="toggleImage hdCollapse">less</div>

				<section class="summarySec1 wide extra open">
					<ul>
						<li>
							<div style="padding-left: 90px;">
								<span style="display: inline; line-height: 160%">
									<c:forEach var="interaction" items="${interactions}" varStatus="status">
										${interaction}<c:if test="${!status.last}"><br/></c:if>
									</c:forEach>
									<a id="interactionLink" href="${configBean.FEWI_URL}interaction/explorer?markerIDs=${marker.primaryID}" class="markerNoteButton" style="display:inline;">View&nbsp;All</a>
								</span>
							</div>
						</li>
					</ul>
				</section>

			</div>
		</div>
	</c:if>
