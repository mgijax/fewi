	<c:if test="${not empty marker.references}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>" >
				References
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>" >
			<style>
				div.refPad { display: inline-block; padding-right: 50px; }
			</style>

				<table>
					<tr>
						<td class="top">
							<span id="toggleReferenceRibbon" title="Show More" class="toggleImage hdExpand" onclick="toggleRibbon('ReferenceRibbon'); return false;"></span>
						</td>
						<td>
							<div id="closedReferenceRibbon" style="display:block;">
								<table border="none">
									<c:if test="${marker.countOfReferences > 0}">
										<tr>
											<td class='rightBorderThinGray label padded top right'><font class="label">Summaries</font></td>
											<td class='padded'><div class="refPad">All <a href="${configBean.FEWI_URL}reference/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfReferences}</a></div>
												<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
													<div class="refPad">Diseases <a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}?typeFilter=Literature">${diseaseRefCount}</a></div>
												</c:if>
												<c:if test="${marker.countOfGxdLiterature > 0}">
													<div class="refPad">Expression <a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a></div>
												</c:if>
												<c:if test="${marker.countOfGOReferences > 0}">
													<div class="refPad">Gene Ontology <a href="${configBean.FEWI_URL}reference/go/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfGOReferences}</a></div>
												</c:if>
												<c:if test="${marker.countOfPhenotypeReferences > 0}">
													<div class="refPad">Phenotypes <a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfPhenotypeReferences}</a></div>
												</c:if>
											</td>
										</tr>
									</c:if>
								</table>
							</div>
							<div id="openedReferenceRibbon" style="display:none;">
								<table border="none">
									<c:if test="${marker.countOfReferences > 0}">
										<tr>
											<td class='rightBorderThinGray label padded top right'><font class="label">Summaries</font></td>
											<td class='padded'><div class="refPad">All <a href="${configBean.FEWI_URL}reference/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfReferences}</a></div>
												<c:if test="${not empty diseaseRefCount && diseaseRefCount > 0}">
													<div class="refPad">Diseases <a href="${configBean.FEWI_URL}reference/diseaseRelevantMarker/${marker.primaryID}?typeFilter=Literature">${diseaseRefCount}</a></div>
												</c:if>
												<c:if test="${marker.countOfGxdLiterature > 0}">
													<div class="refPad">Expression <a href="${configBean.FEWI_URL}gxdlit/marker/${marker.primaryID}">${marker.countOfGxdLiterature}</a></div>
												</c:if>
												<c:if test="${marker.countOfGOReferences > 0}">
													<div class="refPad">Gene Ontology <a href="${configBean.FEWI_URL}reference/go/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfGOReferences}</a></div>
												</c:if>
												<c:if test="${marker.countOfPhenotypeReferences > 0}">
													<div class="refPad">Phenotypes <a href="${configBean.FEWI_URL}reference/phenotype/marker/${marker.primaryID}?typeFilter=Literature">${marker.countOfPhenotypeReferences}</a></div>
												</c:if>
											</td>
										</tr>
									</c:if>
									<c:set var="earliestRef" value="${marker.earliestReference}"/>
									<c:if test="${not empty earliestRef}">
										<tr>
											<td class='rightBorderThinGray label padded top right'><font class="label">Earliest</font></td>
											<td class='padded'><a href="${configBean.FEWI_URL}reference/${earliestRef.jnumID}">${earliestRef.jnumID}</a> ${earliestRef.shortCitation}</td>
										</tr>
									</c:if>
									<c:set var="latestRef" value="${marker.latestReference}"/>
									<c:if test="${not empty latestRef}">
										<tr>
											<td class='rightBorderThinGray label padded top right'><font class="label">Latest</font></td>
											<td class='padded'><a href="${configBean.FEWI_URL}reference/${latestRef.jnumID}">${latestRef.jnumID}</a> ${latestRef.shortCitation}</td>
										</tr>
									</c:if>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</c:if>
