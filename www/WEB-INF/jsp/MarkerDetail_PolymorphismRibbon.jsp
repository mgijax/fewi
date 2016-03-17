	<c:if test="${not empty marker.polymorphismCountsByType}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Polymorphisms
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<div title="Show Less" class="toggleImage hdCollapse">less</div>

				<c:set var="snpsfound" value="false"/>
				<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
					<c:if test="${(fn:startsWith(item.countType, 'SNP')) and (item.count > 0)}">
						<c:set var="snpsfound" value="true"/>
					</c:if>
				</c:forEach>

				<c:if test="${snpsfound}">
					<section class="summarySec1 extra">
						<ul style="display: table-cell;">
							<c:forEach var="item" items="${marker.polymorphismCountsByType}" varStatus="status">
								<c:if test="${(fn:startsWith(item.countType, 'SNP')) and (item.count > 0)}">
									<li>
									<c:set var="polyUrl" value="${configBean.FEWI_URL}snp/marker/${marker.primaryID}"/>
										<div class="label" style="white-space: normal;">${item.countType}</div>
										<div class="value"><a href="${polyUrl}">${item.count}</a>
											<c:if test="${not empty snpBuildNumber}"><span style="font-size: smaller; font-weight: normal;">from ${snpBuildNumber}</span></c:if>
										</div>
									</li>
								</c:if>
							</c:forEach>
						</ul>
					</section>
				</c:if>
				
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


