	<c:if test="${(not empty humanHomologs) or (marker.hasOneEnsemblGeneModelID) or (not empty marker.pirsfAnnotation) }">
		<div class="row" id="homologyRibbon">
			<div class="header <%=leftTdStyles.getNext() %>">Homology</div>
			<div class="detail <%=rightTdStyles.getNext() %>">

				<c:set var="wider" value="" />
				<c:set var="arrowstate" value="hdExpand" />
				<c:set var="arrowtext" value="more" />
				<c:set var="titletext" value="Show More" />
				<c:if test="${fn:length(humanHomologs) == 0}">
					<c:set var="arrowstate" value="hdCollapse" />
					<c:set var="arrowtext" value="less" />
					<c:set var="titletext" value="Show Less" />
				</c:if>

				<div id="homToggle" title="${titletext}" class="toggleImage ${arrowstate}">${arrowtext}</div>

				<c:if test="${fn:length(humanHomologs) > 0}">
					<section class="summarySec1 extra open">
						<ul>
							<c:set var="humanHomolog" value="${humanHomologs[0]}" />
							<li>
								<div class="label">
									Human Ortholog
								</div>
								<div class="value">
									<c:set var="humanCoords" value="${humanHomolog.preferredCoordinates}"/>
									<fmt:formatNumber value="${humanCoords.startCoordinate}" pattern="#0" var="humanStartCoord"/>
									<fmt:formatNumber value="${humanCoords.endCoordinate}" pattern="#0" var="humanEndCoord"/>
									${humanHomolog.symbol}, ${humanHomolog.name}
								</div>
							</li>
						</ul>
					</section>

					<section class="summarySec2 extra open">
						<ul>
							<li>
								<c:forEach var="homologyClass" items="${homologyClasses}">
									<c:if test="${not empty homologyClass.clusterKey}">
										<div class="label">
											Vertebrate&nbsp;Orthologs
										</div>
										<div class="value">
											<c:set var="organismOrthologyCount" value="0"/>
											<c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">
												<c:if test="${organismOrthology.organism != 'mouse'}">
													<c:set var="organismOrthologyCount" value="${organismOrthology.markerCount + organismOrthologyCount}" />
												</c:if>
											</c:forEach>
											${organismOrthologyCount}
										</div>
									</c:if>
								</c:forEach>
							</li>
						</ul>
					</section>
				</c:if>

				<c:set var="padIfNoHuman" value="" />
				<c:set var="sectionstate" value="closed" />
				<c:if test="${fn:length(humanHomologs) == 0}">
					<c:set var="sectionstate" value="open" />
					<c:set var="padIfNoHuman" value=" style='margin-left: 60px;'" />
				</c:if>

				<div class="homologyExtra extra ${sectionstate}">
					<c:if test="${fn:length(humanHomologs) > 0}">
						<c:set var="wider" value="wider" />
						<section class="summarySec1 wide">
							<div class="label ${wider}" style="height: 3em;">Vertebrate Orthology Source</div>
							<div class="value">MGI Vertebrate Homology</div>
						</section>
					</c:if>

					<c:forEach var="humanHomolog" items="${humanHomologs}" varStatus="humanHomologStatus">

						<section class="summarySec1 wide">
							<ul>
								<li>
									<div class="label ${wider}">
										Human&nbsp;Ortholog
									</div>
									<div class="value">
										<c:set var="humanCoords" value="${humanHomolog.preferredCoordinates}"/>
										<fmt:formatNumber value="${humanCoords.startCoordinate}" pattern="#0" var="humanStartCoord"/>
										<fmt:formatNumber value="${humanCoords.endCoordinate}" pattern="#0" var="humanEndCoord"/>
										<c:set var="allianceLink" value=""/>
										<c:forEach var="link" items="${humanHomolog.homologyLinks}">
											<c:if test="${link.displayText == 'MGI Vertebrate Homology' }">
												<c:set var="allianceLink" value="${link.url}"/>
											</c:if>
										</c:forEach>
						
										<c:if test="${allianceLink == '' }">
											${humanHomolog.symbol},
										</c:if>
										<c:if test="${allianceLink != '' }">
											<a href="${allianceLink}" target="_blank">${humanHomolog.symbol}</a>,
										</c:if>
										${humanHomolog.name}<br />
									</div>
								</li>

								<c:if test="${not empty humanHomolog.synonyms}">
									<li>
										<div class="label ${wider}">
											Synonyms
										</div>
										<div class="value">
											<c:forEach var="synonym" items="${humanHomolog.synonyms}" varStatus="synonymStatus">
												${synonym.synonym}<c:if test="${!synonymStatus.last}">, </c:if>
											</c:forEach>
										</div>
									</li>
								</c:if>

								<li>
									<div class="label ${wider}">
										Links
									</div>
									<div class="value">

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

										<c:if test="${not empty humanHomolog.uniProtIDs}">
											<div style="float: left; margin-right: 20px;">UniProt:
												<c:forEach var="uniProt" items="${humanHomolog.uniProtIDs}" varStatus="uniProtStatus">
													<a href="${fn:replace(externalUrls.UniProt2, '@@@@', uniProt.accID)}" target="_blank">${uniProt.accID}</a><c:if test="${!uniProtStatus.last}">, </c:if>
												</c:forEach>
											</div>
										</c:if>

										<br style="clear:left;"/>
									</div>
								</li>

								<li>
									<div class="label ${wider}">
										Chr&nbsp;Location
									</div>
									<div class="value">
										<c:set var="humanCytoband" value="${humanHomolog.preferredCytoband}"/>
										<c:if test="${not empty humanCytoband}">${humanCytoband.chromosome}${humanCytoband.cytogeneticOffset}<c:if test="${not empty humanCoords}">; </c:if></c:if>
										<c:if test="${not empty humanCoords}">
											chr${humanCoords.chromosome}:${humanStartCoord}-${humanEndCoord}
											<c:if test="${not empty humanCoords.strand}">(${humanCoords.strand})</c:if>&nbsp;&nbsp;<I>${humanCoords.buildIdentifier}</I>
										</c:if>
									</div>
								</li>

							</ul>
						</section>
						<hr>
					</c:forEach><!-- human homolog -->

					<section class="summarySec1"${padIfNoHuman}>
						<ul>

							<c:forEach var="homologyClass" items="${homologyClasses}">
								<c:if test="${not empty homologyClass.clusterKey}">
									<li>
										<div class="label ${wider}">Alliance of Genome Resources</div>
										<div class="value">
											<a href="${configBean.FEWI_URL}homology/cluster/key/${homologyClass.clusterKey}">${marker.symbol} stringent orthology</a><br/>
											<c:forEach var="organismOrthology" items="${homologyClass.orthologs}" varStatus="status">${organismOrthology.markerCount} ${organismOrthology.organism}<c:if test="${!status.last}">;</c:if></c:forEach><br/>
										</div>
									</li>
								</c:if>
							</c:forEach>

							<c:if test="${not empty hcopLinks}">
								<li>
									<div class="label ${wider}">HCOP</div>
									<div class="value">
										<c:forEach var="organism" items="${hcopLinks}">
											<c:if test="${fn:length(organism.value) > 0}">
												vertebrate homology predictions:
												<c:forEach var="hmarker" items="${organism.value}" varStatus="hcstat">
													<c:if test="${organism.key == 'human'}">
														<a href="${fn:replace(urls.HCOP, '@@@@', hmarker.value.symbol)}" target="_blank">${hmarker.value.symbol}</a><c:if test="${!hcstat.last}">, </c:if>
													</c:if>
												</c:forEach>
											</c:if>
										</c:forEach>
									</div>
								</li>
							</c:if>

							<c:if test="${not empty marker.pirsfAnnotation}">
								<li>
									<div class="label ${wider}">Protein&nbsp;SuperFamily</div>
									<div class="value">
										<a href="${configBean.FEWI_URL}vocab/pirsf/${marker.pirsfAnnotation.termID}">${marker.pirsfAnnotation.term}</a>
									</div>
								</li>
							</c:if>

							<c:if test="${marker.hasOneEnsemblGeneModelID}">
								<c:set var="genetreeUrl" value="${configBean.GENETREE_URL}"/>
								<c:set var="genetreeUrl" value="${fn:replace(genetreeUrl, '<model_id>', marker.ensemblGeneModelID.accID)}"/>
								<li>
									<div class="label ${wider}">Gene&nbsp;Tree</div>
									<div class="value">
										<a href="${configBean.GENETREE_URL}${marker.ensemblGeneModelID.accID}" target="_blank">${marker.symbol}</a><br/>
									</div>
								</li>
							</c:if>

						</ul>
					</section>

				</div>
			</div>
		</div>
	</c:if>
