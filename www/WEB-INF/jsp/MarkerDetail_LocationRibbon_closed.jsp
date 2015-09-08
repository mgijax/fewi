							<table>
								<tr>
									<td style="vertical-align: top;">
										<table>
											<tr>
												<td class="rightBorderThinGray label padded top right">
													<font class="label">Sequence Map</font>
												</td>
												<td class="padded">
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
												</td>
											</tr>
										</table>
									</td>
									<td style="vertical-align: top;padding-left: 45px;">
										<table>
											<tr>
												<td class="rightBorderThinGray label padded top right">
													<font class="label">Genetic Map</font>
												</td>
												<td class="padded">
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
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>

