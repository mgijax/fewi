								<table>
									<tr>
										<td class="top" style="padding-right: 45px;">
											<table>
												<c:if test="${fn:length(humanHomologs) > 0}">
													<c:set var="humanHomolog" value="${humanHomologs[0]}" />
													<tr>
														<td class="rightBorderThinGray label padded top right"><font class="label">Human&nbsp;Ortholog</font></td>
														<td class="padded" style="width: 100%">
															<c:set var="humanCoords" value="${humanHomolog.preferredCoordinates}"/>
															<fmt:formatNumber value="${humanCoords.startCoordinate}" pattern="#0" var="humanStartCoord"/>
															<fmt:formatNumber value="${humanCoords.endCoordinate}" pattern="#0" var="humanEndCoord"/>
															<div style="float: left;">${humanHomolog.symbol}, ${humanHomolog.name}</div>
														</td>
													</tr>
												</c:if>

												<c:if test="${fn:length(humanHomologs) == 0}">
													<c:forEach var="homologyClass" items="${homologyClasses}">
														<c:if test="${not empty homologyClass.primaryID}">
															<tr>
																<td class="rightBorderThinGray label padded top right"><font class="label">HomoloGene</font></td>
																<td class="padded" style="width: 100%;">
																	<c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">${organismOrthology.markerCount} ${organismOrthology.organism}<c:if test="${!status.last}">;</c:if></c:forEach><br/>
																</td>
															</tr>
														</c:if>
													</c:forEach>
												</c:if>
											</table>
										</td>
										<c:if test="${fn:length(humanHomologs) > 0}">
											<td>
												<table>
													<c:forEach var="homologyClass" items="${homologyClasses}">
														<c:if test="${not empty homologyClass.primaryID}">
															<tr>
																<td class="rightBorderThinGray label padded top right"><font class="label">Vertebrate&nbsp;Orthologs</font></td>
																<td class="padded" style="width: 100%">
																	<c:set var="organismOrthologyCount" value="0"/>
																	<c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">
																		<c:set var="organismOrthologyCount" value="${organismOrthology.markerCount + organismOrthologyCount}" />
																	</c:forEach>
																	${organismOrthologyCount}
																</td>
															</tr>
														</c:if>
													</c:forEach>
												</table>
											</td>
										</c:if>
									</tr>
								</table>

