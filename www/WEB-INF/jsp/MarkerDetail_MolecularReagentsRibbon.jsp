	<c:if test="${(not empty marker.molecularReagentCountsByType) || (marker.countOfMicroarrayProbesets > 0)}">
		<div class="row" >
			<div class="header <%=leftTdStyles.getNext() %>">
				Molecular<br/>reagents
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
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
					${item.countType}(<a href="${reagentUrl}${reagentType}">${item.count}</a>)
				</c:forEach>
				<br/>
				<c:if test="${marker.countOfMicroarrayProbesets > 0}">
					Microarray probesets(<a href="${configBean.FEWI_URL}marker/probeset/${marker.primaryID}">${marker.countOfMicroarrayProbesets}</a>)
				</c:if>
			</div>
		</div>
	</c:if>


