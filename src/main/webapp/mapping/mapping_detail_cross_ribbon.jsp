<c:if test="${not empty experiment.cross}">
	<c:set var="cross" value="${experiment.cross}"/>
	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>">
			${experiment.type}
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> detailsRibbon">
			<section class="summarySec1 ">
				<ul>
				<c:if test="${not empty cross.crossType}">
					<li>
						<div class="label">Type</div>
						<div class="value">
							${cross.crossType}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty cross.femaleParent}">
					<li>
						<div class="label">Female Parent</div>
						<div class="value">
							<fewi:verbatim value="${cross.femaleParent}"/>
						</div>
					</li>
				</c:if>
				<c:if test="${not empty cross.femaleStrain}">
					<li>
						<div class="label">Strain</div>
						<div class="value">
							${cross.femaleStrain}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty cross.maleParent}">
					<li>
						<div class="label">Male Parent</div>
						<div class="value">
							<fewi:verbatim value="${cross.maleParent}"/>
						</div>
					</li>
				</c:if>
				<c:if test="${not empty cross.maleStrain}">
					<li>
						<div class="label">Strain</div>
						<div class="value">
							${cross.maleStrain}
						</div>
					</li>
				</c:if>
				<c:if test="${not empty cross.panelName}">
					<li>
						<div class="label">Mapping Panel</div>
						<div class="value">
							<c:choose>
								<c:when test="${not empty cross.panelFilename}">
									<a href="${configBean.PUB_REPORTS_URL}${cross.panelFilename}" target="_blank">${cross.panelName}</a>
								</c:when>
								<c:otherwise>
									${cross.panelName}
								</c:otherwise>
							</c:choose>
						</div>
					</li>
				</c:if>
				<c:if test="${(not empty cross.homozygousAllele) and (not empty cross.homozygousStrain)}">
					<li>
						<div class="label">Allele 1</div>
						<div class="value">
							<b>${cross.homozygousAllele}</b> from <b>${cross.homozygousStrain}</b>
						</div>
					</li>
				</c:if>
				<c:if test="${(not empty cross.heterozygousAllele) and (not empty cross.heterozygousStrain)}">
					<li>
						<div class="label">Allele 2</div>
						<div class="value">
							<b>${cross.heterozygousAllele}</b> from <b>${cross.heterozygousStrain}</b>
						</div>
					</li>
				</c:if>
				</ul>

				<!-- CROSS data matrix -->
				<c:set var="experimentTable" value="${experiment.crossMatrix}"/>
				<c:set var="experimentTableName" value="CROSS Data"/>
				<c:set var="superscriptLabels" value="false"/>
				<%@ include file="mapping_detail_table.jsp" %>
				<c:set var="superscriptLabels" value="true"/>
				
				<!-- CROSS 2x2 data -->
				<c:set var="experimentTable" value="${experiment.cross2x2}"/>
				<!-- title needs to be customized -->
				<c:choose>
					<c:when test="${empty experiment.crossMatrix}">
						<c:set var="experimentTableName" value="2x2 Data Reported"/>
					</c:when>
					<c:otherwise>
						<c:set var="experimentTableName" value="Additional Mice Typed Only for Specific Pairs of Loci"/>
					</c:otherwise>
				</c:choose>
				<%@ include file="mapping_detail_table.jsp" %>
				
				<!-- CROSS statistics -->
				<c:set var="experimentTable" value="${experiment.crossStatistics}"/>
				<c:set var="experimentTableName" value="Statistics"/>
				<%@ include file="mapping_detail_table.jsp" %>
			</section>
		</div>
	</div>
</c:if>