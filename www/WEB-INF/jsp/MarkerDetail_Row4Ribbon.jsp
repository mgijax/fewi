	<c:set var="hasGeneticLocation" value="0"/>
	<c:if test="${(not empty marker.preferredCentimorgans) or (not empty marker.preferredCytoband) or (marker.countOfMappingExperiments > 0)}">
		<div class="row">
			<div class="header <%=leftTdStyles.getNext() %>">
				Genetic&nbsp;Map
			</div>
			<div class="detail <%=rightTdStyles.getNext() %>">
				<div>
					<c:if test="${not empty marker.preferredCentimorgans}">
						<c:if test="${marker.preferredCentimorgans.chromosome != 'UN'}">
							<c:set var="linkmapUrl" value="${configBean.WI_URL}searches/linkmap.cgi?chromosome=${marker.preferredCentimorgans.chromosome}&midpoint=${marker.preferredCentimorgans.cmOffset}&cmrange=1.0&dsegments=1&syntenics=0"/>
							<c:if test="${marker.preferredCentimorgans.cmOffset > 0.0}">
								<c:if test="${not empty miniMap}">
									<div style="float:right;text-align:left;"><a href="${linkmapUrl}"><img src="${miniMap}" border="0"></a></div>
								</c:if>
							</c:if>
							Chromosome ${marker.preferredCentimorgans.chromosome}<br/>
							<c:if test="${marker.preferredCentimorgans.cmOffset > 0.0}">
								<c:set var="hasGeneticLocation" value="1"/>
								<c:if test="${marker.markerType == 'QTL'}">
									cM position of peak correlated region/marker:
								</c:if>
								<fmt:formatNumber value="${marker.preferredCentimorgans.cmOffset}" minFractionDigits="2" maxFractionDigits="2"/> ${marker.preferredCentimorgans.mapUnits}<c:if test="${not empty marker.preferredCytoband}">, cytoband ${marker.preferredCytoband.cytogeneticOffset}</c:if>
								<br/>
								<a href="${linkmapUrl}">Detailed Genetic Map &#177; 1 cM</a><br/>
							</c:if>
							<c:if test="${marker.preferredCentimorgans.cmOffset == -1.0}">
								<c:if test="${marker.markerType == 'QTL'}">
									cM position of peak correlated region/marker:
								</c:if>
								Syntenic
							</c:if>
						</c:if>
						<c:if test="${marker.preferredCentimorgans.chromosome == 'UN'}">
							Chromosome Unknown
						</c:if>
						<br/>
					</c:if>
					<c:if test="${(empty marker.preferredCentimorgans) and (not empty marker.preferredCytoband)}">
						<c:if test="${marker.preferredCytoband.chromosome != 'UN'}">
							Chromosome ${marker.preferredCytoband.chromosome}<br/>
						</c:if>
						<c:if test="${marker.preferredCytoband.chromosome == 'UN'}">
							Chromosome Unknown<br/>
						</c:if>
						cytoband ${marker.preferredCytoband.cytogeneticOffset}<br/>
					</c:if>
					<c:if test="${not empty qtlIDs}">
						Download data from the QTL Archive:
						<c:forEach var="qtlID" items="${qtlIDs}" varStatus="status">
							${qtlID}<c:if test="${!status.last}">, </c:if>
						</c:forEach>
						<br/>
					</c:if>
					<c:if test="${marker.countOfMappingExperiments > 0}">
						<br/></>Mapping data(<a href="${configBean.WI_URL}searches/mapdata_report_by_marker.cgi?${marker.markerKey}">${marker.countOfMappingExperiments}</a>)
					</c:if>
				</div>
			</div>
		</div>
	</c:if>


