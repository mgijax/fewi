	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="summaryRibbonLabel">
			Summary
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section id="summaryLeft" class="summarySec1 ">
				<ul>
					<li>
						<div class="label narrow">Strain Name</div>
						<div class="valueNarrow" id="strainName">
							<fewi:super value="${strain.name}"/> 
						</div>
					</li>

					<c:set var="isStrainFamily" value="false" />
					<c:if test="${not empty strain.strainAttributes}">
						<li>
							<div class="label narrow">Attributes</div>
							<div class="valueNarrow" id="strainAttributes">
								<c:forEach var="attribute" items="${strain.strainAttributes}" varStatus="status">
									<fewi:super value="${attribute.attribute}"/><c:if test="${!status.last}">, </c:if>
									<c:if test="${attribute.attribute == 'strain family'}">
										<c:set var="isStrainFamily" value="true" />
									</c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>

					<li>
						<div class="label narrow">MGI ID</div>
						<div class="valueNarrow" id="strainPrimaryID">${strain.primaryID}</div>
					</li>

					<c:if test="${not empty strain.strainSynonyms}">
						<li>
							<div class="label narrow">Synonyms</div>
							<div class="valueNarrow" id="strainSynonyms">
								<c:forEach var="synonym" items="${strain.strainSynonyms}" varStatus="status">
									<fewi:super value="${synonym.synonym}"/><c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>

					<c:if test="${(not empty strain.collectionStrings) and (strain.isFounder)}">
						<li>
							<div class="label narrow">Collection</div>
							<div class="valueNarrow" id="strainCollection">
								DO/CC Founder
							</div>
						</li>
					</c:if>
				</ul>
			</section>

			<section id="summaryRight" class="summarySec1 ">
				<ul>
					<c:if test="${not empty strain.mpdID}">
						<li title="Mouse Phenome Database">
							<div class="label">Phenomic Data</div>
							<div class="value">
								<a href="${fn:replace(externalUrls.MPD, '@@@@', strain.mpdID)}" target="_blank" id="mpdLink">Mouse Phenome Database (MPD)</a>
							</div>
						</li>
					</c:if>
					<c:if test="${strain.isSequenced == 1}">
						<li title="Multiple Genome Viewer">
							<div class="label">Mouse Genome Browsers</div>
							<div class="value">
								<a href="${configBean.MGV_URL}#ref=${strain.name}&genomes=${externalUrls.MGV_DOCCFounder_Strains}" target="_blank" id="mgvLink">Multiple Genome Viewer (MGV)</a>
							</div>
						</li>
					</c:if>
					<c:if test="${not empty strain.otherIDs}">
						<li>
							<div class="label">Other IDs</div>
							<div class="value" id="otherIDs">
								<c:forEach var="id" items="${strain.otherIDs}" varStatus="status">
									<c:set var="displayID" value="${id.accID}"/>
									<c:catch var="err">
										<fmt:parseNumber value="${displayID}" type="number" integerOnly="true" var="numericID" />
										<c:choose>
											<c:when test="${id.logicalDB == 'JAX Registry'}">
												<c:set var="displayID" value="JAX:${displayID}"/>
											</c:when>
											<c:otherwise>
												<c:set var="displayID" value="${id.logicalDB}:${displayID}"/>
											</c:otherwise>
										</c:choose>
									</c:catch>
									${displayID}<c:if test="${not status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>
					<c:if test="${(not empty relatedStrainCount) and isStrainFamily}">
						<li>
							<div class="label">Strain Family Members</div>
							<div class="value" id="relatedStrains">
								<a href="${configBean.FEWI_URL}strain/summary?strainName=${strain.name}*&attributes=inbred strain" id="relatedStrainLink" target="_blank">${relatedStrainCount}</a>
							</div>
						</li>
					</c:if>
				</ul>
			</section>
		</div>
	</div>

<script>
try {
	// if name or synonyms have wrapped to a second line -- and if they contain superscripts -- adjust line height
	// for readability
	var oneLineHeight = $('#strainPrimaryID')[0].getBoundingClientRect().height;
	var ids = [ '#strainName', '#strainSynonyms' ];
	for (var i = 0; i < ids.length; i++) {
		if ($(ids[i]).html().indexOf('<') >= 0) {
			if ($(ids[i])[0].getBoundingClientRect().height > oneLineHeight) {
				$(ids[i]).css({'line-height' :'1.8em'});
			}
		}
	}
} catch (err) {}
</script>
