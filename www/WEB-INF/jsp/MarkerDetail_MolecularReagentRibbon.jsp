	<c:if test="${(not empty marker.molecularReagentCountsByType) || (marker.countOfMicroarrayProbesets > 0)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Molecular<br/>Reagents
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<div title="Show Less" class="toggleImage hdCollapse">less</div>

				<section class="summarySec1 wide extra open">
					<ul>
						<li>
							<div style="padding-left: 90px;">
								<c:set var="reagentUrl" value="${configBean.WI_URL}searches/probe_report.cgi?_Marker_key=${marker.markerKey}"/>
								<c:forEach var="item" items="${marker.molecularReagentCountsByType}">
									<c:set var="reagentType" value="&DNAtypes=${item.countType}"/>
									<c:if test="${fn:startsWith(item.countType, 'Primer')}">
										<c:set var="reagentType" value="&DNAtypes=primer"/>
									</c:if>
									<c:if test="${item.countType == 'Other'}">
										<c:set var="reagentType" value="&notDNAtypes=genomic,primer,cDNA"/>
									</c:if>
									<c:if test="${fn:startsWith(item.countType, 'All')}">
										<c:set var="reagentType" value=""/>
									</c:if>
									<div style="float: left; padding-right: 25px;">${item.countType} <a href="${reagentUrl}${reagentType}">${item.count}</a></div>
								</c:forEach>
								<c:if test="${marker.countOfMicroarrayProbesets > 0}">
									<br/>
									<div style="float: left; padding-right: 25px;">Microarray probesets <a href="${configBean.FEWI_URL}marker/probeset/${marker.primaryID}">${marker.countOfMicroarrayProbesets}</a></div>
								</c:if>
							</div>
						</li>
					</ul>
				</section>
			</div>
		</div>
	</c:if>

