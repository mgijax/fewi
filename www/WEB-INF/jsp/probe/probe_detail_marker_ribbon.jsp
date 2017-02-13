<c:if test="${not empty probe.markerAssociations}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Genes
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> markerRibbon">
			<section class="summarySec1 ">
				<table id="markerTable">
				<c:forEach var="markerAssoc" items="${probe.markerAssociations}">
					<tr><td>
					<a href="${configBean.FEWI_URL}marker/${markerAssoc.marker.primaryID}">${markerAssoc.marker.symbol}</a> 
					</td><td>
					${markerAssoc.marker.name}
					</td><td>
					<c:if test="${markerAssoc.qualifier == 'P'}">(PUTATIVE)</c:if>
					</td></tr>
				</c:forEach>
				</table>
			</section>
		</div>
	</div>
</c:if>