	<div class="row locationRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			<header>Location & Maps</header>
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">

			<section class="summarySec1">
				<ul>
					<li>
						<div class="label">
							<div id="toggleLocationRibbon" title="Show More" class="toggleImage hdExpand"></div>
							Sequence map
						</div>
						<div class="value">
							<c:if test="${not (empty marker.preferredCoordinates and empty vegaGenomeBrowserUrl and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
								<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate}" pattern="#0" var="startCoord"/>
								<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate}" pattern="#0" var="endCoord"/>
								<c:set var="chromosome" value="${marker.preferredCoordinates.chromosome}"/>
								<c:set var="coord1" value="${fn:replace('Chr<chr>:<start>-<end>', '<chr>', chromosome)}"/>
								<c:set var="coord2" value="${fn:replace(coord1, '<start>', startCoord)}"/>
								<c:set var="coords" value="${fn:replace(coord2, '<end>', endCoord)}"/>

								<c:if test="${not empty marker.preferredCoordinates}">
									Chr${chromosome}:${startCoord}-${endCoord}
									${marker.preferredCoordinates.mapUnits}<c:if test="${not empty marker.preferredCoordinates.strand}">, ${marker.preferredCoordinates.strand} strand</c:if>
								</c:if>
							</c:if>

							<c:if test="${(empty marker.preferredCoordinates and empty vegaGenomeBrowserUrl and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
								<span style="font-style: italic;font-size: smaller;">Genome coordinates not available</span>
							</c:if>
						</div>
					</li>
				</ul>

				<div class="extra closed">

					<section class="summarySec1">
						<ul>
							<li>
								<div class="value">

									<c:if test="${not empty marker.preferredCoordinates}">
										From ${marker.preferredCoordinates.provider} annotation of ${marker.preferredCoordinates.buildIdentifier}<br />
									</c:if>
									<p />

									<c:if test="${not empty jbrowseUrl}">
										<table>
											<tbody>
												<tr>
													<td align="center">
														<a href="${jbrowseUrl}">Mouse Genome Browser</a><br />
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

							<c:if test="${not empty marker.preferredCoordinates}">
								<li>
									<div class="label">
										Download<br/>Sequence
									</div>
									<div class="value">
										<form name="markerCoordForm" method="GET" action="${configBean.SEQFETCH_URL}">
											<c:set var="length" value="${marker.preferredCoordinates.endCoordinate - marker.preferredCoordinates.startCoordinate + 1}"/>
											<c:set var="seqfetchParms" value="mousegenome!!${marker.preferredCoordinates.chromosome}!${startCoord}!${endCoord}!!"/>

											<!-- handle end < start, which is very atypical -->
											<c:if test="${length < 0}">
												<c:set var="length" value="${marker.preferredCoordinates.startCoordinate - marker.preferredCoordinates.endCoordinate + 1}"/>
												<c:set var="seqfetchParms" value="mousegenome!!${marker.preferredCoordinates.chromosome}!${endCoord}!${startCoord}!!"/>
											</c:if>

											<fmt:formatNumber value="${length}" pattern="#0" var="lengthStr"/>

											<input type="hidden" name="seq1" value="${seqfetchParms}">
											<input type="button" value="Get FASTA" onClick="formatFastaArgs()">
											&nbsp;&nbsp;${lengthStr} bp
											&nbsp;&nbsp;&#177; <input type="text" size="3" name="flank1" value="0">&nbsp;kb&nbsp;flank
										</form>
									</div>
									<br />
								</li>
							</c:if>

							<c:if test="${not (empty vegaGenomeBrowserUrl and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty ncbiMapViewerUrl)}">
								<c:set var="vegaID" value="${marker.vegaGeneModelID.accID}"/>
								<c:set var="ensemblID" value="${marker.ensemblGeneModelID.accID}"/>
								<c:set var="ncbiID" value="${marker.ncbiGeneModelID.accID}"/>
								<c:set var="foundOne" value="0"/>
								<li>
									<div class="label">
										Genome<br/>Browser Links
									</div>
									<div class="value">
										<c:if test="${not empty vegaGenomeBrowserUrl}">
											<a href="${vegaGenomeBrowserUrl}" target="_new">VEGA</a>
											<c:set var="foundOne" value="1"/>
										</c:if>
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

						</ul>
					</section>

				</div>
			</section>

			<c:if test="${(not empty marker.preferredCentimorgans) or (not empty marker.preferredCytoband) or (marker.countOfMappingExperiments > 0) or (not empty qtlIDs) or (not empty marker.aliases)}">
				<section class="summarySec2">
					<ul>
						<li>
							<div class="label">Genetic Map</div>
							<div class="value" style="height: 45px;">
								<c:if test="${(not empty marker.preferredCentimorgans) or (not empty marker.preferredCytoband) or (marker.countOfMappingExperiments > 0)}">
									<c:if test="${not empty marker.preferredCentimorgans}">
										<c:if test="${marker.preferredCentimorgans.chromosome != 'UN'}">
											<c:set var="linkmapUrl" value="${configBean.WI_URL}searches/linkmap.cgi?chromosome=${marker.preferredCentimorgans.chromosome}&midpoint=${marker.preferredCentimorgans.cmOffset}&cmrange=1.0&dsegments=1&syntenics=0"/>
											Chromosome ${marker.preferredCentimorgans.chromosome},
											<c:if test="${marker.preferredCentimorgans.cmOffset > 0.0}">
												<fmt:formatNumber value="${marker.preferredCentimorgans.cmOffset}" minFractionDigits="2" maxFractionDigits="2"/> ${marker.preferredCentimorgans.mapUnits}<c:if test="${not empty marker.preferredCytoband}">, cytoband ${marker.preferredCytoband.cytogeneticOffset}</c:if>
												<c:if test="${marker.markerType == 'QTL'}">
													<span style="font-style: italic;font-size: smaller;">(cM position of peak correlated region/marker)</span>
												</c:if>
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
									</c:if>
									<c:if test="${(empty marker.preferredCentimorgans) and (not empty marker.preferredCytoband)}">
										<c:if test="${marker.preferredCytoband.chromosome != 'UN'}">
											Chromosome ${marker.preferredCytoband.chromosome}<c:if test="${not empty marker.preferredCytoband}">, cytoband ${marker.preferredCytoband.cytogeneticOffset}</c:if><br/>
										</c:if>
										<c:if test="${marker.preferredCytoband.chromosome == 'UN'}">
											Chromosome Unknown<c:if test="${not empty marker.preferredCytoband}">, cytoband ${marker.preferredCytoband.cytogeneticOffset}</c:if><br/>
										</c:if>
									</c:if>
								</c:if>
							</div>
						</li>
					</ul>
					
					<div class="extra closed">

						<section class="summarySec1">
							<ul>
							<c:if test="${not empty linkmapUrl}">
								<li>
									<div class="value">
										<a href="${linkmapUrl}">Detailed Genetic Map &#177; 1 cM</a>
										<c:if test="${not empty miniMap}">
											<br/><a href="${linkmapUrl}" style="background-color: transparent"><img src="${miniMap}" border="0" style="padding-top: 4px"></a>
										</c:if>
									</div>
								</li>
							</c:if>

							<c:if test="${not empty qtlIDs}">
								<li>
									<div class="label">QTL Archive</div>
									<div class="value">
										<c:forEach var="qtlID" items="${qtlIDs}" varStatus="status">
											${qtlID} download<c:if test="${!status.last}">, </c:if>
										</c:forEach>
									</div>
								</li>
							</c:if>

							<c:if test="${marker.countOfMappingExperiments > 0}">
								<li>
									<div class="label">Mapping Data</div>
									<div class="value">
										<a href="${configBean.WI_URL}searches/mapdata_report_by_marker.cgi?${marker.markerKey}">${marker.countOfMappingExperiments}</a> experiment<c:if test="${marker.countOfMappingExperiments > 1}">s</c:if>
									</div>
								</li>
							</c:if>


							<c:if test="${not empty marker.aliases}">
								<li>
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
					</div>
				</section>
			</c:if>
		</div>
	</div>
