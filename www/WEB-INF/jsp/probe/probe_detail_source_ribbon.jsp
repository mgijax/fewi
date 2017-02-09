<c:if test="${probe.segmentType != 'primer'}">
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			Source
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> sourceRibbon">
			<section class="summarySec1 ">
				<ul>
					<c:if test="${not empty probe.library}">
						<li>
							<div class="label">Library</div>
							<div class="value">${probe.library}
								<c:if test="${not empty probe.libraryJnum}">
								(<a href="${configBean.FEWI_URL}reference/${probe.libraryJnum}">${probe.libraryJnum}</a>)
								</c:if>
							</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.organism}">
						<li>
							<div class="label">Species</div>
							<div class="value">${probe.organism}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty showStrain}">
						<li>
							<div class="label">Strain</div>
							<div class="value">${probe.strain}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty showAge}">
						<li>
							<div class="label">Age</div>
							<div class="value">${probe.age}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty showSex}">
						<li>
							<div class="label">Sex</div>
							<div class="value">${probe.sex}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty showTissue}">
						<li>
							<div class="label">Tissue</div>
							<div class="value">${probe.tissue}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.tissueNote}">
						<li>
							<div class="label">Tissue Description</div>
							<div class="value">${probe.tissueNote}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty showCellLine}">
						<li>
							<div class="label">Cell Line</div>
							<div class="value">${probe.cellLine}</div>
						</li>
					</c:if>
				</ul>
			</section>
		</div>
	</div>
</c:if>