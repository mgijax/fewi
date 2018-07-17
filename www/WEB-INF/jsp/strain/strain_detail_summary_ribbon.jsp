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
								<span id="strainIsStandard"> (interim) </span>
							</c:if>
						</div>
					</li>

					<c:set var="isStrainFamily" value="false" />
					<c:if test="${not empty strain.strainAttributes}">
						<li>
							<div class="label">Attributes</div>
							<div class="value" id="strainAttributes">
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

			<section class="summarySec1 ">
				<ul>
					<c:if test="${not empty strain.mpdData}">
						<li title="Mouse Phenome Database">
							<div class="label">MPD</div>
							<div class="value">
								<a href="${fn:replace(externalUrls.MPD, '@@@@', strain.firstMpdData.mpdID)}" target="_blank" id="mpdLink">View Phenomic Data</a>
							</div>
						</li>
					</c:if>
					<c:if test="${strain.isSequenced == 1}">
						<li title="Multiple Genome Viewer">
							<div class="label">MGV</div>
							<div class="value">
								<a href="${externalUrls.MGV}#ref=${strain.name}&genomes=${externalUrls.MGV_Strains}" target="_blank" id="mgvLink">View Genome</a>
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

