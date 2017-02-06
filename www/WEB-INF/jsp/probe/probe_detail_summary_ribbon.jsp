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
							<div class="label">Sequence type</div>
							<div class="value">${probe.segmentType}</div>
						</li>
					</c:if>

					<li>
						<div class="label">MGI Accession ID</div>
						<div class="value">${probe.primaryID}</div>
					</li>

					<c:if test="${not empty probe.regionCovered}">
						<li>
							<div class="label">Region covered</div>
							<div class="value">${probe.regionCovered}</div>
						</li>
					</c:if>
					
					<!-- TODO: parent clone -->
					<!-- TODO: child clone -->
					
					<c:if test="${not empty probe.vector}">
						<li>
							<div class="label">Vector type</div>
							<div class="value">${probe.vector}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.insertSite}">
						<li>
							<div class="label">Insert site</div>
							<div class="value">${probe.insertSite}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.insertSize}">
						<li>
							<div class="label">Insert size</div>
							<div class="value">${probe.insertSize}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.secondaryIds}">
						<li>
							<div class="label">Other Accession IDs</div>
							<div class="value">
								<c:forEach var="secID" items="${probe.secondaryIds}">
									${secID.accID} (${secID.logicalDB})<br/>
								</c:forEach>
							</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.probeNote}">
						<li>
							<div class="label">Note</div>
							<div class="value">
								${probe.probeNote}
							</div>
						</li>
					</c:if>

					<c:if test="${not empty probe.synonyms}">
						<li>
							<div class="label">Synonyms</div>
							<div class="value">
								<c:forEach var="synonym" items="${probe.synonyms}">
									${synonym}<br/>
								</c:forEach>
							</div>
						</li>
					</c:if>
					
				</ul>
			</section>
		</div>
	</div>

