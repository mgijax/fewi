<c:if test="${not empty experiment.inSitu}">
	<c:set var="inSitu" value="${experiment.inSitu}"/>
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			${experiment.type}
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> detailsRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${(not empty inSitu.band) and (fn:length(fn:trim(inSitu.band)) > 0)}">
					<li>
						<div class="label">Band</div>
						<div class="value">
							${inSitu.band}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.strain}">
					<li>
						<div class="label">Strain</div>
						<div class="value">
							${inSitu.strain}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.cellOrigin}">
					<li>
						<div class="label">Cell Type</div>
						<div class="value">
							${inSitu.cellOrigin}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.karyotypeMethod}">
					<li>
						<div class="label">Karyotype Method</div>
						<div class="value">
							${inSitu.karyotypeMethod}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.robertsonians}">
					<li>
						<div class="label">Robertsonian/Translocation</div>
						<div class="value" style="margin-left: 14.5em">
							${inSitu.robertsonians}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.metaphaseCount}">
					<li>
						<div class="label">Metaphases Analyzed</div>
						<div class="value" style="margin-left: 10.5em">
							${inSitu.metaphaseCount}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.grainsScored}">
					<li>
						<div class="label">Grains Scored</div>
						<div class="value">
							${inSitu.grainsScored}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.grainsOnCorrectChromosome}">
					<li>
						<div class="label">Grains on Correct Chromosome</div>
						<div class="value" style="margin-left: 15.5em">
							${inSitu.grainsOnCorrectChromosome}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty inSitu.grainsOnOtherChromosome}">
					<li>
						<div class="label">Grains on Other Chromosome</div>
						<div class="value" style="margin-left: 14.75em">
							${inSitu.grainsOnOtherChromosome}
						</div>
					</li>
				</c:if>
				</ul>

				<!-- INSITU data matrix -->
				<c:set var="experimentTable" value="${experiment.inSituMatrix}"/>
				<c:set var="experimentTableName" value="IN SITU Data"/>
				<c:set var="superscriptLabels" value="true"/>
				<%@ include file="mapping_detail_table.jsp" %>
			</section>
		</div>
	</div>
</c:if>