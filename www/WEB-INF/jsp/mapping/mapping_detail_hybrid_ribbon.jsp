<c:if test="${not empty experiment.hybrid}">
	<c:set var="hybrid" value="${experiment.hybrid}"/>
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			${experiment.type}
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> detailsRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${not empty hybrid.band}">
					<li>
						<div class="label">Band</div>
						<div class="value">
							${hybrid.band}
						</div>
					</li>
				</c:if>
				<c:if test="${(not empty hybrid.concordanceType) and (not empty experiment.hybridMatrix)}">
					<li>
						<div class="label">Concordance</div>
						<div class="value">
							${hybrid.concordanceType}
						</div>
					</li>
				</c:if>
				</ul>

				<!-- HYBRID data matrix -->
				<c:set var="experimentTable" value="${experiment.hybridMatrix}"/>
				<c:set var="experimentTableName" value="HYBRID Data"/>
				<c:set var="superscriptLabels" value="true"/>
				<div id="hybridTable">
				<%@ include file="mapping_detail_table.jsp" %>
				</div>
				<script>
					$('#hybridTable table tr:first :nth-child(2)').addClass('fixedWidthFont');
					$('#hybridTable table tr:first :nth-child(3)').addClass('fixedWidthFont');
					$('#hybridTable table tr:first :nth-child(4)').addClass('fixedWidthFont');
					$('#hybridTable table tr:first :nth-child(5)').addClass('fixedWidthFont');
				</script>
			</section>
		</div>
	</div>
</c:if>