<c:if test="${not empty probe.markerAssociations}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Marker
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> markerRibbon">
			<section class="summarySec1 ">
				<c:forEach var="markerAssoc" items="${probe.markerAssociations}">
					<a href="${configBean.FEWI_URL}marker/${markerAssoc.marker.primaryID}">${markerAssoc.marker.symbol}</a> 
					(${markerAssoc.marker.location})
					<c:if test="${markerAssoc.qualifier == 'P'}">(PUTATIVE)</c:if>
					<br/>
				</c:forEach>
			</section>
		</div>
	</div>
</c:if>