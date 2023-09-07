<c:set var="markerAssociations" value="${experiment.markers}"/>
<c:if test="${not empty markerAssociations}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Genes
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> genesRibbon">
			<section class="summarySec1 ">
			  <div id="mappingTableWrapper">
				<table class="mappingTable">
				<tr><th>Gene</th><th>Allele</th><th>Assay Type</th><th>Description</th></tr>
				<c:forEach var="assoc" items="${markerAssociations}">
					<tr>
					<c:choose>
						<c:when test="${(not empty assoc.marker.primaryID) and (assoc.marker.status != 'withdrawn')}">
							<td><a href="${configBean.FEWI_URL}marker/${assoc.marker.primaryID}"><fewi:super value="${assoc.marker.symbol}"/></a></td>
						</c:when>
						<c:otherwise>
							<td><fewi:super value="${assoc.marker.symbol}"/></td>
						</c:otherwise>
					</c:choose>
					</td>
					<td><fewi:super value="${assoc.allele.symbol}"/></td>
					<td>${assoc.assayType}</td>
					<td>${assoc.description}</td>
					</tr>
				</c:forEach>
				</table>
			  </div>
			</section>
		</div>
	</div>
	<style>
		.maxWidth { max-width: 90%; }
	</style>
	<script>
	// set wrapper width so scrollbar is right beside the table of markers, if we have scrolling
	if ($('#mappingTableWrapper table').height() > $('#mappingTableWrapper').height()) {
		$('#mappingTableWrapper').addClass('maxWidth');
		setTimeout(function() {
			$('#mappingTableWrapper').width($('#mappingTableWrapper table').width() + 37);
			$('#mappingTableWrapper').addClass('scrollY');
			}, 500);
	}
	</script>
</c:if>