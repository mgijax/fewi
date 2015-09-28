	<c:if test="${not empty marker.polymorphismCountsByType}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Polymorphisms
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<div id="togglePolymorphismRibbon" title="Show More" class="toggleImage hdCollapse"></div>
				<section class="summarySec1 extra">
					<ul>
						<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
							<c:if test="${fn:startsWith(item.countType, 'SNP')}">
								<li>
									<c:set var="polyUrl" value="${configBean.WI_URL}searches/snp_report.cgi?_Marker_key=${marker.markerKey}"/>
									<c:if test="${fn:contains(item.countType, 'multiple')}">
										<c:set var="polyUrl" value="${polyUrl}&includeMultiples=1"/>
									</c:if>
									<div class="label" style="white-space: normal;">${item.countType}
										<c:if test="${not empty configBean.SNP_BUILD}"><br/><span style="font-size: smaller; font-weight: normal;">from ${configBean.SNP_BUILD}</span></c:if>
									</div>
									<div class="value"><a href="${polyUrl}">${item.count}</a></div>
								</li>
							</c:if>

						</c:forEach>
					</ul>
				</section>

				
				<section class="summarySec2 extra">
					<ul>
						<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
							<c:if test="${(item.countType == 'PCR') or (item.countType == 'RFLP')}">
								<li>
									<c:set var="polyUrl" value="${configBean.WI_URL}searches/polymorphism_report.cgi?_Marker_key=${marker.markerKey}"/>
									<c:set var="polyUrl" value="${polyUrl}&search=${item.countType}"/>
									<div class="label" style="white-space: normal;">${item.countType}</div>
									<div class="value"><a href="${polyUrl}">${item.count}</a></div>
								</li>
							</c:if>
						</c:forEach>
					</ul>
				</section>
			</div>
		</div>
	</c:if>


