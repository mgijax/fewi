	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Experiment
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
					<li>
						<div class="label">Experiment</div>
						<div class="value">
							${experiment.type}
						</div>
					</li>

					<li>
						<div class="label">Chromosome</div>
						<div class="value">${experiment.chromosome}</div>
					</li>

					<li>
						<div class="label">Reference</div>
						<div class="value"><a href="${configBean.FEWI_URL}reference/${experiment.reference.jnumID}">${experiment.reference.jnumID}</a>
							${experiment.reference.shortCitation}
						</div>
					</li>
					
					<li>
						<div class="label">ID</div>
						<div class="value">${experiment.primaryID}</div>
					</li>
				</ul>
			</section>
		</div>
	</div>

