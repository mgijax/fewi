	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Nomenclature
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
					<li>
						<div class="label">Strain Name</div>
						<div class="value" id="strainName">
							<fewi:super value="${strain.name}"/> 
			
							<c:if test="${strain.isStandard==0}">
								<span id="strainIsStandard"> (pending review) </span>
							</c:if>
						</div>
					</li>

					<c:if test="${not empty strain.strainAttributes}">
						<li>
							<div class="label">Attributes</div>
							<div class="value" id="strainAttributes">
								<c:forEach var="attribute" items="${strain.strainAttributes}" varStatus="status">
									<fewi:super value="${attribute.attribute}"/><c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>

					<li>
						<div class="label">MGI ID</div>
						<div class="value" id="strainPrimaryID">${strain.primaryID}</div>
					</li>

					<c:if test="${not empty strain.strainSynonyms}">
						<li>
							<div class="label">Synonyms</div>
							<div class="value" id="strainSynonyms">
								<c:forEach var="synonym" items="${strain.strainSynonyms}" varStatus="status">
									<fewi:super value="${synonym.synonym}"/><c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>


				</ul>
			</section>
		</div>
	</div>

