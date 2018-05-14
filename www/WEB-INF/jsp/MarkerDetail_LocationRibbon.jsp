	<div class="row locationRibbon">
		<div class="header <%=leftTdStyles.getNext() %>">
			Genome Context & Strain Distribution
		</div>
		<div class="detail <%=rightTdStyles.getNext() %>">

			<c:set var="showJBrowser" value="${not empty marker.preferredCoordinates or not empty jbrowseUrl}" />
			<c:set var="showDownloadSequence" value="${not empty marker.preferredCoordinates}" />
			<c:set var="showGenomeBrowserLinks" value="${not (empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty ncbiMapViewerUrl)}" />

			<c:set var="showGeneticMap" value="${(not empty marker.preferredCentimorgans) or (not empty marker.preferredCytoband) or (marker.countOfMappingExperiments > 0) or (not empty qtlIDs) or (not empty marker.aliases)}" />
			
			<c:set var="geneticMapExtra" value="${not empty qtlIDs or marker.countOfMappingExperiments > 0 or not empty marker.aliases or hasMiniMap}" />
			<c:set var="showLocationNote" value="${not empty marker.locationNote}" />

			<c:set var="arrowstate" value="hdExpand" />
			<c:set var="extrastate" value="" />
			<c:set var="arrowtext" value="more" />
			<c:set var="titletext" value="Show More" />
			<c:if test="${not (showLocationNote or showJBrowser or showDownloadSequence or showGenomeBrowserLinks or geneticMapExtra)}">
				<c:set var="arrowstate" value="hdCollapse" />
				<c:set var="extrastate" value="extra" />
				<c:set var="arrowtext" value="less" />
				<c:set var="titletext" value="Show Less" />
			</c:if>

			<div title="${titletext}" class="toggleImage ${arrowstate}">${arrowtext}</div>

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
									Chr${chromosome}:${startCoord}-${endCoord}
									${marker.preferredCoordinates.mapUnits}<c:if test="${not empty marker.preferredCoordinates.strand}">, ${marker.preferredCoordinates.strand} strand</c:if>
								</c:if>
							</c:if>

							<c:if test="${(empty marker.preferredCoordinates and empty ensemblGenomeBrowserUrl and empty ucscGenomeBrowserUrl and empty gbrowseUrl and empty jbrowseUrl)}">
								<span style="font-style: italic;font-size: smaller;">Genome coordinates not available</span>
							</c:if>
						</div>
					</li>

					<c:if test="${showJBrowser}">
						<li class="extra closed">
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
					</c:if>

					<c:if test="${showDownloadSequence}">
						<li class="extra closed">
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

					<c:if test="${not (empty chromosome or empty startCoord or empty endCoord)}">
						<fmt:formatNumber value="${marker.preferredCoordinates.startCoordinate - 10000}" pattern="#0" var="startCoordWithFlank"/>
						<fmt:formatNumber value="${marker.preferredCoordinates.endCoordinate + 10000}" pattern="#0" var="endCoordWithFlank"/>
						<li class="extra closed">
							<div class="value" style="font-size: smaller; margin-left: 16.5em;">
								<div style="float:left; margin-right: 5px">
									<img src="${configBean.WEBSHARE_URL}images/new_icon.png"/>
								</div>
								<div style="padding-top:5px; font-size: 1.2em;">
									<a href="${externalUrls.MGV}#ref=C57BL/6J&genomes=${externalUrls.MGV_Strains}&chr=${chromosome}&start=${startCoordWithFlank}&end=${endCoordWithFlank}&highlight=${marker.primaryID}" target="_blank" id="mgvLink">
									Multiple Genome Viewer (MGV)
									</a>
								</div>
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
					
						<c:if test="${hasMinimap}">
							<li class="extra closed">
								<div class="value">
								    <div class="minimapWrap">
								        <canvas id="minimap"></canvas>
								    </div>
								    <script>
								    	window.loadMinimap = function(){
								    		Minimap.draw({
								    			id: "minimap",
								    			data: ${minimapInitJson},
								    			rotate90: true
								    		});
								    	}
								    </script>
								</div>
							</li>
						</c:if>

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

			<c:if test="${not empty marker.strainMarkers}">

				<div class="extra closed">
				<table class="padded" id="table_strainMarkers">
					
					<tr class="headerStripe">
					  <th>Strain</th>
					  <th>Gene Model ID</th>
					  <th>Feature Type</th>
					  <th>Coordinates</th>
					  <th>Downloads</th>
					</tr>
					
					<c:forEach var="sm" items="${marker.strainMarkers}">
						<tr>
							<td>
							  <a href="${configBean.FEWI_URL}strain/${sm.strainID}">${sm.strainName}</a>
							</td>
							<td>
								<c:if test="${sm.noAnnotation}">
									no annotation
								</c:if>
								<c:if test="${not sm.noAnnotation}">
									<c:forEach var="gm" items="${sm.geneModels}">
									  ${gm.geneModelID}</br>
									</c:forEach>
								</c:if>
							</td>
							<td>${sm.featureType}</td>
							<td>${sm.location}</td>
							<td></td>
						</tr>
					</c:forEach> 

				</table>
				</div>

			</c:if>

		</div>
	</div>
