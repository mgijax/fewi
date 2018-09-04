	<c:if test="${not empty marker.references}">
		<div class="row" id="referenceRibbon">
			<div class="header <%=leftTdStyles.getNext() %>" >
				References
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>" >
				<div id="refToggle" title="Show More" class="toggleImage hdExpand">more</div>
				<section class="summarySec1 wide">
					<ul>
						<c:if test="${marker.countOfReferences > 0}">
							<li>
								<div class="label">Summaries</div>
								<div class="value">
									<div style="display: inline-block; padding-right: 50px;">All <a href="${configBean.FEWI_URL}reference/marker/${marker.primaryID}?typeFilter=Literature" id="allRefsLink">${marker.countOfReferences}</a></div>
									<c:if test="${marker.countOfGxdLiterature > 0}">
										<div style="display: inline-block; padding-right: 50px;">Developmental Gene Expression <a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}" id="gxdRefsLink">${marker.countOfGxdLiterature}</a></div>
									</c:if>
									<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
										<div style="display: inline-block; padding-right: 50px;">Diseases <a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}?typeFilter=Literature" id="diseaseRefsLink">${diseaseRefCount}</a></div>
									</c:if>
									<c:if test="${marker.countOfGOReferences > 0}">
										<div style="display: inline-block; padding-right: 50px;">Gene Ontology <a href="${configBean.FEWI_URL}reference/go/marker/${marker.primaryID}?typeFilter=Literature" id="goRefsLink">${marker.countOfGOReferences}</a></div>
									</c:if>
									<c:if test="${marker.countOfPhenotypeReferences > 0}">
										<div style="display: inline-block; padding-right: 50px;">Phenotypes <a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature" id="phenoRefsLink">${marker.countOfPhenotypeReferences}</a></div>
									</c:if>
								</div>
							</li>
						</c:if>
						<c:if test="${not empty marker.earliestReference}">
							<li class="extra closed">
								<div class="label">Earliest</div>
								<div class="value"><a href="${configBean.FEWI_URL}reference/${marker.earliestReference.jnumID}">${marker.earliestReference.jnumID}</a> ${marker.earliestReference.shortCitation}</div>
							</li>
						</c:if>
						<c:if test="${not empty marker.latestReference}">
							<li class="extra closed">
								<div class="label">Latest</div>
								<div class="value"><a href="${configBean.FEWI_URL}reference/${marker.latestReference.jnumID}">${marker.latestReference.jnumID}</a> ${marker.latestReference.shortCitation}</div>
							</li>
						</c:if>
					</ul>
				</section>
			</div>
		</div>
	</c:if>
