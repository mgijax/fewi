<c:if test="${not empty experiment.rirc}">
	<c:set var="rirc" value="${experiment.rirc}"/>
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			${experiment.type}
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> detailsRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${not empty rirc.strain1}">
					<li>
						<div class="label">Origin</div>
						<div class="value">
							${rirc.strain1} x ${rirc.strain2}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty rirc.designation}">
					<li>
						<div class="label">Designation</div>
						<div class="value">
							${rirc.designation}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty rirc.abbreviation1}">
					<li>
						<div class="label">Abbreviation 1</div>
						<div class="value">
							${rirc.abbreviation1}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty rirc.abbreviation2}">
					<li>
						<div class="label">Abbreviation 2</div>
						<div class="value">
							${rirc.abbreviation2}
						</div>
					</li>
				</c:if>
				</ul>

				<!-- RI/RC data matrix -->
				<c:set var="experimentTable" value="${experiment.rircMatrix}"/>
				<c:set var="experimentTableName" value="RI Data (columns show RI Lines sampled)"/>
				<c:set var="superscriptLabels" value="true"/>
				<%@ include file="mapping_detail_table.jsp" %>
				
				<!-- RI/RC 2x2 data -->
				<c:set var="experimentTable" value="${experiment.rirc2x2}"/>
				<c:set var="experimentTableName" value="2x2 Data"/>
				<%@ include file="mapping_detail_table.jsp" %>
				
				<!-- RI/RC statistics -->
				<c:set var="experimentTable" value="${experiment.rircStatistics}"/>
				<c:set var="experimentTableName" value="Statistics"/>
				<%@ include file="mapping_detail_table.jsp" %>
			</section>
		</div>
	</div>
</c:if>