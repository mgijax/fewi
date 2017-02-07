<c:set var="polymorphismRefs" value="${probe.polymorphismReferences}"/>
<c:if test="${not empty polymorphismRefs}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Polymorphisms
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> polymorphismRibbon">
			<section class="summarySec1 ">
				<c:forEach var="ref" items="${polymorphismRefs}" varStatus="rStatus">
					<a href="${configBean.FEWI_URL}reference/${ref.reference.jnumID}">${ref.reference.jnumID}</a> 
					${ref.reference.shortCitation}
					<br/>
					<c:if test="${not empty ref.note}">
						<B>Notes:</B> ${ref.note}<br/>
					</c:if>
					<table>
						<tr>
							<th>Endonuclease</th>
							<th>Marker</th>
							<th>Allele</th>
							<th>Fragments</th>
							<th>Strains</th>
						</tr>
					<c:forEach var="polymorphism" items="${ref.probePolymorphisms}" varStatus="pStatus">

						<c:set var="rowCount" value="${fn:length(polymorphism.details)}"/>
						<c:set var="rowSpan" value=""/>
						<c:if test="${rowCount > 1}">
							<c:set var="rowSpan" value=" rowspan='${rowCount}'"/>
						</c:if>

						<c:forEach var="details" items="${polymorphism.details}" varStatus="dStatus">
							<tr>
							<c:if test="${dStatus.first}">
								<td${rowSpan}>${polymorphism.endonuclease}</td>
								<td${rowSpan}>${polymorphism.markerID}</td>
							</c:if>
							<td>${details.allele}</td>
							<td>${details.fragments}</td>
							<td>${details.strains}</td>
							</tr>
						</c:forEach>
					</c:forEach>
					</table>
				</c:forEach>
			</section>
		</div>
	</div>
</c:if>