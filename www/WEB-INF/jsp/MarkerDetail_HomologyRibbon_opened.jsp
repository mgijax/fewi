								<table style='width: 100%'>
									<c:forEach var="humanHomolog" items="${humanHomologs}" varStatus="humanHomologStatus">
										<tr>
											<td class="rightBorderThinGray label padded top right"><font class="label">Human&nbsp;Ortholog</font></td>
											<td class="padded" style="width: 100%">
												<c:set var="humanCoords" value="${humanHomolog.preferredCoordinates}"/>
												<fmt:formatNumber value="${humanCoords.startCoordinate}" pattern="#0" var="humanStartCoord"/>
												<fmt:formatNumber value="${humanCoords.endCoordinate}" pattern="#0" var="humanEndCoord"/>
						
												<div style="float: left;">${humanHomolog.symbol}, ${humanHomolog.name}</div>
												<div style="float: left; margin-left: 20px;">Orthology source: 
													<c:forEach var="homologyCluster" items="${marker.getHomologyClusterSources(humanHomolog)}" varStatus="hstat">
														${homologyCluster.source}<c:if test="${!hstat.last}">, </c:if>
													</c:forEach>	
												</div>
											</td>
										</tr>

										<c:if test="${not empty humanHomolog.synonyms}">
											<tr>
												<td class="rightBorderThinGray label padded top right"><font class="label">Synonyms</font></td>
												<td class="padded" style="width: 100%">
														<c:forEach var="synonym" items="${humanHomolog.synonyms}" varStatus="synonymStatus">
															${synonym.synonym}<c:if test="${!synonymStatus.last}">, </c:if>
														</c:forEach>
												</td>
											</tr>
										</c:if>

										<tr>
											<td class="rightBorderThinGray label padded top right"><font class="label">Links</font></td>
											<td class="padded" style="width: 100%">
												<c:if test="${not empty humanHomolog.hgncID}">
													<div style="float: left; margin-right: 20px;">
														<a href="${fn:replace(urls.HGNC, '@@@@', humanHomolog.hgncID.accID)}" target="_blank">${humanHomolog.hgncID.accID}</a>
													</div>
												</c:if>
					
												<div style="float: left; margin-right: 20px;">NCBI Gene ID: <a href="${fn:replace(urls.Entrez_Gene, '@@@@', humanHomolog.entrezGeneID.accID)}" target="_blank">${humanHomolog.entrezGeneID.accID}</a></div>
							
												<c:if test="${not empty humanHomolog.neXtProtIDs}">
													<div style="float: left; margin-right: 20px;">neXtProt AC:
														<c:forEach var="neXtProt" items="${humanHomolog.neXtProtIDs}" varStatus="neXtProtStatus">
															<a href="${fn:replace(urls.neXtProt, '@@@@', neXtProt.accID)}" target="_blank">${neXtProt.accID}</a><c:if test="${!neXtProtStatus.last}">, </c:if>
														</c:forEach>
													</div>
												</c:if>
											</td>
										</tr>

										<tr>
											<td class="rightBorderThinGray label padded top right"><font class="label">Chr&nbsp;Location</font></td>
											<td class="padded" style="width: 100%">
												<c:set var="humanCytoband" value="${humanHomolog.preferredCytoband}"/>
												<c:if test="${not empty humanCytoband}">${humanCytoband.chromosome}${humanCytoband.cytogeneticOffset}<c:if test="${not empty humanCoords}">; </c:if></c:if>
												<c:if test="${not empty humanCoords}">
													chr${humanCoords.chromosome}:${humanStartCoord}-${humanEndCoord}
													<c:if test="${not empty humanCoords.strand}">(${humanCoords.strand})</c:if>&nbsp;&nbsp;<I>${humanCoords.buildIdentifier}</I>
												</c:if>
											</td>
										</tr>

										<tr>
											<td><hr></td>
											<td><hr></td>
										</tr>
									</c:forEach>

									<c:forEach var="homologyClass" items="${homologyClasses}">
										<c:if test="${not empty homologyClass.primaryID}">
											<tr>
												<td class="rightBorderThinGray label padded top right"><font class="label">HomoloGene</font></td>
												<td class="padded" style="width: 100%;">
													<a href="${configBean.FEWI_URL}homology/${homologyClass.primaryID}">Vertebrate Homology Class ${homologyClass.primaryID}</a><br/>
													<c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">${organismOrthology.markerCount} ${organismOrthology.organism}<c:if test="${!status.last}">;</c:if></c:forEach><br/>
												</td>
											</tr>
										</c:if>
									</c:forEach>

									<c:if test="${not empty hcopLinks}">
										<tr>
											<td class="rightBorderThinGray label padded top right"><font class="label">HCOP</font></td>
											<td class="padded" style="width: 100%">
												<c:forEach var="organism" items="${hcopLinks}">
													<c:if test="${fn:length(organism.value) > 0}">
														${organism.key} homology predictions:
														<c:forEach var="hmarker" items="${organism.value}" varStatus="hcstat">
															<c:if test="${organism.key == 'human'}">
																<a href="${fn:replace(urls.HCOP, '@@@@', hmarker.value.symbol)}" target="_blank">${hmarker.value.symbol}</a><c:if test="${!hcstat.last}">, </c:if>
															</c:if>
														</c:forEach>
													</c:if>
												</c:forEach>
											</td>
										</tr>
									</c:if>

									<c:if test="${not empty marker.pirsfAnnotation}">
										<tr>
											<td class="rightBorderThinGray label padded top right"><font class="label">Protein&nbsp;SuperFamily</font></td>
											<td class="padded" style="width: 100%">
												<a href="${configBean.FEWI_URL}vocab/pirsf/${marker.pirsfAnnotation.termID}">${marker.pirsfAnnotation.term}</a><br/>
											</td>
										</tr>
									</c:if>

									<c:if test="${marker.hasOneEnsemblGeneModelID}">
										<c:set var="genetreeUrl" value="${configBean.GENETREE_URL}"/>
										<c:set var="genetreeUrl" value="${fn:replace(genetreeUrl, '<model_id>', marker.ensemblGeneModelID.accID)}"/>
										<tr>
											<td class="rightBorderThinGray label padded top right"><font class="label">Gene&nbsp;Tree</font></td>
											<td class="padded" style="width: 100%">
												<a href="${configBean.GENETREE_URL}${marker.ensemblGeneModelID.accID}" target="_blank">${marker.symbol}</a><br/>
											</td>
										</tr>
									</c:if>
								</table>

