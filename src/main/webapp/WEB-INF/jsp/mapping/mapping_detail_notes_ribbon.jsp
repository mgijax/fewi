<c:if test="${(not empty experiment.note) or (not empty experiment.referenceNote) or (not empty experiment.rhPanelLink)}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Notes
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${(not empty experiment.referenceNote) or (not empty experiment.rhPanelLink)}">
					<li>
						<div class="label">Reference</div>
						<div class="value">
							${experiment.referenceNoteHtml}
							<c:if test="${not empty experiment.rhPanelLink}">
								<div class="rhPanelLink">
								<a href="${experiment.rhPanelLink.url}" target="_blank">Primary Mapping Data for the T31 Radiation Hybrid Panel</a>
								</div>
							</c:if>
						</div>
					</li>
				</c:if>
				<c:if test="${not empty experiment.note}">
					<li>
						<div class="label">Experiment</div>
						<div class="value">
							${experiment.noteHtml}
						</div>
					</li>
				</c:if>
				</ul>
			</section>
		</div>
	</div>
</c:if>