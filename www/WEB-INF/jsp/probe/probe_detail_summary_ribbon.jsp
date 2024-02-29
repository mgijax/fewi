	<c:set var="suppressedLogicalDatabases" value="MGI"/>
	<c:set var="oldLogicalDatabases" value="ATCC, ATCC home page, WashU, BROAD, NIA, NIA 15K, NIA 7.4K, RIKEN, IMAGE, MGC"/>
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			<c:choose>
				<c:when test="${highLevelSegmentType == 'Probe'}">Nucleotide<br/>Probe/Clone</c:when>
				<c:otherwise>Primers</c:otherwise>
			</c:choose>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<ul>
					<li>
						<div class="label">Name</div>
						<div class="value">
							<fewi:super value="${probe.name}"/>
						</div>
					</li>

					<c:if test="${highLevelSegmentType == 'Probe'}">
						<li>
							<div class="label">Sequence Type</div>
							<div class="value">${probe.segmentType}</div>
						</li>
					</c:if>

					<c:if test="${not empty probe.primerSequence1}">
						<li>
							<div class="label">Primer 1 Sequence</div>
							<div class="value">${probe.primerSequence1}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.primerSequence2}">
						<li>
							<div class="label">Primer 2 Sequence</div>
							<div class="value">${probe.primerSequence2}</div>
						</li>
					</c:if>
					
					<li>
						<div class="label">ID</div>
						<div class="value">${probe.primaryID}</div>
					</li>

					<c:if test="${not empty probe.regionCovered}">
						<li>
							<div class="label">Region Covered</div>
							<div class="value"> 
                                                        <fewi:super value="${probe.regionCovered}"/>
                                                        </div>
						</li>
					</c:if>
					
					<c:set var="parentProbe" value="${probe.parentProbe}"/>
					<c:if test="${not empty parentProbe}">
						<li>
							<div class="label">Parent Clone</div>
							<div class="value"><a href="${configBean.FEWI_URL}probe/${parentProbe.relatedProbeID}">${parentProbe.relatedProbeName}</a></div>
						</li>
					</c:if>
					
					<c:set var="childProbes" value="${probe.childProbes}"/>
					<c:if test="${not empty childProbes}">
						<li>
							<div class="label">Child Clone<c:if test="${fn:length(childProbes) > 1}">s</c:if></div>
							<div class="value">
								<c:forEach var="childProbe" items="${childProbes}" varStatus="status">
									<a href="${configBean.FEWI_URL}probe/${childProbe.relatedProbeID}">${childProbe.relatedProbeName}</a><c:if test="${!status.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>
					
					<c:if test="${not empty showVector}">
						<li>
							<div class="label">Vector Type</div>
							<div class="value">${probe.vector}</div>
						</li>
					</c:if>

                                        <c:if test="${not empty probe.ampPrimer}">
						<li>
							<div class="label">Amplification Primers</div>
							<div class="value">
                                                            <a href="${configBean.FEWI_URL}probe/${probe.ampPrimer.primaryID}">${probe.ampPrimer.primaryID}</a>
                                                        </div>
						</li>
                                        </c:if>

					
					<c:if test="${not empty probe.insertSite}">
						<li>
							<div class="label">Insert Site</div>
							<div class="value">${probe.insertSite}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.insertSize}">
						<li>
							<div class="label">Insert Size</div>
							<div class="value">${probe.insertSize}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.productSize}">
						<li>
							<div class="label">Product Size</div>
							<div class="value">${probe.productSize}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.secondaryIds}">
						<li>
							<div class="label">Other IDs</div>
							<div class="value">
								<c:set var="hasBroad" value="no"/>
								<c:forEach var="secID" items="${probe.secondaryIds}">
									${secID.accID}
									<c:if test="${secID.logicalDB == 'BROAD'}">
										<c:set var="hasBroad" value="yes"/>
									</c:if>
									<c:choose>
										<c:when test="${fn:indexOf(suppressedLogicalDatabases, secID.logicalDB) >= 0}">
											<br/>
										</c:when>
										<c:when test="${fn:indexOf(oldLogicalDatabases, secID.logicalDB) >= 0}">
											(${secID.logicalDB})<br/>
										</c:when>
										<c:otherwise>
											(${fn:replace(idLinker.getFirstLink(secID), " home page", "")})<br/>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.probeNote}">
						<li>
							<div class="label">Note</div>
							<div class="value">
								${probe.probeNote}
								<c:if test="${hasBroad == 'yes'}"><br>Additional information: <a href="${configBean.FEWI_URL}downloads/datasets/index.html#mit">MIT STS Marker Data Files</a></c:if>
							</div>
						</li>
					</c:if>

					<c:if test="${not empty probe.synonyms}">
						<li>
							<div class="label">Synonyms</div>
							<div class="value">
								<c:forEach var="synonym" items="${probe.synonyms}" varStatus="sStatus">
									${synonym}<c:if test="${!sStatus.last}">, </c:if>
								</c:forEach>
							</div>
						</li>
					</c:if>
					
				</ul>
			</section>
		</div>
	</div>

