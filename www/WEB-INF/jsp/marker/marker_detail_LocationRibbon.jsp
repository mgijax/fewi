	<div class="row locationRibbon" id="locationRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Location &amp;<br/>Maps
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">

			<c:set var="showJBrowser" value="${not empty marker.preferredCoordinates or not empty jbrowseUrl}" />
			<c:set var="showGenomeBrowserLinks" value="${not (empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty ncbiMapViewerUrl)}" />

			<c:set var="showGeneticMap" value="${(not empty marker.preferredCentimorgans) or (not empty marker.preferredCytoband) or (marker.countOfMappingExperiments > 0) or (not empty qtlIDs) or (not empty marker.aliases)}" />
			
			<c:set var="geneticMapExtra" value="${not empty qtlIDs or marker.countOfMappingExperiments > 0 or not empty marker.aliases}" />
			<c:set var="showLocationNote" value="${not empty marker.locationNote}" />

			<c:set var="arrowstate" value="hdExpand" />
			<c:set var="extrastate" value="" />
			<c:set var="arrowtext" value="more" />
			<c:set var="titletext" value="Show More" />
			<c:if test="${not (showLocationNote or showJBrowser or showGenomeBrowserLinks or geneticMapExtra)}">
				<c:set var="arrowstate" value="hdCollapse" />
				<c:set var="extrastate" value="extra" />
				<c:set var="arrowtext" value="less" />
				<c:set var="titletext" value="Show Less" />
			</c:if>

			<div id="lmToggle" title="${titletext}" class="toggleImage ${arrowstate}">${arrowtext}</div>

			<section class="summarySec1">
				<ul>
					<li class="${extrastate}">
						<div class="label">
							Sequence Map
						</div>
						<div class="value">
							<c:if test="${not (empty marker.preferredCoordinates and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
								<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
								<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
								<c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
								<c:set var="coord1" value="${fn:replace('Chr<chr>:<start>-<end>', '<chr>', chromosome)}"/>
								<c:set var="coord2" value="${fn:replace(coord1, '<start>', startCoord)}"/>
								<c:set var="coords" value="${fn:replace(coord2, '<end>', endCoord)}"/>

								<c:if test="${not empty marker.preferredCoordinates}">
									<div style="padding-bottom: 3px">
									Chr${chromosome}:${startCoord}-${endCoord} ${marker.preferredCoordinates.mapUnits}<c:if test="${not empty marker.preferredCoordinates.strand}">, ${marker.preferredCoordinates.strand} strand</c:if></div>
									From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier}<br />
								</c:if>
							</c:if>

							<c:if test="${(empty marker.preferredCoordinates and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
								<span style="font-style: italic;font-size: smaller;">Genome coordinates not available from the current reference assembly.</span>
							</c:if>
						</div>
					</li>

					<c:if test="${showJBrowser}">
						<li class="extra closed">
							<div class="value">

								<c:if test="${not empty jbrowseUrl}">
									<table>
										<tbody>
											<tr>
												<td align="center">
													View this region in <a href="${jbrowseUrl}">JBrowse</a><br />
													<c:if test="${not empty gbrowseThumbnailUrl}">
														<a href="${jbrowseUrl}"><img border="0" src="${gbrowseThumbnailUrl}" style="padding-top: 4px"/></a> <br/>
													</c:if>
												</td>
											</tr>
										</tbody>
									</table>
								</c:if>

							</div>
						</li>
					</c:if>

					<c:if test="${showGenomeBrowserLinks}">
						<c:set var="ensemblID" value="${marker.ensemblGeneModelID.accID}"/>
						<c:set var="ncbiID" value="${marker.ncbiGeneModelID.accID}"/>
						<c:set var="foundOne" value="0"/>
						<li class="extra closed">
							<div class="label">
								Genome Browsers
							</div>
							<div class="value">
								<c:if test="${not empty ensemblGenomeBrowserUrl}">
									<c:if test="${foundOne > 0}"> | </c:if>
									<a href="${ensemblGenomeBrowserUrl}" target="_new">Ensembl</a>
									<c:set var="foundOne" value="1"/>
								</c:if>
								<c:if test="${not empty ucscGenomeBrowserUrl}">
									<c:if test="${foundOne > 0}"> | </c:if>
									<a href="${ucscGenomeBrowserUrl}" target="_new">UCSC</a>
									<c:set var="foundOne" value="1"/>
								</c:if>
								<c:if test="${not empty ncbiMapViewerUrl}">
									<c:if test="${foundOne > 0}"> | </c:if>
									<a href="${ncbiMapViewerUrl}" target="_new">NCBI</a>
								</c:if>
							</div>
						</li>
					</c:if>

					<c:if test="${showLocationNote}">
						<li class="extra closed">
							<div class="value" style="font-size: smaller; margin-left: 20.2em;">
								${marker.locationNote}
							</div>
						</li>
					</c:if>
				</ul>
			</section>

			<c:if test="${showGeneticMap}">
				<section class="summarySec2">
					<ul>
						<li class="${extrastate}">
							<div class="label">Genetic Map</div>
							<div class="value">
							    ${markerDetail.geneticMapLocation}
							</div>
						</li>
					
						<c:if test="${not empty qtlIDs}">
							<li class="extra closed">
								<div class="label">QTL Archive</div>
								<div class="value">
									<c:forEach var="qtlID" items="${qtlIDs}" varStatus="status">
										${qtlID} download<c:if test="${!status.last}">, </c:if>
									</c:forEach>
								</div>
							</li>
						</c:if>

						<c:if test="${marker.countOfMappingExperiments > 0}">
							<li class="extra closed">
								<div class="label">Mapping Data</div>
								<div class="value">
									<a href="${configBean.FEWI_URL}mapping/marker/${marker.primaryID}" id="mappingLink">${marker.countOfMappingExperiments}</a> experiment<c:if test="${marker.countOfMappingExperiments > 1}">s</c:if>
								</div>
							</li>
						</c:if>


						<c:if test="${not empty marker.aliases}">
							<li class="extra closed">
								<div class="label">
									<c:if test="${marker.markerType == 'Gene'}">Sequence Tag<c:if test="${fn:length(marker.aliases) > 1}">s</c:if></c:if>
									<c:if test="${marker.markerType != 'Gene'}">Sequence Tag for</c:if>
								</div>
								<div class="value">
									<c:forEach var="alias" items="${marker.aliases}" varStatus="status">
										<a href="${configBean.FEWI_URL}marker/${alias.aliasID}">${alias.aliasSymbol}</a><c:if test="${!status.last}">, </c:if>
									</c:forEach>
								</div>
							</li>
						</c:if>

					</ul>
				</section>
			</c:if>
		</div>
	</div>
