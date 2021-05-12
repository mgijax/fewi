	<div class="row" >
		<div class="header <%=leftTdStyles.getNext() %>">
			Genome Location and Flanking Sequence
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">

			<section class="summarySec1 open">
				<ul>
					<c:forEach var="coord" items="${snp.consensusCoordinates}" varStatus="status">
						<li>
							<div class="label">Location</div>
							<div class="value">Chr${coord.chromosome}:${coord.startCoordinate} (${assemblyVersion})</div>
						</li>
						<li>
							<div class="label">SNP Orientation to the Genome</div>
							<div class="value">${coord.strandFormatted}</div>
						</li>
						<c:if test="${not status.last && fn:length(snp.consensusCoordinates) > 1}"><hr></c:if>
					</c:forEach>
				</ul>
			</section>

			<section class="summarySec1 open">
				<ul>
					<li>
						<div class="label">SNP Reference Flanking Sequence</div>
						<%@ include file="SNPDetail_FlankTable.jsp" %>
					</li>
				</ul>
			</section>

		</div>
	</div>


