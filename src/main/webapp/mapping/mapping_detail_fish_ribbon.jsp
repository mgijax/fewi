<c:if test="${not empty experiment.fish}">
	<c:set var="fish" value="${experiment.fish}"/>
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			${experiment.type}
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> detailsRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${not empty fish.band}">
					<li>
						<div class="label">Band</div>
						<div class="value">
							${fish.band}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.strain}">
					<li>
						<div class="label">Strain</div>
						<div class="value">
							${fish.strain}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.cellOrigin}">
					<li>
						<div class="label">Cell Type</div>
						<div class="value">
							${fish.cellOrigin}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.karyotypeMethod}">
					<li>
						<div class="label">Karyotype Method</div>
						<div class="value">
							${fish.karyotypeMethod}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.robertsonians}">
					<li>
						<div class="label">Robertsonian/Translocation</div>
						<div class="value" style="margin-left: 14.5em">
							${fish.robertsonians}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.metaphaseCount}">
					<li>
						<div class="label">Metaphases Analyzed</div>
						<div class="value" style="margin-left: 10.5em">
							${fish.metaphaseCount}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.singleSignalCount}">
					<li>
						<div class="label">Single Signals</div>
						<div class="value">
							${fish.singleSignalCount}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.doubleSignalCount}">
					<li>
						<div class="label">Double Signals</div>
						<div class="value">
							${fish.doubleSignalCount}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty fish.label}">
					<li>
						<div class="label">Label</div>
						<div class="value">
							${fish.label}
						</div>
					</li>
				</c:if>
				</ul>

				<!-- FISH data matrix -->
				<c:set var="experimentTable" value="${experiment.fishMatrix}"/>
				<c:set var="experimentTableName" value="FISH Data"/>
				<c:set var="superscriptLabels" value="true"/>
				<%@ include file="mapping_detail_table.jsp" %>
			</section>
		</div>
	</div>
</c:if>