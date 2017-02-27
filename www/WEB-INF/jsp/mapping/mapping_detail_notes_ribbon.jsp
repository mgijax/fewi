<c:if test="${(not empty experiment.note) or (not empty experiment.referenceNote)}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Notes
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${not empty experiment.referenceNote}">
					<li>
						<div class="label">Reference</div>
						<div class="value">
							${experiment.referenceNote}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty experiment.note}">
					<li>
						<div class="label">Experiment</div>
						<div class="value">
							${experiment.note}
						</div>
					</li>
				</c:if>
				</ul>
			</section>
		</div>
	</div>
</c:if>