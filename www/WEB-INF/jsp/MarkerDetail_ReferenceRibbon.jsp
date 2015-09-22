	<c:if test="${not empty marker.references}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>" >
				References
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>" >
				<section class="summarySec1">
					<div id="toggleReferenceRibbon" title="Show More" class="toggleImage hdCollapse"></div>
					<ul>
						<c:if test="${marker.countOfReferences > 0}">
							<li>
								<div class="label">Summaries</div>
								<div class="value">
									<div style="display: inline-block; padding-right: 50px;">All <a href="${configBean.FEWI_URL}reference/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfReferences}</a></div>
									<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
										<div style="display: inline-block; padding-right: 50px;">Diseases <a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}?typeFilter=Literature">${diseaseRefCount}</a></div>
									</c:if>
									<c:if test="${marker.countOfGxdLiterature > 0}">
										<div style="display: inline-block; padding-right: 50px;">Expression <a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a></div>
									</c:if>
									<c:if test="${marker.countOfGOReferences > 0}">
										<div style="display: inline-block; padding-right: 50px;">Gene Ontology <a href="${configBean.FEWI_URL}reference/go/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfGOReferences}</a></div>
									</c:if>
									<c:if test="${marker.countOfPhenotypeReferences > 0}">
										<div style="display: inline-block; padding-right: 50px;">Phenotypes <a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfPhenotypeReferences}</a></div>
									</c:if>
								</div>
							</li>
						</c:if>
						<c:if test="${not empty marker.earliestReference}">
							<li class="extra open">
								<div class="label">Earliest</div>
								<div class="value"><a href="${configBean.FEWI_URL}reference/${marker.earliestReference.jnumID}">${marker.earliestReference.jnumID}</a> ${marker.earliestReference.shortCitation}</div>
							</li>
						</c:if>
						<c:if test="${not empty marker.latestReference}">
							<li class="extra open">
								<div class="label">Latest</div>
								<div class="value"><a href="${configBean.FEWI_URL}reference/${marker.latestReference.jnumID}">${marker.latestReference.jnumID}</a> ${marker.latestReference.shortCitation}</div>
							</li>
						</c:if>
					</ul>
				</section>
			</div>
		</div>
	</c:if>
