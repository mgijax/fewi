	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Nomenclature
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
					<li>
						<div class="label">Strain Name</div>
						<div class="value">
							<fewi:super value="${strain.name}"/>
						</div>
					</li>

					<li>
						<div class="label">MGI ID</div>
						<div class="value">${strain.primaryID}</div>
					</li>
				</ul>
			</section>
		</div>
	</div>

