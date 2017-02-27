<c:set var="rirc" value="${experiment.rirc}"/>
<c:if test="${not empty rirc}">
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
			<!--
				<table class="mappingTable">
				<tr><th>Gene</th><th>Allele</th><th>Assay Type</th><th>Description</th></tr>
				<c:forEach var="assoc" items="${markerAssociations}">
					<tr>
					<c:choose>
						<c:when test="${not empty assoc.marker.primaryID}">
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
			-->
			</section>
		</div>
	</div>
</c:if>