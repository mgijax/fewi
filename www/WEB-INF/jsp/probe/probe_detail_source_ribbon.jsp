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
							<div class="value">${probe.library}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.organism}">
						<li>
							<div class="label">Species</div>
							<div class="value">${probe.organism}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.strain}">
						<li>
							<div class="label">Strain</div>
							<div class="value">${probe.strain}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.age}">
						<li>
							<div class="label">Age</div>
							<div class="value">${probe.age}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.sex}">
						<li>
							<div class="label">Sex</div>
							<div class="value">${probe.sex}</div>
						</li>
					</c:if>
					
					<c:if test="${not empty probe.tissue}">
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
					
					<c:if test="${not empty probe.cellLine}">
						<li>
							<div class="label">Cell line</div>
							<div class="value">${probe.cellLine}</div>
						</li>
					</c:if>
				</ul>
			</section>
		</div>
	</div>

