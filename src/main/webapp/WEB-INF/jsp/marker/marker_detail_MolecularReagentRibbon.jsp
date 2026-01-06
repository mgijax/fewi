	<c:if test="${(not empty marker.molecularReagentCountsByType) || (marker.countOfMicroarrayProbesets > 0)}">
		<div class="row" id="molecularReagentsRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">
				Molecular<br/>Reagents
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<div id="mrToggle" title="Show Less" class="toggleImage hdCollapse">less</div>

				<section class="summarySec1 wide extra open">
					<ul>
						<li>
							<div style="padding-left: 90px;">
								<c:set var="reagentUrl" value="${configBean.FEWI_URL}probe/marker/${marker.primaryID}"/>
								<c:forEach var="item" items="${marker.molecularReagentCountsByType}">
									<c:set var="reagentType" value="?segmentType=${item.countType}"/>
									<c:choose>
										<c:when test="${fn:startsWith(item.countType, 'Other')}">	
											<c:set var="reagentType" value="?segmentType=other"/>
										</c:when>
										<c:when test="${fn:startsWith(item.countType, 'Genomic')}">	
											<c:set var="reagentType" value="?segmentType=genomic"/>
										</c:when>
										<c:when test="${fn:startsWith(item.countType, 'Primer')}">	
											<c:set var="reagentType" value="?segmentType=primer"/>
										</c:when>
										<c:when test="${fn:startsWith(item.countType, 'All')}">
											<c:set var="reagentType" value=""/>
										</c:when>
									</c:choose>
									<div style="float: left; padding-right: 25px;">${item.countType} <a href="${reagentUrl}${reagentType}" id="${fn:replace(item.countType, ' ', '')}Link">${item.count}</a></div>
								</c:forEach>
								<c:if test="${marker.countOfAntibodies > 0}">
									<div style="float: left; padding-right: 25px;">Antibodies <a href="${configBean.FEWI_URL}antibody/marker/${marker.primaryID}" id="antibodyLink">${marker.countOfAntibodies}</a></div>
								</c:if>
								<c:if test="${marker.countOfMicroarrayProbesets > 0}">
									<br/>
									<div style="float: left; padding-right: 25px;">Microarray probesets <a href="${configBean.FEWI_URL}marker/probeset/${marker.primaryID}" id="microarrayLink">${marker.countOfMicroarrayProbesets}</a></div>
								</c:if>
							</div>
						</li>
					</ul>
				</section>
			</div>
		</div>
	</c:if>

